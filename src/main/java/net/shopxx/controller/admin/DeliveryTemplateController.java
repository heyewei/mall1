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
import net.shopxx.entity.DeliveryTemplate;
import net.shopxx.service.DeliveryTemplateService;

/**
 * Controller - 快递单模板
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminDeliveryTemplateController")
@RequestMapping("/admin/delivery_template")
public class DeliveryTemplateController extends BaseController {

	@Inject
	private DeliveryTemplateService deliveryTemplateService;

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(Pageable pageable) {
		return "admin/delivery_template/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(DeliveryTemplate deliveryTemplate, RedirectAttributes redirectAttributes) {
		if (!isValid(deliveryTemplate)) {
			return ERROR_VIEW;
		}
		deliveryTemplateService.save(deliveryTemplate);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String eidt(Long id, Model model) {
		model.addAttribute("deliveryTemplate", deliveryTemplateService.find(id));
		return "admin/delivery_template/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String udpate(DeliveryTemplate deliveryTemplate, RedirectAttributes redirectAttributes) {
		if (!isValid(deliveryTemplate)) {
			return ERROR_VIEW;
		}
		deliveryTemplateService.update(deliveryTemplate);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable, Model model) {
		model.addAttribute("page", deliveryTemplateService.findPage(pageable));
		return "admin/delivery_template/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		deliveryTemplateService.delete(ids);
		return SUCCESS_MESSAGE;
	}
}