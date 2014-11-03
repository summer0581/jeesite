/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;

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
	private User busi_manager;//业务经理
	private User busi_departleader;//业务部长
	private User busi_teamleader;//业务组长
	private String cut_vacantperiodtype;//空置期提成方案
	private String cut_businesssaletype;//业绩提成方案
	private Date cancelrentdate;//提前退租时间
	private String cancelrentremark;//提前退租备注
	private String is_terentrentout;//是否租客转租
	private String nextshouldamount;//下次应收应付金额
	private String nextshouldremark;//应收应付备注
	private String person_fixedcut;//业务员业绩提成固定值
	private String manager_fixedcut;//经理业绩提成固定值
	private String departer_fixedcut;//部长业绩提成固定值
	private String teamleader_fixedcut;//组长业绩提成固定值
	
	private String audit_state;//审核状态
	private String add_from;//新增来源
	

	public RentMonth() {
		super();
	}

	public RentMonth(String id){
		this();
		this.id = id;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne(fetch=FetchType.LAZY)
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

	@ManyToOne
	@JoinColumn(name="busi_manager")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	public User getBusi_manager() {
		return busi_manager;
	}

	public void setBusi_manager(User busi_manager) {
		this.busi_manager = busi_manager;
	}

	@ManyToOne
	@JoinColumn(name="busi_departleader")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	public User getBusi_departleader() {
		return busi_departleader;
	}

	public void setBusi_departleader(User busi_departleader) {
		this.busi_departleader = busi_departleader;
	}

	@ManyToOne
	@JoinColumn(name="busi_teamleader")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	public User getBusi_teamleader() {
		return busi_teamleader;
	}

	public void setBusi_teamleader(User busi_teamleader) {
		this.busi_teamleader = busi_teamleader;
	}

	public String getCut_vacantperiodtype() {
		return cut_vacantperiodtype;
	}

	public void setCut_vacantperiodtype(String cut_vacantperiodtype) {
		this.cut_vacantperiodtype = cut_vacantperiodtype;
	}

	public String getCut_businesssaletype() {
		return cut_businesssaletype;
	}

	public void setCut_businesssaletype(String cut_businesssaletype) {
		this.cut_businesssaletype = cut_businesssaletype;
	}

	public Date getCancelrentdate() {
		return cancelrentdate;
	}

	public void setCancelrentdate(Date cancelrentdate) {
		this.cancelrentdate = cancelrentdate;
	}

	
	public String getCancelrentremark() {
		return cancelrentremark;
	}

	public void setCancelrentremark(String cancelrentremark) {
		this.cancelrentremark = cancelrentremark;
	}

	public String getIs_terentrentout() {
		return is_terentrentout;
	}

	public void setIs_terentrentout(String is_terentrentout) {
		this.is_terentrentout = is_terentrentout;
	}

	public String getNextshouldamount() {
		return nextshouldamount;
	}

	public void setNextshouldamount(String nextshouldamount) {
		this.nextshouldamount = nextshouldamount;
	}

	public String getNextshouldremark() {
		return nextshouldremark;
	}

	public void setNextshouldremark(String nextshouldremark) {
		this.nextshouldremark = nextshouldremark;
	}

	public String getPerson_fixedcut() {
		return person_fixedcut;
	}

	public void setPerson_fixedcut(String person_fixedcut) {
		this.person_fixedcut = person_fixedcut;
	}

	public String getManager_fixedcut() {
		return manager_fixedcut;
	}

	public void setManager_fixedcut(String manager_fixedcut) {
		this.manager_fixedcut = manager_fixedcut;
	}

	public String getDeparter_fixedcut() {
		return departer_fixedcut;
	}

	public void setDeparter_fixedcut(String departer_fixedcut) {
		this.departer_fixedcut = departer_fixedcut;
	}

	public String getTeamleader_fixedcut() {
		return teamleader_fixedcut;
	}

	public void setTeamleader_fixedcut(String teamleader_fixedcut) {
		this.teamleader_fixedcut = teamleader_fixedcut;
	}

	public String getAudit_state() {
		return audit_state;
	}

	public void setAudit_state(String audit_state) {
		this.audit_state = audit_state;
	}

	public String getAdd_from() {
		return add_from;
	}

	public void setAdd_from(String add_from) {
		this.add_from = add_from;
	}
	
}


