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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Message;
import net.shopxx.Pageable;
import net.shopxx.entity.Attribute;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Brand;
import net.shopxx.entity.Parameter;
import net.shopxx.entity.Product;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.ProductTag;
import net.shopxx.entity.Promotion;
import net.shopxx.entity.Sku;
import net.shopxx.entity.Specification;
import net.shopxx.service.AttributeService;
import net.shopxx.service.BrandService;
import net.shopxx.service.ParameterValueService;
import net.shopxx.service.ProductCategoryService;
import net.shopxx.service.ProductImageService;
import net.shopxx.service.ProductService;
import net.shopxx.service.ProductTagService;
import net.shopxx.service.PromotionService;
import net.shopxx.service.SkuService;
import net.shopxx.service.SpecificationItemService;
import net.shopxx.service.SpecificationService;

/**
 * Controller - 商品
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminProductController")
@RequestMapping("/admin/product")
public class ProductController extends BaseController {

	@Inject
	private ProductService productService;
	@Inject
	private SkuService skuService;
	@Inject
	private ProductCategoryService productCategoryService;
	@Inject
	private BrandService brandService;
	@Inject
	private PromotionService promotionService;
	@Inject
	private ProductTagService productTagService;
	@Inject
	private ProductImageService productImageService;
	@Inject
	private ParameterValueService parameterValueService;
	@Inject
	private SpecificationItemService specificationItemService;
	@Inject
	private AttributeService attributeService;
	@Inject
	private SpecificationService specificationService;

	/**
	 * 检查编号是否存在
	 */
	@GetMapping("/check_sn")
	public @ResponseBody boolean checkSn(String sn) {
		return StringUtils.isNotEmpty(sn) && !productService.snExists(sn);
	}

	/**
	 * 获取参数
	 */
	@GetMapping("/parameters")
	public @ResponseBody List<Map<String, Object>> parameters(Long productCategoryId) {
		List<Map<String, Object>> data = new ArrayList<>();
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getParameters())) {
			return data;
		}
		for (Parameter parameter : productCategory.getParameters()) {
			Map<String, Object> item = new HashMap<>();
			item.put("group", parameter.getGroup());
			item.put("names", parameter.getNames());
			data.add(item);
		}
		return data;
	}

	/**
	 * 获取属性
	 */
	@GetMapping("/attributes")
	public @ResponseBody List<Map<String, Object>> attributes(Long productCategoryId) {
		List<Map<String, Object>> data = new ArrayList<>();
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getAttributes())) {
			return data;
		}
		for (Attribute attribute : productCategory.getAttributes()) {
			Map<String, Object> item = new HashMap<>();
			item.put("id", attribute.getId());
			item.put("name", attribute.getName());
			item.put("options", attribute.getOptions());
			data.add(item);
		}
		return data;
	}

	/**
	 * 获取规格
	 */
	@GetMapping("/specifications")
	public @ResponseBody List<Map<String, Object>> specifications(Long productCategoryId) {
		List<Map<String, Object>> data = new ArrayList<>();
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		if (productCategory == null || CollectionUtils.isEmpty(productCategory.getSpecifications())) {
			return data;
		}
		for (Specification specification : productCategory.getSpecifications()) {
			Map<String, Object> item = new HashMap<>();
			item.put("name", specification.getName());
			item.put("options", specification.getOptions());
			data.add(item);
		}
		return data;
	}

	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		model.addAttribute("types", Product.Type.values());
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("brands", brandService.findAll());
		model.addAttribute("promotions", promotionService.findAll());
		model.addAttribute("productTags", productTagService.findAll());
		model.addAttribute("specifications", specificationService.findAll());
		return "admin/product/add";
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(Product product, SkuForm skuForm, SkuListForm skuListForm, Long productCategoryId, Long brandId, Long[] promotionIds, Long[] productTagIds, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		productImageService.filter(product.getProductImages());
		parameterValueService.filter(product.getParameterValues());
		specificationItemService.filter(product.getSpecificationItems());
		skuService.filter(skuListForm.getSkuList());

		product.setProductCategory(productCategoryService.find(productCategoryId));
		product.setBrand(brandService.find(brandId));
		product.setPromotions(new HashSet<>(promotionService.findList(promotionIds)));
		product.setProductTags(new HashSet<>(productTagService.findList(productTagIds)));

		product.removeAttributeValue();
		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String value = request.getParameter("attribute_" + attribute.getId());
			String attributeValue = attributeService.toAttributeValue(attribute, value);
			product.setAttributeValue(attribute, attributeValue);
		}

		if (!isValid(product, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(product.getSn()) && productService.snExists(product.getSn())) {
			return ERROR_VIEW;
		}

		if (product.hasSpecification()) {
			List<Sku> skus = skuListForm.getSkuList();
			if (CollectionUtils.isEmpty(skus) || !isValid(skus, getValidationGroup(product.getType()), BaseEntity.Save.class)) {
				return ERROR_VIEW;
			}
			productService.create(product, skus);
		} else {
			Sku sku = skuForm.getSku();
			if (sku == null || !isValid(sku, getValidationGroup(product.getType()), BaseEntity.Save.class)) {
				return ERROR_VIEW;
			}
			productService.create(product, sku);
		}

		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		model.addAttribute("types", Product.Type.values());
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("brands", brandService.findAll());
		model.addAttribute("promotions", promotionService.findAll());
		model.addAttribute("productTags", productTagService.findAll());
		model.addAttribute("specifications", specificationService.findAll());
		model.addAttribute("product", productService.find(id));
		return "admin/product/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Product product, SkuForm skuForm, SkuListForm skuListForm, Long id, Long productCategoryId, Long brandId, Long[] promotionIds, Long[] productTagIds, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		productImageService.filter(product.getProductImages());
		parameterValueService.filter(product.getParameterValues());
		specificationItemService.filter(product.getSpecificationItems());
		skuService.filter(skuListForm.getSkuList());

		Product pProduct = productService.find(id);
		product.setType(pProduct.getType());
		product.setProductCategory(productCategoryService.find(productCategoryId));
		product.setBrand(brandService.find(brandId));
		product.setPromotions(new HashSet<>(promotionService.findList(promotionIds)));
		product.setProductTags(new HashSet<>(productTagService.findList(productTagIds)));

		product.removeAttributeValue();
		for (Attribute attribute : product.getProductCategory().getAttributes()) {
			String value = request.getParameter("attribute_" + attribute.getId());
			String attributeValue = attributeService.toAttributeValue(attribute, value);
			product.setAttributeValue(attribute, attributeValue);
		}

		if (!isValid(product, BaseEntity.Update.class)) {
			return ERROR_VIEW;
		}

		if (product.hasSpecification()) {
			List<Sku> skus = skuListForm.getSkuList();
			if (CollectionUtils.isEmpty(skus) || !isValid(skus, getValidationGroup(product.getType()), BaseEntity.Update.class)) {
				return ERROR_VIEW;
			}
			productService.modify(product, skus);
		} else {
			Sku sku = skuForm.getSku();
			if (sku == null || !isValid(sku, getValidationGroup(product.getType()), BaseEntity.Update.class)) {
				return ERROR_VIEW;
			}
			productService.modify(product, sku);
		}

		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Product.Type type, Long productCategoryId, Long brandId, Long promotionId, Long productTagId, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert, Pageable pageable, ModelMap model) {
		ProductCategory productCategory = productCategoryService.find(productCategoryId);
		Brand brand = brandService.find(brandId);
		Promotion promotion = promotionService.find(promotionId);
		ProductTag productTag = productTagService.find(productTagId);
		model.addAttribute("types", Product.Type.values());
		model.addAttribute("productCategoryTree", productCategoryService.findTree());
		model.addAttribute("brands", brandService.findAll());
		model.addAttribute("promotions", promotionService.findAll());
		model.addAttribute("productTags", productTagService.findAll());
		model.addAttribute("type", type);
		model.addAttribute("productCategoryId", productCategoryId);
		model.addAttribute("brandId", brandId);
		model.addAttribute("promotionId", promotionId);
		model.addAttribute("productTagId", productTagId);
		model.addAttribute("isMarketable", isMarketable);
		model.addAttribute("isList", isList);
		model.addAttribute("isTop", isTop);
		model.addAttribute("isOutOfStock", isOutOfStock);
		model.addAttribute("isStockAlert", isStockAlert);
		model.addAttribute("page", productService.findPage(type, productCategory, brand, promotion, productTag, null, null, null, isMarketable, isList, isTop, isOutOfStock, isStockAlert, null, null, pageable));
		return "admin/product/list";
	}

	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		productService.delete(ids);
		return SUCCESS_MESSAGE;
	}

	/**
	 * 根据类型获取验证组
	 * 
	 * @param type
	 *            类型
	 * @return 验证组
	 */
	private Class<?> getValidationGroup(Product.Type type) {
		Assert.notNull(type);

		switch (type) {
		case general:
			return Sku.General.class;
		case exchange:
			return Sku.Exchange.class;
		case gift:
			return Sku.Gift.class;
		}
		return null;
	}

	/**
	 * FormBean - SKU
	 * 
	 * @author SHOP++ Team
	 * @version 5.0.3
	 */
	public static class SkuForm {

		/**
		 * SKU
		 */
		private Sku sku;

		/**
		 * 获取SKU
		 * 
		 * @return SKU
		 */
		public Sku getSku() {
			return sku;
		}

		/**
		 * 设置SKU
		 * 
		 * @param sku
		 *            SKU
		 */
		public void setSku(Sku sku) {
			this.sku = sku;
		}

	}

	/**
	 * FormBean - SKU
	 * 
	 * @author SHOP++ Team
	 * @version 5.0.3
	 */
	public static class SkuListForm {

		/**
		 * SKU
		 */
		private List<Sku> skuList;

		/**
		 * 获取SKU
		 * 
		 * @return SKU
		 */
		public List<Sku> getSkuList() {
			return skuList;
		}

		/**
		 * 设置SKU
		 * 
		 * @param skuList
		 *            SKU
		 */
		public void setSkuList(List<Sku> skuList) {
			this.skuList = skuList;
		}

	}

}