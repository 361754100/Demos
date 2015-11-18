package com.netty.demo.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.ReferenceCountUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.netty.demo.cache.ClientChannelManager;

public class ServerMessageUtil {
	
	public void sendMessage(String channelId, Object msg){
		
	}
	
	/**
	 * 给所有客户端广播消息
	 * @param msg
	 */
	public void broadcastMsg(Object msg){
		ClientChannelManager instance = ClientChannelManager.getInstance();
		Map<String,Channel> channelMap = instance.getChannelMap();
		if( channelMap == null || channelMap.isEmpty() ){
			return;
		}
		Set<String> keySet = channelMap.keySet();
		Iterator<String> iterator =keySet.iterator();
		String tempMsg = String.valueOf(msg);
		System.out.println("[MessageUtil.broadcastMsg] msg->"+tempMsg);
		
		
		while(iterator.hasNext()){
			ByteBuf buf = Unpooled.buffer(tempMsg.getBytes().length);
			buf.writeBytes(tempMsg.getBytes());
			String key = iterator.next();
			System.out.println("[MessageUtil.broadcastMsg] key->"+key);
			
			Channel ch = channelMap.get(key);
			ch.writeAndFlush(buf.retain());
			
			ReferenceCountUtil.release(buf);
//			buf.release();
		}
	}
}
