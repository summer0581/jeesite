/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.House.RentState;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 房屋明细DAO接口
 * @author 夏天
 * @version 2014-03-15
 */
@Repository
public class HouseDao extends BaseDao<House> {
	public List<House> findByIdIn(String[] ids){
		return find("from House where id in (:p1)", new Parameter(new Object[]{ids}));
	}
	
	/**
	 * 查找房屋list根据sql
	 * @param page
	 * @param house
	 * @param paramMap
	 * @return
	 */
	public Page<House> findListBySql(Page<House> page, House house,Map<String, Object> paramMap, String roleSql) {
		StringBuffer sql = new StringBuffer(); 
		Parameter pm = new Parameter();
		sql.append(" ");
		sql.append("select h.* from finance_house h  ");
		sql.append("LEFT JOIN sys_user cb on cb.id = h.create_by ");
		sql.append("LEFT JOIN sys_user ui on ui.id = h.rentin_user ");
		sql.append("LEFT JOIN sys_user uo on uo.id = h.rentout_user ");
		sql.append("LEFT JOIN sys_office uio on uio.id = ui.office_id ");
		sql.append("LEFT JOIN sys_office uoo on uoo.id = uo.office_id ");
		sql.append("LEFT JOIN finance_customer lc on lc.id = h.landlord_name ");
		sql.append("LEFT JOIN finance_customer tc on tc.id = h.tenant_name ");
		sql.append(" where 1=1 ");
		if (StringUtils.isNotEmpty(house.getName())){
			sql.append(" and h.name like :home_name ");
			pm.put("home_name", "%"+house.getName()+"%");
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getName())){
			sql.append(" and lc.name like :landlord_name ");
			pm.put("landlord_name", "%"+house.getLandlord().getName()+"%");
		}
		if (null != house.getTenant() && StringUtils.isNotEmpty(house.getTenant().getName())){
			sql.append(" and tc.name like :tenant_name ");
			pm.put("tenant_name", "%"+house.getTenant().getName()+"%");
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getTelephone())){
			sql.append(" and lc.telephone like :landlord_telephone ");
			pm.put("landlord_telephone", "%"+house.getLandlord().getTelephone()+"%");
		}
		if (null != house.getTenant() && StringUtils.isNotEmpty(house.getTenant().getTelephone())){
			sql.append(" and tc.telephone like :tenant_telephone ");
			pm.put("tenant_telephone", "%"+house.getTenant().getTelephone()+"%");
		}
		if (StringUtils.isNotBlank(house.getHouses())){
			sql.append(" and h.houses like :home_houses ");
			pm.put("home_houses", "%"+house.getHouses()+"%");
		}
		if (StringUtils.isNotBlank(house.getArea())){
			sql.append(" and h.area = :home_area ");
			pm.put("home_area", house.getArea());
		}
		if (StringUtils.isNotBlank(house.getHouse_source())){
			sql.append(" and h.house_source = :house_source ");
			pm.put("house_source", house.getHouse_source());
		}
		if (StringUtils.isNotBlank(house.getIs_cansale())){
			sql.append(" and h.is_cansale = :home_is_cansale ");
			pm.put("home_is_cansale", house.getIs_cansale());
		}
		if (StringUtils.isNotBlank(house.getIs_canrent())){
			sql.append(" and h.is_canrent = :home_is_canrent ");
			pm.put("home_is_canrent", house.getIs_canrent());
		}
		if (StringUtils.isNotBlank(house.getDirection())){
			sql.append(" and h.direction like :home_direction ");
			pm.put("home_direction", "%"+house.getDirection()+"%");
		}
		if (StringUtils.isNotBlank(house.getAge())){
			sql.append(" and h.age like :home_age ");
			pm.put("home_age", "%"+house.getAge()+"%");
		}
		if (StringUtils.isNotBlank(house.getDecorate())){
			sql.append(" and h.decorate like :home_decorate ");
			pm.put("home_decorate", "%"+house.getDecorate()+"%");
		}
		if (StringUtils.isNotBlank(house.getProp_certno())){
			sql.append(" and h.prop_certno like :home_prop_certno ");
			pm.put("home_prop_certno", "%"+house.getProp_certno()+"%");
		}
		if (StringUtils.isNotBlank(house.getLand_certno())){
			sql.append(" and h.land_certno like :home_land_certno ");
			pm.put("home_land_certno", "%"+house.getLand_certno()+"%");
		}
		if (StringUtils.isNotBlank(house.getHouse_elec())){
			sql.append(" and h.house_elec like :home_house_elec ");
			pm.put("home_house_elec", "%"+house.getHouse_elec()+"%");
		}
		if (StringUtils.isNotBlank(house.getHouse_layout())){
			sql.append(" and h.house_layout like :home_house_layout ");
			pm.put("home_house_layout", "%"+house.getHouse_layout()+"%");
		}
		if (StringUtils.isNotBlank(house.getWy_useful())){
			sql.append(" and h.wy_useful like :home_wy_useful ");
			pm.put("home_wy_useful", "%"+house.getWy_useful()+"%");
		}
		if (StringUtils.isNotBlank(house.getPaytype())){
			sql.append(" and h.paytype = :home_paytype ");
			pm.put("home_paytype", house.getPaytype());
		}
		if (StringUtils.isNotBlank(house.getStructure())){
			sql.append(" and h.structure like :home_structure ");
			pm.put("home_structure", "%"+house.getStructure()+"%");
		}
		if (StringUtils.isNotBlank(house.getArrond_environ())){
			sql.append(" and h.arrond_environ like :home_arrond_environ ");
			pm.put("home_arrond_environ", "%"+house.getArrond_environ()+"%");
		}
		if (StringUtils.isNotBlank(house.getHousing_facilities())){
			sql.append(" and h.housing_facilities like :home_housing_facilities ");
			pm.put("home_housing_facilities", "%"+house.getHousing_facilities()+"%");
		}
		if (StringUtils.isNotBlank(house.getAreadescribe())){
			sql.append(" and h.areadescribe like :home_areadescribe ");
			pm.put("home_areadescribe", "%"+house.getAreadescribe()+"%");
		}
		if (StringUtils.isNotBlank(house.getTraffic_condition())){
			sql.append(" and h.traffic_condition like :home_traffic_condition ");
			pm.put("home_traffic_condition", "%"+house.getTraffic_condition()+"%");
		}
		if (StringUtils.isNotBlank(house.getEntrust_store())){
			sql.append(" and h.entrust_store like :home_entrust_store ");
			pm.put("home_entrust_store", "%"+house.getEntrust_store()+"%");
		}
		if (StringUtils.isNotBlank(house.getRegist_store())){
			sql.append(" and h.regist_store like :home_regist_store ");
			pm.put("home_regist_store", "%"+house.getRegist_store()+"%");
		}
		if (StringUtils.isNotBlank(house.getIs_needdeposit())){
			sql.append(" and h.is_needdeposit = :home_is_needdeposit ");
			pm.put("home_is_needdeposit", house.getIs_needdeposit());
		}
		if (StringUtils.isNotBlank(house.getIs_norentin())){
			sql.append(" and h.is_norentin = :home_is_norentin ");
			pm.put("home_is_norentin", house.getIs_norentin());
		}
		if(StringUtils.isNotBlank(house.getReceive_username())){
			sql.append(" and h.receive_username = :home_receive_username ");
			pm.put("home_receive_username", house.getReceive_username());
		}
		if(null != paramMap){
			String sale_price_min = (String)paramMap.get("sale_price_min");
			String sale_price_max = (String)paramMap.get("sale_price_max");
			String measure_min = (String)paramMap.get("measure_min");
			String measure_max = (String)paramMap.get("measure_max");
			String water_num_min = (String)paramMap.get("water_num_min");
			String water_num_max = (String)paramMap.get("water_num_max");
			String elec_num_min = (String)paramMap.get("elec_num_min");
			String elec_num_max = (String)paramMap.get("elec_num_max");
			String rentin_userid = (String)paramMap.get("rentin_userid");
			String rentout_userid = (String)paramMap.get("rentout_userid");

			if (StringUtils.isNotBlank(sale_price_min)){
				sql.append(" and h.sale_price >= :home_sale_price_min ");
				pm.put("home_sale_price_min", sale_price_min);
			}
			if (StringUtils.isNotBlank(sale_price_max)){
				sql.append(" and h.sale_price <= :home_sale_price_max ");
				pm.put("home_sale_price_max", sale_price_max);
			}
			if (StringUtils.isNotBlank(measure_min)){
				sql.append(" and h.measure >= :home_measure_min ");
				pm.put("home_measure_min", measure_min);
			}
			if (StringUtils.isNotBlank(measure_max)){
				sql.append(" and h.measure <= :home_measure_max ");
				pm.put("home_measure_max", measure_max);
			}
			if (StringUtils.isNotBlank(water_num_min)){
				sql.append(" and h.water_num = :home_water_num_min ");
				pm.put("home_water_num_min", water_num_min);
			}
			if (StringUtils.isNotBlank(water_num_max)){
				sql.append(" and h.water_num <= :home_water_num_max ");
				pm.put("home_water_num_max", water_num_max);
			}
			if (StringUtils.isNotBlank(elec_num_min)){
				sql.append(" and h.elec_num >= :home_elec_num_min ");
				pm.put("home_elec_num_min", elec_num_min);
			}
			if (StringUtils.isNotBlank(elec_num_max)){
				sql.append(" and h.elec_num <= :home_elec_num_max ");
				pm.put("home_elec_num_max", elec_num_max);
			}
			if (StringUtils.isNotBlank(rentin_userid)){
				sql.append(" and ui.id = :rentin_userid ");
				pm.put("rentin_userid", rentin_userid);
			}
			if (StringUtils.isNotBlank(rentout_userid)){
				sql.append(" and uo.id = :rentout_userid ");
				pm.put("rentout_userid", rentout_userid);
			}
		}
		sql.append(" ");
		sql.append(roleSql);
		sql.append(" and h.del_flag = :del_flag");
		pm.put("del_flag", House.DEL_FLAG_NORMAL);
		sql.append(" order by h.id desc");

		return findBySql(page, sql.toString(), pm, House.class);
	}
	public List<House> findByName(String name){
		clear();
		return findBySql("select * from finance_house f where f.name = :p1", new Parameter(name),House.class);
	}
	
	/**
	 * 获取未与明细表关联的房子信息
	 * @param page
	 * @param house
	 * @return
	 */
	public Page<House> findNoRelation(Page<House> page, House house) {

		StringBuffer sql = new StringBuffer();
		Parameter param = new Parameter();
		sql.append(" ");
		sql.append("select h.* from finance_house h ");
		sql.append("LEFT JOIN finance_customer c on c.id = h.landlord_name ");
		sql.append("LEFT JOIN finance_rent r on r.house_id = h.id ");
		sql.append("where h.del_flag=:del_flag and (r.id is null )");
		
		param.put("del_flag", House.DEL_FLAG_NORMAL);
		if (StringUtils.isNotEmpty(house.getName())){
			sql.append(" and h.name like :name ");
			param.put("name", "%"+house.getName()+"%");
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getName())){
			sql.append(" and c.name like :landlord_name ");
			param.put("landlord_name", "%"+house.getLandlord().getName()+"%");
		}
		sql.append(" order by h.name");
		return findBySql(page, sql.toString(), param, House.class);
	}

	
	/**
	 * 获取未与明细表关联的房子信息
	 * @param page
	 * @param house
	 * @return
	 */
	public Page<House> findNoRentin(Page<House> page, House house) {

		StringBuffer sql = new StringBuffer();
		Parameter param = new Parameter();
		sql.append(" ");
		sql.append("select h.* from finance_house h ");
		sql.append("LEFT JOIN finance_customer c on c.id = h.landlord_name ");
		sql.append("where h.del_flag=:del_flag and  h.flag_norentin = 'Y' ");
		
		param.put("del_flag", House.DEL_FLAG_NORMAL);
		if (StringUtils.isNotEmpty(house.getName())){
			sql.append(" and h.name like :name ");
			param.put("name", "%"+house.getName()+"%");
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getName())){
			sql.append(" and c.name like :landlord_name ");
			param.put("landlord_name", "%"+house.getLandlord().getName()+"%");
		}
		sql.append(" order by h.name");
		return findBySql(page, sql.toString(), param, House.class);
	}
	
	/**
	 * 获取所有退租或已停租的房子信息
	 * @param page
	 * @param house
	 * @return
	 */
	public Page<House> findHouseCancelRent(Page<House> page, House house) {

		StringBuffer sql = new StringBuffer();
		Parameter param = new Parameter();
		sql.append(" ");
		sql.append("select h.* from finance_house h ");
		sql.append("LEFT JOIN finance_customer c on c.id = h.landlord_name ");
		
		String rentstateConditionSql = " h.flag_norentout = 'Y' or h.flag_cancelrent = 'Y' ";
		if(RentState.hascancelrent.toString().equals(house.getRent_state())){//已退租
			rentstateConditionSql = " h.flag_cancelrent = 'Y' ";
		}else if(RentState.norentout.toString().equals(house.getRent_state())){//未租出
			rentstateConditionSql = " h.flag_norentout = 'Y' ";
		}
		sql.append("where h.del_flag=:del_flag and ("+rentstateConditionSql+")");
		
		param.put("del_flag", House.DEL_FLAG_NORMAL);
		if (StringUtils.isNotEmpty(house.getName())){
			sql.append(" and h.name like :name ");
			param.put("name", "%"+house.getName()+"%");
		}
		if (null != house.getLandlord() && StringUtils.isNotEmpty(house.getLandlord().getName())){ 
			sql.append(" and c.name like :landlord_name ");
			param.put("landlord_name", "%"+house.getLandlord().getName()+"%");
		}
		sql.append(" order by h.name");
		return findBySql(page, sql.toString(), param, House.class);
	}
	
	/**
	 * 更新所有未租进的房屋记录
	 */
	public int updateHouseNorentinData(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		sql.append(" ");
		sql.append("update finance_house set flag_norentin = 'Y' ");
		if(getSession().isDefaultReadOnly()){
			
		}
		result = updateBySql(sql.toString(),null);
		
		sql = new StringBuffer();
		
		sql.append("		select r.house_id from finance_rent r ");
		sql.append("		where r.business_num is not null ");
		

		List<String> houseresult = findBySql(sql.toString());
		
		sql = new StringBuffer();
		
		sql.append("update finance_house set flag_norentin = 'N' where 1=1 ");
		sql.append(StringUtils.createInSql("id",houseresult));
		sql.append(" ");
		result = updateBySql(sql.toString(),null);
		return result;
	}
	
	
	/**
	 * 更新所有未租出的房屋记录
	 */
	public int updateHouseNorentoutData(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		sql.append(" ");
		sql.append("update finance_house set flag_norentout = 'N' ");
		result = updateBySql(sql.toString(),null);
		
		sql = new StringBuffer();
		sql.append("select r.house_id from finance_rent r ");
		sql.append("where r.business_num <> '' and r.business_num is not null  ");
		sql.append("and r.id not in (SELECT ");
		sql.append("				rm1.rent_id ");
		sql.append("			FROM ");
		sql.append("				finance_rentmonth rm1 ");
		sql.append("			WHERE ");
		sql.append("				rm1.infotype = 'rentout') ");
		List<String> houseresult = findBySql(sql.toString());
		
		sql = new StringBuffer();
		
		sql.append("update finance_house set flag_norentout = 'Y' where 1=1 ");
		sql.append(StringUtils.createInSql("id",houseresult));
		sql.append(" ");
		
		result = updateBySql(sql.toString(),null);
		return result;
	}
	
	/**
	 * 更新所有已退租的房屋记录
	 */
	public int updateHouseCancelrentData(){
		int result = 0;
		StringBuffer sql = new StringBuffer();
		sql.append(" ");
		sql.append("update finance_house set flag_cancelrent = 'N' ");
		result = updateBySql(sql.toString(),null);
		
		sql = new StringBuffer();
		sql.append("	select r.house_id from finance_rent r ");
		sql.append("inner join ( ");
		sql.append("SELECT ");
		sql.append("		* ");
		sql.append("	FROM ");
		sql.append("		( ");
		sql.append("			SELECT ");
		sql.append("				* ");
		sql.append("			FROM ");
		sql.append("				finance_rentmonth rm1 ");
		sql.append("			WHERE ");
		sql.append("				rm1.infotype = 'rentout' ");
		sql.append("			AND rm1.del_flag = '0' ");
		sql.append("			ORDER BY ");
		sql.append("				rm1.create_date DESC ");
		sql.append("		) rm2 ");
		sql.append("	GROUP BY ");
		sql.append("		rm2.rent_id ");
		sql.append(") rm on rm.rent_id = r.id ");
		sql.append("where rm.cancelrentdate is not null ");
		
		
		
		List<String> houseresult = findBySql(sql.toString());
		
		sql = new StringBuffer();
		
		sql.append("update finance_house set flag_cancelrent = 'Y' where 1=1 ");
		sql.append(StringUtils.createInSql("id",houseresult));
		sql.append(" ");
		result = updateBySql(sql.toString(),null);
		return result;
	}
	

}
