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
import com.thinkgem.jeesite.modules.finance.entity.Cutconfig;
import com.thinkgem.jeesite.modules.finance.service.CutconfigService;

/**
 * 包租提成设置Controller
 * @author 夏天
 * @version 2014-05-18
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/cutconfig")
public class CutconfigController extends BaseController {

	@Autowired
	private CutconfigService cutconfigService;
	
	@ModelAttribute
	public Cutconfig get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return cutconfigService.get(id);
		}else{
			return new Cutconfig();
		}
	}
	
	@RequiresPermissions("finance:cutconfig:view")
	@RequestMapping(value = {"list", ""})
	public String list(Cutconfig cutconfig, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			cutconfig.setCreateBy(user);
		}
		
		List<String> cutcodeList = cutconfigService.findCutcodeList();
		model.addAttribute("cutcodeList", cutcodeList);
        Page<Cutconfig> page = cutconfigService.find(new Page<Cutconfig>(request, response), cutconfig); 
        model.addAttribute("page", page);
		return "modules/finance/cutconfigList";
	}

	@RequiresPermissions("finance:cutconfig:view")
	@RequestMapping(value = "form")
	public String form(Cutconfig cutconfig, Model model) {
		model.addAttribute("cutconfig", cutconfig);
		return "modules/finance/cutconfigForm";
	}

	@RequiresPermissions("finance:cutconfig:edit")
	@RequestMapping(value = "save")
	public String save(Cutconfig cutconfig, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, cutconfig)){
			return form(cutconfig, model);
		}
		cutconfigService.save(cutconfig);
		addMessage(redirectAttributes, "保存包租提成设置'" + cutconfig.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/finance/cutconfig/?repage";
	}
	
	@RequiresPermissions("finance:cutconfig:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		cutconfigService.delete(id);
		addMessage(redirectAttributes, "删除包租提成设置成功");
		return "redirect:"+Global.getAdminPath()+"/finance/cutconfig/?repage";
	}
	
		@RequiresPermissions("finance:cutconfig:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(Cutconfig cutconfig, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "包租提成设置数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<Cutconfig> page = cutconfigService.find(new Page<Cutconfig>(request, response, -1), cutconfig); 
    		new ExportExcel("包租提成设置数据", Cutconfig.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出包租提成设置失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"finance/cutconfig/?repage";
    }

	@RequiresPermissions("finance:cutconfig:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Cutconfig> list = ei.getDataList(Cutconfig.class);
			for (Cutconfig cutconfig : list){
				try{
					Cutconfig tempcutconfig = cutconfigService.findByName(cutconfig.getName());
					if (null == tempcutconfig){
						BeanValidators.validateWithException(validator, cutconfig);
						successNum++;
					}else{
						cutconfig.setId(tempcutconfig.getId());
						failureMsg.append("<br/>包租提成设置信息 "+cutconfig.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					cutconfigService.save(cutconfig);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>包租提成设置 "+cutconfig.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>地址 "+cutconfig.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条包租提成设置，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条包租提成设置"+failureMsg);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入包租提成设置失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/cutconfig/?repage";
    }
	
	@RequiresPermissions("finance:cutconfig:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "包租提成设置数据导入模板.xlsx";
    		List<Cutconfig> list = Lists.newArrayList(); 
    		new ExportExcel("包租提成设置数据", Cutconfig.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/cutconfig/?repage";
    }

}
