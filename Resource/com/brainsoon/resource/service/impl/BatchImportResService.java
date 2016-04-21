package com.brainsoon.resource.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.antlr.grammar.v3.ANTLRv3Parser.throwsSpec_return;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.common.service.impl.BaseService;
import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.DoiGenerateUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.dofile.content.PdfUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.common.util.isbn.ISBNChecker;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
import com.brainsoon.resource.po.AnalyzeExcel;
import com.brainsoon.resource.po.ItemAnalyzeExcel;
import com.brainsoon.resource.po.UploadTask;
import com.brainsoon.resource.po.UploadTaskDetail;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IBatchImportResService;
import com.brainsoon.resource.service.ICollectResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ExcelData;
import com.brainsoon.resource.support.ExcelDataCell;
import com.brainsoon.resource.support.ExcelDataDetailMK;
import com.brainsoon.resource.support.ExcelDataRow;
import com.brainsoon.resource.support.ImportResExcelFile;
import com.brainsoon.resource.support.ImportResThread;
import com.brainsoon.resource.support.SemanticResponse;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resource.util.CharacterUtils;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.resource.util.ResUtils;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.CommonMetaData;
import com.brainsoon.semantic.ontology.model.DoFileQueue;
import com.brainsoon.semantic.ontology.model.DoFileQueueList;
import com.brainsoon.semantic.ontology.model.ExtendMetaData;
import com.brainsoon.semantic.schema.CustomMetaData;
import com.brainsoon.semantic.schema.LomProperty;
import com.brainsoon.semantic.schema.QueryImportCode;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.statistics.service.ISourceNumService;
import com.brainsoon.system.model.Company;
import com.brainsoon.system.model.ResTargetData;
import com.brainsoon.system.model.RunNumber;
import com.brainsoon.system.model.Staff;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ICompanyService;
import com.brainsoon.system.service.IStaffService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SystemConstants.BatchImportDetaillType;
import com.brainsoon.system.support.SystemConstants.ConsumeType;
import com.brainsoon.system.support.SystemConstants.ImportStatus;
import com.brainsoon.system.support.SystemConstants.Language;
import com.brainsoon.system.support.SystemConstants.OpeningRate;
import com.brainsoon.system.support.SystemConstants.ResourceMoudle;
import com.brainsoon.system.support.SystemConstants.ResourceStatus;
import com.brainsoon.system.util.FieldExcelValidator;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

/**
 * 批量导入教育资源，图书和组合资源除外
 * 
 * @author zuo
 * 
 */
@Service(value="batchImportResService")
public class BatchImportResService extends BaseService implements
		IBatchImportResService {

	private final static String FTP_LOCAL_MAPPING = WebappConfigUtil.getParameter("FTP_LOCAL_MAPPING");
	public final static String FILE_TEMP = WebAppUtils.getWebRootBaseDir(ConstantsDef.fileTemp);
	private final static SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static final String FILE_ROOT = StringUtils.replace(WebAppUtils.getWebAppBaseFileDirFR(), "\\", "/");
	//String fileRoot = WebAppUtils.getWebRootRelDir(ConstantsDef.fileRoot).replaceAll("\\\\", "/");
	private static final String PUBLISH_SAVE_URL = WebappConfigUtil.getParameter("PUBLISH_SAVE_URL");
	private static final String PUBLISH_OVERRIDE_URL = WebappConfigUtil.getParameter("PUBLISH_OVERRIDE_URL");
	private final static String PUBLISH_FILE_WRITE_QUEUE = WebappConfigUtil.getParameter("PUBLISH_FILE_WRITE_QUEUE");
	private final static String CA_FILERES_SAVE_URL = WebappConfigUtil.getParameter("CA_FILERES_SAVE_URL");
	private final static String PUBLISH_UPDATE_META = WebappConfigUtil.getParameter("PUBLISH_UPDATE_META");

	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private ICollectResService collectResService;
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private ISourceNumService sourceNumService;
	@Autowired
	private IStaffService staffService;
	@Autowired
	private ICompanyService companyService;
	@Autowired
	private ISysParameterService sysParameterService;

	@Autowired
	@Qualifier("resConverfileTaskService")
	private IResConverfileTaskService resConverfileTaskService;
	/**
	 * 执行导入任务
	 */
	public String doTask(Asset asset, ImportResExcelFile taskData,
			UploadTask task,Map<Integer,String> resultLog) {
//		logger.info("开始导入————————————————————————————————————");
//		UploadTaskDetail detailInfo = new UploadTaskDetail();
//		detailInfo.setCreateTime(new Date());
//		detailInfo.setTask(task);
////		asset.getExcelNum()
//		int excelNum =0 ;
//		try {
//			String repeatType = taskData.getRepeatType();
//			String repeatRelevanceType = taskData.getRepeatRelevanceType();
//			String libType = taskData.getLibType();
//			CommonMetaData commonMeta;
//			ExtendMetaData extendMeta;
//			String xpath = asset.getImportXpath();
//			String relationIds = "";
//			commonMeta = asset.getCommonMetaData();
////			extendMeta = asset.getExtendMetaData();
//			commonMeta.setLibType(libType);
//			String keyword = commonMeta.getKeywords();
//			if (StringUtils.isNotBlank(keyword) && keyword.indexOf("，") >= 0) {
//				keyword = keyword.replaceAll("，", ",");
//				commonMeta.setKeywords(keyword);
//			}
//			detailInfo.setExcelNum(excelNum);
//			String type = commonMeta.getType();
//			String title = commonMeta.getTitle();
//			title = title.replaceAll("[:\\[\\]()]", "*");
//			String source = commonMeta.getSource();
//			source = source.replaceAll("[:\\[\\]()]", "*");
//			String creator = commonMeta.getCreator();
//			creator = creator.replaceAll("[:\\[\\]()]", "*");
//			boolean isCover = false;
//			logger.info("开始导入ffffffffffffffffffffff————————————————————————————————————");
//			try {
//				if ("T06".equals(type)) { // 出版图书批量导入
//					String objectId = "";
//					if (StringUtils.equalsIgnoreCase(repeatType, "2")) {
//						List<Ca> repeatCaWithResVersions = baseSemanticSerivce
//								.getCaResourceByResVersion(
//										source,
//										commonMeta.getType(),
//										title,
//										creator,
//										commonMeta.getResVersion(),
//										extendMeta.getISBN(),
//										commonMeta.getModule());
//						if (repeatCaWithResVersions != null
//								&& repeatCaWithResVersions.size() == 1) {
//							Ca ca = repeatCaWithResVersions.get(0);
//							asset.getCommonMetaData().setResVersion(
//									ca.getCommonMetaData().getResVersion());
//							objectId = collectResService.overrideImportBookRes(asset, ca);
//							isCover = true;
//						}
//					}
//					logger.info("开始导入gggggggggggggggggggggggggg————————————————————————————————————");
//					if (!isCover) {
//						List<Ca> repeatCas = baseSemanticSerivce
//								.getCaResourceByMoreCondition(
//										source,
//										commonMeta.getType(),
//										title,
//										creator,
//										extendMeta.getISBN(),
//										commonMeta.getModule());
//						logger.info("开始导入uuuuuuuuuuuuuuuuuuuuuuuuuuuuu————————————————————————————————————");
//						if (repeatCas != null && repeatCas.size() > 0) {
//							if (StringUtils.equalsIgnoreCase(repeatType, "2")) {
//								// 取重复资源第一个覆盖
//								if (repeatCas.size() == 1) {
//									Ca ca = repeatCas.get(0);
//									asset.getCommonMetaData().setResVersion(
//											ca.getCommonMetaData()
//													.getResVersion());
//									objectId = collectResService.overrideImportBookRes(
//											asset, ca);
//								} else {
//									Ca ca = getMaxVersionCa(repeatCas,"");
//									asset.getCommonMetaData().setResVersion(
//											ca.getCommonMetaData()
//													.getResVersion());
//									objectId = collectResService.overrideImportBookRes(
//											asset, ca);
//								}
//							} else if (StringUtils.equalsIgnoreCase(repeatType,
//									"3")) {
//								// 忽略
//								detailInfo.setRemark("第【" + (excelNum)
//										+ "】行重复，已忽略");
//								resultLog.put(excelNum, "资源重复，已忽略");
//								return "0";
//							} else if (StringUtils.equalsIgnoreCase(repeatType,
//									"1")) {
//								// 创建新版本
//								if (repeatCas.size() == 1) {
//									Ca ca = repeatCas.get(0);
//									String resVersion = ca.getCommonMetaData()
//											.getResVersion();
//									if (resVersion != null
//											&& !"".equals(resVersion)) {
//										asset.getCommonMetaData()
//												.setResVersion(
//														(Integer.parseInt(resVersion) + 1)
//																+ "");
//									}
//								} else {
//									Ca ca = getMaxVersionCa(repeatCas,"");
//									String resVersion = ca.getCommonMetaData()
//											.getResVersion();
//									if (resVersion != null
//											&& !"".equals(resVersion)) {
//										asset.getCommonMetaData()
//												.setResVersion(
//														(Integer.parseInt(resVersion) + 1)
//																+ "");
//									}
//								}
//								logger.info("开始导入uyyyyyyyyyyyyyyyyyyy————————————————————————————————————");
//								objectId = collectResService
//										.saveImportBookRes(asset);
//								logger.info("开始导入ttttttttttttttttt————————————————————————————————————");
//							}
//						} else {
//							logger.info("开始导入hhhhhhhhhhhhhhhhhhh————————————————————————————————————");
//							asset.getCommonMetaData().setResVersion("1");
//							objectId = collectResService.saveImportBookRes(asset);
//						}
//					}
//					sysOperateService.saveHistory(
//							WorkFlowUtils.getExecuId(objectId, libType),
//							"", "草稿", "导入", new Date(),
//							taskData.getUserId());
//				} else {
//					// 获取文件的MD5
//					String md5File = "";
//					try {
////						md5File = MD5Util.getFileMD5String(new File(asset
////								.getUploadFile()));
//					} catch (IOException e) {
//						logger.error(e.getMessage());
//					}
//	//				String deleteFilePath = ""; //覆盖资源是原来的文件要删除   转换队列中也要删除 
//					logger.info("开始导入iiiiiiiiiiiiiiiiiiiii————————————————————————————————————");
//					if (StringUtils.equalsIgnoreCase(repeatType, "2")) {
//						List<Asset> repeatAssetWithResVersions = null;
//						if(xpath == null || "".equals(xpath)){
//							repeatAssetWithResVersions = baseSemanticSerivce
//									.getResourceByResVersion(
//											source,
//											commonMeta.getType(),
//											title,
//											creator, md5File,
//											commonMeta.getResVersion(),
//											commonMeta.getModule());
//						}else{
//							repeatAssetWithResVersions = baseSemanticSerivce.getResourceByXpathAndVersion(source, commonMeta.getType(), title, creator, md5File, commonMeta.getResVersion(), commonMeta.getModule(), xpath);
//						}
//						if (repeatAssetWithResVersions != null
//								&& repeatAssetWithResVersions.size() == 1) {
//							Asset ast = repeatAssetWithResVersions.get(0);
//							asset.setObjectId(ast.getObjectId());
//							commonMeta.setObjectId(ast.getCommonMetaData()
//									.getObjectId());
////							asset.getExtendMetaData().setObjectId(
////									ast.getExtendMetaData().getObjectId());
//							isCover = true;
////							List<com.brainsoon.semantic.ontology.model.File> files = ast.getFiles();
////							if(files!=null && files.size()>0){
////								com.brainsoon.semantic.ontology.model.File oldFile = files.get(0);
////								deleteFilePath = oldFile.getPath();
////							}
//						}
//					}
//					if (!isCover) {
//						// 查重，为了提高效率，先根据元数据查重，如果重复，直接覆盖
//						List<Asset> repeatRes = null;
//						if(xpath == null || "".equals(xpath)){
//							repeatRes = baseSemanticSerivce
//									.getResourceByMoreCondition(
//											source,
//											commonMeta.getType(),
//											title,
//											creator,
//											commonMeta.getModule(), md5File);
//						}else{
//							repeatRes = baseSemanticSerivce.getResourceByMoreConditionAndXpath(source, commonMeta.getType(), title, creator, commonMeta.getModule(), md5File, xpath);
//						}
//						if (repeatRes != null && repeatRes.size() > 0) {
//							if (StringUtils.equalsIgnoreCase(repeatType, "2")) {
//								// 取重复资源第一个覆盖
//								Asset ast = new Asset();
//								if (repeatRes.size() == 1) {
//									ast = repeatRes.get(0);
//								} else {
//									ast = getMaxVersionAsset(repeatRes);
//								}
//								asset.setObjectId(ast.getObjectId());
//								commonMeta.setObjectId(ast.getCommonMetaData()
//										.getObjectId());
////								asset.getExtendMetaData().setObjectId(
////										ast.getExtendMetaData().getObjectId());
////								List<com.brainsoon.semantic.ontology.model.File> files = ast.getFiles();
////								if(files!=null && files.size()>0){
////									com.brainsoon.semantic.ontology.model.File oldFile = files.get(0);
////									deleteFilePath = oldFile.getPath();
////								}
//							} else if (StringUtils.equalsIgnoreCase(repeatType,
//									"3")) {
//								// 忽略
//								detailInfo.setRemark("第【" + (excelNum)
//										+ "】行重复，已忽略");
//								logger.info("第【" + (excelNum) + "】行重复，已忽略");
//								resultLog.put(excelNum, "资源重复，已忽略");
//								create(detailInfo);
//								return "0";
//							} else if (StringUtils.equalsIgnoreCase(repeatType,
//									"1")) {
//								// 创建新版本
//								if (repeatRes.size() == 1) {
//									Asset ast = repeatRes.get(0);
//									String resVersion = ast.getCommonMetaData()
//											.getResVersion();
//									if (resVersion != null
//											&& !"".equals(resVersion)) {
//										asset.getCommonMetaData()
//												.setResVersion(
//														(Integer.parseInt(resVersion) + 1)
//																+ "");
//									}
//								} else {
//									Asset ast = getMaxVersionAsset(repeatRes);
//									String resVersion = ast.getCommonMetaData()
//											.getResVersion();
//									if (resVersion != null
//											&& !"".equals(resVersion)) {
//										asset.getCommonMetaData()
//												.setResVersion(
//														(Integer.parseInt(resVersion) + 1)
//																+ "");
//									}
//								}
//								// 建立关联关系
//								if (StringUtils.equalsIgnoreCase(
//										repeatRelevanceType, "1")) {
//									relationIds = "";
//									for (Asset asset2 : repeatRes) {
//										if (StringUtils.isBlank(relationIds)) {
//											relationIds += asset2.getObjectId();
//										} else {
//											relationIds += ","
//													+ asset2.getObjectId();
//										}
//									}
//								}
//							}
//						}
//					}
//					// 保存资源
//					logger.info("开始导入ggggggggggggggggggggggggggggg————————————————————————————————————");
//					String objectId = "";
////					String objectId = resourceService.saveResourceAndThumb(
////							asset, "", asset.getUploadFile(), null, "",
////							repeatType);
////					if (StringUtils.equalsIgnoreCase(repeatType, "2") && !"".equals(deleteFilePath)) {
////						File f = new File(BresAction.FILE_ROOT+ deleteFilePath);
////						if(f.exists()){
////							//删除文件
////							try{
////								FileUtils.forceDelete(f.getParentFile());
////							}catch (Exception e) {
////								logger.error("删除文件失败"+e.getMessage()+f.getParentFile().getAbsolutePath());
////							}
////						}
////					}
//					if (StringUtils.isNotBlank(objectId)
//							&& StringUtils.isNotBlank(relationIds)) {
//						// 自动关联资源
//						baseSemanticSerivce
//								.assetRelation(objectId, relationIds);
//					}
//					logger.info("开始导入kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk————————————————————————————————————");
//					sysOperateService
//							.saveHistory(
//									WorkFlowUtils.getExecuId(objectId, libType),
//									"", "草稿", "导入", new Date(),
//									taskData.getUserId());
//				}
//			} catch (Exception e) {
//				logger.info(e.getMessage());
//				detailInfo.setRemark("第【" + (excelNum) + "】行失败，失败原因:"
//						+ e.getMessage());
//				create(detailInfo);
//				resultLog.put(excelNum, e.getMessage());
//				return "0";
//			}
//			logger.info("开始导入llllllllllllllllllllllll————————————————————————————————————");
//			detailInfo.setRemark("第【" + excelNum + "】行，导入成功！");
//			logger.info("第【" + excelNum + "】行，导入成功！");
//			detailInfo.setStatus(1);
//			create(detailInfo);
//			// resourceService.backWriteStatus(task, succNum,
//			// excelData.getTotalNum(), taskData);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.info(e.getMessage());
//			detailInfo.setRemark(e.getMessage());
//			create(detailInfo);
//			resultLog.put(excelNum, e.getMessage());
//			return "0";
//		}
		return "1";
	}

	

	public Ca getMaxVersionCa(List<Ca> repeatCas,String versionKey) {
		Map<String, Ca> resVersions = new HashMap<String, Ca>();
		for (Ca ca : repeatCas) {
			//CommonMetaData commonMetaData = ca.getCommonMetaData();
			String resVersion = ca.getMetadataMap().get(versionKey);
			if(StringUtils.isNotBlank(resVersion))
				resVersions.put(resVersion, ca);
		}
		Set<String> keys = resVersions.keySet();
		int[] keyArray = new int[keys.size()];
		int m = 0;
		String resVersion = "";
		for (String key : keys) {
			if (!"".equals(key)) {
				keyArray[m] = Integer.parseInt(key);
			} else {
				keyArray[m] = 0;
			}
			m++;
		}
		Arrays.sort(keyArray);
		int temp = keyArray[keyArray.length - 1];
		if(temp<10){
			resVersion = "0"+temp;
		}else{
			resVersion = temp+"";
		}
		Ca ca = resVersions.get(resVersion);
		return ca;
	}

	public Asset getMaxVersionAsset(List<Asset> repeatAssets) {
		Map<String, Asset> resVersions = new HashMap<String, Asset>();
		for (Asset asset : repeatAssets) {
			CommonMetaData commonMetaData = asset.getCommonMetaData();
			String resVersion = commonMetaData.getResVersion();
			resVersions.put(resVersion, asset);
		}
		Set<String> keys = resVersions.keySet();
		int[] keyArray = new int[keys.size()];
		int m = 0;
		for (String key : keys) {
			if (!"".equals(key)) {
				keyArray[m] = Integer.parseInt(key);
			} else {
				keyArray[m] = 0;
			}
			m++;
		}
		Arrays.sort(keyArray);
		int temp = keyArray[keyArray.length - 1];
		Asset asset = resVersions.get(temp + "");
		return asset;
	}

	/**
	 * 导出模板
	 */
	public File getExcelTemplete(String publishType) {
		 List<MetadataDefinition> cm1 = MetadataSupport.getAllMetadateDefineList();
//		com.brainsoon.semantic.schema.ontology.CustomMetaData cm1 = MetaService.getBaseMetaSchemas("CommonMetaData");
		return createExcel(cm1,publishType);
	}

	public List<LomProperty> getPropertyByModule(String module) {
		String datas = baseSemanticSerivce.getCustomMetaData(module);
		Gson gson = new Gson();
		CustomMetaData dataObj = gson.fromJson(datas, CustomMetaData.class);
		List<LomProperty> props = dataObj.getCustomPropertys();
		return props;
	}

	/**
	 * 创建excel模板
	 * 
	 * @param path
	 * @param props
	 */
	@SuppressWarnings("null")
	public File createExcel(List<MetadataDefinition> list,String publishType) {
		//File excel = new File(path);
		Map<String,MetadataDefinition> metadataDefinition = MetadataSupport.getAllMetadataDefinition();
		HSSFSheet oresTempleteSheet = null;
		HSSFWorkbook workbook = null;
			workbook = new HSSFWorkbook();
			 HSSFCellStyle style = workbook.createCellStyle();
			 style.setFillForegroundColor(HSSFColor.AQUA.index);// 设置背景色
			 style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	          style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平   
	          style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
	          style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
	          style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
	          style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框  
	          //生成一个字体
	          HSSFFont font=workbook.createFont();
	          font.setFontName("宋体");
	          font.setFontHeightInPoints((short)13);
	          font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
	         //把字体应用到当前的样式
	          style.setFont(font);
		HSSFSheet sheet = workbook.createSheet("数据清单");
		sheet.setColumnWidth(0, 20 * 256);
		// 处理title
		HSSFRow titlerow0 =  sheet.createRow(0);
		HSSFCell cell0 =null;
		cell0  = titlerow0.createCell(0);
		cell0.setCellStyle(style);
		HSSFRow titleRow = sheet.createRow(0);
		HSSFCell cell  = titleRow.createCell(0);
		cell.setCellValue("文件路径");
		cell.setCellStyle(style);
		HSSFRow titleRow1 = sheet.createRow(1);
		titleRow1.setZeroHeight(true);
		HSSFCell cell1  = titleRow1.createCell(0);
		cell1.setCellStyle(style);
		cell1.setCellValue("filePath");
		sheet.setColumnWidth(0, 20 * 256);
//		List<MetadataDefinition> list = cm1.get(index);
//		cell  = titleRow.createCell(list.size()+1);
//		cell.setCellValue("版本号");
		oresTempleteSheet = workbook.getSheetAt(0);
	    // 定义注释的大小和位置
//		Drawing patriarch1 = oresTempleteSheet.createDrawingPatriarch(); 
//		HSSFClientAnchor clientAnchor1 = new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6,7);  
//		Comment comment1 = patriarch1.createCellComment(clientAnchor1);  
//			//comment.setString(createHelper.createRichTextString("必填项;"));  
//	        // 设置注释内容
////	        comment1.setString(new HSSFRichTextString("(版本号)是以'v'加数字组成，例v1"));
//	    cell.setCellComment(comment1);
//		cell.setCellStyle(style);
		List<MetadataDefinition> metadataDefinitions = MetadataSupport
				.getMetadateDefines(publishType);
		int num = 1;
		boolean fag = false;
			if(metadataDefinitions.size()>0){
				fag = true;
			}
		for(int j =0;j<metadataDefinitions.size();j++){
			cell1 = titleRow1.createCell((short)j+1);
			cell1.setCellStyle(style);
			num = num+1;
			//生成第二行表数据
			cell1.setCellValue(metadataDefinitions.get(j).getFieldName());
			sheet.setColumnWidth(j+1, 20 * 256);
			//第一行显示数据
			HSSFCell cell4 = titleRow.createCell((short)j+1);
			cell4.setCellStyle(style);
			cell4.setCellValue(metadataDefinitions.get(j).getFieldZhName());
//			if(metadataDefinitions.get(j).getFieldName().equals("qualityTime")){
//				System.out.println(metadataDefinitions.get(j).getFieldZhName());
//			}
			String tab = FieldExcelValidator.checkFieldHasPoint(metadataDefinition, metadataDefinitions.get(j).getFieldName());
			if(!tab.equals("")){
			   oresTempleteSheet = workbook.getSheetAt(0);
		        // 定义注释的大小和位置，详见文档
			   Drawing patriarch = oresTempleteSheet.createDrawingPatriarch(); 
			   HSSFClientAnchor clientAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6,7);  
			   Comment comment = patriarch.createCellComment(clientAnchor);  
		        // 设置注释内容
		       comment.setString(new HSSFRichTextString(tab));
		       cell4.setCellComment(comment);
		       System.out.println(tab);
			}
		}
		if(!fag){
			titlerow0 = sheet.createRow(0);
			cell0 = titlerow0.createCell(0);
			cell0.setCellValue("没有定义元数据");
			cell0.setCellStyle(style);
		}
		File outFile = new File(FILE_TEMP + File.separator
				+ dateformat2.format(new Date()) + ".xls");
		OutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
			workbook.write(out);// 写入File
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
		return outFile;
	}

	public void fillData(String excelPath, ExcelData data,Map<Integer,String> resultLog) {
		File excel = new File(excelPath);
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(excel));
			HSSFSheet sheet = workbook.getSheetAt(0);
