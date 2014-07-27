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
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.dao.CustomerDao;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 客户信息Service
 * @author 夏天
 * @version 2014-04-21
 */
@Component
@Transactional(readOnly = true)
public class CustomerService extends BaseService {

	@Autowired
	private CustomerDao customerDao;
	
	public Customer get(String id) {
		return customerDao.get(id);
	}
	
	public Customer findByName(String name) {
		List<Customer> customers = customerDao.findByName(name);
		return (customers.size()>0)?customers.get(0):null;
	}
	
	public Customer findByNameAndTelephone(String name,String telephone) {
		List<Customer> customers = customerDao.findByNameAndTelephone(name,telephone);
		return (customers.size()>0)?customers.get(0):null;
	}
	
	public Page<Customer> find(Page<Customer> page, Customer customer) {
		DetachedCriteria dc = customerDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(customer.getName())){
			dc.add(Restrictions.like("name", "%"+customer.getName()+"%"));
		}
		dc.createAlias("office", "office");
		dc.add(Restrictions.eq(Customer.FIELD_DEL_FLAG, Customer.DEL_FLAG_NORMAL));
		dc.add(dataScopeFilter(UserUtils.getUser(), "office", ""));
		dc.addOrder(Order.desc("id"));
		return customerDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(Customer customer) {
		customerDao.save(customer);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		customerDao.deleteById(id);
	}
	
}
