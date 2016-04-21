package com.brainsoon.system.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.system.model.DictName;
import com.brainsoon.system.model.DictValue;
import com.brainsoon.system.model.Organization;
import com.brainsoon.system.model.Privilege;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.ResTargetData;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IResTargetService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;

@Service
public class ResTargetService extends BaseService implements IResTargetService {
	private static final String PUBLISH_SEARCH_RESOURCE_LIST = WebappConfigUtil
			.getParameter("PUBLISH_SEARCH_RESOURCE_LIST");
	private static final String ADVANCE_SEARCH_RESOURCE_LIST = WebappConfigUtil
			.getParameter("ADVANCE_SEARCH_RESOURCE_LIST");
	private static final String TARGET_QUERY_URL = WebappConfigUtil
			.getParameter("TARGET_QUERY_URL");
	private static final String PUBLISH_QUERY_URL = WebappConfigUtil
			.getParameter("PUBLISH_QUERY_URL");
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private JdbcTemplate jdbcTemplate ;
	@Override
	public List query(String typeTarget, String resId) throws ServiceException {
		if (typeTarget.equals("45")) {
			typeTarget = "3";
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		// String s=resId.substring(9);
		String hql = "";
		if (!typeTarget.equals("")) {
			hql = "from ResTarget where platformId=" + userInfo.getPlatformId()
					+ " and type=" + Long.parseLong(typeTarget)
					+ " and status=1 or type=0";
		} else {
			hql = "from ResTarget where platformId=" + userInfo.getPlatformId()
					+ " and status=1";
		}

		return baseDao.query(hql);
	}

	/**
	 * 标签添加
	 */
	@Override
	public void createTarget(HttpServletRequest request,
			HttpServletResponse response, ResTargetData target)
			throws ServiceException {

		UserInfo userInfo = LoginUserUtil.getLoginUser();
		boolean tarFlag = false;
		boolean firstData = false;
		String tarId = "";
		int size = 0;
		String hql = "from ResTargetData";
		List<ResTargetData> list = baseDao.query(hql);
		logger.info("list.size"+list+"target.getTargetType()"+target.getTargetType());
		ResTargetData targetMysql = new ResTargetData();
		if(list!=null && !list.isEmpty()){
			logger.info("1111111111");
			size = list.size();
			for(int i=0;i<size;i++){
				if(list.get(i).getPid().equals("-1") && list.get(i).getModule().equals(target.getModule()) && list.get(i).getTargetType().equals(target.getTargetType())){
					targetMysql = list.get(i);
					tarFlag = true;
					tarId = targetMysql.getId()+"";
					break;
				}
			}
		}else{
			logger.info("222222222222");
			firstData = true;
			targetMysql.setPid("-1");
			targetMysql.setTargetName(target.getTargetType());
			targetMysql.setModule(target.getModule());
			targetMysql.setUserId(userInfo.getUserId());
			targetMysql.setTargetType(target.getTargetType());
			targetMysql.setTargetStatus(Integer.parseInt("1"));
			baseDao.create(targetMysql);
		}
		try {
			boolean typeFlag = false;
			if (target.getId()!= null) {
				for(int i=0;i<size;i++){
					if(list.get(i).getPid().equals("-1") && list.get(i).getModule().equals(target.getModule()) && list.get(i).getTargetType().equals(target.getTargetType())){
						typeFlag = true;
						break;
					}
				}
				if(typeFlag){
					target.setUserId(userInfo.getUserId());
					target.setPid(tarId);
					//临时加不要状态
					target.setTargetStatus(Integer.parseInt("1"));
					hql = "UPDATE ResTargetData SET pid='"+tarId+"', targetType='"+target.getTargetType()+"', xpath='"+targetMysql.getTargetType()+"', module='"+target.getModule()+"', targetStatus="+target.getTargetStatus()+" WHERE id="+target.getId();
					baseDao.updateWithHql(hql);
				}else{
					targetMysql.setPid(Long.parseLong("-1")+"");
					targetMysql.setTargetName(target.getTargetType());
					targetMysql.setModule(target.getModule());
					targetMysql.setUserId(userInfo.getUserId());
					targetMysql.setTargetType(target.getTargetType());
					targetMysql.setTargetStatus(Integer.parseInt("1"));
					baseDao.create(targetMysql);
					target.setUserId(userInfo.getUserId());
					target.setPid(tarId);
					//临时加不要状态
					target.setTargetStatus(Integer.parseInt("1"));
					hql = "UPDATE ResTargetData SET pid='"+targetMysql.getId()+"', targetType='"+target.getTargetType()+"', xpath='"+targetMysql.getTargetType()+"', module='"+target.getModule()+"', targetStatus="+target.getTargetStatus()+" WHERE id="+target.getId();
					baseDao.updateWithHql(hql);
				}
					SysOperateLogUtils.addLog("target_publishUpdate",  target.getTargetName(), LoginUserUtil.getLoginUser());	
		// 添加
			} else {
				logger.info("333333333333333");
				if(tarFlag){
					Long num = (long) 0;
					target.setUserId(userInfo.getUserId());
					target.setTargetNum(num);
					target.setStatus(Integer.parseInt("1"));
					target.setPid(targetMysql.getId()+"");
					target.setXpath(target.getTargetName()+","+target.getTargetType());
					//临时加不要状态
					target.setTargetStatus(Integer.parseInt("1"));
					baseDao.create(target);
				}else if(firstData){
					logger.info("44444444444444444");
					Long num = (long) 0;
					target.setUserId(userInfo.getUserId());
					target.setTargetNum(num);
					target.setPid(targetMysql.getId()+"");
					target.setStatus(Integer.parseInt("1"));
					target.setXpath(target.getTargetName()+","+target.getTargetType());
					//临时加不要状态
					target.setTargetStatus(Integer.parseInt("1"));
					baseDao.create(target);
				}else{
					logger.info("5555555555555555555");
					targetMysql.setPid(Long.parseLong("-1")+"");
					targetMysql.setTargetName(target.getTargetType());
					targetMysql.setModule(target.getModule());
					targetMysql.setUserId(userInfo.getUserId());
					targetMysql.setTargetType(target.getTargetType());
					targetMysql.setTargetStatus(Integer.parseInt("1"));
					baseDao.create(targetMysql);
					logger.info("66666666666666");
					Long num = (long) 0;
					target.setUserId(userInfo.getUserId());
					target.setTargetNum(num);
					target.setPid(targetMysql.getId()+"");
					target.setXpath(target.getTargetName()+","+target.getTargetType());
					target.setStatus(Integer.parseInt("1"));
					//临时加不要状态
					target.setTargetStatus(Integer.parseInt("1"));
					logger.info("999999999");
					baseDao.create(target);
				}
					SysOperateLogUtils.addLog("target_publishResSave", target.getTargetName(), LoginUserUtil.getLoginUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 右侧列表标签选项
	 */
		@Override
		public Map<String, Object> bachSelectTarget(String libType, String resId,
				String module, String type) throws ServiceException {
			List<ResTarget> useResTargets = new LinkedList<ResTarget>();
			List<ResTargetData> resTargetData = new LinkedList<ResTargetData>();
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String resIds[] = resId.split(",");
			String hql = "";
			String hq = "";
			List<ResTarget> targetName = new LinkedList<ResTarget>();
			hql = " from ResTarget resTarget where status=1 and type=0 and platformId=" + userInfo.getPlatformId()+" and user.id="
					+ userInfo.getUserId();
			targetName = (List<ResTarget>) baseDao.query(hql);
			//System.out.println(targetName.size());
			if(libType.equals("2")||libType.equals("1")||libType.equals("3")){
				hql = "from ResTarget resTarget where platformId="
						+ Long.parseLong(userInfo.getPlatformId() + "")
						+ "and status=1 and type="+Long.parseLong(libType)+" and user.id="
						+ userInfo.getUserId();
			}
			if (libType.equals("120")) {
				hql = "from ResTarget resTarget where platformId=" + userInfo.getPlatformId()
						+ " and type=" + Long.parseLong(libType)
						+ " and status=1 and user.id=" + userInfo.getUserId();
			}
			useResTargets = baseDao.query(hql);
			useResTargets.addAll(targetName);
			int status = 0;
			if (useResTargets != null && useResTargets.size() > 0) {
				status = 1;
			}
			Map<String, Object> rtn = new HashMap<String, Object>();
			Set<ResTarget> hasSelectTarget = new LinkedHashSet<ResTarget>();
			Set<ResTarget> canSelectTarget = new LinkedHashSet<ResTarget>();
			int count = resIds.length;
			for (ResTarget resTarget : useResTargets) {
				int num = 0;
				for (int i = 0; i < resIds.length; i++) {
					if (libType.equals("120")) {
						hq = "from ResTargetData where platformId="
								+ userInfo.getPlatformId() + " and objectId='"
								+ resIds[i] + "' and targetId = "
								+ resTarget.getId();
					} else {
						hq = "from ResTargetData where platformId="
								+ userInfo.getPlatformId() + " and objectId='"
								+ resIds[i] + "' and targetId = "
								+ resTarget.getId() + " and module='" + module
								+ "'" + " and type='" + type + "'";
					}
					List resTargetDatas = baseDao.query(hq);
					if (resTargetDatas != null && resTargetDatas.size() > 0) {
						num++;
					}
				}
				if (count == num) {
					hasSelectTarget.add(resTarget);
				} else {
					canSelectTarget.add(resTarget);
				}
			}
			StringBuffer resultMessage = new StringBuffer();
			if (hasSelectTarget.size() > 0) {
				resultMessage.append("选中删除标签<br>");
			}
			resultMessage.append("<div style=\"border:1px solid #F60\">");
			if (hasSelectTarget.size() <= 0) {
				resultMessage.append("无<br>");
			}
			//已选标签
			int u =0;
			
			for (ResTarget target : hasSelectTarget) {
				u++;
				resultMessage
						.append("&nbsp;")
						.append("<input type=\"checkbox\" id='hascheckbox'class=\"inputnormal\" name=\"hasSelectTarget")
						.append("\" value=\"").append(target.getId())
						.append("\"/>").append(target.getName() +"&nbsp;");
				if(u%3==0){
					resultMessage
					.append("<br>");
				}
			}

			resultMessage.append("</div>");
			resultMessage.append("<br><font color=\"blue\">可选标签</font><br>");
			resultMessage.append("<div style=\"border:1px solid blue\">");
			if(canSelectTarget.size() <= 0) {
				resultMessage.append("无<br>");
			}
			
			int i =0;
			//可选标签
			for (ResTarget target : canSelectTarget) {
				i++;
				resultMessage
						.append("&nbsp;")
						.append("<input type=\"checkbox\" id='checkbox' class=\"inputnormal\" name=\"canSelectTarget")
						.append("\" value=\"").append(target.getId())
						.append("\"/>").append(target.getName()+"&nbsp;&nbsp;");
				if(i%3==0){
					resultMessage
					.append("<br>");
				}
			}
			resultMessage.append("</div>");
			rtn.put("target", resultMessage);
			rtn.put("status", status);

			return rtn;
		}

	/**
	 * 标签查询
	 */
	@Override
	public List selectTarget() throws ServiceException {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String hql = "from ResTarget resTarget where resTarget.userid.id="
				+ Long.parseLong(userInfo.getUserId() + "");
		SysOperateLogUtils.addLog("selectTarget", userInfo.getUsername(),
				userInfo);
		return baseDao.query(hql);

	}

	/**
	 * 批量删除创建
	 */
	@Override
	public void doBatchSaveDeleteTarget(String selectResId,
			String canSelectTargetIds, String hasSelectTargetIds, String resIds)
			throws ServiceException {
		UserInfo user = LoginUserUtil.getLoginUser();
		if (canSelectTargetIds != null && !canSelectTargetIds.equals("")
				&& resIds.length() < 2 && !selectResId.equals(",")) {

			String id = selectResId.substring(selectResId.indexOf(","));
			String b = id.replace(",", "").trim();
			String[] aa = canSelectTargetIds.split(",");
			for (int i = 0; i < aa.length; i++) {
				ResTargetData target = new ResTargetData();
				target.setTargetId(Long.parseLong(aa[i]));
				target.setPlatformId(Long.parseLong(user.getPlatformId() + ""));
				target.setObjectId(b);
				baseDao.create(target);
			}

		}
		if (hasSelectTargetIds != null && !hasSelectTargetIds.equals("")
				&& !selectResId.equals(",")) {
			String id = selectResId.substring(selectResId.indexOf(","));
			String b = id.replace(",", "").trim();
			String[] bb = hasSelectTargetIds.split(",");
			Map<String, Object> parameters = new HashMap<String, Object>();
			for (int j = 0; j < bb.length; j++) {
				parameters.put("targetId", Long.parseLong(bb[j]));
				String hql = "delete from ResTargetData resTarget where targetId=:targetId and resTarget.objectId='"
						+ b + "'";
				baseDao.executeUpdate(hql, parameters);

			}
		}
		if (resIds != null && !resIds.equals("")) {
			String[] aa = canSelectTargetIds.split(",");
			String resId[] = resIds.split(",");
			for (int u = 0; u < aa.length; u++) {
				for (int y = 0; y < resId.length; y++) {
					ResTargetData target = new ResTargetData();
					target.setTargetId(Long.parseLong(aa[u]));
					target.setObjectId(resId[y]);
					target.setPlatformId(Long.parseLong(user.getPlatformId()
							+ ""));
					baseDao.create(target);
				}
			}
		}
	}
/**
 * 公共资源标签查询
 */
	@Override
	public String query(String resTargetId, String libType,
			HttpServletRequest request, QueryConditionList conditionList)
			throws ServiceException {
		List<String> resIds = new LinkedList<String>();
		// String hq = parseCondition(request, conditionList);
		String page = request.getParameter("page");
		int size = conditionList.getPageSize();
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		String hq = "";
		String objectIds = "";
		String b = "";
		int count = 0;
		if (!resTargetId.equals("")) {
			String resTargetIds[] = resTargetId.split(",");
			List<String> resTargetData = new LinkedList<String>();
			List<String> resTargetSize1 = new LinkedList<String>();
			List<String> resTargetSize = new LinkedList<String>();
			hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
					+ Long.parseLong(resTargetIds[0]);
			resTargetData = (List<String>) baseDao.query(hq);
			for(int y = 0; y<resTargetIds.length-1;y++)
			{
			hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
					+ Long.parseLong(resTargetIds[y]);
			resTargetData = (List<String>) baseDao.query(hq);
			for(int o = y + 1;o<resTargetIds.length;o++)
			{
				hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
						+ Long.parseLong(resTargetIds[o]);
				resTargetSize = (List<String>) baseDao.query(hq);
				if(resTargetData.size()>resTargetSize.size()){
					resTargetSize1=resTargetData;
					resTargetData = resTargetSize;
					resTargetSize = resTargetSize1;
				}
			}
			}
			for(int j = 0;j<resTargetData.size();j++){
				b = "";
				count = 0;
				if(resTargetIds.length>1){
			for (int i = 1; i < resTargetIds.length; i++) {
				hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
						+ Long.parseLong(resTargetIds[i]);
				resIds = (List<String>) baseDao.query(hq);
				
				for(int u = 0; u<resIds.size();u++){
				if(b.equals("")){
					if(resTargetData.get(j).equals(resIds.get(u))) {
								b = resTargetData.get(j);
					}
				}
				if(!b.equals("")){
					if(b.equals(resIds.get(u))){
						count++;
					}
				}
			}
		}
		if(count==resTargetIds.length-1){
			objectIds = objectIds + b + ",";
		}
		}else{
			objectIds = objectIds + resTargetData.get(j)+",";
		}
		}
		}
		
		if (!"".equals(objectIds)) {
			objectIds = objectIds.substring(0, objectIds.length()-1);
		}
		objectIds = objectIds + "&page=" + page + "&size=" + size + "&sort="
				+ sort + "&order=" + order;
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(PUBLISH_SEARCH_RESOURCE_LIST
				+ "?queryType=2&objectIds=" + objectIds);
	}

	/**
	 * 分页信息
	 * 
	 * @param request
	 * @param conditionList
	 * @return
	 */
	public String parseCondition(HttpServletRequest request,
			QueryConditionList conditionList) {
		StringBuffer hql = new StringBuffer();
		hql.append("page=").append(request.getParameter("page"))
				.append("&size=").append(conditionList.getPageSize());
		hql.append("&sort=").append(request.getParameter("sort"))
				.append("&order=").append(request.getParameter("order"));
		// 组装查询
		if (null != conditionList) {

			List<QueryConditionItem> items = conditionList.getConditionItems();

			for (QueryConditionItem queryConditionItem : items) {
				String filedName = queryConditionItem.getFieldName();
				try {
					hql.append("&")
							.append(filedName)
							.append("=")
							.append(URLEncoder.encode(
									queryConditionItem.getValue() + "", "utf-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage());
				}
			}

		}
		return hql.toString();
	}

	/**
	 * 左侧原始main标签信息查询
	 */
	@Override
	public String bresQuery(String resTargetId, String libType,
			HttpServletRequest request, QueryConditionList conditionList,
			String module, String type) throws ServiceException {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		int total = 0;
		List<String> resIds = new LinkedList<String>();
		resTargetId = resTargetId.trim();
		// String hq = parseCondition(request, conditionList);
		String page = request.getParameter("page");
		int size = conditionList.getPageSize();
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		String hq = "";
		String objectIds = "";
		String b = "";
		int count = 0;
		if (!resTargetId.equals("")) {
			String resTargetIds[] = resTargetId.split(",");
			List<String> resTargetData = new LinkedList<String>();
			List<String> resTargetSize1 = new LinkedList<String>();
			List<String> resTargetSize = new LinkedList<String>();
			// 判断是否为不同标签选择的同一数据，如果相同则记录传递过去的资源ids
			hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
					+ Long.parseLong(resTargetIds[0])
					+ " and restarget.libType="
					+ Long.parseLong(libType)
					+ " and type='" + type + "' and module='" + module + "' and platformId="+userInfo.getPlatformId();
			resTargetData = (List<String>) baseDao.query(hq);
			for(int y = 0; y<resTargetIds.length-1;y++)
			{
			hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
					+ Long.parseLong(resTargetIds[y]);
			resTargetData = (List<String>) baseDao.query(hq);
			//首先排序找到最小的资源集合。
			for(int o = y + 1;o<resTargetIds.length;o++)
			{
				hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
						+ Long.parseLong(resTargetIds[o]);
				resTargetSize = (List<String>) baseDao.query(hq);
				if(resTargetData.size()>resTargetSize.size()){
					resTargetSize1=resTargetData;
					resTargetData = resTargetSize;
					resTargetSize = resTargetSize1;
				}
			}
			}
			for(int j = 0;j<resTargetData.size();j++){
				b = "";
				count = 0;
				//判断是不是只选了一个标签。如果是则不走内部
				if(resTargetIds.length>1){
			//第二次判断资源ids
			for (int i = 1; i < resTargetIds.length; i++) {
				hq = "select distinct(restarget.objectId) from ResTargetData restarget where targetId="
						+ Long.parseLong(resTargetIds[i])
						+ " and restarget.libType="
						+ Long.parseLong(libType)
						+ " and type='"
						+ type
						+ "' and module='"
						+ module
						+ "' and platformId="+userInfo.getPlatformId();
				resIds = (List<String>) baseDao.query(hq);
				//用第一个资源标签跟每个资源进行比较，排序记录第一个标签。
				for(int u = 0; u<resIds.size();u++){
					if(b.equals("")){
					if(resTargetData.get(j).equals(resIds.get(u))) {
							b = resTargetData.get(j);
					}
					}
					if(!b.equals("")){
						if(b.equals(resIds.get(u))){
							count++;
						}
					}
				}
			}
			if(count==resTargetIds.length-1){
				objectIds = objectIds + b + ",";
			}
			}else{
				objectIds = objectIds + resTargetData.get(j)+",";
			}
			
		}
			//分页信息统计将分页信息放入query里以便返回显示相应的分页结果.
			total = resIds.size();
			Session session = getSession();
			Query query = session.createQuery(hq);
			query.setMaxResults(size);
			int startIndex = (Integer.parseInt(page) - 1) * size;
			query.setFirstResult(startIndex);
		}
		//截取末尾逗号(这里可以不用)
		if (!"".equals(objectIds)) {
			objectIds = objectIds.substring(0, objectIds.length() - 1);
		}
		objectIds = objectIds + "&page=" + page + "&size=" + size + "&total="
				+ total;
		HttpClientUtil http = new HttpClientUtil();
		//通过远程接口TARGET_QUERY_URL地址查找方法查询资源信息
		return http.executeGet(TARGET_QUERY_URL + "?objectIds=" + objectIds);
	}

	/**
	 * 获取session工厂
	 */
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	public Session getSession() {
		// 事务必须是开启的(Required)，否则获取不到

		return sessionFactory.getCurrentSession();
	}

	/**
	 * 批量教育资源标签增删改
	 */
	@Override
	public void doBatchTeaTarget(String selectResId, String canSelectTargetIds,
			String hasSelectTargetIds, String resIds, String libType,
			String module, String type) throws ServiceException {

		UserInfo user = LoginUserUtil.getLoginUser();
		if (canSelectTargetIds != null && !canSelectTargetIds.equals("")
				&& resIds.equals("")) {
			UserInfo userSaveTarget = LoginUserUtil.getLoginUser();
			String[] aa = canSelectTargetIds.split(",");
			List<String> targetName = new LinkedList<String>();
			Asset res = null;
			Ca ca = null;
			String hq = "";
			HttpClientUtil http = new HttpClientUtil();
			Gson gson = new Gson();
			for (int i = 0; i < aa.length; i++) {
				ResTargetData target = new ResTargetData();
				target.setTargetId(Long.parseLong(aa[i]));
				target.setPlatformId(Long.parseLong(user.getPlatformId() + ""));
				target.setObjectId(selectResId);
				target.setLibType(Long.parseLong(libType));
				target.setModule(module);
				target.setType(type);
				baseDao.create(target);
				hq = "select type from ResTarget resTarget where targetId="
						+ Long.parseLong(aa[i] + "");
				targetName = (List<String>) baseDao.query(hq);
				//判断是否是通用标签
				if (targetName.hashCode() == 31) {
					hq = "select name from ResTarget resTarget where targetId="
							+ Long.parseLong(aa[i] + "");
					targetName = (List<String>) baseDao.query(hq);
				} else {
					String hql = "select name from ResTarget resTarget where targetId="
							+ Long.parseLong(aa[i] + "")
							+ " and resTarget.type="
							+ Long.parseLong(libType)
							+ " and status=1 and user.id="
							+ userSaveTarget.getUserId();
					targetName = (List<String>) baseDao.query(hql);
				}
				// 引入接口查询根据资源Id
				if (libType.equals("2") || libType.equals("1")) {
					if(selectResId.indexOf("book")>0){
						ca = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + selectResId), Ca.class);
					}else{
					res = baseSemanticSerivce.getResourceById(selectResId);
					}
				} else if (libType.equals("3")) {
					String resourceDetail = http.executeGet(WebappConfigUtil
							.getParameter("CA_DETAIL_URL")
							+ "?id="
							+ selectResId);
					ca = gson.fromJson(resourceDetail, Ca.class);
				} else {
					String resourceDetail = http.executeGet(WebappConfigUtil
							.getParameter("PUBLISH_DETAIL_URL")
							+ "?id="
							+ selectResId);
					ca = gson.fromJson(resourceDetail, Ca.class);
				}

				if (libType.equals("2")) {
					if(selectResId.indexOf("book")>0){
						SysOperateLogUtils.addLog("target_bookRes", ca
								.getMetadataMap().get("title")
								+ ":添加标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					}else{
					SysOperateLogUtils.addLog("target_resSave", res
							.getMetadataMap().get("title")
							+ ":添加标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
					}
				} else if (libType.equals("1")) {
					if(selectResId.indexOf("book")>0){
						SysOperateLogUtils.addLog("target_bookStand", ca
								.getMetadataMap().get("title")
								+ ":添加标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					}else{
					SysOperateLogUtils.addLog("target_standSave", res
							.getMetadataMap().get("title")
							+ ":添加标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
					}
				} else if (libType.equals("3")) {
					// SysOperateLogUtils.addLog(CA_CREATE,
					// ca.getCommonMetaData().getTitle(), user);
					SysOperateLogUtils.addLog("target_coloSave", ca
							.getMetadataMap().get("title")
							+ ":添加标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
				} else {
					SysOperateLogUtils.addLog("target_publicSave", ca
							.getMetadataMap().get("title")
							+ ":添加标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
				}
			}
		}

		if (hasSelectTargetIds != null && !hasSelectTargetIds.equals("")
				&& !selectResId.equals("") && resIds.equals("")) {
			String[] bb = hasSelectTargetIds.split(",");
			UserInfo userSaveTarget = LoginUserUtil.getLoginUser();
			Map<String, Object> parameters = new HashMap<String, Object>();
			List<String> targetName = new LinkedList<String>();
			Asset res = null;
			Ca ca = null;
			String hq = "";
			HttpClientUtil http = new HttpClientUtil();
			Gson gson = new Gson();
			for (int j = 0; j < bb.length; j++) {
				parameters.put("targetId", Long.parseLong(bb[j]));
				String hql = "delete from ResTargetData resTarget where targetId=:targetId and resTarget.objectId='"
						+ selectResId + "'";
				baseDao.executeUpdate(hql, parameters);
				hq = "select type from ResTarget resTarget where targetId="
						+ Long.parseLong(bb[j] + "");
				targetName = (List<String>) baseDao.query(hq);
				if (targetName.hashCode() == 31) {
					hq = "select name from ResTarget resTarget where targetId="
							+ Long.parseLong(bb[j] + "");
					targetName = (List<String>) baseDao.query(hq);
				} else {

					hq = "select name from ResTarget resTarget where targetId="
							+ Long.parseLong(bb[j] + "")
							+ " and resTarget.type=" + Long.parseLong(libType)
							+ " and status=1 and user.id="
							+ userSaveTarget.getUserId();
					targetName = (List<String>) baseDao.query(hq);
				}
				
				if (libType.equals("2") || libType.equals("1")) {
					if(selectResId.indexOf("book")>0){
						ca = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + selectResId), Ca.class);
					}else{
					res = baseSemanticSerivce.getResourceById(selectResId);
					}
				} else if (libType.equals("3")) {
					String resourceDetail = http.executeGet(WebappConfigUtil
							.getParameter("CA_DETAIL_URL")
							+ "?id="
							+ selectResId);
					ca = gson.fromJson(resourceDetail, Ca.class);
				} else {
					String resourceDetail = http.executeGet(WebappConfigUtil
							.getParameter("PUBLISH_DETAIL_URL")
							+ "?id="
							+ selectResId);
					ca = gson.fromJson(resourceDetail, Ca.class);
				}
				if (libType.equals("2")) {
					if(selectResId.indexOf("book")>0){
						SysOperateLogUtils.addLog("target_bookResele", ca
								.getMetadataMap().get("title")
								+ ":删除标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					}else{
					SysOperateLogUtils.addLog("target_resDele", res
							.getMetadataMap().get("title")
							+ ":删除标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
					}
				} else if (libType.equals("1")) {
					if(selectResId.indexOf("book")>0){
						SysOperateLogUtils.addLog("target_bookStandele", ca
								.getMetadataMap().get("title")
								+ ":删除标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					}else{
					SysOperateLogUtils.addLog("target_standDele", res
							.getMetadataMap().get("title")
							+ ":删除标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
					}
				} else if (libType.equals("3")) {
					// //SysOperateLogUtils.addLog(CA_CREATE,
					// ca.getCommonMetaData().getTitle(), user);
					SysOperateLogUtils.addLog("target_coloDele", ca
							.getMetadataMap().get("title")
							+ ":删除标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
				} else {
					SysOperateLogUtils.addLog("target_publicDele", ca
							.getMetadataMap().get("title")
							+ ":删除标签为:"
							+ targetName, LoginUserUtil.getLoginUser());
				}
			}
		}

		// 批量删除标签
		if (hasSelectTargetIds != null && !hasSelectTargetIds.equals("")
				&& !selectResId.equals("") && !resIds.equals("")) {
			String[] bb = hasSelectTargetIds.split(",");
			String resId[] = resIds.split(",");
			Map<String, Object> parameters = new HashMap<String, Object>();
			UserInfo userSaveTarget = LoginUserUtil.getLoginUser();
			List<String> targetName = new LinkedList<String>();
			Asset res = null;
			Ca ca = null;
			String hq = "";
			HttpClientUtil http = new HttpClientUtil();
			Gson gson = new Gson();
			for (int i = 0; i < resId.length; i++) {
				for (int j = 0; j < bb.length; j++) {
					parameters.put("targetId", Long.parseLong(bb[j]));
					String hql = "delete from ResTargetData resTarget where targetId=:targetId and libType="
							+ Long.parseLong(libType)
							+ " and module='"
							+ module
							+ "' and type='"
							+ type
							+ "' and objectId='" + resId[i] + "'";
					baseDao.executeUpdate(hql, parameters);
					hq = "select type from ResTarget resTarget where targetId="
							+ Long.parseLong(bb[j] + "");
					targetName = (List<String>) baseDao.query(hq);
					if (targetName.hashCode() == 31) {
						hq = "select name from ResTarget resTarget where targetId="
								+ Long.parseLong(bb[j] + "");
						targetName = (List<String>) baseDao.query(hq);
					} else {

						hq = "select name from ResTarget resTarget where targetId="
								+ Long.parseLong(bb[j] + "")
								+ " and resTarget.type="
								+ Long.parseLong(libType)
								+ " and status=1 and user.id="
								+ userSaveTarget.getUserId();
						targetName = (List<String>) baseDao.query(hq);
					}
					if (libType.equals("2") || libType.equals("1")) {
						if(selectResId.indexOf("book")>0){
							ca = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + resId[i]), Ca.class);
						}else{
							res = baseSemanticSerivce.getResourceById(resId[i]);
						}
					} else if (libType.equals("3")) {
						String resourceDetail = http
								.executeGet(WebappConfigUtil
										.getParameter("CA_DETAIL_URL")
										+ "?id="
										+ resId[i]);
						ca = gson.fromJson(resourceDetail, Ca.class);
					} else {
						String resourceDetail = http
								.executeGet(WebappConfigUtil
										.getParameter("PUBLISH_DETAIL_URL")
										+ "?id=" + resId[i]);
						ca = gson.fromJson(resourceDetail, Ca.class);
					}
					if (libType.equals("2")) {
						if(selectResId.indexOf("book")>0){
							SysOperateLogUtils.addLog("target_bookResele", ca
									.getMetadataMap().get("title")
									+ ":批量删除标签为:"
									+ targetName, LoginUserUtil.getLoginUser());
						}else{
						SysOperateLogUtils.addLog("target_resDele", res
								.getMetadataMap().get("title")
								+ ":批量删除标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
						}
					} else if (libType.equals("1")) {
						if(selectResId.indexOf("book")>0){
							SysOperateLogUtils.addLog("target_bookStandele", ca
									.getMetadataMap().get("title")
									+ ":批量删除标签为:"
									+ targetName, LoginUserUtil.getLoginUser());
						}else{
						SysOperateLogUtils.addLog("target_standDele", res
								.getMetadataMap().get("title")
								+ ":批量删除标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
						}
					} else if (libType.equals("3")) {
						SysOperateLogUtils.addLog("target_coloDele", ca
								.getMetadataMap().get("title")
								+ ":批量删除标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					} else {
						SysOperateLogUtils.addLog("target_publicDele", ca
								.getMetadataMap().get("title")
								+ ":批量删除标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					}

				}
			}
		}
		if (resIds != null && !resIds.equals("") && !selectResId.equals("")
				&& hasSelectTargetIds.equals("")) {
			String[] aa = canSelectTargetIds.split(",");
			String resId[] = resIds.split(",");
			UserInfo userSaveTarget = LoginUserUtil.getLoginUser();
			List<String> targetName = new LinkedList<String>();
			Asset res = null;
			Ca ca = null;
			String hq = "";
			HttpClientUtil http = new HttpClientUtil();
			Gson gson = new Gson();
			for (int u = 0; u < aa.length; u++) {
				for (int y = 0; y < resId.length; y++) {
					ResTargetData target = new ResTargetData();
					target.setTargetId(Long.parseLong(aa[u]));
					target.setObjectId(resId[y]);
					target.setPlatformId(Long.parseLong(user.getPlatformId()
							+ ""));
					target.setLibType(Long.parseLong(libType));
					target.setModule(module);
					target.setType(type);
					baseDao.create(target);
					hq = "select type from ResTarget resTarget where targetId="
							+ Long.parseLong(aa[u] + "");
					targetName = (List<String>) baseDao.query(hq);
					if (targetName.hashCode() == 31) {
						hq = "select name from ResTarget resTarget where targetId="
								+ Long.parseLong(aa[u] + "");
						targetName = (List<String>) baseDao.query(hq);
					} else {
						hq = "select name from ResTarget resTarget where targetId="
								+ Long.parseLong(aa[u] + "")
								+ " and resTarget.type="
								+ Long.parseLong(libType)
								+ " and status=1 and user.id="
								+ userSaveTarget.getUserId();
						targetName = (List<String>) baseDao.query(hq);
					}
					if (libType.equals("2") || libType.equals("1")) {
						if(selectResId.indexOf("book")>0){
							ca = gson.fromJson(http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + resId[y]), Ca.class);
						}else{
							res = baseSemanticSerivce.getResourceById(resId[y]);
						}
					} else if (libType.equals("3")) {
						String resourceDetail = http
								.executeGet(WebappConfigUtil
										.getParameter("CA_DETAIL_URL")
										+ "?id="
										+ resId[y]);
						ca = gson.fromJson(resourceDetail, Ca.class);
					} else {
						String resourceDetail = http
								.executeGet(WebappConfigUtil
										.getParameter("PUBLISH_DETAIL_URL")
										+ "?id=" + resId[y]);
						ca = gson.fromJson(resourceDetail, Ca.class);
					}
					if (libType.equals("2")) {
						if(selectResId.indexOf("book")>0){
							SysOperateLogUtils.addLog("target_bookRes", ca
									.getMetadataMap().get("title")
									+ ":批量创建标签为:"
									+ targetName, LoginUserUtil.getLoginUser());
						}else{
						SysOperateLogUtils.addLog("target_resSave", res
								.getMetadataMap().get("title")
								+ ":批量创建标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
						}
					} else if (libType.equals("1")) {
						if(selectResId.indexOf("book")>0){
							SysOperateLogUtils.addLog("target_bookStand", ca
									.getMetadataMap().get("title")
									+ ":批量创建标签为:"
									+ targetName, LoginUserUtil.getLoginUser());
						}else{
						SysOperateLogUtils.addLog("target_standSave", res
								.getMetadataMap().get("title")
								+ ":批量创建标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
						}
					} else if (libType.equals("3")) {
						SysOperateLogUtils.addLog("target_coloSave", ca
								.getMetadataMap().get("title")
								+ ":批量创建标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					} else {
						SysOperateLogUtils.addLog("target_publicSave", ca
								.getMetadataMap().get("title")
								+ ":批量创建标签为:"
								+ targetName, LoginUserUtil.getLoginUser());
					}
				}
			}
		}

	}
/**
 * 标签查询
 */
	@Override
	public List getAllTargets(String libType) throws ServiceException {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String hql = "";
		List<ResTarget> targetName = new LinkedList<ResTarget>();
		hql = " from ResTarget resTarget where status=1 and type=0 and platformId=" + userInfo.getPlatformId()+" and resTarget.user.id="+userInfo.getUserId();
		targetName = (List<ResTarget>) baseDao.query(hql);
		//System.out.println(targetName.size());
		if(libType.equals("2")||libType.equals("1")||libType.equals("3")){
			hql = "from ResTarget resTarget where platformId="
					+ Long.parseLong(userInfo.getPlatformId() + "")
					+ "and status=1 and type="+Long.parseLong(libType)+" and resTarget.user.id="+userInfo.getUserId();
		}
		if (libType.equals("120")) {
			hql = "from ResTarget  where platformId="
					+ Long.parseLong(userInfo.getPlatformId() + "")
					+ " and type=" + Long.parseLong(libType)
					+ " and status=1 and user.id="+userInfo.getUserId();
		}
		targetName.addAll(baseDao.query(hql));
		return targetName;
	}

	/**
	 * 删除该标签是否有连带的资源
	 */
	@Override
	public Map<String, Object> deleteYes(String id,String targetField,String targetName,String publishType) throws ServiceException {
		List<ResTargetData> resTargetData = new LinkedList<ResTargetData>();
		Map<String, Object> ret = new HashMap<String, Object>();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String ids[] = id.split(",");
		int status = 0;
		String hql = "";
		for (int i = 0; i < ids.length; i++) {
			String metadataMap ="{\""+targetField+"\":\""+targetName+"\"}";
			try {
				metadataMap = URLEncoder.encode(metadataMap, "UTF-8");
				hql = hql+"&metadataMap="+metadataMap;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpClientUtil http = new HttpClientUtil();
			String result = http.executeGet(PUBLISH_QUERY_URL
					+ "?publishType="+publishType+hql.toString());
			 Gson gson=new Gson();
			 SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
			 if(caList.getTotal()!=0){
				 status = 1;
			 }
		}
			ret.put("status", status);
			
		return ret;
	}
	/**
	 * 标签删除
	 */
	@Override
	public void deleteAll(Object target, String ids) throws ServiceException {
		// TODO Auto-generated method stub
		String targetIds[] = ids.split(",");
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		for (int i = 0; i < targetIds.length; i++) {
			ResTargetData targets = (ResTargetData)getByPk(ResTargetData.class, Long.parseLong(targetIds[i]));
			baseDao.delete(ResTargetData.class, targetIds[i]);
			parameters.put("targetId", Long.parseLong(targetIds[i]));
			String hql = "delete from ResTargetData  where targetId=:targetId";
			baseDao.executeUpdate(hql, parameters);
				SysOperateLogUtils.addLog("target_publishResDele",  targets.getTargetName(), LoginUserUtil.getLoginUser());
		}
	}
/**
 * 检查标签重复
 */
	@Override
	public List checkRepeat(String name,String module) throws ServiceException {
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String hql = "FROM ResTargetData  WHERE targetName='"+name+"' and module='"+module+"' and pid not in('-1')";
		List<ResTargetData> rs = baseDao.query(hql);
		return rs;
	}

@Override
public String getAllMainTargets(String module,String targetField) throws ServiceException {
	UserInfo userInfo = LoginUserUtil.getLoginUser();
	Map<String,String> rtn = new HashMap<String, String>();
	JSONArray ob1 = new JSONArray();
	JSONObject ob2 = new JSONObject();
	String hql = "";
	if(StringUtils.isNotBlank(targetField)){
		hql = "FROM ResTargetData  WHERE module="+module+" and status=1 and userId="+userInfo.getUserId()+" ORDER BY targetNum DESC";
		List<ResTargetData> list = baseDao.query(hql,0,20);
		String name = "";
		for(int i=0;i<list.size();i++){
			Long num= 0L;
			if(list.get(i).getTargetName()!=null){
				name = list.get(i).getTargetName();
				if(list.get(i).getTargetNum()!=null){
					num= list.get(i).getTargetNum();
				}
				ob2.put("name", name);
				ob2.put("num", num.toString());
			}
				ob1.add(ob2);
			}
	}
	return ob1.toString();
	
}
@Override
public String getTargetsForName(String name,String publishType,String flag) throws ServiceException {
	UserInfo userInfo = LoginUserUtil.getLoginUser();
	JSONArray ob1 = new JSONArray();
	JSONObject ob2 = new JSONObject();
	String hql = "";
	if(name.equals("")){
	 hql = "from ResTargetData where module='"+publishType+"' and status=1 and userId="+userInfo.getUserId()+" order by targetNum desc";
	}else{
	 hql = "from ResTargetData  where targetName like '%"+name+"%' and module='"+publishType+"' and status=1 and userId="+userInfo.getUserId()+" order by targetNum desc";
	}
	List<ResTargetData> list = baseDao.query(hql,0,20);
	for(int i=0;i<list.size();i++){
		if(StringUtils.isNotBlank(list.get(i).getTargetName())){
			Long num= 0L;
			if(list.get(i).getTargetName()!=null){
				name = list.get(i).getTargetName();
				if(list.get(i).getTargetNum()!=null){
					num= list.get(i).getTargetNum();
				}
				ob2.put("name", name);
				ob2.put("num", num.toString());
			}
				ob1.add(ob2);
		}
	}
	return ob1.toString();
}
/**
 * List资源标签查询
 */
	@Override
	public String queryList(String targetNames, String status,
			String publishType,HttpServletRequest request, QueryConditionList conditionList)
			throws ServiceException {
		List<String> resIds = new LinkedList<String>();
		// String hq = parseCondition(request, conditionList);
		String targetValue[] = targetNames.split(",");
		String page = request.getParameter("page");
		int size = conditionList.getPageSize();
		String sort = request.getParameter("sort");
		String order = request.getParameter("order");
		String hql = "";
		String names = "";
		if(StringUtils.isNotBlank(status)){
			hql = "&page=" + page + "&size=" + size + "&sort="
					+ sort + "&order=" + order+"&status="+status;
		}else{
			hql = "&page=" + page + "&size=" + size + "&sort="
				+ sort + "&order=" + order;
		}
		for(int i=1;i<targetValue.length;i++){
			names = names+targetValue[i]+",";
		}
		if(names.equals("")){
			
		}else{
			names = names.substring(0,names.length()-1);
			String metadataMap ="{\""+targetValue[0]+"\":\""+names+"\"}";
			try {
				metadataMap = URLEncoder.encode(metadataMap, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hql = hql+"&metadataMap="+metadataMap;
		}
		int isPrivate = LoginUserUtil.getLoginUser().getIsPrivate();
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		if (isPrivate == 1) {
			if(StringUtils.isNotBlank(userIds)){
			}else{
				userIds = LoginUserUtil.getLoginUser().getUserId()+"";
			}
		}
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			hql = hql+"&creator=" + userIds;
		}else{
			hql = hql+"&creator=-2";
		}
		HttpClientUtil http = new HttpClientUtil();
		return http.executeGet(PUBLISH_QUERY_URL
				+ "?publishType="+publishType+hql.toString());
	}

	@Override
	public String queryTargetJson(String publishType,String targetName,String targetType) throws ServiceException {
		JSONArray array = new JSONArray();
		JSONArray arrayOpen = new JSONArray();
		JSONArray arrayCom = new JSONArray();
		boolean flag = false;
		boolean typeFlag = false;
		boolean oparray = false;
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String userIds = userInfo.getDeptUserIds();
		if(userIds.endsWith(",")){
			userIds = userIds.substring(0,userIds.length()-1);
		}
		List<ResTargetData> targetList = null;
		String pid = "";
		int u =0;
		//设置
		int comNum = 0;
		String comPid = "";
		String comType = "";
		//记录有分类的标签id
		String pid1 = "";
		//记录所有的标签id
		String allId = "";
		//记录通用标签id
		String comid = "";
		//设置被选中的id
		String xId = "";
		if(targetType!=null && targetType.equals("通用标签")){
			targetType = "";
			comType = "1";
		}
		logger.info("--------------------------comType------------------------------"+comType);
		List<DictName> nameList = new ArrayList<DictName>();
		List<DictValue> nameValue = new ArrayList<DictValue>();
		if(StringUtils.isBlank(targetType)){
			targetList = baseDao.query("from ResTargetData where module="+publishType+" and userId in ("+userIds+") and targetStatus in (1) order by targetNum DESC");
			targetType = "";
			//通用标签查询，先查询所有含有通用标签的pid1记录下来
//			for(ResTargetData t1 : targetList){
//				if(t1.getStatus()==null && !t1.getTargetName().equals("")){
//					String hql = "";
//					hql = "from DictName where indexTag='targetType' and status=1";
//					nameList = baseDao.query(hql);
//					hql = "select name from DictValue where pid="+nameList.get(0).getId()+""+" and indexTag='"+t1.getTargetName()+"'";
//					nameValue = baseDao.query(hql);
//					if(nameValue!=null && !nameValue.isEmpty()){
//						if(comType.equals("1")){
//							pid1 = pid1 + t1.getId();
//							logger.info("--------------------------pid1------------------------------"+pid1);
//							continue;
//						}
//					}
//				}
//			}
			//不是按标签查询时targetName为空 全部查询
			if(StringUtils.isBlank(targetName)){
				for(ResTargetData t : targetList){
					JSONObject json = new JSONObject();
					json.put("id", t.getId());
					json.put("pid", t.getPid());
					//查询父节点分类是否为空
					if(t.getPid().equals("-1")){
						String hql = "";
						hql = "from DictName where indexTag='targetType' and status=1";
						nameList = baseDao.query(hql);
						//查询数据字典value是否存在该分类
						if(nameList!=null && !nameList.isEmpty()){
							hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetType()+"'";
							nameValue = baseDao.query(hql);
						}
						//分类为空 否则 不为空如果存在记录该标签的id
						if(nameValue!=null && !nameValue.isEmpty()){
							//不为空记录标签id有分类
								pid1 = pid1 + t.getId().toString()+",";
								logger.info("--------------------------pid1------------------------------"+pid1);
//								continue;
							json.put("name", nameValue.get(0));
						}else{
							//没有分类的标签
								if(comNum==0){
								//通用标签id;
									comid = t.getId().toString();
									comNum++;
								}
								//如果没有分类的标签id都是通用标签切循环一次生成 “通用标签”
								if(comNum==1){
									json.put("id", comid);
									json.put("name", "通用标签");
									comNum++;
								}else if(!t.getPid().equals("-1")){
									json.put("name", t.getTargetName());
									json.put("xpath", "通用标签");
								}else{
									continue;
								}
//							json.put("name", t.getTargetName());
							//没有分类，刚重新循环
						}
						//不是-1标签，不是分类的标签
					}else if(t.getTargetNum()!=null){
						String hql = "";
						hql = "from DictName where indexTag='targetType' and status=1";
						nameList = baseDao.query(hql);
						//查询数据字典value是否存在该分类
						if(nameList!=null && !nameList.isEmpty()){
							hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetType()+"'";
							nameValue = baseDao.query(hql);
						}
						if(nameValue.isEmpty()){
							json.put("xpath", "通用标签");
						}
						//记录所有标签的id
						allId = allId + t.getId().toString()+",";
						json.put("name", t.getTargetName()+"("+t.getTargetNum()+")");
					}else{
						String hql = "";
						hql = "from DictName where indexTag='targetType' and status=1";
						nameList = baseDao.query(hql);
						//查询数据字典value是否存在该分类
						if(nameList!=null && !nameList.isEmpty()){
							hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetType()+"'";
							nameValue = baseDao.query(hql);
						}
						if(nameValue.isEmpty()){
							json.put("xpath", "通用标签");
						}
						allId = allId + t.getId().toString()+",";
						json.put("name", t.getTargetName());
					}
// 					if(pid1.indexOf(t.getPid())!=-1){
// 						continue;
// 					}
 					logger.info("--------------------------添加json------------------------------"+pid1);
					array.add(json);
					typeFlag = true;
				}
			}else{
				for(ResTargetData t : targetList){
					JSONObject json = new JSONObject();
					json.put("id", t.getId());
					json.put("pid", t.getPid());
					if(t.getPid().equals("-1")){
						String hql = "";
						hql = "from DictName where indexTag='targetType' and status=1";
						nameList = baseDao.query(hql);
						if(nameList!=null && !nameList.isEmpty()){
							hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetType()+"'";
							nameValue = baseDao.query(hql);
						}
						if(nameValue!=null && !nameValue.isEmpty()){
							//不为空记录标签id有分类
								pid1 = pid1 + t.getId().toString()+",";
								logger.info("--------------------------pid1------------------------------"+pid1);
//								continue;
							json.put("name", nameValue.get(0));
						}else{
							//没有分类的标签
								if(comNum==0){
								//通用标签id;
									comid = t.getId().toString();
									comNum++;
								}
								//如果没有分类的标签id都是通用标签切循环一次生成 “通用标签”
								if(comNum==1){
									json.put("id", comid);
									json.put("name", "通用标签");
									comNum++;
								}else if(!t.getPid().equals("-1")){
									json.put("name", t.getTargetName());
									json.put("xpath", "通用标签");
								}else{
									continue;
								}
//							json.put("name", t.getTargetName());
							//没有分类，刚重新循环
						}
						//不是-1标签，不是分类的标签
					}else if(t.getTargetNum()!=null){
						String hql = "";
						hql = "from DictName where indexTag='targetType' and status=1";
						nameList = baseDao.query(hql);
						//查询数据字典value是否存在该分类
						if(nameList!=null && !nameList.isEmpty()){
							hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetType()+"'";
							nameValue = baseDao.query(hql);
						}
						if(nameValue.isEmpty()){
							json.put("xpath", "通用标签");
						}
						//记录所有标签的id
						allId = allId + t.getId().toString()+",";
						json.put("name", t.getTargetName()+"("+t.getTargetNum()+")");
					}else{
						String hql = "";
						hql = "from DictName where indexTag='targetType' and status=1";
						nameList = baseDao.query(hql);
						//查询数据字典value是否存在该分类
						hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetType()+"'";
						nameValue = baseDao.query(hql);
						if(nameValue.isEmpty()){
							json.put("xpath", "通用标签");
						}
						allId = allId + t.getId().toString()+",";
						json.put("name", t.getTargetName());
					}
						logger.info("--------------------------json------------------------------"+json.toString());
//						json.put("xpath", t.getXpath());
						if(t.getTargetName().contains(targetName)){
							json.put("checked", "true");
							if(json.get("xpath")!=null && json.get("xpath").toString().contains("通用标签")){
								json.put("xpath", targetName+"通用标签");
							}else{
								json.put("xpath", targetName);
							}
							flag = true;
						}
					array.add(json);
				}
			}
			if(flag){
				boolean conti = false;
				for (Object object : array) {
					String xpath = "";
					  JSONObject o = JSONObject.fromObject(object);
					  if(o.get("xpath")!=null){
						  xpath = o.get("name").toString();
						  String xpArry[] = xpath.split(",");
							  for(int i=0;i<xpArry.length;i++){
								  if(xpArry[i].contains(targetName)){
									  xId = o.get("pid").toString();
									  conti = true;
									  break;
								  }
							  }
							  if(conti){
								  break;
							  }
					  	}
					}
				//设置父节点选中，当选择标签查询时
				for (Object object : array) {
					JSONObject o = JSONObject.fromObject(object);
					if(o.get("id").toString().equals(xId)){
						o.put("open", "true");
					}
					if(pid.contains(o.get("pid")+"")){
						o.put("pid", comPid);
					}
					arrayOpen.add(o);
					oparray = true;
				}
				
			}else if(!typeFlag){
				array.clear();
			}
		}else{
			//分类查询
			targetList = baseDao.query("from ResTargetData where module="+publishType+" and targetType='"+targetType+"' and userId in ("+userIds+")  order by targetNum DESC");
			for(ResTargetData t : targetList){
				JSONObject json = new JSONObject();
				json.put("id", t.getId());
				json.put("pid", t.getPid());
				String hql = "";
				hql = "from DictName where indexTag='targetType' and status=1";
				nameList = baseDao.query(hql);
				if(nameList!=null && !nameList.isEmpty()){
					hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetName()+"'";
					nameValue = baseDao.query(hql);
				}
				if(nameValue!=null && !nameValue.isEmpty()){
					json.put("name", nameValue.get(0));
				}else if(t.getTargetNum()!=null){
					json.put("name", t.getTargetName()+"("+t.getTargetNum()+")");
				}else{
					json.put("name", t.getTargetName());
				}
		//		json.put("xpath", t.getXpath());
				array.add(json);
			}
		}
		//退出循环 执行以上arrary信息
		int n = 0;
		logger.info("--------------------------array------------------------------"+array.toString());
		for(ResTargetData t : targetList){
			int num = 0;
			JSONObject json = new JSONObject();
			json.put("id", t.getId());
//			if(t.getPid().equals(pid)){
//				json.put("pid", comPid);
//			}else{
				json.put("pid", t.getPid());
//			}
			if(t.getStatus()==null && !t.getTargetName().equals("")){
				String hql = "";
				hql = "from DictName where indexTag='targetType' and status=1";
				nameList = baseDao.query(hql);
				if(nameList!=null && !nameList.isEmpty()){
					hql = "select name from DictValue where pid="+nameList.get(0).getId()+" and indexTag='"+t.getTargetName()+"'";
					nameValue = baseDao.query(hql);
				}
				if(nameValue!=null && !nameValue.isEmpty()){
					json.put("name", nameValue.get(0));
				}else{
					json.put("name", t.getTargetName());
				}
			}
			for(ResTargetData t1 : targetList){
				if(t.getId()==Long.parseLong(t1.getPid())){
					num++;
				}
			}
			if(num<1){
				array.remove(json);
			}
			n++;
		}
		boolean falg2 = false;
		//真正标签分类
		if(comType.equals("")){
		if(StringUtils.isNotBlank(allId) && StringUtils.isBlank(pid1)){
			falg2 = true;
			for (Object object : array) {
				JSONObject o = JSONObject.fromObject(object);
				if(!o.get("pid").equals("-1")){
					o.put("pid", comid);
				}
				arrayCom.add(o);
			}
			
		}else if(StringUtils.isNotBlank(allId) && StringUtils.isNotBlank(pid1)){
			falg2 = true;
			int numCom = 0;
			JSONObject oCom = new JSONObject();
			String pidArray[] = pid1.split(",");
			for (Object object : array) {
				boolean tar = false;
				JSONObject o = JSONObject.fromObject(object);
				//记录通用标签使用次数,如果只有一个,则删除
					if(o.get("xpath")!=null && o.get("xpath").toString().equals("通用标签")){
						oCom.put("id", o.get("id"));
						oCom.put("pid", o.get("pid"));
						oCom.put("name", o.get("name"));
						numCom ++;
					}
					for(int j=0;j<pidArray.length;j++){
						//判断标签的id是标签id并且它的pid = pid1中的值 则为真;不做处理说明有该分类
						String pId = pidArray[j].toString();
						if(o.get("pid").toString().equals(pId)){
							tar = true;
							break;
						}
					}
					//否则没有分类，设置空通用标签;
					if(!tar && o.get("xpath")!=null && o.get("xpath").toString().equals("通用标签") && StringUtils.isNotBlank(comid) && !o.get("name").toString().equals("通用标签")){
						o.put("pid", comid);
					}
				arrayCom.add(o);
			}
			
			if(numCom==1){
				arrayCom.remove(oCom);
			}
		}
		if(StringUtils.isBlank(comType)){
			flag = true;
			arrayOpen.clear();
			for (Object object : array) {
				JSONObject o = JSONObject.fromObject(object);
				if(o.get("xpath")!=null && o.get("xpath").toString().contains("通用标签") || o.get("name").toString().contains("通用标签")){
					if(!o.get("name").toString().equals("通用标签") && o.get("xpath").toString().contains("通用标签")){
						o.put("pid", comid);
					}
					arrayOpen.add(o);
				}else{
					arrayOpen.add(o);
				}
			}
		}
		}
		if(StringUtils.isNotBlank(pid) && arrayOpen.isEmpty()){
			flag = true;
			arrayOpen.clear();
			for (Object object : array) {
				JSONObject o = JSONObject.fromObject(object);
				if(pid.contains(o.get("pid").toString())){
					o.put("pid", comPid);
				}
				arrayOpen.add(o);
			}
		}
		if(array!=null && array.size()==1||arrayOpen!=null && arrayOpen.size()==1){
			array.clear();
			arrayOpen.clear();
		}
		//所选分类是通用标签
		if(comType.equals("1")){
			flag = true;
			arrayOpen.clear();
			for (Object object : array) {
				JSONObject o = JSONObject.fromObject(object);
				if(o.get("xpath")!=null && o.get("xpath").toString().equals("通用标签") || o.get("name").toString().equals("通用标签")){
					if(!o.get("name").toString().equals("通用标签")){
						o.put("pid", comid);
					}
					arrayOpen.add(o);
				}
			}
		}
		if(oparray){
			flag = true;
			arrayOpen.clear();
			for (Object object : array) {
				JSONObject o = JSONObject.fromObject(object);
				if(o.get("xpath")!=null && o.get("xpath").toString().contains("通用标签") || o.get("name").toString().contains("通用标签")){
					if(!o.get("name").toString().equals("通用标签") && o.get("xpath").toString().contains("通用标签")){
						o.put("pid", comid);
					}
					if(o.get("id").toString().equals(xId)){
						o.put("open", "true");
					}
					if(pid.contains(o.get("pid")+"")){
						o.put("pid", comPid);
					}
					arrayOpen.add(o);
				}else{
					if(o.get("id").toString().equals(xId)){
						o.put("open", "true");
					}
					if(pid.contains(o.get("pid")+"")){
						o.put("pid", comPid);
					}
					arrayOpen.add(o);
				}
			}
		}
		if(flag){
			logger.info("--------------------------arrayOpen------------------------------"+arrayOpen.toString()+"===comid"+comid+"===allId"+allId);
			return arrayOpen.toString();
		}else if(falg2){
			logger.info("--------------------------arrayCom------------------------------"+array.toString()+"===comid"+comid+"===allId"+allId);
			return arrayCom.toString();
		}else{
			logger.info("--------------------------array------------------------------"+array.toString()+"===comid"+comid+"===allId"+allId);
			return array.toString();
		}
		
	}
}
