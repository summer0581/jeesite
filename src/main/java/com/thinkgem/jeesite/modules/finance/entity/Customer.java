/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

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
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.OfficeType;
import com.thinkgem.jeesite.modules.sys.entity.Office;

/**
 * 客户信息Entity
 * @author 夏天
 * @version 2014-04-21
 */
@Entity
@Table(name = "finance_customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Customer extends IdEntity<Customer> {
	
	private static final long serialVersionUID = 1L;
	private String name; 	// 名称
	private String social_context;//'客户的社会背景',
	private String telephone;//'电话号码',
	private String job;//'工作',
	private String hobby;//'爱好',
	private String sex;//'性别',
	private Office office;//'所属部门',
	private String remark;//'备注',
	  


	public Customer() {
		super();
	}

	public Customer(String id){
		this();
		this.id = id;
	}
	
	@Length(min=1, max=64)
	@ExcelField(title="姓名", type=0, align=1, sort=10)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Length(min=0, max=500)
	@ExcelField(title="社会背景", type=0, align=1, sort=20)
	public String getSocial_context() {
		return social_context;
	}

	public void setSocial_context(String social_context) {
		this.social_context = social_context;
	}
	@Length(min=0, max=64)
	@ExcelField(title="电话号码", type=0, align=1, sort=30)
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	@Length(min=0, max=100)
	@ExcelField(title="工作", type=0, align=1, sort=40)
	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}
	@Length(min=0, max=64)
	@ExcelField(title="爱好", type=0, align=1, sort=50)
	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
	@Length(min=0, max=1)
	@ExcelField(title="性别", type=0, align=1, sort=60, dictType="sys_user_sex")
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	@ManyToOne
	@JoinColumn(name="office_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	@NotNull(message="房东(或租户)归属部门不能为空")
	@ExcelField(title="归属部门", type=0, align=1, sort=70, fieldType=OfficeType.class)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	@Length(min=0, max=255)
	@ExcelField(title="备注", type=0, align=1, sort=80)
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}


