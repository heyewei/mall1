/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.entity.DeliveryCenter;

/**
 * Dao - 发货点
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface DeliveryCenterDao extends BaseDao<DeliveryCenter, Long> {

	/**
	 * 查找默认发货点
	 * 
	 * @return 默认发货点，若不存在则返回null
	 */
	DeliveryCenter findDefault();

	/**
	 * 清除默认
	 */
	void clearDefault();

	/**
	 * 清除默认
	 * 
	 * @param exclude
	 *            排除发货点
	 */
	void clearDefault(DeliveryCenter exclude);

}