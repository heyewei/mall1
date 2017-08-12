/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.ProductNotify;
import net.shopxx.service.ProductNotifyService;

/**
 * Controller - 到货通知
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminProductNotifyntroller")
@RequestMapping("/admin/product_notify")
public class ProductNotifyController extends BaseController {

	@Inject
	private ProductNotifyService productNotifyService;

	/**
	 * 发送到货通知
	 */
	@PostMapping("/send")
	public @ResponseBody Message send(Long[] ids) {
		List<ProductNotify> productNotifies = productNotifyService.findList(ids);
		int count = productNotifyService.send(productNotifies);
		return Message.success("admin.productNotify.sentSuccess", count);
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Boolean isMarketable, Boolean isOutOfStock, Boolean hasSent, Pageable pageable, ModelMap model) {
		model.addAttribute("isMarketable", isMarketable);
		model.addAttribute("isOutOfStock", isOutOfStock);
		model.addAttribute("hasSent", hasSent);
		model.addAttribute("page", productNotifyService.findPage(null, isMarketable, isOutOfStock, hasSent, pageable));
		return "admin/product_notify/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		productNotifyService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}