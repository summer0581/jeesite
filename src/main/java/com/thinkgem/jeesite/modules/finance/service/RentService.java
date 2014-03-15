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
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.dao.RentDao;

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
	
}
