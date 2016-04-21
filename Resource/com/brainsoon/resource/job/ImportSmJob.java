package com.brainsoon.resource.job;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.dao.hibernate.BaseJdbcDao;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.service.IImportSmService;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchParamCaList;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.model.User;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;

/**
 * 书目导入定时任务
 * @author 唐辉
 * 本定时任务执行的前置条件为：
 * 在系统参数中配置：
 * 1、sm_sample_dir   书目地址
 * 2、sm_marcId  默认开始最大id，可以为空
 */
public class ImportSmJob {

	private static final Logger logger = Logger.getLogger(ImportSmJob.class);

	private BaseJdbcDao baseJdbcDaoSqlServer;  // 书目SqlServer数据库jdbc连接 
	private static IBaseService baseQueryService;
	private static IImportSmService importSmService;
	private String smFilePath = ""; //书目样本目录配置
	private String maxMarcId = ""; //最后一次更新的marcId
	private SysParameter sysParameter = null;
	public static String FILE_ROOT = WebAppUtils.getWebAppRoot() + "fileDir/fileRoot/"; //服务器上FILE_ROOT的路径
	private static final String PUBLISH_SAVE_URL = WebappConfigUtil.getParameter("PUBLISH_SAVE_URL");
	private final static String PUBLISH_FILE_WRITE_QUEUE = WebappConfigUtil.getParameter("PUBLISH_FILE_WRITE_QUEUE");
	private HttpClientUtil http = new HttpClientUtil();
	private Gson gson=new Gson();
	private static String publishType = "44";
	private static String compareToTime = "16:55:000"; //定义自动退出循环的时间
	
	
	
