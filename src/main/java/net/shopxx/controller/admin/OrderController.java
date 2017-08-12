/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.Setting;
import net.shopxx.entity.Area;
import net.shopxx.entity.Invoice;
import net.shopxx.entity.Member;
import net.shopxx.entity.Order;
import net.shopxx.entity.OrderItem;
import net.shopxx.entity.OrderPayment;
import net.shopxx.entity.OrderRefunds;
import net.shopxx.entity.OrderReturns;
import net.shopxx.entity.OrderReturnsItem;
import net.shopxx.entity.OrderShipping;
import net.shopxx.entity.OrderShippingItem;
import net.shopxx.entity.PaymentMethod;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.entity.Sku;
import net.shopxx.service.AreaService;
import net.shopxx.service.DeliveryCorpService;
import net.shopxx.service.MemberService;
import net.shopxx.service.OrderService;
import net.shopxx.service.OrderShippingService;
import net.shopxx.service.PaymentMethodService;
import net.shopxx.service.ShippingMethodService;
import net.shopxx.util.SystemUtils;

/**
 * Controller - 订单
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController extends BaseController {

	@Inject
	private AreaService areaService;
	@Inject
	private OrderService orderService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private PaymentMethodService paymentMethodService;
	@Inject
	private DeliveryCorpService deliveryCorpService;
	@Inject
	private OrderShippingService orderShippingService;
	@Inject
	private MemberService memberService;

	/**
	 * 获取订单锁
	 */
	@PostMapping("/acquire_lock")
	public @ResponseBody boolean acquireLock(Long id) {
		Order order = orderService.find(id);
		return order != null && orderService.acquireLock(order);
	}

	/**
	 * 计算
	 */
	@PostMapping("/calculate")
	public @ResponseBody Map<String, Object> calculate(Long id, BigDecimal freight, BigDecimal tax, BigDecimal offsetAmount) {
		Map<String, Object> data = new HashMap<>();
		Order order = orderService.find(id);
		if (order == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("amount", orderService.calculateAmount(order.getPrice(), order.getFee(), freight, tax, order.getPromotionDiscount(), order.getCouponDiscount(), offsetAmount));
		return data;
	}

	/**
	 * 物流动态
	 */
	@GetMapping("/transit_step")
	public @ResponseBody Map<String, Object> transitStep(Long orderShippingId) {
		Map<String, Object> data = new HashMap<>();
		OrderShipping orderShipping = orderShippingService.find(orderShippingId);
		if (orderShipping == null) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		Setting setting = SystemUtils.getSetting();
		if (StringUtils.isEmpty(setting.getKuaidi100Key()) || StringUtils.isEmpty(setting.getKuaidi100Customer()) || StringUtils.isEmpty(orderShipping.getDeliveryCorpCode()) || StringUtils.isEmpty(orderShipping.getTrackingNo())) {
			data.put("message", ERROR_MESSAGE);
			return data;
		}
		data.put("message", SUCCESS_MESSAGE);
		data.put("transitSteps", orderShippingService.getTransitSteps(orderShipping));
		return data;
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		Order order = orderService.find(id);
		if (order == null || order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatus()) && !Order.Status.pendingReview.equals(order.getStatus()))) {
			return ERROR_VIEW;
		}
		model.addAttribute("paymentMethods", paymentMethodService.findAll());
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		model.addAttribute("order", order);
		return "admin/order/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Long id, Long areaId, Long paymentMethodId, Long shippingMethodId, BigDecimal freight, BigDecimal tax, BigDecimal offsetAmount, Long rewardPoint, String consignee, String address, String zipCode, String phone, String invoiceTitle, String memo,
			RedirectAttributes redirectAttributes) {
		Area area = areaService.find(areaId);
		PaymentMethod paymentMethod = paymentMethodService.find(paymentMethodId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);

		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || (!Order.Status.pendingPayment.equals(order.getStatus()) && !Order.Status.pendingReview.equals(order.getStatus()))) {
			return ERROR_VIEW;
		}
		Invoice invoice = StringUtils.isNotEmpty(invoiceTitle) ? new Invoice(invoiceTitle, null) : null;
		order.setTax(invoice != null ? tax : BigDecimal.ZERO);
		order.setOffsetAmount(offsetAmount);
		order.setRewardPoint(rewardPoint);
		order.setMemo(memo);
		order.setInvoice(invoice);
		order.setPaymentMethod(paymentMethod);
		if (order.getIsDelivery()) {
			order.setFreight(freight);
			order.setConsignee(consignee);
			order.setAddress(address);
			order.setZipCode(zipCode);
			order.setPhone(phone);
			order.setArea(area);
			order.setShippingMethod(shippingMethod);
			if (!isValid(order, Order.Delivery.class)) {
				return ERROR_VIEW;
			}
		} else {
			order.setFreight(BigDecimal.ZERO);
			order.setConsignee(null);
			order.setAreaName(null);
			order.setAddress(null);
			order.setZipCode(null);
			order.setPhone(null);
			order.setShippingMethodName(null);
			order.setArea(null);
			order.setShippingMethod(null);
			if (!isValid(order)) {
				return ERROR_VIEW;
			}
		}
		orderService.modify(order);

		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 查看
	 */
	@GetMapping("/view")
	public String view(Long id, ModelMap model) {
		Setting setting = SystemUtils.getSetting();
		model.addAttribute("orderPaymentMethods", OrderPayment.Method.values());
		model.addAttribute("orderRefundsMethods", OrderRefunds.Method.values());
		model.addAttribute("paymentMethods", paymentMethodService.findAll());
		model.addAttribute("shippingMethods", shippingMethodService.findAll());
		model.addAttribute("deliveryCorps", deliveryCorpService.findAll());
		model.addAttribute("isKuaidi100Enabled", StringUtils.isNotEmpty(setting.getKuaidi100Key()) && StringUtils.isNotEmpty(setting.getKuaidi100Customer()));
		model.addAttribute("order", orderService.find(id));
		return "admin/order/view";
	}

	/**
	 * 审核
	 */
	@PostMapping("/review")
	public String review(Long id, Boolean passed, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || !Order.Status.pendingReview.equals(order.getStatus())) {
			return ERROR_VIEW;
		}
		orderService.review(order, passed);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + id;
	}

	/**
	 * 收款
	 */
	@PostMapping("/payment")
	public String payment(OrderPayment orderPayment, Long orderId, Long paymentMethodId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		orderPayment.setOrder(order);
		orderPayment.setPaymentMethod(paymentMethodService.find(paymentMethodId));
		if (!isValid(orderPayment)) {
			return ERROR_VIEW;
		}
		Member member = order.getMember();
		if (OrderPayment.Method.deposit.equals(orderPayment.getMethod()) && orderPayment.getAmount().compareTo(member.getBalance()) > 0) {
			return ERROR_VIEW;
		}
		orderPayment.setFee(BigDecimal.ZERO);
		orderService.payment(order, orderPayment);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 退款
	 */
	@PostMapping("/refunds")
	public String refunds(OrderRefunds orderRefunds, Long orderId, Long paymentMethodId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.getRefundableAmount().compareTo(BigDecimal.ZERO) <= 0) {
			return ERROR_VIEW;
		}
		orderRefunds.setOrder(order);
		orderRefunds.setPaymentMethod(paymentMethodService.find(paymentMethodId));
		if (!isValid(orderRefunds)) {
			return ERROR_VIEW;
		}
		orderService.refunds(order, orderRefunds);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 发货
	 */
	@PostMapping("/shipping")
	public String shipping(OrderShipping orderShipping, Long orderId, Long shippingMethodId, Long deliveryCorpId, Long areaId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.getShippableQuantity() <= 0) {
			return ERROR_VIEW;
		}
		boolean isDelivery = false;
		for (Iterator<OrderShippingItem> iterator = orderShipping.getOrderShippingItems().iterator(); iterator.hasNext();) {
			OrderShippingItem orderShippingItem = iterator.next();
			if (orderShippingItem == null || StringUtils.isEmpty(orderShippingItem.getSn()) || orderShippingItem.getQuantity() == null || orderShippingItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(orderShippingItem.getSn());
			if (orderItem == null || orderShippingItem.getQuantity() > orderItem.getShippableQuantity()) {
				return ERROR_VIEW;
			}
			Sku sku = orderItem.getSku();
			if (sku != null && orderShippingItem.getQuantity() > sku.getStock()) {
				return ERROR_VIEW;
			}
			orderShippingItem.setName(orderItem.getName());
			orderShippingItem.setIsDelivery(orderItem.getIsDelivery());
			orderShippingItem.setSku(sku);
			orderShippingItem.setOrderShipping(orderShipping);
			orderShippingItem.setSpecifications(orderItem.getSpecifications());
			if (orderItem.getIsDelivery()) {
				isDelivery = true;
			}
		}
		orderShipping.setOrder(order);
		orderShipping.setShippingMethod(shippingMethodService.find(shippingMethodId));
		orderShipping.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		orderShipping.setArea(areaService.find(areaId));
		if (isDelivery) {
			if (!isValid(orderShipping, OrderShipping.Delivery.class)) {
				return ERROR_VIEW;
			}
		} else {
			orderShipping.setShippingMethod((String) null);
			orderShipping.setDeliveryCorp((String) null);
			orderShipping.setDeliveryCorpUrl(null);
			orderShipping.setDeliveryCorpCode(null);
			orderShipping.setTrackingNo(null);
			orderShipping.setFreight(null);
			orderShipping.setConsignee(null);
			orderShipping.setArea((String) null);
			orderShipping.setAddress(null);
			orderShipping.setZipCode(null);
			orderShipping.setPhone(null);
			if (!isValid(orderShipping)) {
				return ERROR_VIEW;
			}
		}
		orderService.shipping(order, orderShipping);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 退货
	 */
	@PostMapping("/returns")
	public String returns(OrderReturns orderReturns, Long orderId, Long shippingMethodId, Long deliveryCorpId, Long areaId, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(orderId);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.getReturnableQuantity() <= 0) {
			return ERROR_VIEW;
		}
		for (Iterator<OrderReturnsItem> iterator = orderReturns.getOrderReturnsItems().iterator(); iterator.hasNext();) {
			OrderReturnsItem orderReturnsItem = iterator.next();
			if (orderReturnsItem == null || StringUtils.isEmpty(orderReturnsItem.getSn()) || orderReturnsItem.getQuantity() == null || orderReturnsItem.getQuantity() <= 0) {
				iterator.remove();
				continue;
			}
			OrderItem orderItem = order.getOrderItem(orderReturnsItem.getSn());
			if (orderItem == null || orderReturnsItem.getQuantity() > orderItem.getReturnableQuantity()) {
				return ERROR_VIEW;
			}
			orderReturnsItem.setName(orderItem.getName());
			orderReturnsItem.setOrderReturns(orderReturns);
			orderReturnsItem.setSpecifications(orderItem.getSpecifications());
		}
		orderReturns.setOrder(order);
		orderReturns.setShippingMethod(shippingMethodService.find(shippingMethodId));
		orderReturns.setDeliveryCorp(deliveryCorpService.find(deliveryCorpId));
		orderReturns.setArea(areaService.find(areaId));
		if (!isValid(orderReturns)) {
			return ERROR_VIEW;
		}
		orderService.returns(order, orderReturns);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + orderId;
	}

	/**
	 * 收货
	 */
	@PostMapping("/receive")
	public String receive(Long id, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || !Order.Status.shipped.equals(order.getStatus())) {
			return ERROR_VIEW;
		}
		orderService.receive(order);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + id;
	}

	/**
	 * 完成
	 */
	@PostMapping("/complete")
	public String complete(Long id, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || !Order.Status.received.equals(order.getStatus())) {
			return ERROR_VIEW;
		}
		orderService.complete(order);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + id;
	}

	/**
	 * 失败
	 */
	@PostMapping("/fail")
	public String fail(Long id, RedirectAttributes redirectAttributes) {
		Order order = orderService.find(id);
		if (order == null || !orderService.acquireLock(order)) {
			return ERROR_VIEW;
		}
		if (order.hasExpired() || (!Order.Status.pendingShipment.equals(order.getStatus()) && !Order.Status.shipped.equals(order.getStatus()) && !Order.Status.received.equals(order.getStatus()))) {
			return ERROR_VIEW;
		}
		orderService.fail(order);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:view?id=" + id;
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Order.Type type, Order.Status status, String memberUsername, Boolean isPendingReceive, Boolean isPendingRefunds, Boolean isAllocatedStock, Boolean hasExpired, Pageable pageable, ModelMap model) {
		model.addAttribute("types", Order.Type.values());
		model.addAttribute("statuses", Order.Status.values());
		model.addAttribute("type", type);
		model.addAttribute("status", status);
		model.addAttribute("memberUsername", memberUsername);
		model.addAttribute("isPendingReceive", isPendingReceive);
		model.addAttribute("isPendingRefunds", isPendingRefunds);
		model.addAttribute("isAllocatedStock", isAllocatedStock);
		model.addAttribute("hasExpired", hasExpired);

		Member member = memberService.findByUsername(memberUsername);
		if (StringUtils.isNotEmpty(memberUsername) && member == null) {
			model.addAttribute("page", Page.emptyPage(pageable));
		} else {
			model.addAttribute("page", orderService.findPage(type, status, member, null, isPendingReceive, isPendingRefunds, null, null, isAllocatedStock, hasExpired, pageable));
		}
		return "admin/order/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Order order = orderService.find(id);
				if (order != null && !orderService.acquireLock(order)) {
					return Message.error("admin.order.deleteLockedNotAllowed", order.getSn());
				}
			}
			orderService.delete(ids);
		}
		return SUCCESS_MESSAGE;
	}

}