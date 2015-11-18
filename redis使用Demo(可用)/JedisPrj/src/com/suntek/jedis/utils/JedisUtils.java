package com.suntek.jedis.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/** 
 * Redis工具类,用于获取RedisPool. 
 * 参考官网说明如下： 
 * You shouldn't use the same instance from different threads because you'll have strange errors. 
 * And sometimes creating lots of Jedis instances is not good enough because it means lots of sockets and connections, 
 * which leads to strange errors as well. A single Jedis instance is not threadsafe! 
 * To avoid these problems, you should use JedisPool, which is a threadsafe pool of network connections. 
 * This way you can overcome those strange errors and achieve great performance. 
 * To use it, init a pool: 
 *  JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost"); 
 *  You can store the pool somewhere statically, it is thread-safe. 
 *  JedisPoolConfig includes a number of helpful Redis-specific connection pooling defaults. 
 *  For example, Jedis with JedisPoolConfig will close a connection after 300 seconds if it has not been returned. 
 * @author wujintao 
 */  
public class JedisUtils {
	private static JedisUtils instance = new JedisUtils();
	private static Map<String,JedisPool> poolMap = new ConcurrentHashMap<String, JedisPool>();
	private static final int MaxActive = 100;
	private static final int MaxIdle = 50;
	private static final long MaxWait = 5000;
	private static final int ReadTimeOut = 2;
	private static final int RetryNum = 10;
	
	private JedisUtils(){
		
	}
	
	private static class HolderClass{
		private static JedisUtils util = new JedisUtils();
	}
	
	public static JedisUtils getInstance(){
		if(instance == null){
			instance = HolderClass.util;
		}
		return instance;
	}
	
	 /** 
     * 获取连接池. 
     * @return 连接池实例 
     */  
    public static JedisPool getPool(String ip,int port) {  
        String key = ip+":" +port;  
        JedisPool pool = null;  
        if(!poolMap.containsKey(key)) {  
        	JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxActive(MaxActive);
            config.setMaxIdle(MaxIdle);
            config.setMaxWait(MaxWait);
            config.setTestOnBorrow(true);  
            config.setTestOnReturn(true);
            try{    
                /** 
                 *如果你遇到 java.net.SocketTimeoutException: Read timed out exception的异常信息 
                 *请尝试在构造JedisPool的时候设置自己的超时值. JedisPool默认的超时时间是2秒(单位毫秒) 
                 */  
                pool = new JedisPool(config, ip, port, ReadTimeOut);  
                
//                CommonLogger.info("[JedisUtils] pool->"+pool);
                System.out.println("[JedisUtils] pool->"+pool);
                
                poolMap.put(key, pool);  
            } catch(Exception e) {  
                e.printStackTrace();  
            }  
        }else{  
            pool = poolMap.get(key);  
        }  
        return pool;  
    }  
    
    /** 
     * 获取Redis实例. 
     * @return Redis工具类实例 
     */  
    public Jedis getJedis(String ip, int port){
    	Jedis jedis  = null;  
        int count =0;  
        do{  
            try{   
                jedis = getPool(ip,port).getResource();  
                //log.info("get redis master1!");  
            } catch (Exception e) {  
            	e.printStackTrace();
                 // 销毁对象    
                getPool(ip,port).returnBrokenResource(jedis);    
            }  
            count++;  
        }while(jedis==null && count<RetryNum);  
        return jedis;  
    }
    
    /** 
     * 释放redis实例到连接池. 
     * @param jedis redis实例 
     */  
    public void closeJedis(Jedis jedis,String ip,int port) {  
        if(jedis != null) {  
            getPool(ip,port).returnResource(jedis);  
        }  
    }  
}
