package com.hro.core.client;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperClient {
	
	private static final String HOST = "localhost";
	private static final String PORT = "2181";
	
	public void connect() throws Exception{
		// 创建一个与服务器的连接
		ZooKeeper zookeeper = new ZooKeeper(HOST+":"+PORT, 2000, new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				System.out.println("[ZookeeperClient.watcher] ->触发事件："+event.getType());
			}
		});
		
		// 创建一个目录节点
		zookeeper.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		// 创建一个子目录节点
		zookeeper.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		
		System.out.println("[ZookeeperClient.connect] ->"+ new String(zookeeper.getData("/testRootPath",false,null))); 
		
		// 取出子目录节点列表
		System.out.println("[ZookeeperClient.connect] ->"+ zookeeper.getChildren("/testRootPath",true)); 
		// 修改子目录节点数据
		zookeeper.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
		
		System.out.println("目录节点状态：["+zookeeper.exists("/testRootPath",true)+"]"); 
		// 创建另外一个子目录节点
		zookeeper.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
		
		System.out.println("[ZookeeperClient.connect] ->"+new String(zookeeper.getData("/testRootPath/testChildPathTwo",true,null))); 
		
		// 删除子目录节点
		zookeeper.delete("/testRootPath/testChildPathTwo",-1); 
		zookeeper.delete("/testRootPath/testChildPathOne",-1); 
		
		Thread.sleep(2000);
		
		// 删除父目录节点
		zookeeper.delete("/testRootPath",-1); 
		
		// 关闭连接
		zookeeper.close();
	}
	
	public static void main(String[] args){
		ZookeeperClient client = new ZookeeperClient();
		try {
			client.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
