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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Area;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.FreightConfig;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.service.AreaService;
import net.shopxx.service.FreightConfigService;
import net.shopxx.service.ShippingMethodService;

/**
 * Controller - 运费配置
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminFreightConfigController")
@RequestMapping("/admin/freight_config")
public class FreightConfigController extends BaseController {

	@Inject
	private FreightConfigService freightConfigService;
	@Inject
	private ShippingMethodService shippingMethodService;
	@Inject
	private AreaService areaService;

	/**
	 * 检查地区是否唯一
	 */
	@GetMapping("/check_area")
	public @ResponseBody boolean checkArea(Long id, Long shippingMethodId, Long areaId) {
		if (shippingMethodId == null || areaId == null) {
			return false;
		}
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		Area area = areaService.find(areaId);
		return freightConfigService.unique(id, shippingMethod, area);
	}

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(Long shippingMethodId, ModelMap model) {
		model.addAttribute("shippingMethod", shippingMethodService.find(shippingMethodId));
		return "admin/freight_config/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(FreightConfig freightConfig, Long shippingMethodId, Long areaId, RedirectAttributes redirectAttributes) {
		Area area = areaService.find(areaId);
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		freightConfig.setArea(area);
		freightConfig.setShippingMethod(shippingMethod);
		if (!isValid(freightConfig, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		if (freightConfigService.exists(shippingMethod, area)) {
			return ERROR_VIEW;
		}
		freightConfigService.save(freightConfig);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("freightConfig", freightConfigService.find(id));
		return "admin/freight_config/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(FreightConfig freightConfig, Long id, Long areaId, RedirectAttributes redirectAttributes) {
		Area area = areaService.find(areaId);
		freightConfig.setArea(area);
		if (!isValid(freightConfig, BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}
		FreightConfig pFreightConfig = freightConfigService.find(id);
		if (!freightConfigService.unique(id, pFreightConfig.getShippingMethod(), area)) {
			return ERROR_VIEW;
		}
		freightConfigService.update(freightConfig, "shippingMethod");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable, Long shippingMethodId, ModelMap model) {
		ShippingMethod shippingMethod = shippingMethodService.find(shippingMethodId);
		model.addAttribute("shippingMethod", shippingMethod);
		model.addAttribute("page", freightConfigService.findPage(shippingMethod, pageable));
		return "admin/freight_config/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		freightConfigService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}