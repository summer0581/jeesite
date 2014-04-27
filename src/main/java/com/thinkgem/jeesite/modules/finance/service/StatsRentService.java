/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.finance.dao.VacantPeriodDao;

/**
 * 包租统计Service
 * @author 夏天
 * @version 2014-04-13
 */
@Component
@Transactional(readOnly = true)
public class StatsRentService extends BaseService {

	@Autowired
	private VacantPeriodDao vacantPeriodDao;
	
	public Map<String,Object> vacantPeriod(Map<String, Object> paramMap) {
		StringBuilder ql = new StringBuilder();
		Parameter pm = new Parameter();
		ql.append(" select new map(r.house as house, rip.name as rentin_person,rop.name as rentout_person,  ");
		ql.append(" r.rentin_sdate as rentin_sdate,r.rentin_edate as rentin_edate, ");
		ql.append(" r.rentout_sdate as rentout_sdate, r.rentout_edate as rentout_edate, ");
		ql.append(" r.rentout_rentmonth as rentout_rentmonth ");
		ql.append(" ) from Rent r join r.rentin_person rip join r.rentout_person rop join r.house h join h.team_leader htl ");
		ql.append(" where r.delFlag = :delFlag ");
		pm.put("delFlag", Article.DEL_FLAG_NORMAL);
		
		Date rentin_sdate_begin = DateUtils.parseDate(paramMap.get("rentin_sdate_begin"));
		if (rentin_sdate_begin == null){
			rentin_sdate_begin = DateUtils.parseDate(Global.getConfig("sys.default_sdate"));
			paramMap.put("rentin_sdate_begin", DateUtils.formatDate(rentin_sdate_begin, "yyyy-MM-dd"));
		}
		Date rentin_sdate_end = DateUtils.parseDate(paramMap.get("rentin_sdate_end"));
		if (rentin_sdate_end == null){
			rentin_sdate_end = DateUtils.parseDate(Global.getConfig("sys.default_edate"));
			paramMap.put("rentin_sdate_end", DateUtils.formatDate(rentin_sdate_end, "yyyy-MM-dd"));
		}
		
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		if (rentout_sdate_begin == null){
			rentout_sdate_begin = DateUtils.parseDate(Global.getConfig("sys.default_sdate"));
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.parseDate(Global.getConfig("sys.default_edate"));
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentin_sdate_end, "yyyy-MM-dd"));
		}
		String name = (String)paramMap.get("name");
		if(!StringUtils.isBlank(name)){
			ql.append(" and h.name like :name ");
			pm.put("name", "%"+name+"%");
		}
		
		ql.append(" and r.rentin_sdate between :rentin_sdate_begin and :rentin_sdate_end");
		pm.put("rentin_sdate_begin", rentin_sdate_begin);
		pm.put("rentin_sdate_end", rentin_sdate_end);
		
		ql.append(" and r.rentout_sdate between :rentout_sdate_begin and :rentout_sdate_end");
		pm.put("rentout_sdate_begin", rentout_sdate_begin);
		pm.put("rentout_sdate_end", rentout_sdate_end);

		List<Map<String, Object>> list = vacantPeriodDao.find(ql.toString(), pm);
		Map<String,Object> totalMap = new HashMap<String,Object>();
		long rentin_cut = 0;
		long rentout_cut = 0;
		long teamleader_cut = 0;
		long manager_cut = 0;
		long rentin_cut_total = 0;//租进业务员总数
		long rentout_cut_total = 0;//租出业务员总数
		long teamleader_cut_total = 0;//组长总数
		long manager_cut_total = 0;//经理总数
		for(Map<String,Object> resultmap : list){
			Date rentin_sdate = (Date)resultmap.get("rentin_sdate");
			Date rentout_edate = (Date)resultmap.get("rentout_sdate");
			long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)resultmap.get("rentout_rentmonth"), "0"));
			long vacantperiod = 45-DateUtils.compareDates(rentout_edate, rentin_sdate, Calendar.DATE);
			resultmap.put("vacantperiod", vacantperiod);//空置期天数
			rentin_cut = Math.round(rentout_rentmonth/30 * 0.2 * vacantperiod );
			rentout_cut = Math.round(rentout_rentmonth/30 * 0.2 * vacantperiod );
			teamleader_cut = Math.round(rentout_rentmonth/30  * 0.1 * vacantperiod );
			manager_cut = Math.round(rentout_rentmonth/30 * 0.1 * vacantperiod );
			resultmap.put("rentin_cut", rentin_cut);//租进业务员提成
			resultmap.put("rentout_cut", rentout_cut);//租出业务员提成
			resultmap.put("teamleader_cut", teamleader_cut);//组长提成
			resultmap.put("manager_cut", manager_cut);//经理提成
			rentin_cut_total += rentin_cut;
			rentout_cut_total += rentout_cut;
			teamleader_cut_total += teamleader_cut;
			manager_cut_total += manager_cut;
		}
		totalMap.put("rentin_cut_total", rentin_cut_total);
		totalMap.put("rentout_cut_total", rentout_cut_total);
		totalMap.put("teamleader_cut_total", teamleader_cut_total);
		totalMap.put("manager_cut_total", manager_cut_total);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list", list);
		result.put("total", totalMap);
		
		return result;
	}

	public static void main(String[] args){
		double i = 1456.88888;
		long j = 30;
		System.out.println(Math.round(i/j));
		
	}
}
