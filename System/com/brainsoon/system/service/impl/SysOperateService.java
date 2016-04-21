package com.brainsoon.system.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.dofile.util.OSUtil;
import com.brainsoon.system.model.User;
import com.brainsoon.system.model.log.SysOperateHistory;
import com.brainsoon.system.model.log.SysOperateLog;
import com.brainsoon.system.model.log.SysOperateType;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SystemConstants;

@Service
public class SysOperateService extends BaseService implements ISysOperateService {

	private JdbcTemplate jdbcTemplate ;

	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public void addLog(String operateType, String entityDesc, UserInfo userInfo) throws ServiceException {

		if (logger.isDebugEnabled()) {
			logger.debug("进入SysOperateService.saveLog()...");
		}

		// 操作类型
		List<SysOperateType> typeList = query("from SysOperateType where operateKey='" + operateType + "'");
		SysOperateType optType = null;
		if (null != typeList && !typeList.isEmpty()) {
			optType = typeList.get(0);
		}

		if (optType == null) {
			logger.error("记录业务操作日志出现异常:系统不支持【" + operateType + "】操作日志类型！");
			return;
		}
		SysOperateLog log = new SysOperateLog();
		log.setLogType(SystemConstants.LogType.BUSINESS_LOG);
		log.setOperator(userInfo.getUsername());
		log.setLoginname(userInfo.getName());
		log.setPlatformId(userInfo.getPlatformId());
		log.setOperateType(operateType);
		log.setOperateDesc(optType.getOperateName() + "：" + entityDesc);
		//唐辉添加：如果获取的登录ip为空或者不正确，则主动去获取一下ip地址
		String ip = userInfo.getLoginIp();
		if(ip == null || ip.length() == 0 || ip.equals("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")){
			ip = OSUtil.getLocalIP();
		}
		log.setUserIp(ip);
		log.setSysOperateType(optType);
		log.setOperateTime(new Date());

		try {
			getBaseDao().create(log);
		} catch (DaoException e) {
			if (logger.isErrorEnabled()) {
				logger.error("记录业务操作日志出现异常", e);
			}
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void saveHistory(String wf_id, String remark, String statusDsc, String operateDesc, Date operateTime, Long operateorId) throws ServiceException {
		if (logger.isDebugEnabled()) {
			logger.debug("OperHistoryService.saveHistory()...");
		}
		SysOperateHistory history = new SysOperateHistory();
		history.setBeanId(wf_id);
		history.setBeanStatus(statusDsc);
		history.setRemark(remark);
		history.setOperateDesc(operateDesc);
		if (operateTime == null) {
			history.setOperateTime(new Date());
		} else {
			history.setOperateTime(operateTime);
		}
		User operator = new User();
		operator.setId(operateorId);
		history.setOperator(operator);
		getBaseDao().create(history);
	}

	
	@Override
	public String getSimilarWord(String inputWords) {
		String sql = "SELECT u.login_name,u.user_name FROM sys_user u";
		if(StringUtils.isNotBlank(inputWords)){
			sql = "SELECT u.login_name,u.user_name FROM sys_user u WHERE u.user_name LIKE '%" + inputWords + "%' ORDER BY u.user_name ASC";
		}
		StringBuffer buf = new StringBuffer();
		try {
			List rows = jdbcTemplate.queryForList(sql); 
			Iterator it = rows.iterator();
			buf.append("{data:[");
			int num = 0;
			while (it.hasNext()) {
				num ++;
				Map map = (Map) it.next();
				if(num == rows.size()){
					buf.append("{\"title\":\"" + map.get("login_name").toString() + "\"}");
				}else{
					buf.append("{\"title\":\"" + map.get("login_name").toString() + "\"},");
				}
			}
			buf.append("]}");
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return buf.toString();
	}

	/**
	 * 根据用户的ids获取用户名称
	 * @param ids
	 * @return
	 */
	@Override
	public String getNameByid(String ids) {
		if(ids.endsWith(",")){
			ids = ids.substring(0,ids.length()-1);
		}
		String hql = "select loginName from User where id in ("+ids+")";
		List<String> names = getBaseDao().query(hql);
		String userNames = "";
		for (String string : names) {
			userNames = userNames+",";
			userNames = userNames + "'"+string+"'";
		}
		if(userNames.length()>0 && StringUtils.isNotBlank(userNames)){
			try{
				userNames = userNames.substring(1, userNames.length());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return userNames;
	}

	
}