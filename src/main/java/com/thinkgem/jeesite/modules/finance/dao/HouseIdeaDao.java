/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import org.springframework.stereotype.Repository;
import java.util.List;
import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.modules.finance.entity.HouseIdea;

/**
 * 房屋跟进意见DAO接口
 * @author 夏天
 * @version 2014-08-07
 */
@Repository
public class HouseIdeaDao extends BaseDao<HouseIdea> {
	public List<HouseIdea> findByHouse(String house_id){
		return find("from HouseIdea where house = :p1", new Parameter(house_id));
	}
}
