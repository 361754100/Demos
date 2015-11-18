package com.hro.core.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hro.core.common.util.StringUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	@RequestMapping(value="/login.do", method=RequestMethod.POST)
	public @ResponseBody Map<String,Object> login(@RequestBody Map<String,Object> params){
		String userName = StringUtil.toString(params.get("username"));
		String passwd = StringUtil.toString(params.get("passwd"));
		
		Map<String,Object> rtMap = new HashMap<String,Object>();
		rtMap.put("username", userName);
		rtMap.put("passwd", passwd);
		return rtMap;
	}
}
