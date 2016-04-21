package com.brainsoon.resrelease.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.FileToolkit;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.po.SubjectStore;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.util.RandomNumberGenerator;
import com.brainsoon.resrelease.dao.IResReleaseDao;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ResFileRelation;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResOrderDetail;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.resrelease.support.LogUtil;
import com.brainsoon.resrelease.support.PublishExcelUtil;
import com.brainsoon.resrelease.support.ResMsg;
import com.brainsoon.resrelease.support.ResReleaseConstant;
import com.brainsoon.resrelease.support.SftpUtil;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.MetaDataDC;
import com.brainsoon.system.model.MetaDataModelGroup;
import com.brainsoon.system.model.User;
import com.google.gson.Gson;
@Service
public class ResReleaseService extends BaseService implements
		IResReleaseService {
	private Logger log = Logger.getLogger(ResReleaseService.class);
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	//获取资源根路径
	private static final String fileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot).replaceAll("\\\\", "\\/");
	//获得发布的根路径
	private static final String publishRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replaceAll("\\\\", "\\/");
	//private final static SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static String PUBLISH_SCQUERYBYTIME_URL = WebappConfigUtil.getParameter("PUBLISH_SCQUERYBYTIME_URL");
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	@Qualifier("resConverfileTaskService")
	private IResConverfileTaskService resConverfileTaskService;
	
	@Autowired
	private IResReleaseDao resReleaseDao;
	
	public IBaseDao init(){
		if(baseDao == null){
			try {
				return  (IBaseDao) BeanFactoryUtil.getBean("baseDao");
			} catch (Exception e) {
				return null;
			}
		}else{
			return baseDao;
		}
	}
	/**
	 * 分页查询
	 * @param pageInfo
	 * @param resRelease
	 * @return
	 */
	public PageResult queryResRelease(PageInfo pageInfo, ResRelease resRelease) throws ServiceException{
		String hql=" from ResRelease rel where 1=1 ";
    	Map<String, Object> params=new HashMap<String, Object>();
    	try {
    		baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    
    	return pageInfo.getPageResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ResRelease> getSysList() {
		List<ResRelease> resList = null;
		try {
			resList = query("from ResRelease");
		} catch (DaoException e) {
			if (logger.isErrorEnabled()) {
				logger.error("查询参数出现异常", e);
			}
			throw new ServiceException("查询参数出现异常");
		}
		return resList;
	}


	@Override
	public void save(ResRelease resReleases) {
		this.getBaseDao().create(resReleases);
	}
	
	
	
	public void doProcess(String processTaskId){
		// 设置加工状态为加工中
		updateResRelaseStatus(processTaskId, ResReleaseConstant.TaskStatus.PROCESSING);
		@SuppressWarnings("unchecked")
		List<ResOrderDetail> list = (List<ResOrderDetail>) getBaseDao().getByPk(ResOrderDetail.class, processTaskId);
		for(ResOrderDetail res :list){
			if(ResReleaseConstant.DetailStatus.PROCESSED.equals(res.getStatus())){
				continue;
			}
			//ProductProcessor
		}
	}

	@Override
	public void updateResRelaseStatus(String resReleaseId, String status){
		ResRelease resRelease = (ResRelease) this.getBaseDao().getByPk(ResRelease.class, resReleaseId);
		UserInfo userInfo = LoginUserUtil.getLoginUser(); 
		User user = new User();
		user.setId(userInfo.getUserId());
		resRelease.setStatus(status);
		resRelease.setUpdateTime(new Date());
		resRelease.setProcessTime(new Date());
		resRelease.setUpdateUser(user);
		this.getBaseDao().update(resRelease);
	}
	
	@Override
	public ResRelease queryRelReleaseByOrderId(Long orderId) {
		String hql = "from ResRelease rel where rel.resOrder.orderId= "+orderId;
		@SuppressWarnings("unchecked")
		List<ResRelease> resRelease = getBaseDao().query(hql);
		if(resRelease!=null&&resRelease.size()>0){
			return resRelease.get(0);
		}
		return null;
	}

	/**
	 * 
	 */
	@Override
	public Long saveResReleaseProcessTask(ResOrder order, String batchNum,
			String reasonType, String reasonDesc, 
			String description) {

		String hql = " from ResRelease rel where 1=1";
		if(StringUtils.isNotBlank(batchNum)){
    		hql=hql+" and rel.batchNum="+"'"+batchNum+"'";
    	}
		List<ResRelease> list = getBaseDao().query(hql);
		if(list!=null&&list.size()>0){
			throw new ServiceException("数据库已存在该批次码【"+ batchNum +"】,批次码必须唯一！");
		}
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		User user = new User();
		user.setId(userInfo.getUserId());
		ResRelease resRelease = new ResRelease();
		resRelease.setPlatformId(userInfo.getPlatformId());
		//resRelease.setTemplate(order.getTemplate());
		resRelease.setCreateTime(new Date());
		resRelease.setCreateUser(user);
		resRelease.setReasonDesc(reasonDesc);
		resRelease.setUpdateTime(new Date());
		resRelease.setUpdateUser(user);
		resRelease.setBatchNum(batchNum);
		resRelease.setReasonType(reasonType);
		resRelease.setDescription(description);
		resRelease.setOrderId(order.getOrderId());
		resRelease = (ResRelease) this.getBaseDao().create(resRelease);
		return resRelease.getId();
	}

	@Override
	public PageResult queryPublishDetail(PageInfo pageInfo,
			ResReleaseDetail resReleaseDetail) {
		String hql=" from ResRelease sp where 1=1 ";
    	Map<String, Object> params=new HashMap<String, Object>();
    	try {
    		baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    
    	return pageInfo.getPageResult();
	}

	@Override
	public PageResult resSelectQuery(PageInfo pageInfo) {
		
		return pageInfo.getPageResult();
	}
	
	public void processResource(ResOrder resOrder, Long releaseId, String fileRoot, String header,
			ParamsTempEntity paramsTempEntity) throws NumberFormatException, URISyntaxException, IOException, InterruptedException {
		//获得资源的相关信息返回字符串
		String paramJson = resOrder.getTemplate().getParamsJson();
		JSONObject paramObj = JSONObject.fromObject(paramJson);
		Object flags = paramObj.get("book");
		if (flags == null) {
			try {
				IBaseService baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
				List<ResReleaseDetail> resReleaseDetail = null;
				String hql = " from ResReleaseDetail rrd where rrd.status=0 and rrd.releaseId="+releaseId+" order by rrd.detailId asc";
				resReleaseDetail = baseQueryService.query(hql);
				/*for (ResReleaseDetail detail : resReleaseDetail) {
					TaskInfo task = new TaskInfo();
					task.setDetailId(detail.getDetailId());
					FileProcessMultiThread fileProcessMultiThread = new FileProcessMultiThread(3);
					fileProcessMultiThread.addTask(task);
				}*/
				ResRelease release = (ResRelease) baseQueryService.getByPk(ResRelease.class, releaseId);
				release.setStatus(ResReleaseConstant.OrderStatus.PUBLISHED);
			} catch (DaoException e) {
				throw e;
//				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	
	public String getContent(String[] pathArr, String[] sysDirs){
		String str = "";
		for(String sysDir :sysDirs){
			if(str!=""){
				break;
			}
			for(String arr: pathArr){
				if(sysDir.equals(arr)){
					str = sysDir;
					break;
				}
				
			}
		}
		return str;
	}
	
	public void copyFileFromServer(Long orderId){
		
	}
	
	/**
	 * 结果为 0 ：视频资源
	 * 结果为 1 ：图片资源
	 * 结果为 2 ：文本资源
	 * @param suffix  文件后缀名
 	 * @param typeList 后缀名list集合
	 * @return
	 */
	public int getProcessType(String suffix, List<String[]> typeList){
		int count = 0;
		int flag = -1;
		for(String[] types:typeList){
			for(String type :types){
				if(type.equals(suffix.trim())){
					flag = count;
					break;
				}
			}
			count++;
		}
		return flag;
	}
	
	
	public PageResult getResOrderRecordList(PageInfo pageInfo, ResReleaseDetail resReleaseDetail){
		String hql=" from ResReleaseDetail rrd where 1=1 ";
		Map<String, Object> params=new HashMap<String, Object>();
		try {
			baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    	
		return pageInfo.getPageResult();
	}
	
	@Override
	public void downloadBookRes(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd){
		if(StringUtils.isNotBlank(objectIds)){
			String [] ids = StringUtils.split(objectIds,",");
			String parentPath = BresAction.FILE_TEMP + UUID.randomUUID().toString() + File.separator;
			String title = "资源";
			for (String objectId : ids) {
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail= http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+"?id="+objectId);
				Gson gson=new Gson();
				Ca ca=gson.fromJson(resourceDetail, Ca.class);
				
				title = ca.getCommonMetaData().getTitle();
				File parent = new File(parentPath + title + "_" + RandomNumberGenerator.generateNumber3());
				parent.mkdirs();
			}
			String zipName = "";
			if(ids.length>1){
				//压缩parent目录
				zipName = BresAction.FILE_TEMP + "资源包.zip";
			}else{
				zipName = BresAction.FILE_TEMP + title + ".zip";
			}
			
			try {
				ZipUtil.zipFileOrFolder(parentPath, zipName, null);
				if(StringUtils.isNotBlank(encryptPwd)){
					String encryptZip = BresAction.FILE_TEMP + "资源加密.zip";
					ZipUtil.encryptZipFile(new File(zipName), encryptZip, encryptPwd);
					downloadFile(request, response, encryptZip, false);
				}else{
					downloadFile(request, response, zipName, false);
				}
			} catch (Exception e) {
				logger.error("压缩出现问题" + e.getMessage());
			}
		}
	}
	
	public void downloadRes(HttpServletRequest request,HttpServletResponse response, String objectIds, String encryptPwd){
		if(StringUtils.isNotBlank(objectIds)){
			String [] ids = StringUtils.split(objectIds,",");
			String parentPath = BresAction.FILE_TEMP + UUID.randomUUID().toString() + File.separator;
			for (String objectId : ids) {
				Asset asset = baseSemanticSerivce.getResourceById(objectId);
//				asset.getFiles()
				List<com.brainsoon.semantic.ontology.model.File> modelFiles = null;
				
				String title = asset.getCommonMetaData().getTitle();
				
				File parent = new File(parentPath + title + "_" + RandomNumberGenerator.generateNumber3());
				parent.mkdirs();
				for (com.brainsoon.semantic.ontology.model.File file : modelFiles) {
					try {
						FileUtils.copyFileToDirectory(new File(FILE_ROOT + file.getPath()), parent);
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}
			
			//压缩parent目录
			//String time2Str = DateUtil.convertDateTimeToString(new Date()).replace(":", "").replace(" ", "");
			//String zipName = BresAction.FILE_TEMP + time2Str + ".zip";
		}
	}
	
	
	/**
	 * 下载文件
	 * @param request
	 * @param response
	 * @param filePath
	 * @param isOnLine
	 * @throws Exception
	 */
	public void downloadFile(HttpServletRequest request,HttpServletResponse response, String filePath, boolean isOnLine)
			throws Exception {
		File f = new File(filePath);
		if (!f.exists()) {
			response.sendError(404, "File not found!");
			return;
		}
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
		byte[] buf = new byte[1024];
		int len = 0;

		response.reset(); // 非常重要
		// 处理文件中，主要中文
		String fileName = f.getName();
		String userAgent = request.getHeader("USER-AGENT");
		String new_filename = URLEncoder.encode(fileName, "UTF-8");
		String rtn = "";
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			// IE浏览器，只能采用URLEncoder编码
			if (userAgent.indexOf("msie") != -1) {
				rtn = "filename=\"" + new_filename + "\"";
			}
			// Opera浏览器只能采用filename*
			else if (userAgent.indexOf("opera") != -1) {
				rtn = "filename*=UTF-8''" + new_filename;
			}
			// Safari浏览器，只能采用ISO编码的中文输出
			else if (userAgent.indexOf("safari") != -1) {
				rtn = "filename=\""
						+ new String(fileName.getBytes("UTF-8"), "ISO8859-1")
						+ "\"";
			}
			// Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
			else if (userAgent.indexOf("applewebkit") != -1) {
				new_filename = MimeUtility.encodeText(fileName, "UTF-8", "B");
				rtn = "filename=\"" + new_filename + "\"";
			}
			// FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
			else if (userAgent.indexOf("mozilla") != -1) {
				rtn = "filename*=UTF-8''" + new_filename;
			}
		}
		// 解决下载时，空格变加号
		fileName = fileName.replace("+", "%20");
		response.setCharacterEncoding("utf-8");
		if (isOnLine) { // 在线打开方式
			URL u = new URL("file:///" + filePath);
			response.setContentType(u.openConnection().getContentType());
			response.setHeader("Content-Disposition", "inline; " + rtn + "");
			// 文件名应该编码成UTF-8
		} else { // 纯下载方式
			response.setContentType("application/x-msdownload");
			// fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setHeader("Content-Disposition", "attachment; " + rtn + "");
		}
		OutputStream out = response.getOutputStream();
		while ((len = br.read(buf)) > 0)
			out.write(buf, 0, len);
		br.close();
		out.close();
	}

	@Override
	public PageResult queryResReleaseDetail(PageInfo pageInfo,
			ResReleaseDetail resReleaseDetail, Long releaseId) {
		String hql=" from ResReleaseDetail detail where 1=1 and releaseId="+releaseId;
    	Map<String, Object> params=new HashMap<String, Object>();
    	try {
    		baseDao.query4Page(hql, pageInfo, params);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(e.getMessage());
		}
    
    	return pageInfo.getPageResult();
	}

	@Override
	public List<MetaDataDC> obtainResRelDeatail(ResOrder resOrder, List<ResOrderDetail> ordDetailList, Long relId, Long publishUserId) {
		log.debug("%%%%%%%%%%%%%%%%%%%%%%%");
		log.debug("111+++++++++++");
		int platformId = resOrder.getPlatformId();
		User user = new User();
		user.setId(publishUserId);
		HttpClientUtil http = new HttpClientUtil();
		log.debug("222+++++++++++");
		String resource = "";
		Gson gson = new Gson();
		log.debug("333+++++++++++");
		List<MetaDataDC> metaList = new ArrayList<MetaDataDC>(); 
		int count = 1;
		//将资源的状态设置为代加工
		for(ResOrderDetail orderDetail:ordDetailList){
			ResReleaseDetail relDetail = new ResReleaseDetail();
			if(platformId==1){
				resource = http.executeGet(WebappConfigUtil.getParameter("RES_DETAIL_URL")+ "?id="+orderDetail.getResId());
				JSONObject json = JSONObject.fromObject(resource);
				String type = (String) json.get("type");
				if("CA".equals(type)){
					Ca ca = gson.fromJson(resource, Ca.class);
					MetaDataDC dc = new MetaDataDC();
					dc.setCommonMetaData(ca.getCommonMetaData());
					dc.setExtendMetaData(ca.getExtendMetaData());
					dc.setCopyRightMetaData(ca.getCopyRightMetaData());
					dc.setId(orderDetail.getResId());
					dc.setImportXpathName(ca.getImportXpathName());
					metaList.add(dc);
					relDetail.setModuleName(ca.getCommonMetaData().getModule());
					relDetail.setResType(ca.getCommonMetaData().getType());
					relDetail.setResTitle(ca.getCommonMetaData().getTitle());
					relDetail.setFileType(ca.getCommonMetaData().getFormat());
				}else{
					log.debug("444+++++++++++");
					Asset asset = gson.fromJson(resource, Asset.class);
					log.debug("afterconverasset###########");
					MetaDataDC dc = new MetaDataDC();
					log.debug("1*****&&&&&&&&&&&");
					dc.setCommonMetaData(asset.getCommonMetaData());
					log.debug("2*****&&&&&&&&&&&");
//					dc.setExtendMetaData(asset.getExtendMetaData());
					log.debug("3*****&&&&&&&&&&&");
//					dc.setCopyRightMetaData(asset.getCopyRightMetaData());
					log.debug("4*****&&&&&&&&&&&");
					dc.setId(orderDetail.getResId());
					log.debug("5*****&&&&&&&&&&&");
					dc.setImportXpathName(asset.getImportXpathName());
					log.debug("6*****&&&&&&&&&&&");
//					asset.getFiles()
					List<com.brainsoon.semantic.ontology.model.File> list = null;
					log.debug("7*****&&&&&&&&&&&");
					if(list!=null&&list.size()>=1){
						dc.getCommonMetaData().putCommonMetaData("filePath", list.get(0).getPath());
						dc.getCommonMetaData().putCommonMetaData("fileByte", list.get(0).getFileByte());
					}
					log.debug("8*****&&&&&&&&&&&");
					metaList.add(dc);
					log.debug("9*****&&&&&&&&&&&");
					relDetail.setModuleName(asset.getCommonMetaData().getModule());
					log.debug("10*****&&&&&&&&&&&");
					relDetail.setResType(asset.getCommonMetaData().getType());
					log.debug("11*****&&&&&&&&&&&");
					relDetail.setResTitle(asset.getCommonMetaData().getTitle());
					log.debug("12*****&&&&&&&&&&&");
					relDetail.setFileType(asset.getCommonMetaData().getFormat());
					log.debug("13*****&&&&&&&&&&&");
				}
			}else{
				resource = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+"?id="+orderDetail.getResId());
				Ca ca = gson.fromJson(resource, Ca.class);
				MetaDataDC dc = new MetaDataDC();
				dc.setCommonMetaData(ca.getCommonMetaData());
				dc.setExtendMetaData(ca.getExtendMetaData());
				dc.setCopyRightMetaData(ca.getCopyRightMetaData());
				dc.setId(orderDetail.getResId());
				dc.setImportXpathName(ca.getImportXpathName());
				metaList.add(dc);
				relDetail.setModuleName(ca.getCommonMetaData().getModule());
				relDetail.setResType(ca.getCommonMetaData().getType());
				relDetail.setResTitle(ca.getCommonMetaData().getTitle());
				relDetail.setFileType(ca.getCommonMetaData().getFormat());
			}
			relDetail.setReleaseId(relId);
			log.debug("999+++++++++++");
			relDetail.setResId(orderDetail.getResId());
			log.debug("1010+++++++++++");
			relDetail.setTemplate(resOrder.getTemplate());
			log.debug("1111+++++++++++");
			relDetail.setStatus(ResReleaseConstant.PublishingStatus.WAITING_PUBLISH);
			relDetail.setCreateTime(new Date());
			log.debug("1212+++++++++++");
			relDetail.setChannelName(resOrder.getChannelName());
			relDetail.setCreateUser(user);
			log.debug("1313+++++++++++");
			relDetail.setPlatformId(platformId);
			relDetail.setProcessTimes((long) 0);
			log.debug("metadata_"+count+"   *****************");
			count++;
			log.debug("1414+++++++++++");
		}
		return metaList;
	}
	
	public List<ResOrderDetail> getAllOrderDetail(String orderId){
		String hql = " from ResOrderDetail where orderId="+Long.valueOf(orderId);
		List<ResOrderDetail> detailList = getBaseDao().query(hql);
		return detailList;
	}
	
	@Override
	public void addDetail(ResReleaseDetail detail){
		getBaseDao().saveOrUpdate(detail);
	}
	
	@Override
	public List<ResReleaseDetail> getResReleaseDetailByCnodition(Long releaseId){
		String hql = " from ResReleaseDetail rrd where rrd.releaseId="+releaseId;
		List<ResReleaseDetail> list = getBaseDao().query(hql);
		return list;
	}
	
	/**
	 * 
	 * @Title: saveToResConverfileTask 
	 * @Description: 保存到待转换队列中
	 * @param   
	 * @return void 
	 * @throws
	 */
	@Override
    public void saveToResConverfileTask(Map<String,ResMsg> map) {
    	if(map != null && map.size() > 0){
    		for (String key : map.keySet()) {
			   ResMsg resMsg = map.get(key);
			   if(resMsg != null){
				   ResConverfileTask rcft = new ResConverfileTask(resMsg.getResId(),resMsg.getResPath(),"");
				   resConverfileTaskService.saveResConverfileTask(rcft); //保存到待转换队列中
			   }
			}
    	}
    }
	
	@Override
	public List<ResReleaseDetail> getResReleaseDetailByRelIdAndStatus(Long relId,
			String status) {
		List<ResReleaseDetail> list = new ArrayList<ResReleaseDetail>();
		list = resReleaseDao.getResReleaseDetailByRelIdAndStatus(relId, status);
		return list;
	}
	@Override
	public ResReleaseDetail getResReleaseDetailByRelIdAndResId(Long relId,
			String resId) {
		ResReleaseDetail detail = resReleaseDao.getResReleaseDetailByRelIdAndResId(relId, resId);
		return detail;
	}
	
	@Override
	public void publishOffLine(ResOrder resOrder, String metaInfo, List<ResOrderDetail> detailList, List<String> metaDataList){
		HttpClientUtil http = new HttpClientUtil();
		String time2Str = DateUtil.convertDateTimeToString(resOrder.getCreateTime()).replace(":", "").replace(" ", ""); 
		String publishDir = publishRoot + time2Str +"/" + resOrder.getOrderId() ;
		java.io.File publishFile = new java.io.File(publishDir);
		Long orderId = resOrder.getOrderId();
		List<ResFileRelation> list = resReleaseDao.getFileListByOrderId(orderId);
		Gson gson = new Gson();
		Ca ca = null;
		try{
			if(!publishFile.exists()){
				publishFile.mkdirs();
			}
			List<Ca> listCa = new ArrayList<Ca>();
			
			for(ResFileRelation file: list){
				String fileId = file.getFileId();
				String url = WebappConfigUtil.getParameter("publish_detail_url") + "?id=" + fileId;
				ca = gson.fromJson(http.executeGet(url), Ca.class);
				Map<String, String> map = ca.getMetadataMap();
				String filePath = map.get("filePath");
				
				String fileAbsolutePath = fileRoot + filePath;
				if(new File(fileAbsolutePath).exists()){
					FileToolkit.copyFile(fileAbsolutePath, publishDir);
					listCa.add(ca);
				}else{
					//写文件丢失日志
					LogUtil.fileMissLog(publishDir, resOrder, ca);
				}
			}
			if(metaInfo.contains("元数据Excel")){
				PublishExcelUtil.writeMetaDataExcel(resOrder, listCa, metaDataList);
			}
			
			if(metaInfo.contains("Xml")){
				//List<WraperObj> listsWraperObj = getXmlObject(resOrder, detailList, metaDataList, null, null,
						//false, null);
				
				//XmlUtil.createResultXml(publishDir, listsWraperObj, resOrder.getOrderId().toString());
			}
			
		}catch(Exception e){
			//TODO 写文件丢失日志
			LogUtil.fileMissLog(publishDir, resOrder, ca);
			e.printStackTrace();
		}
	}
	
	@Override
	public void publishOnLine(ResOrder resOrder, List<ResOrderDetail> detailList, 
			List<String> fileIdList, String metaInfo){
		String path = "";
		SftpUtil sftp = null;
		try{
			//TODO url 端口   用户名 密码 
			String targetHost = "";
			String targetPort = "";
			String userName = "";
			String password = "";
			sftp = new SftpUtil(targetHost, targetPort, userName, password);
			List<Ca> caList = new ArrayList<Ca>();
			//List<WraperObj> list = getXmlObject(resOrder, detailList, fileIdList, sftp, targetPort,
				//	true, caList);
			if(metaInfo.contains("元数据Excel")){
				PublishExcelUtil.writeMetaDataExcel(resOrder, caList, fileIdList);
			}
			
			if(metaInfo.contains("Xml")){
				
				//XmlUtil.createResultXml(path, list, resOrder.getOrderId().toString());
			}
			//XmlUtil.createResultXml(path, list, resOrder.getOrderId().toString());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(sftp!=null){
				sftp.closeSFTPConnect();
			}
		}
	}
	@Override
	public ResRelease getResRelease(Long id, String posttype) {
		String sql = "from ResRelease t where t.orderId="+id+" and t.posttype='"+posttype+"' ";
		List<ResRelease> release = getBaseDao().query(sql);
		return release.get(0);
	}
	@Override
	public String updateReleaseInfo(String jsons) {
		logger.info("资源发布回写开始-----------------------");
		JSONObject jsonObject = JSONObject.fromObject(jsons);
		if(jsonObject!=null){
			Object zreleaseIds = jsonObject.get("zreleaseId");
			String zreleaseId = "";
			if(zreleaseIds!=null){
				zreleaseId = jsonObject.getString("zreleaseId");
			}
			Object xreleaseIds = jsonObject.get("xreleaseId");
			String xreleaseId = "";
			if(xreleaseIds!=null){
				xreleaseId = jsonObject.getString("xreleaseId");
			}
				//资源总数量
				//String totalNum = jsonObject.getString("totalNum");
				//资源成功数量
				//String succesNum = jsonObject.getString("succesNum");
				//资源失败数量 
				//String failNum = jsonObject.getString("failNum");
				//如果zreleaseId不为空，则是主题库
				if(StringUtils.isNotBlank(zreleaseId)){
					ResRelease release = null;
					SubjectStore subjectStore =null;
					try {
						release = getResRelease(Long.decode(zreleaseId), "2");
						subjectStore = (SubjectStore) getBaseDao().getByPk(SubjectStore.class, Long.decode(zreleaseId));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(release != null && subjectStore != null){
						//更新主题库状态为已发布；
						subjectStore.setStatus(ResReleaseConstant.OrderStatus.ORDERADD);
						getBaseDao().saveOrUpdate(subjectStore);
						//更新发布表状态为已发布
						release.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHED);
						getBaseDao().saveOrUpdate(release);
						//失败单据集合
						Object arrays =  jsonObject.get("resList");
						if(arrays != null){
							JSONArray array =  jsonObject.getJSONArray("resList");
							for (int j = 0; j < array.size(); j++) {
								JSONObject objects = array.getJSONObject(j);
								String objectId = objects.getString("objectId");
								String msg = objects.getString("msg");
								if(StringUtils.isNotBlank(objectId)){
									ResReleaseDetail resReleaseDetail = null;
									try {
										resReleaseDetail = getResReleaseDetailByRelIdAndResId(release.getId(), objectId);
									} catch (Exception e) {
										e.printStackTrace();
									}
									//更新发布明细表状态为发布失败
									resReleaseDetail.setStatus(ResReleaseConstant.PublishingStatus.FAIL_PUBLISH);
									resReleaseDetail.setMsg(msg);
									getBaseDao().saveOrUpdate(resReleaseDetail);
								}
							}
						}
						String hql="from ResReleaseDetail r where r.releaseId=" + release.getId() + " and r.status != "+ResReleaseConstant.PublishingStatus.FAIL_PUBLISH;
						List<ResReleaseDetail> detailslist = getBaseDao().query(hql);
						for (ResReleaseDetail resReleaseDetail : detailslist) {
							//更改发布明细表状态为发布成功
							resReleaseDetail.setStatus(ResReleaseConstant.PublishingStatus.SUCCESS_PUBLISH);
							getBaseDao().saveOrUpdate(resReleaseDetail);
						}
					}
				}else if(StringUtils.isNotBlank(xreleaseId)){
					ResRelease release = null;
					ResOrder resOrder =null;
					try {
						release =getResRelease(Long.parseLong(xreleaseId), "1");
						resOrder = (ResOrder) getBaseDao().getByPk(ResOrder.class, Long.decode(xreleaseId));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(release != null && resOrder != null){
						//更新主题库状态为已发布；
						resOrder.setStatus(ResReleaseConstant.OrderStatus.ORDERADD);
						getBaseDao().saveOrUpdate(resOrder);
						//更新发布表状态为已发布
						release.setStatus(ResReleaseConstant.ResReleaseStatus.PUBLISHED);
						getBaseDao().saveOrUpdate(release);
						//失败单据集合
						Object arrays =  jsonObject.get("resList");
						if(arrays != null){
							JSONArray array =  jsonObject.getJSONArray("resList");
							for (int j = 0; j < array.size(); j++) {
								JSONObject objects = array.getJSONObject(j);
								String objectId = objects.getString("objectId");
								String msg = objects.getString("msg");
								if(StringUtils.isNotBlank(objectId)){
									ResReleaseDetail resReleaseDetail = null;
									try {
										resReleaseDetail = getResReleaseDetailByRelIdAndResId(release.getId(), objectId);
									} catch (Exception e) {
										e.printStackTrace();
									}
									//更新发布明细表状态为发布失败
									resReleaseDetail.setStatus(ResReleaseConstant.PublishingStatus.FAIL_PUBLISH);
									resReleaseDetail.setMsg(msg);
									getBaseDao().saveOrUpdate(resReleaseDetail);
								}
							}
						}
						String hql="from ResReleaseDetail r where r.releaseId=" + release.getId() + " and r.status != "+ResReleaseConstant.PublishingStatus.FAIL_PUBLISH;
						List<ResReleaseDetail> detailslist = getBaseDao().query(hql);
						for (ResReleaseDetail resReleaseDetail : detailslist) {
							//更改发布明细表状态为发布成功
							resReleaseDetail.setStatus(ResReleaseConstant.PublishingStatus.SUCCESS_PUBLISH);
							getBaseDao().saveOrUpdate(resReleaseDetail);
						}
					}
					
				}
		}
		return "OK";
	}
	@Override
	public String getMaterialRes(String startTime, String endTime) throws Exception{
		String souce="";
		String sql="";
		if(StringUtils.isBlank(startTime)){
			startTime="";
		}else{
			startTime = com.brainsoon.bsrcm.search.util.DateUtil.parseTimes(startTime).getTime()+"";
		}
		if(StringUtils.isBlank(endTime)){
			endTime="";
		}else{
			endTime = com.brainsoon.bsrcm.search.util.DateUtil.parseTimes(endTime).getTime()+"";
		}
		List<MetaDataModelGroup> dataModelGroups = getBaseDao().query("from MetaDataModelGroup t where t.typeName='素材资源'");
		String id = "";
		if(dataModelGroups.size()>0 && !dataModelGroups.isEmpty()){
			for (MetaDataModelGroup metaDataModelGroup : dataModelGroups) {
				id = metaDataModelGroup.getId().toString();
			}
		}
		if(StringUtils.isNotBlank(id)){
			sql = PUBLISH_SCQUERYBYTIME_URL+"?startTime="+startTime+"&endTime="+endTime+"&resType="+id;
		}else{
			throw new ServiceException("元数据【素材资源】未配置！");
		}
		HttpClientUtil http = new HttpClientUtil();
		souce = http.executeGet(sql);
		return souce;
	}
}
