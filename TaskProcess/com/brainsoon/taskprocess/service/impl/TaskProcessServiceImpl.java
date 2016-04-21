/**
 * @FileName: TaskProcessServiceImpl.java
 * @Package:com.brainsoon.taskprocess.service.impl
 * @Description:
 * @author: tanghui
 * @date:2015-3-31 上午10:52:21
 * @version V1.0
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-3-31       y.nie        1.0         1.0 Version
 */
package com.brainsoon.taskprocess.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.DirContext;
import javax.sql.DataSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.antlr.grammar.v3.ANTLRv3Parser.id_return;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.jpdl.internal.activity.StartActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.File;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.taskprocess.dao.ITaskProcessDao;
import com.brainsoon.taskprocess.model.TaskDetail;
import com.brainsoon.taskprocess.model.TaskProcess;
import com.brainsoon.taskprocess.model.TaskProcessorRelation;
import com.brainsoon.taskprocess.model.TaskResRelation;
import com.brainsoon.taskprocess.service.ITaskProcessService;
import com.google.gson.Gson;
import com.itextpdf.tool.xml.html.head.Title;
/**
 * @ClassName: TaskProcessServiceImpl
 * @Description:加工任务单管理服务层实现类
 * @author: tanghui
 * @date:2015-3-31 上午10:52:21
 */

@Service("taskProcessService")
public class TaskProcessServiceImpl extends BaseService implements ITaskProcessService{

	@Autowired
    @Qualifier("taskProcessDao")
	private ITaskProcessDao taskProcessDao;
	private static String url = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
	//资源明细列表接口
	private final static String PUBLISH_DETAILLIST_URL = WebappConfigUtil.getParameter("PUBLISH_DETAILLIST_URL");
	private JdbcTemplate jdbcTemplate ;
	@Autowired
	public void init(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public TaskProcess save(TaskProcess taskProcess) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startTimeDesc = sdf.format(taskProcess.getStartTime());
		String endTimeDesc = sdf.format(taskProcess.getEndTime());
		taskProcess.setStartTimeDesc(startTimeDesc);
		taskProcess.setEndTimeDesc(endTimeDesc);
		return (TaskProcess)this.getBaseDao().create(taskProcess);
	}

	/**
	 * 分页查询:加工任务单
	 *
	 * @param pageInfo
	 * @param taskProcess
	 * @return
	 */
	public PageResult query(PageInfo pageInfo, TaskProcess taskProcess) throws ServiceException {
		String hql = " from TaskProcess m where m.platformId = " + taskProcess.getPlatformId();
		Map<String, Object> params = new HashMap<String, Object>();
		//加工任务名称
		String taskname = taskProcess.getTaskName();
		if (StringUtils.isNotBlank(taskname)) {
			hql = hql + " and m.taskName like :taskName";
			params.put("taskName", "%" + taskname + "%");
		}
		//创建人
		User user = taskProcess.getCreateUser();
		if (user != null && StringUtils.isNotBlank(user.getLoginName())) {
			hql = hql + " and m.createUser.loginName like :loginName";
			params.put("loginName", "%" + user.getLoginName() + "%");
		}
		hql = hql + " order by id desc";
		try {
			baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}

		return pageInfo.getPageResult();
	}
	
	
	
