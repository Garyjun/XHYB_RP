package com.brainsoon.common.tag;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.appframe.util.LoginUserUtil;
import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.support.SysResTypeCacheMap;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.service.IBookService;

public class SelectResTypeTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -2056599250024509108L;
	private String name;
    private String type;
    private String headName;
    private String headValue;
    private String selectedVal;
    private String onchange;
    private String readonly;
    private String exceptValue;
    private String id;
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		StringBuffer sb = new StringBuffer();
		sb.append("<select name='").append(name).append("'");
		sb.append(" id='").append(id).append("'");
		sb.append(" class='").append("form-control").append("'");
		if(!"".equals(onchange)){
			sb.append(" onchange='").append(onchange).append("'");
		}
		if(StringUtils.isNotBlank(readonly)){
			sb.append("disabled=\"disabled\"");
		}
        sb.append(">");
        
        if(StringUtils.isNotBlank(headName)) {
            sb.append("<option value='").append(headValue).append("'>")
                    .append(headName).append("</option>");
        }
        Map<Object, Object> resTypeMap = SysResTypeCacheMap.getMapValue(); 
        int index=0;
        if(resTypeMap != null){
        	HttpSession session = pageContext.getSession();
        	UserInfo user = (UserInfo)session.getAttribute(LoginUserUtil.USER_SESSION_KEY);
        //	UserInfo user = LoginUserUtil.getLoginUser();
        	Iterator it = null;
        	if(user!=null && user.getResTypes()!=null && user.getResTypes().size()>0){
        		it = user.getResTypes().entrySet().iterator();
        	}else if(user.getResTypes()==null){
        		resTypeMap.clear();
        		resTypeMap.put("no", "未定义");
        		it = resTypeMap.entrySet().iterator();
        	}
        	for(Iterator iterator ;it.hasNext();){  
	            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
	            if(StringUtils.isNotBlank(exceptValue)){
	            	if(exceptValue.contains(entry.getValue())){
	            		continue;
	            	}else{
	            		sb.append("<option value='").append(entry.getKey()).append("'");
	            	}
	            	
	            }
	            //判断是否选中
	            if(StringUtils.isNotBlank(selectedVal)) {
	                if(selectedVal.equals(entry.getKey())) {
	                    sb.append(" selected ");
	                }
	            } else {
	                if(index == 0 && headName == null) {
	                    sb.append(" selected ");
	                }
	            }
		            sb.append(">").append(entry.getValue()).append("</option>");
		            index ++;
	        }
        }
        sb.append("</select>");
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHeadName() {
		return headName;
	}
	public void setHeadName(String headName) {
		this.headName = headName;
	}
	public String getHeadValue() {
		return headValue;
	}
	public void setHeadValue(String headValue) {
		this.headValue = headValue;
	}
	public String getSelectedVal() {
		return selectedVal;
	}
	public void setSelectedVal(String selectedVal) {
		this.selectedVal = selectedVal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public String getExceptValue() {
		return exceptValue;
	}

	public void setExceptValue(String exceptValue) {
		this.exceptValue = exceptValue;
	}
	
	
}
