/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.shop;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import net.shopxx.Setting;
import net.shopxx.entity.Member;
import net.shopxx.entity.Order;
import net.shopxx.entity.PaymentMethod;
import net.shopxx.entity.PaymentTransaction;
import net.shopxx.plugin.PaymentPlugin;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.OrderService;
import net.shopxx.service.PaymentTransactionService;
import net.shopxx.service.PluginService;
import net.shopxx.util.SystemUtils;

/**
 * Controller - 支付
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("shopPaymentController")
@RequestMapping("/payment")
public class PaymentController extends BaseController {

	@Inject
	private OrderService orderService;
	@Inject
	private PluginService pluginService;
	@Inject
	private PaymentTransactionService paymentTransactionService;

	/**
	 * 检查是否支付成功
	 */
	@PostMapping("/check_is_pay_success")
	public @ResponseBody boolean checkIsPaySuccess(String paymentTransactionSn) {
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		return paymentTransaction != null && paymentTransaction.getIsSuccess();
	}

	/**
	 * 首页
	 */
	@RequestMapping
	public String index(PaymentTransaction.Type type, String paymentPluginId, String orderSn, BigDecimal rechargeAmount, @CurrentUser Member currentUser) {
		if (type == null) {
			return UNPROCESSABLE_ENTITY_VIEW;
		}
		PaymentPlugin paymentPlugin = pluginService.getPaymentPlugin(paymentPluginId);
		if (paymentPlugin == null || !paymentPlugin.getIsEnabled()) {
			return UNPROCESSABLE_ENTITY_VIEW;
		}
		PaymentTransaction paymentTransaction = null;
		switch (type) {
		case ORDER_PAYMENT:
			Order order = orderService.findBySn(orderSn);
			if (order == null || !currentUser.equals(order.getMember()) || !orderService.acquireLock(order)) {
				return UNPROCESSABLE_ENTITY_VIEW;
			}
			if (order.getPaymentMethod() == null || !PaymentMethod.Method.online.equals(order.getPaymentMethod().getMethod())) {
				return UNPROCESSABLE_ENTITY_VIEW;
			}
			if (order.getAmountPayable().compareTo(BigDecimal.ZERO) <= 0) {
				return UNPROCESSABLE_ENTITY_VIEW;
			}
			paymentTransaction = paymentTransactionService.create(paymentPlugin, new PaymentTransaction.OrderPaymentLineItem(order));
			break;
		case DEPOSIT_RECHARGE:
			Setting setting = SystemUtils.getSetting();
			if (rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0 || rechargeAmount.precision() > 15 || rechargeAmount.scale() > setting.getPriceScale()) {
				return UNPROCESSABLE_ENTITY_VIEW;
			}
			paymentTransaction = paymentTransactionService.create(paymentPlugin, new PaymentTransaction.DepositRechargeLineItem(currentUser, rechargeAmount));
			break;
		}
		return "redirect:" + paymentPlugin.getPrePayUrl(paymentPlugin, paymentTransaction);
	}

	/**
	 * 支付前处理
	 */
	@RequestMapping({ "/pre_pay_{paymentTransactionSn:[^_]+}", "/pre_pay_{paymentTransactionSn[^_]+}_{extra}" })
	public ModelAndView prePay(@PathVariable String paymentTransactionSn, @PathVariable(required = false) String extra, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		if (paymentTransaction == null || paymentTransaction.hasExpired()) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW);
		}
		if (paymentTransaction.getIsSuccess()) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW, "errorMessage", message("shop.payment.payCompleted"));
		}
		String paymentPluginId = paymentTransaction.getPaymentPluginId();
		PaymentPlugin paymentPlugin = StringUtils.isNotEmpty(paymentPluginId) ? pluginService.getPaymentPlugin(paymentPluginId) : null;
		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW);
		}

		ModelAndView modelAndView = new ModelAndView();
		paymentPlugin.prePayHandle(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, request, response, modelAndView);
		return modelAndView;
	}

	/**
	 * 支付处理
	 */
	@RequestMapping({ "/pay_{paymentTransactionSn:[^_]+}", "/pay_{paymentTransactionSn[^_]+}_{extra}" })
	public ModelAndView pay(@PathVariable String paymentTransactionSn, @PathVariable(required = false) String extra, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		if (paymentTransaction == null || paymentTransaction.hasExpired()) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW);
		}
		if (paymentTransaction.getIsSuccess()) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW, "errorMessage", message("shop.payment.payCompleted"));
		}
		String paymentPluginId = paymentTransaction.getPaymentPluginId();
		PaymentPlugin paymentPlugin = StringUtils.isNotEmpty(paymentPluginId) ? pluginService.getPaymentPlugin(paymentPluginId) : null;
		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW);
		}

		ModelAndView modelAndView = new ModelAndView();
		paymentPlugin.payHandle(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, request, response, modelAndView);
		return modelAndView;
	}

	/**
	 * 支付后处理
	 */
	@RequestMapping({ "/post_pay_{paymentTransactionSn:[^_]+}", "/post_pay_{paymentTransactionSn:[^_]+}_{extra}" })
	public ModelAndView postPay(@PathVariable String paymentTransactionSn, @PathVariable(required = false) String extra, HttpServletRequest request, HttpServletResponse response) throws Exception {
		PaymentTransaction paymentTransaction = paymentTransactionService.findBySn(paymentTransactionSn);
		if (paymentTransaction == null) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW);
		}
		String paymentPluginId = paymentTransaction.getPaymentPluginId();
		PaymentPlugin paymentPlugin = StringUtils.isNotEmpty(paymentPluginId) ? pluginService.getPaymentPlugin(paymentPluginId) : null;
		if (paymentPlugin == null || BooleanUtils.isNotTrue(paymentPlugin.getIsEnabled())) {
			return new ModelAndView(UNPROCESSABLE_ENTITY_VIEW);
		}

		boolean isPaySuccess = paymentPlugin.isPaySuccess(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, request, response);
		if (isPaySuccess) {
			paymentTransactionService.handle(paymentTransaction);
		}
		ModelAndView modelAndView = new ModelAndView();
		paymentPlugin.postPayHandle(paymentPlugin, paymentTransaction, getPaymentDescription(paymentTransaction), extra, isPaySuccess, request, response, modelAndView);
		return modelAndView;
	}

	/**
	 * 获取支付描述
	 * 
	 * @param paymentTransaction
	 *            支付事务
	 * @return 支付描述
	 */
	private String getPaymentDescription(PaymentTransaction paymentTransaction) {
		Assert.notNull(paymentTransaction);

		switch (paymentTransaction.getType()) {
		case ORDER_PAYMENT:
			return message("shop.payment.orderPaymentDescription", paymentTransaction.getOrder().getSn());
		case DEPOSIT_RECHARGE:
			return message("shop.payment.depositRechargeDescription", paymentTransaction.getSn());
		default:
			return StringUtils.EMPTY;
		}
	}

}