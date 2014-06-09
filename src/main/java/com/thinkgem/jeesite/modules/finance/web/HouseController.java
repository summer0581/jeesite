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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.beanvalidator.BeanValidators;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.service.HouseService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

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
	@RequestMapping(value = {"houseNoRentinlist"})
	public String houseNoRentinlist(House house, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			house.setCreateBy(user);
		}
		Page<House> pages = new Page<House>(request, response);
		pages.setPageSize(100);
        Page<House> page = houseService.findNoRentin(pages, house); 
        model.addAttribute("page", page);
		return "modules/finance/houseNoRentinList";
	}
	
	@RequiresPermissions("finance:house:view")
	@RequestMapping(value = {"houseCancelRentlist"})
	public String houseCancelRentlist(House house, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			house.setCreateBy(user);
		}
		Page<House> pages = new Page<House>(request, response);
		pages.setPageSize(100);
        Page<House> page = houseService.findHouseCancelRent(pages, house); 
        model.addAttribute("page", page);
		return "modules/finance/houseCancelRentList";
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
	
	/**
	 * 房屋选择列表
	 */
	@RequiresPermissions("finance:house:view")
	@RequestMapping(value = "selectList")
	public String selectList(House house, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			house.setCreateBy(user);
		}
        Page<House> page = houseService.findNoRelation(new Page<House>(request, response), house); 
        model.addAttribute("page", page);
		return "modules/finance/houseSelectList";
	}
	
	/**
	 * 通过编号获取房屋名称
	 */
	@RequiresPermissions("finance:house:view")
	@ResponseBody
	@RequestMapping(value = "findByIds")
	public String findByIds(String ids) {
		List<Object[]> list = houseService.findByIds(ids);
		return JsonMapper.nonDefaultMapper().toJson(list);
	}
	
	@RequiresPermissions("finance:house:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(House house, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Page<House> pages = new Page<House>(request, response, -1);
            pages.setPageSize(500);
    		 Page<House> page = houseService.find(pages, house); 
    		new ExportExcel("房屋数据", House.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出房屋失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/house/?repage";
    }
	
	@RequiresPermissions("finance:house:view")
    @RequestMapping(value = "export4bank", method=RequestMethod.POST)
    public String exportFile4Bank(House house, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋数据(为批量导入银行)"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Page<House> pages = new Page<House>(request, response, -1);
            pages.setPageSize(500);
    		Page<House> page = houseService.find(pages, house); 
    		new ExportExcel("房屋数据(为批量导入银行)", House.class, 1,1).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出房屋失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/house/?repage";
    }


	@RequiresPermissions("finance:house:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<House> list = ei.getDataList(House.class);
			House temphouse = null;
			for (House house : list){
				try{
					temphouse = houseService.findByName(house.getName());
					if (null == temphouse){
						BeanValidators.validateWithException(validator, house);
						houseService.save(house);
						successNum++;
					}else{
						house.setId(temphouse.getId());
						houseService.save(house);
						failureMsg.append("<br/>地址 "+house.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>地址 "+house.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>地址 "+house.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条房屋，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条房屋"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入房屋失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/house/list";
    }
	
	@RequiresPermissions("finance:house:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋数据导入模板.xlsx";
    		List<House> list = Lists.newArrayList(); 
    		new ExportExcel("房屋数据", House.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/house/?repage";
    }


}
