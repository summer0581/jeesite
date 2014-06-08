/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.Rent;

/**
 * 包租明细DAO接口
 * @author 夏天
 * @version 2014-03-15
 */
@Repository
public class RentDao extends BaseDao<Rent> {

	
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

	/**
	 * 包租表的综合查询
	 * @param page
	 * @param paramMap
	 * @return
	 */
	public Page<Rent> rentList(Page<Rent> page,Map<String, Object> paramMap) {

		Date rentin_sdatesdate = DateUtils.parseDate(paramMap.get("rentin_sdatesdate"));
		Date rentin_sdateedate = DateUtils.parseDate(paramMap.get("rentin_sdateedate"));
		Date rentout_sdatesdate = DateUtils.parseDate(paramMap.get("rentout_sdatesdate"));
		Date rentout_sdateedate = DateUtils.parseDate(paramMap.get("rentout_sdateedate"));
		Date rentin_edatesdate = DateUtils.parseDate(paramMap.get("rentin_edatesdate"));
		Date rentin_edateedate = DateUtils.parseDate(paramMap.get("rentin_edateedate"));
		Date rentout_edatesdate = DateUtils.parseDate(paramMap.get("rentout_edatesdate"));
		Date rentout_edateedate = DateUtils.parseDate(paramMap.get("rentout_edateedate"));
		
		Date rentin_nextpaysdate = DateUtils.parseDate(paramMap.get("rentin_nextpaysdate"));
		Date rentin_nextpayedate = DateUtils.parseDate(paramMap.get("rentin_nextpayedate"));
		Date rentout_nextpaysdate = DateUtils.parseDate(paramMap.get("rentout_nextpaysdate"));
		Date rentout_nextpayedate = DateUtils.parseDate(paramMap.get("rentout_nextpayedate"));
		String rentin_rentmonthmin = (String)paramMap.get("rentin_rentmonthmin");
		String rentin_rentmonthmax = (String)paramMap.get("rentin_rentmonthmax");
		String rentout_rentmonthmin = (String)paramMap.get("rentout_rentmonthmin");
		String rentout_rentmonthmax = (String)paramMap.get("rentout_rentmonthmax");
		String rentout_paytype = (String)paramMap.get("rentout_paytype");
		
		StringBuffer sql = new StringBuffer();
		Parameter sqlparam = new Parameter(); 
		sql.append(" ");
		sql.append("select r.* ");
		sql.append("from finance_rent r ");
		
		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentin_sdatesdate","rentin_sdateedate",
				"rentin_nextpaysdate","rentin_nextpayedate","rentin_rentmonthmin","rentin_rentmonthmax",
				"rentin_edatesdate","rentin_edateedate")){
			sql.append("INNER JOIN ( ");
			sql.append("select * ");
			sql.append("from finance_rentmonth rm ");
			sql.append("where rm.infotype = 'rentin'   ");
			if(null != rentin_sdatesdate){
				sql.append("and rm.sdate >= :rentin_sdatesdate   ");
				sqlparam.put("rentin_sdatesdate", rentin_sdatesdate);
			}
			if(null != rentin_sdateedate){
				sql.append("and rm.sdate <= :rentin_sdateedate   ");
				sqlparam.put("rentin_sdateedate", rentin_sdateedate);
			}
			if(null != rentin_edatesdate){
				sql.append("and rm.edate >= :rentin_edatesdate   ");
				sqlparam.put("rentin_edatesdate", rentin_edatesdate);
			}
			if(null != rentin_edateedate){
				sql.append("and rm.edate <= :rentin_edateedate   ");
				sqlparam.put("rentin_edateedate", rentin_edateedate);
			}
			if(null != rentin_nextpaysdate){
				sql.append("and rm.nextpaydate >= :rentin_nextpaysdate   ");
				sqlparam.put("rentin_nextpaysdate", rentin_nextpaysdate);
			}
			if(null != rentin_nextpayedate){
				sql.append("and rm.nextpaydate <= :rentin_nextpayedate   ");
				sqlparam.put("rentin_nextpayedate", rentin_nextpayedate);
			}
			if(StringUtils.isNotBlank(rentin_rentmonthmin)){
				sql.append("and rm.rentmonth >= :rentin_rentmonthmin   ");
				sqlparam.put("rentin_rentmonthmin", StringUtils.toInteger(rentin_rentmonthmin));
			}
			if(StringUtils.isNotBlank(rentin_rentmonthmax)){
				sql.append("and rm.rentmonth <= :rentin_rentmonthmax   ");
				sqlparam.put("rentin_rentmonthmax", StringUtils.toInteger(rentin_rentmonthmax));
			}

			
			sql.append("and not exists (select 1 from finance_rentmonth rm2 where rm.rent_id=rm2.rent_id and rm2.infotype = 'rentin'  and rm.create_date < rm2.create_date) ");
			sql.append(") rms on r.id = rms.rent_id ");
		}

		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentout_sdatesdate","rentout_sdateedate",
				"rentout_nextpaysdate","rentout_nextpayedate","rentout_rentmonthmin","rentout_rentmonthmax","rentout_paytype",
				"rentout_edatesdate","rentout_edateedate")){
			sql.append("INNER JOIN ( ");
			sql.append("select * ");
			sql.append("from finance_rentmonth rm ");
			sql.append("where rm.infotype = 'rentout' ");
			if(null != rentout_sdatesdate){
				sql.append("and rm.sdate >= :rentout_sdatesdate   ");
				sqlparam.put("rentout_sdatesdate", rentout_sdatesdate);
			}
			if(null != rentout_sdateedate){
				sql.append("and rm.sdate <= :rentout_sdateedate   ");
				sqlparam.put("rentout_sdateedate", rentout_sdateedate);
			}
			if(null != rentout_edatesdate){
				sql.append("and rm.edate >= :rentout_edatesdate   ");
				sqlparam.put("rentout_edatesdate", rentout_edatesdate);
			}
			if(null != rentout_edateedate){
				sql.append("and rm.edate <= :rentout_edateedate   ");
				sqlparam.put("rentout_edateedate", rentout_edateedate);
			}
			if(null != rentout_nextpaysdate){
				sql.append("and rm.nextpaydate >= :rentout_nextpaysdate   ");
				sqlparam.put("rentout_nextpaysdate", rentout_nextpaysdate);
			}
			if(null != rentout_nextpayedate){
				sql.append("and rm.nextpaydate <= :rentout_nextpayedate   ");
				sqlparam.put("rentout_nextpayedate", rentout_nextpayedate);
			}
			if(StringUtils.isNotBlank(rentout_rentmonthmin)){
				sql.append("and rm.rentmonth >= :rentout_rentmonthmin   ");
				sqlparam.put("rentout_rentmonthmin", StringUtils.toInteger(rentout_rentmonthmin));
			}
			if(StringUtils.isNotBlank(rentout_rentmonthmax)){
				sql.append("and rm.rentmonth <= :rentout_rentmonthmax   ");
				sqlparam.put("rentout_rentmonthmax", StringUtils.toInteger(rentout_rentmonthmax));
			}
			if(StringUtils.isNotBlank(rentout_paytype)){
				sql.append("and rm.paytype = :rentout_paytype   ");
				sqlparam.put("rentout_paytype", rentout_paytype);
			}
			
			sql.append("and not exists  (select 1 from finance_rentmonth rm2 where rm.rent_id=rm2.rent_id and rm2.infotype = 'rentout' and rm.create_date < rm2.create_date) ");
			sql.append(") rms2 on r.id = rms2.rent_id ");
		}
		sql.append(" where 1=1  ");
		String name = (String)paramMap.get("name");
		if (StringUtils.isNotEmpty(name)){
			sql.append(" and r.name like :rentname ");
			sqlparam.put("rentname", "%"+name+"%");
		}
		String order = (String)paramMap.get("order");
		String desc = (String)paramMap.get("desc");
		if(StringUtils.isBlank(order)){
			order = "r.business_num";
		}
		if(StringUtils.isBlank(desc)){
			desc = "";
		}
		sql.append("order by "+order+" "+desc);
		
		return findBySql(page, sql.toString(),sqlparam, Rent.class);
	}

}
