/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity - 运费配置
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class FreightConfig extends BaseEntity<Long> {

	private static final long serialVersionUID = 6251545767229678811L;

	/**
	 * 首重价格
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	private BigDecimal firstPrice;

	/**
	 * 续重价格
	 */
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	private BigDecimal continuePrice;

	/**
	 * 地区
	 */
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Area area;

	/**
	 * 配送方式
	 */
	@NotNull(groups = Save.class)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, updatable = false)
	private ShippingMethod shippingMethod;

	/**
	 * 获取首重价格
	 * 
	 * @return 首重价格
	 */
	public BigDecimal getFirstPrice() {
		return firstPrice;
	}

	/**
	 * 设置首重价格
	 * 
	 * @param firstPrice
	 *            首重价格
	 */
	public void setFirstPrice(BigDecimal firstPrice) {
		this.firstPrice = firstPrice;
	}

	/**
	 * 获取续重价格
	 * 
	 * @return 续重价格
	 */
	public BigDecimal getContinuePrice() {
		return continuePrice;
	}

	/**
	 * 设置续重价格
	 * 
	 * @param continuePrice
	 *            续重价格
	 */
	public void setContinuePrice(BigDecimal continuePrice) {
		this.continuePrice = continuePrice;
	}

	/**
	 * 获取地区
	 * 
	 * @return 地区
	 */
	public Area getArea() {
		return area;
	}

	/**
	 * 设置地区
	 * 
	 * @param area
	 *            地区
	 */
	public void setArea(Area area) {
		this.area = area;
	}

	/**
	 * 获取配送方式
	 * 
	 * @return 配送方式
	 */
	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	/**
	 * 设置配送方式
	 * 
	 * @param shippingMethod
	 *            配送方式
	 */
	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

}