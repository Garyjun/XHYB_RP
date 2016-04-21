package com.brainsoon.statistics.action;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.statistics.po.ResqsOfPubCollect;
import com.brainsoon.statistics.po.ResqsOfPubCollectResult;
import com.brainsoon.statistics.service.IPubCollectNumService;
import com.brainsoon.system.support.SysOperateLogUtils;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PubCollectNumAction extends BaseAction {
	/** 默认命名空间 **/
	private final String baseUrl = "/statistics/pubCollectNum/";
	// 一次查询查询出所有的记录集，做内存缓存
	private static List<ResqsOfPubCollect> allRecords = new ArrayList<ResqsOfPubCollect>();
	private static Long total = 0L;
	UserInfo userInfo =  LoginUserUtil.getLoginUser();

	@Autowired
	private IPubCollectNumService pubCollectNumService;
	@Autowired
	private IBaseSemanticSerivce baseSemanticSerivce;

	@RequestMapping(baseUrl + "gotoMain")
	public String gotoMain(Model model) {
		return baseUrl + "main";
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
	public @ResponseBody
	PageResultForTNum list(@RequestParam(value = "operate_myfd[]", required = false) String operate, HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException,
			IOException {
		logger.info("进入查询方法");
		PageResultForTNum pageResult = new PageResultForTNum();
		// 重新查询一次
		if(StringUtils.isNotBlank(operate)){
			operate = StringUtils.split(operate,",")[0];
		}
		if (StringUtils.equals(operate, "requery")) {
			QueryConditionList conditionList = getQueryConditionList();
			String hql = baseSemanticSerivce.parseCondition(request, conditionList);
			String resultJson = baseSemanticSerivce.query4Page(hql, "res_collection");
			ObjectMapper objectMapper = new ObjectMapper();
			ResqsOfPubCollectResult result = objectMapper.readValue(resultJson, ResqsOfPubCollectResult.class);
			total = result.getTotal();
			allRecords = result.getRows();
			pageResult.setStatisticsNum(result.getStatisticsNum());
		}

		int pageSize = StringUtil.obj2Int(request.getParameter("rows"));
		int pageNo = StringUtil.obj2Int(request.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;
		List<ResqsOfPubCollect> collections = new ArrayList<ResqsOfPubCollect>();

		for (int i = 0; i < pageSize; i++) {
			if (startIndex < total && total != 0) {
				ResqsOfPubCollect resqsOfPubCollect = allRecords.get(startIndex++);
				if (resqsOfPubCollect != null) {
					collections.add(resqsOfPubCollect);
				} else {
					break;
				}
			}
		}
		pageResult.setTotal(total);
		pageResult.setRows(collections);

		return pageResult;
	}

	/**
	 * 资源模块统计导出下载
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = baseUrl + "exportRes")
	public ResponseEntity<byte[]> exportRes(@RequestParam("ids") String ids) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String filename = URLEncoder.encode("出版资源采集情况统计.xls", "UTF-8");
		headers.setContentDispositionFormData("attachment", filename);
		List datas = new ArrayList();
		if (StringUtils.isNotBlank(ids)) {
			String[] idArr = StringUtils.split(ids,",");
			if(allRecords!=null&&total>=idArr.length){
				for (String id : idArr) {
					datas.add(allRecords.get(Integer.parseInt(id)));
					SysOperateLogUtils.addLog("pub_collect_exportRes",allRecords.get(Integer.parseInt(id)).getSource(), userInfo);
				}
			}
		} else {
			datas.add(allRecords);
		}
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(pubCollectNumService.exportRes(datas)), headers, HttpStatus.OK);
	}

}
