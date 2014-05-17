/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.BaseEntity;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.dao.RentDao;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 包租明细Service
 * @author 夏天
 * @version 2014-03-15
 */
@Component
@Transactional(readOnly = true)
public class RentService extends BaseService {

	@Autowired
	private RentDao rentDao;
	
	public Rent get(String id) {
		return rentDao.get(id);
	}
	
	public Page<Rent> find(Page<Rent> page, Rent rent) {
		DetachedCriteria dc = rentDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(rent.getName())){
			dc.add(Restrictions.like("name", "%"+rent.getName()+"%"));
		}
		dc.createAlias("rentin_person", "rentin");
		dc.createAlias("rentin.office", "rentin_office");
		dc.add(dataScopeFilter(UserUtils.getUser(), "rentin_office", ""));
		dc.add(Restrictions.eq(Rent.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		dc.addOrder(Order.desc("id"));
		return rentDao.find(page, dc);
	}
	

	
	@Transactional(readOnly = false)
	public void save(Rent rent) {
		if(null != rent.getSalesman_vacantperiods())
			for(VacantPeriod vp: rent.getSalesman_vacantperiods()){//批量设置业务员空置期
				if(StringUtils.isBlank(vp.getId())){
					vp.setId(IdGen.uuid());
				}
				vp.setRent(rent);
			}
		if(null != rent.getLandlord_vacantperiods())
			for(VacantPeriod vp: rent.getLandlord_vacantperiods()){//批量设置房东空置期
				if(StringUtils.isBlank(vp.getId())){
					vp.setId(IdGen.uuid());
				}
				vp.setRent(rent);
			}
		if(null != rent.getRentinMonths())
			for(RentMonth r: rent.getRentinMonths()){//批量设置承租月明细
				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentin");
			}
		if(null != rent.getRentoutMonths())
			for(RentMonth r: rent.getRentoutMonths()){//批量设置承租月明细
				if(StringUtils.isBlank(r.getId())){
					r.prePersist();
				}
				r.setRent(rent);
				r.setInfotype("rentout");
			}

		rentDao.save(rent);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
				Rent rent = get(id);
		rent.getHouse().setId("");
		//rent.setDelFlag(BaseEntity.DEL_FLAG_DELETE);
		rentDao.save(rent);
		rentDao.deleteById(id);
		//rentDao.deleteRent(id);
	}
	
	public Page<Rent> rentList(Page<Rent> page,Map<String, Object> paramMap) {
		
		DetachedCriteria dc = rentDao.createDetachedCriteria();
		String name = (String)paramMap.get("name");
		if (StringUtils.isNotEmpty(name)){
			dc.add(Restrictions.like("name", "%"+name+"%"));
		}
		Date rentin_nextpaysdate = DateUtils.parseDate(paramMap.get("rentin_nextpaysdate"));
		Date rentin_nextpayedate = DateUtils.parseDate(paramMap.get("rentin_nextpayedate"));
		Date rentout_nextpaysdate = DateUtils.parseDate(paramMap.get("rentout_nextpaysdate"));
		Date rentout_nextpayedate = DateUtils.parseDate(paramMap.get("rentout_nextpayedate"));
		
		if (null != rentin_nextpaysdate){
			dc.add(Restrictions.gt("rentinMonths.nextpaydate", rentin_nextpaysdate));
		}
		if (null != rentin_nextpayedate){
			dc.add(Restrictions.le("rentinMonths.nextpaydate", rentin_nextpayedate));
		}
		if (null != rentout_nextpaysdate){
			dc.add(Restrictions.gt("rentoutMonths.nextpaydate", rentout_nextpaysdate));
		}
		if (null != rentout_nextpayedate){
			dc.add(Restrictions.le("rentoutMonths.nextpaydate", rentout_nextpayedate));
		}

		dc.createAlias("house", "house", JoinType.LEFT_OUTER_JOIN);
		dc.add(Restrictions.eq(Rent.FIELD_DEL_FLAG, Rent.DEL_FLAG_NORMAL));
		if(StringUtils.isBlank(page.getOrderBy())){
			//dc.addOrder(Order.desc("rentin_nextpaydate")).addOrder(Order.desc("rentout_nextpaydate"));
		}
		
		return rentDao.find(page, dc);
	}
	

	
	/**
	 * 付租
	 * @param rent
	 */
	public void payRent(Rent rent){
		
	}

	/**
	 * 收租
	 * @param rent
	 */
	public void receiveRent(Rent rent){
		
	}
	
	/**
	 * 根据房屋名称查找房屋包租明细
	 * @param name
	 * @return
	 */
	public Rent findByName(String name) {
		List<Rent> rents = rentDao.findByName(name);
		return (rents.size()>0)?rentDao.findByName(name).get(0):null;
	}
	

	
}
