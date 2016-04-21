package com.brainsoon.common.tag;

import javax.servlet.jsp.JspWriter;

import org.springframework.web.servlet.tags.RequestContextAwareTag;

public class RadioTag extends RequestContextAwareTag {

	private static final long serialVersionUID = 1L;
	private String name;
    private String type;
    private String selectedVal;
    private String indexTag;
    private String id;
    private boolean requiredCheck;
    private String onchange;
	@Override
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		StringBuffer sb = new StringBuffer();
		sb.append("<input type='radio' name='").append(name).append("'");
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	

}
