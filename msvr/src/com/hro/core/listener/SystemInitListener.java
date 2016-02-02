package com.hro.core.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hro.core.common.log.MsvrLog;
import com.hro.core.task.SystemInitTask;

public class SystemInitListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		SystemInitTask sysTask = SystemInitTask.getInstance();
		sysTask.initTask();
		MsvrLog.info("[SystemInitListener.contextInitialized] sysTask->"+sysTask );
	}

}
