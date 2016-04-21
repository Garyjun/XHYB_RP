package com.brainsoon.resource.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.aspectj.util.FileUtil;
import org.dom4j.Document;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.util.DoiGenerateUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.UID;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.view.CatalogDTO;
import com.brainsoon.common.util.dofile.zip.ZipDecryptionUtil;
import com.brainsoon.common.util.dofile.zip.ZipOrRarUtil;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.jbpm.constants.ProcessConstants;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.po.FileDownName;
import com.brainsoon.resource.po.FileDownValue;
import com.brainsoon.resource.po.ModifyLog;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.DeleteFileTaskQueue;
import com.brainsoon.resource.support.DeleteResFileForCaIds;
import com.brainsoon.resource.support.FtpCopyFileThread;
import com.brainsoon.resource.support.ResourceTypeUtils;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resource.util.CopyUtil;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.DoFileHistory;
import com.brainsoon.semantic.ontology.model.DoFileQueue;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;
import com.brainsoon.semantic.ontology.model.Organization;
import com.brainsoon.semantic.ontology.model.OrganizationItem;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResult;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.ResTargetData;
import com.brainsoon.system.model.RunNumber;
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.model.User;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.ICompanyService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.IStaffService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.service.IUserService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.brainsoon.system.support.SystemConstants.ResourceStatus;
import com.brainsoon.system.util.MetadataSupport;
import com.brainsoon.taskprocess.service.ITaskProcessService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Service
public class PublishResService extends BaseService implements
		IPublishResService {
	private final static String FTP_LOCAL_MAPPING = WebappConfigUtil
			.getParameter("FTP_LOCAL_MAPPING");
	private static final String PUBLISH_SAVE_URL = WebappConfigUtil
			.getParameter("PUBLISH_SAVE_URL");
	private static final String PUBLISH_FILESAVE_URL = WebappConfigUtil
			.getParameter("PUBLISH_FILESAVE_URL");
	private static final String PUBLISH_OVERRIDE_URL = WebappConfigUtil
			.getParameter("PUBLISH_OVERRIDE_URL");
	private static final String CA_OVERRIDE_URL = WebappConfigUtil
			.getParameter("CA_OVERRIDE_URL");
	private static final String CA_SAVE_NODE_URL = WebappConfigUtil
			.getParameter("CA_SAVE_NODE_URL");
	private static final String CA_DEL_NODE_URL = WebappConfigUtil
			.getParameter("CA_DEL_NODE_URL");
	private static final String PUBLISH_RENAME_FILE = WebappConfigUtil
			.getParameter("PUBLISH_RENAME_FILE");
	public static final String FILE_ROOT = StringUtils.replace(
			WebAppUtils.getWebAppBaseFileDirFR(), "\\", "/");
	public static final String CONVER_FILE_ROOT = StringUtils.replace(
			WebAppUtils.getWebAppBaseFileDirCFR(), "\\", "/");
	private final static String PUBLISH_REPEAT_URL = WebappConfigUtil
			.getParameter("PUBLISH_REPEAT_URL");
	private final static String PUBLISH_FILE_WRITE_QUEUE = WebappConfigUtil
			.getParameter("PUBLISH_FILE_WRITE_QUEUE");
	private static final String COPYRIGHT_ISHAVE_URL = WebappConfigUtil
			.getParameter("COPYRIGHT_ISHAVE_URL");
	private final static String PUBLISH_QUERYBYPOST_URL = WebappConfigUtil
			.getParameter("PUBLISH_QUERYBYPOST_URL");
	public final static String FILE_TEMP = WebAppUtils
			.getWebRootBaseDir(ConstantsDef.fileTemp);
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss:SSS";
	private final static SimpleDateFormat dateformat2 = new SimpleDateFormat(
			"yyyyMMddHHmmssSSS");
	private final static String CA_FILERES_SAVE_URL = WebappConfigUtil
			.getParameter("CA_FILERES_SAVE_URL");
	private static final String CA_FILES_BY_PUBLISHTYP = WebappConfigUtil.getParameter("CA_FILES_BY_PUBLISHTYP");
	/*
	 * //处理中 private final static int zuntreated1 = 1; //待处理 private final
	 * static int zprocess2 = 2; private final static int zpsuccess3 = 3;
	 * private final static int zsuccess4 = 4; private final static int zfail5 =
	 * 5;
	 */
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IResConverfileTaskService resConverfileTaskService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private IBookService bookService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private ISysParameterService sysParameterService;
	@Autowired
	private ITaskProcessService taskProcessService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private IJbpmExcutionService jbpmExcutionService;
	@Resource
	@Qualifier("batchImportResService")
	private IBatchImportResService batchImportResService;
	@Autowired
	private IFLTXService fLTXService;
	@Autowired
	private ICompanyService companyService;
	@Autowired
	private IStaffService staffService;
	@Autowired 
	private IUserService userService;
	public Ca getCaById(String objectId) {
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(WebappConfigUtil
				.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
		Gson gson = new Gson();
		Ca ca = gson.fromJson(resourceDetail, Ca.class);
		if (ca != null && ca.getCommonMetaData() != null) {
			return ca;
		} else {
			return null;
		}
	}

	/**
	 * 批量批量导入详细列表分页查询
	 * 
	 * @param poClass
	 * @param conditionList
	 * @return PageResult
	 */
	public PageResult queryBeachImportDetaill(Class poClass,
			QueryConditionList conditionList) {
		StringBuffer hql = new StringBuffer();

		Map<String, Object> params = null;

		params = parseConditions(poClass, conditionList, hql, params);
		return baseDao.queryBeachImportDetaillPage(hql.toString(),
				conditionList.getStartIndex(), conditionList.getPageSize(),
				params);
	}

	/**
	 * 公共方法
	 * 
	 * @param response
	 * @param ids
	 * @throws IOException
	 */
	public ModelMap jsonArray(Ca bookCa, String objectId, ModelMap model) {
		Long userid  =  LoginUserUtil.getLoginUser().getUserId();
		User user = (User) userService.getByPk(User.class, userid);
		String resourceDataJson = user.getResourceDataJson();
		JSONArray jsonArray = JSONArray.fromObject(resourceDataJson);
		String fields = "";
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if(jsonObject.getString("id").equals(bookCa.getPublishType())){
				fields = jsonObject.getString("field");
			}
		}
		if (bookCa.getRealFiles() != null) {
			List<com.brainsoon.semantic.ontology.model.File> realFiles = bookCa
					.getRealFiles();
			if (realFiles != null && realFiles.size() > 0) {
				JSONObject ztreeObj = new JSONObject();
				JSONArray ztreeArray = new JSONArray();
				String rootpath = "";
				String fieldpath = "";
				for (int u = 0; u < realFiles.size(); u++) {
					com.brainsoon.semantic.ontology.model.File file = realFiles
							.get(u);
					if(file.getPid().equals("-1")){
						rootpath = file.getId();
						if (file.getId() != null) {
							String nodeId = file.getId();
							nodeId = nodeId.replaceAll("\\\\", "/");
							nodeId = replace(nodeId);
							ztreeObj.put("nodeId", nodeId);
						}
						if (file.getPid() != null) {
							String pid = file.getPid();
							pid = pid.replaceAll("\\\\", "/");
							pid = replace(pid);
							ztreeObj.put("pid", pid);
						}
						if (file.getName() != null) {
							String fileName = file.getName();
							fileName = replace(fileName);
							ztreeObj.put("name", fileName);
						}
						if (file.getPath() != null) {
							String path = file.getPath();
							path = path.replaceAll("\\\\", "/");
							path = replace(path);
							ztreeObj.put("path", path);
						}
						if (file.getObjectId() != null) {
							String object = file.getObjectId();
							object = replace(object);
							ztreeObj.put("object", object);
						}
						if (file.getIsDir() != null) {
							String isDir = file.getIsDir();
							isDir = replace(isDir);
							ztreeObj.put("isDir", isDir);
						}
						if (file.getMd5() != null) {
							String md5 = file.getMd5();
							md5 = replace(md5);
							ztreeObj.put("Md5", md5);
						}
						ztreeArray.add(ztreeObj);
					}else if(file.getPid().equals(rootpath)){
						String fieldss[]=fields.split(",");
						for (String field : fieldss) {
							if(file.getId().toLowerCase().contains("/"+field.trim().toLowerCase())){
								fieldpath = file.getId();
								if (file.getId() != null) {
									String nodeId = file.getId();
									nodeId = nodeId.replaceAll("\\\\", "/");
									nodeId = replace(nodeId);
									ztreeObj.put("nodeId", nodeId);
								}
								if (file.getPid() != null) {
									String pid = file.getPid();
									pid = pid.replaceAll("\\\\", "/");
									pid = replace(pid);
									ztreeObj.put("pid", pid);
								}
								if (file.getName() != null) {
									String fileName = file.getName();
									fileName = replace(fileName);
									ztreeObj.put("name", fileName);
								}
								if (file.getPath() != null) {
									String path = file.getPath();
									path = path.replaceAll("\\\\", "/");
									path = replace(path);
									ztreeObj.put("path", path);
								}
								if (file.getObjectId() != null) {
									String object = file.getObjectId();
									object = replace(object);
									ztreeObj.put("object", object);
								}
								if (file.getIsDir() != null) {
									String isDir = file.getIsDir();
									isDir = replace(isDir);
									ztreeObj.put("isDir", isDir);
								}
								if (file.getMd5() != null) {
									String md5 = file.getMd5();
									md5 = replace(md5);
									ztreeObj.put("Md5", md5);
								}
								ztreeArray.add(ztreeObj);
							}
						}
					}else if(file.getPid().indexOf(fieldpath)==0){
						if (file.getId() != null) {
							String nodeId = file.getId();
							nodeId = nodeId.replaceAll("\\\\", "/");
							nodeId = replace(nodeId);
							ztreeObj.put("nodeId", nodeId);
						}
						if (file.getPid() != null) {
							String pid = file.getPid();
							pid = pid.replaceAll("\\\\", "/");
							pid = replace(pid);
							ztreeObj.put("pid", pid);
						}
						if (file.getName() != null) {
							String fileName = file.getName();
							fileName = replace(fileName);
							ztreeObj.put("name", fileName);
						}
						if (file.getPath() != null) {
							String path = file.getPath();
							path = path.replaceAll("\\\\", "/");
							path = replace(path);
							ztreeObj.put("path", path);
						}
						if (file.getObjectId() != null) {
							String object = file.getObjectId();
							object = replace(object);
							ztreeObj.put("object", object);
						}
						if (file.getIsDir() != null) {
							String isDir = file.getIsDir();
							isDir = replace(isDir);
							ztreeObj.put("isDir", isDir);
						}
						if (file.getMd5() != null) {
							String md5 = file.getMd5();
							md5 = replace(md5);
							ztreeObj.put("Md5", md5);
						}
						ztreeArray.add(ztreeObj);
					}
				}
				ztreeArray.toString();
				if (StringUtils.isBlank(resourceDataJson) || resourceDataJson==null || "null".equals(resourceDataJson)) {
					model.addAttribute("ztreeJson", "");
				}else {
					model.addAttribute("ztreeJson", ztreeArray);
				}
				//model.addAttribute("ztreeJson", ztreeArray);
			}
			List<ModifyLog> modifyLogs = queryModifyLogsByResId(objectId);
			if (modifyLogs != null && modifyLogs.size() > 0) {
				JSONObject obj = new JSONObject();
				JSONArray array = new JSONArray();
				for (int i = 0; i < modifyLogs.size(); i++) {
					String modify_old = modifyLogs.get(i).getModifyOld();
					String modify_new = modifyLogs.get(i).getModifyNew();
					String field = modifyLogs.get(i).getModifyField();
					obj.put("modify_old", modify_old);
					obj.put("modify_new", modify_new);
					obj.put("field", field);
					array.add(obj);
				}
				array.toString();
				model.addAttribute("modifyLogsArray", array);
			}
		}
		return model;
	}
	/**
	 * 将json串中的特殊字符替换成空
	 */
	public String replace(String name){
		if(name.contains("'")){
			name = name.replaceAll("'", "’");
		}
		return name;
	}

	/**
	 * 创建组合资源
	 * 
	 * @param ca
	 * @param jsonTree
	 * @param nodeAsset
	 * @param ogId
	 * @throws Exception
	 */
	public void saveCollectRes(Ca ca, String jsonTree, String nodeAsset,
			String ogId, String thumbFile) throws Exception {
		logger.debug("******run at saveRes***********");
		logger.debug("jsonTree ");
		logger.debug(jsonTree);
		ca.setType("1");
		if (ca.getObjectId().equals("-1")) {
			ca.setObjectId("");
		}
		Map<String, String> nodeAssetMap = new HashMap<String, String>();
		if (nodeAsset.length() > 1) {
			nodeAsset = nodeAsset.substring(0, nodeAsset.length() - 1);
			String[] array = nodeAsset.split(";");
			for (int i = 0; i < array.length; i++) {
				String[] tmp = array[i].split(",");
				nodeAssetMap.put(tmp[0], tmp[1]);
			}
		}

		Gson gson = new Gson();
		List<OrganizationItem> items = gson.fromJson(jsonTree,
				new TypeToken<List<OrganizationItem>>() {
				}.getType());
		List<OrganizationItem> endItems = new ArrayList<OrganizationItem>();
		Gson gsonFormat = new GsonBuilder().setPrettyPrinting().create();
		String formateJson = gsonFormat.toJson(ca);
		logger.debug(formateJson);
		Asset asset = new Asset();
		CommonMetaData commonMetaData = ca.getCommonMetaData();
		asset.setCommonMetaData(commonMetaData);
//		asset.setExtendMetaData(ca.getExtendMetaData());
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		commonMetaData.setIdentifier(doi);
		commonMetaData.setStatus(ResourceStatus.STATUS0);
		commonMetaData.setModified_time(DateUtil.convertDateToString(
				dateFormat, new Date()));
		if (ca.getObjectId() == null || "".equals(ca.getObjectId())) {
			String module = commonMetaData.getModule();
			String type = commonMetaData.getType();
			// 按要求生成目录
			String parentPath = resourceService.createParentPath(module, type,
					doi);
			// 创建封面
			String relatePath = createThumb(parentPath, thumbFile);
			commonMetaData.setPath(relatePath);
		} else {
			if (thumbFile != null && !"".equals(thumbFile)) {
				String module = commonMetaData.getModule();
				String type = commonMetaData.getType();
				// 按要求生成目录
				String parentPath = resourceService.createParentPath(module,
						type, doi);
				File parentPathDir = new File(parentPath);
				if (parentPathDir.exists() && parentPathDir.isDirectory()) {
					File[] files = parentPathDir.listFiles();
					if (files != null && files.length > 0) {
						for (File file : files) {
							file.delete();
						}
						String relatePath = createThumb(parentPath, thumbFile);
						commonMetaData.setPath(relatePath);
					}
				}
			}
		}
		ca.setCommonMetaData(commonMetaData);
		String paraJson = gson.toJson(ca);
		logger.debug(paraJson);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(PUBLISH_SAVE_URL, paraJson);
		logger.debug("result *** " + result);
	}

	@SuppressWarnings("finally")
	public String downloadBookRes(HttpServletRequest request,
			String encryptZip, String objectIds, String encryptPwd,
			String ftpFlag,String isComplete) {
		String uuidPath = encryptZip;
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		encryptZip = BresAction.FILE_DOWN + uuidPath + "/";
		String title = "资源";
		String isOk = "";
		int fileNum = 0;
		Long fileSize = 0L;
		Long totalFileSize = 0L;
		//创建临时目录
		String parentPath = BresAction.FILE_TEMP+UUID.randomUUID()+"/";
		FileDownName fileTask = new FileDownName();
		FileDownValue fileValue = null;
		fileTask.setStatus("3");
		fileTask.setFtpPath(uuidPath);
		fileTask.setPwd(encryptPwd);
		fileTask.setCreateTime(new Date());
		fileTask.setDownloadType("HTTP");
		fileTask.setIsComplete(isComplete);
		fileTask.setDownloadUser(userInfo.getUsername());
		fileTask.setLoginUser(userInfo.getName());
		String resName = uuidPath;
		if(uuidPath.endsWith("/")){
			resName = uuidPath.substring(0,uuidPath.length()-1);
		}
		fileTask.setResName(resName);
		resourceService.create(fileTask);
		if (StringUtils.isNotBlank(objectIds)) {
			String[] ids = StringUtils.split(objectIds, ",");
			Gson gson = new Gson();
			for (String objectId : ids) {
				fileSize = 0L;
				fileValue = new FileDownValue();
				fileValue.setResId(objectId);
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail = "";
				Ca ca = null;
				try {
					resourceDetail = http
							.executeGet(WebappConfigUtil
									.getParameter("PUBLISH_DETAIL_URL")
									+ "?id="
									+ objectId);
					ca = gson.fromJson(resourceDetail, Ca.class);
					title = MetadataSupport.getTitle(ca);
				} catch (Exception e) {
					fileValue.setResName("连接中断");
					fileValue.setStatus("1");
					fileValue.setTask(fileTask);
					fileValue.setFtpPath(uuidPath);
					fileValue.setCreateTime(new Date());
					fileValue.setPwd(encryptPwd);
					resourceService.create(fileValue);
					continue;
				}
				SysOperateLogUtils.addLog("res_file_down", title,
						LoginUserUtil.getLoginUser());
				fileValue.setResName(title);
				fileValue.setStatus("3");
				fileValue.setTask(fileTask);
				fileValue.setFtpPath(uuidPath);
				fileValue.setCreateTime(new Date());
				fileValue.setPwd(encryptPwd);
				resourceService.create(fileValue);
				int realFileSize = 0;
				if (ca.getRealFiles() != null) {
					realFileSize = ca.getRealFiles().size();
					fileNum = fileNum + realFileSize;
					for (int j = 0; j < realFileSize; j++) {
						if (ca.getRealFiles().get(j).getFileByte() != null
								&& ca.getRealFiles().get(j).getIsDir()
										.equals("2")) {
							totalFileSize = totalFileSize
									+ Long.parseLong(ca.getRealFiles().get(j)
											.getFileByte());
							fileSize = fileSize
									+ Long.parseLong(ca.getRealFiles().get(j)
											.getFileByte());
						}
					}
					String rootPath = ca.getRootPath();
					rootPath = rootPath.replaceAll("\\\\", "/");
					createRandomDir(parentPath);
					String path = StringUtils.replace(
							WebAppUtils.getWebAppBaseFileDirFR(), "\\\\", "/")
							+ rootPath;
					try {
						//拷贝到临时目录
						FileUtil.copyDir(new File(path), new File(parentPath));
					} catch (IOException e) {
						e.printStackTrace();
					}
					fileValue.setTotalFileSize(fileSize + "");
					fileTask.setStatus("2");
					fileTask.setFileSize(totalFileSize + "");
					resourceService.update(fileTask);
					resourceService.update(fileValue);
				} else {
					fileValue.setTotalFileSize(fileSize + "");
					fileValue.setStatus("1");
					fileValue.setFtpPath("文件为空");
					fileValue.setResName(title);
					fileTask.setFileSize("0");
					fileTask.setStatus("1");
					fileTask.setCreateTime(new Date());
					sysParameterService.update(fileTask);
					sysParameterService.create(fileValue);
//					emptyFlag = emptyFlag + "【" + title + "】，";
					continue;
				}
			}
			fileTask.setFileSize(totalFileSize + "");
			resourceService.update(fileTask);
			String zipName = encryptZip;
			zipName = zipName.replaceAll("\\\\", "/");
			File zipDir = new File(zipName);
			if (!zipDir.exists()) {
				zipDir.mkdirs();
			}
			if (ids.length > 1) {
				// 压缩parent目录
				zipName += "资源包.zip";
			} else {
				zipName += title + ".zip";
			}
			try {
				if (StringUtils.isNotBlank(encryptPwd)) {
//					encryptZip += dateformat2.format(new Date()) + ".zip";
					// 不支持汉字
					ZipDecryptionUtil.encryptZipFile(parentPath, zipName,
							encryptPwd, false, false);
					fileTask.setFtpPath(uuidPath);
					fileTask.setStatus("1");
					fileValue.setStatus("1");
					fileValue.setCreateTime(new Date());
					String hql = "update FileDownValue set status=1 where task.id="
							+ fileTask.getId();
					resourceService.update(fileTask);
					baseDao.updateWithHql(hql);
					isOk = zipName;
				} else {
					ZipUtil.zipFileOrFolder(parentPath, zipName, null);
					fileTask.setFtpPath(uuidPath);
					fileTask.setStatus("1");
					fileValue.setStatus("1");
					fileValue.setCreateTime(new Date());
					String hql = "update FileDownValue set status=1 where task.id="
							+ fileTask.getId();
					resourceService.update(fileTask);
					baseDao.updateWithHql(hql);
					isOk = zipName;
				}
			} catch (Exception e) {
				logger.error("压缩出现问题" + e.getMessage());
				isOk = "压缩出现问题" + e.getMessage();
			} finally {
				return isOk;
			}
		}
		return isOk;
	}

	public void changeFileName(File oldFilePath, Map<String, String> mapName)
			throws Exception {
		String oldName = "";
		String oldPath = "";
		String newName = "";
		String newPath = "";
		if (oldFilePath.isDirectory()) {
			File[] files = oldFilePath.listFiles();
			if (files != null && files.length > 0) {
				for (File file : files) {
					if (!file.isDirectory()) {// 如果是文件，直接更名
						oldName = file.getName();
						oldPath = file.getPath();
						if (mapName.get(oldName) != null) {
							newName = mapName.get(oldName);
						} else {
							break;
						}
						oldPath = oldPath.replaceAll("\\\\", "/");
						newPath = oldPath.substring(0,
								oldPath.lastIndexOf("/") + 1) + newName;
						file.renameTo(new File(newPath));
					} else {// 如果是文件夹，
						oldName = file.getName();
						oldPath = file.getPath();
						if (mapName.get(oldName) != null) {
							newName = mapName.get(oldName);
						} else {
							break;
						}
						oldPath = oldPath.replaceAll("\\\\", "/");
						newPath = oldPath.substring(0,
								oldPath.lastIndexOf("/") + 1) + newName;
						changeFileName(file, mapName);// 递归
						file.renameTo(new File(newPath));
						// 循环完后，把该目录更名。
					}
				}
			}
		} else {
			oldName = oldFilePath.getName();
			oldPath = oldFilePath.getPath();
			newName = mapName.get(oldName);
			oldPath = oldPath.replaceAll("\\\\", "/");
			newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1)
					+ newName;
			oldFilePath.renameTo(new File(newPath));
		}
	}

	public List<CatalogDTO> getResDirAndFile(String id) {
		List<CatalogDTO> catalogDTOs = new ArrayList<CatalogDTO>();
		if (StringUtils.isNotBlank(id)) {
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("CA_DETAIL_URL") + "?id=" + id);
			Gson gson = new Gson();
			Ca ca = gson.fromJson(resourceDetail, Ca.class);
			// List<Organization> organizations= ca.getOrganizations();
			// for (Organization organization : organizations) {
			// List<OrganizationItem> organizationItems =
			// organization.getOrganizationItems();
			// for(OrganizationItem organizationItem:organizationItems){
			// CatalogDTO catalogDTO= new
			// CatalogDTO(organizationItem.getNodeId(),organizationItem.getPid(),organizationItem.getName(),organizationItem.getPath());
			// catalogDTOs.add(catalogDTO);
			// }
			// }
		}
		return catalogDTOs;
	}

	/**
	 * 创建出版图书
	 * 
	 * @param ca
	 * @param uploadFile
	 * @throws Exception
	 */
	public String savePublishRes(Ca ca, String uploadFile, String repeatType,String publishType, String targetNames) throws Exception {
		logger.debug("***---------------------------***run at savePublishRes*****---------------------******");
		boolean isModify = false;
		String logDesc = "publish_update";
		Date now = new Date();
		String time = now.getTime() + "";
		String targetOldTotal[] = null;
		boolean modiflyTarget = false;
		String agoTargets = "";
		String importCoverType="1";//导入封面类型 1：在cover目录下有cover.jpg 2:有cover目录，没有cover.jpg 3：没有cover目录，在根目录有cover.jpg 4：没有cover目录，也没有cover.jpg
		// File srcFile = new File(uploadFile);
		if (!"".equals(targetNames)) {
			targetOldTotal = targetNames.split(",");
		}
		Long userId = LoginUserUtil.getLoginUser().getUserId();
		if (ca.getObjectId() == null || ca.getObjectId().equals("-1") || "".equals(ca.getObjectId())) {
			ca.setObjectId("");
			logDesc = "publish_create";
			ca.setCreator(userId + "");
			ca.setCreateTime(time);
			// ca.setUpdater(userId+"");
			ca.setUpdateTime(time);
			ca.setPublishType(publishType);
		} else if (repeatType.equals("2") && ca.getObjectId() != null) {
			ca.setCreator(userId + "");
			ca.setCreateTime(time);
			ca.setPublishType(publishType);
			isModify = true;
		} else {
			ca.setUpdater(userId + "");
			ca.setUpdateTime(time);
			isModify = true;
		}
		String doi = "";
		String oldDoi = "";
		String autoComple = "";
		logger.info("=======================ca.publishtype===" + ca.getPublishType() + "================");
		doi = DoiGenerateUtil.generateDoiByResDirectory(ca);
		logger.info("====================生成doi===" + doi + "====================");
		String targetField = "";
		String doiField = "";
		List<String> listDate = new ArrayList<String>();
		List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(publishType);
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
//			logger.info("=========== ======无数据字段===" + metadataDefinition.getFieldZhName() + "==================");
			if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 10) {
				targetField = metadataDefinition.getFieldName();
			}
