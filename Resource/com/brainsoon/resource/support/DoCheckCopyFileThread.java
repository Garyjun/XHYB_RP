package com.brainsoon.resource.support;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.bsrcm.search.solr.SolrUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.semantic.ontology.model.Ca;
import com.google.gson.Gson;

/**
 * 
* @ClassName: DoCheckCopyFile
* @Description: 审核时拷贝审核资源中要抽文本的文件到为转换目录
* @author huangjun
* @date 2015-12-24 11:42:30
*
 */
public class DoCheckCopyFileThread implements Runnable {

	private static Logger logger = Logger.getLogger(DoCheckCopyFileThread.class);
	private final static String PUBLISH_DETAIL_URL = WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL");
	// fileDir目录绝对路径
	String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(), "\\", "/");
	// 待抽取txt文件的根目录-绝对路径
	String filesolr = StringUtils.replace(WebAppUtils.getWebRootBaseDir("filesolr"), "\\", "/");

	@Override
	public void run() {
		
		while (true) {
			try {
				// 获取任务队列
				DoCheckCopyFileQueue doCheckCopyFileQueue = DoCheckCopyFileQueue.getInst();
				// 启动任务
				String objectId = doCheckCopyFileQueue.getMessage();
				
				Ca res = getPubres(objectId);

				String uid = objectId.substring(4, objectId.length());
				String combinePath = filesolr + "noconvert/" + uid;
				File combinePathDir = new File(combinePath);
				if (!combinePathDir.exists()) {
					combinePathDir.mkdirs();
				}

				List<com.brainsoon.semantic.ontology.model.File> realFiles = res.getRealFiles();
				if (realFiles != null && realFiles.size() > 0) {
					for (com.brainsoon.semantic.ontology.model.File realFile : realFiles) {
						if ("2".equals(realFile.getIsDir())) {
							String fileType = realFile.getFileType().toLowerCase();
							String path = realFile.getPath();
							path = path.replaceAll("\\\\", "/");
							String absPath = FILE_ROOT + path;
							File absFile = new File(absPath);
							if (absFile.exists()) {
								String newPath = combinePath + "/"+ realFile.getAliasName();
								if (StringUtils.equals("pdf", fileType)) {
									if (!SolrUtil.isCanPdfToTXT(absPath)) {
										continue;
									}
									if (SolrUtil.pdfTextStripper(absPath, newPath)) {
										FileUtils.copyFile(absFile, new File(newPath));
										logger.debug("拷贝该文件到未转换目录：" + newPath);
									}
								} else if (StringUtils.equals("doc", fileType) || StringUtils.equals("docx", fileType)) {
									FileUtils.copyFile(absFile, new File(newPath));
									logger.debug("拷贝该文件到未转换目录：" + newPath);
								} else if (StringUtils.equals("xml", fileType)) {
									if (SolrUtil.xml2Text(absPath, newPath)) {
										FileUtils.copyFile(absFile, new File(newPath));
										logger.debug("拷贝该文件到未转换目录：" + newPath);
									}
								} else if (StringUtils.equals("txt", fileType)) {
									FileUtils.copyFile(absFile, new File(newPath));
									logger.debug("拷贝该文件到未转换目录：" + newPath);
								} else {
									logger.debug("不支持该类型文件：" + fileType);
									continue;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param objectId
	 * @return
	 */
	public Ca getPubres(String objectId) {
		Ca res;
		HttpClientUtil http = new HttpClientUtil();
		String resourceDetail = http.executeGet(PUBLISH_DETAIL_URL + "?id="+ objectId);
		res = new Gson().fromJson(resourceDetail, Ca.class);
		return res;
	}

}
