package com.suitong.mp.baidu.model;

/**
 * 百度地图批量获取坐标距离和时间的JSON对象
 * */
public class RouteMatrixDistance {
	
	private String text;
	private String value;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