//			logger.info("【PublishResService】保存资源savePublishRes->>>根据标识符10获取标签："+targetField);
			if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 5) {
				doiField = metadataDefinition.getFieldName();
			}
//			logger.info("【PublishResService】保存资源savePublishRes->>>根据标识符5获取DOI："+doiField);
			if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 13) {//导入封面类型
				importCoverType = metadataDefinition.getDefaultValue();
				if (ca.getMetadataMap().get("importCoverType") != null) {//如果用户填了该字段 就取用户获取的值
					importCoverType = ca.getMetadataMap().get("importCoverType");
				}else {//没有填该字段，就取默认值
					ca.getMetadataMap().put("importCoverType", importCoverType);
				}
			}
			//存储日期类型field集合
			if (metadataDefinition.getIdentifier() != null && metadataDefinition.getFieldType() == 7) {
				listDate.add(metadataDefinition.getFieldName());
			}
//			logger.info("【PublishResService】保存资源savePublishRes->>>根据标识符13获取importCoverType："+importCoverType);
			if (metadataDefinition.getOpenAutoComple() != null && metadataDefinition.getOpenAutoComple().equals("true")) {
				autoComple = autoComple + metadataDefinition.getOpenAutoComple() + ",";
			}
			// if(metadataDefinition.getFieldType() != null&&
			// metadataDefinition.getFieldType()==7){
			// mapList.add(metadataDefinition.getFieldName());
			// }
		}
		// Map<String, String> metadataMapDate = ca.getMetadataMap();
		// if(metadataMapDate!=null&& metadataMapDate.size()>0 && mapList!=null
		// && mapList.size()>0){
		// ResUtils.mapDate(metadataMapDate, mapList);
		// }
		logger.info("===============================doi字段===" + doiField + "============================");
		logger.info("===============================标签字段===" + targetField + "============================");
		String parentPath = "";
		// 按要求生成目录
		if (ca.getObjectId() == null || "".equals(ca.getObjectId()) || "-1".equals(ca.getObjectId())) {
			parentPath = resourceService.createPublishParentPath(ca.getPublishType(), "");
			ca.setRootPath(parentPath.replace(FILE_ROOT, ""));
		}
		if ("".equals(ca.getStatus())) {
			ca.setStatus(ResourceStatus.STATUS0);
		}
		if ("2".equals(repeatType)) {
			int i = 1;
			String pid = "-1";
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + ca.getObjectId());
			Gson gson = new Gson();
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if(oldCa.getMetadataMap().get(targetField)!=null){
				modiflyTarget = true;
				agoTargets = oldCa.getMetadataMap().get(targetField);
			}
			Map<String, String> metadataMap = ca.getMetadataMap();
			Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
			for (Map.Entry<String, String> newEntry : metadataMap.entrySet()) {
				boolean valFlag = false;
				String newKey = newEntry.getKey();
				String newValue = newEntry.getValue();
				for (Map.Entry<String, String> oldEntry : oldMetadataMap .entrySet()) {
					String oldKey = oldEntry.getKey();
					String oldValue = oldEntry.getValue();
					if (newKey.equals(oldKey) && StringUtils.isNotBlank(oldValue)) {
						valFlag = true;
						break;
					}
				}
				if (!valFlag && StringUtils.isNotBlank(newValue)) {
					oldMetadataMap.put(newEntry.getKey(), newValue);
				}
			}
			// oldCa.setMetadataMap(oldMetadataMap);
			oldCa.setMetadataMap(oldCa.getMetadataMap());
			oldDoi = oldCa.getMetadataMap().get(doiField);
			doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
			if (StringUtils.isNotBlank(oldDoi)) {
				String doiLastNum = oldDoi.substring(oldDoi.length() - 8);
				// doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
				if (StringUtils.isNotBlank(doi)) {
					String newDoi = doi.substring(0, doi.length() - 8);
					newDoi = newDoi + doiLastNum;
					oldCa.getMetadataMap().put(doiField, newDoi);
				} else {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			} else {
				if (StringUtils.isNotBlank(doi)) {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			}
			if (StringUtils.isNotBlank(uploadFile)) {
				logger.info("===--------------------=文件递归改名===" + parentPath + "============");
				parentPath = FILE_ROOT + oldCa.getRootPath();
				if (oldCa.getRealFiles() != null) {
					List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa.getRealFiles();
					Map<String, String> dirMap = new TreeMap<String, String>();
					Map<String, List<String>> md5Map = new HashMap<String, List<String>>();
					for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
						String isDir = oldFile.getIsDir();
						if ("1".equals(isDir)) {
							String id = oldFile.getId();
							id = id.replaceAll("\\\\", "/");
							if (id.endsWith("/")) {
								id = id.substring(0, id.length() - 1);
							}
							logger.info(id + "==========" + oldFile.getName());
							dirMap.put(id, oldFile.getName());
						}
					}
					Set<String> set = new HashSet<String>();
					for (String key : dirMap.keySet()) {
						String value = dirMap.get(key);
						if (key.indexOf("/") > 0) {
							String name = dirMap.get(key);
							String lastDir = key.substring(0, key.lastIndexOf("/"));
							String lastName = dirMap.get(lastDir);
							if (StringUtils.isNotBlank(lastName)) {
								dirMap.put(key, lastName + "/" + name);
								set.add(lastName + "/" + name);
							}
						} else {
							set.add(value);
						}
					}
					for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
						String isDir = oldFile.getIsDir();
						if ("2".equals(isDir)) {
							String tempPid = oldFile.getPid();
							List<String> md5List = null;
							if ("-1".equals(tempPid)) {
								md5List = md5Map.get(tempPid);
								if (md5List == null) {
									md5List = new ArrayList<>();
								}
								md5List.add(oldFile.getMd5());
								md5Map.put("-1", md5List);
							} else {
								tempPid = tempPid.replaceAll("\\\\", "/");
								if (tempPid.endsWith("/")) {
									tempPid = tempPid.substring(0,
											tempPid.length() - 1);
								}
								md5List = md5Map.get(dirMap.get(tempPid));
								if (md5List == null) {
									md5List = new ArrayList<>();
								}
								md5List.add(oldFile.getMd5());
								md5Map.put(dirMap.get(tempPid), md5List);
							}
						}
					}
					for (Map.Entry<String, String> entry : dirMap.entrySet()) {
						logger.info("key= " + entry.getKey() + " and value= " + entry.getValue());
					}
					String outPutPath = FILE_ROOT + oldCa.getRootPath() + UUID.randomUUID();
					uploadFile = BresAction.FILE_TEMP + uploadFile;
					String fileType = uploadFile.substring(uploadFile.lastIndexOf(".") + 1,uploadFile.length());
					ZipOrRarUtil.unzip(uploadFile, outPutPath, fileType);
					File outFile = new File(outPutPath);
					int num = 0;
					logger.info("===--------------------=文件递归改名==="
							+ parentPath + "============");
					String fileDoi = "";
					if (oldCa.getMetadataMap() != null && oldCa.getMetadataMap().get(doiField) != null) {
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid,oldCa, i, set, md5Map, dirMap, srcPath, num, fileDoi);
					oldCa = ResUtils.getOverFileLists(parentPath, outFile, pid,oldCa, set, md5Map, "", fileDoi);//修改 不改名 hunagjun 2015-11-11 09:37:19
					logger.info("=--------------------===文件递归改名结束===" + parentPath + "============");
					ca = oldCa;
				} else {
					int j = 1;
					String outPutPath = FILE_ROOT + oldCa.getRootPath() + UUID.randomUUID();
					uploadFile = BresAction.FILE_TEMP + uploadFile;
					String fileType = uploadFile.substring(uploadFile.lastIndexOf(".") + 1,uploadFile.length());
					ZipOrRarUtil.unzip(uploadFile, outPutPath, fileType);
					File file = new File(outPutPath);
					int num = 0;
					String fileDoi = "";
					if (oldCa.getMetadataMap() != null && oldCa.getMetadataMap().get(doiField) != null) {
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//oldCa = ResUtils.getFileLists(parentPath, file, pid, oldCa,i, j, num, fileDoi, false);
					oldCa = ResUtils.getFileLists(parentPath, file, pid, oldCa, fileDoi, false);
					//TODO 不用改名 huangjun 2015-11-9 18:35:58
					ca = oldCa;
				}
			} else {
				ca = oldCa;
			}

		} else if ("4".equals(repeatType)) {
			int i = 1;
			String pid = "-1";
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+ "?id="+ ca.getObjectId());
			Gson gson = new Gson();
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if(oldCa.getMetadataMap().get(targetField)!=null){
				modiflyTarget = true;
				agoTargets = oldCa.getMetadataMap().get(targetField);
			}
			Map<String, String> newMetadataMap = ca.getMetadataMap();
			Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
			for (Map.Entry<String, String> oldEntry : oldMetadataMap.entrySet()) {
				String oldKey = oldEntry.getKey();
				if (newMetadataMap.get(oldKey) == null) {
					if (oldKey.equals(doiField)
							&& oldMetadataMap.get(oldKey) != null) {
						newMetadataMap.put(oldKey, oldMetadataMap.get(oldKey));
					} else {
						newMetadataMap.put(oldKey, "");
					}
				}
				// if(newMetadataMap.get(oldKey)==null){
				// newMetadataMap.put(oldKey, "");
				// }
			}
			oldCa.setMetadataMap(newMetadataMap);
			oldDoi = oldCa.getMetadataMap().get(doiField);
			if (StringUtils.isNotBlank(oldDoi)) {
				String doiLastNum = oldDoi.substring(oldDoi.length() - 8);
				// doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
				if (StringUtils.isNotBlank(doi)) {
					String newDoi = doi.substring(0, doi.length() - 8);
					newDoi = newDoi + doiLastNum;
					oldCa.getMetadataMap().put(doiField, newDoi);
				} else {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			} else {
				if (StringUtils.isNotBlank(doi)) {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			}
			if (StringUtils.isNotBlank(uploadFile)) {
				parentPath = FILE_ROOT + oldCa.getRootPath();
				if (oldCa.getRealFiles() != null) {
					List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa.getRealFiles();
					Map<String, String> dirMap = new TreeMap<String, String>();
					Map<String, List<String>> md5Map = new HashMap<String, List<String>>();
					for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
						String isDir = oldFile.getIsDir();
						if ("1".equals(isDir)) {
							String id = oldFile.getId();
							id = id.replaceAll("\\\\", "/");
							if (id.endsWith("/")) {
								id = id.substring(0, id.length() - 1);
							}
							logger.info(id + "==========" + oldFile.getName());
							dirMap.put(id, oldFile.getName());
						}
					}
					Set<String> set = new HashSet<String>();
					for (String key : dirMap.keySet()) {
						String value = dirMap.get(key);
						if (key.indexOf("/") > 0) {
							String name = dirMap.get(key);
							String lastDir = key.substring(0,key.lastIndexOf("/"));
							String lastName = dirMap.get(lastDir);
							if (StringUtils.isNotBlank(lastName)) {
								dirMap.put(key, lastName + "/" + name);
								set.add(lastName + "/" + name);
							}
						} else {
							set.add(value);
						}
					}
					for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
						String isDir = oldFile.getIsDir();
						if ("2".equals(isDir)) {
							String tempPid = oldFile.getPid();
							List<String> md5List = null;
							if ("-1".equals(tempPid)) {
								md5List = md5Map.get(tempPid);
								if (md5List == null) {
									md5List = new ArrayList<>();
								}
								md5List.add(oldFile.getMd5());
								md5Map.put("-1", md5List);
							} else {
								tempPid = tempPid.replaceAll("\\\\", "/");
								if (tempPid.endsWith("/")) {
									tempPid = tempPid.substring(0,
											tempPid.length() - 1);
								}
								md5List = md5Map.get(dirMap.get(tempPid));
								if (md5List == null) {
									md5List = new ArrayList<>();
								}
								md5List.add(oldFile.getMd5());
								md5Map.put(dirMap.get(tempPid), md5List);
							}
						}
					}
					for (Map.Entry<String, String> entry : dirMap.entrySet()) {
						logger.info("key= " + entry.getKey() + " and value= " + entry.getValue());
					}
					String fileType = uploadFile.substring(uploadFile.lastIndexOf(".") + 1,uploadFile.length());
					String outPutPath = FILE_ROOT + oldCa.getRootPath() + UUID.randomUUID();
					uploadFile = BresAction.FILE_TEMP + uploadFile;
					ZipOrRarUtil.unzip(uploadFile, outPutPath, fileType);
					File outFile = new File(outPutPath);
					logger.info("===--------------------=文件递归改名===" + parentPath + "============");
					int num = 0;
					String fileDoi = "";
					if (oldCa.getMetadataMap() != null && oldCa.getMetadataMap().get(doiField) != null) {
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//oldCa = ResUtils.getOverFileLists(parentPath, outFile, pid,oldCa, i, set, md5Map, dirMap, "", num, fileDoi);
					oldCa = ResUtils.getOverFileLists(parentPath, outFile, pid,oldCa, set, md5Map, "", fileDoi);//修改 不改名 hunagjun 2015-11-11 09:37:19
					ca = oldCa;
				} else {
					int j = 1;
					String outPutPath = FILE_ROOT + oldCa.getRootPath() + UUID.randomUUID();
					uploadFile = BresAction.FILE_TEMP + uploadFile;
					String fileType = uploadFile.substring(uploadFile.lastIndexOf(".") + 1,uploadFile.length());
					ZipOrRarUtil.unzip(uploadFile, outPutPath, fileType);
					File file = new File(outPutPath);
					int num = 0;
					String fileDoi = "";
					if (oldCa.getMetadataMap() != null&& oldCa.getMetadataMap().get(doiField) != null) {
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//oldCa = ResUtils.getFileLists(parentPath, file, pid, oldCa,i, j, num, fileDoi, false);
					oldCa = ResUtils.getFileLists(parentPath, file, pid, oldCa, fileDoi, false);//修改 不改名 huangjun 2015-11-9 18:42:17
					ca = oldCa;
				}
			} else {
				ca = oldCa;
			}
		} else if ("1".equals(repeatType) && StringUtils.isNotBlank(ca.getObjectId()) && !ca.getObjectId().equals("-1")) {
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + ca.getObjectId());
			Gson gson = new Gson();
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if(oldCa.getMetadataMap().get(targetField)!=null){
				modiflyTarget = true;
				agoTargets = oldCa.getMetadataMap().get(targetField);
			}
			if (oldCa.getRealFiles() != null) {
				ca.setRealFiles(oldCa.getRealFiles());
			}
			String oldDoi1 = oldCa.getMetadataMap().get(doiField);
			if (StringUtils.isNotBlank(oldDoi1)) {
				doi = DoiGenerateUtil.generateDoiByResDirectory(ca);
				// doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
				if (StringUtils.isNotBlank(doi)) {
					ca.getMetadataMap().put(doiField, doi);
				}
			} else {
				ca.getMetadataMap().put(doiField, doi);
			}
			String newRootPath = "";
			if (oldCa.getRootPath() != null) {
				newRootPath = oldCa.getRootPath();
				ca.setRootPath(oldCa.getRootPath());
			}
			logger.info("------++++++++++++++++=--------newRootPath转换前---------"+ newRootPath + "----------++++++++++------------");
			if (newRootPath.lastIndexOf("\\") > 0) {
				newRootPath = newRootPath.substring(0,
						newRootPath.lastIndexOf("\\") + 1);
			} else if (newRootPath.lastIndexOf("/") > 0) {
				newRootPath = newRootPath.substring(0,
						newRootPath.lastIndexOf("/") + 1);
			}
			newRootPath = newRootPath + UUID.randomUUID();
			logger.info("---------++++++++++++++++=---newRootPath转换后---------" + newRootPath + "----------++++++++++-------------");
			ca.setObjectId("");
			ca.setStatus(ResourceStatus.STATUS0);
		} else if (isModify) {
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + ca.getObjectId());
			Gson gson = new Gson();
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if(oldCa.getMetadataMap().get(targetField)!=null){
				modiflyTarget = true;
				agoTargets = oldCa.getMetadataMap().get(targetField);
			}
			Map<String, String> newMetadataMap = ca.getMetadataMap();
			Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
			for (Map.Entry<String, String> oldEntry : oldMetadataMap.entrySet()) {
				String newKey = oldEntry.getKey();
				if (newMetadataMap.get(newKey) == null) {
					newMetadataMap.put(newKey, "");
				}
			}
			//存储为null的字段
			List<String> listKey = new ArrayList<String>();
			for (Map.Entry<String, String> newEntry : newMetadataMap.entrySet()) {
				String newKey = newEntry.getKey();
				if (newMetadataMap.get(newKey) == null||newMetadataMap.get(newKey).equals("null")) {
					listKey.add(newKey);
				}
			}
			//移除为null的字段
			if(!listKey.isEmpty()){
				for(String key:listKey){
					newMetadataMap.remove(key);
				}
			}
			
