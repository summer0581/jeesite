/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.IdEntity;

/**
 * 房屋跟进意见Entity
 * @author 夏天
 * @version 2014-08-07
 */
@Entity
@Table(name = "finance_houseidea")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HouseIdea extends IdEntity<HouseIdea> {
	
	private static final long serialVersionUID = 1L;
	private String name; 	// 名称
	
	private House house;//房屋
	private String content;//跟进内容

	public HouseIdea() {
		super();
	}

	public HouseIdea(String id){
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
	@JoinColumn(name="house")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}


