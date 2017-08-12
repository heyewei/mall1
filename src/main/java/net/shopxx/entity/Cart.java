/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateUtils;

import net.shopxx.Setting;
import net.shopxx.util.JsonUtils;
import net.shopxx.util.SystemUtils;

/**
 * Entity - 购物车
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class Cart extends BaseEntity<Long> implements Iterable<CartItem> {

	private static final long serialVersionUID = -6565967051825794561L;

	/**
	 * 超时时间
	 */
	public static final int TIMEOUT = 604800;

	/**
	 * 最大购物车项数量
	 */
	public static final Integer MAX_CART_ITEM_SIZE = 100;

	/**
	 * "密钥"Cookie名称
	 */
	public static final String KEY_COOKIE_NAME = "cartKey";

	/**
	 * "标签"Cookie名称
	 */
	public static final String TAG_COOKIE_NAME = "cartTag";

	/**
	 * 密钥
	 */
	@Column(name = "cartKey", nullable = false, updatable = false, unique = true)
	private String key;

	/**
	 * 过期时间
	 */
	@Column(updatable = false)
	private Date expire;

	/**
	 * 会员
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable = false)
	private Member member;

	/**
	 * 购物车项
	 */
	@OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<CartItem> cartItems = new HashSet<>();

	/**
	 * 获取密钥
	 * 
	 * @return 密钥
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 设置密钥
	 * 
	 * @param key
	 *            密钥
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * 获取过期时间
	 * 
	 * @return 过期时间
	 */
	public Date getExpire() {
		return expire;
	}

	/**
	 * 设置过期时间
	 * 
	 * @param expire
	 *            过期时间
	 */
	public void setExpire(Date expire) {
		this.expire = expire;
	}

	/**
	 * 获取会员
	 * 
	 * @return 会员
	 */
	public Member getMember() {
		return member;
	}

	/**
	 * 设置会员
	 * 
	 * @param member
	 *            会员
	 */
	public void setMember(Member member) {
		this.member = member;
	}

	/**
	 * 获取购物车项
	 * 
	 * @return 购物车项
	 */
	public Set<CartItem> getCartItems() {
		return cartItems;
	}

	/**
	 * 设置购物车项
	 * 
	 * @param cartItems
	 *            购物车项
	 */
	public void setCartItems(Set<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	/**
	 * 获取商品重量
	 * 
	 * @return 商品重量
	 */
	@Transient
	public int getProductWeight() {
		int productWeight = 0;
		for (CartItem cartItem : this) {
			productWeight += cartItem.getWeight();
		}
		return productWeight;
	}

	/**
	 * 获取商品数量
	 * 
	 * @return 商品数量
	 */
	@Transient
	public int getProductQuantity() {
		int productQuantity = 0;
		for (CartItem cartItem : this) {
			if (cartItem.getQuantity() != null) {
				productQuantity += cartItem.getQuantity();
			}
		}
		return productQuantity;
	}

	/**
	 * 获取赠品重量
	 * 
	 * @return 赠品重量
	 */
	@Transient
	public int getGiftWeight() {
		int giftWeight = 0;
		for (Sku gift : getGifts()) {
			if (gift.getWeight() != null) {
				giftWeight += gift.getWeight();
			}
		}
		return giftWeight;
	}

	/**
	 * 获取赠品数量
	 * 
	 * @return 赠品数量
	 */
	@Transient
	public int getGiftQuantity() {
		return getGifts().size();
	}

	/**
	 * 获取总重量
	 * 
	 * @return 总重量
	 */
	@Transient
	public int getTotalWeight() {
		return getProductWeight() + getGiftWeight();
	}

	/**
	 * 获取总数量
	 * 
	 * @return 总数量
	 */
	@Transient
	public int getTotalQuantity() {
		return getProductQuantity() + getGiftQuantity();
	}

	/**
	 * 获取赠送积分
	 * 
	 * @return 赠送积分
	 */
	@Transient
	public long getRewardPoint() {
		long rewardPoint = 0L;
		for (CartItem cartItem : this) {
			rewardPoint += cartItem.getRewardPoint();
		}
		return rewardPoint;
	}

	/**
	 * 获取兑换积分
	 * 
	 * @return 兑换积分
	 */
	@Transient
	public long getExchangePoint() {
		long exchangePoint = 0L;
		for (CartItem cartItem : this) {
			exchangePoint += cartItem.getExchangePoint();
		}
		return exchangePoint;
	}

	/**
	 * 获取赠送积分增加值
	 * 
	 * @return 赠送积分增加值
	 */
	@Transient
	public long getAddedRewardPoint() {
		Map<CartItem, Long> cartItemRewardPointMap = new HashMap<>();
		for (CartItem cartItem : this) {
			cartItemRewardPointMap.put(cartItem, cartItem.getRewardPoint());
		}
		Long addedRewardPoint = 0L;
		for (Promotion promotion : getPromotions()) {
			long originalRewardPoint = 0;
			Set<CartItem> cartItems = getCartItems(promotion);
			for (CartItem cartItem : cartItems) {
				originalRewardPoint += cartItemRewardPointMap.get(cartItem);
			}
			int quantity = getQuantity(promotion);
			long currentRewardPoint = promotion.calculatePoint(originalRewardPoint, quantity);
			if (originalRewardPoint > 0) {
				BigDecimal rate = new BigDecimal(currentRewardPoint).divide(new BigDecimal(originalRewardPoint), RoundingMode.DOWN);
				for (CartItem cartItem : cartItems) {
					cartItemRewardPointMap.put(cartItem, new BigDecimal(cartItemRewardPointMap.get(cartItem)).multiply(rate).longValue());
				}
			} else {
				for (CartItem cartItem : cartItems) {
					cartItemRewardPointMap.put(cartItem, new BigDecimal(currentRewardPoint).divide(new BigDecimal(quantity)).longValue());
				}
			}
			addedRewardPoint += currentRewardPoint - originalRewardPoint;
		}
		return addedRewardPoint;
	}

	/**
	 * 获取有效赠送积分
	 * 
	 * @return 有效赠送积分
	 */
	@Transient
	public long getEffectiveRewardPoint() {
		long effectiveRewardPoint = getRewardPoint() + getAddedRewardPoint();
		return effectiveRewardPoint >= 0L ? effectiveRewardPoint : 0L;
	}

	/**
	 * 获取价格
	 * 
	 * @return 价格
	 */
	@Transient
	public BigDecimal getPrice() {
		BigDecimal price = BigDecimal.ZERO;
		for (CartItem cartItem : this) {
			price = price.add(cartItem.getSubtotal());
		}
		return price;
	}

	/**
	 * 获取折扣
	 * 
	 * @return 折扣
	 */
	@Transient
	public BigDecimal getDiscount() {
		Map<CartItem, BigDecimal> cartItemPriceMap = new HashMap<>();
		for (CartItem cartItem : this) {
			cartItemPriceMap.put(cartItem, cartItem.getSubtotal());
		}
		BigDecimal discount = BigDecimal.ZERO;
		for (Promotion promotion : getPromotions()) {
			BigDecimal originalPrice = BigDecimal.ZERO;
			BigDecimal currentPrice = BigDecimal.ZERO;
			Set<CartItem> cartItems = getCartItems(promotion);
			for (CartItem cartItem : cartItems) {
				originalPrice = originalPrice.add(cartItemPriceMap.get(cartItem));
			}
			if (originalPrice.compareTo(BigDecimal.ZERO) > 0) {
				int quantity = getQuantity(promotion);
				currentPrice = promotion.calculatePrice(originalPrice, quantity);
				BigDecimal rate = currentPrice.divide(originalPrice, MathContext.DECIMAL128);
				for (CartItem cartItem : cartItems) {
					cartItemPriceMap.put(cartItem, cartItemPriceMap.get(cartItem).multiply(rate));
				}
			} else {
				for (CartItem cartItem : cartItems) {
					cartItemPriceMap.put(cartItem, BigDecimal.ZERO);
				}
			}
			discount = discount.add(originalPrice.subtract(currentPrice));
		}
		Setting setting = SystemUtils.getSetting();
		return setting.setScale(discount);
	}

	/**
	 * 获取有效价格
	 * 
	 * @return 有效价格
	 */
	@Transient
	public BigDecimal getEffectivePrice() {
		BigDecimal effectivePrice = getPrice().subtract(getDiscount());
		return effectivePrice.compareTo(BigDecimal.ZERO) >= 0 ? effectivePrice : BigDecimal.ZERO;
	}

	/**
	 * 获取赠品
	 * 
	 * @return 赠品
	 */
	@Transient
	public Set<Sku> getGifts() {
		Set<Sku> gifts = new HashSet<>();
		for (Promotion promotion : getPromotions()) {
			if (CollectionUtils.isNotEmpty(promotion.getGifts())) {
				for (Sku gift : promotion.getGifts()) {
					if (gift.getIsMarketable() && !gift.getIsOutOfStock()) {
						gifts.add(gift);
					}
				}
			}
		}
		return gifts;
	}

	/**
	 * 获取赠品名称
	 * 
	 * @return 赠品名称
	 */
	@Transient
	public List<String> getGiftNames() {
		List<String> giftNames = new ArrayList<>();
		for (Sku gift : getGifts()) {
			giftNames.add(gift.getName());
		}
		return giftNames;
	}

	/**
	 * 获取促销
	 * 
	 * @return 促销
	 */
	@Transient
	public Set<Promotion> getPromotions() {
		Set<Promotion> allPromotions = new HashSet<>();
		for (CartItem cartItem : this) {
			if (cartItem.getSku() != null) {
				allPromotions.addAll(cartItem.getSku().getValidPromotions());
			}
		}
		Set<Promotion> promotions = new TreeSet<>();
		for (Promotion promotion : allPromotions) {
			if (isValid(promotion)) {
				promotions.add(promotion);
			}
		}
		return promotions;
	}

	/**
	 * 获取促销名称
	 * 
	 * @return 促销名称
	 */
	@Transient
	public List<String> getPromotionNames() {
		List<String> promotionNames = new ArrayList<>();
		for (Promotion promotion : getPromotions()) {
			promotionNames.add(promotion.getName());
		}
		return promotionNames;
	}

	/**
	 * 获取赠送优惠券
	 * 
	 * @return 赠送优惠券
	 */
	@Transient
	public Set<Coupon> getCoupons() {
		Set<Coupon> coupons = new HashSet<>();
		for (Promotion promotion : getPromotions()) {
			if (CollectionUtils.isNotEmpty(promotion.getCoupons())) {
				coupons.addAll(promotion.getCoupons());
			}
		}
		return coupons;
	}

	/**
	 * 获取是否需要物流
	 * 
	 * @return 是否需要物流
	 */
	@Transient
	public boolean getIsDelivery() {
		return CollectionUtils.exists(getCartItems(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && cartItem.getIsDelivery();
			}
		}) || CollectionUtils.exists(getGifts(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Sku sku = (Sku) object;
				return sku != null && sku.getIsDelivery();
			}
		});
	}

	/**
	 * 获取是否库存不足
	 * 
	 * @return 是否库存不足
	 */
	@Transient
	public boolean getIsLowStock() {
		return CollectionUtils.exists(getCartItems(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && cartItem.getIsLowStock();
			}
		});
	}

	/**
	 * 获取标签
	 * 
	 * @return 标签
	 */
	@Transient
	public String getTag() {
		Set<Map<String, Object>> items = new HashSet<>();
		for (CartItem cartItem : this) {
			Map<String, Object> item = new HashMap<>();
			item.put("skuId", cartItem.getSku().getId());
			item.put("quantity", cartItem.getQuantity());
			item.put("price", cartItem.getPrice());
			items.add(item);
		}
		return DigestUtils.md5Hex(JsonUtils.toJson(items));
	}

	/**
	 * 获取购物车项
	 * 
	 * @param sku
	 *            SKU
	 * @return 购物车项
	 */
	@Transient
	public CartItem getCartItem(final Sku sku) {
		if (sku == null) {
			return null;
		}
		return (CartItem) CollectionUtils.find(getCartItems(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && sku.equals(cartItem.getSku());
			}
		});
	}

	/**
	 * 判断是否包含SKU
	 * 
	 * @param sku
	 *            SKU
	 * @return 是否包含SKU
	 */
	@Transient
	public boolean contains(Sku sku) {
		return getCartItem(sku) != null;
	}

	/**
	 * 获取购物车项
	 * 
	 * @param promotion
	 *            促销
	 * @return 购物车项
	 */
	@Transient
	private Set<CartItem> getCartItems(Promotion promotion) {
		Set<CartItem> cartItems = new HashSet<>();
		if (promotion != null) {
			for (CartItem cartItem : this) {
				if (cartItem.getSku() != null && cartItem.getSku().isValid(promotion)) {
					cartItems.add(cartItem);
				}
			}
		}
		return cartItems;
	}

	/**
	 * 获取数量
	 * 
	 * @param promotion
	 *            促销
	 * @return 数量
	 */
	@Transient
	private int getQuantity(Promotion promotion) {
		int quantity = 0;
		for (CartItem cartItem : getCartItems(promotion)) {
			if (cartItem.getQuantity() != null) {
				quantity += cartItem.getQuantity();
			}
		}
		return quantity;
	}

	/**
	 * 获取赠送积分
	 * 
	 * @param promotion
	 *            促销
	 * @return 赠送积分
	 */
	@Transient
	private long getRewardPoint(Promotion promotion) {
		long rewardPoint = 0L;
		for (CartItem cartItem : getCartItems(promotion)) {
			rewardPoint += cartItem.getRewardPoint();
		}
		return rewardPoint;
	}

	/**
	 * 获取价格
	 * 
	 * @param promotion
	 *            促销
	 * @return 价格
	 */
	@Transient
	private BigDecimal getPrice(Promotion promotion) {
		BigDecimal price = BigDecimal.ZERO;
		for (CartItem cartItem : getCartItems(promotion)) {
			price = price.add(cartItem.getSubtotal());
		}
		return price;
	}

	/**
	 * 判断促销是否有效
	 * 
	 * @param promotion
	 *            促销
	 * @return 促销是否有效
	 */
	@Transient
	private boolean isValid(Promotion promotion) {
		if (promotion == null || !promotion.hasBegun() || promotion.hasEnded()) {
			return false;
		}
		if (CollectionUtils.isEmpty(promotion.getMemberRanks()) || getMember() == null || getMember().getMemberRank() == null || !promotion.getMemberRanks().contains(getMember().getMemberRank())) {
			return false;
		}
		Integer quantity = getQuantity(promotion);
		if ((promotion.getMinimumQuantity() != null && promotion.getMinimumQuantity() > quantity) || (promotion.getMaximumQuantity() != null && promotion.getMaximumQuantity() < quantity)) {
			return false;
		}
		BigDecimal price = getPrice(promotion);
		if ((promotion.getMinimumPrice() != null && promotion.getMinimumPrice().compareTo(price) > 0) || (promotion.getMaximumPrice() != null && promotion.getMaximumPrice().compareTo(price) < 0)) {
			return false;
		}
		return true;
	}

	/**
	 * 判断优惠券是否有效
	 * 
	 * @param coupon
	 *            优惠券
	 * @return 优惠券是否有效
	 */
	@Transient
	public boolean isValid(Coupon coupon) {
		if (coupon == null || !coupon.getIsEnabled() || !coupon.hasBegun() || coupon.hasExpired()) {
			return false;
		}
		if ((coupon.getMinimumQuantity() != null && coupon.getMinimumQuantity() > getProductQuantity()) || (coupon.getMaximumQuantity() != null && coupon.getMaximumQuantity() < getProductQuantity())) {
			return false;
		}
		if ((coupon.getMinimumPrice() != null && coupon.getMinimumPrice().compareTo(getEffectivePrice()) > 0) || (coupon.getMaximumPrice() != null && coupon.getMaximumPrice().compareTo(getEffectivePrice()) < 0)) {
			return false;
		}
		if (!isCouponAllowed()) {
			return false;
		}
		return true;
	}

	/**
	 * 判断优惠码是否有效
	 * 
	 * @param couponCode
	 *            优惠码
	 * @return 优惠码是否有效
	 */
	@Transient
	public boolean isValid(CouponCode couponCode) {
		if (couponCode == null || couponCode.getIsUsed() || couponCode.getCoupon() == null) {
			return false;
		}
		return isValid(couponCode.getCoupon());
	}

	/**
	 * 判断是否存在已下架SKU
	 * 
	 * @return 是否存在已下架SKU
	 */
	@Transient
	public boolean hasNotMarketable() {
		return CollectionUtils.exists(getCartItems(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				CartItem cartItem = (CartItem) object;
				return cartItem != null && !cartItem.getIsMarketable();
			}
		});
	}

	/**
	 * 判断是否免运费
	 * 
	 * @return 是否免运费
	 */
	@Transient
	public boolean isFreeShipping() {
		return CollectionUtils.exists(getPromotions(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Promotion promotion = (Promotion) object;
				return promotion != null && BooleanUtils.isTrue(promotion.getIsFreeShipping());
			}
		});
	}

	/**
	 * 判断是否允许使用优惠券
	 * 
	 * @return 是否允许使用优惠券
	 */
	@Transient
	public boolean isCouponAllowed() {
		return !CollectionUtils.exists(getPromotions(), new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				Promotion promotion = (Promotion) object;
				return promotion != null && BooleanUtils.isFalse(promotion.getIsCouponAllowed());
			}
		});
	}

	/**
	 * 购物车项数量
	 * 
	 * @return 购物车项数量
	 */
	@Transient
	public int size() {
		return getCartItems() != null ? getCartItems().size() : 0;
	}

	/**
	 * 判断购物车项是否为空
	 * 
	 * @return 购物车项是否为空
	 */
	@Transient
	public boolean isEmpty() {
		return CollectionUtils.isEmpty(getCartItems());
	}

	/**
	 * 添加购物车项
	 * 
	 * @param cartItem
	 *            购物车项
	 */
	@Transient
	public void add(CartItem cartItem) {
		if (cartItem == null) {
			return;
		}
		if (getCartItems() == null) {
			setCartItems(new HashSet<CartItem>());
		}
		getCartItems().add(cartItem);
	}

	/**
	 * 移除购物车项
	 * 
	 * @param cartItem
	 *            购物车项
	 */
	@Transient
	public void remove(CartItem cartItem) {
		if (getCartItems() != null) {
			getCartItems().remove(cartItem);
		}
	}

	/**
	 * 清空购物车项
	 */
	@Transient
	public void clear() {
		if (getCartItems() != null) {
			getCartItems().clear();
		}
	}

	/**
	 * 实现iterator方法
	 * 
	 * @return Iterator
	 */
	@Override
	@Transient
	public Iterator<CartItem> iterator() {
		return getCartItems() != null ? getCartItems().iterator() : Collections.<CartItem>emptyIterator();
	}

	/**
	 * 持久化前处理
	 */
	@PrePersist
	public void prePersist() {
		setKey(DigestUtils.md5Hex(UUID.randomUUID() + RandomStringUtils.randomAlphabetic(30)));
		if (getMember() == null) {
			setExpire(DateUtils.addSeconds(new Date(), Cart.TIMEOUT));
		} else {
			setExpire(null);
		}
	}

}