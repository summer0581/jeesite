/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CustomerEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.OfficeType;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.UserEntity;
import com.thinkgem.jeesite.modules.cms.utils.CmsUtils;
import com.thinkgem.jeesite.modules.sys.entity.Office;
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
	private int temp_index;//临时序号
	private String busi_id; 	// 业务编号
	private String name;     //地址
	private String houses;
	private Customer landlord;
	//private String landlord_telephone;
	private String debit_card;
	private Customer tenant;
	//private String tenant_telephone;
	private Office office;//所属部门
	private String image;//图片
	
	private String is_canrent;//'是否可租',
	private String is_cansale;//'是否可卖',
	private String area;//'区域',
	private String sale_price;//'价格',
	private String measure;//'面积',
	private String direction;//'朝向',
	private String age;//'年代',
	private String decorate;//'装修',
	private String is_needdeposit;//'是否需要下定金',
	private String is_xingyebank;//是否兴业银行
	private String receive_username;//收款户名
	private String receive_bank;//收款银行及营业网点
	private String is_samecity;//是否同城
	private String remit_address;//汇入地址
	private String house_source;//房屋来源
	
	
	private User team_leader;
	
	private Rent rent;
	private String rent_state;//出租状态
	
	public enum RentState{
		norentout,hascancelrent
	}
	
	

	public House() {
		super();
	}

	public House(String id){
		this();
		this.id = id;
	}
	

	public String getBusi_id() {
		return busi_id;
	}

	public void setBusi_id(String busi_id) {
		this.busi_id = busi_id;
	}
	
	@ExcelField(title="序号", type=1, align=1, sort=10,groups={1})
	@Transient
	public int getTemp_index() {
		return temp_index;
	}

	public void setTemp_index(int temp_index) {
		this.temp_index = temp_index;
	}

	@ExcelField(title="地址", type=0, align=1, sort=20,groups={1})
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	@ExcelField(title="承租租金", type=1, align=1, sort=25,groups={1})
	public String getRentin_rentmonth() throws Exception {
		return rent.getRentin_rentmonth();
	}
	
	@ExcelField(title="楼盘", type=0, align=1, sort=30)
	public String getHouses() {
		return houses;
	}

	public void setHouses(String houses) {
		this.houses = houses;
	}
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="landlord_name")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	@ExcelField(title="房东姓名", type=0, align=1, sort=40, fieldType=CustomerEntity.class)
	public Customer getLandlord() {
		return landlord;
	}
	
	public void setLandlord(Customer landlord) {
		this.landlord = landlord;
	}
	@ExcelField(title="房东联系方式", type=0, align=1, sort=50)
	@Transient
	public String getLandlord_telephone() {
		if(null != this.landlord)
			return this.landlord.getTelephone();
		else
			return "";
	}

	public void setLandlord_telephone(String landlord_telephone) {
		if(null != this.landlord)
			this.landlord.setTelephone(landlord_telephone);
	}
	@ExcelField(title="转帐卡号", type=0, align=1, sort=60)
	public String getDebit_card() {
		return debit_card;
	}

	public void setDebit_card(String debit_card) {
		this.debit_card = debit_card;
	}
	@OneToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="tenant_name")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	@ExcelField(title="租户姓名", type=0, align=1, sort=70, fieldType=CustomerEntity.class)
	public Customer getTenant() {
		return tenant;
	}

	public void setTenant(Customer tenant) {
		this.tenant = tenant;
	}
	
	@ExcelField(title="租户联系方式", type=0, align=1, sort=80)
	@Transient
	public String getTenant_telephone() {
		if(null != this.tenant){
			return this.tenant.getTelephone();
		}
		return "";
		
	}

	public void setTenant_telephone(String tenant_telephone) {
		if(null != this.tenant)
			this.tenant.setTelephone(tenant_telephone);
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
	@ExcelField(title="组长", type=0, align=1, sort=90, fieldType=UserEntity.class)
	public User getTeam_leader() {
		return team_leader;
	}

	public void setTeam_leader(User team_leader) {
		this.team_leader = team_leader;
	}

	@ManyToOne
	@JoinColumn(name="office_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	@ExcelField(title="所属部门", type=0, align=1, sort=100, fieldType=OfficeType.class)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = CmsUtils.formatImageSrcToDb(StringUtils.defaultIfBlank(image, ""));
	}
	
    @Transient
   	public String getImageSrc() {
        return CmsUtils.formatImageSrcToWeb(this.image);
   	}

	@OneToOne(mappedBy="house")
	@NotFound(action = NotFoundAction.IGNORE)
	public Rent getRent() {
		return rent;
	}

	public void setRent(Rent rent) {
		this.rent = rent;
	}
	@ExcelField(title="是否可租", type=0, align=1, sort=110)
	public String getIs_canrent() {
		return is_canrent;
	}

	public void setIs_canrent(String is_canrent) {
		this.is_canrent = is_canrent;
	}
	@ExcelField(title="是否可卖", type=0, align=1, sort=120)
	public String getIs_cansale() {
		return is_cansale;
	}

	public void setIs_cansale(String is_cansale) {
		this.is_cansale = is_cansale;
	}

	@ExcelField(title="区域", type=0, align=1, sort=130, dictType="house_area")
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	@ExcelField(title="价格", type=0, align=1, sort=140)
	public String getSale_price() {
		return sale_price;
	}

	public void setSale_price(String sale_price) {
		this.sale_price = sale_price;
	}

	@ExcelField(title="面积", type=0, align=1, sort=150)
	public String getMeasure() {
		return measure;
	}

	public void setMeasure(String measure) {
		this.measure = measure;
	}

	@ExcelField(title="朝向", type=0, align=1, sort=160)
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@ExcelField(title="年代", type=0, align=1, sort=170)
	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	@ExcelField(title="装修", type=0, align=1, sort=180)
	public String getDecorate() {
		return decorate;
	}

	public void setDecorate(String decorate) {
		this.decorate = decorate;
	}

	@ExcelField(title="是否需要定金", type=0, align=1, sort=190)
	public String getIs_needdeposit() {
		return is_needdeposit;
	}

	public void setIs_needdeposit(String is_needdeposit) {
		this.is_needdeposit = is_needdeposit;
	}
	@ExcelField(title="是否兴业银行", type=0, align=1, sort=200,groups={1})
	public String getIs_xingyebank() {
		return is_xingyebank;
	}

	public void setIs_xingyebank(String is_xingyebank) {
		this.is_xingyebank = is_xingyebank;
	}
	@ExcelField(title="收款户名", type=0, align=1, sort=210,groups={1})
	public String getReceive_username() {
		return receive_username;
	}

	public void setReceive_username(String receive_username) {
		this.receive_username = receive_username;
	}
	@ExcelField(title="收款银行及营业网点", type=0, align=1, sort=220,groups={1})
	public String getReceive_bank() {
		return receive_bank;
	}

	public void setReceive_bank(String receive_bank) {
		this.receive_bank = receive_bank;
	}
	@ExcelField(title="是否同城", type=0, align=1, sort=230,groups={1})
	public String getIs_samecity() {
		return is_samecity;
	}

	public void setIs_samecity(String is_samecity) {
		this.is_samecity = is_samecity;
	}
	@ExcelField(title="汇入地址", type=0, align=1, sort=240,groups={1})
	public String getRemit_address() {
		return remit_address;
	}

	public void setRemit_address(String remit_address) {
		this.remit_address = remit_address;
	}
	@Transient
	@ExcelField(title="应付金额", type=1, align=1, sort=245,groups={1})
	public String getRent_nextshouldpay() throws Exception {
		if(StringUtils.isNotBlank(rent.getRentin().getNextshouldamount())){
			return rent.getRentin().getNextshouldamount();
		}else{
			return rent.getRentin().getRentmonth();
		}
	}
	@Transient
	@ExcelField(title="应付备注", type=1, align=1, sort=247,groups={1})
	public String getRent_remarks() throws Exception {
		return rent.getRentin().getRemarks();
	}
	@ExcelField(title="房屋来源", type=0, align=1, sort=250)
	public String getHouse_source() {
		return house_source;
	}

	public void setHouse_source(String house_source) {
		this.house_source = house_source;
	}
	@Transient
	public String getRent_state() {
		return rent_state;
	}

	public void setRent_state(String rent_state) {
		this.rent_state = rent_state;
	}
	
}


