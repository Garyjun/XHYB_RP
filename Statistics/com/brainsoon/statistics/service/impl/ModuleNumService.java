package com.brainsoon.statistics.service.impl;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.query.QuerySortItem;
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.dofile.zip.ZipOrRarUtil;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.statistics.po.ResqsOfModule;
import com.brainsoon.statistics.service.IModuleNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;
import com.brainsoon.system.service.IDictValueService;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

@Service
public class ModuleNumService extends BaseService implements IModuleNumService{
	private JdbcTemplate jdbcTemplate ;
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private IDictValueService dictValueService;
	
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	public File exportRes(List datas,String encryptPwd){
		File resExcel = StatisticsExcelUtils.getExcelFile("ModuleNumExportTemplete.xls", datas);
		String fileName = resExcel.getAbsolutePath();
		String zipName = StringUtils.substringBeforeLast(fileName, ".") + ".zip";
		if (StringUtils.isNotBlank(encryptPwd)) {
			ZipOrRarUtil.zip(fileName, zipName);
			String encryptZip = StringUtils.substringBeforeLast(fileName, ".") + dateformat2.format(new Date()) + "encrypt.zip";
			try {
				resExcel = ZipUtil.encryptZipFile(new File(zipName), encryptZip, encryptPwd);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return resExcel;
	}

	@SuppressWarnings("rawtypes")
	public List queryListByIds(String ids) {
		String hql = " from ResqsOfModule m";
		if(StringUtils.isNotBlank(ids)){
			hql += " where m.id in("+ids+")";
		}
		List datas = query(hql);
		return datas;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doStatistics(int platformId){
		//统计当天的数据
		Date now = new Date();
		String res = baseSemanticSerivce.resModuleStatistics(format.format(now));
		List<LinkedTreeMap> modules = new ArrayList<LinkedTreeMap>();
		Gson gson = new Gson();
		modules = gson.fromJson(res, modules.getClass());
		if(hasRecord(now)){
			if(modules.size()>0){
				delelteRecodrd(now);
				create(modules,now,platformId);
			}
		}else{
			create(modules,now,platformId);
		}
	}
	
	private void delelteRecodrd(Date now) {
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
		String str = sdf.format(now);
		try {
			Date date = sdf.parse(str);
			sql.append("delete from resqs_of_module where filingDate='"+str+"'");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			jdbcTemplate.execute(sql.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

	private boolean hasRecord(Date now) {
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
		String str = sdf.format(now);
		try {
			Date date = sdf.parse(str);
			sql.append("select * from resqs_of_module where filingDate='"+str+"'");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			if(list.size()==0) {
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return true;
	}
	private void create(List<LinkedTreeMap> modules,Date now,int platformId){
		ResqsOfModule rm;
		for (LinkedTreeMap linkedTreeMap : modules) {
			System.out.println(linkedTreeMap);
			String maturityName = String.valueOf(linkedTreeMap.get("libType"));
			if(StringUtils.isNotBlank(maturityName)&&!maturityName.equals("null")){
				String moduleName = String.valueOf(linkedTreeMap.get("module"));
				String resType = String.valueOf(linkedTreeMap.get("type"));
				String tmVersion = String.valueOf(linkedTreeMap.get("version"));
				String grade = String.valueOf(linkedTreeMap.get("grade"));
				String tmGrade = dictValueService.getNameByIndexTag(grade);
				String tmSubject = String.valueOf(linkedTreeMap.get("subject"));
				String tmSubjectDesc = dictValueService.getNameByIndexTag(tmSubject);
				String educationalPhase = String.valueOf(linkedTreeMap.get("educational_phase"));
				String educationalPhaseDesc = dictValueService.getNameByIndexTag(educationalPhase);
				String num = String.valueOf(linkedTreeMap.get("count"));
				rm = new ResqsOfModule();
				rm.setMaturityName(Integer.parseInt(maturityName));
				rm.setModuleName(moduleName);
				rm.setResType(resType);
				rm.setTmGrade(tmGrade);
				rm.setTmVersion(tmVersion);
				rm.setTmSubject(tmSubjectDesc);
				rm.setEducationalPhase(educationalPhaseDesc);
				rm.setFilingDate(now);
				rm.setNum(Integer.parseInt(StringUtils.substringBeforeLast(num, ".")));
				rm.setPlatformId(platformId);
				if(!resType.equals("V01")){
					create(rm);
				}
			}
		}
	}

	public PageResultForTNum queryForPage(Class poClass,QueryConditionList conditionList,int queryType){
		
		PageResultForTNum result = new PageResultForTNum();
		
		StringBuffer sqlSelect = new StringBuffer();
		StringBuffer sqlWhere = new StringBuffer();
		StringBuffer sqlGroup = new StringBuffer();
		StringBuffer sqlOrder = new StringBuffer();
		//sqlSelect.append("SELECT t.id,t.maturityName,t.moduleName,t.resType,t.tmVersion,t.tmSubject,t.educationalPhase,");
		sqlSelect.append("SELECT t.id,t.maturityName,t.moduleName,t.resType,t.tmVersion,t.tmGrade,t.tmSubject,t.educationalPhase,");
		if(queryType == 1){
			//按日查
			sqlSelect.append(" SUM(t.num) AS num,DATE_FORMAT(t.filingDate,'%Y-%m-%d') AS filingDate2");
			//sqlSelect.append(" t.num,t.filingDate AS filingDate2");
		}else if(queryType == 2){
			//按月
			sqlSelect.append(" SUM(t.num) AS num,DATE_FORMAT(t.filingDate,'%Y-%m') AS filingDate2");
		}else if(queryType == 3){
			//按年
			sqlSelect.append(" SUM(t.num) AS num,DATE_FORMAT(t.filingDate,'%Y') AS filingDate2");
		}
		//通用条件
		sqlWhere.append(" FROM resqs_of_module t WHERE 1=1 ");
		
		if(null != conditionList){
			
			List<QueryConditionItem> items = conditionList.getConditionItems();
			
			for (int i = 0; i < items.size(); i++) {
				QueryConditionItem queryConditionItem = items.get(i);
				String filedName = queryConditionItem.getFieldName();
				String filedValue = queryConditionItem.getValue()+"";
				String opt = queryConditionItem.getOperator().getValue();
				if(filedName.equalsIgnoreCase("filingDate_st")){
					filedName = "filingDate";
					opt = ">=";
					//处理value值
					if(queryType == 2){
						filedValue = getMonthStart(filedValue);
					}else if(queryType == 3){
						filedValue = filedValue + "-01-01";
					}
				}
				if(filedName.equalsIgnoreCase("filingDate_et")){
					filedName = "filingDate";
					opt = "<=";
					//处理value值
					if(queryType == 2){
						filedValue = getMonthEnd(filedValue);
					}else if(queryType == 3){
						filedValue = filedValue + "-12-31";
					}
				}
				if (queryConditionItem.getOperator().equals(Operator.IN)) {
					sqlWhere.append(" and ").append(filedName).append(" ").append(opt).append(" (");
					sqlWhere.append(filedValue);
					sqlWhere.append(")");
				} else {
					sqlWhere.append(" and ").append(filedName).append(" ").append(opt).append(" '").append(filedValue).append("'");
				}
			}
			
			//判断是否分组
			//if(queryType == 2 || queryType == 3){
				//按月, 按年 
				//sqlGroup.append(" GROUP BY t.maturityName,t.moduleName,t.resType,t.tmVersion,t.tmSubject,t.educationalPhase,filingDate2");
				sqlGroup.append(" GROUP BY t.maturityName,t.moduleName,t.resType,t.tmVersion,t.tmGrade,t.tmSubject,t.educationalPhase,filingDate2");
			//}
			
			//取排序
			List<QuerySortItem> sortList = conditionList.getSortList();
			StringBuffer order = new StringBuffer(sortList.size()*10);
			
			for (QuerySortItem querySortItem : sortList) {
				String filedName = querySortItem.getFieldName();
				if(StringUtils.isNotBlank(filedName)){
					order.append(" ,").append(filedName).append(" ").append(querySortItem.getSortMode());
				}
			}
			if(order.length() > 0){
				order.delete(0, 2);
				sqlOrder.append(" order by ").append(order);
			}
		}
		
		String sqlStr = sqlSelect.toString() + sqlWhere.toString() + sqlGroup.toString() + sqlOrder.toString();
		Session session = getBaseDao().getSession();
		SQLQuery query = session.createSQLQuery(sqlStr);
		query.setMaxResults(conditionList.getPageSize());
		query.setFirstResult(conditionList.getStartIndex());
//		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.addScalar("id",Hibernate.LONG);
		query.addScalar("maturityName",Hibernate.INTEGER);
		query.addScalar("moduleName",Hibernate.STRING);
		query.addScalar("resType",Hibernate.STRING);
		query.addScalar("tmVersion",Hibernate.STRING);
		query.addScalar("tmSubject",Hibernate.STRING);
		query.addScalar("tmGrade",Hibernate.STRING);
		query.addScalar("educationalPhase",Hibernate.STRING);
		query.addScalar("num",Hibernate.INTEGER);
		query.addScalar("filingDate2",Hibernate.STRING);
		query.setResultTransformer(Transformers.aliasToBean(poClass));
		result.setRows(query.list());
		String countSql = "select count(id) from (" + sqlStr + ") as t2";
		SQLQuery countQuery = session.createSQLQuery(countSql);
		result.setTotal(new Integer(countQuery.uniqueResult().toString()).intValue());
		
		//查询总数
		String sumNumSQL = "select COALESCE(SUM(num),0) " + sqlWhere.toString();
		SQLQuery sumNumQuery = session.createSQLQuery(sumNumSQL);
		result.setStatisticsNum(new Long(sumNumQuery.uniqueResult().toString()).longValue());
		return result;
	}

	/**
	 * 功能：得到当前月份月初 格式为：xxxx-yy-zz (eg: 2007-12-01)
	 * 
	 * @param time
	 * @return String
	 * @author pure
	 */
	public String getMonthStart(String time) {
		String[] timeArray = StringUtils.split(time, "-");
		String strY = null;
		int x = Integer.parseInt(timeArray[0]);
		int y = Integer.parseInt(timeArray[1]);
		strY = y >= 10 ? String.valueOf(y) : ("0" + y);
		return x + "-" + strY + "-01";
	}

	/**
	 * 功能：得到当前月份月底 格式为：xxxx-yy-zz (eg: 2007-12-31) 
	 * @param time
	 * @return
	 */
	public String getMonthEnd(String time){  
		String [] timeArray = StringUtils.split(time, "-");
        String strY = null;   
        String strZ = null;   
        boolean leap = false;   
        int x = Integer.parseInt(timeArray[0]);   
        int y = Integer.parseInt(timeArray[1]);   
        if (y == 1 || y == 3 || y == 5 || y == 7 || y == 8 || y == 10 || y == 12) {   
            strZ = "31";   
        }   
        if (y == 4 || y == 6 || y == 9 || y == 11) {   
            strZ = "30";   
        }   
        if (y == 2) {   
            leap = leapYear(x);   
            if (leap) {   
                strZ = "29";   
            }else {   
                strZ = "28";   
            }   
        }   
        strY = y >= 10 ? String.valueOf(y) : ("0" + y);   
        return x + "-" + strY + "-" + strZ;   
    }   
	
	/**
	 * 功能：判断输入年份是否为闰年<br>
	 * 
	 * @param year
	 * @return 是：true 否：false
	 * @author pure
	 */
	public boolean leapYear(int year) {
		boolean leap;
		if (year % 4 == 0) {
			if (year % 100 == 0) {
				if (year % 400 == 0)
					leap = true;
				else
					leap = false;
			} else
				leap = true;
		} else
			leap = false;
		return leap;
	}
	
}