//			HSSFRow batchNumAndNameRow = sheet.getRow(1);
			//清单名称
			//data.setqName(batchNumAndNameRow.getCell(1).getStringCellValue());
			//batchNumAndNameRow.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
			//String batchNum = batchNumAndNameRow.getCell(4)
			//		.getStringCellValue();
			//获得批次号
			//data.setBatchNum(batchNum);

			// 处理数据
			/** 第一步，先读取标记行 */
			ExcelDataDetailMK marker = null;
			HSSFRow markerRow = sheet.getRow(1);
			int lastCell = markerRow.getLastCellNum() - 1;
			HSSFCell tempCell = null;
			String tempValue = "";
//			String[] tempValueArray;
			//将excel中的标记转换为实体
			ExcelDataDetailMK[] markers = new ExcelDataDetailMK[lastCell + 1];
			for (int i = 0; i <= lastCell; i++) {
				tempCell = markerRow.getCell(i);
				tempValue = tempCell.getStringCellValue();
				if(tempValue!=null && !"".equals(tempValue)){
					marker = new ExcelDataDetailMK(tempValue);
					markers[i] = marker;
				}
			}
			data.setMarkers(markers);

//			Map<String,MetadataDefinition> metadataDefinition = MetadataSupport.getAllMetadataDefinition();
			/** 第二步，解析每行数据 */
			int lastRowNum = sheet.getLastRowNum();
			HSSFRow tempRow;
			String[] datas;
			//用于存放dataRow对象的集合
			List<ExcelDataRow> rows = new ArrayList<ExcelDataRow>();
			//excel里的每条数据对象dataRow
			ExcelDataRow dataRow = null;
			for (int i = 2; i <= lastRowNum; i++) {
				tempRow = sheet.getRow(i);
				if (tempRow != null) {
					datas = new String[lastCell + 1];
					dataRow = new ExcelDataRow();
					HSSFCell tempCell0 = tempRow.getCell(0);
					HSSFCell tempCell1 = tempRow.getCell(1);
					logger.info("9999999999999999999==================="+i+"-----"+tempCell0+"---------"+tempCell1);
//					if((tempCell0 == null ||tempCell0.getStringCellValue() == null ||"".equals(tempCell0.getStringCellValue())) 
//							&& (tempCell1 == null ||tempCell1.getStringCellValue() == null ||"".equals(tempCell1.getStringCellValue()))) {
//						continue;
//					}
					for (int j = 0; j <= lastCell; j++) {
						tempCell = tempRow.getCell(j);
						if (null != tempCell) {
							int type = tempCell.getCellType();
							String cellContent = "";
							if(HSSFCell.CELL_TYPE_NUMERIC == type){
								DecimalFormat df = new DecimalFormat("0");
							    cellContent =df.format(tempCell.getNumericCellValue());
							    if(HSSFDateUtil.isCellDateFormatted(tempCell)){
							    	
									Date d = tempCell.getDateCellValue();
									DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
									cellContent = formater.format(d);
								}
							    datas[j] = cellContent.trim();
							}else{
								datas[j] = tempCell.getStringCellValue().trim();
							}
						} else {
							datas[j] = "";
						}
						
					}
						dataRow.setDatas(datas);
						dataRow.setNum(i + 1);
						rows.add(dataRow);
				}
			}
			data.setRows(rows);
			data.setTotalNum(rows.size());
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		} finally {
			excel = null;
		}

	}

	/**
	 * 转换数据
	 * 
	 * @param excelData
	 * @param module
	 *            模块code
	 * @param needSaves
	 *            需要保存的asset
	 * @param task
	 * @param asset
	 */
	public void doParseData2Asset(ExcelData excelData, String module,
			List<Asset> needSaves, UploadTask task,Map<Integer,String> resultLog) {
		if (null == excelData)
			return;
		Asset asset = null;
		ExcelDataDetailMK[] markers = excelData.getMarkers();
		List<ExcelDataRow> rows = excelData.getRows();
		int maxRow = rows.size();
		String[] datas;
		String currentData;
		int datasLen = 0;
		ExcelDataDetailMK mk;
		String name;
		int order;// 顺序，大于0参与分类体系
		int necessary;// 是否必须
		int referClass;// 0 保存在CommonMetaData对象上，1保存在ExtendMetaData ，其他忽略
		List<ExcelDataCell> cells = new ArrayList<ExcelDataCell>();

		String moduleNameCN = ResourceMoudle.getValueByKey(module);
		ExcelDataCell moduleCell = new ExcelDataCell(0, moduleNameCN, "module",
				-1);
		moduleCell.setDataCode(module);
		ExcelDataCell cell;
		UploadTaskDetail detailInfo;
		Map<String, String> commonMetaDatas;
		Map<String, String> extendMetaDatas;
		for (int i = 0; i < maxRow; i++) {
			ExcelDataRow row = rows.get(i);
			Map<Integer,String> result = null; 
			datas = row.getDatas();
			datasLen = datas.length;
			cells.clear();
			cells.add(moduleCell);
			detailInfo = new UploadTaskDetail();
			detailInfo.setCreateTime(new Date());
			detailInfo.setTask(task);
			int rowNums = row.getNum();
			detailInfo.setExcelNum(rowNums);
			String error = "第【" + row.getNum() + "】行导入失败，";

			asset = initAsset();
			commonMetaDatas = asset.getCommonMetaData().getCommonMetaDatas();
//			extendMetaDatas = asset.getExtendMetaData().getExtendMetaDatas();
			boolean successData = true;// 数据验证是否通过
			for (int j = 0; j < datasLen; j++) {
				mk = markers[j];
				currentData = datas[j];
				name = mk.name;
				order = mk.order;
				referClass = mk.referClass;
				necessary = mk.necessary;
				if (necessary == 1 && StringUtils.isBlank(currentData)) {
					detailInfo.setRemark(error + "【" + mk.nameCN
							+ "】不允许为空，或者内容不合法，或者空行忽略");
					logger.info(error + "【" + mk.nameCN
							+ "】不允许为空，或者内容不合法，或者空行忽略");
					create(detailInfo);
					successData = false;
					resultLog.put(rowNums,  "【" + mk.nameCN
							+ "】不允许为空，或者内容不合法，或者空行忽略");
					continue;
				}
				if (order >= 0) {
					cell = new ExcelDataCell(order, currentData, name,
							referClass);
					cells.add(cell);
				} else {
					if ("knowledge_point".equals(name)) {
						if (StringUtils.isNotBlank(currentData)) {
							String knowledgeId = baseSemanticSerivce
									.getKnowledgeIDByName(currentData);
							if ("-1".equals(knowledgeId)) {
								detailInfo.setRemark(error + "知识点节点【"
										+ currentData + "】不存在");
								logger.info(error + "知识点节点【" + currentData
										+ "】不存在");
								create(detailInfo);
								successData = false;
								resultLog.put(rowNums,  "知识点节点【"
										+ currentData + "】不存在");
								continue;
							}
							commonMetaDatas.put("knowledge_point", knowledgeId);
						}
					} else {
						currentData = transformValue(name, currentData) + "";
						if (referClass == 0
								|| StringUtils.equalsIgnoreCase("filePath",
										name)
								|| StringUtils.equalsIgnoreCase("fileName",
										name)) {
							commonMetaDatas.put(name, currentData);
						} else if (referClass == 1) {
//							extendMetaDatas.put(name, currentData);
						}
					}
				}
			}
			if (successData) {
				try {
					// 转换分类体系字段,排序后调用接口
					sortMeta(cells);
					transCN2ENCode(cells);
//					fillEnCode(cells, commonMetaDatas, extendMetaDatas);
					// 手动赋值
//					asset.setExcelNum(row.getNum());
					asset.getCommonMetaData().setModule(module);

					// 赋值文件路径
					String fileName = commonMetaDatas.get("fileName");
					String filePath = commonMetaDatas.get("filePath");
					String uploadFile = "";
					if (filePath.indexOf(fileName) > 0) {
						uploadFile = FTP_LOCAL_MAPPING
								+ commonMetaDatas.get("filePath");
					} else {
						uploadFile = FTP_LOCAL_MAPPING
								+ commonMetaDatas.get("filePath")
								+ File.separator
								+ commonMetaDatas.get("fileName");
					}
					uploadFile = uploadFile.replaceAll("\\\\", "/");
					// 判断是否存在文件
					if (!new File(uploadFile).exists()) {
						detailInfo.setRemark(error + "，资源文件不存在，路径【"
								+ uploadFile + "】");
						logger.info(error + "，资源文件不存在，路径【" + uploadFile + "】");
						create(detailInfo);
						resultLog.put(rowNums,  "资源文件不存在，路径【"
								+ uploadFile + "】");
						continue;
					}
//					asset.setUploadFile(uploadFile);
					needSaves.add(asset);
				} catch (Exception e) {
					try{
						detailInfo.setRemark(error + e.getMessage());
						logger.info("99999999999999999======="+detailInfo);
						logger.info(error + e.getMessage());
						create(detailInfo);
						resultLog.put(rowNums, e.getMessage());
						continue;
					}catch(Exception e1){
						resultLog.put(rowNums, e1.getMessage());
						continue;
					}
				}
			}
		}
	}

	/**
	 * 转换数据
	 * 
	 * @param excelData
	 * @param module
	 *            模块code
	 * @param needSaves
	 *            需要保存的asset
	 * @param task
	 * @param asset
	 */
	public void doParseData2Ca(ExcelData excelData, List<Ca> needSaves,
			UploadTask task,Map<Integer,String> resultLog) {
		if (null == excelData)
			return;
		Ca ca = null;
		ExcelDataDetailMK[] markers = excelData.getMarkers();
		List<ExcelDataRow> rows = excelData.getRows();
		int maxRow = rows.size();
		String[] datas;
		String currentData;
		int datasLen = 0;
		ExcelDataDetailMK mk;
		String name;
		int order;// 顺序，大于0参与分类体系
		int necessary;// 是否必须
		int referClass;// 0 保存在CommonMetaData对象上，1保存在ExtendMetaData ，其他忽略
		List<ExcelDataCell> cells = new ArrayList<ExcelDataCell>();

		ExcelDataCell cell;
		UploadTaskDetail detailInfo;
		Map<String, String> commonMetaDatas;
		Map<String, String> extendMetaDatas;
		for (int i = 0; i < maxRow; i++) {
			ExcelDataRow row = rows.get(i);
			datas = row.getDatas();
			datasLen = datas.length;
			cells.clear();
			detailInfo = new UploadTaskDetail();
			detailInfo.setCreateTime(new Date());
			detailInfo.setTask(task);
			int rowNum = row.getNum();
			detailInfo.setExcelNum(rowNum);
			String error = "第【" + row.getNum() + "】行导入失败，";
			ca = initCa();
			commonMetaDatas = ca.getCommonMetaData().getCommonMetaDatas();
			extendMetaDatas = ca.getExtendMetaData().getExtendMetaDatas();
			boolean successData = true;// 数据验证是否通过
			for (int j = 0; j < datasLen; j++) {
				mk = markers[j];
				currentData = datas[j];
				name = mk.name;
				order = mk.order;
				referClass = mk.referClass;
				necessary = mk.necessary;
				if (necessary == 1 && StringUtils.isBlank(currentData)) {
					detailInfo.setRemark(error + "【" + mk.nameCN
							+ "】不允许为空，或者内容不合法，或者空行忽略");
					logger.info(error + "【" + mk.nameCN
							+ "】不允许为空，或者内容不合法，或者空行忽略");
					create(detailInfo);
					resultLog.put(rowNum, "【" + mk.nameCN
							+ "】不允许为空，或者内容不合法，或者空行忽略");
					successData = false;
					break;
				}
				if (order >= 0) {
					cell = new ExcelDataCell(order, currentData, name,
							referClass);
					cells.add(cell);
				} else {
					currentData = transformValue(name, currentData) + "";
					if ("publishType".equals(name)) {
						String nameValue = OperDbUtils.queryKeyByIndexAndName(
								"publishType", currentData);
						commonMetaDatas.put(name, nameValue);
					} else {
						if (referClass == 0
								|| StringUtils.equalsIgnoreCase("filePath",
										name)
								|| StringUtils.equalsIgnoreCase("fileName",
										name)) {
							commonMetaDatas.put(name, currentData);
						} else if (referClass == 1) {
							extendMetaDatas.put(name, currentData);
						}
					}

				}
			}
			if (successData) {
				try {
					// 转换分类体系字段,排序后调用接口
					sortMeta(cells);
					// transCN2ENCode(cells);
					fillEnCode(cells, commonMetaDatas, extendMetaDatas);
					// 手动赋值
					ca.setExcelNum(row.getNum());

					// 赋值文件路径
					String uploadFile = FTP_LOCAL_MAPPING
							+ commonMetaDatas.get("filePath") + File.separator
							+ commonMetaDatas.get("fileName");
					// 判断是否存在文件
					if (!new File(uploadFile).exists()) {
						detailInfo.setRemark(error + "，资源文件不存在，路径【"
								+ uploadFile + "】");
						logger.info(error + "，资源文件不存在，路径【" + uploadFile + "】");
						create(detailInfo);
						resultLog.put(rowNum, "资源文件不存在，路径【"
								+ uploadFile + "】");
						continue;
					}
					ca.setUploadFile(uploadFile);
					needSaves.add(ca);
				} catch (Exception e) {
					detailInfo.setRemark(error + e.getMessage());
					logger.info(error + e.getMessage());
					resultLog.put(rowNum, e.getMessage());
					create(detailInfo);
				}
			}
		}
	}

	/**
	 * 初始化
	 * 
	 * @return
	 */
	public Asset initAsset() {
		Asset asset = new Asset();
		CommonMetaData commonMeta = asset.getCommonMetaData();
		if (null == commonMeta) {
			commonMeta = new CommonMetaData();
			asset.setCommonMetaData(commonMeta);
		}
//		ExtendMetaData extendMeta = asset.getExtendMetaData();
//		if (null == extendMeta) {
//			extendMeta = new ExtendMetaData();
//			asset.setExtendMetaData(extendMeta);
//		}
		Map<String, String> commonMetaDatas = commonMeta.getCommonMetaDatas();
		if (null == commonMetaDatas) {
			commonMetaDatas = new HashMap<String, String>();
		}
//		Map<String, String> extendMetaDatas = extendMeta.getExtendMetaDatas();
//		if (null == extendMetaDatas) {
//			extendMetaDatas = new HashMap<String, String>();
//		}
		return asset;
	}

	/**
	 * 初始化出版资源
	 * 
	 * @return
	 */
	public Ca initCa() {
		Ca ca = new Ca();
		CommonMetaData commonMeta = ca.getCommonMetaData();
		if (null == commonMeta) {
			commonMeta = new CommonMetaData();
			ca.setCommonMetaData(commonMeta);
		}
		ExtendMetaData extendMeta = ca.getExtendMetaData();
		if (null == extendMeta) {
			extendMeta = new ExtendMetaData();
			ca.setExtendMetaData(extendMeta);
		}
		Map<String, String> commonMetaDatas = commonMeta.getCommonMetaDatas();
		if (null == commonMetaDatas) {
			commonMetaDatas = new HashMap<String, String>();
		}
		Map<String, String> extendMetaDatas = extendMeta.getExtendMetaDatas();
		if (null == extendMetaDatas) {
			extendMetaDatas = new HashMap<String, String>();
		}
		return ca;
	}

	public Object transformValue(String name, String value) {
		Class c = dictionaryMapper.get(name);
		if (null != c) {
			try {
				return c.getMethod("getValueByDesc",
						new Class[] { Object.class }).invoke(c, value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return value;
	}

	public Object transformKeyForDesc(String name, String value) {
		Class c = dictionaryMapper.get(name);
		if (null != c) {
			try {
				return c.getMethod("getValueByKey",
						new Class[] { Object.class }).invoke(c, value);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return value;
	}

	static Map<String, Class> dictionaryMapper = new HashMap<String, Class>();
	static {
		dictionaryMapper.put("down_type", ConsumeType.class);
		dictionaryMapper.put("view_type", ConsumeType.class);
		dictionaryMapper.put("language", Language.class);
		dictionaryMapper.put("public_or_not", OpeningRate.class);
		// dictionaryMapper.put("type", ResourceType.class); 分类体系接口会翻译
	}

	public void sortMeta(List<ExcelDataCell> cells) {
		Collections.sort(cells);
	}

	/**
	 * 转换分类体系为英文code
	 * 
	 * @param cells
	 */
	public void transCN2ENCode(List<ExcelDataCell> cells) {
		String version = "";
		String domainType = "1";
		List<String> ar = new ArrayList<String>(cells.size());
		for (ExcelDataCell excelDataCell : cells) {
			if (domainType.equals("1")
					&& StringUtils.equalsIgnoreCase("version",
							excelDataCell.getName())) {
				version = excelDataCell.getData();
				if (StringUtils.isNotBlank(version)) {
					domainType = "0";
				}
			}
			try {
				if (excelDataCell.getData() != null
						&& !"".equals(excelDataCell.getData().trim())) {
					ar.add(URLEncoder.encode(excelDataCell.getData(), "utf-8"));
				}
			} catch (UnsupportedEncodingException e) {
				logger.error("编码出错" + e.getMessage());
			}
		}
		String codes = StringUtils.join(ar, ",");
		String returnData = baseSemanticSerivce.queryImportCode(codes,
				domainType);
		if (StringUtils.isNotBlank(returnData)) {
			Gson gson = new Gson();
			QueryImportCode qCodes = gson.fromJson(returnData,
					QueryImportCode.class);
			// 给code赋值
			String xPath = qCodes.getXpath();
			String[] xPathArray = StringUtils.split(xPath, ",");
			for (int i = 0; i < xPathArray.length; i++) {
				cells.get(i).setDataCode(xPathArray[i]);
			}
			// 处理单元
			String unitId = qCodes.getUnitId();
			String unitName = qCodes.getUnitName();
			if (StringUtils.isNotBlank(unitId)) {
				ExcelDataCell unit = new ExcelDataCell(-1, unitId, "unit", 0);
				unit.setDataCode(unitId);
				ExcelDataCell unitNameCell = new ExcelDataCell(-1, unitName,
						"unitName", 0);
				unitNameCell.setDataCode(unitName);
				cells.add(unit);
				cells.add(unitNameCell);
			}
		} else {
			try {
				throw new ServiceException("数据【"
						+ URLDecoder.decode(codes, "utf-8") + "】，找不到匹配路径。");
			} catch (UnsupportedEncodingException e) {
				throw new ServiceException(e.getMessage());
			}
		}
	}

	/**
	 * 填充分类体系数据
	 * 
	 * @param cells
	 * @param commonMetaDatas
	 * @param extendMetaDatas
	 */
	public void fillEnCode(List<ExcelDataCell> cells,
			Map<String, String> commonMetaDatas,
			Map<String, String> extendMetaDatas) {
		if (null != cells) {
			int referClass;
			String name;
			String code;
			for (ExcelDataCell excelDataCell : cells) {
				referClass = excelDataCell.getReferClass();
				name = excelDataCell.getName();
				code = excelDataCell.getDataCode();
				if (referClass == 0) {
					commonMetaDatas.put(name, code);
				} else if (referClass == 1) {
					extendMetaDatas.put(name, code);
				}
			}
		}
	}

	@Override
	public Map<String, String> doMySqlWithExcel(UploadTaskDetail resTaskDetail) {
		Map<String, String> maps = new HashMap<String, String>();
		try {
			String bodyJson = "";
			JSONObject  da = null;
			if(resTaskDetail!=null){
				bodyJson = resTaskDetail.getBody();
				da = JSONObject.fromObject(bodyJson);
				
				Iterator items = da.keys();  
		        String key;     
		        String value; 
		        while (items.hasNext()) {  
		            key = (String) items.next();  
		            value = (String) da.get(key);  
		            maps.put(key, value);  
		            
		        }  
				//待修改
				System.out.println(maps.toString());
			}
			} catch (Exception e) {
				resTaskDetail.setRemark(resTaskDetail.getRemark()+",body数据json转换map出错！");
				resTaskDetail.setStatus(BatchImportDetaillType.STATUS4);//字表转换失败
				update(resTaskDetail);
			}
		return maps;
	}
	/**
	 * 解析XML文件
	 */
	@Override
	public List<Map<String, String>> savefile(String path,UploadTask uploadTask, UploadTaskDetail uploadTaskDetail)
			throws DocumentException {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> maperror = new HashMap<String, String>();
		Map<String, String> mapzheng = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		
		//处理map，将所有元数据模板中的信息保存到map中，解析到有值时就更新
		List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(uploadTask.getLibType());
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			if (metadataDefinition.getDefaultValue() !=null && !"".equals(metadataDefinition.getDefaultValue())){
				map.put(metadataDefinition.getFieldName(), metadataDefinition.getDefaultValue());
			}else {
				map.put(metadataDefinition.getFieldName(), "");
			}
			logger.info("【BatchImportResService】 解析XML文件 savefile 初始化map->>>map："+map.toString());
		}
		
		
		if(StringUtils.isNotBlank(path)){
			logger.info("------------进入解析代码-----------------");
			/*String fileType = upload.substring(upload.lastIndexOf(".") + 1, upload.length());
			String outPutPath =  FILE_ROOT + UUID.randomUUID();
			ZipOrRarUtil.unzip(FILE_TEMP+upload, outPutPath, fileType);
			String Filename=upload.substring(upload.lastIndexOf("\\")+1, upload.lastIndexOf("."));
			String filepath = outPutPath+"/"+Filename+"/"+"Main.xml";*/
			String wenjianfile = path;
			String filepath = path+"/"+"Main.xml";
			File files = new File(filepath);
			SAXReader reader = new SAXReader();
			Document dom = null;
			try {
				dom = reader.read(files);
			} catch (Exception e) {
				 throw new ServiceException("不符合XML文档规范，如：缺少结束标签，结构嵌套错误。"+e.getMessage());
			}
			//rootElement获取根节点
			Element rootElement =  dom.getRootElement(); 
			//获取二级节点
			Iterator ito = rootElement.elementIterator(); 
			//获取二级节点中的第一个
			while (ito.hasNext()) {
				Element elements = (Element) ito.next();
				if(elements.getName().equals("info")){
		
				for(Iterator iterator=elements.elementIterator();iterator.hasNext();){
					//elementInner是三级节点
					Element elementInner = (Element) iterator.next();   
					 if(elementInner.getName().equals("title")){
						 Attribute roleAttr=elementInner.attribute("role");
						 if(roleAttr==null){
							map.put("title", elementInner.getText());//*标题title
							logger.info("【BatchImportResService】 解析XML文件 savefile->>>标题title:"+elementInner.getText());
						 }else if (StringUtils.isNotBlank(roleAttr.getValue())){
							if("seriestitle".equals(roleAttr.getValue().trim().toLowerCase()) ){//兼容500本
								map.put("serialTitle", elementInner.getText());//*丛书名称serialTitle
								logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书名称serialTitle:"+elementInner.getText());
							}
						 }
							 
							
					 }
					 if(elementInner.getName().equals("subtitle")){
						map.put("subtitle", elementInner.getText());//*副标题subtitle
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>副标题subtitle:"+elementInner.getText());
					 }
					 if(elementInner.getName().equals("biblioid")){
						 Attribute classAttr=elementInner.attribute("class");
						 Attribute otherAttr=elementInner.attribute("otherclass");
						 if (StringUtils.isNotBlank(classAttr.getValue()) ){
							 if(classAttr.getValue().trim().toLowerCase().equals("isbn") || "issn".equals(classAttr.getValue().trim().toLowerCase())){
								 String isbn = elementInner.getText();
								 isbn = isbn.replaceAll("－", "-").replaceAll("—", "-").replaceAll(" ", "");
								 map.put("isbn", isbn);//*ISBNisbn
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>ISBNisbn:" + isbn);
							 }
						 }
						 if (StringUtils.isNotBlank(classAttr.getValue()) ){
							 if(classAttr.getValue().trim().toLowerCase().equals("pressnumber")){//此处换小写，把N该了
								 map.put("pressNumber", elementInner.getText());//*社内书号pressNumber
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>社内书号pressNumber:"+elementInner.getText());
							 }
						 }
						 if (otherAttr != null && StringUtils.isNotBlank(classAttr.getValue()) && StringUtils.isNotBlank(otherAttr.getValue())){
							 if(classAttr.getValue().trim().toLowerCase().equals("other") && otherAttr.getValue().trim().toLowerCase().equals("cip")){
								 map.put("cip", elementInner.getText());//*cip号cip
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>cip号cip:"+elementInner.getText());
							 }
							 if(classAttr.getValue().trim().toLowerCase().equals("other") && otherAttr.getValue().trim().toLowerCase().equals("prtcnt")){
								 map.put("prtcnt", elementInner.getText());//*印次prtcnt
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印次prtcnt:"+elementInner.getText());
							 }
							 if(classAttr.getValue().trim().toLowerCase().equals("other") && otherAttr.getValue().trim().toLowerCase().equals("yinci")){//兼容500本
								 map.put("prtcnt", elementInner.getText());//*印次prtcnt
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印次prtcnt:"+elementInner.getText());
							 }
						 }
					 }
					 /**
					  * 解析authorgroup节点
					  * elementInner代表的是第三级级节点，根据三级节点获取下级节点
					  * author代表四级节点
					  */
					 if(elementInner.getName().equals("authorgroup")){
						 StringBuffer bookAuthor = new StringBuffer();
						 StringBuffer contributor = new StringBuffer();
						 StringBuffer Editor = new StringBuffer();
						 StringBuffer fictionEditor = new StringBuffer();
						 StringBuffer translator = new StringBuffer();
						 StringBuffer designEditor = new StringBuffer();
						 StringBuffer seriesBoooksName = new StringBuffer();
						 StringBuffer executiveEditor = new StringBuffer();
						 Iterator author = elementInner.elementIterator("author");
						 while (author.hasNext()) {
							Staff staff = new Staff();
							Element auth = (Element) author.next(); 
							Attribute roleAttr=auth.attribute("role");
							if (roleAttr != null && StringUtils.isNotBlank(roleAttr.getValue())) {
								if("bookauthor".equals(roleAttr.getValue().trim().toLowerCase()) || roleAttr.getValue().trim().toLowerCase().equals("著")){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
									Element personname = auth.element("personname");
									if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										bookAuthor.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("editor") || roleAttr.getValue().trim().toLowerCase().equals("主编")){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
									/*Iterator personname = auth.elementIterator("personname");
									StringBuffer sbpersonname = new StringBuffer();
									String sbperson=null;
									while (personname.hasNext()) {
										Element personname1 = (Element) personname.next();
										sbpersonname.append(personname1.getText()+",");
									}
									if(sbpersonname.toString().length()>1){
										sbperson =  sbpersonname.toString().substring(0, sbpersonname.toString().length()-1);
										map.put("Editor",sbperson);
									}else{
										map.put("Editor",sbpersonname.toString());
									}*/
							 		Element personname = auth.element("personname");
							 		if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										Editor.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("contributor") || "编著".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										contributor.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("fictioneditor") || ("责任编辑").equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										fictionEditor.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("translator") || "译者姓名".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										translator.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("designeditor") || "策划编辑".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										designEditor.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("seriesboooksname") || "丛书作者姓名".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										seriesBoooksName.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("executiveeditor") || "执行编辑".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname && StringUtils.isNotBlank(personname.getText())){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname && StringUtils.isNotBlank(orgname.getText())){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation && StringUtils.isNotBlank(affiliation.getText())){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address && StringUtils.isNotBlank(address.getText())){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email && StringUtils.isNotBlank(email.getText())){
										staff.setEmail(email.getText());
									}
									if (StringUtils.isNotBlank(staff.getName())) {
										executiveEditor.append(staffService.doSaveOrUpdate(staff)+",");
									}
								 }	
							} 
						 }
						 if(StringUtils.isNotBlank(bookAuthor.toString())){
							 map.put("bookAuthor", bookAuthor.toString().substring(0, bookAuthor.toString().length()-1));//*著bookAuthor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>著bookAuthor:"+bookAuthor.toString().substring(0, bookAuthor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(contributor.toString())){
							 map.put("contributor", contributor.toString().substring(0, contributor.toString().length()-1));//*编著contributor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>编著contributor:"+contributor.toString().substring(0, contributor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(Editor.toString())){
							 map.put("editor", Editor.toString().substring(0, Editor.toString().length()-1));//*主编editor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>主编editor:"+Editor.toString().substring(0, Editor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(fictionEditor.toString())){
							 map.put("fictionEditor", fictionEditor.toString().substring(0, fictionEditor.toString().length()-1));//*责任编辑fictionEditor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>责任编辑fictionEditor:"+fictionEditor.toString().substring(0, fictionEditor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(translator.toString())){
							 map.put("translator", translator.toString().substring(0, translator.toString().length()-1));//*译者姓名translator
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>译者姓名translator:"+translator.toString().substring(0, translator.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(designEditor.toString())){
							 map.put("designEditor", designEditor.toString().substring(0, designEditor.toString().length()-1));//*策划编辑designEditor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>策划编辑designEditor:"+designEditor.toString().substring(0, designEditor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(seriesBoooksName.toString())){
							 map.put("seriesBoooksName", seriesBoooksName.toString().substring(0, seriesBoooksName.toString().length()-1));//*丛书作者姓名seriesBoooksName
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书作者姓名seriesBoooksName:"+seriesBoooksName.toString().substring(0, seriesBoooksName.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(executiveEditor.toString())){
							 map.put("executiveEditor", executiveEditor.toString().substring(0, executiveEditor.toString().length()-1));//*执行编辑executiveEditor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>执行编辑executiveEditor:"+executiveEditor.toString().substring(0, executiveEditor.toString().length()-1));
						 }
						 
					 }
					 if(elementInner.getName().toLowerCase().equals("publisher")){
						 Company company = new Company();
						 Element publishername = elementInner.element("publishername");
						 if(null!=publishername){
							 //map.put("publishername", publishername.getText());
							 company.setName(publishername.getText());
						 }
						 Element address = elementInner.element("address");
						 if(null!=address){
							 //map.put("address", address.getText());
							 company.setAddress(address.getText());
						 }
						 /*if(null!=publishername && null!=address){
							 
						 }*/
						 String companys  =  companyService.doSaveOrUpdate(company);
						 map.put("publishername", companys);//*出版社名称publishername
						 logger.info("【BatchImportResService】 解析XML文件 savefile->>>出版社名称publishername:"+companys);
					 }
					 if(elementInner.getName().equals("revhistory")){
						 Element revision = elementInner.element("revision");
						 if(null!=revision){
							 Element revnumber = revision.element("revnumber");
							 if(null!=revnumber){
								 //若版本为个位，前面加0
								/* String revnumberVal = revnumber.getText();
								 DecimalFormat df=new DecimalFormat("00");
							     revnumberVal=df.format(Integer.parseInt(revnumberVal));*/
							     
								 map.put("revnumber", revnumber.getText());//*版本号revnumber
								 //map.put("revhistory", revnumber.getText());//*版本revhistory   没有该字段的信息
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>版本号revnumber:"+revnumber.getText());
							 }
							 Element date = revision.element("date");
							 if(null!=date){
								 map.put("date", date.getText());//*版本日期date
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>版本日期date:"+date.getText());
							 }
						 }
					 }
					 if(elementInner.getName().equals("abstract")){
						 Iterator para = elementInner.elementIterator("para");
						 StringBuffer sbpara = new StringBuffer();
						 String sbparas = null;
						 while (para.hasNext()) {
							Element paras = (Element) para.next();
							if(StringUtils.isNotBlank(paras.getText())){
//								sbpara.append(paras.getText()+"<br>");
								sbpara.append(paras.getText());
							}
						}
						 if(sbpara.toString().length()>4){
							sbparas = sbpara.toString().substring(0, sbpara.toString().length()-4);
							map.put("abstract", sbparas);
							logger.info("【BatchImportResService】 解析XML文件 savefile->>>摘要abstract:"+sbparas);
						 }else{
							map.put("abstract", sbpara.toString());//*摘要abstract
							logger.info("【BatchImportResService】 解析XML文件 savefile->>>摘要abstract:"+sbpara.toString());
						 }
					 }
					 if(elementInner.getName().equals("legalnotice")){
						 Element para = elementInner.element("para");
						 if(null!=para){
							 map.put("legalnotice", para.getText());//*法律声明legalnotice
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>法律声明legalnotice:"+para.getText());
						 }
					 }
					 
					 if(elementInner.getName().equals("keywordset")){
						 Iterator keyword = elementInner.elementIterator("keyword");
						 StringBuffer sbkeyword = new StringBuffer();
							String sbkey=null;
							while (keyword.hasNext()) {
								Element keyword1 = (Element) keyword.next();
								if(StringUtils.isNotBlank(keyword1.getText())){
									sbkeyword.append(keyword1.getText()+",");
								}
							}
							if(sbkeyword.toString().length()>1){
								sbkey =  sbkeyword.toString().substring(0, sbkeyword.toString().length()-1);
								map.put("keywordset",sbkey);
								logger.info("【BatchImportResService】 解析XML文件 savefile->>>关键字keywordset:"+sbkey);
							}else{
								map.put("keywordset",sbkeyword.toString());//*关键字keywordset
								logger.info("【BatchImportResService】 解析XML文件 savefile->>>关键字keywordset:"+sbkeyword.toString());
							}
					 }
					 if(elementInner.getName().equals("releaseinfo")){
						 Attribute roleAttr=elementInner.attribute("role");
						 if (StringUtils.isNotBlank(roleAttr.getValue())) {
							 if(roleAttr.getValue().trim().toLowerCase().equals("cipinfo")){
								 Iterator alt = elementInner.elementIterator("alt");
								 StringBuffer sbalt = new StringBuffer();
								 String sbalts = null;
								 while (alt.hasNext()) {
									Element alts = (Element) alt.next();
									if(StringUtils.isNotBlank(alts.getText())){
										sbalt.append(alts.getText());
									}
								}
								 if(sbalt.toString().length()>4){
									sbalts = sbalt.toString().substring(0, sbalt.toString().length()-4);
									map.put("cipInfo", sbalts);
									logger.info("【BatchImportResService】 解析XML文件 savefile->>>CIP数据cipInfo:"+sbalts);
								 }else{
									 map.put("cipInfo", sbalt.toString());//*CIP数据cipInfo
									 logger.info("【BatchImportResService】 解析XML文件 savefile->>>CIP数据cipInfo:"+sbalt.toString());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("pprice") || roleAttr.getValue().trim().toLowerCase().equals("price")){//兼容500本
								 map.put("pprice", elementInner.getText());//*纸质图书价格pprice
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>纸质图书价格pprice:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("cbclass") || roleAttr.getValue().trim().toLowerCase().equals("ztfclasscode")){//兼容500本
								 String cbclass = elementInner.getText();
								 cbclass = cbclass.replaceAll("，", ",");
								 map.put("cbclass", cbclass);//*中图分类号cbclass
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>中图分类号cbclass:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("totalpages")){
								 map.put("totalpages", elementInner.getText());//*页码totalpages
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>页码totalpages:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("digitcopyright")){
								 map.put("digitcopyright", elementInner.getText());//*数字版权digitcopyright
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>数字版权digitcopyright:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("dimensions")){
								 map.put("dimensions", elementInner.getText());//*开本dimensions
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>开本dimensions:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("coverdesigner")){//转换小写，把d换了
								 map.put("coverDesigner", elementInner.getText());//*封面设计coverDesigner
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>封面设计coverDesigner:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("binding")){
								 map.put("binding", elementInner.getText());//*装帧设计binding
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>装帧设计binding:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("layoutdesigner")){//转换小写，把d换了
								 map.put("layoutDesigner", elementInner.getText());//*版式设计layoutDesigner
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>版式设计layoutDesigner:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("proofreads")){
								 map.put("proofreads", elementInner.getText());//*责任校对proofreads
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>责任校对proofreads:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("alphabettitle")){//转换小写，把t换了
								 map.put("alphabetTitle", elementInner.getText());//*题名拼音alphabetTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>题名拼音alphabetTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("alttitle")){//转换小写，把t换了
								 map.put("altTitle", elementInner.getText());//*交替题名altTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>交替题名altTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("partitle")){//转换小写，把t换了
								 map.put("parTitle", elementInner.getText());//*并列题名parTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>并列题名parTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("othertitle")){//转换小写，把t换了
								 map.put("otherTitle", elementInner.getText());//*其他题名otherTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>其他题名otherTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("serialtitle")){//转换小写，把t换了
								 map.put("serialTitle", elementInner.getText());//*丛书名称serialTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书名称serialTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("type")){
								 map.put("type", elementInner.getText());//*资源类别type
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>资源类别type:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("printcopies")){//转换小写，把c换了
								 map.put("printCopies", elementInner.getText());//*印数printCopies
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印数printCopies:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("serialnumber")){//转换小写，把n换了
								 map.put("serialNumber", elementInner.getText());//*丛书序列serialNumber
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书序列serialNumber:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("printingunit")){//转换小写，把u换了
								 //map.put("printingUnit", elementInner.getText());//*印刷单位printingUnit
								 
								 Company company = new Company();
								 company.setName(elementInner.getText());
								 String printingUnit  =  companyService.doSaveOrUpdate(company);
								 map.put("printingUnit", printingUnit);//*印刷单位printingUnit
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印刷单位printingUnit:"+printingUnit);
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("words")){
								 map.put("words", elementInner.getText());//*字数words
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>字数words:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("ebookprice")){//转换小写，把p换了
								 map.put("ebookPrice", elementInner.getText());//*电子书价格ebookPrice
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>电子书价格ebookPrice:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("review")){
								 map.put("review", elementInner.getText());//*封底书评review
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>封底书评review:"+elementInner.getText());
							 }
							 /*if(roleAttr.getValue().trim().toLowerCase().equals("format")){
								 map.put("format", elementInner.getText());
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>format:"+elementInner.getText());
							 }*/
							 if(roleAttr.getValue().trim().toLowerCase().equals("distributionchannels")){//转换小写，把c换了
								 //map.put("distributionChannels", elementInner.getText());//*发行渠道distributionChannels
								 
								 Company company = new Company();
								 company.setName(elementInner.getText());
								 String distributionChannels  =  companyService.doSaveOrUpdate(company);
								 map.put("distributionChannels", distributionChannels);//*印刷单位printingUnit
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印刷单位printingUnit:"+distributionChannels);
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("printedsheets")){//转换小写，把s换了
								    String xmlss = null;
									xmlss=elementInner.asXML();
									xmlss=xmlss.substring(xmlss.indexOf(">")+1,xmlss.lastIndexOf("<"));
									map.put("printedSheets", xmlss);//*印张printedSheets
									logger.info("【BatchImportResService】 解析XML文件 savefile->>>印张printedSheets:"+xmlss);
								 //map.put("printedSheets", elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("language")){
								 map.put("language", elementInner.getText());//*语种language
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>语种language:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("images")){
								 Attribute imageshref=elementInner.attribute("href");
								 String image=wenjianfile+"/"+imageshref.getValue();
								 File file = new File(image);
								 if(!file.exists()){
									 sb.append("images路径未找到！请检查！");
								 }else{
									 mapzheng.put("images", imageshref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("source")){
								 Attribute sourcehref=elementInner.attribute("href");
								 String source=wenjianfile+"/"+sourcehref.getValue();
								 File file = new File(source);
								 if(!file.exists()){
									 sb.append("source路径未找到！请检查！");
								 }else{
									 mapzheng.put("source", sourcehref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("epub")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("epub", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("singlehighpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("singlehighpdf", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("singlelowerpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("singlelowerpdf", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("doublehighpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("doublehighpdf", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("doublelowerpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute lowerpdfhref=elementInner.attribute("href");
								 String lowerpdf=wenjianfile+"/"+lowerpdfhref.getValue();
								 File file = new File(lowerpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("doublelowerpdf", lowerpdfhref.getValue());
								 }
							 }
						}
					}
					if(elementInner.getName().equals("cover")){
						Element mediaobject = elementInner.element("mediaobject");
						Element imageobject = mediaobject.element("imageobject");
						Element imagedata = imageobject.element("imagedata");
						Attribute fileref = imagedata.attribute("fileref");
						if(StringUtils.isNotBlank(fileref.getValue())){
							File file = new File(wenjianfile+"/"+fileref.getValue());
							if(!file.exists()){
								sb.append("作者照片路径未找到,请检查!");
							}else{
								mapzheng.put("imagedata", fileref.getValue());
							}
						}
					}
					if(elementInner.getName().toLowerCase().equals("publisherdate")){
						/*Element year = elementInner.element("year");
						if(null!=year){
							map.put("year", year.getText());
						}
						Element holder = elementInner.element("holder");
						if(null!=holder){
							map.put("holder", holder.getText());
						}*/
						map.put("publisherDate", elementInner.getText());//*出版时间publisherDate
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>出版时间publisherDate:"+elementInner.getText());
					}
					if(elementInner.getName().toLowerCase().equals("pressclass")){
						map.put("pressclass", elementInner.getText());//*社内分类pressclass
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>社内分类pressclass:"+elementInner.getText());
					}
					if(elementInner.getName().toLowerCase().equals("subjectclass")){
						map.put("subjectclass", elementInner.getText());//*学科分类subjectclass
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>学科分类subjectclass:"+elementInner.getText());
					}
				} 
				
				
				}
				if(elements.getName().equals("include")){
					Attribute href = elements.attribute("href");
					File file=new File(wenjianfile+href.getValue());
					if(!file.exists()){
						sb.append("引用的配置文件不存在");
					}else{
						mapzheng.put("include", href.getValue());
					}
					
				}
					
			}
		}
		
		//处理摘要
		if (StringUtils.isBlank(map.get("abstract"))){
			//查找pdf文件
			File pdfParentFile = new File(path);
			Pattern p = Pattern.compile("([^\\s]+(\\.(?i)(pdf))$)");
			ArrayList<File> files = DoFileUtils.filePattern( pdfParentFile , p);
			
			//抽取第一本pdf的第四页的的文本
			if (files.size()>0) {
				File firstPdfFile = files.get(0);
				String abstractStr = "";
				try {
					abstractStr = PdfUtil.parsePdf(firstPdfFile.getAbsolutePath(),4,4);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (StringUtils.isNotBlank(abstractStr)) {
					
					try {
						abstractStr = abstractStr.replaceAll("\\s", "");
						abstractStr = abstractStr.replaceAll("　", "");
						abstractStr = abstractStr.replaceAll("序", "");
						abstractStr = abstractStr.replaceAll("内容提要", "");
						//全角转半角
						abstractStr = CharacterUtils.ToDBC(abstractStr);
						//过滤掉非UTF-8字符方法
						abstractStr = CharacterUtils.filterOffUtf8Mb4(abstractStr);
						//过滤特殊字符
						abstractStr = CharacterUtils.stringFilter(abstractStr);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//截取前200个字作为摘要
					if (abstractStr.length()>199) {
						abstractStr = abstractStr.substring(0, 199);
					}
				}
				
				map.put("abstract", abstractStr);
			}
			
		}
		
		//处理ISBN
		boolean isISBN = false;//是否需要从pdf中读取isbn
		String isbn = map.get("isbn");
		if (StringUtils.isBlank(isbn)) {
			isISBN = true;
		}else {
			/*try {
				ISBNChecker good = new ISBNChecker(isbn);
			} catch (Exception e) {
				isISBN = true;
			}*/
		}
		//执行获取ISBN
		if (isISBN) {
			//查找pdf文件
			File pdfParentFile = new File(path);
			Pattern p = Pattern.compile("([^\\s]+(\\.(?i)(pdf))$)");
			ArrayList<File> files = DoFileUtils.filePattern( pdfParentFile , p);
			
			//抽取第一本pdf的第四页的的文本
			if (files.size()>0) {
				File firstPdfFile = files.get(0);
				String isbnStr = "";
				try {
					isbnStr = PdfUtil.parsePdf(firstPdfFile.getAbsolutePath(),3,3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (StringUtils.isNotBlank(isbnStr)) {
					try {
						isbnStr = isbnStr.replaceAll("　", "");
						//全角转半角
						isbnStr = CharacterUtils.ToDBC(isbnStr);
						//过滤掉非UTF-8字符方法
						isbnStr = CharacterUtils.filterOffUtf8Mb4(isbnStr);
						//过滤特殊字符
						isbnStr = CharacterUtils.stringFilter(isbnStr);
						//获取ISBN
						isbnStr = getISBNToChar(isbnStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					if (StringUtils.isNotBlank(isbnStr)) {
						map.put("isbn", isbnStr);
					}
					
					/*try {
						ISBNChecker good = new ISBNChecker(isbnStr);
						map.put("isbn", isbnStr);
					} catch (Exception e) {
						logger.info("--------pdf读取ISBN出错："+e.getMessage());
					}*/
				}
				
			}
		}
		
		maperror.put("maperror", sb.toString());
		list.add(map);
		list.add(maperror);
		logger.info("【BatchImportResService】 解析XML文件 savefile->>>解析完成"+list.get(0).toString());
		
		uploadTaskDetail.setBody(map.toString());
		update(uploadTaskDetail);
		return list;
	}
	
	
	/**
	 * 
	* @Title: validateMetadata
	* @Description: 验证元数据的正确性 （包括必填项，格式等）
	* @param maps	从excel中读取的数据/从xml中读取的数据
	* @param needSaves	验证通过即保存到该list中
	* @param task	当前任务（即主表信息）
	* @param resultLog	日志信息
	* @param batchImportResService
	* @param checkRepeatMetadat	查重字段
	* @param resTaskDetail   子表信息（即要验证的该条资源）
	* @return void    返回类型
	* @throws
	 */
	public void doValidateMetadata(Map<String, String> maps,List<Ca> needSaves,UploadTask task,Map<Integer,String> resultLog/*,IBatchImportResService batchImportResService*/,Map<String,String> checkRepeatMetadat,UploadTaskDetail taskDetail){
		if(null==maps){
			return;
		}
		
		
		Map<String,MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinition();
		Set<String> sets = maps.keySet();
		String filePath = "";
		String title = "";
		logger.info("【BatchImportResService】 资源导入validateMetadata->>>方法开始");
		Ca ca = new Ca();
		String tabNum = "";
		
		ca.setNum(0);//赋初始值doi字段查询
		ca.setExcelNum(taskDetail.getExcelNum());
		ca.setPublishType(task.getLibType());
		ca.setCreateTime(taskDetail.getCreateTime().getTime()+"");
		ca.setCreator(task.getCreateId());
		boolean successData = true;// 数据验证是否通过
		
		//导入旧资源处理逻辑
		maps = changeDataMap(maps);
		
		for (String ma : sets) {
			String values=maps.get(ma);
			if(StringUtils.isNotBlank(ma)){
				ma = ma.trim();
			}
			if(StringUtils.isNotBlank(values)){
				values = values.trim();
			}
			if("filePath".equals(ma)){
				filePath = 	values;
				continue;
			}
			
			if("title".equals(ma)){
				title = values;
			}
			try {
				ca = ResUtils.doCheckAndSetValue(ca, ma, values, metadataDefinitions,task.getFiletype());
				logger.info("【BatchImportResService】 资源导入validateMetadata->>>验证  字段:"+ma+" 字段值："+values);
			} catch (Exception e) {
				if(StringUtils.isNotBlank(tabNum)){
					tabNum += e.getMessage();
				}else{
					tabNum += e.getMessage();
				}
			}
		}
		if(!"".equals(tabNum)){
			taskDetail.setImportStatus(tabNum);
			taskDetail.setRemark("资源名：<"+title+"> 第【" + (taskDetail.getExcelNum()) + "】行文件导入失败，失败原因："+tabNum);
			taskDetail.setStatus(BatchImportDetaillType.STATUS4);
			update(taskDetail);
			//resultLog=tabNum;
			resultLog.put(taskDetail.getExcelNum(),tabNum);
			successData = false;
		}
		if(successData){
			try {
				if (task.getFiletype()==1) {
					// 赋值文件路径
					String src = "";
					List<SysParameter> sr = sysParameterService.findParaValue("bachImportExcelPath");
					if(sr!=null && sr.size()>0){
						if(sr.get(0)!=null && sr.get(0).getParaValue()!=null){
							src = sr.get(0).getParaValue();
						}
					}
					
					src = src.replaceAll("\\\\", "/");
					if(!src.endsWith("/")){
						src = src+"/";
					}
					logger.info("【BatchImportResService】 资源导入validateMetadata->>>Excel方式批量导入-文件存放地址"+src);
					if(StringUtils.isNotBlank(filePath)){
						src = src+"/"+filePath+"/";
					}else {
						src = src+"/"+title+"/";
					}
					
					if(!new File(src).exists()){
						taskDetail.setRemark(taskDetail.getRemark()+",资源名：<"+title+"> 行资源文件不存在，路径【" + src + "】");
						taskDetail.setStatus(2);
						logger.info("第【" + (taskDetail.getExcelNum()) + "】，资源文件不存在，路径【" + src + "】");
						update(taskDetail);
						taskDetail.setStatus(BatchImportDetaillType.STATUS4);
						update(taskDetail);
						resultLog.put(taskDetail.getExcelNum(),tabNum);
						src = "";
					}else if (new File(src.substring(0,src.length()-1)).exists()) {
						src = src.substring(0,src.length()-1);
					}
					// 判断是否存在文件
					ca.setUploadFile(src);
					needSaves.add(ca);
					logger.info("【BatchImportResService】 资源导入validateMetadata->>>Excel方式批量导入-该资源路径："+src);
				}else if(task.getFiletype()==2){
					String path = taskDetail.getPaths();
					if(!new File(path).exists()){
						taskDetail.setRemark(taskDetail.getRemark()+",资源名：<"+title+"> xml导入资源文件不存在，路径【" + path + "】");
						taskDetail.setStatus(2);
						logger.info("资源名：<"+title+"> xml导入资源文件不存在，路径【" + path + "】");
						update(taskDetail);
						taskDetail.setStatus(BatchImportDetaillType.STATUS4);
						update(taskDetail);
						resultLog.put(taskDetail.getExcelNum(),tabNum);
						path = "";
					}
					// 判断是否存在文件
					ca.setUploadFile(path);
					//处理批次编号信息 从主表的ExcelPath字段中截取
					String batchPath = task.getExcelPath().replaceAll("\\\\", "/").replaceAll("//", "/");;
					String batchNum = batchPath.substring(batchPath.lastIndexOf("/")+1);
					ca.getMetadataMap().put("batchNum", batchNum);
					
					needSaves.add(ca);
					logger.info("【BatchImportResService】 资源导入validateMetadata->>>xml方式批量导入-该资源路径："+path);
				}
				
			} catch (Exception e) {
				taskDetail.setRemark(taskDetail.getRemark()+e.getMessage());
				logger.info(e.getMessage());
				resultLog.put(taskDetail.getExcelNum(),tabNum);
				update(taskDetail);
			}
			
		}
		
	} 
	
	/**
	 * 
	* @Title: doPublishTask
	* @Description: 执行导入任务
	* @param ca 	要保存的该条资源Ca
	* @param task	当前主表的信息（即excel或一批次的xml）
	* @param uploadTaskDetail	当前子表的信息（即excel一行数据或一个xml文件）
	* @param resultLog
	* @param checkRepeatMetadate
	* @param fileNotExistLog
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String doPublishTask(Ca ca, UploadTask uploadTask,UploadTaskDetail uploadTaskDetail,
			Map<Integer,String> resultLog,Map<String,String> checkRepeatMetadate,Map<Integer,String> fileNotExistLog) {
		
		String repeatType = uploadTask.getRepeatType();
		uploadTaskDetail.setCreateTime(new Date());
		ca.setCreator(uploadTask.getCreateId());
		
		String value = "1";
		int excelNum = ca.getExcelNum();
		logger.info("【BatchImportResService】 资源导入doPublishTask->>>方法开始");
		
		String versionVal = "";
		String versionKey = "";
		String defaultValue = "";
		String emptyZhField = "";
		String title = ca.getMetadataMap().get("title");
		
		Map<String,MetadataDefinition> metadataDefinitionMap =  MetadataSupport.getAllMetadataDefinition(ca.getPublishType());
		Iterator it = ca.getMetadataMap().entrySet().iterator();
		List<String> emptyCheckFieldList = new ArrayList<String>();
		
		//查重字段
		checkRepeatMetadate.clear();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			MetadataDefinition metadataDefinition = metadataDefinitionMap.get(pairs.getKey());
			if(metadataDefinition!=null){
				if(pairs.getKey()!=null && metadataDefinition.getDuplicateCheck() !=null && "true".equals(metadataDefinition.getDuplicateCheck())){
					if(pairs.getKey()!=null && !pairs.getValue().equals("") || !"".equals(pairs.getKey())){
						String values = pairs.getValue().toString().trim();
						values = ResUtils.solrCodeModify(values);//solr通配符容错处理方法
						checkRepeatMetadate.put(pairs.getKey().toString().trim(), values);
					}else{
						emptyCheckFieldList.add(pairs.getKey().toString());
					}
				}
			}
			if(metadataDefinition!=null && metadataDefinition.getIdentifier()==11 && pairs.getKey()!=null){
					versionKey = pairs.getKey().toString();
					if(StringUtils.isNotBlank(metadataDefinition.getDefaultValue())){
					  defaultValue = metadataDefinition.getDefaultValue();
					}else{
					  defaultValue = "00";
					}
			}
		}
		logger.info("【BatchImportResService】 资源导入doPublishTask->>>获取查重字段"+checkRepeatMetadate.toString()+"-->获取DOI"+defaultValue);
		
		/*if(StringUtils.isBlank(versionKey)){
			uploadTaskDetail.setTask(uploadTask);
			uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);//做失败论
			//detailInfo.setExcelNum(excelNum);
			uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + (excelNum) + "】行版本为空不能录入，查看是否资源模板导入错误，已忽略");
			uploadTaskDetail.setImportStatus("版本号为空不能录入已忽略");
			update(uploadTaskDetail);
			logger.info("【BatchImportResService】 资源导入doPublishTask->>>版本号为空 资源名：<"+title+">   第【" + (excelNum) + "】行版本为空不能录入，查看是否资源模板导入错误，已忽略");
			return "0";
		}else */if(checkRepeatMetadate.isEmpty() && !emptyCheckFieldList.isEmpty() && emptyCheckFieldList.size()>0){
			for(int i=0,len =emptyCheckFieldList.size();i<len;i++){
				emptyZhField = emptyZhField +MetadataSupport.getMetadataDefinitionByName(emptyCheckFieldList.get(i))+",";
			}
			if(emptyZhField.endsWith(",")){
				emptyZhField.substring(0,emptyZhField.length()-1);
			}
			uploadTaskDetail.setTask(uploadTask);
			uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);//做失败论
			uploadTaskDetail.setExcelNum(excelNum);
			uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + (excelNum) + "】查重字段为空，已忽略");
			uploadTaskDetail.setImportStatus("查重字段为空不能录入已忽略");
			update(uploadTaskDetail);
			logger.info("【BatchImportResService】 资源导入doPublishTask->>>查重字段为空不能录入资源名：<"+title+">  第【" + (excelNum) + "】查重字段为空，已忽略");
			return "0";
		}
		
		String paraJson = ResUtils.returnJson(checkRepeatMetadate);
		uploadTaskDetail.setCheckRepeatField(paraJson);
		if(ca.getStatus()==null){
			uploadTask.setStatus(ImportStatus.STATUS2);
		}else{
			uploadTask.setStatus(Integer.parseInt(ca.getStatus()));
		}
		uploadTaskDetail.setTask(uploadTask);
		update(uploadTaskDetail);
		

		
		
		try {
			boolean isCover = false;
			try {
				if(ca.getMetadataMap().get("objectId")==null||"".equals(ca.getMetadataMap().get("objectId"))){
					logger.info("【BatchImportResService】 资源导入doPublishTask->>>进入查重策略处理区域  查重策略为：" + repeatType +"<1、新版本 2、元数据增量（文件增量）3、忽略4、元数据覆盖（文件增量）>");
					// 出版图书批量导入
					if (StringUtils.equalsIgnoreCase(repeatType, "2")) {
						logger.info("【BatchImportResService】 资源导入doPublishTask->>>查重策略  查重策略为：2 元数据增量(文件增量)");
						CaList repeatCaWithResVersions = ResUtils.checkRepeat(checkRepeatMetadate, ca.getPublishType(),versionVal);
						if (repeatCaWithResVersions != null && repeatCaWithResVersions.getTotle() == 1) {
							Ca caTemp = repeatCaWithResVersions.getCas().get(0);
							ca.setObjectId(caTemp.getObjectId());
							ca.setResVersion(caTemp.getMetadataMap().get(versionKey));
							isCover = true;
							logger.info("【BatchImportResService】 资源导入doPublishTask->>>覆盖isCover："+isCover+" 新版本或忽略查重 查重策略为：2 元数据增量(文件增量)");
						}
					}
					if (!isCover) {
						CaList repeatCas = ResUtils.checkRepeat(checkRepeatMetadate, ca.getPublishType(),"");
						logger.info("【BatchImportResService】 资源导入doPublishTask->>>isCover FALSE 根据checkRepeatMetadate:"+checkRepeatMetadate+" 查重查出的重复资源个数"+repeatCas.getTotle());
						if (repeatCas != null && repeatCas.getTotle() > 0) {
							if (StringUtils.equalsIgnoreCase(repeatType, "2")||StringUtils.equalsIgnoreCase(repeatType, "4")) {
								// 取重复资源第一个覆盖
								if (repeatCas.getTotle() == 1) {
									Ca caTemp = repeatCas.getCas().get(0);
									ca.setObjectId(caTemp.getObjectId());
									ca.setResVersion(caTemp.getMetadataMap().get(versionKey));
								} else {
									logger.info("【BatchImportResService】 资源导入doPublishTask->>>覆盖 第【" + (excelNum) + "】行重复，已忽略--------------------");
									uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + (excelNum) + "】行重复，已忽略");
									uploadTaskDetail.setImportStatus("重复已忽略");
									uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS5);//重复
									update(uploadTaskDetail);
									resultLog.put(excelNum, "资源重复，已忽略");
									return "0";
								}
							} else if (StringUtils.equalsIgnoreCase(repeatType, "3")) {
								logger.info("【BatchImportResService】 资源导入doPublishTask->>>repeatType3  第【" + (excelNum) + "】行重复，已忽略--------------------");
								// 忽略
								uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + (excelNum) + "】行重复，已忽略");
								uploadTaskDetail.setImportStatus("重复已忽略");
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS5);//重复
								update(uploadTaskDetail);
								resultLog.put(excelNum, "资源重复，已忽略");
								return "0";
							} else if (StringUtils.equalsIgnoreCase(repeatType, "1")) {
								// 创建新版本
								logger.info("【BatchImportResService】 资源导入doPublishTask->>>repeatType1 新版本");
								if (repeatCas!=null&&repeatCas.getTotle() == 1) {
									Ca caTemp = repeatCas.getCas().get(0);
									String resVersion = caTemp.getMetadataMap().get(versionKey);
									int versionValue= Integer.parseInt(resVersion);
									versionValue = versionValue+1;
									if (resVersion != null && !"".equals(resVersion)&&versionValue<10) {
										ca.putMetadataMap(versionKey, "0"+versionValue);
									}else{
										ca.putMetadataMap(versionKey, versionValue+"");
									}
								} else {
									logger.info("【BatchImportResService】 资源导入doPublishTask->>>repeatType1 新版本版本有多个 在最大的版本号加以");
									Ca caTemp = null;
									String resVersion = "";
									int versionValue= 0;
									if(repeatCas!=null&&repeatCas.getCas()!=null){
										caTemp = getMaxVersionCa(repeatCas.getCas(),versionKey);
										resVersion = caTemp.getMetadataMap().get(versionKey);
										if(StringUtils.isNotBlank(resVersion)){
											versionValue= Integer.parseInt(resVersion);
											versionValue = versionValue+1;
										}
										if (resVersion != null
												&& !"".equals(resVersion)&&versionValue<10) {
											ca.putMetadataMap(versionKey, "0"+versionValue);
										}else{
											ca.putMetadataMap(versionKey, versionValue+"");
										}
									}
								}
							}
						} else {
							logger.info("【BatchImportResService】 资源导入doPublishTask->>>新建资源");
							ca.putMetadataMap(versionKey, defaultValue);
							repeatType = "1";
						}
					}
				}else {
					Gson gson = new Gson();
					HttpClientUtil http = new HttpClientUtil();
					
					String objectId = ca.getMetadataMap().get("objectId");//Excel 中的objectId
					
//					String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + thisObjectId);
//					Ca returnCa = gson.fromJson(resourceDetail, Ca.class);//保存完后返回的Ca
					
					Map<String, String> maps = ca.getMetadataMap();
					logger.info("【BatchImportResService】 资源导入doPublishTask->>> 更新字段"+ca.getMetadataMap().toString());
					Set<String> sets = maps.keySet();
					for (String key : sets) {
						value = maps.get(key);
						
						if(StringUtils.isNotBlank(key)){
							key = key.trim();
						}
						
						if(StringUtils.isNotBlank(value)){
							value = value.trim();
						}
						
						if("filePath".equals(key)){
							continue;
						}
						if("objectId".equals(key)){
							continue;
						}
						if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
							//更新字段
							String returnMsg=  http.executeGet(PUBLISH_UPDATE_META +
									 "?resourceId="+objectId+"&key="+key+"&value="+URLEncoder.encode(value));
						}
						
					}
					
//					//设置修改时间
//					Date now = new Date();
//					String time = now.getTime()+"";
//					returnCa.setUpdateTime(time);
//					String json = gson.toJson(returnCa);
//					String result = "";
////					if ("2".equals(repeatType)||"4".equals(repeatType)) { // 覆盖资源
//						result = http.postJson(PUBLISH_OVERRIDE_URL, json);
////					}
					
					uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + excelNum + "】行导入成功！");
					logger.info("【BatchImportResService】 资源导入doPublishTask->>>导入完毕 "+"资源名：<"+title+"> 第【" + excelNum + "】行导入成功！");
					uploadTaskDetail.setImportStatus("导入成功！");
					resultLog.put(excelNum, "导入成功！");
					uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS3);
					update(uploadTaskDetail);
					return "1";
				}
				
				
				
				String objectId = "";
				String uuid = uploadTask.getUuid();
				if(StringUtils.isBlank(ca.getUploadFile())){
					Date now = new Date();
					String time = now.getTime()+"";
					ca.setCreator(uploadTask.getType()+"");
					ca.setCreateTime(time);
					ca.setUpdater(uploadTask.getType()+"");
					ca.setUpdateTime(time);
					Map<String, Map<String,String>> fileMetadataFlag = null;
					try {
						objectId = saveImportPublishRes(ca,repeatType,fileMetadataFlag,uploadTask,uploadTaskDetail);
					} catch (Exception e) {
						e.printStackTrace();
						String errorMsg = e.getMessage();
						uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + excelNum + "】行导入失败！失败原因:"+ errorMsg);
						logger.info("【BatchImportResService】 资源导入doPublishTask->>>导 入  "+"资源名：<"+title+"> 第【" + excelNum + "】行导入失败！");
						uploadTaskDetail.setImportStatus("导入失败！");
						resultLog.put(excelNum, "导入失败！");
						uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
						update(uploadTaskDetail);
						return "0";
					}
					uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + excelNum + "】行导入成功！");
					logger.info("【BatchImportResService】 资源导入doPublishTask->>>导入完毕 "+"资源名：<"+title+"> 第【" + excelNum + "】行导入成功！");
					uploadTaskDetail.setImportStatus("导入成功！");
					resultLog.put(excelNum, "导入成功！");
					uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS3);
					update(uploadTaskDetail);
					
				}else{
					File files = new File(ca.getUploadFile());
					if (files.isDirectory()) {
						File[] children = files.listFiles();
						if(children!=null&&children.length>0){
							Date now = new Date();
							String time = now.getTime()+"";
							ca.setCreator(uploadTask.getType()+"");
							ca.setCreateTime(time);
							ca.setUpdater(uploadTask.getType()+"");
							ca.setUpdateTime(time);
							Map<String, Map<String,String>> fileMetadataFlag = null;
							try {
								objectId = saveImportPublishRes(ca,repeatType,fileMetadataFlag,uploadTask,uploadTaskDetail);
							} catch (Exception e) {
								e.printStackTrace();
								String errorMsg = e.getMessage();
								uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + excelNum + "】行导入失败！失败原因:"+ errorMsg);
								logger.info("【BatchImportResService】 资源导入doPublishTask->>>导入  "+"资源名：<"+title+"> 第【" + excelNum + "】行导入失败！");
								uploadTaskDetail.setImportStatus("导入失败！");
								resultLog.put(excelNum, "导入失败！");
								uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
								update(uploadTaskDetail);
								return "0";
							}
							
							uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + excelNum + "】行导入成功！");
							logger.info("【BatchImportResService】 资源导入doPublishTask->>>导入完毕 "+"第【" + excelNum + "】行导入成功！");
							uploadTaskDetail.setImportStatus("导入成功！");
							resultLog.put(excelNum, "导入成功！");
							if(resultLog.size()>0){
		//						String fileRoot = taskData.getFileDir();
								/**
								 * TODO	这里的失败日志文件地址 待确定
								 */
								String excelDir1 = FILE_ROOT+"failExcelDir"+File.separator+uuid+File.separator;
								String tempExcelPath = excelDir1+"失败日志(不包含文件不存在).xls";
								tempExcelPath = tempExcelPath.replaceAll("\\\\", "/");
								uploadTask.setFailExcelPath(tempExcelPath);
							}
							uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS3);//成功
							update(uploadTaskDetail);
						}else{
							/**
							 * TODO	逻辑待确认
							 */
							fileNotExistLog.put(excelNum, "文件目录不存在"+files.getPath());
							uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + (excelNum) + "】行失败，失败原因:"+ "文件列表为空");
							if(fileNotExistLog.size()>0){
								//String fileRoot = taskData.getFileDir();
								String excelDir1 = FILE_ROOT+"failExcelDir"+File.separator+uuid+File.separator;
								String fileNotExistTemp = excelDir1+"文件不存在日志.xls";
								fileNotExistTemp = fileNotExistTemp.replaceAll("\\\\", "/");
								uploadTask.setFileNotExistPath(fileNotExistTemp);
							}
							uploadTaskDetail.setImportStatus("文件列表为空");
							uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);//失败
							update(uploadTaskDetail);
							return "0";
						}
						
					}else if (files.isFile()){//上传的是单个文件
						Date now = new Date();
						String time = now.getTime()+"";
						ca.setCreator(uploadTask.getType()+"");
						ca.setCreateTime(time);
						ca.setUpdater(uploadTask.getType()+"");
						ca.setUpdateTime(time);
						Map<String, Map<String,String>> fileMetadataFlag = null;
						try {
							objectId = saveImportPublishRes(ca,repeatType,fileMetadataFlag,uploadTask,uploadTaskDetail);
						} catch (Exception e) {
							e.printStackTrace();
							String errorMsg = e.getMessage();
							uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + excelNum + "】行导入失败！失败原因:"+ errorMsg);
							logger.info("【BatchImportResService】 资源导入doPublishTask->>>导入  "+"资源名：<"+title+"> 第【" + excelNum + "】行导入失败！");
							uploadTaskDetail.setImportStatus("导入失败！");
							resultLog.put(excelNum, "导入失败！");
							uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);
							update(uploadTaskDetail);
							return "0";
						}
						
						uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + excelNum + "】行导入成功！");
						logger.info("【BatchImportResService】 资源导入doPublishTask->>>导入完毕 "+"第【" + excelNum + "】行导入成功！");
						uploadTaskDetail.setImportStatus("导入成功！");
						resultLog.put(excelNum, "导入成功！");
						if(resultLog.size()>0){
	//						String fileRoot = taskData.getFileDir();
							/**
							 * TODO	这里的失败日志文件地址 待确定
							 */
							String excelDir1 = FILE_ROOT+"failExcelDir"+File.separator+uuid+File.separator;
							String tempExcelPath = excelDir1+"失败日志(不包含文件不存在).xls";
							tempExcelPath = tempExcelPath.replaceAll("\\\\", "/");
							uploadTask.setFailExcelPath(tempExcelPath);
						}
						uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS3);//成功
						update(uploadTaskDetail);
					}
					
				}
			} catch (Exception e) {
				uploadTaskDetail.setRemark("资源名：<"+title+"> 第【" + (excelNum) + "】行失败，失败原因:" + e.getMessage());
				uploadTaskDetail.setImportStatus("资源名：<"+title+"> 第【" + (excelNum) + "】行失败，失败原因:" + e.getMessage());
				uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);//失败
				update(uploadTaskDetail);
				resultLog.put(excelNum, e.getMessage());
				return "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			uploadTaskDetail.setRemark(uploadTaskDetail.getRemark()+e.getMessage());
			uploadTaskDetail.setImportStatus(e.getMessage());
			uploadTaskDetail.setStatus(BatchImportDetaillType.STATUS4);//失败
			update(uploadTaskDetail);
			resultLog.put(excelNum, e.getMessage());
			return "0";
		}
		return "1";
	}
	
	/**
	 * 
	* @Title: saveImportPublishRes
	* @Description: 创建批量导入出版图书
	* @param ca	要保存的Ca
	* @param repeatType	重复策略
	* @param fileMetadataFlag	查重字段
	* @return
	* @throws Exception    参数
	* @return String    返回类型
	* @throws
	 */
	public String saveImportPublishRes(Ca ca,String repeatType,Map<String, Map<String,String>> fileMetadataFlag,UploadTask uploadTask,UploadTaskDetail uploadTaskDetail){
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
		String title = ca.getMetadataMap().get("title");//资源名
		//String importCoverType="1";//导入封面类型 1：在cover目录下有cover.jpg 2:有cover目录，没有cover.jpg 3：没有cover目录，在根目录有cover.jpg 4：没有cover目录，也没有cover.jpg
		logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>方法开始");
		// 获得DOI字段
		List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(ca.getPublishType());
		logger.info("metadataDefinitions");
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 5) {
				doiField = metadataDefinition.getFieldName();
			}
			logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>根据标识符5获取DOI："+doiField);
			if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 10) {
				targetField = metadataDefinition.getFieldName();
			}
			logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>根据标识符10获取标签："+targetField);
			if (metadataDefinition.getIdentifier() != null && metadataDefinition.getIdentifier() == 11) {
				versionField = metadataDefinition.getFieldName();
			}
			logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>根据标识符11获取版本："+versionField);
		}
		
		String resVersion = ca.getMetadataMap().get(versionField);
		if (resVersion == null || "".equals(resVersion)) {
			ca.putMetadataMap(versionField, "00");
		}
		
		boolean flag = false;
		Set<String> set = null;
		Map<String, String> dirMap = null;
		Map<String, List<String>> md5Map = null;
		
		logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>根据查重策略，进入不同的导入逻辑");
		
		
