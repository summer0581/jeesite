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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.EntityUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.CustomerDao;
import com.thinkgem.jeesite.modules.finance.dao.HouseDao;
import com.thinkgem.jeesite.modules.finance.dao.RentDao;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.dao.VacantPeriodDao;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;
import com.thinkgem.jeesite.modules.sys.entity.User;
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
	private HouseDao houseDao;
	@Autowired
	private CustomerDao customerDao;
	@Autowired
	private RentMonthDao rentMonthDao;
	
	@Autowired
	private VacantPeriodDao vacantPeriodDao;
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
		if(null != rent.getHouse() && StringUtils.isNotBlank(rent.getHouse().getName())){
			//rent.setName(rent.getHouse().getName());
			if(StringUtils.isNotBlank(rent.getHouse().getId())){
				rent.setHouse(houseDao.get(rent.getHouse().getId()));
			}
		}

		
		if(null != rent.getSalesman_vacantperiods())
			for(VacantPeriod vp: rent.getSalesman_vacantperiods()){//批量设置业务员空置期
				vp.setRent(rent);
				if(StringUtils.isBlank(vp.getId())){
					vp.prePersist();
				}
				
			}
		if(null != rent.getLandlord_vacantperiods())
			for(VacantPeriod vp: rent.getLandlord_vacantperiods()){//批量设置房东空置期
				vp.setRent(rent);
				if(StringUtils.isBlank(vp.getId())){
					vp.prePersist();
				}
				
			}
		if(StringUtils.isBlank(rent.getId())){
			rent.setRentinMonths(null);
			rent.setRentoutMonths(null);
		}
		if(null != rent.getRentinMonths())
			for(int i = 0 ;  i < rent.getRentinMonths().size(); i++){//批量设置承租月明细
				RentMonth r = rent.getRentinMonths().get(i);

				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentin");
			}
		if(null != rent.getRentoutMonths())
			for(int i = 0 ;  i < rent.getRentoutMonths().size(); i++){//批量设置承租月明细
				RentMonth r = rent.getRentoutMonths().get(i);

				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentout");
			}

		rentDao.save(rent);
	}
	
	@Transactional(readOnly = false)
	public void save4quickLuruHetong(Rent rent) throws Exception {
		rent.setRentin_busi_departleader(UserUtils.getUserById(rent.getRentin_busi_departleader().getId()));
		rent.setBusisaler_vacantPeriods(rent.getBusisaler_vacantPeriodsTemp());
		rent.setLandlord_vacantPeriods(rent.getLandlord_vacantPeriodsTemp());
		if(null != rent.getHouse() && StringUtils.isNotBlank(rent.getHouse().getName()) && StringUtils.isBlank(rent.getHouse().getId())){//房子如果是新增的，则初始化
			rent.getHouse().prePersist();
			rent.setName(rent.getHouse().getName());
			
			if(null != rent.getHouse().getLandlord() && StringUtils.isNotBlank(rent.getHouse().getLandlord().getName()) && StringUtils.isBlank(rent.getHouse().getLandlord().getId())){
				rent.getHouse().getLandlord().prePersist();
				rent.getHouse().getLandlord().setOffice(rent.getRentin_busi_departleader().getOffice());
			}else{
				rent.getHouse().setLandlord(null);
			}
			if(null != rent.getHouse().getTenant() && StringUtils.isNotBlank(rent.getHouse().getTenant().getName()) && StringUtils.isBlank(rent.getHouse().getTenant().getId())){
				rent.getHouse().getTenant().prePersist();
				rent.getHouse().getTenant().setOffice(rent.getRentin_busi_departleader().getOffice());
			}else{
				rent.getHouse().setTenant(null);
			}
		}
		
		if(null != rent.getSalesman_vacantperiods())
			for(VacantPeriod vp: rent.getSalesman_vacantperiods()){//批量设置业务员空置期
				vp.setRent(rent);
				if(StringUtils.isBlank(vp.getId())){
					vp.prePersist();
				}
				
			}
		if(null != rent.getLandlord_vacantperiods())
			for(VacantPeriod vp: rent.getLandlord_vacantperiods()){//批量设置房东空置期
				vp.setRent(rent);
				if(StringUtils.isBlank(vp.getId())){
					vp.prePersist();
				}
				
			}
		if(null != rent.getRentinMonths())
			for(int i = 0 ;  i < rent.getRentinMonths().size(); i++){//批量设置承租月明细
				RentMonth r = rent.getRentinMonths().get(i);
				rent.getHouse().setRentin_user(rent.getRentin_person());//将租进月明细中的租进业务员设置给房子的租进业务员。
				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentin");
			}
		if(null != rent.getRentoutMonths())
			for(int i = 0 ;  i < rent.getRentoutMonths().size(); i++){//批量设置承租月明细
				RentMonth r = rent.getRentoutMonths().get(i);
				rent.getHouse().setRentout_user(rent.getRentout_person());//将租出月明细中的租出业务员设置给房子的租出业务员。
				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentout");
			}
		houseDao.save(rent.getHouse());
		rentDao.save(rent);
	}

	
	@Transactional(readOnly = false)
	public void save4Excel(Rent rent) throws Exception {

		if(null != rent.getLandlord() && null == rent.getLandlord().getOffice()){
			User landlord_tempuser = null;
			if(null != rent.getRentin_person() && !StringUtils.isBlank(rent.getRentin_person().getLoginName())){
				landlord_tempuser = rent.getRentin_person();
			}else if(null != rent.getRentin_busi_departleader() && !StringUtils.isBlank(rent.getRentin_busi_departleader().getLoginName())){
				landlord_tempuser = rent.getRentin_busi_departleader();
			}else if(null != rent.getRentin_busi_manager() && !StringUtils.isBlank(rent.getRentin_busi_manager().getLoginName())){
				landlord_tempuser = rent.getRentin_busi_manager();
			}
			if(null != landlord_tempuser){
				rent.getLandlord().setOffice(landlord_tempuser.getOffice());
				rent.getHouse().setRentin_user(landlord_tempuser);
			}
		}
		if(null != rent.getTenant() && null == rent.getTenant().getOffice()){
			User tenant_tempuser = null;
			if(null != rent.getRentout_person() && !StringUtils.isBlank(rent.getRentout_person().getLoginName())){
				tenant_tempuser = rent.getRentout_person();
			}else if(null != rent.getRentin_busi_departleader() && !StringUtils.isBlank(rent.getRentin_busi_departleader().getLoginName())){
				tenant_tempuser = rent.getRentin_busi_departleader();
			}else if(null != rent.getRentin_busi_manager() && !StringUtils.isBlank(rent.getRentin_busi_manager().getLoginName())){
				tenant_tempuser = rent.getRentin_busi_manager();
			}
			if(null != tenant_tempuser){
				rent.getTenant().setOffice(tenant_tempuser.getOffice());
				rent.getHouse().setRentout_user(tenant_tempuser);
			}
		}
		if(null != rent.getSalesman_vacantperiods())
			for(VacantPeriod vp: rent.getSalesman_vacantperiods()){//批量设置业务员空置期
				vp.setRent(rent);
				if(StringUtils.isBlank(vp.getId())){
					List<VacantPeriod> vplist = vacantPeriodDao.findVacantPeriod(vp);//如果该空置期已存在于数据库，则跳出
					if(null != vplist && vplist.size() > 0 ){
						continue;
					}
					vp.prePersist();
				}
				
			}
		if(null != rent.getLandlord_vacantperiods())
			for(VacantPeriod vp: rent.getLandlord_vacantperiods()){//批量设置房东空置期
				vp.setRent(rent);
				if(StringUtils.isBlank(vp.getId())){
					List<VacantPeriod> vplist = vacantPeriodDao.findVacantPeriod(vp);//如果该空置期已存在于数据库，则跳出
					if(null != vplist && vplist.size() > 0 ){
						continue;
					}
					vp.prePersist();
				}
				
			}
		if(null != rent.getRentinMonths())
			for(int i = 0 ;  i < rent.getRentinMonths().size(); i++){//批量设置承租月明细
				RentMonth r = rent.getRentinMonths().get(i);
				if(StringUtils.isBlank(r.getId()) 
						&& (null == r.getPerson() || StringUtils.isBlank(r.getPerson().getName()) )
						&& null == r.getSdate() 
						&& null == r.getEdate()
						&& null == r.getLastpaysdate()
						&& null == r.getLastpayedate()){//从excel导入的数据中有很多是为空的值,判断3个值是否存在 
					rent.getRentinMonths().remove(i);
					i--;
					continue;
				}
				if(StringUtils.isBlank(r.getId())){
					if(null != r.getLastpaysdate() && null != r.getLastpayedate()){//根据上次付租起始时间和上次付租结束时间去查，看是否已存在记录
						RentMonth tempRm = rentMonthDao.findByNameLastpaySdateAndEdate(rent,r.getLastpaysdate(), r.getLastpayedate(),RentMonth.INFOTYPE.rentin);
						if(null != tempRm){
							r.setId(tempRm.getId());
							r = (RentMonth)EntityUtils.copyBasePro2NewEntity(tempRm, r);
							r.preUpdate();
						}else{
							r.prePersist();
						}
					}else{
						r.prePersist();
					}
				}
				r.setRent(rent);
				r.setInfotype("rentin");
			}
		if(null != rent.getRentoutMonths())
			for(int i = 0 ;  i < rent.getRentoutMonths().size(); i++){//批量设置承租月明细
				RentMonth r = rent.getRentoutMonths().get(i);
				if(StringUtils.isBlank(r.getId()) 
						&& (null == r.getPerson() || StringUtils.isBlank(r.getPerson().getName()) ) 
						&& null == r.getSdate() 
						&& null == r.getEdate()
						&& null == r.getLastpaysdate()
						&& null == r.getLastpayedate()){//从excel导入的数据中有很多是为空的值,判断3个值是否存在
					rent.getRentoutMonths().remove(i);
					i--;
					continue;
				}
				if(StringUtils.isBlank(r.getId())){
					if(null != r.getLastpaysdate() && null != r.getLastpayedate()){//根据上次付租起始时间和上次付租结束时间去查，看是否已存在记录
						RentMonth tempRm = rentMonthDao.findByNameLastpaySdateAndEdate(rent,r.getLastpaysdate(), r.getLastpayedate(),RentMonth.INFOTYPE.rentout);
						if(null != tempRm){
							r.setId(tempRm.getId());
							r = (RentMonth)EntityUtils.copyBasePro2NewEntity(tempRm, r);
							r.preUpdate();
						}else{
							r.prePersist();
						}
					}else{
						r.prePersist();
					}

				}
				r.setRent(rent);
				r.setInfotype("rentout");
			}
		rentDao.save(rent);
		
	}

	
	@Transactional(readOnly = false)
	public void delete(String id) {
				//Rent rent = get(id);
		//rent.getHouse().setId("");
		//rent.setDelFlag(BaseEntity.DEL_FLAG_DELETE);
		//rentDao.save(rent);
		rentDao.deleteById(id);
		//rentDao.deleteRent(id);
	}
		
	/**
	 * 获取租进的包租列表(即将要支付下个月的钱）
	 * @param paramMap
	 * @return
	 * @throws Exception 
	 */
	public Page<Rent> rentInListWillNeedPayNextMonth(Page<Rent> pages,Map<String, Object> paramMap) throws Exception {
		Page<Rent> pagerent = rentDao.rentList(pages, paramMap);
	    List<Rent> rentlist = pagerent.getList();
	    for (int i = 0; i < rentlist.size(); i++) {
	      Rent rent = (Rent)rentlist.get(i);
	      Map<String,Object> resultMap = this.rentMonthService.getLandlordVacantPeriodByRentMonth(rent.getRentin(), rent.getLandlord_vacantperiods());
	      if (resultMap != null) {
	        rent.setLandlord_vacantPeriodsdate((Date)resultMap.get("landlord_vacantPeriodsdate"));
	        rent.setLandlord_vacantPeriodedate((Date)resultMap.get("landlord_vacantPeriodedate"));
	      }
	    }
		return pagerent;
	}
	
	/**
	 * 获取租出的包租列表(即将要收取下个月的钱）
	 * @param paramMap
	 * @return
	 */
	public Page<Rent> rentOutListWillNeedPayNextMonth(Page<Rent> pages,Map<String, Object> paramMap) {
		Page<Rent> pagerent = rentDao.rentList(pages, paramMap);
		return pagerent;
	}
	
	/**
	 * 获取包租列表字段总计数
	 * @param paramMap
	 * @return
	 */
	public Map<String,String> rentListSumColumn(Map<String, Object> paramMap,RentMonth.INFOTYPE infotype){
		return rentDao.rentListSumColumn( paramMap,infotype);
	}
	

	
	public Page<Rent> rentList(Page<Rent> page,Map<String, Object> paramMap) {

		return rentDao.rentList(page,paramMap);
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
				RentMonth rentMonthTarget = new RentMonth();
				if(infotype.equals(RentMonth.INFOTYPE.rentin.toString())){
					rentMonth = get(rentidArry[i]).getRentin();
					BeanUtils.copyProperties(rentMonth, rentMonthTarget);
					rentMonthTarget.setId("");
					rentMonthService.setNewRentinMonth(rentMonthTarget);
				}else if(infotype.equals(RentMonth.INFOTYPE.rentout.toString())){
					rentMonth = get(rentidArry[i]).getRentout();
					BeanUtils.copyProperties(rentMonth, rentMonthTarget);
					rentMonthTarget.setId("");
					rentMonthService.setNewRentoutMonth(rentMonthTarget);
				}
				rentMonthDao.save(rentMonthTarget);
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
	
	/**
	 * 获取系统最大的编号但不包括5位数的
	 * @return
	 */
	public int getMaxBusinessNum(){

		return rentDao.getMaxBusinessNum();
	}
	
}
