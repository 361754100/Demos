package com.hro.core.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hro.core.common.log.MsvrLog;
import com.hro.core.common.util.EhCacheSingleton;
import com.hro.core.common.util.HttpRequestUtil;
import com.hro.core.common.util.StringUtil;
import com.hro.core.model.UserModel;

@Controller
@RequestMapping("/user")
public class UserController {

	/**
	 * 用户登录
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/login.do", method=RequestMethod.POST)
	public @ResponseBody Map<String,Object> login(@RequestBody Map<String,Object> params, HttpServletRequest request){
		String userName = StringUtil.toString(params.get("username"));
		String passwd = StringUtil.toString(params.get("passwd"));
		
		UserModel userModel = new UserModel();
		userModel.setUserName(userName);
		userModel.setPasswd(passwd);
		
		EhCacheSingleton ehcacheSingleton = EhCacheSingleton.getInstance();
		Cache userCache =  ehcacheSingleton.getCache("USER_CACHE");
		if(userCache == null){
			userCache = new Cache("USER_CACHE", 10, true, false, 10*60, 20*60);
			ehcacheSingleton.addCache(userCache);
		}
		userCache.put(new Element( userName, userModel ));
		
		Map<String,Object> rtMap = new HashMap<String,Object>();
		rtMap.put("username", userName);
		rtMap.put("passwd", passwd);
		
		MsvrLog.info("[UserController.login] userName->"+userName+" passwd->"+passwd+" request->"+HttpRequestUtil.getServerIp(request)+":"+ request.getServerPort());
		
		return rtMap;
	}
	
	/**
	 * 查询在线用户
	 * @return
	 */
	@RequestMapping(value="/queryOnlineUsers.do", method=RequestMethod.GET)
	public @ResponseBody List<UserModel> queryOnlineUsers(){
		List<UserModel> cacheList = new ArrayList<UserModel>();
		EhCacheSingleton ehcacheSingleton = EhCacheSingleton.getInstance();
		CacheManager cacheManager = ehcacheSingleton.getCacheManager();
		String[] cacheNames = cacheManager.getCacheNames();
		
		Cache userCache = cacheManager.getCache("USER_CACHE");
		if(userCache != null){
			List<String> keys = userCache.getKeys();
			for(String key: keys){
				Element el = userCache.get(key);
				UserModel model = null;
				if(el != null){
					model = (UserModel) el.getObjectValue();
					
					MsvrLog.info("[UserController.queryOnlineUsers] model->"+model);
					cacheList.add(model);
				}else {
					MsvrLog.warn("[UserController.queryOnlineUsers] model->"+model);
				}
				
			}
		}
		
		return cacheList;
	}
}
