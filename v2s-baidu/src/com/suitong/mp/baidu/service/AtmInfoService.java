package com.suitong.mp.baidu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suitong.mp.baidu.dao.BaseDao;
import com.suitong.mp.common.util.StringUtil;

public class AtmInfoService {
	
	private BaseDao baseDao = null;
	
	public static final int SQL_TYPE_ALL = 0;
	public static final int SQL_TYPE_UPDATE = 1;
	public static final int SQL_TYPE_LEFTPART = 3;
	
	public static final int OPERATE_TYPE_ADD = 1; 
	public static final int OPERATE_TYPE_UPDATE = 1; 
	
	public AtmInfoService(Map<String, String> jdbcParams){
		String driverClassName = jdbcParams.get("driverClassName");
		String jdbcUrl 		   = jdbcParams.get("jdbcUrl");
		String jdbcUserName	   = jdbcParams.get("jdbcUserName");
		String jdbcPassword    = jdbcParams.get("jdbcPassword");
		
		this.baseDao = new BaseDao(driverClassName, jdbcUrl, jdbcUserName, jdbcPassword);
	}
	
	/**
	 * 翻页获取ARM信息
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Map<String,Object>> getAtmInfoListByPage(int pageNo, int pageSize ){
		List<Object> params = new ArrayList<Object>();
		params.add(pageNo*pageSize);
		params.add((pageNo-1)*pageSize);
		StringBuffer sql = new StringBuffer(" select * from ( select rownum rn, t.* from( ")
//			.append(" select * from  V2S_ATM_INFO a where C_BD_X is not null and C_BD_Y is not null order by C_TERMID asc ")
			.append(" select * from V2S_ATM_INFO where C_TERMID = '0000' union all ")
			.append(" select * from ( ")
			.append(" select a.* from V2S_ATM_INFO a, tml_info@dbl_fsms b where a.C_TERMID = b.C_TERMID and b.C_USE_STATE <> 4 order by a.C_TERMID asc ")
			.append(" ) t1 ")
			.append(" ) t where rownum <= ? ) tt where tt.rn > ?");
		
		List<Map<String,Object>> atmList = this.baseDao.getJdbc().queryForList(sql.toString(), params.toArray());
		return atmList;
	}
	
	/**
	 * 查询公司的坐标点
	 * @return
	 */
	public Map<String,Object> queryCompanyMap(){
		StringBuffer sql = new StringBuffer(" select * from V2S_ATM_INFO where C_TERMID = '0000' ");
		List<Map<String,Object>> list = this.baseDao.getJdbc().queryForList(sql.toString());
		Map<String,Object> map = null;
		if(list != null && list.size() > 0){
			map = list.get(0);
		}
		return map;
	}
	
	/**
	 * 获取ATM总数
	 * @return
	 */
	public int getAtmInfoCount(){
		StringBuffer sql = new StringBuffer(" select count(1) from  V2S_ATM_INFO a where C_BD_X is not null and C_BD_Y is not null order by C_TERMID asc ");
		int count = this.baseDao.getJdbc().queryForObject(sql.toString(), Integer.class);
		return count;
	}
	
