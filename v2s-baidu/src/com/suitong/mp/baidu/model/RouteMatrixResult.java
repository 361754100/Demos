package com.suitong.mp.baidu.model;

import java.util.List;

/**
 * 百度地图批量获取坐标距离和时间的JSON对象
 * */
public class RouteMatrixResult {
	
	private List<RouteMatrixElement> elements;

	public List<RouteMatrixElement> getElements() {
		return elements;
	}

	public void setElements(List<RouteMatrixElement> elements) {
		this.elements = elements;
	}
	
}
