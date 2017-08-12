/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.DepositLog;
import net.shopxx.entity.Member;
import net.shopxx.service.DepositLogService;
import net.shopxx.service.MemberService;

/**
 * Controller - 预存款
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminDepositController")
@RequestMapping("/admin/deposit")
public class DepositController extends BaseController {

	@Inject
	private DepositLogService depositLogService;
	@Inject
	private MemberService memberService;

	/**
	 * 检查会员
	 */
	@GetMapping("/check_member")
	public @ResponseBody Map<String, Object> checkMember(String username) {
		Map<String, Object> data = new HashMap<>();
		Member member = memberService.findByUsername(username);
		if (member == null) {
			data.put("message", Message.warn("admin.deposit.memberNotExist"));
			return data;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("balance", member.getBalance());
		return data;
	}

	/**
	 * 调整
	 */
	@GetMapping("/adjust")
	public String adjust() {
		return "admin/deposit/adjust";
	}

	/**
	 * 调整
	 */
	@PostMapping("/adjust")
	public String adjust(String username, BigDecimal amount, String memo, RedirectAttributes redirectAttributes) {
		Member member = memberService.findByUsername(username);
		if (member == null) {
			return ERROR_VIEW;
		}
		if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
			return ERROR_VIEW;
		}
		if (member.getBalance() == null || member.getBalance().add(amount).compareTo(BigDecimal.ZERO) < 0) {
			return ERROR_VIEW;
		}
		memberService.addBalance(member, amount, DepositLog.Type.adjustment, memo);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:log";
	}

	/**
	 * 记录
	 */
	@GetMapping("/log")
	public String log(Long memberId, Pageable pageable, ModelMap model) {
		Member member = memberService.find(memberId);
		if (member != null) {
			model.addAttribute("member", member);
			model.addAttribute("page", depositLogService.findPage(member, pageable));
		} else {
			model.addAttribute("page", depositLogService.findPage(pageable));
		}
		return "admin/deposit/log";
	}

}