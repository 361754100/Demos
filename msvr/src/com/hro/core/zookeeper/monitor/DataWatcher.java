package com.hro.core.zookeeper.monitor;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.hro.core.common.log.MsvrLog;
import com.hro.core.task.SystemInitTask;
import com.hro.core.zookeeper.executor.ZookeeperExecutor;

public class DataWatcher implements Watcher, StatCallback{
	
	private String znode;
	
	public DataWatcher(String znode){
		this.znode = znode;
	}
	
	/**
	 * 捕获命名空间的事件
	 */
	@Override
	public void process(WatchedEvent event) {
		SystemInitTask sysTask = SystemInitTask.getInstance();
		ZookeeperExecutor zkExecutor = (ZookeeperExecutor) sysTask.getSysTask(ZookeeperExecutor.class.getName());
		ZooKeeper zooKeeper = zkExecutor.getZooKeeper();
		
		EventType eType = event.getType();
		MsvrLog.info("[DataWatcher.process] event->"+event+" eventType->"+eType);
		
		String path = event.getPath();
		if(eType == Event.EventType.None){
			// We are are being told that the state of the
            // connection has changed
			KeeperState state = event.getState();
			switch(state){
				case SyncConnected:
					// In this particular example we don't need to do anything
	                // here - watches are automatically re-registered with 
	                // server and any watches triggered while the client was 
	                // disconnected will be delivered (in order of course)
					MsvrLog.info("[DataWatcher.process] state->SyncConnected");
					break;
				case Expired:
					// It's all over
					MsvrLog.info("[DataWatcher.process] state->Expired");
					break;
				case Disconnected:
					//Server is disconnect
					MsvrLog.info("[DataWatcher.process] state->Disconnected");
					break;
			}
		} else {
			if(path != null && path.equals(znode)){
				zooKeeper.exists(path, true, this, null);
			}
		}
	}

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		SystemInitTask sysTask = SystemInitTask.getInstance();
		ZookeeperExecutor zkExecutor = (ZookeeperExecutor) sysTask.getSysTask(ZookeeperExecutor.class.getName());
		ZooKeeper zooKeeper = zkExecutor.getZooKeeper();
		
		switch (rc){
			case Code.Ok:
				MsvrLog.info("[DataWatcher.processResult] state->Ok");
				break;
			case Code.NoNode:
				MsvrLog.info("[DataWatcher.processResult] state->NoNode");
				break;
			case Code.SessionExpired:
				MsvrLog.info("[DataWatcher.processResult] state->SessionExpired");
				break;
			case Code.NoAuth:
				MsvrLog.info("[DataWatcher.processResult] state->NoAuth");
				return;
			default:
				MsvrLog.info("[DataWatcher.processResult] state->default");
				zooKeeper.exists(znode, true, this, ctx);
				return;
		}
	}

}
