/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;

import net.shopxx.Setting;
import net.shopxx.dao.OrderShippingDao;
import net.shopxx.dao.SnDao;
import net.shopxx.entity.OrderShipping;
import net.shopxx.entity.Sn;
import net.shopxx.service.OrderShippingService;
import net.shopxx.util.JsonUtils;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.WebUtils;

/**
 * Service - 订单发货
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class OrderShippingServiceImpl extends BaseServiceImpl<OrderShipping, Long> implements OrderShippingService {

	@Inject
	private OrderShippingDao orderShippingDao;
	@Inject
	private SnDao snDao;

	@Transactional(readOnly = true)
	public OrderShipping findBySn(String sn) {
		return orderShippingDao.find("sn", StringUtils.lowerCase(sn));
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Cacheable("transitSteps")
	public List<Map<String, String>> getTransitSteps(OrderShipping orderShipping) {
		Setting setting = SystemUtils.getSetting();
		String kuaidi100Key = setting.getKuaidi100Key();
		String kuaidi100Customer = setting.getKuaidi100Customer();
		if (StringUtils.isEmpty(kuaidi100Key) || StringUtils.isEmpty(kuaidi100Customer) || StringUtils.isEmpty(orderShipping.getDeliveryCorpCode()) || StringUtils.isEmpty(orderShipping.getTrackingNo())) {
			return Collections.emptyList();
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("com", orderShipping.getDeliveryCorpCode());
		paramMap.put("num", orderShipping.getTrackingNo());
		String param = JsonUtils.toJson(paramMap);
		String sign = DigestUtils.md5Hex(param + kuaidi100Key + kuaidi100Customer).toUpperCase();

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("customer", kuaidi100Customer);
		parameterMap.put("sign", sign);
		parameterMap.put("param", param);
		String content = WebUtils.post("http://poll.kuaidi100.com/poll/query.do", parameterMap);
		Map<String, Object> data = JsonUtils.toObject(content, new TypeReference<Map<String, Object>>() {
		});

		if (!StringUtils.equals(String.valueOf(data.get("message")), "ok")) {
			return Collections.emptyList();
		}
		return (List<Map<String, String>>) data.get("data");
	}

	@Override
	@Transactional
	public OrderShipping save(OrderShipping orderShipping) {
		Assert.notNull(orderShipping);

		orderShipping.setSn(snDao.generate(Sn.Type.orderShipping));

		return super.save(orderShipping);
	}

}