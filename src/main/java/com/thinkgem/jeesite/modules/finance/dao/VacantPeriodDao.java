/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;

/**
 * 空置期提成DAO接口
 * @author 夏天
 * @version 2014-04-13
 */
@Repository
public class VacantPeriodDao extends BaseDao<VacantPeriod> {
	
	public List<VacantPeriod> findVacantPeriod(VacantPeriod vp){
		return findBySql("select * from finance_vacantperiod where rent_id = :p1 and type = :p2 and sdate = :p3 and edate = :p4",new Parameter(null != vp.getRent()?vp.getRent().getId():"",vp.getType(),vp.getSdate(),vp.getEdate()),VacantPeriod.class);
	}
}
