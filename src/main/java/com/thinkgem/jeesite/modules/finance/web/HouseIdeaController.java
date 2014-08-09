/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.thinkgem.jeesite.modules.finance.entity.HouseIdea;
import com.thinkgem.jeesite.modules.finance.service.HouseIdeaService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 房屋跟进意见Controller
 * @author 夏天
 * @version 2014-08-07
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/houseIdea")
public class HouseIdeaController extends BaseController {

	@Autowired
	private HouseIdeaService houseIdeaService;
	
	@ModelAttribute
	public HouseIdea get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return houseIdeaService.get(id);
		}else{
			return new HouseIdea();
		}
	}
	
	@RequiresPermissions("finance:houseIdea:view")
	@RequestMapping(value = {"list", ""})
	public String list(@RequestParam Map<String, Object> paramMap,HouseIdea houseIdea, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			houseIdea.setCreateBy(user);
		}
        Page<HouseIdea> page = houseIdeaService.find(new Page<HouseIdea>(request, response), houseIdea, paramMap); 
        model.addAttribute("page", page);
        model.addAttribute("paramMap",paramMap);
		return "modules/finance/houseIdeaList";
	}
	
	@RequiresPermissions("finance:houseIdea:view")
	@RequestMapping(value = {"innerList"})
	public String innerList(@RequestParam Map<String, Object> paramMap,HouseIdea houseIdea, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			houseIdea.setCreateBy(user);
		}
		Page<HouseIdea> houseideaPage = new Page<HouseIdea>(request, response);
		houseideaPage.setPageSize(200);
        Page<HouseIdea> page = houseIdeaService.find(houseideaPage, houseIdea,paramMap); 
        model.addAttribute("page", page);
		return "modules/finance/houseIdeaInnerList";
	}

	@RequiresPermissions("finance:houseIdea:view")
	@RequestMapping(value = "form")
	public String form(HouseIdea houseIdea, Model model) {
		model.addAttribute("houseIdea", houseIdea);
		return "modules/finance/houseIdeaForm";
	}
	
	@RequiresPermissions("finance:houseIdea:view")
	@RequestMapping(value = "innerForm")
	public String innerForm(HouseIdea houseIdea, Model model) {
		if(null == houseIdea.getCreateBy() || StringUtils.isBlank(houseIdea.getCreateBy().getName())){
			houseIdea.setCreateBy(UserUtils.getUser());
		}
		if(null == houseIdea.getCreateDate() ){
			houseIdea.setCreateDate(new Date());
		}
		model.addAttribute("houseIdea", houseIdea);
		return "modules/finance/houseIdeaInnerForm";
	}

	@RequiresPermissions("finance:houseIdea:edit")
	@RequestMapping(value = "save")
	public String save(HouseIdea houseIdea, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, houseIdea)){
			return form(houseIdea, model);
		}
		houseIdeaService.save(houseIdea);
		addMessage(redirectAttributes, "保存房屋跟进意见成功");
		return "redirect:"+Global.getAdminPath()+"/finance/houseIdea/?repage";
	}
	
	@RequiresPermissions("finance:houseIdea:edit")
	@RequestMapping(value = "innerSave")
	public String innerSave(HouseIdea houseIdea, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, houseIdea)){
			return form(houseIdea, model);
		}
		houseIdeaService.save(houseIdea);
		model.addAttribute("message", "保存房屋跟进意见成功!");
		return "modules/finance/opersucess";
	}
	
	@RequiresPermissions("finance:houseIdea:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		houseIdeaService.delete(id);
		addMessage(redirectAttributes, "删除房屋跟进意见成功");
		return "redirect:"+Global.getAdminPath()+"/finance/houseIdea/?repage";
	}
	
	@RequiresPermissions("finance:houseIdea:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(HouseIdea houseIdea, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋跟进意见数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<HouseIdea> page = houseIdeaService.find(new Page<HouseIdea>(request, response, -1), houseIdea,new HashMap<String,Object>()); 
    		new ExportExcel("房屋跟进意见数据", HouseIdea.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出房屋跟进意见失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"finance/houseIdea/?repage";
    }

	@RequiresPermissions("finance:houseIdea:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<HouseIdea> list = ei.getDataList(HouseIdea.class);
			for (HouseIdea houseIdea : list){
				try{
					HouseIdea temphouseIdea = houseIdeaService.findByHouse(houseIdea.getName());
					if (null == temphouseIdea){
						BeanValidators.validateWithException(validator, houseIdea);
						successNum++;
					}else{
						houseIdea.setId(temphouseIdea.getId());
						failureMsg.append("<br/>房屋跟进意见信息 "+houseIdea.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					houseIdeaService.save(houseIdea);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>房屋跟进意见 "+houseIdea.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>地址 "+houseIdea.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条房屋跟进意见，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条房屋跟进意见"+failureMsg);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入房屋跟进意见失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/houseIdea/?repage";
    }
	
	@RequiresPermissions("finance:houseIdea:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋跟进意见数据导入模板.xlsx";
    		List<HouseIdea> list = Lists.newArrayList(); 
    		new ExportExcel("房屋跟进意见数据", HouseIdea.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/houseIdea/?repage";
    }

}
