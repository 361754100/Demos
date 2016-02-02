package com.hro.core.zookeeper.executor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.hro.core.common.log.MsvrLog;
import com.hro.core.task.SystemTask;
import com.hro.core.zookeeper.monitor.DataWatcher;

public class ZookeeperExecutor implements SystemTask{
	
	private String hostPort;
	private String znode;
	private int sessionTimeout;
	private ZooKeeper zooKeeper;
	//用来存放命名空间对应的watcher
	private Map<String,DataWatcher> watcherMap = new ConcurrentHashMap<String,DataWatcher>();
	
	public ZookeeperExecutor(String hostPort, String znode, int sessionTimeout) {
		this.hostPort = hostPort;
		this.znode = znode;
		this.sessionTimeout = sessionTimeout;
	}
	
	@Override
	public void execute() throws IOException {
		if(!watcherMap.containsKey(znode)){
			DataWatcher watcher = new DataWatcher(this.znode);
			watcherMap.put(znode, watcher);
		}
		this.zooKeeper = new ZooKeeper(hostPort, this.sessionTimeout, watcherMap.get(znode));
		MsvrLog.info("[ZookeeperExecutor.execute] hostPort->"+hostPort+" znode->"+znode+" sessionTimeout->"+sessionTimeout);
	}
	
	/**
	 * 创建数据节点
	 * @param nodePath
	 * @param data
	 */
	public void createNode(String nodePath, String data){
		if(!watcherMap.containsKey(nodePath)){
			DataWatcher watcher = new DataWatcher(nodePath);
			watcherMap.put(nodePath, watcher);
		}
		try {
			Stat stat = this.zooKeeper.exists(nodePath, watcherMap.get(nodePath));
			if(stat == null){
				this.zooKeeper.create(nodePath, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				MsvrLog.info("[ZookeeperExecutor.createNode] msg1->"+data);
			}else {
				byte bytes[] = this.zooKeeper.getData(nodePath, true, stat);
				String msg = new String(bytes);
				MsvrLog.info("[ZookeeperExecutor.createNode] msg2->"+msg);
			}
		} catch (KeeperException e) {
			MsvrLog.error("[ZookeeperExecutor.createNode] error1->"+e.getMessage(), e);
		} catch (InterruptedException e) {
			MsvrLog.error("[ZookeeperExecutor.createNode] error2->"+e.getMessage(), e);
		}
	}
	
	/**
	 * 设置节点数据
	 * @param nodePath
	 * @param data
	 */
	public void setNodeData(String nodePath, String data){
		if(!watcherMap.containsKey(nodePath)){
			DataWatcher watcher = new DataWatcher(nodePath);
			watcherMap.put(nodePath, watcher);
		}
		try {
			Stat stat = this.zooKeeper.exists(nodePath, watcherMap.get(nodePath));
			if(stat == null){
				this.zooKeeper.create(nodePath, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				MsvrLog.info("[ZookeeperExecutor.setNodeData] msg1->"+data);
			}else {
				this.zooKeeper.setData(nodePath, data.getBytes(), -1);
				MsvrLog.info("[ZookeeperExecutor.setNodeData] msg2->"+data);
			}
		} catch (KeeperException e) {
			MsvrLog.error("[ZookeeperExecutor.setNodeData] error1->"+e.getMessage(), e);
		} catch (InterruptedException e) {
			MsvrLog.error("[ZookeeperExecutor.setNodeData] error2->"+e.getMessage(), e);
		}
	}
	
	/**
	 * 获得节点数据
	 * @param nodePath
	 * @return
	 */
	public String getNodeData(String nodePath){
		String data = null;
		if(!watcherMap.containsKey(nodePath)){
			DataWatcher watcher = new DataWatcher(nodePath);
			watcherMap.put(nodePath, watcher);
		}
		try {
			Stat stat = this.zooKeeper.exists(nodePath, watcherMap.get(nodePath));
			if(stat != null){
				byte bytes[] = this.zooKeeper.getData(nodePath, true, stat);
				data = new String(bytes);
			}
		} catch (KeeperException e) {
			MsvrLog.error("[ZookeeperExecutor.getNodeData] error1->"+e.getMessage(), e);
		} catch (InterruptedException e) {
			MsvrLog.error("[ZookeeperExecutor.getNodeData] error2->"+e.getMessage(), e);
		}
		MsvrLog.info("[ZookeeperExecutor.getNodeData] data->"+data);
		return data;
	}

	public ZooKeeper getZooKeeper() {
		return zooKeeper;
	}
	
//	public static void main(String[] args) {
//		String hostPort = "localhost:2181";
//		String znode = "/msvrNode";
//		String fileName = "msvrFile";
//		String exec[] = new String[]{"msvr1","msvr2","msvr3"};
//		try {
//			Thread executor = new Thread(new ZookeeperExecutor(hostPort, znode, fileName, exec));
//			executor.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}
