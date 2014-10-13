package com.thinkgem.jeesite.modules.finance.excel.entity;

import javax.persistence.Transient;

import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CustomerEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.HouseEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.UserEntity;
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.sys.entity.User;

public class Excel2Rent4RentoutWillReachEdate {
	private static final long serialVersionUID = 1L;
	private int business_num; // 业务编号
	private House house; //房屋
	

	public Excel2Rent4RentoutWillReachEdate() {
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
	
	@ExcelField(title="租进业务员", type=0, align=1, sort=40, fieldType=UserEntity.class)
	public User getRentin_person() {
		return null;
	}
	
	@ExcelField(title="租进月租金", type=0, align=1, sort=50)
	public String getRentin_rentmonth() {
		return "";
	}
	
	@ExcelField(title="租出业务员", type=0, align=1, sort=60, fieldType=UserEntity.class)
	public User getRentout_person()  {
		return null;
	}
	
	@ExcelField(title="出租时间", type=0, align=1, sort=70)
	public String getRentout_date() {
		return "";
	}
	
	@ExcelField(title="出租月租金", type=0, align=1, sort=80)
	public String getRentout_rentmonth() {
		return "";
	}

	@ExcelField(title="出租押金", type=0, align=1, sort=90)
	public String getRentout_deposit() {
		return "";
	}
	
	@Transient
	@ExcelField(title="付款方式", type=0, align=1, sort=100, dictType="finance_rent_paytype")
	public String getRentout_paytype() {
		return "";
	}


	@ExcelField(title="租客姓名", type=0, align=1, sort=110 ,fieldType= CustomerEntity.class)
	public Customer getTenant() {
		return house.getTenant();
	}

	@ExcelField(title="租客电话", type=0, align=1, sort=120 )
	public String getTenant_telephone(){
		return "";
	}

}
