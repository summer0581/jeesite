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
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 房屋明细Entity
 * @author 夏天
 * @version 2014-03-15
 */
@Entity
@Table(name = "finance_house")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class House extends IdEntity<House> {
	
	private static final long serialVersionUID = 1L;
	private String busi_id; 	// 业务编号
	private String name;     //地址
	private String houses;
	private String landlord_name;
	private String landlord_telephone;
	private String debit_card;
	private String tenant_name;
	private String tenant_telephone;
	
	private User team_leader;
	
	

	public House() {
		super();
	}

	public House(String id){
		this();
		this.id = id;
	}
	

	@Length(min=1, max=64)
	@ExcelField(title="序号", type=0, align=1, sort=10)
	public String getBusi_id() {
		return busi_id;
	}

	public void setBusi_id(String busi_id) {
		this.busi_id = busi_id;
	}
	@Length(min=1, max=255)
	@ExcelField(title="地址", type=0, align=1, sort=20)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Length(min=1, max=64)
	@ExcelField(title="楼盘", type=0, align=1, sort=30)
	public String getHouses() {
		return houses;
	}

	public void setHouses(String houses) {
		this.houses = houses;
	}
	@Length(min=0, max=64)
	@ExcelField(title="房东姓名", type=0, align=1, sort=40)
	public String getLandlord_name() {
		return landlord_name;
	}
	
	public void setLandlord_name(String landlord_name) {
		this.landlord_name = landlord_name;
	}
	@Length(min=0, max=64)
	@ExcelField(title="房东联系方式", type=0, align=1, sort=50)
	public String getLandlord_telephone() {
		return landlord_telephone;
	}

	public void setLandlord_telephone(String landlord_telephone) {
		this.landlord_telephone = landlord_telephone;
	}
	@Length(min=0, max=64)
	@ExcelField(title="转帐卡号", type=0, align=1, sort=60)
	public String getDebit_card() {
		return debit_card;
	}

	public void setDebit_card(String debit_card) {
		this.debit_card = debit_card;
	}
	@Length(min=0, max=64)
	@ExcelField(title="租户姓名", type=0, align=1, sort=70)
	public String getTenant_name() {
		return tenant_name;
	}

	public void setTenant_name(String tenant_name) {
		this.tenant_name = tenant_name;
	}
	@Length(min=0, max=64)
	@ExcelField(title="租户联系方式", type=0, align=1, sort=80)
	public String getTenant_telephone() {
		return tenant_telephone;
	}

	public void setTenant_telephone(String tenant_telephone) {
		this.tenant_telephone = tenant_telephone;
	}
/*	@Length(min=0, max=64)
	@ExcelField(title="组长", type=0, align=1, sort=90)
	public String getTeam_leader() {
		return team_leader;
	}

	public void setTeam_leader(String team_leader) {
		this.team_leader = team_leader;
	}
	*/

	@ManyToOne
	@JoinColumn(name="team_leader")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	@ExcelField(title="组长", type=0, align=1, sort=80)
	public User getTeam_leader() {
		return team_leader;
	}

	public void setTeam_leader(User team_leader) {
		this.team_leader = team_leader;
	}
	
	
	
}


