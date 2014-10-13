/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.excel.entity;

import java.util.Date;

import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CustomerEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.HouseEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.UserEntity;
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 包租即将收租导出模板bean
 * @author 夏天
 * @version 2014-06-23
 */
public class Excel2Rent4RentinWillReachEdate extends IdEntity<Rent> {
	
	private static final long serialVersionUID = 1L;
	private int business_num; // 业务编号
	private House house; //房屋
	

	public Excel2Rent4RentinWillReachEdate() {
		super();
	}

	/*以下是非持久变量*/
	@ExcelField(title="编号", type=0, align=1, sort=10)
	public String getBusiness_num_str() {
		return String.valueOf(business_num);
	}
	
	@ExcelField(title="房屋地址", type=0, align=1, sort=20, fieldType=HouseEntity.class)
	public House getHouse() {
		return house;
	}
	
	@ExcelField(title="楼盘", type=0, align=1, sort=30)
	public String getHouses() {
		return "";
	}
	
	@ExcelField(title="承租时间", type=0, align=1, sort=40)
	public String getRentin_date() throws Exception{
		return "";
	}
	
	@ExcelField(title="承租月租金", type=0, align=1, sort=50)
	public String getRentin_rentmonth() throws Exception {
		return "";
	}
	
	@ExcelField(title="出租时间", type=0, align=1, sort=60)
	public String getRentout_date() throws Exception{
		return "";
	}
	
	@ExcelField(title="租进业务员", type=0, align=1, sort=70, fieldType=UserEntity.class)
	public User getRentin_person() throws Exception {
		return null;
	}

	@ExcelField(title="房东姓名", type=0, align=1, sort=80 ,fieldType= CustomerEntity.class)
	public Customer getLandlord() throws Exception {
		return house.getLandlord();
	}

	@ExcelField(title="房东电话", type=0, align=1, sort=90 )
	public String getLandlord_telephone() throws Exception {
		return house.getLandlord_telephone();
	}


}


