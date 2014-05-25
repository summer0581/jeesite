/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import java.util.Date;

import javax.persistence.Entity;
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

import com.thinkgem.jeesite.common.persistence.IdEntity;

/**
 * 空置期提成Entity
 * @author 夏天
 * @version 2014-04-13
 */
@Entity
@Table(name = "finance_vacantperiod")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class VacantPeriod extends IdEntity<VacantPeriod> {

	
	private static final long serialVersionUID = 1L;
	private String name; 	// 名称
	private Date sdate;//起始时间
	private Date edate;//结束时间
	private String sn;//序号
	private String type;//类别
	
	private Rent rent;//包租明细

	public VacantPeriod() {
		super();
	}

	public VacantPeriod(String id){
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

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	@ManyToOne
	@JoinColumn(name="rent_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	public Rent getRent() {
		return rent;
	}

	public void setRent(Rent rent) {
		this.rent = rent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
}