	/**
	 * 初始化服务
	 */
	public void initService() {
		//加载数据服务接口
		try {
			baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			importSmService = (IImportSmService) BeanFactoryUtil.getBean("importSmService");
			baseJdbcDaoSqlServer = (BaseJdbcDao) BeanFactoryUtil.getBean("baseJdbcDao");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取样本地址
	 */
	public void getSmSampleDir(){
		try {
			smFilePath = OperDbUtils.queryParamValueByKey("sm_sample_dir");
		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
		if(StringUtils.isBlank(smFilePath)){
			throw new ServiceException("书目同步失败：书目样本目录【sm_sample_dir】没有配置，请确认！");
		}
	}
	
	
	/**
	 * 获取最后一次更新书目的最大MarcId值
	 */
	public void getMaxMarcId(){
//		if(StringUtils.isNotBlank(maxMarcId)){
		try {
			sysParameter = OperDbUtils.querySysParameterByKey("sm_marcId");
			if(sysParameter == null){
				throw new ServiceException("获取最大的marcId失败：可能是上次没有更新配置，请确认！");
			}
			maxMarcId =  sysParameter.getParaValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		}
		if(StringUtils.isBlank(maxMarcId)){
			throw new ServiceException("获取最大的marcId失败：可能是上次没有更新配置，请确认！");
		}
	}
	

	/* 线程同步标识 */
	private static boolean isruning = false;

	//总入口方法
	public void doImportSmDataJob() {
		
		//初始化服务
		initService();
		
		//获取样本地址
		getSmSampleDir();
		
		//获取最后一次更新书目的最大MarcId值
		getMaxMarcId();
		
		/* 正在处理中则返回 */
		if (isruning) {
			logger.debug("书目导入正在进行中...");
			return;
		}
		
		/* 设置线程为正在运行状态 */
		isruning = true;

		try {
			while (true) {
				//到定义的时间就自动退出循环
//				Date date = new Date();
//				int t = DateUtil.compareDate(DateUtil.getDateTime("yyyy-MM-dd HH:MM:SS",date), DateUtil.getDateTime("yyyy-MM-dd",date) + " " + compareToTime);
//				if(t == -1){
//					logger.info("当前时间【"+DateUtil.getDateTime("yyyy-MM-dd HH:MM:SS",date)+"】超过所定范围:【"+DateUtil.getDateTime("yyyy-MM-dd",date) + " " + compareToTime+"】,自动退出循环");
//					 break;
//				}
				Long startw = System.currentTimeMillis();
				startw = System.currentTimeMillis();
				/* 取出10条数据进行更新 */
				SearchParamCaList searchParamCaList = new SearchParamCaList();
				searchParamCaList.setUploadFilePath(FILE_ROOT);
				User user = new User();
				user.setId(Long.parseLong("172"));
				try {
					List<Map<String, Object>> mapList = baseJdbcDaoSqlServer.getJdbcTemplate().queryForList("select top 50 marcid, marc from cor_marc where marcid > " + maxMarcId + " ORDER BY marcid ASC ; ");
					logger.debug("SqlServer查询数据用时：" + (System.currentTimeMillis() - startw));
					if (mapList != null && mapList.size() > 0) {
						for (int i = 0; i < mapList.size(); i++) {
							Map<String, Object> oneMap = mapList.get(i);
							Set<String> key = oneMap.keySet();
							SearchParamCa searchParamCa = new SearchParamCa();
							searchParamCa.setFilePath(FILE_ROOT);
							searchParamCa.setSmFilePath(smFilePath);
							searchParamCa.setPublishType(publishType);
							//ConvertUtils.register(new DateConverter(null), java.util.Date.class);
							for (Iterator<String> it = key.iterator(); it.hasNext();) {
								String field = (String) it.next();
								String fieldValue = String.valueOf(oneMap.get(field));
								BeanUtils.setProperty(searchParamCa, field, fieldValue);
							}
							searchParamCa.setUpdateTime(new Date().getTime()+"");
							searchParamCa.setUpdateUser(user);
							searchParamCaList.addSearchParamCa(searchParamCa);
						}
					}
				} catch (Exception ex) {
					logger.error("书目数据库连接异常，等待下次重新尝试连接！");
				}

				if (searchParamCaList != null && searchParamCaList.getCas() != null) {
					List<SearchParamCa> cas = searchParamCaList.getCas();
					for (SearchParamCa cnmarcSqlServerPO : cas) {
						Long start = System.currentTimeMillis();
						Ca ca = null;
						try {
							//解析处理逻辑开始....
							//1、保存mac文件
							ca = importSmService.addSm(cnmarcSqlServerPO);
							
							//调用底层方法执行保存：
							//2 保存元数据
							String paraJson = gson.toJson(ca);
							String result = http.postJson(PUBLISH_SAVE_URL,paraJson);
							SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
							String objectId = rtn.getObjectId();
							if(rtn!=null && rtn.getState()==0){
								logger.info("书目资源入库成功！");
								//同时更新marcId参数的值
								sysParameter.setParaValue(cnmarcSqlServerPO.getMarcid());
								baseQueryService.update(sysParameter);
								//日志（有一个默认用户）
								
								String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
								Ca returnCa = gson.fromJson(resourceDetail, Ca.class);//保存完后返回的Ca
								
								List<com.brainsoon.semantic.ontology.model.File> realFiles = returnCa.getRealFiles();
								logger.debug("文件转换----------start--------------");
								if (realFiles != null && realFiles.size() > 0) {
									DoFileQueueList doFileList = ResUtils.converPath(realFiles,objectId);
									logger.debug("文件转换-----111111111111-------------------");
									if(doFileList.getDoFileQueueList().size()>0){
										result = http.postJson(PUBLISH_FILE_WRITE_QUEUE,
												gson.toJson(doFileList));
									}
									logger.debug("文件转换结束 -----end-------------------");
								}
								
							}else{
								logger.error("书目资源入库异常！");
								UserInfo userInfo = new UserInfo();
								userInfo.setUserId(Long.parseLong("172"));
								SysOperateLogUtils.addLog("publish_create", cnmarcSqlServerPO.getMarcid()+":书目资源入库异常！", userInfo);
							}
						} catch (Exception ex) {
							logger.error(cnmarcSqlServerPO.getMarc());
							logger.error("书目资源入库异常！" + ex.getMessage());
							UserInfo userInfo = new UserInfo();
							userInfo.setUserId(Long.parseLong("172"));
							String errorMsg = ex.getMessage();
							if (StringUtils.isNotBlank(errorMsg) && errorMsg.length() > 512) {
								errorMsg = errorMsg.substring(0, 512);
								SysOperateLogUtils.addLog("publish_create", cnmarcSqlServerPO.getMarcid()+":"+errorMsg, userInfo);
							}
							
						} finally {
							//写日志
							if(ca != null && ca.getMetadataMap() != null){
								UserInfo userInfo = new UserInfo();
								userInfo.setUserId(Long.parseLong("172"));
								SysOperateLogUtils.addLog("publish_create", ca.getMetadataMap().get("title"), userInfo);
							}
						}
						logger.debug("单条入库用时：" + (System.currentTimeMillis() - start));
					}
				} else {
					break;
				}
			}
		} catch (Exception ex) {
			logger.error("书目入库异常！", ex);
		} finally {
			isruning = false;
		}

		logger.debug("退出书目导入到资源库！");
	}
	
	
	
	public BaseJdbcDao getBaseJdbcDaoSqlServer() {
		return baseJdbcDaoSqlServer;
	}

	public void setBaseJdbcDaoSqlServer(BaseJdbcDao baseJdbcDaoSqlServer) {
		this.baseJdbcDaoSqlServer = baseJdbcDaoSqlServer;
	}



}
