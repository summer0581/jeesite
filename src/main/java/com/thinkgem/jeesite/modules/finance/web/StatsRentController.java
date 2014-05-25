/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.finance.entity.VacantPeriod;
import com.thinkgem.jeesite.modules.finance.service.StatsRentService;

/**
 * 空置期提成Controller
 * @author 夏天
 * @version 2014-04-13
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/stats")
public class StatsRentController extends BaseController {

	@Autowired
	private StatsRentService statsRentService;
	
	
	@RequiresPermissions("finance:stats:vacantPeriod") 
	@RequestMapping(value = {"vacantPeriod"})
	public String vacantPeriod(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.vacantPeriod(paramMap);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsVacantPeriodList";
	}

	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"vacantPeriod4Person"})
	public String vacantPeriod4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.vacantPeriod4Person(paramMap);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsVacantPeriod4PersonList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"vacantPeriodDetail4Person"})
	public String vacantPeriodDetail4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.vacantPeriodDetail4Person(paramMap);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		
		return "modules/finance/statsVacantPeriodDetail4PersonList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"businessCut"})
	public String businessCut(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.businessSaleCut(paramMap);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsBusinessCutList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"businessCut4Person"})
	public String businessCut4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.businessSaleCut4PersonList(paramMap);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsBusinessCut4PersonList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"businessCutDetail4Person"})
	public String businessCutDetail4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.businessSaleCutDetail4PersonList(paramMap);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		
		return "modules/finance/statsBusinessCutDetail4PersonList";
	}


}
