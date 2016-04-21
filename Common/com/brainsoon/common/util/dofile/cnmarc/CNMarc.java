package com.brainsoon.common.util.dofile.cnmarc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
/**
 * CNMarc实体类，封装相应处理方法
 * @author zuo
 *
 */
public class CNMarc {
	private static final Logger logger = Logger.getLogger(CNMarcUtils.class);
	
	/*记录头标区24个字符长当做id,头标区字段标识为000*/
	private String headArea;
	/*地址目次区*/
	private CNMarcCatalog catalog;
	/*数据字段区*/
	private Map<String,CNMarcColumnBase> columns;
	/*marc数据类型，0  国图    、 1 CALIS  注：国图头标区的oam2，CALIS为nam */
	private int cnMarcType = 0;
	/**
	 * 根据marc数据装载对象
	 * @param marc
	 */
	@SuppressWarnings("unchecked")
	protected void load(String marc){
		logger.debug("开始装载数据");
		try {
			String headAreaId = CNMarcUtils.substring(marc, 0, 24);
			//处理头标区
			setHeadArea(headAreaId);
			//处理目次区
			//数据基地址12-16 (总长度为头标区+目次区长度，包含休止符)
			int titleLength = Integer.parseInt(CNMarcUtils.substring(marc, 12, 5));
			String catalogStr = CNMarcUtils.substring(marc, 24, (titleLength-24));
			setCatalog(new CNMarcCatalog());
			getCatalog().load(catalogStr);
			
			//处理数据区,根据目次区切割
			setColumns(CNMarcUtils.splitColumnsByCatalog(catalog, CNMarcUtils.substring(marc,titleLength)));
		} catch (Exception e) {
			logger.debug("加载数据区异常",e);
		}
		logger.debug("装载数据完毕");
	}
	/**
	 * 获取marc数据,带分隔符
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public String getMarcData() throws Exception{
		logger.debug("获取marc数据start");
		StringBuffer rtn = new StringBuffer();
		//头标区 + 目次区 + 数据区 +  记录分隔符
		rtn.append(headArea).append(catalog.getMarcData());
		for (Iterator ite = columns.entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			String colkey = (String) entry.getKey();
			CNMarcColumnBase colValue = (CNMarcColumnBase) entry.getValue();
			rtn.append(colValue.getMarcData());
		}
		rtn.append(CNMarcConstants.F_CHAR29);
		logger.debug("获取marc数据end");
		return rtn.toString();
	}
	/**
	 * 组装字段描述(目前为marc数据分隔符替换为$显示)
	 * @param translate 是否翻译字段内容
	 * @param addDescribe 是否对翻译字段加前缀描述 (如果不翻译,则此参数无效) 如 ISBN:
	 * @return String
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String getDescription(boolean translate,boolean addDescribe)throws Exception{
		StringBuffer rtn = new StringBuffer();
		rtn.append("000").append(CNMarcConstants.FIELDTAB).append(headArea).append(CNMarcConstants.NEWLINESTR);
		for (Iterator ite = columns.entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			String colkey = (String) entry.getKey();
			CNMarcColumnBase colValue = (CNMarcColumnBase) entry.getValue();
			rtn.append(CNMarcUtils.substring(colkey, 0, 3)).append(CNMarcConstants.FIELDTAB).append(colValue.getDescription(translate,addDescribe)).append(CNMarcConstants.NEWLINESTR);
		}
		//过滤掉三种分隔符
		return CNMarcUtils.filterControlCharacter(rtn.toString(),0);
	}
	/**
	 * 组装字段描述(目前为marc数据分隔符替换为$显示),加换行显示
	 */
	public String getDescription() throws Exception{
		return getDescription(false,false);
	}
	/**
	 * 获取字段或者子字段详细信息(默认不翻译字段中的内容)
	 * @param desc 如：ISBN
	 * @return String
	 */
	public String getFieldDesc(String desc){
		return getFieldDesc(desc, false,CNMarcConstants.repeatFieldMax,null,"，");
	}
	/**
	 * 获取字段或者子字段详细信息(默认不翻译字段中的内容)
	 * @param desc
	 * @param subSplit 子字段间的分隔符
	 * @return
	 */
	public String getFieldDesc(String desc,String subSplit){
		return getFieldDesc(desc, false,CNMarcConstants.repeatFieldMax,null,subSplit);
	}
	/**
	 * 获取字段或者子字段详细信息(默认不翻译字段中的内容)
	 * @param desc
	 * @param max 同字段标识符下获取字段数
	 * @param subSplit 子字段间的分隔符
	 * @return
	 */
	public String getFieldDesc(String desc,int max,String subSplit){
		return getFieldDesc(desc, false,max,null,subSplit);
	}
	/**
	 * 获取字段或者子字段详细信息
	 * @param desc 如：ISBN
	 * @param translate 是否要对字段内容进行翻译，如阅读对象中 a:普通青少年
	 * @param max 同字段标识符下获取字段数
	 * @param split 字段允许重复时，多个字段内容间的连接符  如：，
	 * @param subSplit 子字段允许重复时，多个子字段内容间的连接符  如：；
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getFieldDesc(String desc,boolean translate,int max,String split,String subSplit){
		String mapvalue = CNMarcFieldsConstants.CNFILEDS.get(desc);
		logger.debug("查询字段：" + desc + ",值：" + mapvalue);
		if(null == mapvalue || desc.length() <= 3){
			return null;
		}
		String[] marks = mapvalue.split(CNMarcFieldsConstants.FILEDSPLITSTR);
		//数组长度为 2 - 4(至少长2)。 注：1 子字段标识符 、2 字符开始位置 、 3 字符数 、4 翻译字段
		//先根据字段标识号取出 CNMarcColumnBase 对象，后分析内容
		/*为了处理可以重复的字段，需要循环取出key相等的对象，认为如: 606 , 606-1 ,606-2  相等，都取出*/
		String keyField = "-1";
		if(desc.length() > 3){
			keyField = desc.substring(0, 3);//字段标识符在map的key的前三位
		}
		String currentSubFieldId = marks[0];//子字段标识符
		int currentStart = Integer.parseInt(marks[1]);//字符开始位置
		int currentCount = 0;//字符数
		Map translateMap = null; //翻译串
		if(currentStart >= 0){
			currentCount = Integer.parseInt(marks[2]);
			if(translate && marks.length > 3){
				translateMap = CNMarcUtils.JSON2Map(marks[3]);
			}
		}
		if(null == split || split.equals("")){
			if(CNMarcConstants.fieldContentSplit.containsKey(keyField)){
				split = CNMarcConstants.fieldContentSplit.get(keyField);
			}else{
				split = "";
			}
		}
		StringBuffer strBuffer = new StringBuffer();//字段内容
		//先最多支持n个重复字段
		for(int j = 0;j < max;j ++){
			String nId = keyField;
			if(j > 0){
				nId = keyField +"-" + j;
			}
			CNMarcColumnBase column = columns.get(nId);
			if(null != column){
				try {
					if(strBuffer.length() > 0){//加入字段间分隔符
						strBuffer.append(split);
					}
					StringBuffer subBuffer = new StringBuffer();//子字段内容
					//获取子字段
					List<CNMarcSubColumn> subs = column.getSubColumns();
					for (CNMarcSubColumn cnMarcSubColumn : subs) {
						if(cnMarcSubColumn.getSubField().equals(currentSubFieldId)){
							String contents = cnMarcSubColumn.getSubContent();
							if(subBuffer.length() > 0){//加入子字段间分隔符
								subBuffer.append(subSplit);
							}
							if(currentStart == -1){
								subBuffer.append(CNMarcUtils.substring(contents, currentStart));
							}else{
								String subStr = CNMarcUtils.substring(contents, currentStart, currentCount);
								subBuffer.append(CNMarcUtils.translateSubContentByMap(subStr,translateMap));
							}
						}
					}
					strBuffer.append(subBuffer);
					
				} catch (Exception e) {
					logger.debug("查询字段异常",e);
					return "";
				}
			}
		}
		
		logger.debug("该字段内容为："+strBuffer.toString());
		return strBuffer.toString();
	}
	
	public String getHeadArea() {
		return headArea;
	}
	public void setHeadArea(String headArea) {
		this.headArea = headArea;
	}
	public CNMarcCatalog getCatalog() {
		return catalog;
	}
	public void setCatalog(CNMarcCatalog catalog) {
		this.catalog = catalog;
	}
	public Map<String, CNMarcColumnBase> getColumns() {
		return columns;
	}
	public void setColumns(Map<String, CNMarcColumnBase> columns) {
		this.columns = columns;
	}
	public int getCnMarcType() {
		return cnMarcType;
	}
	public void setCnMarcType(int cnMarcType) {
		this.cnMarcType = cnMarcType;
	}
	
	
}
