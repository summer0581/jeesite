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
 * 空置期提成Entity
 * @author 夏天
 * @version 2014-04-13
 */
@Entity
@Table(name = "finance_vacantPeriod")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class VacantPeriod extends IdEntity<VacantPeriod> {
	
	private static final long serialVersionUID = 1L;
	private String name; 	// 名称

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
	
}


