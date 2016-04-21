package com.brainsoon.resource.support;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipDecryptionUtil;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.resource.action.BresAction;
import com.brainsoon.resource.po.FileDownName;
import com.brainsoon.resource.po.FileDownValue;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.semantic.schema.QueryImportCode;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IDictNameService;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.Audience;
import com.brainsoon.system.support.SystemConstants.ConsumeType;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.brainsoon.system.support.SystemConstants.Language;
import com.brainsoon.system.support.SystemConstants.OpeningRate;
import com.brainsoon.system.support.SystemConstants.OperatType;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.util.FieldValidator;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;


public class FtpCopyFileThreading implements Runnable {
	private static Logger logger = Logger.getLogger(FtpCopyFileThreading.class);
	private final static String FTP_LOCAL_MAPPING = WebappConfigUtil
			.getParameter("FTP_LOCAL_MAPPING");
	private ISysParameterService sysParameterService = null;
	private IFLTXService fLTXService;
	private IDictNameService dictNameService = null;
	private final static SimpleDateFormat dateformat2 = new SimpleDateFormat(
			"yyyyMMddHHmmssSSS");
	private IBatchImportResService batchImportResService = null;
	@Override
	public void run() {
		try {
			batchImportResService = (IBatchImportResService)BeanFactoryUtil.getBean("batchImportResService");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		String parentPath = BresAction.FILE_TEMP + UUID.randomUUID().toString() + File.separator;
//		String hql = " from FileDownValue where status!=1";
//		List<FileDownValue> fileValuelist = new ArrayList<FileDownValue>();
//		fileValuelist = batchImportResService.query(hql);
		while (true) {
			try {
				// 获取任务队列
				FtpTaskQueue queue = FtpTaskQueue.getInst();
//				sysParameterService = (ISysParameterService)BeanFactoryUtil.getBean("sysParameterService");
//				IEffectNumService iEffectNumService = (IEffectNumService)BeanFactoryUtil.getBean("effectNumService");
//				fLTXService = (IFLTXService)BeanFactoryUtil.getBean("FLTXService");
//				dictNameService = (IDictNameService)BeanFactoryUtil.getBean("dictNameService");
				// 启动任务
				FtpFileIds taskData = queue.getMessage();
				if (taskData!=null) {
					int num =0;
					String title = "";
					double fileSize= 0;
					String ids = taskData.getIds();
					String encrypt = taskData.getPwd();
					String idsArray[]= ids.split(",");
					String parentPath = BresAction.FILE_TEMP
							+ UUID.randomUUID().toString() + File.separator;
					List<String> list = new ArrayList<String>();
					FileDownName fileTask = new FileDownName();
					FileDownValue fileValue = null;
					fileTask.setStatus("");
					batchImportResService.create(fileTask);
					//获取业务类处理
					logger.info("cccccccc=============");
					for(int i=0;i<idsArray.length;i++){
						fileValue = new FileDownValue();
						fileValue.setResId(idsArray[i]);
						
						HttpClientUtil http = new HttpClientUtil();
						String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + idsArray[i]);
						Gson gson = new Gson();
						Ca ca = gson.fromJson(resourceDetail, Ca.class);
						title = MetadataSupport.getTitle(ca);
//						SysOperateLogUtils.addLog("res_file_down", title, LoginUserUtil.getLoginUser());
						if(list.contains(title)){
							num++;
							title=title+"("+num+")";
						}
						fileValue.setResName(title);
						fileValue.setStatus("否");
						fileValue.setTask(fileTask);
						batchImportResService.create(fileValue);
						list.add(title);
						if(ca!=null){
							if(ca.getRealFiles()!=null){
								Map<String, String> mapName = new HashMap<String, String>();
								int realNum = ca.getRealFiles().size();
								for (int j = 0; j < realNum; j++) {
									if(ca.getRealFiles().get(j).getFileByte()!=null && ca.getRealFiles().get(j).getIsDir().equals("2")){
										fileSize = fileSize + Double.valueOf(ca.getRealFiles().get(j).getFileByte());
									}
									if (ca.getRealFiles().get(j).getName() != null
											&& ca.getRealFiles().get(j).getAliasName() != null) {
										mapName.put(ca.getRealFiles().get(j).getAliasName(), ca
												.getRealFiles().get(j).getName());
									}
								}
								String rootPath = ca.getRootPath();
								rootPath = rootPath.replaceAll("\\\\", "/");
								createRandomDir(parentPath + title);
								String path = StringUtils.replace(
										WebAppUtils.getWebAppBaseFileDirFR(), "\\\\", "/")
										+ rootPath;
								try {
									FileUtil.copyDir(new File(path), new File(parentPath
											+ title));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									changeFileName(new File(parentPath + title), mapName);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								String resName = "";
								for(String li:list){
									resName = resName+li+",";
								}
								fileTask.setFileSize(fileSize+"");
								fileTask.setResName(resName);
								batchImportResService.update(fileTask);
								try {
									String zipUuid = UUID.randomUUID().toString();
									String zipName = BresAction.FILE_TEMP + "zipDir/" + zipUuid + "/";
									zipName = zipName.replaceAll("\\\\", "/");
									File zipDir = new File(zipName);
									if (!zipDir.exists()) {
										zipDir.mkdirs();
									}
									zipName =zipName+ title+ dateformat2.format(new Date())  + ".zip";
									if (StringUtils.isNotBlank(encrypt)) {//等修改密码
										String encryptZip = BresAction.FILE_TEMP
												+ dateformat2.format(new Date()) + ".zip";
										// 不支持汉字
										ZipDecryptionUtil.encryptZipFile(parentPath, encryptZip, encrypt, false, false);
//										String hq = " from FileDownName where id="+list.getTask().getId();
//										List<FileDownValue> fileValuelist = new ArrayList<FileDownValue>();
//										fileValuelist = batchImportResService.query(hql);
										
//										FileDownName task = new FileDownName();
//										task.setId(list.getTask().getId());
//										FileDownValue fileValue = new FileDownValue();
//										fileValue.setId(list.getId());
										fileValue.setStatus("1");
										fileValue.setTask(fileTask);
										batchImportResService.update(fileValue);
									} else {
										ZipUtil.zipFileOrFolder(parentPath, zipName, null);
										fileValue.setStatus("1");
										fileValue.setTask(fileTask);
										batchImportResService.update(fileValue);
									}
								} catch (Exception e) {
									logger.error("压缩出现问题" + e.getMessage());
								}
							}
						}
						
					}
				}
			}catch (Exception e) {
				logger.info("wwwwwwwwwwwwwwwww"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
		
	/**
	 * 创建路径
	 * @param path
	 */
	public void createRandomDir(String path) {
		Random random = new Random();
		int value = random.nextInt(10);
		File file = new File(path);
		if (file.exists()) {
			path = path + "_"+ value;
			createRandomDir(path);
		}else{
			file.mkdirs();
		}
	}
	/**
	 * 文件改名
	 * @param oldFilePath
	 * @param mapName
	 * @throws Exception
	 */
	public void changeFileName(File oldFilePath, Map<String, String> mapName)
			throws Exception {
		String oldName ="";
		String oldPath = "";
		String newName = ""; 
		String newPath = "";
		if(oldFilePath.isDirectory()){
			File[] files = oldFilePath.listFiles();
			if(files!=null && files.length>0){
				for(File file:files){
					if (!file.isDirectory()) {// 如果是文件，直接更名
						oldName = file.getName();
						oldPath = file.getPath();
						if(mapName.get(oldName)!=null){
							newName = mapName.get(oldName);
						}else{
							break;
						}
						oldPath = oldPath.replaceAll("\\\\", "/");
						newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1)
								+ newName;
						file.renameTo(new File(newPath));
					} else {// 如果是文件夹，
						oldName = file.getName();
						oldPath = file.getPath();
						if(mapName.get(oldName)!=null){
							newName = mapName.get(oldName);
						}else{
							break;
						}
						oldPath = oldPath.replaceAll("\\\\", "/");
						newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1)
								+ newName;
						changeFileName(file, mapName);// 递归
						file.renameTo(new File(newPath));
						// 循环完后，把该目录更名。
					}
				}
			}
		}else{
			oldName = oldFilePath.getName();
			oldPath = oldFilePath.getPath();
			newName = mapName.get(oldName);
			oldPath = oldPath.replaceAll("\\\\", "/");
			newPath = oldPath.substring(0, oldPath.lastIndexOf("/") + 1)
					+ newName;
			oldFilePath.renameTo(new File(newPath));
		}
	}
	
}
