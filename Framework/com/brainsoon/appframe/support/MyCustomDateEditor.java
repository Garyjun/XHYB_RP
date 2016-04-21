/**
 * @FileName: MyCustomDateEditor.java
 * @Package:com.brainsoon.appframe.support
 * @Description:
 * @author: tanghui
 * @date:2015-4-2 上午10:50:40
 * Modification History:
 * Date         Author      Version       Description
 * ------------------------------------------------------------------
 * 2015-4-2       TANGHUI        1.0         1.0 Version
 */
package com.brainsoon.appframe.support;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * @ClassName: MyCustomDateEditor
 * @Description:
 * @author: tanghui
 * @date:2015-4-2 上午10:50:40
 */
public class MyCustomDateEditor extends PropertyEditorSupport {

	public MyCustomDateEditor(DateFormat dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		exactDateLength = -1;
	}

	public MyCustomDateEditor(DateFormat dateFormat, boolean allowEmpty,
			int exactDateLength) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = exactDateLength;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (allowEmpty && !StringUtils.hasText(text)) {
			setValue(null);
		} else {
			if (text != null && exactDateLength >= 0
					&& text.length() != exactDateLength)
				throw new IllegalArgumentException((new StringBuilder(
						"Could not parse date: it is not exactly"))
						.append(exactDateLength).append("characters long")
						.toString());
			try {
				//唐辉add：如果是不带时间后缀的日期，则增加一段后缀，防止转换失败
				if(text.length() == 10){
					text += " 00:00:00";
				}
				setValue(dateFormat.parse(text));
			} catch (ParseException ex) {
				throw new IllegalArgumentException((new StringBuilder(
						"Could not parse date: ")).append(ex.getMessage())
						.toString(), ex);
			}
		}
	}

	public String getAsText() {
		Object obj = getSource();
		Date value = (Date) getValue();
		return value == null ? "" : dateFormat.format(value);
	}

	private final DateFormat dateFormat;
	private final boolean allowEmpty;
	private final int exactDateLength;
}
