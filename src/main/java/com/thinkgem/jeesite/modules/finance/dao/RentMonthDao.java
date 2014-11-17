/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.CharUtils;
import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 包租月记录DAO接口
 * @author 夏天
 * @version 2014-05-06
 */
@Repository
public class RentMonthDao extends BaseDao<RentMonth> {
	
	public List<RentMonth> rentInListWillNeedPayNextMonth(){
		StringBuffer sql = new StringBuffer();

		sql.append("select * from (select max(rr.nextpaydate) SS,rr.* ");
		sql.append("from (select * from finance_rentmonth r order by r.nextpaydate DESC) rr INNER JOIN finance_rent fr ON fr.id = rr.rent_id ");
		sql.append("where rr.del_flag=:del_flag and fr.del_flag=:del_flag  ");
		sql.append("and rr.infotype=:infotype  ");
		sql.append("group by rr.rent_id having  datediff(max(rr.nextpaydate),current_timestamp)<7  ) rrr order by rrr.nextpaydate");
		Parameter pm = new Parameter();
		pm.put("del_flag", Rent.DEL_FLAG_NORMAL);
		pm.put("infotype", "rentin");
		return findBySql(sql.toString(), pm, RentMonth.class);
	}
	

	public List<RentMonth> rentOutListWillNeedPayNextMonth(){
		StringBuffer sql = new StringBuffer();

		sql.append("select * from (select max(rr.nextpaydate) SS,rr.* ");
		sql.append("from (select * from finance_rentmonth r order by r.nextpaydate DESC) rr INNER JOIN finance_rent fr ON fr.id = rr.rent_id ");
		sql.append("where rr.del_flag=:del_flag and fr.del_flag=:del_flag  ");
		sql.append("and rr.infotype=:infotype  ");
		sql.append("group by rr.rent_id having  datediff(max(rr.nextpaydate),current_timestamp)<7  ) rrr order by rrr.nextpaydate");
		Parameter pm = new Parameter();
		pm.put("del_flag", Rent.DEL_FLAG_NORMAL);
		pm.put("infotype", "rentout");
		return findBySql(sql.toString(), pm, RentMonth.class);
	}
	public Page<RentMonth> rentInListWillReachEdate(Page<RentMonth> pages){
		StringBuffer sql = new StringBuffer();

		sql.append("select * from (select max(rr.edate) SS,rr.* ");
		sql.append("from (select * from finance_rentmonth r order by r.edate DESC) rr INNER JOIN finance_rent fr ON fr.id = rr.rent_id ");
		sql.append("where rr.del_flag=:del_flag and fr.del_flag=:del_flag ");
		sql.append("and rr.infotype=:infotype  ");
		sql.append("group by rr.rent_id having  datediff(max(rr.edate),current_timestamp)<=180  ) rrr order by rrr.nextpaydate");
		Parameter pm = new Parameter();
		pm.put("del_flag", Rent.DEL_FLAG_NORMAL);
		pm.put("infotype", "rentin");
		return findBySql(pages,sql.toString(), pm, RentMonth.class);
	}
	public Page<RentMonth> rentOutListWillReachEdate(Page<RentMonth> pages){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT rm.* FROM ( ");
		sql.append("	SELECT * FROM ");
		sql.append("		(SELECT * FROM finance_rentmonth rm1 ");
		sql.append("			WHERE rm1.infotype = :infotype AND rm1.del_flag = :del_flag ");
		sql.append("			ORDER BY rm1.create_date DESC ");
		sql.append("		) rm2 ");
		sql.append("	GROUP BY rm2.rent_id ");
		sql.append(") rm WHERE 1 = 1 ");
		sql.append("AND datediff(rm.edate,current_timestamp)<=180 ");
		sql.append("AND ( ");
		sql.append("	rm.cancelrentdate IS NULL ");
		sql.append("	OR rm.cancelrentdate = '' ");
		sql.append(") order by rm.nextpaydate");
		Parameter pm = new Parameter();
		pm.put("del_flag", Rent.DEL_FLAG_NORMAL);
		pm.put("infotype", "rentout");
		return findBySql(pages,sql.toString(), pm, RentMonth.class);
	}
	/**
	 * 获取业绩提成基础列表
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> getBusinesscutBaseList(Map<String, Object> paramMap){
		StringBuffer sql = new StringBuffer();
		Parameter pm = new Parameter();
		sql.append("select t.id,t.infotype,t.rent_id,h.id house_id,h.name,t.sdate,t.edate,t.lastpaysdate,t.lastpayedate,t.cut_businesssaletype,");
		sql.append(" t.person,c_p.name person_name,c_p.login_name person_loginname,c_p.user_busitype person_busitype,");
		sql.append(" t.busi_manager,c_m.name busi_manager_name,c_m.login_name busi_manager_loginname,");
		sql.append(" t.busi_departleader,c_d.name busi_departleader_name,c_d.login_name busi_departleader_loginname, ");
		sql.append(" t.busi_teamleader,c_t.name busi_teamleader_name,c_t.login_name busi_teamleader_loginname, ");
		sql.append(" r.business_num,t.agencyfee,t.person_fixedcut,t.manager_fixedcut,t.departer_fixedcut,t.teamleader_fixedcut ");
		sql.append(" from ( ");
		sql.append("		SELECT * FROM ( ");
		sql.append("				SELECT * FROM finance_rentmonth rm1 ");
		sql.append("				WHERE LAST_DAY(date_add(rm1.edate, interval -1 month)) >= :lastpaysdate and rm1.sdate <= :lastpayedate and rm1.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
		sql.append("				ORDER BY rm1.create_date DESC ");
		sql.append("			) rm2 GROUP BY rm2.sdate,rm2.rent_id,rm2.infotype ");
		sql.append("	) t inner join finance_rent r on r.id = t.rent_id ");
		sql.append(" inner join finance_house h on h.id = r.house_id ");
		sql.append(" left join sys_user c_p on c_p.id = t.person ");
		sql.append(" left join sys_user c_m on c_m.id = t.busi_manager ");
		sql.append(" left join sys_user c_d on c_d.id = t.busi_departleader ");
		sql.append(" left join sys_user c_t on c_t.id = t.busi_teamleader ");
		sql.append(" where t.del_flag = :del_flag ");
		
		sql.append(" and t.infotype = 'rentin'");
		String name = (String)paramMap.get("name");
		if(!StringUtils.isBlank(name)){
			sql.append(" and h.name like :house_name");
			pm.put("house_name", "%"+name+"%");
		}
		sql.append(" order by r.business_num");
		
		pm.put("del_flag", RentMonth.DEL_FLAG_NORMAL);
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		if (rentout_sdate_begin == null){
			rentout_sdate_begin = DateUtils.getFirstDayOfMonth(new Date());
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		pm.put("lastpaysdate", rentout_sdate_begin);
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.getLastDayOfMonth(new Date());
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentout_sdate_end, "yyyy-MM-dd"));
		}
		pm.put("lastpayedate", rentout_sdate_end);
		
		List<Map<String,Object>> result = findBySql(sql.toString(), pm, Map.class);
		/****************************封装数据到最后list************************************/
		List<RentMonth> rentMonthList = new ArrayList<RentMonth>();
		for(int i = 0 ; i < result.size() ; i++){
			Map<String,Object> rMap = result.get(i);
			RentMonth rentMonth = new RentMonth();
			Rent rent = new Rent();
			House house = new House();
			
			rentMonth.setId((String)rMap.get("id"));
			rentMonth.setInfotype((String)rMap.get("infotype"));
			rentMonth.setSdate((Date)rMap.get("sdate"));
			rentMonth.setEdate((Date)rMap.get("edate"));
			rentMonth.setLastpaysdate((Date)rMap.get("lastpaysdate"));
			rentMonth.setLastpayedate((Date)rMap.get("lastpayedate"));
			rentMonth.setCut_businesssaletype((String)rMap.get("cut_businesssaletype"));
			rentMonth.setAgencyfee((String)rMap.get("agencyfee"));
			rentMonth.setPerson_fixedcut((String)rMap.get("person_fixedcut"));
			rentMonth.setTeamleader_fixedcut((String)rMap.get("teamleader_fixedcut"));
			rentMonth.setDeparter_fixedcut((String)rMap.get("departer_fixedcut"));
			rentMonth.setManager_fixedcut((String)rMap.get("manager_fixedcut"));
			
			
			house.setId((String)rMap.get("house_id"));
			house.setName((String)rMap.get("name"));
			rent.setHouse(house);
			rent.setId((String)rMap.get("rent_id"));
			rent.setBusiness_num((Integer)rMap.get("business_num"));
			
			User person = null;
			if(StringUtils.isNotBlank((String)rMap.get("person_name"))){
				person = new User();
				person.setId((String)rMap.get("person"));
				person.setName((String)rMap.get("person_name"));
				person.setUserBusitype((String)rMap.get("person_busitype"));
				person.setLoginName((String)rMap.get("person_loginname"));
			}
			
			User teamleader = null;
			if(StringUtils.isNotBlank((String)rMap.get("busi_teamleader_name"))){
				teamleader = new User();
				teamleader.setId((String)rMap.get("busi_teamleader"));
				teamleader.setName((String)rMap.get("busi_teamleader_name"));
				teamleader.setLoginName((String)rMap.get("busi_teamleader_loginname"));
			}
			
			User departleader = null;
			if(StringUtils.isNotBlank((String)rMap.get("busi_departleader_name"))){
				departleader = new User();
				departleader.setId((String)rMap.get("busi_departleader"));
				departleader.setName((String)rMap.get("busi_departleader_name"));
				departleader.setLoginName((String)rMap.get("busi_departleader_loginname"));
			}

			User manager = null;
			if(StringUtils.isNotBlank((String)rMap.get("busi_manager_name"))){
				manager = new User();
				manager.setId((String)rMap.get("busi_manager"));
				manager.setName((String)rMap.get("busi_manager_name"));
				manager.setLoginName((String)rMap.get("busi_manager_loginname"));
			}

			rentMonth.setRent(rent);
			rentMonth.setPerson(person);
			rentMonth.setBusi_teamleader(teamleader);
			rentMonth.setBusi_departleader(departleader);
			rentMonth.setBusi_manager(manager);

			rentMonthList.add(rentMonth);
		}
		return rentMonthList;
	}
	
