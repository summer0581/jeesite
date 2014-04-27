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
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.RentDao;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 包租明细Service
 * @author 夏天
 * @version 2014-03-15
 */
@Component
@Transactional(readOnly = true)
public class RentService extends BaseService {

	@Autowired
	private RentDao rentDao;
	
	public Rent get(String id) {
		return rentDao.get(id);
	}
	
	public Page<Rent> find(Page<Rent> page, Rent rent) {
		DetachedCriteria dc = rentDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(rent.getName())){
			dc.add(Restrictions.like("name", "%"+rent.getName()+"%"));
		}
		dc.createAlias("rentin_person", "rentin");
		dc.createAlias("rentin.office", "rentin_office");
		dc.add(dataScopeFilter(UserUtils.getUser(), "rentin_office", ""));
		dc.add(Restrictions.eq(Rent.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("id"));
		return rentDao.find(page, dc);
	}
	

	
	@Transactional(readOnly = false)
	public void save(Rent rent) {
		rentDao.save(rent);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
				Rent rent = get(id);
		rent.getHouse().setId("");
		//rent.setDelFlag(BaseEntity.DEL_FLAG_DELETE);
		rentDao.save(rent);
		rentDao.deleteById(id);
		//rentDao.deleteRent(id);
	}
	
	public Page<Rent> rentList(Page<Rent> page,Map<String, Object> paramMap) {
		
		DetachedCriteria dc = rentDao.createDetachedCriteria();
		String name = (String)paramMap.get("name");
		if (StringUtils.isNotEmpty(name)){
			dc.add(Restrictions.like("name", "%"+name+"%"));
		}
		Date rentin_nextpaysdate = DateUtils.parseDate(paramMap.get("rentin_nextpaysdate"));
		Date rentin_nextpayedate = DateUtils.parseDate(paramMap.get("rentin_nextpayedate"));
		Date rentout_nextpaysdate = DateUtils.parseDate(paramMap.get("rentout_nextpaysdate"));
		Date rentout_nextpayedate = DateUtils.parseDate(paramMap.get("rentout_nextpayedate"));
		
		if (null != rentin_nextpaysdate){
			dc.add(Restrictions.gt("rentin_nextpaydate", rentin_nextpaysdate));
		}
		if (null != rentin_nextpayedate){
			dc.add(Restrictions.le("rentin_nextpaydate", rentin_nextpayedate));
		}
		if (null != rentout_nextpaysdate){
			dc.add(Restrictions.gt("rentout_nextpaydate", rentout_nextpaysdate));
		}
		if (null != rentout_nextpayedate){
			dc.add(Restrictions.le("rentout_nextpaydate", rentout_nextpayedate));
		}

		dc.createAlias("house", "house", JoinType.LEFT_OUTER_JOIN);
		dc.add(Restrictions.eq(Rent.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		if(StringUtils.isBlank(page.getOrderBy())){
			dc.addOrder(Order.desc("rentin_nextpaydate")).addOrder(Order.desc("rentout_nextpaydate"));
		}
		
		return rentDao.find(page, dc);
	}
	
	/**
	 * 获取租进的包租列表
	 * @param paramMap
	 * @return
	 */
	public List<Rent> rentInList(Map<String, Object> paramMap) {
		return rentDao.rentInList();
	}
	
	/**
	 * 获取租出的包租列表
	 * @param paramMap
	 * @return
	 */
	public List<Rent> rentOutList(Map<String, Object> paramMap) {
		return rentDao.rentOutList();
	}

	
	/**
	 * 付租
	 * @param rent
	 */
	public void payRent(Rent rent){
		if(null == rent.getRentin_sdate()){
			return;
		}
		int payMonthUnit = getPayMonthUnit(rent.getRentin_paytype());
		if(null == rent.getRentin_nextpaydate()){//如果承租下次付租时间为空
			rent.setRentin_lastpaysdate(rent.getRentin_sdate());
			Date lastpayedate = DateUtils.addMonths(rent.getRentin_sdate(), payMonthUnit);
			if(lastpayedate.after(rent.getRentin_edate()))//如果上期付租时间已经在租凭最终日期之后，则上期付租时间最终点等于租凭最终日期
				lastpayedate = rent.getRentin_edate();
			rent.setRentin_lastpayedate(lastpayedate);
			rent.setRentin_nextpaydate(DateUtils.addDays(DateUtils.addMonths(rent.getRentin_sdate(), payMonthUnit), -7));
		}else{
			rent.setRentin_lastpaysdate(DateUtils.addDays(DateUtils.addMonths(rent.getRentin_lastpaysdate(), payMonthUnit),1));
			Date lastpayedate = DateUtils.addMonths(rent.getRentin_lastpayedate(), payMonthUnit);
			if(lastpayedate.after(rent.getRentin_edate()))//如果上期付租时间已经在租凭最终日期之后，则上期付租时间最终点等于租凭最终日期
				lastpayedate = rent.getRentin_edate();
			rent.setRentin_lastpayedate(lastpayedate);
			rent.setRentin_nextpaydate(DateUtils.addDays(DateUtils.addMonths(rent.getRentin_lastpaysdate(), payMonthUnit), -7));
		}

	}

	/**
	 * 收租
	 * @param rent
	 */
	public void receiveRent(Rent rent){
		if(null == rent.getRentout_sdate()){
			return;
		}
		int payMonthUnit = getPayMonthUnit(rent.getRentout_paytype());
		if(null == rent.getRentout_nextpaydate()){//如果收租下次收租时间为空
			rent.setRentout_lastpaysdate(rent.getRentout_sdate());
			Date lastpayedate = DateUtils.addMonths(rent.getRentout_sdate(), payMonthUnit);
			if(lastpayedate.after(rent.getRentout_edate()))//如果上期收租时间已经在租凭最终日期之后，则上期收租时间最终点等于租凭最终日期
				lastpayedate = rent.getRentout_edate();
			rent.setRentout_lastpayedate(lastpayedate);
			rent.setRentout_lastpayedate(lastpayedate);
			rent.setRentout_nextpaydate(DateUtils.addDays(DateUtils.addMonths(rent.getRentout_sdate(), payMonthUnit), -7));
		}else{
			rent.setRentout_lastpaysdate(DateUtils.addDays(DateUtils.addMonths(rent.getRentout_lastpaysdate(), payMonthUnit),1));
			Date lastpayedate = DateUtils.addMonths(rent.getRentout_lastpayedate(), payMonthUnit);
			if(lastpayedate.after(rent.getRentout_edate()))
				lastpayedate = rent.getRentout_edate();
			rent.setRentout_lastpayedate(lastpayedate);
			rent.setRentout_nextpaydate(DateUtils.addDays(DateUtils.addMonths(rent.getRentout_lastpaysdate(), payMonthUnit), -7));
		}
		int rentout_amountreceived = StringUtils.isBlank(rent.getRentout_amountreceived())?0:Integer.parseInt(rent.getRentout_amountreceived());
		rent.setRentout_amountreceived(Integer.toString(rentout_amountreceived+Integer.parseInt(rent.getRentout_rentmonth())*payMonthUnit));
	}
	
	/**
	 * 根据房屋名称查找房屋包租明细
	 * @param name
	 * @return
	 */
	public Rent findByName(String name) {
		List<Rent> rents = rentDao.findByName(name);
		return (rents.size()>0)?rentDao.findByName(name).get(0):null;
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
