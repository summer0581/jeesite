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
	
	public static int deNull(Object val){
		if(null != val){
			if(val instanceof String){
				if(StringUtils.isNotBlank((String)val)){
					return Integer.valueOf((String)val);
				}
			}else if(val instanceof Integer){
				return (Integer)val;
			}
		}
		return 0;
	}
}
