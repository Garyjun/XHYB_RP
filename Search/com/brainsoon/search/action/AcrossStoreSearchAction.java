package com.brainsoon.search.action;



import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ExcelUtil;
import com.brainsoon.search.service.IAcrossStoreSearchService;
import com.brainsoon.semantic.ontology.model.Ca;
import com.brainsoon.semantic.ontology.model.CaList;
import com.brainsoon.semantic.ontology.model.SearchParamCa;
import com.brainsoon.semantic.ontology.model.SearchResultCa;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.google.gson.Gson;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AcrossStoreSearchAction extends BaseAction {

	/**默认命名空间**/
	private static final String baseUrl = "/acrossStoreSearch/";
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	@Autowired
	private IAcrossStoreSearchService acrossStoreSearchService;
	
	@Autowired
	private ISysParameterService sysParameterService;
	
	@Autowired
	private IResourceService resourceService;
	
	private final static String PUBLISH_DETAILLIST_URL = WebappConfigUtil.getParameter("PUBLISH_DETAILLIST_URL");
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
		String metadataMap = (String)request.getParameter("params");
		String page = (String)request.getParameter("page");
		String size = (String)request.getParameter("rows");
		try {
			if(StringUtils.isNotBlank(metadataMap)){
				metadataMap = URLEncoder.encode(metadataMap, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		}
		String formList = acrossStoreSearchService.queryFormList(metadataMap,page,size);
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
	@RequestMapping(baseUrl+ "getExportExcel")
	@ResponseBody
	public String getExportExcel(HttpServletRequest request,HttpServletResponse response){
		
		String page = "";       //记录输入的起始页
		String endPage = "";    //记录输入的结束页
		String size = "";	 	//记录页面上每页显示的数据数
		String level = "";		//记录选中的导出级别
		String metadataMap = "";  //记录页面输入的查询的参数
		String ids = "";			//记录按选中数据的id
		
		int resSize = 0;			//记录数据字典中定义的允许上传的最大数
		int resNumSize = 0;
		String num = "";            //要导出数据的总数
		String formList = "";      //根据ids查询返回的结果集
		
		File excelFile = null;		
		String returnPath = "";		//记录导出元数据生成的excel的路径
		boolean check = false;
		String searchParamCa = request.getParameter("searchParamCa");
		
		//获取页面传过来的参数，通过gson注入到SearchParamCa对应的属性中
		List<Ca> cas = new ArrayList<Ca>();
		Gson gson = new Gson();
		try{
			SearchParamCa spc = gson.fromJson(searchParamCa, SearchParamCa.class);
			if(spc != null){
				page = spc.getPage();
				endPage = spc.getPage1();
				size = spc.getSize()+"";
				level = spc.getLevel();
				metadataMap = spc.getMetadataMap();
				ids = spc.getIds();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//获取数据字典定义的导出数量的最大值
		try{
			List<SysParameter> sr = sysParameterService.findParaValue("resDownloadSize");
			if(sr !=null && sr.size()>0){
				if(sr.get(0) != null && StringUtils.isNotBlank(sr.get(0).getParaValue())){
					resSize = Integer.valueOf((sr.get(0).getParaValue()));
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//按照输入的页数导出方式，计算共导出多少条记录
		if(StringUtils.isNotBlank(page) && StringUtils.isNotBlank(endPage)){
			num = ((Integer.parseInt(endPage) - Integer.parseInt(page) + 1)*Integer.parseInt(size)) +"";
			if(resSize >= Integer.parseInt(num)){
				formList = acrossStoreSearchService.queryFormList(metadataMap,page,num);
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
				try{
					SearchParamCa searchParamCaIds = new SearchParamCa();
					HttpClientUtil http = new HttpClientUtil();
					searchParamCaIds.setIds(ids);
					ids = gson.toJson(searchParamCaIds);
					String resource = http.postJson(PUBLISH_DETAILLIST_URL, ids);
					CaList caList = gson.fromJson(resource, CaList.class);
					cas = caList.getCas();
					check = true;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		
		//判断是否允许导出,生成要导出的元数据的excel，并返回地址
		if(check){
			try {
				excelFile = ExcelUtil.createExcelByRes(cas,level,"");
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
					String encryptZip =UUID.randomUUID()+"/";
					isOk = acrossStoreSearchService.createFtpDownload(ids, encryptPwd, ftpFlag, encryptZip,isComplete);
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
	@RequestMapping(baseUrl+"downloadPageResource")
	@ResponseBody
	public String downloadPageResource(HttpServletRequest request,HttpServletResponse response){
		String gotoFtpHttp = request.getParameter("gotoFtpHttp");
		String startPage = "";      //  导出文件的起始页
		String endPage = "";		//  导出文件的结束页
		String pageSize = "";		//	每页显示的记录的条数
		String result = "";			// 返回资源结果集的字符串
		String encryptPwd = "";		//	加密密匙
		String isComplete ="";		//	是否进行加密
		String data = "";			//	最后返回值，供前台判断
		String metadataMap = "";    //记录页面输入的查询的参数
		List<Ca> cas = new ArrayList<Ca>();
		String flag = request.getParameter("ftpFlag");
		
		String encryptZip = UUID.randomUUID()+"/";
		Gson gson = new Gson();
		if(StringUtils.isNotBlank(gotoFtpHttp)){
			try{
				SearchParamCa spc = gson.fromJson(gotoFtpHttp, SearchParamCa.class);
				if(spc != null){
					startPage = spc.getPage();
					endPage = spc.getPage1();
					pageSize = spc.getSize()+"";
					encryptPwd = spc.getEncryptPwd();
					isComplete = spc.getIsComplete();
					metadataMap = spc.getMetadataMap();
				}
				
				//计算共导出多少条记录
				Long num = (long) ((Integer.parseInt(endPage) - Integer.parseInt(startPage)+1)*Integer.parseInt(pageSize));
				result = acrossStoreSearchService.queryFormList(metadataMap,startPage,num.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(StringUtils.isNotBlank(result)){
				SearchResultCa caList = gson.fromJson(result, SearchResultCa.class);
				cas = caList.getRows();
				data = acrossStoreSearchService.createByPageFtpDownload(cas, flag, encryptZip, encryptPwd, isComplete);
			}
		}
		return data;
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
