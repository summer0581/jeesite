/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.List;
import java.util.Map;

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
	
	public Page<House> find(Page<House> page, House house,Map<String, Object> paramMap) {
		DetachedCriteria dc = houseDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(house.getName())){
			dc.add(Restrictions.like("name", "%"+house.getName()+"%"));
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getName())){
			dc.add(Restrictions.like("landlord.name", "%"+house.getLandlord().getName()+"%"));
		}
		if (null != house.getTenant() && StringUtils.isNotEmpty(house.getTenant().getName())){
			dc.add(Restrictions.like("tenant.name", "%"+house.getTenant().getName()+"%"));
		}
		if (StringUtils.isNotBlank(house.getHouses())){
			dc.add(Restrictions.like("houses", "%"+house.getHouses()+"%"));
		}
		if (StringUtils.isNotBlank(house.getArea())){
			dc.add(Restrictions.eq("area", house.getArea()));
		}
		if (StringUtils.isNotBlank(house.getHouse_source())){
			dc.add(Restrictions.eq("house_source", house.getHouse_source()));
		}
		if (StringUtils.isNotBlank(house.getIs_cansale())){
			dc.add(Restrictions.eq("is_cansale", house.getIs_cansale()));
		}
		if (StringUtils.isNotBlank(house.getIs_canrent())){
			dc.add(Restrictions.eq("is_canrent", house.getIs_canrent()));
		}
		if (StringUtils.isNotBlank(house.getDirection())){
			dc.add(Restrictions.like("direction", "%"+house.getDirection()+"%"));
		}
		if (StringUtils.isNotBlank(house.getAge())){
			dc.add(Restrictions.like("age", "%"+house.getAge()+"%"));
		}
		if (StringUtils.isNotBlank(house.getDecorate())){
			dc.add(Restrictions.like("decorate", "%"+house.getDecorate()+"%"));
		}
		if (StringUtils.isNotBlank(house.getProp_certno())){
			dc.add(Restrictions.like("prop_certno", "%"+house.getProp_certno()+"%"));
		}
		if (StringUtils.isNotBlank(house.getLand_certno())){
			dc.add(Restrictions.like("land_certno", "%"+house.getLand_certno()+"%"));
		}
		if (StringUtils.isNotBlank(house.getHouse_elec())){
			dc.add(Restrictions.like("house_elec", "%"+house.getHouse_elec()+"%"));
		}
		if (StringUtils.isNotBlank(house.getHouse_layout())){
			dc.add(Restrictions.like("house_layout", "%"+house.getHouse_layout()+"%"));
		}
		if (StringUtils.isNotBlank(house.getWy_useful())){
			dc.add(Restrictions.like("wy_useful", "%"+house.getWy_useful()+"%"));
		}
		if (StringUtils.isNotBlank(house.getPaytype())){
			dc.add(Restrictions.eq("paytype", house.getPaytype()));
		}
		if (StringUtils.isNotBlank(house.getStructure())){
			dc.add(Restrictions.like("structure", "%"+house.getStructure()+"%"));
		}
		if (StringUtils.isNotBlank(house.getArrond_environ())){
			dc.add(Restrictions.like("arrond_environ", "%"+house.getArrond_environ()+"%"));
		}
		if (StringUtils.isNotBlank(house.getHousing_facilities())){
			dc.add(Restrictions.like("housing_facilities", "%"+house.getHousing_facilities()+"%"));
		}
		if (StringUtils.isNotBlank(house.getAreadescribe())){
			dc.add(Restrictions.like("areadescribe", "%"+house.getAreadescribe()+"%"));
		}
		if (StringUtils.isNotBlank(house.getTraffic_condition())){
			dc.add(Restrictions.like("traffic_condition", "%"+house.getTraffic_condition()+"%"));
		}
		if (StringUtils.isNotBlank(house.getEntrust_store())){
			dc.add(Restrictions.like("entrust_store", "%"+house.getEntrust_store()+"%"));
		}
		if (StringUtils.isNotBlank(house.getRegist_store())){
			dc.add(Restrictions.like("regist_store", "%"+house.getRegist_store()+"%"));
		}
		if (StringUtils.isNotBlank(house.getIs_needdeposit())){
			dc.add(Restrictions.eq("is_needdeposit", house.getIs_needdeposit()));
		}
		
		if(null != paramMap){
			String sale_price_min = (String)paramMap.get("sale_price_min");
			String sale_price_max = (String)paramMap.get("sale_price_max");
			String measure_min = (String)paramMap.get("measure_min");
			String measure_max = (String)paramMap.get("measure_max");
			String water_num_min = (String)paramMap.get("water_num_min");
			String water_num_max = (String)paramMap.get("water_num_max");
			String elec_num_min = (String)paramMap.get("elec_num_min");
			String elec_num_max = (String)paramMap.get("elec_num_max");

			if (StringUtils.isNotBlank(sale_price_min)){
				dc.add(Restrictions.ge("sale_price", sale_price_min));
			}
			if (StringUtils.isNotBlank(sale_price_max)){
				dc.add(Restrictions.le("sale_price", sale_price_max));
			}
			if (StringUtils.isNotBlank(measure_min)){
				dc.add(Restrictions.ge("measure", measure_min));
			}
			if (StringUtils.isNotBlank(measure_max)){
				dc.add(Restrictions.le("measure", measure_max));
			}
			if (StringUtils.isNotBlank(water_num_min)){
				dc.add(Restrictions.ge("water_num", water_num_min));
			}
			if (StringUtils.isNotBlank(water_num_max)){
				dc.add(Restrictions.le("water_num", water_num_max));
			}
			if (StringUtils.isNotBlank(elec_num_min)){
				dc.add(Restrictions.ge("elec_num", elec_num_min));
			}
			if (StringUtils.isNotBlank(elec_num_max)){
				dc.add(Restrictions.le("elec_num", elec_num_max));
			}
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
