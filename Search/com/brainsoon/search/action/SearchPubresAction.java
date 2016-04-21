package com.brainsoon.search.action;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

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
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.util.WebappConfigUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IPublishResService;
import com.brainsoon.search.service.ISearchPubresService;
import com.brainsoon.system.support.SystemConstants.ResourceType;

@Controller
@RequestMapping("/search/pubres")
public class SearchPubresAction extends BaseAction {

	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;
	@Autowired
	private ISearchPubresService searchPubresService;
	@Autowired
	private IPublishResService publishResService;
	// 缓存查询条件，注意该action一定要是单例的
	private String queryCondition;

	@RequestMapping("gotoMain")
	public String gotoMain(Model model) {
		model.addAttribute("resTypeMap", ResourceType.map.getEntryMap());
		model.addAttribute("fileRoot", WebAppUtils.getWebRootRelDir(ConstantsDef.fileRoot));
		return "/search/pubres/searchIndex";
	}

	@RequestMapping("list")
	public void list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		String hql = baseSemanticSerivce.parseCondition(request, conditionList);
		outputResult(baseSemanticSerivce.query4PageByPubRes(hql, WebappConfigUtil.getParameter("PUBLISH_QUERY_URL")));
		String page = request.getParameter("page");
		queryCondition = StringUtils.replace(hql, "page=" + page, "page=-1");
	}

	@RequestMapping("getDic")
	@ResponseBody
	public String getAdvaceSearchQueryConditionDic(@RequestParam(value = "eduPhase", required = false) String eduPhase, @RequestParam(value = "subject", required = false) String subject,
			@RequestParam(value = "type", required = false) String type) {
		return searchPubresService.getAdvaceSearchQueryConditionDic(eduPhase, subject, type);
	}

	/**
	 * 准备导出数据
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("initExportRes")
	public void initExportRes(HttpServletRequest request, HttpServletResponse response) {
		File resFile = searchPubresService.exportRes(queryCondition);
		outputResult(resFile.getAbsolutePath());
	}

	/**
	 * 导出资源
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("exportRes")
	public ResponseEntity<byte[]> exportRes(HttpServletRequest request) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filePath = request.getParameter("filePath");
		String filename = URLEncoder.encode("资源下载." + StringUtils.substringAfterLast(filePath, "."), "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(filePath)), headers, HttpStatus.CREATED);
	}
}
