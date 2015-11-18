package com.suitong.mp.baidu.model;

/**
 * 百度地图批量获取坐标距离和时间的JSON对象
 * */
public class RouteMatrixElement {
	
	private RouteMatrixDistance distance;
	private RouteMatrixDuration duration;
	
	public RouteMatrixDistance getDistance() {
		return distance;
	}
	public void setDistance(RouteMatrixDistance distance) {
		this.distance = distance;
	}
	public RouteMatrixDuration getDuration() {
		return duration;
	}
	public void setDuration(RouteMatrixDuration duration) {
		this.duration = duration;
	}
	
}
