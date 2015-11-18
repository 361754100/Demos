package com.suitong.mp.baidu.timer;

import java.util.TimerTask;


public class AtmForecastTimer extends TimerTask {

	private Runnable handler = null;
	public AtmForecastTimer(Runnable handler) {
		// TODO Auto-generated constructor stub
		this.handler = handler;
	}

	@Override
	public void run() {
		ThreadPoolManager.execute(handler);
	}
	
}
