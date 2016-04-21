package com.brainsoon.common.tag;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.brainsoon.appframe.util.StringUtil;
import com.brainsoon.common.support.OperDbUtils;
import com.brainsoon.common.util.BeanFactoryUtil;
import com.brainsoon.common.util.date.DatePickerUtil;
import com.brainsoon.system.service.IBookService;

public class DateTimeTag extends RequestContextAwareTag {
	private static final long serialVersionUID = -2056599250024509108L;
	private String property;

	private String cssClass = "inputnotnull";

	private String clearImage = "images/lookupClear.gif";

	private String dateImage = "images/calendar.gif";

	private String style;
	
	private boolean readOnly = true;
	
	private int startYear = 0;
	private int endYear = 0;
	
	private String callBackJS = "";
	
	private String onFocus;
	
	private String showMode;

	private String dateFmt = "yyyy-MM-dd";
	
	private String id;
	
	public String getDateFmt() {
		return dateFmt;
	}

	public void setDateFmt(String dateFmt) {
		this.dateFmt = dateFmt;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public String getClearImage() {
		return clearImage;
	}

	public void setClearImage(String clearImage) {
		this.clearImage = clearImage;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String styleClass) {
		this.cssClass = styleClass;
	}

	public DateTimeTag() {
	}

	/**
	 * @return Returns the property.
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param property
	 *            The property to set.
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	protected int doStartTagInternal() throws Exception {
		JspWriter writer = pageContext.getOut();
		writer.append(buildText());
		writer.flush();
		return 0;
	}

	private String buildText() throws JspException{
//		String value = "";
//		if(getValue(getProperty())!=null ){
//			value= getValue(getProperty()).toString();
//		}
		String textContent = DatePickerUtil.createDatePicker(getProperty(),id, "", getCssClass(), getStyle(), getOnFocus(), isReadOnly(), buildDateFmt(), getShowMode(),callBackJS);
		return textContent;
	}
	private  String buildDateFmt() {
		if(StringUtils.isNotBlank(showMode)){
			if(showMode.equals("1")){
				dateFmt = "yyyy-MM-dd";
			}else if(showMode.equals("2")){
				dateFmt = "yyyy-MM";
			}else if(showMode.equals("3")){
				dateFmt = "yyyy-MM-dd HH:mm:ss";
			}else if(showMode.equals("4")){
				dateFmt = "yyyy-MM-dd HH:mm";
			}else if(showMode.equals("5")){
				dateFmt = "H:m:s";
			}else if(showMode.equals("6")){
				dateFmt = "MMMM d,yyyy";
			}else{
				dateFmt = "yyyy-MM-dd";
			}
		}

		return dateFmt;
	}
	public String getDateImage() {
		return dateImage;
	}

	public void setDateImage(String dateImage) {
		this.dateImage = dateImage;
	}

	public String getCallBackJS() {
		return callBackJS;
	}

	public void setCallBackJS(String callBackJS) {
		this.callBackJS = callBackJS;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	
	public String getOnFocus() {
		return onFocus;
	}

	public void setOnFocus(String onFocus) {
		this.onFocus = onFocus;
	}

	public String getShowMode() {
		return showMode;
	}

	public void setShowMode(String showMode) {
		this.showMode = showMode;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public static void main(String[] srgs){
		DateTimeTag dateTimeTag = new DateTimeTag();
	//	dateTimeTag.getOnFocusStr();
		//return result;
	}

	
}
