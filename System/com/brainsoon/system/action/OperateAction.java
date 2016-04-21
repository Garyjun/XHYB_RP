package com.brainsoon.system.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
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
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.util.date.DateUtil;
import com.brainsoon.system.model.log.SysOperateHistory;
import com.brainsoon.system.model.log.SysOperateLog;
import com.brainsoon.system.service.IModuleService;
import com.brainsoon.system.service.ISysOperateService;

/**
 * 操作日志记录
 * 
 * @author Administrator
 * 
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class OperateAction extends BaseAction {
	@Autowired
	private ISysOperateService sysOperateService;
	@Autowired
	private IModuleService moduleService;
	UserInfo userInfo = LoginUserUtil.getLoginUser();

	@RequestMapping(value = "/log/list")
	@ResponseBody
	public PageResult query(@RequestParam(value = "operator", required = false) String operator, @RequestParam(value = "moduleId", required = false) String moduleId,
			@RequestParam(value = "optype", required = false) String optype, @RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,HttpServletRequest request) throws ParseException, UnsupportedEncodingException {
		logger.info("进入查询方法");
		QueryConditionList conditionList = getQueryConditionList();
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		String workLog = request.getParameter("workLog");
		if (moduleId != null) {
			conditionList.addCondition(new QueryConditionItem("sysOperateType.module.id", Operator.EQUAL, Long.valueOf(moduleId)));
		}
		if (optype != null) {
			conditionList.addCondition(new QueryConditionItem("sysOperateType.id", Operator.EQUAL, Long.valueOf(optype)));
		}
		if (operator != null) {
			try {
				operator = URLDecoder.decode(operator,"utf-8");
			} catch (Exception e) {
				// TODO: handle exception
			}
			conditionList.addCondition(new QueryConditionItem("operator", Operator.LIKE, "%" + operator + "%"));
			//conditionList.addCondition(new QueryConditionItem("operator", Operator.LIKE, "%" + operator + "%"));
		} else {
			if (!userInfo.isAdmin()) {
//				String userIds = userInfo.getDeptUserIds();
				String userIds = userInfo.getUserId()+"";
				if(StringUtils.isNotBlank(userIds)){
					try{
						String userNames = sysOperateService.getNameByid(userIds);
						conditionList.addCondition(new QueryConditionItem("loginname", Operator.IN, userNames));
						//conditionList.addCondition(new QueryConditionItem("operator", Operator.IN, userNames));
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					conditionList.addCondition(new QueryConditionItem("loginname", Operator.EQUAL,userInfo.getUsername()));
					//conditionList.addCondition(new QueryConditionItem("operator", Operator.EQUAL,userInfo.getUsername()));
				}
			}else{
				if(StringUtils.isNotBlank(workLog)){
					String userIds = userInfo.getUserId()+"";
					if(StringUtils.isNotBlank(userIds)){
						try{
							String userNames = sysOperateService.getNameByid(userIds);
							conditionList.addCondition(new QueryConditionItem("loginname", Operator.IN, userNames));
							//conditionList.addCondition(new QueryConditionItem("operator", Operator.IN, userNames));
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}
		}
		if (startDate != null) {
			startDate = URLDecoder.decode(startDate, "UTF-8");
			conditionList.addCondition(new QueryConditionItem("operateTime", Operator.GTE, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", startDate)));
		}
		if (endDate != null) {
//			Date d= DateUtil.convertStringToDate(endDate);
//			//获取输入的结束日期的下一天
//			d= DateUtil.getNextDate(d);
			endDate = URLDecoder.decode(endDate, "UTF-8");
			conditionList.addCondition(new QueryConditionItem("operateTime", Operator.LTE, DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", endDate)));
		}
		return sysOperateService.query4Page(SysOperateLog.class, conditionList);
	}

	/**
	 * 获取操作类型
	 * 
	 * @param moduleId
	 */
	@RequestMapping(value = "/log/getOperateTypes")
	@ResponseBody
	public String getOperateTypes(@RequestParam("moduleId") Long moduleId) {
		return moduleService.getOperateTypeJsonByModuleId(moduleId);
	}

	/**
	 * 日志查询主页
	 * 
	 * @param moduleId
	 */
	@RequestMapping(value = "/log/main")
	public String gotoMain(Model model) {
		Map<Long, String> moduleMap = moduleService.getModuleList(userInfo.getPlatformId());
		model.addAttribute("moduleList", moduleMap);
		return "/system/log/main";
	}

	/**
	 * 流程历史记录
	 * 
	 * @param moduleId
	 */
	@RequestMapping(value = "/operateHistory/list")
	@ResponseBody
	public PageResult operateHistoryList() {
		logger.info("进入流程操作历史查询方法");
		QueryConditionList conditionList = getQueryConditionListWithNoPlat();
		return sysOperateService.query4Page(SysOperateHistory.class, conditionList);
	}

	@RequestMapping(value = "/log/getSimilarWords")
	@ResponseBody
	public String getSimilarWord(@RequestParam("query") String query) {
		return sysOperateService.getSimilarWord(query);
	}
}
