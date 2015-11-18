package com.netty.demo.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeServerHandler extends ChannelHandlerAdapter {

	public void channelActive(final ChannelHandlerContext ctx){
		final ByteBuf time = ctx.alloc().buffer(4);
		time.writeInt((int) (System.currentTimeMillis()/ 1000L + 2208988800L));
		
		final ChannelFuture future = ctx.writeAndFlush(time);
		future.addListener(new ChannelFutureListener(){
			public void operationComplete(ChannelFuture future2){
				assert future == future2;
				ctx.close();
			}
		});
	}
	
	public void channelRead(ChannelHandlerContext ctx, Object msg){
		ByteBuf in = (ByteBuf) msg;
		try {
			while(in.isReadable()){
				System.out.println((char) in.readByte());
				System.out.flush();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		/*ctx.write(msg);
		ctx.flush();*/
	}
}
