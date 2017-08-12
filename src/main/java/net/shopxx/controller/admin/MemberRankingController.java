/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.Pageable;
import net.shopxx.entity.Member;
import net.shopxx.service.MemberService;

/**
 * Controller - 会员排名
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminMemberRankingController")
@RequestMapping("/admin/member_ranking")
public class MemberRankingController extends BaseController {

	@Inject
	private MemberService memberService;

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Member.RankingType rankingType, Pageable pageable, Model model) {
		if (rankingType == null) {
			rankingType = Member.RankingType.amount;
		}
		model.addAttribute("rankingTypes", Member.RankingType.values());
		model.addAttribute("rankingType", rankingType);
		model.addAttribute("page", memberService.findPage(rankingType, pageable));
		return "admin/member_ranking/list";
	}

}