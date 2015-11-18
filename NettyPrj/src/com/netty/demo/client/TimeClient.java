package com.netty.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;

import java.net.InetSocketAddress;
import java.util.Random;

import com.netty.demo.client.handler.TimeClientHandler;

public class TimeClient {
	
	private static String host = "localhost";
	private static int port = 8013;
	
	public void connect(){
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap boot =new Bootstrap();
			boot.group(workerGroup);
			boot.channel(NioSocketChannel.class);
			boot.option(ChannelOption.SO_KEEPALIVE, true);
			boot.handler(new ChannelInitializer<SocketChannel>(){
				public void initChannel(SocketChannel ch){
					ch.pipeline().addLast( new TimeClientHandler() );
				}
			});
			// Start the client.
			ChannelFuture future = boot.connect(new InetSocketAddress(host, port));
			
	        String str = "Hello world! msg->"+new Random().nextInt();
	        
	        //通知您已经链接上客户端
	  		ByteBuf buf = Unpooled.buffer(str.getBytes().length);
	  		buf.writeBytes(str.getBytes());
	  		future.channel().writeAndFlush(buf.retain());
	  		
	  		ReferenceCountUtil.release(buf);
			
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception{
		int clients = 0;
		while(true){
			Thread clientThreads = new Thread(){
				public void run() {
					TimeClient client = new TimeClient();
					client.connect();
				};
			};
			clientThreads.start();
			
			clients++;
			Thread.sleep(1000);
			System.out.println("[TimeClient] clients->"+clients);
		}
		
	}
}
