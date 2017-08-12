/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.Date;
import java.util.List;

import net.shopxx.entity.Statistic;

/**
 * Dao - 统计
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface StatisticDao extends BaseDao<Statistic, Long> {

	/**
	 * 判断统计是否存在
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @return 统计是否存在
	 */
	boolean exists(int year, int month, int day);

	/**
	 * 分析
	 * 
	 * @param period
	 *            周期
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 统计
	 */
	List<Statistic> analyze(Statistic.Period period, Date beginDate, Date endDate);

}