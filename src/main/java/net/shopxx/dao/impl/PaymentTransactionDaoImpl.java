/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import net.shopxx.dao.PaymentTransactionDao;
import net.shopxx.entity.PaymentTransaction;
import net.shopxx.plugin.PaymentPlugin;

/**
 * Dao - 支付事务
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class PaymentTransactionDaoImpl extends BaseDaoImpl<PaymentTransaction, Long> implements PaymentTransactionDao {

	public PaymentTransaction findAvailable(PaymentPlugin paymentPlugin, PaymentTransaction.LineItem lineItem) {
		Assert.notNull(paymentPlugin);
		Assert.notNull(lineItem);
		Assert.notNull(lineItem.getType());
		Assert.notNull(lineItem.getAmount());

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PaymentTransaction> criteriaQuery = criteriaBuilder.createQuery(PaymentTransaction.class);
		Root<PaymentTransaction> root = criteriaQuery.from(PaymentTransaction.class);
		criteriaQuery.select(root);
		BigDecimal amount = paymentPlugin.calculateAmount(lineItem.getAmount());
		BigDecimal fee = paymentPlugin.calculateFee(lineItem.getAmount());
		Predicate restrictions = criteriaBuilder.and(criteriaBuilder.equal(root.get("type"), lineItem.getType()), criteriaBuilder.equal(root.get("amount"), amount), criteriaBuilder.equal(root.get("fee"), fee), criteriaBuilder.equal(root.get("isSuccess"), false),
				criteriaBuilder.equal(root.get("paymentPluginId"), paymentPlugin.getId()), criteriaBuilder.or(root.get("expire").isNull(), criteriaBuilder.greaterThan(root.<Date>get("expire"), new Date())), root.get("parent").isNull());
		switch (lineItem.getType()) {
		case ORDER_PAYMENT:
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("order"), lineItem.getTarget()));
			break;
		case DEPOSIT_RECHARGE:
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("member"), lineItem.getTarget()));
			break;
		default:
			break;
		}
		criteriaQuery.where(restrictions);
		try {
			return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}