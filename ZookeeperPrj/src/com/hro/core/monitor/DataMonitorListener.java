package com.hro.core.monitor;

public interface DataMonitorListener {
	
	/**
     * The existence status of the node has changed.
     */
	public abstract void exists(byte data[]);
	
	/**
     * The ZooKeeper session is no longer valid.
     *
     * @param rc
     *                the ZooKeeper reason code
     */
    public abstract void sessionExpired(int rc);
    
    /**
     * 连不上ZooKeeper服务器，很可能是该服务器关闭了
     * @param rc
     */
    public abstract void serverDisconnect(int rc);
}
