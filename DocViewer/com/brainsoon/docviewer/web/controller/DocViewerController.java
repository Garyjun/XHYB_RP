package com.brainsoon.docviewer.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.cnmarc.CNMarc;
import com.brainsoon.common.util.dofile.cnmarc.CNMarcConstants;
import com.brainsoon.common.util.dofile.cnmarc.ICNMarcService;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.conver.OfficeToPdfUtils;
import com.brainsoon.common.util.dofile.conver.PdfToSwfUtil;
import com.brainsoon.common.util.dofile.conver.TxtToPdfUtils;
import com.brainsoon.common.util.dofile.image.ImgCoverUtil;
import com.brainsoon.common.util.dofile.metadata.FileMetadataFactory;
import com.brainsoon.common.util.dofile.metadata.fo.Multimedia;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.FilePathUtil;
import com.brainsoon.common.util.dofile.util.PropertiesReader;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.dofile.util.XmlUtil;
import com.brainsoon.common.util.dofile.view.CatalogDTO;
import com.brainsoon.common.util.dofile.view.CreateNcxUtil;
import com.brainsoon.common.util.dofile.view.ReadOnLineUtil;
import com.brainsoon.common.util.dofile.view.XmlFileUtil;
import com.brainsoon.common.util.dofile.zip.ZipOrRarUtil;
import com.brainsoon.docviewer.model.Viewer;
import com.brainsoon.docviewer.service.IDocViewerService;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.docviewer.support.ViewStatus;
import com.brainsoon.resource.service.ICollectResService;

