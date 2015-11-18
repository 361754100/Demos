package com.suitong.mp.baidu.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.suitong.mp.baidu.thread.AtmForecastThread;
import com.suitong.mp.baidu.thread.AtmForecastUpdateThread;
import com.suitong.mp.baidu.timer.AtmForecastTimer;
import com.suitong.mp.baidu.timer.TaskManager;

public class SystemInitServlet extends HttpServlet implements
		ServletContextListener {

	private static final long serialVersionUID = 7795526851993116506L;
	
	private static final String BAIDU_AKS = "BAIDU_AKS";
	
	private static final String BAIDU_MODIFY_AKS = "BAIDU_MODIFY_AKS";
	
	private static final Logger logger = Logger.getLogger("com.suitong.mp.baidu");
	
	public void contextDestroyed(ServletContextEvent event) {

	}

	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		
		String aks = context.getInitParameter(BAIDU_AKS);
		if( aks == null || "".equals(aks) ){
			logger.error("[SystemInitServlet.contextInitialized] ->配置文件中找不到参数：AK");
			return ;
		}
		
		String modAks = context.getInitParameter(BAIDU_MODIFY_AKS);
		if( modAks == null || "".equals(modAks) ){
			logger.error("[SystemInitServlet.contextInitialized] ->配置文件中找不到参数：BAIDU_MODIFY_AKS");
		}
		
		String computer = context.getInitParameter("computer");
		if( computer == null || "".equals(computer.trim()) ){
			logger.error("[SystemInitServlet.contextInitialized] ->配置文件中找不到参数：computer");
			return ;
		}
		
		Map<String, String> jdbcParams = new HashMap<String, String>();
		String driverClassName = context.getInitParameter("driverClassName");
		if( driverClassName == null || "".equals(driverClassName.trim()) ){
			logger.error("[SystemInitServlet.contextInitialized] ->配置文件中找不到参数：driverClassName");
			return ;
		}
		jdbcParams.put("driverClassName", driverClassName.trim());
		
		String jdbcUrl = context.getInitParameter("jdbcUrl");
		if( jdbcUrl == null || "".equals(jdbcUrl.trim()) ){
			logger.error("[SystemInitServlet.contextInitialized] ->配置文件中找不到参数：jdbcUrl");
			return ;
		}
		jdbcParams.put("jdbcUrl", jdbcUrl.trim());
		
		String jdbcUserName = context.getInitParameter("jdbcUserName");
		if( jdbcUserName == null || "".equals(jdbcUserName.trim()) ){
			logger.error("[SystemInitServlet.contextInitialized] ->配置文件中找不到参数：jdbcUserName");
			return ;
		}
		jdbcParams.put("jdbcUserName", jdbcUserName.trim());
		
		String jdbcPassword = context.getInitParameter("jdbcPassword");
		if( jdbcPassword == null || "".equals(jdbcPassword.trim()) ){
			logger.error("[SystemInitServlet.contextInitialized] ->配置文件中找不到参数：jdbcPassword");
			return ;
		}
		
		jdbcParams.put("jdbcPassword", jdbcPassword.trim());
		
		String sleepTime = context.getInitParameter("THREAD_SLEEPTIME");
		sleepTime = sleepTime == null || "".equals(sleepTime)?"500":sleepTime;
		jdbcParams.put("THREAD_SLEEPTIME", sleepTime);
		
		String operateType = context.getInitParameter("OPERATE_TYPE");
		operateType = operateType == null || "".equals(operateType)?"1":operateType;
		jdbcParams.put("OPERATE_TYPE", operateType);
		
		String maxTryNumber = context.getInitParameter("MAX_TRY_NUMBER");
		maxTryNumber = maxTryNumber == null || "".equals(maxTryNumber)?"5":maxTryNumber;
		jdbcParams.put("MAX_TRY_NUMBER", maxTryNumber);
		
		String lastModifyDay = context.getInitParameter("ATM_LAST_MODIFYDAY");
		lastModifyDay = lastModifyDay == null || "".equals(lastModifyDay)?"2":lastModifyDay;
		jdbcParams.put("ATM_LAST_MODIFYDAY", lastModifyDay);
		
		String pageNo = context.getInitParameter("pageNo");
		pageNo = pageNo == null || "".equals(pageNo)? "1":pageNo;
		int pNo = Integer.parseInt(pageNo);
		
		String pageSize = context.getInitParameter("pageSize");
		pageSize = pageSize == null || "".equals(pageSize)? "0":pageSize;
		int pSize = Integer.parseInt(pageSize);
		
		String orgIds = context.getInitParameter("ORGIDS");
		orgIds = orgIds == null?"":orgIds;
		
		String workType = context.getInitParameter("WORK_TYPE");
		workType = workType == null?"":workType;
		
		String taskDate = context.getInitParameter("TASK_DATE");
		taskDate = taskDate == null?"":taskDate;
		
		Map<String,String> taskParams = new HashMap<String,String>();
		taskParams.put("ATM_FORECAST_PEROID", context.getInitParameter("ATM_FORECAST_PEROID"));
		taskParams.put("ATM_FORECAST_STARTTIME", context.getInitParameter("ATM_FORECAST_STARTTIME"));
		taskParams.put("ATM_FORECAST_FIRSTEXEC", context.getInitParameter("ATM_FORECAST_FIRSTEXEC"));
		taskParams.put("ORGIDS", orgIds);
		taskParams.put("WORK_TYPE", workType);
		taskParams.put("TASK_DATE", taskDate);
		
		TaskManager taskManager = TaskManager.getInstance();
		taskManager.setJdbcParams(jdbcParams);
		taskManager.setTaskParams(taskParams);
		taskManager.setAks(aks.split(","));
		taskManager.setModAks(modAks.split(","));
		taskManager.setComputer(computer);
		//用定时器去跑
		if( "1".equals(operateType) ){
			//只做新增操作
			AtmForecastTimer timerTask = new AtmForecastTimer( new AtmForecastThread(pNo, pSize) );
			logger.info("[SystemInitServlet.contextInitialized] taskManager->"+ taskManager );
			taskManager.beginAtmForecastTask( timerTask );
		}else if( "2".equals(operateType) ){
			//新增或更新指定时间段的
			AtmForecastTimer timerTask = new AtmForecastTimer( new AtmForecastUpdateThread(pNo, pSize) );
			logger.info("[SystemInitServlet.contextInitialized] taskManager->"+ taskManager );
			taskManager.beginAtmForecastTask( timerTask );
		}else if( "3".equals(operateType) ){
			//定时更新指定的线路所包含的设备点与所有其他设备点的地理数据
			logger.info("[SystemInitServlet.contextInitialized] taskManager->"+ taskManager );
			
		}
		//用线程去跑
//		Thread forecastThread = new Thread(new AtmForecastThread( jdbcParams, aks.split(","), computer, pNo, pSize));
//		forecastThread.start();
//		logger.info("[SystemInitServlet.contextInitialized] forecastThread->"+ forecastThread );
		
		//用代理去跑
//		String httpProxys = context.getInitParameter("HTTP_PROXYS");
//		if(httpProxys != null ){
//			String[] proxys = httpProxys.split(",");
//			int proxyNum = proxys.length;
//			for(int pageNum = 1; pageNum <= proxyNum; pageNum++ ){
//				String proxyIp = proxys[pageNum-1].trim().split(":")[0];
//				int proxyPort = Integer.parseInt(proxys[pageNum-1].split(":")[1]);
//				Thread forecastThread = new Thread(new AtmForecastProxyThread( pageNum, pSize, proxyIp, proxyPort));
//				forecastThread.start();
//			}
//		}
	}

	public SystemInitServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); 
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/plain; charset=utf-8");
		PrintWriter out = response.getWriter();
		out.flush();
		out.close();
	}

	public void init() throws ServletException {
		
	}
	
}
