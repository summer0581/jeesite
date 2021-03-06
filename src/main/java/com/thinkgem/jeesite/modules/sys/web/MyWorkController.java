/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.modules.sys.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.service.RentMonthService;
import com.thinkgem.jeesite.modules.finance.service.RentService;

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
	public String list( Model model) throws Exception {
		Page<Rent> page1 = new Page<Rent>(0,5);
		Map<String, Object> paramMap = new HashMap<String,Object>();
		paramMap.put("rentin_nextpayedate",DateUtils.formatDate(DateUtils.addDays(new Date(), 30), "yyyy-MM-dd"));
		paramMap.put("order", "rms.nextpaydate");
		Page<Rent> temprentinlist = rentService.rentInListWillNeedPayNextMonth(page1,paramMap);
		Page<Rent> page2 = new Page<Rent>(0,5);
		paramMap = new HashMap<String,Object>();
		paramMap.put("rentout_nextpayedate",DateUtils.formatDate(DateUtils.addDays(new Date(), 30), "yyyy-MM-dd"));
		paramMap.put("notcancelrent", "true");
		paramMap.put("order", "rms2.nextpaydate");
		Page<Rent> temprentoutlist = rentService.rentOutListWillNeedPayNextMonth(page2,paramMap);
		Page<RentMonth> page3 = new Page<RentMonth>(0,5);
		Page<RentMonth> temprentinwillreachedatelist = rentMonthService.rentInListWillReachEdate(page3);
		Page<RentMonth> page4 = new Page<RentMonth>(0,5);
		Page<RentMonth> temprentoutwillreachedatelist = rentMonthService.rentOutListWillReachEdate(page4);
		model.addAttribute("sysdate", new Date());
		model.addAttribute("rentwarndate", DateUtils.formatDate(DateUtils.addDays(new Date(), 180), "yyyy-MM-dd"));
        model.addAttribute("rentinlistcount", temprentinlist.getCount());
        model.addAttribute("rentoutlistcount", temprentoutlist.getCount());
        model.addAttribute("rentinWRElistcount", temprentinwillreachedatelist.getCount());
        model.addAttribute("rentoutWRElistcount", temprentoutwillreachedatelist.getCount());
        model.addAttribute("rentinlist", temprentinlist);
        model.addAttribute("rentoutlist", temprentoutlist);
        model.addAttribute("rentinWRElist", temprentinwillreachedatelist);
        model.addAttribute("rentoutWRElist", temprentoutwillreachedatelist);
		return "modules/sys/myWork";
	}
	



}
