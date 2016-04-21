package com.brainsoon.system.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.semantic.schema.ontology.CustomMetaData;
import com.brainsoon.semantic.schema.ontology.MetadataDefinition;
import com.brainsoon.semantic.schema.store.jena.service.MetaService;
import com.brainsoon.system.model.MetaDataFileModelGroup;
import com.brainsoon.system.model.ResTarget;
import com.brainsoon.system.model.ResTargetData;
import com.brainsoon.system.model.SysDoi;
import com.brainsoon.system.service.IResTargetService;
import com.brainsoon.system.service.ISysDoiService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.util.MetadataSupport;

@Controller
public class SysDoiAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/system/sysDoi/";
	@Autowired
	private ISysDoiService sysDoiService;
	/**
	 * 修改添加页
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "toUpd")
	public String upd(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("进入修改/添加Doi页面");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String publishType = request.getParameter("publishType");
		SysDoi sysDoi = null;
		List<SysDoi> sysDoi1 =null;
		int statas = 0;
		if(StringUtils.isNotBlank(publishType)){
			String hql = "from SysDoi where publishType="+Long.parseLong(publishType);
			sysDoi1 = (List<SysDoi>) sysDoiService.query(hql);
			if(sysDoi1.size()>0){
				sysDoi = sysDoi1.get(0);
				model.addAttribute("doiId", sysDoi.getId());
			}
		}
		if(sysDoi==null){
			sysDoi = new SysDoi();
			statas = 1;
			model.addAttribute("statas", statas);
		}
		model.addAttribute("publishType", publishType);
		model.addAttribute("frmWord", sysDoi);
		if(userInfo.getPlatformId()==2){
		return baseUrl + "chooseMetadataForDoiEdit";
		}
		return baseUrl + "chooseMetadataForDoiEdit";
	}
	/**
	 * Doi保存
	 * @param request
	 * @param response
	 * @param target
	 * @param model
	 * @return 
	 * @return
	 */
	@RequestMapping(baseUrl + "updDoi")
	public @ResponseBody String updtarget(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmWord") SysDoi sysDoi,ModelMap model){
		logger.info("进入保存Doi方法");
		boolean tag = false;
		String publishType = request.getParameter("publishType");
		String id = request.getParameter("doiId");
		if(StringUtils.isNotBlank(id)){
			sysDoi.setId(Long.parseLong(id));
		}
		sysDoi.setPublishType(Long.parseLong(publishType));
		tag = sysDoiService.createDoi(request, response, sysDoi);
		if(tag==true){
		return "1";
		}
		return "0";
	}

	/**
	 * Doi获取字段属性
	 * @param request
	 * @param response
	 * @param target
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "doiField")
	public @ResponseBody Map<String, String> doiField(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("进入保存Doi方法");
		String publishType = request.getParameter("publishType");
		Map<String,String> checkRepeatMetadate = MetadataSupport.getAllMetaFieldNameAndFieldZhName(publishType);
		return checkRepeatMetadate;
	}
	/**
	 * 执行Doi子菜单查询操作
	 * @param request
	 * @param response 
	 */
	@RequestMapping(baseUrl + "doiSubMenu")
	@Token(save=true)
	public @ResponseBody String queryDoiSubMenu(HttpServletRequest request,HttpServletResponse response){
		List<SysDoi> doiSubName = sysDoiService.queryDoiSub();
		JSONArray array = new JSONArray();
		for(SysDoi sysDoi : doiSubName){
			JSONObject json = new JSONObject();
			json.put("name", sysDoi.getPublishType());
			json.put("id", sysDoi.getId());
			array.add(json);
		}
		return array.toString();
	}
	}
	
