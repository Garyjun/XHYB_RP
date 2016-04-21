package com.brainsoon.appframe.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.brainsoon.appframe.query.Operator;
import com.brainsoon.appframe.query.QueryConditionItem;
import com.brainsoon.appframe.query.QueryConditionList;
import com.brainsoon.appframe.query.QuerySortItem;
import com.brainsoon.appframe.support.MyCustomDateEditor;
import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.pagination.PageInfo;
import com.brainsoon.common.service.IBaseService;
import com.brainsoon.rp.support.SearchParam;
import com.google.gson.Gson;


/**
 *
 * 控制类基类
 *
 * @author zuo
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public abstract class BaseAction extends MultiActionController{

	protected transient final Log logger = LogFactory.getLog(getClass());
	@Autowired
	protected IBaseService baseService;
	public final static String ACTION_MSGKEY = "_action_msg";
	public final static String ACTION_ERRORKEY = "_action_error";
	public final static String RESULT_MSGKEY = "_result_msg";
	public final static String FILEDENDMARK = "_myfd[]";

	public Map _params = new HashMap();//封装页面参数

	private final static Map<String,Operator> OperatorMap = Operator.getAllMaps();
	/**
	 * 添加系统提示消息
	 * @param msg
	 */
	public void addActionMsg(String msg){
		getRequest().setAttribute(ACTION_MSGKEY, msg);
	}
	/**
	 * 添加系统提示消息
	 * @param e
	 */
	public void addActionMsg(Exception e){
		getRequest().setAttribute(ACTION_MSGKEY, e.getMessage());
	}
	/**
	 * 添加系统错误提示消息
	 * @param error
	 */
	public void addActionError(String error){
		getRequest().setAttribute(ACTION_ERRORKEY, error);
	}
	/**
	 * 添加系统错误提示消息
	 * @param e
	 */
	public void addActionError(Exception e){
		getRequest().setAttribute(ACTION_ERRORKEY, e.getMessage());
	}
	/**
	 * 输出字符串，利用拦截器实现
	 * @param result
	 */
	public void outputResult(String result){
		getRequest().setAttribute(RESULT_MSGKEY, result);
	}
	public HttpServletRequest getRequest(){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	public HttpSession getSession(){
		return getRequest().getSession();
	}

	/**
	 * 解析datagrid传递的条件查询参数
	 * @return QueryConditionList
	 */
	public QueryConditionList getQueryConditionList(){
		QueryConditionList conditionList= createBaseConditionList();
		//加入平台ID参数
//		QueryConditionItem qc = new QueryConditionItem();
//		qc.setFieldName("platformId");
//		qc.setOperator(Operator.EQUAL);
//		if(LoginUserUtil.getLoginUser() != null){
//			qc.setValue(LoginUserUtil.getLoginUser().getPlatformId());
//		}else{
//			qc.setValue(1);
//		}
//		conditionList.addCondition(qc);
		return conditionList;
	}
	/**
	 * 不需要平台ID的查询条件的构建
	 * @return
	 */
	public QueryConditionList getQueryConditionListWithNoPlat(){
		return createBaseConditionList();
	}
	/**
	 *  字段属性权限
	 */
	public List<QueryConditionItem> getUserPrivilege(String publishType){
		Gson gson = new Gson();
		List<QueryConditionItem> items = new ArrayList<QueryConditionItem>();
		UserInfo userinfo = LoginUserUtil.getLoginUser();
		if(userinfo != null){
			String dataPreRange = userinfo.getDataPreRangeArray();
			if(StringUtils.isBlank(dataPreRange) ||"[".equals(dataPreRange)){
				return null;
			}
			logger.info("dddddddddddddd"+dataPreRange);
			JSONArray array = JSONArray.fromObject(dataPreRange);
			for(int i=0;i<array.size();i++){
				String jsonObj = array.get(i).toString();
				if(StringUtils.isBlank(jsonObj) || "null".equals(jsonObj)){
					break;
				}
				QueryConditionItem item = gson.fromJson(jsonObj, QueryConditionItem.class);
				if(publishType.equals(item.getResType())){
					String field = item.getFieldName();
					String operator = item.getOperator().toString();
					if("BETWEEN".equals(operator)){
						String value = item.getValue().toString();
						if(StringUtils.isNotBlank(value) && value.indexOf(",")>0){
							String[] values = value.split(",");
							QueryConditionItem itemStart = new QueryConditionItem();
							field = field+"_metaField_StartDate";
							itemStart.setFieldName(field);
							itemStart.setIsMetadata("1");
							itemStart.setValue(values[0]);
							items.add(itemStart);
							QueryConditionItem itemEnd = new QueryConditionItem();
							field = field+"_metaField_EndDate";
							itemEnd.setFieldName(field);
							itemEnd.setIsMetadata("1");
							itemEnd.setValue(values[1]);
							items.add(itemEnd);
						}
					}else if("LIKE".equals(operator)){
						String value = item.getValue().toString();
						item.setFieldName(field);
						item.setIsMetadata("1");
						item.setValue("*"+value+"*");
						items.add(item);
					}else{
				//		field = field;
						item.setFieldName(field);
						item.setIsMetadata("1");
						items.add(item);
					}
				}
			}
		}
		return items;
	}
	public QueryConditionList createBaseConditionList(){
		QueryConditionList conditionList = new QueryConditionList();
		HttpServletRequest request = getRequest();
		//先赋值查询页码等已知参数
		int pageSize = StringUtil.obj2Int(request.getParameter("rows"));
		conditionList.setPageSize(pageSize);

		int pageNo = StringUtil.obj2Int(request.getParameter("page"));
		int startIndex = (pageNo - 1) * pageSize > 0 ? (pageNo - 1) * pageSize : 0;

		conditionList.setStartIndex(startIndex);

		String sortName = request.getParameter("sort");
		String order = request.getParameter("order");
		String queryFlag = request.getParameter("queryFlag");

		if(StringUtils.isNotBlank(sortName)){
			QuerySortItem sort = new QuerySortItem();
			sort.setFieldName(sortName);
			sort.setSortMode(QueryConditionList.SORT_MODE_DESC);//默认倒序

			if(StringUtils.equalsIgnoreCase(order, QueryConditionList.SORT_MODE_ASC)){
				sort.setSortMode(QueryConditionList.SORT_MODE_ASC);
			}
			conditionList.addSort(sort);
		}

		Iterator iter = request.getParameterMap().entrySet().iterator();

		while(iter.hasNext()){
			Map.Entry<String,String[]> entry = (Map.Entry)iter.next();
			String key = entry.getKey();
			String[] value = entry.getValue();
			conditionList = setQueryConditionListParam(conditionList, key, value, queryFlag);
		}
		return conditionList;
	}
	
	
	/*
	 * 设置QueryConditionList查询参数
	 */
	public QueryConditionList setQueryConditionListParam(QueryConditionList conditionList,String key,String[] value,String queryFlag){
		//此逻辑限于使用&方式传递参数，非json方式
		if(StringUtils.isNotBlank(queryFlag) && value != null && value[0].contains("[") && value[0].contains("]")){
			String midStri  =  value[0].substring(1,value[0].length());
			value = midStri.substring(0,midStri.length()-1).split(",");
		}
		
		if(value != null && value.length >= 3){
			if(key.endsWith(FILEDENDMARK)){
				QueryConditionItem item = new QueryConditionItem();
				key = StringUtils.substringBeforeLast(key, FILEDENDMARK);
				String qMatch = value[1];
				Object realValue = null;
				try {
					realValue = URLDecoder.decode(value[0], "UTF-8");
					if(realValue==null||realValue.equals("null")){
						return conditionList;
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
				if(key.endsWith("_StartTime")){
					key = StringUtils.substringBeforeLast(key, "_StartTime");
					qMatch = ">=";
					realValue = StringUtil.stringToDate(realValue+"");
				}
				if(key.endsWith("_EndTime")){
					key = StringUtils.substringBeforeLast(key, "_EndTime");
					qMatch = "<=";
					realValue = StringUtil.stringToDate(realValue+"");
				}
				String isMetadata = "0";
				if(key.endsWith("_metaField_StartDate") || key.endsWith("_metaField_EndDate")){
					item.setIsMetadata("1");
					isMetadata = "1";
				}
				String qType = value[2];
				if(key.endsWith("_metaField")){
					key = StringUtils.substringBeforeLast(key, "_metaField");
					item.setIsMetadata("1");
					isMetadata = "1";
				}
				item.setFieldName(key);
				Operator operator = OperatorMap.get(qMatch);
				item.setOperator(operator);

				if(StringUtils.equalsIgnoreCase(qType, "int")){
					realValue = StringUtil.obj2Int(realValue);
				}else if(StringUtils.equalsIgnoreCase(qType, "long")){
					realValue = StringUtil.obj2Long(realValue);
				}else if(StringUtils.equalsIgnoreCase(qType, "date")){
					realValue = StringUtil.objToSqlTime(realValue+"");
				}
				if(StringUtils.equalsIgnoreCase(qMatch, "like")){
					if("1".equals(isMetadata)){
						realValue = "*"+realValue+"*";
					}else{
						realValue = "%"+realValue+"%";
					}
				}
				item.setValue(realValue);
				conditionList.addCondition(item);
			}
		}
		return conditionList;
	}
	
	
	
	public Map get_params() {
		return _params;
	}
	public void set_params(Map _params) {
		this._params = _params;
	}
	public PageInfo getPageInfo(){
		PageInfo pageInfo=new PageInfo();
		HttpServletRequest request=getRequest();
		pageInfo.setPage(Integer.parseInt(request.getParameter("page")));
		pageInfo.setSort(request.getParameter("sort"));
		pageInfo.setOrder(request.getParameter("order"));
		pageInfo.setRows(Integer.parseInt(request.getParameter("rows")));
		return pageInfo;
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request,
            ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(Integer.class,new CustomNumberEditor(Integer.class, true));
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class,true));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setLenient(false);
        MyCustomDateEditor dateEditor = new MyCustomDateEditor(format, true);
	    binder.registerCustomEditor(Date.class, dateEditor);
	    super.initBinder(request, binder);
	}
	
	public SearchParam getDataTableResult(){
		HttpServletRequest request=getRequest();
		Gson gson = new Gson();
		
		String ssearchParam = request.getParameter("searchParam");
		SearchParam searchParam = gson.fromJson(ssearchParam, SearchParam.class);
		
		searchParam.setDraw(Integer.parseInt(request.getParameter("draw")));//请求次数
		searchParam.setStart(Integer.parseInt(request.getParameter("start")));//开始索引
		searchParam.setLength(Integer.parseInt(request.getParameter("length")));//每页显示条数
		searchParam.setSearchValue(request.getParameter("search[value]"));//搜索词
		String sortColumn = request.getParameter("order[0][column]");//排序字段
		if (StringUtils.isNotBlank(sortColumn) && !"0".equals(sortColumn)) {
			searchParam.setSortColumn("createTime");
		}else {
			searchParam.setSortColumn("id");
		}
		searchParam.setOrder(request.getParameter("order[0][dir]"));//排序

		return searchParam;
	}
}
