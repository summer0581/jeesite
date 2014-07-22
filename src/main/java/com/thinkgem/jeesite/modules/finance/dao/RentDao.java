/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

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
		Date rentout_cancelrentsdate = DateUtils.parseDate(paramMap.get("rentout_cancelrentsdate"));
		Date rentout_cancelrentedate = DateUtils.parseDate(paramMap.get("rentout_cancelrentedate"));
		String rentin_rentmonthmin = (String)paramMap.get("rentin_rentmonthmin");
		String rentin_rentmonthmax = (String)paramMap.get("rentin_rentmonthmax");
		String rentout_rentmonthmin = (String)paramMap.get("rentout_rentmonthmin");
		String rentout_rentmonthmax = (String)paramMap.get("rentout_rentmonthmax");
		
		String rentinperson_id = (String)paramMap.get("rentinperson_id");
		String rentoutperson_id = (String)paramMap.get("rentoutperson_id");
		if(StringUtils.isNotBlank(rentinperson_id)){
			paramMap.put("rentinperson", UserUtils.getUserById(rentinperson_id));
		}
		if(StringUtils.isNotBlank(rentoutperson_id)){
			paramMap.put("rentoutperson", UserUtils.getUserById(rentoutperson_id));
		}
		

		String rentout_paytype = (String)paramMap.get("rentout_paytype");
		
		StringBuffer sql = new StringBuffer();
		Parameter sqlparam = new Parameter(); 
		sql.append(" ");
		sql.append("select r.* ");
		sql.append("from finance_rent r ");
		
		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentin_sdatesdate","rentin_sdateedate",
				"rentin_nextpaysdate","rentin_nextpayedate","rentin_rentmonthmin","rentin_rentmonthmax",
				"rentin_edatesdate","rentin_edateedate","rentinperson_id")){
			sql.append("INNER JOIN ( ");
			//2014.7.12 sql 进行优化，从以前的查询要40多秒，优化到只要1秒不到，主要原因是，以前用的方式是每行去一一比对，现在是全部排序进行比对
			sql.append("SELECT rm.* FROM  ( ");
			sql.append("		SELECT * FROM ( ");
			sql.append("				SELECT * FROM finance_rentmonth rm1 ");
			sql.append("				WHERE rm1.infotype = 'rentin' AND rm1.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
			sql.append("				ORDER BY rm1.create_date DESC ");
			sql.append("			) rm2 GROUP BY rm2.rent_id ");
			sql.append("	) rm where 1=1 ");
		
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
			if(StringUtils.isNotBlank(rentinperson_id)){
				sql.append("and rm.person = :rentinperson_id   ");
				sqlparam.put("rentinperson_id", rentinperson_id);
			}
			
			sql.append(") rms on r.id = rms.rent_id ");
		}

		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentout_sdatesdate","rentout_sdateedate",
				"rentout_nextpaysdate","rentout_nextpayedate","rentout_rentmonthmin","rentout_rentmonthmax","rentout_paytype",
				"rentout_edatesdate","rentout_edateedate","rentout_cancelrentsdate","rentout_cancelrentedate","rentoutperson_id",
				"notcancelrent")){
			sql.append("INNER JOIN ( ");
			//2014.7.12 sql 进行优化，从以前的查询要40多秒，优化到只要1秒不到，主要原因是，以前用的方式是每行去一一比对，现在是全部排序进行比对
			sql.append("SELECT rm.* FROM  ( ");
			sql.append("		SELECT * FROM ( ");
			sql.append("				SELECT * FROM finance_rentmonth rm1 ");
			sql.append("				WHERE rm1.infotype = 'rentout' AND rm1.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
			sql.append("				ORDER BY rm1.create_date DESC ");
			sql.append("			) rm2 GROUP BY rm2.rent_id ");
			sql.append("	) rm where 1=1 ");
			
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
			if(null != rentout_cancelrentsdate){
				sql.append("and rm.cancelrentdate >= :rentout_cancelrentsdate   ");
				sqlparam.put("rentout_cancelrentsdate", rentout_cancelrentsdate);
			}
			if(null != rentout_cancelrentedate){
				sql.append("and rm.cancelrentdate <= :rentout_cancelrentedate   ");
				sqlparam.put("rentout_cancelrentedate", rentout_cancelrentedate);
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
			if(StringUtils.isNotBlank(rentoutperson_id)){
				sql.append("and rm.person = :rentoutperson_id   ");
				sqlparam.put("rentoutperson_id", rentoutperson_id);
			}
			String notcancelrent = (String)paramMap.get("notcancelrent");
			if (StringUtils.isNotEmpty(notcancelrent) && "true".equals(notcancelrent)){
				sql.append(" and （rm.cancelrentdate is null or rm.cancelrentdate = '')");
			}
			
			sql.append(") rms2 on r.id = rms2.rent_id ");
		}
		sql.append(" where 1=1  ");
		sql.append("and r.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
		String name = (String)paramMap.get("name");
		if (StringUtils.isNotEmpty(name)){
			sql.append(" and r.name like :rentname ");
			sqlparam.put("rentname", "%"+name+"%");
		}
		String business_num = (String)paramMap.get("business_num");
		if (StringUtils.isNotEmpty(business_num)){
			sql.append(" and r.business_num = :business_num ");
			sqlparam.put("business_num", business_num);
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

	/**
	 * 获取系统最大的编号但不包括5位数的
	 * @return
	 */
	public int getMaxBusinessNum(){
		StringBuffer sql = new StringBuffer();
		sql.append("select max(r.business_num) maxnum from finance_rent r where LENGTH(r.business_num) <5");
		List<Map<String,Integer>> resultlist = findBySql(sql.toString(),null,Map.class);
		if(null != resultlist && resultlist.size()>0){
			return resultlist.get(0).get("maxnum");
		}else{
			return 0;
		}
	}
	
}
