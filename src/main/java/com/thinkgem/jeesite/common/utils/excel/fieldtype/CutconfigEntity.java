/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.common.utils.excel.fieldtype;

import java.util.List;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;
import com.thinkgem.jeesite.modules.finance.service.CutconfigService;

/**
 * 字段类型转换
 * @author ThinkGem
 * @version 2013-03-10
 */
public class CutconfigEntity {
	private static CutconfigService cutconfigService = SpringContextHolder.getBean(CutconfigService.class);
	

	/**
	 * 获取对象值（导入）
	 */
	public static String getValue(String val) {
		return cutconfigService.findByName(val).getCut_code();
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(String val) {
		if (val != null ){
			List<Cutconfig> list = cutconfigService.findCutconfiglistByCutcode(val);
			if(null != list && list.size() > 0){
				return list.get(0).getName();
			}
		}
		return "";
	}
}
