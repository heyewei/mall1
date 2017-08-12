/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Pageable;
import net.shopxx.entity.Sku;
import net.shopxx.entity.StockLog;
import net.shopxx.service.SkuService;
import net.shopxx.service.StockLogService;

/**
 * Controller - 库存
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminStockController")
@RequestMapping("/admin/stock")
public class StockController extends BaseController {

	@Inject
	private StockLogService stockLogService;
	@Inject
	private SkuService skuService;

	/**
	 * SKU选择
	 */
	@GetMapping("/sku_select")
	public @ResponseBody List<Map<String, Object>> skuSelect(@RequestParam("q") String keyword, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		List<Sku> skus = skuService.search(null, keyword, null, count);
		for (Sku sku : skus) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", sku.getId());
			item.put("sn", sku.getSn());
			item.put("name", sku.getName());
			item.put("stock", sku.getStock());
			item.put("allocatedStock", sku.getAllocatedStock());
			item.put("specifications", sku.getSpecifications());
			data.add(item);
		}
		return data;
	}

	/**
	 * 入库
	 */
	@GetMapping("/stock_in")
	public String stockIn(Long skuId, ModelMap model) {
		model.addAttribute("sku", skuService.find(skuId));
		return "admin/stock/stock_in";
	}

	/**
	 * 入库
	 */
	@PostMapping("/stock_in")
	public String stockIn(Long skuId, Integer quantity, String memo, RedirectAttributes redirectAttributes) {
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			return ERROR_VIEW;
		}
		if (quantity == null || quantity <= 0) {
			return ERROR_VIEW;
		}
		skuService.addStock(sku, quantity, StockLog.Type.stockIn, memo);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:log";
	}

	/**
	 * 出库
	 */
	@GetMapping("/stock_out")
	public String stockOut(Long skuId, ModelMap model) {
		model.addAttribute("sku", skuService.find(skuId));
		return "admin/stock/stock_out";
	}

	/**
	 * 出库
	 */
	@PostMapping("/stock_out")
	public String stockOut(Long skuId, Integer quantity, String memo, RedirectAttributes redirectAttributes) {
		Sku sku = skuService.find(skuId);
		if (sku == null) {
			return ERROR_VIEW;
		}
		if (quantity == null || quantity <= 0) {
			return ERROR_VIEW;
		}
		if (sku.getStock() - quantity < 0) {
			return ERROR_VIEW;
		}
		skuService.addStock(sku, -quantity, StockLog.Type.stockOut, memo);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:log";
	}

	/**
	 * 记录
	 */
	@GetMapping("/log")
	public String log(Pageable pageable, ModelMap model) {
		model.addAttribute("page", stockLogService.findPage(pageable));
		return "admin/stock/log";
	}

}