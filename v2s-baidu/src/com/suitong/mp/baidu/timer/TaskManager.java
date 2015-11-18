package com.suitong.mp.baidu.timer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class TaskManager {
	
	private static final Logger logger = Logger.getLogger("com.suitong.mp.baidu");
	
	private DateFormat dFormatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private DateFormat dFormatShort = new SimpleDateFormat("yyyy-MM-dd ");
	private Timer timer = new Timer(true);
	
	private static TaskManager instance = null;
	//用于判断定时任务是否在运行
	private boolean isTaskRunning = false;
	//用于判断http接口线程是否在运行(全局设备点纠正)
	private boolean isHttpTaskRunning = false;
	//用于判断http接口线程是否在运行(线路设备点纠正)
	private boolean isHttpLineTaskRunning = false;
	//用于判断根据所属银行等查询条件更新局部地理数据
	private boolean isUpdatePartRunning = false;
	
	//用于判断线路数据更新的线程是否在运行
	private boolean isCorrectLineRunning = false;
	//用于判断线路更新线程是否该暂停更新
	private boolean isCorrectPause = false;
	//用于记录上一次跑到哪个起点和哪个终点 (i,j)
	private int[] correctPos = {0,0};
	
	private Map<String, String> jdbcParams = new HashMap<String, String>();
	private Map<String, String> taskParams = new HashMap<String, String>();
	//定时器线程使用的AK
	private String[] aks = new String[]{};
	//修改线程使用的AK
	private String[] modAks = new String[]{};
	private String computer = null;
	
	private static class HolderClass{
		private static TaskManager manager = new TaskManager(); 
	}
	
	public static TaskManager getInstance() {
		if (instance == null) {
			instance = HolderClass.manager;
		}
		return instance;
	}
	
	public void beginAtmForecastTask(TimerTask task ){
		logger.info("[TaskManager.beginAtmForecastTask] ----------------->");
		
		timer = new Timer(true);
		Date taskDate = new Date();
		
		String datePrestr = dFormatShort.format(taskDate);
		String periodStr = taskParams.get("ATM_FORECAST_PEROID");
		periodStr = periodStr == null?"24":periodStr;
		int period = Integer.parseInt(periodStr);
		
		String atmForecastTime = taskParams.get("ATM_FORECAST_STARTTIME");
		atmForecastTime = atmForecastTime == null?"01:00:00":atmForecastTime;
		String startTime = datePrestr + atmForecastTime;
		
		String firstExec = taskParams.get("ATM_FORECAST_FIRSTEXEC");;
		try {
			int result = compareWithNowTime(startTime);
			if(result == -1 && "no".equals(firstExec)){
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DAY_OF_MONTH, 1);
				startTime = dFormatShort.format(c.getTime()) + atmForecastTime;
				logger.info("[TaskManager.beginAtmForecastTask] ->百度数据采集任务,首次采集时间调整至"+startTime);
			}
			timer.schedule(task, dFormatLong.parse(startTime), period*1000*60*60);
			logger.info("[TaskManager.beginAtmForecastTask] ->百度数据采集任务添加成功");
		} catch (ParseException e) {
			logger.error("[TaskManager.beginAtmForecastTask] ->百度数据采集任务添加失败, 配置时间格式有误");
		}
	}
	
	//把系统时间跟计划时间做对比,相同 0 比系统时间早 -1 比系统时间晚 1
	private int compareWithNowTime(String time) throws ParseException{
		Date time1 = dFormatLong.parse(time);
		Date now = new Date();
		return time1.compareTo(now);
	}
	
	public boolean isTaskRunning() {
		return isTaskRunning;
	}

	public void setTaskRunning(boolean isTaskRunning) {
		logger.info("[TaskManager.setTaskRunning] ----------------->isTaskRunning:"+isTaskRunning);
		this.isTaskRunning = isTaskRunning;
	}

	public void closeTimer(){
		logger.info("[TaskManager.closeTimer] ----------------->");
		timer.cancel();
	}

	public Map<String, String> getJdbcParams() {
		return jdbcParams;
	}

	public void setJdbcParams(Map<String, String> jdbcParams) {
		this.jdbcParams = jdbcParams;
	}

	public Map<String, String> getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(Map<String, String> taskParams) {
		this.taskParams = taskParams;
	}

	public String[] getAks() {
		return aks;
	}

	public void setAks(String[] aks) {
		this.aks = aks;
	}

	public String getComputer() {
		return computer;
	}

	public void setComputer(String computer) {
		this.computer = computer;
	}

	public String[] getModAks() {
		return modAks;
	}

	public void setModAks(String[] modAks) {
		this.modAks = modAks;
	}

	public boolean isHttpTaskRunning() {
		return isHttpTaskRunning;
	}

	public void setHttpTaskRunning(boolean isHttpTaskRunning) {
		this.isHttpTaskRunning = isHttpTaskRunning;
	}
	
	public boolean isHttpLineTaskRunning() {
		return isHttpLineTaskRunning;
	}

	public void setHttpLineTaskRunning(boolean isHttpLineTaskRunning) {
		this.isHttpLineTaskRunning = isHttpLineTaskRunning;
	}

	public boolean isUpdatePartRunning() {
		return isUpdatePartRunning;
	}

	public void setUpdatePartRunning(boolean isUpdatePartRunning) {
		this.isUpdatePartRunning = isUpdatePartRunning;
	}

	public boolean isCorrectPause() {
		return isCorrectPause;
	}

	public void setCorrectPause(boolean isCorrectPause) {
		this.isCorrectPause = isCorrectPause;
	}

	public boolean isCorrectLineRunning() {
		return isCorrectLineRunning;
	}

	public void setCorrectLineRunning(boolean isCorrectLineRunning) {
		this.isCorrectLineRunning = isCorrectLineRunning;
	}

	public int[] getCorrectPos() {
		return correctPos;
	}

	public void setCorrectPos(int[] correctPos) {
		this.correctPos = correctPos;
	}
	
	public void updateCorrectPos(int index, int value){
		this.correctPos[index] = value;
	}
	
}
