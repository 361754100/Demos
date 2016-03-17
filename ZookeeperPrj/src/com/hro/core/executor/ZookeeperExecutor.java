package com.hro.core.executor;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.hro.core.monitor.DataMonitor;
import com.hro.core.monitor.DataMonitorListener;

public class ZookeeperExecutor implements Watcher, Runnable, DataMonitorListener{
	
	private String znode;
	private DataMonitor dataMonitor;
	private ZooKeeper zooKeeper;
	private String fileName;
	private String exec[];
	private Process child;
	
	public ZookeeperExecutor(String hostPort, String znode, String fileName, String exec[]) throws KeeperException, IOException{
		this.fileName = fileName;
		this.exec = exec;
		this.zooKeeper = new ZooKeeper(hostPort, 3000, this);
		this.znode = znode;
		
		dataMonitor = new DataMonitor(this.zooKeeper, znode, null, this);
	}
	
	@Override
	public void run() {
//		try {
//			synchronized (this) {
//				while(!dataMonitor.isDead()){
//					wait();
//				}
//			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		try {
			Stat stat = this.zooKeeper.exists(znode, true);
			if(stat == null){
				this.zooKeeper.create(znode, "hello zooKeeper!".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}else {
				byte bytes[] = this.zooKeeper.getData(znode, true, stat);
				String msg = new String(bytes);
				System.out.println("[ZookeeperExecutor.run] msg->"+msg);
				
				this.zooKeeper.setData(znode, "hello zooKeeper555!".getBytes(), -1);
				
			}
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void process(WatchedEvent event) {
		dataMonitor.process(event);
	}

	@Override
	public void exists(byte[] data) {
		if( data == null){
			if(child != null){
				System.out.println("[ZookeeperExecutor.exists] ->Killing process");
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			child = null;
		}else {
			if(child != null){
				System.out.println("[ZookeeperExecutor.exists] ->Stopping child");
				child.destroy();
				try {
					child.waitFor();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				FileOutputStream fos = new FileOutputStream(fileName);
				fos.write(data);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				System.out.println("[ZookeeperExecutor.exists] ->Starting child");
				child = Runtime.getRuntime().exec(exec);
				new StreamWriter(child.getInputStream(), System.out);
				new StreamWriter(child.getErrorStream(), System.err);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void sessionExpired(int rc) {
		synchronized (this) {
			notifyAll();
		}
	}
	
	@Override
	public void serverDisconnect(int rc) {
		synchronized (this) {
			notifyAll();
		}
	}

	public static void main(String[] args) {
		String hostPort = "localhost:2181";
		String znode = "/msvrNode";
		String fileName = "msvrFile";
		String exec[] = new String[]{"msvr1","msvr2","msvr3"};
		try {
			Thread executor = new Thread(new ZookeeperExecutor(hostPort, znode, fileName, exec));
			executor.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
