/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

import java.util.Date;
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
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.service.RentService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

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
        model.addAttribute("sysdate", new Date());
        model.addAttribute("page", page);
		return "modules/finance/rentList";
	}
	
	/**
	 * 包租信息
	 * @param paramMap
	 * @param model
	 * @return
	 */
	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = "rentList")
	public String rentList(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<Rent> page = rentService.rentList(new Page<Rent>(request, response),paramMap);
		model.addAttribute("page", page);
		model.addAttribute("sysdate", new Date());
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/rentList";
	}

	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = "form")
	public String form(Rent rent, Model model) {
		model.addAttribute("rent", rent);
		return "modules/finance/rentForm";
	}

	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "save")
	public String save(Rent rent, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, rent)){
			return form(rent, model);
		}
		rentService.save(rent);
		addMessage(redirectAttributes, "保存包租明细'" + rent.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList";
	}
	
	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		rentService.delete(id);
		addMessage(redirectAttributes, "删除包租明细成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList";
	}
	/**
	 * 租金处理类别
	 * @author summer
	 *
	 */
	public enum RentHandleType{
		payRent,receiveRent
	}
	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "rentHandle")
	public String rentHandle(Rent rent, HttpServletRequest request,RedirectAttributes redirectAttributes) {
		String handletype = request.getParameter("handletype");
		if(RentHandleType.payRent.equals(RentHandleType.valueOf(handletype))){
			rentService.payRent(rent);
		}else if(RentHandleType.receiveRent.equals(RentHandleType.valueOf(handletype))){
			rentService.receiveRent(rent);
		}
		rentService.save(rent);
		addMessage(redirectAttributes, "租金处理成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList";
	}

	@RequiresPermissions("finance:rent:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(Rent rent, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "包租数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<Rent> page = rentService.find(new Page<Rent>(request, response, -1), rent); 
    		new ExportExcel("包租数据", Rent.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出包租失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList";
    }

	@RequiresPermissions("finance:rent:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Rent> list = ei.getDataList(Rent.class);
			for (Rent rent : list){
				try{
					Rent temprent = rentService.findByName(rent.getName());
					if (null == temprent){
						BeanValidators.validateWithException(validator, rent);
						successNum++;
					}else{
						rent.setId(temprent.getId());
						failureMsg.append("<br/>包租信息 "+rent.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					rentService.save(rent);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>包租 "+rent.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>地址 "+rent.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条房屋包租，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条房屋包租"+failureMsg);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入房屋包租失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList";
    }
	
	@RequiresPermissions("finance:rent:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋包租数据导入模板.xlsx";
    		List<Rent> list = Lists.newArrayList(); 
    		new ExportExcel("房屋包租数据", Rent.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rent/?repage";
    }


	


}
