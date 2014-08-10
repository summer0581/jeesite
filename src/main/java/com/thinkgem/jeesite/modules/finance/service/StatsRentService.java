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
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	/**
	 * 中介费 计算的最大月份 小于或等于
	 */
	private int AgencyfeeMonthMax = 12;
	/**
	 * 每个月的天数
	 */
	private int DaysPerMonth = 30;

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
		double cutlevel = 1;//提成折扣
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录

		

		for(RentMonth rentmonth : list){
			Map<String,Object> resultMap = new HashMap<String,Object>();

			lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
			
			sameMonthRentin = getSameMonthRentinByRentoutMonth(rentmonth);
			if(null == sameMonthRentin && "1".equals(rentmonth.getFirstmonth_num())){//第一次出租头期必须要有对应的进租月记录
				continue;
			}
			
			vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
			Map<String,Object> result = rentMonthService.getRecentVacantPeriodByRentMonth(rentmonth, vacantPeriods);
			if(null == result){
				continue;
			}
			recentVacantPeriodSdate = (Date)result.get("recent_vacantPeriodSdate");
			recentVacantPeriodEdate = (Date)result.get("recent_vacantPeriodEdate");
			recentVacantType = (String)result.get("recent_vacantType");
			
			long vacantperiod = getVacantPeriodCount(rentmonth,sameMonthRentin,lastRentoutMonth,(int)DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));
			if(vacantperiod<0){
				continue;
			}
			cutlevel = getVacantPeriodCutLevel(rentmonth, recentVacantType, (int)vacantperiod);
			
			long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));

			cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentmonth.getCut_vacantperiodtype());
			rentin_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
			rentout_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
			teamleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
			departleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
			manager_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
			
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
		double cutlevel = 1;//提成折扣
		String username_temp= "";//用户名占位符
		Map<String,Integer> rentoutPeriod = new HashMap<String,Integer>();//承租的所有人的提成总计
		Map<String,Integer> rentinPeriod = new HashMap<String,Integer>();//出租的所有人的提成总计
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录
		
			for(RentMonth rentmonth : list){
				
				lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
				
				sameMonthRentin = getSameMonthRentinByRentoutMonth(rentmonth);
				if(null == sameMonthRentin && "1".equals(rentmonth.getFirstmonth_num())){//第一次出租头期必须要有对应的进租月记录
					continue;
				}
				
				vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
				Map<String,Object> result = rentMonthService.getRecentVacantPeriodByRentMonth(rentmonth, vacantPeriods);
				if(null == result){
					continue;
				}
				recentVacantPeriodSdate = (Date)result.get("recent_vacantPeriodSdate");
				recentVacantPeriodEdate = (Date)result.get("recent_vacantPeriodEdate");
				recentVacantType = (String)result.get("recent_vacantType");
				
				long vacantperiod = getVacantPeriodCount(rentmonth,sameMonthRentin,lastRentoutMonth,(int)DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));
				if(vacantperiod<0){
					continue;
				}
				cutlevel = getVacantPeriodCutLevel(rentmonth, recentVacantType, (int)vacantperiod);
				
				long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));
				cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentmonth.getCut_vacantperiodtype());
				rentin_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				rentout_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				teamleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				departleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
				manager_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
				
				/**************************以下是空置期合计的计算**********************/
				if(null != sameMonthRentin){//如果没设置进去记录，则无法找到对应的经理，部长，组长，租进业务员
					if(null != sameMonthRentin.getPerson()){
						username_temp = sameMonthRentin.getPerson().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)rentin_cut);//通过用户名(承租业务员)，去累加他的承租空置期提成
					}
					if(null != sameMonthRentin.getBusi_departleader()){//如果有部长
						username_temp = sameMonthRentin.getBusi_departleader().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)departleader_cut);//通过用户名(部长)，去累加他的承租空置期提成
					}
					if(null != sameMonthRentin.getBusi_manager()){
						username_temp = sameMonthRentin.getBusi_manager().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)manager_cut);//通过用户名(经理)，去累加他的承租空置期提成
					}
					if(null != sameMonthRentin.getBusi_teamleader()){
						username_temp = sameMonthRentin.getBusi_teamleader().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)teamleader_cut);//通过用户名(组长)，去累加他的承租空置期提成
					}

				}
				if(null != rentmonth.getPerson()){
					username_temp = rentmonth.getPerson().getLoginName();
					rentoutPeriod.put(username_temp, MathUtils.deNull(rentoutPeriod.get(username_temp))+(int)rentout_cut);//通过用户名(出租业务员)，去累加他的出租租空置期提成
				}
				
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
		double cutlevel = 1;//提成折扣
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录


		
			for(RentMonth rentmonth : list){
				lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
				
				Map<String,Object> resultMap = new HashMap<String,Object>();
				
				sameMonthRentin = getSameMonthRentinByRentoutMonth(rentmonth);
				if(null == sameMonthRentin && "1".equals(rentmonth.getFirstmonth_num())){//第一次出租头期必须要有对应的进租月记录
					continue;
				}
				
				vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
				Map<String,Object> result = rentMonthService.getRecentVacantPeriodByRentMonth(rentmonth, vacantPeriods);
				if(null == result){
					continue;
				}
				recentVacantPeriodSdate = (Date)result.get("recent_vacantPeriodSdate");
				recentVacantPeriodEdate = (Date)result.get("recent_vacantPeriodEdate");
				recentVacantType = (String)result.get("recent_vacantType");
				
				long vacantperiod = getVacantPeriodCount(rentmonth,sameMonthRentin,lastRentoutMonth,(int)DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));
				if(vacantperiod<0){
					continue;
				}
				cutlevel = getVacantPeriodCutLevel(rentmonth, recentVacantType, (int)vacantperiod);
				
				long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));

				resultMap.put("rentinmonth", sameMonthRentin);
				resultMap.put("rentmonth", rentmonth);
				resultMap.put("vacantperiodconfig", DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));//空置期天数
				resultMap.put("vacantperiod", vacantperiod);//空置期天数
				resultMap.put("vacantperiod_type", recentVacantType);//空置期提成类型


				cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentmonth.getCut_vacantperiodtype());
				if(null != sameMonthRentin){//如果没设置进去记录，则无法找到对应的经理，部长，组长，租进业务员
					if(person.equals(sameMonthRentin.getBusi_manager())){//判断此人是不是经理
						manager_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
						manager_cut_total += manager_cut;
						resultMap.put("manager_cut", manager_cut);
						rentinRentMonths.add(resultMap);
					}
					if(person.equals(sameMonthRentin.getBusi_departleader())){//判断此人是不是部长
						departleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
						departleader_cut_total += departleader_cut;
						resultMap.put("departleader_cut", departleader_cut);
						rentinRentMonths.add(resultMap);
					}
					if(person.equals(sameMonthRentin.getBusi_teamleader())){//判断此人是不是组长
						teamleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
						teamleader_cut_total += teamleader_cut;
						resultMap.put("teamleader_cut", teamleader_cut);
						rentinRentMonths.add(resultMap);
					}
					if(person.equals(sameMonthRentin.getPerson())){//判断是否为租进业务员
						rentin_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
						rentin_cut_total += rentin_cut;
						resultMap.put("rentin_cut", rentin_cut);
						rentinRentMonths.add(resultMap);
					}

				}
				
				
				if(person.equals(rentmonth.getPerson())){//判断是否为租出业务员
					rentout_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
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
	 * 按人以及月份统计空置期的总计
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> vacantPeriod4PersonByMonth(Map<String, Object> paramMap) throws Exception{

		List<RentMonth> list = getVacantperiodBaseAllList(paramMap); 
		String themonth = (String)paramMap.get("rentout_sdate_begin");
		Date themonthdate = DateUtils.parseDate(themonth);
		int totalpaymonth = 6;//付完空置期的总月数
		int permonth = 6;//每隔多少月付空置期提成
		
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
		double cutlevel = 1;//提成折扣
		String username_temp= "";//用户名占位符
		Map<String,Integer> rentoutPeriod = new HashMap<String,Integer>();//承租的所有人的提成总计
		Map<String,Integer> rentinPeriod = new HashMap<String,Integer>();//出租的所有人的提成总计
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录
		
			for(RentMonth rentmonth : list){
				//判断，房子出租的起始日期距离选择的起始日期是否为 设置的 支付月总数，因空置期提成支付改成了分6个月支付
				long xmonth = DateUtils.compareDates(themonthdate,rentmonth.getLastpaysdate(), Calendar.MONTH);


				if(xmonth < 0 || xmonth >= totalpaymonth){
					continue;
				}
				 
				lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
				
				sameMonthRentin = getSameMonthRentinByRentoutMonth(rentmonth);
				if(null == sameMonthRentin && "1".equals(rentmonth.getFirstmonth_num())){//第一次出租头期必须要有对应的进租月记录
					continue;
				}
				
				vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
				Map<String,Object> result = rentMonthService.getRecentVacantPeriodByRentMonth(rentmonth, vacantPeriods);
				if(null == result){
					continue;
				}
				recentVacantPeriodSdate = (Date)result.get("recent_vacantPeriodSdate");
				recentVacantPeriodEdate = (Date)result.get("recent_vacantPeriodEdate");
				recentVacantType = (String)result.get("recent_vacantType");
				

				long vacantperiod = getVacantPeriodCount(rentmonth,sameMonthRentin,lastRentoutMonth,(int)DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));
				if(vacantperiod<0){
					continue;
				}
				cutlevel = getVacantPeriodCutLevel(rentmonth, recentVacantType, (int)vacantperiod);
				
				long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));
				cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentmonth.getCut_vacantperiodtype());
				rentin_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				rentout_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				teamleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
				departleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
				manager_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
				
				/**************************以下是空置期合计的计算**********************/
				double perlevel = 1.0/(double)totalpaymonth;
				double rentinSalePerlevel = perlevel;//租进业务员分期付款百分比
				double rentoutSalePerlevel = perlevel;//租出业务员分期付款百分比

				if(null != sameMonthRentin){//如果没设置进去记录，则无法找到对应的经理，部长，组长，租进业务员
					if(null != sameMonthRentin.getPerson()){
						long hasEntriedMonth = -1;
						//判断租出业务员的入职时间是否大于等于2014年8月1日
						if(null!= rentmonth.getPerson() && null != rentmonth.getPerson().getEntryDate() && DateUtils.compareDates(DateUtils.parseDate("2014-08-01"),rentmonth.getPerson().getEntryDate(), Calendar.MONTH) >= 0){
							hasEntriedMonth = DateUtils.compareDates(rentmonth.getPerson().getEntryDate(),rentmonth.getLastpaysdate(), Calendar.MONTH)+1;
							//判断房子租进时间，是否在新员工入职后的六个月内，如果是六个月内，则1月租出的只能在距离租出一个月内查到，2月租出的只能在距离租出二个月内查到，以此类推
							if(-1 != hasEntriedMonth && hasEntriedMonth < 6 && xmonth <= hasEntriedMonth){
								rentinSalePerlevel = 1.0/hasEntriedMonth;
							}else{
								rentinSalePerlevel = 0.0;
							}
						}
						username_temp = sameMonthRentin.getPerson().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)(Math.round(rentin_cut*rentinSalePerlevel)));//通过用户名(承租业务员)，去累加他的承租空置期提成
					}
					if(null != sameMonthRentin.getBusi_departleader()){//如果有部长
						username_temp = sameMonthRentin.getBusi_departleader().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)(Math.round(departleader_cut*perlevel)));//通过用户名(部长)，去累加他的承租空置期提成
					}
					if(null != sameMonthRentin.getBusi_manager()){
						username_temp = sameMonthRentin.getBusi_manager().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)(Math.round(manager_cut*perlevel)));//通过用户名(经理)，去累加他的承租空置期提成
					}
					if(null != sameMonthRentin.getBusi_teamleader()){
						username_temp = sameMonthRentin.getBusi_teamleader().getLoginName();
						rentinPeriod.put(username_temp, MathUtils.deNull(rentinPeriod.get(username_temp))+(int)(Math.round(teamleader_cut*perlevel)));//通过用户名(组长)，去累加他的承租空置期提成
					}

				}
				if(null != rentmonth.getPerson()){
					username_temp = rentmonth.getPerson().getLoginName();
					long hasEntriedMonth = -1;
					//判断租出业务员的入职时间是否大于等于2014年8月1日
					if(null!= rentmonth.getPerson() && null != rentmonth.getPerson().getEntryDate() && DateUtils.compareDates(DateUtils.parseDate("2014-08-01"),rentmonth.getPerson().getEntryDate(), Calendar.MONTH) >= 0){
						hasEntriedMonth = DateUtils.compareDates(rentmonth.getPerson().getEntryDate(),rentmonth.getLastpaysdate(), Calendar.MONTH)+1;
						//判断房子租进时间，是否在新员工入职后的六个月内，如果是六个月内，则1月租出的只能在距离租出一个月内查到，2月租出的只能在距离租出二个月内查到，以此类推
						if(-1 != hasEntriedMonth && hasEntriedMonth < 6 && xmonth <= hasEntriedMonth){
							rentoutSalePerlevel = 1.0/hasEntriedMonth;
						}else{
							rentoutSalePerlevel = 0.0;
						}
					}
					rentoutPeriod.put(username_temp, MathUtils.deNull(rentoutPeriod.get(username_temp))+(int)(Math.round(rentout_cut*rentoutSalePerlevel)));//通过用户名(出租业务员)，去累加他的出租租空置期提成
				}
				
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

	public Map<String,Object> vacantPeriodDetail4PersonByMonth(Map<String, Object> paramMap) throws Exception{
		String personid = (String)paramMap.get("personid");
		User person = UserUtils.getUserById(personid);
		//paramMap.put("name", "凯轩云顶809");

		List<RentMonth> list = getVacantperiodBaseAllList(paramMap); 
		String themonth = (String)paramMap.get("rentout_sdate_begin");
		Date themonthdate = DateUtils.parseDate(themonth);
		int totalpaymonth = 6;//付完空置期的总月数
		int permonth = 6;//每隔多少月付空置期提成
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
		double cutlevel = 1;//提成折扣
		List<Cutconfig> cut_vacantperiodtypeconfigs = null;
		RentMonth lastRentoutMonth = null;//上一次的出租月记录
		RentMonth sameMonthRentin = null;//同期的租进月记录
		
			for(RentMonth rentmonth : list){
				//判断，房子出租的起始日期距离选择的起始日期是否为 设置的 支付月总数，因空置期提成支付改成了分6个月支付
				long xmonth = DateUtils.compareDates(themonthdate,rentmonth.getLastpaysdate(), Calendar.MONTH);
				long hasEntriedMonth = -1;
				//判断租出业务员的入职时间是否大于等于2014年8月1日
				if(null!= rentmonth.getPerson() && null != person.getEntryDate() && DateUtils.compareDates(DateUtils.parseDate("2014-08-01"),person.getEntryDate(), Calendar.MONTH) >= 0){
					hasEntriedMonth = DateUtils.compareDates(person.getEntryDate(),rentmonth.getLastpaysdate(), Calendar.MONTH)+1;
					//判断房子租进时间，是否在新员工入职后的六个月内，如果是六个月内，则1月租出的只能在距离租出一个月内查到，2月租出的只能在距离租出二个月内查到，以此类推
					if(hasEntriedMonth < 6 && xmonth > hasEntriedMonth){
						continue;
					}
				}
				if(xmonth < 0 || xmonth >= totalpaymonth){
					continue;
				}
				lastRentoutMonth = rentMonthService.findLastRentMonth(rentmonth);
				
				Map<String,Object> resultMap = new HashMap<String,Object>();
				
				sameMonthRentin = getSameMonthRentinByRentoutMonth(rentmonth);
				if(null == sameMonthRentin && "1".equals(rentmonth.getFirstmonth_num())){//第一次出租头期必须要有对应的进租月记录
					continue;
				}
				
				vacantPeriods = rentmonth.getRent().getSalesman_vacantperiods();
				Map<String,Object> result = rentMonthService.getRecentVacantPeriodByRentMonth(rentmonth, vacantPeriods);
				if(null == result){
					continue;
				}
				recentVacantPeriodSdate = (Date)result.get("recent_vacantPeriodSdate");
				recentVacantPeriodEdate = (Date)result.get("recent_vacantPeriodEdate");
				recentVacantType = (String)result.get("recent_vacantType");
				
				long vacantperiod = getVacantPeriodCount(rentmonth,sameMonthRentin,lastRentoutMonth,(int)DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));
				if(vacantperiod<0){
					continue;
				}
				cutlevel = getVacantPeriodCutLevel(rentmonth, recentVacantType, (int)vacantperiod);
				
				long rentout_rentmonth = Long.parseLong(StringUtils.defaultIfEmpty((String)rentmonth.getRentmonth(), "0"));

				resultMap.put("rentinmonth", sameMonthRentin);
				resultMap.put("rentmonth", rentmonth);
				resultMap.put("vacantperiodconfig", DateUtils.compareDates(recentVacantPeriodEdate, recentVacantPeriodSdate, Calendar.DATE));//空置期天数
				resultMap.put("vacantperiod", vacantperiod);//空置期天数
				resultMap.put("vacantperiod_type", recentVacantType);//空置期提成类型

				double perlevel = 1.0/(double)totalpaymonth;
				if(-1 != hasEntriedMonth && hasEntriedMonth < 6){//如果已入职月份已设值，则按已入职月份计算分月支付折扣。
					perlevel = 1.0/hasEntriedMonth;
				}
				cut_vacantperiodtypeconfigs = cutconfigService.findCutconfiglistByCutcode(rentmonth.getCut_vacantperiodtype());
				if(null != sameMonthRentin){//如果没设置进去记录，则无法找到对应的经理，部长，组长，租进业务员
					if(person.equals(sameMonthRentin.getBusi_manager())){//判断此人是不是经理
						manager_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
						manager_cut = Math.round(manager_cut * perlevel);
						manager_cut_total += manager_cut;
						resultMap.put("manager_cut", manager_cut);
						rentinRentMonths.add(resultMap);
					}
					if(person.equals(sameMonthRentin.getBusi_departleader())){//判断此人是不是部长
						departleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
						departleader_cut = Math.round(departleader_cut * perlevel);
						departleader_cut_total += departleader_cut;
						resultMap.put("departleader_cut", departleader_cut);
						rentinRentMonths.add(resultMap);
					}
					if(person.equals(sameMonthRentin.getBusi_teamleader())){//判断此人是不是组长
						teamleader_cut = Math.round(rentout_rentmonth/DaysPerMonth  * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
						teamleader_cut = Math.round(teamleader_cut * perlevel);
						teamleader_cut_total += teamleader_cut;
						resultMap.put("teamleader_cut", teamleader_cut);
						rentinRentMonths.add(resultMap);
					}
					if(person.equals(sameMonthRentin.getPerson())){//判断是否为租进业务员
						rentin_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel);
						rentin_cut = Math.round(rentin_cut * perlevel);
						rentin_cut_total += rentin_cut;
						resultMap.put("rentin_cut", rentin_cut);
						rentinRentMonths.add(resultMap);
					}

				}
				
				
				if(person.equals(rentmonth.getPerson())){//判断是否为租出业务员
					rentout_cut = Math.round(rentout_rentmonth/DaysPerMonth * cutconfigService.getCutpercentByPersonAndType(cut_vacantperiodtypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_vacantperiod) * vacantperiod * cutlevel );
					rentout_cut = Math.round(rentout_cut * perlevel);
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

		List<RentMonth> list = getBusinesscutBaseSqlList(paramMap); 
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
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
		int inmonthnumper = 0;
		int outmonthnumper = 0;
		List<Cutconfig> cut_businesssaletypeconfigs = null;
		RentMonth sameMonthRentout = null;//同期的租进月记录
		for(RentMonth rentinmonth : list){
			sameMonthRentout = getSameMonthRentoutByRentinMonthSql(rentinmonth);
			
			if(null == sameMonthRentout){//如果未找到同期的出租记录，则跳出循环
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
			cut_businesssaletypeconfigs = cutconfigService.findCutconfiglistByCutcode(sameMonthRentout.getCut_businesssaletype());
			
			inmonthnumper = (int)DateUtils.compareDates(rentinmonth.getEdate(), rentinmonth.getSdate(), Calendar.DATE)/DaysPerMonth;
			if(0 == inmonthnumper){//有极少可能没设置起始日期和结束日期
				continue;
			}
			if(inmonthnumper > AgencyfeeMonthMax){
				inmonthnumper = AgencyfeeMonthMax;
			}

			if(null != rentinmonth.getPerson() && StringUtils.isNotBlank(rentinmonth.getPerson().getName())){
				tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler_old, CutConfigTypeConstant.cut_businesssales);
				
				if(User.Busi_type.oldbusier.toString().equals(rentinmonth.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
					rentin_cut = Math.round(tempcut);
				}else{
					rentin_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_businesssales));
				}
				/*此处比较的起始时间于2014.7.11改为用lastpaysdate与rentout_sdate_begin进行比较，
				如果lastpaysdate大于rentout_sdate_begin，则用lastpaysdate，否则用rentout_sdate_begin
				*/
				Date agencyfeeBeginDate = rentout_sdate_begin;
				if(DateUtils.compareDates(rentinmonth.getLastpaysdate(), rentout_sdate_begin, Calendar.DATE) > 0){
					agencyfeeBeginDate = rentinmonth.getLastpaysdate();
				}
				if((int)DateUtils.compareDates(agencyfeeBeginDate, rentinmonth.getSdate(), Calendar.DATE)/DaysPerMonth < AgencyfeeMonthMax){//只有前12个月才算中介费
					rentin_cut = (rentin_cut*inmonthnumper-MathUtils.deNull(rentinmonth.getAgencyfee()))/inmonthnumper;
				}
				
			}
			if(null != sameMonthRentout ){
				outmonthnumper = (int)DateUtils.compareDates(sameMonthRentout.getEdate(), sameMonthRentout.getSdate(), Calendar.DATE)/DaysPerMonth;
				if(0 == outmonthnumper){//有极少可能没设置起始日期和结束日期
					continue;
				}
				if(outmonthnumper > AgencyfeeMonthMax){
					outmonthnumper = AgencyfeeMonthMax;
				}
				if(null != sameMonthRentout.getPerson() && StringUtils.isNotBlank(sameMonthRentout.getPerson().getName())){
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler_old, CutConfigTypeConstant.cut_businesssales);
					if(User.Busi_type.oldbusier.toString().equals(sameMonthRentout.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
						rentout_cut = Math.round(tempcut);
					}else{
						rentout_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_businesssales));
					}
					/*此处比较的起始时间于2014.7.11改为用lastpaysdate与rentout_sdate_begin进行比较，
					如果lastpaysdate大于rentout_sdate_begin，则用lastpaysdate，否则用rentout_sdate_begin
					*/
					Date agencyfeeBeginDate = rentout_sdate_begin;
					if(DateUtils.compareDates(rentinmonth.getLastpaysdate(), rentout_sdate_begin, Calendar.DATE) > 0){
						agencyfeeBeginDate = rentinmonth.getLastpaysdate();
					}
					if((int)DateUtils.compareDates(agencyfeeBeginDate, sameMonthRentout.getSdate(), Calendar.DATE)/DaysPerMonth < AgencyfeeMonthMax){//只有前12个月才算中介费
						rentout_cut = (rentout_cut*outmonthnumper-MathUtils.deNull(sameMonthRentout.getAgencyfee()))/outmonthnumper;
					}
					
				}

			}
			if(null != rentinmonth.getBusi_teamleader() && StringUtils.isNotBlank(rentinmonth.getBusi_teamleader().getName())){
				teamleader_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_businesssales));				
			}
			if(null != rentinmonth.getBusi_departleader() && StringUtils.isNotBlank(rentinmonth.getBusi_departleader().getName())){
				departleader_cut = Math.round(cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_businesssales));				
			}
			if(null != rentinmonth.getBusi_manager() && StringUtils.isNotBlank(rentinmonth.getBusi_manager().getName())){
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

		List<RentMonth> list = getBusinesscutBaseSqlList(paramMap); 
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		/*******************以下开始生成提成列表数据***********************/
		Map<String,LinkedHashSet<User>> map = getLevelUsersWithRentinMonths(list,paramMap);
		LinkedHashSet<User> managers = map.get("managers");
		LinkedHashSet<User> departleaders = map.get("departleaders");
		LinkedHashSet<User> teamleaders = map.get("teamleaders");
		LinkedHashSet<User> salers = map.get("salers");//普通业务员

		Set<User> showUserList = new LinkedHashSet<User>();
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
		int inmonthnumper = 0;
		int outmonthnumper = 0;
		
		List<Cutconfig> cut_businesssaletypeconfigs = null;
		RentMonth sameMonthRentout = null;//同期的租进月记录
		String username_temp = "";
		

		for(RentMonth rentinmonth : list){
			sameMonthRentout = getSameMonthRentoutByRentinMonthSql(rentinmonth);
			if(null == sameMonthRentout){//如果未找到同期的出租记录，则跳出循环
				continue;
			}
			cut_businesssaletypeconfigs = cutconfigService.findCutconfiglistByCutcode(sameMonthRentout.getCut_businesssaletype());
			inmonthnumper = (int)DateUtils.compareDates(rentinmonth.getEdate(), rentinmonth.getSdate(), Calendar.DATE)/DaysPerMonth;
			if(0 == inmonthnumper){//有极少可能没设置起始日期和结束日期
				continue;
			}
			if(inmonthnumper > AgencyfeeMonthMax){
				inmonthnumper = AgencyfeeMonthMax;
			}
			if(null != rentinmonth.getBusi_manager() && StringUtils.isNotBlank(rentinmonth.getBusi_manager().getName())){
				username_temp = rentinmonth.getBusi_manager().getLoginName();
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_businesssales);
				managerCutMap.put(username_temp, MathUtils.deNull(managerCutMap.get(username_temp))+tempdouble);
			}
			if(null != rentinmonth.getBusi_departleader() && StringUtils.isNotBlank(rentinmonth.getBusi_departleader().getName())){
				username_temp = rentinmonth.getBusi_departleader().getLoginName();
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_businesssales);
				departleaderCutMap.put(username_temp, MathUtils.deNull(departleaderCutMap.get(username_temp))+tempdouble);
			}
			if(null != rentinmonth.getBusi_teamleader() && StringUtils.isNotBlank(rentinmonth.getBusi_teamleader().getName())){
				username_temp = rentinmonth.getBusi_teamleader().getLoginName();
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_businesssales);
				teamleaderCutMap.put(username_temp, MathUtils.deNull(teamleaderCutMap.get(username_temp))+tempdouble);
			}
			if(null != rentinmonth.getPerson() && StringUtils.isNotBlank(rentinmonth.getPerson().getName())){
				username_temp = rentinmonth.getPerson().getLoginName();
				tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler_old, CutConfigTypeConstant.cut_businesssales);
				if(User.Busi_type.oldbusier.toString().equals(rentinmonth.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置

				}else{
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_businesssales);
				}
				/*此处比较的起始时间于2014.7.11改为用lastpaysdate与rentout_sdate_begin进行比较，
				如果lastpaysdate大于rentout_sdate_begin，则用lastpaysdate，否则用rentout_sdate_begin
				*/
				Date agencyfeeBeginDate = rentout_sdate_begin;
				if(DateUtils.compareDates(rentinmonth.getLastpaysdate(), rentout_sdate_begin, Calendar.DATE) > 0){
					agencyfeeBeginDate = rentinmonth.getLastpaysdate();
				}
				if((int)DateUtils.compareDates(agencyfeeBeginDate, rentinmonth.getSdate(), Calendar.DATE)/DaysPerMonth < AgencyfeeMonthMax){//只有前12个月才算中介费
					tempdouble = (tempcut*inmonthnumper-MathUtils.deNull(rentinmonth.getAgencyfee()))/inmonthnumper;
				}else{
					tempdouble = tempcut;
				}

				rentinCutMap.put(username_temp, MathUtils.deNull(rentinCutMap.get(username_temp))+tempdouble);

			}
			if(null != sameMonthRentout){
				outmonthnumper = (int)DateUtils.compareDates(sameMonthRentout.getEdate(), sameMonthRentout.getSdate(), Calendar.DATE)/DaysPerMonth;
				if(0 == outmonthnumper){//有极少可能没设置起始日期和结束日期
					continue;
				}
				if(outmonthnumper > AgencyfeeMonthMax){
					outmonthnumper = AgencyfeeMonthMax;
				}
				if(null != sameMonthRentout.getPerson() && StringUtils.isNotBlank(sameMonthRentout.getPerson().getName())){
					username_temp = sameMonthRentout.getPerson().getLoginName();
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler_old, CutConfigTypeConstant.cut_businesssales);
					if(User.Busi_type.oldbusier.toString().equals(sameMonthRentout.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置

					}else{
						tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_businesssales);
					}
					/*此处比较的起始时间于2014.7.11改为用lastpaysdate与rentout_sdate_begin进行比较，
					如果lastpaysdate大于rentout_sdate_begin，则用lastpaysdate，否则用rentout_sdate_begin
					*/
					Date agencyfeeBeginDate = rentout_sdate_begin;
					if(DateUtils.compareDates(rentinmonth.getLastpaysdate(), rentout_sdate_begin, Calendar.DATE) > 0){
						agencyfeeBeginDate = rentinmonth.getLastpaysdate();
					}
					if((int)DateUtils.compareDates(agencyfeeBeginDate, sameMonthRentout.getSdate(), Calendar.DATE)/DaysPerMonth < AgencyfeeMonthMax){//只有前12个月才算中介费
						tempdouble = (tempcut*outmonthnumper-MathUtils.deNull(sameMonthRentout.getAgencyfee()))/outmonthnumper;
					}else{
						tempdouble = tempcut;
					}

					rentoutCutMap.put(username_temp, MathUtils.deNull(rentoutCutMap.get(username_temp))+tempdouble);
				}

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
		//paramMap.put("name", "湘江锦绣4-1306");
		List<RentMonth> list = getBusinesscutBaseSqlList(paramMap); 
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		
		List<Map<String, Object>> rentinRentMonths = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rentoutRentMonths = new ArrayList<Map<String, Object>>();
		
		/*******************以下开始生成提成列表数据***********************/


		Map<String,Integer> totalMap = new HashMap<String,Integer>();
		
		int tempcut = 0;
		int tempdouble = 0;
		int inmonthnumper = 0;
		int outmonthnumper = 0;

		List<Cutconfig> cut_businesssaletypeconfigs = null;
		RentMonth sameMonthRentout = null;//同期的租进月记录
		int rentin_total = 0;
		int rentout_total = 0;
		int manager_total = 0;
		int departleader_total = 0;
		int teamleader_total = 0;
		Map<String,Object> resultMap = new HashMap<String,Object>();
		

		for(RentMonth rentinmonth : list){
			sameMonthRentout = getSameMonthRentoutByRentinMonthSql(rentinmonth);	
			if(null == sameMonthRentout){//如果未找到同期的出租记录，则跳出循环
				continue;
			}
			resultMap = new HashMap<String,Object>();
			resultMap.put("rentinmonth", rentinmonth);
			resultMap.put("rentoutmonth", sameMonthRentout);

			cut_businesssaletypeconfigs = cutconfigService.findCutconfiglistByCutcode(sameMonthRentout.getCut_businesssaletype());
			if(person.equals(rentinmonth.getBusi_manager())){
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.manager, CutConfigTypeConstant.cut_businesssales);
				resultMap.put("manager_cut",tempdouble);
				manager_total += tempdouble;
			}
			if(person.equals(rentinmonth.getBusi_departleader())){
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.departleader, CutConfigTypeConstant.cut_businesssales);
				resultMap.put("departleader_cut",tempdouble);
				departleader_total += tempdouble;
			}
			if(person.equals(rentinmonth.getBusi_teamleader())){
				tempdouble = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.teamleader, CutConfigTypeConstant.cut_businesssales);
				resultMap.put("teamleader_cut",tempdouble);
				teamleader_total += tempdouble;
			}
			inmonthnumper = (int)DateUtils.compareDates(rentinmonth.getEdate(), rentinmonth.getSdate(), Calendar.DATE)/DaysPerMonth;
			if(0 == inmonthnumper){//有极少可能没设置起始日期和结束日期
				continue;
			}
			if(inmonthnumper > AgencyfeeMonthMax){
				inmonthnumper = AgencyfeeMonthMax;
			}

			if(person.equals(rentinmonth.getPerson())){
				tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler_old, CutConfigTypeConstant.cut_businesssales);
				if(User.Busi_type.oldbusier.toString().equals(rentinmonth.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置

				}else{
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentinsaler, CutConfigTypeConstant.cut_businesssales);
				}
				/*此处比较的起始时间于2014.7.11改为用lastpaysdate与rentout_sdate_begin进行比较，
				如果lastpaysdate大于rentout_sdate_begin，则用lastpaysdate，否则用rentout_sdate_begin
				*/
				Date agencyfeeBeginDate = rentout_sdate_begin;
				if(DateUtils.compareDates(rentinmonth.getLastpaysdate(), rentout_sdate_begin, Calendar.DATE) > 0){
					agencyfeeBeginDate = rentinmonth.getLastpaysdate();
				}
				if(null != sameMonthRentout && (int)DateUtils.compareDates(agencyfeeBeginDate, rentinmonth.getSdate(), Calendar.DATE)/DaysPerMonth < AgencyfeeMonthMax){//只有前12个月才算中介费
					tempdouble = (tempcut*inmonthnumper-MathUtils.deNull(rentinmonth.getAgencyfee()))/inmonthnumper;
				}else{
					tempdouble = tempcut;
				}
				resultMap.put("rentin_cut",tempdouble);
				rentin_total += tempdouble;
				if("谢朝阳".equals(rentinmonth.getPerson().getName())){
					System.out.println(rentinmonth.getRent().getName()+"::"+tempdouble+"::"+rentin_total);
				}
			}
			rentinRentMonths.add(resultMap);
			if(null != sameMonthRentout){
				outmonthnumper = (int)DateUtils.compareDates(sameMonthRentout.getEdate(), sameMonthRentout.getSdate(), Calendar.DATE)/DaysPerMonth;
				if(0 == outmonthnumper){//有极少可能没设置起始日期和结束日期
					continue;
				}
				if(outmonthnumper > AgencyfeeMonthMax){
					outmonthnumper = AgencyfeeMonthMax;
				}
				if(person.equals(sameMonthRentout.getPerson())){
					tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler_old, CutConfigTypeConstant.cut_businesssales);
					if(User.Busi_type.oldbusier.toString().equals(sameMonthRentout.getPerson().getUserBusitype()) && tempcut != 0){//如果是老业务员并且有相应的老业务员提成设置
						
					}else{
						tempcut = (int)cutconfigService.getCutpercentByPersonAndType(cut_businesssaletypeconfigs, CutConfigPersonConstant.rentoutsaler, CutConfigTypeConstant.cut_businesssales);
					}
					/*此处比较的起始时间于2014.7.11改为用lastpaysdate与rentout_sdate_begin进行比较，
					如果lastpaysdate大于rentout_sdate_begin，则用lastpaysdate，否则用rentout_sdate_begin
					*/
					Date agencyfeeBeginDate = rentout_sdate_begin;
					if(DateUtils.compareDates(rentinmonth.getLastpaysdate(), rentout_sdate_begin, Calendar.DATE) > 0){
						agencyfeeBeginDate = rentinmonth.getLastpaysdate();
					}
					if((int)DateUtils.compareDates(agencyfeeBeginDate, sameMonthRentout.getSdate(), Calendar.DATE)/DaysPerMonth < AgencyfeeMonthMax){//只有前12个月才算中介费
						tempdouble = (tempcut*outmonthnumper-MathUtils.deNull(sameMonthRentout.getAgencyfee()))/outmonthnumper;
					}else{
						tempdouble = tempcut;
					}
					
					resultMap.put("rentout_cut",tempdouble);
					rentout_total += tempdouble;
					rentoutRentMonths.add(resultMap);
				}

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
	 * 完成量个人统计
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> businessSaleCount4PersonList(Map<String, Object> paramMap) throws Exception{

		paramMap.put("infotype", "businessCount");
		List<RentMonth> list = getBusinessCountBaseSqlList(paramMap); 
		
		/*******************以下开始生成提成列表数据***********************/
		Map<String,LinkedHashSet<User>> map = getLevelUsersWithRentinMonths(list,paramMap);
		LinkedHashSet<User> managers = map.get("managers");
		LinkedHashSet<User> departleaders = map.get("departleaders");
		LinkedHashSet<User> teamleaders = map.get("teamleaders");
		LinkedHashSet<User> salers = map.get("salers");//普通业务员

		Set<User> showUserList = new LinkedHashSet<User>();
		/*showUserList.addAll(managers);
		showUserList.addAll(departleaders);
		showUserList.addAll(teamleaders);*/
		showUserList.addAll(salers);
		
		List<Map<String,Object>> resultlist = new ArrayList<Map<String,Object>>();
		
		Map<String,Integer> rentinCutMap = new HashMap<String,Integer>();
		Map<String,Integer> rentoutCutMap = new HashMap<String,Integer>();
		Map<String,Integer> totalMap = new HashMap<String,Integer>();

		String username_temp = "";

		for(RentMonth rentmonth : list){

			if("rentin".equals(rentmonth.getInfotype())){
				/*if(null != rentmonth.getBusi_manager()){
					username_temp = rentmonth.getBusi_manager().getLoginName();
					rentinCutMap.put(username_temp, MathUtils.deNull(rentinCutMap.get(username_temp))+1);
				}
				if(null != rentmonth.getBusi_departleader()){
					username_temp = rentmonth.getBusi_departleader().getLoginName();
					rentinCutMap.put(username_temp, MathUtils.deNull(rentoutCutMap.get(username_temp))+1);
				}
				if(null != rentmonth.getBusi_teamleader()){
					username_temp = rentmonth.getBusi_teamleader().getLoginName();
					rentinCutMap.put(username_temp, MathUtils.deNull(rentinCutMap.get(username_temp))+1);
				}*/
				if(null != rentmonth.getPerson()){
					username_temp = rentmonth.getPerson().getLoginName();
					rentinCutMap.put(username_temp, MathUtils.deNull(rentinCutMap.get(username_temp))+1);
				}

			}else if("rentout".equals(rentmonth.getInfotype())){
				/*if(null != rentmonth.getBusi_manager()){
					username_temp = rentmonth.getBusi_manager().getLoginName();
					rentoutCutMap.put(username_temp, MathUtils.deNull(rentoutCutMap.get(username_temp))+1);
				}
				if(null != rentmonth.getBusi_departleader()){
					username_temp = rentmonth.getBusi_departleader().getLoginName();
					rentoutCutMap.put(username_temp, MathUtils.deNull(rentoutCutMap.get(username_temp))+1);
				}
				if(null != rentmonth.getBusi_teamleader()){
					username_temp = rentmonth.getBusi_teamleader().getLoginName();
					rentoutCutMap.put(username_temp, MathUtils.deNull(rentoutCutMap.get(username_temp))+1);
				}*/
				if(null != rentmonth.getPerson()){
					username_temp = rentmonth.getPerson().getLoginName();
					rentoutCutMap.put(username_temp, MathUtils.deNull(rentoutCutMap.get(username_temp))+1);
				}

			}else{
				continue;
			}
		}
		
		/***********************以下是封装最后合计列表数据************************/
		int rentin_total = 0;
		int rentout_total = 0;
		int cut_total = 0;
		
		int person_rentin_total = 0;
		int person_rentout_total = 0;
		int person_cut_total = 0;
		Map<String,Object> resultMap = new HashMap<String,Object>();
		for(User user : showUserList){
			resultMap = new HashMap<String,Object>();
			username_temp = user.getLoginName();
			person_rentin_total = MathUtils.deNull(rentinCutMap.get(username_temp));
			person_rentout_total = MathUtils.deNull(rentoutCutMap.get(username_temp));
			person_cut_total = person_rentin_total+person_rentout_total;
			resultMap.put("person", user);
			resultMap.put("rentinCutTotal", person_rentin_total);
			resultMap.put("rentoutCutTotal", person_rentout_total);
			resultMap.put("cutTotal", person_cut_total);
			resultlist.add(resultMap);
			rentin_total += person_rentin_total;
			rentout_total += person_rentout_total;
			cut_total += person_cut_total;
		}
		
		totalMap.put("rentin_cut_total", rentin_total);
		totalMap.put("rentout_cut_total", rentout_total);
		totalMap.put("cut_total", cut_total);

		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list", resultlist);
		result.put("total", totalMap);
		
		return result;

	}
	
	/**
	 * 完成量个人统计详细记录
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> businessSaleCountDetail4PersonList(Map<String, Object> paramMap) throws Exception{
		String personid = (String)paramMap.get("personid");
		User person = UserUtils.getUserById(personid);
		
		paramMap.put("infotype", "businessCount");
		List<RentMonth> list = getBusinessCountBaseSqlList(paramMap); 
		List<Map<String, Object>> rentinRentMonths = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rentoutRentMonths = new ArrayList<Map<String, Object>>();
		
		/*******************以下开始生成提成列表数据***********************/


		Map<String,Integer> totalMap = new HashMap<String,Integer>();
		
		int rentin_total = 0;
		int rentout_total = 0;

		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		for(RentMonth rentmonth : list){
			resultMap = new HashMap<String,Object>();
			resultMap.put("rentmonth", rentmonth);
			if("rentin".equals(rentmonth.getInfotype())){
				if(person.equals(rentmonth.getPerson())){
					rentin_total += 1;
					rentinRentMonths.add(resultMap);
				}
			}else if("rentout".equals(rentmonth.getInfotype())){
				if(person.equals(rentmonth.getPerson())){
					rentout_total += 1;
					rentoutRentMonths.add(resultMap);
				}
			}else{
				continue;
			}
		}
		
		/***********************以下是封装最后合计列表数据************************/
		
		totalMap.put("rentin_cut_total", rentin_total);
		totalMap.put("rentout_cut_total", rentout_total);

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
			rentout_sdate_begin = DateUtils.getFirstDayOfMonth(new Date());
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.getLastDayOfMonth(new Date());
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentout_sdate_end, "yyyy-MM-dd"));
		}
		dc.add(Restrictions.neOrIsNotNull("firstmonth_num", ""));
		dc.add(Restrictions.eq("infotype", "rentout"));
		dc.add(Restrictions.and(Restrictions.ge("lastpaysdate", rentout_sdate_begin),Restrictions.le("lastpaysdate", rentout_sdate_end)));

		String name = (String)paramMap.get("name");
		if(!StringUtils.isBlank(name)){
			dc.add(Restrictions.like("rent.name", "%"+name+"%"));
		}
		dc.addOrder(Order.asc("rent.business_num"));
		return rentMonthDao.find(dc); 
	}
	
	/**
	 * 获取空置期提成基础列表（不按时间查，全部查出）
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> getVacantperiodBaseAllList(Map<String, Object> paramMap){
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();
		dc.createAlias("rent", "rent");
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		
		Date rentout_sdate_begin = DateUtils.parseDate(paramMap.get("rentout_sdate_begin"));
		if (rentout_sdate_begin == null){
			rentout_sdate_begin = DateUtils.getFirstDayOfMonth(new Date());
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		dc.add(Restrictions.neOrIsNotNull("firstmonth_num", ""));
		dc.add(Restrictions.eq("infotype", "rentout"));

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
			rentout_sdate_begin = DateUtils.getFirstDayOfMonth(new Date());
			paramMap.put("rentout_sdate_begin", DateUtils.formatDate(rentout_sdate_begin, "yyyy-MM-dd"));
		}
		Date rentout_sdate_end = DateUtils.parseDate(paramMap.get("rentout_sdate_end"));
		if (rentout_sdate_end == null){
			rentout_sdate_end = DateUtils.getLastDayOfMonth(new Date());
			paramMap.put("rentout_sdate_end", DateUtils.formatDate(rentout_sdate_end, "yyyy-MM-dd"));
		}
		String infotype = (String)paramMap.get("infotype");
		if(!StringUtils.isBlank(infotype) && "businessCount".equals(infotype)){//完成量统计
			dc.add(Restrictions.eqProperty("lastpaysdate", "sdate"));
		}else{
			dc.add(Restrictions.eq("infotype", "rentin"));
		}
		User person = (User)paramMap.get("person");
		if(null != person){
			dc.add(Restrictions.or(Restrictions.eq("person", person),Restrictions.eq("busi_manager", person),Restrictions.eq("busi_departleader", person),Restrictions.eq("busi_teamleader", person)));
		}
		
		dc.add(Restrictions.and(Restrictions.le("lastpaysdate", rentout_sdate_end),Restrictions.ge("lastpayedate", rentout_sdate_begin)));

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
	public List<RentMonth> getBusinesscutBaseSqlList(Map<String, Object> paramMap){
		List<RentMonth> result = rentMonthDao.getBusinesscutBaseList(paramMap);

		return result;
	}

	/**
	 * 获取完成量统计基础列表
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> getBusinessCountBaseSqlList(Map<String, Object> paramMap){
		List<RentMonth> result = rentMonthDao.getBusinessCountBaseList(paramMap);

		return result;
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
			paramMap1.put("sdate_begin", rentmonth.getLastpayedate());
			List<RentMonth> tempList = rentMonthService.findSameRentinByRentout(paramMap1);
			if(null != tempList && tempList.size() > 0){
				sameMonthRentin = tempList.get(0);
				if(null != sameMonthRentin.getBusi_manager()){
					managers.add(sameMonthRentin.getBusi_manager());
					salers.remove(sameMonthRentin.getBusi_manager());
				}
				if(null != sameMonthRentin.getBusi_departleader()){
					departleaders.add(sameMonthRentin.getBusi_departleader());
					salers.remove(sameMonthRentin.getBusi_departleader());
				}
				if(null != sameMonthRentin.getBusi_teamleader()){
					teamleaders.add(sameMonthRentin.getBusi_teamleader());
					salers.remove(sameMonthRentin.getBusi_teamleader());
				}
				if(null != sameMonthRentin.getPerson() && !managers.contains(sameMonthRentin.getPerson()) && !departleaders.contains(sameMonthRentin.getPerson()) && !teamleaders.contains(sameMonthRentin.getPerson())){
					salers.add(sameMonthRentin.getPerson());
				}

			}

			if(null != rentmonth.getPerson() && !managers.contains(rentmonth.getPerson()) && !departleaders.contains(rentmonth.getPerson()) && !teamleaders.contains(rentmonth.getPerson())){
				salers.add(rentmonth.getPerson());
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
			sameMonthRentout = getSameMonthRentoutByRentinMonthSql(rentmonth);

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
	
	/**
	 * 根据出租月记录获取相对应的租进月记录
	 * @param rentoutMonth
	 * @return
	 */
	public RentMonth getSameMonthRentinByRentoutMonth(RentMonth rentoutMonth){
		RentMonth sameMonthRentin = null;
		Map<String,Object> paramMap1 = new HashMap<String,Object>();
		paramMap1.put("infotype", "rentin");
		paramMap1.put("rent", rentoutMonth.getRent());
		paramMap1.put("sdate_begin", rentoutMonth.getLastpayedate());

		List<RentMonth> tempList = rentMonthService.findSameRentinByRentout(paramMap1);
		if(null != tempList && tempList.size() > 0){
			sameMonthRentin = tempList.get(0);
		}else{
			sameMonthRentin = null;
		}
		return sameMonthRentin;
	}
	
	/**
	 * 根据出租月记录获取相对应的租进月记录
	 * @param rentoutMonth
	 * @return
	 */
	public RentMonth getSameMonthRentoutByRentinMonth(RentMonth rentinMonth){
		RentMonth sameMonthRentout = null;
		Map<String,Object> paramMap1 = new HashMap<String,Object>();
		paramMap1.put("infotype", "rentout");
		paramMap1.put("rent", rentinMonth.getRent());
		paramMap1.put("sdate_begin", rentinMonth.getLastpaysdate());
		paramMap1.put("sdate_end", rentinMonth.getLastpayedate());

		List<RentMonth> tempList = rentMonthService.findSameRentoutByRentin(paramMap1);
		if(null != tempList && tempList.size() > 0){
			sameMonthRentout = tempList.get(0);
		}else{
			sameMonthRentout = null;
		}
		return sameMonthRentout;
	}
	
	
	/**
	 * 根据出租月记录获取相对应的租进月记录
	 * @param rentoutMonth
	 * @return
	 */
	public RentMonth getSameMonthRentoutByRentinMonthSql(RentMonth rentinMonth){
		Map<String,Object> paramMap1 = new HashMap<String,Object>();
		paramMap1.put("infotype", "rentout");
		paramMap1.put("rent", rentinMonth.getRent());
		paramMap1.put("sdate_begin", rentinMonth.getLastpaysdate());
		paramMap1.put("sdate_end", rentinMonth.getLastpayedate());

		return rentMonthDao.findSameRentoutByRentin(paramMap1);
	}
	
	/**
	 * 根据空置期配置以及相应的包租月记录，获取对应的空置期剩余天数
	 * @param rentoutmonth 出租月记录
	 * @param sameMonthRentin 当前出租对应的承租月记录
	 * @param lastRentoutMonth 上一次出租月记录
	 * @param firstmonth_num 第几个头期
	 * @param vacantPeriodConfig 空置期配置天数
	 * @return
	 */
	public int getVacantPeriodCount(RentMonth rentoutmonth,RentMonth sameMonthRentin,RentMonth lastRentoutMonth,int vacantPeriodConfig){
		/*如果是第一个头期，也就是第一次租进租出，那么出租 上一次收租时间应该减去 租进的月记录的起始日（因为此时并没有上一条收租记录）。
		 * 如果不是第一个头期，则要用出租上一次收租时间减去 上一条收租记录的（如果有提前退租时间，则减提前退租时间.如果没有则减 收租结束时间）
		 */
		Date oldRentoutDate = null;
		if(!"1".equals(rentoutmonth.getFirstmonth_num())){
			if(null == lastRentoutMonth)
				return -1;
			if(null != lastRentoutMonth.getCancelrentdate()){
				oldRentoutDate = lastRentoutMonth.getCancelrentdate();
			}else{
				oldRentoutDate = lastRentoutMonth.getEdate();
			}
		}else{
			oldRentoutDate = sameMonthRentin.getSdate();
		}
		if(null == oldRentoutDate){
			return -1;
		}
		return (int)(vacantPeriodConfig-DateUtils.compareDates(rentoutmonth.getLastpaysdate(), oldRentoutDate, Calendar.DATE));

	}
	/**
	 * 根据相关参数获取空置期提成折扣值
	 * @param rentoutmonth 出租月记录
	 * @param recentVacantType 空置期类别
	 * @param vacantperiod 空置期剩余天数
	 * @return
	 */
	public double getVacantPeriodCutLevel(RentMonth rentoutmonth,String recentVacantType,int vacantperiod){
		double cutlevel = 1;
		if(DateUtils.compareDates(rentoutmonth.getEdate(), rentoutmonth.getSdate(), Calendar.DATE) <360){//如果租出时间少于1年，则提成减半
			cutlevel = 0.5;
		}
		if(VacantPeriodConstant.NOSALE_VACANTPERIOD.equals(recentVacantType) && vacantperiod > 0){//如果空置期为公司提供的，当空置时间大于0 ，无提成；小于0，则扣提成
			cutlevel = 0.0;
		}
		return cutlevel;
	}

	public static void main(String[] args){
		LinkedHashSet<User> managers = new LinkedHashSet<User>();
		User user1 = new User();
		user1.setLoginName("xx");
		User user2 = new User();
		user2.setLoginName("xx1");
		User user3 = new User();
		user3.setLoginName("xx");
		
		managers.add(user1);
		managers.add(user2);
		managers.add(user3);
		System.out.println("1");
	}
}
