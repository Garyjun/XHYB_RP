package com.brainsoon.statistics.service.impl;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.statistics.po.ResqsOfEffect;
import com.brainsoon.statistics.po.vo.ResultList;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.statistics.support.StatisticsExcelUtils;
import com.brainsoon.system.model.User;
import com.brainsoon.system.support.SystemConstants;

@Service
public class EffectNumService extends BaseService implements IEffectNumService {

	@Override
	public boolean doPiecework(Long userId, String operateType, String maturityType, String starRating, int countNum) {
		ResqsOfEffect effect = new ResqsOfEffect();
		User user = (User) getByPk(User.class, userId);
		user.setId(userId);
		effect.setUserId(userId);
		effect.setOperateType(operateType);
		effect.setMaturityName(maturityType);
		effect.setStarRating(starRating);
		effect.setCountNum(countNum);
		effect.setFilingDate(new Date());
		this.baseDao.create(effect);
		return false;
	}

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
	public ResultList doStatistic(Map<String, String> paramsMap) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT @ROW := @ROW + 1 AS id,  userId,  operateType,  maturityName,  starRating,  filingDate,  SUM(countNum) AS countNum FROM  resqs_of_effect, (SELECT @ROW := - 1) identity WHERE 1 = 1");
		if (StringUtils.isNotBlank(paramsMap.get("userName"))) {
			String userSql = "select id from sys_user where user_name like '%" + paramsMap.get("userName") + "%'";
			List<Long> result = jdbcTemplate.queryForList(userSql, Long.class);
			String ids = StringUtils.substringBetween(result.toString(), "[", "]");
			sql.append(" and userId in (" + ids + ")");
		}
		if (StringUtils.isNotBlank(paramsMap.get("operateType"))) {
			sql.append(" and operateType =  '" + paramsMap.get("operateType") + "'");
		}
		if (StringUtils.isNotBlank(paramsMap.get("maturityName"))) {
			sql.append(" and maturityName =  '" + paramsMap.get("maturityName") + "'");
		}
		if (StringUtils.isNotBlank(paramsMap.get("starRating"))) {
			sql.append(" and starRating =  '" + paramsMap.get("starRating") + "'");
		}
		if (StringUtils.isNotBlank(paramsMap.get("startTime"))) {
			sql.append(" and filingDate >  '" + paramsMap.get("startTime") + "'");
		}
		if (StringUtils.isNotBlank(paramsMap.get("endTime"))) {
			sql.append(" and filingDate <  '" + paramsMap.get("endTime") + "'");
		}
		sql.append(" GROUP BY userId,  operateType,  maturityName,  starRating,  filingDate ORDER BY id");
		List<ResqsOfEffect> allReconds = new ArrayList<ResqsOfEffect>();
		ResultList resultList = new ResultList();
		int totalSum = 0;
		try {
			List rows = jdbcTemplate.queryForList(sql.toString());
			Iterator it = rows.iterator();
			while (it.hasNext()) {
				Map map = (Map) it.next();
				ResqsOfEffect effect = new ResqsOfEffect();
				User user = (User) getByPk(User.class, Long.valueOf(map.get("userId").toString()));
				effect.setId(Long.valueOf(map.get("id").toString()));
				effect.setCountNum(Integer.parseInt(map.get("countNum").toString()));
				if(user!=null){
					effect.setUserName(user.getUserName());
				}
				Object operateType = map.get("operateType");
				Object maturityName = map.get("maturityName");
				Object filingDate = map.get("filingDate");
				Object startRating = map.get("starRating");
				effect.setOperateType(operateType == null ? "" : SystemConstants.OperatType.getValueByKey(operateType.toString()));
				effect.setMaturityName(maturityName == null ? "" : SystemConstants.LibType.getValueByKey(maturityName.toString()));
				effect.setStarRating(startRating == null ? "" : SystemConstants.StarLevel.getValueByKey(startRating.toString()));
				effect.setFilingDate(filingDate == null ? null : (Date) filingDate);
				if (map.get("countNum") != null) {
					totalSum = totalSum + Integer.parseInt(map.get("countNum").toString());
				}
				allReconds.add(effect);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		resultList.setList(allReconds);
		resultList.setTotalSum(totalSum);
		return resultList;
	}

	@Override
	public File exportRes(List datas) {
		File resExcel = StatisticsExcelUtils.getExcelFile("EffectNumExportTemplete.xls", datas);
		return resExcel;
	}

}
