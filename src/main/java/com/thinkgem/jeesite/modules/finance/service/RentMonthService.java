/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.CutconfigDao;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
 * 包租月记录Service
 * @author 夏天
 * @version 2014-05-06
 */
@Component
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
		dc.addOrder(Order.desc("lastpayedate"));
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
	
	/**
	 * 获取租进的包租列表(即将要支付下个月的钱）
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> rentInListWillNeedPayNextMonth() {
		return rentMonthDao.rentInListWillNeedPayNextMonth();
	}
	
	/**
	 * 获取租出的包租列表(即将要收取下个月的钱）
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> rentOutListWillNeedPayNextMonth() {
		return rentMonthDao.rentOutListWillNeedPayNextMonth();
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
	 * 获取所有未租出去的房子信息
	 * @param page
	 * @param house
	 * @return
	 */
	public List<RentMonth> findNoRentOut() {
		return rentMonthDao.rentOutListHasCancel();
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
		}else if("半年付".equals(paytype)){
			payMonthUnit = 6;
		}else if("年付".equals(paytype)){
			payMonthUnit = 12;
		}
		return payMonthUnit;
	}
	
}
