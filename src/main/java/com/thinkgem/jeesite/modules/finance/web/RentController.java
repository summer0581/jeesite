/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.poi.ss.usermodel.Row;
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
import com.thinkgem.jeesite.modules.finance.entity.House;
import com.thinkgem.jeesite.modules.finance.entity.Rent;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.excel.entity.Excel2House4BatchBank;
import com.thinkgem.jeesite.modules.finance.excel.entity.Excel2House4ZuoZhang;
import com.thinkgem.jeesite.modules.finance.excel.entity.Excel2Rent4WillReceive;
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
	
	/**
	 * 即将要付租的房子列表
	 * @param paramMap
	 * @param model
	 * @return
	 * @throws Exception 
	 */
	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = "rentList4WillRentinPayfor")
	public String rentList4WillRentinPayfor(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		if(null == paramMap.get("rentin_nextpayedate")){
			paramMap.put("rentin_nextpayedate",DateUtils.formatDate(DateUtils.addDays(new Date(), 7), "yyyy-MM-dd"));
		}
		paramMap.put("order", "rms.nextpaydate,r.business_num");
		Page<Rent> pages = new Page<Rent>(request, response);
		pages.setPageSize(50);
		Page<Rent> page = rentService.rentInListWillNeedPayNextMonth(pages,paramMap);
		Map<String,String> rentsum = rentService.rentListSumColumn(paramMap,RentMonth.INFOTYPE.rentin);
		model.addAttribute("page", page);
		model.addAttribute("sysdate", new Date());
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("rentsum", rentsum);
		return "modules/finance/rentList4willrentinpayfor";
	}

	/**
	 * 即将要收租的房子列表
	 * @param paramMap
	 * @param model
	 * @return
	 */
	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = "rentList4WillRentoutReceive")
	public String rentList4WillRentoutReceive(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(null == paramMap.get("rentout_nextpayedate")){
			paramMap.put("rentout_nextpayedate",DateUtils.formatDate(DateUtils.addDays(new Date(), 7), "yyyy-MM-dd"));
		}
		paramMap.put("order", "rms2.nextpaydate,r.business_num");
		paramMap.put("notcancelrent", "true");
		
		Page<Rent> pages = new Page<Rent>(request, response);
		pages.setPageSize(50);
		Page<Rent> page = rentService.rentOutListWillNeedPayNextMonth(pages,paramMap);
		Map<String,String> rentsum = rentService.rentListSumColumn(paramMap,RentMonth.INFOTYPE.rentout);
		model.addAttribute("page", page);
		model.addAttribute("sysdate", new Date());
		model.addAttribute("paramMap", paramMap);
		model.addAttribute("rentsum", rentsum);
		return "modules/finance/rentList4willrentoutreceive";
	}
	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = "form")
	public String form(Rent rent, Model model) {
		model.addAttribute("rent", rent);
		if(StringUtils.isBlank(rent.getId())){
			rent.setBusiness_num(rentService.getMaxBusinessNum());
		}
		return "modules/finance/rentForm";
	}
	//快速录入合同
	@RequiresPermissions("finance:rent:view")
	@RequestMapping(value = "quickluruhetongform")
	public String quickluruhetongform(Rent rent, Model model) {
		model.addAttribute("rent", rent);
		if(StringUtils.isBlank(rent.getId())){
			rent.setBusiness_num(rentService.getMaxBusinessNum());
		}
		return "modules/finance/rentQuickLuruHetongForm";
	}

	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "save")
	public String save(Rent rent, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, rent)){
			return form(rent, model);
		}
		
		rentService.save(rent);
		addMessage(redirectAttributes, "保存包租明细'" + rent.getName() + "'成功");
		return form(rent, model);
	}
	
	//快速录入合同的保存
	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "save4quickLuruHetong")
	public String save4quickLuruHetong(Rent rent, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, rent)){
			return form(rent, model);
		}
		try{
			rentService.save4quickLuruHetong(rent);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出包租失败！失败信息："+e.getMessage());
			return quickluruhetongform(rent, model);
		}
		addMessage(redirectAttributes, "保存包租明细'" + rent.getName() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/finance/oper/sucess?repage";
	}
	
	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		rentService.delete(id);
		addMessage(redirectAttributes, "删除包租明细成功");
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList";
	}
	
	@RequiresPermissions("finance:rent:edit")
	@RequestMapping(value = "batchProcessRentMonth")
	public String batchProcessRentMonth(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, Model model,RedirectAttributes redirectAttributes){
		String rentids = (String)paramMap.get("rentids");
		String infotype = (String)paramMap.get("infotype");

		rentService.batchProcessRentMonth(rentids, infotype);
		model.addAttribute("paramMap", paramMap);
		addMessage(redirectAttributes, "租金处理成功");
		if(RentMonth.INFOTYPE.rentout.equals(RentMonth.INFOTYPE.valueOf(infotype))){
			return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList4WillRentoutReceive";
		}else{
			return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList4WillRentinPayfor";
		}
		
		
	}

	@RequiresPermissions("finance:rent:view")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "包租数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Page<Rent> pages = new Page<Rent>(request, response, -1);
            pages.setPageSize(500);
    		Page<Rent> page = rentService.rentList(pages, paramMap); 
    		new ExportExcel("包租数据", Rent.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出包租失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList?repage";
    }
	
	@RequiresPermissions("finance:rent:view")
    @RequestMapping(value = "export4willreceive", method=RequestMethod.POST)
    public String exportFile4willreceive(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "包租数据"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Page<Rent> pages = new Page<Rent>(request, response, -1);
            pages.setPageSize(500);
    		Page<Rent> page = rentService.rentList(pages, paramMap); 
    		new ExportExcel("包租数据", Excel2Rent4WillReceive.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出包租失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rent/rentList?repage";
    }

	@RequiresPermissions("finance:rent:view")
    @RequestMapping(value = "export4bank", method=RequestMethod.POST)
    public String exportFile4Bank(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋数据(为批量导入银行)"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Page<Rent> pages = new Page<Rent>(request, response, -1);
            pages.setPageSize(2500);
            Page<Rent> page = rentService.rentInListWillNeedPayNextMonth(pages, paramMap); 
            List<House>  houseList = new ArrayList<House>();
            List<Rent> rentList = page.getList();
            for(int i = 0 ; i < rentList.size() ; i ++){
            	Rent rent = rentList.get(i);
            	rent.getHouse().setTemp_index(i+1);
            	houseList.add(rent.getHouse());
            }
    		new ExportExcel("房屋数据(为批量导入银行)", Excel2House4BatchBank.class).setDataList(houseList).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出房屋失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rentList/?repage";
    }
	
	@RequiresPermissions("finance:rent:view")
    @RequestMapping(value = "export4zuozhang", method=RequestMethod.POST)
    public String exportFile4Zuozhang(@RequestParam Map<String, Object> paramMap, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋数据(做账依据)"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            Page<Rent> pages = new Page<Rent>(request, response, -1);
            pages.setPageSize(2500);
            if(null == paramMap.get("rentin_nextpayedate")){
    			paramMap.put("rentin_nextpayedate",DateUtils.formatDate(DateUtils.addDays(new Date(), 7), "yyyy-MM-dd"));
    		}
    		paramMap.put("order", "rms.nextpaydate,r.business_num");
            Page<Rent> page = rentService.rentInListWillNeedPayNextMonth(pages, paramMap); 
            Map<String,String> rentsum = rentService.rentListSumColumn(paramMap,RentMonth.INFOTYPE.rentin);
            List<Rent> rentList = page.getList();

            ExportExcel excel = new ExportExcel("房屋数据(做账依据)", Excel2House4ZuoZhang.class).setDataList(rentList);
            Row row1 = excel.addRow();
            excel.mergeCell(row1.getRowNum(), row1.getRowNum(), 0, 1);
    		excel.addCell(row1, 0, "合计");
    		excel.addCell(row1, 2, rentsum.get("rentrentmonthsum"));
    		excel.addCell(row1, 5, rentsum.get("rentnextshouldpaysum"));
    		excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出房屋失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/finance/rentList/?repage";
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
					}else{
						rent.setId(temprent.getId());
						failureMsg.append("<br/>包租信息 "+rent.getName()+" 已存在;进行更新; ");
						failureNum++;
					}
					rentService.save4Excel(rent);
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
