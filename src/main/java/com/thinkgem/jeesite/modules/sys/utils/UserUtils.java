/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.modules.sys.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.service.BaseService;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.sys.dao.AreaDao;
import com.thinkgem.jeesite.modules.sys.dao.MenuDao;
import com.thinkgem.jeesite.modules.sys.dao.OfficeDao;
import com.thinkgem.jeesite.modules.sys.dao.RoleDao;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.Area;
import com.thinkgem.jeesite.modules.sys.entity.Menu;
import com.thinkgem.jeesite.modules.sys.entity.Office;
import com.thinkgem.jeesite.modules.sys.entity.Role;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;

/**
 * 用户工具类
 * @author ThinkGem
 * @version 2013-5-29
 */
public class UserUtils extends BaseService {

	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);
	private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
	private static AreaDao areaDao = SpringContextHolder.getBean(AreaDao.class);
	private static OfficeDao officeDao = SpringContextHolder.getBean(OfficeDao.class);

	public static final String CACHE_USER = "user";
	public static final String CACHE_USER_LIST = "userList";
	public static final String CACHE_ROLE_LIST = "roleList";
	public static final String CACHE_MENU_LIST = "menuList";
	public static final String CACHE_AREA_LIST = "areaList";
	public static final String CACHE_OFFICE_LIST = "officeList";
	
	public static User getUser(){
		User user = (User)getCache(CACHE_USER);
		if (user == null){
			try{
				Subject subject = SecurityUtils.getSubject();
				Principal principal = (Principal)subject.getPrincipal();
				if (principal!=null){
					user = userDao.get(principal.getId());
//					Hibernate.initialize(user.getRoleList());
					putCache(CACHE_USER, user);
				}
			}catch (UnavailableSecurityManagerException e) {
				
			}catch (InvalidSessionException e){
				
			}
		}
		if (user == null){
			user = new User();
			try{
				SecurityUtils.getSubject().logout();
			}catch (UnavailableSecurityManagerException e) {
				
			}catch (InvalidSessionException e){
				
			}
		}
		return user;
	}
	
	public static User getUser(boolean isRefresh){
		if (isRefresh){
			removeCache(CACHE_USER);
		}
		return getUser();
	}
	
	public static List<User> getUserList(){
		@SuppressWarnings("unchecked")
		List<User> list = (List<User>)getCache(CACHE_USER_LIST);
		if (list == null){
			User user = getUser();
			DetachedCriteria dc = userDao.createDetachedCriteria();
			dc.createAlias("office", "office");
			dc.add(dataScopeFilter(user, "office",dc.getAlias()));
			dc.add(Restrictions.eq(User.FIELD_DEL_FLAG, User.DEL_FLAG_NORMAL));
			dc.addOrder(Order.asc("name"));
			list = userDao.find(dc);
			putCache(CACHE_USER_LIST, list);
		}
		return list;
	}

	public static List<Role> getRoleList(){
		@SuppressWarnings("unchecked")
		List<Role> list = (List<Role>)getCache(CACHE_ROLE_LIST);
		if (list == null){
			User user = getUser();
			DetachedCriteria dc = roleDao.createDetachedCriteria();
			dc.createAlias("office", "office");
			dc.createAlias("userList", "userList", JoinType.LEFT_OUTER_JOIN);
			dc.add(dataScopeFilter(user, "office", "userList"));
			dc.add(Restrictions.eq(Role.FIELD_DEL_FLAG, Role.DEL_FLAG_NORMAL));
			dc.addOrder(Order.asc("office.code")).addOrder(Order.asc("name"));
			list = roleDao.find(dc);
			putCache(CACHE_ROLE_LIST, list);
		}
		return list;
	}
	
	public static List<Menu> getMenuList(){
		@SuppressWarnings("unchecked")
		List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
		if (menuList == null){
			User user = getUser();
			if (user.isAdmin()){
				menuList = menuDao.findAllList();
			}else{
				menuList = menuDao.findByUserId(user.getId());
			}
			putCache(CACHE_MENU_LIST, menuList);
		}
		return menuList;
	}
	
	public static List<Area> getAreaList(){
		@SuppressWarnings("unchecked")
		List<Area> areaList = (List<Area>)getCache(CACHE_AREA_LIST);
		if (areaList == null){
//			User user = getUser();
//			if (user.isAdmin()){
				areaList = areaDao.findAllList();
//			}else{
//				areaList = areaDao.findAllChild(user.getArea().getId(), "%,"+user.getArea().getId()+",%");
//			}
			putCache(CACHE_AREA_LIST, areaList);
		}
		return areaList;
	}
	
	public static List<Office> getOfficeList(){
		@SuppressWarnings("unchecked")
		List<Office> officeList = (List<Office>)getCache(CACHE_OFFICE_LIST);
		if (officeList == null){
			User user = getUser();
//			if (user.isAdmin()){
//				officeList = officeDao.findAllList();
//			}else{
//				officeList = officeDao.findAllChild(user.getOffice().getId(), "%,"+user.getOffice().getId()+",%");
//			}
			DetachedCriteria dc = officeDao.createDetachedCriteria();
			dc.add(dataScopeFilter(user, dc.getAlias(), ""));
			dc.add(Restrictions.eq("delFlag", Office.DEL_FLAG_NORMAL));
			dc.addOrder(Order.asc("code"));
			officeList = officeDao.find(dc);
			putCache(CACHE_OFFICE_LIST, officeList);
		}
		return officeList;
	}
	

	public static User getUserById(String id){
		if(StringUtils.isNotBlank(id)) {
			return userDao.get(id);
		} else {
			return null;
		}
	}
	public enum UserLevel{
		manager,departleader,teamleader,saler
	}
	
	/**
	 * 根据级别获取相应的用户
	 * @param userlevel
	 * @return
	 */
	public static List<User>  getListByUserLevel(UserLevel userlevel){
		List<User> userlist = new ArrayList<User>();
		if(UserLevel.manager.equals(userlevel)){
			for(Office office : UserUtils.getOfficeList()){
				if("1".equals(office.getType()) && null != office.getMaster() && StringUtils.isNotBlank(office.getMaster().getUserBusitype())){
					userlist.add(office.getMaster());
				}
			}
		}else if(UserLevel.departleader.equals(userlevel)){
			for(Office office : UserUtils.getOfficeList()){
				if("2".equals(office.getType()) && null != office.getMaster() && StringUtils.isNotBlank(office.getMaster().getUserBusitype())){
					userlist.add(office.getMaster());
				}
			}
		}else if(UserLevel.teamleader.equals(userlevel)){
			for(Office office : UserUtils.getOfficeList()){
				if("3".equals(office.getType()) && null != office.getMaster() && StringUtils.isNotBlank(office.getMaster().getUserBusitype())){
					userlist.add(office.getMaster());
				}
			}
		}else{
			for(Office office : UserUtils.getOfficeList()){
				for(User usertemp : office.getUserList()){
					if(!usertemp.equals(office.getMaster()) && StringUtils.isNotBlank(usertemp.getUserBusitype())){
						userlist.add(usertemp);
					}	
				}
			}
		}
		return userlist;
	}
	/**
	 * 获取当前用户的所有领导：manager,departleader,teamleader
	 * @return
	 */
	public static Map<String,User> getUserLeaders(User curUser){
		Map<String,User> leaders = new HashMap<String,User>();
		 Office office = curUser.getOffice();
		 setLeader(office,curUser,leaders);
		 return leaders;
	}
	
	private static void setLeader(Office office,User curUser,Map<String,User> resultMap){
		if("1".equals(office.getType())){
			if(null != office.getMaster() && !curUser.equals(office.getMaster())){
				resultMap.put("manager", office.getMaster());
			}
		}else{
				if("2".equals(office.getType())){
					if(null != office.getMaster() && !curUser.equals(office.getMaster())){
						resultMap.put("departleader", office.getMaster());
					}
					setLeader(office.getParent(),curUser,resultMap);
				}else if("3".equals(office.getType()) && !curUser.equals(office.getMaster())){
					if(null != office.getMaster()){
						resultMap.put("teamleader", office.getMaster());
					}
					setLeader(office.getParent(),curUser,resultMap);
				}				
		}
		

	}
	
	/**
	 * 判断用户是否有权限
	 * @param permittedStr
	 * @return
	 */
	public static boolean isPermitted(String permittedStr){
		Subject user = SecurityUtils.getSubject();
		return user.isPermitted(permittedStr);
	}
	
	/**
	 * 判断用户是否有角色
	 * @param permittedStr
	 * @return
	 */
	public static boolean hasRole(String roleIdentifier){
		Subject user = SecurityUtils.getSubject();
		return user.hasRole(roleIdentifier);
	}
	
	
	// ============== User Cache ==============
	
	public static Object getCache(String key) {
		return getCache(key, null);
	}
	
	public static Object getCache(String key, Object defaultValue) {
		Object obj = getCacheMap().get(key);
		return obj==null?defaultValue:obj;
	}

	public static void putCache(String key, Object value) {
		getCacheMap().put(key, value);
	}

	public static void removeCache(String key) {
		getCacheMap().remove(key);
	}
	
	public static Map<String, Object> getCacheMap(){
		Map<String, Object> map = Maps.newHashMap();
		try{
			Subject subject = SecurityUtils.getSubject();
			Principal principal = (Principal)subject.getPrincipal();
			return principal!=null?principal.getCacheMap():map;
		}catch (UnavailableSecurityManagerException e) {
			
		}catch (InvalidSessionException e){
			
		}
		return map;
	}
	

	
}
