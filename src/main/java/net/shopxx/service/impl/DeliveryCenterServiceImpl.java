/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.dao.DeliveryCenterDao;
import net.shopxx.entity.DeliveryCenter;
import net.shopxx.service.DeliveryCenterService;

/**
 * Service - 发货点
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class DeliveryCenterServiceImpl extends BaseServiceImpl<DeliveryCenter, Long> implements DeliveryCenterService {

	@Inject
	private DeliveryCenterDao deliveryCenterDao;

	@Transactional(readOnly = true)
	public DeliveryCenter findDefault() {
		return deliveryCenterDao.findDefault();
	}

	@Override
	@Transactional
	public DeliveryCenter save(DeliveryCenter deliveryCenter) {
		Assert.notNull(deliveryCenter);

		if (BooleanUtils.isTrue(deliveryCenter.getIsDefault())) {
			deliveryCenterDao.clearDefault();
		}
		return super.save(deliveryCenter);
	}

	@Override
	@Transactional
	public DeliveryCenter update(DeliveryCenter deliveryCenter) {
		Assert.notNull(deliveryCenter);

		DeliveryCenter pDeliveryCenter = super.update(deliveryCenter);
		if (BooleanUtils.isTrue(pDeliveryCenter.getIsDefault())) {
			deliveryCenterDao.clearDefault(pDeliveryCenter);
		}
		return pDeliveryCenter;
	}

}