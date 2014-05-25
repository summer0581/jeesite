/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.thinkgem.jeesite.common.persistence.BaseDao;
import com.thinkgem.jeesite.common.persistence.Parameter;
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;

/**
 * 包租提成设置DAO接口
 * @author 夏天
 * @version 2014-05-18
 */
@Repository
public class CutconfigDao extends BaseDao<Cutconfig> {
	public List<Cutconfig> findByName(String name){
		return find("from Cutconfig where name = :p1", new Parameter(name));
	}
	
	public List<String> findCutcodeList(){
		return find("select cut_code from Cutconfig where delFlag=:p1 group by cut_code", new Parameter(Cutconfig.DEL_FLAG_NORMAL));
	}
	
	public List<Cutconfig> findCutcodeList(String cut_type){
		return find("from Cutconfig where delFlag=:p1 and cut_type=:p2 group by cut_code", new Parameter(Cutconfig.DEL_FLAG_NORMAL,cut_type));
	}
}
