package com.brainsoon.statistics.action;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.statistics.po.PubResqsOfRelease;
import com.brainsoon.statistics.po.ResqsOfRelease;
import com.brainsoon.statistics.po.vo.ResultList;
import com.brainsoon.statistics.service.IPubReleaseNumService;
import com.brainsoon.statistics.service.IReleaseNumService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PubReleaseNumAction extends BaseAction {
	/** 默认命名空间 **/
	private final String baseUrl = "/statistics/pubReleaseNum/";
	// 一次查询查询出所有的记录集，做内存缓存
	private static List<PubResqsOfRelease> allRecords = new ArrayList<PubResqsOfRelease>();
	private static int total = 0;
	UserInfo userInfo =  LoginUserUtil.getLoginUser();
	@Autowired
	private IPubReleaseNumService pubReleaseNumService;

	@RequestMapping(baseUrl + "gotoMain")
	public String gotoMain(Model model) {
		return "/statistics/pubReleaseNum/main";
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
	public PageResultForTNum list(HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
		logger.info("进入查询方法");
		PageResultForTNum pageResult = new PageResultForTNum();
		Map<String, String> paramsMap = getQueryParams(request);
		if (StringUtils.equals(paramsMap.get("operate"), "requery")) {
			ResultList resultList = pubReleaseNumService.doStatistic(paramsMap);
			allRecords = resultList.getList();
			if (allRecords != null) {
				total = allRecords.size();
				pageResult.setStatisticsNum(resultList.getTotalSum());
			}
		}
		int pageSize = StringUtil.obj2Int(request.getParameter("rows"));
		int pageNo = StringUtil.obj2Int(request.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<PubResqsOfRelease> onePage = new ArrayList<PubResqsOfRelease>();

		for (int i = 0; i < pageSize; i++) {
			if (startIndex < total && total != 0) {
				PubResqsOfRelease vo = allRecords.get(startIndex++);
				if (vo != null) {
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

	/**
	 * @param request
	 */
	private Map<String,String> getQueryParams(HttpServletRequest request) {
		Map<String, String> paramsMap = new HashMap<String, String>();
		String operate = request.getParameter("operate_myfd[]");
		String pubResType = request.getParameter("pubResType_myfd[]");
		String channelName = request.getParameter("channelName_myfd[]");
		String startTime = request.getParameter("filingDate_StartTime_myfd[]");
		String endTime = request.getParameter("filingDate_EndTime_myfd[]");
		if (StringUtils.isNotBlank(operate)) {
			operate = StringUtils.split(operate, ",")[0];
			paramsMap.put("operate", operate);
		}
		if (StringUtils.isNotBlank(pubResType)) {
			pubResType = StringUtils.split(pubResType, ",")[0];
			paramsMap.put("pubResType", pubResType);
		}
		if (StringUtils.isNotBlank(channelName)) {
			channelName = StringUtils.split(channelName, ",")[0];
			paramsMap.put("channelName", channelName);
		}
		if (StringUtils.isNotBlank(startTime)) {
			startTime = StringUtils.split(startTime, ",")[0];
			paramsMap.put("startTime", startTime);
		}
		if (StringUtils.isNotBlank(endTime)) {
			endTime = StringUtils.split(endTime, ",")[0];
			paramsMap.put("endTime", endTime);
		}
		return paramsMap;
	}
	
	
	@RequestMapping(value = baseUrl + "exportRes")
	public ResponseEntity<byte[]> exportRes(@RequestParam("ids") String ids) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("出版资源发布情况统计.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		List datas = new ArrayList();
		if (StringUtils.isNotBlank(ids)) {
			String[] idArr = StringUtils.split(ids, ",");
			if (allRecords != null && total >= idArr.length) {
				for (String id : idArr) {
					datas.add(allRecords.get(Integer.parseInt(id)));
					SysOperateLogUtils.addLog("pub_release_exportRes",allRecords.get(Integer.parseInt(id)).getChannelName(), userInfo);
				}
			}
		} else {
			datas.add(allRecords);
		}
		
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(pubReleaseNumService.exportRes(datas)), headers, HttpStatus.OK);
	}

}
