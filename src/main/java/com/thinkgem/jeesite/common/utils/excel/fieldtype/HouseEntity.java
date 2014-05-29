/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.common.utils.excel.fieldtype;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.service.HouseService;

/**
 * 字段类型转换
 * @author ThinkGem
 * @version 2013-03-10
 */
public class HouseEntity {
	private static HouseService houseService = SpringContextHolder.getBean(HouseService.class);
	

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		House house = houseService.findByName(val);
		if(null == house && StringUtils.isNotBlank(val)){
			house = new House();
			house.prePersist();
			house.setName(val);
		}
		return house;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((House)val).getName() != null){
			return ((House)val).getName();
		}
		return "";
	}
}
