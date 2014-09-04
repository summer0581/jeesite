package com.thinkgem.jeesite.modules.sys.schedule;

import java.util.Calendar;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.modules.finance.service.HouseService;

@Service
@DependsOn({"houseDao"})
public class MyTask extends TimerTask { 
  private static final String C_SCHEDULE_HOUR = ",8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,"; 
  private static boolean isRunning = false; 
  private ServletContext context = null; 
  private static HouseService houseService = SpringContextHolder.getBean(HouseService.class);

  public MyTask() { 
  } 
  public MyTask(ServletContext context) { 
    this.context = context; 
  } 

  public void run() { 
    Calendar cal = Calendar.getInstance(); 

    if (!isRunning) { 
      if (C_SCHEDULE_HOUR.indexOf(","+String.valueOf(cal.get(Calendar.HOUR_OF_DAY))+",") != -1) { 
        isRunning = true; 
        long a=System.currentTimeMillis();
        context.log("开始执行指定任务"); 
        //TODO 添加自定义的详细任务，以下只是示例 
        houseService.updateHouseBusiSearchData();  

        isRunning = false; 
        context.log("指定任务执行结束,执行时间为："+(System.currentTimeMillis()-a)/1000f+" 秒 "); 
      } 
    } 
    else { 
      context.log("上一次任务执行还未结束"); 
    } 
  } 

} 
