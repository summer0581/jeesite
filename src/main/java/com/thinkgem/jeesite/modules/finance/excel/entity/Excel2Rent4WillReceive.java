/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.excel.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CustomerEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CutconfigEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.HouseEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.UserEntity;
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

/**
 * 包租即将收租导出模板bean
 * @author 夏天
 * @version 2014-06-23
 */
public class Excel2Rent4WillReceive extends IdEntity<Rent> {
	
	private static final long serialVersionUID = 1L;
	private int business_num; // 业务编号
	private House house; //房屋
	

	public Excel2Rent4WillReceive() {
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
	
	@ExcelField(title="业务员", type=0, align=1, sort=30, fieldType=UserEntity.class)
	public User getRentout_person() throws Exception {
		return null;
	}

	@ExcelField(title="付款方式", type=0, align=1, sort=40, dictType="finance_rent_paytype")
	public String getRentout_paytype() throws Exception {
		return null;
	}
	
	@ExcelField(title="出租时间", type=0, align=1, sort=50)
	public String getRentout_date() throws Exception{
		return "";
	}

	@ExcelField(title="已付月份", type=0, align=1, sort=60)
	public String getRentout_lastpaydate() throws Exception{
		return "";
	}
	
	@ExcelField(title="下次收租日期", type=0, align=1, sort=70)
	public Date getRentout_nextpaydate() throws Exception {
		return null;
	}

	@ExcelField(title="月租金", type=0, align=1, sort=80)
	public String getRentout_rentmonth() throws Exception {
		return "";
	}

	@ExcelField(title="租户姓名", type=0, align=1, sort=90 ,fieldType= CustomerEntity.class)
	public Customer getTenant() throws Exception {
		return house.getTenant();
	}

	@ExcelField(title="租户联系方式", type=0, align=1, sort=100 )
	public String getTenant_telephone() throws Exception {
		return house.getTenant_telephone();
	}

	@ExcelField(title="应收金额", type=0, align=1, sort=110,stringtoType=Integer.class)
	public String getRentout_nextshouldamount() throws Exception {
		return "";
	}
	
	@ExcelField(title="备注", type=0, align=1, sort=120)
	public String getRentout_nextshouldremark() throws Exception {
		return "";
	}


}


