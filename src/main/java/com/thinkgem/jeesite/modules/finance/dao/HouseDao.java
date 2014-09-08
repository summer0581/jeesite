/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.House.RentState;

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
		sql.append("		inner join ( ");
		sql.append("		SELECT ");
		sql.append("				* ");
		sql.append("			FROM ");
		sql.append("				( ");
		sql.append("					SELECT ");
		sql.append("						* ");
		sql.append("					FROM ");
		sql.append("						finance_rentmonth rm1 ");
		sql.append("					WHERE ");
		sql.append("						rm1.infotype = 'rentin' ");
		sql.append("					AND rm1.del_flag = '0' ");
		sql.append("					ORDER BY ");
		sql.append("						rm1.create_date DESC ");
		sql.append("				) rm2 ");
		sql.append("			GROUP BY ");
		sql.append("				rm2.rent_id ");
		sql.append("		) rm on rm.rent_id = r.id ");

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
		sql.append("		select r.house_id from finance_rent r ");
		sql.append("	inner join ( ");
		sql.append("	SELECT ");
		sql.append("			* ");
		sql.append("		FROM ");
		sql.append("			( ");
		sql.append("				SELECT ");
		sql.append("					* ");
		sql.append("				FROM ");
		sql.append("					finance_rentmonth rm1 ");
		sql.append("				WHERE ");
		sql.append("					rm1.infotype = 'rentin' ");
		sql.append("				AND rm1.del_flag = '0' ");
		sql.append("				ORDER BY ");
		sql.append("					rm1.create_date DESC ");
		sql.append("			) rm2 ");
		sql.append("		GROUP BY ");
		sql.append("			rm2.rent_id ");
		sql.append("	) rm on rm.rent_id = r.id ");
		sql.append("	left join ( ");
		sql.append("		SELECT ");
		sql.append("			* ");
		sql.append("		FROM ");
		sql.append("			( ");
		sql.append("				SELECT ");
		sql.append("					* ");
		sql.append("				FROM ");
		sql.append("					finance_rentmonth rm1 ");
		sql.append("				WHERE ");
		sql.append("					rm1.infotype = 'rentout' ");
		sql.append("				AND rm1.del_flag = '0' ");
		sql.append("				ORDER BY ");
		sql.append("					rm1.create_date DESC ");
		sql.append("			) rm2 ");
		sql.append("		GROUP BY ");
		sql.append("			rm2.rent_id ");
		sql.append("	) rm2 on rm2.rent_id = r.id ");
		sql.append("	where rm2.rent_id is null  ");
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
