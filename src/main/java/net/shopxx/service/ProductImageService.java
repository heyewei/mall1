/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service;

import java.util.List;

import net.shopxx.entity.ProductImage;

/**
 * Service - 商品图片
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface ProductImageService {

	/**
	 * 商品图片过滤
	 * 
	 * @param productImages
	 *            商品图片
	 */
	void filter(List<ProductImage> productImages);

	/**
	 * 商品图片文件验证
	 * 
	 * @param productImage
	 *            商品图片
	 * @return 是否验证通过
	 */
	boolean isValid(ProductImage productImage);

	/**
	 * 生成商品图片(异步)
	 * 
	 * @param productImage
	 *            商品图片
	 */
	void generate(ProductImage productImage);

	/**
	 * 生成商品图片(异步)
	 * 
	 * @param productImages
	 *            商品图片
	 */
	void generate(List<ProductImage> productImages);

}