package com.suitong.mp.baidu.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.suitong.mp.baidu.model.RouteMatrixElement;
import com.suitong.mp.baidu.model.RouteMatrixJson;
import com.suitong.mp.baidu.service.AtmInfoService;
import com.suitong.mp.baidu.timer.AtmForecastTimer;
import com.suitong.mp.baidu.timer.TaskManager;
import com.suitong.mp.common.util.StringUtil;

/**
 * 将新增或修改的ATM信息重新获取距离和时间
 * @author suitong
 *
 */
public class AtmForecastUpdateThread implements Runnable {
	
	private static final Logger logger = Logger.getLogger("com.suitong.mp.baidu.update");
	
	private String[] aks = null;
	private Map<String,String> akMap = new HashMap<String,String>();
	private RouteMatrixJson matrixJson = null;
	
	private List<Map<String,Object>> originList = new ArrayList<Map<String,Object>>();
	private AtmInfoService atmInfoService = null;
	//最大尝试次数
	private int maxTryNum = 5;
	//记录当前尝试次数
	private int tryNum = 5;
	
	private String computer;
	private long sleepTime = 500;
	
	private int operateType = 2;
	//采集几天前的ATM数据
	private int lastModifyDay = 2;
	
	private Map<String, String> jdbcParams = new HashMap<String, String>();
	//执行continue的次数
	private int continueTime = 0;
	private int maxContinueTime = 5;
	
	private int pageNo = 0;
	private int pageSize = 0;
	
	private TaskManager taskManager = null;
	
	private DateFormat dShortFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private String COMPANY_TERM = "0000";
	
	private String modifyTime = null;
	
	private DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public AtmForecastUpdateThread(int pageNo, int pageSize){
		this.taskManager = TaskManager.getInstance();
		this.jdbcParams = taskManager.getJdbcParams();
		this.aks = taskManager.getAks();
		this.computer = taskManager.getComputer();
		this.atmInfoService = new AtmInfoService(jdbcParams);
		this.sleepTime = Long.parseLong(jdbcParams.get("THREAD_SLEEPTIME"));
		this.operateType = Integer.parseInt(jdbcParams.get("OPERATE_TYPE"));
		this.tryNum = Integer.parseInt(jdbcParams.get("MAX_TRY_NUMBER"));
		this.maxTryNum = Integer.parseInt(jdbcParams.get("MAX_TRY_NUMBER"));
		this.lastModifyDay = -Integer.parseInt(jdbcParams.get("ATM_LAST_MODIFYDAY"));
		
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}
	
	public void run() {
		tryNum = 5;
		if( taskManager.isTaskRunning() ){
			logger.info("[AtmForecastThread] threadId->"+Thread.currentThread().getId()+" isTaskRunning->"+taskManager.isTaskRunning() );
			return;
		}
		logger.info("[AtmForecastUpdateThread] threadId->"+Thread.currentThread().getId()+" sleepTime->"+sleepTime+" ===begin===");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, this.lastModifyDay);
		
		/***四行采集***/
//		Map<String,Object> companyMap = atmInfoService.queryCompanyMap();
//		originList.add(companyMap);
		
		
		this.modifyTime = dShortFormat.format(calendar.getTime());
		List<Map<String,Object>> atmList = atmInfoService.getModifyAtmInfoListByPage(this.pageNo, this.pageSize, modifyTime);
		if(atmList != null && atmList.size() > 0 ){
			originList.addAll(atmList); 
		}
		logger.info("[AtmForecastUpdateThread] threadId->"+Thread.currentThread().getId()+" modifyTime->"+modifyTime+"  originList->"+originList.size() );
		
