/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.excel.entity;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 房屋批量转账导出模板beanEntity
 * @author 夏天
 * @version 2014-03-15
 */

public class Excel2House4BatchBank extends IdEntity<House> {
	
	private static final long serialVersionUID = 1L;
	private int temp_index;//临时序号
	private String busi_id; 	// 业务编号
	private String name;     //地址
	private String houses;
	private Customer landlord;
	//private String landlord_telephone;
	private String debit_card;
	private Customer tenant;
	//private String tenant_telephone;
	private Office office;//所属部门
	private String image;//图片
	
	private String is_canrent;//'是否可租',
	private String is_cansale;//'是否可卖',
	private String area;//'区域',
	private String sale_price;//'价格',
	private String measure;//'面积',
	private String direction;//'朝向',
	private String age;//'年代',
	private String decorate;//'装修',
	private String is_needdeposit;//'是否需要下定金',
	private String is_xingyebank;//是否兴业银行
	private String receive_username;//收款户名
	private String receive_bank;//收款银行及营业网点
	private String is_samecity;//是否同城
	private String remit_address;//汇入地址
	private String house_source;//房屋来源
	
	
	private User team_leader;
	
	private Rent rent;
	
	

	public Excel2House4BatchBank() {
		super();
	}

	public Excel2House4BatchBank(String id){
		this();
		this.id = id;
	}
	

	public String getBusi_id() {
		return busi_id;
	}

	public void setBusi_id(String busi_id) {
		this.busi_id = busi_id;
	}
	
	@ExcelField(title="序号", type=1, align=1, sort=10,groups={1})
	@Transient
	public int getTemp_index() {
		return temp_index;
	}

	public void setTemp_index(int temp_index) {
		this.temp_index = temp_index;
	}

	@ExcelField(title="是否兴业银行账户", type=0, align=1, sort=10)
	public String getIs_xingyebank() {
		return is_xingyebank;
	}
	public void setIs_xingyebank(String is_xingyebank) {
		this.is_xingyebank = is_xingyebank;
	}
	
	@ExcelField(title="收款账号", type=0, align=1, sort=20)
	public String getDebit_card() {
		return debit_card;
	}
	public void setDebit_card(String debit_card) {
		this.debit_card = debit_card;
	}
	
	@ExcelField(title="收款户名", type=0, align=1, sort=30)
	public String getReceive_username() {
		return receive_username;
	}
	public void setReceive_username(String receive_username) {
		this.receive_username = receive_username;
	}
	
	@ExcelField(title="收款银行及营业网点", type=0, align=1, sort=40)
	public String getReceive_bank() {
		return receive_bank;
	}
	public void setReceive_bank(String receive_bank) {
		this.receive_bank = receive_bank;
	}
	
	@ExcelField(title="是否同城", type=0, align=1, sort=50)
	public String getIs_samecity() {
		return is_samecity;
	}
	public void setIs_samecity(String is_samecity) {
		this.is_samecity = is_samecity;
	}
	
	@ExcelField(title="汇入地址", type=0, align=1, sort=60)
	public String getRemit_address() {
		return remit_address;
	}
	public void setRemit_address(String remit_address) {
		this.remit_address = remit_address;
	}
	
	@Transient
	@ExcelField(title="转账金额", type=1, align=1, sort=70)
	public String getRent_nextshouldpay() throws Exception {
			return rent.getRentin().getRentmonth();
		
	}
	
	@ExcelField(title="转账用途", type=0, align=1, sort=80)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	public String getRent_remarks() throws Exception {
		return rent.getRentin().getRemarks();
	}






	
	
	
	
}


