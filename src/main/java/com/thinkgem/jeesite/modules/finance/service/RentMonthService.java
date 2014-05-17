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

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
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
		dc.addOrder(Order.desc("createDate"));
		return rentMonthDao.find(dc);
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
	 * 获取租进的包租列表
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> rentInList(Map<String, Object> paramMap) {
		return rentMonthDao.rentInList();
	}
	
	/**
	 * 获取租出的包租列表
	 * @param paramMap
	 * @return
	 */
	public List<RentMonth> rentOutList(Map<String, Object> paramMap) {
		return rentMonthDao.rentOutList();
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
	private int getPayMonthUnit(String paytypevalue){
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
