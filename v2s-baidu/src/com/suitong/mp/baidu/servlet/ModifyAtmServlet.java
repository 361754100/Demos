package com.suitong.mp.baidu.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.suitong.mp.baidu.model.AtmHttpReqManager;
import com.suitong.mp.baidu.thread.AtmForecastHttpAtmsThread;
import com.suitong.mp.baidu.thread.AtmForecastHttpLinesThread;
import com.suitong.mp.baidu.thread.AtmForecastHttpThread;
import com.suitong.mp.baidu.thread.AtmForecastUpdatePartThread;
import com.suitong.mp.baidu.timer.TaskManager;

public class ModifyAtmServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger("com.suitong.mp.baidu.http");
	
	public ModifyAtmServlet() {
		super();
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
		
		String operate = request.getParameter("operate");
		
		String termId = request.getParameter("termId");
		logger.info("[ModifyAtmServlet.doPost] receive termId->"+termId);
		
		String lineSns = request.getParameter("lineSns");
		logger.info("[ModifyAtmServlet.doPost] receive lineSns->"+lineSns);
		
		String nePoint = request.getParameter("nePoint");
		String swPoint = request.getParameter("swPoint");
		
		String orgIds = request.getParameter("orgIds");
		String taskDate = request.getParameter("taskDate");
		String workType = request.getParameter("workType");
		
		TaskManager taskManager = TaskManager.getInstance();
		Map<String,String> taskParams = taskManager.getTaskParams();
		
		if(orgIds != null && !"".equals(orgIds)){
			taskParams.put("ORGIDS", orgIds);
		}
		if(workType != null && !"".equals(workType)){
			taskParams.put("WORK_TYPE", workType);
		}
		if(taskDate != null && !"".equals(taskDate)){
			taskParams.put("TASK_DATE", taskDate);
		}
		
		response.setStatus(200);
		PrintWriter out = response.getWriter();
		out.write("OK");
		out.flush();
		out.close();
		
		if(operate!= null && "updateLines".equals(operate) ){
			//
			AtmHttpReqManager reqManager = AtmHttpReqManager.getInstance();
			reqManager.addLineSns(lineSns);
			
			Thread modThread = new Thread(new AtmForecastHttpLinesThread());
			modThread.start();
		}else if( operate!= null && "updateAtms".equals(operate) ){
			Thread modThread = new Thread(new AtmForecastHttpAtmsThread());
			Map<String, String> pMap = new HashMap<String,String>();
			pMap.put("nePoint", nePoint);
			pMap.put("swPoint", swPoint);
			
			AtmHttpReqManager reqManager = AtmHttpReqManager.getInstance();
			reqManager.addXYPoints(pMap);
			
			modThread.start();
		}else if( operate!= null && "updatePart".equals(operate) ){
			Thread modThread = new Thread(new AtmForecastUpdatePartThread());
			modThread.start();
		}
		//线路数据采集
		else if( operate != null && "correctLineData".equals(operate) ){
			AtmHttpReqManager reqManager = AtmHttpReqManager.getInstance();
			reqManager.addCorrectLineSns(lineSns);
		}else {
			if(termId != null && !"".equals(termId)){
				AtmHttpReqManager reqManager = AtmHttpReqManager.getInstance();
				reqManager.addTermId(termId);
				
				Thread modThread = new Thread(new AtmForecastHttpThread());
				modThread.start();
			}
		}
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