	/**
	 * 获取所有ARM信息
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Map<String,Object>> getAtmInfoList(){
		StringBuffer sql = new StringBuffer(" select * from  V2S_ATM_INFO a where C_BD_X is not null and C_BD_Y is not null order by C_TERMID asc ");
		List<Map<String,Object>> atmList = this.baseDao.getJdbc().queryForList(sql.toString() );
		return atmList;
	}
	
	/**
	 * 获取所有ARM信息
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Map<String,Object>> getLeftDestinationList(String termStart){
//		StringBuffer sql = new StringBuffer(" select * from V2S_ATM_INFO a where not exists ( select 1 from V2S_ATM_FORECAST b where b.C_TERM_START = ? and a.C_TERMID = b.C_TERM_END and b.I_PAGENO is not null) ");
		StringBuffer sql = new StringBuffer(" select * from ( select * from V2S_ATM_INFO where C_TERMID = '0000' union all ")
			.append(" select * from ( select a.* from V2S_ATM_INFO a, tml_info@dbl_fsms b where a.C_TERMID = b.C_TERMID ")
			.append(" and b.C_USE_STATE <> 4 ) t1 ")
			.append(" ) tt where not exists ( select 1 from V2S_ATM_FORECAST b where b.C_TERM_START = ? and tt.C_TERMID = b.C_TERM_END and b.I_PAGENO is not null)  ");
		List<Map<String,Object>> atmList = this.baseDao.getJdbc().queryForList(sql.toString(), new Object[]{termStart});
		return atmList;
	}
	
	/**
	 * 获取所有ARM信息
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Map<String,Object>> getLeftDestinationList(){
		/***OK没问题的SQL
		StringBuffer sql = new StringBuffer(" select * from V2S_ATM_INFO where C_TERMID = '0000' union all ")
			.append(" select * from ( select a.* from V2S_ATM_INFO a, tml_info@dbl_fsms b where a.C_TERMID = b.C_TERMID ")
			.append(" and b.C_USE_STATE <>4 ) t1 ");
		***/
		
		/***四行采集
		StringBuffer sql = new StringBuffer(" select t.* from V_ATM_TML_INFO t where 1=1 and c_orgid in ")
			.append(" ('033','032','034','026','028','030','031','029','027','006','036','024','017','005','036','024','017','005') and c_use_state != '4' ");
		***/
		/***工行、华夏
		StringBuffer sql = new StringBuffer(" select t.* from V_ATM_TML_INFO t where 1=1 and c_orgid in ")
			.append(" ('013') and c_use_state != '4' ");
		***/
		/***招行、浦发、花旗、长沙、南昌、渤海、华兴
		StringBuffer sql = new StringBuffer(" select t.* from V_ATM_TML_INFO t where 1=1 and c_orgid in ")
			.append(" ('037','025','035','014','023','016','004') and c_use_state != '4' ");
		***/
		
		/***修正中午的数据***/
		StringBuffer sql = new StringBuffer(" select * from V2S_ATM_INFO where C_TERMID in ( ")
			.append(" select distinct C_TERM_END from V2S_ATM_FORECAST a where a.C_TERM_END in ( ")
			.append(" select t.C_TERMID from V_ATM_TML_INFO t where 1=1 and c_orgid in ")
			.append(" ( '001','013' ) and c_use_state != '4' ")
			.append(" ) and C_UPDATE_TIME is not null and substr(C_UPDATE_TIME, 12,20) between '07:00:00' and '23:59:00') ");
		
		List<Map<String,Object>> atmList = this.baseDao.getJdbc().queryForList( sql.toString() );
		
