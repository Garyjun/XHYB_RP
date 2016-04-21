package com.brainsoon.system.action;


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.brainsoon.common.exception.DaoException;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.support.Constants;
import com.brainsoon.system.model.SysDir;
import com.brainsoon.system.model.SysParameter;
import com.brainsoon.system.service.ISysParameterService;
import com.brainsoon.system.support.SysOperateLogUtils;
import com.brainsoon.system.support.SystemConstants;

@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SysParameterAction extends BaseAction {
	@Autowired
	private ISysParameterService sysParameterService;
    UserInfo userinfo = LoginUserUtil.getLoginUser();
	@RequestMapping(value = "/sysParameter/list")
	public String list(Model model, SysParameter sysParameter) {
		logger.debug("to list ");
		model.addAttribute(Constants.COMMAND, sysParameter);
		return "system/sysParameter/parameterList";
	}
	
	@RequestMapping(value = "/sysParameter/query")
	@ResponseBody
	public PageResult query(@RequestParam(value = "paraKey", required = false) String paraKey,
			@RequestParam(value = "paraValue", required = false) String paraValue) {
		QueryConditionList conditionList = getQueryConditionList();
		if (paraKey != null) {
			conditionList.addCondition(new QueryConditionItem("paraKey", Operator.LIKE,"%"+paraKey+"%"));
		}
		if (paraValue != null) {
			conditionList.addCondition(new QueryConditionItem("paraValue", Operator.LIKE,"%"+paraValue+"%"));
		}
		return sysParameterService.query4Page(SysParameter.class, conditionList);

	}
	
	@RequestMapping(value = "/sysParameter/delete")
	public String delete(@RequestParam("id") String id) {
		try {
			SysParameter sysParameter = (SysParameter)sysParameterService.getByPk(SysParameter.class,Long.parseLong(id));
			SysOperateLogUtils.addLog("sysParameter_delete", sysParameter.getParaKey(), userinfo);
			sysParameterService.delete(SysParameter.class, id);
			
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysParameter/list.action";
	}
	

	@RequestMapping(value = "/sysParameter/batchDelete/{ids}")
	public String batchDelete(@PathVariable String ids) {
		logger.debug("*********batchDelete*********");
		try {
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				SysParameter sysParameter = (SysParameter)sysParameterService.getByPk(SysParameter.class,Long.parseLong(idArray[i]));
				SysOperateLogUtils.addLog("sysParameter_delete", sysParameter.getParaKey(), userinfo);
				sysParameterService.delete(SysParameter.class, new Long(idArray[i]));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysParameter/list.action";
	}
	
	/*@RequestMapping(value = "/sysParameter/delete", method = { RequestMethod.GET })
	public String delete(@RequestParam Long id) {
		try {
			sysParameterService.delete(SysParameter.class, id);
//			UserInfo userInfo = LoginUserUtil.getLoginUser();
//			SysOperateLogUtils.addLog("sysParameter_delete", userInfo.getUsername(), userInfo);
		} catch (DaoException e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysParameter/list.action";
	}*/
	
/*	@RequestMapping(value = "/sysParameter/batchDelete/{ids}")
	public String batchDelete(@PathVariable String ids) {
		logger.debug("*********batchDelete*********");
		try {
			String[] idArray = ids.split(",");
			for (int i = 0; i < idArray.length; i++) {
				sysParameterService.delete(SysParameter.class, new Long(idArray[i]));
			}
//			UserInfo userInfo = LoginUserUtil.getLoginUser();
//			SysOperateLogUtils.addLog("sysParameter_batchdelete", userInfo.getUsername(), userInfo);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "redirect:/sysParameter/list.action";
	}*/
	
	@RequestMapping(value = "/sysParameter/toEdit")
	@Token(save = true)
	public String toEdit(Model model, @RequestParam("id") Long id) {
		logger.info("进入修改/添加系统参数页面");
		SysParameter sysParameter = new SysParameter();
		if(id>-1){
			sysParameter = (SysParameter) sysParameterService.getByPk(SysParameter.class, id);
		}
		model.addAttribute("type",SystemConstants.Types.map);
		model.addAttribute("status",SystemConstants.Status.map);
 	    model.addAttribute(Constants.ID,id);
		model.addAttribute("sysParameter", sysParameter);
		model.addAttribute("statusInt",String.valueOf(sysParameter.getParaStatus()));
		model.addAttribute("typeValue", String.valueOf(sysParameter.getParaType()));
		
		return "system/sysParameter/parameterEdit";
	}
	
	@RequestMapping(value = "/sysParameter/add")
	@Token(remove=true)
	public void add(Model model, @ModelAttribute("command") SysParameter sysParameter, HttpServletResponse response) {
		logger.info("进入保存系统参数方法");
		try {
			sysParameter.setPlatformId(userinfo.getPlatformId());
			sysParameterService.save(sysParameter);
			SysOperateLogUtils.addLog("sysParameter_add", sysParameter.getParaKey(), userinfo);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			addActionError(e);
		}
	}
	
	@RequestMapping(value = "/sysParameter/validateParamKey")
	public @ResponseBody String validateParamKey(HttpServletRequest request){
		logger.info("进入查询方法");
		int platformId = userinfo.getPlatformId();
		String id = null;
		String paraKey = request.getParameter("fieldValue");
		String paramKeyId = request.getParameter("paraKeyId");
		String sql = "select id,para_key from sys_parameter where para_key='"+paraKey+"' and platformId="+platformId+"";
		List nameList = sysParameterService.validateParamKey(sql);
		Iterator it = nameList.iterator();    
		while(it.hasNext()) {    
		    Map map = (Map) it.next();
		    id = map.get("id").toString();
		   
		} 
		if(nameList.size()==1){
			if(paramKeyId.equals(id)) {
				return "{\"jsonValidateReturn\": [\"paraKey\",true]}";
			} else {
				return "{\"jsonValidateReturn\": [\"paraKey\",false]}";
			}
		}
		return "{\"jsonValidateReturn\": [\"paraKey\",true]}";
	}
	
	
	@RequestMapping(value = "/sysParameter/validateParamKeyAdd")
	public @ResponseBody String validateParamKeyAdd(HttpServletRequest request){
		logger.info("进入查询方法");
		int platformId = userinfo.getPlatformId();
		String paraKey = request.getParameter("fieldValue");
		String sql = "select para_key from sys_parameter where para_key='"+paraKey+"' and platformId="+platformId+"";
		List nameList = sysParameterService.validateParamKey(sql);
		
		if(nameList.size()<1){
			return "{\"jsonValidateReturn\": [\"paraKey\",true]}";
		}
		return "{\"jsonValidateReturn\": [\"paraKey\",false]}";
	}
	@RequestMapping(value = "/sysParameter/update")
    @Token(remove=true)
    public void update(Model model, @ModelAttribute("command")SysParameter sysParameter, HttpServletResponse response) {
		try {
			sysParameter.setPlatformId(userinfo.getPlatformId());
        	sysParameterService.update(sysParameter);
			SysOperateLogUtils.addLog("sysParameter_update", sysParameter.getParaKey(), userinfo);
			
		} catch (Exception e) {
			    logger.error(e.getMessage());
			    addActionError(e);
		}
    }
	
	@RequestMapping(value = "/sysParameter/view")
	public String view(Model model, @RequestParam("id") Long id) {
		SysParameter sysParameter = (SysParameter) baseService.getByPk(SysParameter.class, id);
		model.addAttribute("sysParameter", sysParameter);
		return "system/sysParameter/parameterDetail";
	}
	
}
