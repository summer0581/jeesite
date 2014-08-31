package com.thinkgem.jeesite.modules.sys.schedule;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

public class ContextListener extends HttpServlet implements
		ServletContextListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContextListener() {
	}

	private java.util.Timer timer = null;

	public void contextInitialized(ServletContextEvent event) {
		timer = new java.util.Timer(true);
		event.getServletContext().log("定时器已启动");
		timer.schedule(new MyTask(event.getServletContext()), 0, 2*60*60*1000);
		event.getServletContext().log("已经添加任务调度表");
	}

	public void contextDestroyed(ServletContextEvent event) {
		timer.cancel();
		event.getServletContext().log("定时器销毁");
	}

}