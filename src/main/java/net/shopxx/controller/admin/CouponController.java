/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.ExcelView;
import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Coupon;
import net.shopxx.entity.CouponCode;
import net.shopxx.service.CouponCodeService;
import net.shopxx.service.CouponService;

/**
 * Controller - 优惠券
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminCouponController")
@RequestMapping("/admin/coupon")
public class CouponController extends BaseController {

	@Inject
	private CouponService couponService;
	@Inject
	private CouponCodeService couponCodeService;

	/**
	 * 检查价格运算表达式是否正确
	 */
	@GetMapping("/check_price_expression")
	public @ResponseBody boolean checkPriceExpression(String priceExpression) {
		return StringUtils.isNotEmpty(priceExpression) && couponService.isValidPriceExpression(priceExpression);
	}

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		return "admin/coupon/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(Coupon coupon, RedirectAttributes redirectAttributes) {
		if (!isValid(coupon)) {
			return ERROR_VIEW;
		}
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			return ERROR_VIEW;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			return ERROR_VIEW;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression()) && !couponService.isValidPriceExpression(coupon.getPriceExpression())) {
			return ERROR_VIEW;
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			return ERROR_VIEW;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}
		coupon.setCouponCodes(null);
		coupon.setPromotions(null);
		coupon.setOrders(null);
		couponService.save(coupon);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("coupon", couponService.find(id));
		return "admin/coupon/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Coupon coupon, RedirectAttributes redirectAttributes) {
		if (!isValid(coupon)) {
			return ERROR_VIEW;
		}
		if (coupon.getBeginDate() != null && coupon.getEndDate() != null && coupon.getBeginDate().after(coupon.getEndDate())) {
			return ERROR_VIEW;
		}
		if (coupon.getMinimumQuantity() != null && coupon.getMaximumQuantity() != null && coupon.getMinimumQuantity() > coupon.getMaximumQuantity()) {
			return ERROR_VIEW;
		}
		if (coupon.getMinimumPrice() != null && coupon.getMaximumPrice() != null && coupon.getMinimumPrice().compareTo(coupon.getMaximumPrice()) > 0) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(coupon.getPriceExpression()) && !couponService.isValidPriceExpression(coupon.getPriceExpression())) {
			return ERROR_VIEW;
		}
		if (coupon.getIsExchange() && coupon.getPoint() == null) {
			return ERROR_VIEW;
		}
		if (!coupon.getIsExchange()) {
			coupon.setPoint(null);
		}
		couponService.update(coupon, "couponCodes", "promotions", "orders");
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable, ModelMap model) {
		model.addAttribute("page", couponService.findPage(pageable));
		return "admin/coupon/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		couponService.delete(ids);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 生成优惠码
	 */
	@GetMapping("/generate")
	public String generate(Long id, ModelMap model) {
		Coupon coupon = couponService.find(id);
		model.addAttribute("coupon", coupon);
		model.addAttribute("totalCount", couponCodeService.count(coupon, null, null, null, null));
		model.addAttribute("usedCount", couponCodeService.count(coupon, null, null, null, true));
		return "admin/coupon/generate";
	}

	/**
	 * 下载优惠码
	 */
	@PostMapping("/download")
	public ModelAndView download(Long id, Integer count, ModelMap model) {
		if (count == null || count <= 0) {
			count = 100;
		}
		Coupon coupon = couponService.find(id);
		List<CouponCode> couponCodes = couponCodeService.generate(coupon, null, count);
		String filename = "coupon_code_" + DateFormatUtils.format(new Date(), "yyyyMM") + ".xls";
		model.addAttribute("coupon", coupon);
		model.addAttribute("couponCodes", couponCodes);
		model.addAttribute("date", new Date());
		return new ModelAndView(new ExcelView("admin/coupon/download.xls", filename), model);
	}

}