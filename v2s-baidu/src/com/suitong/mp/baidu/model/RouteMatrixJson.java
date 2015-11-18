package com.suitong.mp.baidu.model;

/**
 * 百度地图批量获取坐标距离和时间的JSON对象
 * */
public class RouteMatrixJson {
	private String status;
	private String message;
	private RouteMatrixInfo info;
	private RouteMatrixResult result;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public RouteMatrixInfo getInfo() {
		return info;
	}
	public void setInfo(RouteMatrixInfo info) {
		this.info = info;
	}
	
	public RouteMatrixResult getResult() {
		return result;
	}
	public void setResult(RouteMatrixResult result) {
		this.result = result;
	}
	
}
