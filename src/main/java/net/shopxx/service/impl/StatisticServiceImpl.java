/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.dao.MemberDao;
import net.shopxx.dao.OrderDao;
import net.shopxx.dao.StatisticDao;
import net.shopxx.entity.Statistic;
import net.shopxx.service.StatisticService;

/**
 * Service - 统计
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class StatisticServiceImpl extends BaseServiceImpl<Statistic, Long> implements StatisticService {

	@Inject
	private StatisticDao statisticDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private OrderDao orderDao;

	@Transactional(readOnly = true)
	public boolean exists(int year, int month, int day) {
		return statisticDao.exists(year, month, day);
	}

	@Transactional(readOnly = true)
	public Statistic collect(int year, int month, int day) {
		Assert.state(month >= 0);
		Assert.state(day >= 0);

		Calendar beginCalendar = Calendar.getInstance();
		beginCalendar.set(year, month, day);
		beginCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
		beginCalendar.set(Calendar.MINUTE, beginCalendar.getActualMinimum(Calendar.MINUTE));
		beginCalendar.set(Calendar.SECOND, beginCalendar.getActualMinimum(Calendar.SECOND));
		Date beginDate = beginCalendar.getTime();

		Calendar endCalendar = Calendar.getInstance();
		endCalendar.set(year, month, day);
		endCalendar.set(Calendar.HOUR_OF_DAY, beginCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		endCalendar.set(Calendar.MINUTE, beginCalendar.getActualMaximum(Calendar.MINUTE));
		endCalendar.set(Calendar.SECOND, beginCalendar.getActualMaximum(Calendar.SECOND));
		Date endDate = endCalendar.getTime();

		Statistic statistics = new Statistic();
		statistics.setYear(year);
		statistics.setMonth(month);
		statistics.setDay(day);
		statistics.setRegisterMemberCount(memberDao.registerMemberCount(beginDate, endDate));
		statistics.setCreateOrderCount(orderDao.createOrderCount(beginDate, endDate));
		statistics.setCompleteOrderCount(orderDao.completeOrderCount(beginDate, endDate));
		statistics.setCreateOrderAmount(orderDao.createOrderAmount(beginDate, endDate));
		statistics.setCompleteOrderAmount(orderDao.completeOrderAmount(beginDate, endDate));

		return statistics;
	}

	@Transactional(readOnly = true)
	public List<Statistic> analyze(Statistic.Period period, Date beginDate, Date endDate) {
		return statisticDao.analyze(period, beginDate, endDate);
	}

}