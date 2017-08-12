/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.UniquePredicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.CommonAttributes;
import net.shopxx.Filter;
import net.shopxx.Order;
import net.shopxx.dao.AreaDao;
import net.shopxx.dao.MemberAttributeDao;
import net.shopxx.dao.MemberDao;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.service.MemberAttributeService;

/**
 * Service - 会员注册项
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class MemberAttributeServiceImpl extends BaseServiceImpl<MemberAttribute, Long> implements MemberAttributeService {

	@Inject
	private MemberAttributeDao memberAttributeDao;
	@Inject
	private MemberDao memberDao;
	@Inject
	private AreaDao areaDao;

	@Transactional(readOnly = true)
	public Integer findUnusedPropertyIndex() {
		return memberAttributeDao.findUnusedPropertyIndex();
	}

	@Transactional(readOnly = true)
	public List<MemberAttribute> findList(Boolean isEnabled, Integer count, List<Filter> filters, List<Order> orders) {
		return memberAttributeDao.findList(isEnabled, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "memberAttribute", condition = "#useCache")
	public List<MemberAttribute> findList(Boolean isEnabled, Integer count, List<Filter> filters, List<Order> orders, boolean useCache) {
		return memberAttributeDao.findList(isEnabled, count, filters, orders);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "memberAttribute", condition = "#useCache")
	public List<MemberAttribute> findList(Boolean isEnabled, boolean useCache) {
		return memberAttributeDao.findList(isEnabled, null, null, null);
	}

	@Transactional(readOnly = true)
	public boolean isValid(MemberAttribute memberAttribute, String[] values) {
		Assert.notNull(memberAttribute);
		Assert.notNull(memberAttribute.getType());

		Object value = toMemberAttributeValue(memberAttribute, values);
		if (BooleanUtils.isTrue(memberAttribute.getIsRequired()) && isEmpty(value)) {
			return false;
		}
		if (StringUtils.isNotEmpty(memberAttribute.getPattern()) && value instanceof String && StringUtils.isNotEmpty((String) value) && !Pattern.compile(memberAttribute.getPattern()).matcher((String) value).matches()) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public Object toMemberAttributeValue(MemberAttribute memberAttribute, String[] values) {
		Assert.notNull(memberAttribute);
		Assert.notNull(memberAttribute.getType());

		if (ArrayUtils.isEmpty(values)) {
			return null;
		}
		List<String> valueList = (List<String>) CollectionUtils.collect(Arrays.asList(values), new Transformer() {
			public Object transform(Object input) {
				return StringUtils.trim((String) input);
			}
		});

		String value = valueList.get(0);
		final List<String> options = memberAttribute.getOptions();
		switch (memberAttribute.getType()) {
		case name:
		case address:
		case zipCode:
		case phone:
		case text:
			return value;
		case gender:
			if (StringUtils.isNotEmpty(value)) {
				try {
					return Member.Gender.valueOf(value);
				} catch (IllegalArgumentException e) {
				}
			}
			break;
		case birth:
			if (StringUtils.isNotEmpty(value)) {
				try {
					return DateUtils.parseDate(value, CommonAttributes.DATE_PATTERNS);
				} catch (ParseException e) {
				}
			}
			break;
		case area:
			if (StringUtils.isNotEmpty(value)) {
				Long id = NumberUtils.toLong(value, -1L);
				return areaDao.find(id);
			}
			break;
		case select:
			if (CollectionUtils.isNotEmpty(options) && options.contains(value)) {
				return value;
			}
			break;
		case checkbox:
			if (CollectionUtils.isNotEmpty(options)) {
				return CollectionUtils.select(valueList, new AndPredicate(new UniquePredicate(), new Predicate() {
					public boolean evaluate(Object object) {
						return object != null && options.contains((String) object);
					}
				}));
			}
			break;
		}
		return null;
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public MemberAttribute save(MemberAttribute memberAttribute) {
		Assert.notNull(memberAttribute);

		Integer unusedPropertyIndex = memberAttributeDao.findUnusedPropertyIndex();
		Assert.notNull(unusedPropertyIndex);

		memberAttribute.setPropertyIndex(unusedPropertyIndex);

		return super.save(memberAttribute);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public MemberAttribute update(MemberAttribute memberAttribute) {
		return super.update(memberAttribute);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public MemberAttribute update(MemberAttribute memberAttribute, String... ignoreProperties) {
		return super.update(memberAttribute, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "memberAttribute", allEntries = true)
	public void delete(MemberAttribute memberAttribute) {
		if (memberAttribute != null) {
			memberDao.clearAttributeValue(memberAttribute);
		}

		super.delete(memberAttribute);
	}

	/**
	 * 判断是否为空
	 * 
	 * @param value
	 *            值
	 * @return 是否为空
	 */
	private boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof String) {
			return StringUtils.isBlank((String) value);
		}
		if (value instanceof Object[]) {
			return ArrayUtils.isEmpty((Object[]) value);
		}
		if (value instanceof Collection) {
			return CollectionUtils.isEmpty((Collection<?>) value);
		}
		if (value instanceof Map) {
			return MapUtils.isEmpty((Map<?, ?>) value);
		}
		return false;
	}

}