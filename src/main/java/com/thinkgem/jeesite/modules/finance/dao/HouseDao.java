/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.finance.entity.House;

/**
 * 房屋明细DAO接口
 * @author 夏天
 * @version 2014-03-15
 */
@Repository
public class HouseDao extends BaseDao<House> {
	public List<House> findByIdIn(String[] ids){
		return find("from House where id in (:p1)", new Parameter(new Object[]{ids}));
	}
	
	public List<House> findByName(String name){
		return find("from House where name = :p1", new Parameter(name));
	}
}