	/**
	 * 获取完成量统计基础列表
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> getBusinessCountBaseList(Map<String, Object> paramMap){
		StringBuffer sql = new StringBuffer();
		Parameter pm = new Parameter();
		sql.append("select t.id,t.infotype,t.rent_id,h.id house_id,h.name,t.sdate,t.edate,t.lastpaysdate,t.lastpayedate,t.cut_businesssaletype,");
		sql.append(" t.person,c_p.name person_name,c_p.login_name person_loginname,c_p.user_busitype person_busitype,");
		sql.append(" t.busi_manager,c_m.name busi_manager_name,c_m.login_name busi_manager_loginname,");
		sql.append(" t.busi_departleader,c_d.name busi_departleader_name,c_d.login_name busi_departleader_loginname, ");
		sql.append(" t.busi_teamleader,c_t.name busi_teamleader_name,c_t.login_name busi_teamleader_loginname, ");
		sql.append(" r.business_num,t.agencyfee,t.is_terentrentout ");
		sql.append(" from ( ");
		sql.append("		SELECT * FROM ( ");
		sql.append("				SELECT * FROM finance_rentmonth rm1 ");
		sql.append("				WHERE rm1.sdate >= :lastpaysdate and rm1.sdate <= :lastpayedate and rm1.del_flag = "+Rent.DEL_FLAG_NORMAL+" ");
		sql.append("				ORDER BY rm1.create_date DESC ");
		sql.append("			) rm2 GROUP BY rm2.sdate,rm2.rent_id,rm2.infotype ");
		sql.append("	) t inner join finance_rent r on r.id = t.rent_id ");
		sql.append(" inner join finance_house h on h.id = r.house_id ");
		sql.append(" left join sys_user c_p on c_p.id = t.person ");
		sql.append(" left join sys_user c_m on c_m.id = t.busi_manager ");
		sql.append(" left join sys_user c_d on c_d.id = t.busi_departleader ");
		sql.append(" left join sys_user c_t on c_t.id = t.busi_teamleader ");
		sql.append(" where t.del_flag = :del_flag ");
		

		String name = (String)paramMap.get("name");
		if(!StringUtils.isBlank(name)){
			sql.append(" and h.name like :house_name");
			pm.put("house_name", "%"+name+"%");
		}
		sql.append(" order by r.business_num");
		
		pm.put("del_flag", RentMonth.DEL_FLAG_NORMAL);
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		if (rentout_sdate_begin == null){
			rentout_sdate_begin = DateUtils.getFirstDayOfMonth(new Date());
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		pm.put("lastpaysdate", rentout_sdate_begin);
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.getLastDayOfMonth(new Date());
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentout_sdate_end, "yyyy-MM-dd"));
		}
		pm.put("lastpayedate", rentout_sdate_end);
		List<Map<String,Object>> result = findBySql(sql.toString(), pm, Map.class);
		/****************************封装数据到最后list************************************/
		List<RentMonth> rentMonthList = new ArrayList<RentMonth>();
		for(int i = 0 ; i < result.size() ; i++){
			Map<String,Object> rMap = result.get(i);
			RentMonth rentMonth = new RentMonth();
			Rent rent = new Rent();
			House house = new House();
			
			rentMonth.setId((String)rMap.get("id"));
			rentMonth.setInfotype((String)rMap.get("infotype"));
			rentMonth.setSdate((Date)rMap.get("sdate"));
			rentMonth.setEdate((Date)rMap.get("edate"));
			rentMonth.setLastpaysdate((Date)rMap.get("lastpaysdate"));
			rentMonth.setLastpayedate((Date)rMap.get("lastpayedate"));
			rentMonth.setCut_businesssaletype((String)rMap.get("cut_businesssaletype"));
			rentMonth.setAgencyfee((String)rMap.get("agencyfee"));
			rentMonth.setIs_terentrentout(CharUtils.toString((Character)rMap.get("is_terentrentout")));
			
			house.setId((String)rMap.get("house_id"));
			house.setName((String)rMap.get("name"));
			rent.setHouse(house);
			rent.setId((String)rMap.get("rent_id"));
			rent.setBusiness_num((Integer)rMap.get("business_num"));
			
			User person = null;
			if(StringUtils.isNotBlank((String)rMap.get("person_name"))){
				person = new User();
				person.setId((String)rMap.get("person"));
				person.setName((String)rMap.get("person_name"));
				person.setUserBusitype((String)rMap.get("person_busitype"));
				person.setLoginName((String)rMap.get("person_loginname"));
			}
			
			User teamleader = null;
			if(StringUtils.isNotBlank((String)rMap.get("busi_teamleader_name"))){
				teamleader = new User();
				teamleader.setId((String)rMap.get("busi_teamleader"));
				teamleader.setName((String)rMap.get("busi_teamleader_name"));
				teamleader.setLoginName((String)rMap.get("busi_teamleader_loginname"));
			}
			
			User departleader = null;
			if(StringUtils.isNotBlank((String)rMap.get("busi_departleader_name"))){
				departleader = new User();
				departleader.setId((String)rMap.get("busi_departleader"));
				departleader.setName((String)rMap.get("busi_departleader_name"));
				departleader.setLoginName((String)rMap.get("busi_departleader_loginname"));
			}

			User manager = null;
			if(StringUtils.isNotBlank((String)rMap.get("busi_manager_name"))){
				manager = new User();
				manager.setId((String)rMap.get("busi_manager"));
				manager.setName((String)rMap.get("busi_manager_name"));
				manager.setLoginName((String)rMap.get("busi_manager_loginname"));
			}

			rentMonth.setRent(rent);
			rentMonth.setPerson(person);
			rentMonth.setBusi_teamleader(teamleader);
			rentMonth.setBusi_departleader(departleader);
			rentMonth.setBusi_manager(manager);

			rentMonthList.add(rentMonth);
		}
		return rentMonthList;
	}

	
	public RentMonth findSameRentoutByRentin(Map<String, Object> paramMap){
		StringBuffer sql = new StringBuffer();
		Parameter pm = new Parameter();
		sql.append("select t.id,t.rent_id,t.sdate,t.edate,t.lastpaysdate,t.lastpayedate,t.cut_businesssaletype,");
		sql.append(" t.person,c_p.name person_name,c_p.user_busitype person_busitype,c_p.login_name person_loginname, ");
		sql.append(" t.agencyfee,t.person_fixedcut,t.manager_fixedcut,t.departer_fixedcut,t.teamleader_fixedcut, ");
		sql.append(" t.cancelrentdate");
		sql.append(" from  finance_rentmonth t  ");
		sql.append(" left join sys_user c_p on c_p.id = t.person ");
		sql.append(" where t.del_flag = :del_flag and t.infotype = 'rentout' ");
		sql.append(" and t.rent_id = :rent_id ");
		pm.put("del_flag", RentMonth.DEL_FLAG_NORMAL);
		if (null != paramMap.get("rent")){
			pm.put("rent_id", ((Rent)paramMap.get("rent")).getId());
		}else{
			pm.put("rent_id", "null");
		}
		if (StringUtils.isNotEmpty((String)paramMap.get("firstmonth_num"))){
			sql.append(" and t.firstmonth_num = :firstmonth_num ");
			pm.put("firstmonth_num", (String)paramMap.get("firstmonth_num"));
		}
		if(null != paramMap.get("sdate_begin") && null != paramMap.get("sdate_end") ){//传入的是当前出租月记录的上次付租起始时间，查当前rent对应的租进月记录中 上次付租起始时间比它小的。
			Date sdate_begin = null;
			Date sdate_end = null;
			if(paramMap.get("sdate_begin") instanceof Date){
				sdate_begin = (Date)paramMap.get("sdate_begin");
			}else{
				sdate_begin = DateUtils.parseDate(paramMap.get("sdate_begin"));
			}
			if(paramMap.get("sdate_end") instanceof Date){
				sdate_end = (Date)paramMap.get("sdate_end");
			}else{
				sdate_end = DateUtils.parseDate(paramMap.get("sdate_end"));
			}
			sql.append(" and ((t.lastpaysdate <= :sdate_begin and t.lastpayedate >= :sdate_begin) or (t.lastpaysdate >= :sdate_begin and t.lastpaysdate <= :sdate_end)) ");
			pm.put("sdate_begin", sdate_begin);
			pm.put("sdate_end", sdate_end);
			if(null != paramMap.get("queryEdate")){
				Date queryEdate = (Date)paramMap.get("queryEdate");
				sql.append(" and t.lastpaysdate < :queryEdate ");
				pm.put("queryEdate", queryEdate);
			}
		}
		sql.append(" order by t.lastpayedate desc ");
		List<Map<String,Object>> resultList = findBySql(sql.toString(), pm, Map.class);
		/****************************以下是拼接字符串************************************/
		RentMonth rentMonth = null;
		if(null != resultList && resultList.size() > 0){
			Map<String,Object> rMap = resultList.get(0);
			rentMonth = new RentMonth();

			rentMonth.setId((String)rMap.get("id"));
			rentMonth.setSdate((Date)rMap.get("sdate"));
			rentMonth.setEdate((Date)rMap.get("edate"));
			rentMonth.setLastpaysdate((Date)rMap.get("lastpaysdate"));
			rentMonth.setLastpayedate((Date)rMap.get("lastpayedate"));
			rentMonth.setCut_businesssaletype((String)rMap.get("cut_businesssaletype"));
			rentMonth.setAgencyfee((String)rMap.get("agencyfee"));
			rentMonth.setPerson_fixedcut((String)rMap.get("person_fixedcut"));
			rentMonth.setTeamleader_fixedcut((String)rMap.get("teamleader_fixedcut"));
			rentMonth.setDeparter_fixedcut((String)rMap.get("departer_fixedcut"));
			rentMonth.setManager_fixedcut((String)rMap.get("manager_fixedcut"));
			rentMonth.setCancelrentdate((Date)rMap.get("cancelrentdate"));
			
			User person = null;
			if(StringUtils.isNotBlank((String)rMap.get("person_name"))){
				person = new User();
				person.setId((String)rMap.get("person"));
				person.setName((String)rMap.get("person_name"));
				person.setUserBusitype((String)rMap.get("person_busitype"));	
				person.setLoginName((String)rMap.get("person_loginname"));
			}

			
			rentMonth.setPerson(person);
		}
				
		return rentMonth;
	}

	
	public RentMonth findSameRentoutByRentinAndQueryEdate(Map<String, Object> paramMap){
		StringBuffer sql = new StringBuffer();
		Parameter pm = new Parameter();
		sql.append("select t.id,t.rent_id,t.sdate,t.edate,t.lastpaysdate,t.lastpayedate,t.cut_businesssaletype,");
		sql.append(" t.person,c_p.name person_name,c_p.user_busitype person_busitype,c_p.login_name person_loginname, ");
		sql.append(" t.agencyfee,t.person_fixedcut,t.manager_fixedcut,t.departer_fixedcut,t.teamleader_fixedcut, ");
		sql.append(" t.cancelrentdate");
		sql.append(" from  finance_rentmonth t  ");
		sql.append(" left join sys_user c_p on c_p.id = t.person ");
		sql.append(" where t.del_flag = :del_flag and t.infotype = 'rentout' ");
		sql.append(" and t.rent_id = :rent_id ");
		pm.put("del_flag", RentMonth.DEL_FLAG_NORMAL);
		if (null != paramMap.get("rent")){
			pm.put("rent_id", ((Rent)paramMap.get("rent")).getId());
		}else{
			pm.put("rent_id", "null");
		}

		if(null != paramMap.get("queryEdate")){
			Date queryEdate = (Date)paramMap.get("queryEdate"); 
			sql.append(" and t.sdate <= :queryEdate and t.edate > :queryEdate");
			pm.put("queryEdate", queryEdate);
		}
		sql.append(" order by t.lastpayedate desc limit 0,1 ");
		List<Map<String,Object>> resultList = findBySql(sql.toString(), pm, Map.class);
		/****************************以下是拼接字符串************************************/
		RentMonth rentMonth = null;
		if(null != resultList && resultList.size() > 0){
			Map<String,Object> rMap = resultList.get(0);
			rentMonth = new RentMonth();

			rentMonth.setId((String)rMap.get("id"));
			rentMonth.setSdate((Date)rMap.get("sdate"));
			rentMonth.setEdate((Date)rMap.get("edate"));
			rentMonth.setLastpaysdate((Date)rMap.get("lastpaysdate"));
			rentMonth.setLastpayedate((Date)rMap.get("lastpayedate"));
			rentMonth.setCut_businesssaletype((String)rMap.get("cut_businesssaletype"));
			rentMonth.setAgencyfee((String)rMap.get("agencyfee"));
			rentMonth.setPerson_fixedcut((String)rMap.get("person_fixedcut"));
			rentMonth.setTeamleader_fixedcut((String)rMap.get("teamleader_fixedcut"));
			rentMonth.setDeparter_fixedcut((String)rMap.get("departer_fixedcut"));
			rentMonth.setManager_fixedcut((String)rMap.get("manager_fixedcut"));
			rentMonth.setCancelrentdate((Date)rMap.get("cancelrentdate"));
			
			User person = null;
			if(StringUtils.isNotBlank((String)rMap.get("person_name"))){
				person = new User();
				person.setId((String)rMap.get("person"));
				person.setName((String)rMap.get("person_name"));
				person.setUserBusitype((String)rMap.get("person_busitype"));	
				person.setLoginName((String)rMap.get("person_loginname"));
			}

			
			rentMonth.setPerson(person);
		}
				
		return rentMonth;
	}


	public List<RentMonth> findByName(String name){
		return find("from Rentmonth where name = :p1", new Parameter(name));
	}
	/**
	 * 根据上次付租的起始时间和结束时间找到相应的月记录
	 * @param name
	 * @return
	 */
	public RentMonth findByNameLastpaySdateAndEdate(Rent rent, Date lastpaysdate,Date lastpayedate,RentMonth.INFOTYPE infotype){
		List<RentMonth> list = findBySql("select * from finance_rentmonth rm where rm.rent_id = :p1 and rm.lastpaysdate = :p2 and rm.lastpayedate = :p3 and rm.infotype = :p4", new Parameter(rent.getId(),lastpaysdate,lastpayedate,infotype.toString()),RentMonth.class);
		return list.size()>0?list.get(0):null;
	}
	/**
	 * 根据上级字符串，本级字符串，上级id获取相应的本级用户集合
	 * @param paramMap
	 * @param leaderstr
	 * @param memberstr
	 * @return
	 */
	public List<User> findUserSortUsetTypeLevel(Map<String, Object> paramMap,String leaderstr,String memberstr,String leaderid){
		Parameter pm = new Parameter();
		
		StringBuffer sql = new StringBuffer();
		sql.append(" ");
		sql.append("select u.* ");
		sql.append(" from (  ");
		sql.append("		SELECT * FROM (  ");
		sql.append("				SELECT * FROM finance_rentmonth rm1  ");
		sql.append("				WHERE rm1.sdate >= :lastpaysdate and rm1.sdate <= :lastpayedate and rm1.del_flag = :del_flag  ");
		sql.append("				ORDER BY rm1.create_date DESC  ");
		//sql.append("			) rm2 GROUP BY rm2.sdate,rm2.rent_id  ");//9.2 13:11改
		sql.append("			) rm2   ");
		sql.append("	) t ");
		sql.append(" inner join sys_user u on u.id = t.xx ");
		sql.append("where  t.xx <> '' and t.xx is not null ");
		if(!"busi_manager".equals(memberstr)){//如果查询的本级为经理级，则没有上级
			sql.append("  and t.yy = :leaderid");
			pm.put("leaderid", leaderid);
		}
		sql.append(" group by t.xx ");
		
		String sqlStr = sql.toString();
		if(!"busi_manager".equals(memberstr)){//如果查询的本级为经理级，则没有上级
			sqlStr = sqlStr.replaceAll("yy", leaderstr);
		}
		sqlStr = sqlStr.replaceAll("xx", memberstr);
		
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		if (rentout_sdate_begin == null){
			rentout_sdate_begin = DateUtils.getFirstDayOfMonth(new Date());
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		pm.put("lastpaysdate", rentout_sdate_begin);
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.getLastDayOfMonth(new Date());
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentout_sdate_end, "yyyy-MM-dd"));
		}
		pm.put("lastpayedate", rentout_sdate_end);
		pm.put("del_flag", RentMonth.DEL_FLAG_NORMAL);
		
		List<User> userlist = findBySql(sqlStr, pm, User.class);
		
		return userlist;
	}
}
