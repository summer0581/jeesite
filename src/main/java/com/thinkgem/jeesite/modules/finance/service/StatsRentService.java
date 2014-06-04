/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.MathUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.constant.CutConfigPersonConstant;
import com.thinkgem.jeesite.modules.finance.constant.CutConfigTypeConstant;
import com.thinkgem.jeesite.modules.finance.constant.VacantPeriodConstant;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.dao.VacantPeriodDao;
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 包租统计Service
 * @author 夏天
 * @version 2014-04-13
 */
@Service
@Transactional(readOnly = true)
public class StatsRentService extends BaseService {

	@Autowired
	private VacantPeriodDao vacantPeriodDao;
	
	@Autowired
	private RentMonthDao rentMonthDao;
	
	@Autowired
	private RentMonthService rentMonthService;
	
	@Autowired
	private CutconfigService cutconfigService;

	
	public Map<String,Object> vacantPeriod(Map<String, Object> paramMap) throws Exception{

		List<RentMonth> list = getVacantperiodBaseList(paramMap);
		
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
		String recentVacantType = "";//空置期类型
		int greatThanDateNum = 0;//月包租明细大于空置期的数量
		int greatThanDateNumTemp = 0;//月包租明细大于空置期的临时数量
		double cutlevel = 1;//提成折扣
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录

		

		for(RentMonth rentmonth : list){
			Map<String,Object> resultMap = new HashMap<String,Object>();

			lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
			
			Map<String,Object> paramMap1 = new HashMap<String,Object>();
			paramMap1.put("infotype", "rentin");
			paramMap1.put("rent", rentmonth.getRent());
			paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
			paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
			List<RentMonth> tempList = rentMonthService.find(paramMap1);
			if(null != tempList && tempList.size() > 0){
				sameMonthRentin = tempList.get(0);
			}else{
				sameMonthRentin = null;
				continue;
			}
			
			vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
			recentVacantPeriodSdate = null;
			recentVacantPeriodEdate = null;
			recentVacantType = "";
			greatThanDateNum = 99999;
			for(VacantPeriod vp : vacantPeriods){ //此循环是为了获取实际要用到的空置期
				if(null == vp.getSdate() || null == vp.getEdate() || VacantPeriodConstant.LANDLORD_VACANTPERIOD.equals(vp.getType())){
					continue;
				}
				greatThanDateNumTemp = rentmonth.getSdate().compareTo(vp.getSdate());
				if(greatThanDateNumTemp > 0 && greatThanDateNumTemp < greatThanDateNum){//如果月租起始日期大于空置期起始日期 并且 大的数量小于上一次比较的数量
					recentVacantPeriodSdate = vp.getSdate();
					recentVacantPeriodEdate = vp.getEdate();
					recentVacantType = vp.getType();
				}
				greatThanDateNum = greatThanDateNumTemp;
				
			}
			if(99999 == greatThanDateNum){//如果greatThanDateNum仍然是99999，则表示没设置空置期，则跳出
				continue;
			}
			if(null != lastRentoutMonth && recentVacantPeriodSdate.compareTo(lastRentoutMonth.getLastpaysdate()) >0){//如果上一次出租月记录的付租起始时间 > 空置期起始日期，则排除，说明已计算过空置期了
				continue;
			}
			long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));
			long vacantperiod = DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE)-DateUtils.compareDates(rentmonth.getLastpaysdate(), rentmonth.getRent().getRentin_sdate(), Calendar.DATE);
			
			if(DateUtils.compareDates(rentmonth.getEdate(), rentmonth.getSdate(), Calendar.DATE) <360){//如果租出时间少于1年，则提成减半
				cutlevel = 0.5;
			}
			if(VacantPeriodConstant.NOSALE_VACANTPERIOD.equals(recentVacantType) && vacantperiod > 0){//如果空置期为公司提供的，当空置时间大于0 ，无提成；小于0，则扣提成
				cutlevel = 0.0;
			}
			cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(sameMonthRentin.getCut_vacantperiodtype());
			rentin_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
			rentout_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
			teamleader_cut = Math.round(rentout_rentmonth/30  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
			departleader_cut = Math.round(rentout_rentmonth/30  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
			manager_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
			
			resultMap.put("rentinmonth", sameMonthRentin);
			resultMap.put("rentmonth", rentmonth);
			resultMap.put("vacantperiodconfig", DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));
			resultMap.put("vacantperiod", vacantperiod);//空置期天数
			resultMap.put("rentin_cut", rentin_cut);//租进业务员提成
			resultMap.put("rentout_cut", rentout_cut);//租出业务员提成
			resultMap.put("teamleader_cut", teamleader_cut);//组长提成
			resultMap.put("departleader_cut", departleader_cut);//部长提成
			resultMap.put("manager_cut", manager_cut);//经理提成
			resultMap.put("vacantperiod_type", recentVacantType);//空置期提成类型
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
	
	/**
	 * 按人统计空置期的总计
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> vacantPeriod4Person(Map<String, Object> paramMap) throws Exception{

		List<RentMonth> list = getVacantperiodBaseList(paramMap); 
		
		Map<String,LinkedHashSet<User>> map = getLevelUsersWithRentMonths(list,paramMap);
		LinkedHashSet<User> managers = map.get("managers");
		LinkedHashSet<User> departleaders = map.get("departleaders");
		LinkedHashSet<User> teamleaders = map.get("teamleaders");
		LinkedHashSet<User> salers = map.get("salers");//普通业务员

		
		List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
		Map<String,Object> totalMap = new HashMap<String,Object>();
		long rentin_cut = 0;
		long rentout_cut = 0;
		long teamleader_cut = 0;
		long departleader_cut = 0;
		long manager_cut = 0;
		int rentin_cut_total = 0;//租进业务员总数
		int rentout_cut_total = 0;//租出业务员总数
		int cut_total = 0;//组长总数

		List<VacantPeriod> vacantPeriods = new ArrayList<VacantPeriod>();
		Date recentVacantPeriodSdate = null;//与当前设置日期最近的空置期起始时间
		Date recentVacantPeriodEdate = null;//与当前设置日期最近的空置期结束时间
		String recentVacantType = "";//空置期类型
		int greatThanDateNum = 0;//月包租明细大于空置期的数量
		int greatThanDateNumTemp = 0;//月包租明细大于空置期的临时数量
		double cutlevel = 1;//提成折扣
		String username_temp= "";//用户名占位符
		Map<String,Integer> rentoutPeriod = new HashMap<String,Integer>();//承租的所有人的提成总计
		Map<String,Integer> rentinPeriod = new HashMap<String,Integer>();//出租的所有人的提成总计
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录
		
			for(RentMonth rentmonth : list){
				
				lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
				
				Map<String,Object> paramMap1 = new HashMap<String,Object>();
				paramMap1.put("infotype", "rentin");
				paramMap1.put("rent", rentmonth.getRent());
				paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
				paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
				List<RentMonth> tempList = rentMonthService.find(paramMap1);
				if(null != tempList && tempList.size() > 0){
					sameMonthRentin = tempList.get(0);
				}else{
					sameMonthRentin = null;
					continue;
				}
				
				vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
				recentVacantPeriodSdate = null;
				recentVacantPeriodEdate = null;
				recentVacantType = "";
				greatThanDateNum = 99999;
				for(VacantPeriod vp : vacantPeriods){ //此循环是为了获取实际要用到的空置期
					if(null == vp.getSdate() || null == vp.getEdate() || VacantPeriodConstant.LANDLORD_VACANTPERIOD.equals(vp.getType())){
						continue;
					}
					greatThanDateNumTemp = rentmonth.getSdate().compareTo(vp.getSdate());
					if(greatThanDateNumTemp > 0 && greatThanDateNumTemp < greatThanDateNum){//如果月租起始日期大于空置期起始日期 并且 大的数量小于上一次比较的数量
						recentVacantPeriodSdate = vp.getSdate();
						recentVacantPeriodEdate = vp.getEdate();
						recentVacantType = vp.getType();
					}
					greatThanDateNum = greatThanDateNumTemp;
					
				}
				if(99999 == greatThanDateNum){//如果greatThanDateNum仍然是99999，则表示没设置空置期，则跳出
					continue;
				}
				if(null != lastRentoutMonth && recentVacantPeriodSdate.compareTo(lastRentoutMonth.getLastpaysdate()) >0){//如果上一次出租月记录的付租起始时间 > 空置期起始日期，则排除，说明已计算过空置期了
					continue;
				}
				long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));
				long vacantperiod = DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE)-DateUtils.compareDates(rentmonth.getLastpaysdate(), rentmonth.getRent().getRentin_sdate(), Calendar.DATE);
				
				if(DateUtils.compareDates(rentmonth.getEdate(), rentmonth.getSdate(), Calendar.DATE) <360){//如果租出时间少于1年，则提成减半
					cutlevel = 0.5;
				}
				if(VacantPeriodConstant.NOSALE_VACANTPERIOD.equals(recentVacantType) && vacantperiod > 0){//如果空置期为公司提供的，当空置时间大于0 ，无提成；小于0，则扣提成
					cutlevel = 0.0;
				}
				cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(sameMonthRentin.getCut_vacantperiodtype());
				rentin_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				rentout_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				teamleader_cut = Math.round(rentout_rentmonth/30  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				departleader_cut = Math.round(rentout_rentmonth/30  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
				manager_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
				
				/**************************以下是空置期合计的计算**********************/
				username_temp = sameMonthRentin.getPerson().getLoginName();
				rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)rentin_cut);//通过用户名(承租业务员)，去累加他的承租空置期提成
				if(null != sameMonthRentin.getBusi_departleader()){//如果有部长
					username_temp = sameMonthRentin.getBusi_departleader().getLoginName();
					rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)departleader_cut);//通过用户名(部长)，去累加他的承租空置期提成
				}
				if(null != sameMonthRentin.getBusi_manager()){
					username_temp = sameMonthRentin.getBusi_manager().getLoginName();
					rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)manager_cut);//通过用户名(经理)，去累加他的承租空置期提成
				}
				if(null != sameMonthRentin.getBusi_teamleader()){
					username_temp = rentmonth.getBusi_teamleader().getLoginName();
					rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)teamleader_cut);//通过用户名(组长)，去累加他的承租空置期提成
				}
				
				username_temp = rentmonth.getPerson().getLoginName();
				rentoutPeriod.put(username_temp, MathUtils.deNull(rentoutPeriod.get(username_temp))+(int)rentout_cut);//通过用户名(出租业务员)，去累加他的出租租空置期提成
				
			}
			
			/***********************以下是封装最后合计列表数据************************/
			Map<String,Object> resultMap = new HashMap<String,Object>();
			for(User user : managers){
				resultMap = new HashMap<String,Object>();
				username_temp = user.getLoginName();
				resultMap.put("person", user);
				resultMap.put("periodTotal", MathUtils.deNull(rentinPeriod.get(username_temp)));
				resultlist.add(resultMap);
				cut_total += (Integer)resultMap.get("periodTotal");
			}
			
			for(User user : departleaders){
				resultMap = new HashMap<String,Object>();
				username_temp = user.getLoginName();
				resultMap.put("person", user);
				resultMap.put("periodTotal", MathUtils.deNull(rentinPeriod.get(username_temp)));
				resultlist.add(resultMap);
				cut_total += (Integer)resultMap.get("periodTotal");
			}
			
			for(User user : teamleaders){
				resultMap = new HashMap<String,Object>();
				username_temp = user.getLoginName();
				resultMap.put("person", user);
				resultMap.put("periodTotal", MathUtils.deNull(rentinPeriod.get(username_temp)));
				resultlist.add(resultMap);
				cut_total += (Integer)resultMap.get("periodTotal");
			}
			
			
			for(User user : salers) {
				resultMap = new HashMap<String,Object>();
				username_temp = user.getLoginName();
				resultMap.put("person", user);
				resultMap.put("rentinPeriodTotal", MathUtils.deNull(rentinPeriod.get(username_temp)));
				resultMap.put("rentoutPeriodTotal", MathUtils.deNull(rentoutPeriod.get(username_temp)));
				resultMap.put("periodTotal", MathUtils.deNull(rentinPeriod.get(username_temp)) + MathUtils.deNull(rentoutPeriod.get(username_temp)));
				resultlist.add(resultMap);
				
				rentin_cut_total += (Integer)resultMap.get("rentinPeriodTotal");
				rentout_cut_total += (Integer)resultMap.get("rentoutPeriodTotal");
				cut_total += (Integer)resultMap.get("periodTotal");
			}
			
			totalMap.put("rentin_cut_total", rentin_cut_total);
			totalMap.put("rentout_cut_total", rentout_cut_total);
			totalMap.put("cut_total", cut_total);


		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list", resultlist);
		result.put("total", totalMap);
		return result;

	}
	
	public Map<String,Object> vacantPeriodDetail4Person(Map<String, Object> paramMap) throws Exception{
		String personid = (String)paramMap.get("personid");
		User person = UserUtils.getUserById(personid);

		List<RentMonth> list = getVacantperiodBaseList(paramMap); 
		List<Map<String, Object>> rentinRentMonths = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rentoutRentMonths = new ArrayList<Map<String, Object>>();


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
		String recentVacantType = "";//空置期类型
		int greatThanDateNum = 0;//月包租明细大于空置期的数量
		int greatThanDateNumTemp = 0;//月包租明细大于空置期的临时数量
		double cutlevel = 1;//提成折扣
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录


		
			for(RentMonth rentmonth : list){
				lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
				
				Map<String,Object> paramMap1 = new HashMap<String,Object>();
				paramMap1.put("infotype", "rentin");
				paramMap1.put("rent", rentmonth.getRent());
				paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
				paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
				List<RentMonth> tempList = rentMonthService.find(paramMap1);
				if(null != tempList && tempList.size() > 0){
					sameMonthRentin = tempList.get(0);
				}else{
					sameMonthRentin = null;
					continue;
				}
				Map<String,Object> resultMap = new HashMap<String,Object>();
				
				vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
				recentVacantPeriodSdate = null;
				recentVacantPeriodEdate = null;
				recentVacantType = "";
				greatThanDateNum = 99999;
				for(VacantPeriod vp : vacantPeriods){ //此循环是为了获取实际要用到的空置期
					if(null == vp.getSdate() || null == vp.getEdate() || VacantPeriodConstant.LANDLORD_VACANTPERIOD.equals(vp.getType())){
						continue;
					}
					greatThanDateNumTemp = rentmonth.getSdate().compareTo(vp.getSdate());
					if(greatThanDateNumTemp > 0 && greatThanDateNumTemp < greatThanDateNum){//如果月租起始日期大于空置期起始日期 并且 大的数量小于上一次比较的数量
						recentVacantPeriodSdate = vp.getSdate();
						recentVacantPeriodEdate = vp.getEdate();
						recentVacantType = vp.getType();
					}
					greatThanDateNum = greatThanDateNumTemp;
					
				}
				if(99999 == greatThanDateNum){//如果greatThanDateNum仍然是99999，则表示没设置空置期，则跳出
					continue;
				}
				if(null != lastRentoutMonth && recentVacantPeriodSdate.compareTo(lastRentoutMonth.getLastpaysdate()) >0){//如果上一次出租月记录的付租起始时间 > 空置期起始日期，则排除，说明已计算过空置期了
					continue;
				}
				long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));
				long vacantperiod = DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE)-DateUtils.compareDates(rentmonth.getLastpaysdate(), rentmonth.getRent().getRentin_sdate(), Calendar.DATE);
				
				if(DateUtils.compareDates(rentmonth.getEdate(), rentmonth.getSdate(), Calendar.DATE) <360){//如果租出时间少于1年，则提成减半
					cutlevel = 0.5;
				}
				if(VacantPeriodConstant.NOSALE_VACANTPERIOD.equals(recentVacantType) && vacantperiod > 0){//如果空置期为公司提供的，当空置时间大于0 ，无提成；小于0，则扣提成
					cutlevel = 0.0;
				}
				
				
				resultMap.put("rentinmonth", sameMonthRentin);
				resultMap.put("rentmonth", rentmonth);
				resultMap.put("vacantperiodconfig", DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));//空置期天数
				resultMap.put("vacantperiod", vacantperiod);//空置期天数
				resultMap.put("vacantperiod_type", recentVacantType);//空置期提成类型


				cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(sameMonthRentin.getCut_vacantperiodtype());
				if(person.equals(sameMonthRentin.getBusi_manager())){//判断此人是不是经理
					manager_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
					manager_cut_total += manager_cut;
					resultMap.put("manager_cut", manager_cut);
					rentinRentMonths.add(resultMap);
				}
				if(person.equals(sameMonthRentin.getBusi_departleader())){//判断此人是不是部长
					departleader_cut = Math.round(rentout_rentmonth/30  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
					departleader_cut_total += departleader_cut;
					resultMap.put("departleader_cut", departleader_cut);
					rentinRentMonths.add(resultMap);
				}
				if(person.equals(sameMonthRentin.getBusi_teamleader())){//判断此人是不是组长
					teamleader_cut = Math.round(rentout_rentmonth/30  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
					teamleader_cut_total += teamleader_cut;
					resultMap.put("teamleader_cut", teamleader_cut);
					rentinRentMonths.add(resultMap);
				}
				if(sameMonthRentin.getPerson().equals(person)){//判断是否为租进业务员
					rentin_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
					rentin_cut_total += rentin_cut;
					resultMap.put("rentin_cut", rentin_cut);
					rentinRentMonths.add(resultMap);
				}
				
				
				if(rentmonth.getPerson().equals(person)){//判断是否为租出业务员
					rentout_cut = Math.round(rentout_rentmonth/30 * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
					rentout_cut_total += rentout_cut;
					resultMap.put("rentout_cut", rentout_cut);
					rentoutRentMonths.add(resultMap);
				}
				
				
			}
		totalMap.put("rentin_cut_total", rentin_cut_total);
		totalMap.put("rentout_cut_total", rentout_cut_total);
		totalMap.put("teamleader_cut_total", teamleader_cut_total);
		totalMap.put("departleader_cut_total", departleader_cut_total);
		totalMap.put("manager_cut_total", manager_cut_total);
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("rentinRentMonths", rentinRentMonths);
		result.put("rentoutRentMonths", rentoutRentMonths);
		result.put("totalMap", totalMap);
			
		return result;
	}
	/**
	 * 业绩提成统计
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> businessSaleCut(Map<String, Object> paramMap) throws Exception{

		List<RentMonth> list = getBusinesscutBaseList(paramMap); 
		
		/*******************以下开始生成提成列表数据***********************/
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		Map<String,Object> resultMap = null;
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
		int tempcut = 0;
		List<Cutconfig> cut_businesssaletypeconfigs = null;
		RentMonth sameMonthRentout = null;//同期的租进月记录
		for(RentMonth rentinmonth : list){
			
			Map<String,Object> paramMap1 = new HashMap<String,Object>();
			paramMap1.put("infotype", "rentout");
			paramMap1.put("rent", rentinmonth.getRent());
			paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
			paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
			List<RentMonth> tempList = rentMonthService.find(paramMap1);
			if(null != tempList && tempList.size() > 0){
				sameMonthRentout = tempList.get(0);
			}else{
				sameMonthRentout = null;
				continue;
			}
			
			rentin_cut = 0;
			rentout_cut = 0;
			teamleader_cut = 0;
			departleader_cut = 0;
			manager_cut = 0;
			
			resultMap = new HashMap<String,Object>();
			resultMap.put("rentinmonth",rentinmonth);
			resultMap.put("rentoutmonth",sameMonthRentout);
			cut_businesssaletypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentinmonth.getCut_businesssaletype());
			if(null != rentinmonth.getPerson()){
				if(User.Busi_type.oldbusier.toString().equals(rentinmonth.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
					rentin_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler_old, CutConfigTypeConstant.cut_businesssales));
				}else{
					rentin_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_businesssales));
				}
			}
			if(null != sameMonthRentout.getPerson()){
				if(User.Busi_type.oldbusier.toString().equals(sameMonthRentout.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
					rentout_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler_old, CutConfigTypeConstant.cut_businesssales));
				}else{
					rentout_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_businesssales));
				}
			}
			if(null != rentinmonth.getBusi_teamleader()){
				teamleader_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_businesssales));				
			}
			if(null != rentinmonth.getBusi_departleader()){
				departleader_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_businesssales));				
			}
			if(null != rentinmonth.getBusi_manager()){
				manager_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_businesssales));	
			}

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
			resultList.add(resultMap);
		}
		
		totalMap.put("rentin_cut_total", rentin_cut_total);
		totalMap.put("rentout_cut_total", rentout_cut_total);
		totalMap.put("teamleader_cut_total", teamleader_cut_total);
		totalMap.put("departleader_cut_total", departleader_cut_total);
		totalMap.put("manager_cut_total", manager_cut_total);
		Map<String,Object> result = new HashMap<String,Object>();

		result.put("resultList", resultList);
		result.put("total", totalMap);
		
		return result;

	}

	/**
	 * 业绩提成个人统计
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> businessSaleCut4PersonList(Map<String, Object> paramMap) throws Exception{

		List<RentMonth> list = getBusinesscutBaseList(paramMap); 
		
		/*******************以下开始生成提成列表数据***********************/
		Map<String,LinkedHashSet<User>> map = getLevelUsersWithRentinMonths(list,paramMap);
		LinkedHashSet<User> managers = map.get("managers");
		LinkedHashSet<User> departleaders = map.get("departleaders");
		LinkedHashSet<User> teamleaders = map.get("teamleaders");
		LinkedHashSet<User> salers = map.get("salers");//普通业务员

		List<User> showUserList = new ArrayList<User>();
		showUserList.addAll(managers);
		showUserList.addAll(departleaders);
		showUserList.addAll(teamleaders);
		showUserList.addAll(salers);
		
		List<Map<String,Object>> resultlist = new ArrayList<Map<String,Object>>();
		
		Map<String,Integer> rentinCutMap = new HashMap<String,Integer>();
		Map<String,Integer> rentoutCutMap = new HashMap<String,Integer>();
		Map<String,Integer> managerCutMap = new HashMap<String,Integer>();
		Map<String,Integer> departleaderCutMap = new HashMap<String,Integer>();
		Map<String,Integer> teamleaderCutMap = new HashMap<String,Integer>();
		Map<String,Integer> totalMap = new HashMap<String,Integer>();
		
		int tempcut = 0;
		int tempdouble = 0;

		List<Cutconfig> cut_businesssaletypeconfigs = null;
		RentMonth sameMonthRentout = null;//同期的租进月记录
		String username_temp = "";
		

		for(RentMonth rentinmonth : list){
			Map<String,Object> paramMap1 = new HashMap<String,Object>();
			paramMap1.put("infotype", "rentout");
			paramMap1.put("rent", rentinmonth.getRent());
			paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
			paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
			List<RentMonth> tempList = rentMonthService.find(paramMap1);
			if(null != tempList && tempList.size() > 0){
				sameMonthRentout = tempList.get(0);
			}else{
				sameMonthRentout = null;
				continue;
			}
			cut_businesssaletypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentinmonth.getCut_businesssaletype());
			if(null != rentinmonth.getBusi_manager()){
				username_temp = rentinmonth.getBusi_manager().getLoginName();
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_businesssales);
				managerCutMap.put(username_temp, MathUtils.deNull(managerCutMap.get(username_temp))+tempdouble);
			}
			if(null != rentinmonth.getBusi_departleader()){
				username_temp = rentinmonth.getBusi_departleader().getLoginName();
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_businesssales);
				departleaderCutMap.put(username_temp, MathUtils.deNull(departleaderCutMap.get(username_temp))+tempdouble);
			}
			if(null != rentinmonth.getBusi_teamleader()){
				username_temp = rentinmonth.getBusi_teamleader().getLoginName();
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_businesssales);
				teamleaderCutMap.put(username_temp, MathUtils.deNull(teamleaderCutMap.get(username_temp))+tempdouble);
			}
			if(null != rentinmonth.getPerson()){
				username_temp = rentinmonth.getPerson().getLoginName();
				if(User.Busi_type.oldbusier.toString().equals(rentinmonth.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler_old, CutConfigTypeConstant.cut_businesssales);
				}else{
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_businesssales);
				}
				tempdouble = (tempcut*12-MathUtils.deNull(rentinmonth.getAgencyfee()))/12;
				rentinCutMap.put(username_temp, MathUtils.deNull(rentinCutMap.get(username_temp))+tempdouble);
			}
			if(null != sameMonthRentout.getPerson()){
				username_temp = sameMonthRentout.getPerson().getLoginName();
				if(User.Busi_type.oldbusier.toString().equals(sameMonthRentout.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler_old, CutConfigTypeConstant.cut_businesssales);
				}else{
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_businesssales);
				}
				tempdouble = (tempcut*12-MathUtils.deNull(sameMonthRentout.getAgencyfee()))/12;
				rentoutCutMap.put(username_temp, MathUtils.deNull(rentoutCutMap.get(username_temp))+tempdouble);
			}
		}
		
		/***********************以下是封装最后合计列表数据************************/
		int rentin_total = 0;
		int rentout_total = 0;
		int manager_total = 0;
		int departleader_total = 0;
		int teamleader_total = 0;
		int cut_total = 0;
		
		int person_rentin_total = 0;
		int person_rentout_total = 0;
		int person_manager_total = 0;
		int person_departleader_total = 0;
		int person_teamleader_total = 0;
		int person_cut_total = 0;
		Map<String,Object> resultMap = new HashMap<String,Object>();
		for(User user : showUserList){
			resultMap = new HashMap<String,Object>();
			username_temp = user.getLoginName();
			person_rentin_total = MathUtils.deNull(rentinCutMap.get(username_temp));
			person_rentout_total = MathUtils.deNull(rentoutCutMap.get(username_temp));
			person_manager_total = MathUtils.deNull(managerCutMap.get(username_temp));
			person_departleader_total = MathUtils.deNull(departleaderCutMap.get(username_temp));
			person_teamleader_total = MathUtils.deNull(teamleaderCutMap.get(username_temp));
			person_cut_total = person_rentin_total+person_rentout_total+person_manager_total+person_departleader_total+person_teamleader_total;
			resultMap.put("person", user);
			resultMap.put("rentinCutTotal", person_rentin_total);
			resultMap.put("rentoutCutTotal", person_rentout_total);
			resultMap.put("rentmanagerCutTotal", person_manager_total);
			resultMap.put("rentdepartleaderCutTotal", person_departleader_total);
			resultMap.put("rentteamleaderCutTotal", person_teamleader_total);
			resultMap.put("cutTotal", person_cut_total);
			resultlist.add(resultMap);
			rentin_total += person_rentin_total;
			rentout_total += person_rentout_total;
			manager_total += person_manager_total;
			departleader_total += person_departleader_total;
			teamleader_total += person_teamleader_total;
			cut_total += person_cut_total;
		}
		
		totalMap.put("rentin_cut_total", rentin_total);
		totalMap.put("rentout_cut_total", rentout_total);
		totalMap.put("manager_cut_total", manager_total);
		totalMap.put("departleader_cut_total", departleader_total);
		totalMap.put("teamleader_cut_total", teamleader_total);
		totalMap.put("cut_total", cut_total);

		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list", resultlist);
		result.put("total", totalMap);
		
		return result;

	}
	
	/**
	 * 业绩提成个人统计
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> businessSaleCutDetail4PersonList(Map<String, Object> paramMap) throws Exception{
		String personid = (String)paramMap.get("personid");
		User person = UserUtils.getUserById(personid);
		
		List<RentMonth> list = getBusinesscutBaseList(paramMap); 
		List<Map<String, Object>> rentinRentMonths = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rentoutRentMonths = new ArrayList<Map<String, Object>>();
		
		/*******************以下开始生成提成列表数据***********************/


		Map<String,Integer> totalMap = new HashMap<String,Integer>();
		
		int tempcut = 0;
		int tempdouble = 0;

		List<Cutconfig> cut_businesssaletypeconfigs = null;
		RentMonth sameMonthRentout = null;//同期的租进月记录
		int rentin_total = 0;
		int rentout_total = 0;
		int manager_total = 0;
		int departleader_total = 0;
		int teamleader_total = 0;
		Map<String,Object> resultMap = new HashMap<String,Object>();
		

		for(RentMonth rentinmonth : list){
			Map<String,Object> paramMap1 = new HashMap<String,Object>();
			resultMap = new HashMap<String,Object>();
			paramMap1.put("infotype", "rentout");
			paramMap1.put("rent", rentinmonth.getRent());
			paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
			paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
			List<RentMonth> tempList = rentMonthService.find(paramMap1);
			if(null != tempList && tempList.size() > 0){
				sameMonthRentout = tempList.get(0);
			}else{
				sameMonthRentout = null;
				continue;
			}
			
			resultMap.put("rentinmonth", rentinmonth);
			resultMap.put("rentoutmonth", sameMonthRentout);

			cut_businesssaletypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentinmonth.getCut_businesssaletype());
			if(person.equals(rentinmonth.getBusi_manager())){
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_businesssales);
				resultMap.put("manager_cut",tempdouble);
				manager_total += tempdouble;
				rentinRentMonths.add(resultMap);
			}
			if(person.equals(rentinmonth.getBusi_departleader())){
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_businesssales);
				resultMap.put("departleader_cut",tempdouble);
				departleader_total += tempdouble;
				rentinRentMonths.add(resultMap);
			}
			if(person.equals(rentinmonth.getBusi_teamleader())){
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_businesssales);
				resultMap.put("teamleader_cut",tempdouble);
				teamleader_total += tempdouble;
				rentinRentMonths.add(resultMap);
			}
			if(person.equals(rentinmonth.getPerson())){
				if(User.Busi_type.oldbusier.toString().equals(rentinmonth.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler_old, CutConfigTypeConstant.cut_businesssales);
				}else{
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_businesssales);
				}
				tempdouble = (tempcut*12-MathUtils.deNull(rentinmonth.getAgencyfee()))/12;
				resultMap.put("rentin_cut",tempdouble);
				rentin_total += tempdouble;
				rentinRentMonths.add(resultMap);
			}
			if(person.equals(sameMonthRentout.getPerson())){
				if(User.Busi_type.oldbusier.toString().equals(sameMonthRentout.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler_old, CutConfigTypeConstant.cut_businesssales);
				}else{
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_businesssales);
				}
				tempdouble = (tempcut*12-MathUtils.deNull(sameMonthRentout.getAgencyfee()))/12;
				resultMap.put("rentout_cut",tempdouble);
				rentout_total += tempdouble;
				rentoutRentMonths.add(resultMap);
			}
		}
		
		/***********************以下是封装最后合计列表数据************************/
		
		totalMap.put("rentin_cut_total", rentin_total);
		totalMap.put("rentout_cut_total", rentout_total);
		totalMap.put("teamleader_cut_total", teamleader_total);
		totalMap.put("departleader_cut_total", departleader_total);
		totalMap.put("manager_cut_total", manager_total);

		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("rentinRentMonths", rentinRentMonths);
		result.put("rentoutRentMonths", rentoutRentMonths);
		result.put("totalMap", totalMap);
		
		return result;

	}

	
	/**
	 * 获取空置期提成基础列表
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> getVacantperiodBaseList(Map<String, Object> paramMap){
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();
		dc.createAlias("rent", "rent");
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		if (rentout_sdate_begin == null){
			rentout_sdate_begin = DateUtils.parseDate(Global.getConfig("sys.default_sdate"));
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.parseDate(Global.getConfig("sys.default_edate"));
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentout_sdate_end, "yyyy-MM-dd"));
		}
		dc.add(Restrictions.neOrIsNotNull("firstmonth_num", ""));
		dc.add(Restrictions.eq("infotype", "rentout"));
		dc.add(Restrictions.and(Restrictions.gt("lastpaysdate", rentout_sdate_begin),Restrictions.lt("lastpaysdate", rentout_sdate_end)));

		String name = (String)paramMap.get("name");
		if(!StringUtils.isBlank(name)){
			dc.add(Restrictions.like("rent.name", "%"+name+"%"));
		}
		dc.addOrder(Order.asc("rent.business_num"));
		return rentMonthDao.find(dc); 
	}
	
	/**
	 * 获取业绩提成基础列表
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> getBusinesscutBaseList(Map<String, Object> paramMap){
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();
		dc.createAlias("rent", "rent");
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		if (rentout_sdate_begin == null){
			rentout_sdate_begin = DateUtils.parseDate(Global.getConfig("sys.default_sdate"));
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.parseDate(Global.getConfig("sys.default_edate"));
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentout_sdate_end, "yyyy-MM-dd"));
		}
		dc.add(Restrictions.eq("infotype", "rentin"));
		dc.add(Restrictions.and(Restrictions.gt("lastpaysdate", rentout_sdate_begin),Restrictions.lt("lastpaysdate", rentout_sdate_end)));

		String name = (String)paramMap.get("name");
		if(!StringUtils.isBlank(name)){
			dc.add(Restrictions.like("rent.name", "%"+name+"%"));
		}
		dc.addOrder(Order.asc("rent.business_num"));
		return rentMonthDao.find(dc); 
	}
	/**
	 * 获取各级别的人员list
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String,LinkedHashSet<User>> getLevelUsersWithRentMonths(List<RentMonth> list,Map<String,Object> paramMap) throws Exception{
		LinkedHashSet<User> managers = new LinkedHashSet<User>();
		LinkedHashSet<User> departleaders = new LinkedHashSet<User>();
		LinkedHashSet<User> teamleaders = new LinkedHashSet<User>();
		LinkedHashSet<User> salers = new LinkedHashSet<User>();//普通业务员
		
		RentMonth sameMonthRentin = null;//同期的租进月记录
		for(RentMonth rentmonth : list){
			
			Map<String,Object> paramMap1 = new HashMap<String,Object>();
			paramMap1.put("infotype", "rentin");
			paramMap1.put("rent", rentmonth.getRent());
			paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
			paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
			List<RentMonth> tempList = rentMonthService.find(paramMap1);
			if(null != tempList && tempList.size() > 0){
				sameMonthRentin = tempList.get(0);
			}

			if(null != sameMonthRentin.getBusi_manager()){
				managers.add(sameMonthRentin.getBusi_manager());
			}
			if(null != sameMonthRentin.getBusi_departleader()){
				departleaders.add(sameMonthRentin.getBusi_departleader());
			}
			if(null != sameMonthRentin.getBusi_teamleader()){
				teamleaders.add(sameMonthRentin.getBusi_teamleader());
			}
			
			if(null != rentmonth.getPerson() && !managers.contains(rentmonth.getPerson()) && !departleaders.contains(rentmonth.getPerson()) && !teamleaders.contains(rentmonth.getPerson())){
				salers.add(rentmonth.getPerson());
			}
			if(null != sameMonthRentin.getPerson() && !managers.contains(sameMonthRentin.getPerson()) && !departleaders.contains(sameMonthRentin.getPerson()) && !teamleaders.contains(sameMonthRentin.getPerson())){
				salers.add(sameMonthRentin.getPerson());
			}
		}
		
		Map<String,LinkedHashSet<User>> resultMap = new HashMap<String,LinkedHashSet<User>>();
		resultMap.put("managers", managers);
		resultMap.put("departleaders", departleaders);
		resultMap.put("teamleaders", teamleaders);
		resultMap.put("salers", salers);
		return resultMap;
	}

	
	/**
	 * 获取各级别的人员list
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public Map<String,LinkedHashSet<User>> getLevelUsersWithRentinMonths(List<RentMonth> list,Map<String,Object> paramMap) throws Exception{
		LinkedHashSet<User> managers = new LinkedHashSet<User>();
		LinkedHashSet<User> departleaders = new LinkedHashSet<User>();
		LinkedHashSet<User> teamleaders = new LinkedHashSet<User>();
		LinkedHashSet<User> salers = new LinkedHashSet<User>();//普通业务员
		
		RentMonth sameMonthRentout = null;//同期的租进月记录
		for(RentMonth rentmonth : list){
			Map<String,Object> paramMap1 = new HashMap<String,Object>();
			paramMap1.put("infotype", "rentout");
			paramMap1.put("rent", rentmonth.getRent());
			paramMap1.put("sdate_begin", paramMap.get("rentout_sdate_begin"));
			paramMap1.put("sdate_end", paramMap.get("rentout_sdate_end"));
			List<RentMonth> tempList = rentMonthService.find(paramMap1);
			if(null != tempList && tempList.size() > 0){
				sameMonthRentout = tempList.get(0);
			}

			if(null != rentmonth ){
				if(null != rentmonth.getBusi_manager()){
					managers.add(rentmonth.getBusi_manager());
				}
				if(null != rentmonth.getBusi_departleader()){
					departleaders.add(rentmonth.getBusi_departleader());
				}
				if(null != rentmonth.getBusi_teamleader()){
					teamleaders.add(rentmonth.getBusi_teamleader());
				}		
				if(null != rentmonth.getPerson() && !managers.contains(rentmonth.getPerson()) && !departleaders.contains(rentmonth.getPerson()) && !teamleaders.contains(rentmonth.getPerson())){
					salers.add(rentmonth.getPerson());
				}
			}
			if(null != sameMonthRentout && null != sameMonthRentout.getPerson() && !managers.contains(sameMonthRentout.getPerson()) && !departleaders.contains(sameMonthRentout.getPerson()) && !teamleaders.contains(sameMonthRentout.getPerson())){
				salers.add(sameMonthRentout.getPerson());
			}
		}
		
		Map<String,LinkedHashSet<User>> resultMap = new HashMap<String,LinkedHashSet<User>>();
		resultMap.put("managers", managers);
		resultMap.put("departleaders", departleaders);
		resultMap.put("teamleaders", teamleaders);
		resultMap.put("salers", salers);
		return resultMap;
	}

	public static void main(String[] args){
		double i = 1456.88888;
		long j = 30;
		System.out.println(Math.round(i/j));
		
	}
}
