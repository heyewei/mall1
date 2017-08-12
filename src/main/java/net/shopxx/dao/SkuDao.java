/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import java.util.List;
import java.util.Set;

import net.shopxx.entity.Product;
import net.shopxx.entity.Sku;

/**
 * Dao - SKU
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface SkuDao extends BaseDao<Sku, Long> {

	/**
	 * 通过编号、名称查找SKU
	 * 
	 * @param type
	 *            类型
	 * @param keyword
	 *            关键词
	 * @param excludes
	 *            排除SKU
	 * @param count
	 *            数量
	 * @return SKU
	 */
	List<Sku> search(Product.Type type, String keyword, Set<Sku> excludes, Integer count);

}