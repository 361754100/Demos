package com.hro.core.monitor;

import java.util.Arrays;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class DataMonitor implements Watcher, StatCallback{
	
	private ZooKeeper zooKeeper;
	private String znode;
	private Watcher chainedWatcher;
	private boolean dead;
	private byte prevData[];
	private DataMonitorListener listener;
	
	public DataMonitor(ZooKeeper zk, String znode, Watcher watcher, DataMonitorListener listener){
		this.zooKeeper = zk;
		this.znode = znode;
		this.chainedWatcher = watcher;
		this.listener = listener;
		// Get things started by checking if the node exists. We are going
        // to be completely event driven
		this.zooKeeper.exists(znode, true, this, null);
	}

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		boolean exists;
		switch (rc){
			case Code.Ok:
				exists = true;
				break;
			case Code.NoNode:
				exists = false;
				break;
			case Code.SessionExpired:
			case Code.NoAuth:
				dead = true;
				listener.sessionExpired(rc);
				return;
			default:
				zooKeeper.exists(znode, true, this, null);
				return;
		}
		
		byte b[] = null;
		if(exists){
			try {
				b = zooKeeper.getData(znode, false, null);
			} catch (KeeperException e) {
				// We don't need to worry about recovering now. The watch
                // callbacks will kick off any exception handling
				e.printStackTrace();
			}catch (InterruptedException e) {
				return;
			}
		}
		if((b == null && b != prevData) || (b != null && !Arrays.equals(prevData, b))){
			listener.exists(b);
			prevData = b;
		}
	}

	@Override
	public void process(WatchedEvent event) {
		String path = event.getPath();
		if(event.getType() == Event.EventType.None){
			// We are are being told that the state of the
            // connection has changed
			int state = event.getState().getIntValue();
			switch(event.getState()){
				case SyncConnected:
					// In this particular example we don't need to do anything
	                // here - watches are automatically re-registered with 
	                // server and any watches triggered while the client was 
	                // disconnected will be delivered (in order of course)
					System.out.println("[DataMonitor.process] state->SyncConnected");
					break;
				case Expired:
					// It's all over
					dead = true;
					listener.sessionExpired(KeeperException.Code.SessionExpired);
					System.out.println("[DataMonitor.process] state->Expired");
					break;
				case Disconnected:
					//Server is disconnect
					dead = true;
					listener.serverDisconnect(KeeperException.Code.ConnectionLoss);
					System.out.println("[DataMonitor.process] state->Expired");
					break;
			}
		} else {
			if(path != null && path.equals(znode)){
				zooKeeper.exists(path, true, this, null);
			}
		}
		if(chainedWatcher != null){
			chainedWatcher.process(event);
		}
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
}
