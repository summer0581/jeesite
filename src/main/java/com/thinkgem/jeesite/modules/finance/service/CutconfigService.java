/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.finance.constant.CutConfigPersonConstant;
import com.thinkgem.jeesite.modules.finance.constant.CutConfigTypeConstant;
import com.thinkgem.jeesite.modules.finance.dao.CutconfigDao;
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;

/**
 * 包租提成设置Service
 * @author 夏天
 * @version 2014-05-18
 */
@Component
@Transactional(readOnly = true)
public class CutconfigService extends BaseService {

	@Autowired
	private CutconfigDao cutconfigDao;
	
	public Cutconfig get(String id) {
		return cutconfigDao.get(id);
	}
	
	public Cutconfig findByName(String name) {
		List<Cutconfig> cutconfigs = cutconfigDao.findByName(name);
		return (cutconfigs.size()>0)?cutconfigDao.findByName(name).get(0):null;
	}
	
	
	public Page<Cutconfig> find(Page<Cutconfig> page, Cutconfig cutconfig) {
		DetachedCriteria dc = cutconfigDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(cutconfig.getName())){
			dc.add(Restrictions.like("name", "%"+cutconfig.getName()+"%"));
		}
		if (StringUtils.isNotEmpty(cutconfig.getCut_code())){
			dc.add(Restrictions.eq("cut_code", cutconfig.getCut_code()));
		}
		dc.add(Restrictions.eq(Cutconfig.FIELD_DEL_FLAG, Cutconfig.DEL_FLAG_NORMAL));
		dc.addOrder(Order.asc("cut_code")).addOrder(Order.asc("person")).addOrder(Order.desc("id"));
		return cutconfigDao.find(page, dc);
	}
	
	public List<Cutconfig> findCutconfiglistByCutcode(String cutcode){
		DetachedCriteria dc = cutconfigDao.createDetachedCriteria();
		if (StringUtils.isNotBlank(cutcode)){
			dc.add(Restrictions.eq("cut_code", cutcode));
			return cutconfigDao.find(dc);
		}
		return new ArrayList<Cutconfig>();
	}
	
	@Transactional(readOnly = false)
	public void save(Cutconfig cutconfig) {
		cutconfigDao.save(cutconfig);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		cutconfigDao.deleteById(id);
	}
	
	public List<String> findCutcodeList(){
		return cutconfigDao.findCutcodeList();
	}
	
	public double getCutpercentByPersonAndType(List<Cutconfig> cutconfigs,CutConfigPersonConstant person,CutConfigTypeConstant type){
		double new_cut = 0.0;
		String personstring = person.toString().replace("_old", "");
		if(null != cutconfigs)
			for(Cutconfig cutconfig : cutconfigs){
				if(person.toString().equals(cutconfig.getPerson()) && type.toString().equals(cutconfig.getCut_type())){
					if(type.equals(CutConfigTypeConstant.cut_vacantperiod))
						return Integer.valueOf(cutconfig.getCut_num())*0.01;
					else if(type.equals(CutConfigTypeConstant.cut_businesssales)){
						return Integer.valueOf(cutconfig.getCut_num());
					}
				}
				if(personstring.equals(cutconfig.getPerson()) && type.toString().equals(cutconfig.getCut_type())){
					if(type.equals(CutConfigTypeConstant.cut_vacantperiod))
						new_cut = Integer.valueOf(cutconfig.getCut_num())*0.01;
					else if(type.equals(CutConfigTypeConstant.cut_businesssales)){
						new_cut = Integer.valueOf(cutconfig.getCut_num());
					}
				}
			}
		
		if(person.equals(CutConfigPersonConstant.rentinsaler_old) || person.equals(CutConfigPersonConstant.rentoutsaler_old)){//如果是老业务员提成，搜索完后没有找到任意提成记录，则按新业务员提成计算
			return new_cut;
		}
		return 0;
	}
	
}
