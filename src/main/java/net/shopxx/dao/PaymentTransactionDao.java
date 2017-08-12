/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.entity.PaymentTransaction;
import net.shopxx.plugin.PaymentPlugin;

/**
 * Dao - 支付事务
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface PaymentTransactionDao extends BaseDao<PaymentTransaction, Long> {

	/**
	 * 查找可用支付事务
	 * 
	 * @param paymentPlugin
	 *            支付插件
	 * @param lineItem
	 *            支付明细
	 * @return 可用支付事务
	 */
	PaymentTransaction findAvailable(PaymentPlugin paymentPlugin, PaymentTransaction.LineItem lineItem);

}