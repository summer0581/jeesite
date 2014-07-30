/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.modules.finance.entity.HouseAreaRole;
import com.thinkgem.jeesite.modules.finance.entity.LabelValueObj;

/**
 * 房屋区域权限设置DAO接口
 * @author 夏天
 * @version 2014-07-30
 */
@Repository
public class HouseAreaRoleDao extends BaseDao<HouseAreaRole> {
	public List<HouseAreaRole> findByName(String name){
		return find("from HouseAreaRole where name = :p1", new Parameter(name));
	}
	
	public HouseAreaRole findByPerson(String person){
		List<HouseAreaRole> harList = findBySql("select * from finance_housearearole where roleperson = :p1", new Parameter(person),HouseAreaRole.class);
		if(harList.size() > 0){
			return harList.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 获取所有的房屋区域信息
	 * @return
	 */
	public List<LabelValueObj> findAllAreas(){
		List<LabelValueObj> lvlist = new ArrayList<LabelValueObj>();
		List<Map> listmap =  findBySql("select h.houses label,h.houses value from finance_house h where h.houses <> '' group by h.houses",null,Map.class);
		for(Map tempMap : listmap){
			LabelValueObj lvo = new LabelValueObj();
			lvo.setLabel((String)tempMap.get("label"));
			lvo.setValue((String)tempMap.get("value"));
			lvlist.add(lvo);
		}
		return lvlist;
	}
	
}
