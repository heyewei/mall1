/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import net.shopxx.Setting;
import net.shopxx.service.CacheService;
import net.shopxx.util.SystemUtils;
import net.shopxx.util.WebUtils;

/**
 * Controller - 统计
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("statisticsController")
@RequestMapping("/admin/statistics")
public class StatisticsController extends BaseController {

	@Inject
	private CacheService cacheService;

	/**
	 * 查看
	 */
	@GetMapping("/view")
	public String view(ModelMap model) {
		Setting setting = SystemUtils.getSetting();
		model.addAttribute("cnzzSiteId", setting.getCnzzSiteId());
		model.addAttribute("cnzzPassword", setting.getCnzzPassword());
		return "admin/statistics/view";
	}

	/**
	 * 设置
	 */
	@GetMapping("/setting")
	public String setting(ModelMap model) {
		Setting setting = SystemUtils.getSetting();
		model.addAttribute("isCnzzEnabled", setting.getIsCnzzEnabled());
		return "admin/statistics/setting";
	}

	/**
	 * 设置
	 */
	@PostMapping("/setting")
	public String setting(@RequestParam(defaultValue = "false") Boolean isEnabled, RedirectAttributes redirectAttributes) {
		Setting setting = SystemUtils.getSetting();
		if (isEnabled) {
			if (StringUtils.isEmpty(setting.getCnzzSiteId()) || StringUtils.isEmpty(setting.getCnzzPassword())) {
				String domain = setting.getSiteUrl().replaceAll("(^[\\s\\S]*?[^a-zA-Z0-9-.]+)|([^a-zA-Z0-9-.][\\s\\S]*$)", "");
				Map<String, Object> parameterMap = new HashMap<>();
				parameterMap.put("domain", domain);
				parameterMap.put("key", DigestUtils.md5Hex(domain + "Lfg4uP0H"));
				String content = WebUtils.get("http://intf.cnzz.com/user/companion/shopxx.php", parameterMap);
				if (StringUtils.contains(content, "@")) {
					setting.setCnzzSiteId(StringUtils.substringBefore(content, "@"));
					setting.setCnzzPassword(StringUtils.substringAfter(content, "@"));
				}
			}
		}
		setting.setIsCnzzEnabled(isEnabled);
		SystemUtils.setSetting(setting);
		cacheService.clear();
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:setting";
	}

}