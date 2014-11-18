/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
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
	 * 包租表的综合查询(待审核的）
	 * @param page
	 * @param paramMap
	 * @return
	 */
	public Page<Rent> rentList4Audit(Page<Rent> page,Map<String, Object> paramMap) {
		StringBuffer sql = new StringBuffer();
		sql.append(" ");
		sql.append("SELECT ");
		sql.append("DISTINCT r.*,h.houses ");
		sql.append("FROM ");
		sql.append("finance_rent r ");
		sql.append("INNER JOIN finance_rentmonth rm ON r.id = rm.rent_id ");
		sql.append("inner join finance_house h on h.id = r.house_id ");
		sql.append("WHERE ");
		sql.append("rm.audit_state = 'N' ");
		sql.append("AND rm.add_from = 'noreturnlist' ");
		Parameter sqlparam = new Parameter();
		String name = (String)paramMap.get("name");
		if(StringUtils.isNotBlank(name)){
			sql.append("AND h.name like :name ");
			sqlparam.put("name", "%"+name+"%");
		}
		String business_num = (String)paramMap.get("business_num");
		if(StringUtils.isNotBlank(business_num)){
			sql.append("AND r.business_num = :business_num ");
			sqlparam.put("business_num", business_num);
		}
		sql.append(" ORDER BY ");
		sql.append("rm.update_date DESC ");
		
		
		return findBySql(page, sql.toString(),sqlparam, Rent.class);
	}
	/**
	 * 设置包租月记录为已审核
	 * @param rent_id
	 */
	public void setAudited(String rent_id){
		StringBuffer sql = new StringBuffer();
		sql.append(" update finance_rentmonth rm set rm.audit_state = 'Y' where rm.rent_id = :rent_id ");
		Parameter sqlparam = new Parameter();
		sqlparam.put("rent_id", rent_id);
		updateBySql(sql.toString(), sqlparam);
	}

	/**
	 * 包租表的综合查询
	 * @param page
	 * @param paramMap
	 * @return
	 */
	public Page<Rent> rentList(Page<Rent> page,Map<String, Object> paramMap) {
		Map<String,Object> result = createRentListBaseSql("r.*,h.houses",paramMap);
		String sql = (String)result.get("sql"); 
		Parameter sqlparam = (Parameter)result.get("sqlparam"); 
		
		return findBySql(page, sql,sqlparam, Rent.class);
	}
	
	/**
	 * 包租表的承租金额字段总计查询合查询
	 * @param page
	 * @param paramMap
	 * @return
	 */
	public Map<String,String> rentListSumColumn(Map<String, Object> paramMap,RentMonth.INFOTYPE infotype) {
		String sumcolumnSql = "";
		if(RentMonth.INFOTYPE.rentin.equals(infotype)){
			sumcolumnSql = "sum(rms.rentmonth) rentrentmonthsum,sum(if(''=rms.nextshouldamount,rms.rentmonth,rms.nextshouldamount)) rentnextshouldpaysum";
		}else{
			sumcolumnSql = "sum(rms2.rentmonth) rentrentmonthsum,sum(rms2.nextshouldamount) rentnextshouldpaysum";
		}
		Map<String,Object> result = createRentListBaseSql(sumcolumnSql,paramMap);
		String sql = (String)result.get("sql"); 
		Parameter sqlparam = (Parameter)result.get("sqlparam"); 
		List<Map<String,String>> resultList = findBySql(sql,sqlparam, Map.class);
		if(null != resultList && resultList.size() > 0){
			return resultList.get(0);
		}
		return new HashMap<String,String>();
	}

	
	/**
	 * 构建rentlist的基本查询语句
	 * @param selectcolumn
	 * @param paramMap
	 * @return
	 */
	public Map<String,Object> createRentListBaseSql(String selectcolumn,Map<String, Object> paramMap){
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
		
		String rentin_departleader = (String)paramMap.get("rentin_departleader_id");
		String rentin_teamleader = (String)paramMap.get("rentin_teamleader_id");
		String rentout_departleader = (String)paramMap.get("rentout_departleader_id");
		String rentout_teamleader = (String)paramMap.get("rentout_teamleader_id");
		if(StringUtils.isNotBlank(rentin_departleader)){
			paramMap.put("rentin_departleader", UserUtils.getUserById(rentin_departleader));
		}
		if(StringUtils.isNotBlank(rentin_teamleader)){
			paramMap.put("rentin_teamleader", UserUtils.getUserById(rentin_teamleader));
		}
		if(StringUtils.isNotBlank(rentout_departleader)){
			paramMap.put("rentout_departleader", UserUtils.getUserById(rentout_departleader));
		}
		if(StringUtils.isNotBlank(rentout_teamleader)){
			paramMap.put("rentout_teamleader", UserUtils.getUserById(rentout_teamleader));
		}
		
		
		
		String rentinperson_id = (String)paramMap.get("rentinperson_id");
		String rentoutperson_id = (String)paramMap.get("rentoutperson_id");
		String rentin_remark = (String)paramMap.get("rentin_remark");
		String rentout_remark = (String)paramMap.get("rentout_remark");
		String rentin_nextshouldremark = (String)paramMap.get("rentin_nextshouldremark");
		String rentout_nextshouldremark = (String)paramMap.get("rentout_nextshouldremark");
		String rentout_cancelrentremark = (String)paramMap.get("rentout_cancelrentremark");
		String is_terentrentout = (String)paramMap.get("is_terentrentout");
		
		String housefilter = (String)paramMap.get("housefilter");
		if(StringUtils.isNotBlank(rentinperson_id)){
			paramMap.put("rentinperson", UserUtils.getUserById(rentinperson_id));
		}
		if(StringUtils.isNotBlank(rentoutperson_id)){
			paramMap.put("rentoutperson", UserUtils.getUserById(rentoutperson_id));
		}
		
		String rentin_paytype = (String)paramMap.get("rentin_paytype");
		String rentout_paytype = (String)paramMap.get("rentout_paytype");
		
		StringBuffer sql = new StringBuffer();
		Parameter sqlparam = new Parameter();
		Map<String,Object> result = new HashMap<String,Object>();
		sql.append(" ");
		sql.append("select "+selectcolumn+" ");
		sql.append("from finance_rent r ");
		sql.append("inner join finance_house h on h.id = r.house_id ");
		if(StringUtils.isNotEmpty(housefilter) && "true".equals(housefilter)){//房屋过滤，用于业务部进行包租信息查看
			sql.append("		inner join sys_user u1 on u1.id = h.rentin_user ");
			sql.append("		inner join sys_office o1 on u1.office_id = o1.id ");
			sql.append("		inner join sys_user u2 on u2.id = h.rentout_user ");
			sql.append("		inner join sys_office o2 on u2.office_id = o2.id ");
		}
		
		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentin_sdatesdate","rentin_sdateedate",
				"rentin_nextpaysdate","rentin_nextpayedate","rentin_rentmonthmin","rentin_rentmonthmax",
				"rentin_edatesdate","rentin_edateedate","rentinperson_id","rentin_paytype","rentin_remark",
				"rentin_departleader_id","rentin_teamleader_id","rentin_nextshouldremark")){
			sql.append("INNER JOIN ( ");
			//2014.7.12 sql 进行优化，从以前的查询要40多秒，优化到只要1秒不到，主要原因是，以前用的方式是每行去一一比对，现在是全部排序进行比对
			sql.append("SELECT rm.* FROM  ( ");
			sql.append("		SELECT * FROM ( ");
			sql.append("				SELECT * FROM finance_rentmonth rm1 ");
			sql.append("				WHERE rm1.infotype = 'rentin' AND rm1.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
			if (StringUtils.isNotBlank(rentin_nextshouldremark)){//下次付租备注需要能查以前的月记录
				sql.append(" and rm1.nextshouldremark like :rentin_nextshouldremark ");
				sqlparam.put("rentin_nextshouldremark", "%"+rentin_nextshouldremark+"%");
			}
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
			if(StringUtils.isNotBlank(rentin_paytype)){
				sql.append("and rm.paytype = :rentin_paytype   ");
				sqlparam.put("rentin_paytype", rentin_paytype);
			}
			if (StringUtils.isNotBlank(rentin_remark)){
				sql.append(" and rm.remarks like :rentin_remark ");
				sqlparam.put("rentin_remark", "%"+rentin_remark+"%");
			}

			if(StringUtils.isNotBlank(rentin_departleader)){
				sql.append("and rm.busi_departleader = :rentin_departleader   ");
				sqlparam.put("rentin_departleader", rentin_departleader);
			}
			if(StringUtils.isNotBlank(rentin_teamleader)){
				sql.append("and rm.busi_teamleader = :rentin_teamleader   ");
				sqlparam.put("rentin_teamleader", rentin_teamleader);
			}
			
			
			
			sql.append(") rms on r.id = rms.rent_id ");
		}

		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentout_sdatesdate","rentout_sdateedate",
				"rentout_nextpaysdate","rentout_nextpayedate","rentout_rentmonthmin","rentout_rentmonthmax","rentout_paytype",
				"rentout_edatesdate","rentout_edateedate","rentoutperson_id","rentout_departleader_id","rentout_teamleader_id",
				"notcancelrent","notcancelrentonly","rentout_remark","is_terentrentout","rentout_cancelrentremark",
				"rentout_nextshouldremark")){
			sql.append("INNER JOIN ( ");
			//2014.7.12 sql 进行优化，从以前的查询要40多秒，优化到只要1秒不到，主要原因是，以前用的方式是每行去一一比对，现在是全部排序进行比对
			sql.append("SELECT rm.* FROM  ( ");
			sql.append("		SELECT * FROM ( ");
			sql.append("				SELECT * FROM finance_rentmonth rm1 ");
			sql.append("				WHERE rm1.infotype = 'rentout' AND rm1.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
			if (StringUtils.isNotBlank(rentout_nextshouldremark)){
				sql.append(" and rm1.nextshouldremark like :rentout_nextshouldremark ");
				sqlparam.put("rentout_nextshouldremark", "%"+rentout_nextshouldremark+"%");
			}
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
			if (StringUtils.isNotBlank(rentout_remark)){
				sql.append(" and rm.remarks like :rentout_remark ");
				sqlparam.put("rentout_remark", "%"+rentout_remark+"%");
			}

			if (StringUtils.isNotBlank(rentout_cancelrentremark)){
				sql.append(" and rm.cancelrentremark like :rentout_cancelrentremark ");
				sqlparam.put("rentout_cancelrentremark", "%"+rentout_cancelrentremark+"%");
			}
			
			if(StringUtils.isNotBlank(is_terentrentout)){
				sql.append(" and rm.is_terentrentout = :is_terentrentout ");
				sqlparam.put("is_terentrentout", is_terentrentout);
			}
			if(StringUtils.isNotBlank(rentout_departleader)){
				sql.append("and rm.busi_departleader = :rentout_departleader   ");
				sqlparam.put("rentout_departleader", rentout_departleader);
			}
			if(StringUtils.isNotBlank(rentout_teamleader)){
				sql.append("and rm.busi_teamleader = :rentout_teamleader   ");
				sqlparam.put("rentout_teamleader", rentout_teamleader);
			}
			String notcancelrent = (String)paramMap.get("notcancelrent");
			if (StringUtils.isNotEmpty(notcancelrent) && "true".equals(notcancelrent)){
				sql.append(" and (rm.cancelrentdate is null or rm.cancelrentdate = '') and rm.edate <> rm.lastpayedate ");
			}
			String notcancelrentonly = (String)paramMap.get("notcancelrentonly");
			if (StringUtils.isNotEmpty(notcancelrentonly) && "true".equals(notcancelrentonly)){
				sql.append(" and (rm.cancelrentdate is null or rm.cancelrentdate = '')  ");
			}
			
			sql.append(") rms2 on r.id = rms2.rent_id ");
		}
		sql.append(" where 1=1  ");
		sql.append("and r.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
		String name = (String)paramMap.get("name");
		if (StringUtils.isNotEmpty(name)){
			sql.append(" and h.name like :rentname ");
			sqlparam.put("rentname", "%"+name+"%");
		}
		String business_num = (String)paramMap.get("business_num");
		if (StringUtils.isNotEmpty(business_num)){
			sql.append(" and r.business_num = :business_num ");
			sqlparam.put("business_num", business_num);
		}
		
		String is_norentout = (String)paramMap.get("is_norentout");
		if (StringUtils.isNotEmpty(is_norentout)){
			sql.append(" and (h.flag_norentout = :is_norentout OR h.flag_cancelrent = :is_norentout) ");
			String flag_norentout = "0".equals(is_norentout)?"N":"Y";
			sqlparam.put("is_norentout",  flag_norentout);
		}
		
		if(null != rentout_cancelrentsdate || null != rentout_cancelrentedate){//2014.08.25 刘睿提出，退租日期查询的，历史记录也要查出来
			StringBuffer cancelrentSql = new StringBuffer();
			Parameter cancelSqlParam = new Parameter();
			cancelrentSql.append(" SELECT rm.rent_id FROM finance_rentmonth rm WHERE 1 = 1 ");
			if(null != rentout_cancelrentsdate){
				cancelrentSql.append(" and rm.cancelrentdate >= :rentout_cancelrentsdate ");
				cancelSqlParam.put("rentout_cancelrentsdate", rentout_cancelrentsdate);
			}
			if(null != rentout_cancelrentedate){
				cancelrentSql.append("  and  rm.cancelrentdate <= :rentout_cancelrentedate ");
				cancelSqlParam.put("rentout_cancelrentedate", rentout_cancelrentedate);
			}
			List<String> cancelrentresult = findBySql(cancelrentSql.toString(),cancelSqlParam);
			if(cancelrentresult.size()>0){//此处，需要先将in的内容查出来，否则直接用子查询查会慢很多倍
				sql.append(StringUtils.createInSql("r.id", cancelrentresult));
			}

			
		}
		
		
		if(StringUtils.isNotEmpty(housefilter) && "true".equals(housefilter)){//房屋过滤，用于业务部进行包租信息查看
			String houseAreaRole = (String)paramMap.get("houseAreaRole");
			sql.append("and ( ");
			sql.append(BaseService.dataScopeFilterString(UserUtils.getUser(), "o1", "u1").replace("and", "")+" or ");
			sql.append(BaseService.dataScopeFilterString(UserUtils.getUser(), "o2", "u2").replace("and", "")+" or ");
			sql.append(" h.houses in ( ");
			sql.append(houseAreaRole);
			sql.append(" ) ");
			sql.append(" ) ");
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
		result.put("sql", sql.toString());
		result.put("sqlparam", sqlparam);
		return result;
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
			return resultlist.get(0).get("maxnum")+1;
		}else{
			return 0;
		}
	}
	
}
