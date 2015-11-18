package com.netty.demo.cache;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * �Ѿ����ӵĿͻ��˹ܵ��Ĺ�����
 * @author Mr Mo
 *
 */
public class ClientChannelManager {
	
	private static ClientChannelManager instance = null;
	//���������Server�������ӵĿͻ��˵�ͨ��
	private Map<String,Channel> channelMap = new ConcurrentHashMap<String,Channel>();
	
	private static class HolderClass{
		private static ClientChannelManager manager = new ClientChannelManager();
	}
	
	public static ClientChannelManager getInstance(){
		if(instance == null){
			instance = HolderClass.manager;
		}
		return instance;
	}
	
	/**
	 * ���Ӷ�Ӧ�Ĺܵ�
	 * @param channelKey
	 * @param ch
	 * @return
	 */
	public void addChannel(String channelKey, Channel ch){
		if(!this.channelMap.containsKey(channelKey)){
			this.channelMap.put(channelKey, ch);
		}
	}
	
	/**
	 * �Ƴ��رն�Ӧ�Ĺܵ�
	 * @param channelKey
	 * @return
	 */
	public void removeChannel(String channelKey){
		Channel ch = this.channelMap.get(channelKey);
		if(ch != null ){
//			if( !ch.isActive() ){
//				
//			}
			try {
				ch.close().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.channelMap.remove(channelKey);
		}
	}
	
	/**
	 * ��ȡ��Ӧ�Ĺܵ�
	 * @param channelKey
	 * @return
	 */
	public Channel getChannel(String channelKey){
		return this.channelMap.get(channelKey);
	}

	public Map<String, Channel> getChannelMap() {
		return channelMap;
	}
	
}
