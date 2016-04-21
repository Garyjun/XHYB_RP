package com.brainsoon.docviewer.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.brainsoon.appframe.action.BaseAction;
import com.brainsoon.appframe.action.Token;
import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.query.QuerySortItem;
import com.brainsoon.appframe.support.PageResult;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.common.dao.IBaseDao;
import com.brainsoon.docviewer.model.ResConverfileTask;
import com.brainsoon.docviewer.model.ResConverfileTaskHistory;
import com.brainsoon.docviewer.model.ResConverfileTaskId;
import com.brainsoon.docviewer.model.Viewer;
import com.brainsoon.docviewer.service.IResConverfileTaskIdService;
import com.brainsoon.docviewer.service.IResConverfileTaskService;
@Controller
public class ResConverfileTaskAction extends BaseAction {
	/**默认命名空间**/
	private static final String baseUrl = "/resConver/";
	@Autowired
	private IResConverfileTaskService resConverfileTaskService;
	@Autowired
	private IResConverfileTaskIdService resConverfileTaskIdService;
	@Autowired
    protected IBaseDao baseDao;
	/**
	 * 列表查询页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "list")
	public String list(HttpServletRequest request,ResConverfileTask ConverfileTask, Model model){
		logger.info("查询转换表");
//		String id = request.getParameter("id");
		//判断点击二级菜单跳转哪个表查询
		String flag = "flag";
		HttpSession session = getSession();
		session.setAttribute("flag", flag);
		return "/resConverfileTask/resConverfileTaskList";
	}
	/**
	 * 批量重试
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "batchRetry")
	public String batchRetry(HttpServletRequest request,ResConverfileTask ConverfileTask, Model model){
		logger.info("查询转换表");
		String ids = request.getParameter("ids");
		resConverfileTaskService.doTaskHistory(ids);
		//判断点击二级菜单跳转哪个表查询
//		String flag = "flag";
//		HttpSession session = getSession();
//		session.setAttribute("flag", flag);
		return "/resConverfileTask/resConverfileTaskList";
	}
	/**
	 * 批量重试
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "pageAll")
	public void pageAll(HttpServletRequest request,ResConverfileTask ConverfileTask, Model model,HttpServletResponse response){
		logger.info("查询待转换表");
		PrintWriter out = null;
		String a = resConverfileTaskService.createPageAll();
		try {
			out = response.getWriter();
			response.setContentType("text/plain");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setCharacterEncoding("UTF-8");
			out.println(a);// 返回字符串
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
		//return "/resConverfileTask/resConverfileTaskList";
	}
	/**
	 * 
	 * @Title: saveToResConverfileTask 
	 * @Description: 保存到待转换队列中
	 * @param   
	 * @return void 
	 * @throws
	 */
//@RequestMapping(baseUrl + "saveToResConverfileTask")
//    public void saveToResConverfileTask(Map<String,ResMsg> map) {
//    	if(map != null && map.size() > 0){
//    		for (String key : map.keySet()) {
//			   ResMsg resMsg = map.get(key);
//			   if(resMsg != null){
//				   ResConverfileTask rcft = new ResConverfileTask(resMsg.getResId(),resMsg.getResPath(),"");
//				   resConverfileTaskService.saveResConverfileTask(rcft); //保存到待转换队列中
//			   }
//			}
//    	}
//    }
	/**
	 * 修改添加页
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "upd")
	public String upd(HttpServletRequest request,HttpServletResponse response,ModelMap model){
		logger.info("进入修改/添加标签页面");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		//String id = request.getParameter("id");
		List<Object> taskId = null;
		List<Object> resConverfileTask = null;
		String ip ="";
		String singleGroupIdParmNumber ="";
		String groupIdParmNumber ="";
		ResConverfileTaskId	task = null;
			taskId = baseDao.queryBySql("select * from res_converfile_task_id", ResConverfileTaskId.class);
			resConverfileTask = baseDao.queryBySql("select * from res_converfile_task where platformId="+userInfo.getPlatformId(), ResConverfileTask.class);
			if(taskId.size()>0){
				Iterator<Object> iter = taskId.iterator();
				while(iter.hasNext()){
					ResConverfileTaskId ipAdd = (ResConverfileTaskId)iter.next();
					String ipAddress = ipAdd.getIpAddr();
					ip+=ipAddress+",";
					if(singleGroupIdParmNumber.equals("")){
						singleGroupIdParmNumber = ipAdd.getSingleGroupIdParmNumber();
					}
					if(groupIdParmNumber.equals("")){
						groupIdParmNumber =ipAdd.getGroupIdParmNumber();
					}
				}
					task = new ResConverfileTaskId();
					task.setIpAddr(ip.substring(0,ip.length()-1));
					task.setSingleGroupIdParmNumber(singleGroupIdParmNumber);
					task.setGroupIdParmNumber(groupIdParmNumber);
			}
		
		if(task==null){
			task = new ResConverfileTaskId();
		}
		model.addAttribute("count", resConverfileTask.size());
		model.addAttribute("frmWord", task);
		return "/resConverfileTask/ipAddressAdd";
	}
	/**
	 * taskId保存
	 * @param request
	 * @param response
	 * @param target
	 * @param model
	 * @return
	 */
	@RequestMapping(baseUrl + "updTaskId")
	@Token(save=true)
	public String updTaskId(HttpServletRequest request,HttpServletResponse response,@ModelAttribute("frmWord") ResConverfileTaskId taskId,ModelMap model){
		logger.info("进入保存taskId方法");
		String ipAddrs =taskId.getIpAddr();
		String singleGroupIdParmNumber = taskId.getSingleGroupIdParmNumber();
		String groupIdParmNumber = taskId.getGroupIdParmNumber();
		resConverfileTaskIdService.addFpIpAddrWithId(ipAddrs, singleGroupIdParmNumber, groupIdParmNumber);
		return "/resConverfileTask/ipAddressAdd";
	}
	/**
	 * 查询
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(baseUrl + "query")
	public @ResponseBody PageResult query(HttpServletRequest request,HttpServletResponse response){
		logger.info("查询转换表");
		UserInfo userInfo = LoginUserUtil.getLoginUser();
		QueryConditionList conditionList= createBaseConditionList();
		QueryConditionItem qc = new QueryConditionItem();
		qc.setFieldName("platformId");
		qc.setOperator(Operator.EQUAL);
		PageResult result=null;
		HttpSession session = getSession();
		if(LoginUserUtil.getLoginUser() != null){
			qc.setValue(LoginUserUtil.getLoginUser().getPlatformId());
		}else{
			qc.setValue(1);
		}
		conditionList.addCondition(qc);
		List<QueryConditionItem> items = conditionList.getConditionItems();
		int stat=0;
		int historyFlag = 0;
		String flag1 = (String) request.getSession().getAttribute("flag");
			for (int i = 0; i < items.size(); i++) {
				String value = conditionList.getConditionItems().get(i).getValue().toString();
				String name = conditionList.getConditionItems().get(i).getFieldName().toString();
				if(name.equals("imgStauts")&&value.equals("0")||name.equals("status")&&value.equals("0")){
					stat++;
				}
				if(name.equals("status")&&value.equals("2")||name.equals("status")&&value.equals("3")){
					historyFlag++;
				}
			}
			//判断只点击查询的时候只走history查询
			if(stat>0 && flag1==null||historyFlag>0){
				result = query4Page(ResConverfileTaskHistory.class, conditionList);
			}else{
			 	result = query4Page(ResConverfileTask.class, conditionList);
			 	session.removeAttribute("flag");
			}
		return result;
	}
	/**
	 * 分页查询，智能添加前台参数
	 * @param poClass
	 * @param conditionList
	 * @return PageResult
	 */
	public PageResult query4Page(Class poClass,QueryConditionList conditionList){
		StringBuffer hql = new StringBuffer();
		
		Map<String, Object> params = null;
		
		params = parseConditions(poClass, conditionList, hql, params);
		return baseDao.query4Page(hql.toString(), conditionList.getStartIndex(), conditionList.getPageSize(), params);
	}
	/**
	 * 解析查询条件
	 * @param poClass
	 * @param conditionList
	 * @param hql
	 * @param params
	 * @return
	 */
	public Map<String, Object> parseConditions(Class poClass,
			QueryConditionList conditionList, StringBuffer hql,
			Map<String, Object> params) {
		String poName = getClassName(poClass);
		hql.append(" from ").append(poName);
		//组装查询
		if(null != conditionList){
			params = new HashMap<String, Object>();
			
			List<QueryConditionItem> items = conditionList.getConditionItems();
			
			hql.append(" where 1=1 ");
			for (int i = 0; i < items.size(); i++) {
				QueryConditionItem queryConditionItem = items.get(i);
				String filedName = queryConditionItem.getFieldName();
				String value = conditionList.getConditionItems().get(i).getValue().toString();
				if(value.equals("0")||value.equals("1")||value.equals("2")||value.equals("3")||value.equals("10")||value.equals("11")||value.equals("12")||value.equals("13")){
				if(conditionList.getConditionItems().get(i).getFieldName().equals("status") && conditionList.getConditionItems().get(i).getValue().toString().equals("3")){
					hql.append(" and ").append("status=3");
				}else if(conditionList.getConditionItems().get(i).getFieldName().equals("status") && conditionList.getConditionItems().get(i).getValue().toString().equals("2")){
					hql.append(" and ").append("status=2");
				}else if(conditionList.getConditionItems().get(i).getFieldName().equals("status")){
					hql.append(" and ").append("status in ('0','1','2','3')");
				}else if(conditionList.getConditionItems().get(i).getFieldName().equals("id")){
					hql.append(" and ").append("id="+conditionList.getConditionItems().get(i).getValue());
				}else if((Integer)conditionList.getConditionItems().get(i).getValue()==2&&conditionList.getConditionItems().get(i).getFieldName().equals("txtStr")){
					hql.append(" and ").append("txtStr<>'' or txtStr!=null");
				}else if((Integer)conditionList.getConditionItems().get(i).getValue()==3&&conditionList.getConditionItems().get(i).getFieldName().equals("txtStr")){
					hql.append(" and ").append("txtStr='' or txtStr=null");
				}else if((Integer)conditionList.getConditionItems().get(i).getValue()==13&&conditionList.getConditionItems().get(i).getFieldName().equals("doHasType")){
					hql.append(" and ").append("doResultType=''");
				}else if((Integer)conditionList.getConditionItems().get(i).getValue()==12&&conditionList.getConditionItems().get(i).getFieldName().equals("doHasType")){
					hql.append(" and ").append(filedName).append(" ").append("= ''");
				}else if((Integer)conditionList.getConditionItems().get(i).getValue()==11&&conditionList.getConditionItems().get(i).getFieldName().equals("doHasType")){
					hql.append(" and ").append(filedName).append(" ").append("=");
					hql.append("doResultType");
				}else if((Integer)conditionList.getConditionItems().get(i).getValue()==10&&conditionList.getConditionItems().get(i).getFieldName().equals("doHasType")){
					hql.append(" and ").append(filedName).append(" ").append("<>");
					hql.append("doResultType");
				}
				else if (queryConditionItem.getOperator().equals(Operator.IN)) {
					hql.append(" and ").append(filedName).append(" ").append(queryConditionItem.getOperator().getValue()).append(" (");
					hql.append(queryConditionItem.getValue());
					hql.append(")");
				} else {
					hql.append(" and ").append(filedName).append(" ").append(queryConditionItem.getOperator().getValue()).append(" :");
					filedName = StringUtils.replace(filedName, ".", "_");
					//解决时间查询同一个属性，作为多个条件查询
					hql.append(filedName+"_"+i);
					params.put(filedName+"_"+i, queryConditionItem.getValue());
				}
			}else{
				if(conditionList.getConditionItems().get(i).getFieldName().equals("id")){
					hql.append(" and ").append("id="+conditionList.getConditionItems().get(i).getValue());
				}else if (queryConditionItem.getOperator().equals(Operator.IN)) {
					hql.append(" and ").append(filedName).append(" ").append(queryConditionItem.getOperator().getValue()).append(" (");
					hql.append(queryConditionItem.getValue());
					hql.append(")");
				} else {
					hql.append(" and ").append(filedName).append(" ").append(queryConditionItem.getOperator().getValue()).append(" :");
					filedName = StringUtils.replace(filedName, ".", "_");
					//解决时间查询同一个属性，作为多个条件查询
					hql.append(filedName+"_"+i);
					params.put(filedName+"_"+i, queryConditionItem.getValue());
				}
			}
			}
			//取排序
			List<QuerySortItem> sortList = conditionList.getSortList();
			StringBuffer order = new StringBuffer(sortList.size()*10);
			
			for (QuerySortItem querySortItem : sortList) {
				String filedName = querySortItem.getFieldName();
				if(StringUtils.isNotBlank(filedName)){
					order.append(" ,").append(filedName).append(" ").append(querySortItem.getSortMode());
				}
			}
			if(order.length() > 0){
				order.delete(0, 2);
				hql.append(" order by ").append(order);
			}
		}
		return params;
	}
	protected String getClassName(Class poClass){
		if (poClass==null) {
			throw new InvalidParameterException("必须指定PO的类");
		}
		
		String className=poClass.getName();
		int index=className.lastIndexOf(".");
		return className.substring(index+1);
	}
	
	
	/**
	 * 
	 * @Title: doTaskHistoryByPath
	 * @Description: 通过原路径进行重试
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "doTaskHistoryByPath", method = { RequestMethod.GET })
	public void doTaskHistoryByPath(HttpServletRequest request,
			HttpServletResponse response){
		try {
			// 文件路径
			String filePath = URLDecoder.decode(request.getParameter("filePath"), "UTF-8");
			String id = request.getParameter("id");
			//String resId = request.getParameter("id");
			String numStr = resConverfileTaskService.doTaskHistoryByPath(filePath,id);
			PrintWriter out = null;
			try {
				out = response.getWriter();
				response.setContentType("text/plain");
				response.setHeader("Pragma", "No-cache");
				response.setHeader("Cache-Control", "no-cache");
				response.setDateHeader("Expires", 0);
				response.setCharacterEncoding("UTF-8");
				out.println(numStr);// 返回字符串
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(out != null){
					out.close();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @Title: doTaskCheckExitJL
	 * @Description: 通过txt文件校验文件是否存在
	 * @param
	 * @return String
	 * @throws
	 */
	@RequestMapping(value = baseUrl + "doTaskCheckExitJL")
	public void doTaskCheckExitJL(HttpServletRequest request,
			HttpServletResponse response){
		PrintWriter out = null;
		try {
			// 文件路径
			String ischeck = request.getParameter("ischeck");
			String numStr = "执行失败，无法继续。";
			if(StringUtils.isNotBlank(ischeck) && ischeck.equals("1")){
				numStr = resConverfileTaskService.doTaskCheckExitJL();
			}
			out = response.getWriter();
			response.setContentType("text/plain");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setCharacterEncoding("UTF-8");
			out.println(numStr);// 返回字符串
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				out.close();
			}
		}
	}
	
}
