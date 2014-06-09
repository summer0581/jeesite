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
		return find("from House where name = :p1", new Parameter(name));
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
		sql.append("LEFT JOIN finance_rent r on r.house_id = h.id ");
		sql.append("LEFT JOIN ( select * from finance_rentmonth rm where rm.infotype = 'rentin'  and  ");
		sql.append(" not exists (select 1 from finance_rentmonth rm2 where rm.rent_id=rm2.rent_id and rm2.infotype = 'rentin'  ");
		sql.append(" and rm.create_date < rm2.create_date) ) rms on r.id = rms.rent_id  ");
		sql.append("where h.del_flag=:del_flag and (r.id is null or rms.id is null or rms.edate < current_timestamp )");
		
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
		sql.append("LEFT JOIN finance_rent r on r.house_id = h.id ");
		sql.append("LEFT JOIN ( select * from finance_rentmonth rm where rm.infotype = 'rentout'  and  ");
		sql.append(" not exists (select 1 from finance_rentmonth rm2 where rm.rent_id=rm2.rent_id  and rm2.infotype = 'rentout' ");
		sql.append(" and rm.create_date < rm2.create_date) ) rms on r.id = rms.rent_id  ");
		sql.append("where h.del_flag=:del_flag and (r.id is null or rms.id is null or rms.edate < current_timestamp or rms.cancelrentdate is not null)");
		
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

}
