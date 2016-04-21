package com.brainsoon.statistics.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.support.GlobalDataCacheMap;
import com.brainsoon.resrelease.po.ResReleaseDetail;
import com.brainsoon.statistics.po.ResqsOfRelease;
import com.brainsoon.statistics.po.vo.ResultList;
import com.brainsoon.statistics.service.IReleaseNumService;
import com.brainsoon.statistics.service.impl.ReleaseNumService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.taskprocess.model.TaskResRelation;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ReleaseNumAction extends BaseAction {
	/** 默认命名空间 **/
	private final String baseUrl = "/statistics/releaseNum/";
	// 一次查询查询出所有的记录集，做内存缓存
	private static List<ResqsOfRelease> allRecords = new ArrayList<ResqsOfRelease>();
	private static int total = 0;
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	@Autowired
	private IReleaseNumService releaseNumService;

	@RequestMapping(baseUrl + "gotoMain")
	public String gotoMain(Model model) {
		return "/statistics/releaseNum/main";
	}

	/**
	 * 列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@RequestMapping(baseUrl + "list")
	@ResponseBody
	public PageResultForTNum list(HttpServletRequest request, HttpServletResponse response,Model model) throws JsonParseException, JsonMappingException, IOException {
		logger.info("进入查询方法");
		PageResultForTNum pageResult = new PageResultForTNum();
		String fileType = (String)request.getParameter("fileType");
		String channelName = (String)request.getParameter("channelName");
		if(StringUtils.isNotBlank(channelName)){
			channelName = URLDecoder.decode(channelName, "utf-8");
		}
		String posttype = (String)request.getParameter("posttype");
		String filingDate_StartTime = (String)request.getParameter("filingDate_StartTime");
		String filingDate_EndTime = (String)request.getParameter("filingDate_EndTime");
		Map<String,String> paramsMap = new HashMap<String,String>();
		paramsMap.put("fileType", fileType);
		paramsMap.put("channelName", channelName);
		paramsMap.put("posttype", posttype);
		paramsMap.put("filingDate_StartTime", filingDate_StartTime);
		paramsMap.put("filingDate_EndTime", filingDate_EndTime);
		ResultList resultList = releaseNumService.doStatistic(paramsMap);
		allRecords = resultList.getList();
		if (allRecords != null) {
			total = allRecords.size();
			pageResult.setStatisticsNum(resultList.getTotalSum());
		}
		int pageSize = StringUtil.obj2Int(request.getParameter("rows"));
		int pageNo = StringUtil.obj2Int(request.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<ResqsOfRelease> onePage = new ArrayList<ResqsOfRelease>();
		for (int i = 0; i < pageSize; i++) {
			if (startIndex < total && total != 0) {
				ResqsOfRelease vo = allRecords.get(startIndex++);
				if (vo != null) {
					String posttypes = vo.getPosttype();
					String map = GlobalDataCacheMap.getNameValueWithNameByKeyAndChildKey("XqdZtk", posttypes);
					vo.setPosttypeDesc(map);
					onePage.add(vo);
				} else {
					break;
				}
			}
		}
		pageResult.setTotal(total);
		pageResult.setRows(onePage);
		return pageResult;
	}
	@RequestMapping(baseUrl + "chart")
	@ResponseBody
	public String chart(HttpServletRequest request,HttpServletResponse response){
		JSONArray array = new JSONArray();
		for (int i = 0; i < allRecords.size(); i++) {
			//channelName
			String names = allRecords.get(i).getChannelName();
			Integer counts = allRecords.get(i).getCountNum();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("names", names);
			jsonObject.put("counts", counts);
			array.add(jsonObject);
		}
		return array.toString();
	}
	
	@RequestMapping(value = baseUrl + "exportRes")
	public ResponseEntity<byte[]> exportRes(@RequestParam("ids") String ids) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("资源发布情况统计.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		List datas = new ArrayList();
		if (StringUtils.isNotBlank(ids)) {
			String[] idArr = StringUtils.split(ids, ",");
			if (allRecords != null && total >= idArr.length) {
				for (String id : idArr) {
					for(int i=0;i<allRecords.size();i++){
						Long releaseId = allRecords.get(i).getId();
						if(releaseId==Long.parseLong(id)){
							datas.add(allRecords.get(i));
							SysOperateLogUtils.addLog("release_exportRes",allRecords.get(i).getPosttypeDesc(), userInfo);
						}
					}
					
				}
			}
		} else {
			for(int i=0;i<allRecords.size();i++){
				datas.add(allRecords.get(i));
				
			}
		}
		
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(releaseNumService.exportRes(datas)), headers, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = baseUrl + "querySourceType")
	public @ResponseBody String querySourceType(HttpServletRequest request,HttpServletResponse response) throws IOException {
		List<ResReleaseDetail> sourceTypeList = releaseNumService.doSoutceTypeList();
		JSONArray array = new JSONArray();
		for(ResReleaseDetail resReleaseDetail : sourceTypeList){
			JSONObject json = new JSONObject();
			json.put("type",resReleaseDetail.getFileType());
			array.add(json);
		}
		return array.toString();
	}
}
