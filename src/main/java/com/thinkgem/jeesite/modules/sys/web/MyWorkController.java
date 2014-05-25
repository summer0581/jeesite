/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.modules.sys.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.service.RentMonthService;
import com.thinkgem.jeesite.modules.finance.service.RentService;
import com.thinkgem.jeesite.modules.finance.web.RentController.RentHandleType;

/**
 * 我的工作Controller
 * @author 夏天
 * @version 2014-4-2
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/mywork")
public class MyWorkController extends BaseController {
	@Autowired
	private RentMonthService rentMonthService;
	
	@Autowired
	private RentService rentService;

	@RequestMapping(value = {"list", ""})
	public String list( Model model) {
		List<RentMonth> temprentinlist = rentMonthService.rentInListWillNeedPayNextMonth();
		List<RentMonth> temprentoutlist = rentMonthService.rentOutListWillNeedPayNextMonth();
		List<RentMonth> temprentinwillreachedatelist = rentMonthService.rentInListWillReachEdate();
		List<RentMonth> temprentoutwillreachedatelist = rentMonthService.rentOutListWillReachEdate();
		model.addAttribute("sysdate", new Date());
        model.addAttribute("rentinlistcount", temprentinlist.size());
        model.addAttribute("rentoutlistcount", temprentoutlist.size());
        model.addAttribute("rentinWRElistcount", temprentinwillreachedatelist.size());
        model.addAttribute("rentoutWRElistcount", temprentoutwillreachedatelist.size());
        model.addAttribute("rentinlist", temprentinlist.subList(0, temprentinlist.size()>5?5:temprentinlist.size()));
        model.addAttribute("rentoutlist", temprentoutlist.subList(0, temprentoutlist.size()>5?5:temprentoutlist.size()));
        model.addAttribute("rentinWRElist", temprentinwillreachedatelist.subList(0, temprentinwillreachedatelist.size()>5?5:temprentinwillreachedatelist.size()));
        model.addAttribute("rentoutWRElist", temprentoutwillreachedatelist.subList(0, temprentoutwillreachedatelist.size()>5?5:temprentoutwillreachedatelist.size()));
		return "modules/sys/myWork";
	}
	
	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "rentHandle")
	public String rentHandle( HttpServletRequest request,RedirectAttributes redirectAttributes) {
		String id = request.getParameter("id");
		String handletype = request.getParameter("handletype");
		if(StringUtils.isNotBlank(id)){
			Rent rent = rentService.get(id);
			if(RentHandleType.payRent.equals(RentHandleType.valueOf(handletype))){
				rentService.payRent(rent);
			}else if(RentHandleType.receiveRent.equals(RentHandleType.valueOf(handletype))){
				rentService.receiveRent(rent);
			}
			rentService.save(rent);
			addMessage(redirectAttributes, "租金处理成功");
		}else{
			addMessage(redirectAttributes, "id不能为空");
		}
		
		
		return "redirect:"+Global.getAdminPath()+"/sys/mywork?repage";
	}



}
