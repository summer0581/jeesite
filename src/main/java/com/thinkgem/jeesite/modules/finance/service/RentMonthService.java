/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.MathUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.constant.RentMonthConstant;
import com.thinkgem.jeesite.modules.finance.constant.VacantPeriodConstant;
import com.thinkgem.jeesite.modules.finance.dao.CutconfigDao;
import com.thinkgem.jeesite.modules.finance.dao.HouseDao;
import com.thinkgem.jeesite.modules.finance.dao.RentDao;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
 * 包租月记录Service
 * @author 夏天
 * @version 2014-05-06
 */
@Component
@Lazy(value=true)
@Transactional(readOnly = true)
public class RentMonthService extends BaseService {

	@Autowired
	private RentMonthDao rentMonthDao;
	
	@Autowired
	private RentDao rentDao;
	
	@Autowired
	private HouseDao houseDao;
	
	@Autowired
	private CutconfigDao cutconfigDao;
	
	public RentMonth get(String id) {
		return rentMonthDao.get(id);
	}
	
	public RentMonth findByName(String name) {
		List<RentMonth> rentMonths = rentMonthDao.findByName(name);
		return (rentMonths.size()>0)?rentMonthDao.findByName(name).get(0):null;
	}
	
	
	public Page<RentMonth> find(Page<RentMonth> page, RentMonth rentMonth) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(rentMonth.getName())){
			dc.add(Restrictions.like("name", "%"+rentMonth.getName()+"%"));
		}
		if (StringUtils.isNotEmpty(rentMonth.getInfotype())){
			dc.add(Restrictions.eq("infotype", rentMonth.getInfotype()));
		}
		if (StringUtils.isNotEmpty(rentMonth.getRent().getId())){
			dc.add(Restrictions.eq("rent.id", rentMonth.getRent().getId()));
		}

		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, RentMonth.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("createDate"));
		return rentMonthDao.find(page, dc);
	}
	
	public List<RentMonth> find(RentMonth rentMonth) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();

		if (StringUtils.isNotEmpty(rentMonth.getInfotype())){
			dc.add(Restrictions.eq("infotype", rentMonth.getInfotype()));
		}
		if (StringUtils.isNotEmpty(rentMonth.getRent().getId())){
			dc.add(Restrictions.eq("rent.id", rentMonth.getRent().getId()));
		}

		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, RentMonth.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("lastpayedate"));
		return rentMonthDao.find(dc);
	}
	
	/**
	 * 根据出租月记录找出相应的租进月记录
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> findSameRentinByRentout(Map<String, Object> paramMap) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();

		if (StringUtils.isNotEmpty((String)paramMap.get("firstmonth_num"))){
			dc.add(Restrictions.eq("firstmonth_num", (String)paramMap.get("firstmonth_num")));
		}
		if (StringUtils.isNotEmpty((String)paramMap.get("infotype"))){
			dc.add(Restrictions.eq("infotype", (String)paramMap.get("infotype")));
		}
		if (null != paramMap.get("rent")){
			dc.add(Restrictions.eq("rent", paramMap.get("rent")));
		}
		if(null != paramMap.get("sdate_begin") ){//传入的是当前出租月记录的上次付租起始时间，查当前rent对应的租进月记录中 上次付租起始时间比它小的。
			Date sdate_begin = null;
			if(paramMap.get("sdate_begin") instanceof Date){
				sdate_begin = (Date)paramMap.get("sdate_begin");
			}else{
				sdate_begin = (Date)paramMap.get("sdate_begin");
			}
			dc.add(Restrictions.le("lastpayedate", sdate_begin));
			
		}
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, RentMonth.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("lastpayedate"));
		return rentMonthDao.find(dc);
	}
	
	/**
	 * 根据租进月记录找出相应的租出月记录
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> findSameRentoutByRentin(Map<String, Object> paramMap) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();

		if (StringUtils.isNotEmpty((String)paramMap.get("firstmonth_num"))){
			dc.add(Restrictions.eq("firstmonth_num", (String)paramMap.get("firstmonth_num")));
		}
		if (StringUtils.isNotEmpty((String)paramMap.get("infotype"))){
			dc.add(Restrictions.eq("infotype", (String)paramMap.get("infotype")));
		}
		if (null != paramMap.get("rent")){
			dc.add(Restrictions.eq("rent", paramMap.get("rent")));
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
			
			dc.add(Restrictions.or(Restrictions.and(Restrictions.le("lastpaysdate", sdate_begin),Restrictions.ge("lastpayedate", sdate_begin)),Restrictions.and(Restrictions.ge("lastpaysdate", sdate_begin),Restrictions.le("lastpaysdate", sdate_end))));
		}
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, RentMonth.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("lastpayedate"));
		return rentMonthDao.find(dc);
	}

	
	public RentMonth findLastRentMonth(RentMonth rentmonth) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();

		dc.add(Restrictions.eq("infotype", rentmonth.getInfotype()));
		dc.add(Restrictions.eq("rent", rentmonth.getRent()));
		
		dc.add(Restrictions.lt("lastpaysdate", rentmonth.getLastpaysdate()));
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, RentMonth.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("lastpaysdate"));
		List<RentMonth> tempList = rentMonthDao.find(dc);
		if(null != tempList && tempList.size() > 0){
			return tempList.get(0);
		}
		return null;
	}
	
	
	
	@Transactional(readOnly = false)
	public void save(RentMonth rentMonth) {
		Rent rent = rentDao.get(rentMonth.getRent().getId());
		House house = rent.getHouse();
		if(RentMonth.INFOTYPE.rentout.toString().equals(rentMonth.getInfotype())){
			house.setFlag_norentout(House.N);//此处还待改进，租出时，会将房子的未租出状态修改
			if(null != rentMonth.getCancelrentdate()){//当设置了提前退租时间，则房子设置为已退租
				house.setFlag_cancelrent(House.Y);
			}else{
				house.setFlag_cancelrent(House.N);
			}
			
		}
		
		rentMonthDao.save(rentMonth);
		rentMonthDao.flush();
		houseDao.save(house);
		houseDao.flush();
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		rentMonthDao.deleteById(id);
	}
	
	/**
	 * 设置下一次承租信息
	 * @param rentMonth
	 * @return
	 */
	public RentMonth setNewRentinMonth(RentMonth rentMonth){
		if(StringUtils.isBlank(rentMonth.getId())){//如果id为空则表示为新增，则取最新一条的相关数据
			if(null == rentMonth.getLastpayedate() && null == rentMonth.getNextpaydate()){//新增，否则是批量付租
				rentMonth.setInfotype(RentMonth.INFOTYPE.rentin.toString());
				List<RentMonth> rentMonthList = find(rentMonth);
				if(rentMonthList.size()>0){
					rentMonth = find(rentMonth).get(0);
				}
			}
			
			if(null != rentMonth){
				int monthunit = getPayMonthUnit(DictUtils.getDictLabel(rentMonth.getPaytype(), "finance_rent_paytype", "月付"));
				int addMonth = getPayMonthUnit(rentMonth.getPaytype());
				int addDay = addMonth*30;
				List<VacantPeriod> vacantPeriods = rentMonth.getRent().getLandlord_vacantperiods();
				RentMonth tempRentmonth = new RentMonth();
				if(null != rentMonth.getLastpayedate()){
					tempRentmonth.setLastpaysdate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getLastpayedate(), -addMonth),1));
					tempRentmonth.setLastpayedate(rentMonth.getLastpayedate());
				}
				Map<String,Object> resultMap = getLandlordVacantPeriodByRentMonth(tempRentmonth,vacantPeriods);
				int vacantPeriodDays = 0;
				if(null != resultMap){//房东空置期计算
					vacantPeriodDays = (int)DateUtils.compareDates((Date)resultMap.get("landlord_vacantPeriodedate"), (Date)resultMap.get("landlord_vacantPeriodsdate"), Calendar.DATE)+1;
				}

				rentMonth.setId("");
				
				//此计算是计算当前要支付的月份，起始与结束
				Map<String,Object> resultMap1 = ruleNextPayDate(rentMonth.getLastpaysdate(),rentMonth.getLastpayedate(),rentMonth.getEdate(),addMonth,vacantPeriodDays);
				Date willlastpaysdate = (Date)resultMap1.get("willlastpaysdate");
				Date willlastpayedate = (Date)resultMap1.get("willlastpayedate");
				rentMonth.setLastpaysdate(willlastpaysdate);
				rentMonth.setLastpayedate(willlastpayedate);
				//设置下一次已付起始日期和结束日期
				if(null != willlastpayedate && null != willlastpayedate){
					tempRentmonth.setLastpaysdate(DateUtils.addDays(DateUtils.addMonths(willlastpayedate, -addMonth),1));
					tempRentmonth.setLastpayedate(willlastpayedate);
					
					resultMap = getLandlordVacantPeriodByRentMonth(tempRentmonth,vacantPeriods);  
					if(null != resultMap){//下一次房东空置期计算
						vacantPeriodDays = (int)DateUtils.compareDates((Date)resultMap.get("landlord_vacantPeriodedate"), (Date)resultMap.get("landlord_vacantPeriodsdate"), Calendar.DATE)+1;
					}else{//如果没找到空置期，则设置空置期天数为0；
						vacantPeriodDays = 0;
					}
					//此计算式计算下次要支付的月份，起始与结束
					Map<String,Object> resultMap2 = ruleNextPayDate(willlastpaysdate,willlastpayedate,rentMonth.getEdate(),addMonth,vacantPeriodDays);
					Date nextwilllastpaysdate = (Date)resultMap2.get("willlastpaysdate");
					Date nextwilllastpayedate = (Date)resultMap2.get("willlastpayedate");
					if(null != rentMonth.getNextpaydate() && null != rentMonth.getLastpayedate())  
						//rentMonth.setNextpaydate(DateUtils.addDays(rentMonth.getLastpayedate(), vacantPeriodDays-RentMonthConstant.PAYFOR_REMIND_DAYS-1));
						rentMonth.setNextpaydate(DateUtils.addMonths(rentMonth.getNextpaydate(), monthunit));
					
					
					int addTotalDay = 30;
					if(null != nextwilllastpayedate && null != nextwilllastpaysdate){
						//addDay = (int)DateUtils.compareDates(nextwilllastpayedate, nextwilllastpaysdate, Calendar.DATE)+1;
						willlastpaysdate = DateUtils.addDays(willlastpayedate, 1);
						willlastpayedate = DateUtils.addMonths(willlastpayedate, addMonth);
						//addTotalDay = (int)DateUtils.compareDates(willlastpayedate, willlastpaysdate, Calendar.DATE)+1;
						addDay = addTotalDay-vacantPeriodDays; //2014-11-17刘睿要求所有月份都按30天籁算
						
					}
					
					
					double vacantPeriodCut = (double)(addDay)/addTotalDay;
					if(vacantPeriodCut < 0){
						vacantPeriodCut = 0;
					}
					//提成计算公式：月支付数*月份 * (实际支付总天数/计划支付总天数)
					rentMonth.setNextshouldamount(String.valueOf((int)(Integer.valueOf(StringUtils.defaultIfBlank(rentMonth.getRentmonth(), "0"))*addMonth*vacantPeriodCut)));

				}
				


								
			}
		}
		return rentMonth;
	}
	
	/**
	 * 根据规则以及相应的参数设置下次付租月明细的时间
	 * @param lastpayedate
	 * @param edate
	 * @param addMonth
	 * @param vacantPeriodDays 房东空置期时间
	 * @return
	 */
	public Map<String,Object> ruleNextPayDate(Date lastpaysdate,Date lastpayedate,Date edate,int addMonth,int vacantPeriodDays){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Date willlastpaysdate = null;
		Date willlastpayedate = null;
		if(null != lastpaysdate){
			willlastpaysdate = DateUtils.addDays(lastpayedate, vacantPeriodDays+1);//下次付租的起始日期应该为 给房东的空置期+1天
		}
			
		if(null != lastpayedate){//1，判断上次收租结束日期是否大于出租结束日期，如果大于，则看收租起始日期是否大于结束日期，大于则跳出，否则上次付租结束日期=结束日期
			//willlastpayedate = DateUtils.addDays(DateUtils.addMonths(lastpayedate, addMonth), vacantPeriodDays);
			willlastpayedate = DateUtils.addMonths(lastpayedate, addMonth);
			if(DateUtils.compareDates(willlastpaysdate, willlastpayedate, Calendar.DATE) > 0){//已付起始日期最多只能与结束日期相等
				willlastpaysdate = DateUtils.addDays(willlastpayedate,1);
			}
			if(DateUtils.compareDates(willlastpayedate, edate, Calendar.DATE) > 0){
				if(null != willlastpaysdate ){
					if(DateUtils.compareDates(willlastpaysdate, edate, Calendar.DATE) > 0){
						willlastpaysdate = null;//如果已经超过结束日期，则设置为空
						willlastpayedate = null;//如果已经超过结束日期，则设置为空
					}else{
						willlastpayedate = edate;
					}
				}
			}
		}
		if(null != willlastpaysdate && null != willlastpayedate){//起始日期最多不能超过结束日期
			if(DateUtils.compareDates(willlastpaysdate, willlastpayedate, Calendar.DATE) > 0){
				willlastpaysdate = willlastpayedate;
			}
		}
		resultMap.put("willlastpaysdate", willlastpaysdate);
		resultMap.put("willlastpayedate", willlastpayedate);
		return resultMap;
	}
	/**
	 * 设置下一次出租信息
	 * @param rentMonth
	 * @return
	 */
	public RentMonth setNewRentoutMonth(RentMonth rentMonth){
		if(StringUtils.isBlank(rentMonth.getId())){//如果id为空则表示为新增，则取最新一条的相关数据
			if(null == rentMonth.getLastpayedate() && null == rentMonth.getNextpaydate()){//新增，否则是批量收租
				rentMonth.setInfotype(RentMonth.INFOTYPE.rentout.toString());
				List<RentMonth> rentMonthList = find(rentMonth);
				if(rentMonthList.size()>0){
					rentMonth = find(rentMonth).get(0);
				}
			}
			
			if(null != rentMonth){
				int addMonth = getPayMonthUnit(rentMonth.getPaytype());
				rentMonth.setId("");
				//rentMonth.setRemarks("");
				//此计算是计算当前要收租的月份，起始与结束
				Map<String,Object> resultMap = ruleNextReceiveDate(rentMonth.getLastpaysdate(),rentMonth.getLastpayedate(),rentMonth.getEdate(),addMonth);
				Date willlastpaysdate = (Date)resultMap.get("willlastpaysdate");
				Date willlastpayedate = (Date)resultMap.get("willlastpayedate");
				//此计算是计算下次要收租的月份，起始与结束
				Map<String,Object> resultMap1 = ruleNextReceiveDate(willlastpaysdate,willlastpayedate,rentMonth.getEdate(),addMonth);
				Date nextwilllastpaysdate = (Date)resultMap1.get("willlastpaysdate");
				Date nextwilllastpayedate = (Date)resultMap1.get("willlastpayedate");
	
				rentMonth.setLastpaysdate(willlastpaysdate);
				rentMonth.setLastpayedate(willlastpayedate);
				if(null != rentMonth.getNextpaydate() && null != rentMonth.getLastpayedate())
					rentMonth.setNextpaydate(DateUtils.addDays(rentMonth.getLastpayedate(), -RentMonthConstant.PAYFOR_REMIND_DAYS-1));
				 
				String amountReceived = String.valueOf(MathUtils.sumInt(rentMonth.getAmountreceived(),String.valueOf(Integer.valueOf(StringUtils.defaultIfBlank(rentMonth.getRentmonth(), "0"))*addMonth)));
				if(StringUtils.isNotBlank(rentMonth.getNextshouldamount())){//如果设置了下次应付金额，则已收总计是用下次应付金额来累加
					amountReceived = String.valueOf(MathUtils.sumInt(rentMonth.getAmountreceived(),String.valueOf(Integer.valueOf(StringUtils.defaultIfBlank(rentMonth.getNextshouldamount(), "0")))));
					rentMonth.setAmountreceived(amountReceived);
				}else{
					rentMonth.setAmountreceived(amountReceived);
				}
				addMonth = (int)DateUtils.compareDates(nextwilllastpayedate, nextwilllastpaysdate, Calendar.MONTH);
				rentMonth.setNextshouldamount(String.valueOf(Integer.valueOf(StringUtils.defaultIfBlank(rentMonth.getRentmonth(), "0"))*addMonth));
			}
		}
		return rentMonth;
	}
	/**
	 * 根据规则以及相应的参数设置下次收租月明细的时间
	 * @param lastpayedate
	 * @param edate
	 * @param addMonth
	 * @return
	 */
	public Map<String,Object> ruleNextReceiveDate(Date lastpaysdate,Date lastpayedate,Date edate,int addMonth){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		Date willlastpaysdate = null;
		Date willlastpayedate = null;
		if(null != lastpaysdate){
			willlastpaysdate = DateUtils.addDays(lastpayedate, 1);//下次收租起始日期应该为上次付租结束日期+1
		}
			
		if(null != lastpayedate){//1，判断上次收租结束日期是否大于出租结束日期，如果大于，则看收租起始日期是否大于结束日期，大于则跳出，否则上次付租结束日期=结束日期
			willlastpayedate = DateUtils.addMonths(lastpayedate, addMonth);
			if(DateUtils.compareDates(willlastpayedate, edate, Calendar.DATE) > 0){
				if(null != willlastpaysdate ){
					if(DateUtils.compareDates(willlastpaysdate, edate, Calendar.DATE) > 0){
						willlastpaysdate = null;//如果已经超过结束日期，则设置为空
						willlastpayedate = null;//如果已经超过结束日期，则设置为空
					}else{
						willlastpayedate = edate;
					}
				}
			}
		}
		resultMap.put("willlastpaysdate", willlastpaysdate);
		resultMap.put("willlastpayedate", willlastpayedate);
		return resultMap;
	}
	
	/**
	 * 获取当前出租月记录对应的最近的业务员空置期
	 * @return
	 */
	public Map<String,Object> getRecentVacantPeriodByRentMonth(RentMonth rentout,List<VacantPeriod> recent_vacantperiods){
		Date recentVacantPeriodSdate = null;
		Date recentVacantPeriodEdate = null;
		RentMonth lastRentoutMonth = findLastRentMonth(rentout);
		int greatThanDateNumTemp = 0;
		int greatThanDateNum = 99999;
		String recentVacantType = "";
		for(VacantPeriod vp : recent_vacantperiods){ //此循环是为了获取实际要用到的空置期
			if(null == vp.getSdate() || null == vp.getEdate() || VacantPeriodConstant.LANDLORD_VACANTPERIOD.equals(vp.getType())){
				continue;
			}
			greatThanDateNumTemp = rentout.getSdate().compareTo(vp.getSdate());
			if(greatThanDateNumTemp >= 0 && greatThanDateNumTemp < greatThanDateNum){//如果月租起始日期大于空置期起始日期 并且 大的数量小于上一次比较的数量
				recentVacantPeriodSdate = vp.getSdate();
				recentVacantPeriodEdate = vp.getEdate();
				recentVacantType = vp.getType();
			}
			if(greatThanDateNumTemp >= 0){//如果业务员空置期的起始时间去比较下次付租起始时间得到的数量大于0
				greatThanDateNum = greatThanDateNumTemp;
			}
			
		}
		if(99999 == greatThanDateNum){//如果greatThanDateNum仍然是99999，则表示没设置空置期，则跳出
			return null;
		}
		if(null != lastRentoutMonth && lastRentoutMonth.getLastpaysdate().compareTo(recentVacantPeriodSdate) >=0){//如果上一次出租月记录的付租起始时间 > 空置期起始日期，则排除，说明已计算过空置期了
			return null;
		}
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("recent_vacantPeriodSdate", recentVacantPeriodSdate);
		resultMap.put("recent_vacantPeriodEdate", recentVacantPeriodEdate);
		resultMap.put("recent_vacantType", recentVacantType);
		return resultMap;
	}
	

	/**
	 * 获取空置期
	 * @return
	 */
	public Map<String,Object> getLandlordVacantPeriodByRentMonth(RentMonth rentin,List<VacantPeriod> landlord_vacantperiods){
		Date landlord_vacantPeriodsdate = null;
		Date landlord_vacantPeriodedate = null;
		int monthunit = getPayMonthUnit(DictUtils.getDictLabel(rentin.getPaytype(), "finance_rent_paytype", "月付"));
		int dayunit = monthunit * 30;
		RentMonth lastRentoutMonth = findLastRentMonth(rentin);
		int greatThanDateNumTemp = 0;
		int greatThanDateNum = 99999;
		if(null == rentin.getLastpaysdate() || null == rentin.getLastpayedate()){
			return null;
		}
		Date nextLastpaysdate = DateUtils.addMonths(rentin.getLastpaysdate(), monthunit);
		Date nextLastpayedate = DateUtils.addMonths(rentin.getLastpayedate(), monthunit);
		if(null == landlord_vacantperiods){
			return null;
		}
		//System.out.println(rentin.getRent().getHouse().getName());
		for(VacantPeriod vp : landlord_vacantperiods){ //此循环是为了获取实际要用到的空置期,
			if(null == vp.getSdate() || null == vp.getEdate() 
					|| //空置期起始日期要在付租起始日期与付租结束日期之间才计算
					!VacantPeriodConstant.LANDLORD_VACANTPERIOD.equals(vp.getType())){
				continue;
			}

			if((vp.getSdate().compareTo( nextLastpaysdate) >= 0 
				&& vp.getSdate().compareTo( nextLastpayedate) <= 0)){//首先判断空置期起始日是否在已付时间段之内
				if(vp.getEdate().compareTo( nextLastpayedate) <= 0){//再次判断空置期结束日期是否在已付时间段之内
					landlord_vacantPeriodsdate = vp.getSdate();
					landlord_vacantPeriodedate = vp.getEdate();
				}else{
					landlord_vacantPeriodsdate = vp.getSdate();
					landlord_vacantPeriodedate = nextLastpayedate;
				}
			}else if(vp.getSdate().compareTo( nextLastpaysdate) < 0
					&& vp.getEdate().compareTo( nextLastpaysdate) > 0){//首先判断空置期起始日，是否小于已付起始时间,并且空置期结束日要大于已付起始日期
				if(vp.getEdate().compareTo( nextLastpayedate) >= 0){//再次判断空置期结束日，是否大于或等于已付结束日期
					landlord_vacantPeriodsdate = nextLastpaysdate;
					landlord_vacantPeriodedate = nextLastpayedate;
				}else{
					landlord_vacantPeriodsdate = nextLastpaysdate;
					landlord_vacantPeriodedate = vp.getEdate();
				}
			}

		}
		if(null == landlord_vacantPeriodsdate){//如果landlord_vacantPeriodsdate为空，则表示没设置空置期，则跳出
			return null;
		}
		if(null != rentin && null != landlord_vacantPeriodsdate && rentin.getLastpaysdate().compareTo(landlord_vacantPeriodsdate) >=0){//如果上一次出租月记录的付租起始时间 > 空置期起始日期，则排除，说明已计算过空置期了
			return null;
		}
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("landlord_vacantPeriodsdate", landlord_vacantPeriodsdate);
		resultMap.put("landlord_vacantPeriodedate", landlord_vacantPeriodedate);
		return resultMap;
	}
	
	/**
	 * 获取租进的包租列表(即将要到达租进的最后时间）
	 * @param paramMap
	 * @return
	 */
	public Page<RentMonth> rentInListWillReachEdate(Page<RentMonth> pages) {
		return rentMonthDao.rentInListWillReachEdate(pages);
	}
	
	/**
	 * 获取租出的包租列表(即将要到达租出的最后时间）
	 * @param paramMap
	 * @return
	 */
	public Page<RentMonth> rentOutListWillReachEdate(Page<RentMonth> pages) {
		return rentMonthDao.rentOutListWillReachEdate(pages);
	}
	
	/**
	 * 获取空置期提成方案集合
	 * @return
	 */
	public List<Cutconfig> findVacantPeriodCutconfigList(){
		return cutconfigDao.findCutcodeList("cut_vacantperiod");
	}
	
	/**
	 * 获取业绩提成方案集合
	 * @return
	 */
	public List<Cutconfig> findBusinessSaleCutconfigList(){
		return cutconfigDao.findCutcodeList("cut_businesssales");
	}

	
	/**
	 * 获取每多少月付款的月份数
	 * @param paytypevalue
	 * @return
	 */
	public int getPayMonthUnit(String paytypevalue){
		String paytype = DictUtils.getDictLabel(paytypevalue, "finance_rent_paytype", "");
		int payMonthUnit = 0;
		if(StringUtils.isBlank(paytype) || "月付".equals(paytype)){
			payMonthUnit = 1;
		}else if("二月付".equals(paytype)){
			payMonthUnit = 3;
		}else if("季付".equals(paytype)){
			payMonthUnit = 3;
		}else if("四月付".equals(paytype)){
			payMonthUnit = 4;
		}else if("五月付".equals(paytype)){
			payMonthUnit = 5;
		}else if("半年付".equals(paytype)){
			payMonthUnit = 6;
		}else if("七月付".equals(paytype)){
			payMonthUnit = 7;
		}else if("八月付".equals(paytype)){
			payMonthUnit = 8;
		}else if("九月付".equals(paytype)){
			payMonthUnit = 9;
		}else if("十月付".equals(paytype)){
			payMonthUnit = 10;
		}else if("十一月付".equals(paytype)){
			payMonthUnit = 11;
		}else if("年付".equals(paytype)){
			payMonthUnit = 12;
		}
		return payMonthUnit;
	}
	
}
