package com.brainsoon.common.tag;

import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

public class DictSelectTag extends RequestContextAwareTag {
	private static final long serialVersionUID = 6168036718779986830L;
	private String name;
    private String type;
    private String selectedVal;
    private String indexTag;
    private String id;

	@Override
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"col-md-9\">");
		sb.append("     <select type=\"multiSelEle\" multiple=\"multiple\" name=\""+name+"\" id=\""+id+"\" >");
		sb.append("		</select>");
		sb.append("</div>");
		sb.append("<script type=\"text/javascript\">");
		if (StringUtils.isNotBlank(selectedVal)) {
			sb.append("		dyAddOpts(\""+indexTag+"\",\"edit\",\"" + selectedVal +"\", \"" +  id + "\""+ ");");
		}else {
			sb.append("		dyAddOpts(\""+indexTag+"\",\"edit\",\"\", \"" +  id + "\""+ ");");
		}
		sb.append("</script>");
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

}
