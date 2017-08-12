/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.Message;
import net.shopxx.Results;
import net.shopxx.Setting;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Member;
import net.shopxx.entity.SafeKey;
import net.shopxx.service.MailService;
import net.shopxx.service.MemberService;
import net.shopxx.util.SystemUtils;

/**
 * Controller - 密码
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("shopPasswordController")
@RequestMapping("/password")
public class PasswordController extends BaseController {

	@Inject
	private MemberService memberService;
	@Inject
	private MailService mailService;

	/**
	 * 忘记密码
	 */
	@GetMapping("/forgot")
	public String forgot() {
		return "shop/password/forgot";
	}

	/**
	 * 忘记密码
	 */
	@PostMapping("/forgot")
	public ResponseEntity<?> forgot(String username, String email) {
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
			return Results.UNPROCESSABLE_ENTITY;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			return Results.unprocessableEntity("shop.password.memberNotExist");
		}
		if (StringUtils.isEmpty(member.getEmail())) {
			return Results.unprocessableEntity("shop.password.emailEmpty");
		}
		if (!StringUtils.equalsIgnoreCase(member.getEmail(), email)) {
			return Results.unprocessableEntity("shop.password.invalidEmail");
		}

		Setting setting = SystemUtils.getSetting();
		SafeKey safeKey = new SafeKey();
		safeKey.setValue(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		safeKey.setExpire(setting.getSafeKeyExpiryTime() != 0 ? DateUtils.addMinutes(new Date(), setting.getSafeKeyExpiryTime()) : null);
		member.setSafeKey(safeKey);
		memberService.update(member);
		mailService.sendForgotPasswordMail(member.getEmail(), member.getUsername(), safeKey);
		return Results.ok("shop.password.forgotSuccess");
	}

	/**
	 * 重置密码
	 */
	@GetMapping("/reset")
	public String reset(String username, String key, Model model) {
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(key)) {
			return UNPROCESSABLE_ENTITY_VIEW;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			return UNPROCESSABLE_ENTITY_VIEW;
		}
		SafeKey safeKey = member.getSafeKey();
		if (safeKey == null || safeKey.getValue() == null || !safeKey.getValue().equals(key)) {
			return UNPROCESSABLE_ENTITY_VIEW;
		}
		if (safeKey.hasExpired()) {
			model.addAttribute("errorMessage", Message.warn("shop.password.hasExpired"));
			return UNPROCESSABLE_ENTITY_VIEW;
		}

		model.addAttribute("member", member);
		model.addAttribute("key", key);
		return "shop/password/reset";
	}

	/**
	 * 重置密码
	 */
	@PostMapping("/reset")
	public ResponseEntity<?> reset(String username, String newPassword, String key) {
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(newPassword) || StringUtils.isEmpty(key)) {
			return Results.UNPROCESSABLE_ENTITY;
		}
		Member member = memberService.findByUsername(username);
		if (member == null) {
			return Results.UNPROCESSABLE_ENTITY;
		}
		if (!isValid(Member.class, "password", newPassword, BaseEntity.Save.class)) {
			return Results.unprocessableEntity("shop.password.invalidPassword");
		}
		SafeKey safeKey = member.getSafeKey();
		if (safeKey == null || safeKey.getValue() == null || !safeKey.getValue().equals(key)) {
			return Results.UNPROCESSABLE_ENTITY;
		}
		if (safeKey.hasExpired()) {
			return Results.unprocessableEntity("shop.password.hasExpired");
		}
		member.setPassword(newPassword);
		member.setSafeKey(null);
		memberService.update(member);
		return Results.ok("shop.password.resetSuccess");
	}

}