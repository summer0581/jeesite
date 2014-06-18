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
import com.thinkgem.jeesite.modules.finance.constant.VacantPeriodConstant;
import com.thinkgem.jeesite.modules.finance.dao.CutconfigDao;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;
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
			Date sdate_begin = DateUtils.parseDate(paramMap.get("sdate_begin"));
			//dc.add(Restrictions.le("lastpaysdate", sdate_begin));
			
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
			Date sdate_begin = (Date)paramMap.get("sdate_begin");
			Date sdate_end = (Date)paramMap.get("sdate_end");
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
		rentMonthDao.save(rentMonth);
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
				List<VacantPeriod> vacantPeriods = rentMonth.getRent().getLandlord_vacantperiods();
				Map<String,Object> resultMap = getLandlordVacantPeriodByRentMonth(rentMonth,vacantPeriods);
				int vacantPeriodDays = 0;
				if(null != resultMap){//房东空置期计算
					vacantPeriodDays = (int)DateUtils.compareDates((Date)resultMap.get("landlord_vacantPeriodedate"), (Date)resultMap.get("landlord_vacantPeriodsdate"), Calendar.DATE);
				}

				rentMonth.setId("");
				//rentMonth.setRemarks("");14-06-18 刘睿建议不取消remarks复制。
				int addMonth = getPayMonthUnit(rentMonth.getPaytype());
				if(null != rentMonth.getLastpaysdate())
					rentMonth.setLastpaysdate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getLastpaysdate(), addMonth), vacantPeriodDays));
				if(null != rentMonth.getLastpayedate())
					rentMonth.setLastpayedate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getLastpayedate(), addMonth), vacantPeriodDays));
				if(null != rentMonth.getNextpaydate())
					rentMonth.setNextpaydate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getNextpaydate(), addMonth), vacantPeriodDays));
				rentMonth.setNextshouldamount(String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth));
				
			}
		}
		return rentMonth;
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
				if(null != rentMonth.getLastpaysdate())
					rentMonth.setLastpaysdate(DateUtils.addMonths(rentMonth.getLastpaysdate(), addMonth));
				if(null != rentMonth.getLastpayedate())
					rentMonth.setLastpayedate(DateUtils.addMonths(rentMonth.getLastpayedate(), addMonth));
				if(null != rentMonth.getNextpaydate())
					rentMonth.setNextpaydate(DateUtils.addMonths(rentMonth.getNextpaydate(), addMonth));
				
				String amountReceived = String.valueOf(MathUtils.sumInt(rentMonth.getAmountreceived(),String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth)));
				if(StringUtils.isNotBlank(rentMonth.getNextshouldamount())){//如果设置了下次应付金额，则已收总计是用下次应付金额来累加
					amountReceived = String.valueOf(MathUtils.sumInt(rentMonth.getAmountreceived(),String.valueOf(Integer.valueOf(rentMonth.getNextshouldamount()))));
					rentMonth.setAmountreceived(amountReceived);
				}else{
					rentMonth.setAmountreceived(amountReceived);
				}
				rentMonth.setNextshouldamount(String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth));
			}
		}
		return rentMonth;
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
			if(greatThanDateNumTemp > 0 && greatThanDateNumTemp < greatThanDateNum){//如果月租起始日期大于空置期起始日期 并且 大的数量小于上一次比较的数量
				recentVacantPeriodSdate = vp.getSdate();
				recentVacantPeriodEdate = vp.getEdate();
				recentVacantType = vp.getType();
			}
			greatThanDateNum = greatThanDateNumTemp;
			
		}
		if(99999 == greatThanDateNum){//如果greatThanDateNum仍然是99999，则表示没设置空置期，则跳出
			return null;
		}
		if(null != lastRentoutMonth && recentVacantPeriodSdate.compareTo(lastRentoutMonth.getLastpaysdate()) >0){//如果上一次出租月记录的付租起始时间 > 空置期起始日期，则排除，说明已计算过空置期了
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
		RentMonth lastRentoutMonth = findLastRentMonth(rentin);
		int greatThanDateNumTemp = 0;
		int greatThanDateNum = 99999;
		for(VacantPeriod vp : landlord_vacantperiods){ //此循环是为了获取实际要用到的空置期
			if(null == vp.getSdate() || null == vp.getEdate() || !VacantPeriodConstant.LANDLORD_VACANTPERIOD.equals(vp.getType())){
				continue;
			}
			greatThanDateNumTemp = vp.getSdate().compareTo(rentin.getNextpaydate());//用房东空置期的起始时间去比较下次付租起始时间
			if(greatThanDateNumTemp >= 0 && greatThanDateNumTemp < greatThanDateNum){//如果月租起始日期大于空置期起始日期 并且 大的数量小于上一次比较的数量
				landlord_vacantPeriodsdate = vp.getSdate();
				landlord_vacantPeriodedate = vp.getEdate();
			}
			greatThanDateNum = greatThanDateNumTemp;
			
		}
		if(99999 == greatThanDateNum){//如果greatThanDateNum仍然是99999，则表示没设置空置期，则跳出
			return null;
		}
		if(null != lastRentoutMonth && landlord_vacantPeriodsdate.compareTo(lastRentoutMonth.getLastpaysdate()) >0){//如果上一次出租月记录的付租起始时间 > 空置期起始日期，则排除，说明已计算过空置期了
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
	public List<RentMonth> rentInListWillReachEdate() {
		return rentMonthDao.rentInListWillReachEdate();
	}
	
	/**
	 * 获取租出的包租列表(即将要到达租出的最后时间）
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> rentOutListWillReachEdate() {
		return rentMonthDao.rentOutListWillReachEdate();
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
		}else if("季付".equals(paytype)){
			payMonthUnit = 3;
		}else if("四月付".equals(paytype)){
			payMonthUnit = 4;
		}else if("五月付".equals(paytype)){
			payMonthUnit = 5;
		}else if("八月付".equals(paytype)){
			payMonthUnit = 8;
		}else if("十月付".equals(paytype)){
			payMonthUnit = 10;
		}else if("半年付".equals(paytype)){
			payMonthUnit = 6;
		}else if("年付".equals(paytype)){
			payMonthUnit = 12;
		}
		return payMonthUnit;
	}
	
}
