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
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.entity.Site;
import com.thinkgem.jeesite.modules.finance.dao.RentDao;
import com.thinkgem.jeesite.modules.finance.entity.Rent;

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
		rentDao.deleteById(id);
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
			dc.add(Restrictions.le("rentin_nextpaydate", rentout_nextpaysdate));
		}
		if (null != rentout_nextpayedate){
			dc.add(Restrictions.le("rentin_nextpaydate", rentout_nextpayedate));
		}

		
		dc.add(Restrictions.eq(Rent.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("id"));
		return rentDao.find(page, dc);
	}
	
}
