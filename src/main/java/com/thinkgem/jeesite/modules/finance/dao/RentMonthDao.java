/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
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


	public List<RentMonth> findByName(String name){
		return find("from Rentmonth where name = :p1", new Parameter(name));
	}
}
