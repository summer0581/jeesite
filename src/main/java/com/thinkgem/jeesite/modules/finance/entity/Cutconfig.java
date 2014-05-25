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
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 包租提成设置Entity
 * @author 夏天
 * @version 2014-05-18
 */
@Entity
@Table(name = "finance_cutconfig")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Cutconfig extends IdEntity<Cutconfig> {
	
	private static final long serialVersionUID = 1L;
	private String name; 	// 名称
	private String	cut_code;//提成标示符
	private String	cut_type;//提成类别
	private String	person;//人员
	private String	cut_num	;//提成系数

	public Cutconfig() {
		super();
	}

	public Cutconfig(String id){
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

	public String getCut_code() {
		return cut_code;
	}

	public void setCut_code(String cut_code) {
		this.cut_code = cut_code;
	}

	public String getCut_type() {
		return cut_type;
	}

	public void setCut_type(String cut_type) {
		this.cut_type = cut_type;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getCut_num() {
		return cut_num;
	}

	public void setCut_num(String cut_num) {
		this.cut_num = cut_num;
	}
	
	
	
}


