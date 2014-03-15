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
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.service.HouseService;

/**
 * 房屋明细Controller
 * @author 夏天
 * @version 2014-03-15
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/house")
public class HouseController extends BaseController {

	@Autowired
	private HouseService houseService;
	
	@ModelAttribute
	public House get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return houseService.get(id);
		}else{
			return new House();
		}
	}
	
	@RequiresPermissions("finance:house:view")
	@RequestMapping(value = {"list", ""})
	public String list(House house, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			house.setCreateBy(user);
		}
        Page<House> page = houseService.find(new Page<House>(request, response), house); 
        model.addAttribute("page", page);
		return "modules/finance/houseList";
	}

	@RequiresPermissions("finance:house:view")
	@RequestMapping(value = "form")
	public String form(House house, Model model) {
		model.addAttribute("house", house);
		return "modules/finance/houseForm";
	}

	@RequiresPermissions("finance:house:edit")
	@RequestMapping(value = "save")
	public String save(House house, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, house)){
			return form(house, model);
		}
		houseService.save(house);
		addMessage(redirectAttributes, "保存房屋明细'" + house.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/finance/house/?repage";
	}
	
	@RequiresPermissions("finance:house:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		houseService.delete(id);
		addMessage(redirectAttributes, "删除房屋明细成功");
		return "redirect:"+Global.getAdminPath()+"/finance/house/?repage";
	}

}
