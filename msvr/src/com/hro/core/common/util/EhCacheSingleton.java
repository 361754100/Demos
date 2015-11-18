package com.hro.core.common.util;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import com.hro.core.common.log.MsvrLog;

/**
 * EhCache 缓存单例
 * @author mjzhang
 */
public class EhCacheSingleton {
	
	private static EhCacheSingleton instance = null;
	
	private static CacheManager cacheManager = CacheManager.create();
	
	private static class HolderClass{
		private static final EhCacheSingleton singleton = new EhCacheSingleton();
	}
	
	public static EhCacheSingleton getInstance(){
		if(instance == null){
			instance = HolderClass.singleton;
			//注册被管理的Bean  
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();  
	        ManagementService.registerMBeans(cacheManager, mBeanServer, true, true, true, true);  
		}
		return instance;
	}
	
	public void addCache(Cache cache){
		MsvrLog.info("[EhCacheSingleton.addCache] cache ->"+cache);
		cacheManager.addCache(cache);
	}
	
	public Cache getCache(String name){
		Cache cache = cacheManager.getCache(name);
		
		MsvrLog.info("[EhCacheSingleton.getCache] name ->"+name+" cache ->"+cache);
		
		return cache;
	}
	
	public CacheManager getCacheManager(){
		return this.cacheManager;
	}
	
}
