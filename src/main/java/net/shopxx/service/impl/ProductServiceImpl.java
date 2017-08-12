/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.Setting;
import net.shopxx.dao.AttributeDao;
import net.shopxx.dao.BrandDao;
import net.shopxx.dao.ProductCategoryDao;
import net.shopxx.dao.ProductDao;
import net.shopxx.dao.ProductTagDao;
import net.shopxx.dao.PromotionDao;
import net.shopxx.dao.SkuDao;
import net.shopxx.dao.SnDao;
import net.shopxx.dao.StockLogDao;
import net.shopxx.entity.Attribute;
import net.shopxx.entity.Brand;
import net.shopxx.entity.Product;
import net.shopxx.entity.ProductCategory;
import net.shopxx.entity.ProductTag;
import net.shopxx.entity.Promotion;
import net.shopxx.entity.Sku;
import net.shopxx.entity.Sn;
import net.shopxx.entity.SpecificationItem;
import net.shopxx.entity.StockLog;
import net.shopxx.service.ProductImageService;
import net.shopxx.service.ProductService;
import net.shopxx.service.SpecificationValueService;
import net.shopxx.util.SystemUtils;

/**
 * Service - 商品
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class ProductServiceImpl extends BaseServiceImpl<Product, Long> implements ProductService {

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private CacheManager cacheManager;
	@Inject
	private ProductDao productDao;
	@Inject
	private SkuDao skuDao;
	@Inject
	private SnDao snDao;
	@Inject
	private ProductCategoryDao productCategoryDao;
	@Inject
	private BrandDao brandDao;
	@Inject
	private PromotionDao promotionDao;
	@Inject
	private ProductTagDao productTagDao;
	@Inject
	private AttributeDao attributeDao;
	@Inject
	private StockLogDao stockLogDao;
	@Inject
	private SpecificationValueService specificationValueService;
	@Inject
	private ProductImageService productImageService;

	@Transactional(readOnly = true)
	public boolean snExists(String sn) {
		return productDao.exists("sn", sn, true);
	}

	@Transactional(readOnly = true)
	public Product findBySn(String sn) {
		return productDao.find("sn", sn, true);
	}

	@Transactional(readOnly = true)
	public List<Product> findList(Product.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, ProductTag productTag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders) {
		return productDao.findList(type, productCategory, brand, promotion, productTag, attributeValueMap, startPrice, endPrice, isMarketable, isList, isTop, isOutOfStock, isStockAlert, hasPromotion, orderType, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "product", condition = "#useCache")
	public List<Product> findList(Product.Type type, Long productCategoryId, Long brandId, Long promotionId, Long productTagId, Map<Long, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock,
			Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		ProductCategory productCategory = productCategoryDao.find(productCategoryId);
		if (productCategoryId != null && productCategory == null) {
			return Collections.emptyList();
		}
		Brand brand = brandDao.find(brandId);
		if (brandId != null && brand == null) {
			return Collections.emptyList();
		}
		Promotion promotion = promotionDao.find(promotionId);
		if (promotionId != null && promotion == null) {
			return Collections.emptyList();
		}
		ProductTag productTag = productTagDao.find(productTagId);
		if (productTagId != null && productTag == null) {
			return Collections.emptyList();
		}
		Map<Attribute, String> map = new HashMap<>();
		if (attributeValueMap != null) {
			for (Map.Entry<Long, String> entry : attributeValueMap.entrySet()) {
				Attribute attribute = attributeDao.find(entry.getKey());
				if (attribute != null) {
					map.put(attribute, entry.getValue());
				}
			}
		}
		if (MapUtils.isNotEmpty(attributeValueMap) && MapUtils.isEmpty(map)) {
			return Collections.emptyList();
		}
		return productDao.findList(type, productCategory, brand, promotion, productTag, map, startPrice, endPrice, isMarketable, isList, isTop, isOutOfStock, isStockAlert, hasPromotion, orderType, count, filters, orders);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(Product.Type type, ProductCategory productCategory, Brand brand, Promotion promotion, ProductTag productTag, Map<Attribute, String> attributeValueMap, BigDecimal startPrice, BigDecimal endPrice, Boolean isMarketable, Boolean isList, Boolean isTop,
			Boolean isOutOfStock, Boolean isStockAlert, Boolean hasPromotion, Product.OrderType orderType, Pageable pageable) {
		return productDao.findPage(type, productCategory, brand, promotion, productTag, attributeValueMap, startPrice, endPrice, isMarketable, isList, isTop, isOutOfStock, isStockAlert, hasPromotion, orderType, pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> findPage(Product.RankingType rankingType, Pageable pageable) {
		return productDao.findPage(rankingType, pageable);
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Page<Product> search(String keyword, BigDecimal startPrice, BigDecimal endPrice, Product.OrderType orderType, Pageable pageable) {
		if (StringUtils.isEmpty(keyword)) {
			return new Page<>();
		}

		if (pageable == null) {
			pageable = new Pageable();
		}

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Product.class).get();

		Query snPhraseQuery = queryBuilder.phrase().onField("sn").sentence(keyword).createQuery();
		Query namePhraseQuery = queryBuilder.phrase().withSlop(3).onField("name").sentence(keyword).createQuery();
		Query nameFuzzyQuery = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1).onField("name").ignoreAnalyzer().matching(keyword).createQuery();
		Query keywordPhraseQuery = queryBuilder.phrase().withSlop(3).onField("keyword").sentence(keyword).createQuery();
		Query keywordFuzzyQuery = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(1).onField("keyword").ignoreAnalyzer().matching(keyword).createQuery();
		Query introductionPhraseQuery = queryBuilder.phrase().withSlop(3).onField("introduction").sentence(keyword).createQuery();
		Query isMarketablePhraseQuery = queryBuilder.phrase().onField("isMarketable").sentence("true").createQuery();
		Query isListPhraseQuery = queryBuilder.phrase().onField("isList").sentence("true").createQuery();
		BooleanJunction<?> booleanJunction = queryBuilder.bool().must(queryBuilder.bool().should(snPhraseQuery).should(namePhraseQuery).should(nameFuzzyQuery).should(keywordPhraseQuery).should(keywordFuzzyQuery).should(introductionPhraseQuery).createQuery()).must(isMarketablePhraseQuery)
				.must(isListPhraseQuery);
		if (startPrice != null && endPrice != null) {
			Query priceRangeQuery = queryBuilder.range().onField("price").from(startPrice.doubleValue()).to(endPrice.doubleValue()).createQuery();
			booleanJunction = booleanJunction.must(priceRangeQuery);
		} else if (startPrice != null) {
			Query priceRangeQuery = queryBuilder.range().onField("price").above(startPrice.doubleValue()).createQuery();
			booleanJunction = booleanJunction.must(priceRangeQuery);
		} else if (endPrice != null) {
			Query priceRangeQuery = queryBuilder.range().onField("price").below(endPrice.doubleValue()).createQuery();
			booleanJunction = booleanJunction.must(priceRangeQuery);
		}
		FullTextQuery fullTextQuery = fullTextEntityManager.createFullTextQuery(booleanJunction.createQuery(), Product.class);

		SortField[] sortFields = null;
		if (orderType != null) {
			switch (orderType) {
			case topDesc:
				sortFields = new SortField[] { new SortField("isTop", SortField.Type.STRING, true), new SortField(null, SortField.Type.SCORE), new SortField("createdDate", SortField.Type.LONG, true) };
				break;
			case priceAsc:
				sortFields = new SortField[] { new SortField("price", SortField.Type.DOUBLE, false), new SortField("createdDate", SortField.Type.LONG, true) };
				break;
			case priceDesc:
				sortFields = new SortField[] { new SortField("price", SortField.Type.DOUBLE, true), new SortField("createdDate", SortField.Type.LONG, true) };
				break;
			case salesDesc:
				sortFields = new SortField[] { new SortField("sales", SortField.Type.LONG, true), new SortField("createdDate", SortField.Type.LONG, true) };
				break;
			case scoreDesc:
				sortFields = new SortField[] { new SortField("score", SortField.Type.FLOAT, true), new SortField("createdDate", SortField.Type.LONG, true) };
				break;
			case dateDesc:
				sortFields = new SortField[] { new SortField("createdDate", SortField.Type.LONG, true) };
				break;
			}
		} else {
			sortFields = new SortField[] { new SortField("isTop", SortField.Type.STRING, true), new SortField(null, SortField.Type.SCORE), new SortField("createdDate", SortField.Type.LONG, true) };
		}
		fullTextQuery.setSort(new Sort(sortFields));
		fullTextQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
		fullTextQuery.setMaxResults(pageable.getPageSize());
		return new Page<>(fullTextQuery.getResultList(), fullTextQuery.getResultSize(), pageable);
	}

	@Transactional(readOnly = true)
	public Long count(Product.Type type, Boolean isMarketable, Boolean isList, Boolean isTop, Boolean isOutOfStock, Boolean isStockAlert) {
		return productDao.count(type, isMarketable, isList, isTop, isOutOfStock, isStockAlert);
	}

	public long viewHits(Long id) {
		Assert.notNull(id);

		Ehcache cache = cacheManager.getEhcache(Product.HITS_CACHE_NAME);
		cache.acquireWriteLockOnKey(id);
		try {
			Element element = cache.get(id);
			Long hits;
			if (element != null) {
				hits = (Long) element.getObjectValue() + 1;
			} else {
				Product product = productDao.find(id);
				if (product == null) {
					return 0L;
				}
				hits = product.getHits() + 1;
			}
			cache.put(new Element(id, hits));
			return hits;
		} finally {
			cache.releaseWriteLockOnKey(id);
		}
	}

	public void addHits(Product product, long amount) {
		Assert.notNull(product);
		Assert.state(amount >= 0);

		if (amount == 0) {
			return;
		}

		if (!LockModeType.PESSIMISTIC_WRITE.equals(productDao.getLockMode(product))) {
			productDao.flush();
			productDao.refresh(product, LockModeType.PESSIMISTIC_WRITE);
		}

		Calendar nowCalendar = Calendar.getInstance();
		Calendar weekHitsCalendar = DateUtils.toCalendar(product.getWeekHitsDate());
		Calendar monthHitsCalendar = DateUtils.toCalendar(product.getMonthHitsDate());
		if (nowCalendar.get(Calendar.YEAR) > weekHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekHitsCalendar.get(Calendar.WEEK_OF_YEAR)) {
			product.setWeekHits(amount);
		} else {
			product.setWeekHits(product.getWeekHits() + amount);
		}
		if (nowCalendar.get(Calendar.YEAR) > monthHitsCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthHitsCalendar.get(Calendar.MONTH)) {
			product.setMonthHits(amount);
		} else {
			product.setMonthHits(product.getMonthHits() + amount);
		}
		product.setHits(product.getHits() + amount);
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		productDao.flush();
	}

	public void addSales(Product product, long amount) {
		Assert.notNull(product);
		Assert.state(amount >= 0);

		if (amount == 0) {
			return;
		}

		if (!LockModeType.PESSIMISTIC_WRITE.equals(productDao.getLockMode(product))) {
			productDao.flush();
			productDao.refresh(product, LockModeType.PESSIMISTIC_WRITE);
		}

		Calendar nowCalendar = Calendar.getInstance();
		Calendar weekSalesCalendar = DateUtils.toCalendar(product.getWeekSalesDate());
		Calendar monthSalesCalendar = DateUtils.toCalendar(product.getMonthSalesDate());
		if (nowCalendar.get(Calendar.YEAR) > weekSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.WEEK_OF_YEAR) > weekSalesCalendar.get(Calendar.WEEK_OF_YEAR)) {
			product.setWeekSales(amount);
		} else {
			product.setWeekSales(product.getWeekSales() + amount);
		}
		if (nowCalendar.get(Calendar.YEAR) > monthSalesCalendar.get(Calendar.YEAR) || nowCalendar.get(Calendar.MONTH) > monthSalesCalendar.get(Calendar.MONTH)) {
			product.setMonthSales(amount);
		} else {
			product.setMonthSales(product.getMonthSales() + amount);
		}
		product.setSales(product.getSales() + amount);
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		productDao.flush();
	}

	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public Product create(Product product, Sku sku) {
		Assert.notNull(product);
		Assert.isTrue(product.isNew());
		Assert.notNull(product.getType());
		Assert.isTrue(!product.hasSpecification());
		Assert.notNull(sku);
		Assert.isTrue(sku.isNew());
		Assert.state(!sku.hasSpecification());

		switch (product.getType()) {
		case general:
			sku.setExchangePoint(0L);
			break;
		case exchange:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			product.setPromotions(null);
			break;
		case gift:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			sku.setExchangePoint(0L);
			product.setPromotions(null);
			break;
		}
		if (sku.getMarketPrice() == null) {
			sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
		}
		if (sku.getRewardPoint() == null) {
			sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
		}
		sku.setAllocatedStock(0);
		sku.setIsDefault(true);
		sku.setProduct(product);
		sku.setSpecificationValues(null);
		sku.setCartItems(null);
		sku.setOrderItems(null);
		sku.setOrderShippingItems(null);
		sku.setProductNotifies(null);
		sku.setStockLogs(null);
		sku.setGiftPromotions(null);

		product.setPrice(sku.getPrice());
		product.setCost(sku.getCost());
		product.setMarketPrice(sku.getMarketPrice());
		product.setScore(0F);
		product.setTotalScore(0L);
		product.setScoreCount(0L);
		product.setHits(0L);
		product.setWeekHits(0L);
		product.setMonthHits(0L);
		product.setSales(0L);
		product.setWeekSales(0L);
		product.setMonthSales(0L);
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		product.setSpecificationItems(null);
		product.setReviews(null);
		product.setConsultations(null);
		product.setProductFavorites(null);
		product.setSkus(null);
		setValue(product);
		productDao.persist(product);

		setValue(sku);
		skuDao.persist(sku);
		stockIn(sku);

		return product;
	}

	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public Product create(Product product, List<Sku> skus) {
		Assert.notNull(product);
		Assert.isTrue(product.isNew());
		Assert.notNull(product.getType());
		Assert.isTrue(product.hasSpecification());
		Assert.notEmpty(skus);

		final List<SpecificationItem> specificationItems = product.getSpecificationItems();
		if (CollectionUtils.exists(skus, new Predicate() {
			private Set<List<Integer>> set = new HashSet<>();

			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku == null || !sku.isNew() || !sku.hasSpecification() || !set.add(sku.getSpecificationValueIds()) || !specificationValueService.isValid(specificationItems, sku.getSpecificationValues());
			}
		})) {
			throw new IllegalArgumentException();
		}

		Sku defaultSku = (Sku) CollectionUtils.find(skus, new Predicate() {
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsDefault();
			}
		});
		if (defaultSku == null) {
			defaultSku = skus.get(0);
			defaultSku.setIsDefault(true);
		}

		for (Sku sku : skus) {
			switch (product.getType()) {
			case general:
				sku.setExchangePoint(0L);
				break;
			case exchange:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				product.setPromotions(null);
				break;
			case gift:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				sku.setExchangePoint(0L);
				product.setPromotions(null);
				break;
			}
			if (sku.getMarketPrice() == null) {
				sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
			}
			if (sku.getRewardPoint() == null) {
				sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
			}
			if (sku != defaultSku) {
				sku.setIsDefault(false);
			}
			sku.setAllocatedStock(0);
			sku.setProduct(product);
			sku.setCartItems(null);
			sku.setOrderItems(null);
			sku.setOrderShippingItems(null);
			sku.setProductNotifies(null);
			sku.setStockLogs(null);
			sku.setGiftPromotions(null);
		}

		product.setPrice(defaultSku.getPrice());
		product.setCost(defaultSku.getCost());
		product.setMarketPrice(defaultSku.getMarketPrice());
		product.setScore(0F);
		product.setTotalScore(0L);
		product.setScoreCount(0L);
		product.setHits(0L);
		product.setWeekHits(0L);
		product.setMonthHits(0L);
		product.setSales(0L);
		product.setWeekSales(0L);
		product.setMonthSales(0L);
		product.setWeekHitsDate(new Date());
		product.setMonthHitsDate(new Date());
		product.setWeekSalesDate(new Date());
		product.setMonthSalesDate(new Date());
		product.setReviews(null);
		product.setConsultations(null);
		product.setProductFavorites(null);
		product.setSkus(null);
		setValue(product);
		productDao.persist(product);

		for (Sku sku : skus) {
			setValue(sku);
			skuDao.persist(sku);
			stockIn(sku);
		}

		return product;
	}

	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public Product modify(Product product, Sku sku) {
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.isTrue(!product.hasSpecification());
		Assert.notNull(sku);
		Assert.isTrue(sku.isNew());
		Assert.state(!sku.hasSpecification());

		Product pProduct = productDao.find(product.getId());
		switch (pProduct.getType()) {
		case general:
			sku.setExchangePoint(0L);
			break;
		case exchange:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			product.setPromotions(null);
			break;
		case gift:
			sku.setPrice(BigDecimal.ZERO);
			sku.setRewardPoint(0L);
			sku.setExchangePoint(0L);
			product.setPromotions(null);
			break;
		}
		if (sku.getMarketPrice() == null) {
			sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
		}
		if (sku.getRewardPoint() == null) {
			sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
		}
		sku.setAllocatedStock(0);
		sku.setIsDefault(true);
		sku.setProduct(pProduct);
		sku.setSpecificationValues(null);
		sku.setCartItems(null);
		sku.setOrderItems(null);
		sku.setOrderShippingItems(null);
		sku.setProductNotifies(null);
		sku.setStockLogs(null);
		sku.setGiftPromotions(null);

		if (pProduct.hasSpecification()) {
			for (Sku pSku : pProduct.getSkus()) {
				skuDao.remove(pSku);
			}
			if (sku.getStock() == null) {
				throw new IllegalArgumentException();
			}
			setValue(sku);
			skuDao.persist(sku);
			stockIn(sku);
		} else {
			Sku defaultSku = pProduct.getDefaultSku();
			defaultSku.setPrice(sku.getPrice());
			defaultSku.setCost(sku.getCost());
			defaultSku.setMarketPrice(sku.getMarketPrice());
			defaultSku.setRewardPoint(sku.getRewardPoint());
			defaultSku.setExchangePoint(sku.getExchangePoint());
		}

		product.setPrice(sku.getPrice());
		product.setCost(sku.getCost());
		product.setMarketPrice(sku.getMarketPrice());
		setValue(product);
		copyProperties(product, pProduct, "sn", "type", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "reviews", "consultations", "productFavorites", "skus");

		return pProduct;
	}

	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public Product modify(Product product, List<Sku> skus) {
		Assert.notNull(product);
		Assert.isTrue(!product.isNew());
		Assert.isTrue(product.hasSpecification());
		Assert.notEmpty(skus);

		final List<SpecificationItem> specificationItems = product.getSpecificationItems();
		if (CollectionUtils.exists(skus, new Predicate() {
			private Set<List<Integer>> set = new HashSet<>();

			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku == null || !sku.isNew() || !sku.hasSpecification() || !set.add(sku.getSpecificationValueIds()) || !specificationValueService.isValid(specificationItems, sku.getSpecificationValues());
			}
		})) {
			throw new IllegalArgumentException();
		}

		Sku defaultSku = (Sku) CollectionUtils.find(skus, new Predicate() {
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsDefault();
			}
		});
		if (defaultSku == null) {
			defaultSku = skus.get(0);
			defaultSku.setIsDefault(true);
		}

		Product pProduct = productDao.find(product.getId());
		for (Sku sku : skus) {
			switch (pProduct.getType()) {
			case general:
				sku.setExchangePoint(0L);
				break;
			case exchange:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				product.setPromotions(null);
				break;
			case gift:
				sku.setPrice(BigDecimal.ZERO);
				sku.setRewardPoint(0L);
				sku.setExchangePoint(0L);
				product.setPromotions(null);
				break;
			}
			if (sku.getMarketPrice() == null) {
				sku.setMarketPrice(calculateDefaultMarketPrice(sku.getPrice()));
			}
			if (sku.getRewardPoint() == null) {
				sku.setRewardPoint(calculateDefaultRewardPoint(sku.getPrice()));
			}
			if (sku != defaultSku) {
				sku.setIsDefault(false);
			}
			sku.setAllocatedStock(0);
			sku.setProduct(pProduct);
			sku.setCartItems(null);
			sku.setOrderItems(null);
			sku.setOrderShippingItems(null);
			sku.setProductNotifies(null);
			sku.setStockLogs(null);
			sku.setGiftPromotions(null);
		}

		if (pProduct.hasSpecification()) {
			for (Sku pSku : pProduct.getSkus()) {
				if (!exists(skus, pSku.getSpecificationValueIds())) {
					skuDao.remove(pSku);
				}
			}
			for (Sku sku : skus) {
				Sku pSku = find(pProduct.getSkus(), sku.getSpecificationValueIds());
				if (pSku != null) {
					pSku.setPrice(sku.getPrice());
					pSku.setCost(sku.getCost());
					pSku.setMarketPrice(sku.getMarketPrice());
					pSku.setRewardPoint(sku.getRewardPoint());
					pSku.setExchangePoint(sku.getExchangePoint());
					pSku.setIsDefault(sku.getIsDefault());
					pSku.setSpecificationValues(sku.getSpecificationValues());
				} else {
					if (sku.getStock() == null) {
						throw new IllegalArgumentException();
					}
					setValue(sku);
					skuDao.persist(sku);
					stockIn(sku);
				}
			}
		} else {
			skuDao.remove(pProduct.getDefaultSku());
			for (Sku sku : skus) {
				if (sku.getStock() == null) {
					throw new IllegalArgumentException();
				}
				setValue(sku);
				skuDao.persist(sku);
				stockIn(sku);
			}
		}

		product.setPrice(defaultSku.getPrice());
		product.setCost(defaultSku.getCost());
		product.setMarketPrice(defaultSku.getMarketPrice());
		setValue(product);
		copyProperties(product, pProduct, "sn", "type", "score", "totalScore", "scoreCount", "hits", "weekHits", "monthHits", "sales", "weekSales", "monthSales", "weekHitsDate", "monthHitsDate", "weekSalesDate", "monthSalesDate", "reviews", "consultations", "productFavorites", "skus");
		return pProduct;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public Product save(Product product) {
		Assert.notNull(product);

		setValue(product);
		return super.save(product);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public Product update(Product product) {
		Assert.notNull(product);

		setValue(product);
		return super.update(product);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public Product update(Product product, String... ignoreProperties) {
		return super.update(product, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "product", "productCategory" }, allEntries = true)
	public void delete(Product product) {
		super.delete(product);
	}

	/**
	 * 设置商品值
	 * 
	 * @param product
	 *            商品
	 */
	private void setValue(Product product) {
		if (product == null) {
			return;
		}

		productImageService.generate(product.getProductImages());
		if (StringUtils.isEmpty(product.getImage()) && StringUtils.isNotEmpty(product.getThumbnail())) {
			product.setImage(product.getThumbnail());
		}
		if (product.isNew()) {
			if (StringUtils.isEmpty(product.getSn())) {
				String sn;
				do {
					sn = snDao.generate(Sn.Type.product);
				} while (snExists(sn));
				product.setSn(sn);
			}
		}
	}

	/**
	 * 设置SKU值
	 * 
	 * @param sku
	 *            SKU
	 */
	private void setValue(Sku sku) {
		if (sku == null) {
			return;
		}

		if (sku.isNew()) {
			Product product = sku.getProduct();
			if (product != null && StringUtils.isNotEmpty(product.getSn())) {
				String sn;
				int i = sku.hasSpecification() ? 1 : 0;
				do {
					sn = product.getSn() + (i == 0 ? "" : "_" + i);
					i++;
				} while (skuDao.exists("sn", sn, true));
				sku.setSn(sn);
			}
		}
	}

	/**
	 * 计算默认市场价
	 * 
	 * @param price
	 *            价格
	 * @return 默认市场价
	 */
	private BigDecimal calculateDefaultMarketPrice(BigDecimal price) {
		Assert.notNull(price);

		Setting setting = SystemUtils.getSetting();
		Double defaultMarketPriceScale = setting.getDefaultMarketPriceScale();
		return defaultMarketPriceScale != null ? setting.setScale(price.multiply(new BigDecimal(String.valueOf(defaultMarketPriceScale)))) : BigDecimal.ZERO;
	}

	/**
	 * 计算默认赠送积分
	 * 
	 * @param price
	 *            价格
	 * @return 默认赠送积分
	 */
	private long calculateDefaultRewardPoint(BigDecimal price) {
		Assert.notNull(price);

		Setting setting = SystemUtils.getSetting();
		Double defaultPointScale = setting.getDefaultPointScale();
		return defaultPointScale != null ? price.multiply(new BigDecimal(String.valueOf(defaultPointScale))).longValue() : 0L;
	}

	/**
	 * 根据规格值ID查找SKU
	 * 
	 * @param skus
	 *            SKU
	 * @param specificationValueIds
	 *            规格值ID
	 * @return SKU
	 */
	private Sku find(Collection<Sku> skus, final List<Integer> specificationValueIds) {
		if (CollectionUtils.isEmpty(skus) || CollectionUtils.isEmpty(specificationValueIds)) {
			return null;
		}

		return (Sku) CollectionUtils.find(skus, new Predicate() {
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getSpecificationValueIds() != null && sku.getSpecificationValueIds().equals(specificationValueIds);
			}
		});
	}

	/**
	 * 根据规格值ID判断SKU是否存在
	 * 
	 * @param skus
	 *            SKU
	 * @param specificationValueIds
	 *            规格值ID
	 * @return SKU是否存在
	 */
	private boolean exists(Collection<Sku> skus, final List<Integer> specificationValueIds) {
		return find(skus, specificationValueIds) != null;
	}

	/**
	 * 入库
	 * 
	 * @param sku
	 *            SKU
	 */
	private void stockIn(Sku sku) {
		if (sku == null || sku.getStock() == null || sku.getStock() <= 0) {
			return;
		}

		StockLog stockLog = new StockLog();
		stockLog.setType(StockLog.Type.stockIn);
		stockLog.setInQuantity(sku.getStock());
		stockLog.setOutQuantity(0);
		stockLog.setStock(sku.getStock());
		stockLog.setMemo(null);
		stockLog.setSku(sku);
		stockLogDao.persist(stockLog);
	}

}