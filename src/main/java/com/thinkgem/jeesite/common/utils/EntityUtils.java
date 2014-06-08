package com.thinkgem.jeesite.common.utils;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 实体工具类
 * @author summer
 *
 */
public class EntityUtils {
	
	/**
	 * 复制持久bean的基本信息，并返回它
	 * @param olde
	 * @param newe
	 * @return
	 */
	public static DataEntity copyBasePro2NewEntity(DataEntity olde,DataEntity newe){
		newe.setCreateBy(olde.getCreateBy());
		newe.setCreateDate(olde.getCreateDate());
		newe.setDelFlag(olde.getDelFlag());
		return newe;
	}
}
