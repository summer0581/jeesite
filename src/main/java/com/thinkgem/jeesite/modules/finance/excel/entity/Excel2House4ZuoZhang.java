/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.excel.entity;

import java.util.Date;

import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CustomerEntity;
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.House;

/**
 * 房屋批量转账导出模板beanEntity
 * @author 夏天
 * @version 2014-03-15
 */

public class Excel2House4ZuoZhang extends IdEntity<House> {
	
	private static final long serialVersionUID = 1L;

	public Excel2House4ZuoZhang() {
		super();
	}

	public Excel2House4ZuoZhang(String id){
		this();
		this.id = id;
	}
	
	@ExcelField(title="编号", type=0, align=1, sort=10)
	public String getBusiness_num_str() {
		return "";
	}
	
	@ExcelField(title="地址", type=0, align=1, sort=20)
	public String getName() {
		return "";
	}

	@ExcelField(title="租金", type=0, align=1, sort=30)
	public String getRentin_rentmonth() throws Exception {
		return "";
	}
	@ExcelField(title="付款日期", type=0, align=1, sort=40)
	public Date getRentin_lastpaysdate() throws Exception {
		return null;
	}

	@ExcelField(title="收款人", type=0, align=1, sort=50 ,fieldType= CustomerEntity.class)
	public Customer getLandlord() throws Exception {
		return null;
	}

	@ExcelField(title="应付金额", type=0, align=1, sort=60)
	public String getRentin_nextshouldamountBydefault() throws Exception {
		return "";
	}

	@ExcelField(title="备注", type=0, align=1, sort=70)
	public String getRentin_nextshouldremark() throws Exception {
		return "";
	}	
	
}


