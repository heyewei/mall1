/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.DeliveryCenter;
import net.shopxx.service.AreaService;
import net.shopxx.service.DeliveryCenterService;

/**
 * Controller - 发货点
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminDeliveryCenterController")
@RequestMapping("/admin/delivery_center")
public class DeliveryCenterController extends BaseController {

	@Inject
	private DeliveryCenterService deliveryCenterService;
	@Inject
	private AreaService areaService;

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add() {
		return "admin/delivery_center/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(DeliveryCenter deliveryCenter, Long areaId, Model model, RedirectAttributes redirectAttributes) {
		deliveryCenter.setArea(areaService.find(areaId));
		if (!isValid(deliveryCenter)) {
			return ERROR_VIEW;
		}
		deliveryCenter.setAreaName(null);
		deliveryCenterService.save(deliveryCenter);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, Model model) {
		model.addAttribute("deliveryCenter", deliveryCenterService.find(id));
		return "admin/delivery_center/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(DeliveryCenter deliveryCenter, Long areaId, RedirectAttributes redirectAttributes) {
		deliveryCenter.setArea(areaService.find(areaId));
		if (!isValid(deliveryCenter)) {
			return ERROR_VIEW;
		}
		deliveryCenterService.update(deliveryCenter, "areaName");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Model model, Pageable pageable) {
		model.addAttribute("page", deliveryCenterService.findPage(pageable));
		return "admin/delivery_center/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		deliveryCenterService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}