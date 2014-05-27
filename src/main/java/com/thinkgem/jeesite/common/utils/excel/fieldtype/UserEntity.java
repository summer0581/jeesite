/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.common.utils.excel.fieldtype;

import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 字段类型转换
 * @author ThinkGem
 * @version 2013-03-10
 */
public class UserEntity {

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		for (User e : UserUtils.getUserList()){
			if (val.equals(e.getName())){
				return e;
			}
		}
		User user = new User();
		user.prePersist();
		user.setName(val);
		return user;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if (val != null && ((User)val).getName() != null){
			return ((User)val).getName();
		}
		return "";
	}
}
