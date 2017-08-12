/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import net.shopxx.plugin.PaymentPlugin;

/**
 * Entity - 支付事务
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class PaymentTransaction extends BaseEntity<Long> {

	private static final long serialVersionUID = 5940192764031208143L;

	/**
	 * 类型
	 */
	public enum Type {

		/**
		 * 订单支付
		 */
		ORDER_PAYMENT,

		/**
		 * 预存款充值
		 */
		DEPOSIT_RECHARGE
	}

	/**
	 * 编号
	 */
	@Column(nullable = false, updatable = false, unique = true)
	private String sn;

	/**
	 * 类型
	 */
	@Column(nullable = false, updatable = false)
	private PaymentTransaction.Type type;

	/**
	 * 支付金额
	 */
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	private BigDecimal amount;

	/**
	 * 支付手续费
	 */
	@Column(nullable = false, updatable = false, precision = 21, scale = 6)
	private BigDecimal fee;

	/**
	 * 是否成功
	 */
	@Column(nullable = false)
	private Boolean isSuccess;

	/**
	 * 支付插件ID
	 */
	@Column(nullable = false, updatable = false)
	private String paymentPluginId;

	/**
	 * 支付插件名称
	 */
	@Column(nullable = false, updatable = false)
	private String paymentPluginName;

	/**
	 * 过期时间
	 */
	@Column(updatable = false)
	private Date expire;

	/**
	 * 父事务
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	private PaymentTransaction parent;

	/**
	 * 订单
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orders", updatable = false)
	private Order order;

	/**
	 * 会员
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	private Member member;

	/**
	 * 子事务
	 */
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<PaymentTransaction> children = new HashSet<>();

	/**
	 * 获取编号
	 * 
	 * @return 编号
	 */
	public String getSn() {
		return sn;
	}

	/**
	 * 设置编号
	 * 
	 * @param sn
	 *            编号
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}

	/**
	 * 获取类型
	 * 
	 * @return 类型
	 */
	public PaymentTransaction.Type getType() {
		return type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            类型
	 */
	public void setType(PaymentTransaction.Type type) {
		this.type = type;
	}

	/**
	 * 获取支付金额
	 * 
	 * @return 支付金额
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * 设置支付金额
	 * 
	 * @param amount
	 *            支付金额
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * 获取支付手续费
	 * 
	 * @return 支付手续费
	 */
	public BigDecimal getFee() {
		return fee;
	}

	/**
	 * 设置支付手续费
	 * 
	 * @param fee
	 *            支付手续费
	 */
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	/**
	 * 获取是否成功
	 * 
	 * @return 是否成功
	 */
	public Boolean getIsSuccess() {
		return isSuccess;
	}

	/**
	 * 设置是否成功
	 * 
	 * @param isSuccess
	 *            是否成功
	 */
	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * 获取支付插件ID
	 * 
	 * @return 支付插件ID
	 */
	public String getPaymentPluginId() {
		return paymentPluginId;
	}

	/**
	 * 设置支付插件ID
	 * 
	 * @param paymentPluginId
	 *            支付插件ID
	 */
	public void setPaymentPluginId(String paymentPluginId) {
		this.paymentPluginId = paymentPluginId;
	}

	/**
	 * 获取支付插件名称
	 * 
	 * @return 支付插件名称
	 */
	public String getPaymentPluginName() {
		return paymentPluginName;
	}

	/**
	 * 设置支付插件名称
	 * 
	 * @param paymentPluginName
	 *            支付插件名称
	 */
	public void setPaymentPluginName(String paymentPluginName) {
		this.paymentPluginName = paymentPluginName;
	}

	/**
	 * 获取过期时间
	 * 
	 * @return 过期时间
	 */
	public Date getExpire() {
		return expire;
	}

	/**
	 * 设置过期时间
	 * 
	 * @param expire
	 *            过期时间
	 */
	public void setExpire(Date expire) {
		this.expire = expire;
	}

	/**
	 * 获取父事务
	 * 
	 * @return 父事务
	 */
	public PaymentTransaction getParent() {
		return parent;
	}

	/**
	 * 设置父事务
	 * 
	 * @param parent
	 *            父事务
	 */
	public void setParent(PaymentTransaction parent) {
		this.parent = parent;
	}

	/**
	 * 获取订单
	 * 
	 * @return 订单
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * 设置订单
	 * 
	 * @param order
	 *            订单
	 */
	public void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取子事务
	 * 
	 * @return 子事务
	 */
	public Set<PaymentTransaction> getChildren() {
		return children;
	}

	/**
	 * 设置子事务
	 * 
	 * @param children
	 *            子事务
	 */
	public void setChildren(Set<PaymentTransaction> children) {
		this.children = children;
	}

	/**
	 * 判断是否已过期
	 * 
	 * @return 是否已过期
	 */
	@Transient
	public boolean hasExpired() {
		return getExpire() != null && !getExpire().after(new Date());
	}

	/**
	 * 获取有效金额
	 * 
	 * @return 有效金额
	 */
	@Transient
	public BigDecimal getEffectiveAmount() {
		BigDecimal effectiveAmount = getAmount().subtract(getFee());
		return effectiveAmount.compareTo(BigDecimal.ZERO) >= 0 ? effectiveAmount : BigDecimal.ZERO;
	}

	/**
	 * 设置支付插件
	 * 
	 * @param paymentPlugin
	 *            支付插件
	 */
	@Transient
	public void setPaymentPlugin(PaymentPlugin paymentPlugin) {
		setPaymentPluginId(paymentPlugin != null ? paymentPlugin.getId() : null);
		setPaymentPluginName(paymentPlugin != null ? paymentPlugin.getName() : null);
	}

	/**
	 * 设置支付目标
	 * 
	 * @param target
	 *            支付目标
	 */
	@Transient
	public void setTarget(Object target) {
		if (target instanceof Order) {
			setOrder((Order) target);
		} else if (target instanceof Member) {
			setMember((Member) target);
		}
	}

	/**
	 * 持久化前处理
	 */
	@PrePersist
	public void prePersist() {
		setSn(StringUtils.lowerCase(getSn()));
	}

	/**
	 * Entity - 支付明细
	 * 
	 * @author SHOP++ Team
	 * @version 5.0.3
	 */
	public abstract static class LineItem {
		/**
		 * 金额
		 */
		private BigDecimal amount;

		/**
		 * 获取金额
		 * 
		 * @return 金额
		 */
		public BigDecimal getAmount() {
			return amount;
		}

		/**
		 * 设置金额
		 * 
		 * @param amount
		 *            金额
		 */
		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		/**
		 * 获取支付事务类型
		 * 
		 * @return 支付事务类型
		 */
		public abstract PaymentTransaction.Type getType();

		/**
		 * 获取支付目标
		 * 
		 * @return 支付目标
		 */
		public abstract Object getTarget();
	}

	/**
	 * Entity - 订单支付明细
	 * 
	 * @author SHOP++ Team
	 * @version 5.0.3
	 */
	public static class OrderPaymentLineItem extends LineItem {

		/**
		 * 订单
		 */
		private Order order;

		/**
		 * 构造方法
		 * 
		 * @param order
		 *            订单
		 */
		public OrderPaymentLineItem(Order order) {
			this.order = order;
			super.amount = order.getAmountPayable();
		}

		@Override
		public PaymentTransaction.Type getType() {
			return PaymentTransaction.Type.ORDER_PAYMENT;
		}

		@Override
		public Object getTarget() {
			return order;
		}
	}

	/**
	 * Entity - 预存款充值明细
	 * 
	 * @author SHOP++ Team
	 * @version 5.0.3
	 */
	public static class DepositRechargeLineItem extends LineItem {

		/**
		 * 会员
		 */
		private Member member;

		/**
		 * 构造方法
		 * 
		 * @param member
		 *            会员
		 * @param amount
		 *            金额
		 */
		public DepositRechargeLineItem(Member member, BigDecimal amount) {
			this.member = member;
			super.amount = amount;
		}

		@Override
		public PaymentTransaction.Type getType() {
			return PaymentTransaction.Type.DEPOSIT_RECHARGE;
		}

		@Override
		public Object getTarget() {
			return member;
		}
	}

}