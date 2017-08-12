/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import net.shopxx.dao.DeliveryTemplateDao;
import net.shopxx.entity.DeliveryTemplate;

/**
 * Dao - 快递单模板
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class DeliveryTemplateDaoImpl extends BaseDaoImpl<DeliveryTemplate, Long> implements DeliveryTemplateDao {

	public DeliveryTemplate findDefault() {
		try {
			String jpql = "select deliveryTemplate from DeliveryTemplate deliveryTemplate where deliveryTemplate.isDefault = true";
			return entityManager.createQuery(jpql, DeliveryTemplate.class).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void clearDefault() {
		String jpql = "update DeliveryTemplate deliveryTemplate set deliveryTemplate.isDefault = false where deliveryTemplate.isDefault = true";
		entityManager.createQuery(jpql).executeUpdate();
	}

	public void clearDefault(DeliveryTemplate exclude) {
		Assert.notNull(exclude);

		String jpql = "update DeliveryTemplate deliveryTemplate set deliveryTemplate.isDefault = false where deliveryTemplate.isDefault = true and deliveryTemplate != :exclude";
		entityManager.createQuery(jpql).setParameter("exclude", exclude).executeUpdate();
	}

}