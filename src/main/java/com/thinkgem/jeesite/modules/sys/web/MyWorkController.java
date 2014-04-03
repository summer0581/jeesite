/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.thinkgem.jeesite.modules.sys.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thinkgem.jeesite.common.web.BaseController;

/**
 * 我的工作Controller
 * @author 夏天
 * @version 2014-4-2
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/mywork")
public class MyWorkController extends BaseController {


	@RequestMapping(value = {"list", ""})
	public String list( Model model) {
        //model.addAttribute("list", list);
		return "modules/sys/myWork";
	}


}
