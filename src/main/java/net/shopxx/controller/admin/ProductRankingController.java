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
import org.springframework.web.bind.annotation.RequestMapping;

import net.shopxx.Pageable;
import net.shopxx.entity.Product;
import net.shopxx.service.ProductService;

/**
 * Controller - 商品排名
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminProductRankingController")
@RequestMapping("/admin/product_ranking")
public class ProductRankingController extends BaseController {

	@Inject
	private ProductService productService;

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Product.RankingType rankingType, Pageable pageable, Model model) {
		if (rankingType == null) {
			rankingType = Product.RankingType.sales;
		}
		model.addAttribute("rankingTypes", Product.RankingType.values());
		model.addAttribute("rankingType", rankingType);
		model.addAttribute("page", productService.findPage(rankingType, pageable));
		return "admin/product_ranking/list";
	}

}