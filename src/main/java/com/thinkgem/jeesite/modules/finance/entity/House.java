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
import org.hibernate.annotations.Where;
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
	private String prop_certno;//产权证号',
	private String land_certno;//国土证号',
	private String house_elec;//家电家具',
	private String water_num;//水',
	private String elec_num;//电',
	private String house_layout;//户型',
	private String wy_useful;//物业用途',
	private String paytype;//付款方式',
	private String structure;//结构',
	private String arrond_environ;//周边环境',
	private String housing_facilities;//房屋设施',
	private String areadescribe;//面积描述',
	private String traffic_condition;//交通情况',
	private String entrust_store;//委托门店',
	private String regist_store;//登记门店',
	
	
	private User rentin_user;//租进业务员
	private User rentout_user;//租出业务员
	
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
	
	
	@Transient
	public int getTemp_index() {
		return temp_index;
	}

	public void setTemp_index(int temp_index) {
		this.temp_index = temp_index;
	}

	@ExcelField(title="地址", type=0, align=1, sort=20)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	@ExcelField(title="承租租金", type=1, align=1, sort=25)
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
	@JoinColumn(name="rentin_user")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	@ExcelField(title="租进业务员", type=0, align=1, sort=90, fieldType=UserEntity.class)
	public User getRentin_user() {
		return rentin_user;
	}

	public void setRentin_user(User rentin_user) {
		this.rentin_user = rentin_user;
	}
	
	@ManyToOne
	@JoinColumn(name="rentout_user")
	@NotFound(action = NotFoundAction.IGNORE)
	@IndexedEmbedded
	@ExcelField(title="租出业务员", type=0, align=1, sort=100, fieldType=UserEntity.class)
	public User getRentout_user() {
		return rentout_user;
	}

	public void setRentout_user(User rentout_user) {
		this.rentout_user = rentout_user;
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
	@ExcelField(title="是否可租", type=0, align=1, sort=110, dictType="yes_no")
	public String getIs_canrent() {
		return is_canrent;
	}

	public void setIs_canrent(String is_canrent) {
		this.is_canrent = is_canrent;
	}
	@ExcelField(title="是否可卖", type=0, align=1, sort=120, dictType="yes_no")
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

	@ExcelField(title="是否需要定金", type=0, align=1, sort=190, dictType="yes_no")
	public String getIs_needdeposit() {
		return is_needdeposit;
	}

	public void setIs_needdeposit(String is_needdeposit) {
		this.is_needdeposit = is_needdeposit;
	}
	@ExcelField(title="是否兴业银行", type=0, align=1, sort=200,groups={1}, dictType="yes_no")
	public String getIs_xingyebank() {
		return is_xingyebank;
	}

	public void setIs_xingyebank(String is_xingyebank) {
		this.is_xingyebank = is_xingyebank;
	}
	@ExcelField(title="收款户名", type=0, align=1, sort=210)
	public String getReceive_username() {
		return receive_username;
	}

	public void setReceive_username(String receive_username) {
		this.receive_username = receive_username;
	}
	@ExcelField(title="收款银行及营业网点", type=0, align=1, sort=220)
	public String getReceive_bank() {
		return receive_bank;
	}

	public void setReceive_bank(String receive_bank) {
		this.receive_bank = receive_bank;
	}
	@ExcelField(title="是否同城", type=0, align=1, sort=230, dictType="yes_no")
	public String getIs_samecity() {
		return is_samecity;
	}

	public void setIs_samecity(String is_samecity) {
		this.is_samecity = is_samecity;
	}
	@ExcelField(title="汇入地址", type=0, align=1, sort=240)
	public String getRemit_address() {
		return remit_address;
	}

	public void setRemit_address(String remit_address) {
		this.remit_address = remit_address;
	}
	@Transient
	@ExcelField(title="应付金额", type=1, align=1, sort=245)
	public String getRent_nextshouldpay() throws Exception {
		if(StringUtils.isNotBlank(rent.getRentin().getNextshouldamount())){
			return rent.getRentin().getNextshouldamount();
		}else{
			return rent.getRentin().getRentmonth();
		}
	}
	@Transient
	@ExcelField(title="应付备注", type=1, align=1, sort=247)
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
	
	@ExcelField(title="产权证号", type=0, align=1, sort=260)
	public String getProp_certno() {
		return prop_certno;
	}

	public void setProp_certno(String prop_certno) {
		this.prop_certno = prop_certno;
	}
	@ExcelField(title="国土证号", type=0, align=1, sort=270)
	public String getLand_certno() {
		return land_certno;
	}

	public void setLand_certno(String land_certno) {
		this.land_certno = land_certno;
	}
	@ExcelField(title="家电家具", type=0, align=1, sort=280)
	public String getHouse_elec() {
		return house_elec;
	}

	public void setHouse_elec(String house_elec) {
		this.house_elec = house_elec;
	}
	@ExcelField(title="水", type=0, align=1, sort=290)
	public String getWater_num() {
		return water_num;
	}

	public void setWater_num(String water_num) {
		this.water_num = water_num;
	}
	@ExcelField(title="电", type=0, align=1, sort=300)
	public String getElec_num() {
		return elec_num;
	}

	public void setElec_num(String elec_num) {
		this.elec_num = elec_num;
	}
	@ExcelField(title="户型", type=0, align=1, sort=310)
	public String getHouse_layout() {
		return house_layout;
	}

	public void setHouse_layout(String house_layout) {
		this.house_layout = house_layout;
	}
	@ExcelField(title="物业用途", type=0, align=1, sort=320)
	public String getWy_useful() {
		return wy_useful;
	}

	public void setWy_useful(String wy_useful) {
		this.wy_useful = wy_useful;
	}
	@ExcelField(title="付款方式", type=0, align=1, sort=330)
	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}
	@ExcelField(title="结构", type=0, align=1, sort=340)
	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}
	@ExcelField(title="周边环境", type=0, align=1, sort=350)
	public String getArrond_environ() {
		return arrond_environ;
	}

	public void setArrond_environ(String arrond_environ) {
		this.arrond_environ = arrond_environ;
	}
	@ExcelField(title="房屋设置", type=0, align=1, sort=360)
	public String getHousing_facilities() {
		return housing_facilities;
	}

	public void setHousing_facilities(String housing_facilities) {
		this.housing_facilities = housing_facilities;
	}
	@ExcelField(title="面积描述", type=0, align=1, sort=370)
	public String getAreadescribe() {
		return areadescribe;
	}

	public void setAreadescribe(String areadescribe) {
		this.areadescribe = areadescribe;
	}
	@ExcelField(title="交通情况", type=0, align=1, sort=380)
	public String getTraffic_condition() {
		return traffic_condition;
	}

	public void setTraffic_condition(String traffic_condition) {
		this.traffic_condition = traffic_condition;
	}
	@ExcelField(title="委托门店", type=0, align=1, sort=390)
	public String getEntrust_store() {
		return entrust_store;
	}

	public void setEntrust_store(String entrust_store) {
		this.entrust_store = entrust_store;
	}
	@ExcelField(title="登记门店", type=0, align=1, sort=400)
	public String getRegist_store() {
		return regist_store;
	}

	public void setRegist_store(String regist_store) {
		this.regist_store = regist_store;
	}

	@Transient
	public String getRent_state() {
		return rent_state;
	}

	public void setRent_state(String rent_state) {
		this.rent_state = rent_state;
	}
	
	
	
}


