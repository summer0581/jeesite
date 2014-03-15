/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

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
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.service.RentService;

/**
 * 包租明细Controller
 * @author 夏天
 * @version 2014-03-15
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/rent")
public class RentController extends BaseController {

	@Autowired
	private RentService rentService;
	
	@ModelAttribute
	public Rent get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return rentService.get(id);
		}else{
			return new Rent();
		}
	}
	
	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = {"list", ""})
	public String list(Rent rent, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			rent.setCreateBy(user);
		}
        Page<Rent> page = rentService.find(new Page<Rent>(request, response), rent); 
        model.addAttribute("page", page);
		return "finance/rentList";
	}

	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = "form")
	public String form(Rent rent, Model model) {
		model.addAttribute("rent", rent);
		return "finance/rentForm";
	}

	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "save")
	public String save(Rent rent, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, rent)){
			return form(rent, model);
		}
		rentService.save(rent);
		addMessage(redirectAttributes, "保存包租明细'" + rent.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rent/?repage";
	}
	
	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		rentService.delete(id);
		addMessage(redirectAttributes, "删除包租明细成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rent/?repage";
	}

}
