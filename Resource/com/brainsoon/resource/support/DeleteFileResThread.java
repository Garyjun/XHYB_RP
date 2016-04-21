package com.brainsoon.resource.support;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
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


public class DeleteFileResThread implements Runnable {
	private static Logger logger = Logger.getLogger(DeleteFileResThread.class);
	public static final String FILE_ROOT = StringUtils.replace(
			WebAppUtils.getWebAppBaseFileDirFR(), "\\", "/");
	private IResConverfileTaskService resConverfileTaskService;
	@Override
	public void run() {
		while (true) {
			try {
				resConverfileTaskService = (IResConverfileTaskService)BeanFactoryUtil.getBean("resConverfileTaskService");
				// 获取任务队列
				DeleteFileTaskQueue deleteQueue = DeleteFileTaskQueue.getInst();
				// 启动任务
				DeleteResFileForCaIds deleteTaskData = deleteQueue.getMessage();
				if (null != deleteTaskData) {
					Ca ca = deleteTaskData.getCa();
					String id = deleteTaskData.getId();
					try {
						//删除转换记录和转换后的文件 2015-12-18 14:26:55
						resConverfileTaskService.deleteDoFileQueue(id,ca.getRootPath());
						List<com.brainsoon.semantic.ontology.model.File> fileList = ca.getRealFiles();
						for (com.brainsoon.semantic.ontology.model.File files : fileList) {
							String path = files.getPath();
							if (StringUtils.isNotBlank(path)) {
								File f = new File(FILE_ROOT + path);
								if (f.exists()) {
									// 删除文件
									if (f.isDirectory()) {
										File parentFile = f.getParentFile();
										if (parentFile.exists()) {
											// File pparentFile =
											// parentFile.getParentFile();
											// if(pparentFile.exists()){
											FileUtils.deleteDirectory(parentFile);
											logger.error("删除文件成功---------------------"+parentFile);
											// }
										}
									} else {
										try {
											FileUtils.forceDelete(f.getParentFile());
										} catch (Exception e) {
											logger.error("删除文件失败"+ e.getMessage()+ f.getParentFile().getAbsolutePath());
										}
									}
								}
							}
						}
						
					} catch (IOException e) {
						logger.info("删除文件异常"+e.getMessage());
					}
				}
//				else{
//					logger.info("中断删除线程");
//					break;
//				}
			}catch (Exception e) {
				logger.info("获取服务异常"+e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
