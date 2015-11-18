package com.netty.demo.server.handler;

import java.net.SocketAddress;

import com.netty.demo.cache.ClientChannelManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class NettyServerHandler extends ChannelHandlerAdapter {
	
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		System.out.println("[NettyServerHandler.channelRead] msg->"+msg);
		ByteBuf in = (ByteBuf) msg;
		try {
			while(in.isReadable()){
				System.out.print((char) in.readByte());
				System.out.flush();
			}
			System.out.println("");
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			in.release();
		}
		
		/*ctx.write(msg);
		ctx.flush();*/
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
//		ctx.writeAndFlush("This is Server Handler.");
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
	
    @Override
    @Skip
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    	// TODO Auto-generated method stub
    	super.channelRegistered(ctx);
    	System.out.println("[NettyServerHandler.channelRegistered] ->ctx:"+ ctx);
    	Channel ch = ctx.channel();
    	String channelId = ch.id().asLongText();
    	ClientChannelManager instance = ClientChannelManager.getInstance();
    	instance.addChannel(channelId, ch);
    }
}
