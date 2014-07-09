/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;

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
		sql.append("from (select * from finance_rentmonth r order by r.nextpaydate DESC) rr ");
		sql.append("where rr.del_flag=:del_flag  ");
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
		sql.append("from (select * from finance_rentmonth r order by r.nextpaydate DESC) rr ");
		sql.append("where rr.del_flag=:del_flag  ");
		sql.append("and rr.infotype=:infotype  ");
		sql.append("group by rr.rent_id having  datediff(max(rr.nextpaydate),current_timestamp)<7  ) rrr order by rrr.nextpaydate");
		Parameter pm = new Parameter();
		pm.put("del_flag", Rent.DEL_FLAG_NORMAL);
		pm.put("infotype", "rentout");
		return findBySql(sql.toString(), pm, RentMonth.class);
	}
	public List<RentMonth> rentInListWillReachEdate(){
		StringBuffer sql = new StringBuffer();

		sql.append("select * from (select max(rr.edate) SS,rr.* ");
		sql.append("from (select * from finance_rentmonth r order by r.edate DESC) rr ");
		sql.append("where rr.del_flag=:del_flag  ");
		sql.append("and rr.infotype=:infotype  ");
		sql.append("group by rr.rent_id having  datediff(max(rr.edate),current_timestamp)<=30  ) rrr order by rrr.edate");
		Parameter pm = new Parameter();
		pm.put("del_flag", Rent.DEL_FLAG_NORMAL);
		pm.put("infotype", "rentin");
		return findBySql(sql.toString(), pm, RentMonth.class);
	}
	public List<RentMonth> rentOutListWillReachEdate(){
		StringBuffer sql = new StringBuffer();

		sql.append("select * from (select max(rr.edate) SS,rr.* ");
		sql.append("from (select * from finance_rentmonth r order by r.edate DESC) rr ");
		sql.append("where rr.del_flag=:del_flag  ");
		sql.append("and rr.infotype=:infotype  ");
		sql.append("group by rr.rent_id having  datediff(max(rr.edate),current_timestamp)<=30  ) rrr order by rrr.edate");
		Parameter pm = new Parameter();
		pm.put("del_flag", Rent.DEL_FLAG_NORMAL);
		pm.put("infotype", "rentout");
		return findBySql(sql.toString(), pm, RentMonth.class);
	}
	/**
	 * 获取业绩提成基础列表
	 * @param paramMap
	 * @return
	 */
	public List<Map<String,Object>> getBusinesscutBaseList(Map<String, Object> paramMap){
		StringBuffer sql = new StringBuffer();
		Parameter pm = new Parameter();
		sql.append("select t.id,t.rent_id,h.id house_id,h.name,t.sdate,t.edate,t.lastpaysdate,t.lastpayedate,t.cut_businesssaletype,");
		sql.append(" t.person,c_p.name person_name,c_p.user_busitype person_busitype,t.busi_manager,c_m.name busi_manager_name,t.busi_departleader,c_d.name busi_departleader_name, ");
		sql.append(" t.busi_teamleader,c_t.name busi_teamleader_name,r.business_num,t.agencyfee ");
		sql.append(" from ( ");
		sql.append("		select rm.* from finance_rentmonth rm  ");
		sql.append("		where rm.lastpaysdate <= :lastpayedate and rm.lastpayedate >= :lastpaysdate");
		/*sql.append("		union  ");
		sql.append("		select rm.*,r.name,r.business_num from finance_rentmonth rm  ");
		sql.append("		inner join finance_rent r on r.id = rm.rent_id ");
		sql.append("		where rm.lastpayedate >= :lastpayedate ");*/
		sql.append("	) t inner join finance_rent r on r.id = t.rent_id ");
		sql.append(" inner join finance_house h on h.id = r.house_id ");
		sql.append(" left join sys_user c_p on c_p.id = t.person ");
		sql.append(" left join sys_user c_m on c_m.id = t.busi_manager ");
		sql.append(" left join sys_user c_d on c_d.id = t.busi_departleader ");
		sql.append(" left join sys_user c_t on c_t.id = t.busi_teamleader ");
		sql.append(" where t.del_flag = :del_flag ");
		String infotype = (String)paramMap.get("infotype");
		if(!StringUtils.isBlank(infotype) && "businessCount".equals(infotype)){//完成量统计
			sql.append(" and t.lastpaysdate = t.sdate");
		}else{
			sql.append(" and t.infotype = 'rentin'");
		}
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
		
		return findBySql(sql.toString(), pm, Map.class);
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
}
