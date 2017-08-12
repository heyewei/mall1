/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.dao;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.DepositLog;
import net.shopxx.entity.Member;

/**
 * Dao - 预存款记录
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
public interface DepositLogDao extends BaseDao<DepositLog, Long> {

	/**
	 * 查找预存款记录分页
	 * 
	 * @param member
	 *            会员
	 * @param pageable
	 *            分页信息
	 * @return 预存款记录分页
	 */
	Page<DepositLog> findPage(Member member, Pageable pageable);

}