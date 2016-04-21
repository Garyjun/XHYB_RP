package com.brainsoon.resource.action;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jbpm.api.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.WebAppUtils;
import com.brainsoon.jbpm.service.IJbpmExcutionService;
import com.brainsoon.resource.service.IAreaService;
import com.brainsoon.resource.service.IBaseSemanticSerivce;
import com.brainsoon.resource.service.IResourceService;
import com.brainsoon.resource.support.ResourceTypeUtils;
import com.brainsoon.resource.support.WorkFlowUtils;
import com.brainsoon.resource.util.MD5Util;
import com.brainsoon.semantic.ontology.model.Asset;
import com.brainsoon.statistics.service.IEffectNumService;
import com.brainsoon.system.service.IBookService;
import com.brainsoon.system.service.ISysOperateService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants.OperatType;
import com.google.gson.Gson;

/**
 * 资源统一管理
 * 
 * @author xu
 */
@Controller
@RequestMapping("/resource/")
public class ResourceAction extends BaseAction {

	/**
	 * 详细
	 * 
	 * @param objectId
	 * @param model
	 * @throws Exception
	 */
	@RequestMapping("detail")
	public String detail(@RequestParam("objectId") String objectId, RedirectAttributes attr) throws Exception {

		attr.addAttribute("objectId", objectId);

		/**
		 * 1、原始资源、标准资源
		 * 2、应用资源
		 * 3、图书资源
		 */
		
		String redirectUrl = "";
		return "redirect:" + redirectUrl;
	}

}
