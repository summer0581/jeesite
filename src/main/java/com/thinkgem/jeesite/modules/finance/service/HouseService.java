/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.CustomerDao;
import com.thinkgem.jeesite.modules.finance.dao.HouseDao;
import com.thinkgem.jeesite.modules.finance.dao.RentMonthDao;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 房屋明细Service
 * @author 夏天
 * @version 2014-03-15
 */
@Component
@Transactional(readOnly = true)
public class HouseService extends BaseService {

	@Autowired
	private HouseDao houseDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private RentMonthDao rentMonthDao;
	
	public House get(String id) {
		return houseDao.get(id);
	}
	
	public Page<House> find(Page<House> page, House house) {
		DetachedCriteria dc = houseDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(house.getName())){
			dc.add(Restrictions.like("name", "%"+house.getName()+"%"));
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getName())){
			dc.add(Restrictions.like("landlord.name", "%"+house.getLandlord().getName()+"%"));
		}
		dc.createAlias("office", "office");
		
		dc.createAlias("landlord", "landlord", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("tenant", "tenant", JoinType.LEFT_OUTER_JOIN);

		dc.add(dataScopeFilter(UserUtils.getUser(), "office", ""));
		dc.add(Restrictions.eq(House.FIELD_DEL_FLAG, House.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("id"));
		return houseDao.find(page, dc);
	}
	/**
	 * 获取未与明细表关联的房子信息
	 * @param page
	 * @param house
	 * @return
	 */
	public Page<House> findNoRelation(Page<House> page, House house) {
		return houseDao.findNoRelation(page, house);
	}
	
	/**
	 * 获取未租进的房子
	 * @param page
	 * @param house
	 * @return
	 */
	public Page<House> findNoRentin(Page<House> page, House house) {
		return houseDao.findNoRelation(page, house);
	}
	
	/**
	 * 获取所有退租或已停租的房子信息
	 * @param page
	 * @param house
	 * @return
	 */
	public Page<House> findHouseCancelRent(Page<House> page, House house) {
		return houseDao.findHouseCancelRent(page,house);
	}
	


	
	/**
	 * 通过编号获取房屋名称
	 * @return new Object[]{房屋Id,房屋名称}
	 */
	public List<Object[]> findByIds(String ids) {
		List<Object[]> list = Lists.newArrayList();
		String[] idss = StringUtils.split(ids,",");
		if (idss.length>0){
			List<House> l = houseDao.findByIdIn(idss);
			for (House e : l){
				list.add(new Object[]{e.getId(),StringUtils.abbr(e.getName(),50)});
			}
		}
		return list;
	}
	
	/**
	 * 根据房屋名称查找房屋
	 * @param name
	 * @return
	 */
	public House findByName(String name) {
		List<House> houses = houseDao.findByName(name);
		return (houses.size()>0)? houses.get(0) : null;
	}
	
	@Transactional(readOnly = false)
	public void save(House house) {
		if(StringUtils.isBlank(house.getId())){//当新增记录时，将组长的部门设置给当前房子。
			house.setOffice(UserUtils.getUserById(house.getTeam_leader().getId()).getOffice());
		}
		if(null == house.getLandlord() || null == house.getLandlord().getOffice()){//房东很有可能只带有id，需要在这里自动查询一次
			if(StringUtils.isNotBlank(house.getLandlord().getId())){
				house.setLandlord(customerDao.get(house.getLandlord().getId()));
			}
		}
		if(null == house.getTenant() || null == house.getTenant().getOffice()){//租户很有可能只带有id，需要在这里自动查询一次
			if(StringUtils.isNotBlank(house.getTenant().getId())){
				house.setTenant(customerDao.get(house.getTenant().getId()));
			}
		}
		houseDao.save(house);
	}

	
	@Transactional(readOnly = false)
	public void save4ExcelImport(House house) {
		List<House> hlist = houseDao.findByName(house.getName());
		House temphouse = null;
		if(null != hlist && hlist.size() > 0){
			temphouse = hlist.get(0);
		}
		if(null == house.getOffice()){//当新增记录时，将组长的部门设置给当前房子。
			if(null != temphouse){
				house.setOffice(temphouse.getOffice());
			}
		}
		
		if(null == house.getLandlord() ){//房东很有可能只带有id，需要在这里自动查询一次
			if(null != temphouse){
				house.setLandlord(temphouse.getLandlord());
			}
		}
		if(null == house.getTenant() ){//租户很有可能只带有id，需要在这里自动查询一次
			if(null != temphouse){
				house.setTenant(temphouse.getTenant());
			}
		}
		houseDao.save(house);
	}

	@Transactional(readOnly = false)
	public void delete(String id) {
		houseDao.deleteById(id);
	}
	
}
