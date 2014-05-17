/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.dao.VacantPeriodDao;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;

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
	
	@Autowired
	private RentMonthDao rentMonthDao;

	
	public Map<String,Object> vacantPeriod(Map<String, Object> paramMap) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();
		dc.createAlias("rent", "rent");
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		
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
		dc.add(Restrictions.isNotNull("firstmonth_num"));
		dc.add(Restrictions.eq("infotype", "rentout"));
		dc.add(Restrictions.not(Restrictions.or(Restrictions.lt("lastpayedate", rentin_sdate_begin),Restrictions.gt("lastpaysdate", rentin_sdate_end))));

		String name = (String)paramMap.get("name");
		if(!StringUtils.isBlank(name)){
			dc.add(Restrictions.like("rent.name", "%"+name+"%"));
		}

		List<RentMonth> list = rentMonthDao.find(dc); 
		List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
		Map<String,Object> totalMap = new HashMap<String,Object>();
		long rentin_cut = 0;
		long rentout_cut = 0;
		long teamleader_cut = 0;
		long departleader_cut = 0;
		long manager_cut = 0;
		long rentin_cut_total = 0;//租进业务员总数
		long rentout_cut_total = 0;//租出业务员总数
		long teamleader_cut_total = 0;//组长总数
		long departleader_cut_total = 0;//部长总数
		long manager_cut_total = 0;//经理总数
		List<VacantPeriod> vacantPeriods = new ArrayList<VacantPeriod>();
		Date recentVacantPeriodSdate = null;//与当前设置日期最近的空置期起始时间
		Date recentVacantPeriodEdate = null;//与当前设置日期最近的空置期结束时间
		int greatThanDateNum = 0;//月包租明细大于空置期的数量
		int greatThanDateNumTemp = 0;//月包租明细大于空置期的临时数量
		double cutlevel = 1;//提成折扣
		
		for(RentMonth rentmonth : list){
			Map<String,Object> resultMap = new HashMap<String,Object>();
			
			vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
			recentVacantPeriodSdate = null;
			recentVacantPeriodEdate = null;
			greatThanDateNum = 99999;
			for(VacantPeriod vp : vacantPeriods){ //此循环是为了获取实际要用到的空置期
				if(null == vp.getSdate()){
					continue;
				}
				greatThanDateNumTemp = rentmonth.getSdate().compareTo(vp.getSdate());
				if(greatThanDateNumTemp > 0 && greatThanDateNumTemp < greatThanDateNum){//如果月租起始日期大于空置期起始日期 并且 大的数量小于上一次比较的数量
					recentVacantPeriodSdate = vp.getSdate();
					recentVacantPeriodEdate = vp.getEdate();
				}
				greatThanDateNum = greatThanDateNumTemp;
				
			}
			if(99999 == greatThanDateNum){//如果greatThanDateNum仍然是99999，则表示没设置空置期，则跳出
				continue;
			}
			long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));
			long vacantperiod = DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE)-DateUtils.compareDates(rentmonth.getLastpayedate(), rentmonth.getLastpaysdate(), Calendar.DATE);
			
			if(DateUtils.compareDates(rentmonth.getEdate(), rentmonth.getSdate(), Calendar.DATE) <360){//如果租出时间少于1年，则提成减半
				cutlevel = 0.5;
			}
			
			rentin_cut = Math.round(rentout_rentmonth/30 * 0.2 * vacantperiod * cutlevel);
			rentout_cut = Math.round(rentout_rentmonth/30 * 0.2 * vacantperiod * cutlevel);
			teamleader_cut = Math.round(rentout_rentmonth/30  * 0.08 * vacantperiod * cutlevel);
			departleader_cut = Math.round(rentout_rentmonth/30  * 0.07 * vacantperiod * cutlevel );
			manager_cut = Math.round(rentout_rentmonth/30 * 0.05 * vacantperiod * cutlevel );
			
			resultMap.put("rent", rentmonth.getRent());
			resultMap.put("vacantperiod", vacantperiod);//空置期天数
			resultMap.put("rentin_cut", rentin_cut);//租进业务员提成
			resultMap.put("rentout_cut", rentout_cut);//租出业务员提成
			resultMap.put("teamleader_cut", teamleader_cut);//组长提成
			resultMap.put("departleader_cut", departleader_cut);//部长提成
			resultMap.put("manager_cut", manager_cut);//经理提成
			rentin_cut_total += rentin_cut;
			rentout_cut_total += rentout_cut;
			teamleader_cut_total += teamleader_cut;
			departleader_cut_total += departleader_cut;
			manager_cut_total += manager_cut;
			resultlist.add(resultMap);
		}
		
		totalMap.put("rentin_cut_total", rentin_cut_total);
		totalMap.put("rentout_cut_total", rentout_cut_total);
		totalMap.put("teamleader_cut_total", teamleader_cut_total);
		totalMap.put("departleader_cut_total", departleader_cut_total);
		totalMap.put("manager_cut_total", manager_cut_total);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list", resultlist);
		result.put("total", totalMap);
		
		return result;
	}

	public static void main(String[] args){
		double i = 1456.88888;
		long j = 30;
		System.out.println(Math.round(i/j));
		
	}
}
