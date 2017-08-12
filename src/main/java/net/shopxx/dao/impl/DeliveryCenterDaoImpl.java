/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao.impl;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import net.shopxx.dao.DeliveryCenterDao;
import net.shopxx.entity.DeliveryCenter;

/**
 * Dao - 发货点
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Repository
public class DeliveryCenterDaoImpl extends BaseDaoImpl<DeliveryCenter, Long> implements DeliveryCenterDao {

	public DeliveryCenter findDefault() {
		try {
			String jpql = "select deliveryCenter from DeliveryCenter deliveryCenter where deliveryCenter.isDefault = true";
			return entityManager.createQuery(jpql, DeliveryCenter.class).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void clearDefault() {
		String jpql = "update DeliveryCenter deliveryCenter set deliveryCenter.isDefault = false where deliveryCenter.isDefault = true";
		entityManager.createQuery(jpql).executeUpdate();
	}

	public void clearDefault(DeliveryCenter exclude) {
		Assert.notNull(exclude);

		String jpql = "update DeliveryCenter deliveryCenter set deliveryCenter.isDefault = false where deliveryCenter.isDefault = true and deliveryCenter != :exclude";
		entityManager.createQuery(jpql).setParameter("exclude", exclude).executeUpdate();
	}

}