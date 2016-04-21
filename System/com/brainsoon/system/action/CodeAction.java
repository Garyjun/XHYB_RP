package com.brainsoon.system.action;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.exception.ServiceException;
import com.brainsoon.system.model.Code;
import com.brainsoon.system.model.InDefinition;
import com.brainsoon.system.service.ICodeService;
import com.brainsoon.system.service.IDictNameService;

@Controller
public class CodeAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/code/";
	@Autowired
	private ICodeService codeService;
	@Autowired
	private IDictNameService dictNameService;
	
	/**
	 * 列表查询页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "list")
	public String list(HttpServletRequest request,Code code, Model model){
		logger.info("查询编码表");
		String id = request.getParameter("id");
		HttpSession session = getSession();
		session.setAttribute("id", id);
		InDefinition inDefinition = (InDefinition) codeService.getByPk(InDefinition.class, Long.valueOf(id));
		session.setAttribute("inDefName", inDefinition.getName());
		return "/code/codeDataList";
	}
	/**
	 * 编码详细
	 * @param codeId
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "detail")
	public String detail(@RequestParam("codeId") Long codeId,HttpServletResponse response,ModelMap model){
		logger.info("查看编码");
		UserInfo userInfo = LoginUserUtil.getLoginUser();	
		Code code = (Code) codeService.getByPk(Code.class, codeId);
		model.addAttribute("frmWord", code);
//		SysOperateLogUtils.addLog("code_detail", userInfo.getUsername(),
//				userInfo);
		return "/code/codeDataDetail";
	}
	
	@RequestMapping(baseUrl + "query")
	public @ResponseBody PageResult query(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询编码表");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		QueryConditionList conditionList = getQueryConditionList();
		//String adapterType = request.getParameter("adapterType");
		//conditionList.addCondition(new QueryConditionItem("adapterType", Operator.EQUAL, 2));
		return codeService.query4Page(Code.class, conditionList);
	}
	/**
	 * 修改添加页
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "upd")
	public String upd(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("进入修改/添加编码页面");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String id = request.getParameter("id");
		Code code = null;
		int statas = 0;
		if(StringUtils.isNotBlank(id)){
			logger.info("new 对象修改");
			code = (Code) codeService.getByPk(Code.class, Long.parseLong(id));
		}
		if(code==null){
			logger.info("new 空对象");
			code = new Code();
			statas = 1;
			model.addAttribute("statas", statas);
		}
		model.addAttribute("frmWord", code);
		HttpSession session = getSession();
		session.setAttribute("codeId", code.getCodeId());
		if(statas==0){
		return "code/codeDataUpd";
	}else{
		logger.info("进入添加页面");
		return "code/codeDataEdit";
	}
	}
	/**
	 * 接口查询编码类型
	 * @param request
	 * @throws ServiceException
	 */
	@RequestMapping(baseUrl + "codeType")
	public @ResponseBody LinkedHashMap<String,String> codeType(HttpServletRequest request,
			String module) throws ServiceException {
		String adapterType = request.getParameter("adapterType");
		if(adapterType.equals("00")){
			adapterType = "资源类型Res";
		}else if(adapterType.equals("01")){
			adapterType = "分册";
		}else if(adapterType.equals("02")){
			adapterType = "学科";
		}else if(adapterType.equals("03")){
			adapterType = "教育阶段";
		}else if(adapterType.equals("04")){
			adapterType = "教育阶段";
		}else{
			adapterType = "适用年级";
		}
		LinkedHashMap<String,String> map = dictNameService.getDictMapByName(adapterType);
		return map;
	}
	/**
	 * 保存编码
	 * @param request
	 * @param response
	 * @param model
	 */
	@RequestMapping(baseUrl + "toUpdCode")
	@Token(save=true)
	public String updtarget(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmWord") Code code,ModelMap model){
		logger.info("进入保存编码方法");
		String dicName = request.getParameter("dicName");
		String codeId = request.getParameter("codeId");
		String adapterName = request.getParameter("adapterName");
		String codeName = request.getParameter("codeName");
		JSONObject jb=new JSONObject();
		String codeName1=(String)jb.fromObject(codeName).get("code"); 
		code.setAdapterName(adapterName);
		code.setCodeName(codeName1);
		if(codeId!=null){
			code.setCodeId(Long.parseLong(codeId));
		}
		codeService.createCode(request, response, code,dicName);
		return "/code/codeDataList";
	}
	/**
	 * 编码删除
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "delete")
	public String delete(HttpServletRequest request,HttpServletResponse response){
		logger.info("删除编码");
		String ids = request.getParameter("ids");
		codeService.deleteAll(Code.class, ids);
		return "/code/codeDataList";
	}
}