/**
 * 
 * @ClassName: DocViewerController
 * @Description: 在线预览
 * @author tanghui
 * @date 2014-5-5 下午2:55:14
 * @update 2014-09-29 下午 添加了在线预览前对文件状态的判断
 * 
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DocViewerController {
	protected static final Logger logger = Logger.getLogger(DocViewerController.class);

	/** 默认命名空间 **/
	private final String baseUrl = "/docviewer/";
	@Autowired
	@Qualifier("docViewerService")
	private IDocViewerService docViewerService;
	@Autowired
	private ICollectResService collectResService;
	@Autowired
	@Qualifier("resConverfileTaskService")
	private IResConverfileTaskService resConverfileTaskService;
	private static final String PUBLISH_READ_WRITE_QUEUE = WebappConfigUtil.getParameter("PUBLISH_READ_WRITE_QUEUE");
	public  static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(),"\\", "/");
	private static final String documentFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.documentFormat);
	private static final String videoFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat);
	private static final String audioFormat = PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat);
	public  static final String CONVER_FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirCFR(),"\\", "/");
	/**
	 * 
	 * @Title: checkFileIsOk 
	 * @Description: 校验文件是否可以预览
	 * @param  filePath 源文件路径 （相对）
	 * @return num 文件状态 
	 * 0：待转换 
	 * 1：转换中 
	 * 2：转换成功 
	 * 3：转换失败
	 * 4：无需转换
	 * 5：文件未进入待转换队列，写入出错
	 * 6：传入的文件路径为空
	 * 7：文件不存在
	 * 8：无法识别文件扩展名
	 * 具体类型参照：ViewStatus 枚举类
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "checkFileIsOk", method = { RequestMethod.GET })
	public void checkFileIsOk(HttpServletRequest request,
			HttpServletResponse response){
		try {
			logger.info("-----------------------------进入checkFileIsOk方法----------------------------------------");
			//文件原路径 相对
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
			//文件原路径 绝对
			filePath = FILE_ROOT+ filePath.replaceAll("\\\\", "/").replaceAll("//", "/");
			//文件原路径 文件objectId
			String id = request.getParameter("id");
			//文件类型
			String fileType = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
			logger.info("---------------------------传过来的filePath--"+filePath+"----------------------------------------");
			
			//从转换表中查询转换后的路径 绝对
			String converPath = docViewerService.getConverPathByObjectId(id);
			//转换后的路径 相对
			String relfilePath = converPath.replace(WebAppUtils.getWebAppBaseFileDirCFR().replaceAll("\\\\", "/").replaceAll("//", "/"), "");//相对
			logger.info("-----------------------------路径CONVER_FILE_ROOT"+converPath+"----------------------------------------");
			
			int num = checkFileCanPreview(converPath+"/",fileType);
			logger.info("-----------------------------num"+num+"-结束---------------------------------------");
			
			setReturnResponseInfo(response, getMsg(num,filePath,id,relfilePath),"text/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @Title: pdfViewer2NAV
	 * @Description: pdf文件预览---获取pdf导航 支持的格式为：pdf、office系列（word/ppt）
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "pdfViewer2nav", method = { RequestMethod.GET })
	public void pdfViewer2NAV(HttpServletRequest request,
			HttpServletResponse response) {
		 try {
			 // 文件路径 相对
				String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
				// 对应的id
				String id = request.getParameter("id");
				// 绝对
				String absfilePath = WebAppUtils.getWebAppBaseFileDirFR().replaceAll("\\\\", "/").replaceAll("//", "/")+filePath;
				String absfileDir = absfilePath.substring(0, absfilePath.lastIndexOf("/")+1);
				// 相对
				String relfilePath = WebAppUtils.getWebAppRelFileDirFR().replaceAll("\\\\", "/").replaceAll("//", "/")+filePath;
				String relfileDir = relfilePath.substring(0, relfilePath.lastIndexOf("/")+1);
				
				//获取不带扩展名的文件名
			    String srcFileName = DoFileUtils.getFileNameNoEx(absfilePath);
			    // NCX文件临时路径
				String tempNcxPath = DoFileUtils.connectFilePath(absfileDir, "book.ncx");
				// 带node节点的xml文件临时路径
				String tempNodeXmlPath = DoFileUtils.connectFilePath(absfileDir, "book.xml");
				
				//组装对象
				Viewer viewer = new Viewer(absfilePath,relfileDir,srcFileName,tempNcxPath,tempNodeXmlPath,"","");
				//调用方法
				goPdfViewer2NAV(request, response, viewer);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 
	 * @Title: pdfViewer2SWF
	 * @Description: pdf文件预览---转swf文件
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "pdfviewer2swf", method = { RequestMethod.GET })
	public void pdfViewer2SWF(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// 文件路径
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
			// 源文件绝对路径
			filePath = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirFR(),filePath);
			// 转换后的文件路径(目录)
			String tarPath  = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirCFR(),FilePathUtil.getConverFileSaveRelPath(filePath));
			// 获取不带扩展名的文件名
		    String srcFileName = DoFileUtils.getFileNameNoEx(filePath);
			// 开始页
			String thisIndex = request.getParameter("thisIndex");
			thisIndex = thisIndex==null?"1":thisIndex;
			// 最大页数
			String maxPages = request.getParameter("maxPages");
			// 获取pdf最大页数
			if (StringUtils.isBlank(maxPages)) {
				maxPages = PdfUtil.getPdfAllPageTotalNum(filePath) + "";
			}
			
			//组装对象
			Viewer viewer = new Viewer(filePath,tarPath,srcFileName,"","",thisIndex,maxPages);
			//调用方法
			goPdfViewer2SWF(request, response, viewer);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	



	/**
	 * 
	 * @Title: epubviewer
	 * @Description: epub文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "epubviewer", method = { RequestMethod.GET })
	public void epubviewer(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// 文件路径
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
			String id = request.getParameter("id");
			int num =0;
//			int num = checkFileCanPreview(filePath);
			String type = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
			if(type.equals("epub")){
				num =2;
			}
			if(num == 2 || num == 4){ //判断待预览的文件状态
				//最终的根路径
				//filePath = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirFR(),filePath);
				String epubType="epub";
				String newfilePath = null;
				String newfileRelPath = null;//转换相对目录
				if (epubType.contains(type)) {
					String regex = "^urn:.*"; 
					if(id.matches(regex)){
						newfilePath = FilePathUtil.filePreViewTempPath(filePath,id);//返回相对路径
						filePath = DoFileUtils.connectFilePath(FilePathUtil.getFileViewerBaseDir(),newfilePath);
					}
				}
				//组装对象
				Viewer viewer = new Viewer(filePath);
				//调用方法
				goEpubviewer(request, response, viewer);
			}else{
				setReturnResponseInfo(response,JSONObject.fromObject(getMsg(num,filePath,id,"")).toString(),"text/json");//TODO
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 
	 * @Title: marcviewer
	 * @Description: marc文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "marcviewer", method = { RequestMethod.GET })
	public String marcviewer(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String,Object> marcMap = new HashMap<String,Object>();//marc阅读相关
		try {
//			// 文件路径
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
		    if(StringUtils.isNotEmpty(filePath)){
		    	//最终的文件路径
				filePath = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirFR(),filePath);
		    	if (DoFileUtils.exitFile(filePath)) {
						List<CNMarc> marcs = ICNMarcService.loadCNMarcFromISO(filePath, CNMarcConstants.ENCODING);
						List<Map<String,String>> marcMaps1 = new ArrayList<Map<String,String>>();
						List<Map<String,String>> marcMaps2 = new ArrayList<Map<String,String>>();
						List<String> marcMaps3 = new ArrayList<String>();
						for (CNMarc cnMarc : marcs) {
							Map<String, String> transMap = null;
							try {
								transMap = ICNMarcService.getTranslateDesc(cnMarc);
								marcMaps1.add(transMap);
								transMap = ICNMarcService.getNoTranslateDesc(cnMarc);
								marcMaps2.add(transMap);
								marcMaps3.add(cnMarc.getMarcData());
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
						marcMap.put("1", marcMaps1);
						marcMap.put("2", marcMaps2);
						marcMap.put("3", marcMaps3);
			    }else{
			    	marcMap.put("3", "未获取到文件路径！");
			    }
		    }
			request.setAttribute("marcMap", marcMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "docviewer/jsp/marc";
	}
	
	
	
	/**
	 * 
	 * @Title: videoviewer
	 * @Description: 视频文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "videoviewer", method = { RequestMethod.GET })
	public void videoviewer(HttpServletRequest request,
			HttpServletResponse response) {
	   try {
		    // 文件路径
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
//			filePath = FILE_ROOT+ filePath.replaceAll("\\\\", "/").replaceAll("//", "/");
			String objectId = request.getParameter("id");
			
			String fileType = DoFileUtils.getExtensionName(filePath); // 获取文件扩展名;
			
			String videoType="flvmp3mp4swf";
			String regex = "^urn:.*"; 
			String newfilePath = null;
			if (videoType.contains(fileType)) {//不用转换的类型
				if(objectId.matches(regex)){
					newfilePath = FilePathUtil.filePreViewTempPath(filePath,objectId);//返回相对路径
				}
			}else {//需要转换的类型
				newfilePath = docViewerService.getConverPathByObjectId(objectId);
			}
			//组装对象
			Viewer viewer = new Viewer(newfilePath);
			//调用方法
			goVideoviewer(request, response, viewer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @Title: imgviewer
	 * @Description:图片文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "imgviewer", method = { RequestMethod.GET })
	public void imgviewer(HttpServletRequest request,HttpServletResponse response) {
		try {
			// 文件路径
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
			String objectId = request.getParameter("id");
			// 文件类型
			String fileType = DoFileUtils.getExtensionName(filePath); // 获取文件扩展名;
			String imageType="jpgpngbmpjpeggiftiftiff";
			String newfilePath = null;
			String newfileRelPath = null;//转换相对目录
			if (imageType.contains(fileType)) {//不用转换的类型
				String regex = "^urn:.*"; 
				if(objectId.matches(regex)){
					newfilePath = FilePathUtil.filePreViewTempPath(filePath,objectId);//返回相对路径
				}
			}else {//需要转换的类型
				String converPath = docViewerService.getConverPathByObjectId(objectId);
				newfilePath = converPath.replace(WebAppUtils.getWebAppBaseFileDirCFR(), "");
			}
			
			//组装对象
			Viewer viewer = new Viewer(newfilePath);
			//调用方法
			goImgviewer(request, response, viewer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	/**
	 * 
	 * @Title: htmlviewer
	 * @Description:html文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "htmlviewer", method = { RequestMethod.GET })
	public void htmlviewer(HttpServletRequest request,
			HttpServletResponse response) {
		  try {
			  	// 文件路径
				String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
				//组装对象
				Viewer viewer = new Viewer(filePath);
				//调用方法
				goHtmlviewer(request, response, viewer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * 
	 * @Title: zipviewer
	 * @Description: zip文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "zipviewer", method = { RequestMethod.GET })
	public void zipviewer(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// 文件路径
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
			String fileType = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
			String id = request.getParameter("id");
			int num = checkFileCanPreview(filePath,fileType);
			if(num == 2 || num == 4){ //判断待预览的文件状态
				//组装对象
				Viewer viewer = new Viewer(filePath);
				//调用方法
				goZipviewer(request, response, viewer);
			}else{
				setReturnResponseInfo(response, getMsg(num,filePath,id,""),"text/json");//TODO
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @Title: expandviewer
	 * @Description: 图书及聚合资源文件预览（扩展资源文件类型）
	 * 扩展资源类型(图书资源<tszy>、聚合资源<jhzy>)
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "expandviewer", method = { RequestMethod.GET })
	public void expandviewer(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			// 资源id
			String id = request.getParameter("id");
			
			// 资源类型
			String fileType = request.getParameter("fileType"); 

			//组装对象
			Viewer viewer = new Viewer(id,fileType);
			
			//调用方法
			goExpandviewer(request, response, viewer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @Title: indexviewer
	 * @Description: 用于测试文件预览的总页面
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "indexviewer", method = { RequestMethod.GET })
	public String indexviewer(HttpServletRequest request,
			HttpServletResponse response) {
		return "docviewer/jsp/demo";
	}
	
	
	//=============================************======================================
	//=============================以下代码为底层处理方法体=================================
	//=============================************======================================
	
	/**
	 * 
	 * @Title: goPdfViewer2NAV
	 * @Description: pdf文件预览---获取pdf导航 支持的格式为：pdf、office系列（word/ppt） 此方法为底层方法,一般情况不用动
	 * @param
	 * @return String
	 * @throws
	 */
	public void goPdfViewer2NAV(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) {
		try {
			// 源文件绝对路径
			String filePath = viewer.getFilePath();
			// 转换后的文件路径(目录)
			String tarPath  = viewer.getTarPath();
			// 文件类型
			String fileType = DoFileUtils.getExtensionName(filePath); // 获取文件扩展名;
			//获取不带扩展名的文件名
		    String srcFileName = DoFileUtils.getFileNameNoEx(filePath);
		    // NCX文件临时路径
			String tempNcxPath = viewer.getTempNcxPath();
			// 带node节点的xml文件临时路径
			String tempNodeXmlPath = viewer.getTempNodeXmlPath();
			
			// 获取pdf最大页数
			int maxPages = 0;
			String pdfPath = filePath;
			// 如果带node节点的文件不存在，则生成， 否则直接读取
			if (!new File(tempNodeXmlPath).exists()) {
				//如果不是PDF
				if(!"pdf".equals(fileType.toLowerCase())){
					//转换后的临时pdf路径，将其赋值给 filePath
					 	pdfPath = DoFileUtils.connectFilePath(tarPath, srcFileName + ".pdf");
					// 判断是否是office系列的，如果是，则需要转换成pdf
					if (PropertiesReader.getInstance().getProperty("officeFormat").toLowerCase().contains(fileType.toLowerCase())) {
						pdfPath = OfficeToPdfUtils.convertToPdf(filePath,pdfPath);
					} else if (PropertiesReader.getInstance().getProperty("txtFormat").toLowerCase().contains(fileType.toLowerCase())) {// 文本文件系列
						// 返回转换后的临时pdf路径，将其赋值给 filePath
						pdfPath = TxtToPdfUtils.convertToPdf(filePath,pdfPath);
					}
				}
				
				// 获取pdf最大页数
				maxPages = PdfUtil.getPdfAllPageTotalNum(pdfPath);
				// 生成NCX文本内容
				String content = CreateNcxUtil.loadNcx(pdfPath, null);
				//如果提取的内容不为空，则继续生成含node节点的导航文件，否则不生成
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
				}else{
					String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n <book>\n  <totalpage>" + maxPages + "</totalpage>\n</book>";
					XmlFileUtil.createXMLFile(xmlContent, tempNodeXmlPath);
				}
				

				//转换全部---转换SWF文件
//				ReadOnlineConverUtils.pdf2swfProcess(new File(pdfPath),
//					1, maxPages,tarPath, srcFileName);
				PdfToSwfUtil.pdf2Swf(1,pdfPath,tarPath,null,maxPages,"mswf",true);
				
			}
			
			Document document = XmlUtil.read(tempNodeXmlPath);
			if(document !=null){
				setReturnResponseInfo(response,document.asXML(),"application/octet-stream; charset=utf-8");
			}else{
				setReturnResponseInfo(response,"","application/octet-stream; charset=utf-8");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: goPdfViewer2SWF
	 * @Description: pdf文件预览---转swf文件
	 * @param
	 * @return String
	 * @throws
	 */
	public void goPdfViewer2SWF(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) {
		try {
		/*	// 源文件绝对路径
			String filePath = viewer.getFilePath();
			// 转换后的文件路径(目录)
			String tarPath  = viewer.getTarPath();
			// 文件类型
			String fileType = DoFileUtils.getExtensionName(filePath); // 获取文件扩展名;
			//获取不带扩展名的文件名
		    String srcFileName = DoFileUtils.getFileNameNoEx(filePath);
		    // 开始索引页
			String thisIndex = viewer.getThisIndex();
			// 最大页码数
			String maxPages = viewer.getMaxPages();
						
			//pdf路径
			String pdfPath = filePath;*/
			//如果不是PDF
//			if(!"pdf".equals(fileType.toLowerCase())){
//				// 转换后的临时pdf路径，将其赋值给 filePath
//				pdfPath = DoFileUtils.connectFilePath(tarPath, srcFileName + ".pdf");
//				// 判断是否是office系列的，如果是，则需要转换成pdf
//				if (PropertiesReader.getInstance().getProperty("officeFormat")
//						.toLowerCase().contains(fileType.toLowerCase())) {
//					filePath = OfficeToPdfUtils.convertToPdf(filePath,pdfPath);
//				} else if (PropertiesReader.getInstance().getProperty("txtFormat")
//						.toLowerCase().contains(fileType.toLowerCase())) {// 文本文件系列
//					// 返回转换后的临时pdf路径，将其赋值给 filePath
//					filePath = TxtToPdfUtils.convertToPdf(filePath,pdfPath);
//				}
//			}
			// 转换
//			ReadOnlineConverUtils.pdf2swfProcess(new File(pdfPath),
//					Integer.parseInt(thisIndex), Integer.parseInt(maxPages),
//					tarPath, srcFileName);
			//PdfToSwfUtil.pdf2Swf(1,pdfPath,tarPath,null);

			setReturnResponseInfo(response, "1","text/json");
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 
	 * @Title: goEpubviewer
	 * @Description: epub文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	public void goEpubviewer(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) {
		try {
			// 源文件绝对路径
			String filePath = viewer.getFilePath();
			if(filePath.contains("’")){
				filePath = filePath.replaceAll("’", "'");
			}
			// 文件类型
			String fileType = DoFileUtils.getExtensionName(filePath); // 获取文件扩展名;
			
			// 目录json
			String catalogListGson = "";
			// 目前采用截取最后一个目录
			HashMap<String, Object> hm = ReadOnLineUtil.getEpubInfo(filePath,fileType);
			if (null != hm) {
				@SuppressWarnings("unchecked")
				List<CatalogDTO> catalogList = (List<CatalogDTO>) hm.get("catalogList");
				if (null != catalogList) {
					// 添加根目录---图书目录
					// CatalogDTO catalog = new
					// CatalogDTO("0","-99999","图书目录",true,true);
					// catalogList.add(catalog);
					JSONArray jsonArray = JSONArray.fromObject(catalogList);
					catalogListGson = jsonArray.toString();
					catalogListGson = ReadOnLineUtil
							.stringToJson(catalogListGson);
					catalogListGson = ReadOnLineUtil
							.StringPageToJSON(catalogListGson);
				}
			}

			setReturnResponseInfo(response, catalogListGson,"text/json");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @Title: goVideoviewer
	 * @Description: 视频文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	public void goVideoviewer(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) throws UnsupportedEncodingException {
		// 源文件绝对路径
		String filePath = viewer.getFilePath();
		// 返回页面的相对路径
		StringBuffer fileReadRelPath = null; 
		//最终的根路径
		if(StringUtils.isNotBlank(filePath)){
			if(fileReadRelPath == null){
				fileReadRelPath = new StringBuffer();
			}
			//视频等
			String[] filePaths = filePath.split(",");
			for (int i = 0; i < filePaths.length; i++) {
				String filePathStr = filePaths[i];
				// 文件类型
				String fileType = DoFileUtils.getExtensionName(filePathStr); // 获取文件扩展名;
				// 最终的文件名称
				String fileName = "";
				// filePath 文件相对路径
				String fileRelPath = filePathStr; //默认为flv or mp3
				// 文件绝对路径
				if(fileType.equals("flv") || fileType.equals("swf") || fileType.equals("mp3")|| fileType.equals("mp4")){
					// 绝对路径
					filePath = DoFileUtils.connectFilePath(FilePathUtil.getFileViewerBaseDir(),filePath); 
					// 相对路径
					fileReadRelPath.append((FilePathUtil.getFileViewerRelDir() + fileRelPath).replaceAll("\\\\", "/"));
				}else{
					if(PropertiesReader.getInstance().getProperty(ConstantsDef.audioFormat).contains(fileType)){//音频
						fileName = DoFileUtils.getFileNameNoEx(filePathStr) +  ".mp3";
					}else if(PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat).contains(fileType)){//视频
						fileName = DoFileUtils.getFileNameNoEx(filePathStr) +  ".flv";
					}else{
						logger.error("无法识别文件类型.");
					}
					// 如果不是flv，则到转换后的文件路径(目录)中去找文件
					// 转换后的文件路径(相对目录)
					fileRelPath = FilePathUtil.getConverFileSaveRelPath(filePath);
					// 绝对路径
					filePath  = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirCFR(),fileRelPath + fileName);
					// 相对路径
					fileReadRelPath.append((WebAppUtils.getWebAppRelFileDirCFR() +  fileRelPath + fileName).replaceAll("\\\\", "/"));
					
				}
				String videoWH = "";
				 if(
					PropertiesReader.getInstance().getProperty(ConstantsDef.videoFormat).contains(fileType)
					 || PropertiesReader.getInstance().getProperty(ConstantsDef.animaFormat).contains(fileType)
					 ) {
					 Multimedia video = (Multimedia) FileMetadataFactory.getMetadata(filePath);
					 videoWH = "|" + video.getWidth() + "," + video.getHeight();
					 fileReadRelPath.append(videoWH);
				 }
				 
				 fileReadRelPath.append(",");
			}
		}
		
		if(fileReadRelPath != null){
			filePath = StringUtils.substringBeforeLast(fileReadRelPath.toString(), ",");// 返回流对象
		}
		setReturnResponseInfo(response, filePath,"");
	}
	
	
	/**
	 * 
	 * @Title: goImgviewer
	 * @Description:图片文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	public void goImgviewer(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) throws UnsupportedEncodingException {
		// 源文件相对路径
		String filePath = viewer.getFilePath();
		// 返回页面的相对路径
		StringBuffer fileReadRelPath = null; 
		//最终的根路径
		if(StringUtils.isNotBlank(filePath)){
			if(fileReadRelPath == null){
				fileReadRelPath = new StringBuffer();
			}
			String[] filePaths = filePath.split(",");
			for (int i = 0; i < filePaths.length; i++) {
				String filePathStr = filePaths[i];
				// 文件类型
				String fileType = DoFileUtils.getExtensionName(filePathStr); // 获取文件扩展名;
				// filePath 文件相对路径
				String fileRelPath = filePathStr;
				// 最终的文件名称
				String fileName = DoFileUtils.getFileNameNoEx(fileRelPath);
				// 文件绝对路径
				if(fileType.equals("tif") || fileType.equals("tiff") //如果是tiff，则转换成jpg 
						){
					// 原绝对路径
					filePath = DoFileUtils.connectFilePath(FilePathUtil.getFileViewerBaseDir(),fileRelPath); 
					// 转换后的文件路径(相对目录)
					fileRelPath = fileRelPath.substring(0, fileRelPath.lastIndexOf(".")+1)+"jpg";  
					// 新的绝对路径
					String newFilePath  = FilePathUtil.getFileViewerBaseDir()+fileRelPath; 
					//执行转换
					ImgCoverUtil.conver2Other(filePath, newFilePath);
					// 相对文件路径
					fileReadRelPath = fileReadRelPath.append((FilePathUtil.getFileViewerRelDir() + "/" + fileRelPath).replaceAll("\\\\", "/"));
				}else{
					// 相对路径
					fileReadRelPath.append((FilePathUtil.getFileViewerRelDir() + "/" + fileRelPath).replaceAll("\\\\", "/")).append(",");
				}
			}
		}
		
		if(fileReadRelPath != null){
			filePath = StringUtils.substringBeforeLast(fileReadRelPath.toString(), ",");// 返回流对象
		}
		setReturnResponseInfo(response, filePath,"");
	}
	
	
	/**
	 * 
	 * @Title: goHtmlviewer
	 * @Description:html文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	public void goHtmlviewer(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) throws UnsupportedEncodingException {
		// 源文件绝对路径
		String filePath = viewer.getFilePath();
		// 相对路径
		filePath = (WebAppUtils.getWebAppRelFileDirFR() + filePath).replaceAll("\\\\", "/");
		setReturnResponseInfo(response, filePath,"");
	}
	
	
	
	
	/**
	 * 
	 * @Title: goZipviewer
	 * @Description: zip文件预览
	 * @param
	 * @return String
	 * @throws
	 */
	public void goZipviewer(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) {
		try {
			// 源文件绝对路径
			String filePath = viewer.getFilePath();
			// 原绝对路径
			String baseFilePath = DoFileUtils.connectFilePath(WebAppUtils.getWebAppBaseFileDirFR(),filePath); 
			// 文件类型
			String fileType = DoFileUtils.getExtensionName(filePath); // 获取文件扩展名;
			// 目录json
			String catalogListGson = "";
			List<CatalogDTO> catalogList = new ArrayList<CatalogDTO>();
			// 解压后的绝对路径
			String unZipBasePath = "";
			// 解压后的相对路径
			String unZipRelPath = "";
			//用于判断是否做过自解压，好根据此来判断最后的阅读路径
			boolean b = false;
			if(StringUtils.isNotBlank(filePath)){
				//如果是压缩文件并且未解压的，则需要先解压,否则直接解析目录结构
				if(fileType.equals("zip") || fileType.equals("rar")){
					// 文件名称(带扩展名)
					String fileFullName = DoFileUtils.getFileNameHasEx(filePath);
					// 解压后的路径  = 原文件的相对路径+文件类型
					String unZipPath = fileType + "/" + filePath.substring(WebAppUtils.getWebAppBaseFileDirFR().length(), filePath.length() - fileFullName.length());
					// 解压后的绝对路径
					unZipBasePath = DoFileUtils.connectFilePath(WebAppUtils.getWebRootBaseDir(ConstantsDef.viewer),unZipPath);
					// 解压后的相对路径
					unZipRelPath = DoFileUtils.connectFilePath(WebAppUtils.getWebRootRelDir(ConstantsDef.viewer),unZipPath);
					//如果文件夹路径存在 && 文件的个数不为零，则不解压
					File unZipRelPathFile = new File(unZipBasePath);
					if(unZipRelPathFile.exists() && unZipRelPathFile.listFiles().length == 0){
						// 解压
						ZipOrRarUtil.unzip(baseFilePath, unZipBasePath, fileType);
					}
					//将解压后的路径指向新的路径
					filePath = unZipBasePath;
					b = true;
				}
				
				File dirFile = new File(filePath);
				int i = 1;
				if (dirFile.isDirectory()) {// 是否是目录，是目录则遍历
					File[] fileChild = dirFile.listFiles();
					for (File file2 : fileChild) {
						if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
							doSetCatalogList(file2, i, 0, catalogList, b);
							i++;
						}else if(file2.isDirectory()){ //是目录
							doSetCatalogList(file2, i, 0, catalogList, b);
							findFiles2(file2,i,catalogList,b);
							i++;
						}
					}
				} else {
					doSetCatalogList(dirFile, i, 0, catalogList, b);
					i++;
				}
			}
			
			
			if (null != catalogList) {
				JSONArray jsonArray = JSONArray.fromObject(catalogList);
				catalogListGson = jsonArray.toString();
				catalogListGson = ReadOnLineUtil
						.stringToJson(catalogListGson);
				catalogListGson = ReadOnLineUtil
						.StringPageToJSON(catalogListGson);
			}

			setReturnResponseInfo(response, catalogListGson,"text/json");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 递归查找指定类型文件
	 * @param file22 遍历目录
	 */
	public void findFiles2(File dirFile,int i,List<CatalogDTO> catalogList,boolean b) {
		int k = i * 1000;
		// 判断目录是否存在
		if (dirFile.isDirectory()) {// 是否是目录，是目录则遍历
			File[] fileChild = dirFile.listFiles();
			for (File file2 : fileChild) {
				if (file2.isFile()) {// 只对文件做处理，过滤到文件夹
					doSetCatalogList(file2, k, i, catalogList, b);
					k++;
				}else if(file2.isDirectory()){ //是目录
					doSetCatalogList(file2, k, i, catalogList, b);
					findFiles2(file2,k,catalogList,b);
					k++;
				}
			}
		} else {
			doSetCatalogList(dirFile, k, i, catalogList, b);
			k++;
		}
	}
	
	/**给 CatalogDTO对象设置值*/
	public void doSetCatalogList(File file,int k,int i,List<CatalogDTO> catalogList,boolean b){
		String srcRelPath = ""; //最后的文件相对路径
		if(!b){ //fileRoot
			 srcRelPath = FilePathUtil.getFileRelPath(
					WebAppUtils.getWebAppBaseFileDirFR(),
					file.getAbsolutePath());
		}else{ // viewer
			 srcRelPath = FilePathUtil.getFileRelPath(
					WebAppUtils.getWebRootBaseDir(""),
					file.getAbsolutePath());
		}
		//创建对象并放入集合中
		catalogList.add(new CatalogDTO(k+"",i+"",file.getName(),srcRelPath));
	}
	
	
	
	
	/**
	 * 
	 * @Title: 图书及聚合资源文件预览（扩展资源文件类型）
	 * @Description: 图书及聚合资源文件预览（扩展资源文件类型）
	 * 扩展资源类型(图书资源<tszy>、聚合资源<jhzy>)
	 * @param 
	 * @return String
	 * @throws
	 */
	public void goExpandviewer(HttpServletRequest request,
			HttpServletResponse response,Viewer viewer) {
		try {
			// 资源id
			String id = viewer.getId();
			// 资源类型 --- 图书资源<tszy>、聚合资源<jhzy>
			//String fileType = viewer.getFileType();
			
			// 目录json
			String catalogListGson = "";
			List<CatalogDTO> catalogList = null;
			//判断id和类型是否为空，不为空，则调用 其他类中--生成 List<CatalogDTO> 集合对象的方法
			if(StringUtils.isNotEmpty(id)){
				//调用
				catalogList = collectResService.getResDirAndFile(id);
			}
			
			if (null != catalogList) {
				JSONArray jsonArray = JSONArray.fromObject(catalogList);
				catalogListGson = jsonArray.toString();
				catalogListGson = ReadOnLineUtil
						.stringToJson(catalogListGson);
				catalogListGson = ReadOnLineUtil
						.StringPageToJSON(catalogListGson);
			}
			
			setReturnResponseInfo(response, catalogListGson,"text/json");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * @Title: setReturnResponseInfo 
	 * @Description: 设置 setReturnResponseInfo 相关信息
	 * @param   
	 * @return HttpServletResponse 
	 * @throws
	 */
	public void setReturnResponseInfo(
			HttpServletResponse response,String filePath,String contentType){
		
		PrintWriter out = null;
		try {
			out = response.getWriter();
			response.setContentType("text/plain");
			if(StringUtils.isNotBlank(contentType)){
				response.setContentType(contentType);
			}
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setCharacterEncoding("UTF-8");
			out.println(filePath);// 返回流对象
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
	
	
	
	
	/**
	 * 
	 * @Title: checkFileCanPreview 
	 * @Description: 校验文件是否可以预览
	 * @param  filePath 源文件路径 （相对）
	 * @return num 文件状态 
	 * 0：待转换 
	 * 1：转换中 
	 * 2：转换成功 
	 * 3：转换失败
	 * 4：无需转换
	 * 5：文件未进入待转换队列，写入出错
	 * 6：传入的文件路径为空
	 * 7：文件不存在
	 * 8：无法识别文件扩展名
	 * 具体类型参照：ViewStatus 枚举类
	 * @throws
	 */
	public int checkFileCanPreview(String filePath,String fileType){
		logger.info("--------------------------------进入num返回值方法-----------------------------");
		HttpClientUtil http = new HttpClientUtil();
		logger.info("-----------------------------进入num返回值方法filepath路径"+filePath+"----------------------------------------");
		//String fileType = filePath.substring(filePath.lastIndexOf(".")+1,filePath.length());
		String path = filePath.substring(0,filePath.lastIndexOf(".")+1);
		logger.info("-----------------------------打印转换前path"+path+"----------------------------------------");
		//文档
		if(fileType.equals("mp4")||fileType.equals("mp3")||fileType.equals("flv")||fileType.equals("swf")){
			return 2;
		}
		if (checkArrContainsSoStr(documentFormat,fileType)) {
			if (!filePath.endsWith("/")) {
				filePath = filePath+"/";
			}
			//path = filePath.substring(0,filePath.lastIndexOf("/"));
			
			path = filePath+"1.swf";
		//音频
		}else if(checkArrContainsSoStr(videoFormat,fileType)){
			path = path+"flv";
		}else if(checkArrContainsSoStr(audioFormat,fileType)){
			path = path+"mp3";
		}
		logger.info("-----------------------------打印转换后path"+path+"----------------------------------------");
		String num ="";
			File file = new File(path);
			if(file.exists()){
				logger.info("-----------------------------判断path是否存在"+file.exists()+"----------------------------------------");
				return 2;
			}else{
				num = http.executeGet(PUBLISH_READ_WRITE_QUEUE + "?filePath="+ filePath);
			}
		if(!StringUtils.isNotBlank(num)){
			logger.info("-----------------------------如果num不存在返回5----------------------------------------");
			return 5;
		}else{
			logger.info("--------------------------------num值为"+JSONObject.fromObject(num).getInt("status")+"-----------------------------");
		    return JSONObject.fromObject(num).getInt("status");
		}
		
	}
	
	public String getMsg(int num,String srcPath,String id,String relfilePath){
		if(num == 3||num == 5){
			return "{\"noteInfo\":\"" + ViewStatus.getName(num)+ " 状态码：【" +  num + "】<input type='button' value='重试' onclick=doTaskHistoryByPath('" + srcPath+"','"+ id +"');> \",\"noteNum\":\"" + num + "\"}";
		
		}else{
			return "{\"noteInfo\":\"" + ViewStatus.getName(num)+ "  状态码：【" +  num + "】\",\"noteNum\":\"" + num + "\",\"relfilePath\":\"" + relfilePath + "\"}";
		}
	}
	/**
	 *
	 * @Title: checkArrContainsSoStr
	 * @Description:判断某字符串是否包含某个字符串
	 * @param
	 * @return boolean
	 * @throws
	 */
	public static boolean checkArrContainsSoStr(String strArr,String soStr) {
		boolean b = false;
		if(StringUtils.isNotBlank(strArr) && StringUtils.isNotBlank(soStr)){
			String[] strArray = strArr.split(",");
			for (int i = 0; i < strArray.length; i++) {
				if(strArray[i].equals(soStr)){
					b = true;
					break;
				}
			}
		}
		return b;
	}

}
