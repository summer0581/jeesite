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
import com.thinkgem.jeesite.modules.finance.entity.HouseAreaRole;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.dao.CustomerDao;
import com.thinkgem.jeesite.modules.finance.dao.HouseAreaRoleDao;
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
	
	@Autowired
	private HouseAreaRoleDao houseAreaRoleDao;
	
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
		/*DetachedCriteria dc = customerDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(customer.getName())){
			dc.add(Restrictions.like("name", "%"+customer.getName()+"%"));
		}
		dc.createAlias("office", "office");
		dc.add(Restrictions.eq(Customer.FIELD_DEL_FLAG, Customer.DEL_FLAG_NORMAL));
		dc.add(dataScopeFilter(UserUtils.getUser(), "office", ""));
		dc.addOrder(Order.desc("id"));
		return customerDao.find(page, dc);*/
		StringBuffer sql = new StringBuffer();

		if(isSuperEditRole()){//如果是有超级编辑权限的角色，即可看到所有客户信息
			sql.append("select * from finance_customer t");
		}else{
			sql.append("select DISTINCT t.* from ( ");
			sql.append("		select c.*,h.name house_name from finance_customer c  ");
			sql.append("		inner join finance_house h on h.landlord_name = c.id  ");
			sql.append("		inner join sys_user u on u.id = h.rentin_user ");
			sql.append("		inner join sys_office o on o.id = u.office_id ");
			sql.append("		where 1=1  ");
			sql.append(dataScopeFilterString(UserUtils.getUser(), "o", "u"));
			sql.append("		union ALL ");
			sql.append("		select c.*,h.name house_name from finance_customer c  ");
			sql.append("		inner join finance_house h on h.tenant_name = c.id  ");
			sql.append("		inner join sys_user u on u.id = h.rentout_user ");
			sql.append("		inner join sys_office o on o.id = u.office_id ");
			sql.append("		where 1=1  ");
			sql.append(dataScopeFilterString(UserUtils.getUser(), "o", "u"));
			HouseAreaRole houseAreaRole = houseAreaRoleDao.findByPerson(UserUtils.getUser().getId());
			if(null != houseAreaRole && StringUtils.isNotBlank(houseAreaRole.getAreas())){//房屋查询可以设置区域查询权限
				sql.append("		union ALL ");
				sql.append("		select c.*,h.name house_name from finance_customer c  ");
				sql.append("		inner join finance_house h on h.landlord_name = c.id  ");
				sql.append("		where  h.houses in ('"+houseAreaRole.getAreas().replace(",","','")+"')");
				sql.append("		union ALL ");
				sql.append("		select c.*,h.name house_name from finance_customer c  ");
				sql.append("		inner join finance_house h on h.tenant_name = c.id  ");
				sql.append("		where h.houses in ('"+houseAreaRole.getAreas().replace(",","','")+"')");
			}
			sql.append("		) t ");
		}
		sql.append(" where 1=1 ");
		if (StringUtils.isNotEmpty(customer.getName())){
			sql.append(" and t.name like '%"+customer.getName()+"%'");
		}
		return customerDao.findBySql(page, sql.toString(),null,Customer.class);
	}
	
	@Transactional(readOnly = false)
	public void save(Customer customer) {
		customerDao.save(customer);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		customerDao.deleteById(id);
	}
	
	/**
	 * 是否拥有超级编辑权限
	 * @return
	 */
	public boolean isSuperEditRole(){
		return UserUtils.getUser().isAdmin() || UserUtils.hasRole("财务管理员");
	}
	
}
