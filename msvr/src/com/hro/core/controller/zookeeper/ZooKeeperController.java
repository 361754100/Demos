package com.hro.core.controller.zookeeper;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.hro.core.common.log.MsvrLog;
import com.hro.core.task.SystemInitTask;
import com.hro.core.zookeeper.executor.ZookeeperExecutor;

@Controller
@RequestMapping("/zkController")
public class ZooKeeperController {
	
	@RequestMapping(value="/setData.do", method=RequestMethod.GET)
	public String setData(@RequestParam String nodePath, @RequestParam String data){
		SystemInitTask sysTask = SystemInitTask.getInstance();
		ZookeeperExecutor zkExecutor = (ZookeeperExecutor) sysTask.getSysTask(ZookeeperExecutor.class.getName());
		MsvrLog.info("[ZooKeeperController.setData] zkExecutor->"+zkExecutor);
		if(zkExecutor != null){
			zkExecutor.setNodeData("/"+nodePath, data);
		}
		return "success";
	}
}
