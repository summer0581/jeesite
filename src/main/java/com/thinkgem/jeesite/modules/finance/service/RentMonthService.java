/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

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
	
	public List<RentMonth> find(Map<String, Object> paramMap) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();

		Date sdate_begin = DateUtils.parseDate(paramMap.get("sdate_begin"));
		Date sdate_end = DateUtils.parseDate(paramMap.get("sdate_end"));

		if (StringUtils.isNotEmpty((String)paramMap.get("infotype"))){
			dc.add(Restrictions.eq("infotype", (String)paramMap.get("infotype")));
		}
		if (null != paramMap.get("rent")){
			dc.add(Restrictions.eq("rent", paramMap.get("rent")));
		}
		
		dc.add(Restrictions.and(Restrictions.gt("lastpaysdate", sdate_begin),Restrictions.lt("lastpaysdate", sdate_end)));
		dc.add(Restrictions.eq(RentMonth.FIELD_DEL_FLAG, RentMonth.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("lastpayedate"));
		return rentMonthDao.find(dc);
	}
	
	public RentMonth findLastRentMonth(RentMonth rentmonth) {
		DetachedCriteria dc = rentMonthDao.createDetachedCriteria();

		dc.add(Restrictions.eq("infotype", rentmonth.getInfotype()));
		dc.add(Restrictions.eq("rent", rentmonth.getRent()));
		
		dc.add(Restrictions.and(Restrictions.lt("lastpaysdate", rentmonth.getLastpaysdate())));
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
	
	public void setNewRentinMonth(RentMonth rentMonth){
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
				rentMonth.setId("");
				int addMonth = getPayMonthUnit(rentMonth.getPaytype());
				if(null != rentMonth.getLastpaysdate())
					rentMonth.setLastpaysdate(DateUtils.addMonths(rentMonth.getLastpaysdate(), addMonth));
				if(null != rentMonth.getLastpayedate())
					rentMonth.setLastpayedate(DateUtils.addMonths(rentMonth.getLastpayedate(), addMonth));
				if(null != rentMonth.getNextpaydate())
					rentMonth.setNextpaydate(DateUtils.addMonths(rentMonth.getNextpaydate(), addMonth));
				rentMonth.setNextshouldamount(String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth));
			}
		}
	}
	
	public void setNewRentoutMonth(RentMonth rentMonth){
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
				if(null != rentMonth.getLastpaysdate())
					rentMonth.setLastpaysdate(DateUtils.addMonths(rentMonth.getLastpaysdate(), addMonth));
				if(null != rentMonth.getLastpayedate())
					rentMonth.setLastpayedate(DateUtils.addMonths(rentMonth.getLastpayedate(), addMonth));
				if(null != rentMonth.getNextpaydate())
					rentMonth.setNextpaydate(DateUtils.addMonths(rentMonth.getNextpaydate(), addMonth));
				rentMonth.setNextshouldamount(String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth));
				rentMonth.setAmountreceived(String.valueOf(MathUtils.sumInt(rentMonth.getAmountreceived(),String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth))));
			}
		}
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
	 * 付租
	 * @param rentMonth
	 */
	public void payRent(RentMonth rentMonth){
		if(null == rentMonth.getSdate()){
			return;
		}
		int payMonthUnit = getPayMonthUnit(rentMonth.getPaytype());
		if(null == rentMonth.getNextpaydate()){//如果承租下次付租时间为空
			rentMonth.setLastpaysdate(rentMonth.getSdate());
			Date lastpayedate = DateUtils.addMonths(rentMonth.getSdate(), payMonthUnit);
			if(lastpayedate.after(rentMonth.getEdate()))//如果上期付租时间已经在租凭最终日期之后，则上期付租时间最终点等于租凭最终日期
				lastpayedate = rentMonth.getEdate();
			rentMonth.setLastpayedate(lastpayedate);
			rentMonth.setNextpaydate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getSdate(), payMonthUnit), -7));
		}else{
			rentMonth.setLastpaysdate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getLastpaysdate(), payMonthUnit),1));
			Date lastpayedate = DateUtils.addMonths(rentMonth.getLastpayedate(), payMonthUnit);
			if(lastpayedate.after(rentMonth.getEdate()))//如果上期付租时间已经在租凭最终日期之后，则上期付租时间最终点等于租凭最终日期
				lastpayedate = rentMonth.getEdate();
			rentMonth.setLastpayedate(lastpayedate);
			rentMonth.setNextpaydate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getLastpaysdate(), payMonthUnit), -7));
		}

	}

	/**
	 * 收租
	 * @param rentMonth
	 */
	public void receiveRent(RentMonth rentMonth){
		if(null == rentMonth.getSdate()){
			return;
		}
		int payMonthUnit = getPayMonthUnit(rentMonth.getPaytype());
		if(null == rentMonth.getNextpaydate()){//如果收租下次收租时间为空
			rentMonth.setLastpaysdate(rentMonth.getSdate());
			Date lastpayedate = DateUtils.addMonths(rentMonth.getSdate(), payMonthUnit);
			if(lastpayedate.after(rentMonth.getEdate()))//如果上期收租时间已经在租凭最终日期之后，则上期收租时间最终点等于租凭最终日期
				lastpayedate = rentMonth.getEdate();
			rentMonth.setLastpayedate(lastpayedate);
			rentMonth.setNextpaydate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getSdate(), payMonthUnit), -7));
		}else{
			rentMonth.setLastpaysdate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getLastpaysdate(), payMonthUnit),1));
			Date lastpayedate = DateUtils.addMonths(rentMonth.getLastpayedate(), payMonthUnit);
			if(lastpayedate.after(rentMonth.getEdate()))
				lastpayedate = rentMonth.getEdate();
			rentMonth.setLastpayedate(lastpayedate);
			rentMonth.setNextpaydate(DateUtils.addDays(DateUtils.addMonths(rentMonth.getLastpaysdate(), payMonthUnit), -7));
		}
		int rentMonthout_amountreceived = StringUtils.isBlank(rentMonth.getAmountreceived())?0:Integer.parseInt(rentMonth.getAmountreceived());
		rentMonth.setAmountreceived(Integer.toString(rentMonthout_amountreceived+Integer.parseInt(rentMonth.getRentmonth())*payMonthUnit));
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