	/**
	 * 通过taskProcessId 查询本加工任务下的所有的资源元数据
	 * @param taskProcessId
	 * @return
	 */
	public List<String> getAllSysResDirectoryList(Long taskProcessId){
		List<String> resIdList = null;
		if(taskProcessId != null && taskProcessId.longValue() !=0l){
			List<TaskDetail> detailList = query(" from TaskDetail where taskProcess.id=" + taskProcessId);
			if(detailList!=null && detailList.size()>0){
				resIdList = new ArrayList<String>();
				for(TaskDetail detail: detailList){
					List<TaskResRelation> tempResList = query(" from TaskResRelation where taskDetail.id=" + detail.getId());
					if(tempResList!=null&&tempResList.size()>0){
						resIdList.add(tempResList.get(0).getSysResDirectoryId());
					}
				}
			}
		}
		return resIdList;
	}
	
	
	/**
	 * 通过taskProcessId获取加工任务详细信息
	 * @param taskProcessId
	 * @return
	 */
	public TaskProcess getTaskProcessInfo(Long taskProcessId){
		if(taskProcessId == null && taskProcessId.longValue() ==0l){
			return null;
		}
		return (TaskProcess) getByPk(TaskProcess.class, taskProcessId);
	}
	
	
	/**
	 *通过taskProcessId获取加工任务详细信息
	 * @param resourceIds 本次要添加到加工任务的资源Ids
	 * @param resIdList 本次动作前 本任务中已有的资源Ids
	 * @param taskProcessId 本次加工任务信息
	 * @return 
	 */
	public void addResource(String resourceIds,List<String> resIdList,TaskProcess taskProcess){
		
		//当resIdList(需求单中原有的资源ID) 不为空时，需查重
		StringBuffer ids = new StringBuffer();//要传入URL的参数
		if(resIdList!=null){
			String[] rsIds = resourceIds.split(",");
			for (int j = 0; j < rsIds.length; j++) {
				if(!resIdList.contains(rsIds[j])){
					ids.append( rsIds[j] + "," );
				}
			}
			if (ids.toString().endsWith(",")) {
				resourceIds = ids.toString().substring(0, ids.toString().length()-1);
			}
		}
		
		Gson gson = new Gson();
		HttpClientUtil http = new HttpClientUtil();
		SearchParamCa searchParamCa = new SearchParamCa();
		searchParamCa.setIds(resourceIds);
		resourceIds = gson.toJson(searchParamCa);
		String resource = http.postJson(PUBLISH_DETAILLIST_URL, resourceIds);
		CaList caList = gson.fromJson(resource, CaList.class);
		if (caList != null) {
			List<Ca> cas = caList.getCas();
			if(cas != null && cas.size() > 0 ){
				for (Ca ca : cas) {
					try {
						
						//向task_detail表中写数据
						TaskDetail taskDetail = new TaskDetail();
						taskDetail.setTaskProcess(taskProcess);
						taskDetail.setDescription(taskProcess.getDescription());
						taskDetail.setStartTime(taskProcess.getStartTime());
						taskDetail.setEndTime(taskProcess.getEndTime());
						taskDetail.setStatus(0);
						create(taskDetail);
						
						//向task_res_relation中写入数据
						TaskResRelation taskResRelation = new TaskResRelation();
						taskResRelation.setSysResDirectoryId(ca.getObjectId());
						taskResRelation.setTaskDetail(taskDetail);
						taskResRelation.setPublishType(ca.getPublishType());
						taskResRelation.setResName(ca.getMetadataMap().get("title"));
						taskResRelation.setStatus(0);
						create(taskResRelation);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void addMakers(String makerIds,String taskProcessId){
		/*List<TaskDetail> detailList = query("from TaskDetail where taskProcess.id="+
				Long.parseLong(taskProcessId));
		List<User> makers = new ArrayList<User>();
		for(String id : makerIds.split(",")){
			User user = (User) getByPk(User.class, Long.parseLong(id));
			makers.add(user);
		}	
		for(int i=0; i<detailList.size(); i++){
			TaskDetail taskDetail = detailList.get(i);
			if(taskDetail.getStatus()==1){
				taskDetail.setUser(makers.get((i+1)%makers.size()));
				taskDetail.setStatus(2);
				saveOrUpdate(taskDetail);
			}
		}*/
		@SuppressWarnings("unchecked")
		List<TaskProcessorRelation> detailList = query("from TaskProcessorRelation where taskProcess.id=" +
				Long.parseLong(taskProcessId));
		
		if(StringUtils.isNotEmpty(makerIds)){
			if(detailList!=null){
				List<Long> makers = new ArrayList<Long>();
				for(TaskProcessorRelation processor: detailList){
					makers.add(processor.getProcessor().getId());
				}
				for(String id : makerIds.split(",")){
					Long userId = Long.valueOf(id);
					if(!makers.contains(userId)){
						TaskProcessorRelation processor = new TaskProcessorRelation();
						User u = new User();
						u.setId(userId);
						processor.setProcessor(u);
						TaskProcess taskProcess = new TaskProcess();
						taskProcess.setId(Long.valueOf(taskProcessId));
						processor.setTaskProcess(taskProcess);
						create(processor);
					}
				}
			}else{
				for(String id : makerIds.split(",")){
					TaskProcessorRelation processor = new TaskProcessorRelation();
					Long userId = Long.valueOf(id);
					User u = new User();
					u.setId(userId);
					processor.setProcessor(u);
					TaskProcess taskProcess = new TaskProcess();
					taskProcess.setId(Long.valueOf(taskProcessId));
					processor.setTaskProcess(taskProcess);
					create(processor);
				}
			}
		}
	}
	
	public void finishTaskDetail(String sysResDirectoryId){
		List<TaskResRelation> list = query("from TaskResRelation where sysResDirectoryId = '" + sysResDirectoryId + "'");
		if(list!=null&&list.size()>0){
			//TaskResRelation trr = list.get(0);
			for(TaskResRelation trr: list){
				TaskDetail taskDetail = trr.getTaskDetail();
				taskDetail.setStatus(2);
				trr.setStatus(2);
				trr.setStatusDesc("已完成");
				saveOrUpdate(taskDetail);
				saveOrUpdate(trr);
			}
		}
	}

	@Override
	public void deleteRelativeResource(String taskProcessId) {
		List<TaskDetail> list = query("from TaskDetail where taskProcess.id =" + taskProcessId);
		if(list.size()>0){
			for(TaskDetail taskDetail : list){
				String taskResHql = "delete from TaskResRelation where taskDetail.id = :taskDetailId";
				HashMap<String, Object> taskResParas = new HashMap<String, Object>();
				taskResParas.put("taskDetailId", taskDetail.getId());
				baseDao.executeUpdate(taskResHql, taskResParas);
			}
			
			String taskDetailHql = "delete from TaskDetail where taskProcess.id =  :taskProcessId";
			HashMap<String, Object> paras = new HashMap<String, Object>();
			paras.put("taskProcessId", Long.parseLong(taskProcessId));
			baseDao.executeUpdate(taskDetailHql, paras);
		}
	}
	
	@Override
	public String resourceDetail(String resId) {
		HttpClientUtil http = new HttpClientUtil();
		String resUrl = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + resId;
		String resource = http.executeGet(resUrl);
		String objectIds = "";
		if(StringUtils.isNotEmpty(resource)){
			Gson gson = new Gson();
			Ca ca = gson.fromJson(resource, Ca.class);
			if(ca!=null){
				List<File> fileList = ca.getRealFiles();
				if(fileList!=null){
					for(File file : fileList){
						if ("2".equals(file.getIsDir())) {//是文件才查文件详细
							objectIds += file.getObjectId() + ",";
						}
					}
					if(StringUtils.isNotEmpty(objectIds)){
						objectIds = objectIds.substring(0, objectIds.length()-1);
					}
				}
			}
		}	
		String filesUrl = WebappConfigUtil.getParameter("PUBLISH_FILELIST_URL") + "?page=1&size=1000" + "&objectIds=" + objectIds;
		String filesDetail = http.executeGet(filesUrl);
		return filesDetail;
	}

	@Override
	public String queryResourcesByTaskIdAndProcessor(String taskId, String processorId, 
			String page, String size) {
		String result = "";
		List<TaskResRelation> list = query("from TaskResRelation where taskDetail.taskProcess.id = " + Long.valueOf(taskId) + " and taskDetail.user.id=" + Long.valueOf(processorId));
		TaskProcess process  = (TaskProcess) getByPk(TaskProcess.class, Long.valueOf(taskId));
		String resType = process.getResType();
		String resIds = "";
		if(list!=null){
			for(TaskResRelation tr : list){
				String resId = tr.getSysResDirectoryId();
				resIds += resId + ",";
			}
			if(StringUtils.isNotEmpty(resIds)){
				resIds = resIds.substring(0, resIds.length()-1);
			}
			
			int resIdsLen = list.size();
			//由于传过去的参数过长，拼成solr的查询条件过长，报错  所以查过80个资源ID的查询方式变为：
			//  去除Ids条件 在所有查询结果中筛选出符合条件的
			if (resIdsLen>80) {//Ids大于80的情况
				
				HttpClientUtil http = new HttpClientUtil();
				String url = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL");
				if(page==null) {
					page="";
				}
				if(size==null) {
					size="";
				}
				url+="?publishType="+resType+"&page=1&size=1000000&queryType=1";
				String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
				if (StringUtils.isNotBlank(userIds)) {
					if(userIds.endsWith(",")){
						userIds = userIds.substring(0,userIds.length()-1);
					}
					url+="&creator="+userIds;
				}else{
					url+="&creator=-2";
				}
				
				result = http.executeGet(url);
				//处理返回结果
				result = getResourcesByIds(page,size,result,resIds);
				
			}else {//Ids小于80的情况 
				HttpClientUtil http = new HttpClientUtil();
				String url = WebappConfigUtil.getParameter("PUBLISH_QUERYBYPOST_URL");
				if(StringUtils.isEmpty(page)){
					page = "1";
				}
				if(StringUtils.isEmpty(size)){
					size = "0";
				}
				SearchParamCa searchParamCa = new SearchParamCa();
				searchParamCa.setQueryType(0);
				searchParamCa.setObjectIds(resIds);
				searchParamCa.setPage(Integer.parseInt(page)+"");
				searchParamCa.setSize(Integer.parseInt(size));
				Gson gson = new Gson();
				String paraJson = gson.toJson(searchParamCa);
				result = http.postJson(url, paraJson);
			}
			
		}
		return result;
	}

	@Override
	public String queryResourcesByTaskIdAndStatus(String taskId, String status,
			String page, String size) {
		String result = "";
		List<TaskResRelation> list = query("from TaskResRelation where taskDetail.taskProcess.id = " + Long.valueOf(taskId) + " and status=" + Integer.valueOf(status));
		String resIds = "";
		if(list!=null){
			for(TaskResRelation tr : list){
				String resId = tr.getSysResDirectoryId();
				resIds += resId + ",";
			}
			if(StringUtils.isNotEmpty(resIds)){
				resIds = resIds.substring(0, resIds.length()-1);
			}
			HttpClientUtil http = new HttpClientUtil();
			String url = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL");
			if(StringUtils.isEmpty(page)){
				page = "";
			}
			if(StringUtils.isEmpty(size)){
				size = "";
			}
			url += "?queryType=0&pubObjectIds=" + resIds + "&page=" + page + "&size=" + size;
			result = http.executeGet(url);
		}
		return result;
	}

	@Override
	public void batchDeleteResourceByTaskIdAndResDetailIds(Long taskId, String taskDetailIds) {
		if(StringUtils.isNotEmpty(taskDetailIds)){
			String[] arrs = taskDetailIds.split(",");
			for(String id : arrs){
				TaskDetail taskDetail = (TaskDetail) getByPk(TaskDetail.class,  Long.valueOf(id));
				List<TaskResRelation> resList = query(" from TaskResRelation where taskDetail.id =" + Long.valueOf(id));
				baseDao.delete(taskDetail);
				if(resList!=null){
					TaskResRelation res = resList.get(0);
					baseDao.delete(res);
				}
			}
		}
	}
	
	@Override
	public void deleteResourceByTaskIdAndResDetailId(Long taskId, String taskDetailId){
		TaskDetail taskDetail = (TaskDetail) getByPk(TaskDetail.class,  Long.valueOf(taskDetailId));
		String taskResHql = "delete from TaskResRelation where taskDetail.id = :taskDetailId";
		HashMap<String, Object> taskResParas = new HashMap<String, Object>();
		taskResParas.put("taskDetailId", taskDetail.getId());
		baseDao.delete(taskDetail);
		baseDao.executeUpdate(taskResHql, taskResParas);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getNumByTaskIdAndUserId(Long taskId, Long userId) {
		List<TaskDetail> list = query("from TaskDetail where taskProcess.id = " + taskId + " and user.id=" + userId);
		int count = 0;
		if(list!=null){
			count = list.size();
		}
		return count;
	}
	
	@Override
	public void delProcessor(Long taskId, String processordIds){
		if(StringUtils.isNotEmpty(processordIds)){
			String[] ids = processordIds.split(",");
			for(String id : ids){
				String taskResHql = "delete from TaskProcessorRelation where processor.id = :processorId";
				HashMap<String, Object> taskResParas = new HashMap<String, Object>();
				taskResParas.put("processorId", Long.valueOf(id));
				baseDao.executeUpdate(taskResHql, taskResParas);
			}
		}
	}
	
	@Override
	public List<TaskDetail> getTaskDetailByTaskIdAndStatus(Long taskId, Integer status){
		if(status!=null){
			List<TaskDetail> list = query("from TaskDetail where taskProcess.id = " + taskId + " and status=" + status);
			return list;
		}else{
			List<TaskDetail> list = query("from TaskDetail where taskProcess.id = " + taskId);
			return list;
		}
	}

	@Override
	public List<TaskProcessorRelation> getTaskProcessorRelationByTaskId(Long taskId) {
		List<TaskProcessorRelation> list = query("from TaskProcessorRelation where taskProcess.id = " + taskId);
		return list;
	}
	@Override
	public List<TaskProcessorRelation> getRelationByTaskIdAndProcessorIds(Long taskId , String processorIds) {
		List<TaskProcessorRelation> list = query("from TaskProcessorRelation where taskProcess.id = " + taskId +" and processor.id in ("+processorIds+")");
		return list;
	}
	
	@Override
	public String averageDist(List<TaskDetail> taskDetailList, List<TaskProcessorRelation> taskProcessorList){
		String result = "";
		int len = taskDetailList.size();
		if(taskProcessorList!=null){
			int proNum = taskProcessorList.size();
			int avgNum = len/proNum;
			int temp = 0;
			int userCounter = 0;
			for(int counter=0; counter<len; counter++){
				if(temp>=avgNum){
					userCounter++;
					temp = 0;
				}
				if(userCounter>=proNum){
					userCounter = proNum-1;
				}
				TaskProcessorRelation tp = taskProcessorList.get(userCounter);
				User user = tp.getProcessor();
				TaskDetail taskDetail = taskDetailList.get(counter);
				taskDetail.setUser(user);
				taskDetail.setStatus(Integer.valueOf(1));
				baseDao.saveOrUpdate(taskDetail);
				temp++;
			}
		}
		result = "success";
		return result;
	}

	@Override
	public TaskResRelation getResListByDetailId(Long taskDetailId) {
		// TODO Auto-generated method stub
		List<TaskResRelation> list = query("from TaskResRelation where taskDetail.id = " + taskDetailId);
		if(list!=null){
			return list.get(0);
		}
		return null;
	}

	@Override
	public String getTaskResPkByTaskIdAndProcessorIds(Long taskId,
			String processorIds) {
		String taskResPk = "";
		if(taskId!=null&&StringUtils.isNotEmpty(processorIds)){
			String[] processors = processorIds.split(","); 
			for(String processorId: processors){
				@SuppressWarnings("unchecked")
				List<TaskResRelation> list = query("from TaskResRelation where taskDetail.taskProcess.id = " +
							taskId + " and taskDetail.user.id=" + Long.valueOf(processorId));
				if(list!=null){
					for(TaskResRelation trr: list){
						taskResPk += trr.getId() + ",";
					}
				}
			}
			if(StringUtils.isNotEmpty(taskResPk)){
				int len = taskResPk.length();
				taskResPk = taskResPk.substring(0, len-1);
			}
		}
		return taskResPk;
	}

	@Override
	public String copyProcessFiles(Long taskId, String processorIds) {
		TaskProcess taskProcess = (TaskProcess) getByPk(TaskProcess.class, taskId);
		if(taskProcess!=null){
			String processExportRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.processExportRoot).replaceAll("\\\\", "\\/"); //资源文件下载根路径
			String fileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replaceAll("\\\\", "\\/"); //资源文件根路径
			String time2Str = DateUtil.convertDateTimeToString(taskProcess.getCreateTime()).replace(":", "").replace(" ", "");
			try{
				if(StringUtils.isNotEmpty(processorIds)){
					String[] arr = processorIds.split(",");
					String url = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
					for(String processorId: arr){
						User user = (User) getByPk(User.class, Long.valueOf(processorId));
						
						//资源下载路径    processExportRoot/{资源类型}/{加工单ID}/创建时间/{加工员ID}/资源名/文件夹/文件 2015-10-8 15:38:50 huangjun
						//只展示     /{加工单ID}/创建时间/{加工员ID}/
						
						String resType = taskProcess.getResType();//资源类型ID
						String restype = "";
						//若是用户在数据字典里面配置了{资源类型}  则替换路径
				    	IDictNameService dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
				    	LinkedHashMap<String,String> map = new LinkedHashMap<String, String>();
						map = dictNameService.getDictMapByName("模板导入资源类型目录");
						if(!map.isEmpty() && map.get(resType)!=null){
							restype = map.get(resType);
						}
						
						String processExportDir = processExportRoot + restype + "/" + taskProcess.getId() + "/" + time2Str + "/";//资源文件下载路径
						String resIds = getTaskResIdsByTaskIdAndProcessorIds(taskId, processorId);
						logger.info("导出素材文件 要导出的资源ID："+resIds);
						if(user!=null){
							Long userId = user.getId();
							processExportDir += userId ;
						}
						
						if(StringUtils.isNotEmpty(resIds)){
							copyFileToProcessorDir(fileRoot, url, resIds,
									processExportDir);
						}
					}
				}
			}catch(Exception e){
				logger.error("导出素材文件出错......");
				return null;
			}
		}
		return null;
	}

	/**
	 * 导出资源文件
	 * @param fileRoot 资源根路径
	 * @param url 查询资源详细url
	 * @param resIds 资源id
	 * @param processExportFile 导出后文件的根路径
	 * 
	 * */
	private void copyFileToProcessorDir(String fileRoot, String url,
			String resIds, String processExportDir) throws IOException {
		
		for(String resId : resIds.split(",")){
			HttpClientUtil http = new HttpClientUtil();
			String resUrl = url + "?id=" + resId;
			String resDetail = http.executeGet(resUrl);
			Gson gson = new Gson();
			Ca ca = gson.fromJson(resDetail, Ca.class);
			
			String  title = ca.getMetadataMapValue("title");
			logger.info("导出素材文件 要导出的资源name："+title);
			//String objectId = ca.getObjectId().substring(4,37);
			
			if(ca!=null){
				List<File> fileList = ca.getRealFiles();
				if(fileList!=null){
					for(File fl : fileList){
						String filePath = fl.getPath();
						if(filePath!=null){
							String dir = "";
							if ("2".equals(fl.getIsDir())) {
								int index = fl.getId().indexOf(fl.getAliasName());
								dir = fl.getId().substring(0,index);
								logger.info("导出素材文件  资源名："+title+"  文件id："+fl.getId()+" 文件AliasName："+fl.getAliasName());
							}else {
								dir = fl.getId();
								logger.info("导出素材文件  资源名："+title+"  文件id："+fl.getId()+" 文件AliasName："+fl.getAliasName());
							}
							String resAbsolutePath = fileRoot.replace("\\\\", "\\/") + filePath.replace("\\\\", "\\/");
							java.io.File fileDir = new java.io.File(resAbsolutePath);
							
							
							java.io.File processExportFile = new java.io.File(processExportDir + "/" + title + "/" + dir );
							if(!processExportFile.exists()){
								processExportFile.mkdirs();
							}
							
							
							if(fileDir.exists() && "2".equals(fl.getIsDir())){
								FileUtils.copyFileToDirectory(fileDir, processExportFile);
								logger.info("文件路径++++++++++++++ " + fileDir);
								logger.info("要拷贝到的文件夹路径++++++++++++++ " + processExportFile);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 根据查询条件、资源类型查询该资源的所有的资源信息，返回所有的资源id json串
	 * @param conditions
	 * @param resType
	 * @return
	 */
	private JSONArray getResourcesByCondition(String conditions, String resType) {
		HttpClientUtil http = new HttpClientUtil();
		String url = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL") + "?queryType=1&page=1&size=10000000&publishType=" + resType;
		if(conditions!=null&&!"''".equals(conditions)){
			try {
				conditions = URLEncoder.encode(conditions, "UTF-8");
				url += "&metadataMap=" + conditions;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		int isPrivate = LoginUserUtil.getLoginUser().getIsPrivate();
		if (isPrivate == 1) {
			if(StringUtils.isNotBlank(userIds)){
			}else{
				userIds = LoginUserUtil.getLoginUser().getUserId()+",";
			}
		}
		if (StringUtils.isNotBlank(userIds)) {
			if(userIds.endsWith(",")){
				userIds = userIds.substring(0,userIds.length()-1);
			}
			url+="&creator="+userIds;
		}else{
//			userIds = LoginUserUtil.getLoginUser().getUserId()+",";
			url+="&creator=-2";
		}
		String resources = http.executeGet(url);
		JSONObject allJson = JSONObject.fromObject(resources);
		JSONArray allResult = (JSONArray) allJson.get("rows");
		return allResult;
	}
	
	@Override
	public void saveAllResByCondition(Long taskId, String conditions){
		//第一步：查询加工任务详细信息
		TaskProcess taskProcess = getTaskProcessInfo(taskId);
		if(taskProcess != null){
			//第二步：查询所有的资源信息，返回所有的资源id json串
			JSONArray resArr = getResourcesByCondition(conditions, taskProcess.getResType());
			if(resArr!= null){
				//第三步：找所本加工任务下的所有已经存在的资源集合
				List<String> resIdList = getAllSysResDirectoryList(taskId);
				//第四步：循环遍历资源id信息，不存在的则加入（创建），已存在的则过滤掉
				Gson gson = new Gson();
				String resStrings = "";
				StringBuffer resIds = new StringBuffer();
				for(int i=0;i<resArr.size();i++){//循环资源
					Ca ca = gson.fromJson(resArr.get(i).toString(), Ca.class);//资源Ca
					resIds.append(ca.getObjectId()+",");
				}
				if (resIds.toString().endsWith(",")) {
					resStrings = resIds.toString().substring(0, resIds.toString().length()-1);
				}
				addResource(resStrings,resIdList,taskProcess);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String canDeleteUser(String userIds) {
		String result = "0";
		List<TaskProcessorRelation> list = query("from TaskProcessorRelation t where t.processor.id in (" + userIds +")");
		if(list!=null && list.size() > 0){
			result = "1";
		}
		return result;
	}
	
	private void writeLogFile(String pathName, String logDir, User user, String param){
		FileWriter fileWriter = null;
		java.io.File logFile = new java.io.File(logDir);
		if(!logFile.exists()){
			logFile.mkdirs();
		}
		try {
			fileWriter = new FileWriter(pathName, true);
			fileWriter.write(param + "++++++++   "+ user.getLoginName() + "\r\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveAverageDist(List<TaskDetail> taskList,
			List<TaskProcessorRelation> processorList) {
		int avgNum = 0;
		int taskCount = 0;
		int processorCount = 0;
		//String path = "C:\\Users\\root\\Desktop\\log.txt";
		//String logDir = "C:\\Users\\root\\Desktop";
		if(taskList!=null&&taskList.size()>0&&
				processorList!=null&&processorList.size()>0){
			taskCount = taskList.size();
			processorCount = processorList.size();
			avgNum = taskCount/processorCount;
			int distedNum =avgNum*processorCount; 
			List<TaskDetail> avgList = new ArrayList<TaskDetail>();
			if(taskCount>distedNum){
				int surplusNum = taskCount - distedNum;
				
				List<TaskDetail> surplusList = new ArrayList<TaskDetail>();
				for(int index=0;index<taskCount;index++){
					if(index<surplusNum){
						surplusList.add(taskList.get(index));
					}else{
						avgList.add(taskList.get(index));
					}
				}
				for(int i=0;i<surplusNum;i++){
					TaskProcessorRelation tp = processorList.get(i);
					User user = tp.getProcessor();
					TaskDetail taskDetail = surplusList.get(i);
					Long detailId = taskDetail.getId(); 
					taskDetail.setUser(user);
					//writeLogFile(path, logDir, user, "11111");
					taskDetail.setStatus(Integer.valueOf(1));
					update(taskDetail);
					TaskResRelation tr = getResListByDetailId(detailId);
					tr.setStatus(Integer.valueOf(1));
					tr.setStatusDesc("未加工");
					update(tr);
				}
			}else{
				avgList = taskList;
			}
			int temp = 0;
			int userCounter = 0;
			if(avgList!=null&&avgList.size()>0){
				for(int counter=0; counter<avgList.size(); counter++){
					if(temp>=avgNum){
						userCounter++;
						temp = 0;
					}
					TaskProcessorRelation tp = processorList.get(userCounter);
					User user = tp.getProcessor();
					TaskDetail taskDetail = avgList.get(counter);
					taskDetail.setUser(user);
					//writeLogFile(path, logDir, user, "22222");
					taskDetail.setStatus(Integer.valueOf(1));
					update(taskDetail);
					TaskResRelation tr = getResListByDetailId(taskDetail.getId());
					tr.setStatus(Integer.valueOf(1));
					update(tr);
					temp++;
				}
			}
		}
		
		
	}

	@Override
	public String getTaskResIdsByTaskIdAndProcessorIds(Long taskId,
			String processorIds) {
		String taskResPk = "";
		if(taskId!=null&&StringUtils.isNotEmpty(processorIds)){
			String[] processors = processorIds.split(","); 
			for(String processorId: processors){
				@SuppressWarnings("unchecked")
				List<TaskResRelation> list = query("from TaskResRelation where taskDetail.taskProcess.id = " +
							taskId + " and taskDetail.user.id=" + Long.valueOf(processorId));
				if(list!=null){
					for(TaskResRelation trr: list){
						taskResPk += trr.getSysResDirectoryId() + ",";
					}
				}
			}
			if(StringUtils.isNotEmpty(taskResPk)){
				int len = taskResPk.length();
				taskResPk = taskResPk.substring(0, len-1);
			}
		}
		return taskResPk;
	}

	@Override
	public void deleteRelativeProcessor(String taskProcessId) {
		String taskResHql = "delete from TaskProcessorRelation where taskProcess.id = :taskProcessId";
		HashMap<String, Object> taskResParas = new HashMap<String, Object>();
		taskResParas.put("taskProcessId", Long.valueOf(taskProcessId));
		baseDao.executeUpdate(taskResHql, taskResParas);
	}
	
	/**
	 * 根据id和资源类型获取资源Id
	 * @param ids
	 * @param publishType
	 * @return
	 */
	public String getResIdsByIdAndPublishType(String ids , String publishType,String status){
		StringBuffer sql = new StringBuffer();
		String metaDataIds = "";
		sql.append("SELECT sys_res_directory_id FROM task_res_relation where id in ("+ids+") and publishType="+Integer.parseInt(publishType));
		if (StringUtils.isNotEmpty(status)) {
			sql.append(" and status="+Integer.parseInt(status));
		}
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();
			StringBuffer resIds = new StringBuffer();
			while(it.hasNext()) {    
				Map map = (Map) it.next();
			    String resId = map.get("sys_res_directory_id").toString();
			    resIds.append(resId+",");
			}
			metaDataIds = resIds.toString();
			if (metaDataIds.endsWith(",")) {
				metaDataIds = metaDataIds.substring(0,metaDataIds.length()-1).toString();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return metaDataIds;
	}

	@Override
	public String getResIdsByPage(String page, String page1, String pageSize,
			String resName, String userName, String taskName, String publishType) {
		StringBuffer sql = new StringBuffer();
		String metaDataIds = "";
		sql.append("SELECT * FROM task_res_relation , task_detail , task_process where  "
				+ " task_res_relation.task_detail_id = task_detail.id "
				+ " and task_detail.task_process_id = task_process.id ");
		
		//加工单名称
		if(StringUtils.isNotBlank(taskName)){
			try {
				taskName = URLDecoder.decode(taskName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sql.append(" and task_process.taskName like "+"'%"+taskName+"%'");
		}
		
		//加工员
		if(StringUtils.isNotBlank(userName)){
			try {
				userName = URLDecoder.decode(userName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sql.append(" and sys_user.user_name like "+"'%"+userName+"%'");			
		}
		
		//资源名称
		if(StringUtils.isNotBlank(resName)){
			try {
				resName = URLDecoder.decode(resName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sql.append(" and task_res_relation.res_name like "+"'%"+resName+"%'");			
		}
		if(StringUtils.isNotBlank(publishType)){
			sql.append(" and task_res_relation.publishType = "+Integer.parseInt(publishType));			
		}
		
		//权限相关
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		if(userInfo!=null){
			Map<String, String> resMap = userInfo.getResTypes();
			if(resMap!=null){
				Set<String> set = resMap.keySet();
				Iterator<String> it = set.iterator();
				String resTypes = "";
				while(it.hasNext()){
					resTypes += "'" + it.next() + "'" + ",";
				}
				if(StringUtils.isNotEmpty(resTypes)){
					resTypes = resTypes.substring(0, resTypes.length()-1);
					sql.append(" and task_res_relation.publishType in ( "+resTypes+")");	
				}
				
			}
			String userIds = userInfo.getDeptUserIds();
			int isPrivate = userInfo.getIsPrivate();
			if(1==isPrivate){
				if(StringUtils.isNotBlank(userIds)){
					sql.append(" and task_process.create_user in ("+userIds.substring(0, userIds.length()-1)+")");
				}else{
					sql.append(" and task_process.create_user = "+userInfo.getUserId());
				}
			}else{
				if(StringUtils.isNotEmpty(userIds)){
		    		sql.append(" and task_process.create_user in ("+userIds.substring(0, userIds.length()-1)+")");
				}else{
					sql.append(" and task_process.create_user in (-2)");
				}
			}
		}
		
		//获取第page页到page1页的所有数据
		String startIndex = ( Integer.parseInt(page) - 1 ) * Integer.parseInt(pageSize)+"";
		String rowTotal = ( Integer.parseInt(page1) - Integer.parseInt(page) + 1 ) * Integer.parseInt(pageSize)+"";
		sql.append(" ORDER BY task_res_relation.id DESC limit "+startIndex+","+rowTotal);
		
		
		//执行SQL
		try {
			List list = jdbcTemplate.queryForList(sql.toString());
			Iterator it = list.iterator();
			StringBuffer resIds = new StringBuffer();
			
			//获取resId
			while(it.hasNext()) {    
				Map map = (Map) it.next();
			    String resId = map.get("sys_res_directory_id").toString();
			    resIds.append(resId+",");
			}
			
			metaDataIds = resIds.toString();
			if (metaDataIds.endsWith(",")) {
				metaDataIds = metaDataIds.substring(0,metaDataIds.length()-1).toString();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
		return metaDataIds;
	}
	
	/**
	 * 取消分配的资源
	 * @param taskId
	 * @param processorId
	 * @param resIds
	 * @return
	 */
	public String removeResToProcessor(String taskId,String processorId,String resIds){
		String result = "SUCCESS";
		try {
			/*String detailSql = "from TaskDetail where taskProcess.id ="+Long.parseLong(taskId) +" and user.id = "+Long.parseLong(processorId);
			List<TaskDetail> details= baseDao.query(detailSql);
			for (int i = 0; i < details.size(); i++) {
				details.get(i).setUser(null);
				details.get(i).setStatus(Integer.valueOf(0));
				baseDao.update(details.get(i));
			}*/
			
			//处理资源ids,资源加单引号
			String[] res = resIds.split(",");
			String rsIdsStr = "";
			for (String string : res) {
				rsIdsStr += "'"+string+"',";
			}
			if (rsIdsStr.endsWith(",")) {
				rsIdsStr = rsIdsStr.substring(0,rsIdsStr.length()-1);
			}
			
			String relationSql = "from TaskResRelation where taskDetail.taskProcess.id ="+Long.parseLong(taskId) +" and sysResDirectoryId in ( "+rsIdsStr +")";
			List<TaskResRelation> relations= baseDao.query(relationSql);
			for (int j = 0; j < relations.size(); j++) {
				//更新task_detail表
				TaskDetail taskDetail = relations.get(j).getTaskDetail();
				taskDetail.setStatus(Integer.valueOf(0));
				taskDetail.setUser(null);
				baseDao.update(taskDetail);
				
				//更新task_res_relation表
				relations.get(j).setStatus(Integer.valueOf(0));
				relations.get(j).setStatusDesc(null);
				baseDao.update(relations.get(j));
			}
		} catch (NumberFormatException e) {
			logger.error("removeResToProcessor 类型转换异常 任务单："+taskId+" 加工员："+processorId+" 资源ids："+resIds);
			result = "ERROR";
			e.printStackTrace();
		} catch (DaoException e) {
			logger.error("removeResToProcessor 数据库连接 任务单："+taskId+" 加工员："+processorId+" 资源ids："+resIds);
			result = "ERROR";
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * 在Ids大于80的情况下，查询全部结果，在结果中筛选
	 * @param result 查询的全部结果（没有Ids的情况下）
	 * @param resIds 帅选条件
	 * @return
	 */
	public String getResourcesByIds(String page,String size,String result , String resIds){
		SearchResultCa searchCa = new Gson().fromJson(result, SearchResultCa.class);
		List<Ca> cas = searchCa.getRows();
		logger.info("-------------加工任务 -加工员 - 详情 全部资源--------"+searchCa.getTotal()+"------------------------------");
		List<Ca> allEligibleCas = new ArrayList<Ca>();
		List<Ca> eligibleCasPage = new ArrayList<Ca>();
		
		//符合条件的查询结果
		for (int i=0 ;i<cas.size();i++ ) {
			if (resIds.contains(cas.get(i).getObjectId())) {
				allEligibleCas.add(cas.get(i));
			}
		}
		logger.info("-------------加工任务 -加工员 - 详情  符合条件的资源--------"+allEligibleCas.toString()+"------------------------------");
		
		//若page=2，size=10   则为查询11-20条
		int start = (Integer.parseInt(page) - 1) * Integer.parseInt(size) +1;
		int end =  Integer.parseInt(page) * Integer.parseInt(size);
		
		//分页
		for (int j = 0; j < allEligibleCas.size(); j++) {
			if ((j+1) >= start && (j+1) <= end ) {
				eligibleCasPage.add(allEligibleCas.get(j));
			}
		}
		logger.info("-------------加工任务 -加工员 - 详情  符合条件分页的资源List<Ca>--------"+eligibleCasPage.toString()+"------------------------------");
		
		SearchResultCa resultCa = new SearchResultCa();
		resultCa.setMaxRow(Integer.parseInt(size));
		resultCa.setRows(eligibleCasPage);
		resultCa.setStartRow(start);
		resultCa.setTotal(allEligibleCas.size());
		logger.info(JSONObject.fromObject(resultCa).toString());
		logger.info("-------------加工任务 -加工员 - 详情  符合条件分页的资源SearchResultCa--------"+JSONObject.fromObject(resultCa).toString()+"------------------------------");
		return JSONObject.fromObject(resultCa).toString();
		
	}
	
}