//			//移除时间为null或为空的字段
//			if(!listDate.isEmpty()){
//				for(String dateField:listDate){
//					if(StringUtils.isBlank(newMetadataMap.get(dateField))){
//						newMetadataMap.remove(dateField);
//					}
//				}
//			}
			oldCa.setMetadataMap(newMetadataMap);
			oldDoi = oldCa.getMetadataMap().get(doiField);
			if (StringUtils.isNotBlank(oldDoi)) {
				String doiLastNum = oldDoi.substring(oldDoi.length() - 8);
				doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
				if (StringUtils.isNotBlank(doi)) {
					String newDoi = doi.substring(0, doi.length() - 8);
					newDoi = newDoi + doiLastNum;
					oldCa.getMetadataMap().put(doiField, newDoi);
				} else {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			} else if (StringUtils.isNotBlank(doiField) && StringUtils.isNotBlank(doi)) {
				ca.getMetadataMap().put(doiField, doi);
			}
			ca = oldCa;
		} else {
			doi = DoiGenerateUtil.generateDoiByResDirectory(ca);
			ca.getMetadataMap().put("cover", "");
			if (StringUtils.isNotBlank(uploadFile)) {
				String fileType = uploadFile.substring(uploadFile.lastIndexOf(".") + 1, uploadFile.length());
				if(fileType.equalsIgnoreCase("zip")&&ca.getIsCompress().equals("true")||fileType.equalsIgnoreCase("rar")&&ca.getIsCompress().equals("true")){
					ca = ZipOrRarUtil.unzipCa(BresAction.FILE_TEMP + uploadFile, parentPath, fileType, ca, doi);
				}else{
					ca = ResUtils.getFileLists(uploadFile, parentPath, fileType,ca,doi);
				}
			}
			oldDoi = ca.getMetadataMap().get(doiField);
			if (StringUtils.isNotBlank(oldDoi)) {
				String doiLastNum = oldDoi.substring(oldDoi.length() - 8);
				// doi = DoiGenerateUtil.generateDoiByResDirectory(ca);
				if (StringUtils.isNotBlank(doi)) {
					String newDoi = doi.substring(0, doi.length() - 8);
					newDoi = newDoi + doiLastNum;
					parentPath = resourceService.createPublishParentPath(ca.getPublishType(), newDoi);
					ca.setRootPath(parentPath.replace(FILE_ROOT, ""));
					ca.getMetadataMap().put(doiField, newDoi);
				} else {
					ca.setRootPath(parentPath.replace(FILE_ROOT, ""));
					ca.getMetadataMap().put(doiField, doi);
				}
			} else if (StringUtils.isNotBlank(doiField) && StringUtils.isNotBlank(doi)) {
				ca.getMetadataMap().put(doiField, doi);
			}
		}
		//移除时间为null或为空的字段并转换日期格式
		if(!listDate.isEmpty()){
			for(String dateField:listDate){
				if(StringUtils.isNotBlank(ca.getMetadataMap().get(dateField))){
					String dateTime = ca.getMetadataMap().get(dateField);
					dateTime = ResUtils.simpleDate(dateTime);
					dateTime = com.brainsoon.bsrcm.search.util.DateUtil.parseTimes(dateTime).getTime()+"";
					ca.getMetadataMap().put(dateField, dateTime);
				}
			}
		}
		// if (StringUtils.isNotBlank(doiField)&&StringUtils.isNotBlank(doi)) {
		// ca.getMetadataMap().put(doiField, doi);
		// }
		//调版权接口
