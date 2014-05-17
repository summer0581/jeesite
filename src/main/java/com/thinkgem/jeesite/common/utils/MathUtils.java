package com.thinkgem.jeesite.common.utils;

public class MathUtils {
	/**
	 * 将字符串相加，求整数
	 * @param strings
	 * @return
	 */
	public static int sumInt(String...strings ){
		int i = 0;
		for(String str : strings){
			if(StringUtils.isNotBlank(str))
			i+=Integer.parseInt(str);
		}
		return i;
	}
}
