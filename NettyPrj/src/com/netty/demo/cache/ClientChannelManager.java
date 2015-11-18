package com.netty.demo.cache;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 已经连接的客户端管道的管理类
 * @author Mr Mo
 *
 */
public class ClientChannelManager {
	
	private static ClientChannelManager instance = null;
	//用来存放与Server建立连接的客户端的通道
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
	 * 增加对应的管道
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
	 * 移除关闭对应的管道
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
	 * 获取对应的管道
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
