/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.entity;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import net.shopxx.util.FreeMarkerUtils;

/**
 * Entity - 快递单模板
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Entity
public class DeliveryTemplate extends BaseEntity<Long> {

	private static final long serialVersionUID = -3711024981692804054L;

	/**
	 * 名称
	 */
	@NotEmpty
	@Length(max = 200)
	@Column(nullable = false)
	private String name;

	/**
	 * 内容
	 */
	@NotEmpty
	@Lob
	@Column(nullable = false)
	private String content;

	/**
	 * 宽度
	 */
	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer width;

	/**
	 * 高度
	 */
	@NotNull
	@Min(1)
	@Column(nullable = false)
	private Integer height;

	/**
	 * 偏移量X
	 */
	@NotNull
	@Column(nullable = false)
	private Integer offsetX;

	/**
	 * 偏移量Y
	 */
	@NotNull
	@Column(nullable = false)
	private Integer offsetY;

	/**
	 * 背景图
	 */
	@Length(max = 200)
	@Pattern(regexp = "^(?i)(http:\\/\\/|https:\\/\\/|\\/).*$")
	private String background;

	/**
	 * 是否默认
	 */
	@NotNull
	@Column(nullable = false)
	private Boolean isDefault;

	/**
	 * 备注
	 */
	@Length(max = 200)
	private String memo;

	/**
	 * 获取名称
	 * 
	 * @return 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 *            名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取内容
	 * 
	 * @return 内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置内容
	 * 
	 * @param content
	 *            内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取宽度
	 * 
	 * @return 宽度
	 */
	public Integer getWidth() {
		return width;
	}

	/**
	 * 设置宽度
	 * 
	 * @param width
	 *            宽度
	 */
	public void setWidth(Integer width) {
		this.width = width;
	}

	/**
	 * 获取高度
	 * 
	 * @return 高度
	 */
	public Integer getHeight() {
		return height;
	}

	/**
	 * 设置高度
	 * 
	 * @param height
	 *            高度
	 */
	public void setHeight(Integer height) {
		this.height = height;
	}

	/**
	 * 获取偏移量X
	 * 
	 * @return 偏移量X
	 */
	public Integer getOffsetX() {
		return offsetX;
	}

	/**
	 * 设置偏移量X
	 * 
	 * @param offsetX
	 *            偏移量X
	 */
	public void setOffsetX(Integer offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * 获取偏移量Y
	 * 
	 * @return 偏移量Y
	 */
	public Integer getOffsetY() {
		return offsetY;
	}

	/**
	 * 设置偏移量Y
	 * 
	 * @param offsetY
	 *            偏移量Y
	 */
	public void setOffsetY(Integer offsetY) {
		this.offsetY = offsetY;
	}

	/**
	 * 获取背景图
	 * 
	 * @return 背景图
	 */
	public String getBackground() {
		return background;
	}

	/**
	 * 设置背景图
	 * 
	 * @param background
	 *            背景图
	 */
	public void setBackground(String background) {
		this.background = background;
	}

	/**
	 * 获取是否默认
	 * 
	 * @return 是否默认
	 */
	public Boolean getIsDefault() {
		return isDefault;
	}

	/**
	 * 设置是否默认
	 * 
	 * @param isDefault
	 *            是否默认
	 */
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * 获取备注
	 * 
	 * @return 备注
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 设置备注
	 * 
	 * @param memo
	 *            备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 解析内容
	 * 
	 * @return 内容
	 */
	@Transient
	public String resolveContent() {
		try {
			Environment environment = FreeMarkerUtils.getCurrentEnvironment();
			return FreeMarkerUtils.process(getContent(), environment != null ? environment.getDataModel() : null);
		} catch (IOException e) {
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

}