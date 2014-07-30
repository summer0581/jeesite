/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.HouseAreaRoleDao;
import com.thinkgem.jeesite.modules.finance.entity.HouseAreaRole;
import com.thinkgem.jeesite.modules.finance.entity.LabelValueObj;

/**
 * 房屋区域权限设置Service
 * @author 夏天
 * @version 2014-07-30
 */
@Component
@Transactional(readOnly = true)
public class HouseAreaRoleService extends BaseService {

	@Autowired
	private HouseAreaRoleDao houseAreaRoleDao;
	
	public HouseAreaRole get(String id) {
		return houseAreaRoleDao.get(id);
	}
	
	public HouseAreaRole findByName(String name) {
		List<HouseAreaRole> houseAreaRoles = houseAreaRoleDao.findByName(name);
		return (houseAreaRoles.size()>0)?houseAreaRoleDao.findByName(name).get(0):null;
	}
	
	
	public Page<HouseAreaRole> find(Page<HouseAreaRole> page, HouseAreaRole houseAreaRole) {
		DetachedCriteria dc = houseAreaRoleDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(houseAreaRole.getName())){
			dc.add(Restrictions.like("name", "%"+houseAreaRole.getName()+"%"));
		}
		dc.add(Restrictions.eq(HouseAreaRole.FIELD_DEL_FLAG, HouseAreaRole.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("id"));
		return houseAreaRoleDao.find(page, dc);
	}
	
	/**
	 * 获取所有的房屋区域信息
	 * @return
	 */
	public List<LabelValueObj> findAllAreas(){
		return houseAreaRoleDao.findAllAreas();
	}
	
	@Transactional(readOnly = false)
	public void save(HouseAreaRole houseAreaRole) {
		houseAreaRole.setName("房屋区域权限设置");
		houseAreaRoleDao.save(houseAreaRole);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		houseAreaRoleDao.deleteById(id);
	}
	
}
