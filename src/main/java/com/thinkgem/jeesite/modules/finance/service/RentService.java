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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.RentDao;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;
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
	@Autowired
	private RentMonthDao rentMonthDao;
	@Autowired
	private RentMonthService rentMonthService;
	
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
		if(null != rent.getSalesman_vacantperiods())
			for(VacantPeriod vp: rent.getSalesman_vacantperiods()){//批量设置业务员空置期
				if(StringUtils.isBlank(vp.getId())){
					vp.prePersist();
				}
				vp.setRent(rent);
			}
		if(null != rent.getLandlord_vacantperiods())
			for(VacantPeriod vp: rent.getLandlord_vacantperiods()){//批量设置房东空置期
				if(StringUtils.isBlank(vp.getId())){
					vp.prePersist();
				}
				vp.setRent(rent);
			}
		if(null != rent.getRentinMonths())
			for(RentMonth r: rent.getRentinMonths()){//批量设置承租月明细
				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentin");
			}
		if(null != rent.getRentoutMonths())
			for(RentMonth r: rent.getRentoutMonths()){//批量设置承租月明细
				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentout");
			}

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
		
	/**
	 * 获取租进的包租列表(即将要支付下个月的钱）
	 * @param paramMap
	 * @return
	 */
	public Page<Rent> rentInListWillNeedPayNextMonth() {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("rentin_nextpayedate",DateUtils.formatDate(DateUtils.addDays(new Date(), 7), "yyyy-MM-dd"));
		Page<Rent> pages = new Page<Rent>(0, 6);
		return rentList(pages, paramMap);
	}
	
	/**
	 * 获取租出的包租列表(即将要收取下个月的钱）
	 * @param paramMap
	 * @return
	 */
	public Page<Rent> rentOutListWillNeedPayNextMonth() {
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("rentout_nextpayedate",DateUtils.formatDate(DateUtils.addDays(new Date(), 7), "yyyy-MM-dd"));
		Page<Rent> pages = new Page<Rent>(0, 6);
		return rentList(pages, paramMap);
	}

	
	public Page<Rent> rentList(Page<Rent> page,Map<String, Object> paramMap) {

		Date rentin_sdatesdate = DateUtils.parseDate(paramMap.get("rentin_sdatesdate"));
		Date rentin_sdateedate = DateUtils.parseDate(paramMap.get("rentin_sdateedate"));
		Date rentout_sdatesdate = DateUtils.parseDate(paramMap.get("rentout_sdatesdate"));
		Date rentout_sdateedate = DateUtils.parseDate(paramMap.get("rentout_sdateedate"));
		Date rentin_edatesdate = DateUtils.parseDate(paramMap.get("rentin_edatesdate"));
		Date rentin_edateedate = DateUtils.parseDate(paramMap.get("rentin_edateedate"));
		Date rentout_edatesdate = DateUtils.parseDate(paramMap.get("rentout_edatesdate"));
		Date rentout_edateedate = DateUtils.parseDate(paramMap.get("rentout_edateedate"));
		
		Date rentin_nextpaysdate = DateUtils.parseDate(paramMap.get("rentin_nextpaysdate"));
		Date rentin_nextpayedate = DateUtils.parseDate(paramMap.get("rentin_nextpayedate"));
		Date rentout_nextpaysdate = DateUtils.parseDate(paramMap.get("rentout_nextpaysdate"));
		Date rentout_nextpayedate = DateUtils.parseDate(paramMap.get("rentout_nextpayedate"));
		String rentin_rentmonthmin = (String)paramMap.get("rentin_rentmonthmin");
		String rentin_rentmonthmax = (String)paramMap.get("rentin_rentmonthmax");
		String rentout_rentmonthmin = (String)paramMap.get("rentout_rentmonthmin");
		String rentout_rentmonthmax = (String)paramMap.get("rentout_rentmonthmax");
		String rentout_paytype = (String)paramMap.get("rentout_paytype");
		
		StringBuffer sql = new StringBuffer();
		Parameter sqlparam = new Parameter(); 
		sql.append(" ");
		sql.append("select r.* ");
		sql.append("from finance_rent r ");
		
		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentin_sdatesdate","rentin_sdateedate",
				"rentin_nextpaysdate","rentin_nextpayedate","rentin_rentmonthmin","rentin_rentmonthmax",
				"rentin_edatesdate","rentin_edateedate")){
			sql.append("INNER JOIN ( ");
			sql.append("select * ");
			sql.append("from finance_rentmonth rm ");
			sql.append("where rm.infotype = 'rentin'   ");
			if(null != rentin_sdatesdate){
				sql.append("and rm.sdate >= :rentin_sdatesdate   ");
				sqlparam.put("rentin_sdatesdate", rentin_sdatesdate);
			}
			if(null != rentin_sdateedate){
				sql.append("and rm.sdate <= :rentin_sdateedate   ");
				sqlparam.put("rentin_sdateedate", rentin_sdateedate);
			}
			if(null != rentin_edatesdate){
				sql.append("and rm.edate >= :rentin_edatesdate   ");
				sqlparam.put("rentin_edatesdate", rentin_edatesdate);
			}
			if(null != rentin_edateedate){
				sql.append("and rm.edate <= :rentin_edateedate   ");
				sqlparam.put("rentin_edateedate", rentin_edateedate);
			}
			if(null != rentin_nextpaysdate){
				sql.append("and rm.nextpaydate >= :rentin_nextpaysdate   ");
				sqlparam.put("rentin_nextpaysdate", rentin_nextpaysdate);
			}
			if(null != rentin_nextpayedate){
				sql.append("and rm.nextpaydate <= :rentin_nextpayedate   ");
				sqlparam.put("rentin_nextpayedate", rentin_nextpayedate);
			}
			if(StringUtils.isNotBlank(rentin_rentmonthmin)){
				sql.append("and rm.rentmonth >= :rentin_rentmonthmin   ");
				sqlparam.put("rentin_rentmonthmin", StringUtils.toInteger(rentin_rentmonthmin));
			}
			if(StringUtils.isNotBlank(rentin_rentmonthmax)){
				sql.append("and rm.rentmonth <= :rentin_rentmonthmax   ");
				sqlparam.put("rentin_rentmonthmax", StringUtils.toInteger(rentin_rentmonthmax));
			}

			
			sql.append("and not exists (select 1 from finance_rentmonth rm2 where rm.rent_id=rm2.rent_id and rm2.infotype = 'rentin'  and rm.create_date < rm2.create_date) ");
			sql.append(") rms on r.id = rms.rent_id ");
		}

		if(!StringUtils.checkParameterIsAllBlank(paramMap, "rentout_sdatesdate","rentout_sdateedate",
				"rentout_nextpaysdate","rentout_nextpayedate","rentout_rentmonthmin","rentout_rentmonthmax","rentout_paytype",
				"rentout_edatesdate","rentout_edateedate")){
			sql.append("INNER JOIN ( ");
			sql.append("select * ");
			sql.append("from finance_rentmonth rm ");
			sql.append("where rm.infotype = 'rentout' ");
			if(null != rentout_sdatesdate){
				sql.append("and rm.sdate >= :rentout_sdatesdate   ");
				sqlparam.put("rentout_sdatesdate", rentout_sdatesdate);
			}
			if(null != rentout_sdateedate){
				sql.append("and rm.sdate <= :rentout_sdateedate   ");
				sqlparam.put("rentout_sdateedate", rentout_sdateedate);
			}
			if(null != rentout_edatesdate){
				sql.append("and rm.edate >= :rentout_edatesdate   ");
				sqlparam.put("rentout_edatesdate", rentout_edatesdate);
			}
			if(null != rentout_edateedate){
				sql.append("and rm.edate <= :rentout_edateedate   ");
				sqlparam.put("rentout_edateedate", rentout_edateedate);
			}
			if(null != rentout_nextpaysdate){
				sql.append("and rm.nextpaydate >= :rentout_nextpaysdate   ");
				sqlparam.put("rentout_nextpaysdate", rentout_nextpaysdate);
			}
			if(null != rentout_nextpayedate){
				sql.append("and rm.nextpaydate <= :rentout_nextpayedate   ");
				sqlparam.put("rentout_nextpayedate", rentout_nextpayedate);
			}
			if(StringUtils.isNotBlank(rentout_rentmonthmin)){
				sql.append("and rm.rentmonth >= :rentout_rentmonthmin   ");
				sqlparam.put("rentout_rentmonthmin", StringUtils.toInteger(rentout_rentmonthmin));
			}
			if(StringUtils.isNotBlank(rentout_rentmonthmax)){
				sql.append("and rm.rentmonth <= :rentout_rentmonthmax   ");
				sqlparam.put("rentout_rentmonthmax", StringUtils.toInteger(rentout_rentmonthmax));
			}
			if(StringUtils.isNotBlank(rentout_paytype)){
				sql.append("and rm.paytype = :rentout_paytype   ");
				sqlparam.put("rentout_paytype", rentout_paytype);
			}
			
			sql.append("and not exists  (select 1 from finance_rentmonth rm2 where rm.rent_id=rm2.rent_id and rm2.infotype = 'rentout' and rm.create_date < rm2.create_date) ");
			sql.append(") rms2 on r.id = rms2.rent_id ");
		}
		sql.append(" where 1=1  ");
		String name = (String)paramMap.get("name");
		if (StringUtils.isNotEmpty(name)){
			sql.append(" and r.name like :rentname ");
			sqlparam.put("rentname", "%"+name+"%");
		}
		String order = (String)paramMap.get("order");
		String desc = (String)paramMap.get("desc");
		if(StringUtils.isBlank(order)){
			order = "r.business_num";
		}
		if(StringUtils.isBlank(desc)){
			desc = "";
		}
		sql.append("order by "+order+" "+desc);
		
		return rentDao.findBySql(page, sql.toString(),sqlparam, Rent.class);
	}
	

	/**
	 * 批量处理包租月记录
	 * @param rentids
	 * @param infotype
	 * @return
	 */
	@Transactional(readOnly = false)
	public String batchProcessRentMonth(String rentids,String infotype){
		if(StringUtils.isBlank(rentids))
			return "";
		String[] rentidArry = rentids.split(",");
		
		try{
			for(int i = 0 ; i < rentidArry.length ; i ++){
				RentMonth rentMonth = null;
				if(infotype.equals(RentMonth.INFOTYPE.rentin.toString())){
					rentMonth = get(rentidArry[i]).getRentin();
					rentMonth.setId("");
					rentMonthService.setNewRentinMonth(rentMonth);
				}else if(infotype.equals(RentMonth.INFOTYPE.rentout.toString())){
					rentMonth = get(rentidArry[i]).getRentout();
					rentMonth.setId("");
					rentMonthService.setNewRentoutMonth(rentMonth);
				}
				rentMonthDao.save(rentMonth);
				rentMonthDao.flush();
			}
		}catch(Exception e){
			logger.error("包租批量处理报错："+e.getMessage());
			return null;
		}

		return "";
	}
	
	/**
	 * 付租
	 * @param rent
	 */
	public void payRent(Rent rent){
		
	}

	/**
	 * 收租
	 * @param rent
	 */
	public void receiveRent(Rent rent){
		
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
	

	
}
