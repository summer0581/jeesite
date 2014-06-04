/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.thinkgem.jeesite.modules.finance.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportTemplateExcel;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.finance.entity.RentMonth;
import com.thinkgem.jeesite.modules.finance.service.StatsRentService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 空置期提成Controller
 * @author 夏天
 * @version 2014-04-13
 */
@Controller
@RequestMapping(value = "${adminPath}/finance/stats")
public class StatsRentController extends BaseController {

	@Autowired
	private StatsRentService statsRentService;
	
	private static Map<String,Map<String,Object>> vacantPeriodMap = new HashMap<String,Map<String,Object>>();
	private static Map<String,Map<String,Object>> vacantPeriod4PersonMap = new HashMap<String,Map<String,Object>>();
	private static Map<String,Map<String,Object>> vacantPeriodDetail4PersonMap = new HashMap<String,Map<String,Object>>();
	private static Map<String,Map<String,Object>> businessCutMap = new HashMap<String,Map<String,Object>>();
	private static Map<String,Map<String,Object>> businessCut4PersonMap = new HashMap<String,Map<String,Object>>();
	private static Map<String,Map<String,Object>> businessCutDetail4PersonMap = new HashMap<String,Map<String,Object>>();
	
	
	@RequiresPermissions("finance:stats:vacantPeriod") 
	@RequestMapping(value = {"vacantPeriod"})
	public String vacantPeriod(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.vacantPeriod(paramMap);
		vacantPeriodMap.put(UserUtils.getUser().getLoginName()+UserUtils.getUser().getLoginIp(), result);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsVacantPeriodList";
	}

	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"vacantPeriod4Person"})
	public String vacantPeriod4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.vacantPeriod4Person(paramMap);
		vacantPeriod4PersonMap.put(UserUtils.getUser().getLoginName()+UserUtils.getUser().getLoginIp(), result);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsVacantPeriod4PersonList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"vacantPeriodDetail4Person"})
	public String vacantPeriodDetail4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.vacantPeriodDetail4Person(paramMap);
		vacantPeriodDetail4PersonMap.put(UserUtils.getUser().getLoginName()+UserUtils.getUser().getLoginIp(), result);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		
		return "modules/finance/statsVacantPeriodDetail4PersonList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"businessCut"})
	public String businessCut(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.businessSaleCut(paramMap);
		businessCutMap.put(UserUtils.getUser().getLoginName()+UserUtils.getUser().getLoginIp(), result);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsBusinessCutList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"businessCut4Person"})
	public String businessCut4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.businessSaleCut4PersonList(paramMap);
		businessCut4PersonMap.put(UserUtils.getUser().getLoginName()+UserUtils.getUser().getLoginIp(), result);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		return "modules/finance/statsBusinessCut4PersonList";
	}
	
	@RequiresPermissions("finance:stats:vacantPeriod")
	@RequestMapping(value = {"businessCutDetail4Person"})
	public String businessCutDetail4Person(@RequestParam Map<String, Object> paramMap, Model model) throws Exception{
		Map<String,Object> result = statsRentService.businessSaleCutDetail4PersonList(paramMap);
		businessCutDetail4PersonMap.put(UserUtils.getUser().getLoginName()+UserUtils.getUser().getLoginIp(), result);
		model.addAllAttributes(result);
		model.addAttribute("paramMap", paramMap);
		
		return "modules/finance/statsBusinessCutDetail4PersonList";
	}


	@RequiresPermissions("finance:stats:vacantPeriod")
    @RequestMapping(value = "export/vacantPeriod")
    public String export4vacantPeriod(@RequestParam Map<String, Object> paramMap,HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋包租空置期提成.xlsx";
            int curnum = 2;//模板中已存在两行，故从2开始计算
            String ss = FileUtils.getPathByCLassPath("com/thinkgem/jeesite/modules/finance/excel/kz1.xlsx"); 
            ExportTemplateExcel ete = new ExportTemplateExcel((String)paramMap.get("rentout_sdate_begin")+"-"+(String)paramMap.get("rentout_sdate_end")+"空置期提成",ss,curnum);
            Map<String,Object> result = getCurruserMap(vacantPeriodMap);
   			List<Map<String,Object>> list = (List<Map<String,Object>>)result.get("list");
   			Map<String,Object> total = (Map<String,Object>)result.get("total");
   			for(Map<String,Object> map : list){
         	   RentMonth rentinmonth = (RentMonth)map.get("rentinmonth");
         	   RentMonth rentoutmonth = (RentMonth)map.get("rentmonth");
         	   long vacantperiodconfig = (Long)map.get("vacantperiodconfig");
	         	long vacantperiod = (Long)map.get("vacantperiod");
	         	long rentin_cut = (Long)map.get("rentin_cut");
	         	long rentout_cut = (Long)map.get("rentout_cut");
	         	long teamleader_cut = (Long)map.get("teamleader_cut");
	         	long departleader_cut = (Long)map.get("departleader_cut");
	         	long manager_cut = (Long)map.get("manager_cut");
	         	Row row = ete.addRow();
	         	ete.addCell(row, 0, rentinmonth.getRent().getBusiness_num());
	        	ete.addCell(row, 1, rentinmonth.getRent().getHouse().getName());
	        	ete.addCell(row, 2, rentinmonth.getPerson().getName());
	        	ete.addCell(row, 3, DateUtils.formatDate(rentinmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentinmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 4, DateUtils.formatDate(rentoutmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentoutmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 5, rentoutmonth.getRentmonth());
	        	ete.addCell(row, 6, rentoutmonth.getPerson().getName());
	        	ete.addCell(row, 7, rentinmonth.getBusi_departleader().getName());
	        	ete.addCell(row, 8, vacantperiodconfig);
	        	ete.addCell(row, 9, vacantperiod);
	        	ete.addCell(row, 10, rentin_cut);
	        	ete.addCell(row, 11, rentout_cut);
	        	ete.addCell(row, 12, teamleader_cut);
	        	ete.addCell(row, 13, departleader_cut);
	        	ete.addCell(row, 14, manager_cut);
   			}
   			long rentin_cut_total = (Long)total.get("rentin_cut_total");
   			long rentout_cut_total = (Long)total.get("rentout_cut_total");
   			long teamleader_cut_total = (Long)total.get("teamleader_cut_total");
   			long departleader_cut_total = (Long)total.get("departleader_cut_total");
   			long manager_cut_total = (Long)total.get("manager_cut_total");
   			Row row = ete.addRow();
            ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 9);
            ete.addCell(row, 0, "合计", 2);
            ete.addCell(row, 10, rentin_cut_total);
            ete.addCell(row, 11, rentout_cut_total);
            ete.addCell(row, 12, teamleader_cut_total);
            ete.addCell(row, 13, departleader_cut_total);
            ete.addCell(row, 14, manager_cut_total);
            ete.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/modules/finance/statsVacantPeriodList?repage";
    }
	
	@RequiresPermissions("finance:stats:vacantPeriod")
    @RequestMapping(value = "export/vacantPeriod4Person")
    public String export4vacantPeriod4Person(@RequestParam Map<String, Object> paramMap,HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋包租空置期提成个人总计.xlsx";
            int curnum = 2;//模板中已存在两行，故从2开始计算
            String ss = FileUtils.getPathByCLassPath("com/thinkgem/jeesite/modules/finance/excel/kz2.xlsx");
            ExportTemplateExcel ete = new ExportTemplateExcel((String)paramMap.get("rentout_sdate_begin")+"-"+(String)paramMap.get("rentout_sdate_end")+"空置期提成(个人总计)",ss,curnum);

            Map<String,Object> result = getCurruserMap(vacantPeriod4PersonMap);
   			List<Map<String,Object>> list = (List<Map<String,Object>>)result.get("list");
   			Map<String,Object> total = (Map<String,Object>)result.get("total");
   			for(Map<String,Object> map : list){
         	   User person = (User)map.get("person");
         	   int rentinPeriodTotal = null != map.get("rentinPeriodTotal")? (Integer)map.get("rentinPeriodTotal"):0;
         	   int rentoutPeriodTotal = null != map.get("rentoutPeriodTotal")? (Integer)map.get("rentoutPeriodTotal"):0;
         	   int periodTotal = null != map.get("periodTotal")? (Integer)map.get("periodTotal"):0;

	         	Row row = ete.addRow();
	        	ete.addCell(row, 0, person.getName());
	        	ete.addCell(row, 1, rentinPeriodTotal);
	        	ete.addCell(row, 2, rentoutPeriodTotal);
	        	ete.addCell(row, 3, periodTotal);

   			}
   			long rentin_cut_total = null != total.get("rentin_cut_total")? (Integer)total.get("rentin_cut_total"):0;
   			long rentout_cut_total = null != total.get("rentout_cut_total")? (Integer)total.get("rentout_cut_total"):0;
   			long cut_total = null != total.get("cut_total")? (Integer)total.get("cut_total"):0;

   			Row row = ete.addRow();
            ete.addCell(row, 0, "合计", 2);
            ete.addCell(row, 1, rentin_cut_total);
            ete.addCell(row, 2, rentout_cut_total);
            ete.addCell(row, 3, cut_total);

            ete.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/modules/finance/statsVacantPeriodList?repage";
    }
	
	@RequiresPermissions("finance:stats:vacantPeriod")
    @RequestMapping(value = "export/vacantPeriodDetail4Person")
    public String export4vacantPeriodDetail4Person(@RequestParam Map<String, Object> paramMap,HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String personid = (String)paramMap.get("personid");
		User person = UserUtils.getUserById(personid);
		String[] rentinHeaders = {"编号","地址","租进业务员","承租情况","出租情况","租金","租出业务员","部长","空置期设置","空置期天数","租进业务员提成","组长提成","部长提成","经理提成"};
		String[] rentoutHeaders = {"编号","地址","租进业务员","承租情况","出租情况","租金","租出业务员","部长","空置期设置","空置期天数","租出业务员提成"};
		try {
            String fileName = "房屋包租空置期提成个人明细.xlsx";
            int curnum = 1;//模板中已存在一行，故从0开始计算
            String ss = FileUtils.getPathByCLassPath("com/thinkgem/jeesite/modules/finance/excel/kz3.xlsx");
            ExportTemplateExcel ete = new ExportTemplateExcel("空置期("+person.getName()+")个人明细",ss,curnum);

            Map<String,Object> result = getCurruserMap(vacantPeriodDetail4PersonMap);
   			List<Map<String,Object>> rentinRentMonths = (List<Map<String,Object>>)result.get("rentinRentMonths");
   			List<Map<String,Object>> rentoutRentMonths = (List<Map<String,Object>>)result.get("rentoutRentMonths");
   			Map<String,Object> total = (Map<String,Object>)result.get("totalMap");
   			
   			Row row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 13);
   			Cell cell = ete.addCell(row, 0, "承租空置期提成汇总", 2);
   			cell.setCellStyle(ete.getStyle("header"));
   			
   			row = ete.addRow();
   			//第一步，先生成 租进列表头
   			for(int i = 0 ; i < rentinHeaders.length; i++){
   				cell = ete.addCell(row, i, rentinHeaders[i]);
   				cell.setCellStyle(ete.getStyle("header"));
   				
   			}
   			
   			//第二步，塞入 租进统计信息
   			for(Map<String,Object> map : rentinRentMonths){
         	   RentMonth rentinmonth = (RentMonth)map.get("rentinmonth");
         	   RentMonth rentoutmonth = (RentMonth)map.get("rentmonth");
         	   long vacantperiodconfig = (Long)map.get("vacantperiodconfig");
	         	long vacantperiod = (Long)map.get("vacantperiod");
	         	long rentin_cut = null != map.get("rentin_cut")? (Long)map.get("rentin_cut"):0;
	         	long teamleader_cut = null != map.get("teamleader_cut")? (Long)map.get("teamleader_cut"):0;
	         	long departleader_cut = null != map.get("departleader_cut")? (Long)map.get("departleader_cut"):0;
	         	long manager_cut = null != map.get("manager_cut")? (Long)map.get("manager_cut"):0;

	         	row = ete.addRow();
	         	ete.addCell(row, 0, rentinmonth.getRent().getBusiness_num());
	        	ete.addCell(row, 1, rentinmonth.getRent().getHouse().getName());
	        	ete.addCell(row, 2, rentinmonth.getPerson().getName());
	        	ete.addCell(row, 3, DateUtils.formatDate(rentinmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentinmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 4, DateUtils.formatDate(rentoutmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentoutmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 5, rentoutmonth.getRentmonth());
	        	ete.addCell(row, 6, null != rentoutmonth.getPerson()?rentoutmonth.getPerson().getName():"");
	        	ete.addCell(row, 7, null != rentinmonth.getBusi_departleader()?rentinmonth.getBusi_departleader().getName():"");
	        	ete.addCell(row, 8, vacantperiodconfig);
	        	ete.addCell(row, 9, vacantperiod);
	        	ete.addCell(row, 10, rentin_cut);
	        	ete.addCell(row, 11, teamleader_cut);
	        	ete.addCell(row, 12, departleader_cut);
	        	ete.addCell(row, 13, manager_cut);
   			}
   			long rentin_cut_total = (Long)total.get("rentin_cut_total");
   			long teamleader_cut_total = (Long)total.get("teamleader_cut_total");
   			long departleader_cut_total = (Long)total.get("departleader_cut_total");
   			long manager_cut_total = (Long)total.get("manager_cut_total");

   			row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 9);
            ete.addCell(row, 0, "合计", 2);
            ete.addCell(row, 10, rentin_cut_total);
            ete.addCell(row, 11, teamleader_cut_total);
            ete.addCell(row, 12, departleader_cut_total);
            ete.addCell(row, 13, manager_cut_total);

            row = ete.addRow();//插入一条空行
            
   			row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 13);
   			cell = ete.addCell(row, 0, "出租空置期提成汇总", 2);
   			cell.setCellStyle(ete.getStyle("header"));
            
            row = ete.addRow();
   			//第三步，先生成 租出列表头
   			for(int i = 0 ; i < rentoutHeaders.length; i++){
   				cell = ete.addCell(row, i, rentoutHeaders[i]);
   				cell.setCellStyle(ete.getStyle("header"));
   				
   			}
   			
   			//第四步，塞入 租出统计信息
   			for(Map<String,Object> map : rentoutRentMonths){
         	   RentMonth rentinmonth = (RentMonth)map.get("rentinmonth");
         	   RentMonth rentoutmonth = (RentMonth)map.get("rentmonth");
         	   long vacantperiodconfig = (Long)map.get("vacantperiodconfig");
	         	long vacantperiod = (Long)map.get("vacantperiod");
	         	long rentout_cut = (Long)map.get("rentout_cut");

	         	row = ete.addRow();
	         	ete.addCell(row, 0, rentinmonth.getRent().getBusiness_num());
	        	ete.addCell(row, 1, rentinmonth.getRent().getHouse().getName());
	        	ete.addCell(row, 2, rentinmonth.getPerson().getName());
	        	ete.addCell(row, 3, DateUtils.formatDate(rentinmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentinmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 4, DateUtils.formatDate(rentoutmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentoutmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 5, rentoutmonth.getRentmonth());
	        	ete.addCell(row, 6, null != rentoutmonth.getPerson()?rentoutmonth.getPerson().getName():"");
	        	ete.addCell(row, 7, null != rentinmonth.getBusi_departleader()?rentinmonth.getBusi_departleader().getName():"");
	        	ete.addCell(row, 8, vacantperiodconfig);
	        	ete.addCell(row, 9, vacantperiod);
	        	ete.addCell(row, 10, rentout_cut);
	        	
   			}
   			long rentout_cut_total = (Long)total.get("rentout_cut_total");

   			row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 9);
            ete.addCell(row, 0, "合计", 2);
            ete.addCell(row, 10, rentout_cut_total);

            ete.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/modules/finance/statsVacantPeriodList?repage";
    }


	
	@RequiresPermissions("finance:stats:vacantPeriod")
    @RequestMapping(value = "export/businessCut")
    public String export4businessCut(@RequestParam Map<String, Object> paramMap,HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋包租业绩提成导入模板.xlsx";
            int curnum = 2;//模板中已存在两行，故从2开始计算
           String ss = FileUtils.getPathByCLassPath("com/thinkgem/jeesite/modules/finance/excel/yj1.xlsx");

           ExportTemplateExcel ete = new ExportTemplateExcel((String)paramMap.get("rentout_sdate_begin")+"-"+(String)paramMap.get("rentout_sdate_end")+"业绩提成",ss,curnum);

           Map<String,Object> result = getCurruserMap(businessCutMap);
           List<Map<String,Object>> resultList = (List<Map<String,Object>>)result.get("resultList");
           Map<String,Object> total = (Map<String,Object>)result.get("total");

           for(Map<String,Object> map : resultList){
        	   RentMonth rentinmonth = (RentMonth)map.get("rentinmonth");
        	   RentMonth rentoutmonth = (RentMonth)map.get("rentoutmonth");
	         	long rentin_cut = (Long)map.get("rentin_cut");
	         	long rentout_cut = (Long)map.get("rentout_cut");
	         	long teamleader_cut = (Long)map.get("teamleader_cut");
	         	long departleader_cut = (Long)map.get("departleader_cut");
	         	long manager_cut = (Long)map.get("manager_cut");
        	   Row row = ete.addRow();
        	   ete.addCell(row, 0, rentinmonth.getRent().getBusiness_num());
        	   ete.addCell(row, 1, rentinmonth.getRent().getHouse().getName());
        	   ete.addCell(row, 2, null != rentinmonth.getPerson()?rentinmonth.getPerson().getName():"");
        	   ete.addCell(row, 3, null != rentinmonth.getBusi_departleader()?rentinmonth.getBusi_departleader().getName():"");
        	   ete.addCell(row, 4, DateUtils.formatDate(rentinmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentinmonth.getEdate(), "yyyy-MM-dd"));
        	   ete.addCell(row, 5, null !=rentoutmonth.getPerson() ?rentoutmonth.getPerson().getName():"");
        	   ete.addCell(row, 6, DateUtils.formatDate(rentoutmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentoutmonth.getEdate(), "yyyy-MM-dd"));
        	   ete.addCell(row, 7, rentinmonth.getAgencyfee());
        	   ete.addCell(row, 8, rentoutmonth.getAgencyfee());
        	   ete.addCell(row, 9, rentin_cut);
        	   ete.addCell(row, 10, rentout_cut);
        	   ete.addCell(row, 11, teamleader_cut);
        	   ete.addCell(row, 12, departleader_cut);
        	   ete.addCell(row, 13, manager_cut);

           }
  			long rentin_cut_total = (Long)total.get("rentin_cut_total");
  			long rentout_cut_total = (Long)total.get("rentout_cut_total");
  			long teamleader_cut_total = (Long)total.get("teamleader_cut_total");
  			long departleader_cut_total = (Long)total.get("departleader_cut_total");
  			long manager_cut_total = (Long)total.get("manager_cut_total");
  			Row row = ete.addRow();
           ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 8);
           ete.addCell(row, 0, "合计", 2);
           ete.addCell(row, 9, rentin_cut_total);
           ete.addCell(row, 10, rentout_cut_total);
           ete.addCell(row, 11, teamleader_cut_total);
           ete.addCell(row, 12, departleader_cut_total);
           ete.addCell(row, 13, manager_cut_total);
           ete.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/modules/finance/businessCut?repage";
    }
	
	@RequiresPermissions("finance:stats:vacantPeriod")
    @RequestMapping(value = "export/businessCut4Person")
    public String export4businessCut4Person(@RequestParam Map<String, Object> paramMap,HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "房屋包租业绩提成个人总计.xlsx";
            int curnum = 2;//模板中已存在两行，故从2开始计算
            String ss = FileUtils.getPathByCLassPath("com/thinkgem/jeesite/modules/finance/excel/yj2.xlsx");
            ExportTemplateExcel ete = new ExportTemplateExcel((String)paramMap.get("rentout_sdate_begin")+"-"+(String)paramMap.get("rentout_sdate_end")+"业绩提成(个人总计)",ss,curnum);

            Map<String,Object> result = getCurruserMap(businessCut4PersonMap);
   			List<Map<String,Object>> list = (List<Map<String,Object>>)result.get("list");
   			Map<String,Object> total = (Map<String,Object>)result.get("total");
   			for(Map<String,Object> map : list){
         	   User person = (User)map.get("person");
         	   int rentinCutTotal = null != map.get("rentinCutTotal")? (Integer)map.get("rentinCutTotal"):0;
         	   int rentoutCutTotal = null != map.get("rentoutCutTotal")? (Integer)map.get("rentoutCutTotal"):0;
         	   int rentmanagerCutTotal = null != map.get("rentmanagerCutTotal")? (Integer)map.get("rentmanagerCutTotal"):0;
         	  int rentdepartleaderCutTotal = null != map.get("rentdepartleaderCutTotal")? (Integer)map.get("rentdepartleaderCutTotal"):0;
         	 int rentteamleaderCutTotal = null != map.get("rentteamleaderCutTotal")? (Integer)map.get("rentteamleaderCutTotal"):0;
         	int cutTotal = null != map.get("cutTotal")? (Integer)map.get("cutTotal"):0;

	         	Row row = ete.addRow();
	        	ete.addCell(row, 0, person.getName());
	        	ete.addCell(row, 1, rentinCutTotal);
	        	ete.addCell(row, 2, rentoutCutTotal);
	        	ete.addCell(row, 3, rentmanagerCutTotal);
	        	ete.addCell(row, 4, rentdepartleaderCutTotal);
	        	ete.addCell(row, 5, rentteamleaderCutTotal);
	        	ete.addCell(row, 6, cutTotal);

   			}
   			long rentin_cut_total = null != total.get("rentin_cut_total")? (Integer)total.get("rentin_cut_total"):0;
   			long rentout_cut_total = null != total.get("rentout_cut_total")? (Integer)total.get("rentout_cut_total"):0;
   			long manager_cut_total = null != total.get("manager_cut_total")? (Integer)total.get("manager_cut_total"):0;
   			long departleader_cut_total = null != total.get("departleader_cut_total")? (Integer)total.get("departleader_cut_total"):0;
   			long teamleader_cut_total = null != total.get("teamleader_cut_total")? (Integer)total.get("teamleader_cut_total"):0;
   			long cut_total = null != total.get("cut_total")? (Integer)total.get("cut_total"):0;

   			Row row = ete.addRow();
            ete.addCell(row, 0, "合计", 2);
            ete.addCell(row, 1, rentin_cut_total);
            ete.addCell(row, 2, rentout_cut_total);
            ete.addCell(row, 3, manager_cut_total);
            ete.addCell(row, 4, departleader_cut_total);
            ete.addCell(row, 5, teamleader_cut_total);
            ete.addCell(row, 6, cut_total);

            ete.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/modules/finance/businessCut?repage";
    }
	
	@RequiresPermissions("finance:stats:vacantPeriod")
    @RequestMapping(value = "export/businessCutDetail4Person")
    public String export4businessCutDetail4Person(@RequestParam Map<String, Object> paramMap,HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String personid = (String)paramMap.get("personid");
		User person = UserUtils.getUserById(personid);
		String[] rentinHeaders = {"编号","地址","租进业务员","承租情况","出租情况","租金","租出业务员","部长","租进业务员提成","组长提成","部长提成","经理提成"};
		String[] rentoutHeaders = {"编号","地址","租进业务员","承租情况","出租情况","租金","租出业务员","部长","租出业务员提成"};
		try {
            String fileName = "房屋包租业绩提成个人明细.xlsx";
            int curnum = 1;//模板中已存在一行，故从0开始计算
            String ss = FileUtils.getPathByCLassPath("com/thinkgem/jeesite/modules/finance/excel/yj3.xlsx");
            ExportTemplateExcel ete = new ExportTemplateExcel("业绩提成("+person.getName()+")个人明细",ss,curnum);

            Map<String,Object> result = getCurruserMap(businessCutDetail4PersonMap);
   			List<Map<String,Object>> rentinRentMonths = (List<Map<String,Object>>)result.get("rentinRentMonths");
   			List<Map<String,Object>> rentoutRentMonths = (List<Map<String,Object>>)result.get("rentoutRentMonths");
   			Map<String,Object> total = (Map<String,Object>)result.get("totalMap");
   			
   			Row row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 11);
   			Cell cell = ete.addCell(row, 0, "承租业绩提成汇总", 2);
   			cell.setCellStyle(ete.getStyle("header"));
   			
   			row = ete.addRow();
   			//第一步，先生成 租进列表头
   			for(int i = 0 ; i < rentinHeaders.length; i++){
   				cell = ete.addCell(row, i, rentinHeaders[i]);
   				cell.setCellStyle(ete.getStyle("header"));
   				
   			}
   			
   			//第二步，塞入 租进统计信息
   			for(Map<String,Object> map : rentinRentMonths){
         	   RentMonth rentinmonth = (RentMonth)map.get("rentinmonth");
         	   RentMonth rentoutmonth = (RentMonth)map.get("rentoutmonth");

	         	long rentin_cut = null != map.get("rentin_cut")? (Integer)map.get("rentin_cut"):0;
	         	long teamleader_cut = null != map.get("teamleader_cut")? (Integer)map.get("teamleader_cut"):0;
	         	long departleader_cut = null != map.get("departleader_cut")? (Integer)map.get("departleader_cut"):0;
	         	long manager_cut = null != map.get("manager_cut")? (Integer)map.get("manager_cut"):0;

	         	row = ete.addRow();
	         	ete.addCell(row, 0, rentinmonth.getRent().getBusiness_num());
	        	ete.addCell(row, 1, rentinmonth.getRent().getHouse().getName());
	        	ete.addCell(row, 2, rentinmonth.getPerson().getName());
	        	ete.addCell(row, 3, DateUtils.formatDate(rentinmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentinmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 4, DateUtils.formatDate(rentoutmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentoutmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 5, rentoutmonth.getRentmonth());
	        	ete.addCell(row, 6, null != rentoutmonth.getPerson()?rentoutmonth.getPerson().getName():"");
	        	ete.addCell(row, 7, null != rentinmonth.getBusi_departleader()?rentinmonth.getBusi_departleader().getName():"");
	        	ete.addCell(row, 8, rentin_cut);
	        	ete.addCell(row, 9, teamleader_cut);
	        	ete.addCell(row, 10, departleader_cut);
	        	ete.addCell(row, 11, manager_cut);
   			}
   			long rentin_cut_total = (Integer)total.get("rentin_cut_total");
   			long teamleader_cut_total = (Integer)total.get("teamleader_cut_total");
   			long departleader_cut_total = (Integer)total.get("departleader_cut_total");
   			long manager_cut_total = (Integer)total.get("manager_cut_total");

   			row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 7);
            ete.addCell(row, 0, "合计", 2);
            ete.addCell(row, 8, rentin_cut_total);
            ete.addCell(row, 9, teamleader_cut_total);
            ete.addCell(row, 10, departleader_cut_total);
            ete.addCell(row, 11, manager_cut_total);

   			row = ete.addRow();//插入一条空行
            
   			row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 11);
   			cell = ete.addCell(row, 0, "出租业绩提成汇总", 2);
   			cell.setCellStyle(ete.getStyle("header"));
 
            row = ete.addRow();
   			//第三步，先生成 租出列表头
   			for(int i = 0 ; i < rentoutHeaders.length; i++){
   				cell = ete.addCell(row, i, rentoutHeaders[i]);
   				cell.setCellStyle(ete.getStyle("header"));
   				
   			}
   			
   			//第四步，塞入 租出统计信息
   			for(Map<String,Object> map : rentoutRentMonths){
         	   RentMonth rentinmonth = (RentMonth)map.get("rentinmonth");
         	   RentMonth rentoutmonth = (RentMonth)map.get("rentoutmonth");
	         	long rentout_cut = (Integer)map.get("rentout_cut");

	         	row = ete.addRow();
	         	ete.addCell(row, 0, rentinmonth.getRent().getBusiness_num());
	        	ete.addCell(row, 1, rentinmonth.getRent().getHouse().getName());
	        	ete.addCell(row, 2, rentinmonth.getPerson().getName());
	        	ete.addCell(row, 3, DateUtils.formatDate(rentinmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentinmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 4, DateUtils.formatDate(rentoutmonth.getSdate(), "yyyy-MM-dd")+"-"+DateUtils.formatDate(rentoutmonth.getEdate(), "yyyy-MM-dd"));
	        	ete.addCell(row, 5, rentoutmonth.getRentmonth());
	        	ete.addCell(row, 6, null != rentoutmonth.getPerson()?rentoutmonth.getPerson().getName():"");
	        	ete.addCell(row, 7, null != rentinmonth.getBusi_departleader()?rentinmonth.getBusi_departleader().getName():"");
	        	ete.addCell(row, 8, rentout_cut);
	        	
   			}
   			long rentout_cut_total = (Integer)total.get("rentout_cut_total");

   			row = ete.addRow();
   			ete.mergeCell(ete.getCurRownum(), ete.getCurRownum(), 0, 7);
            ete.addCell(row, 0, "合计", 2);
            ete.addCell(row, 8, rentout_cut_total);

            ete.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/modules/finance/businessCut?repage";
    }

	/**
	 * 从统计的各项缓存中根据【用户名+用户id】获取相应的值
	 * @param cacheMap
	 * @return
	 */
	private Map<String,Object> getCurruserMap(Map<String,Map<String,Object>> cacheMap){
		return cacheMap.get(UserUtils.getUser().getLoginName()+UserUtils.getUser().getLoginIp());
	}
}
