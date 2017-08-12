/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Product;
import net.shopxx.entity.Promotion;
import net.shopxx.entity.Sku;
import net.shopxx.service.CouponService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.PromotionService;
import net.shopxx.service.SkuService;

/**
 * Controller - 促销
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminPromotionController")
@RequestMapping("/admin/promotion")
public class PromotionController extends BaseController {

	@Inject
	private PromotionService promotionService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private SkuService skuService;
	@Inject
	private CouponService couponService;

	/**
	 * 检查价格运算表达式是否正确
	 */
	@GetMapping("/check_price_expression")
	public @ResponseBody boolean checkPriceExpression(String priceExpression) {
		return StringUtils.isNotEmpty(priceExpression) && promotionService.isValidPriceExpression(priceExpression);
	}

	/**
	 * 检查积分运算表达式是否正确
	 */
	@GetMapping("/check_point_expression")
	public @ResponseBody boolean checkPointExpression(String pointExpression) {
		return StringUtils.isNotEmpty(pointExpression) && promotionService.isValidPointExpression(pointExpression);
	}

	/**
	 * 赠品选择
	 */
	@GetMapping("/gift_select")
	public @ResponseBody List<Map<String, Object>> giftSelect(@RequestParam("q") String keyword, Long[] excludeIds, @RequestParam("limit") Integer count) {
		List<Map<String, Object>> data = new ArrayList<>();
		if (StringUtils.isEmpty(keyword)) {
			return data;
		}
		Set<Sku> excludes = new HashSet<>(skuService.findList(excludeIds));
		List<Sku> skus = skuService.search(Product.Type.gift, keyword, excludes, count);
		for (Sku sku : skus) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", sku.getId());
			item.put("sn", sku.getSn());
			item.put("name", sku.getName());
			item.put("specifications", sku.getSpecifications());
			item.put("path", sku.getPath());
			data.add(item);
		}
		return data;
	}

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("coupons", couponService.findAll());
		return "admin/promotion/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(Promotion promotion, Long[] memberRankIds, Long[] couponIds, Long[] giftIds, RedirectAttributes redirectAttributes) {
		promotion.setMemberRanks(new HashSet<>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new HashSet<>(couponService.findList(couponIds)));
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Sku> gifts = skuService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				public boolean evaluate(Object object) {
					Sku gift = (Sku) object;
					return gift != null && Product.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new HashSet<>(gifts));
		} else {
			promotion.setGifts(null);
		}
		if (!isValid(promotion)) {
			return ERROR_VIEW;
		}
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(promotion.getPriceExpression()) && !promotionService.isValidPriceExpression(promotion.getPriceExpression())) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(promotion.getPointExpression()) && !promotionService.isValidPointExpression(promotion.getPointExpression())) {
			return ERROR_VIEW;
		}
		promotion.setProducts(null);
		promotion.setProductCategories(null);
		promotionService.save(promotion);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("promotion", promotionService.find(id));
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("coupons", couponService.findAll());
		return "admin/promotion/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Promotion promotion, Long[] memberRankIds, Long[] couponIds, Long[] giftIds, RedirectAttributes redirectAttributes) {
		promotion.setMemberRanks(new HashSet<>(memberRankService.findList(memberRankIds)));
		promotion.setCoupons(new HashSet<>(couponService.findList(couponIds)));
		if (ArrayUtils.isNotEmpty(giftIds)) {
			List<Sku> gifts = skuService.findList(giftIds);
			CollectionUtils.filter(gifts, new Predicate() {
				public boolean evaluate(Object object) {
					Sku gift = (Sku) object;
					return gift != null && Product.Type.gift.equals(gift.getType());
				}
			});
			promotion.setGifts(new HashSet<>(gifts));
		} else {
			promotion.setGifts(null);
		}
		if (promotion.getBeginDate() != null && promotion.getEndDate() != null && promotion.getBeginDate().after(promotion.getEndDate())) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumQuantity() != null && promotion.getMaximumQuantity() != null && promotion.getMinimumQuantity() > promotion.getMaximumQuantity()) {
			return ERROR_VIEW;
		}
		if (promotion.getMinimumPrice() != null && promotion.getMaximumPrice() != null && promotion.getMinimumPrice().compareTo(promotion.getMaximumPrice()) > 0) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(promotion.getPriceExpression()) && !promotionService.isValidPriceExpression(promotion.getPriceExpression())) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(promotion.getPointExpression()) && !promotionService.isValidPointExpression(promotion.getPointExpression())) {
			return ERROR_VIEW;
		}
		promotionService.update(promotion, "products", "productCategories");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", promotionService.findPage(pageable));
		return "admin/promotion/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		promotionService.delete(ids);
		return SUCCESS_MESSAGE;
	}

}