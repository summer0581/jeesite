/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.common.utils.excel.fieldtype;

import org.apache.commons.lang3.StringUtils;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.service.CustomerService;

/**
 * 字段类型转换
 * @author ThinkGem
 * @version 2013-03-10
 */
public class CustomerEntity {
	private static CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
	

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		Customer customer = customerService.findByName(val);
		if(null == customer && StringUtils.isNotBlank(val)){
			customer = new Customer();
			customer.prePersist();
			customer.setName(val);
		}
		return customer;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((Customer)val).getName() != null){
			return ((Customer)val).getName();
		}
		return "";
	}
}
