package com.brainsoon.common.util.dofile.cnmarc;
/**
 * 子字段
 * @author zuo
 *
 */
public class CNMarcSubColumn {
	/** 子字段标识符，如：a */
	private String subField = null;
	/** 子字段内容，如：978-7-04-010937-5 */
	private String subContent = null;
	/**子字段所属字段对象**/
	private CNMarcColumnBase parent;
	
	public CNMarcSubColumn() {
	}
	public CNMarcSubColumn(String subField, String subContent) {
		super();
		this.subField = subField;
		this.subContent = subContent;
	}
	public String getSubField() {
		return subField;
	}
	public void setSubField(String subField) {
		this.subField = subField;
	}
	public String getSubContent() {
		return subContent;
	}
	public void setSubContent(String subContent) {
		this.subContent = subContent;
	}
	public CNMarcColumnBase getParent() {
		return parent;
	}
	public void setParent(CNMarcColumnBase parent) {
		this.parent = parent;
	}
}
