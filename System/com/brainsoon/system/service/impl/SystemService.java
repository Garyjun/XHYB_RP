package com.brainsoon.system.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.Constants;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.po.CopyrightWarning;
import com.brainsoon.semantic.ontology.model.CopyRightMetaData;
import com.brainsoon.system.model.User;

@Service
public class SystemService extends BaseService implements ISystemService {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public String getInformNums() {
		StringBuffer json = new StringBuffer();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String wfPrivis = LoginUserUtil.getWfPrivis();
		if(LoginUserUtil.getLoginUser()!=null && StringUtils.isNotBlank(wfPrivis)){
			String platfromId = userInfo.getPlatformId()+"";
			String hql = "SELECT COUNT(*) FROM view_wait_task WHERE swim_name in(" + wfPrivis + ") and platform_id='"+platfromId+"'";
			String publishType = "";
			if(userInfo.getResTypes()!=null){
				Iterator it = userInfo.getResTypes().entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pairs = (Map.Entry) it.next();
					if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
						publishType = publishType+pairs.getKey().toString()+",";
					}
				}
				if(publishType.endsWith(",")){
					publishType = publishType.substring(0,publishType.length()-1);
				}
				hql =hql+" and publishType in ("+publishType+")";
			}
			String userIds = userInfo.getDeptUserIds();
			//判断是不是个人用户授权
			int isPrivate = userInfo.getIsPrivate();
			if (isPrivate == 1) {
				if(StringUtils.isNotBlank(userIds)){
				}else{
					userIds = LoginUserUtil.getLoginUser().getUserId()+"";
				}
			} 
			String creatorName = "";
			if (StringUtils.isNotBlank(userIds)) {
				if(userIds.endsWith(",")){
					userIds = userIds.substring(0,userIds.length()-1);
				}
				if(userIds!=null){
				String userIdsArray[] = userIds.split(",");
				for(String userIdsArray1:userIdsArray){
					User user = (User) baseDao.getByPk(User.class, Long.parseLong(userIdsArray1));
					creatorName = creatorName+"'"+user.getLoginName()+"'"+",";
				}
				if(creatorName.endsWith(",")){
					creatorName = creatorName.substring(0,creatorName.length()-1);
				}
				hql =hql+" and apply_user in ("+creatorName+")";
				}
//				hql.append("&creator=" + userIds);
			}else{
				hql =hql+" and apply_user in (-2)";
			}
			
			int taskNum = jdbcTemplate.queryForInt(hql);
			int total = 0;
			HttpClientUtil http = new HttpClientUtil();
			List<CopyrightWarning> warningList = null;
			hql = " from CopyrightWarning where 1=1";
			warningList =  baseDao.query(hql);
			if(warningList!=null && !warningList.isEmpty()){
					total = warningList.size();
			}
			json.append("{");
			json.append("\"taskNum\":" + taskNum);
			json.append(",");
			json.append("\"copyrightNum\":" + total);
			json.append(",");
			json.append("\"total\":" + (total + taskNum));
			json.append("}");
		}

		return json.toString();
	}

}
