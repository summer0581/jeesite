/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 包租月记录Entity
 * @author 夏天
 * @version 2014-05-06
 */
@Entity
@Table(name = "finance_rentmonth")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RentMonth extends IdEntity<RentMonth> {
	
	public enum INFOTYPE{
		rentin,rentout
	}
	
	private static final long serialVersionUID = 1L;
	private String name; 	// 名称
	private Rent rent;//包租主信息
	private User person;//'承租业务员'
	private String paytype;//'承租付款方式',
	private Date  sdate;//'承租开始时间',
	private Date  edate;//'承租结束时间',
	private String  deposit;//'承租押金',
	private String  rentmonth;//'承租月租金',
	private Date  lastpaysdate;//'承租上次付款开始日期',
	private Date  lastpayedate;//'承租上次付款结束日期',
	private Date  nextpaydate;//'承租下次付租日期',
	private String amountreceived;//'出租已收金额',
	private String infotype;//信息类别
	private String firstmonth_num;//是否一期的头一个月
	private String agencyfee;//中介费
	

	public RentMonth() {
		super();
	}

	public RentMonth(String id){
		this();
		this.id = id;
	}

	@Length(min=1, max=200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	@JoinColumn(name="rent_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@NotNull
	@IndexedEmbedded
	public Rent getRent() {
		return rent;
	}

	public void setRent(Rent rent) {
		this.rent = rent;
	}

	@ManyToOne
	@JoinColumn(name="person")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	public User getPerson() {
		return person;
	}

	public void setPerson(User person) {
		this.person = person;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public Date getSdate() {
		return sdate;
	}

	public void setSdate(Date sdate) {
		this.sdate = sdate;
	}

	public Date getEdate() {
		return edate;
	}

	public void setEdate(Date edate) {
		this.edate = edate;
	}

	public String getDeposit() {
		return deposit;
	}

	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	public String getRentmonth() {
		return rentmonth;
	}

	public void setRentmonth(String rentmonth) {
		this.rentmonth = rentmonth;
	}

	public Date getLastpaysdate() {
		return lastpaysdate;
	}

	public void setLastpaysdate(Date lastpaysdate) {
		this.lastpaysdate = lastpaysdate;
	}

	public Date getLastpayedate() {
		return lastpayedate;
	}

	public void setLastpayedate(Date lastpayedate) {
		this.lastpayedate = lastpayedate;
	}

	public Date getNextpaydate() {
		return nextpaydate;
	}

	public void setNextpaydate(Date nextpaydate) {
		this.nextpaydate = nextpaydate;
	}

	public String getAmountreceived() {
		return amountreceived;
	}

	public void setAmountreceived(String amountreceived) {
		this.amountreceived = amountreceived;
	}

	public String getInfotype() {
		return infotype;
	}

	public void setInfotype(String infotype) {
		this.infotype = infotype;
	}

	public String getFirstmonth_num() {
		return firstmonth_num;
	}

	public void setFirstmonth_num(String firstmonth_num) {
		this.firstmonth_num = firstmonth_num;
	}

	public String getAgencyfee() {
		return agencyfee;
	}

	public void setAgencyfee(String agencyfee) {
		this.agencyfee = agencyfee;
	}


	
	
	
}


