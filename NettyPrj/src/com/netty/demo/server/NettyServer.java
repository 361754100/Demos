package com.netty.demo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.netty.demo.client.TimeClient;
import com.netty.demo.server.handler.NettyServerHandler;
import com.netty.demo.utils.ServerMessageUtil;

public class NettyServer {
	
	private int port = 8013;
	
	public NettyServer(int port){
		this.port = port;
	}
	
	public void execute(){
		/**
		 * 一个经常被叫做‘boss’，用来接收进来的连接。第二个经常被叫做‘worker’，
		 * 用来处理已经被接收的连接，一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上。
		 * 如何知道多少个线程已经被使用，如何映射到已经创建的Channels上都需要依赖于EventLoopGroup的实现，
		 * 并且可以通过构造函数来配置他们的关系
		 */
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap boot = new ServerBootstrap();
			boot.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer <SocketChannel>(){
					@Override
					protected void initChannel(SocketChannel ch)
							throws Exception {
						ch.pipeline().addLast(new NettyServerHandler());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			// Bind and start to accept incoming connections.
			ChannelFuture future = boot.bind(this.port).sync();
			
			// Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception{
		int port = 8013;
		NettyServer server = new NettyServer(port);
		
		Thread borcastThread = new Thread(){
			public void run() {
				ServerMessageUtil msgUtil = new ServerMessageUtil();
				String msg = "GO GO GO!";
				
				try {
					while(true){
						msgUtil.broadcastMsg(msg);
						Thread.sleep(3000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		};
		borcastThread.start();
		
		server.execute();
	}
}