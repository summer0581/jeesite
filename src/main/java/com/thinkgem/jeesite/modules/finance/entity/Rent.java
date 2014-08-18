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

import com.thinkgem.jeesite.common.persistence.IdEntity;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CustomerEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.CutconfigEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.HouseEntity;
import com.thinkgem.jeesite.common.utils.excel.fieldtype.UserEntity;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;

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
	/**
	 * 数组字符串之间的占位符
	 */
	public static String STRINGHODlER = ",";
	
	private static final long serialVersionUID = 1L;
	private String name; 	//房屋地址
	private int business_num; // 业务编号
	private House house; //房屋
	
	private List<VacantPeriod> salesman_vacantperiods;//业务员空置期设置
	
	private List<VacantPeriod> landlord_vacantperiods;//房东空置期设置
	
	private List<RentMonth> rentinMonths;//承租记录
	
	private List<RentMonth> rentoutMonths;//出租记录
	
	private Date landlord_vacantPeriodsdate;//房东空置期最近起始时间
	private Date landlord_vacantPeriodedate;//房东空置期最近结束时间
	

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
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne(cascade=CascadeType.ALL)
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
	
	public int getBusiness_num() {
		return business_num;
	}

	public void setBusiness_num(int business_num) {
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
	@ExcelField(title="编号", type=0, align=1, sort=15)
	public String getBusiness_num_str() {
		return String.valueOf(business_num);
	}

	public void setBusiness_num_str(String business_num) {
		this.business_num = Integer.parseInt(business_num);
	}
	
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
	@ExcelField(title="租进业务员", type=0, align=1, sort=20, fieldType=UserEntity.class)
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
		if(null != rentin_sdate && null != rentin_edate){
			return DateUtils.formatDate(rentin_sdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentin_edate,"yyyy-MM-dd");
		}else{
			return "";
		}

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
		if(null != rentin_lastpaysdate && null != rentin_lastpayedate){
			return DateUtils.formatDate(rentin_lastpaysdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentin_lastpayedate,"yyyy-MM-dd");
		}else{
			return "";
		}
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
	
	@Transient
	@ExcelField(title="承租中介费", type=0, align=1, sort=85)
	public String getRentin_agancyfee() throws Exception {
		return getRentin().getAgencyfee();
	}

	public void setRentin_agancyfee(String agencyfee) throws Exception {
		getRentin().setAgencyfee(agencyfee);
	}
	
	@Transient
	@ExcelField(title="承租业务经理", type=0, align=1, sort=90, fieldType=UserEntity.class)
	public User getRentin_busi_manager() throws Exception {
		return getRentin().getBusi_manager();
	}

	public void setRentin_busi_manager(User busi_manager) throws Exception {
		getRentin().setBusi_manager(busi_manager);
	}
	
	@Transient
	@ExcelField(title="承租业务部长", type=0, align=1, sort=95, fieldType=UserEntity.class)
	public User getRentin_busi_departleader() throws Exception {
		return getRentin().getBusi_departleader();
	}

	public void setRentin_busi_departleader(User busi_departleader) throws Exception {
		getRentin().setBusi_departleader(busi_departleader);
	}
	
	@Transient
	@ExcelField(title="承租业务组长", type=0, align=1, sort=100, fieldType=UserEntity.class)
	public User getRentin_busi_teamleader() throws Exception {
		return getRentin().getBusi_teamleader();
	}

	public void setRentin_busi_teamleader(User busi_teamleader) throws Exception {
		getRentin().setBusi_teamleader(busi_teamleader);
	}
	
	@Transient
	public String getRentin_Cut_vacantperiodtype() throws Exception{
		return getRentin().getCut_vacantperiodtype();
	}
	
	public void setRentin_Cut_vacantperiodtype(String cut_vacantperiodtype) throws Exception {
		getRentin().setCut_vacantperiodtype(cut_vacantperiodtype);
	}
	
	@Transient
	public String getRentin_Cut_businesssaletype() throws Exception{
		return getRentin().getCut_businesssaletype();
	}
	
	public void setRentin_Cut_businesssaletype(String cut_businesssaletype) throws Exception {
		getRentin().setCut_businesssaletype(cut_businesssaletype);
	}
	
	@Transient
	@ExcelField(title="承租下次应付金额", type=0, align=1, sort=115)
	public String getRentin_nextshouldamount() throws Exception {
		return getRentin().getNextshouldamount();
	}

	public void setRentin_nextshouldamount(String nextshouldamount) throws Exception {
		getRentin().setNextshouldamount(nextshouldamount);
	}
	@Transient
	@ExcelField(title="承租下次应付备注", type=0, align=1, sort=117)
	public String getRentin_nextshouldremark() throws Exception {
		return getRentin().getNextshouldremark();
	}

	public void setRentin_nextshouldremark(String nextshouldremark) throws Exception {
		getRentin().setNextshouldremark(nextshouldremark);
	}
	@Transient
	public String getRentin_firstmonth_num() throws Exception{
		return getRentin().getFirstmonth_num();
	}
	
	public void setRentin_firstmonth_num(String firstmonth_num) throws Exception {
		getRentin().setFirstmonth_num(firstmonth_num);
	}
	
	@Transient
	@ExcelField(title="承租备注", type=0, align=1, sort=125)
	public String getRentin_remarks() throws Exception{
		return getRentin().getRemarks();
	}
	
	public void setRentin_remarks(String remarks) throws Exception {
		getRentin().setRemarks(remarks);
	}

	
	public void setRentout_date(String rentout_date) throws Exception{
		String[] dates = StringUtils.splitWithTokenIndex(rentout_date, Rent.DATEHODlER, 3);
		getRentout().setSdate(DateUtils.parseDate(dates[0]));
		getRentout().setEdate(DateUtils.parseDate(dates[1]));
	}
	@ExcelField(title="出租日期", type=0, align=1, sort=185)
	@Transient
	public String getRentout_date() throws Exception{
		Date rentout_sdate = getRentout().getSdate();
		Date rentout_edate = getRentout().getEdate();
		if(null != rentout_sdate && null != rentout_edate){
			return DateUtils.formatDate(rentout_sdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentout_edate,"yyyy-MM-dd");
		}else{
			return "";
		}
	}
	@Transient
	@ExcelField(title="出租押金", type=0, align=1, sort=190)
	public String getRentout_deposit() throws Exception {
		return getRentout().getDeposit();
	}

	public void setRentout_deposit(String rentout_deposit) throws Exception {
		getRentout().setDeposit(rentout_deposit);
	}
	@Transient
	@ExcelField(title="出租月租金", type=0, align=1, sort=200)
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
	@ExcelField(title="出租上次付款日期", type=0, align=1, sort=210)
	@Transient
	public String getRentout_lastpaydate() throws Exception{
		Date rentout_lastpaysdate = getRentout().getLastpaysdate();
		Date rentout_lastpayedate = getRentout().getLastpayedate();
		return DateUtils.formatDate(rentout_lastpaysdate,"yyyy-MM-dd")+Rent.DATEHODlER+DateUtils.formatDate(rentout_lastpayedate,"yyyy-MM-dd");
	}
	
	@Transient
	@ExcelField(title="出租已收金额", type=0, align=1, sort=230)
	public String getRentout_amountreceived() throws Exception {
		return getRentout().getAmountreceived();
	}

	public void setRentout_amountreceived(String rentout_amountreceived) throws Exception {
		getRentout().setAmountreceived(rentout_amountreceived);
	}
	@Transient
	@ExcelField(title="出租下次收租日期", type=0, align=1, sort=240)
	public Date getRentout_nextpaydate() throws Exception {
		return getRentout().getNextpaydate();
	}

	public void setRentout_nextpaydate(Date rentout_nextpaydate) throws Exception {
		getRentout().setNextpaydate(rentout_nextpaydate);
	}
	@Transient
	@ExcelField(title="出租付款方式", type=0, align=1, sort=250, dictType="finance_rent_paytype")
	public String getRentout_paytype() throws Exception {
		return getRentout().getPaytype();
	}

	public void setRentout_paytype(String rentout_paytype) throws Exception {
		getRentout().setPaytype(rentout_paytype);
	}	
	@Transient
	@ExcelField(title="出租人", type=0, align=1, sort=260, fieldType=UserEntity.class)
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
	
	@Transient
	@ExcelField(title="出租中介费", type=0, align=1, sort=270)
	public String getRentout_agancyfee() throws Exception {
		return getRentout().getAgencyfee();
	}

	public void setRentout_agancyfee(String agencyfee) throws Exception {
		getRentout().setAgencyfee(agencyfee);
	}
	@Transient
	@ExcelField(title="出租业务经理", type=0, align=1, sort=275, fieldType=UserEntity.class)
	public User getRentout_busi_manager() throws Exception {
		return getRentout().getBusi_manager();
	}

	public void setRentout_busi_manager(User busi_manager) throws Exception {
		getRentout().setBusi_manager(busi_manager);
	}
	
	@Transient
	@ExcelField(title="出租业务部长", type=0, align=1, sort=280, fieldType=UserEntity.class)
	public User getRentout_busi_departleader() throws Exception {
		return getRentout().getBusi_departleader();
	}

	public void setRentout_busi_departleader(User busi_departleader) throws Exception {
		getRentout().setBusi_departleader(busi_departleader);
	}
	
	@Transient
	@ExcelField(title="出租业务组长", type=0, align=1, sort=285, fieldType=UserEntity.class)
	public User getRentout_busi_teamleader() throws Exception {
		return getRentout().getBusi_teamleader();
	}

	public void setRentout_busi_teamleader(User busi_teamleader) throws Exception {
		getRentout().setBusi_teamleader(busi_teamleader);
	}
		
	@Transient
	@ExcelField(title="出租第几个头期", type=0, align=1, sort=290)
	public String getRentout_firstmonth_num() throws Exception{
		return getRentout().getFirstmonth_num();
	}
	
	public void setRentout_firstmonth_num(String firstmonth_num) throws Exception {
		getRentout().setFirstmonth_num(firstmonth_num);
	}
	
	@Transient
	@ExcelField(title="出租空置期方案设置", type=0, align=1, sort=292, fieldType=CutconfigEntity.class)
	public String getRentout_Cut_vacantperiodtype() throws Exception{
		return getRentout().getCut_vacantperiodtype();
	}
	
	public void setRentout_Cut_vacantperiodtype(String cut_vacantperiodtype) throws Exception {
		getRentout().setCut_vacantperiodtype(cut_vacantperiodtype);
	}
	
	@Transient
	@ExcelField(title="出租业绩提成方案设置", type=0, align=1, sort=293, fieldType=CutconfigEntity.class)
	public String getRentout_Cut_businesssaletype() throws Exception{
		return getRentout().getCut_businesssaletype();
	}
	
	public void setRentout_Cut_businesssaletype(String cut_businesssaletype) throws Exception {
		getRentout().setCut_businesssaletype(cut_businesssaletype);
	}
	
	@Transient
	@ExcelField(title="出租下次应收金额", type=0, align=1, sort=295)
	public String getRentout_nextshouldamount() throws Exception {
		return getRentout().getNextshouldamount();
	}

	public void setRentout_nextshouldamount(String nextshouldamount) throws Exception {
		getRentout().setNextshouldamount(nextshouldamount);
	}
	@Transient
	@ExcelField(title="出租下次应收备注", type=0, align=1, sort=297)
	public String getRentout_nextshouldremark() throws Exception {
		return getRentout().getNextshouldremark();
	}

	public void setRentout_nextshouldremark(String nextshouldremark) throws Exception {
		getRentout().setNextshouldremark(nextshouldremark);
	}

	@Transient
	@ExcelField(title="出租提前退租时间", type=0, align=1, sort=300)
	public Date getRentout_cancelrentdate() throws Exception {
		return getRentout().getCancelrentdate();
	}

	public void setRentout_cancelrentdate(Date cancelrentdate) throws Exception {
		getRentout().setCancelrentdate(cancelrentdate);
	}	
	
	@Transient
	@ExcelField(title="出租提前退租备注", type=0, align=1, sort=303)
	public String getRentout_cancelrentremark() throws Exception {
		return getRentout().getCancelrentremark();
	}

	public void setRentout_cancelrentremark(String cancelrentremark) throws Exception {
		getRentout().setCancelrentremark(cancelrentremark);
	}	

	
	@Transient
	@ExcelField(title="业务员空置期", type=0, align=1, sort=305)
	public String getBusisaler_vacantPeriods() throws Exception{
		StringBuffer vacantPeriods = new StringBuffer();
		if(null != salesman_vacantperiods){
			for(VacantPeriod vp : salesman_vacantperiods){
				if(null != vp.getSdate() && null != vp.getEdate() && vp.getType().equals(DictUtils.getDictValue("分配给业务员的空置期", "finance_vacnatperiod_type", "1"))){
					vacantPeriods.append(DateUtils.formatDate(vp.getSdate(), "yyyy-MM-dd")+DATEHODlER+DateUtils.formatDate(vp.getEdate(), "yyyy-MM-dd")+STRINGHODlER);
				}
			}
		}
		if(vacantPeriods.length() > 0){
			return vacantPeriods.substring(0,vacantPeriods.length()-1);
		}else{
			return "";
		}
	}

	/**
	 * 空置期用逗号隔开，如 30,30,30 或 30,2014-05-01-2014-06-01,30
	 * @param vacantPeriods
	 * @throws Exception
	 */
	public void setBusisaler_vacantPeriods(String vacantPeriods) throws Exception {
		String[] vacantPeriodArry = vacantPeriods.split(STRINGHODlER);
		salesman_vacantperiods = initVacantPeriodList(vacantPeriodArry,DictUtils.getDictValue("分配给业务员的空置期", "finance_vacnatperiod_type", "1"),salesman_vacantperiods);
	}
	
	@Transient
	@ExcelField(title="内部无提成空置期", type=0, align=1, sort=310)
	public String getNocut_vacantPeriods() throws Exception{
		StringBuffer vacantPeriods = new StringBuffer();
		if(null != salesman_vacantperiods){
			for(VacantPeriod vp : salesman_vacantperiods){
				if(null != vp.getSdate() && null != vp.getEdate() && vp.getType().equals(DictUtils.getDictValue("公司内部给出的无提成的空置期", "finance_vacnatperiod_type", "1"))){
					vacantPeriods.append(DateUtils.formatDate(vp.getSdate(), "yyyy-MM-dd")+DATEHODlER+DateUtils.formatDate(vp.getEdate(), "yyyy-MM-dd")+STRINGHODlER);
				}
			}
		}
		if(vacantPeriods.length() > 0){
			return vacantPeriods.substring(0,vacantPeriods.length()-1);
		}else{
			return "";
		}
	}
	
	public void setNocut_vacantPeriods(String vacantPeriods) throws Exception {
		String[] vacantPeriodArry = vacantPeriods.split(STRINGHODlER);
		salesman_vacantperiods = initVacantPeriodList(vacantPeriodArry,DictUtils.getDictValue("公司内部给出的无提成的空置期", "finance_vacnatperiod_type", "1"),salesman_vacantperiods);
	}
	
	@Transient
	@ExcelField(title="房东空置期", type=0, align=1, sort=315)
	public String getLandlord_vacantPeriods() throws Exception{
		StringBuffer vacantPeriods = new StringBuffer();
		if(null != landlord_vacantperiods){
			for(VacantPeriod vp : landlord_vacantperiods){
				if(null != vp.getSdate() && null != vp.getEdate() && vp.getType().equals(DictUtils.getDictValue("房东指定的空置期", "finance_vacnatperiod_type", "1"))){
					vacantPeriods.append(DateUtils.formatDate(vp.getSdate(), "yyyy-MM-dd")+DATEHODlER+DateUtils.formatDate(vp.getEdate(), "yyyy-MM-dd")+STRINGHODlER);
				}
			}
		}
		if(vacantPeriods.length() > 0){
			return vacantPeriods.substring(0,vacantPeriods.length()-1);
		}else{
			return "";
		}
		
	}
	
	public void setLandlord_vacantPeriods(String vacantPeriods) throws Exception {
		String[] vacantPeriodArry = vacantPeriods.split(STRINGHODlER);
		landlord_vacantperiods = initVacantPeriodList(vacantPeriodArry,DictUtils.getDictValue("房东指定的空置期", "finance_vacnatperiod_type", "1"),landlord_vacantperiods);
	}
	
	@Transient
	@ExcelField(title="出租备注", type=0, align=1, sort=303)
	public String getRentout_remarks() throws Exception{
		return getRentout().getRemarks();
	}
	
	public void setRentout_remarks(String remarks) throws Exception {
		getRentout().setRemarks(remarks);
	}

	@Transient
	@ExcelField(title="房东姓名", type=0, align=1, sort=405 ,fieldType= CustomerEntity.class)
	public Customer getLandlord() throws Exception {
		return house.getLandlord();
	}

	public void setLandlord(Customer landlord) throws Exception {
		house.setLandlord(landlord);
	}

	@Transient
	@ExcelField(title="房东联系方式", type=0, align=1, sort=410 )
	public String getLandlord_telephone() throws Exception {
		return house.getLandlord_telephone();
	}

	public void setLandlord_telephone(String landlord_telephone) throws Exception {
		house.setLandlord_telephone(landlord_telephone);
	}

	@Transient
	@ExcelField(title="租户姓名", type=0, align=1, sort=415 ,fieldType= CustomerEntity.class)
	public Customer getTenant() throws Exception {
		return house.getTenant();
	}

	public void setTenant(Customer tenant) throws Exception {
		house.setTenant(tenant);
	}

	@Transient
	@ExcelField(title="租户联系方式", type=0, align=1, sort=420 )
	public String getTenant_telephone() throws Exception {
		return house.getTenant_telephone();
	}

	public void setTenant_telephone(String tenant_telephone) throws Exception {
		house.setTenant_telephone(tenant_telephone);
	}
	
	@Transient
	@ExcelField(title="租进业务员业绩提成固定值", type=0, align=1, sort=430 )
	public String getRentin_personfixedcut() throws Exception {
		return getRentin().getPerson_fixedcut();
	}

	public void setRentin_personfixedcut(String rentin_personfixedcut) throws Exception {
		getRentin().setPerson_fixedcut(rentin_personfixedcut);
	}
	
	@Transient
	@ExcelField(title="租出业务员业绩提成固定值", type=0, align=1, sort=440 )
	public String getRentout_personfixedcut() throws Exception {
		return getRentout().getPerson_fixedcut();
	}

	public void setRentout_personfixedcut(String rentout_personfixedcut) throws Exception {
		getRentout().setPerson_fixedcut(rentout_personfixedcut);
	}
	
	@Transient
	@ExcelField(title="经理业绩提成固定值", type=0, align=1, sort=450 )
	public String getRentin_managerfixedcut() throws Exception {
		return getRentin().getManager_fixedcut();
	}

	public void setRentin_managerfixedcut(String rentin_managerfixedcut) throws Exception {
		getRentin().setManager_fixedcut(rentin_managerfixedcut);
	}
	
	@Transient
	@ExcelField(title="部长业绩提成固定值", type=0, align=1, sort=460 )
	public String getRentin_departerfixedcut() throws Exception {
		return getRentin().getDeparter_fixedcut();
	}

	public void setRentin_departerfixedcut(String rentin_departerfixedcut) throws Exception {
		getRentin().setDeparter_fixedcut(rentin_departerfixedcut);
	}
	
	@Transient
	@ExcelField(title="组长业绩提成固定值", type=0, align=1, sort=470 )
	public String getRentin_teamleaderfixedcut() throws Exception {
		return getRentin().getTeamleader_fixedcut();
	}

	public void setRentin_teamleaderfixedcut(String rentin_teamleaderfixedcut) throws Exception {
		getRentin().setTeamleader_fixedcut(rentin_teamleaderfixedcut);
	}

	
	@Transient
	public Date getLandlord_vacantPeriodsdate() {
		return landlord_vacantPeriodsdate;
	}

	public void setLandlord_vacantPeriodsdate(Date landlord_vacantPeriodsdate) {
		this.landlord_vacantPeriodsdate = landlord_vacantPeriodsdate;
	}


	@Transient
	public Date getLandlord_vacantPeriodedate() {
		return landlord_vacantPeriodedate;
	}
	public void setLandlord_vacantPeriodedate(Date landlord_vacantPeriodedate) {
		this.landlord_vacantPeriodedate = landlord_vacantPeriodedate;
	}
	
	/**
	 * 根据传入的空置期数组，塞进指定的空置期集合
	 * @param vacantPeriodArry
	 * @param type
	 * @param vacantPeriodList
	 * @return
	 * @throws Exception
	 */
	private List<VacantPeriod> initVacantPeriodList(String[] vacantPeriodArry,String type,List<VacantPeriod> vacantPeriodList) throws Exception{
		if(null == vacantPeriodList){
			vacantPeriodList = new ArrayList<VacantPeriod>();
		}
		for(int i = 0 ; i < vacantPeriodArry.length; i++){
			if(StringUtils.isBlank(vacantPeriodArry[i])){
				continue;
			}
			VacantPeriod vp = new VacantPeriod();
			if(StringUtils.isNumeric(vacantPeriodArry[i])){//如果是数字
				vp.setSdate(DateUtils.addYears(getRentin_sdate(), i));
				vp.setEdate(DateUtils.addDays(DateUtils.addYears(getRentin_sdate(), i), Integer.valueOf(vacantPeriodArry[i])-1));
			}
			if(vacantPeriodArry[i].indexOf(DATEHODlER) != -1 ){//如果是日期格式
				String[] dates = StringUtils.splitWithTokenIndex(vacantPeriodArry[i], Rent.DATEHODlER, 3);
				if(null != dates && dates.length == 2 ){
					vp.setSdate(DateUtils.parseDate(dates[0]));
					vp.setEdate(DateUtils.parseDate(dates[1]));
				}
			}
			vp.setSn(String.valueOf(vacantPeriodList.size()+1));
			vp.setType(type);
			vacantPeriodList.add(vp);
		}
		return vacantPeriodList;
	}

	
	private String busisaler_vacantPeriodsTemp = "";
	private String landlord_vacantPeriodsTemp = "";
	
	@Transient
	public String getBusisaler_vacantPeriodsTemp() throws Exception{
		return busisaler_vacantPeriodsTemp;
	}

	/**
	 * 空置期临时变量，必须要建这个，不然方法设置的顺序会导致报错用逗号隔开，如 30,30,30 或 30,2014-05-01-2014-06-01,30
	 * @param vacantPeriods
	 * @throws Exception
	 */
	public void setBusisaler_vacantPeriodsTemp(String vacantPeriods) throws Exception {
		busisaler_vacantPeriodsTemp = vacantPeriods;
	}

	@Transient
	public String getLandlord_vacantPeriodsTemp() throws Exception{
		return landlord_vacantPeriodsTemp;
	}
	
	public void setLandlord_vacantPeriodsTemp(String vacantPeriods) throws Exception {
		landlord_vacantPeriodsTemp = vacantPeriods;
	}
	//带默认值的租进下次应收金额
	@Transient
	public String getRentin_nextshouldamountBydefault() throws Exception {
		if(StringUtils.isNotBlank(getRentin_nextshouldamount())){
			return getRentin_nextshouldamount();
		}else{
			return getRentin_rentmonth();
		}
	}

	
	public static void main(String[] args){
		//String date = "2013-01-04至2014-02-05";
		//String[] strarry = StringUtils.splitWithTokenIndex(date, "至", 3);
		Date d = DateUtils.parseDate("2013-1-3");
		System.out.println(11);
	}


	
	
}


