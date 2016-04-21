package com.brainsoon.resrelease.support;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import magick.MagickException;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DateTools;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.zip.ZipUtil;
import com.brainsoon.resrelease.po.ParamsTempEntity;
import com.brainsoon.resrelease.po.ResOrder;
import com.brainsoon.resrelease.po.ResRelease;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.resrelease.service.IPublishTempService;
import com.brainsoon.resrelease.service.IResOrderService;
import com.brainsoon.resrelease.service.IResReleaseService;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.google.gson.Gson;

/**
 * @ClassName: ProcessFileC
 * @Description: 消费线程
 * @author xiehewei
 * @date 2014年9月1日 下午3:36:43
 *
 */
public class ProcessFileC extends BaseService implements Runnable {

	private static final Logger logger = Logger.getLogger(ProcessFileC.class);
	
	ProcessFilePC processFilePC = new ProcessFilePC();

	ProcessFileC(ProcessFilePC processFilePC) {
		this.processFilePC = processFilePC;
	}
	
	private Long orderId;

	/**
	 * 
	 * 消费进程(转换文件)
	 * 
	 */
	@Override
	public void run() {
		while (true) {
			logger.info("消费线程：【" + Thread.currentThread().getName() + "】处理开始...");
			// 转换服务
			long ss = DateTools.getStartTime();
			ResReleaseDetail detail = processFilePC.popFile();
			
			// 获得目录下的根路径
			String rootPath = WebAppUtils.getWebRootBaseDir(ConstantsDef.prodFile).replace("/", "\\");
			// 资源根路径
			String fileRoot = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileRoot);
			IResOrderService resOrderService;
			IPublishTempService publishTempService;
			IBaseService baseQueryService = null;
			IResReleaseService resReleaseService;
			String header = rootPath.substring(0, rootPath.length() - 1);
			String zipName = "";
			boolean b = true;
			String describe = "转换成功！"; // 描述
			String pathContent = "";
			ResOrder resOrder = null;
			try {
				resOrderService = (IResOrderService) BeanFactoryUtil.getBean("resOrderService");
				publishTempService = (IPublishTempService) BeanFactoryUtil.getBean("publishTempService");
				ParamsTempEntity paramsTempEntity = new ParamsTempEntity();
			    baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
			    resReleaseService = (IResReleaseService) BeanFactoryUtil.getBean("resReleaseService");
				resOrder = (ResOrder) resOrderService.getByPk(ResOrder.class, (Long.valueOf(orderId)));
				zipName = resOrder.getOrderId() + "-" + DateUtil.convertDateToString(new Date()) + "-" + resOrder.getChannelName() + ".zip";
				header = FileUtil.makeDir(header, DateUtil.getDate(new Date()) + "\\" + resOrder.getOrderId());
				paramsTempEntity = publishTempService.convertEntity(resOrder.getTemplate());
				//header = FileUtil.makeDir(header, DateUtil.getDate(new Date()) + "\\" + resOrder.getOrderId());
				if(detail==null){
					resOrder.setStatus(ResReleaseConstant.OrderStatus.PUBLISHED);
					break;
				}else{
				// 第一步：写状态，表示在转换中,status 1：发布中
				detail.setStatus(ResReleaseConstant.PublishingStatus.PUBLISHING);
				baseQueryService.update(detail);
				//对资源进行处理
				HttpClientUtil http = new HttpClientUtil();
				// 获得资源的相关信息返回字符串
				String paramJson = resOrder.getTemplate().getParamsJson();
				JSONObject paramObj = JSONObject.fromObject(paramJson);
				Object flag = paramObj.get("book");
				String bookRes = null;
				String eduRes = null;
				Gson gson = new Gson();
				if (flag == null) {
					String resId = detail.getResId();
					// 教育资源都是asset
					if (resId.substring(4, 6).equals("ca")) {
						eduRes = http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + detail.getResId());
						Ca eduCa = gson.fromJson(eduRes, Ca.class);
//						List<Organization> eduOrgs = eduCa.getOrganizations();
//						for (Organization organization : eduOrgs) {
//							List<OrganizationItem> organizationItems = organization.getOrganizationItems();
//							for (OrganizationItem organizationItem : organizationItems) {
//								List<com.brainsoon.semantic.ontology.model.File> files = organizationItem.getFiles();
//								// 忽略目录
//								if (files != null) {
//									for (com.brainsoon.semantic.ontology.model.File file : files) {
//										// 资源位置
//										String srcPath = fileRoot + file.getPath();
//										File srcFile = new File(srcPath.replace("/", File.separator));
//
//										if (srcFile.exists() && !srcFile.isDirectory()) {
//											// 获得资源的目录结构
//										}
//									}
//									detail.setStatus(ResReleaseConstant.PublishingStatus.SUCCESS_PUBLISH);
//									resReleaseService.update(detail);
//									XmlUtil.createMetadataXml(pathContent, eduCa, "", resId, paramsTempEntity.getResourceType());
//								}
//							}
//						}
						//XmlUtil.createMetadataXml("", eduCa, resId,null,"");
					} else {
						eduRes = http.executeGet(WebappConfigUtil.getParameter("ASSET_DETAIL_URL") + "?id=" + detail.getResId());
						Asset eduAsset = gson.fromJson(eduRes, Asset.class);
//						eduAsset.getFiles()
						List<com.brainsoon.semantic.ontology.model.File> files = null;
						int num = 0;
						for (com.brainsoon.semantic.ontology.model.File file : files) {
							String eduSrcPath = fileRoot.replace("/", "\\") + file.getPath().replace("/", "\\");
							File srcEduFile = new File(eduSrcPath);
							if (srcEduFile.exists() && !srcEduFile.isDirectory()) {
								String title = eduAsset.getCommonMetaData().getTitle();
								pathContent = FileUtil.makeDir(header, title + "_" + resId.replace("-", "-").replace(":", "："));
								try {
									org.apache.commons.io.FileUtils.copyFileToDirectory(srcEduFile, new File(pathContent));
								//XmlUtil.createMetadataXml(pathContent, eduAsset, "",resId, paramsTempEntity.getResourceType());
								} catch (IOException e) {
									detail.setStatus(ResReleaseConstant.PublishingStatus.FAIL_PUBLISH);
									detail.setRemark("资源文件不存在！");
									resReleaseService.update(detail);
									e.printStackTrace();
								}
								int pos = eduSrcPath.lastIndexOf("\\");
								String proFilePath = pathContent + File.separator + eduSrcPath.substring(pos + 1, eduSrcPath.length());
//								try {
//									resReleaseService.processFile(null,proFilePath.replace("/", File.separator), paramsTempEntity);
//									num++;
//								} catch (NumberFormatException e) {
//									detail.setRemark("发布模板数据格式错误！");
//									resReleaseService.update(detail);
//									e.printStackTrace();
//								} catch (IOException e) {
//									detail.setRemark("资源文件不存在！");
//									resReleaseService.update(detail);
//									e.printStackTrace();
//								} catch (MagickException e) {
//									detail.setRemark("资源文件加工出错！");
//									resReleaseService.update(detail);
//									e.printStackTrace();
//								} catch (URISyntaxException e) {
//									detail.setRemark("资源文件路径存在非法字符，无法获得资源文件！");
//									resReleaseService.update(detail);
//									e.printStackTrace();
//								} catch (InterruptedException e) {
//									detail.setRemark("资源文件加工出错！");
//									resReleaseService.update(detail);
//									e.printStackTrace();
//								} catch (Exception e) {
//									detail.setRemark(e.getMessage());
//									resReleaseService.update(detail);
//								}
							}
						}
						if(num==files.size()){
							detail.setStatus(ResReleaseConstant.PublishingStatus.SUCCESS_PUBLISH);
							resReleaseService.update(detail);
						}
						//XmlUtil.createMetadataXml(pathContent, eduAsset, "", resId, paramsTempEntity.getResourceType());
					}
				} else {
					// 图书资源都是ca
					bookRes = http.executeGet(WebappConfigUtil.getParameter("CA_DETAIL_URL") + "?id=" + detail.getResId());
					Ca ca = gson.fromJson(bookRes, Ca.class);
					// 资源名称
					String title = ca.getCommonMetaData().getTitle();
				}
				}
			} catch (Exception e) {
				b = false;
				describe = "转换失败:" + e.getMessage();
			} finally {
				// 第三步：写状态和失败的原因，表示在转换完成,status -> 2：转换成功 3：转换失败
				if (b) {
					if(detail!=null){
						detail.setStatus("1"); // 1：转换成功
					}
				} else {
					if(detail!=null){
						detail.setStatus("0"); // 0：转换失败
						// 添加重试次数
						detail.setProcessTimes(detail.getProcessTimes() + 1);
					}
				}
				// 执行更新
				//baseQueryService.update(detail);
			}

			DateTools.getTotaltime(ss);
			
			logger.info("消费线程：【" + Thread.currentThread().getName() + "】处理结束");
			// 等待一会
			try {
				Thread.sleep((int) (Math.random() * 2000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void zipFile(ResRelease resRelease, String header, String zipName){
		String prossedFilePath = header.substring(0, header.lastIndexOf(File.separator));
		String zipNames = prossedFilePath + "\\" + zipName;
		IResReleaseService resReleaseService;
		try {
			resReleaseService = (IResReleaseService) BeanFactoryUtil.getBean("resReleaseService");
			ZipUtil.zipFileOrFolder(header, zipNames, null);
			org.apache.commons.io.FileUtils.copyFileToDirectory(new File(prossedFilePath+"\\"+zipName), new File(header));
			resRelease.setFtpUrl(header+File.separator+zipName);
			resReleaseService.update(resRelease);
			File file = new File(zipNames);
			file.delete();
		} catch (IOException e) {
			logger.error("文件不存在！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
