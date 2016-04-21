package com.brainsoon.common.util.dofile.cnmarc;

import java.util.ArrayList;
import java.util.List;
/**
 * 数据字段区对象
 * 每个字段（00- 字段除外）均由两个指示符以及随其后的任意数目的子字段组成。
 * 每个子字段的开头为一个子字段标识符，即一位子字段分隔符和一位标识该子字段的子字段代码。每条记录的长度，限定最大为99，999个字符，也可以由交换双方协商确定
 * @author zuo
 *
 */
public class CNMarcColumnBase{
	/** 字段标识(3位数字)，如：010 */
	private String character = null;
	/** 字段名称，如：单价 */
	private String fieldName = null;
	/** 指示符1，没有填空格 */
	private String identicator1 = null;
	/** 指示符2，没有填空格 */
	private String identicator2 = null;
	/** 字段内容，如：20120529145849.4  注意：00-字段此字段有效*/
	private String content = null;
	/** 子字段 可以无限扩展*/
	private List<CNMarcSubColumn> subColumns = new ArrayList<CNMarcSubColumn>();
	
	/*------------业务相关方法start----------------*/
	protected void load(String column,String character) throws Exception {
		if(CNMarcConstants.isRepalce2Marc){
			//两个标准转换
			character = CNMarcUtils.replaceCharacter(character, "1");
		}
		//  a978-7-04-010937-5b精装dCNY37.00RS
		setCharacter(character);
		List<byte[]> cols = CNMarcUtils.splitByteByChar(column.getBytes(CNMarcConstants.ENCODING),CNMarcConstants.F_CHAR31);
		//判断字段，如果是001和005字段，则无指示符和子字段标识符
		if(character.equalsIgnoreCase("001") || character.equalsIgnoreCase("005")){
			setContent(CNMarcUtils.byte2String(cols.get(0)));
		}else{
			//list中第一个应该为两个指示符，后面为子字段
			for (int i = 0; i < cols.size(); i++) {
				byte[] cbt = cols.get(i);
				if(i == 0){
					setIdenticator1(CNMarcUtils.byte2String(cbt[0]));
					setIdenticator2(CNMarcUtils.byte2String(cbt[1]));
				}else{
					//子字段
					String subField = CNMarcUtils.byte2String(cbt[0]);
					if(CNMarcConstants.isRepalce2Marc){
						//两个标准转换
						subField = CNMarcUtils.replaceSubField(subField, "1");
					}
					String subContent = CNMarcUtils.substring(CNMarcUtils.byte2String(cbt), 1);
					if(CNMarcConstants.isRepalce2Marc){
						//第n版转为n版 只针对205字段
						if(character.equals("205") && subField.equals("a")){
							subContent = subContent.replace("第", "");
						}
					}
					CNMarcSubColumn sub = new CNMarcSubColumn(subField, subContent);
					sub.setParent(this);
					subColumns.add(sub);
				}
			}
		}
	}
	/**
	 * 获取marc数据,带分隔符
	 */
	public String getMarcData() throws Exception{
		StringBuffer rtn = new StringBuffer();
		if(character.equalsIgnoreCase("001") || character.equalsIgnoreCase("005")){
			rtn.append(content);
		}else{
			rtn.append(identicator1).append(identicator2);
			for (CNMarcSubColumn cnMarcSubColumn : subColumns) {
				rtn.append(CNMarcConstants.F_CHAR31);
				rtn.append(cnMarcSubColumn.getSubField());
				rtn.append(cnMarcSubColumn.getSubContent());
			}
		}
		rtn.append(CNMarcConstants.F_CHAR30);
		return rtn.toString();
	}
	/**
	 * 组装字段描述(目前为marc数据分隔符替换为$显示)
	 * @param translate 是否翻译字段内容
	 * @param addDescribe 是否对翻译字段加前缀描述 (如果不翻译,则此参数无效) 如 ISBN:
	 * @return
	 * @throws Exception
	 */
	public String getDescription(boolean translate,boolean addDescribe) throws Exception{
		StringBuffer rtn = new StringBuffer();
		if(character.equalsIgnoreCase("001") || character.equalsIgnoreCase("005")){
			rtn.append(content);
		}else{
			rtn.append(identicator1).append(identicator2);
			for (CNMarcSubColumn cnMarcSubColumn : subColumns) {
				rtn.append("$");
				rtn.append(cnMarcSubColumn.getSubField());
				if(translate){
					rtn.append(CNMarcUtils.translateCNMarcSubColumn(cnMarcSubColumn, addDescribe));
				}else{
					rtn.append(cnMarcSubColumn.getSubContent());
				}
			}
		}
		return rtn.toString();
//		return CNMarcUtils.replaceStr(getMarcData(), CNMarcConstants.F_CHAR31, CNMarcConstants.F_CHAR36);
	}
	/*------------业务相关方法end----------------*/
	/*set get方法*/
	public String getCharacter() {
		return character;
	}
	public void setCharacter(String character) {
		this.character = character;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getIdenticator1() {
		return identicator1;
	}
	public void setIdenticator1(String identicator1) {
		this.identicator1 = identicator1;
	}
	public String getIdenticator2() {
		return identicator2;
	}
	public void setIdenticator2(String identicator2) {
		this.identicator2 = identicator2;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<CNMarcSubColumn> getSubColumns() {
		return subColumns;
	}
	public void setSubColumns(List<CNMarcSubColumn> subColumns) {
		this.subColumns = subColumns;
	}
	
}
