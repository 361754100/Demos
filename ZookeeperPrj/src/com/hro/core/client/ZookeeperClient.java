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
		// ����һ���������������
		ZooKeeper zookeeper = new ZooKeeper(HOST+":"+PORT, 2000, new Watcher(){
			@Override
			public void process(WatchedEvent event) {
				System.out.println("[ZookeeperClient.watcher] ->�����¼���"+event.getType());
			}
		});
		
		// ����һ��Ŀ¼�ڵ�
		zookeeper.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		// ����һ����Ŀ¼�ڵ�
		zookeeper.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		
		System.out.println("[ZookeeperClient.connect] ->"+ new String(zookeeper.getData("/testRootPath",false,null))); 
		
		// ȡ����Ŀ¼�ڵ��б�
		System.out.println("[ZookeeperClient.connect] ->"+ zookeeper.getChildren("/testRootPath",true)); 
		// �޸���Ŀ¼�ڵ�����
		zookeeper.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
		
		System.out.println("Ŀ¼�ڵ�״̬��["+zookeeper.exists("/testRootPath",true)+"]"); 
		// ��������һ����Ŀ¼�ڵ�
		zookeeper.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
		
		System.out.println("[ZookeeperClient.connect] ->"+new String(zookeeper.getData("/testRootPath/testChildPathTwo",true,null))); 
		
		// ɾ����Ŀ¼�ڵ�
		zookeeper.delete("/testRootPath/testChildPathTwo",-1); 
		zookeeper.delete("/testRootPath/testChildPathOne",-1); 
		
		Thread.sleep(2000);
		
		// ɾ����Ŀ¼�ڵ�
		zookeeper.delete("/testRootPath",-1); 
		
		// �ر�����
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
