/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.dao.HouseDao;

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
	
	public House get(String id) {
		return houseDao.get(id);
	}
	
	public Page<House> find(Page<House> page, House house) {
		DetachedCriteria dc = houseDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(house.getName())){
			dc.add(Restrictions.like("name", "%"+house.getName()+"%"));
		}
		if (StringUtils.isNotEmpty(house.getLandlord_name())){
			dc.add(Restrictions.like("landlord_name", "%"+house.getLandlord_name()+"%"));
		}
		dc.add(Restrictions.eq(House.FIELD_DEL_FLAG, House.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("id"));
		return houseDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(House house) {
		houseDao.save(house);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		houseDao.deleteById(id);
	}
	
}
