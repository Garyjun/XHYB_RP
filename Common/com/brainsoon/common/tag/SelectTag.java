package com.brainsoon.common.tag;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.system.service.IBookService;

public class SelectTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -2056599250024509108L;
	private String name;
    private String type;
    private boolean group;
    private String grouplabel;
    private String headName;
    private String headValue;
    private String selectedVal;
    private String indexTag;
    private boolean requiredCheck;
    private String onchange;
    private String id;
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		StringBuffer sb = new StringBuffer();
		sb.append("<select name='").append(name).append("'");
		sb.append(" id='").append(id).append("'");
		if(requiredCheck){
			sb.append(" class='").append("form-control validate[required]").append("'");
		}else{
			sb.append(" class='").append("form-control").append("'");
		}
		if(!"".equals(onchange)){
			sb.append(" onchange='").append(onchange).append("'");
		}
        sb.append(">");
        
        if(StringUtils.isNotBlank(headName)) {
            sb.append("<option value='").append(headValue).append("'>")
                    .append(headName).append("</option>");
        }
        int index = 0;
        LinkedHashMap<String, String> childMap = OperDbUtils.queryValueByKey(indexTag); 
        if(childMap != null){
        	for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
	            Entry<Object, String> entry = (Entry<Object, String>)it.next();  
	            sb.append("<option value='").append(entry.getKey()).append("'");
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
	public boolean isGroup() {
		return group;
	}
	public void setGroup(boolean group) {
		this.group = group;
	}
	public String getGrouplabel() {
		return grouplabel;
	}
	public void setGrouplabel(String grouplabel) {
		this.grouplabel = grouplabel;
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
	public String getIndexTag() {
		return indexTag;
	}
	public void setIndexTag(String indexTag) {
		this.indexTag = indexTag;
	}

	public boolean isRequiredCheck() {
		return requiredCheck;
	}

	public void setRequiredCheck(boolean requiredCheck) {
		this.requiredCheck = requiredCheck;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