		taskManager.setTaskRunning(true);
		if(originList!=null && originList.size() > 0){
			try {
				initAtmForecastInfo();
				logger.info("[AtmForecastUpdateThread] threadId->"+Thread.currentThread().getId()+" ===finished===");
				taskManager.setTaskRunning(false);
				//如果continue次数达到最大值，则说明ak已被百度禁止，通过重启定时器再去尝试抓取数据
				if(continueTime == maxContinueTime){
					//先把原来的定时器关掉,再重启定时器任务
					taskManager.closeTimer();
					Thread.sleep(sleepTime);
					//再重启定时器任务
					AtmForecastTimer timerTask = new AtmForecastTimer( new AtmForecastUpdateThread(this.pageNo, this.pageSize) );
					taskManager.beginAtmForecastTask( timerTask );
				}
			} catch (Exception e) {
				logger.error("[AtmForecastUpdateThread] threadId->"+Thread.currentThread().getId()+"  error->"+e.getMessage(),e);
				e.printStackTrace();
			} finally{
				taskManager.setTaskRunning(false);
			}
		}
	}
	
	public void initAtmForecastInfo() {
		if(aks == null || aks.length == 0){
			logger.info("[AtmForecastThread.initAtmForecastInfo] threadId->"+Thread.currentThread().getId()+"  aks is empty! ");
			return;
		}
		if(akMap.isEmpty()){
			akMap.put("ak", aks[0].trim());
			akMap.put("num", "0");
		}
		
		try {
			getAtmForecastInfoFromBaidu(originList);
		} catch (Exception e) {
			logger.error("[AtmForecastThread.initAtmForecastInfo] threadId->"+Thread.currentThread().getId()+"  error->"+e.getMessage(),e);
			e.printStackTrace();
			//如果发生异常则重新尝试遍历操作
			logger.error("[AtmForecastThread.initAtmForecastInfo] threadId->"+Thread.currentThread().getId()+" try again");
			initAtmForecastInfo();
		}
		
	}
	
	/**
	 * 从百度获取坐标的距离信息
	 * @param origins
	 * @param destinations
	 * @return
	 * @throws Exception
	 */
	public Map<String,RouteMatrixJson> getAtmForecastInfoFromBaidu(List<Map<String, Object>> forecastList ) throws Exception{
		Map<String,RouteMatrixJson> jsonMap = new HashMap<String,RouteMatrixJson>();
		if(forecastList == null ){
			return jsonMap;
		}
//		List<Map<String,Object>> destinations = new ArrayList<Map<String,Object>>();
//		List<Map<String,Object>> leftDestinations = atmInfoService.getLeftDestinationList();
		
		for(int i = 0; i< forecastList.size() ; i++){
			Map<String, Object> forecast = forecastList.get(i);
			String termStart = StringUtil.toString(forecast.get("C_TERM_START"));
			String termEnd = StringUtil.toString(forecast.get("C_TERM_END"));
			
			//如果是公司为起点，则只对新增或更新的点作遍历
			/***OK这里没问题,暂时屏蔽
			if( COMPANY_TERM.equals(termStart) ){
				destinations = atmInfoService.getModifyAtmInfoListByPage(1, 20000, modifyTime);
			}else {
				destinations = leftDestinations;
			}
			destinations = leftDestinations;
			***/
			if( tryNum == 0 ){
				logger.info("[AtmForecastThread.getAtmForecastInfoFromBaidu] threadId->"+Thread.currentThread().getId()+" tryNum->"+tryNum +" ak->"+akMap.get("ak"));
				break;
			}
//			logger.info("[AtmForecastThread] threadId->"+Thread.currentThread().getId()+"  destinations->"+destinations.size() );
//			if( destinations == null || destinations.size() == 0 ){
//				continue;
//			}
//			for(int j = 0; j< destinations.size() ; j++){
//				Map<String,Object> destination = destinations.get(j);
				
				Date udate = new Date();
				String modifyTime = dFormat.format(udate);
				
//				String termEnd = StringUtil.toString(destination.get("C_TERMID"));
				//坚持起点到终点的数据关系是否存在
				boolean isExist = atmInfoService.isExistForecaseInfo(termStart, termEnd);
				//如果数据已存在且配置里面设置为只做新增 或者 数据有误则跳过
				if( (operateType == AtmInfoService.OPERATE_TYPE_ADD && isExist )
						|| "".equals(termStart) || "".equals(termEnd) || termStart.equals(termEnd) ){
					logger.info("[AtmForecastThread] threadId->"+Thread.currentThread().getId()+" operateType->"+operateType+" termStart->"+termStart+" termEnd->"+termEnd);
					List<Object> params = new ArrayList<Object>();
					//如果起点和终点是同一个设备，则默认地理数据为0
					if( !isExist && !"".equals(termStart) && !"".equals(termEnd)){
						//记录ID主键
						String pkId = UUID.randomUUID().toString();
						pkId = pkId.replaceAll("-", "");
						params.add(pkId);
						//起点
						params.add(termStart);
						//终点
						params.add(termEnd);
						//距离
						long distance = 0;
						String distanceText = "";
						params.add(distance);
						params.add(distanceText);
						//时间
						long duration = 0;
						String durationText = "";
						params.add(duration);
						params.add(durationText);
						params.add("127.0.0.1");
						params.add(0);
						params.add(modifyTime);
						atmInfoService.addForecaseInfo(params);
					}
					continue;
				}
				matrixJson = null;
				if( tryNum == 0 ){
					logger.info("[AtmForecastThread.getAtmForecastInfoFromBaidu] threadId->"+Thread.currentThread().getId()+" tryNum->"+tryNum +" ak->"+akMap.get("ak"));
					break;
				}
				
				Map<String,Object> origin = atmInfoService.queryAtmInfoByTermId(termStart);
				Map<String,Object> destination = atmInfoService.queryAtmInfoByTermId(termEnd);
				RouteMatrixJson routeJson = connectBaiduAPI(origin, destination);
				
				if(routeJson == null || routeJson.getResult() == null || routeJson.getResult().getElements() == null ){
					//如果连续 maxContinueTime 次执行到此逻辑则跳出循环，重新执行定时器
					if(continueTime == maxContinueTime){
						break;
					}else {
						continueTime++;
						continue;
					}
				}
				//获取成功则把尝试次数重置
				tryNum = maxTryNum;
				List<RouteMatrixElement> elements = routeJson.getResult().getElements();
				for(RouteMatrixElement element : elements){
					List<Object> params = new ArrayList<Object>();
					if( !isExist ){
						//记录ID主键
						String pkId = UUID.randomUUID().toString();
						pkId = pkId.replaceAll("-", "");
						params.add(pkId);
						//起点
						params.add(termStart);
						//终点
						params.add(termEnd);
						//距离
						long distance = 0;
						String distanceText = "";
						if(element.getDistance() != null && element.getDistance().getValue() != null){
							distance = Long.parseLong(element.getDistance().getValue());
							distanceText = element.getDistance().getText();
						}
						params.add(distance);
						params.add(distanceText);
						//时间
						long duration = 0;
						String durationText = "";
						if(element.getDuration() != null && element.getDuration().getValue() != null){
							duration = Long.parseLong(element.getDuration().getValue());
							durationText = element.getDuration().getText();
						}
						params.add(duration);
						params.add(durationText);
						params.add(computer);
						params.add(0);
						params.add(modifyTime);
						atmInfoService.addForecaseInfo(params);
					}else {
						//距离
						long distance = 0;
						String distanceText = "";
						if(element.getDistance() != null && element.getDistance().getValue() != null){
							distance = Long.parseLong(element.getDistance().getValue());
							distanceText = element.getDistance().getText();
						}
						params.add(distance);
						params.add(distanceText);
						//时间
						long duration = 0;
						String durationText = "";
						if(element.getDuration() != null && element.getDuration().getValue() != null){
							duration = Long.parseLong(element.getDuration().getValue());
							durationText = element.getDuration().getText();
						}
						params.add(duration);
						params.add(durationText);
						params.add(modifyTime);
						//起点
						params.add(termStart);
						//终点
						params.add(termEnd);
						atmInfoService.updateForecaseInfo(params);
					}
				}
				//百度返回响应需要3ms，请求间隔限制 500ms
				Thread.sleep(sleepTime);
			}
			//如果连续 maxContinueTime 次执行到此逻辑则跳出循环，重新执行定时器
//			if(continueTime == maxContinueTime){
//				break;
//			}
//		}
		return jsonMap;
	}
	
	/**
	 * 从百度获取路线预测数据(如果要改为多线程，此方法要弄在线程里面)
	 * @param origin
	 * @param destination
	 * @param ak
	 * @return
	 */
	public RouteMatrixJson connectBaiduAPI(Map<String,Object> origin, Map<String,Object> destination ){
		boolean isContinue = false;
		String akNow = akMap.get("ak");
		if(akNow == null){
			akNow = aks[0].trim();
			akMap.put("ak", akNow);
			akMap.put("num", "0");
		}
		
		String url = "http://api.map.baidu.com/direction/v1/routematrix?output=json"
				+ "&origins="+StringUtil.toString(origin.get("C_BD_Y"))+","+StringUtil.toString(origin.get("C_BD_X"))
				+ "&destinations="+StringUtil.toString(destination.get("C_BD_Y"))+","+StringUtil.toString(destination.get("C_BD_X"))
				+ "&ak="+akNow+"&tactics=11";
		
//		if(!akMarker.equals(akNow)){
			logger.info("[AtmForecastThread.connectBaiduAPI] threadId->"+Thread.currentThread().getId()+" ak->"+akNow 
					+" C_TERM_START->"+ origin.get("C_TERMID")+" C_TERM_END->"+ destination.get("C_TERMID") );
//			akMarker = akNow;
//		}
		
		String lines = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
//			HttpHost proxy = new HttpHost("221.10.102.203", 82, "http");
//			RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setSocketTimeout(8000).setConnectTimeout(8001).build();//设置请求和传输超时时间

			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(8000).setConnectTimeout(8001).build();//设置请求和传输超时时间
			HttpGet httpGet = new HttpGet(url);
			httpGet.setConfig(requestConfig);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			BufferedReader reader = null;
			try {
                HttpEntity entity = response.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                InputStream in = entity.getContent();
    			reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
    			lines = reader.readLine();
            } finally {
            	if(reader != null){
            		reader.close();
            	}
            	response.close();
            }
		} catch (MalformedURLException e) {
			logger.info("[AtmForecastThread.connectBaiduAPI] threadId->"+Thread.currentThread().getId()+" ak->"+akNow+" MalformedURLException->"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("[AtmForecastThread.connectBaiduAPI] threadId->"+Thread.currentThread().getId()+" ak->"+akNow+" IOException->"+e.getMessage());
			e.printStackTrace();
			String akNum = akMap.get("num");
			int akNext = Integer.parseInt(akNum)+1;
			if(akNext >= aks.length){
				logger.info("[AtmForecastThread.connectBaiduAPI] threadId->"+Thread.currentThread().getId()+" AK已用完或者被百度屏蔽了IP，目前使用的AK->"+akMap.get("ak")+" --程序将再次查找可用的AK");
				akMap.clear();
				return matrixJson;
			}
			String akNew = aks[akNext].trim();
			akMap.put("ak", akNew);
			akMap.put("num", akNext+"");
			
			matrixJson = connectBaiduAPI(origin, destination);
			isContinue = true;
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//如果上面发生IO异常则跳过此逻辑
		if( !isContinue ){
			if (lines != null) {
				matrixJson = JSONObject.parseObject(lines, RouteMatrixJson.class);
			}
			//如果获取不到数据则再尝试5次
			if(lines == null || matrixJson == null || matrixJson.getResult() == null 
					|| "401".equals(matrixJson.getStatus()) || matrixJson.getResult().getElements() == null){
				String akNum = akMap.get("num");
				int akNext = Integer.parseInt(akNum)+1;
				if(akNext >= aks.length){
					if(tryNum > 0){
						try {
							tryNum--;
							akNum = "-1";
							Thread.currentThread().sleep(2*60*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}else {
						logger.info("[AtmForecastThread.connectBaiduAPI] threadId->"+Thread.currentThread().getId()+" tryNum->["+tryNum+"] AK已用完或者被百度屏蔽了IP，目前使用的AK->"+akMap.get("ak"));
						return matrixJson;
					}
				}else if(tryNum < maxTryNum){
					tryNum = maxTryNum;
				}
				
				String akNew = aks[Integer.parseInt(akNum)+1].trim();
				akMap.put("ak", akNew);
				akMap.put("num", akNext+"");
				
				matrixJson = connectBaiduAPI(origin, destination);
			}
		}
		
		return matrixJson;
	}
}
