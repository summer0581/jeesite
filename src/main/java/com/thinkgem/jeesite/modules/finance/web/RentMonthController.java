/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

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
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.service.RentMonthService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 包租月记录Controller
 * @author 夏天
 * @version 2014-05-06
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/rentMonth")
public class RentMonthController extends BaseController {

	@Autowired
	private RentMonthService rentMonthService;
	
	@ModelAttribute
	public RentMonth get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return rentMonthService.get(id);
		}else{
			return new RentMonth();
		}
	}
	
	@RequiresPermissions("finance:rentMonth:view")
	@RequestMapping(value = {"list", ""})
	public String list(@RequestParam Map<String, Object> paramMap,RentMonth rentMonth, HttpServletRequest request, HttpServletResponse response, Model model) {
		String viewtype = "";
		if(null != paramMap){
			viewtype = (String)paramMap.get("viewtype");
		}
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			rentMonth.setCreateBy(user);
		}
		Page<RentMonth> page = new Page<RentMonth>(request, response);
		page.setPageSize(10);
        page = rentMonthService.find(page, rentMonth); 
        model.addAttribute("page", page);
        model.addAttribute("rent", rentMonth.getRent());
        model.addAttribute("paramMap", paramMap);
        if("noreturnlist".equals(viewtype)){
            if(RentMonth.INFOTYPE.rentin.toString().equals(rentMonth.getInfotype())){
            	return "modules/finance/rentinMonthList4Secret";
            }else if(RentMonth.INFOTYPE.rentout.toString().equals(rentMonth.getInfotype())){
            	return "modules/finance/rentoutMonthList4Secret";
            }
        }else{
            if(RentMonth.INFOTYPE.rentin.toString().equals(rentMonth.getInfotype())){
            	return "modules/finance/rentinMonthList";
            }else if(RentMonth.INFOTYPE.rentout.toString().equals(rentMonth.getInfotype())){
            	return "modules/finance/rentoutMonthList";
            }
        }
        return "modules/finance/rentinMonthList";
	}
	


	@RequiresPermissions("finance:rentMonth:view")
	@RequestMapping(value = "form")
	public String form(RentMonth rentMonth, Model model) {
		model.addAttribute("rentMonth", rentMonth);
		return "modules/finance/rentMonthForm";
	}
	
	@RequiresPermissions("finance:rentMonth:view")
	@RequestMapping(value = "rentinform")
	public String rentinform(@RequestParam Map<String, Object> paramMap,RentMonth rentMonth, Model model) {
		rentMonth = rentMonthService.setNewRentinMonth(rentMonth);
		model.addAttribute("vacantPeriodCutconfigs", rentMonthService.findVacantPeriodCutconfigList());
		model.addAttribute("businessSaleCutconfigs", rentMonthService.findBusinessSaleCutconfigList());
		model.addAttribute("rentMonth", rentMonth);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/rentinMonthForm";
	}

	@RequiresPermissions("finance:rentMonth:view")
	@RequestMapping(value = "rentoutform")
	public String rentoutform(@RequestParam Map<String, Object> paramMap,RentMonth rentMonth, Model model) {
		rentMonth = rentMonthService.setNewRentoutMonth(rentMonth);
		model.addAttribute("vacantPeriodCutconfigs", rentMonthService.findVacantPeriodCutconfigList());
		model.addAttribute("businessSaleCutconfigs", rentMonthService.findBusinessSaleCutconfigList());
		model.addAttribute("rentMonth", rentMonth);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/rentoutMonthForm";
	} 

	

	@RequiresPermissions("finance:rentMonth:edit")
	@RequestMapping(value = "save")
	public String save(RentMonth rentMonth, @RequestParam Map<String, Object> paramMap,Model model, RedirectAttributes redirectAttributes) {
		String viewtype = "";
		if(null != paramMap){
			viewtype = (String)paramMap.get("viewtype");
		}
		if (!beanValidator(model, rentMonth)){
			return form(rentMonth, model);
		}
		//rentMonth.setBusi_departleader(null); 2014.8.16 万科金色家园2区612 这个房子的部长的第一条租进月记录的部长死活保存不成功，只能通过代码强行清除它原来的部长，再修改，就没问题了
		//2014.8.17 仍然未找到解决办法，只能通过Request直接获取值来解决此问题，后续还有待观察
		//2014.8.18终于找到问题所在，是因为hibernate的session共享bean导致，如果组长，部长，经理，业务员，有一样的人，就会导致两项被绑定，指向同一个持久bean，所以。。
		User person = new User();
		User manager = new User();
		User departleader = new User();
		User teamleader = new User();
		person.setId((String)paramMap.get("person.id"));
		manager.setId((String)paramMap.get("busi_manager.id"));
		departleader.setId((String)paramMap.get("busi_departleader.id"));
		teamleader.setId((String)paramMap.get("busi_teamleader.id"));
		rentMonth.setPerson(person);
		rentMonth.setBusi_manager(manager);
		rentMonth.setBusi_departleader(departleader);
		rentMonth.setBusi_teamleader(teamleader);
		rentMonth.setAdd_from(viewtype);
		rentMonth.setAudit_state("N");
		rentMonthService.save(rentMonth);
		addMessage(redirectAttributes, "保存包租月记录成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rentMonth/?infotype="+rentMonth.getInfotype()+"&rent.id="+rentMonth.getRent().getId()+"&viewtype="+viewtype;
	}
	
	@RequiresPermissions("finance:rentMonth:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		RentMonth rentMonth = rentMonthService.get(id);
		rentMonthService.delete(id);
		addMessage(redirectAttributes, "删除包租月记录成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rentMonth/?infotype="+rentMonth.getInfotype()+"&rent.id="+rentMonth.getRent().getId();
	}
	
		@RequiresPermissions("finance:rentMonth:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(RentMonth rentMonth, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "包租月记录数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<RentMonth> page = rentMonthService.find(new Page<RentMonth>(request, response, -1), rentMonth); 
    		new ExportExcel("包租月记录数据", RentMonth.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出包租月记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"finance/rentMonth/?repage";
    }
		


	@RequiresPermissions("finance:rentMonth:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<RentMonth> list = ei.getDataList(RentMonth.class);
			for (RentMonth rentMonth : list){
				try{
					RentMonth temprentMonth = rentMonthService.findByName(rentMonth.getName());
					if (null == temprentMonth){
						BeanValidators.validateWithException(validator, rentMonth);
						successNum++;
					}else{
						rentMonth.setId(temprentMonth.getId());
						failureMsg.append("<br/>包租月记录信息 "+rentMonth.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					rentMonthService.save(rentMonth);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>包租月记录 "+rentMonth.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>地址 "+rentMonth.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条包租月记录，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条包租月记录"+failureMsg);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入包租月记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rentMonth/?repage";
    }
	
	@RequiresPermissions("finance:rentMonth:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "包租月记录数据导入模板.xlsx";
    		List<RentMonth> list = Lists.newArrayList(); 
    		new ExportExcel("包租月记录数据", RentMonth.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rentMonth/?repage";
    }

	

}
