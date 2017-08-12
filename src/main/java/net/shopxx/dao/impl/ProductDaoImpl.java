/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.Setting;
import net.shopxx.dao.ProductDao;
import net.shopxx.entity.Attribute;
import net.shopxx.entity.Brand;
import net.shopxx.entity.Product;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.ProductTag;
import net.shopxx.entity.Promotion;
import net.shopxx.entity.Sku;
import net.shopxx.util.SystemUtils;

/**
 * Dao - 商品
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class ProductDaoImpl extends BaseDaoImpl<Product, Long> implements ProductDao {

	public List<Product> findList(Product.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, ProductTag productTag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (productCategory != null) {
			Subquery<ProductCategory> subquery = criteriaQuery.subquery(ProductCategory.class);
			Root<ProductCategory> subqueryRoot = subquery.from(ProductCategory.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.or(criteriaBuilder.equal(subqueryRoot, productCategory), criteriaBuilder.like(subqueryRoot.<String>get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("productCategory")).value(subquery));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.join("promotions"), promotion));
		}
		if (productTag != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.join("productTags"), productTag));
		}
		if (attributeValueMap != null) {
			for (Map.Entry<Attribute, String> entry : attributeValueMap.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(BigDecimal.ZERO) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number>get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(BigDecimal.ZERO) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number>get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isOutOfStock != null) {
			Subquery<Sku> subquery = criteriaQuery.subquery(Sku.class);
			Root<Sku> subqueryRoot = subquery.from(Sku.class);
			subquery.select(subqueryRoot);
			Path<Integer> stock = subqueryRoot.get("stock");
			Path<Integer> allocatedStock = subqueryRoot.get("allocatedStock");
			if (isOutOfStock) {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.greaterThan(stock, allocatedStock));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (isStockAlert != null) {
			Subquery<Sku> subquery = criteriaQuery.subquery(Sku.class);
			Root<Sku> subqueryRoot = subquery.from(Sku.class);
			subquery.select(subqueryRoot);
			Path<Integer> stock = subqueryRoot.get("stock");
			Path<Integer> allocatedStock = subqueryRoot.get("allocatedStock");
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (hasPromotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(root.join("promotions")));
		}
		criteriaQuery.where(restrictions);
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case priceAsc:
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("price")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case priceDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("price")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case salesDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sales")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case scoreDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("score")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case dateDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdDate")));
				break;
			}
		} else if (CollectionUtils.isEmpty(orders)) {
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")), criteriaBuilder.desc(root.get("createdDate")));
		}
		return super.findList(criteriaQuery, null, count, filters, orders);
	}

	public List<Product> findList(ProductCategory productCategory, Boolean isMarketable, Date beginDate, Date endDate, Integer first, Integer count) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (productCategory != null) {
			Subquery<ProductCategory> subquery = criteriaQuery.subquery(ProductCategory.class);
			Root<ProductCategory> subqueryRoot = subquery.from(ProductCategory.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.or(criteriaBuilder.equal(subqueryRoot, productCategory), criteriaBuilder.like(subqueryRoot.<String>get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("productCategory")).value(subquery));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (beginDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("createdDate"), beginDate));
		}
		if (endDate != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(root.<Date>get("createdDate"), endDate));
		}
		criteriaQuery.where(restrictions);
		return super.findList(criteriaQuery, first, count);
	}

	public Page<Product> findPage(Product.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, ProductTag productTag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (productCategory != null) {
			Subquery<ProductCategory> subquery = criteriaQuery.subquery(ProductCategory.class);
			Root<ProductCategory> subqueryRoot = subquery.from(ProductCategory.class);
			subquery.select(subqueryRoot);
			subquery.where(criteriaBuilder.or(criteriaBuilder.equal(subqueryRoot, productCategory), criteriaBuilder.like(subqueryRoot.<String>get("treePath"), "%" + ProductCategory.TREE_PATH_SEPARATOR + productCategory.getId() + ProductCategory.TREE_PATH_SEPARATOR + "%")));
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.in(root.get("productCategory")).value(subquery));
		}
		if (brand != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("brand"), brand));
		}
		if (promotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.join("promotions"), promotion));
		}
		if (productTag != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.join("productTags"), productTag));
		}
		if (attributeValueMap != null) {
			for (Map.Entry<Attribute, String> entry : attributeValueMap.entrySet()) {
				String propertyName = Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + entry.getKey().getPropertyIndex();
				restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get(propertyName), entry.getValue()));
			}
		}
		if (startPrice != null && endPrice != null && startPrice.compareTo(endPrice) > 0) {
			BigDecimal temp = startPrice;
			startPrice = endPrice;
			endPrice = temp;
		}
		if (startPrice != null && startPrice.compareTo(BigDecimal.ZERO) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.ge(root.<Number>get("price"), startPrice));
		}
		if (endPrice != null && endPrice.compareTo(BigDecimal.ZERO) >= 0) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.le(root.<Number>get("price"), endPrice));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isOutOfStock != null) {
			Subquery<Sku> subquery = criteriaQuery.subquery(Sku.class);
			Root<Sku> subqueryRoot = subquery.from(Sku.class);
			subquery.select(subqueryRoot);
			Path<Integer> stock = subqueryRoot.get("stock");
			Path<Integer> allocatedStock = subqueryRoot.get("allocatedStock");
			if (isOutOfStock) {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.greaterThan(stock, allocatedStock));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (isStockAlert != null) {
			Subquery<Sku> subquery = criteriaQuery.subquery(Sku.class);
			Root<Sku> subqueryRoot = subquery.from(Sku.class);
			subquery.select(subqueryRoot);
			Path<Integer> stock = subqueryRoot.get("stock");
			Path<Integer> allocatedStock = subqueryRoot.get("allocatedStock");
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (hasPromotion != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.isNotNull(root.join("promotions")));
		}
		criteriaQuery.where(restrictions);
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case priceAsc:
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("price")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case priceDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("price")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case salesDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sales")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case scoreDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("score")), criteriaBuilder.desc(root.get("createdDate")));
				break;
			case dateDesc:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdDate")));
				break;
			}
		} else if (pageable == null || ((StringUtils.isEmpty(pageable.getOrderProperty()) || pageable.getOrderDirection() == null) && (CollectionUtils.isEmpty(pageable.getOrders())))) {
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("isTop")), criteriaBuilder.desc(root.get("createdDate")));
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public Page<Product> findPage(Product.RankingType rankingType, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		if (rankingType != null) {
			switch (rankingType) {
			case score:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("score")), criteriaBuilder.desc(root.get("scoreCount")));
				break;
			case scoreCount:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("scoreCount")), criteriaBuilder.desc(root.get("score")));
				break;
			case weekHits:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("weekHits")));
				break;
			case monthHits:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("monthHits")));
				break;
			case hits:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("hits")));
				break;
			case weekSales:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("weekSales")));
				break;
			case monthSales:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("monthSales")));
				break;
			case sales:
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sales")));
				break;
			}
		}
		return super.findPage(criteriaQuery, pageable);
	}

	public Long count(Product.Type type, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> root = criteriaQuery.from(Product.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (type != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("type"), type));
		}
		if (isMarketable != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isMarketable"), isMarketable));
		}
		if (isList != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isList"), isList));
		}
		if (isTop != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("isTop"), isTop));
		}
		if (isOutOfStock != null) {
			Subquery<Sku> subquery = criteriaQuery.subquery(Sku.class);
			Root<Sku> subqueryRoot = subquery.from(Sku.class);
			subquery.select(subqueryRoot);
			Path<Integer> stock = subqueryRoot.get("stock");
			Path<Integer> allocatedStock = subqueryRoot.get("allocatedStock");
			if (isOutOfStock) {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.lessThanOrEqualTo(stock, allocatedStock));
			} else {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.greaterThan(stock, allocatedStock));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		if (isStockAlert != null) {
			Subquery<Sku> subquery = criteriaQuery.subquery(Sku.class);
			Root<Sku> subqueryRoot = subquery.from(Sku.class);
			subquery.select(subqueryRoot);
			Path<Integer> stock = subqueryRoot.get("stock");
			Path<Integer> allocatedStock = subqueryRoot.get("allocatedStock");
			Setting setting = SystemUtils.getSetting();
			if (isStockAlert) {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.lessThanOrEqualTo(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			} else {
				subquery.where(criteriaBuilder.equal(subqueryRoot.get("product"), root), criteriaBuilder.greaterThan(stock, criteriaBuilder.sum(allocatedStock, setting.getStockAlertCount())));
			}
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.exists(subquery));
		}
		criteriaQuery.where(restrictions);
		return super.count(criteriaQuery, null);
	}

	public void clearAttributeValue(Attribute attribute) {
		if (attribute == null || attribute.getPropertyIndex() == null || attribute.getProductCategory() == null) {
			return;
		}

		String jpql = "update Product product set product." + Product.ATTRIBUTE_VALUE_PROPERTY_NAME_PREFIX + attribute.getPropertyIndex() + " = null where product.productCategory = :productCategory";
		entityManager.createQuery(jpql).setParameter("productCategory", attribute.getProductCategory()).executeUpdate();
	}

}