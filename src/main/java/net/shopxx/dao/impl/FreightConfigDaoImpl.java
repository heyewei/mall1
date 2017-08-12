/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FreightConfigDao;
import net.shopxx.entity.Area;
import net.shopxx.entity.FreightConfig;
import net.shopxx.entity.ShippingMethod;

/**
 * Dao - 运费配置
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class FreightConfigDaoImpl extends BaseDaoImpl<FreightConfig, Long> implements FreightConfigDao {

	public boolean exists(ShippingMethod shippingMethod, Area area) {
		if (shippingMethod == null || area == null) {
			return true;
		}
		String jpql = "select count(*) from FreightConfig freightConfig where freightConfig.shippingMethod = :shippingMethod and freightConfig.area = :area";
		Long count = entityManager.createQuery(jpql, Long.class).setParameter("shippingMethod", shippingMethod).setParameter("area", area).getSingleResult();
		return count > 0;
	}

	public boolean unique(Long id, ShippingMethod shippingMethod, Area area) {
		if (shippingMethod == null || area == null) {
			return false;
		}
		if (id != null) {
			String jpql = "select count(*) from FreightConfig freightConfig where freightConfig.id != :id and freightConfig.shippingMethod = :shippingMethod and freightConfig.area = :area";
			Long count = entityManager.createQuery(jpql, Long.class).setParameter("id", id).setParameter("shippingMethod", shippingMethod).setParameter("area", area).getSingleResult();
			return count <= 0;
		} else {
			String jpql = "select count(*) from FreightConfig freightConfig where freightConfig.shippingMethod = :shippingMethod and freightConfig.area = :area";
			Long count = entityManager.createQuery(jpql, Long.class).setParameter("shippingMethod", shippingMethod).setParameter("area", area).getSingleResult();
			return count <= 0;
		}
	}

	public Page<FreightConfig> findPage(ShippingMethod shippingMethod, Pageable pageable) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<FreightConfig> criteriaQuery = criteriaBuilder.createQuery(FreightConfig.class);
		Root<FreightConfig> root = criteriaQuery.from(FreightConfig.class);
		criteriaQuery.select(root);
		Predicate restrictions = criteriaBuilder.conjunction();
		if (shippingMethod != null) {
			restrictions = criteriaBuilder.and(restrictions, criteriaBuilder.equal(root.get("shippingMethod"), shippingMethod));
		}
		criteriaQuery.where(restrictions);
		return super.findPage(criteriaQuery, pageable);
	}

}