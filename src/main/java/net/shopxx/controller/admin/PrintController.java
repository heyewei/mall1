/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.entity.DeliveryCenter;
import net.shopxx.entity.DeliveryTemplate;
import net.shopxx.service.DeliveryCenterService;
import net.shopxx.service.DeliveryTemplateService;
import net.shopxx.service.OrderService;

/**
 * Controller - 打印
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminPrintController")
@RequestMapping("/admin/print")
public class PrintController extends BaseController {

	@Inject
	private OrderService orderService;
	@Inject
	private DeliveryTemplateService deliveryTemplateService;
	@Inject
	private DeliveryCenterService deliveryCenterService;

	/**
	 * 订单打印
	 */
	@GetMapping("/order")
	public String order(Long id, ModelMap model) {
		model.addAttribute("order", orderService.find(id));
		return "admin/print/order";
	}

	/**
	 * 购物单打印
	 */
	@GetMapping("/product")
	public String product(Long id, ModelMap model) {
		model.addAttribute("order", orderService.find(id));
		return "admin/print/product";
	}

	/**
	 * 发货单打印
	 */
	@GetMapping("/shipping")
	public String shipping(Long id, ModelMap model) {
		model.addAttribute("order", orderService.find(id));
		return "admin/print/shipping";
	}

	/**
	 * 快递单打印
	 */
	@GetMapping("/delivery")
	public String delivery(Long orderId, Long deliveryTemplateId, Long deliveryCenterId, ModelMap model) {
		DeliveryTemplate deliveryTemplate = deliveryTemplateService.find(deliveryTemplateId);
		DeliveryCenter deliveryCenter = deliveryCenterService.find(deliveryCenterId);
		if (deliveryTemplate == null) {
			deliveryTemplate = deliveryTemplateService.findDefault();
		}
		if (deliveryCenter == null) {
			deliveryCenter = deliveryCenterService.findDefault();
		}
		model.addAttribute("deliveryTemplates", deliveryTemplateService.findAll());
		model.addAttribute("deliveryCenters", deliveryCenterService.findAll());
		model.addAttribute("order", orderService.find(orderId));
		model.addAttribute("deliveryTemplate", deliveryTemplate);
		model.addAttribute("deliveryCenter", deliveryCenter);
		return "admin/print/delivery";
	}

}