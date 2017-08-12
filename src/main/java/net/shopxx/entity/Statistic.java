/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Entity - 统计
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class Statistic extends BaseEntity<Long> {

	private static final long serialVersionUID = 2022131337300482638L;

	/**
	 * 周期
	 */
	public enum Period {

		/**
		 * 年
		 */
		year,

		/**
		 * 月
		 */
		month,

		/**
		 * 日
		 */
		day
	}

	/**
	 * 年
	 */
	@Column(nullable = false, updatable = false)
	private Integer year;

	/**
	 * 月
	 */
	@Column(nullable = false, updatable = false)
	private Integer month;

	/**
	 * 日
	 */
	@Column(nullable = false, updatable = false)
	private Integer day;

	/**
	 * 会员注册数
	 */
	@Column(nullable = false, updatable = false)
	private Long registerMemberCount;

	/**
	 * 订单创建数
	 */
	@Column(nullable = false, updatable = false)
	private Long createOrderCount;

	/**
	 * 订单完成数
	 */
	@Column(nullable = false, updatable = false)
	private Long completeOrderCount;

	/**
	 * 订单创建金额
	 */
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	private BigDecimal createOrderAmount;

	/**
	 * 订单完成金额
	 */
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	private BigDecimal completeOrderAmount;

	/**
	 * 构造方法
	 */
	public Statistic() {
	}

	/**
	 * 构造方法
	 * 
	 * @param year
	 *            年
	 * @param registerMemberCount
	 *            会员注册数
	 * @param createOrderCount
	 *            订单创建数
	 * @param completeOrderCount
	 *            订单完成数
	 * @param createOrderAmount
	 *            订单创建金额
	 * @param completeOrderAmount
	 *            订单完成金额
	 */
	public Statistic(Integer year, Long registerMemberCount, Long createOrderCount, Long completeOrderCount, BigDecimal createOrderAmount, BigDecimal completeOrderAmount) {
		this.year = year;
		this.registerMemberCount = registerMemberCount;
		this.createOrderCount = createOrderCount;
		this.completeOrderCount = completeOrderCount;
		this.createOrderAmount = createOrderAmount;
		this.completeOrderAmount = completeOrderAmount;
	}

	/**
	 * 构造方法
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param registerMemberCount
	 *            会员注册数
	 * @param createOrderCount
	 *            订单创建数
	 * @param completeOrderCount
	 *            订单完成数
	 * @param createOrderAmount
	 *            订单创建金额
	 * @param completeOrderAmount
	 *            订单完成金额
	 */
	public Statistic(Integer year, Integer month, Long registerMemberCount, Long createOrderCount, Long completeOrderCount, BigDecimal createOrderAmount, BigDecimal completeOrderAmount) {
		this.year = year;
		this.month = month;
		this.registerMemberCount = registerMemberCount;
		this.createOrderCount = createOrderCount;
		this.completeOrderCount = completeOrderCount;
		this.createOrderAmount = createOrderAmount;
		this.completeOrderAmount = completeOrderAmount;
	}

	/**
	 * 构造方法
	 * 
	 * @param year
	 *            年
	 * @param month
	 *            月
	 * @param day
	 *            日
	 * @param registerMemberCount
	 *            会员注册数
	 * @param createOrderCount
	 *            订单创建数
	 * @param completeOrderCount
	 *            订单完成数
	 * @param createOrderAmount
	 *            订单创建金额
	 * @param completeOrderAmount
	 *            订单完成金额
	 */
	public Statistic(Integer year, Integer month, Integer day, Long registerMemberCount, Long createOrderCount, Long completeOrderCount, BigDecimal createOrderAmount, BigDecimal completeOrderAmount) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.registerMemberCount = registerMemberCount;
		this.createOrderCount = createOrderCount;
		this.completeOrderCount = completeOrderCount;
		this.createOrderAmount = createOrderAmount;
		this.completeOrderAmount = completeOrderAmount;
	}

	/**
	 * 获取年
	 * 
	 * @return 年
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * 设置年
	 * 
	 * @param year
	 *            年
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * 获取月
	 * 
	 * @return 月
	 */
	public Integer getMonth() {
		return month;
	}

	/**
	 * 设置月
	 * 
	 * @param month
	 *            月
	 */
	public void setMonth(Integer month) {
		this.month = month;
	}

	/**
	 * 获取日
	 * 
	 * @return 日
	 */
	public Integer getDay() {
		return day;
	}

	/**
	 * 设置日
	 * 
	 * @param day
	 *            日
	 */
	public void setDay(Integer day) {
		this.day = day;
	}

	/**
	 * 获取会员注册数
	 * 
	 * @return 会员注册数
	 */
	public Long getRegisterMemberCount() {
		return registerMemberCount;
	}

	/**
	 * 设置会员注册数
	 * 
	 * @param registerMemberCount
	 *            会员注册数
	 */
	public void setRegisterMemberCount(Long registerMemberCount) {
		this.registerMemberCount = registerMemberCount;
	}

	/**
	 * 获取订单创建数
	 * 
	 * @return 订单创建数
	 */
	public Long getCreateOrderCount() {
		return createOrderCount;
	}

	/**
	 * 设置订单创建数
	 * 
	 * @param createOrderCount
	 *            订单创建数
	 */
	public void setCreateOrderCount(Long createOrderCount) {
		this.createOrderCount = createOrderCount;
	}

	/**
	 * 获取订单完成数
	 * 
	 * @return 订单完成数
	 */
	public Long getCompleteOrderCount() {
		return completeOrderCount;
	}

	/**
	 * 设置订单完成数
	 * 
	 * @param completeOrderCount
	 *            订单完成数
	 */
	public void setCompleteOrderCount(Long completeOrderCount) {
		this.completeOrderCount = completeOrderCount;
	}

	/**
	 * 获取订单创建金额
	 * 
	 * @return 订单创建金额
	 */
	public BigDecimal getCreateOrderAmount() {
		return createOrderAmount;
	}

	/**
	 * 设置订单创建金额
	 * 
	 * @param createOrderAmount
	 *            订单创建金额
	 */
	public void setCreateOrderAmount(BigDecimal createOrderAmount) {
		this.createOrderAmount = createOrderAmount;
	}

	/**
	 * 获取订单完成金额
	 * 
	 * @return 订单完成金额
	 */
	public BigDecimal getCompleteOrderAmount() {
		return completeOrderAmount;
	}

	/**
	 * 设置订单完成金额
	 * 
	 * @param completeOrderAmount
	 *            订单完成金额
	 */
	public void setCompleteOrderAmount(BigDecimal completeOrderAmount) {
		this.completeOrderAmount = completeOrderAmount;
	}

	/**
	 * 获取日期
	 * 
	 * @return 日期
	 */
	@Transient
	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		if (getYear() != null) {
			calendar.set(Calendar.YEAR, getYear());
		}
		if (getMonth() != null) {
			calendar.set(Calendar.MONTH, getMonth());
		}
		if (getDay() != null) {
			calendar.set(Calendar.DAY_OF_MONTH, getDay());
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

}