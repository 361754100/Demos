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
		 * һ��������������boss�����������ս��������ӡ��ڶ���������������worker����
		 * ���������Ѿ������յ����ӣ�һ����boss�����յ����ӣ��ͻ��������Ϣע�ᵽ��worker���ϡ�
		 * ���֪�����ٸ��߳��Ѿ���ʹ�ã����ӳ�䵽�Ѿ�������Channels�϶���Ҫ������EventLoopGroup��ʵ�֣�
		 * ���ҿ���ͨ�����캯�����������ǵĹ�ϵ
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