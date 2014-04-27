/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package ${packageName}.${moduleName}.web${subModuleName};

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
import ${packageName}.${moduleName}.entity${subModuleName}.${ClassName};
import ${packageName}.${moduleName}.service${subModuleName}.${ClassName}Service;

/**
 * ${functionName}Controller
 * @author ${classAuthor}
 * @version ${classVersion}
 */
@Controller
@RequestMapping(value = "${r"${adminPath}"}/${urlPrefix}")
public class ${ClassName}Controller extends BaseController {

	@Autowired
	private ${ClassName}Service ${className}Service;
	
	@ModelAttribute
	public ${ClassName} get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return ${className}Service.get(id);
		}else{
			return new ${ClassName}();
		}
	}
	
	@RequiresPermissions("${permissionPrefix}:view")
	@RequestMapping(value = {"list", ""})
	public String list(${ClassName} ${className}, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			${className}.setCreateBy(user);
		}
        Page<${ClassName}> page = ${className}Service.find(new Page<${ClassName}>(request, response), ${className}); 
        model.addAttribute("page", page);
		return "modules/${viewPrefix}List";
	}

	@RequiresPermissions("${permissionPrefix}:view")
	@RequestMapping(value = "form")
	public String form(${ClassName} ${className}, Model model) {
		model.addAttribute("${className}", ${className});
		return "modules/${viewPrefix}Form";
	}

	@RequiresPermissions("${permissionPrefix}:edit")
	@RequestMapping(value = "save")
	public String save(${ClassName} ${className}, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, ${className})){
			return form(${className}, model);
		}
		${className}Service.save(${className});
		addMessage(redirectAttributes, "保存${functionName}'" + ${className}.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/${viewPrefix}/?repage";
	}
	
	@RequiresPermissions("${permissionPrefix}:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		${className}Service.delete(id);
		addMessage(redirectAttributes, "删除${functionName}成功");
		return "redirect:"+Global.getAdminPath()+"/${viewPrefix}/?repage";
	}
	
		@RequiresPermissions("${permissionPrefix}:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(${ClassName} ${className}, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "${functionName}数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
    		Page<${ClassName}> page = ${className}Service.find(new Page<${ClassName}>(request, response, -1), ${className}); 
    		new ExportExcel("${functionName}数据", ${ClassName}.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出${functionName}失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"${viewPrefix}/?repage";
    }

	@RequiresPermissions("${permissionPrefix}:edit")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<${ClassName}> list = ei.getDataList(${ClassName}.class);
			for (${ClassName} ${className} : list){
				try{
					${ClassName} temp${className} = ${className}Service.findByName(${className}.getName());
					if (null == temp${className}){
						BeanValidators.validateWithException(validator, ${className});
						successNum++;
					}else{
						${className}.setId(temp${className}.getId());
						failureMsg.append("<br/>${functionName}信息 "+${className}.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					${className}Service.save(${className});
					successNum++;
				}catch(ConstraintViolationException ex){
					failureMsg.append("<br/>${functionName} "+${className}.getName()+" 导入失败：");
					List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
					for (String message : messageList){
						failureMsg.append(message+"; ");
						failureNum++;
					}
				}catch (Exception ex) {
					failureMsg.append("<br/>地址 "+${className}.getName()+" 导入失败："+ex.getMessage());
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条${functionName}，导入信息如下：");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条${functionName}"+failureMsg);
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入${functionName}失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/${viewPrefix}/?repage";
    }
	
	@RequiresPermissions("${permissionPrefix}:view")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "${functionName}数据导入模板.xlsx";
    		List<${ClassName}> list = Lists.newArrayList(); 
    		new ExportExcel("${functionName}数据", ${ClassName}.class, 2).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/${viewPrefix}/?repage";
    }

}
