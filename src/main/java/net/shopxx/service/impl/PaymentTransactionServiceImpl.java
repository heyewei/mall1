/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.LockModeType;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.dao.PaymentTransactionDao;
import net.shopxx.dao.SnDao;
import net.shopxx.entity.DepositLog;
import net.shopxx.entity.Member;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderPayment;
import net.shopxx.entity.PaymentTransaction;
import net.shopxx.entity.Sn;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.service.MemberService;
import net.shopxx.service.OrderService;
import net.shopxx.service.PaymentTransactionService;

/**
 * Service - 支付事务
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class PaymentTransactionServiceImpl extends BaseServiceImpl<PaymentTransaction, Long> implements PaymentTransactionService {

	@Inject
	private PaymentTransactionDao paymentTransactionDao;
	@Inject
	private SnDao snDao;
	@Inject
	private OrderService orderService;
	@Inject
	private MemberService memberService;

	@Transactional(readOnly = true)
	public PaymentTransaction findBySn(String sn) {
		return paymentTransactionDao.find("sn", StringUtils.lowerCase(sn));
	}

	public PaymentTransaction create(PaymentPlugin paymentPlugin, PaymentTransaction.LineItem lineItem) {
		Assert.notNull(paymentPlugin);
		Assert.notNull(lineItem);
		Assert.notNull(lineItem.getType());
		Assert.notNull(lineItem.getAmount());

		PaymentTransaction paymentTransaction = paymentTransactionDao.findAvailable(paymentPlugin, lineItem);
		if (paymentTransaction == null) {
			BigDecimal amount = paymentPlugin.calculateAmount(lineItem.getAmount());
			BigDecimal fee = paymentPlugin.calculateFee(lineItem.getAmount());
			paymentTransaction = new PaymentTransaction();
			paymentTransaction.setSn(null);
			paymentTransaction.setType(lineItem.getType());
			paymentTransaction.setAmount(amount);
			paymentTransaction.setFee(fee);
			paymentTransaction.setIsSuccess(false);
			paymentTransaction.setExpire(paymentPlugin.getTimeout() != null ? DateUtils.addSeconds(new Date(), paymentPlugin.getTimeout()) : null);
			paymentTransaction.setParent(null);
			paymentTransaction.setChildren(null);
			paymentTransaction.setTarget(lineItem.getTarget());
			paymentTransaction.setPaymentPlugin(paymentPlugin);
			save(paymentTransaction);
		}
		return paymentTransaction;
	}

	public void handle(PaymentTransaction paymentTransaction) {
		Assert.notNull(paymentTransaction);

		if (!LockModeType.PESSIMISTIC_WRITE.equals(paymentTransactionDao.getLockMode(paymentTransaction))) {
			paymentTransactionDao.flush();
			paymentTransactionDao.refresh(paymentTransaction, LockModeType.PESSIMISTIC_WRITE);
		}

		Assert.notNull(paymentTransaction.getType());

		if (BooleanUtils.isNotFalse(paymentTransaction.getIsSuccess())) {
			return;
		}

		switch (paymentTransaction.getType()) {
		case ORDER_PAYMENT:
			Order order = paymentTransaction.getOrder();
			if (order != null) {
				OrderPayment orderPayment = new OrderPayment();
				orderPayment.setMethod(OrderPayment.Method.online);
				orderPayment.setPaymentMethod(paymentTransaction.getPaymentPluginName());
				orderPayment.setAmount(paymentTransaction.getAmount());
				orderPayment.setFee(paymentTransaction.getFee());
				orderPayment.setOrder(order);
				orderService.payment(order, orderPayment);
			}
			break;
		case DEPOSIT_RECHARGE:
			Member member = paymentTransaction.getMember();
			if (member != null) {
				memberService.addBalance(member, paymentTransaction.getEffectiveAmount(), DepositLog.Type.recharge, null);
			}
			break;
		}
		paymentTransaction.setIsSuccess(true);
	}

	@Override
	@Transactional
	public PaymentTransaction save(PaymentTransaction paymentTransaction) {
		Assert.notNull(paymentTransaction);

		paymentTransaction.setSn(snDao.generate(Sn.Type.paymentTransaction));

		return super.save(paymentTransaction);
	}

}