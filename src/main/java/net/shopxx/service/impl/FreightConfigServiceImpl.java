/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.FreightConfigDao;
import net.shopxx.entity.Area;
import net.shopxx.entity.FreightConfig;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.service.FreightConfigService;

/**
 * Service - 运费配置
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class FreightConfigServiceImpl extends BaseServiceImpl<FreightConfig, Long> implements FreightConfigService {

	@Inject
	private FreightConfigDao freightConfigDao;

	@Transactional(readOnly = true)
	public boolean exists(ShippingMethod shippingMethod, Area area) {
		return freightConfigDao.exists(shippingMethod, area);
	}

	@Transactional(readOnly = true)
	public boolean unique(Long id, ShippingMethod shippingMethod, Area area) {
		return freightConfigDao.unique(id, shippingMethod, area);
	}

	@Transactional(readOnly = true)
	public Page<FreightConfig> findPage(ShippingMethod shippingMethod, Pageable pageable) {
		return freightConfigDao.findPage(shippingMethod, pageable);
	}

}