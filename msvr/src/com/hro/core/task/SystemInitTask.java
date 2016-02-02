package com.hro.core.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.hro.core.common.log.MsvrLog;
import com.hro.core.zookeeper.executor.ZookeeperExecutor;

public class SystemInitTask {
	
	private static SystemInitTask instance = null;
	private Map<String, SystemTask> taskMap = new ConcurrentHashMap<String, SystemTask>();
	
	private SystemInitTask(){}
	
	private static class HolderClass{
		private static SystemInitTask sysTask = new SystemInitTask();
	}
	
	public static SystemInitTask getInstance(){
		if(instance == null){
			instance = HolderClass.sysTask;
		}
		return instance;
	}
	
	public void initTask(){
		SystemTask zooKeeperTask = new ZookeeperExecutor("localhost:2181", "/msvr", 3000);
		taskMap.put(ZookeeperExecutor.class.getName(), zooKeeperTask);
		try {
			zooKeeperTask.execute();
		} catch (Exception e) {
			MsvrLog.error("[SystemInitTask.initTask] error->"+e.getMessage(), e);
		}
	}
	
	public SystemTask getSysTask(String className){
		return taskMap.get(className);
	}
}
