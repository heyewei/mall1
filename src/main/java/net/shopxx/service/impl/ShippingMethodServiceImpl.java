/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.Setting;
import net.shopxx.entity.Area;
import net.shopxx.entity.FreightConfig;
import net.shopxx.entity.Receiver;
import net.shopxx.entity.ShippingMethod;
import net.shopxx.service.ShippingMethodService;
import net.shopxx.util.SystemUtils;

/**
 * Service - 配送方式
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class ShippingMethodServiceImpl extends BaseServiceImpl<ShippingMethod, Long> implements ShippingMethodService {

	@Transactional(readOnly = true)
	public BigDecimal calculateFreight(ShippingMethod shippingMethod, Area area, Integer weight) {
		Assert.notNull(shippingMethod);

		Setting setting = SystemUtils.getSetting();
		BigDecimal firstPrice = shippingMethod.getDefaultFirstPrice();
		BigDecimal continuePrice = shippingMethod.getDefaultContinuePrice();
		if (area != null && CollectionUtils.isNotEmpty(shippingMethod.getFreightConfigs())) {
			List<Area> areas = new ArrayList<>();
			areas.addAll(area.getParents());
			areas.add(area);
			for (int i = areas.size() - 1; i >= 0; i--) {
				FreightConfig freightConfig = shippingMethod.getFreightConfig(areas.get(i));
				if (freightConfig != null) {
					firstPrice = freightConfig.getFirstPrice();
					continuePrice = freightConfig.getContinuePrice();
					break;
				}
			}
		}
		if (weight == null || weight <= shippingMethod.getFirstWeight() || continuePrice.compareTo(BigDecimal.ZERO) == 0) {
			return setting.setScale(firstPrice);
		} else {
			double contiuneWeightCount = Math.ceil((weight - shippingMethod.getFirstWeight()) / (double) shippingMethod.getContinueWeight());
			return setting.setScale(firstPrice.add(continuePrice.multiply(new BigDecimal(String.valueOf(contiuneWeightCount)))));
		}
	}

	@Transactional(readOnly = true)
	public BigDecimal calculateFreight(ShippingMethod shippingMethod, Receiver receiver, Integer weight) {
		return calculateFreight(shippingMethod, receiver != null ? receiver.getArea() : null, weight);
	}

}