//		if (isModify && StringUtils.isBlank(ca.getHasCopyright())) {
//			HttpClientUtil http = new HttpClientUtil();
//			Gson gson = new Gson();
//			if (StringUtils.isBlank(ca.getHasCopyright())) {
//				String isHave = http.executeGet(WebappConfigUtil.getParameter("COPYRIGHT_ISHAVE_URL") + "&identifier=" + ca.getObjectId());
//				SemanticResponse isHaveReturn = gson.fromJson(isHave,SemanticResponse.class);
//				String data = isHaveReturn.getData();
//				if (data.equals("true")) {
//					ca.setHasCopyright("1");
//				}
//			}
//		}
		boolean tN = false;
		String hql = "";
		Long num = (long) 0;
		List<String> targetNameList = new LinkedList<String>();
		List<Long> targetNameList1 = new LinkedList<Long>();
		Gson gson = new Gson();
		String paraJson = gson.toJson(ca);
		logger.debug(paraJson);
		HttpClientUtil http = new HttpClientUtil();
		String result = "";
		if ("2".equals(repeatType) || "4".equals(repeatType)) { // 覆盖资源
			result = http.postJson(PUBLISH_OVERRIDE_URL, paraJson);
		} else {
			result = http.postJson(PUBLISH_SAVE_URL, paraJson);
		}
		SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
		UserInfo user = LoginUserUtil.getLoginUser();
		String objectId = rtn.getObjectId();
		if (StringUtils.isNotBlank(objectId)) {
			// 文件转换
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			Ca newCa = gson.fromJson(resourceDetail, Ca.class);
			List<com.brainsoon.semantic.ontology.model.File> realFiles = newCa.getRealFiles();
			try {
				if (realFiles != null && realFiles.size() > 0) {
					logger.info("=======文件转换=====start====");
					DoFileQueueList doFileList = ResUtils.converPath(realFiles,objectId);
					logger.info("=======文件1111转换=====11111111111111111111====");
					if(doFileList !=null && doFileList.getDoFileQueueList().size()>0){
						//result = http.postJson(PUBLISH_FILE_WRITE_QUEUE,gson.toJson(doFileList));
						resConverfileTaskService.insertQueue(doFileList);
					}
				}
					logger.info("=======文件2222转换=====e====");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String tarFeld = ca.getMetadataMap().get(targetField);
		// if(ca.getMetadataMap().get(targetField).equals(anObject))
		String minusTarget = "";
		String plusTarget = "";
		String oldArray[] = null;
		String nowArray[] = null;
		String targetName[] = null;
		if(modiflyTarget||!isModify){
			if(StringUtils.isNotBlank(agoTargets) && StringUtils.isNotBlank(tarFeld)){
				//差集
				targetName = minus(agoTargets.split(","),tarFeld.split(","));
			}else if(StringUtils.isNotBlank(tarFeld)){
				targetName = tarFeld.split(",");
			}
//			[1,2,3]old [5,3.4]now
			//记录减标签
			if(StringUtils.isNotBlank(agoTargets) && StringUtils.isNotBlank(ca.getMetadataMap().get(targetField))){
				oldArray = agoTargets.split(",");
				nowArray = ca.getMetadataMap().get(targetField).split(",");
				for(int u = 0;u<oldArray.length;u++){
					boolean falgOld = false;
					for(int p = 0;p<nowArray.length;p++){
						if(oldArray[u].equals(nowArray[p])){
							falgOld = true;
							break;
						}
					}
					if(!falgOld){
						//减标签
						minusTarget = minusTarget + oldArray[u]+",";
					}
				}
			}else if(StringUtils.isNotBlank(agoTargets) && StringUtils.isBlank(ca.getMetadataMap().get(targetField))){
				minusTarget = agoTargets;
			}else{
				minusTarget = "";
			}
			//加标签
			String tarArray[] = null;
			String minuTarry[] = null;
			if(StringUtils.isNotBlank(minusTarget) && targetName!=null){
				minuTarry = minusTarget.split(",");
				for(String st:targetName){
					boolean falgPlus = false;
					tarArray = st.split(",");
					for(int i=0;i<tarArray.length;i++){
						for(int j =0;j<minuTarry.length;j++){
							if(tarArray[i].toString().equals(minuTarry[j])){
								falgPlus = true;
								break;
							}
						}
						if(!falgPlus){
							plusTarget = plusTarget+tarArray[i]+",";
						}
					}
				}
			
			}else if(StringUtils.isBlank(agoTargets) && StringUtils.isNotBlank(ca.getMetadataMap().get(targetField))){
				plusTarget = ca.getMetadataMap().get(targetField);
			}else if(StringUtils.isBlank(minusTarget) && targetName!=null){
				for(int i=0;i<targetName.length;i++){
					plusTarget = plusTarget+targetName[i]+",";
				}
			}else{
				plusTarget = "";
			}
			//记录加标签
//			if(targetName!=null&&StringUtils.isNotBlank(minusTarget)){
//				String minuArray[] = minusTarget.split(",");
//				for(int u = 0;u<targetName.length;u++){
//					boolean falgOld = false;
//					for(int p = 0;p<minusTarget.length();p++){
//						if(oldArray[u].equals(nowArray[p])){
//							falgOld = true;
//							break;
//						}
//					}
//					if(!falgOld){
//						//减标签
//						plusTarget = plusTarget + oldArray[u]+",";
//					}
//				}
//				
//			}
			//加操作
			if (StringUtils.isNotBlank(plusTarget)) {
				targetName = plusTarget.split(",");
				for (int i = 0; i < targetName.length; i++) {
					num = (long) 0;
					hql = "select targetName from ResTargetData  where targetName='"
							+ targetName[i] + "' and module='"+publishType+"' and userId="+userId+" and pid not in('-1')";
					targetNameList = (List<String>) baseDao.query(hql);
					if (targetNameList.size() > 0) {
						hql = "select targetNum from ResTargetData  where targetName='"
								+ targetName[i] + "' and module='"+publishType+"' and userId="+userId+" and pid not in('-1')";
						targetNameList1 = (List<Long>) baseDao.query(hql);
						if (targetNameList1.size() > 0) {
							num = targetNameList1.get(0) + 1;
						}
						hql = "UPDATE ResTargetData SET targetNum=" + num
								+ " where targetName='" + targetName[i] + "' and module='"+publishType+"' and  pid not in('-1')";
						baseDao.updateWithHql(hql);
						tN = true;
					} else {
						String hq = "from ResTargetData";
						boolean tarFlag = false;
						List<ResTargetData> list = baseDao.query(hq);
						ResTargetData targetMysql = new ResTargetData();
						ResTargetData target = new ResTargetData();
						if(list!=null && list.size()>0){
							for(int u=0;u<list.size();u++){
								if(list.get(u).getPid().equals("-1") && list.get(u).getModule().equals(publishType) && list.get(u).getTargetName().equals("通用标签")){
									targetMysql = list.get(u);
									tarFlag = true;
									break;
								}
							}
						}
						if(tarFlag){
							Long num1 = (long) 1;
							target.setObjectId(objectId);
							target.setUserId(user.getUserId());
							target.setTargetNum(num1);
							target.setPid(targetMysql.getId()+"");
							target.setTargetName(targetName[i].trim());
							target.setTargetType("通用标签");
							target.setModule(publishType);
							target.setStatus(1);
							target.setTargetStatus(Integer.parseInt("1"));
							target.setXpath(target.getTargetName()+","+target.getTargetType());
							baseDao.create(target);
						}else{
							targetMysql.setPid(Long.parseLong("-1")+"");
							targetMysql.setTargetName("通用标签");
							targetMysql.setModule(publishType);
							targetMysql.setUserId(user.getUserId());
							targetMysql.setTargetType("通用标签");
							targetMysql.setTargetStatus(Integer.parseInt("1"));
							baseDao.create(targetMysql);
							Long num1 = (long) 1;
							target.setObjectId(objectId);
							target.setUserId(user.getUserId());
							target.setStatus(1);
							target.setTargetNum(num1);
							target.setTargetType("通用标签");
							target.setModule(publishType);
							target.setTargetName(targetName[i].trim());
							target.setPid(targetMysql.getId()+"");
							target.setXpath(target.getTargetName()+","+target.getTargetType());
							target.setTargetStatus(Integer.parseInt("1"));
							baseDao.create(target);
							
						}
//						target.setObjectId(objectId);
//						target.setStatus(1);
//						target.setTargetName(targetName[i]);
//						target.setModule(publishType);
//						target.setUserId(userId);
//						target.setTargetNum(Long.parseLong("1"));
//						baseDao.create(target);
					}
				}
			}
			
			// 减操作
			if (StringUtils.isNotBlank(minusTarget)) {
				String targeta[] = minusTarget.split(",");
					for (int y = 0; y < targeta.length; y++) {
						hql = "select targetNum from ResTargetData  where targetName='"
								+ targeta[y] + "' and pid!='-1' and module="+publishType+" and userId="+userId;
						targetNameList1 = (List<Long>) baseDao.query(hql);
						if (targetNameList1.get(0) != null
								&& targetNameList1.get(0) != (long) 0) {
							num = targetNameList1.get(0) - 1;
							hql = "UPDATE ResTargetData SET targetNum=" + num
									+ " where targetName='" + targeta[y]
									+ "' and pid!='-1' and module="+publishType+" and userId="+userId;
							baseDao.updateWithHql(hql);
						}
					}
				}
		}
		String checkOpinion = "";
		if (StringUtils.isBlank(ca.getObjectId()) && !isModify || "1".equals(repeatType)) {
			// 记录操作历史
			sysOperateService.saveHistory(WorkFlowUtils.getExecuId(objectId, "pubresCheck"),checkOpinion, "资源草稿", "添加", new Date(), userId);
			// //SysOperateLogUtils.addLog(operateKey+libType, "书名："+
			// ca.getCommonMetaData().getTitle(), LoginUserUtil.getLoginUser());
			SysOperateLogUtils.addLog(logDesc, MetadataSupport.getTitle(ca),user);
		} else {
			sysOperateService.saveHistory(WorkFlowUtils.getExecuId(objectId, "pubresCheck"),checkOpinion, "资源草稿", "修改", new Date(), userId);
			SysOperateLogUtils.addLog(logDesc, MetadataSupport.getTitle(ca),user);
		}
		if (rtn.getState() != 0) {
			throw new ServiceException("资源报错异常");
		} else {
			if (!isModify || "1".equals(repeatType) || "".equals(repeatType)) {
				int resNum = 0;
				hql = "select oresNums from RunNumber where publishType='"+ publishType + "'";
				targetNameList = (List<String>) baseDao.query(hql);
				if (targetNameList.size() > 0) {
					if (targetNameList.get(0) != null) {
						resNum = Integer.parseInt(targetNameList.get(0)) + 1;
					}
					hql = "UPDATE RunNumber SET oresNums='" + resNum+ "' where oresNums='"
							+ Integer.parseInt(targetNameList.get(0))+ "' and publishType='" + publishType + "'";
					baseDao.updateWithHql(hql);
				} else {
					RunNumber runNumber = new RunNumber();
					runNumber.setOresNums("1");
					runNumber.setPublishType(publishType);
					baseDao.create(runNumber);
				}
			}
			return rtn.getObjectId();
		}
	}

	public void createRandomDir(String path) {
		Random random = new Random();
		int value = random.nextInt(10);
		File file = new File(path);
		if (file.exists()) {
			path = path + "_" + value;
			createRandomDir(path);
		} else {
			file.mkdirs();
		}
	}

	/**
	 * 创建批量导入出版图书
	 * 
	 * @param ca
	 * @param uploadFile
	 * @throws Exception
	 */
	public String saveImportPublishRes(Ca ca, String processTask,
			String repeatType, Map<String, Map<String, String>> fileMetadataFlag)
			throws Exception {
		logger.info("进入saveImportPublishRes方法！");
		String doi = "";
		int i = 1;
		int j = 1;
		String pid = "-1";
		String srcPath = ca.getUploadFile();
		File srcFile = new File(srcPath);

		String doiField = "";
		String oldDoi = "";
		String targetField = "";
		String versionField = "";
		String agoTargets = "";
		logger.info("进入saveImportPublishRes方法2！");
		// 获得DOI字段
		List<MetadataDefinition> metadataDefinitions = MetadataSupport
				.getMetadateDefines(ca.getPublishType());
		logger.info("metadataDefinitions");
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			logger.info("1");
			if (metadataDefinition.getIdentifier() != null
					&& metadataDefinition.getIdentifier() == 5) {
				doiField = metadataDefinition.getFieldName();
			}
			logger.info("2" + doiField);
			if (metadataDefinition.getIdentifier() != null
					&& metadataDefinition.getIdentifier() == 10) {
				targetField = metadataDefinition.getFieldName();
			}
			logger.info("3" + targetField);
			if (metadataDefinition.getIdentifier() != null
					&& metadataDefinition.getIdentifier() == 11) {
				versionField = metadataDefinition.getFieldName();
			}
			logger.info("4" + versionField);
		}
		logger.info("进入saveImportPublishRes方法3！");
		String resVersion = ca.getMetadataMap().get(versionField);
		if (resVersion == null || "".equals(resVersion)) {
			ca.putMetadataMap(versionField, "00");
		}
		boolean flag = false;
		Set<String> set = null;
		Map<String, String> dirMap = null;
		Map<String, List<String>> md5Map = null;
		if (ca.getMetadataMap().get("objectId") != null
				&& !"".equals(ca.getMetadataMap().get("objectId"))) {
			HttpClientUtil http = new HttpClientUtil();
			Gson gson = new Gson();
			if (StringUtils.isBlank(ca.getHasCopyright())) {
				String isHave = http.executeGet(WebappConfigUtil
						.getParameter("COPYRIGHT_ISHAVE_URL")
						+ "&identifier="
						+ ca.getMetadataMap().get("objectId"));
				SemanticResponse isHaveReturn = gson.fromJson(isHave,
						SemanticResponse.class);
				String data = isHaveReturn.getData();
				if (data.equals("true")) {
					ca.setHasCopyright("1");
				}
			}
			if (processTask.equals("1")) {
				if (StringUtils.isNotBlank(ca.getMetadataMap().get("objectId"))) {
					taskProcessService.finishTaskDetail(ca.getMetadataMap()
							.get("objectId"));
				}
			}
			flag = true;
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("PUBLISH_DETAIL_URL")
					+ "?id="
					+ ca.getMetadataMap().get("objectId"));
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
//			if(oldCa.getMetadataMap().get(targetField)!=null){
//				agoTargets = oldCa.getMetadataMap().get(targetField);
//			}
			if (oldCa.getMetadataMap() != null)
				if (doiField != null || !"".equals(doiField)) {
					oldDoi = oldCa.getMetadataMap().get(doiField);
				} else {
					oldCa.setMetadataMap(ca.getMetadataMap());
				}
			ca.putMetadataMap(doiField, oldDoi);
			String parentPath = oldCa.getRootPath();
			parentPath = FILE_ROOT + parentPath;

			Map<String, String> metadataMap = ca.getMetadataMap();
			// ca.setMetadataMap(oldCa.getMetadataMap());
			Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
			for (Map.Entry<String, String> newEntry : metadataMap.entrySet()) {
				String value = newEntry.getValue();
				if (StringUtils.isNotBlank(value)) {
					oldMetadataMap.put(newEntry.getKey(), value);
				}
			}
			oldCa.setMetadataMap(oldMetadataMap);
			oldDoi = oldCa.getMetadataMap().get(doiField);
			if (StringUtils.isNotBlank(oldDoi)) {
				String doiLastNum = oldDoi.substring(oldDoi.length() - 8);
				doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
				if (StringUtils.isNotBlank(doi)) {
					String newDoi = doi.substring(0, doi.length() - 8);
					newDoi = newDoi + doiLastNum;
					oldCa.getMetadataMap().put(doiField, newDoi);
				} else {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			}
			if (oldCa.getRealFiles() != null) {
				List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa
						.getRealFiles();
				logger.info("=---------····【【【【【【【【【【【【【【【【【-----------===元数据覆盖===输出文oldFiles件大小==="
						+ oldFiles.size() + "=====】】】】】】】】】】】】】=======");
				dirMap = new TreeMap<String, String>();
				md5Map = new HashMap<String, List<String>>();
				for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
					String isDir = oldFile.getIsDir();
					if ("1".equals(isDir)) {
						String id = oldFile.getId();
						id = id.replaceAll("\\\\", "/");
						if (id.endsWith("/")) {
							id = id.substring(0, id.length() - 1);
						}
						logger.info(id + "==========" + oldFile.getName());
						dirMap.put(id, oldFile.getName());
					}
				}
				set = new HashSet<String>();
				for (String key : dirMap.keySet()) {
					String value = dirMap.get(key);
					if (key.indexOf("/") > 0) {
						String name = dirMap.get(key);
						String lastDir = key.substring(0, key.lastIndexOf("/"));
						String lastName = dirMap.get(lastDir);
						if (StringUtils.isNotBlank(lastName)) {
							dirMap.put(key, lastName + "/" + name);
							set.add(lastName + "/" + name);
						}
					} else {
						set.add(value);
					}
				}
				for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
					String isDir = oldFile.getIsDir();
					if ("2".equals(isDir)) {
						String tempPid = oldFile.getPid();
						List<String> md5List = null;
						if ("-1".equals(tempPid)) {
							md5List = md5Map.get(tempPid);
							if (md5List == null) {
								md5List = new ArrayList<>();
							}
							md5List.add(oldFile.getMd5());
							md5Map.put("-1", md5List);
						} else {
							tempPid = tempPid.replaceAll("\\\\", "/");
							if (tempPid.endsWith("/")) {
								tempPid = tempPid.substring(0,
										tempPid.length() - 1);
							}
							md5List = md5Map.get(dirMap.get(tempPid));
							if (md5List == null) {
								md5List = new ArrayList<>();
							}
							md5List.add(oldFile.getMd5());
							md5Map.put(dirMap.get(tempPid), md5List);
						}
					}
				}
			}
			if (oldCa.getRealFiles() == null) {
				oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
				try {
					int num = 0;
					String fileDoi = "";
					if (oldCa.getMetadataMap() != null
							&& oldCa.getMetadataMap().get(doiField) != null) {
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa,i, j, num, fileDoi, false);
					ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa, fileDoi, false);
					//TODO  2015-11-9 18:35:08
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
				logger.info("========文件转换开始path" + parentPath + "====srcFile"
						+ srcFile + "=======");
				int num = 0;
				String fileDoi = "";
				if (oldCa.getMetadataMap() != null
						&& oldCa.getMetadataMap().get(doiField) != null) {
					fileDoi = oldCa.getMetadataMap().get(doiField);
				}
				//oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid,oldCa, i, set, md5Map, dirMap, srcPath, num, fileDoi);
				oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid,oldCa, set, md5Map, srcPath, fileDoi);//修改 不改名 hunagjun 2015-11-11 09:37:19
				logger.info("=--------------------===文件递归改名结束===" + parentPath
						+ "============");
			}
			oldCa.getMetadataMap().remove("objectId");
			ca = oldCa;
		} else if ("2".equals(repeatType)) {
			logger.info("2repeatType！");
			HttpClientUtil http = new HttpClientUtil();
			// ca.setObjectId("urn:publish-578321a8-5a3f-4d56-98f4-916a618ffe03");
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("PUBLISH_DETAIL_URL")
					+ "?id="
					+ ca.getObjectId());
			Gson gson = new Gson();
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if(oldCa.getMetadataMap().get(targetField)!=null){
				agoTargets = oldCa.getMetadataMap().get(targetField);
			}
			Map<String, String> metadataMap = ca.getMetadataMap();
			Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
			for (Map.Entry<String, String> newEntry : metadataMap.entrySet()) {
				boolean valFlag = false;
				String newKey = newEntry.getKey();
				String newValue = newEntry.getValue();
				for (Map.Entry<String, String> oldEntry : oldMetadataMap
						.entrySet()) {
					String oldKey = oldEntry.getKey();
					String oldValue = oldEntry.getValue();
					if (newKey.equals(oldKey)
							&& StringUtils.isNotBlank(oldValue)) {
						valFlag = true;
						break;
					}
				}
				if (!valFlag && StringUtils.isNotBlank(newValue)) {
					oldMetadataMap.put(newEntry.getKey(), newValue);
				}
			}
			// oldCa.setMetadataMap(oldMetadataMap);
			oldCa.setMetadataMap(oldCa.getMetadataMap());
			oldDoi = oldCa.getMetadataMap().get(doiField);
			if (StringUtils.isNotBlank(oldDoi)) {
				String doiLastNum = oldDoi.substring(oldDoi.length() - 8);
				doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
				if (StringUtils.isNotBlank(doi)) {
					String newDoi = doi.substring(0, doi.length() - 8);
					newDoi = newDoi + doiLastNum;
					oldCa.getMetadataMap().put(doiField, newDoi);
				} else {
					oldCa.getMetadataMap().put(doiField, doi);
				}

			}
			String parentPath = FILE_ROOT + oldCa.getRootPath();
			if (oldCa.getRealFiles() != null) {
				List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa
						.getRealFiles();
				logger.info("=---------····【【【【【【【【【【【【【【【【【-----------===元数据增量===输出文oldFiles件大小==="
						+ oldFiles.size() + "=====】】】】】】】】】】】】】=======");
				dirMap = new TreeMap<String, String>();
				md5Map = new HashMap<String, List<String>>();
				for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
					String isDir = oldFile.getIsDir();
					if ("1".equals(isDir)) {
						String id = oldFile.getId();
						id = id.replaceAll("\\\\", "/");
						if (id.endsWith("/")) {
							id = id.substring(0, id.length() - 1);
						}
						logger.info(id + "==========" + oldFile.getName());
						dirMap.put(id, oldFile.getName());
					}
				}
				set = new HashSet<String>();
				for (String key : dirMap.keySet()) {
					String value = dirMap.get(key);
					if (key.indexOf("/") > 0) {
						String name = dirMap.get(key);
						String lastDir = key.substring(0, key.lastIndexOf("/"));
						String lastName = dirMap.get(lastDir);
						if (StringUtils.isNotBlank(lastName)) {
							dirMap.put(key, lastName + "/" + name);
							set.add(lastName + "/" + name);
						}
					} else {
						set.add(value);
					}
				}
				for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
					String isDir = oldFile.getIsDir();
					if ("2".equals(isDir)) {
						String tempPid = oldFile.getPid();
						List<String> md5List = null;
						if ("-1".equals(tempPid)) {
							md5List = md5Map.get(tempPid);
							if (md5List == null) {
								md5List = new ArrayList<>();
							}
							md5List.add(oldFile.getMd5());
							md5Map.put("-1", md5List);
						} else {
							tempPid = tempPid.replaceAll("\\\\", "/");
							if (tempPid.endsWith("/")) {
								tempPid = tempPid.substring(0,
										tempPid.length() - 1);
							}
							md5List = md5Map.get(dirMap.get(tempPid));
							if (md5List == null) {
								md5List = new ArrayList<>();
							}
							md5List.add(oldFile.getMd5());
							md5Map.put(dirMap.get(tempPid), md5List);
						}
					}
				}
			}
			if (oldCa.getRealFiles() == null) {
				oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
				try {
					int num = 0;
					String fileDoi = "";
					if (oldCa.getMetadataMap() != null
							&& oldCa.getMetadataMap().get(doiField) != null) {
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa,i, j, num, fileDoi, false);
					ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa, fileDoi, false);
					//TODO
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
				int num = 0;
				String fileDoi = "";
				if (oldCa.getMetadataMap() != null
						&& oldCa.getMetadataMap().get(doiField) != null) {
					fileDoi = oldCa.getMetadataMap().get(doiField);
				}
				//oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid,oldCa, i, set, md5Map, dirMap, srcPath, num, fileDoi);
				oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid,oldCa, set, md5Map, srcPath, fileDoi);//修改 不改名 hunagjun 2015-11-11 09:37:19
			}
			// for (Map.Entry<String, String> entry : dirMap.entrySet()) {
			// logger.info("key= " + entry.getKey() + " and value= "
			// + entry.getValue());
			// }
			logger.info("===--------------------=文件递归改名===" + parentPath
					+ "============");
			logger.info("=--------------------===文件递归改名结束===" + parentPath
					+ "============");
			ca = oldCa;
			// 元数据覆盖
		} else if ("4".equals(repeatType)) {
			logger.info("4repeatType！");
			HttpClientUtil http = new HttpClientUtil();
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("PUBLISH_DETAIL_URL")
					+ "?id="
					+ ca.getObjectId());
			Gson gson = new Gson();
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if(oldCa.getMetadataMap().get(targetField)!=null){
				agoTargets = oldCa.getMetadataMap().get(targetField);
			}
			String parentPath = oldCa.getRootPath();
			parentPath = FILE_ROOT + parentPath;
			Map<String, String> newMetadataMap = ca.getMetadataMap();
			Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
			for (Map.Entry<String, String> oldEntry : oldMetadataMap.entrySet()) {
				String oldKey = oldEntry.getKey();
				if (newMetadataMap.get(oldKey) == null) {
					if (oldKey.equals(doiField)
							&& oldMetadataMap.get(oldKey) != null) {
						newMetadataMap.put(oldKey, oldMetadataMap.get(oldKey));
					} else {
						newMetadataMap.put(oldKey, "");
					}
				}
			}
			oldCa.setMetadataMap(newMetadataMap);
			oldDoi = oldCa.getMetadataMap().get(doiField);
			if (StringUtils.isNotBlank(oldDoi)) {
				String doiLastNum = oldDoi.substring(oldDoi.length() - 8);
				doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
				if (StringUtils.isNotBlank(doi)) {
					String newDoi = doi.substring(0, doi.length() - 8);
					newDoi = newDoi + doiLastNum;
					oldCa.getMetadataMap().put(doiField, newDoi);
				} else {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			} else {
				if (StringUtils.isNotBlank(doi)) {
					oldCa.getMetadataMap().put(doiField, doi);
				}
			}
			if (oldCa.getRealFiles() != null) {
				List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa
						.getRealFiles();
				logger.info("=---------····【【【【【【【【【【【【【【【【【-----------===元数据覆盖===输出文oldFiles件大小==="
						+ oldFiles.size() + "=====】】】】】】】】】】】】】=======");
				dirMap = new TreeMap<String, String>();
				md5Map = new HashMap<String, List<String>>();
				for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
					String isDir = oldFile.getIsDir();
					if ("1".equals(isDir)) {
						String id = oldFile.getId();
						id = id.replaceAll("\\\\", "/");
						if (id.endsWith("/")) {
							id = id.substring(0, id.length() - 1);
						}
						logger.info(id + "==========" + oldFile.getName());
						dirMap.put(id, oldFile.getName());
					}
				}
				set = new HashSet<String>();
				for (String key : dirMap.keySet()) {
					String value = dirMap.get(key);
					if (key.indexOf("/") > 0) {
						String name = dirMap.get(key);
						String lastDir = key.substring(0, key.lastIndexOf("/"));
						String lastName = dirMap.get(lastDir);
						if (StringUtils.isNotBlank(lastName)) {
							dirMap.put(key, lastName + "/" + name);
							set.add(lastName + "/" + name);
						}
					} else {
						set.add(value);
					}
				}
				for (com.brainsoon.semantic.ontology.model.File oldFile : oldFiles) {
					String isDir = oldFile.getIsDir();
					if ("2".equals(isDir)) {
						String tempPid = oldFile.getPid();
						List<String> md5List = null;
						if ("-1".equals(tempPid)) {
							md5List = md5Map.get(tempPid);
							if (md5List == null) {
								md5List = new ArrayList<>();
							}
							md5List.add(oldFile.getMd5());
							md5Map.put("-1", md5List);
						} else {
							tempPid = tempPid.replaceAll("\\\\", "/");
							if (tempPid.endsWith("/")) {
								tempPid = tempPid.substring(0,
										tempPid.length() - 1);
							}
							md5List = md5Map.get(dirMap.get(tempPid));
							if (md5List == null) {
								md5List = new ArrayList<>();
							}
							md5List.add(oldFile.getMd5());
							md5Map.put(dirMap.get(tempPid), md5List);
						}
					}
				}
			}
			if (oldCa.getRealFiles() == null) {
				oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
				try {
					int num = 0;
					String fileDoi = "";
					if (oldCa.getMetadataMap() != null
							&& oldCa.getMetadataMap().get(doiField) != null) {
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa,i, j, num, fileDoi, false);
					ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa, fileDoi, false);
					//TODO
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				int num = 0;
				String fileDoi = "";
				if (oldCa.getMetadataMap() != null
						&& oldCa.getMetadataMap().get(doiField) != null) {
					fileDoi = oldCa.getMetadataMap().get(doiField);
				}
				//oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid,oldCa, i, set, md5Map, dirMap, srcPath, num, fileDoi);
				oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid,oldCa, set, md5Map, srcPath, fileDoi);//修改 不改名 hunagjun 2015-11-11 09:37:19
			}
			// for (Map.Entry<String, String> entry : dirMap.entrySet()) {
			// logger.info("key= " + entry.getKey() + " and value= "
			// + entry.getValue());
			// }
			logger.info("===--------------------=文件递归改名===" + parentPath
					+ "============");
			// oldCa = getOverFileLists(parentPath, srcFile, pid, oldCa, i, set,
			// md5Map, dirMap,srcPath);
			logger.info("=--------------------===文件递归改名结束===" + parentPath
					+ "============");
			ca = oldCa;
		} else {
			logger.info("else忽略！");
			doi = DoiGenerateUtil.generateDoiByResDirectory(ca);
			// 按要求生成目录
			String parentPath = resourceService.createPublishParentPath(
					ca.getPublishType(), doi);
			ca.setRootPath(parentPath.replace(FILE_ROOT, ""));
			try {
				int num = 0;
				//ca = ResUtils.getFileLists(parentPath, srcFile, pid, ca, i, j,num, doi, false);
				ca = ResUtils.getFileLists(parentPath, srcFile, pid, ca, doi, false);//修改 不改名 huangjun 2015-11-9 18:44:40
				//TODO
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (StringUtils.isNotBlank(doiField) && StringUtils.isNotBlank(doi)) {
				ca.getMetadataMap().put(doiField, doi);
			}
			ca.setStatus(ResourceStatus.STATUS0);
			logger.info("进入保存方法！");
		}
		Date date = new Date();
		ca.setUpdateTime(date.getTime() + "");
		Gson gson = new Gson();
		if (ca.getRealFiles() != null) {
			logger.info("=---------····【【【【【【【【【【【【【【【【【-----------===输出最终文件大小==="
					+ ca.getRealFiles().size() + "=====】】】】】】】】】】】】】=======");
		}
		// if(fileMetadataFlag!=null && fileMetadataFlag.size()>0){
		// ca.getFileMetadataFlag().clear();
		// }
		String paraJson = gson.toJson(ca);
		logger.debug(paraJson);
		HttpClientUtil http = new HttpClientUtil();
		String result = "";
		if ("2".equals(repeatType) || flag || "4".equals(repeatType)) { // 覆盖资源
			result = http.postJson(PUBLISH_OVERRIDE_URL, paraJson);
			logger.info("调接口覆盖返回result！" + result + "");
		} else {
			logger.info("调接口进入保存方法！");
			result = http.postJson(PUBLISH_SAVE_URL, paraJson);
		}
		SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
		String objectId = rtn.getObjectId();
		// 是否来自加工任务
		if (StringUtils.isNotBlank(objectId)) {
			// 文件转换
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			Ca returnCa = gson.fromJson(resourceDetail, Ca.class);//保存完后返回的Ca
			List<com.brainsoon.semantic.ontology.model.File> realFiles = returnCa.getRealFiles();
			logger.debug("文件转换----------start--------------");
			if (realFiles != null && realFiles.size() > 0) {
				DoFileQueueList doFileList = ResUtils.converPath(realFiles,objectId);
				logger.debug("文件转换-----111111111111-------------------");
				if(doFileList !=null && doFileList.getDoFileQueueList().size()>0){
					//result = http.postJson(PUBLISH_FILE_WRITE_QUEUE,gson.toJson(doFileList));
					resConverfileTaskService.insertQueue(doFileList);
				}
				logger.debug("文件转换结束 -----end-------------------");
			}
			String tarFieldValue = ca.getMetadataMap().get(targetField);
			if(StringUtils.isNotBlank(tarFieldValue)){
				String tarValueLength[]=tarFieldValue.split(",");
				List<String> targetNameList = new LinkedList<String>();
				List<Long> targetNameList1 = new LinkedList<Long>();
				Long num = (long) 0;
				String hql ="";
				String minusTarget = "";
				String plusTarget = "";
				String oldArray[] = null;
				String nowArray[] = null;
				String targetName[] = null;
				if (ca.getMetadataMap() != null && tarFieldValue != null
						&& !"".equals(tarFieldValue)) {
					if(StringUtils.isNotBlank(agoTargets) && StringUtils.isNotBlank(tarFieldValue)){
						//差集
						targetName = minus(agoTargets.split(","),tarFieldValue.split(","));
					}else if(StringUtils.isNotBlank(tarFieldValue)){
						targetName = tarFieldValue.split(",");
					}
//					[1,2,3]old [5,3.4]now
					//记录减标签
					if(StringUtils.isNotBlank(agoTargets) && StringUtils.isNotBlank(ca.getMetadataMap().get(targetField))){
						oldArray = agoTargets.split(",");
						nowArray = ca.getMetadataMap().get(targetField).split(",");
						for(int u = 0;u<oldArray.length;u++){
							boolean falgOld = false;
							for(int p = 0;p<nowArray.length;p++){
								if(oldArray[u].equals(nowArray[p])){
									falgOld = true;
									break;
								}
							}
							if(!falgOld){
								//减标签
								minusTarget = minusTarget + oldArray[u]+",";
							}
						}
					}else if(StringUtils.isNotBlank(agoTargets) && StringUtils.isBlank(ca.getMetadataMap().get(targetField))){
						minusTarget = agoTargets;
					}else{
						minusTarget = "";
					}
					//加标签
					String tarArray[] = null;
					String minuTarry[] = null;
					if(StringUtils.isNotBlank(minusTarget) && targetName!=null){
						minuTarry = minusTarget.split(",");
						for(String st:targetName){
							boolean falgPlus = false;
							tarArray = st.split(",");
							for(int u=0;u<tarArray.length;u++){
								for(int t =0;t<minuTarry.length;t++){
									if(tarArray[u].toString().equals(minuTarry[t])){
										falgPlus = true;
										break;
									}
								}
								if(!falgPlus){
									plusTarget = plusTarget+tarArray[u]+",";
								}
							}
						}
					
					}else if(StringUtils.isBlank(agoTargets) && StringUtils.isNotBlank(ca.getMetadataMap().get(targetField))){
						plusTarget = ca.getMetadataMap().get(targetField);
					}else if(StringUtils.isBlank(minusTarget) && targetName!=null){
						for(int h=0;h<targetName.length;h++){
							plusTarget = plusTarget+targetName[h]+",";
						}
					}else{
						plusTarget = "";
					}
					
					if (StringUtils.isNotBlank(plusTarget)) {
						targetName = plusTarget.split(",");
						for (int k = 0; k < targetName.length; k++) {
							num = (long) 0;
							hql = "select targetName from ResTargetData  where targetName='"
									+ targetName[k] + "' and module='"+ca.getPublishType()+"' and userId="+ca.getCreator()+" and pid not in('-1')";
							targetNameList = (List<String>) baseDao.query(hql);
							if (targetNameList.size() > 0) {
								hql = "select targetNum from ResTargetData  where targetName='"
										+ targetName[k] + "' and module='"+ca.getPublishType()+"' and userId="+ca.getCreator()+" and pid not in('-1')";
								targetNameList1 = (List<Long>) baseDao.query(hql);
								if (targetNameList1.size() > 0) {
									num = targetNameList1.get(0) + 1;
								}
								hql = "UPDATE ResTargetData SET targetNum=" + num
										+ " where targetName='" + targetName[k] + "' and module='"+ca.getPublishType()+"' and pid not in('-1')";
								baseDao.updateWithHql(hql);
							} else {
								String hq = "from ResTargetData";
								boolean tarFlag = false;
								List<ResTargetData> list = baseDao.query(hq);
								ResTargetData targetMysql = null;
								ResTargetData target = new ResTargetData();
								if(list!=null && list.size()>0){
									for(int u=0;u<list.size();u++){
										if(list.get(u).getPid().equals("-1") && list.get(u).getModule().equals(ca.getPublishType()) && list.get(u).getTargetName().equals("通用标签")){
											targetMysql = list.get(u);
											tarFlag = true;
											break;
										}
									}
								}
								if(tarFlag){
									Long num1 = (long) 1;
									target.setObjectId(objectId);
									target.setUserId(Long.parseLong(ca.getCreator()));
									target.setTargetNum(num1);
									target.setPid(targetMysql.getId()+"");
									target.setTargetName(targetName[k].trim());
									target.setTargetType("通用标签");
									target.setModule(ca.getPublishType());
									target.setStatus(1);
									target.setTargetStatus(Integer.parseInt("1"));
									target.setXpath(target.getTargetName()+","+target.getTargetType());
									baseDao.create(target);
								}else{
									targetMysql = new ResTargetData();
									targetMysql.setPid(Long.parseLong("-1")+"");
									targetMysql.setTargetName("通用标签");
									targetMysql.setModule(ca.getPublishType());
									targetMysql.setUserId(Long.parseLong(ca.getCreator()));
									targetMysql.setTargetType("通用标签");
									targetMysql.setTargetStatus(Integer.parseInt("1"));
									baseDao.create(targetMysql);
									Long num1 = (long) 1;
									target.setObjectId(objectId);
									target.setUserId(Long.decode(ca.getCreator()));
									target.setStatus(1);
									target.setTargetNum(num1);
									target.setTargetType("通用标签");
									target.setModule(ca.getPublishType());
									target.setTargetName(targetName[k].trim());
									target.setPid(targetMysql.getId()+"");
									target.setXpath(target.getTargetName()+","+target.getTargetType());
									target.setTargetStatus(Integer.parseInt("1"));
									baseDao.create(target);
									
								}
//								target.setObjectId(objectId);
//								target.setStatus(1);
//								target.setTargetName(targetName[i]);
//								target.setModule(publishType);
//								target.setUserId(userId);
//								target.setTargetNum(Long.parseLong("1"));
//								baseDao.create(target);
							}
						}
					}
					
					// 减操作
					if (StringUtils.isNotBlank(minusTarget)) {
						String targeta[] = minusTarget.split(",");
							for (int y = 0; y < targeta.length; y++) {
								hql = "select targetNum from ResTargetData  where targetName='"
										+ targeta[y] + "' and pid!='-1' and module="+ca.getPublishType()+" and userId="+ca.getCreator();
								targetNameList1 = (List<Long>) baseDao.query(hql);
								if (targetNameList1!=null && targetNameList1.get(0) != null
										&& targetNameList1.get(0) != (long) 0) {
									num = targetNameList1.get(0) - 1;
									hql = "UPDATE ResTargetData SET targetNum=" + num
											+ " where targetName='" + targeta[y]
											+ "' and pid!='-1' and module="+ca.getPublishType()+" and userId="+ca.getCreator();
									baseDao.updateWithHql(hql);
								}
							}
						}				
				}
			}
			String operateDesc = "导入";
			sysOperateService.saveHistory(
					WorkFlowUtils.getExecuId(objectId, "pubresCheck"), "",
					"资源草稿", operateDesc, new Date(),
					Long.parseLong(ca.getCreator()));
		}
		if (StringUtils.isNotBlank(objectId) && fileMetadataFlag != null
				&& fileMetadataFlag.size() > 0) {
			String resourceDetail = http.executeGet(WebappConfigUtil
					.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if (oldCa.getRealFiles() != null && oldCa.getRealFiles().size() > 0
					&& fileMetadataFlag != null && fileMetadataFlag.size() > 0) {
				List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = oldCa
						.getRealFiles();
				for (com.brainsoon.semantic.ontology.model.File file : tempRealFiles) {
					if (fileMetadataFlag.get(file.getMd5()) != null
							&& !fileMetadataFlag.get(file.getMd5()).isEmpty()) {
						file.setFileMetadataMap(fileMetadataFlag.get(file
								.getMd5()));
						String resultFile = http.postJson(CA_FILERES_SAVE_URL,
								gson.toJson(file));
						// String changeName =
						// http.executeGet(PUBLISH_RENAME_FILE + "?objectId="
						// + file.getObjectId());
					} else if (fileMetadataFlag.get(file.getObjectId()) != null) {
						file.setFileMetadataMap(fileMetadataFlag.get(file
								.getObjectId()));
						String resultFile = http.postJson(CA_FILERES_SAVE_URL,
								gson.toJson(file));
					}
				}
			}
		}
		if ("1".equals(repeatType) || "3".equals(repeatType)
				|| "".equals(repeatType)) {
			List<String> targetNameList = new LinkedList<String>();
			int resNum = 0;
			String hql = "";
			hql = "select oresNums from RunNumber where publishType='"
					+ ca.getPublishType() + "'";
			targetNameList = (List<String>) baseDao.query(hql);
			if (targetNameList.size() > 0) {
				if (targetNameList.get(0) != null) {
					resNum = Integer.parseInt(targetNameList.get(0)) + 1;
				}
				hql = "UPDATE RunNumber SET oresNums='" + resNum
						+ "' where oresNums='"
						+ Integer.parseInt(targetNameList.get(0))
						+ "' and publishType='" + ca.getPublishType() + "'";
				baseDao.updateWithHql(hql);
			} else {
				RunNumber runNumber = new RunNumber();
				runNumber.setPublishType(ca.getPublishType());
				runNumber.setOresNums("1");
				baseDao.create(runNumber);
			}
		}
		// ResConverfileTask rcft = new ResConverfileTask();
		// rcft.setSrcPath(newDir.replace(FILE_ROOT, ""));
		// rcft.setDoHasType("0"); //处理类型：0，文档或者视频转换（转换服务） 1，抽取封面文件，2抽取文本
		// rcft.setResId(objectId);
		// rcft.setPlatformId(2);
		// resConverfileTaskService.saveResConverfileTask(rcft);
		return objectId;
		// return "";
	}
	//去差集
		 public String[] minus(String[] arr1, String[] arr2) {
			 LinkedList<String> list = new LinkedList<String>();   
		       LinkedList<String> history = new LinkedList<String>();  
		       String[] longerArr = arr1;   
		       String[] shorterArr = arr2;   
		       if (arr1.length > arr2.length) {   
		           longerArr = arr2;   
		           shorterArr = arr1;   
		       }   
		       for (String str : longerArr) {   
		           if (!list.contains(str)) {   
		               list.add(str);   
		           }   
		       }   
		       for (String str : shorterArr) {   
		           if (list.contains(str)) {   
		               history.add(str);   
		               list.remove(str);   
		           } else {   
		               if (!history.contains(str)) {   
		                   list.add(str);   
		               }   
		           }   
		       }   
		 
		       String[] result = {};   
		       return list.toArray(result);   
			 
		 }
	/**
	 * 批量导入图书创建Ca对象
	 * 
	 * @param ca
	 * @param uploadFile
	 * @throws Exception
	 */
	public Ca saveImportBookRes(Asset asset) {
		logger.debug("******run at saveRes***********");
		logger.debug("jsonTree ");
		Ca ca = new Ca();
		ca = createCaObject(asset, ca);
		Gson gson = new Gson();
		String paraJson = gson.toJson(ca);
		logger.debug(paraJson);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(PUBLISH_SAVE_URL, paraJson);
		SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
		SysOperateLogUtils.addLog("res_imp_book_"
				+ ca.getCommonMetaData().getLibType(), "资源标题："
				+ ca.getCommonMetaData().getTitle(),
				LoginUserUtil.getLoginUser());
		logger.debug("result *** " + result);
		return ca;
	}

	/**
	 * 批量导入图书覆盖Ca对象
	 * 
	 * @param ca
	 * @param uploadFile
	 * @throws Exception
	 */
	public Ca overrideImportBookRes(Asset asset, Ca ca) {
		logger.debug("******run at saveRes***********");
		logger.debug("jsonTree ");
		ca = createCaObject(asset, ca);
		Gson gson = new Gson();
		String paraJson = gson.toJson(ca);
		logger.debug(paraJson);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(CA_OVERRIDE_URL, paraJson);
		SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
		logger.debug("result *** " + result);
		return ca;
	}

	/**
	 * 创建ca对象
	 * 
	 * @param ca
	 * @param uploadFile
	 * @throws Exception
	 */
	public Ca createCaObject(Asset asset, Ca ca) {
		logger.debug("******run at saveRes***********");
		logger.debug("jsonTree ");
		ca.setType("2");
		if (ca.getObjectId() != null && ca.getObjectId().equals("-1")) {
			ca.setObjectId("");
		}
		CommonMetaData commonMetaData = asset.getCommonMetaData();
		CommonMetaData commonMetaDataTemp = ca.getCommonMetaData();
		if (commonMetaData.getResVersion() == null
				|| "".equals(commonMetaData.getResVersion())) {
			if (commonMetaDataTemp == null) {
				commonMetaData.setResVersion("1");
			} else {
				if (commonMetaDataTemp.getResVersion() == null
						|| "".equals(commonMetaDataTemp.getResVersion())) {
					commonMetaData.setResVersion("1");
				}
			}
		}
		Map<String, String> commonMetaDatas = commonMetaData
				.getCommonMetaDatas();
		commonMetaData.setFascicule("");
		String doi = ResourceTypeUtils.getDOIByAsset(asset);
		// 文件存储
		String module = commonMetaData.getModule();
		String type = commonMetaData.getType();
		// 赋值文件路径
		String uploadFile = FTP_LOCAL_MAPPING + commonMetaDatas.get("filePath")
				+ File.separator + commonMetaDatas.get("fileName");
		// 按要求生成目录
		String parentPath = resourceService.createParentPath(module, type, doi);
		try {
			FileUtil.copyDir(new File(uploadFile), new File(parentPath));
		} catch (Exception e) {
			logger.info("文件目录拷贝出错");
		}
		String newDir = parentPath + File.separator
				+ commonMetaDatas.get("fileName");
		ResConverfileTask rcft = new ResConverfileTask(newDir.replace(
				FILE_ROOT, ""));
		resConverfileTaskService.saveResConverfileTask(rcft);
		// destFile.delete();
		OrganizationItem rootItem = new OrganizationItem();
		rootItem.setName("文件列表");
		rootItem.setNodeId("-1");
		rootItem.setXpath("-1");
		List<OrganizationItem> items = new ArrayList<OrganizationItem>();
		items = getItems(new File(newDir), rootItem, items);
		Organization org = new Organization();
		org.setOrganizationItems(items);
		List<Organization> organizations = new ArrayList<Organization>();
		organizations.add(org);
		// ca.setOrganizations(organizations);
		commonMetaData.setIdentifier(doi);
		commonMetaData.setStatus(ResourceStatus.STATUS0);
		commonMetaData.setModified_time(DateUtil.convertDateToString(
				dateFormat, new Date()));
		ca.setCommonMetaData(commonMetaData);
//		ca.setExtendMetaData(asset.getExtendMetaData());
		return ca;
	}

	// 创建封面
	public String createThumb(String path, String thumbFilePath) {
		String thumbPath = path + "/thumb/cover.jpg";
		thumbPath = thumbPath.replaceAll("\\\\", "/");
		File thumbFile = new File(thumbPath);
		if (!thumbFile.getParentFile().exists()) {
			thumbFile.getParentFile().mkdir();
		}
		// 保存缩略图
		File realFile = new File(BresAction.FILE_TEMP + thumbFilePath);
		try {
			FileUtils.copyFile(realFile, thumbFile);
			realFile.getParentFile().delete();
		} catch (IOException e) {
			logger.error("缩略图保存失败" + e.getMessage());
		}
		thumbPath = StringUtils.replace(thumbPath, FILE_ROOT, "");
		return thumbPath;
	}

	/**
	 * 增加版权信息
	 * 
	 * @param ca
	 * @param uploadFile
	 * @throws Exception
	 */
	public String saveBookCopyright(Ca ca) throws Exception {
		logger.debug("******run at saveRes***********");
		logger.debug("jsonTree ");
		Gson gson = new Gson();
		String paraJson = gson.toJson(ca);
		logger.debug(paraJson);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(PUBLISH_SAVE_URL, paraJson);
		logger.debug("result *** " + result);
		return result;
	}

	/**
	 * 保存文件
	 * 
	 * @param uploadFile
	 * @throws Exception
	 */
	public SemanticResponse saveFile(
			com.brainsoon.semantic.ontology.model.File file) throws Exception {
		Gson gson = new Gson();
		String paraJson = gson.toJson(file);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(PUBLISH_FILESAVE_URL, paraJson);
		SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
		if (rtn.getState() == 0) {
			ModifyLog modifyLog = new ModifyLog();
			modifyLog.setResId(file.getCaId());
			if ("1".equals(file.getIsDir())) {
				modifyLog.setModifyOld("addList");
			} else {
				modifyLog.setModifyOld("addFile");
			}
			modifyLog.setModifyNew(file.getName());
			modifyLog.setModifyField("tree");
			modifyLog.setModifyTime(new Date());
			modifyLog.setRestype("type");
			getBaseDao().create(modifyLog);
		}
		return rtn;
	}

	/**
	 * 递归获取图书目录
	 * 
	 * @param paretFile
	 * @param parentItem
	 * @param items
	 * @return
	 */
	public List<OrganizationItem> getItems(File paretFile,
			OrganizationItem parentItem, List<OrganizationItem> items) {
		OrganizationItem item = null;
		com.brainsoon.semantic.ontology.model.File itemFile = null;
		if (paretFile.isDirectory()) {
			File[] children = paretFile.listFiles();
			for (File child : children) {
				item = new OrganizationItem();
				item.setNodeId(String.valueOf(UID.next()));
				item.setName(child.getName());
				item.setPid(parentItem.getNodeId());
				item.setXpath(parentItem.getXpath() + "," + item.getNodeId());
				item.setPath(child.getAbsolutePath()
						.replace(File.separator, "/").replace(FILE_ROOT, ""));
				if (child.isFile()) {
					itemFile = new com.brainsoon.semantic.ontology.model.File();
					itemFile.setName(child.getName());
					itemFile.setPath(child.getAbsolutePath()
							.replace(File.separator, "/")
							.replace(FILE_ROOT, ""));
					List files = new ArrayList<com.brainsoon.semantic.ontology.model.File>();
					files.add(itemFile);
					item.setFiles(files);
				}
				items.add(item);
				if (child.isDirectory()) {
					getItems(child, item, items);
				}
			}
		}
		return items;
	}

	/**
	 * 修改组合资源(图书资源没有封面文件)
	 * 
	 * @param ca
	 * @throws Exception
	 */
	public void updateCollectRes(Ca ca, String thumbFile) throws Exception {
		CommonMetaData commonMetaData = ca.getCommonMetaData();
		commonMetaData.setModified_time(DateUtil.convertDateToString(
				dateFormat, new Date()));
		String doi = ResourceTypeUtils.getPublishDOI(ca);
		commonMetaData.setIdentifier(doi);
		String keyword = commonMetaData.getKeywords();
		if (StringUtils.isNotBlank(keyword) && keyword.indexOf("，") >= 0) {
			keyword = keyword.replaceAll("，", ",");
			commonMetaData.setKeywords(keyword);
		}
		ca.setCommonMetaData(commonMetaData);
		Ca oldCa = getCaById(ca.getObjectId());
		if (oldCa != null) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("objectId", ca.getObjectId());
			String hql = "delete from ModifyLog  where resId=:objectId and restype<>''";
			getBaseDao().executeUpdate(hql, parameters);
			CommonMetaData oldCommonMetaData = oldCa.getCommonMetaData();
			Map<String, String> oldMap = oldCommonMetaData.getCommonMetaDatas();
			Map<String, String> newMap = commonMetaData.getCommonMetaDatas();
			Iterator<Entry<String, String>> iterator = newMap.entrySet()
					.iterator();

			while (iterator.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Entry) iterator.next();
				String newKey = (String) entry.getKey();
				if ("res_version".equals(newKey)
						|| "modified_time".equals(newKey)
						|| "mapName".equals(newKey)) {
					continue;
				}
				String newValue = (String) entry.getValue();
				String oldValue = oldMap.get(newKey);
				if (StringUtils.isBlank(newValue)
						&& StringUtils.isNotBlank(oldValue)) {
					ModifyLog modifyLog = new ModifyLog();
					modifyLog.setModifyOld(oldValue);
					modifyLog.setModifyField(newKey);
					modifyLog.setModifyNew("");
					modifyLog.setModifyTime(new Date());
					modifyLog.setResId(ca.getObjectId());
					getBaseDao().create(modifyLog);
				} else if (StringUtils.isNotBlank(newValue)
						&& StringUtils.isBlank(oldValue)) {
					ModifyLog modifyLog = new ModifyLog();
					modifyLog.setModifyField(newKey);
					modifyLog.setModifyOld("");
					modifyLog.setModifyNew(newValue);
					modifyLog.setModifyTime(new Date());
					modifyLog.setResId(ca.getObjectId());
					getBaseDao().create(modifyLog);
				} else if (StringUtils.isNotBlank(oldValue)
						&& StringUtils.isNotBlank(newValue)
						&& !oldValue.equals(newValue)) {
					ModifyLog modifyLog = new ModifyLog();
					modifyLog.setModifyField(newKey);
					modifyLog.setModifyOld(oldValue);
					modifyLog.setModifyNew(newValue);
					modifyLog.setModifyTime(new Date());
					modifyLog.setResId(ca.getObjectId());
					getBaseDao().create(modifyLog);
				}
			}
		}
		Gson gson = new Gson();
		String paraJson = gson.toJson(ca);
		logger.debug(paraJson);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.postJson(PUBLISH_SAVE_URL, paraJson);
		logger.debug("result *** " + result);
	}

	/**
	 * 批量删除资源对象，逗号分隔，同时删除标签数据
	 * 
	 * @param ids
	 * @throws IOException
	 */
	public void deleteByIds(String ids) throws IOException {
		if (StringUtils.isNotBlank(ids)) {
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String[] idsArr = StringUtils.split(ids, ",");
			for (String id : idsArr) {
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + id);
				Gson gson = new Gson();
				Ca ca = gson.fromJson(resourceDetail, Ca.class);
				List<com.brainsoon.semantic.ontology.model.File> fileList = ca.getRealFiles();
				if (fileList != null && fileList.size() > 0) {
					//添加删除文件队列
					DeleteResFileForCaIds deleteResFileForCaIds = new DeleteResFileForCaIds();
					deleteResFileForCaIds.setCa(ca);
					deleteResFileForCaIds.setId(id);
					DeleteFileTaskQueue.getInst().addMessage(deleteResFileForCaIds);
					//删除转换记录和转换后的文件 2015-12-18 14:26:55 huangjun
//					resConverfileTaskService.deleteDoFileQueue(id,ca.getRootPath());
//					for (com.brainsoon.semantic.ontology.model.File files : fileList) {
//						String path = files.getPath();
//						if (StringUtils.isNotBlank(path)) {
//							File f = new File(FILE_ROOT + path);
//							if (f.exists()) {
//								// 删除文件
//								if (f.isDirectory()) {
//									File parentFile = f.getParentFile();
//									if (parentFile.exists()) {
//										// File pparentFile =
//										// parentFile.getParentFile();
//										// if(pparentFile.exists()){
//										FileUtils.deleteDirectory(parentFile);
//										// }
//									}
//								} else {
//									try {
//										FileUtils.forceDelete(f.getParentFile());
//									} catch (Exception e) {
//										logger.error("删除文件失败"+ e.getMessage()+ f.getParentFile().getAbsolutePath());
//									}
//								}
//								// }
//								// }
//							}
//						}
//					}

					if (ca.getStatus().equals(SystemConstants.ResourceStatus.STATUS5)) {
						String wfTaskId = "";
						// 暂时先不删除流程数据
						try {
							List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(
											ca.getObjectId(), "pubresCheck"));
							jbpmExcutionService.endTask(tasks.get(0).getId(),ProcessConstants.
									SUBMIT, userInfo.getUserId().toString(), ca.getPublishType());
							Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(ca.getObjectId(), "pubresCheck"));
							wfTaskId = map.get("wfTaskId");
							jbpmExcutionService.doApprove(wfTaskId, userInfo.getUserId().toString(), ca.getPublishType());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String title = ca.getMetadataMap().get("title");
					http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DELETE_URL") + "?id=" + id);
					SysOperateLogUtils.addLog("publish_delete", "书名：" + title,LoginUserUtil.getLoginUser());

				} else {
					http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DELETE_URL") + "?id=" + id);
					if (ca.getStatus().equals(SystemConstants.ResourceStatus.STATUS5)) {
						String wfTaskId = "";
						// 暂时先不删除流程数据
						try {
							List<Task> tasks = jbpmExcutionService.getCurrentTasks(WorkFlowUtils.getExecuId(
											ca.getObjectId(), "pubresCheck"));
							jbpmExcutionService.endTask(tasks.get(0).getId(),ProcessConstants.SUBMIT, 
									userInfo.getUserId().toString(), ca.getPublishType());
							Map<String, String> map = getWorkFlowInfo(WorkFlowUtils.getExecuId(ca.getObjectId(), "pubresCheck"));
							wfTaskId = map.get("wfTaskId");
							jbpmExcutionService.doApprove(wfTaskId, userInfo.getUserId().toString(), ca.getPublishType());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					String title = ca.getMetadataMap().get("title");
					http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DELETE_URL") + "?id=" + id);
					SysOperateLogUtils.addLog("publish_delete", "书名：" + title,LoginUserUtil.getLoginUser());
				}
			}
		}
	}

	/**
	 * 流操作
	 * 
	 * @param execuId
	 * @return
	 */
	public Map<String, String> getWorkFlowInfo(String execuId) {
		Map<String, String> map = new HashMap<String, String>();
		List<Task> taskList = jbpmExcutionService.getCurrentTasks(execuId);
		Task task = null;
		if (taskList != null && taskList.size() > 0) {
			task = taskList.get(0);
		}
		if (task != null) {
			map.put("execuId", task.getExecutionId());
			map.put("wfTaskId", task.getId());
		}

		return map;
	}

	public List<Ca> getCaResourceByMoreCondition(
			Map<String, String> checkRepeatMetadate) {
		String params = "";
		try {
			JSONArray json = JSONArray.fromObject(checkRepeatMetadate);
			params = checkRepeatMetadate.toString();
			params = URLEncoder.encode(params, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String caList = http.executeGet(WebappConfigUtil
				.getParameter("PUBLISH_REPEAT_URL") + "?checkRepeat=" + params);
		Gson gson = new Gson();
		CaList list = gson.fromJson(caList, CaList.class);
		return list.getCas();
	}

	public List<Ca> getCaResourceByResVersion(String publishtype, String title,
			String creator, String resVersion, String isbn) {
		String params = "";
		try {
			JSONObject object = new JSONObject();
			object.put("title", title);
			object.put("creator", creator);
			object.put("isbn", isbn);
			params = object.toString();
			params = URLEncoder.encode(params, "utf-8");
			// params =
			// "title="+URLEncoder.encode(title,"utf-8")+"creator="+creator+"resVersion="+resVersion+"isbn="+isbn;
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		HttpClientUtil http = new HttpClientUtil();
		String caList = http.executeGet(WebappConfigUtil
				.getParameter("PUBLISH_REPEAT_URL")
				+ "?checkRepeat="
				+ params
				+ "&publish_type=" + publishtype + "&resVersion=" + resVersion);
		Gson gson = new Gson();
		CaList list = gson.fromJson(caList, CaList.class);
		return list.getCas();
	}

	/**
	 * 更新目录节点
	 * 
	 * @param nodeJson
	 */
	@SuppressWarnings("null")
	public String updateNode(String jsonFile, String treeEditOldName,
			String fileFlag, File restore, String fileObjectId, String nodeId,
			String objectId, String newFilePath, String title) throws Exception {
		String isDir = "";
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		try {
			Gson gson = new Gson();
			com.brainsoon.semantic.ontology.model.File uploadFile = new com.brainsoon.semantic.ontology.model.File();
			com.brainsoon.semantic.ontology.model.File file = null;
			if (jsonFile != null) {
				file = gson.fromJson(jsonFile,
						com.brainsoon.semantic.ontology.model.File.class);
				uploadFile.setName(file.getName());
				// uploadFile.setCaId(objectId);
				uploadFile.setCaId(file.getObjectId());
				uploadFile.setPath(newFilePath);
				if (nodeId != null) {
					uploadFile.setPid(nodeId);
				} else {
					uploadFile.setPid(file.getPid());
				}
				uploadFile.setObjectId(fileObjectId);
				if (treeEditOldName != null) {
					String treeEditNewName = file.getName();
					ModifyLog modifyLog = new ModifyLog();
					modifyLog.setResId(objectId);
					if (treeEditOldName.equals("") && !fileFlag.equals("")) {
						modifyLog.setModifyOld("addFile");
						modifyLog.setModifyNew(treeEditNewName);
					} else if (treeEditOldName.equals("")
							&& fileFlag.equals("")) {
						modifyLog.setModifyOld("addList");
						modifyLog.setModifyNew(treeEditNewName);
					} else if (!treeEditOldName.equals("")
							&& !treeEditNewName.equals("")) {
						modifyLog.setModifyOld(treeEditOldName);
						modifyLog.setModifyNew(treeEditNewName);
					}
					modifyLog.setModifyField("tree");
					modifyLog.setModifyTime(new Date());
					modifyLog.setRestype("type");
					getBaseDao().create(modifyLog);
				}
				if (treeEditOldName != null) {
					if (StringUtils.isNotBlank(newFilePath)) { // 目录重命名
						String oldRealPath = FILE_ROOT + file.getPath();
						File oldRealFile = new File(oldRealPath);
						File newRealFile = new File(FILE_ROOT + newFilePath);
						ResConverfileTask rcft = new ResConverfileTask();
						rcft.setResId(file.getObjectId());
						rcft.setSrcPath(newFilePath);
						rcft.setOldSrcPath(file.getPath());
						String desc = resConverfileTaskService
								.updateConverfileTask(rcft);
						if (!newRealFile.exists()) {
							newRealFile.mkdirs();
						}
						if (!file.getPath().equals(newFilePath)
								&& !title.equals("addNode")
								&& !title.equals("addRoot")) {
							FileUtils.copyDirectory(oldRealFile, newRealFile);
							if (oldRealFile.exists()
									&& oldRealFile.isDirectory()) {
								FileUtils.deleteDirectory(oldRealFile);
							} else if (oldRealFile.exists()
									&& oldRealFile.isFile()) {
								FileUtils.forceDelete(oldRealFile);
							}
						}
						logger.debug(desc);
					}
				}
			}
			if (fileObjectId == null && !"add".equals(title)
					&& !title.equals("addRoot")) {
				com.brainsoon.semantic.ontology.model.File uplodeFile = new com.brainsoon.semantic.ontology.model.File();
				String fileType = restore.getName().substring(
						restore.getName().lastIndexOf(".") + 1,
						restore.getName().length());
				uplodeFile.setPath(restore.getAbsolutePath()
						.replace(File.separator, "/").replace(FILE_ROOT, ""));
				uplodeFile.setCaId(objectId);
				uplodeFile.setAliasName(restore.getName());
				uplodeFile.setName(restore.getName());
				uplodeFile.setFileType(fileType);
				uplodeFile.setFileByte(restore.length() + "");
				uplodeFile.setCreate_time(new Date().getTime() + "");
				uplodeFile.setCreator(userInfo.getUserId() + "");
				uplodeFile.setMd5(MD5Util.getFileMD5String(restore));
				uplodeFile.setModified_time(new Date().getTime() + "");
				// uplodeFile.setModifieder("有名");
				uplodeFile.setVersion("01");
				uplodeFile.setIsDir("2");
				String ids = String.valueOf(new Date().getTime());
				uplodeFile.setId(ids);
				uplodeFile.setPid(nodeId);
				Gson gson1 = new Gson();
				HttpClientUtil http = new HttpClientUtil();
				String paraJson = gson1.toJson(uplodeFile);
				String result = http.postJson(PUBLISH_FILESAVE_URL, paraJson);
				SemanticResponse rtn = gson.fromJson(result,
						SemanticResponse.class);
				objectId = rtn.getObjectId();
				isDir = "2";
			} else {
				uploadFile.setIsDir("1");
				uploadFile.setId(file.getId());
				String paraJson = gson.toJson(uploadFile);
				logger.debug(paraJson);
				HttpClientUtil http = new HttpClientUtil();
				String result = http.postJson(PUBLISH_FILESAVE_URL, paraJson);
				SemanticResponse rtn = gson.fromJson(result,
						SemanticResponse.class);
				objectId = rtn.getObjectId();
				isDir = "1";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		String path = restore.getAbsolutePath().replace(File.separator, "/")
				.replace(FILE_ROOT, "");
		return objectId + "," + isDir + "," + path;
	}

	//
	/**
	 * 删除目录节点
	 * 
	 * @param caId
	 * @param nodeId
	 * @throws Exception
	 */
	public void deleteNode(String caId, String fileObjectId, String path,
			String name, String deleteType) throws Exception {
		String realPath = FILE_ROOT + path;
		File file = new File(realPath);
		if (file.exists()) {
			if (file.isDirectory()) {
				FileUtils.deleteDirectory(file);
			} else {
				FileUtils.forceDelete(file);
			}
		}
		ModifyLog modifyLog = new ModifyLog();
		modifyLog.setResId(caId);
		modifyLog.setModifyField("tree");
		modifyLog.setModifyOld(deleteType);
		modifyLog.setModifyNew(name);
		modifyLog.setModifyTime(new Date());
		modifyLog.setRestype("type");
		getBaseDao().create(modifyLog);
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(WebappConfigUtil
				.getParameter("PUBLISH_DEL_NODE_URL")
				+ "?caId="
				+ caId
				+ "&nodeId=" + fileObjectId);
	}

	/**
	 * 分页查询，智能添加前台参数，读取rdf库
	 * 
	 * @param request
	 * @param conditionList
	 * @return String json
	 */
	public String queryResource4Page(HttpServletRequest request,
			QueryConditionList conditionList, String queryUrl) {
		SearchParamCa searchParamCa = new SearchParamCa();
		String page = request.getParameter("page");
		if (StringUtils.isEmpty(page)) {
			page = "1";
		}
		searchParamCa.setPage(page);
		searchParamCa.setSorts(request.getParameter("sort"));
		searchParamCa.setOrders(request.getParameter("order"));
		searchParamCa.setSize(conditionList.getPageSize());
		// 组装查询
		if (null != conditionList) {
			List<QueryConditionItem> items = conditionList.getConditionItems();
			String metadataMap = "{";
			for (QueryConditionItem queryConditionItem : items) {
				String filedName = queryConditionItem.getFieldName();
				Object value = queryConditionItem.getValue();
				String values = "";
				if (value != null) {
					values = ResUtils.solrCodeModify(value.toString());
				}
				if (StringUtils.isNotBlank(filedName) && value != null) {
					if ("1".equals(queryConditionItem.getIsMetadata())) {
						if (value != null && !"".equals(value.toString())
								&& !"null".equals(value.toString())) {
							metadataMap += "\"" + filedName + "\":\"" + values
									+ "\",";
						}
					} else {
						try {
							BeanUtils.setProperty(searchParamCa, filedName,
									values);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			if (metadataMap.length() > 1) {
				metadataMap = metadataMap
						.substring(0, metadataMap.length() - 1);
				metadataMap += "}";
				String metadataMapUTF8 = "";
				metadataMapUTF8 = metadataMap;
				searchParamCa.setMetadataMap(metadataMapUTF8);
			}
		}
		HttpClientUtil http = new HttpClientUtil();
		// 判断不是组织机构授权
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		// 判断是不是个人用户授权 0:未授权   1:已授权
		int isPrivate = LoginUserUtil.getLoginUser().getIsPrivate();
		if (isPrivate == 1) {    //表示已授权
			/*if (StringUtils.isNotBlank(userIds)) {
			} else {
				userIds = LoginUserUtil.getLoginUser().getUserId() + "";
			}*/
			userIds = LoginUserUtil.getLoginUser().getUserId() + "";
		}
		
		//如果授权了则只能看到自己上传的资源，如果未授权则可以看到当前登录人所属的数据权限和部门权限相同部分的用户组
		if (StringUtils.isNotBlank(userIds)) {
			if (userIds.endsWith(",")) {
				userIds = userIds.substring(0, userIds.length() - 1);
			}
			searchParamCa.setCreator(userIds);
		} else {
			searchParamCa.setCreator("-2");
		}

		// POST提交
		Gson gson = new Gson();
		String paramStrs = gson.toJson(searchParamCa);
		String result = http.postJson(queryUrl, paramStrs);
		return result;
	}

	/**
	 * 分页查询,带有动态生成的条件的查询
	 * 
	 * @param request
	 * @param conditionList
	 * @return String json
	 */
	public String queryResource4PageByParam(HttpServletRequest request,
			int page, Long size, String queryUrl, String pageSize,
			QueryConditionList conditionList, String targetNames) {
		StringBuffer hql = new StringBuffer();
		hql.append("page=").append(page).append("&total=").append(size)
				.append("&size=").append(pageSize);
		hql.append("&sort=").append("create_time").append("&order=")
				.append("desc");
		if (request.getAttribute("publishType") != null)
			hql.append("&publishType=").append(
					request.getAttribute("publishType"));
		if (request.getAttribute("status") != null)
			hql.append("&status=").append(request.getParameter("status"));
		List<MetadataDefinition> metadataDefinitions = MetadataSupport
				.getAllMetadateDefineList();
		String metadataMap = "{";
		if (null != conditionList) {
			List<QueryConditionItem> items = conditionList.getConditionItems();
			for (QueryConditionItem queryConditionItem : items) {
				String filedName = queryConditionItem.getFieldName();
				if ("1".equals(queryConditionItem.getIsMetadata())) {
					Object value = queryConditionItem.getValue();
					if (value != null && !"".equals(value.toString())
							&& !"null".equals(value.toString())) {
						try {
							metadataMap += "\"" + filedName + "\":\""
									+ URLEncoder.encode(value + "", "utf-8")
									+ "\",";
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				} else {
					try {
						hql.append("&")
								.append(filedName)
								.append("=")
								.append(URLEncoder.encode(
										queryConditionItem.getValue() + "",
										"utf-8"));
					} catch (UnsupportedEncodingException e) {
						logger.error(e.getMessage());
					}
				}
			}
		}
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			int queryModel = metadataDefinition.getQueryModel();
			if (queryModel == 4) {
				String startField = metadataDefinition.getFieldName()
						+ "_metaField_StartDate";
				String endField = metadataDefinition.getFieldName()
						+ "_metaField_EndDate";
				String startValue = request.getParameter(startField);
				String endValue = request.getParameter(endField);
				if (startValue != null && !"".equals(startValue)
						&& !"undefined".equals(startValue)) {
					metadataMap += "\"" + startField + "\":\"" + startValue
							+ "\",";
				}
				if (endValue != null && !"".equals(endValue)
						&& !"undefined".equals(endValue)) {
					metadataMap += "\"" + endField + "\":\"" + endValue + "\",";
				}
			} else {
				String fieldName = metadataDefinition.getFieldName()
						+ "_metaField";
				String paramValue = request.getParameter(fieldName);
				if (paramValue != null && !"".equals(paramValue)
						&& !"undefined".equals(paramValue)) {
					metadataMap += "\"" + metadataDefinition.getFieldName()
							+ "\":\"" + paramValue + "\",";
				}
			}
		}
		String names = "";
		if (StringUtils.isNotBlank(targetNames)) {
			String targetValue[] = targetNames.split(",");
			if (targetValue.length > 1) {
				for (int i = 1; i < targetValue.length; i++) {
					names = names + targetValue[i] + ",";
				}
				names = names.substring(0, names.length() - 1);
				if(names.contains("\n")){
					names = names.replaceAll("\n",",");
				}
				if(names.contains("\r")){
					names = names.replaceAll("\r",",");
				}
				metadataMap += "\"" + targetValue[0] + "\":\"" + names + "\""
						+ ",";
			}
		}
		if (metadataMap.length() > 1) {
			metadataMap = metadataMap.substring(0, metadataMap.length() - 1);
			metadataMap += "}";
			String metadataMapUTF8 = "";
			try {
				metadataMapUTF8 = URLEncoder.encode(metadataMap, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			hql.append("&metadataMap=").append(metadataMapUTF8);
		}
		String userIds = LoginUserUtil.getLoginUser().getDeptUserIds();
		// 判断是不是个人用户授权
		int isPrivate = LoginUserUtil.getLoginUser().getIsPrivate();
		if (isPrivate == 1) {
			if (StringUtils.isNotBlank(userIds)) {
			} else {
				userIds = LoginUserUtil.getLoginUser().getUserId() + "";
			}
		}
		// if (isPrivate == 1) {
		// if(StringUtils.isNotBlank(userIds)){
		// if(userIds.endsWith(",")){
		// userIds += LoginUserUtil.getLoginUser().getUserId();
		// }else{
		// userIds += "," + LoginUserUtil.getLoginUser().getUserId();
		// }
		// }else{
		// userIds = LoginUserUtil.getLoginUser().getUserId()+"";
		// }
		// }

		if (StringUtils.isNotBlank(userIds)) {
			if (userIds.endsWith(",")) {
				userIds = userIds.substring(0, userIds.length() - 1);
			}
			hql.append("&creator=" + userIds);
		} else {
			hql.append("&creator=-2");
		}
		HttpClientUtil http = new HttpClientUtil();
		String result = http.executeGet(queryUrl + "?" + hql.toString());
		logger.debug(result);
		return result;
	}

	public void backWriteStatus(UploadTask task, int successNum, int realNum) {
		if (successNum == 0) {
			// 全部失败
			task.setStatus(ImportStatus.STATUS4);
		} else if (realNum == successNum) {
			// 全部成功
			task.setStatus(ImportStatus.STATUS2);
		} else {
			// 部分成功
			task.setStatus(ImportStatus.STATUS3);
		}
	}

	@Override
	public String fileSize(HttpServletRequest request,
			HttpServletResponse response, String objectIds, String encryptPwd) {
		String boo = "0";
		Long srcSizeNum = (long) 0;
		Long dictSizeNum = (long) 0;
		String dictFile = "fileSize";
		List<SysParameter> dictSize = sysParameterService
				.findParaValue(dictFile);
		if (dictSize != null && dictSize.size() > 0) {
			dictSizeNum = Long.parseLong(dictSize.get(0).getParaValue());
		}
		
		
		if (StringUtils.isNotBlank(objectIds)) {
			String[] ids = StringUtils.split(objectIds, ",");
			
			for (String objectId : ids) {
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail = http
						.executeGet(WebappConfigUtil
								.getParameter("PUBLISH_DETAIL_URL")
								+ "?id="
								+ objectId);
				Gson gson = new Gson();
				Ca ca = gson.fromJson(resourceDetail, Ca.class);
				if (ca.getRealFiles() != null && ca.getRealFiles().size() > 0) {
					List<com.brainsoon.semantic.ontology.model.File> files = ca
							.getRealFiles();
					if (files != null) {
						for (com.brainsoon.semantic.ontology.model.File file : files) {
							if (file.getFileByte() != null) {
								srcSizeNum = srcSizeNum
										+ Long.parseLong(file.getFileByte());
							}
						}
					}
				}
			}
			// srcSizeNum = srcSizeNum / 1024;
		}
		if (srcSizeNum > dictSizeNum) {
			logger.error("下载文件超过限制大小");
			boo = "1";
		}
		return boo;
	}

	/**
	 * 根据资源ID获得改资源上一次的修改记录
	 * 
	 * @param resId
	 */
	public List<ModifyLog> queryModifyLogsByResId(String resId) {
		List<ModifyLog> list = query("from ModifyLog where resId='" + resId
				+ "'");
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	/**
	 * 批量保存标签
	 */
	@Override
	public String updateBeachSaveTarget(Ca ca, String targetName,
			String targetField) {
		String targetNames[] = targetName.split(",");
		String metaMapTarget[] = null;
		String totalTarget = "";
		String[] result_union = null;
		String tarNumValue = "";
		String hql = "";
		String result = "0";
		String metaTarget = ca.getMetadataMap().get(targetField);
		List<String> targetNameList = new LinkedList<String>();
		if (ca.getMetadataMap().get(targetField) == null
				|| "".equals(ca.getMetadataMap().get(targetField))) {
			for (int y = 0; y < targetNames.length; y++) {
				totalTarget = totalTarget + targetNames[y] + ",";
			}
		} else {
			metaMapTarget = ca.getMetadataMap().get(targetField).split(",");
			result_union = union(targetNames, metaMapTarget);
			for (int i = 0; i < result_union.length; i++) {
				totalTarget = totalTarget + result_union[i] + ",";
			}
		}
		if(result_union!=null){
			String metaArray[] = metaTarget.split(",");
			for(int u=0;u<result_union.length;u++){
				boolean tag = false;
				for(int y=0;y<metaArray.length;y++){
					if(result_union[u].equals(metaArray[y])){
						tag = true;
						break;
					}
				}
				if(!tag){
					tarNumValue = tarNumValue + result_union[u]+",";
				}
			}
			if(StringUtils.isNotBlank(tarNumValue)){
				if(tarNumValue.endsWith(",")){
					tarNumValue = tarNumValue.substring(0, tarNumValue.length() - 1);
				}
			}
			if(StringUtils.isNotBlank(tarNumValue)){
			targetNames = tarNumValue.split(",");
			List<Long> targetNameList1 = new LinkedList<Long>();
			for (int o = 0; o < targetNames.length; o++) {
				Long num = (long) 0;
				hql = "select targetName from ResTargetData  where targetName='"
						+ targetNames[o] + "' and module='"+ca.getPublishType()+"' and pid!='-1'";
				targetNameList = (List<String>) baseDao.query(hql);
					if (targetNameList.size() > 0) {
						hql = "select targetNum from ResTargetData  where targetName='"
								+ targetNames[o] + "' and module='"+ca.getPublishType()+"' and pid!='-1'";
						targetNameList1 = (List<Long>) baseDao.query(hql);
						if (targetNameList1.size() > 0) {
							num = targetNameList1.get(0) + 1;
						}
						hql = "UPDATE ResTargetData SET targetNum=" + num
								+ " where targetName='" + targetNames[o] + "' and module='"+ca.getPublishType()+"'";
						baseDao.updateWithHql(hql);
					}
				}
			}
			totalTarget = totalTarget.substring(0, totalTarget.length() - 1);
			ca.getMetadataMap().put(targetField, totalTarget);
			Date date = new Date();
			ca.setUpdateTime(date.getTime()+"");
			logger.info("targetField"+targetField);
			Gson gson = new Gson();
			String paraJson = gson.toJson(ca);
			HttpClientUtil http = new HttpClientUtil();
			logger.info("paraJson"+paraJson);
			result = http.postJson(PUBLISH_SAVE_URL, paraJson);
		}else if(StringUtils.isNotBlank(totalTarget)){
			totalTarget = totalTarget.substring(0, totalTarget.length() - 1);
			List<Long> targetNameList1 = new LinkedList<Long>();
			for (int o = 0; o < targetNames.length; o++) {
				Long num = (long) 0;
				hql = "select targetName from ResTargetData  where targetName='"
						+ targetNames[o] + "' and module='"+ca.getPublishType()+"' and pid!='-1'";
				targetNameList = (List<String>) baseDao.query(hql);
				if (targetNameList.size() > 0) {
					hql = "select targetNum from ResTargetData  where targetName='"
							+ targetNames[o] + "' and module='"+ca.getPublishType()+"'";
					targetNameList1 = (List<Long>) baseDao.query(hql);
					if (targetNameList1.size() > 0) {
						num = targetNameList1.get(0) + 1;
					}
					hql = "UPDATE ResTargetData SET targetNum=" + num
							+ " where targetName='" + targetNames[o] + "' and module='"+ca.getPublishType()+"' and pid!='-1'";
					baseDao.updateWithHql(hql);
				}
			}
			ca.getMetadataMap().put(targetField, totalTarget);
			Date date = new Date();
			ca.setUpdateTime(date.getTime()+"");
			logger.info("targetField"+targetField);
			Gson gson = new Gson();
			String paraJson = gson.toJson(ca);
			HttpClientUtil http = new HttpClientUtil();
			logger.info("paraJson"+paraJson);
			result = http.postJson(PUBLISH_SAVE_URL, paraJson);
			
		}
		return result;
	}

	// 求两个字符串数组的并集，利用set的元素唯一性
	public String[] union(String[] arr1, String[] arr2) {
		Set<String> set = new HashSet<String>();
		for (String str : arr1) {
			set.add(str);
		}
		for (String str : arr2) {
			set.add(str);
		}
		String[] result = {};
		return set.toArray(result);
	}

	/**
	 * 目录重命名（在录入资源的时候本地文件目录已经改名只需要更新rdf数据库即可）
	 * 
	 * @param nodeJson
	 */
	public String doRenameNode(String caObjectId, String fileObjectId,
			String newName, String oldName) throws Exception {
		try {
			HttpClientUtil http = new HttpClientUtil();
			String result = http.executeGet(PUBLISH_RENAME_FILE + "?objectId="
					+ fileObjectId + "&name=" + newName);
			if ("0".equals(result)) {
				ModifyLog modifyLog = new ModifyLog();
				modifyLog.setResId(caObjectId);
				modifyLog.setModifyOld(oldName);
				modifyLog.setModifyNew(newName);
				modifyLog.setModifyField("tree");
				modifyLog.setModifyTime(new Date());
				modifyLog.setRestype("type");
				getBaseDao().create(modifyLog);
				return "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "-1";
	}

	/**
		 * 
		 */
	@Override
	public CaList beachImportCheckRepeat(String id, String excelPath,
			String publishType) throws Exception {
		String jsonMap = "";
		int excelNum = 0;
		StringBuffer hql = new StringBuffer();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		List<UploadTaskDetail> list = query("from UploadTaskDetail where id="
				+ Integer.parseInt(id));
		if (list.size() > 0 && list.get(0).getCheckRepeatField() != null
				&& !"".equals(list.get(0).getCheckRepeatField())) {
			// List<ResTaskDetail> valueList = list.get(0).getValueList();
			jsonMap = list.get(0).getCheckRepeatField();
		}
		if (list.size() > 0 && list.get(0).getExcelNum() != 0
				&& !"".equals(list.get(0).getExcelNum())) {
			excelNum = list.get(0).getExcelNum();
		}
		try {
			jsonMap = URLEncoder.encode(jsonMap, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		CaList caList = null;
		if (!"".equals(jsonMap)) {
			String result = http.executeGet(PUBLISH_REPEAT_URL
					+ "?checkRepeat=" + jsonMap + "&publish_type="
					+ publishType);
			caList = (CaList) gson.fromJson(result, CaList.class);
		} else {
			caList = null;
		}
		return caList;
	}

	@Override
	public Ca installCa(String id, Ca ca, String excelPath) throws Exception {
		int excelNum = 0;
		String filePath = "";
		List<UploadTaskDetail> list = query("from UploadTaskDetail where id=" + Integer.parseInt(id));
		
		if (list.size() > 0 && list.get(0).getTask().getFiletype()==1) {
			if (list.get(0).getExcelNum() != 0 && !"".equals(list.get(0).getExcelNum())) {
				excelNum = list.get(0).getExcelNum();
			}
			FileInputStream is = new FileInputStream(excelPath);
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			HSSFRow tempRowNum = sheet.getRow(excelNum - 1);
			HSSFRow tempRowMark = sheet.getRow(1);
			int num = tempRowNum.getLastCellNum();
			String metaMap[] = new String[num];
			String metaMapMark[] = new String[num];
			HSSFCell tempCell = null;
			for (int i = 0; i < num; i++) {
				if (tempRowNum.getCell(i) != null
						&& !"".equals(tempRowNum.getCell(i))) {
					// int type = tempRowNum.getCell(i).getCellType();
					String cellContent = "";
					tempCell = tempRowNum.getCell(i);
					int type = tempCell.getCellType();
					if (HSSFCell.CELL_TYPE_NUMERIC == type) {
						DecimalFormat df = new DecimalFormat("0");
						cellContent = df.format(tempCell.getNumericCellValue());
						if (HSSFDateUtil.isCellDateFormatted(tempCell)) {
							cellContent = df.format(tempCell.getNumericCellValue());
							Date d = tempCell.getDateCellValue();
							DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
							cellContent = formater.format(d);
							metaMap[i] = cellContent;
						}
						metaMap[i] = cellContent;

					} else {
						metaMap[i] = tempRowNum.getCell(i).toString();
					}

				}
				if (tempRowMark.getCell(i) != null
						&& !"".equals(tempRowMark.getCell(i))) {
					metaMapMark[i] = tempRowMark.getCell(i).toString();
				}
			}
			JSONObject jsonMap = new JSONObject();
			for (int j = 1; j < num; j++) {
				if (metaMap[j] != null && metaMapMark[j] != null) {
					jsonMap.put(metaMapMark[j], metaMap[j]);
				}
			}
			String src = "";
			List<SysParameter> sr = sysParameterService.findParaValue("bachImportExcelPath");
			if (sr != null && sr.size() > 0) {
				if (sr.get(0) != null && sr.get(0).getParaValue() != null) {
					src = sr.get(0).getParaValue();
				}
			}
			src = src.replaceAll("\\\\", "/");
			if (!src.endsWith("/")) {
				src = src + "/";
				src = src + ca.getPublishType();
			}
			ca.setMetadataMap(jsonMap);
			if (StringUtils.isNotBlank(tempRowNum.getCell(0).toString())) {
				filePath = tempRowNum.getCell(0).toString();
				filePath = src + "/" + filePath;
				ca.setUploadFile(filePath);
			} else {
				ca.setUploadFile("");
			}
		}else if(list.get(0).getTask().getFiletype()==2){
			UploadTask uploadTask = new UploadTask();
			uploadTask.setLibType(ca.getPublishType());
			
			Map<String, String> maps = null;//解析excel或xml的返回的数据
			List<Map<String, String>> xmlList = new ArrayList<Map<String,String>>();
			
			String path = list.get(0).getPaths();
			xmlList=batchImportResService.savefile(path,uploadTask,list.get(0));
			maps=xmlList.get(0);
			ca.setMetadataMap(maps);
			if(!new File(path).exists()){
				path = "";
			}
			ca.setUploadFile(path);
			//处理批次编号信息 从主表的ExcelPath字段中截取
			String batchPath = list.get(0).getTask().getExcelPath().replaceAll("\\\\", "/").replaceAll("//", "/");;
			String batchNum = batchPath.substring(batchPath.lastIndexOf("/")+1);
			ca.getMetadataMap().put("batchNum", batchNum);
		}
		
		
		
		return ca;
	}

	@Override
	public boolean updateStatus(String id, String taskId) throws Exception {
		boolean flag = false;
		int excelNum = 0;
		int failNum = 0;
		int succNum = 0;
		String hql = "";
		List<UploadTaskDetail> uploadTaskDetails = query("from UploadTaskDetail where id="+ Integer.parseInt(id));
		List<UploadTask> uploadTasks = query("from UploadTask where id="+ Integer.parseInt(taskId));
		if (uploadTasks.size() > 0 && uploadTasks.get(0).getFailNum() != 0 && !"".equals(uploadTasks.get(0))) {
			failNum = uploadTasks.get(0).getFailNum();
			succNum = uploadTasks.get(0).getSuccNum();
			if (failNum != 0) {
				--failNum;
				++succNum;
				hql = "UPDATE UploadTask SET failNum=" + failNum + ",succNum="
						+ succNum + ",status=" + 3 + "  where id="+ Integer.parseInt(taskId);
				flag = baseDao.updateWithHql(hql);
			}
			// 全部成功
			if (failNum == 0) {
				hql = "UPDATE UploadTask SET status=" + 4 + " where id=" + Integer.parseInt(taskId);
				flag = baseDao.updateWithHql(hql);
			}
		}
		if (uploadTaskDetails.size() > 0 && uploadTaskDetails.get(0).getExcelNum() != 0 && !"".equals(uploadTaskDetails.get(0).getExcelNum())) {
			excelNum = uploadTaskDetails.get(0).getExcelNum();
			hql = "UPDATE UploadTaskDetail SET status='" + 3 + "', remark='第【"
					+ excelNum + "】行，导入成功！',importStatus='导入成功！' where id="+ Integer.parseInt(id);
			flag = baseDao.updateWithHql(hql);
		}
		return flag;
	}

	public static void main(String args[]) {
		HttpClientUtil http = new HttpClientUtil();
//		http://10.130.29.26:8080/semantic_index_server/publish/ontologyListQuery/filesByPublishType?publish_type=1&privilage=141&page=1&size=100

		String url = "http://10.130.29.26:8080/semantic_index_server/publish/ontologyListQuery/filesByPublishType?publish_type=1&privilage=141&page=1&size=10";
		String resource = http.executeGet(url);
		// String json =
		// "{\"fileId\":\"11/23/34.jpg\",\"resId\":\"urn:publish-249716eb-a2d9-4772-a707-8b29534d2aa0\",\"fileFormat\":\"jpg\",\"srcPath\":\"E:/Workspaces/BSFW/WebRoot/fileDir/fileRoot/1/G00001/tt_tt-tttt_wwww_116_01_00000555/11/23/34.jpg\",\"resultConveredfilePath\":\"E:/Workspaces/BSFW/WebRoot/fileDir/converFileRoot/1/G00001/tt_tt-tttt_wwww_116_01_00000555/11/23/jpg/34.jpg\"}";
		// HttpClientUtil http = new HttpClientUtil();
		// try {
		// json = URLEncoder.encode(json, "UTF-8");
		// DoFileQueueList doFileList = new DoFileQueueList();
		// DoFileQueue doFileQueue1 = new DoFileQueue();
		// doFileQueue1.setResId("test01");
		// doFileQueue1.setFileId("test01");
		// doFileQueue1.setSrcPath("/home/szgl/byserver/test/视频/平行线.mp4");
		// doFileQueue1.setPendingType("0");
		// doFileQueue1
		// .setResultConveredfilePath("/home/szgl/byserver/123/视频/test01.flv");
		//
		// DoFileQueue doFileQueue2 = new DoFileQueue();
		// doFileQueue2.setResId("test02");
		// doFileQueue2.setFileId("test02");
		// doFileQueue2.setSrcPath("/home/szgl/byserver/test/音频/渡口.flac");
		// doFileQueue2.setPendingType("0");
		// doFileQueue2
		// .setResultConveredfilePath("/home/szgl/byserver/123/音频/test05.mp3");
		//
		// doFileList.addDoFileQueue(doFileQueue1);
		// doFileList.addDoFileQueue(doFileQueue2);
		// // String result = http.executeGet(PUBLISH_FILE_WRITE_QUEUE +
		// // "?fileQueueJsonStr="+json);
		// Gson gson = new Gson();
		// String result = http.postJson(
		// "http://10.130.39.1:8099/fileService/writeToQueue",
		// gson.toJson(doFileList));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		String var = "abcdefgggg";
		String var1 = var.substring(0, var.length() - 4);
		System.out.println(var1);
	}
//创建ftp下载
	@Override
	public String createHttpFtpDownload(String ids, String encryptPwd,
			String ftpFlag, String encryptZip, String isComplete) {
			UserInfo userInfo = LoginUserUtil.getLoginUser();
			String beachName = "";
			boolean flag = false;
			int fileEmptyNum = 0;
		try {
			if (StringUtils.isNotBlank(ids)) {
				String uuidPath = encryptZip;
				String title = "";
				Long totalSize = 0L;
				String idsArray[] = ids.split(",");
				//创建主表
				FileDownName fileTask = new FileDownName();
				//保存待处理状态3
				fileTask.setStatus("3");
				//保存初始创建路径
				fileTask.setFtpPath(uuidPath);
				//保存加密密钥
				fileTask.setPwd(encryptPwd);
				//fileTask.setCreateTime(new Date());
				//保存是否压缩
				fileTask.setIsComplete(isComplete);
				//保存下载方式
				fileTask.setDownloadType("FTP");
				//保存下载人
				fileTask.setDownloadUser(userInfo.getUsername());
				//保存登录人
				fileTask.setLoginUser(userInfo.getName());
				//保存批次名称
				if(uuidPath.endsWith("/")){
					beachName = uuidPath.substring(0,uuidPath.length()-1);
				}
				fileTask.setResName(beachName);
				//保存创建日期
				fileTask.setCreateTime(new Date());
				sysParameterService.create(fileTask);
				// 获取业务类处理
				logger.info("cccccccc=============");
				for (int i = 0; i < idsArray.length; i++) {
					String path = "";
					Long fileSize = 0L;
					HttpClientUtil http = new HttpClientUtil();
					String resourceDetail = http.executeGet(WebappConfigUtil
							.getParameter("PUBLISH_DETAIL_URL")
							+ "?id="
							+ idsArray[i]);
					Gson gson = new Gson();
					Ca ca = gson.fromJson(resourceDetail, Ca.class);
					title = MetadataSupport.getTitle(ca);
					//创建从表详细表
					FileDownValue fileValue = new FileDownValue();
					//保存资源resId
					fileValue.setResId(idsArray[i]);
					//保存title
					fileValue.setResName(title);
					//保存待处理状态
					fileValue.setStatus("3");
					fileValue.setTask(fileTask);
					//保存初始创建路径
					fileValue.setFtpPath(uuidPath);
					//保存密钥
					fileValue.setPwd(encryptPwd);
					//判断Ca文件
					if (ca != null) {
						if (ca.getRealFiles() != null) {
							String rootPath = ca.getRootPath();
//							rootPath = rootPath.replaceAll("\\\\", "/");
							path = StringUtils.replace(
									WebAppUtils.getWebAppBaseFileDirFR(), "\\\\", "/")
									+ rootPath;
							path = path.replaceAll("\\\\", "/");
							int realNum = ca.getRealFiles().size();
							for (int j = 0; j < realNum; j++) {
								if (ca.getRealFiles().get(j).getFileByte() != null
										&& ca.getRealFiles().get(j).getIsDir()
												.equals("2")) {
									//计算单个资源的文件总数量
									fileSize = fileSize
											+ Long.parseLong(ca.getRealFiles()
													.get(j).getFileByte());
								}
							}
							//累加所有资源总文件数量
							totalSize = totalSize + fileSize;
						}else{
							//变量统计为空文件个数
							fileEmptyNum++;
							//为空标记
							flag = true;
							//如果为空
//							fileTask.setFileSize(totalSize + "");
							fileTask.setCreateTime(new Date());
							fileValue.setFtpPath("无文件");
							fileValue.setTotalFileSize(fileSize + "");
							fileValue.setCreateTime(new Date());
							//直接保存处理完状态
							fileValue.setStatus("1");
						}
						fileTask.setFileSize(totalSize + "");
						fileValue.setTotalFileSize(fileSize + "");
						fileValue.setFilePath(path);
//						sysParameterService.update(fileTask);
						sysParameterService.create(fileValue);
					}else{
						continue;
					}
				}
				fileTask.setFileSize(totalSize + "");
				sysParameterService.update(fileTask);
				//将主表状态改为已完成
				if(flag && fileEmptyNum == idsArray.length){
					fileTask.setStatus("1");
					sysParameterService.update(fileTask);
				}
			}
			new Thread(FtpCopyFileThread.getInstance()).start();
		} catch (Exception e) {
			logger.info("wwwwwwwwwwwwwwwww" + e.getMessage());
			e.printStackTrace();
		}

		return ftpFlag;
	}


	/**
	 * 资源预览 资源树
	 * 
	 * @param bookCa
	 * @param objectId
	 * @return
	 */
	public ModelMap getZtreeJson(Ca bookCa, String objectId, ModelMap model) {
		if (bookCa.getRealFiles() != null) {
			List<com.brainsoon.semantic.ontology.model.File> realFiles = bookCa
					.getRealFiles();
			Long userid  =  LoginUserUtil.getLoginUser().getUserId();
			User user = (User) userService.getByPk(User.class, userid);
			String resourceDataJson = user.getResourceDataJson();
			
			JSONArray jsonArray = JSONArray.fromObject(resourceDataJson);
			String fields = "";
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if(jsonObject.getString("id").equals(bookCa.getPublishType())){
					fields = jsonObject.getString("field");
				}
			}
			if (realFiles != null && realFiles.size() > 0) {
				JSONObject ztreeObj = new JSONObject();
				JSONArray ztreeArray = new JSONArray();
				String rootpath = "";
				String fieldpath = "";
				for (int u = 0; u < realFiles.size(); u++) {
					com.brainsoon.semantic.ontology.model.File file = realFiles
							.get(u);
					if(file.getPid().equals("-1")){
						rootpath = file.getId();
						if (file.getId() != null) {
							String nodeId = file.getId();
							nodeId = nodeId.replaceAll("\\\\", "/");
							ztreeObj.put("nodeId", nodeId);
						}
						if (file.getPid() != null) {
							String pid = file.getPid();
							pid = pid.replaceAll("\\\\", "/");
							ztreeObj.put("pid", pid);
						}
						if (file.getName() != null) {
							ztreeObj.put("name", file.getName());
						}
						if (file.getPath() != null) {
							String path = file.getPath();
							path = path.replaceAll("\\\\", "/");
							ztreeObj.put("path", path);
						}
						if (file.getObjectId() != null) {
							ztreeObj.put("object", file.getObjectId());
						}
						if (file.getIsDir() != null) {
							ztreeObj.put("isDir", file.getIsDir());
						}
						if (file.getMd5() != null) {
							ztreeObj.put("Md5", file.getMd5());
						}
						ztreeArray.add(ztreeObj);
					}else if(file.getPid().equals(rootpath)){
						String fieldss[]=fields.split(",");
						for (String field : fieldss) {
							if(file.getId().toLowerCase().contains("/"+field.trim().toLowerCase())){
								fieldpath = file.getId();
								if (file.getId() != null) {
									String nodeId = file.getId();
									nodeId = nodeId.replaceAll("\\\\", "/");
									ztreeObj.put("nodeId", nodeId);
								}
								if (file.getPid() != null) {
									String pid = file.getPid();
									pid = pid.replaceAll("\\\\", "/");
									ztreeObj.put("pid", pid);
								}
								if (file.getName() != null) {
									ztreeObj.put("name", file.getName());
								}
								if (file.getPath() != null) {
									String path = file.getPath();
									path = path.replaceAll("\\\\", "/");
									ztreeObj.put("path", path);
								}
								if (file.getObjectId() != null) {
									ztreeObj.put("object", file.getObjectId());
								}
								if (file.getIsDir() != null) {
									ztreeObj.put("isDir", file.getIsDir());
								}
								if (file.getMd5() != null) {
									ztreeObj.put("Md5", file.getMd5());
								}
								ztreeArray.add(ztreeObj);
							}
						}
					}else if(file.getPid().indexOf(fieldpath)==0){
						if (file.getId() != null) {
							String nodeId = file.getId();
							nodeId = nodeId.replaceAll("\\\\", "/");
							ztreeObj.put("nodeId", nodeId);
						}
						if (file.getPid() != null) {
							String pid = file.getPid();
							pid = pid.replaceAll("\\\\", "/");
							ztreeObj.put("pid", pid);
						}
						if (file.getName() != null) {
							ztreeObj.put("name", file.getName());
						}
						if (file.getPath() != null) {
							String path = file.getPath();
							path = path.replaceAll("\\\\", "/");
							ztreeObj.put("path", path);
						}
						if (file.getObjectId() != null) {
							ztreeObj.put("object", file.getObjectId());
						}
						if (file.getIsDir() != null) {
							ztreeObj.put("isDir", file.getIsDir());
						}
						if (file.getMd5() != null) {
							ztreeObj.put("Md5", file.getMd5());
						}
						ztreeArray.add(ztreeObj);
					}
					
				}
				ztreeArray.toString();
				if (StringUtils.isBlank(resourceDataJson) || resourceDataJson==null || "null".equals(resourceDataJson)) {
					model.addAttribute("ztreeJson", "");
				}else {
					model.addAttribute("ztreeJson", ztreeArray);
				}
			}
		}
		return model;
	}

	/**
	 * 分页查询文件
	 */
	public List<SearchResult> queryFile4Page(String page, String size, String startDate,
			String endDate, String createUser, String resName,
			String publishType,String fileExtensionName) {
//		publish_type=1&privilage=141&page=1&size=10";
		List<SearchResult> fileList = new ArrayList<SearchResult>();
		String []publishTypeArray = publishType.split(",");
		String resource = "";
		Gson gson=new Gson();
		SearchResult file = null;
		if(StringUtils.isNotBlank(resName)){
			try {
				resName = URLEncoder.encode(resName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		for(String resType:publishTypeArray){
			HttpClientUtil http = new HttpClientUtil();
			
			resource = http.executeGet(CA_FILES_BY_PUBLISHTYP + "?publish_type=" + resType+"&privilage="+createUser+"&page="+page+"&size="+size+"&resName="+resName+"&fileType="+fileExtensionName+"&createStartTime="+startDate+"&createEndTime="+endDate);
			try {
				file = gson.fromJson(resource, SearchResult.class);
				if(file.getRows().size()>0){
					fileList.add(file);
				}
			} catch (Exception e) {
				continue;
			}
			
		}
		return fileList;
	}

	@Override
	public Map<String,String> updateBeachReplace(List<Ca> cas, String field, String fieldValue) {
		String inputErrorValue = "";
		Map<String,String> map = new HashMap<String, String>();
		MetadataDefinition metadataDefinition = MetadataSupport.getMetadataDefinitionByName(field);
		if(metadataDefinition!=null){
			
			// fieldType 1单文本 5文本域 4单选 3多选 2下拉选择 7日期 8URL 6树形 11单位 10人员
			//输入类型为树形 需要转换
			if(metadataDefinition!=null && metadataDefinition.getFieldType()==6){
				if(fieldValue!=null && !"".equals(fieldValue)){
					if(StringUtils.isNotBlank(metadataDefinition.getValueRange())){
					String graph = fLTXService.getFLTXNodeByCode(Long.parseLong(metadataDefinition.getValueRange()), fieldValue);
						if("0".equals(graph) && metadataDefinition.getAllowNull() == 0){
							inputErrorValue = metadataDefinition.getFieldZhName() +":["+fieldValue+"]不符合规范";
							map.put("status", inputErrorValue);
						}else if("0".equals(graph) && metadataDefinition.getAllowNull() == 1){
							inputErrorValue = metadataDefinition.getFieldZhName() +":["+fieldValue+"]不符合规范";
							map.put("status", inputErrorValue);
						}else{
							for (Ca ca : cas) {
								ca.getMetadataMap().put(field, graph.trim());
								map = returnMapValue(ca);
							}
						}
					}
				}
			
			//输入类型为 4单选或  2下拉选择  数据转换
			}else if(metadataDefinition!=null && (metadataDefinition.getFieldType()==4 || metadataDefinition.getFieldType()==2)){
				logger.info("key:"+field+"value:"+fieldValue);
				 if(StringUtils.isNotBlank(fieldValue) && StringUtils.isNotBlank(field)){
					 String arr[] = fieldValue.split(",");
					 if (arr.length != 1) {
						 inputErrorValue = "["+metadataDefinition.getFieldZhName()+"]输入类型为单选或下拉选择,只能输入一个选项 ;";
						 map.put("status", inputErrorValue);
					 }else {
						String impt = GlobalDataCacheMap.getChildCodeByIdAndChildValue(metadataDefinition.getValueRange(), arr[0]);
						if(StringUtils.isBlank(impt)){
							impt = GlobalDataCacheMap.getNameValueWithIdByKeyAndChildValue(metadataDefinition.getValueRange(), arr[0]);
						}
						if(impt.endsWith(",")){
							impt = impt.substring(0,impt.length()-1);
						}
						if(StringUtils.isBlank(impt)){
							inputErrorValue = metadataDefinition.getFieldZhName() +":["+arr[0]+"]不符合规范";
							map.put("status", inputErrorValue);
						}else{
							for (Ca ca : cas) {
								ca.getMetadataMap().put(field, impt.trim());
								map = returnMapValue(ca);
							}
						}
					}
				 }
				
			//输入类型为3多选 数据转换
			}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==3){
				logger.info("key:"+field+"value:"+fieldValue);
				String code = "";
				boolean zhName = false;
				if(StringUtils.isNotBlank(fieldValue) && StringUtils.isNotBlank(field)){
					String arr[] = fieldValue.split(",");
					if (arr.length > Integer.parseInt(metadataDefinition.getValueLength())) {
						inputErrorValue = "["+metadataDefinition.getFieldZhName()+"]输入类型为多选,最多只能输入"+metadataDefinition.getValueLength()+"个选项 ;";
						map.put("status", inputErrorValue);
					}else {
						 for(int i=0;i<arr.length;i++){
							String impt = GlobalDataCacheMap.getChildCodeByIdAndChildValue(metadataDefinition.getValueRange(), arr[0]);
							if(StringUtils.isBlank(impt)){
								impt = GlobalDataCacheMap.getNameValueWithIdByKeyAndChildValue(metadataDefinition.getValueRange(), arr[0]);
							}
							if(StringUtils.isNotBlank(impt)){
									code = code + impt+",";
							}else{
								inputErrorValue = inputErrorValue+arr[i]+",";
								zhName = true;
							}
						}
						if(zhName==true&&StringUtils.isNotBlank(inputErrorValue)){
							inputErrorValue = metadataDefinition.getFieldZhName() +":["+inputErrorValue+"]不符合规范";
							map.put("status", inputErrorValue);
						}
					}
					if(code.endsWith(","))
						 code = code.substring(0,code.length()-1);
					if(inputErrorValue.endsWith(","))
						inputErrorValue = inputErrorValue.substring(0,inputErrorValue.length()-1);
					if(StringUtils.isBlank(inputErrorValue)){
						for (Ca ca : cas) {
							ca.getMetadataMap().put(field, code.trim());
							map = returnMapValue(ca);
						}
					}
				}
				
			//输入类型为10人员 数据转换
			}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==10){// && filetype==1
				logger.info("key:"+field+"value:"+fieldValue);
				String staffId="";
				if(fieldValue!=null && !"".equals(fieldValue)){
					String arr[] = fieldValue.split(",");
					if (arr.length > Integer.parseInt(metadataDefinition.getValueLength())) {
						inputErrorValue = "["+metadataDefinition.getFieldZhName()+"]输入类型为人员,最多只能输入"+metadataDefinition.getValueLength()+"个选项 ;";
						map.put("status", inputErrorValue);
					}else {
						 for(int i=0;i<arr.length;i++){
							 Staff staff = new Staff();
			            	staff.setName(arr[i]);
			            	String id = staffService.doSaveOrUpdate(staff);
			            	if(StringUtils.isNotBlank(id)){
			            		staffId = staffId + id + ",";
							}
						 }
						 if(staffId.endsWith(","))
							 staffId = staffId.substring(0,staffId.length()-1);
						 if(StringUtils.isNotBlank(staffId)){
							 for (Ca ca : cas) {
								 ca.getMetadataMap().put(field, staffId.trim());
								 map = returnMapValue(ca);
							 }
						 }else {
							 inputErrorValue = "["+metadataDefinition.getFieldZhName()+"]输入类型为人员,请确认输入数值的准确性;";
							 map.put("status", inputErrorValue);
						}
						 
					}
				}
			//输入类型为11单位数据转换
			}else if(metadataDefinition!=null && metadataDefinition.getFieldType()==11){// && filetype==1
				logger.info("key:"+field+"value:"+fieldValue);
				String companyId="";
				if(fieldValue!=null && !"".equals(fieldValue)){
					String arr[] = fieldValue.split(",");
					if (arr.length > Integer.parseInt(metadataDefinition.getValueLength())) {
						inputErrorValue = "["+metadataDefinition.getFieldZhName()+"]输入类型为单位,最多只能输入"+metadataDefinition.getValueLength()+"个选项 ;";
						map.put("status", inputErrorValue);
					}else {
						 for(int i=0;i<arr.length;i++){
							 Company company = new Company();
			            	company.setName(arr[i]);
			            	String id  = companyService.doSaveOrUpdate(company);
			            	if(StringUtils.isNotBlank(id)){
			            		companyId = companyId + id + ",";
							}
						 }
						 if(companyId.endsWith(","))
							 companyId = companyId.substring(0,companyId.length()-1);
						 if(StringUtils.isNotBlank(companyId)){
							 for (Ca ca : cas) {
								 ca.getMetadataMap().put(field, companyId.trim());
								 map = returnMapValue(ca);
							 }
						 }else {
							 inputErrorValue = "["+metadataDefinition.getFieldZhName()+"]输入类型为单位,请确认输入数值的准确性;";
							 map.put("status", inputErrorValue);
						}
						 
					}
				}
			//处理资源特殊标识11-版本
			}else if(metadataDefinition.getIdentifier()==12 || metadataDefinition.getIdentifier()==6){
				
				 for (Ca ca : cas) {
					 int num = ca.getNum();
						num++;
						ca.setNum(num);
						ca.getMetadataMap().put(field, fieldValue);
					 map = returnMapValue(ca);
				 }
			}else if(StringUtils.isNotBlank(fieldValue)&& StringUtils.isNotBlank(field)){
				for (Ca ca : cas) {
					 ca.getMetadataMap().put(field, fieldValue);
					 map = returnMapValue(ca);
				 }
				
			}
		}else{
			if(StringUtils.isNotBlank(fieldValue)&& StringUtils.isNotBlank(field)){
				for (Ca ca : cas) {
					ca.getMetadataMap().put(field, fieldValue.trim());
					 map = returnMapValue(ca);
				 }
			}
		}
		//处理版本逻辑
//		else if(metadataDefinition!=null && metadataDefinition.getIdentifier()==11){
//			if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
//				 for (Ca ca : cas) {
//					 ca.getMetadataMap().put(field, metadataDefinition.getDefaultValue());
//					 map = returnMapValue(ca);
//				 }
//			}else{
//				ca.getMetadataMap().put(field, "00");
//			}
//			 
//		}
//		Gson gson = new Gson();
//		String paraJson = gson.toJson(ca);
//		HttpClientUtil http = new HttpClientUtil();
//		if(StringUtils.isBlank(inputErrorValue)){
//			String result = http.postJson(PUBLISH_SAVE_URL, paraJson);
//			SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
//			try {
//				if(rtn.getState()==0){
//					inputErrorValue="操作成功";
//				}
//			} catch (Exception e) {
//				inputErrorValue="操作失败";
//			}
//		}
//		map.put("status", inputErrorValue);
		return map;
	}
	public Map<String,String> returnMapValue(Ca ca){
		String inputErrorValue = "";
		Map<String,String> map = new HashMap<String, String>();
		Gson gson = new Gson();
		String paraJson = gson.toJson(ca);
		HttpClientUtil http = new HttpClientUtil();
		if(StringUtils.isBlank(inputErrorValue)){
			String result = http.postJson(PUBLISH_SAVE_URL, paraJson);
			SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
			try {
				if(rtn.getState()==0){
					inputErrorValue="全部成功";
				}
			} catch (Exception e) {
				inputErrorValue="部分失败连接错误";
			}
		}
		map.put("status", inputErrorValue);
		return map;
	}
	
	/**
	 * 
	* @Title: getTMJson
	* @Description: 根据批量导入的路径获取下面的批次信息以及批次下面的资源信息
	* @param path	批量导入的路径
	* @return    参数
	* @return String    返回类型	json数据
	* @throws
	 */
	public String getTMJson(){
		// 赋值文件路径
		String importTMPath = "";
		List<SysParameter> lists = sysParameterService.findParaValue("importTmPath");
		if(lists!=null && lists.size()>0){
			if(lists.get(0)!=null && lists.get(0).getParaValue()!=null){
				importTMPath = lists.get(0).getParaValue();
			}
		}
		
		importTMPath = importTMPath.replaceAll("\\\\", "/");
		logger.info("【batchImportResService】-->getTMJson()-->获取批次资源信息  导入目录：importTMPath:"+ importTMPath);
		
		JSONArray array = new JSONArray();
		
		String fileEncode = System.getProperty("file.encoding");
		//File baseFile = new File(bachImportXmlPath);
		File baseFile = null;
		try {
			baseFile = new File(new String(importTMPath.getBytes("UTF-8"), fileEncode));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int id = 1;
		int count = 1;
		if (baseFile.exists()) {
			File[] batchFiles = baseFile.listFiles();//批次目录
			if (batchFiles != null && batchFiles.length > 0) {
				for (File batchFile : batchFiles) {
					if (batchFile.isDirectory()) {
						String batchFilePath = batchFile.getAbsolutePath().replaceAll("\\\\", "/");
						String batchFileName = batchFile.getName().replaceAll("\\\\", "/");
						String batchOppositePath = batchFilePath.replace(importTMPath, "");
						JSONObject batchJson = new JSONObject();
						batchJson.put("id", id);
						batchJson.put("pId", "-1");
						batchJson.put("name", batchFileName);
						batchJson.put("type", "dir");
						batchJson.put("path", batchFilePath);
						batchJson.put("open", false);
						array.add(batchJson);
						count = id ;
						id++;
						logger.info("【batchImportResService】-->getXmlJson()-->获取批次资源信息  批次目录：batchFilePath:"+ batchFilePath);
						
						File[] resFiles = batchFile.listFiles();//资源目录
						for (File resFile : resFiles) {
							if (resFile.isFile()) {
								String resFileName = resFile.getName();
								String fileType = resFileName.substring(resFileName.lastIndexOf(".")+1); 
								String resFilePath = resFile.getAbsolutePath().replaceAll("\\\\", "/");
								String resOppositePath = resFilePath.replace(importTMPath, "");
								if ("xml".equals(fileType.toLowerCase())) {
									JSONObject resJson = new JSONObject();
									resJson.put("id", id);
									resJson.put("pId", count);
									resJson.put("name", resFileName);
									resJson.put("type", "file");
									resJson.put("path", resFilePath);
									resJson.put("open", true);
									//resJson.put("nocheck", true);
									array.add(resJson);
									id++;
									logger.info("【batchImportResService】-->getXmlJson()-->获取批次资源信息  资源目录：batchFilePath:"+ resFilePath);
								}
							}
						}
					}
				}
			}
		}
		
		JSONObject root = new JSONObject();
		root.put("id", "-1");
		//root.put("pId", "0");
		root.put("name", "条目资源导入目录");
		root.put("type", "root");
		root.put("open", true);
		root.put("path", "");
		array.add(root);
		return array.toString();
	}
	
	/**
	 * 
	* @Title: saveResByTM
	* @Description: 保存批量导入xml方式数据
	* @param paths	批次的路径
	* @param remark	备注信息
	* @param repeatType	重复策略
	* @param publishType 资源类型
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String saveResByTM(String paths, String publishType){
		String retStatus = "SUCCESS";
		String xmlPaths[] = paths.split(",");
		UserInfo userinfo = LoginUserUtil.getLoginUser();
		for(int i=0,len = xmlPaths.length;i<len;i++){
			String xmlPath=xmlPaths[i].replaceAll("\\\\", "/");
			File xmlFile = new File(xmlPath);
			try {
				if(xmlFile.exists()){
					//解析xml文档
					Document doc = ResUtils.read(xmlFile);
			    	Map<String, String> map = ResUtils.xml2Map(doc);
			    	
			    	Ca ca = new Ca();
			    	ca.setPublishType(publishType);
			    	ca.setCreateName(userinfo.getUserId()+"");
			    	ca.setStatus("0");
			    	ca.setCreateTime(new Date().getTime()+"");
			    	ca.setCreator(userinfo.getUserId()+"");
			    	
			    	//将返回的map放入到ca中
			    	String identifier = "";
			    	List<MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinitionByResType(publishType);
			    	//List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(publishType);
					for (MetadataDefinition metadataDefinition : metadataDefinitions) {
						if (metadataDefinition.getRelatedWords() != null) {
							String relatedWordStr = metadataDefinition.getRelatedWords();
							String[] relatedWords = relatedWordStr.split(",");
							String value = "";
							for (String relatedWord : relatedWords) {
								if (relatedWord.startsWith("en_")) {
									relatedWord = relatedWord.substring(3);
									value = map.get(relatedWord);
								}
								if(StringUtils.isNotBlank(value)){
									value = value.trim();
								}
								if (StringUtils.isNotBlank(value) && "identifier".equals(metadataDefinition.getFieldName())) {
									identifier= relatedWord;
								}
								if (StringUtils.isNotBlank(value)) {
									ca.getMetadataMap().put(metadataDefinition.getFieldName(), value);
								}
							}
						}else {
							String value = map.get(metadataDefinition.getFieldName());
							if(StringUtils.isNotBlank(value)){
								value = value.trim();
							}
							ca.getMetadataMap().put(metadataDefinition.getFieldName(), value);
						}
					}
					
			    	//存放资源
			    	String resDir = map.get(identifier);
			    	// 按要求生成目录
					String parentPath = resourceService.createPublishParentPath(publishType, "");
					File resPath = new File(parentPath+File.separator+resDir);
					if (!resPath.exists()) {
						try {
							FileUtils.forceMkdir(resPath);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					//获取文件信息
					String body = map.get("tm_body");
					ArrayList<String> imgList = ResUtils.getImgSrc(body);
					try {
						ca = ResUtils.getTMFileLists(ca,resPath, xmlFile, imgList);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Date date = new Date();
					ca.setUpdateTime(date.getTime() + "");
			    	
					//调用接口保存Ca
					String objectId = "";
					String result = "";
					Gson gson = new Gson();
					HttpClientUtil http = new HttpClientUtil();
					String paraJson = gson.toJson(ca);
					result = http.postJson(PUBLISH_SAVE_URL, paraJson);
					logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>调接口进入保存方法返回result： "+result);
					if (result!=null && !"".equals(result) && StringUtils.isNotBlank(result)) {
						SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
						objectId = rtn.getObjectId();//保存成功返回objectId
					}else {
						throw new ServiceException("保存数据信息错误 ！");
					}
						
					//文件转换
					logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>文件转换逻辑开始 资源objectId："+objectId);
					if (StringUtils.isNotBlank(objectId)) {
						String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
						Ca returnCa = gson.fromJson(resourceDetail, Ca.class);//保存完后返回的Ca
						List<com.brainsoon.semantic.ontology.model.File> realFiles = returnCa.getRealFiles();
						if (realFiles != null && realFiles.size() > 0) {
							DoFileQueueList doFileList = ResUtils.converPath(realFiles,objectId);
							logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>文件转换逻辑开始 待转换文件："+doFileList.toString());
							if(doFileList !=null && doFileList.getDoFileQueueList().size()>0){
								resConverfileTaskService.insertQueue(doFileList);
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
				
		}
		return retStatus;
	}

	@Override
	public StringBuffer fileDetail(String id,String name) {
		String hql = " from FileDownValue where task.id="+id;
		List<FileDownValue> fileDetailList = baseDao.query(hql,0,20);
		StringBuffer sb = new StringBuffer();
		sb .append("<label for=\"name\" class=\"col-sm-4 control-label text-right\"><font color=\"#840042\">第"+name+"批次文件信息：</font></label>");
		sb.append("<table border=\"1\" style=\"border: solid thin blue\">");
		sb.append("<tr>");
		sb.append("<td style=\"width:100px;text-align:center;\">");
		sb.append("资源名称");
		sb.append("</td>");
		sb.append("<td style=\"width:100px;text-align:center;\">");
		sb.append("完成状态");
		sb.append("</td>");
		sb.append("</td>");
		if(fileDetailList!=null && fileDetailList.size()>0){
			for(int i=0;i<fileDetailList.size();i++){
				sb.append("<tr>");
				sb.append("<td style=\"width:100px;text-align:center;\">");
				String resName = fileDetailList.get(i).getResName();
				if(resName.length()>17){
					String newName= resName.substring(0,17)+"......";
					sb.append("<span data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(resName).append("\"> ").append(newName).append("</span>");
					sb.append("</td>");
				}else{
					sb.append(""+resName+"");
					sb.append("</td>");
				}
				
				if(fileDetailList.get(i).getStatus().equals("1")){
					sb.append("<td style=\"width:100px;text-align:center;\">");
					sb.append("已完成");
					sb.append("</td>");
				}else{
					sb.append("<td style=\"width:100px;text-align:center;\">");
					sb.append("处理中");
					sb.append("</td>");
				}
//				htmlst = htmlst+fileDetailList.get(i).getResName()+fileDetailList.get(i).getStatus();
				sb.append("</tr>");
			}
		}
		sb.append("</table>");
		return sb;
	}

	/**
	 * 
	* @Title: updateFCS
	* @Description: 本方法是解决转换历史表中的id与文件id不对应的情况，请勿调用
	* @return void    返回类型
	* @throws
	 */
	public void updateFCS(){
		List<DoFileHistory> histories = getBaseDao().query("from DoFileHistory group by resId ");
		HttpClientUtil http = new HttpClientUtil();
		Gson gson = new Gson();
		try {
			for (DoFileHistory doFileHistory : histories) {
				String resId = doFileHistory.getResId();
				List<DoFileHistory> resHistories = getBaseDao().query("from DoFileHistory where resId ='"+resId+"'");
				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + resId);
				Ca ca = gson.fromJson(resourceDetail, Ca.class);//保存完后返回的Ca
				List<com.brainsoon.semantic.ontology.model.File> filse= ca.getRealFiles();
				
				if (filse != null && filse.size() > 0) {
					for (com.brainsoon.semantic.ontology.model.File file : filse) {
						for (DoFileHistory doFileHistory2 : resHistories) {
							String srcPath = doFileHistory2.getSrcPath();
							srcPath = srcPath.replaceAll("\\\\", "/");
							String fileObjectId = doFileHistory2.getObjectId();
							String fileSrcPath = FILE_ROOT + file.getPath();
							fileSrcPath = fileSrcPath.replaceAll("\\\\", "/");
							if (fileSrcPath.equals(srcPath)) {
								if (!fileObjectId.equals(file.getObjectId())) {
									doFileHistory2.setObjectId(file.getObjectId());
									getBaseDao().saveOrUpdate(doFileHistory2);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	* @Title: covertFailToQueue
	* @Description: 本方法是解决转换历史表中有些没有转换，判断转换后的目录存不存在，若不存在就放到转换表中
	* @return void    返回类型
	* @throws
	 */
	public void doCovertFailToQueue(){
		StringBuffer selectHql = new StringBuffer();
		selectHql.append(" from DoFileHistory order by id ");
		List<DoFileHistory> histories = getBaseDao().query(selectHql.toString());
		if (histories!=null && histories.size()>0) {
			for (DoFileHistory doFileHistory : histories) {
				boolean isMove = false;//是否执行移动到转换表的操作
				String resultConveredfilePath = doFileHistory.getResultConveredfilePath();
				File file = new File(resultConveredfilePath);
				if (file.exists()) {
					Pattern p = Pattern.compile(".+\\.(swf)$");
					List<File> files = DoFileUtils.filePattern(file, p, false);//swf文件数

					if(files == null || files.size() <= 0 ){
						isMove = true;
					}
				}else {
					isMove = true;
				}
				
				if (isMove) {//执行移动
					try {
						DoFileQueue doFileQueue = new DoFileQueue();
						doFileHistory.setTimestamp("new");
						doFileHistory.setStatusConvered(0);
						String time = DateUtil.convertDateTimeToString(new Date());
						doFileHistory.setUpdateTime(time);
						if ("mswf".equals(doFileHistory.getFileFormat())) {
							doFileHistory.setFileFormat("nswf");
						}
						//将历史表信息拷贝到转换表
						CopyUtil.CopyToQueue(doFileHistory, doFileQueue);
						//插入到转换表
						getBaseDao().saveOrUpdate(doFileQueue);
						//删除历史表
						getBaseDao().delete(doFileHistory);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	}

	@Override
	public String queryEntry(String url) {
		HttpClientUtil hc = new HttpClientUtil();
		String result = hc.executeGet(url);
		return result;
	}
}
