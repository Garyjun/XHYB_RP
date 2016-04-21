package com.brainsoon.search.action;



import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ExcelUtil;
import com.brainsoon.search.service.ISearchIndexService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.IFLTXService;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.service.IZTFLService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;
import com.google.gson.Gson;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SearchIndexAction extends BaseAction {

	/**默认命名空间**/
	private static final String baseUrl = "/search/";
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	@Autowired
	private ISearchIndexService searchIndexService;
	@Autowired
	private ISysParameterService sysParameterService;
	
	@Autowired
	private IPublishResService publishResService;
	@Autowired
	private IResourceService resourceService;
	
	@Autowired
	private IZTFLService zTFLService;
	
	@Autowired
	private IFLTXService fLTXService;
	
	private final static String PUBLISH_DETAILLIST_URL = WebappConfigUtil.getParameter("PUBLISH_DETAILLIST_URL");
	
	@RequestMapping(baseUrl+"gotoSearchIndex")
	public String gotoMain(HttpServletRequest request,HttpServletResponse response,Model model) {
		String publishType = request.getParameter("publishType");
		if(StringUtils.isNotBlank(publishType)){
			request.setAttribute("publishType", publishType);
		}
		return baseUrl + "searchIndex";
	}
	
	@RequestMapping(baseUrl+"initQueryColumn")
	@ResponseBody
	public String initQueryColumn(HttpServletRequest request,HttpServletResponse response){
		String publishType = request.getParameter("publishType");
		UserInfo user = LoginUserUtil.getLoginUser();
		List<CustomMetaData>  customMetaDatas = MetadataSupport.getAllMetadateList(user,publishType);
		String queryColumn = "[";
		for(CustomMetaData customMetaData:customMetaDatas){
			List<MetadataDefinition> metadataDefinitions = customMetaData.getCustomPropertys();
			for(MetadataDefinition metadataDefinition:metadataDefinitions){
				String viewPriority = metadataDefinition.getViewPriority();
				if(StringUtils.isNotBlank(viewPriority) && viewPriority.contains("2")){
					queryColumn +="{field:'metadataMap."+metadataDefinition.getFieldName()+"',title:'"+metadataDefinition.getFieldZhName()+"',width:fillsize(0.17),align:'center'},";
					//sb.append("{field:'metadataMap.").append(metadataDefinition.getFieldName()).append("',title:'").append(metadataDefinition.getFieldZhName()).append("',width:fillsize(0.17),align:'center'},");
				}
			}
		}
		queryColumn +="{field:'opt1',title:'操作',width:fillsize(0.27),align:'center',formatter:$operate,align:'center'}]";
		return queryColumn;
	}
	
	
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping(baseUrl + "queryFormList")
	@Token(save=true)
	public @ResponseBody String queryFormList(HttpServletRequest request,HttpServletResponse response){
		String queryType = "1";
		String publishType = (String)request.getParameter("publishType");
		String metadataMap = (String)request.getParameter("params");
		String page = (String)request.getParameter("page");
		String size = (String)request.getParameter("rows");
		String objectIds = request.getParameter("objectIds");
		String queryTypeTemp = request.getParameter("queryType");
		if(StringUtils.isNotBlank(queryTypeTemp)){
			queryType = queryTypeTemp;
		}
		try {
			if(StringUtils.isNotBlank(metadataMap)){
				metadataMap = URLEncoder.encode(metadataMap, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		
		//将资源中存放的是id的转换成对应的中文
		boolean hasCatagory = false;	//是否有在列表页展示为资源存放的id而需要转换成中文的
		List<MetadataDefinition> metadataDefinitions = MetadataSupport.getAllMetadataDefinitionByResType(publishType);
		Map<String,String> categoryFields = new HashMap<String,String>();  //存放需要转换的元数据项
		Map<String,String> dateType = new HashMap<String,String>();
		
		for(MetadataDefinition metadataDefinition:metadataDefinitions){
			 int fieldType = metadataDefinition.getFieldType();
			 hasCatagory = true;
			 String viewPriority = metadataDefinition.getViewPriority();
			 if(viewPriority != null){
				 if(fieldType == 6 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 9 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 10 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 11 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
				 }else if(fieldType == 2 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 3 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 4 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", "9");
				 }else if(fieldType == 7 && viewPriority.contains("2")){
					 hasCatagory = true;
					 categoryFields.put(metadataDefinition.getFieldName()+"", fieldType+"");
					 dateType.put(metadataDefinition.getFieldName(), metadataDefinition.getValueRange());
				 }
			 }
		 } 
		
		String formList = searchIndexService.queryFormList(metadataMap,publishType,page,size,queryType,objectIds);
	
	//说明用高级查询的项，有需要转换成对应中文的，所以将查询的结果集重新转换成cas，对ca进行重组
	 if(hasCatagory){
		 logger.info("+++++++++++++++++++++++++++++++++hasCatagory++++++++++++++++++++++++++++++++++++++++++++=");
		 Gson gson=new Gson();
		 SearchResultCa caList = gson.fromJson(formList, SearchResultCa.class);
		 if(caList!=null && !caList.getRows().isEmpty()){
			 List<Ca> cas = caList.getRows();
			 if(cas!=null && cas.size()>0){
				 for(int i=0;i<cas.size();i++){
					 Ca ca = cas.get(i);
					 Map<String,String> metadataMaps = ca.getMetadataMap();
					 boolean hasCategoryName =false;
					 Iterator it = categoryFields.entrySet().iterator();
					 while (it.hasNext()) {
							Map.Entry pairs = (Map.Entry) it.next();
							if(StringUtils.isNotBlank(pairs.getKey().toString())&&StringUtils.isNotBlank(pairs.getValue().toString())){
								if(pairs.getValue().toString().equals("6")){
									 String value = metadataMaps.get(pairs.getKey().toString());
									 if(StringUtils.isNotBlank(value)){
										 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)1++++++++++++++++++++++++++++++++++++++++++++=");
										 String categoryName ="";
										 try {
											 categoryName = fLTXService.queryCatagoryCnName(value);
											 metadataMaps.put(pairs.getKey().toString(), categoryName);
										} catch (Exception e) {
											metadataMaps.put(pairs.getKey().toString(), value);
										}
										 hasCategoryName = true;
									 }
								}else if(pairs.getValue().toString().equals("9")){
									 String value = metadataMaps.get(pairs.getKey().toString());
									 if(StringUtils.isNotBlank(value)){
										 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
										 String categoryName = "";
										 try {
											 categoryName = zTFLService.queryDictValue(value);
											 metadataMaps.put(pairs.getKey().toString(), categoryName);
										} catch (Exception e) {
											 metadataMaps.put(pairs.getKey().toString(), value);
										}
										 hasCategoryName = true;
									 }
								}else if(pairs.getValue().toString().equals("10")){
									 String value = metadataMaps.get(pairs.getKey().toString());
									 if(StringUtils.isNotBlank(value)){
										 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
										 String categoryName = "";
										 try {
											 String arrayStaff[] = value.split(",");
											 for(String sta:arrayStaff){
												 categoryName = categoryName + GlobalDataCacheMap.getNameStaffWithNameByKeyAndChildKey(sta+"staff",value)+",";
											 }
											 if(categoryName.endsWith(",")){
												 categoryName = categoryName.substring(0,categoryName.length()-1);
											 }
											 metadataMaps.put(pairs.getKey().toString(), categoryName);
										} catch (Exception e) {
											 metadataMaps.put(pairs.getKey().toString(), value);
										}
										 hasCategoryName = true;
									 }
								}else if(pairs.getValue().toString().equals("11")){
									 String value = metadataMaps.get(pairs.getKey().toString());
									 if(StringUtils.isNotBlank(value)){
										 logger.info("+++++++++++++++++++++++++++++++++zTFLService.queryCatagoryCnName(value)2++++++++++++++++++++++++++++++++++++++++++++=");
										 String categoryName = "";
										 try {
											 String arrayCompany[] = value.split(",");
											 for(String com:arrayCompany){
												 categoryName = categoryName + GlobalDataCacheMap.getNameCompanyWithNameByKeyAndChildKey(com+"company",value)+",";
											 }
											 if(categoryName.endsWith(",")){
												 categoryName = categoryName.substring(0,categoryName.length()-1);
											 }
											 metadataMaps.put(pairs.getKey().toString(), categoryName);
										} catch (Exception e) {
											 metadataMaps.put(pairs.getKey().toString(), value);
										}
										 hasCategoryName = true;
									 }
								}else if(pairs.getValue().toString().equals("7")){
										String value = metadataMaps.get(pairs.getKey().toString());
									try{
										 String format = dateType.get(pairs.getKey().toString());
										 if (StringUtils.isNotBlank(value)) {
											Date dateOld = new Date(Long.parseLong(value)); // 根据long类型的毫秒数生命一个date类型的时间
											value = new SimpleDateFormat(format).format(dateOld);
											metadataMaps.put(pairs.getKey().toString(), value);
										 }
									}catch(Exception e){
										metadataMaps.put(pairs.getKey().toString(), value);
										e.printStackTrace();
									}
								}
							}
						}
						 if(hasCategoryName){
							 ca.setMetadataMap(metadataMaps);
							 cas.set(i, ca);
						 }
				 }
			 }
		 }
		 formList = gson.toJson(caList);
	 }
		
		return formList;
	}
	/**
	 * 执行查询操作
	 * @param request
	 * @param response 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException
	 * 2015年12月2日，线上添加资源的状态为3（通过）才能查询到 
	 */
	@RequestMapping(baseUrl + "queryFormLists")
	@Token(save=true)
	public @ResponseBody String queryFormLists(HttpServletRequest request,HttpServletResponse response){
		String publishType = (String)request.getParameter("publishType");
		String metadataMap = (String)request.getParameter("params");
		String page = (String)request.getParameter("page");
		String size = (String)request.getParameter("rows");
		String objectIds = request.getParameter("objectIds");
		try {
			if(StringUtils.isNotBlank(metadataMap)){
				metadataMap = URLEncoder.encode(metadataMap, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		String formList = searchIndexService.queryFormLists(metadataMap,publishType,page,size,"1",objectIds);
		return formList;
	}
	
	
	/**
	 * fengda  2015年11月5日
	 * 批量导出元数据
	 * 分为两种方式导出
	 * 1.按照输入的页数，计算共导出多少条记录
	 * 2.直接选中要导出的数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "getExportExcel")
	@ResponseBody
	public String getExportExcel(HttpServletRequest request,HttpServletResponse response){
		String queryType = "1";
		String metadataMap = "";    //记录页面输入的查询的参数
		String objectIds = ""; 
		String publishType = "";    //记录资源的类型
		String page1 = "";			//记录输入的结束页
		String level = "";			//记录选中的导出级别
		String page = "";			//记录输入的起始页
		String size = "";			//记录页面上每页显示的数据数
		String formList = "";		//记录按输入页数查询的结果
		String ids = "";			//记录按选中数据的id
		long resSize = 0L;			//记录数据字典中定义的允许上传的最大数
		long resNumSize = 0L;		//记录按选中导出指定数据的个数
		boolean check = false;		//标记是否导出成功
		File excelFile = null;		
		String returnPath = "";		//记录导出元数据生成的excel的路径
		
		//获取页面传过来的参数，通过gson注入到SearchParamCa对应的属性中
		Gson gson = new Gson();
		List<Ca> cas = new ArrayList<Ca>();
		try{
			String searchParamCa = request.getParameter("searchParamCa");
			SearchParamCa spc = gson.fromJson(searchParamCa, SearchParamCa.class);
			if(spc!=null){
				page = spc.getPage();
				page1 = spc.getPage1();
				size = spc.getSize()+"";
				publishType = spc.getPublishType();
				level = spc.getLevel();
				metadataMap = spc.getMetadataMap();
				ids = spc.getIds();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//获取数据字典定义的导出数量的最大值
		List<SysParameter> sr = sysParameterService.findParaValue("resDownloadSize");
		if(sr !=null && sr.size()>0){
			if(sr.get(0) != null && StringUtils.isNotBlank(sr.get(0).getParaValue())){
				resSize = Long.parseLong(sr.get(0).getParaValue());
			}
		}
		
		//按照输入的页数导出方式，计算共导出多少条记录
		if(StringUtils.isNotBlank(page) && StringUtils.isNotBlank(page1)){
		    Long num = (long) ((Integer.parseInt(page1) - Integer.parseInt(page)+1)*Integer.parseInt(size));
			size = num.toString();
			if(resSize >= num){
				try{
					formList = searchIndexService.queryFormList(metadataMap,publishType,page,size,queryType,objectIds);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(StringUtils.isNotBlank(formList)){
				try{
					SearchResultCa caList = gson.fromJson(formList, SearchResultCa.class);
					cas = caList.getRows();
					check = true;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}else{
			//直接选中要导出的数据的到厨方式，获取选中的资源的id.
			
			resNumSize = ids.split(",").length;
			if(resSize >= resNumSize){
				SearchParamCa searchParamCaIds = new SearchParamCa();
				HttpClientUtil http = new HttpClientUtil();
				searchParamCaIds.setIds(ids);
				ids = gson.toJson(searchParamCaIds);
				String resource = http.postJson(PUBLISH_DETAILLIST_URL, ids);
				CaList caList = gson.fromJson(resource, CaList.class);
				cas = caList.getCas();
				check = true;
			}
		}
		
		
		//判断是否允许导出,生成要导出的元数据的excel，并返回地址
		if(check){
			try {
				excelFile = ExcelUtil.createExcelByRes(cas,level,publishType);
				returnPath = excelFile.getAbsolutePath();
				SysOperateLogUtils.addLog("batch_exportRes","批量检索导出元数据", userInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return returnPath;
	}
	
	
	/**
	 * fengda 2015年11月5日
	 * 根据生成的excel的路径，实现在浏览器上下载
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(baseUrl + "getExportExcelDown")
	@ResponseBody
	public ResponseEntity<byte[]> getExportExcelDown(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String excelFilePath = request.getParameter("excelFilePath");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("元数据导出.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		File excelFile = new File(excelFilePath);
		return new ResponseEntity<byte[]>(
				FileUtils.readFileToByteArray(excelFile), headers,
				HttpStatus.OK);
	}
	
	
	/**
	 * fengda 2015年11月9日
	 * ftp下载等待资源对应的文件
	 * 按照选中指定的资源根据资源id下载对应资源下的文件
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl+"downloadPublishRes")
	@ResponseBody
	public String downloadPublishRes(HttpServletRequest request,HttpServletResponse response){
		String gotoFtpHttp = request.getParameter("gotoFtpHttp");
		String ids = "";       //选中的资源id
		String encryptPwd = "";//加密密匙
		String ftpFlag = "";   //下载方式1.http下载 2.ftp下载但此处只支持ftp下载
		String isComplete = ""; //是否压缩  1.是 2.不是
		String isOk = "";      
		if(StringUtils.isNotBlank(gotoFtpHttp)){
			Gson gson = new Gson();
			try{
				SearchParamCa spc = gson.fromJson(gotoFtpHttp, SearchParamCa.class);
				if(spc != null){
					ids = spc.getIds();
					encryptPwd = spc.getEncryptPwd();
					ftpFlag = spc.getFtpFlag();
					isComplete = spc.getIsComplete();
					Date date = new Date();
					String time =  DateUtil.convertDateToString("yyyyMMddHHmmss",date);
					String encryptZip =time + "/";
					
					//isOk = searchIndexService.createFtpDownload(ids, encryptPwd, ftpFlag, encryptZip,isComplete);
					isOk = publishResService.createHttpFtpDownload(ids, encryptPwd, ftpFlag, encryptZip,isComplete);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return isOk;
	}
	
	
	
	
	/**
	 * 按照分页导出资源文件
	 * fengda 2015年11月9日
	 * @param request
	 * @param response
	 * gotoFtpHttp   页面的查询条件
	 * ftpFlag ：2  标记为ftp下载
	 * @return
	 */
	@RequestMapping(value=baseUrl+"downloadPageResource",method = {RequestMethod.POST})
	@ResponseBody
	public String downloadPageResource(HttpServletRequest request,HttpServletResponse response){
		String gotoFtpHttp = request.getParameter("gotoFtpHttp");
		String startPage = "";      //  导出文件的起始页
		String endPage = "";		//  导出文件的结束页
		String pageSize = "";		//	每页显示的记录的条数
		String targetNames = "";	//  此方法中没有作用，但因为service是公用的
		String result = "";			//  返回资源结果集的字符串
		String publishType = "";	//	资源类型
		String encryptPwd = "";		//	加密密匙
		String isComplete ="";		//	是否进行加密
		String data = "";			//	最后返回值，供前台判断
		String metadataMap = "";    //	记录页面输入的查询的参数
		String queryType = "1";
		String objectIds = null; 
		List<Ca> cas = new ArrayList<Ca>();
		String isOk = "";
		//String encryptZip = UUID.randomUUID()+"/";
		Date date = new Date();
		String time =  DateUtil.convertDateToString("yyyyMMddHHmmss",date);
		Gson gson = new Gson();
		if(StringUtils.isNotBlank(gotoFtpHttp)){
			try{
				String encryptZip =time + "/";
				SearchParamCa spc = gson.fromJson(gotoFtpHttp, SearchParamCa.class);
				if(spc != null){
					startPage = spc.getPage();
					endPage = spc.getPage1();
					pageSize = spc.getSize()+"";
					encryptPwd = spc.getEncryptPwd();
					isComplete = spc.getIsComplete();
					metadataMap = spc.getMetadataMap();
					publishType = spc.getPublishType();
				}
				String flag = request.getParameter("ftpFlag");
				//计算共导出多少条记录
				Long num = (long) ((Integer.parseInt(endPage) - Integer.parseInt(startPage)+1)*Integer.parseInt(pageSize));
				
				//返回要导出的资源列表转换成String类型
				result= searchIndexService.queryFormList(metadataMap,publishType,startPage,num.toString(),queryType,objectIds);
				
				if(StringUtils.isNotBlank(result)){
					try{
						SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
						cas = caList.getRows();
						String ids = "";
						for (Ca ca : cas) {
							ids += ca.getObjectId()+",";
						}
						if(StringUtils.isNotBlank(ids)){
							ids = ids.substring(0, ids.length()-1);
							isOk = publishResService.createHttpFtpDownload(ids, encryptPwd, flag, encryptZip,isComplete);
						}
						//cas = caList.getRows();
						//data = searchIndexService.createByPageFtpDownload(cas, flag, encryptZip, encryptPwd, isComplete);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return isOk;
	}
	
	
	/**
	 * 下载资源
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping(baseUrl + "downloadFile")
	public  void downloadFile(HttpServletRequest request,HttpServletResponse response){
		String zipName = request.getParameter("zipName");
		try {
			zipName=  URLDecoder.decode(zipName,"UTF-8");
			resourceService.downloadFile(request, response, zipName,
					false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
