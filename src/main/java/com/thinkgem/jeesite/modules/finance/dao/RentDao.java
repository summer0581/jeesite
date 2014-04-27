/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;

/**
 * 包租明细DAO接口
 * @author 夏天
 * @version 2014-03-15
 */
@Repository
public class RentDao extends BaseDao<Rent> {
	public List<Rent> rentInList(){
		return find("from Rent where delFlag=:p1 and rentin_lastpayedate < rentin_edate order by rentin_nextpaydate desc", new Parameter(Rent.DEL_FLAG_NORMAL));
	}
	public List<Rent> rentOutList(){
		return find("from Rent where delFlag=:p1 and rentout_lastpayedate < rentout_edate order by rentout_nextpaydate desc", new Parameter(Rent.DEL_FLAG_NORMAL));
	}
	
	public List<Rent> findByName(String name){
		return find("from Rent where name = :p1", new Parameter(name));
	}
	
	/**
	 * 删除包租信息，并清除房屋关系
	 * @param id
	 * @return
	 */
	public int deleteRent(String id){
		return update(" update Rent set house=:p1,delFlag=:p2 where id = :p3", new Parameter(null,BaseEntity.DEL_FLAG_DELETE,id));
	}

}
