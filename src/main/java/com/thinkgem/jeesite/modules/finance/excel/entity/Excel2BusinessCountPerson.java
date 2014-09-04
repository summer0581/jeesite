package com.thinkgem.jeesite.modules.finance.excel.entity;

import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;

public class Excel2BusinessCountPerson {
	private String person;//业务员
	private String rentin_count;//租进数量
	private String rentout_count;//租出数量
	private String total_count;//合计
	@ExcelField(title="业务员", type=1, align=1, sort=10)
	public String getPerson() {
		return person;
	}
	public void setPerson(String person) {
		this.person = person;
	}
	@ExcelField(title="租进", type=1, align=1, sort=30)
	public String getRentin_count() {
		return rentin_count;
	}
	public void setRentin_count(String rentin_count) {
		this.rentin_count = rentin_count;
	}
	@ExcelField(title="租出", type=1, align=1, sort=60)
	public String getRentout_count() {
		return rentout_count;
	}
	public void setRentout_count(String rentout_count) {
		this.rentout_count = rentout_count;
	}
	@ExcelField(title="合计", type=1, align=1, sort=90)
	public String getTotal_count() {
		return total_count;
	}
	public void setTotal_count(String total_count) {
		this.total_count = total_count;
	}
	
	
}
