/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.beanvalidator.BeanValidators;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.finance.entity.HouseAreaRole;
import com.thinkgem.jeesite.modules.finance.service.HouseAreaRoleService;

/**
 * 房屋区域权限设置Controller
 * @author 夏天
 * @version 2014-07-30
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/houseAreaRole")
public class HouseAreaRoleController extends BaseController {

	@Autowired
	private HouseAreaRoleService houseAreaRoleService;
	
	@ModelAttribute
	public HouseAreaRole get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return houseAreaRoleService.get(id);
		}else{
			return new HouseAreaRole();
		}
	}
	
	@RequiresPermissions("finance:houseAreaRole:view")
	@RequestMapping(value = {"list", ""})
	public String list(HouseAreaRole houseAreaRole, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			houseAreaRole.setCreateBy(user);
		}
        Page<HouseAreaRole> page = houseAreaRoleService.find(new Page<HouseAreaRole>(request, response), houseAreaRole); 
        model.addAttribute("page", page);
		return "modules/finance/houseAreaRoleList";
	}

	@RequiresPermissions("finance:houseAreaRole:view")
	@RequestMapping(value = "form")
	public String form(HouseAreaRole houseAreaRole, Model model) {
		model.addAttribute("houseAreaRole", houseAreaRole);
		model.addAttribute("allareas",houseAreaRoleService.findAllAreas());
		return "modules/finance/houseAreaRoleForm";
	}

	@RequiresPermissions("finance:houseAreaRole:edit")
	@RequestMapping(value = "save")
	public String save(HouseAreaRole houseAreaRole, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, houseAreaRole)){
			return form(houseAreaRole, model);
		}
		houseAreaRoleService.save(houseAreaRole);
		addMessage(redirectAttributes, "保存房屋区域权限设置'" + houseAreaRole.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/finance/houseAreaRole/?repage";
	}
	
	@RequiresPermissions("finance:houseAreaRole:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		houseAreaRoleService.delete(id);
		addMessage(redirectAttributes, "删除房屋区域权限设置成功");
		return "redirect:"+Global.getAdminPath()+"/finance/houseAreaRole/?repage";
	}
	
		@RequiresPermissions("finance:houseAreaRole:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(HouseAreaRole houseAreaRole, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋区域权限设置数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<HouseAreaRole> page = houseAreaRoleService.find(new Page<HouseAreaRole>(request, response, -1), houseAreaRole); 
    		new ExportExcel("房屋区域权限设置数据", HouseAreaRole.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出房屋区域权限设置失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"finance/houseAreaRole/?repage";
    }

	@RequiresPermissions("finance:houseAreaRole:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<HouseAreaRole> list = ei.getDataList(HouseAreaRole.class);
			for (HouseAreaRole houseAreaRole : list){
				try{
					HouseAreaRole temphouseAreaRole = houseAreaRoleService.findByName(houseAreaRole.getName());
					if (null == temphouseAreaRole){
						BeanValidators.validateWithException(validator, houseAreaRole);
						successNum++;
					}else{
						houseAreaRole.setId(temphouseAreaRole.getId());
						failureMsg.append("<br/>房屋区域权限设置信息 "+houseAreaRole.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					houseAreaRoleService.save(houseAreaRole);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>房屋区域权限设置 "+houseAreaRole.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>地址 "+houseAreaRole.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条房屋区域权限设置，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条房屋区域权限设置"+failureMsg);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入房屋区域权限设置失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/houseAreaRole/?repage";
    }
	
	@RequiresPermissions("finance:houseAreaRole:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋区域权限设置数据导入模板.xlsx";
    		List<HouseAreaRole> list = Lists.newArrayList(); 
    		new ExportExcel("房屋区域权限设置数据", HouseAreaRole.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/houseAreaRole/?repage";
    }

}
