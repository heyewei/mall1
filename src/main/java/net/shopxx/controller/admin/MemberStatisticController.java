/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.entity.Statistic;
import net.shopxx.service.StatisticService;

/**
 * Controller - 会员统计
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminMemberStatisticController")
@RequestMapping("/admin/member_statistic")
public class MemberStatisticController extends BaseController {

	@Inject
	private StatisticService statisticService;

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Statistic.Period period, Date beginDate, Date endDate, Model model) {
		if (period == null) {
			period = Statistic.Period.day;
		}
		if (beginDate == null) {
			switch (period) {
			case year:
				beginDate = DateUtils.addYears(new Date(), -10);
				break;
			case month:
				beginDate = DateUtils.addYears(new Date(), -1);
				break;
			case day:
				beginDate = DateUtils.addMonths(new Date(), -1);
				break;
			}
		}
		if (endDate == null) {
			endDate = new Date();
		}
		model.addAttribute("periods", Statistic.Period.values());
		model.addAttribute("period", period);
		model.addAttribute("beginDate", beginDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("statistics", statisticService.analyze(period, beginDate, endDate));
		return "admin/member_statistic/list";
	}

}