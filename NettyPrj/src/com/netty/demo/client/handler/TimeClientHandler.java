package com.netty.demo.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

public class TimeClientHandler extends ChannelHandlerAdapter {
	
	private ChannelHandlerContext ctx = null;
	private ChannelPromise promise = null;
	
	public void channelRead(ChannelHandlerContext ctx, Object msg){
//		ByteBuf m = (ByteBuf) msg;
//		try {
//			long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
//			System.out.println(new Date(currentTimeMillis));
//		} catch (Exception e) {
//			// TODO: handle exception
//		} finally {
//			m.release();
//		}
		System.out.println("[TimeClientHandler.channelRead] instance->"+this+" msg->"+msg);
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
			ReferenceCountUtil.release(in);
		}
	}
	
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
//        String str = "Hello msg";
//        
//        //通知您已经链接上客户端
//  		ByteBuf buf = Unpooled.buffer(str.getBytes().length);
//  		buf.writeBytes(str.getBytes());
//  		ctx.writeAndFlush(buf);
    }
	
}
