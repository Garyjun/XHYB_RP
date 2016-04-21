package com.brainsoon.common.util.dofile.service.thread;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.brainsoon.common.service.IBaseService;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.conver.ConverUtils;
import com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils;
import com.brainsoon.common.util.dofile.conver.PdfToSwfUtil;
import com.brainsoon.common.util.dofile.conver.TxtToPdfUtils;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DateTools;
import com.brainsoon.common.util.dofile.util.DoFileException;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.view.CatalogDTO;
import com.brainsoon.common.util.dofile.view.CreateNcxUtil;
import com.brainsoon.common.util.dofile.view.XmlFileUtil;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.model.ResConverfileTaskHistory;

/**
 * 
 * @ClassName: ResConverfileTaskConsumption
 * @Description: 文件转换消费类（转换文件）
 * @author tanghui
 * @date 2014-5-23 上午11:36:24
 * extends BaseService
 * 
 */
public class ConverFileC extends BaseService implements Runnable {
	
	private static IBaseService baseQueryService = null;
	
	private static final Logger logger = Logger.getLogger(ConverFileC.class);
	
	ConverFilePC converFilePC = new ConverFilePC();

	ConverFileC(ConverFilePC converFilePC) {
		this.converFilePC = converFilePC;
	}

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
			boolean b = true;
			String describe = "转换成功！"; //描述
			ResConverfileTask converFile = converFilePC.popFile();
			try {
				baseQueryService = (IBaseService) BeanFactoryUtil.getBean("baseService");
				converFile = (ResConverfileTask) baseQueryService.getByPk(ResConverfileTask.class, converFile.getId());
				//重新判断是否有处理完成的记录:针对处理成功或者处理失败并且重试3次的直接拷贝到历史表中去
				if(converFile != null){
					if(!checkStatus(converFile)){
						// 第一步：写状态，表示在转换中,status 0：待转换 1：转换中
						converFile.setStatus(1);
						baseQueryService.update(converFile);
						
						// 第二步：执行转换....
						// 待文件路径(绝对)
						String srcPath = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirFR(),converFile.getSrcPath());
						// 转换后的文件路径(目录)
						String tarPath = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirCFR(),converFile.getTarPath());
						// 文件类型
						String fileType = converFile.getFileType();
						//获取不带扩展名的文件名
					    String srcFileName = DoFileUtils.getFileNameNoEx(srcPath);
						// 判断文件类型，并做转换处理
						// 主要处理（转换）的文件类型为：
						// pdf ---> swf
						// office 系列 ----> pdf ---> swf
						// txt（文本） ---> pdf ---> swf
						// 其他格式的视频 ---> flv
					    if (!fileType.equals("flv") && PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat).contains(fileType)) { //视频
							//转换后flv路径
							tarPath = DoFileUtils.connectFilePath(tarPath, srcFileName + ".flv");
							b = ConverUtils.processFfmpegToFLV(srcPath,tarPath);
						}else if (!fileType.equals("mp3") && PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat).contains(fileType)) { //视频
							//转换后flv路径
							tarPath = DoFileUtils.connectFilePath(tarPath, srcFileName + ".mp3");
							b = ConverUtils.processFfmpegToMp3(srcPath,tarPath);
						} else if (PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat).contains(fileType)) { 
							    //转换后的临时pdf路径，将其赋值给 filePath
								String pdfPath = DoFileUtils.connectFilePath(tarPath,srcFileName + ".pdf");
							    if(!fileType.equals("pdf")){
									TxtToPdfUtils.convertToPdf(srcPath,pdfPath);// 返回转换后的临时pdf路径，将其赋值给 filePath
							    }else{
							    	pdfPath = srcPath;
							    }
								// 获取pdf最大页数
								int maxPages = PdfUtil.getPdfAllPageTotalNum(pdfPath);
								if(maxPages == 0){
									throw new DoFileException("获取PDF总页数失败，请检查.");
								}
								
								//===========生成  1.ncx文件 2.带node节点的xml文件 3.所有的swf文件===============
								// NCX文件临时路径
								String tempNcxPath = DoFileUtils.connectFilePath(tarPath, srcFileName + ".ncx");
								// 带node节点的xml文件临时路径
								String tempNodeXmlPath = DoFileUtils.connectFilePath(tarPath, srcFileName + ".xml");
								// 生成NCX文本内容
								String content = CreateNcxUtil.loadNcx(pdfPath, null);
								// 如果提取的内容不为空，则继续生成含node节点的导航文件，否则不生成
								if(StringUtils.isNotBlank(content)){
									// 将NCX文本转成DOCUMENT对象
									Document doc = DocumentHelper.parseText(content);
									// 生成临时带node的NCX文件
									XmlFileUtil.createXMLFile(doc, tempNcxPath);
									// 读取node节点文档---根据ncx内容生成符合在线预览的带node节点的xml
									List<CatalogDTO> catalogList = XmlFileUtil.NcxCvtXml(tempNcxPath,
											tempNodeXmlPath, maxPages, "");
									if(catalogList == null || catalogList.size() == 0){
										String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <book>\n  <totalpage>" + maxPages + "</totalpage>\n</book>";
										XmlFileUtil.createXMLFile(xmlContent, tempNodeXmlPath);
									}
								}else{//生成一个只带总页数的导航文件
									String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <book>\n  <totalpage>" + maxPages + "</totalpage>\n</book>";
									XmlFileUtil.createXMLFile(xmlContent, tempNodeXmlPath);
								}
								
								logger.debug(" ====pdfPath======= " + pdfPath + "\n ======tarPath======" + tarPath+ "\n ======maxPages======" + maxPages);
								
								// 转换SWF文件
								PdfToSwfUtil.pdf2Swf(1,pdfPath,tarPath,null,maxPages,null,true);
								
								// 生成一个Pattern,同时编译一个正则表达式 ,判断是否存在swf文件，如果没有则说明未转换成功。
								Pattern p = Pattern.compile(".+\\.(swf)$");
								ArrayList<File> files = DoFileUtils.filePattern(new File(tarPath),p);
								if(files == null || files.size() <= 0){
									b = false;
									describe = "转换失败:转SWF文件未成功.";
								}
							}
					  }
				}
			} catch (Exception e) {
				b = false;
				describe = "转换失败:" + e.getMessage();
			} finally {
				// 第三步：写状态和失败的原因，表示在转换完成,status ->  2：转换成功 3：转换失败
				if(converFile != null){
					if(!checkStatus(converFile)){
						if (b) {
							converFile.setStatus(2); //2：转换成功
						} else {
							converFile.setStatus(3); //3：转换失败
							//添加重试次数
							converFile.setRetryNum(converFile.getRetryNum() + 1);
						}
						converFile.setDescribes(describe);
						//执行更新
						baseQueryService.update(converFile);
					 }
					
					//*******************************写历史表************start*****************
					//如果(转换成功)或者(转换失败并且重试次数为3)，则放入到转换历史表中
					if(checkStatus(converFile)){
						try {
							ResConverfileTaskHistory  converFileHistory = new ResConverfileTaskHistory();
							//拷贝对象
							BeanUtils.copyProperties(converFileHistory,converFile);
							//在历史表中创建新对象
							converFileHistory.setId(null);
							baseQueryService.create(converFileHistory);
							//在删除在待转换表中的记录
							baseQueryService.delete(ResConverfileTask.class, converFile.getId());
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
					//*******************************写历史表************end*****************
						
					}
				}
			// 监控的代码
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
	
	//判断状态：是否是成功或者处理失败并且重试3次记录
	public boolean checkStatus(ResConverfileTask converFile){
		if(converFile != null){
			return converFile.getStatus() == 2  || (converFile.getStatus() == 3 && converFile.getRetryNum() == 3);
		}else{
			return false;
		}
		
	}
}