		/***四行采集
		Map<String,Object> companyMap = this.queryCompanyMap();
		atmList.add(companyMap);
		***/
		/***工行、华夏
		Map<String,Object> companyMap = this.queryCompanyMap();
		atmList.add(companyMap);
		***/
		return atmList;
	}
	
	/**
	 * 判断该起点终点的百度数据是否存在
	 * @param termStart
	 * @param termEnd
	 * @return
	 */
	public boolean isExistForecaseInfo(String termStart, String termEnd){
		StringBuffer sql = new StringBuffer(" select C_ID from V2S_ATM_FORECAST where C_TERM_START = ? and C_TERM_END = ? and I_PAGENO is not null ");
		List<Map<String,Object>> list = this.baseDao.getJdbc().queryForList(sql.toString(), new Object[]{ termStart, termEnd });
		boolean isExist = false;
		if(list != null && list.size() > 0 ){
			isExist = true;
		}
		return isExist;
	}
	
	/**
	 * 保存百度数据
	 * @param params
	 * @return
	 */
	public int addForecaseInfo(List<Object> params){
		String sql = " insert into V2S_ATM_FORECAST (C_ID, C_TERM_START, C_TERM_END, I_DISTANCE, C_DISTANCE_TEXT, I_DURATION, C_DURATION_TEXT, C_EXECUTE_COMPUTER, I_PAGENO, C_UPDATE_TIME ) values (?,?,?,?,?,?,?,?,?,? ) ";
		int cnt = this.baseDao.getJdbc().update(sql, params.toArray());
		return cnt;
	}
	
	/**
	 * 更新百度数据
	 * @param params
	 * @return
	 */
	public int updateForecaseInfo(List<Object> params){
		String sql = " update V2S_ATM_FORECAST set I_DISTANCE = ?, C_DISTANCE_TEXT = ?, I_DURATION = ?, C_DURATION_TEXT = ?, C_UPDATE_TIME = ? where C_TERM_START = ? and C_TERM_END = ? ";
		int cnt = this.baseDao.getJdbc().update(sql, params.toArray());
		return cnt;
	}
	
	/**
	 * 获取某台电脑最后录入的是哪个起点到哪个终点的数据
	 * @param computer
	 * @return
	 */
	public List<Map<String, Object>> getMaxPageRecordList(String computer){
		String sql = " select MAX(I_PAGENO) as I_PAGENO, MAX(I_ORIGIN_ROW) as I_ORIGIN_ROW, MAX(I_DESTINATION_ROW) as I_DISTINATION_ROW from V2S_ATM_FORECAST where C_EXECUTE_COMPUTER = ? group by I_ORIGIN_ROW ";
		List<Map<String, Object>> result  = this.baseDao.getJdbc().queryForList(sql, new Object[]{computer});
		return result;
	}
	
	
	/**
	 * 获取新增或修改的ATM信息
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Map<String,Object>> getModifyAtmInfoListByPage( int pageNo, int pageSize, String modifyTime ){
		List<Object> params = new ArrayList<Object>();
		/***OK没问题的SQL
		StringBuffer sql = new StringBuffer(" select * from ( select rownum rn, tt.* from ( ")
			.append(" select * from V2S_ATM_INFO where C_TERMID = '0000' union all ")
			.append(" select * from ( select a.* from V2S_ATM_INFO a, tml_info@dbl_fsms b ")
			.append(" where a.C_TERMID = b.C_TERMID and b.C_USE_STATE <> 4 and not exists ")
			.append(" (select 1 from V2S_ATM_FORECAST c where a.C_TERMID = c.C_TERM_START ) order by a.C_TERMID asc ) t1 union all")
			.append(" select a.* from V2S_ATM_INFO a, tml_info@dbl_fsms b where a.C_TERMID = b.C_TERMID and b.C_USE_STATE <> 4 ")
			.append(" and ( a.C_MODIFY_TIME >= ? or a.C_MODIFY_TIME is null) ) tt where rownum <=? ) ttt where ttt.rn >? ");
		if(modifyTime.length() >10 ){
			modifyTime = modifyTime.substring(0,10);
		}
		params.add(modifyTime);
		***/
		
		/***四行采集
		StringBuffer sql = new StringBuffer(" select * from ( select rownum rn, t.* from V_ATM_TML_INFO t where 1=1 ")
			.append(" and c_orgid in ('033','032','034','026','028','030','031','029','027','006','036','024','017','005','036','024','017','005') ") 
			.append(" and c_use_state != '4' and rownum <=? ) tt where tt.rn >? ");
		***/
		
		/***工行、华夏
		StringBuffer sql = new StringBuffer(" select * from ( select rownum rn, t.* from V_ATM_TML_INFO t where 1=1 ")
			.append(" and c_orgid in ('007') ") 
			.append(" and c_use_state != '4' and rownum <=? ) tt where tt.rn >? ");
		***/
		/***
		StringBuffer sql = new StringBuffer(" select * from V_ATM_TML_INFO where C_ORGID in('013') and ( ")
		   .append(" C_TERMID not in ( select distinct C_TERM_START from V2S_ATM_FORECAST a where a.C_TERM_START in ( ")
		   .append(" select t.C_TERMID from V_ATM_TML_INFO t where 1=1 and c_orgid in ( '013' ) and c_use_state != '4' ) ) ")
		   .append(" or C_TERMID not in ( select distinct C_TERM_END from V2S_ATM_FORECAST a where a.C_TERM_END in ( ")
		   .append(" select t.C_TERMID from V_ATM_TML_INFO t where 1=1 and c_orgid in ( '013' ) and c_use_state != '4'))) ");
		***/
		/***招行、浦发、花旗、长沙、南昌、渤海、华兴
		StringBuffer sql = new StringBuffer(" select * from ( select rownum rn, t.* from V_ATM_TML_INFO t where 1=1 ")
			.append(" and c_orgid in ('037','025','035','014','023','016','004') ") 
			.append(" and c_use_state != '4' and rownum <=? ) tt where tt.rn >? ");
		***/
		/***修正中午采集的数据
		 * 中行、光大、广银、平安 '033','032','034','026','028','030','031','029','027','006','036','024','017','005','036','024','017','005'
		 * 
		StringBuffer sql = new StringBuffer(" select * from (select rownum rn, aa.* from V2S_ATM_INFO aa where aa.C_TERMID in ( ")
			.append(" select distinct C_TERM_START from V2S_ATM_FORECAST a where a.C_TERM_START in ( ")
            .append(" select t.C_TERMID from V_ATM_TML_INFO t where 1=1 and c_orgid in ")
            .append(" ( '004', '014', '039' ) and c_use_state != '4' ")
            .append(" ) and substr(C_UPDATE_TIME, 12,20) between '07:00:00' and '23:59:00' ) and rownum <=? ) tt where rn >? ");
		***/
		
		/***四行剩余中午时分 '033','032','034','026','028','030','031','029','027','006','036','024','017','005','036','024','017','005'***/
		StringBuffer sql = new StringBuffer(" select * from V2S_ATM_FORECAST a where ( a.C_TERM_START in ( ")
			.append(" select t.C_TERMID from V_ATM_TML_INFO t where 1=1 and c_orgid in ")
			.append(" ( '001','013' ) and c_use_state != '4' ")
			.append(" ) or a.C_TERM_END in (select t.C_TERMID from V_ATM_TML_INFO t where 1=1 and c_orgid in ")
			.append(" ( '001','013' ) and c_use_state != '4' ) ) ")
			.append(" and C_UPDATE_TIME is not null and substr(C_UPDATE_TIME, 12,20) between '07:00:00' and '23:59:00' ");
		
		/***
		params.add(pageNo*pageSize);
		params.add((pageNo-1)*pageSize);
		***/
		List<Map<String,Object>> atmList = this.baseDao.getJdbc().queryForList(sql.toString(), params.toArray() );
		/***四行采集
		Map<String,Object> companyMap = this.queryCompanyMap();
		atmList.add(companyMap);
		***/
		
		/***工行、华夏
		Map<String,Object> companyMap = this.queryCompanyMap();
		atmList.add(companyMap);
		***/
		return atmList;
	}
	
	/**
	 * 根据设备编号获取ATM的其他信息
	 * @param termId
	 * @return
	 */
	public Map<String, Object> findAtmInfoByTermId( String termId ){
		StringBuffer sql = new StringBuffer(" select * from V2S_ATM_INFO where C_TERMID = ? ");
		List<Map<String,Object>> atmList = this.baseDao.getJdbc().queryForList(sql.toString(), new Object[]{termId});
		Map<String,Object> atmMap = null;
		if(atmList != null && atmList.size() > 0 ){
			atmMap = atmList.get(0);
		}
		return atmMap;
	}
	
	/**
	 * 根据查询条件获取需求
	 * @param queryParams
	 * @return
	 */
	public List<Map<String,Object>> requireSql(Map<String,String> queryParams) {
		 StringBuffer sql = new StringBuffer();
		 List<Object> params = new ArrayList<Object>();
		 sql.append("select t1.* from v_task_require_gps2 t1 where 1=1");
		 //sql.append(" and c_status='0'");
		 sql.append(" and c_show_status='1'");
		 String taskDate = StringUtil.toString(queryParams.get("TASK_DATE"));
		 if(taskDate != null && !"".equals(taskDate) ) {
			 sql.append(" and d_task_date = to_date(?,'yyyy-MM-dd')");
			 params.add( taskDate );
		 }
		 
		 String orgIds = StringUtil.toString(queryParams.get("ORGIDS"));
		 if( orgIds != null && !"".equals(orgIds) ){
			 String[] subOrgList = orgIds.split(",");
			 StringBuffer subOrgIdSql = new StringBuffer("");
			 for(String orgId : subOrgList){
				if(!"".equals(orgId)){
					subOrgIdSql.append("?,");
					params.add(orgId.trim());
				}
			 }
			 sql.append(" and c_orgid in (").append(subOrgIdSql).append("'' ) ");
		 }
		 
		 String areaIds = StringUtil.toString(queryParams.get("AREAIDS"));
		 StringBuffer subAreaIdSql = new StringBuffer("");
		 if(!"".equals(areaIds)){
			String[] areaIdArr = areaIds.split(",");
			for(String areaId : areaIdArr){
				if(!"".equals(areaId)){
					subAreaIdSql.append("?,");
					params.add(areaId.trim());
				}
			}
		 }
		 if(!"".equals(subAreaIdSql.toString())){
			sql.append(" and c_area_id in (").append(subAreaIdSql).append("'' ) ");
		 }
		 String workType = StringUtil.toString(queryParams.get("WORK_TYPE"));
		 if(workType != null && !"".equals(workType)) {
			 sql.append(" and (");
			 String[] types = workType.split(",");
			 for(int i = 0 ;i<types.length;i++) {
				 String type = types[i].trim();
				 if(i != 0) {
					 sql.append(" or");
				 }
				 if("".equals(type)){
					 sql.append(" c_worktype like '"+type+"'");
				 }else {
					 sql.append(" c_worktype like '%"+type+"%'");
				 }
			 }
			 sql.append(")");
		 }
		 
		 List<Map<String,Object>> rtList = this.baseDao.getJdbc().queryForList(sql.toString(), params);
		 return rtList;
	}
	
	/**
	 * 获取指定设备中不存在采集数据的设备
	 * @param code
	 * @return
	 */
	public List<Map<String,Object>> queryUnExistForcastItems(Map<String,String> queryParams){
		List<Object> params = new ArrayList<Object>();
		String taskDate = StringUtil.toString(queryParams.get("TASK_DATE"));
		
		StringBuffer sql = new StringBuffer(" select distinct(TERM_START) as C_TERMID from ( ")
			.append(" select a.C_TERMID as TERM_START, b.C_TERMID as TERM_END from v_task_require_gps2 a ")
			.append(" cross join v_task_require_gps2 b where a.C_TASK_TIME =  b.C_TASK_TIME and a.D_TASK_DATE = b.D_TASK_DATE ")
			.append(" and a.D_TASK_DATE = to_date(?,'yyyy-MM-dd') ");
		params.add(taskDate);
		
		String orgIds = StringUtil.toString(queryParams.get("ORGIDS"));
		if( orgIds != null && !"".equals(orgIds) ){
			 String[] subOrgList = orgIds.split(",");
			 StringBuffer subOrgIdSql = new StringBuffer("");
			 //第一个in条件
			 for(String orgId : subOrgList){
				if(!"".equals(orgId)){
					subOrgIdSql.append("?,");
					params.add(orgId.trim());
				}
			 }
			 //第二个in条件
			 for(String orgId : subOrgList){
				if(!"".equals(orgId)){
					params.add(orgId.trim());
				}
			 }
			 sql.append(" and a.C_ORGID in (").append(subOrgIdSql).append("'' ) ");
			 sql.append(" and b.C_ORGID in (").append(subOrgIdSql).append("'' ) ");
		}
		
		String workType = StringUtil.toString(queryParams.get("WORK_TYPE"));
		if(workType != null && !"".equals(workType)) {
			StringBuffer sqlA = new StringBuffer(" and (");
			StringBuffer sqlB = new StringBuffer(" and (");
			String[] types = workType.split(",");
			for(int i = 0 ;i<types.length;i++) {
				String type = types[i].trim();
				if(i != 0) {
					sqlA.append(" or");
					sqlB.append(" or");
				}
				if("".equals(type)){
					sqlA.append(" a.c_worktype like '"+type+"'");
					sqlB.append(" a.c_worktype like '"+type+"'");
				}else {
					sqlA.append(" a.c_worktype like '%"+type+"%'");
					sqlB.append(" a.c_worktype like '%"+type+"%'");
				}
			}
			sqlA.append(")");
			sqlB.append(")");
			sql.append(sqlA);
			sql.append(sqlB);
		}
		
		sql.append(" )tt where not exists ( ");
		sql.append(" select 1 from V2S_ATM_FORECAST t where tt.TERM_START = t.C_TERM_START and tt.TERM_END = t.C_TERM_END ) ");
		
		List<Map<String,Object>> result = this.baseDao.getJdbc().queryForList(sql.toString(), params.toArray());
		return result;
	}
	
	/**
	 * 查询线路关联的点信息
	 * @param lineSn
	 * @return
	 */
	public List<Map<String, Object>> getLineInfosBySn(String lineSn){
		
		StringBuffer infoSql = new StringBuffer(" select a.*, b.I_ISLUNCH, b.I_IS_CUSTOMTIME, b.I_LUNCH_TIME, b.I_RISK_TIME from V2S_TEMP_TASK_INFO a, V2S_TEMP_TASK_LINE b where a.C_LINE_SN = b.C_LINE_SN and a.C_LINE_SN in (");
		List<Object> params = new ArrayList<Object>();
		StringBuffer subSql = new StringBuffer("");
		String[] sns = lineSn.split(",");
		for(String sn:sns){
			subSql.append("?,");
			params.add(sn);
		}
		
		String subSqlStr = subSql.toString();
		String regex = "\\S+,$";
		if(subSqlStr.matches(regex)){
			subSqlStr = subSqlStr.substring(0, subSqlStr.length()-1);
		}
		infoSql.append(subSqlStr).append(" ) order by a.I_ORDER asc");
		
		List<Map<String,Object>> rtList = this.baseDao.getJdbc().queryForList(infoSql.toString(), params.toArray());
		return rtList;
	}
	
	/***
	 * 获取ATM的坐标信息
	 * @param termId
	 * @return
	 */
	public Map<String,Object> queryAtmInfoByTermId(String termId){
		String sql = " select * from V2S_ATM_INFO where C_TERMID = ? "; 
		Map<String,Object> atmMap = this.baseDao.getJdbc().queryForMap(sql, new Object[]{termId} );
		return atmMap;
	}
	
	/***
	 * 根据线路编号获取相关设备点
	 * @param lineSns
	 * @return
	 */
	public List<Map<String,Object>> queryOriginListByLineSn(String lineSns){
		StringBuffer sql = new StringBuffer(" select * from V2S_TEMP_TASK_INFO where C_LINE_SN in(");
		
		List<Object> params = new ArrayList<Object>();
		
		StringBuffer subSql = new StringBuffer("");
		String[] sns = lineSns.split(",");
		for(String sn:sns){
			subSql.append("?,");
			params.add(sn);
		}
		
		String subSqlStr = subSql.toString();
		String regex = "\\S+,$";
		if(subSqlStr.matches(regex)){
			subSqlStr = subSqlStr.substring(0, subSqlStr.length()-1);
		}
		sql.append(subSqlStr).append(" ) ");
		
		List<Map<String,Object>> result = this.baseDao.getJdbc().queryForList(sql.toString(), params.toArray());
		return result;
	}
	
	/**
	 * 获取所有ARM信息
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public List<Map<String,Object>> getAllDestinationList(){
		/***工行、华夏***/
		StringBuffer sql = new StringBuffer(" select t.* from V_ATM_TML_INFO t where c_use_state != '4' ");
		
		List<Map<String,Object>> atmList = this.baseDao.getJdbc().queryForList( sql.toString() );
		
		Map<String,Object> companyMap = this.queryCompanyMap();
		atmList.add(companyMap);
		
		return atmList;
	}
	
	
}
