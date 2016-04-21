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

public class BsMenu extends RequestContextAwareTag {
	private static final long serialVersionUID = -2056599250024509108L;
	private String name;
	private String cnName;
    private String field;
    private boolean group;
    private String headName;
    private String headValue;
    private String selectedVal;
    private String indexTag;
    private boolean requiredCheck;
    private String onchange;
    private String maxNumber;
    private String id;
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		StringBuffer sb = new StringBuffer();
		//初始化
		boolean flag = false;
		LinkedHashMap<String, String> childMap = OperDbUtils.queryValueByKey(indexTag); 
		String nowField = id;
		if(id.contains("metadataMap")){
			id = id.replace("metadataMap['", "");
			id = id.replace("']", "");
			flag = true;
		}
		String spanValue = "";
		sb.append("  <div id=\"lead\" class=\"card\">	  ");
		sb.append("  <div class=\"topnav\"> ");
		sb.append("  <a id=\""+id+"_Div\" href=\"javascript:void(0);\" class=\"as\"> ");
		sb.append(" 	<span id=\"spanValue\" title=\"\" \">请选择</span>  ");
		sb.append("  </a> ");
		sb.append("  </div> ");
		//给隐藏域赋值
		if(flag){
			sb.append(" <input type=\"text\" value=\"\" id=\""+id+"\" name=\""+nowField+"\"/>  ");
		}else{
			sb.append(" <input type=\"text\" value=\"\" id=\""+id+"\" name=\""+name+"\" />  ");
		}
		//单选
		if(StringUtils.isNotBlank(maxNumber)){
			sb.append(" <input type=\"hidden\" value=\"1\" id=\""+id+"MaxNumber\"/>  ");
		//多选
		}else{
			sb.append(" <input type=\"hidden\" value=\"" + maxNumber + "\" id=\""+id+"MaxNumber\"/>  ");
		}
		sb.append(" </div> ");
		sb.append(" <div id=\""+id+"m2\" class=\"xmenu\" style=\"display: none;\"> ");
		sb.append(" <div class=\"select-info\"> ");
		sb.append(" <label class=\"top-label\">已选"+cnName+"：</label> ");
		sb.append(" <ul> ");
		sb.append(" </ul> ");
		sb.append(" <a  name=\"menu-confirm\" href=\"javascript:void(0);\" class=\"a-btn\"> ");
		sb.append(" <span class=\"a-btn-text\">确定</span>");
		sb.append(" </a> ");
		sb.append("  </div> ");
		sb.append(" <dl>");
		sb.append(" <dt class=\"open\">选择"+cnName+"</dt>");
		sb.append(" <dd><ul id=\"selectValue\">");
        if(childMap != null){
        	for(Iterator it = childMap.entrySet().iterator();it.hasNext();){  
	            Entry<Object, String> entry = (Entry<Object, String>)it.next();
	            if(StringUtils.isNotBlank(selectedVal)){
	            	if(selectedVal.contains(entry.getKey().toString())){
	            		spanValue = spanValue+entry.getValue().toString()+",";
	            	}
	            }
	          //判断是否选中
	            sb.append("<li rel=\"").append(entry.getKey()).append("\"");
	            sb.append(">").append(entry.getValue()).append("</li>");
	        }
        }
		if(spanValue.endsWith(",")){
			spanValue = spanValue.substring(0,spanValue.length()-1);
		}
        sb.append(" </ul></dd>");
		sb.append(" </dl>");
		sb.append(" </div>");
		sb.append("	 <script type=\"text/javascript\">");
		sb.append("	 $(document).ready(function(){");
		sb.append("	 $(\"#"+id+"_Div\").xMenu({");
		sb.append("	 width :600, ");
		sb.append("	 eventType: \"click\",");
		sb.append("	 dropmenu:\"#"+id+"m2\",");
		sb.append("	 hiddenID : \""+id+"\"");
		sb.append("	  });");
		if(StringUtils.isNotBlank(selectedVal)){
			//给隐藏域赋值，给title赋值属性
			sb.append("	if("+selectedVal+"!=null){ $(\"#"+id+"\").val('"+selectedVal+"'); $(\"#spanValue\").attr(\"title\",'"+spanValue+"'); $(\"#spanValue\").text('"+spanValue+"') }");
		}
		sb.append("	  });");
		sb.append("  </script>");
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
	
	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public boolean isGroup() {
		return group;
	}
	public void setGroup(boolean group) {
		this.group = group;
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

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(String maxNumber) {
		this.maxNumber = maxNumber;
	}
	
}
