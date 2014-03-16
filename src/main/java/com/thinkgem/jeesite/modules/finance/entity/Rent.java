/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.IdEntity;

/**
 * 包租明细Entity
 * @author 夏天
 * @version 2014-03-15
 */
@Entity
@Table(name = "finance_rent")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Rent extends IdEntity<Rent> {
	
	private static final long serialVersionUID = 1L;
	private String name; 	//房屋地址
	private House house; //房屋
	private String rentin_person;//'承租业务员'
	private String rentin_paytype;//'承租付款方式',
	private Date  rentin_sdate;//'承租开始时间',
	private Date  rentin_edate;//'承租结束时间',
	private String  rentin_deposit;//'承租押金',
	private String  rentin_rentmonth;//'承租月租金',
	private Date  rentin_lastpaysdate;//'承租上次付款开始日期',
	private Date  rentin_lastpayedate;//'承租上次付款结束日期',
	private Date  rentin_nextpaydate;//'承租下次付租日期',
	  
	private Date  rentout_sdate;//'出租开始时间',
	private Date  rentout_edate;//'出租结束时间',
	private String  rentout_deposit;//'出租押金',
	private String  rentout_rentmonth;//'出租月租金',
	private Date  rentout_lastpaysdate;//'出租上次付款开始日期',
	private Date  rentout_lastpayedate;//'出租上次付款结束日期',
	private String  rentout_amountreceived;//'出租已收金额',
	private Date  rentout_nextpaydate;//'出租下次付租日期',
	private String  rentout_paytype;//'出租付款方式',
	private String  rentout_person;//'出租业务员',
	private String  rentout_profitmonth;//'出租每月利润',

	public Rent() {
		super();
	}

	public Rent(String id){
		this();
		this.id = id;
	}
	
	public Rent(House house){
		this();
		this.house = house;
	}
	

	@Length(min=1, max=200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@OneToOne
	@JoinColumn(name="house_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@NotNull
	@IndexedEmbedded
	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}
	@Length(min=0, max=64)
	public String getRentin_person() {
		return rentin_person;
	}

	public void setRentin_person(String rentin_person) {
		this.rentin_person = rentin_person;
	}
	@Length(min=0, max=64)
	public String getRentin_paytype() {
		return rentin_paytype;
	}

	public void setRentin_paytype(String rentin_paytype) {
		this.rentin_paytype = rentin_paytype;
	}

	
	@Length(min=0, max=64)
	public String getRentin_deposit() {
		return rentin_deposit;
	}

	public void setRentin_deposit(String rentin_deposit) {
		this.rentin_deposit = rentin_deposit;
	}
	@Length(min=0, max=64)
	public String getRentin_rentmonth() {
		return rentin_rentmonth;
	}

	public void setRentin_rentmonth(String rentin_rentmonth) {
		this.rentin_rentmonth = rentin_rentmonth;
	}

	public Date getRentin_lastpaysdate() {
		return rentin_lastpaysdate;
	}

	public void setRentin_lastpaysdate(Date rentin_lastpaysdate) {
		this.rentin_lastpaysdate = rentin_lastpaysdate;
	}

	public Date getRentin_lastpayedate() {
		return rentin_lastpayedate;
	}

	public void setRentin_lastpayedate(Date rentin_lastpayedate) {
		this.rentin_lastpayedate = rentin_lastpayedate;
	}

	public Date getRentin_nextpaydate() {
		return rentin_nextpaydate;
	}

	public void setRentin_nextpaydate(Date rentin_nextpaydate) {
		this.rentin_nextpaydate = rentin_nextpaydate;
	}

	@Length(min=0, max=64)
	public String getRentout_deposit() {
		return rentout_deposit;
	}

	public void setRentout_deposit(String rentout_deposit) {
		this.rentout_deposit = rentout_deposit;
	}
	@Length(min=0, max=64)
	public String getRentout_rentmonth() {
		return rentout_rentmonth;
	}

	public void setRentout_rentmonth(String rentout_rentmonth) {
		this.rentout_rentmonth = rentout_rentmonth;
	}

	public Date getRentout_lastpaysdate() {
		return rentout_lastpaysdate;
	}

	public void setRentout_lastpaysdate(Date rentout_lastpaysdate) {
		this.rentout_lastpaysdate = rentout_lastpaysdate;
	}

	public Date getRentout_lastpayedate() {
		return rentout_lastpayedate;
	}

	public void setRentout_lastpayedate(Date rentout_lastpayedate) {
		this.rentout_lastpayedate = rentout_lastpayedate;
	}
	@Length(min=0, max=64)
	public String getRentout_amountreceived() {
		return rentout_amountreceived;
	}

	public void setRentout_amountreceived(String rentout_amountreceived) {
		this.rentout_amountreceived = rentout_amountreceived;
	}

	public Date getRentout_nextpaydate() {
		return rentout_nextpaydate;
	}

	public void setRentout_nextpaydate(Date rentout_nextpaydate) {
		this.rentout_nextpaydate = rentout_nextpaydate;
	}
	@Length(min=0, max=64)
	public String getRentout_paytype() {
		return rentout_paytype;
	}

	public void setRentout_paytype(String rentout_paytype) {
		this.rentout_paytype = rentout_paytype;
	}
	@Length(min=0, max=64)
	public String getRentout_person() {
		return rentout_person;
	}

	public void setRentout_person(String rentout_person) {
		this.rentout_person = rentout_person;
	}
	@Length(min=0, max=64)
	public String getRentout_profitmonth() {
		return rentout_profitmonth;
	}

	public void setRentout_profitmonth(String rentout_profitmonth) {
		this.rentout_profitmonth = rentout_profitmonth;
	}

	public Date getRentin_sdate() {
		return rentin_sdate;
	}

	public void setRentin_sdate(Date rentin_sdate) {
		this.rentin_sdate = rentin_sdate;
	}

	public Date getRentin_edate() {
		return rentin_edate;
	}

	public void setRentin_edate(Date rentin_edate) {
		this.rentin_edate = rentin_edate;
	}

	public Date getRentout_sdate() {
		return rentout_sdate;
	}

	public void setRentout_sdate(Date rentout_sdate) {
		this.rentout_sdate = rentout_sdate;
	}

	public Date getRentout_edate() {
		return rentout_edate;
	}

	public void setRentout_edate(Date rentout_edate) {
		this.rentout_edate = rentout_edate;
	}
	
	
	
}


