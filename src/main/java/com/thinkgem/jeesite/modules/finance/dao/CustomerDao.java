/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.Rent;

/**
 * 客户信息DAO接口
 * @author 夏天
 * @version 2014-04-21
 */
@Repository
public class CustomerDao extends BaseDao<Customer> {
	public List<Customer> findByName(String name){
		return findBySql("select * from finance_customer c where c.name = :p1", new Parameter(name),Customer.class);
	}
	
	public List<Customer> findByNameAndTelephone(String name,String telephone){
		return findBySql("select * from finance_customer c where c.name = :p1 and c.telephone = :p2", new Parameter(name,telephone),Customer.class);
	}
}
