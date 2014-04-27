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
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.dao.HouseDao;
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
		dc.createAlias("team_leader", "team_leader");
		dc.createAlias("landlord", "landlord", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("tenant", "tenant", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("team_leader.office", "team_leader_office");
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
		DetachedCriteria dc = houseDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(house.getName())){
			dc.add(Restrictions.like("name", "%"+house.getName()+"%"));
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getName())){
			dc.add(Restrictions.like("landlord.name", "%"+house.getLandlord().getName()+"%"));
		}
		dc.createAlias("office", "office");
		dc.createAlias("team_leader", "team_leader");
		dc.createAlias("landlord", "landlord", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("tenant", "tenant", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("rent", "rent", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("team_leader.office", "team_leader_office");
		dc.add(dataScopeFilter(UserUtils.getUser(), "office", ""));
		dc.add(Restrictions.eq(House.FIELD_DEL_FLAG, House.DEL_FLAG_NORMAL));
		dc.add(Restrictions.isNull("rent.house"));
		dc.addOrder(Order.desc("id"));
		return houseDao.find(page, dc);
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
		houseDao.save(house);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		houseDao.deleteById(id);
	}
	
}
