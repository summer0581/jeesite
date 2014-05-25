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
import com.thinkgem.jeesite.common.utils.MathUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.service.RentMonthService;

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
	public String list(RentMonth rentMonth, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			rentMonth.setCreateBy(user);
		}
        Page<RentMonth> page = rentMonthService.find(new Page<RentMonth>(request, response), rentMonth); 
        model.addAttribute("page", page);
        if(RentMonth.INFOTYPE.rentin.toString().equals(rentMonth.getInfotype())){
        	return "modules/finance/rentinMonthList";
        }else if(RentMonth.INFOTYPE.rentout.toString().equals(rentMonth.getInfotype())){
        	return "modules/finance/rentoutMonthList";
        }
        return "modules/finance/rentinMonthList";
	}
	
	@RequiresPermissions("finance:house:view")
	@RequestMapping(value = {"houseCancelRentlist"})
	public String houseCancelRentlist(RentMonth rentMonth, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			rentMonth.setCreateBy(user);
		}
 
        List<RentMonth> page = rentMonthService.findNoRentOut(); 
        model.addAttribute("page", page);
		return "modules/finance/houseCancelRentList";
	}
	


	@RequiresPermissions("finance:rentMonth:view")
	@RequestMapping(value = "form")
	public String form(RentMonth rentMonth, Model model) {
		model.addAttribute("rentMonth", rentMonth);
		return "modules/finance/rentMonthForm";
	}
	
	@RequiresPermissions("finance:rentMonth:view")
	@RequestMapping(value = "rentinform")
	public String rentinform(RentMonth rentMonth, Model model) {
		if(StringUtils.isBlank(rentMonth.getId())){//如果id为空则表示为新增，则取最新一条的相关数据
			rentMonth.setInfotype(RentMonth.INFOTYPE.rentin.toString());
			List<RentMonth> rentMonthList = rentMonthService.find(rentMonth);
			if(rentMonthList.size()>0){
				rentMonth = rentMonthService.find(rentMonth).get(0);
				rentMonth.setId("");
				int addMonth = rentMonthService.getPayMonthUnit(rentMonth.getPaytype());
				if(null != rentMonth.getLastpaysdate())
					rentMonth.setLastpaysdate(DateUtils.addMonths(rentMonth.getLastpaysdate(), addMonth));
				if(null != rentMonth.getLastpayedate())
					rentMonth.setLastpayedate(DateUtils.addMonths(rentMonth.getLastpayedate(), addMonth));
				if(null != rentMonth.getNextpaydate())
					rentMonth.setNextpaydate(DateUtils.addMonths(rentMonth.getNextpaydate(), addMonth));
				rentMonth.setNextshouldamount(String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth));
			}
		}
		model.addAttribute("vacantPeriodCutconfigs", rentMonthService.findVacantPeriodCutconfigList());
		model.addAttribute("businessSaleCutconfigs", rentMonthService.findBusinessSaleCutconfigList());
		model.addAttribute("rentMonth", rentMonth);
		return "modules/finance/rentinMonthForm";
	}

	@RequiresPermissions("finance:rentMonth:view")
	@RequestMapping(value = "rentoutform")
	public String rentoutform(RentMonth rentMonth, Model model) {
		if(StringUtils.isBlank(rentMonth.getId())){//如果id为空则表示为新增，则取最新一条的相关数据
			rentMonth.setInfotype(RentMonth.INFOTYPE.rentout.toString());
			List<RentMonth> rentMonthList = rentMonthService.find(rentMonth);
			if(rentMonthList.size()>0){
				rentMonth = rentMonthService.find(rentMonth).get(0);
				int addMonth = rentMonthService.getPayMonthUnit(rentMonth.getPaytype());
				rentMonth.setId("");
				if(null != rentMonth.getLastpaysdate())
					rentMonth.setLastpaysdate(DateUtils.addMonths(rentMonth.getLastpaysdate(), addMonth));
				if(null != rentMonth.getLastpayedate())
					rentMonth.setLastpayedate(DateUtils.addMonths(rentMonth.getLastpayedate(), addMonth));
				if(null != rentMonth.getNextpaydate())
					rentMonth.setNextpaydate(DateUtils.addMonths(rentMonth.getNextpaydate(), addMonth));
				rentMonth.setNextshouldamount(String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth));
				rentMonth.setAmountreceived(String.valueOf(MathUtils.sumInt(rentMonth.getAmountreceived(),String.valueOf(Integer.valueOf(rentMonth.getRentmonth())*addMonth))));
			}
		}
		model.addAttribute("vacantPeriodCutconfigs", rentMonthService.findVacantPeriodCutconfigList());
		model.addAttribute("businessSaleCutconfigs", rentMonthService.findBusinessSaleCutconfigList());
		model.addAttribute("rentMonth", rentMonth);
		return "modules/finance/rentoutMonthForm";
	} 

	

	@RequiresPermissions("finance:rentMonth:edit")
	@RequestMapping(value = "save")
	public String save(RentMonth rentMonth, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, rentMonth)){
			return form(rentMonth, model);
		}
		rentMonthService.save(rentMonth);
		addMessage(redirectAttributes, "保存包租月记录成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rentMonth/?infotype="+rentMonth.getInfotype()+"&rent.id="+rentMonth.getRent().getId();
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