/**
 * 处理逻辑
 * 	if(objectId!=""){//该资源不是新资源
 * `	if("2".equals(repeatType)){
 * 			
 * 		}else if("5".equals(repeatType)){
 * 
 * 		}
 * `}esle{
 * ``//该资源为新资源
 * `}
 */
		
		if (ca.getObjectId() != null && !"".equals(ca.getMetadataMap().get("objectId"))) {//该资源不是新资源 才会考虑重复策略
			
			if ("2".equals(repeatType)) {
				logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 2repeatType 元数据增量");
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+ "?id=" + ca.getObjectId());
				Gson gson = new Gson();
				Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
				if(oldCa.getMetadataMap().get(targetField)!=null){
					agoTargets = oldCa.getMetadataMap().get(targetField);
				}
				Map<String, String> metadataMap = ca.getMetadataMap();
				Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
				for(Map.Entry<String, String> newEntry : metadataMap.entrySet()){
					String newKey = newEntry.getKey();
					String newValue = newEntry.getValue();
					for (Map.Entry<String, String> oldEntry : oldMetadataMap.entrySet()) {
						String oldKey = oldEntry.getKey();
						String oldValue = oldEntry.getValue();
						//当原Ca该字段没值，而新Ca该字段有值时，将值放入原Ca中，完成增加的目的
						if (newKey.equals(oldKey) && StringUtils.isBlank(oldValue) && StringUtils.isNotBlank(newValue)) {
							oldMetadataMap.put(newEntry.getKey(), newValue);
							logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 2repeatType 元数据增量 字段增量：key "+newEntry.getKey()+" value "+newValue);
							break;
						}
					}
				}
				oldCa.setMetadataMap(oldMetadataMap);
//				oldCa.setMetadataMap(oldCa.getMetadataMap());
				oldDoi = oldCa.getMetadataMap().get(doiField);
				if(StringUtils.isNotBlank(oldDoi)){
					String doiLastNum = oldDoi.substring(oldDoi.length()-8);//保留原来的DOI的流水号
					try {
						doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException("生成ＤＯＩ出错 ！");
					}
					if(StringUtils.isNotBlank(doi)){
						String newDoi = doi.substring(0,doi.length()-8);//获取新的DIO的除了流水号部分
						newDoi = newDoi+doiLastNum;//拼成新的DOI
						logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 2repeatType 元数据增量 新的DOI "+newDoi);
						oldCa.getMetadataMap().put(doiField, newDoi);
					}else{
						oldCa.getMetadataMap().put(doiField, doi);
					}
					
				}
				
				String parentPath = FILE_ROOT + oldCa.getRootPath();
				if(oldCa.getRealFiles()!=null){
					List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa.getRealFiles();
	/**
	 * 文件的格式 
	 * 
	 * 展示格式如下-------
	 * 
	 * 	这是测试数据
	 * 		|-other
	 * 			|-Hibernate中文参考文档 V3.2.chm
	 * 
	 * 存储格式如下-------
	 * 
	 * id：					11/															11/22/								11/22/33.chm
	 * name:  			    这是测试数据														other							Hibernate中文参考文档 V3.2.chm
	 * path:	 1/G2/a0a5bd47-0dbd-4da7-8309-17f313588489/11/	1/G2/a0a5bd47-0dbd-4da7-8309-17f313588489/11/22/ 1/G2/a0a5bd47-0dbd-4da7-8309-17f313588489/11/22/33.chm
	 * objectId: urn:file-75ccf4bd-0bbf-4605-ba4a-27fe63a730a6  urn:file-e774bf89-a692-42e8-9714-c08c8aef1267	 urn:file-06f577e7-f485-4055-8caf-007c319d286f
	 * isDir:				1															 1										2
	 * MD5:																											721235c3b62206ef77eda33f54b966c9
	 *  
	 */
					logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 2repeatType 元数据增量 oldFiles件大小 "+oldFiles.size());
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
							dirMap.put(id, oldFile.getName());
							logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 2repeatType 元数据增量 dirMap 文件夹id"+id+" 文件夹名 "+oldFile.getName());
						}
					}
	/**
	 * dirMap中存放的数据如下
	 * 	 key 	 	  value	
	 * 	 11		            这是测试数据
	 *  11/22    	   other
	 * 11/22/33.chm Hibernate中文参考文档 V3.2.chm		//只有isDir=1的文件夹数据  该条没有
	 */
					
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
	/**
	 * set中存放的数据如下
	 *  这是测试数据
	 *  这是测试数据/other
	 *  这是测试数据/other/Hibernate中文参考文档 V3.2.chm	//只有isDir=1的文件夹数据  该条没有
	 *  
	 * 	dirMap中数据更新如下
	 * 	 key 	 	  value	
	 * 	 11		               这是测试数据
	 *  11/22    	   这是测试数据/other
	 * 11/22/33.chm  这是测试数据/other/Hibernate中文参考文档 V3.2.chm	//只有isDir=1的文件夹数据  该条没有
	 */
					
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
									tempPid = tempPid.substring(0, tempPid.length() - 1);
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
				
				if(oldCa.getRealFiles()==null){
					oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
					try {
						String fileDoi = "";
						if(oldCa.getMetadataMap()!=null && oldCa.getMetadataMap().get(doiField)!=null){
							fileDoi = oldCa.getMetadataMap().get(doiField);
						}
						//ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa, i, j,num,fileDoi,false);
						ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa,fileDoi,false);
					} catch (IOException e) {
						e.printStackTrace();
						throw new ServiceException("获取资源文件信息错误 ！");
					}
				}else{
					oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
					int num = 0;
					String fileDoi = "";
					if(oldCa.getMetadataMap()!=null && oldCa.getMetadataMap().get(doiField)!=null){
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid, oldCa, i, set,md5Map, dirMap,srcPath,num,fileDoi);
					try {
						oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid, oldCa, set,md5Map,srcPath,fileDoi);
					} catch (IOException e) {
						e.printStackTrace();
						throw new ServiceException("获取文件信息出错！");
					}
				}
				logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 2repeatType 文件处理结束 资源路径"+parentPath);
				ca = oldCa;
				// 元数据覆盖
			} else if ("4".equals(repeatType)) {
				logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 4repeatType 元数据覆盖");
				HttpClientUtil http = new HttpClientUtil();
				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+ "?id="+ ca.getObjectId());
				Gson gson = new Gson();
				Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
				if(oldCa.getMetadataMap().get(targetField)!=null){
					agoTargets = oldCa.getMetadataMap().get(targetField);
				}
				String parentPath = oldCa.getRootPath();
				parentPath = FILE_ROOT +parentPath;
				Map<String, String> newMetadataMap = ca.getMetadataMap();
				Map<String, String> oldMetadataMap = oldCa.getMetadataMap();
				for(Map.Entry<String, String> oldEntry : oldMetadataMap.entrySet()){
					String oldKey = oldEntry.getKey();
					if(newMetadataMap.get(oldKey)==null){
						if(oldKey.equals(doiField) && oldMetadataMap.get(oldKey)!=null){
							newMetadataMap.put(oldKey, oldMetadataMap.get(oldKey));
						}else{
							newMetadataMap.put(oldKey, "");
						}
					}
				}
				oldCa.setMetadataMap(newMetadataMap);
				oldDoi = oldCa.getMetadataMap().get(doiField);
				if(StringUtils.isNotBlank(oldDoi)){
					String doiLastNum = oldDoi.substring(oldDoi.length()-8);
					try {
						doi = DoiGenerateUtil.generateDoiByResDirectory(oldCa);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException("生成DOI错误！");
					}
					if(StringUtils.isNotBlank(doi)){
						String newDoi = doi.substring(0,doi.length()-8);
						newDoi = newDoi+doiLastNum;
						oldCa.getMetadataMap().put(doiField, newDoi);
					}else{
						oldCa.getMetadataMap().put(doiField, doi);
					}
				}else{
					if(StringUtils.isNotBlank(doi)){
						oldCa.getMetadataMap().put(doiField, doi);
					}
				}
				

				if(oldCa.getRealFiles()!=null){
					List<com.brainsoon.semantic.ontology.model.File> oldFiles = oldCa.getRealFiles();
					logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 2repeatType 元数据覆盖 oldFiles件大小 "+oldFiles.size());
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
									tempPid = tempPid
											.substring(0, tempPid.length() - 1);
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
				
				if(oldCa.getRealFiles()==null){
					oldCa.setRootPath(parentPath.replace(FILE_ROOT, ""));
					try {
						int num = 0;
						String fileDoi = "";
						if(oldCa.getMetadataMap()!=null && oldCa.getMetadataMap().get(doiField)!=null){
							fileDoi = oldCa.getMetadataMap().get(doiField);
						}
						//ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa, i, j,num,fileDoi,false);
						ca = ResUtils.getFileLists(parentPath, srcFile, pid, oldCa,fileDoi,false);
					} catch (IOException e) {
						e.printStackTrace();
						throw new ServiceException("获取资源文件信息错误 ！");
					}
				}else{
					int num = 0;
					String fileDoi = "";
					if(oldCa.getMetadataMap()!=null && oldCa.getMetadataMap().get(doiField)!=null){
						fileDoi = oldCa.getMetadataMap().get(doiField);
					}
					//oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid, oldCa, i, set,md5Map, dirMap,srcPath,num,fileDoi);
					try {
						oldCa = ResUtils.getOverFileLists(parentPath, srcFile, pid, oldCa, set,md5Map,srcPath,fileDoi);
					} catch (IOException e) {
						e.printStackTrace();
						throw new ServiceException("获取资源文件信息错误 ！");
					}
				}
				
				logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 4repeatType 文件处理结束 资源路径"+parentPath);
				ca = oldCa;
			} 
			
		}else {//新资源
			logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>导入逻辑 新建资源 ");
			try {
				doi = DoiGenerateUtil.generateDoiByResDirectory(ca);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("生成DOI错误 ！");
			}
			
			String parentPath = "";
			try {
				// 按要求生成目录
				parentPath = resourceService.createPublishParentPath(ca.getPublishType(), doi);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("生成目录错误 ！");
			}
			ca.setRootPath(parentPath.replace(FILE_ROOT, ""));
			
			ca.getMetadataMap().put("cover", "");
			
			if (StringUtils.isNotBlank(srcPath)) {
				if (srcFile.isDirectory()) {
					try {
						//处理封面图片
						ResUtils.modifyCoverImage(srcFile,srcFile,parentPath);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException(e.getMessage());
					}
					
					try {
						//ca = ResUtils.getFileLists(parentPath, srcFile, pid, ca, i, j,num,doi,false);
						ca = ResUtils.getFileLists(parentPath, srcFile, pid, ca,doi,false);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException("获取资源文件信息错误 ！");
					}
				}else {
					try {
						ca = ResUtils.getFileList(parentPath, srcFile, ca);
					} catch (Exception e) {
						e.printStackTrace();
						throw new ServiceException("获取资源文件信息错误 ！");
					}
				}
			}
			
			
			if (StringUtils.isNotBlank(doiField)&&StringUtils.isNotBlank(doi)) {
				ca.getMetadataMap().put(doiField, doi);
			}
			ca.setStatus(ResourceStatus.STATUS0);
		}
		Date date = new Date();
		ca.setUpdateTime(date.getTime() + "");
		if(ca.getRealFiles()!=null){
			logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>输出最终文件大小  "+ca.getRealFiles().size());
		}
		
		String objectId = "";
		String result = "";
		Gson gson = new Gson();
		HttpClientUtil http = new HttpClientUtil();
		try {
			String paraJson = gson.toJson(ca);
			logger.debug("【BatchImportResService】 资源导入saveImportPublishRes->>>输出最终Ca  paraJson： "+paraJson);
			if ("2".equals(repeatType)||flag||"4".equals(repeatType)) { // 覆盖资源
				result = http.postJson(PUBLISH_OVERRIDE_URL, paraJson);
				logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>调接口覆盖返回result： "+result);
			} else {
				result = http.postJson(PUBLISH_SAVE_URL, paraJson);
				logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>调接口进入保存方法返回result： "+result);
			}
			if (result!="" && !"".equals(result)) {
				SemanticResponse rtn = gson.fromJson(result, SemanticResponse.class);
				objectId = rtn.getObjectId();//保存成功返回objectId
			}else {
				throw new ServiceException("保存数据信息错误 ！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("保存数据信息错误 ！");
		}
		
		
		/**
		 * 文件转换逻辑开始
		 */
		logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>文件转换逻辑开始 资源objectId："+objectId);
		if (StringUtils.isNotBlank(objectId)) {
			try {
				//文件转换
				String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL") + "?id=" + objectId);
				Ca returnCa = gson.fromJson(resourceDetail, Ca.class);//保存完后返回的Ca
				List<com.brainsoon.semantic.ontology.model.File> realFiles = returnCa.getRealFiles();
				if (realFiles != null && realFiles.size() > 0) {
					DoFileQueueList doFileList = ResUtils.converPath(realFiles,objectId);
					logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>文件转换逻辑开始 待转换文件："+doFileList.toString());
					if(doFileList !=null && doFileList.getDoFileQueueList().size()>0){
						//result = http.postJson(PUBLISH_FILE_WRITE_QUEUE,gson.toJson(doFileList));
						for (DoFileQueue doFileQueue : doFileList.getDoFileQueueList()) {
							resConverfileTaskService.addQueue(doFileQueue);
						}
					}
					logger.info("【BatchImportResService】 资源导入saveImportPublishRes->>>文件转换逻辑结束 待转换文件："+result);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException("文件转换数据错误！");
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
		sysOperateService.saveHistory(WorkFlowUtils.getExecuId(objectId, "pubresCheck"), "","资源草稿", operateDesc, new Date(), Long.parseLong(ca.getCreator()));
		}
		
		
		
		
		if(StringUtils.isNotBlank(objectId) && fileMetadataFlag!=null && fileMetadataFlag.size()>0){
			String resourceDetail = http.executeGet(WebappConfigUtil.getParameter("PUBLISH_DETAIL_URL")+ "?id="+ objectId);
			Ca oldCa = gson.fromJson(resourceDetail, Ca.class);
			if(oldCa.getRealFiles()!=null && oldCa.getRealFiles().size()>0 && fileMetadataFlag!=null && fileMetadataFlag.size()>0){
				List<com.brainsoon.semantic.ontology.model.File> tempRealFiles = oldCa.getRealFiles();
				for(com.brainsoon.semantic.ontology.model.File file:tempRealFiles){
					if(fileMetadataFlag.get(file.getMd5())!=null&& !fileMetadataFlag.get(file.getMd5()).isEmpty()){
						file.setFileMetadataMap(fileMetadataFlag.get(file.getMd5()));
						String resultFile = http.postJson(CA_FILERES_SAVE_URL,gson.toJson(file));
//						String changeName = http.executeGet(PUBLISH_RENAME_FILE + "?objectId="
//								+ file.getObjectId());
					}else if(fileMetadataFlag.get(file.getObjectId())!=null){
						file.setFileMetadataMap(fileMetadataFlag.get(file.getObjectId()));
						String resultFile = http.postJson(CA_FILERES_SAVE_URL,gson.toJson(file));
					}
				}
			}
		}
		
		if ("1".equals(repeatType)||"3".equals(repeatType)||"".equals(repeatType)) {
			List<String> targetNameList = new LinkedList<String>();
			int resNum = 0;
			String hql = "";
			hql = "select oresNums from RunNumber where publishType='"+ca.getPublishType()+"'";
			targetNameList = (List<String>) baseDao.query(hql);
			if (targetNameList.size() > 0) {
				if (targetNameList.get(0) != null) {
					resNum = Integer.parseInt(targetNameList.get(0)) + 1;
				}
				hql = "UPDATE RunNumber SET oresNums='" + resNum
						+ "' where oresNums='"
						+ Integer.parseInt(targetNameList.get(0)) + "' and publishType='"+ca.getPublishType()+"'";
				baseDao.updateWithHql(hql);
			} else {
				RunNumber runNumber = new RunNumber();
				runNumber.setPublishType(ca.getPublishType());
				runNumber.setOresNums("1");
				baseDao.create(runNumber);
			}
		}
		return objectId;
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
	@Override
	public String saveRes(HttpServletRequest request) {
		String status = "SUCCESS";
		String repeatType = request.getParameter("repeatType");
		String publishType = request.getParameter("publishType");
		String remark = request.getParameter("remark");
		//String processTask = request.getParameter("processTask");
		logger.debug(repeatType);
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		MultipartFile multipartFile = null;
		for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
			multipartFile = set.getValue();// 文件名
		}
		String flag = "";
		String fileName = multipartFile.getOriginalFilename();
		if (multipartFile.isEmpty()) {
			logger.error("文件未上传");
			status = "FALSE";
		} else {
			
			//------------------使用UUID生成文件名称
			flag = UUID.randomUUID().toString();
			String folder = FILE_TEMP + flag;////////////////////
			new File(folder).mkdir();
			File restore = new File(folder + File.separator + fileName);
			try {
				multipartFile.transferTo(restore);
			} catch (Exception e) {
				status = "FALSE";
			}
			
			
			//--------------------处理Excel数据 开始
			boolean excelFlag = true;
			ExcelData data = null;
			try {
				data = dealExcelData(restore);
			} catch(FileNotFoundException e){
				excelFlag = false;
				logger.error("【batchImportResService】-->saveRes()-->找不到Excel文件");
			} catch(IOException e){
				excelFlag = false;
				logger.error("【batchImportResService】-->saveRes()-->获取excel文件异常");
			} catch (Exception e) {
				excelFlag = false;
				logger.error("【batchImportResService】-->saveRes()-->读取excel文件数据异常");
			}
			//--------------------处理Excel数据 结束
			
			//------------------填写主表数据 开始
			UserInfo user = LoginUserUtil.getLoginUser();
			if(!excelFlag){	//解析失败
				UploadTask task = new UploadTask();
				task.setExcelName(fileName);
				task.setName(fileName);
				task.setExcelPath(restore.getAbsolutePath());
				task.setCreateTime(new Date());
				task.setStatus(ImportStatus.STATUS5);//主表  处理失败
				task.setLibType(publishType);//资源类型
				task.setModule("1");//暂时没用
				task.setRemark(remark);
				task.setRepeatType(repeatType);
				task.setType(user.getUserId()+"");
				task.setCreateId(user.getUserId()+"");
				//存1表示是excel导入
				task.setFiletype(1);
				resourceService.create(task);
				UploadTaskDetail taskDetail = new UploadTaskDetail();
				taskDetail.setTask(task);
				taskDetail.setCreateTime(new Date());
				taskDetail.setRemark("解析Excel出错，请检查上传的Excel表格");
				taskDetail.setImportStatus("解析Excel出错，请检查上传的Excel表格");
				create(taskDetail);
			}else{
				UploadTask task = new UploadTask();
				task.setExcelName(fileName);
				task.setName(fileName);
				task.setExcelPath(restore.getAbsolutePath());
				task.setCreateTime(new Date());
				task.setStatus(ImportStatus.STATUS1);//主表待处理
				task.setLibType(publishType);
				task.setModule("1");
				task.setRemark(remark);
				task.setRepeatType(repeatType);
				task.setType(user.getUserId()+"");
				task.setCreateId(user.getUserId()+"");
				//存1表示是excel导入
				task.setFiletype(1);
				create(task);
				String[] datas;
				Long datasLen = 0L;
				ExcelDataDetailMK mk;
				String currentData = "";
				String name = "";
				ExcelDataDetailMK[] markers = data.getMarkers();
				List<ExcelDataRow> rows = data.getRows();
				int maxRow = rows.size();

				//----------------根据每行数据添加子表数据   开始
				for (int i = 0; i < maxRow; i++) {
					UploadTaskDetail taskDetail = new UploadTaskDetail();
					//JSONArray rowArray = new JSONArray();
					JSONObject rowObj = new JSONObject();
					ExcelDataRow row = rows.get(i);
					datas = row.getDatas();
					datasLen = (long) datas.length;
					int rowNum = row.getNum();
					for(int j=0;j<datasLen;j++){
						mk = markers[j];
						name = mk.name;
						currentData = datas[j];
						rowObj.put(name, currentData);
					}
					//rowArray.add(rowObj);
					taskDetail.setBody(rowObj.toString());
					taskDetail.setTask(task);
					taskDetail.setCreateTime(new Date());
					taskDetail.setExcelNum(rowNum);
					taskDetail.setRemark("第【"+rowNum+"】行");
					taskDetail.setStatus(BatchImportDetaillType.STATUS1);//字表待处理
					create(taskDetail);
				}
				//----------------根据每行数据添加子表数据   结束
				
				//new Thread(new ImportResThread()).start();
				ImportResThread importResThread = new ImportResThread();
				importResThread.start();
				
			}
			
			//------------------填写主表数据 结束
		}
		return status;
		
	}
	
	/**
	 * 
	* @Title: dealExcelData
	* @Description: 批量导入-》处理Excel数据
	* @param restore	上传的Excel文件
	* @return
	* @throws FileNotFoundException
	* @throws IOException    参数
	* @return ExcelData    返回类型	处理后的Excel数据
	* @throws
	 */
	public ExcelData dealExcelData(File restore) throws FileNotFoundException, IOException{

		//------------------解析excel数据   开始
		ExcelData data = new ExcelData();
		File excel = new File(restore.getAbsolutePath());
		HSSFWorkbook workbook = null;
			workbook = new HSSFWorkbook(new FileInputStream(excel));
		HSSFSheet sheet = workbook.getSheetAt(0);
		/** 第一步，先读取标记行 */
		ExcelDataDetailMK marker = null;
		HSSFRow markerRow = sheet.getRow(1);
		int lastCell = markerRow.getLastCellNum() - 1;
		HSSFCell tempCell = null;
		String tempValue = "";
//			String[] tempValueArray;
		//将excel中的标记转换为实体
		ExcelDataDetailMK[] markers = new ExcelDataDetailMK[lastCell + 1];
		for (int i = 0; i <= lastCell; i++) {
			tempCell = markerRow.getCell(i);
			tempValue = tempCell.getStringCellValue();
			if(tempValue!=null && !"".equals(tempValue)){
				marker = new ExcelDataDetailMK(tempValue);
				markers[i] = marker;
			}
		}
		data.setMarkers(markers);

//			Map<String,MetadataDefinition> metadataDefinition = MetadataSupport.getAllMetadataDefinition();
		/** 第二步，解析每行数据 */
		int lastRowNum = sheet.getLastRowNum();
		HSSFRow tempRow;
		String[] datas;
		//用于存放dataRow对象的集合
		List<ExcelDataRow> rows = new ArrayList<ExcelDataRow>();
		//excel里的每条数据对象dataRow
		ExcelDataRow dataRow = null;
		for (int i = 2; i <= lastRowNum; i++) {
			tempRow = sheet.getRow(i);
			if (tempRow != null) {
				datas = new String[lastCell + 1];
				dataRow = new ExcelDataRow();
				HSSFCell tempCell0 = tempRow.getCell(0);
				HSSFCell tempCell1 = tempRow.getCell(1);
				for (int j = 0; j <= lastCell; j++) {
					tempCell = tempRow.getCell(j);
					if (null != tempCell) {
						int type = tempCell.getCellType();
						String cellContent = "";
						if(HSSFCell.CELL_TYPE_NUMERIC == type){
							DecimalFormat df = new DecimalFormat("0");
						    cellContent =df.format(tempCell.getNumericCellValue());
						    if(HSSFDateUtil.isCellDateFormatted(tempCell)){
						    	
								Date d = tempCell.getDateCellValue();
								DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
								cellContent = formater.format(d);
							}
						    datas[j] = cellContent.trim();
						}else{
							datas[j] = tempCell.getStringCellValue().trim();
						}
					} else {
						datas[j] = "";
					}
					
				}
					dataRow.setDatas(datas);
					dataRow.setNum(i + 1);
					rows.add(dataRow);
			}
		}
		data.setRows(rows);
		data.setTotalNum(rows.size());
		//------------------解析excel数据   结束
		return data;
	}
	
	/**
	 * 
	* @Title: getXmlJson
	* @Description: 根据批量导入的路径获取下面的批次信息以及批次下面的资源信息
	* @param path	批量导入的路径
	* @return    参数
	* @return String    返回类型	json数据
	* @throws
	 */
	public String getXmlJson(){
		// 赋值文件路径
		String bachImportXmlPath = "";
		List<SysParameter> lists = sysParameterService.findParaValue("bachImportXmlPath");
		if(lists!=null && lists.size()>0){
			if(lists.get(0)!=null && lists.get(0).getParaValue()!=null){
				bachImportXmlPath = lists.get(0).getParaValue();
			}
		}
		
		bachImportXmlPath = bachImportXmlPath.replaceAll("\\\\", "/");
		logger.info("【batchImportResService】-->getXmlJson()-->获取批次资源信息  导入目录：bachImportXmlPath:"+ bachImportXmlPath);
		
		JSONArray array = new JSONArray();
		
		String fileEncode = System.getProperty("file.encoding");
		//File baseFile = new File(bachImportXmlPath);
		File baseFile = null;
		try {
			baseFile = new File(new String(bachImportXmlPath.getBytes("UTF-8"), fileEncode));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (baseFile.exists()) {
			File[] batchFiles = baseFile.listFiles();//批次目录
			if (batchFiles != null && batchFiles.length > 0) {
				for (File batchFile : batchFiles) {
					if (batchFile.isDirectory()) {
						String batchFilePath = batchFile.getAbsolutePath().replaceAll("\\\\", "/");
						String batchFileName = batchFile.getName().replaceAll("\\\\", "/");
						if (batchFileName.indexOf("hidden")!=-1) {
							continue;//不显示包含hidden的批次目录
						}
						String batchOppositePath = batchFilePath.replace(bachImportXmlPath, "");
						JSONObject batchJson = new JSONObject();
						batchJson.put("id", batchOppositePath);
						batchJson.put("pId", "root");
						batchJson.put("name", batchFileName);
						batchJson.put("path", batchFilePath);
						batchJson.put("open", false);
						array.add(batchJson);
						logger.info("【batchImportResService】-->getXmlJson()-->获取批次资源信息  批次目录：batchFilePath:"+ batchFilePath);
						
						File[] resFiles = batchFile.listFiles();//资源目录
						for (File resFile : resFiles) {
							if (resFile.isDirectory()) {
								String resFilePath = resFile.getAbsolutePath().replaceAll("\\\\", "/");
								String resFileName = resFile.getName();
								String resOppositePath = resFilePath.replace(bachImportXmlPath, "");
								JSONObject resJson = new JSONObject();
								resJson.put("id", resOppositePath);
								resJson.put("pId", batchOppositePath);
								resJson.put("name", resFileName);
								//resJson.put("iconSkin", "ico_close");
								resJson.put("path", resFilePath);
								resJson.put("open", true);
								resJson.put("nocheck", true);
								array.add(resJson);
								logger.info("【batchImportResService】-->getXmlJson()-->获取批次资源信息  资源目录：batchFilePath:"+ resFilePath);
							}
						}
					}
				}
			}
		}
		
		JSONObject root = new JSONObject();
		root.put("id", "root");
		root.put("pId", "-1");
		root.put("name", "标准资源导入目录");
		root.put("open", true);
		root.put("path", "");
		array.add(root);
		return array.toString();
	}
	
	/**
	 * 
	* @Title: saveResByXml
	* @Description: 保存批量导入xml方式数据
	* @param paths	批次的路径
	* @param remark	备注信息
	* @param repeatType	重复策略
	* @param publishType 资源类型
	* @return    参数
	* @return String    返回类型
	* @throws
	 */
	public String saveResByXml(String paths, String remark,String repeatType, String publishType){
		String retStatus = "SUCCESS";
		String batchPaths[] = paths.split(",");
		UserInfo userinfo = LoginUserUtil.getLoginUser();
		for(int i=0,len = batchPaths.length;i<len;i++){
			String batchPath=batchPaths[i].replaceAll("\\\\", "/");
			File batchFile = new File(batchPath);
			UploadTask task = new UploadTask();
			if(!batchFile.exists()){
				System.out.println("路径"+batchFile + "不正确");
				logger.error("【batchImportResService】-->saveResByXml()-->保存批量导入xml方式 路径"+ batchFile.getAbsolutePath() + "不正确");
			}else if (batchFile.isDirectory()){
				//处理批次信息（主表）
				task.setName(batchFile.getName());
				task.setCreateTime(new Date());
				task.setExcelPath(batchFile.getAbsolutePath());
				task.setStatus(ImportStatus.STATUS1);//主表待处理
				task.setLibType(publishType);
				task.setType(userinfo.getUserId().toString());
				task.setCreateId(userinfo.getUserId().toString());
				task.setRemark(remark);
				task.setRepeatType(repeatType);
				task.setFiletype(2);//存2表示是xml导入
				logger.error("【batchImportResService】-->saveResByXml()-->保存批量导入xml方式  批次路径"+ batchFile.getAbsolutePath());
				create(task);
				
				File resFiles[]=batchFile.listFiles();
				for(int j=0,length=resFiles.length;j<length;j++){
					File resFile=resFiles[j];
					if(resFile.isDirectory()){
						UploadTaskDetail taskDetail = new UploadTaskDetail();
						//taskDetail.setBody(rowObj.toString());
						taskDetail.setTask(task);
						taskDetail.setPaths(resFile.getAbsolutePath());
						taskDetail.setCreateTime(new Date());
						taskDetail.setStatus(BatchImportDetaillType.STATUS1);//字表待处理
						taskDetail.setRemark("xml:"+resFile.getName()+"");
						logger.error("【batchImportResService】-->saveResByXml()-->保存批量导入xml方式  资源路径"+ resFile.getAbsolutePath());
						create(taskDetail);
					}
				}
			}
		}
		ImportResThread importResThread = new ImportResThread();
		importResThread.start();
		return retStatus;
	}
	
	/**
	 * 
	* @Title: changeDataMap
	* @Description: 专门处理数据兼容
	* @param maps	未处理之前的数据
	* @return    参数
	* @return Map<String,String>   处理之后的数据
	* @throws
	 */
	public static Map<String, String> changeDataMap(Map<String, String> maps){
		if(!maps.isEmpty()){
			Set<String> sets = maps.keySet();
			
			String newValue = "";
			
			//兼容 版本时间和出版时间 如果一个有值一个没值，将有值的赋给没值的
			if (StringUtils.isBlank(maps.get("date")) || StringUtils.isBlank(maps.get("publisherDate")) ) {
				if (StringUtils.isNotBlank(maps.get("date")) && StringUtils.isBlank(maps.get("publisherDate"))) {
					maps.put("publisherDate", maps.get("date"));
				}
				if (StringUtils.isBlank(maps.get("date")) && StringUtils.isNotBlank(maps.get("publisherDate"))) {
					maps.put("date", maps.get("publisherDate"));
				}
			}
			
			for (String ma : sets) {
				String values = maps.get(ma);
				if(StringUtils.isNotBlank(ma)){
					ma = ma.trim();
				}
				if(StringUtils.isNotBlank(values)){
					values = values.trim();
				}
				
				//处理版本的兼容
				if("revnumber".equals(ma)){
					if (values.startsWith("第") && values.endsWith("版")) {
						newValue = values.substring(1,values.length()-1);
						maps.put(ma, newValue);
					}
					continue;
				}
				
				//处理语种的兼容
				if("language".equals(ma)){
					if (values.indexOf("简")!=-1) {
						newValue = "zh-Hans";
						maps.put(ma, newValue);
						continue;
					}
					if (values.indexOf("繁")!=-1) {
						newValue = "zh-Hant";
						maps.put(ma, newValue);
						continue;
					}
					if (values.indexOf("英")!=-1) {
						newValue = "en";
						maps.put(ma, newValue);
						continue;
					}
					if (values.indexOf("日")!=-1) {
						newValue = "ja";
						maps.put(ma, newValue);
						continue;
					}
					if (values.indexOf("朝")!=-1 ||values.indexOf("韩")!=-1) {
						newValue = "zko-KR";
						maps.put(ma, newValue);
						continue;
					}
					if (values.indexOf("俄")!=-1) {
						newValue = "ru";
						maps.put(ma, newValue);
						continue;
					}
					if (values.indexOf("法")!=-1) {
						newValue = "fr";
						maps.put(ma, newValue);
						continue;
					}
					if (values.indexOf("德")!=-1) {
						newValue = "de";
						maps.put(ma, newValue);
						continue;
					}
				}
				
				//处理版本日期的兼容
				if("date".equals(ma)){
					if (values.indexOf("/")!=-1) {
					    String[] data = values.split("/");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}else if (values.indexOf("-")!=-1) {
						String[] data = values.split("-");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
								Date date1 = df1.parse(values);
								newValue = values;
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}else if(values.length()==4){
						try {
							DateFormat df1 = new SimpleDateFormat("yyyy");
							Date date1 = df1.parse(values);
							df1 = new SimpleDateFormat("yyyy-MM-dd");
							newValue = df1.format(date1);
							maps.put(ma, newValue);
//							maps.put(ma, date1.getTime()+"");
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					continue;
				}
				
				//处理版本日期的兼容
				if("publisherDate".equals(ma)){
					if (values.indexOf("/")!=-1) {
						String[] data = values.split("/");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}else if (values.indexOf("-")!=-1) {
						String[] data = values.split("-");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
								Date date1 = df1.parse(values);
								newValue = values;
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					continue;
				}
				//处理授权结束时间兼容
				if("authorEndTime".equals(ma)){
					if (values.indexOf("/")!=-1) {
						String[] data = values.split("/");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}else if (values.indexOf("-")!=-1) {
						String[] data = values.split("-");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
								Date date1 = df1.parse(values);
								newValue = values;
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					continue;
				}
				//处理授权开始时间兼容
				if("authorStartTime".equals(ma)){
					if (values.indexOf("/")!=-1) {
					    String[] data = values.split("/");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("MM/yyyy");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}else if (values.indexOf("-")!=-1) {
						String[] data = values.split("-");
						if (data.length==3) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
								Date date1 = df1.parse(values);
								newValue = values;
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}else if (data.length==2) {
							try {
								DateFormat df1 = new SimpleDateFormat("yyyy-MM");
								Date date1 = df1.parse(values);
								df1 = new SimpleDateFormat("yyyy-MM-dd");
								newValue = df1.format(date1);
								maps.put(ma, newValue);
//								maps.put(ma, date1.getTime()+"");
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
					continue;
				}
				
				//处理64开bug
				if("dimensions".equals(ma)){
					if (values.indexOf("64开")!=-1) {
						newValue = "64mo";
						maps.put(ma, newValue);
					}
					continue;
				}
				
			}
		}
		return maps;
	}
	
	@Override
	public String saveAnalyzeExcel(HttpServletRequest request) {
		String random = request.getParameter("random"); 
		String publishType = request.getParameter("publishType");
		Object fileRandomVal = null;
		String excelPath = "";
		try {
			ItemAnalyzeExcel itemAnalyzeExcel = new ItemAnalyzeExcel();
			while(fileRandomVal == null){
				fileRandomVal = GlobalAppCacheMap.getValue("file_" + random);
				GlobalAppCacheMap.putKey("file_" + random, Integer.parseInt(fileRandomVal==null?"0":fileRandomVal.toString()) + 1);
				MultipartRequest multipartRequest = (MultipartRequest) request;
				Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
				MultipartFile multipartFile = null;
				String flag = "";
				String filePath = "";
				for (Map.Entry<String, MultipartFile> set : fileMap.entrySet()) {
		//			for (int o=0;o<2;o++) {
		//				if(o==0){
		//					filePath = "F:/ZBexcle/素材资源元数据入库表/02Excel.xls";
		//				}else{
		//					filePath = "F:/ZBexcle/素材资源元数据入库表/03Excel.xls";
		//				}
					multipartFile = set.getValue();// 文件名
					AnalyzeExcel analyzeExcel = new AnalyzeExcel();
					String fileName = multipartFile.getOriginalFilename();
					String excelMetaName = "";
					if (multipartFile.isEmpty()) {
						logger.error("文件未上传");
					} else {
						//------------------使用UUID生成文件名称
						flag = "mergerExcel";
						String folder = FILE_TEMP + flag;////////////////////
						new File(folder).mkdir();
						File restore = new File(folder + File.separator + fileName);
		//				File restore = new File(filePath);
						try {
							multipartFile.transferTo(restore);
						} catch (Exception e) {
		//					status = "-1";
						}
						boolean excelFlag = true;
						ExcelData data = null;
						try {
							data = analyzeExcelData(restore);
						} catch(FileNotFoundException e){
							excelFlag = false;
							logger.error("【batchImportResService】-->saveRes()-->找不到Excel文件");
						} catch(IOException e){
							excelFlag = false;
							logger.error("【batchImportResService】-->saveRes()-->获取excel文件异常");
						} catch (Exception e) {
							excelFlag = false;
							logger.error("【batchImportResService】-->saveRes()-->读取excel文件数据异常");
						}
						
						String[] datas;
						Long datasLen = 0L;
						ExcelDataDetailMK mk;
						String currentData = "";
						String name = "";
						ExcelDataDetailMK[] markers = data.getMarkers();
						List<ExcelDataRow> rows = data.getRows();
						int maxRow = rows.size();
						List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
						//----------------根据每行数据添加子表数据   开始
						for (int i = 0; i < maxRow; i++) {
							//根据每个上传的excel生成listMap集合
							//JSONArray rowArray = new JSONArray();
							Map<String,String> rowMap = new HashMap<String, String>();
							ExcelDataRow row = rows.get(i);
							datas = row.getDatas();
							datasLen = (long) datas.length;
							int rowNum = row.getNum();
							//循环每条记录放入rowMap
							for(int j=0;j<datasLen;j++){
								mk = markers[j];
								name = mk.name;
								currentData = datas[j];
								rowMap.put(name, currentData);
								int num = 0;
								if(StringUtils.isBlank(excelMetaName)){
									if(name.equals("所属素材模块")&&num ==0||name.equals("关联图书名称") &&num ==0){
										excelMetaName = name;
										num++;
									}
								}
								if(name.equals("入库资源编号") && StringUtils.isNotBlank(currentData)){
									excelMetaName = "1";
								}
							}
							//全map数据放入list中一行
							listMap.add(rowMap);
							analyzeExcel.setListMap(listMap);
							analyzeExcel.setName(excelMetaName);
						}
						ItemAnalyzeExcel cacheItemAnalyzeExcel = (ItemAnalyzeExcel) GlobalAppCacheMap.getValue("filedata_" + random);
						if(cacheItemAnalyzeExcel != null){
							itemAnalyzeExcel = cacheItemAnalyzeExcel;
						}
						itemAnalyzeExcel.addAnalyzeExcel(analyzeExcel);
						GlobalAppCacheMap.putKey("filedata_" + random, itemAnalyzeExcel);
					}
				}
				fileRandomVal = GlobalAppCacheMap.getValue("file_" + random);
			}
			
//			else{
//				GlobalAppCacheMap.putKey("file_" + random, "1");
//			}
	//		List<Map<String,String>> listMap = null;
			if(fileRandomVal != null && fileRandomVal.toString().equals("3")){
				List<Map<String,String>> listRowMap1 =null;
				List<Map<String,String>> listRowMap2 = null;
				List<Map<String,String>> listRowMap3 = null;
				try {
					itemAnalyzeExcel = (ItemAnalyzeExcel) GlobalAppCacheMap.getValue("filedata_" + random);
					 List<AnalyzeExcel> listMapTotal = itemAnalyzeExcel.getListMap();
					if(listMapTotal.size()>1&&listMapTotal.size()<3){
						if(listMapTotal.get(0).getName().equals("所属素材模块")){
							listRowMap1 = (List<Map<String, String>>) listMapTotal.get(0).getListMap();
						}else{
							listRowMap1 = (List<Map<String, String>>) listMapTotal.get(1).getListMap();
						}
						
						if(listMapTotal.get(0).getName().equals("关联图书名称")){
							listRowMap2 = (List<Map<String, String>>) listMapTotal.get(0).getListMap();
						}else{
							listRowMap2 = (List<Map<String, String>>) listMapTotal.get(1).getListMap();
						}
					}else if(listMapTotal.size()>1&&listMapTotal.size()<4){
						for(int i=0;i<listMapTotal.size();i++){
							if(listMapTotal.get(i).getName().equals("所属素材模块")){
								listRowMap1 = (List<Map<String, String>>) listMapTotal.get(i).getListMap();
							}
							if(listMapTotal.get(i).getName().equals("关联图书名称")){
								listRowMap2 = (List<Map<String, String>>) listMapTotal.get(i).getListMap();
							}
							if(listMapTotal.get(i).getName()!=null&&listMapTotal.get(i).getName().equals("1")){
								listRowMap3 = (List<Map<String, String>>) listMapTotal.get(i).getListMap();
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				//解析处理map循环
				List<Map<String, String>> resultMap0  = new ArrayList<Map<String, String>>();
				List<Map<String, String>> resultMap1 = analyzeRowMap(listRowMap1,"02所属素材模块资源列表.xls","原始资源编号","所属素材模块");
				List<Map<String, String>> resultMap2 = analyzeRowMap(listRowMap2,"03关联图书资源列表.xls","原始资源编号","关联图书名称");
				List<Map<String, String>> resultMap3 = analyzeRowMap(resultMap1,resultMap2,"原始资源编号");
				List<Map<String, String>> resultMap =  analyzeRowMap(listRowMap3,resultMap3,"原始资源编号");
				excelPath = returnExcel(publishType,resultMap);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			fileRandomVal = GlobalAppCacheMap.getValue("file_" + random);
			if(fileRandomVal != null && fileRandomVal.toString().equals("3")){
				GlobalAppCacheMap.removeKey("file_" + random);
				ItemAnalyzeExcel cacheItemAnalyzeExcel = (ItemAnalyzeExcel) GlobalAppCacheMap.getValue("filedata_" + random);
				if(cacheItemAnalyzeExcel != null){
					GlobalAppCacheMap.removeKey("filedata_" + random);
				}
			}
		}
		if(StringUtils.isNotBlank(excelPath)){
			return excelPath;
		}else{
			return null;
		}
	}
	
	/**
	 * 返回单个rowMap1的重复处理
	 * @param rowMap1
	 * @param rowMap2
	 * @return
	 */
	public List<Map<String, String>> analyzeRowMap(List<Map<String,String>> rowMap,String excelName,String cnNumberName,String cnDoCellName) {
		List<Map<String, String>> resultListMap  = new ArrayList<Map<String, String>>();
		try {
			StringBuffer logger = new StringBuffer();
			if(rowMap != null){
				List<Integer> delIndex = new ArrayList<Integer>();
				for(int i = 0;i<rowMap.size();i++){
					boolean isContinue = false;
					if(delIndex.size() > 0){
						for (Integer index : delIndex) {
							if(i == index){
								isContinue = true;
								break;
							}
						}
						if(isContinue){
							continue;
						}
					}
					
					Map<String, String> map1 = rowMap.get(i);
					String number1 = map1.get(cnNumberName);
					if(StringUtils.isNotBlank(number1)){
						if(rowMap.size() > 1){
							String moduleVal = map1.get(cnDoCellName);
							moduleVal = (moduleVal == null) ? "":moduleVal;
							for(int j=1;j<rowMap.size();j++){
								Map<String, String> map2 = rowMap.get(j);
								String number2 = map2.get(cnNumberName);
								if(StringUtils.isNotBlank(number2)){
									if(number1.equals(number2)){
										String module2 = map2.get(cnDoCellName);
										module2 = (moduleVal == null) ? "":module2;
										if(StringUtils.isBlank(moduleVal)){
											moduleVal = module2;
										}else{
											if(StringUtils.isNotBlank(module2) && ("," + moduleVal).indexOf("," + module2) == -1){
												moduleVal += "," + module2;
											 }
										  }
										delIndex.add(j);
									   }
								   }else{
									   logger.append("" + excelName + " 第｛" + (i + 3) + "｝行，第｛2｝列【" + cnNumberName + "】为空。\n");
								   }
							  }
							map1.put(cnDoCellName, moduleVal);
						}
						resultListMap.add(map1); 
					 }else{
						//errorListMap.add(map1); //"" + cnNumberName + "为空的记录：" 
						logger.append("" + excelName + " 第｛" + (i + 3) + "｝行，第｛2｝列【" + cnNumberName + "】为空。\n");
					 }
					
					//resultListMap.add(map1); 
				 }
			 }else{
				logger.append("" + excelName + " 无数据 或数据有误。 \n");
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultListMap;
	}
	
	
	/**
	 * 返回单个rowMap的重复处理
	 * @param rowMap1 
	 * @param rowMap2
	 * @return
	 */
	public List<Map<String, String>> analyzeRowMap(List<Map<String, String>> resultMap02,List<Map<String, String>> resultMap03,String cnNumberName) {
		List<Map<String, String>> resultListMap  = new ArrayList<Map<String, String>>();
		try {
			if(resultMap02 !=null && resultMap03 != null){
				for(int i = 0;i<resultMap02.size();i++){
					Map<String, String> map02 = resultMap02.get(i);
					String number02 = map02.get(cnNumberName);
					if(StringUtils.isNotBlank(number02)){
						for(int j=0;j<resultMap03.size();j++){
							Map<String, String> map03 = resultMap03.get(j);
							String number03 = map03.get(cnNumberName);
							if(StringUtils.isNotBlank(number03)){
								if(number02.equals(number03)){
									for (Map.Entry<String, String> oldEntry : map03.entrySet()) {
										String oldKey = oldEntry.getKey();
										String oldValue = oldEntry.getValue();
										if (map02.get(oldKey) == null) {
											map02.put(oldKey, oldValue);
										}else{
											if(map02.get(oldKey).equals("")){
												map02.put(oldKey, oldValue);
											}
										}
									  }
								   }
							   } 
						  }
					 }
					resultListMap.add(map02); 
				 }
				
				//找出03excel中存在，但02excel中不存在的记录
				for(int j=0;j<resultMap03.size();j++){
					Map<String, String> map03 = resultMap03.get(j);
					String number03 = map03.get(cnNumberName);
					if(StringUtils.isNotBlank(number03)){
					   boolean b = false;
					   for(int i = 0;i<resultMap02.size();i++){
							Map<String, String> map02 = resultMap02.get(i);
							String number02 = map02.get(cnNumberName);
							if(StringUtils.isNotBlank(number02)){
								if(number03.equals(number02)){
									b = true;
									break;
							     }
						     }
					    }
					   if(!b){
						   resultListMap.add(map03); 
					   }
					}
				}
				
				
			 }else if(resultMap02 != null && resultMap03 == null){
				 resultListMap = resultMap02;
			 }else if(resultMap02 == null && resultMap03 != null){
				 resultListMap = resultMap03;
			 }
			} catch (Exception e) {
			e.printStackTrace();
		}
		return resultListMap;
	}
	
	
	/**
	 * 返回工作表EXCEL
	 */
	public String returnExcel(String publishType,List<Map<String, String>> listMap) {
		Map<String,MetadataDefinition> metadataDefinition = MetadataSupport.getAllMetadataDefinition();
		HSSFSheet oresTempleteSheet = null;
		HSSFWorkbook workbook = null;
			workbook = new HSSFWorkbook();
			 HSSFCellStyle style = workbook.createCellStyle();
			 style.setFillForegroundColor(HSSFColor.AQUA.index);// 设置背景色
			 style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	          style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平   
	          style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
	          style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
	          style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
	          style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框  
	          //生成一个字体
	          HSSFFont font=workbook.createFont();
	          font.setFontName("宋体");
	          font.setFontHeightInPoints((short)13);
	          font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);         //字体增粗
	         //把字体应用到当前的样式
	          style.setFont(font);
		HSSFSheet sheet = workbook.createSheet("数据清单");
		sheet.setColumnWidth(0, 20 * 256);
		// 用于第一行显示数据
		HSSFRow titleRow0 = sheet.createRow(0);
		HSSFCell cell0 =null;
		//用于显示第二行隐藏数据
		HSSFRow titleRow1 = sheet.createRow(1);
		titleRow1.setZeroHeight(true);
		HSSFCell cell1  = null;
		sheet.setColumnWidth(0, 20 * 256);
		oresTempleteSheet = workbook.getSheetAt(0);
	    // 定义注释的大小和位置
//		Drawing patriarch1 = oresTempleteSheet.createDrawingPatriarch(); 
//		HSSFClientAnchor clientAnchor1 = new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6,7);  
//		Comment comment1 = patriarch1.createCellComment(clientAnchor1);  
//			//comment.setString(createHelper.createRichTextString("必填项;"));  
//	        // 设置注释内容
////	        comment1.setString(new HSSFRichTextString("(版本号)是以'v'加数字组成，例v1"));
//	    cell.setCellComment(comment1);
//		cell.setCellStyle(style);
		List<MetadataDefinition> metadataDefinitions = MetadataSupport
				.getMetadateDefines(publishType);
		//用于存储所有的头标签项
		List<String> listMark = new ArrayList<String>();
//		listMark.add("文件路径");
		int num = 1;
		boolean fag = false;
		if(metadataDefinitions.size()>0){
			fag = true;
		}
		for(int j =0;j<metadataDefinitions.size();j++){
			cell1 = titleRow1.createCell((short)j);
			cell1.setCellStyle(style);
			num = num+1;
			//生成第二行表数据
			if(metadataDefinitions.get(j).getFieldName().equalsIgnoreCase("scInputNumber")){
				cell1.setCellValue("filePath");
			}else{
				cell1.setCellValue(metadataDefinitions.get(j).getFieldName());
			}
			sheet.setColumnWidth(j+1, 20 * 256);
			//第一行显示数据
			HSSFCell cell4 = titleRow0.createCell((short)j);
			cell4.setCellStyle(style);
			cell4.setCellValue(metadataDefinitions.get(j).getFieldZhName());
			listMark.add(metadataDefinitions.get(j).getFieldZhName());
//			if(metadataDefinitions.get(j).getFieldName().equals("qualityTime")){
//				System.out.println(metadataDefinitions.get(j).getFieldZhName());
//			}
			String tab = FieldExcelValidator.checkFieldHasPoint(metadataDefinition, metadataDefinitions.get(j).getFieldName());
			if(!tab.equals("")){
			   oresTempleteSheet = workbook.getSheetAt(0);
		        // 定义注释的大小和位置，详见文档
			   Drawing patriarch = oresTempleteSheet.createDrawingPatriarch(); 
			   HSSFClientAnchor clientAnchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6,7);  
			   Comment comment = patriarch.createCellComment(clientAnchor);  
		        // 设置注释内容
		       comment.setString(new HSSFRichTextString(tab));
		       cell4.setCellComment(comment);
		       System.out.println(tab);
			}
		}
		//循环map数据填充excel
		for(int i=0;i<listMap.size();i++){
			titleRow0 = sheet.createRow(2+i);
			if(listMap.get(i).get("素材名称")!=null){
				listMap.get(i).put("资源标题", listMap.get(i).get("素材名称"));
				listMap.get(i).remove("素材名称");
			}else if(listMap.get(i).get("入库资源编号")!=null){
				
			}
			//所有的标签项
			for(int j=0;j<listMark.size();j++){
				cell0 = titleRow0.createCell(j);
				if(listMap.get(i).get(listMark.get(j))!=null){
					//循环数据添充excel
					cell0.setCellValue(listMap.get(i).get(listMark.get(j)));
				}
			}
		}
		File outFile = new File(FILE_TEMP + File.separator
				+ dateformat2.format(new Date()) + ".xls");
		OutputStream out = null;
		try {
			out = new FileOutputStream(outFile);
			workbook.write(out);// 写入File
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return outFile.getAbsolutePath();
	}
	/**
	 * 
	* @Title: analyzeExcelData
	* @Description: 处理合并Excel数据
	* @param restore	上传的Excel文件
	* @return
	* @throws FileNotFoundException
	* @throws IOException    参数
	* @return ExcelData    返回类型	处理后的Excel数据
	* @throws
	 */
	public ExcelData analyzeExcelData(File restore) throws FileNotFoundException, IOException{

		//------------------解析excel数据   开始
		ExcelData data = new ExcelData();
		File excel = new File(restore.getAbsolutePath());
		HSSFWorkbook workbook = null;
		
        workbook = new HSSFWorkbook(new FileInputStream(excel)); 
		HSSFSheet sheet = workbook.getSheetAt(0);
		/** 第一步，先读取标记行 */
		ExcelDataDetailMK marker = null;
		HSSFRow markerRow = sheet.getRow(0);
		int lastCell = markerRow.getLastCellNum() - 1;
		HSSFCell tempCell = null;
		String tempValue = "";
//			String[] tempValueArray;
		//将excel中的标记转换为实体
		ExcelDataDetailMK[] markers = new ExcelDataDetailMK[lastCell+1];
		for (int i = 0; i <= lastCell; i++) {
			tempCell = markerRow.getCell(i);
			tempValue = tempCell.getStringCellValue();
			if(tempValue!=null && !"".equals(tempValue)){
				marker = new ExcelDataDetailMK(tempValue);
				markers[i] = marker;
			}
		}
		data.setMarkers(markers);

//			Map<String,MetadataDefinition> metadataDefinition = MetadataSupport.getAllMetadataDefinition();
		/** 第二步，解析每行数据 */
		int lastRowNum = sheet.getLastRowNum();
		HSSFRow tempRow;
		String[] datas;
		//用于存放dataRow对象的集合
		List<ExcelDataRow> rows = new ArrayList<ExcelDataRow>();
		//excel里的每条数据对象dataRow
		ExcelDataRow dataRow = null;
		for (int i = 1; i <= lastRowNum; i++) {
			tempRow = sheet.getRow(i);
			if (tempRow != null) {
				datas = new String[lastCell + 1];
				dataRow = new ExcelDataRow();
				HSSFCell tempCell1 = tempRow.getCell(0);
//				HSSFCell tempCell1 = tempRow.getCell(1);
				for (int j = 0; j <= lastCell; j++) {
					tempCell = tempRow.getCell(j);
					if (null != tempCell) {
						int type = tempCell.getCellType();
						String cellContent = "";
						if(HSSFCell.CELL_TYPE_NUMERIC == type){
							DecimalFormat df = new DecimalFormat("0");
						    cellContent =df.format(tempCell.getNumericCellValue());
						    if(HSSFDateUtil.isCellDateFormatted(tempCell)){
						    	
								Date d = tempCell.getDateCellValue();
								DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
								cellContent = formater.format(d);
							}
						    datas[j] = cellContent.trim();
						}else{
							if(tempCell.getStringCellValue()!=null && !"".equals(tempCell.getStringCellValue()))
							datas[j] = tempCell.getStringCellValue().trim();
						}
					} else {
						datas[j] = "";
					}
					
				}
					dataRow.setDatas(datas);
					dataRow.setNum(i + 1);
					rows.add(dataRow);
			}
		}
		data.setRows(rows);
		data.setTotalNum(rows.size());
		//------------------解析excel数据   结束
		return data;
	}
	
	/**
    *
    * @Title: getISBNToChar
    * @Description:  从字符串中获取isbn号
    * @param @param str
    * @param @return
    * @return String
    * @throws
    */
   public static String getISBNToChar(String str) {
   	String strLine= "";
   	if(StringUtils.isNotBlank(str)){
   		   StringReader stringReader = new StringReader(str);
   	  	   BufferedReader bufferedReader = new BufferedReader(stringReader);
   	  	   try {
   	  		while((strLine = bufferedReader.readLine())!= null){
   	  			if(strLine.toLowerCase().contains("isbn")){
   	  				strLine = strLine.toLowerCase().replaceAll("isbn", "");
   	  				strLine = strLine.trim();
   	  				break;
   	  			}
   	  		   }
   		  	} catch (IOException e) {
   		  		e.printStackTrace();
   		  	}
   	}
   	 return strLine;
   }

	
    
    public static void main(String[] args) {
//		Map<String, String> maps= new HashMap<>();
//		maps.put("revnumber", "第12版");
//		maps.put("language", "简体中文");
//		maps.put("date", "01/04/2015");
//		maps.put("date", "04/2015");
//		maps.put("date", "2011-12");
//		maps.put("date", "2011-12-4");
//		maps = changeDataMap(maps);
//		System.out.println(maps.toString());
		
//		String str = "abstract=序·１·序全国政ư协常委中国煤炭工业协会会长煤炭 』是我ư国的基础能源，为保障我国经济社会发展发挥了重要ư作用。２０１１年我国原煤产量达到３５ư２亿吨，“十二五”规划煤炭产能将达到４１亿吨；据专家预测，到２０５０年，煤炭在我国一次能源结构中的比例仍将超过５０％。.…因此，煤炭作为我国主体能源…的地位在相当长的时间内是不会改变的。科技进步是支撑我国煤炭产量快速增长的根本保证。近年来，我国在煤炭科技，特别是煤炭开";
//		str =  stringFilter(str);
//		System.out.println(str);
		
//		try {
//			DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
//			Date date1 = df1.parse("01/04/2015");
//			System.out.println(date1.getTime());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
		
//		//查找pdf文件
//		File pdfParentFile = new File("C:/Users/brainsoon/Desktop/9787502041519H.pdf");
//		Pattern p = Pattern.compile("([^\\s]+(\\.(?i)(pdf))$)");
//		ArrayList<File> files = DoFileUtils.filePattern( pdfParentFile , p);
//		
//		//抽取第一本pdf的第四页的的文本
//		if (files.size()>0) {
//			File firstPdfFile = files.get(0);
//			String abstractStr = null;
//			try {
//				abstractStr = PdfUtil.parsePdf(firstPdfFile.getAbsolutePath(),3,3);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			//截取前200个字作为摘要
//			if (StringUtils.isNotBlank(abstractStr)) {
//				//转码
//				try {
//					abstractStr = new String(abstractStr.getBytes(), "UTF-8");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				abstractStr = abstractStr.replaceAll("\\s", "");
//				abstractStr = abstractStr.replaceAll("　", "");
//				abstractStr = abstractStr.replaceAll("序", "");
//				abstractStr = abstractStr.replaceAll("９", "9");
//				abstractStr = abstractStr.replaceAll("内容提要", "");
//				abstractStr = CharacterUtils.ToSBC(abstractStr);
//				
//				//过滤掉非UTF-8字符方法
//				try {
//					abstractStr =CharacterUtils.filterOffUtf8Mb4(abstractStr);
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				//过滤特殊字符
//				abstractStr =CharacterUtils.stringFilter(abstractStr);//过滤特殊字符
//				if (abstractStr.length()>199) {
//					abstractStr = abstractStr.substring(0, 199);
//					System.out.println(abstractStr);
//				}
//			}
//		}
    	
    	
//    	//查找pdf文件
//		File pdfParentFile = new File("C:/Users/brainsoon/Desktop/9787502041519H.pdf");
//		Pattern p = Pattern.compile("([^\\s]+(\\.(?i)(pdf))$)");
//		ArrayList<File> files = DoFileUtils.filePattern( pdfParentFile , p);
//		
//		//抽取第一本pdf的第四页的的文本
//		if (files.size()>0) {
//			File firstPdfFile = files.get(0);
//			String isbnStr = "";
//			try {
//				isbnStr = PdfUtil.parsePdf(firstPdfFile.getAbsolutePath(),3,3);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			if (StringUtils.isNotBlank(isbnStr)) {
//				try {
//					isbnStr = isbnStr.replaceAll("　", "");
//					//全角转半角
//					isbnStr = CharacterUtils.ToDBC(isbnStr);
//					//过滤掉非UTF-8字符方法
//					isbnStr = CharacterUtils.filterOffUtf8Mb4(isbnStr);
//					//过滤特殊字符
//					isbnStr = CharacterUtils.stringFilter(isbnStr);
//					//获取ISBN
//					isbnStr = getISBNToChar(isbnStr);
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				System.out.println(isbnStr);
//				
//			}
//			
//		}
    	
//    	
//    	String isbn = "978－7－5020－4287－5";
//    	System.out.println(isbn);
//    	isbn = isbn.replaceAll("－", "-").replaceAll(" ", "");
//    	System.out.println(isbn);
    	
	}
    
  /*  *//**
	 * 解析XML文件
	 *//*
	public List<Map<String, String>> savefile_new(String path,UploadTask uploadTask, UploadTaskDetail uploadTaskDetail)
			throws DocumentException {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> maperror = new HashMap<String, String>();
		Map<String, String> mapzheng = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		
		//处理map，将所有元数据模板中的信息保存到map中，解析到有值时就更新
		List<MetadataDefinition> metadataDefinitions = MetadataSupport.getMetadateDefines(uploadTask.getLibType());
		for (MetadataDefinition metadataDefinition : metadataDefinitions) {
			if (metadataDefinition.getDefaultValue() !=null && !"".equals(metadataDefinition.getDefaultValue())){
				map.put(metadataDefinition.getFieldName(), metadataDefinition.getDefaultValue());
			}else {
				map.put(metadataDefinition.getFieldName(), "");
			}
			logger.info("【BatchImportResService】 解析XML文件 savefile 初始化map->>>map："+map.toString());
		}
		
		
		if(StringUtils.isNotBlank(path)){
			logger.info("------------进入解析代码-----------------");
			String fileType = upload.substring(upload.lastIndexOf(".") + 1, upload.length());
			String outPutPath =  FILE_ROOT + UUID.randomUUID();
			ZipOrRarUtil.unzip(FILE_TEMP+upload, outPutPath, fileType);
			String Filename=upload.substring(upload.lastIndexOf("\\")+1, upload.lastIndexOf("."));
			String filepath = outPutPath+"/"+Filename+"/"+"Main.xml";
			String wenjianfile = path;
			String filepath = path+"/"+"Main.xml";
			File files = new File(filepath);
			SAXReader reader = new SAXReader();
			Document dom = null;
			try {
				dom = reader.read(files);
			} catch (Exception e) {
				 throw new ServiceException("不符合XML文档规范，如：缺少结束标签，结构嵌套错误。");
			}
			//rootElement获取根节点
			Element rootElement =  dom.getRootElement(); 
			//获取二级节点
			Iterator ito = rootElement.elementIterator(); 
			//获取二级节点中的第一个
			while (ito.hasNext()) {
				Element elements = (Element) ito.next();
				if(elements.getName().equals("info")){
		
				for(Iterator iterator=elements.elementIterator();iterator.hasNext();){
					//elementInner是三级节点
					Element elementInner = (Element) iterator.next();   
					 if(elementInner.getName().equals("title")){
						 Attribute roleAttr=elementInner.attribute("role");
						 if(roleAttr==null){
							map.put("title", elementInner.getText());//*标题title
							logger.info("【BatchImportResService】 解析XML文件 savefile->>>标题title:"+elementInner.getText());
						 }else if (StringUtils.isNotBlank(roleAttr.getValue())){
							if("seriestitle".equals(roleAttr.getValue().trim().toLowerCase()) ){//兼容500本
								map.put("serialTitle", elementInner.getText());//*丛书名称serialTitle
								logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书名称serialTitle:"+elementInner.getText());
							}
						 }
							 
							
					 }
					 if(elementInner.getName().equals("subtitle")){
						map.put("subtitle", elementInner.getText());//*副标题subtitle
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>副标题subtitle:"+elementInner.getText());
					 }
					 if(elementInner.getName().equals("biblioid")){
						 Attribute classAttr=elementInner.attribute("class");
						 Attribute otherAttr=elementInner.attribute("otherclass");
						 if (StringUtils.isNotBlank(classAttr.getValue()) ){
							 if(classAttr.getValue().trim().toLowerCase().equals("isbn") || "issn".equals(classAttr.getValue().trim().toLowerCase())){
								 String isbn = elementInner.getText();
								 isbn = isbn.replaceAll("－", "-").replaceAll(" ", "");
								 map.put("isbn", isbn);//*ISBNisbn
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>ISBNisbn:" + isbn);
							 }
						 }
						 if (StringUtils.isNotBlank(classAttr.getValue()) ){
							 if(classAttr.getValue().trim().toLowerCase().equals("pressnumber")){//此处换小写，把N该了
								 map.put("pressNumber", elementInner.getText());//*社内书号pressNumber
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>社内书号pressNumber:"+elementInner.getText());
							 }
						 }
						 if (otherAttr != null && StringUtils.isNotBlank(classAttr.getValue()) && StringUtils.isNotBlank(otherAttr.getValue())){
							 if(classAttr.getValue().trim().toLowerCase().equals("other") && otherAttr.getValue().trim().toLowerCase().equals("cip")){
								 map.put("cip", elementInner.getText());//*cip号cip
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>cip号cip:"+elementInner.getText());
							 }
							 if(classAttr.getValue().trim().toLowerCase().equals("other") && otherAttr.getValue().trim().toLowerCase().equals("prtcnt")){
								 map.put("prtcnt", elementInner.getText());//*印次prtcnt
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印次prtcnt:"+elementInner.getText());
							 }
							 if(classAttr.getValue().trim().toLowerCase().equals("other") && otherAttr.getValue().trim().toLowerCase().equals("yinci")){//兼容500本
								 map.put("prtcnt", elementInner.getText());//*印次prtcnt
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印次prtcnt:"+elementInner.getText());
							 }
						 }
					 }
					 *//**
					  * 解析authorgroup节点
					  * elementInner代表的是第三级级节点，根据三级节点获取下级节点
					  * author代表四级节点
					  *//*
					 if(elementInner.getName().equals("authorgroup")){
						 StringBuffer bookAuthor = new StringBuffer();
						 StringBuffer contributor = new StringBuffer();
						 StringBuffer Editor = new StringBuffer();
						 StringBuffer fictionEditor = new StringBuffer();
						 StringBuffer translator = new StringBuffer();
						 StringBuffer designEditor = new StringBuffer();
						 StringBuffer seriesBoooksName = new StringBuffer();
						 StringBuffer executiveEditor = new StringBuffer();
						 Iterator author = elementInner.elementIterator("author");
						 while (author.hasNext()) {
							Staff staff = new Staff();
							Element auth = (Element) author.next(); 
							Attribute roleAttr=auth.attribute("role");
							if (roleAttr != null && StringUtils.isNotBlank(roleAttr.getValue())) {
								if("bookauthor".equals(roleAttr.getValue().trim().toLowerCase()) || roleAttr.getValue().trim().toLowerCase().equals("著")){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
									Element personname = auth.element("personname");
									if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									bookAuthor.append(staffService.doSaveOrUpdate(staff)+",");
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("editor") || roleAttr.getValue().trim().toLowerCase().equals("主编")){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
									Iterator personname = auth.elementIterator("personname");
									StringBuffer sbpersonname = new StringBuffer();
									String sbperson=null;
									while (personname.hasNext()) {
										Element personname1 = (Element) personname.next();
										sbpersonname.append(personname1.getText()+",");
									}
									if(sbpersonname.toString().length()>1){
										sbperson =  sbpersonname.toString().substring(0, sbpersonname.toString().length()-1);
										map.put("Editor",sbperson);
									}else{
										map.put("Editor",sbpersonname.toString());
									}
							 		Element personname = auth.element("personname");
							 		if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									Editor.append(staffService.doSaveOrUpdate(staff)+",");
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("contributor") || "编著".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									contributor.append(staffService.doSaveOrUpdate(staff)+",");
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("fictioneditor") || ("责任编辑").equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									fictionEditor.append(staffService.doSaveOrUpdate(staff)+",");
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("translator") || "译者姓名".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									translator.append(staffService.doSaveOrUpdate(staff)+",");
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("designeditor") || "策划编辑".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									designEditor.append(staffService.doSaveOrUpdate(staff)+",");
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("seriesboooksname") || "丛书作者姓名".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									seriesBoooksName.append(staffService.doSaveOrUpdate(staff)+",");
								 }
							 	if(roleAttr.getValue().trim().toLowerCase().equals("executiveeditor") || "执行编辑".equals(roleAttr.getValue().trim().toLowerCase())){//兼容500本
							 		//根据四级节点获取下级节点(这是获取personname节点)
							 		Element personname = auth.element("personname");
							 		if(null!=personname){
										//map.put("bookAuthor",personname.getText());
										staff.setName(personname.getText());
									}
									if(null==personname){
										Element orgname = auth.element("orgname");
										if(null!=orgname){
											//map.put("bookAuthor", orgname.getText());
											staff.setName(orgname.getText());
										}
									}
									Element affiliation = auth.element("affiliation");
									if(null!=affiliation){
										staff.setWorkName(affiliation.getText());
									}
									Element address = auth.element("address");
									if(null!=address){
										staff.setAddress(address.getText());
									}
									Element email = auth.element("email");
									if(null!=email){
										staff.setEmail(email.getText());
									}
									executiveEditor.append(staffService.doSaveOrUpdate(staff)+",");
								 }	
							} 
						 }
						 if(StringUtils.isNotBlank(bookAuthor.toString())){
							 map.put("bookAuthor", bookAuthor.toString().substring(0, bookAuthor.toString().length()-1));//*著bookAuthor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>著bookAuthor:"+bookAuthor.toString().substring(0, bookAuthor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(contributor.toString())){
							 map.put("contributor", contributor.toString().substring(0, contributor.toString().length()-1));//*编著contributor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>编著contributor:"+contributor.toString().substring(0, contributor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(Editor.toString())){
							 map.put("editor", Editor.toString().substring(0, Editor.toString().length()-1));//*主编editor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>主编editor:"+Editor.toString().substring(0, Editor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(fictionEditor.toString())){
							 map.put("fictionEditor", fictionEditor.toString().substring(0, fictionEditor.toString().length()-1));//*责任编辑fictionEditor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>责任编辑fictionEditor:"+fictionEditor.toString().substring(0, fictionEditor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(translator.toString())){
							 map.put("translator", translator.toString().substring(0, translator.toString().length()-1));//*译者姓名translator
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>译者姓名translator:"+translator.toString().substring(0, translator.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(designEditor.toString())){
							 map.put("designEditor", designEditor.toString().substring(0, designEditor.toString().length()-1));//*策划编辑designEditor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>策划编辑designEditor:"+designEditor.toString().substring(0, designEditor.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(seriesBoooksName.toString())){
							 map.put("seriesBoooksName", seriesBoooksName.toString().substring(0, seriesBoooksName.toString().length()-1));//*丛书作者姓名seriesBoooksName
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书作者姓名seriesBoooksName:"+seriesBoooksName.toString().substring(0, seriesBoooksName.toString().length()-1));
						 }
						 if(StringUtils.isNotBlank(executiveEditor.toString())){
							 map.put("executiveEditor", executiveEditor.toString().substring(0, executiveEditor.toString().length()-1));//*执行编辑executiveEditor
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>执行编辑executiveEditor:"+executiveEditor.toString().substring(0, executiveEditor.toString().length()-1));
						 }
						 
					 }
					 if(elementInner.getName().toLowerCase().equals("publisher")){
						 Company company = new Company();
						 Element publishername = elementInner.element("publishername");
						 if(null!=publishername){
							 //map.put("publishername", publishername.getText());
							 company.setName(publishername.getText());
						 }
						 Element address = elementInner.element("address");
						 if(null!=address){
							 //map.put("address", address.getText());
							 company.setAddress(address.getText());
						 }
						 if(null!=publishername && null!=address){
							 
						 }
						 String companys  =  companyService.doSaveOrUpdate(company);
						 map.put("publishername", companys);//*出版社名称publishername
						 logger.info("【BatchImportResService】 解析XML文件 savefile->>>出版社名称publishername:"+companys);
					 }
					 if(elementInner.getName().equals("revhistory")){
						 Element revision = elementInner.element("revision");
						 if(null!=revision){
							 Element revnumber = revision.element("revnumber");
							 if(null!=revnumber){
								 //若版本为个位，前面加0
								 String revnumberVal = revnumber.getText();
								 DecimalFormat df=new DecimalFormat("00");
							     revnumberVal=df.format(Integer.parseInt(revnumberVal));
							     
								 map.put("revnumber", revnumber.getText());//*版本号revnumber
								 //map.put("revhistory", revnumber.getText());//*版本revhistory   没有该字段的信息
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>版本号revnumber:"+revnumber.getText());
							 }
							 Element date = revision.element("date");
							 if(null!=date){
								 map.put("date", date.getText());//*版本日期date
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>版本日期date:"+date.getText());
							 }
						 }
					 }
					 if(elementInner.getName().equals("abstract")){
						 Iterator para = elementInner.elementIterator("para");
						 StringBuffer sbpara = new StringBuffer();
						 String sbparas = null;
						 while (para.hasNext()) {
							Element paras = (Element) para.next();
							if(StringUtils.isNotBlank(paras.getText())){
//								sbpara.append(paras.getText()+"<br>");
								sbpara.append(paras.getText());
							}
						}
						 if(sbpara.toString().length()>4){
							sbparas = sbpara.toString().substring(0, sbpara.toString().length()-4);
							map.put("abstract", sbparas);
							logger.info("【BatchImportResService】 解析XML文件 savefile->>>摘要abstract:"+sbparas);
						 }else{
							map.put("abstract", sbpara.toString());//*摘要abstract
							logger.info("【BatchImportResService】 解析XML文件 savefile->>>摘要abstract:"+sbpara.toString());
						 }
					 }
					 if(elementInner.getName().equals("legalnotice")){
						 Element para = elementInner.element("para");
						 if(null!=para){
							 map.put("legalnotice", para.getText());//*法律声明legalnotice
							 logger.info("【BatchImportResService】 解析XML文件 savefile->>>法律声明legalnotice:"+para.getText());
						 }
					 }
					 
					 if(elementInner.getName().equals("keywordset")){
						 Iterator keyword = elementInner.elementIterator("keyword");
						 StringBuffer sbkeyword = new StringBuffer();
							String sbkey=null;
							while (keyword.hasNext()) {
								Element keyword1 = (Element) keyword.next();
								if(StringUtils.isNotBlank(keyword1.getText())){
									sbkeyword.append(keyword1.getText()+",");
								}
							}
							if(sbkeyword.toString().length()>1){
								sbkey =  sbkeyword.toString().substring(0, sbkeyword.toString().length()-1);
								map.put("keywordset",sbkey);
								logger.info("【BatchImportResService】 解析XML文件 savefile->>>关键字keywordset:"+sbkey);
							}else{
								map.put("keywordset",sbkeyword.toString());//*关键字keywordset
								logger.info("【BatchImportResService】 解析XML文件 savefile->>>关键字keywordset:"+sbkeyword.toString());
							}
					 }
					 if(elementInner.getName().equals("releaseinfo")){
						 Attribute roleAttr=elementInner.attribute("role");
						 if (StringUtils.isNotBlank(roleAttr.getValue())) {
							 if(roleAttr.getValue().trim().toLowerCase().equals("cipinfo")){
								 Iterator alt = elementInner.elementIterator("alt");
								 StringBuffer sbalt = new StringBuffer();
								 String sbalts = null;
								 while (alt.hasNext()) {
									Element alts = (Element) alt.next();
									if(StringUtils.isNotBlank(alts.getText())){
										sbalt.append(alts.getText()+"<br>");
									}
								}
								 if(sbalt.toString().length()>4){
									sbalts = sbalt.toString().substring(0, sbalt.toString().length()-4);
									map.put("cipInfo", sbalts);
									logger.info("【BatchImportResService】 解析XML文件 savefile->>>CIP数据cipInfo:"+sbalts);
								 }else{
									 map.put("cipInfo", sbalt.toString());//*CIP数据cipInfo
									 logger.info("【BatchImportResService】 解析XML文件 savefile->>>CIP数据cipInfo:"+sbalt.toString());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("pprice") || roleAttr.getValue().trim().toLowerCase().equals("price")){//兼容500本
								 map.put("pprice", elementInner.getText());//*纸质图书价格pprice
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>纸质图书价格pprice:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("cbclass") || roleAttr.getValue().trim().toLowerCase().equals("ztfclasscode")){//兼容500本
								 map.put("cbclass", elementInner.getText());//*中图分类号cbclass
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>中图分类号cbclass:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("totalpages")){
								 map.put("totalpages", elementInner.getText());//*页码totalpages
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>页码totalpages:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("digitcopyright")){
								 map.put("digitcopyright", elementInner.getText());//*数字版权digitcopyright
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>数字版权digitcopyright:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("dimensions")){
								 map.put("dimensions", elementInner.getText());//*开本dimensions
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>开本dimensions:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("coverdesigner")){//转换小写，把d换了
								 map.put("coverDesigner", elementInner.getText());//*封面设计coverDesigner
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>封面设计coverDesigner:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("binding")){
								 map.put("binding", elementInner.getText());//*装帧设计binding
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>装帧设计binding:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("layoutdesigner")){//转换小写，把d换了
								 map.put("layoutDesigner", elementInner.getText());//*版式设计layoutDesigner
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>版式设计layoutDesigner:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("proofreads")){
								 map.put("proofreads", elementInner.getText());//*责任校对proofreads
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>责任校对proofreads:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("alphabettitle")){//转换小写，把t换了
								 map.put("alphabetTitle", elementInner.getText());//*题名拼音alphabetTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>题名拼音alphabetTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("alttitle")){//转换小写，把t换了
								 map.put("altTitle", elementInner.getText());//*交替题名altTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>交替题名altTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("partitle")){//转换小写，把t换了
								 map.put("parTitle", elementInner.getText());//*并列题名parTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>并列题名parTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("othertitle")){//转换小写，把t换了
								 map.put("otherTitle", elementInner.getText());//*其他题名otherTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>其他题名otherTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("serialtitle")){//转换小写，把t换了
								 map.put("serialTitle", elementInner.getText());//*丛书名称serialTitle
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书名称serialTitle:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("type")){
								 map.put("type", elementInner.getText());//*资源类别type
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>资源类别type:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("printcopies")){//转换小写，把c换了
								 map.put("printCopies", elementInner.getText());//*印数printCopies
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印数printCopies:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("serialnumber")){//转换小写，把n换了
								 map.put("serialNumber", elementInner.getText());//*丛书序列serialNumber
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>丛书序列serialNumber:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("printingunit")){//转换小写，把u换了
								 //map.put("printingUnit", elementInner.getText());//*印刷单位printingUnit
								 
								 Company company = new Company();
								 company.setName(elementInner.getText());
								 String printingUnit  =  companyService.doSaveOrUpdate(company);
								 map.put("printingUnit", printingUnit);//*印刷单位printingUnit
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印刷单位printingUnit:"+printingUnit);
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("words")){
								 map.put("words", elementInner.getText());//*字数words
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>字数words:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("ebookprice")){//转换小写，把p换了
								 map.put("ebookPrice", elementInner.getText());//*电子书价格ebookPrice
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>电子书价格ebookPrice:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("review")){
								 map.put("review", elementInner.getText());//*封底书评review
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>封底书评review:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("format")){
								 map.put("format", elementInner.getText());
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>format:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("distributionchannels")){//转换小写，把c换了
								 //map.put("distributionChannels", elementInner.getText());//*发行渠道distributionChannels
								 
								 Company company = new Company();
								 company.setName(elementInner.getText());
								 String distributionChannels  =  companyService.doSaveOrUpdate(company);
								 map.put("distributionChannels", distributionChannels);//*印刷单位printingUnit
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>印刷单位printingUnit:"+distributionChannels);
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("printedsheets")){//转换小写，把s换了
								    String xmlss = null;
									xmlss=elementInner.asXML();
									xmlss=xmlss.substring(xmlss.indexOf(">")+1,xmlss.lastIndexOf("<"));
									map.put("printedSheets", xmlss);//*印张printedSheets
									logger.info("【BatchImportResService】 解析XML文件 savefile->>>印张printedSheets:"+xmlss);
								 //map.put("printedSheets", elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("language")){
								 map.put("language", elementInner.getText());//*语种language
								 logger.info("【BatchImportResService】 解析XML文件 savefile->>>语种language:"+elementInner.getText());
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("images")){
								 Attribute imageshref=elementInner.attribute("href");
								 String image=wenjianfile+"/"+imageshref.getValue();
								 File file = new File(image);
								 if(!file.exists()){
									 sb.append("images路径未找到！请检查！");
								 }else{
									 mapzheng.put("images", imageshref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("source")){
								 Attribute sourcehref=elementInner.attribute("href");
								 String source=wenjianfile+"/"+sourcehref.getValue();
								 File file = new File(source);
								 if(!file.exists()){
									 sb.append("source路径未找到！请检查！");
								 }else{
									 mapzheng.put("source", sourcehref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("epub")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("epub", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("singlehighpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("singlehighpdf", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("singlelowerpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("singlelowerpdf", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("doublehighpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute highpdfhref=elementInner.attribute("href");
								 String highpdf=wenjianfile+"/"+highpdfhref.getValue();
								 File file = new File(highpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("doublehighpdf", highpdfhref.getValue());
								 }
							 }
							 if(roleAttr.getValue().trim().toLowerCase().equals("doublelowerpdf")){
								 Attribute annotations =elementInner.attribute("annotations");
								 Attribute lowerpdfhref=elementInner.attribute("href");
								 String lowerpdf=wenjianfile+"/"+lowerpdfhref.getValue();
								 File file = new File(lowerpdf);
								 if(!file.exists()){
									 sb.append(annotations.getValue()+"文件路径未找到！请检查！");
								 }else{
									 mapzheng.put("doublelowerpdf", lowerpdfhref.getValue());
								 }
							 }
						}
					}
					if(elementInner.getName().equals("cover")){
						Element mediaobject = elementInner.element("mediaobject");
						Element imageobject = mediaobject.element("imageobject");
						Element imagedata = imageobject.element("imagedata");
						Attribute fileref = imagedata.attribute("fileref");
						if(StringUtils.isNotBlank(fileref.getValue())){
							File file = new File(wenjianfile+"/"+fileref.getValue());
							if(!file.exists()){
								sb.append("作者照片路径未找到,请检查!");
							}else{
								mapzheng.put("imagedata", fileref.getValue());
							}
						}
					}
					if(elementInner.getName().toLowerCase().equals("publisherdate")){
						Element year = elementInner.element("year");
						if(null!=year){
							map.put("year", year.getText());
						}
						Element holder = elementInner.element("holder");
						if(null!=holder){
							map.put("holder", holder.getText());
						}
						map.put("publisherDate", elementInner.getText());//*出版时间publisherDate
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>出版时间publisherDate:"+elementInner.getText());
					}
					if(elementInner.getName().toLowerCase().equals("pressclass")){
						map.put("pressclass", elementInner.getText());//*社内分类pressclass
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>社内分类pressclass:"+elementInner.getText());
					}
					if(elementInner.getName().toLowerCase().equals("subjectclass")){
						map.put("subjectclass", elementInner.getText());//*学科分类subjectclass
						logger.info("【BatchImportResService】 解析XML文件 savefile->>>学科分类subjectclass:"+elementInner.getText());
					}
				} 
				
				
				}
				if(elements.getName().equals("include")){
					Attribute href = elements.attribute("href");
					File file=new File(wenjianfile+href.getValue());
					if(!file.exists()){
						sb.append("引用的配置文件不存在");
					}else{
						mapzheng.put("include", href.getValue());
					}
					
				}
					
			}
		}
		
		//处理摘要
		if (StringUtils.isBlank(map.get("abstract"))){
			//查找pdf文件
			File pdfParentFile = new File(path);
			Pattern p = Pattern.compile("([^\\s]+(\\.(?i)(pdf))$)");
			ArrayList<File> files = DoFileUtils.filePattern( pdfParentFile , p);
			
			//抽取第一本pdf的第四页的的文本
			if (files.size()>0) {
				File firstPdfFile = files.get(0);
				String abstractStr = "";
				try {
					abstractStr = PdfUtil.parsePdf(firstPdfFile.getAbsolutePath(),4,4);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (StringUtils.isNotBlank(abstractStr)) {
					
					try {
						abstractStr = abstractStr.replaceAll("\\s", "");
						abstractStr = abstractStr.replaceAll("　", "");
						abstractStr = abstractStr.replaceAll("序", "");
						abstractStr = abstractStr.replaceAll("内容提要", "");
						//全角转半角
						abstractStr = CharacterUtils.ToDBC(abstractStr);
						//过滤掉非UTF-8字符方法
						abstractStr = CharacterUtils.filterOffUtf8Mb4(abstractStr);
						//过滤特殊字符
						abstractStr = CharacterUtils.stringFilter(abstractStr);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//截取前200个字作为摘要
					if (abstractStr.length()>199) {
						abstractStr = abstractStr.substring(0, 199);
					}
				}
				
				map.put("abstract", abstractStr);
			}
			
		}
		
		//处理ISBN
		boolean isISBN = false;//是否需要从pdf中读取isbn
		String isbn = map.get("isbn");
		if (StringUtils.isBlank(isbn)) {
			isISBN = true;
		}else {
			try {
				ISBNChecker good = new ISBNChecker(isbn);
			} catch (Exception e) {
				isISBN = true;
			}
		}
		//执行获取ISBN
		if (isISBN) {
			//查找pdf文件
			File pdfParentFile = new File(path);
			Pattern p = Pattern.compile("([^\\s]+(\\.(?i)(pdf))$)");
			ArrayList<File> files = DoFileUtils.filePattern( pdfParentFile , p);
			
			//抽取第一本pdf的第四页的的文本
			if (files.size()>0) {
				File firstPdfFile = files.get(0);
				String isbnStr = "";
				try {
					isbnStr = PdfUtil.parsePdf(firstPdfFile.getAbsolutePath(),3,3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				if (StringUtils.isNotBlank(isbnStr)) {
					try {
						isbnStr = isbnStr.replaceAll("　", "");
						//全角转半角
						isbnStr = CharacterUtils.ToDBC(isbnStr);
						//过滤掉非UTF-8字符方法
						isbnStr = CharacterUtils.filterOffUtf8Mb4(isbnStr);
						//过滤特殊字符
						isbnStr = CharacterUtils.stringFilter(isbnStr);
						//获取ISBN
						isbnStr = getISBNToChar(isbnStr);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					
					if (StringUtils.isNotBlank(isbnStr)) {
						map.put("isbn", isbnStr);
					}
					
					try {
						ISBNChecker good = new ISBNChecker(isbnStr);
						map.put("isbn", isbnStr);
					} catch (Exception e) {
						logger.info("--------pdf读取ISBN出错："+e.getMessage());
					}
				}
				
			}
		}
		
		maperror.put("maperror", sb.toString());
		list.add(map);
		list.add(maperror);
		logger.info("【BatchImportResService】 解析XML文件 savefile->>>解析完成"+list.get(0).toString());
		
		uploadTaskDetail.setBody(map.toString());
		update(uploadTaskDetail);
		return list;
	}
	
	*/

}
