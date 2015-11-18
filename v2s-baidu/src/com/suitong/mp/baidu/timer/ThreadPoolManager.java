package com.suitong.mp.baidu.timer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
	
	private static ExecutorService pool;
	
	public static ExecutorService initThreadPool(int poolSize) {
		if(pool == null) {
			pool = Executors.newFixedThreadPool(poolSize);
		}
		return pool;
	}
	
	public static void destroy() {
		if(pool != null) 
			pool.shutdown();
	}
	
	public static void execute(Runnable command) {
		if(pool == null) {
			initThreadPool(10);
		}
		pool.execute(command);
	}

}