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
import com.thinkgem.jeesite.modules.finance.entity.Customer;
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.service.CustomerService;

/**
 * 客户信息Controller
 * @author 夏天
 * @version 2014-04-21
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/customer")
public class CustomerController extends BaseController {

	@Autowired
	private CustomerService customerService;
	
	@ModelAttribute
	public Customer get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return customerService.get(id);
		}else{
			return new Customer();
		}
	}
	
	@RequiresPermissions("finance:customer:view")
	@RequestMapping(value = {"list", ""})
	public String list(Customer customer, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			customer.setCreateBy(user);
		}
        Page<Customer> page = customerService.find(new Page<Customer>(request, response), customer); 
        model.addAttribute("page", page);
		return "modules/finance/customerList";
	}

	@RequiresPermissions("finance:customer:view")
	@RequestMapping(value = "form")
	public String form(Customer customer, Model model) {
		model.addAttribute("customer", customer);
		return "modules/finance/customerForm";
	}

	@RequiresPermissions("finance:customer:edit")
	@RequestMapping(value = "save")
	public String save(Customer customer, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, customer)){
			return form(customer, model);
		}
		customerService.save(customer);
		addMessage(redirectAttributes, "保存客户信息'" + customer.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/finance/customer/?repage";
	}
	
	@RequiresPermissions("finance:customer:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		customerService.delete(id);
		addMessage(redirectAttributes, "删除客户信息成功");
		return "redirect:"+Global.getAdminPath()+"/finance/customer/?repage";
	}
	
	/**
	 * 客户选择列表
	 */
	@RequiresPermissions("finance:customer:view")
	@RequestMapping(value = "selectList")
	public String selectList(Customer customer, HttpServletRequest request, HttpServletResponse response, Model model) {
        list(customer, request, response, model);
        String listtype = request.getParameter("listtype");
        if("landlord".equals(listtype)){
        	return "modules/finance/landlordSelectList";
        }else if("tenant".equals(listtype)){
        	return "modules/finance/tenantSelectList";
        }
		return "modules/finance/customerSelectList";
	}
	
	

	@RequiresPermissions("finance:customer:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(Customer customer, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "客户信息数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<Customer> page = customerService.find(new Page<Customer>(request, response, -1), customer); 
    		new ExportExcel("客户信息数据", Customer.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出客户信息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/customer/?repage";
    }

	@RequiresPermissions("finance:customer:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<Customer> list = ei.getDataList(Customer.class);
			for (Customer customer : list){
				try{
					Customer tempcustomer = customerService.findByName(customer.getName());
					if (null == tempcustomer){
						BeanValidators.validateWithException(validator, customer);
						successNum++;
					}else{
						customer.setId(tempcustomer.getId());
						failureMsg.append("<br/>客户信息 "+customer.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					customerService.save(customer);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>客户 "+customer.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>姓名"+customer.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条客户信息，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条客户信息"+failureMsg);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入客户信息失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/customer/?repage";
    }
	
	@RequiresPermissions("finance:customer:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "客户信息数据导入模板.xlsx";
    		List<Customer> list = Lists.newArrayList(); 
    		new ExportExcel("客户信息数据", Customer.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/customer/?repage";
    }
}
