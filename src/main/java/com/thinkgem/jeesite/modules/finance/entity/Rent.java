/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.ListUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.HouseEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.UserEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 包租明细Entity
 * @author 夏天
 * @version 2014-03-15
 */
@Entity
@Table(name = "finance_rent")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Rent extends IdEntity<Rent> {
	
	/**
	 * 两个日子之间的占位符， - 符号
	 */
	public static String DATEHODlER = "-";
	
	private static final long serialVersionUID = 1L;
	private String name; 	//房屋地址
	private String business_num; // 业务编号
	private House house; //房屋
	
	private List<VacantPeriod> salesman_vacantperiods;//业务员空置期设置
	
	private List<VacantPeriod> landlord_vacantperiods;//房东空置期设置
	
	private List<RentMonth> rentinMonths;//承租记录
	
	private List<RentMonth> rentoutMonths;//出租记录
	

	public Rent() {
		super();
	}

	public Rent(String id){
		this();
		this.id = id;
	}
	
	public Rent(House house){
		this();
		this.house = house;
	}
	

	@Length(min=1, max=200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne
	@JoinColumn(name="house_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@NotNull
	@IndexedEmbedded
	@ExcelField(title="房屋地址", type=0, align=1, sort=10, fieldType=HouseEntity.class)
	public House getHouse() {
		return house;
	}

	public void setHouse(House house) {
		this.house = house;
		if(null != house){
			this.name = house.getName();
		}else{
			this.name = "";
		}
		
	}
	
	@ExcelField(title="编号", type=0, align=1, sort=15)
	public String getBusiness_num() {
		return business_num;
	}

	public void setBusiness_num(String business_num) {
		this.business_num = business_num;
	}

	@OneToMany(mappedBy="rent",cascade=CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@Where(clause="type='1' or type='3'")
	@OrderBy("sn")
	public List<VacantPeriod> getSalesman_vacantperiods() {
		return salesman_vacantperiods;
	}

	public void setSalesman_vacantperiods(List<VacantPeriod> salesman_vacantperiods) {
		this.salesman_vacantperiods = salesman_vacantperiods;
	}

	@OneToMany(mappedBy="rent",cascade=CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@Where(clause="type='2'")
	@OrderBy("sn")
	public List<VacantPeriod> getLandlord_vacantperiods() {
		return landlord_vacantperiods;
	}

	public void setLandlord_vacantperiods(List<VacantPeriod> landlord_vacantperiods) {
		this.landlord_vacantperiods = landlord_vacantperiods;
	}
	
	
	@OneToMany(mappedBy="rent",cascade=CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@Where(clause="infotype='rentin' and del_flag='"+DEL_FLAG_NORMAL+"'")
	@OrderBy("createDate desc")
	public List<RentMonth> getRentinMonths() {
		return rentinMonths;
	}

	public void setRentinMonths(List<RentMonth> rentinMonths) {
		this.rentinMonths = rentinMonths;
	}

	@OneToMany(mappedBy="rent",cascade=CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	@Where(clause="infotype='rentout' and del_flag='"+DEL_FLAG_NORMAL+"'")
	@OrderBy("createDate desc")
	public List<RentMonth> getRentoutMonths() {
		return rentoutMonths;
	}

	public void setRentoutMonths(List<RentMonth> rentoutMonths) {
		this.rentoutMonths = rentoutMonths;
	}
	
	/*以下是非持久变量*/
	@Transient
	public RentMonth getRentin()  throws Exception{
		if(null == rentinMonths){
			rentinMonths = new ArrayList<RentMonth>();
		}
		if(rentinMonths.size() == 0){
			rentinMonths.add(new RentMonth());
		}
		return rentinMonths.get(0);
	}
	@Transient
	public RentMonth getRentout()  throws Exception{
		if(null == rentoutMonths){
			rentoutMonths = new ArrayList<RentMonth>();
		}
		if(rentoutMonths.size() == 0){
			rentoutMonths.add(new RentMonth());
		}
		return rentoutMonths.get(0);
	}

	
	@Transient
	@ExcelField(title="承租人", type=0, align=1, sort=20, fieldType=UserEntity.class)
	public User getRentin_person() throws Exception  {
		return getRentin().getPerson();
	}

	public void setRentin_person(User rentin_person) throws Exception {
		getRentin().setPerson(rentin_person);
	}

	@Transient
	@ExcelField(title="承租付款方式", type=0, align=1, sort=30, dictType="finance_rent_paytype")
	public String getRentin_paytype() throws Exception {
		return getRentin().getPaytype();
	}

	public void setRentin_paytype(String rentin_paytype) throws Exception {
		getRentin().setPaytype(rentin_paytype);
	}

	
	@Transient
	@ExcelField(title="承租押金", type=0, align=1, sort=40)
	public String getRentin_deposit() throws Exception {
		return getRentin().getDeposit();
	}

	public void setRentin_deposit(String rentin_deposit) throws Exception {
		getRentin().setDeposit(rentin_deposit);
	}
	@Transient
	@ExcelField(title="承租月租金", type=0, align=1, sort=50)
	public String getRentin_rentmonth() throws Exception {
		return getRentin().getRentmonth();
	}

	public void setRentin_rentmonth(String rentin_rentmonth) throws Exception {
		getRentin().setRentmonth(rentin_rentmonth);
	}
	@Transient
	public Date getRentin_lastpaysdate() throws Exception {
		return getRentin().getLastpaysdate();
	}

	public void setRentin_lastpaysdate(Date rentin_lastpaysdate) throws Exception {
		getRentin().setLastpaysdate(rentin_lastpaysdate);
	}
	@Transient
	public Date getRentin_lastpayedate() throws Exception {
		return getRentin().getLastpayedate();
	}
	@Transient
	public Date getRentin_sdate() throws Exception {
		return getRentin().getSdate();
	}

	public void setRentin_sdate(Date rentin_sdate) throws Exception {
		getRentin().setSdate(rentin_sdate);
	}
	@Transient
	public Date getRentin_edate() throws Exception {
		return getRentin().getEdate();
	}
	
	public void setRentin_date(String rentin_date) throws Exception{
		String[] dates = StringUtils.splitWithTokenIndex(rentin_date, Rent.DATEHODlER, 3);
		getRentin().setSdate(DateUtils.parseDate(dates[0]));
		getRentin().setEdate(DateUtils.parseDate(dates[1]));
	}
	@ExcelField(title="承租日期", type=0, align=1, sort=60)
	@Transient
	public String getRentin_date() throws Exception{
		Date rentin_sdate = getRentin().getSdate();
		Date rentin_edate = getRentin().getEdate();
		return DateUtils.formatDate(rentin_sdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentin_edate,"yyyy-MM-dd");
	}

	public void setRentin_lastpaydate(String rentin_lastpaydate) throws Exception{
		String[] dates = StringUtils.splitWithTokenIndex(rentin_lastpaydate, Rent.DATEHODlER, 3);
		getRentin().setLastpaysdate(DateUtils.parseDate(dates[0]));
		getRentin().setLastpayedate(DateUtils.parseDate(dates[1]));
	}
	@ExcelField(title="承租上次付款日期", type=0, align=1, sort=70)
	@Transient
	public String getRentin_lastpaydate() throws Exception{
		Date rentin_lastpaysdate = getRentin().getLastpaysdate();
		Date rentin_lastpayedate = getRentin().getLastpayedate();
		return DateUtils.formatDate(rentin_lastpaysdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentin_lastpayedate,"yyyy-MM-dd");
	}

	public void setRentin_lastpayedate(Date rentin_lastpayedate) throws Exception {
		getRentin().setLastpayedate(rentin_lastpayedate);
	}
	@Transient
	@ExcelField(title="承租下次付款日期", type=0, align=1, sort=80)
	public Date getRentin_nextpaydate() throws Exception {
		return getRentin().getNextpaydate();
	}

	public void setRentin_nextpaydate(Date rentin_nextpaydate) throws Exception {
		getRentin().setNextpaydate(rentin_nextpaydate);
	}
	
	public void setRentout_date(String rentout_date) throws Exception{
		String[] dates = StringUtils.splitWithTokenIndex(rentout_date, Rent.DATEHODlER, 3);
		getRentout().setSdate(DateUtils.parseDate(dates[0]));
		getRentout().setEdate(DateUtils.parseDate(dates[1]));
	}
	@ExcelField(title="出租日期", type=0, align=1, sort=85)
	@Transient
	public String getRentout_date() throws Exception{
		Date rentout_sdate = getRentout().getSdate();
		Date rentout_edate = getRentout().getEdate();
		return DateUtils.formatDate(rentout_sdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentout_edate,"yyyy-MM-dd");
	}
	@Transient
	@ExcelField(title="出租押金", type=0, align=1, sort=90)
	public String getRentout_deposit() throws Exception {
		return getRentout().getDeposit();
	}

	public void setRentout_deposit(String rentout_deposit) throws Exception {
		getRentout().setDeposit(rentout_deposit);
	}
	@Transient
	@ExcelField(title="出租月租金", type=0, align=1, sort=100)
	public String getRentout_rentmonth() throws Exception {
		return getRentout().getRentmonth();
	}

	public void setRentout_rentmonth(String rentout_rentmonth) throws Exception {
		getRentout().setRentmonth(rentout_rentmonth);
	}
	@Transient
	public Date getRentout_lastpaysdate() throws Exception {
		return getRentout().getLastpaysdate();
	}

	public void setRentout_lastpaysdate(Date rentout_lastpaysdate) throws Exception {
		getRentout().setLastpaysdate(rentout_lastpaysdate);
	}
	@Transient
	public Date getRentout_lastpayedate() throws Exception {
		return getRentout().getLastpayedate();
	}

	public void setRentout_lastpayedate(Date rentout_lastpayedate) throws Exception {
		getRentout().setLastpayedate(rentout_lastpayedate);
	}
	
	public void setRentout_lastpaydate(String rentout_lastpaydate) throws Exception{
		String[] dates = StringUtils.splitWithTokenIndex(rentout_lastpaydate, Rent.DATEHODlER, 3);
		getRentout().setLastpaysdate(DateUtils.parseDate(dates[0]));
		getRentout().setLastpayedate(DateUtils.parseDate(dates[1]));
	}
	@ExcelField(title="出租上次付款日期", type=0, align=1, sort=110)
	@Transient
	public String getRentout_lastpaydate() throws Exception{
		Date rentout_lastpaysdate = getRentout().getLastpaysdate();
		Date rentout_lastpayedate = getRentout().getLastpayedate();
		return DateUtils.formatDate(rentout_lastpaysdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentout_lastpayedate,"yyyy-MM-dd");
	}
	
	@Transient
	@ExcelField(title="出租已收金额", type=0, align=1, sort=130)
	public String getRentout_amountreceived() throws Exception {
		return getRentout().getAmountreceived();
	}

	public void setRentout_amountreceived(String rentout_amountreceived) throws Exception {
		getRentout().setAmountreceived(rentout_amountreceived);
	}
	@Transient
	@ExcelField(title="出租下次收租日期", type=0, align=1, sort=140)
	public Date getRentout_nextpaydate() throws Exception {
		return getRentout().getNextpaydate();
	}

	public void setRentout_nextpaydate(Date rentout_nextpaydate) throws Exception {
		getRentout().setNextpaydate(rentout_nextpaydate);
	}
	@Transient
	@ExcelField(title="出租付款方式", type=0, align=1, sort=150, dictType="finance_rent_paytype")
	public String getRentout_paytype() throws Exception {
		return getRentout().getPaytype();
	}

	public void setRentout_paytype(String rentout_paytype) throws Exception {
		getRentout().setPaytype(rentout_paytype);
	}	
	@Transient
	@ExcelField(title="出租人", type=0, align=1, sort=160, fieldType=UserEntity.class)
	public User getRentout_person() throws Exception {
		return getRentout().getPerson();
	}

	public void setRentout_person(User rentout_person) throws Exception {
		getRentout().setPerson(rentout_person) ;
	}



	public void setRentin_edate(Date rentin_edate)  throws Exception{
		getRentin().setEdate(rentin_edate);
	}
	@Transient
	public Date getRentout_sdate()  throws Exception{
		return getRentout().getSdate();
	}

	public void setRentout_sdate(Date rentout_sdate) throws Exception {
		getRentout().setSdate(rentout_sdate);
	}
	@Transient
	public Date getRentout_edate() throws Exception {
		return getRentout().getEdate();
	}

	public void setRentout_edate(Date rentout_edate) throws Exception {
		getRentout().setEdate(rentout_edate);
	}

	public static void main(String[] args){
		//String date = "2013-01-04至2014-02-05";
		//String[] strarry = StringUtils.splitWithTokenIndex(date, "至", 3);
		Date d = DateUtils.parseDate("2013-1-3");
		System.out.println(11);
	}


	
	
}


