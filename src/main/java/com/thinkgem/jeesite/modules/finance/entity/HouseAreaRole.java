/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 房屋区域权限设置Entity
 * @author 夏天
 * @version 2014-07-30
 */
@Entity
@Table(name = "finance_housearearole")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HouseAreaRole extends IdEntity<HouseAreaRole> {
	
	private static final long serialVersionUID = 1L;
	private String name; 	// 名称
	
	private User roleperson;//'权限授予人',
	private String areas;//'授予的查看区域',
	

	public HouseAreaRole() {
		super();
	}

	public HouseAreaRole(String id){
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


	@OneToOne
	@JoinColumn(name="roleperson")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	public User getRoleperson() {
		return roleperson;
	}

	public void setRoleperson(User roleperson) {
		this.roleperson = roleperson;
	}

	public String getAreas() {
		return areas;
	}

	public void setAreas(String areas) {
		this.areas = areas;
	}
	
	
	
}


