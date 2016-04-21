package com.brainsoon.statistics.action;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.brainsoon.appframe.support.PageResultForTNum;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.statistics.po.ResqsOfModule;
import com.brainsoon.statistics.service.IModuleNumService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.ResourceCaType;
import com.brainsoon.system.support.SystemConstants.ResourceType;

@Controller
public class ModuleNumAction extends BaseAction{
	/** 默认命名空间 **/
	private final String baseUrl = "/statistics/moduleNum/";
	
	@Autowired
	private IModuleNumService moduleNumService;
	
	private List<ResqsOfModule> resqsOfModuleList = new ArrayList<ResqsOfModule>();
	
	
	@RequestMapping(baseUrl+"goMain")
	public String gotoMain(Model model) {
		model.addAttribute("resTypeMap",ResourceType.map.getEntryMap());
		model.addAttribute("resCaTypeMap",ResourceCaType.map.getEntryMap());
		return baseUrl + "main";
	}
	/**
	 * 列表查询
	 * 
	 * @param request
	 * @param response
	 * @param params
	 * @return json
	 */
	@RequestMapping(value = baseUrl + "list")
	public @ResponseBody PageResultForTNum list(HttpServletRequest request, HttpServletResponse response) {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		PageResultForTNum results = moduleNumService.queryForPage(ResqsOfModule.class, conditionList,Integer.parseInt(request.getParameter("queryType")));
		resqsOfModuleList.clear();
		resqsOfModuleList.addAll(results.getRows());
		return results;
	}
	
	/**
	 * 资源模块统计导出下载
	 * @return
	 * @throws IOException
	 */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = baseUrl + "exportRes")
    public ResponseEntity<byte[]> exportRes(HttpServletRequest request,@RequestParam("ids") String ids) throws IOException {
    	String encryptPwd = request.getParameter("encryptPwd");
        HttpHeaders headers = new HttpHeaders();
        UserInfo userInfo =  LoginUserUtil.getLoginUser();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String filename = URLEncoder.encode("资源模块统计.xls", "UTF-8");
        if(StringUtils.isNotEmpty(encryptPwd)){
        	filename = URLEncoder.encode("资源模块统计.zip", "UTF-8");
        }
        headers.setContentDispositionFormData("attachment", filename);
        List datas = new ArrayList();
        if(StringUtils.isNotBlank(ids)){
        	List idList = Arrays.asList(StringUtils.split(ids,","));
        	int size = idList.size();
        	//遍历出id相等的
        	for (ResqsOfModule object : resqsOfModuleList) {
				if(idList.contains(object.getId()+"")){
					datas.add(object);
					SysOperateLogUtils.addLog("module_exportRes",object.getMaturityNameDesc(), userInfo);
					if(datas.size() == size){
						break;
					}
				}
			}
        }
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(moduleNumService.exportRes(datas,encryptPwd)),headers, HttpStatus.OK);
    }
}
