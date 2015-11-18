package com.suitong.mp.baidu.model;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.suitong.mp.common.util.StringUtil;

public class AtmHttpReqManager {
	
	private static AtmHttpReqManager instance = null;
	private static BlockingQueue bQueue = new LinkedBlockingQueue<String>();
	private static BlockingQueue linesQueue = new LinkedBlockingQueue<String>();
	private static BlockingQueue correctLinesQueue = new LinkedBlockingQueue<String>();
	private static BlockingQueue pointsQueue = new LinkedBlockingQueue<Map<String,String>>();
	
	private static final Logger logger = Logger.getLogger("com.suitong.mp.baidu.http");
	
	private static class HolderClass{
		private static final AtmHttpReqManager manager = new AtmHttpReqManager();
	}
	
	public static AtmHttpReqManager getInstance(){
		if(instance == null){
			instance = HolderClass.manager;
		}
		return instance;
	}
	
	public void addTermId(String termId){
		bQueue.add(termId);
		logger.info("[AtmHttpReqManager.addTermId] termId->"+termId+" bQueue.size->"+bQueue.size());
	}
	
	public void addLineSns(String lineSns){
		linesQueue.add(lineSns);
		logger.info("[AtmHttpReqManager.addLineSns] lineSns->"+lineSns+" linesQueue.size->"+linesQueue.size());
	}
	
	public void addXYPoints(Map pointMap){
		pointsQueue.add(pointMap);
		logger.info("[AtmHttpReqManager.addXYPoints] pointMap->"+pointMap+" pointsQueue.size->"+pointsQueue.size());
	}
	
	public void addCorrectLineSns(String lineSns){
		correctLinesQueue.add(lineSns);
		logger.info("[AtmHttpReqManager.addCorrectLineSns] correctLinesQueue.size->"+correctLinesQueue.size());
	}
	
	public String getTermId(){
		String termId = StringUtil.toString(bQueue.poll());
		return termId;
	}
	
	public String getLineSns(){
		String lineSns = StringUtil.toString(linesQueue.poll());
		return lineSns;
	}
	
	public String getCorrectLineSns(){
		String lineSns = StringUtil.toString(correctLinesQueue.poll());
		return lineSns;
	}
	
	public Map<String, String> getXYPoint(){
		Map<String, String> pMap = (Map) pointsQueue.poll();
		return pMap;
	}
	
	public boolean isQueueEmpty(){
		return bQueue.isEmpty();
	}
	
	public boolean isLineQueueEmpty(){
		return linesQueue.isEmpty();
	}
	
	public boolean isCorrectLinesQueueEmpty(){
		return correctLinesQueue.isEmpty();
	}
}
