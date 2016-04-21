package com.brainsoon.appframe.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

public class ConstantsTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -2056599250024509108L;
	private String id = "";
	private String name = "";
	private String inputType = "";
	private String className;
	private String repository;
	private String selected;
	private String cssType;
	private String headerKey;
	private String headerValue;
	private String onchange;
	//要忽略的key 多个逗号分隔
	private String ignoreKeys;
	private IConstantsRepository constantsRepository = ConstantsRepository.getInstance();
	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		StringBuffer sb = new StringBuffer();
		// 获取常量类数据
		ConstantsMap map = constantsRepository.getConstantsMap(repository, className);
		//构造input
		buildInput(sb, map);
		writer.append(sb.toString());
		writer.flush();
		return 0;
	}
	@SuppressWarnings("unchecked")
	public void buildInput(StringBuffer sb,ConstantsMap map){
		List<String> ignoreKeysList = null;
		if(StringUtils.isNotBlank(ignoreKeys)){
			String [] ignoreKeysArray = StringUtils.split(ignoreKeys,",");
			ignoreKeysList = new ArrayList<String>(Arrays.asList(ignoreKeysArray));
		}
		Set<Map.Entry<Object, String>> datas = (Set<Map.Entry<Object, String>>) map.getConstantsSet();
		if(StringUtils.equalsIgnoreCase(inputType, "select")){
			sb.append("<select ");
			if(StringUtils.isBlank(id)){
				sb.append("id = \"").append(name).append("\" ");
			}else{
				sb.append("id = \"").append(id).append("\" ");
			}
			
			if(StringUtils.isNotBlank(cssType)){
				sb.append("class = \"").append(cssType).append("\" ");
			}
			if(StringUtils.isNotBlank(onchange)){
				sb.append("onchange = \"").append(onchange).append("\" ");
			}
			sb.append("name = \"").append(name).append("\" >");
			if(StringUtils.isNotBlank(headerValue)){
				if(StringUtils.isBlank(headerKey))
					headerKey = "";
				sb.append("<option value=\"").append(headerKey).append("\" >").append(headerValue).append("</option>");
			}
			for (Map.Entry<Object, String> m : datas) {
				String currentKey = m.getKey()+"";
				if(null != ignoreKeysList && ignoreKeysList.contains(currentKey)){
					continue;
				}
				sb.append("<option value=\"").append(currentKey).append("\" ");
				if(StringUtils.endsWithIgnoreCase(selected, currentKey)){
					sb.append("selected ");
				}
				sb.append(">").append(m.getValue()).append("</option>");
			}
			sb.append("</select>");
			
		}else{
			String labelInline = "";
			if(StringUtils.equalsIgnoreCase(inputType, "checkbox") || StringUtils.equalsIgnoreCase(inputType, "radio")){
				labelInline = inputType + "-inline";
			}
			
			for (Map.Entry<Object, String> m : datas) {
				String currentKey = m.getKey()+"";
				if(null != ignoreKeysList && ignoreKeysList.contains(currentKey)){
					continue;
				}
				if(StringUtils.isNotBlank(labelInline)){
					sb.append("<label class=\"").append(labelInline).append("\">");
				}
				sb.append("<input ");
				if(StringUtils.isBlank(id)){
					sb.append("id = \"").append(name).append("\" ");
				}else{
					sb.append("id = \"").append(id).append("\" ");
				}
				if(StringUtils.isNotBlank(cssType)){
					sb.append("class = \"").append(cssType).append("\" ");
				}
				if(StringUtils.endsWithIgnoreCase(selected, currentKey)){
					sb.append("checked=\"checked\" ");
				}
				sb.append("name = \"").append(name).append("\" type = \"").append(inputType);
				sb.append("\" value = \"").append(currentKey).append("\" valueDesc =\"").append(m.getValue()).append("\" />").append(m.getValue()).append(" ");
				if(StringUtils.isNotBlank(labelInline)){
					sb.append("</label>");
				}
			}
		}
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	public static void main(String[] args) {
//		ConstantsMap map = ConstantsRepository.getInstance().getConstantsMap("com.appframe.support.AppConstants", "Gender");
//		Set<Map.Entry<String, String>> ss = (Set<Map.Entry<String, String>>) map.getConstantsSet();
//		for (Map.Entry<String, String> m : ss) {
//			System.out.println("key:" + m.getKey() + " value" + m.getValue());
//		}
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public String getCssType() {
		return cssType;
	}
	public void setCssType(String cssType) {
		this.cssType = cssType;
	}
	public String getHeaderKey() {
		return headerKey;
	}
	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}
	public String getHeaderValue() {
		return headerValue;
	}
	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}
	public String getOnchange() {
		return onchange;
	}
	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}
	public String getIgnoreKeys() {
		return ignoreKeys;
	}
	public void setIgnoreKeys(String ignoreKeys) {
		this.ignoreKeys = ignoreKeys;
	}
}
