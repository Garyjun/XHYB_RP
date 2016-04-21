package com.brainsoon.common.util.dofile.cnmarc;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 目次区对象
 * @author zuo
 *
 */
public class CNMarcCatalog {
	private Map<String,String> columnCatalogs;
	/**
	 * 根据目次数据加载对象
	 * @param catalog
	 * @throws UnsupportedEncodingException 
	 */
	protected void load(String catalog) throws UnsupportedEncodingException{
		if(null != catalog && !"".equalsIgnoreCase(catalog)){
			columnCatalogs = new LinkedHashMap<String, String>();
			String cur = "";
			String catalogId = "";
			String catalogContent = "";
			for(int i = 0 ; i < catalog.getBytes(CNMarcConstants.ENCODING).length-1; i += 12){
				cur = CNMarcUtils.substring(catalog, i, 12);
				catalogId = CNMarcUtils.substring(cur, 0, 3);
				if(CNMarcConstants.isRepalce2Marc){
					//两个标准转换
					catalogId = CNMarcUtils.replaceCharacter(catalogId, "1");
				}
				catalogContent = CNMarcUtils.substring(cur, 3, 9);
				//处理重复key，自动加标识
				if(columnCatalogs.containsKey(catalogId)){
					String nId = "";
					//最多支持n个重复字段
					for(int j = 1;j < CNMarcConstants.repeatFieldMax;j ++){
						nId = catalogId +"-" + j;
						if(!columnCatalogs.containsKey(nId)){
							catalogId = nId;
							break;
						}
					}
				}
				columnCatalogs.put(catalogId, catalogContent);
			}
			
		}
	}
	/**
	 * 获取marc数据,带分隔符
	 */
	@SuppressWarnings("rawtypes")
	protected String getMarcData() throws Exception{
		StringBuffer rtn = new StringBuffer();
		for (Iterator ite = columnCatalogs.entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			String colkey = (String) entry.getKey();
			String colValue = (String) entry.getValue();
			//由于重复字段的原因,处理key,只取前三位
			rtn.append(CNMarcUtils.substring(colkey, 0, 3)).append(colValue);
		}
		rtn.append(CNMarcConstants.F_CHAR30);
		return rtn.toString();
	}
	public Map<String, String> getColumnCatalogs() {
		return columnCatalogs;
	}

	public void setColumnCatalogs(Map<String, String> columnCatalogs) {
		this.columnCatalogs = columnCatalogs;
	}
}
