package com.brainsoon.search.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.bsrcm.search.po.AdvancedSearchHistory;
import com.brainsoon.common.util.HttpClientUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.search.service.ISearchService;
import com.brainsoon.system.support.SystemConstants.ResourceType;

@Controller
@RequestMapping("/search/")
public class SearchAction extends BaseAction {

	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;

	@Autowired
	private ISearchService searchService;

	//缓存查询条件，注意该action一定要是单例的
	private String queryCondition;
	
	@RequestMapping("gotoMain")
	public String gotoMain(Model model) {
		model.addAttribute("resTypeMap",ResourceType.map.getEntryMap());
		String fileRoot = WebAppUtils.getWebRootRelDir(ConstantsDef.fileRoot);
		String fileRoot1 = fileRoot.replaceAll("\\\\", "/");
		//fileRoot = fileRoot1.substring(0,fileRoot1.lastIndexOf("/"));
		model.addAttribute("fileRoot",fileRoot1);
		return "/search/search";
	}
	
	@RequestMapping("list")
	public void list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String hql = baseSemanticSerivce.parseCondition(request, conditionList);
		outputResult(baseSemanticSerivce.query4Page(hql, "advance_search"));
		//缓存sql，把page替换为-1
		String page = request.getParameter("page");
		queryCondition = StringUtils.replace(hql, "page="+page, "page=-1");
	}

	@RequestMapping("getDic")
	@ResponseBody
	public String getAdvaceSearchQueryConditionDic(
			@RequestParam(value = "eduPhase", required = false) String eduPhase, 
			@RequestParam(value = "subject", required = false) String subject,
			//@RequestParam(value = "grade", required = false) String grade, 
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "xPath", required = false) String xPath) {
		return searchService.getAdvaceSearchQueryConditionDic(eduPhase, subject, type,xPath);
	}
	/**
	 * 准备导出数据
	 * @param request
	 * @param response
	 */
	@RequestMapping("initExportRes")
	public void initExportRes(HttpServletRequest request, HttpServletResponse response,@RequestParam("downType") String downType) {
		String conditionStr = "page=1&size=1000000"+StringUtils.substringAfter(queryCondition, "page=-1&size=10");
		File resFile = searchService.exportRes(baseSemanticSerivce.getAllMetaDataDC(conditionStr, downType), downType);
		outputResult(resFile.getAbsolutePath());
	}
	/**
	 * 导出资源
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("exportRes")
    public ResponseEntity<byte[]> exportRes(HttpServletRequest request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filePath = request.getParameter("filePath");
        String filename = URLEncoder.encode("导出数据."+StringUtils.substringAfterLast(filePath, "."), "UTF-8");
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)),headers, HttpStatus.CREATED);
    }
    
    @RequestMapping("getAllDefineMetaData")
    @ResponseBody
    public String getAllDefineMetaData(@RequestParam(value = "resType") String resType, @RequestParam(value = "flag") String flag){
    	String metaDatas = searchService.getAllDefineMetaData(resType, flag);
    	return metaDatas;
    }
    
    /**
     * 为保证其他逻辑不变，为用户权限查询元数据单独写的调用方法
     * 
     * 
     * */
    @RequestMapping("getDefineMetaDataByResType")
    @ResponseBody
    public String getDefineMetaDataByResType(@RequestParam(value = "resType") String resType){
    	String metaDatas = searchService.getDefineMetaDataByResType(resType);
    	return metaDatas;
    }
    
    @RequestMapping("getMetaDataValueByEnName")
    @ResponseBody
    public String getMetaDataValueByEnName(String enName){
    	String value = searchService.getMetaDataValueByEnName(enName);
    	return value;
    }
    
    @RequestMapping("listQueryHistory")
    @ResponseBody
    public PageResult listQueryHistory(HttpServletRequest request, HttpServletResponse response){
    	logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String id = request.getParameter("id");
		if(StringUtils.isNotBlank(id)){
			QueryConditionItem item = new QueryConditionItem("id", Operator.EQUAL, Long.valueOf(id));
			conditionList.addCondition(item);
		}
    	PageResult pageResult = searchService.query4Page(AdvancedSearchHistory.class, conditionList);
    	return pageResult;
    }
    
    @RequestMapping("saveQueryHistory")
    public void saveQueryHistory(@RequestParam("querySql") String querySql){
    	AdvancedSearchHistory history = new AdvancedSearchHistory();
    	history.setQuerySql(querySql);
    	Date date = new Date();
    	history.setCreateTime(date);
    	history.setDescription("该条查询记录创建于" + DateUtil.convertDateTimeToString(date));
    	searchService.saveOrUpdate(history);
    }
    
    @RequestMapping("showFLTX")
    public String showFLTX(){
    	return "/search/ztfl";
    }
    
    @RequestMapping("advSearch")
    @ResponseBody
    public String advSearch(@RequestParam("conditionParam") String conditionParam){
    	HttpClientUtil http = new HttpClientUtil();
    	String baseUrl = WebappConfigUtil.getParameter("PUBLISH_QUERY_URL");
    	String httpUrl = "";
    	try {
			httpUrl = baseUrl + "?queryType=1&page=1&size=20&metadataMap=" + URLEncoder.encode(conditionParam, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	String result = http.executeGet(httpUrl);
    	return result;
    }
}
