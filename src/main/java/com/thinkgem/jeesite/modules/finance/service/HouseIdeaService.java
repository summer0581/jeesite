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
import org.springframework.web.bind.annotation.RequestParam;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.HouseIdeaDao;
import com.thinkgem.jeesite.modules.finance.entity.HouseIdea;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 房屋跟进意见Service
 * @author 夏天
 * @version 2014-08-07
 */
@Component
@Transactional(readOnly = true)
public class HouseIdeaService extends BaseService {

	@Autowired
	private HouseIdeaDao houseIdeaDao;
	
	public HouseIdea get(String id) {
		return houseIdeaDao.get(id);
	}
	
	public HouseIdea findByHouse(String name) {
		List<HouseIdea> houseIdeas = houseIdeaDao.findByHouse(name);
		return (houseIdeas.size()>0)?houseIdeaDao.findByHouse(name).get(0):null;
	}
	
	
	public Page<HouseIdea> find(Page<HouseIdea> page, HouseIdea houseIdea, @RequestParam Map<String, Object> paramMap) {
		DetachedCriteria dc = houseIdeaDao.createDetachedCriteria();
		dc.createAlias("house", "house");
		dc.createAlias("createBy", "createBy");
		if (StringUtils.isNotEmpty(houseIdea.getName())){
			dc.add(Restrictions.like("name", "%"+houseIdea.getName()+"%"));
		}
		if (StringUtils.isNotEmpty(houseIdea.getType())){
			dc.add(Restrictions.eq("type", houseIdea.getType()));
		}
		if (null != houseIdea.getHouse() && StringUtils.isNotEmpty(houseIdea.getHouse().getId())){
			dc.add(Restrictions.eq("house.id", houseIdea.getHouse().getId()));
		}
		if(StringUtils.isNotBlank((String)paramMap.get("content"))){
			dc.add(Restrictions.like("content", "%"+(String)paramMap.get("content")+"%"));
		}
		if(StringUtils.isNotBlank((String)paramMap.get("house_name"))){
			dc.add(Restrictions.like("house.name", "%"+(String)paramMap.get("house_name")+"%"));
		}
		if (StringUtils.isNotBlank((String)paramMap.get("createBy_id"))){
			dc.add(Restrictions.eq("createBy.id", (String)paramMap.get("createBy_id")));
			paramMap.put("createBy", UserUtils.getUserById((String)paramMap.get("createBy_id")));
		}
		if (StringUtils.isNotBlank((String)paramMap.get("createDate_sdate"))){
			dc.add(Restrictions.gt("createDate", DateUtils.parseDate(paramMap.get("createDate_sdate"))));
		}
		if (StringUtils.isNotBlank((String)paramMap.get("createDate_edate"))){
			dc.add(Restrictions.lt("createDate", DateUtils.parseDate(paramMap.get("createDate_edate"))));
		}
		
		dc.add(Restrictions.eq(HouseIdea.FIELD_DEL_FLAG, HouseIdea.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("createDate")); 
		return houseIdeaDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(HouseIdea houseIdea) {
		houseIdeaDao.save(houseIdea);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		houseIdeaDao.deleteById(id);
	}
	
}
