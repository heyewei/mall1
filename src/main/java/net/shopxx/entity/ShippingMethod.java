/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PreRemove;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Entity - 配送方式
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class ShippingMethod extends OrderedEntity<Long> {

	private static final long serialVersionUID = 5873163245980853245L;

	/**
	 * 名称
	 */
	@JsonView(BaseView.class)
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	private String name;

	/**
	 * 首重量
	 */
	@JsonView(BaseView.class)
	@NotNull
	@Min(0)
	@Column(nullable = false)
	private Integer firstWeight;

	/**
	 * 续重量
	 */
	@JsonView(BaseView.class)
	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer continueWeight;

	/**
	 * 默认首重价格
	 */
	@JsonView(BaseView.class)
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	private BigDecimal defaultFirstPrice;

	/**
	 * 默认续重价格
	 */
	@JsonView(BaseView.class)
	@NotNull
	@Min(0)
	@Digits(integer = 12, fraction = 3)
	@Column(nullable = false, precision = 21, scale = 6)
	private BigDecimal defaultContinuePrice;

	/**
	 * 图标
	 */
	@JsonView(BaseView.class)
	@Length(max = 200)
	@Pattern(regexp = "^(?i)(http:\\/\\/|https:\\/\\/|\\/).*$")
	private String icon;

	/**
	 * 介绍
	 */
	@JsonView(BaseView.class)
	@Length(max = 200)
	private String description;

	/**
	 * 默认物流公司
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private DeliveryCorp defaultDeliveryCorp;

	/**
	 * 支持支付方式
	 */
	@JsonView(BaseView.class)
	@ManyToMany(fetch = FetchType.LAZY)
	@OrderBy("order asc")
	private Set<PaymentMethod> paymentMethods = new HashSet<>();

	/**
	 * 运费配置
	 */
	@OneToMany(mappedBy = "shippingMethod", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<FreightConfig> freightConfigs = new HashSet<>();

	/**
	 * 订单
	 */
	@OneToMany(mappedBy = "shippingMethod", fetch = FetchType.LAZY)
	private Set<Order> orders = new HashSet<>();

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取首重量
	 * 
	 * @return 首重量
	 */
	public Integer getFirstWeight() {
		return firstWeight;
	}

	/**
	 * 设置首重量
	 * 
	 * @param firstWeight
	 *            首重量
	 */
	public void setFirstWeight(Integer firstWeight) {
		this.firstWeight = firstWeight;
	}

	/**
	 * 获取续重量
	 * 
	 * @return 续重量
	 */
	public Integer getContinueWeight() {
		return continueWeight;
	}

	/**
	 * 设置续重量
	 * 
	 * @param continueWeight
	 *            续重量
	 */
	public void setContinueWeight(Integer continueWeight) {
		this.continueWeight = continueWeight;
	}

	/**
	 * 获取默认首重价格
	 * 
	 * @return 默认首重价格
	 */
	public BigDecimal getDefaultFirstPrice() {
		return defaultFirstPrice;
	}

	/**
	 * 设置默认首重价格
	 * 
	 * @param defaultFirstPrice
	 *            默认首重价格
	 */
	public void setDefaultFirstPrice(BigDecimal defaultFirstPrice) {
		this.defaultFirstPrice = defaultFirstPrice;
	}

	/**
	 * 获取默认续重价格
	 * 
	 * @return 默认续重价格
	 */
	public BigDecimal getDefaultContinuePrice() {
		return defaultContinuePrice;
	}

	/**
	 * 设置默认续重价格
	 * 
	 * @param defaultContinuePrice
	 *            默认续重价格
	 */
	public void setDefaultContinuePrice(BigDecimal defaultContinuePrice) {
		this.defaultContinuePrice = defaultContinuePrice;
	}

	/**
	 * 获取图标
	 * 
	 * @return 图标
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * 设置图标
	 * 
	 * @param icon
	 *            图标
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * 获取介绍
	 * 
	 * @return 介绍
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置介绍
	 * 
	 * @param description
	 *            介绍
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取默认物流公司
	 * 
	 * @return 默认物流公司
	 */
	public DeliveryCorp getDefaultDeliveryCorp() {
		return defaultDeliveryCorp;
	}

	/**
	 * 设置默认物流公司
	 * 
	 * @param defaultDeliveryCorp
	 *            默认物流公司
	 */
	public void setDefaultDeliveryCorp(DeliveryCorp defaultDeliveryCorp) {
		this.defaultDeliveryCorp = defaultDeliveryCorp;
	}

	/**
	 * 获取支持支付方式
	 * 
	 * @return 支持支付方式
	 */
	public Set<PaymentMethod> getPaymentMethods() {
		return paymentMethods;
	}

	/**
	 * 设置支持支付方式
	 * 
	 * @param paymentMethods
	 *            支持支付方式
	 */
	public void setPaymentMethods(Set<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	/**
	 * 获取运费配置
	 * 
	 * @return 运费配置
	 */
	public Set<FreightConfig> getFreightConfigs() {
		return freightConfigs;
	}

	/**
	 * 设置运费配置
	 * 
	 * @param freightConfigs
	 *            运费配置
	 */
	public void setFreightConfigs(Set<FreightConfig> freightConfigs) {
		this.freightConfigs = freightConfigs;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Set<Order> getOrders() {
		return orders;
	}

	/**
	 * 设置订单
	 * 
	 * @param orders
	 *            订单
	 */
	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	/**
	 * 判断是否支持支付方式
	 * 
	 * @param paymentMethod
	 *            支付方式
	 * @return 是否支持支付方式
	 */
	@JsonView(BaseView.class)
	public boolean isSupported(PaymentMethod paymentMethod) {
		return paymentMethod == null || (getPaymentMethods() != null && getPaymentMethods().contains(paymentMethod));
	}

	/**
	 * 获取运费配置
	 * 
	 * @param area
	 *            地区
	 * @return 运费配置
	 */
	@Transient
	public FreightConfig getFreightConfig(Area area) {
		if (area == null || CollectionUtils.isEmpty(getFreightConfigs())) {
			return null;
		}
		for (FreightConfig freightConfig : getFreightConfigs()) {
			if (freightConfig.getArea() != null && freightConfig.getArea().equals(area)) {
				return freightConfig;
			}
		}
		return null;
	}

	/**
	 * 删除前处理
	 */
	@PreRemove
	public void preRemove() {
		Set<Order> orders = getOrders();
		if (orders != null) {
			for (Order order : orders) {
				order.setShippingMethod(null);
			}
		}
	}

}