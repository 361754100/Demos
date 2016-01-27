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
     * ������ZooKeeper���������ܿ����Ǹ÷������ر���
     * @param rc
     */
    public abstract void serverDisconnect(int rc);
}
