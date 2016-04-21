package com.brainsoon.common.util.dofile.cnmarc;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CNMarc相关常量类
 * @author zuo
 *
 */
public class CNMarcConstants {
	/**
	 * "$"作为子字段标识的第一个符号 US Unit Separator (单元分隔符) 用于分割如子字段
	 */
	public static final char F_CHAR31 = 31; 
	/**
	 * //用于字段之间,休止符 RS
	 */
	public static final char F_CHAR30 = 30; 
	/**
	 * 结束符 GS Group Separator ( 记录分隔符)单条marc数据结束
	 */
	public static final char F_CHAR29 = 29; 
	/**
	 * 用于显示,$
	 */
	public static final char F_CHAR36 = 36;
	public static final char F_CHAR10 = 10; 
	/**
	 * 换行符
	 */
	public static final String NEWLINESTR = System.getProperty("line.separator");
	/**
	 * 字段标识(字段号)与内容之间的分割串,用于显示
	 */
	public static final String FIELDTAB = "		";
	/**
	 * cnmarc处理编码,按GBK处理
	 */
	public static final String ENCODING = "GBK"; 
	
	/**
	 * excel导出时，多子字段合并时，内容的连接符号
	 */
	public static final String CONNECTOREXCELFIELD = "，";
	/**
	 * cnmarc里最大支持重复字段数，适当调整大小
	 */
	public static final int repeatFieldMax = 10;
	/**
	 * 两种marc标准,是否进行转换
	 */
	public static final boolean isRepalce2Marc = true;
	/**
	 * 丛书的字段标识符
	 */
	public static final Map<String,String> seriesCharacter = new HashMap<String,String>();
	/**
	 * 字段允许重复的情况下,重复后,内容之间的连接符。
	 */
	public static final Map<String,String> fieldContentSplit = new HashMap<String,String>();
	
	/**
	 * Map中的key是国图标准,value为calis标准
	 */
	public static final Map<String,String> replaceCharacter = new HashMap<String,String>();
	public static final Map<String,String> replaceSubField = new HashMap<String,String>();
	/**
	 * 开本尺寸转换
	 */
	public static final Map<String,String> dimension = new LinkedHashMap<String,String>();
	
	static{
		//字段标识符需要转换的内容
		replaceCharacter.put("461", "410");
		replaceCharacter.put("462", "411");
		//子字段标识符需要转换的内容
		replaceSubField.put("9", "A");
		
		//是否为丛书的标识
		seriesCharacter.put("461", "国图丛书");
		seriesCharacter.put("410", "calis丛书");
		
		//字段允许重复的情况下,重复后,内容之间的连接符。
		fieldContentSplit.put("606", "；");
		//开本尺寸 key 为长(高)
		dimension.put("37","8开");
		dimension.put("38","8开");
		dimension.put("40","大8开");
		dimension.put("41","大8开");
		dimension.put("42","大8开");
		
		dimension.put("26","16开");
		dimension.put("22","16开");
		dimension.put("23","16开");
		dimension.put("24","16开");
		dimension.put("25","16开");
		dimension.put("28","大16开");
		dimension.put("29","大16开");
		dimension.put("30","大16开");
		
		dimension.put("18","32开");
		dimension.put("19","32开");
		dimension.put("16","32开");
		dimension.put("17","32开");
		dimension.put("20","32开");
		dimension.put("21","大32开");
		
		dimension.put("10×14cm","128开");
		
		dimension.put("10","64开");
		dimension.put("11","64开");
		dimension.put("12","64开");
		dimension.put("16","64开");
		dimension.put("17","64开");
		
		dimension.put("13","大64开");
		dimension.put("14","大64开");
		dimension.put("15","大64开");
		
		
	}
	
	/**
	 * 导出excel需要的常量数据
	 */
	public static final String excelStart = "<?xml version='1.0' encoding='utf-8'?>"+CNMarcConstants.NEWLINESTR
			+"<?mso-application progid='Excel.Sheet'?>"+CNMarcConstants.NEWLINESTR
			+"<Workbook xmlns='urn:schemas-microsoft-com:office:spreadsheet'"+CNMarcConstants.NEWLINESTR
			+"  xmlns:o='urn:schemas-microsoft-com:office:office'"+CNMarcConstants.NEWLINESTR
			+"  xmlns:x='urn:schemas-microsoft-com:office:excel'"+CNMarcConstants.NEWLINESTR
			+"  xmlns:ss='urn:schemas-microsoft-com:office:spreadsheet'"+CNMarcConstants.NEWLINESTR
			+"  xmlns:html='http://www.w3.org/TR/REC-html40'>"+CNMarcConstants.NEWLINESTR
			+"  <DocumentProperties xmlns='urn:schemas-microsoft-com:office:office'>"+CNMarcConstants.NEWLINESTR
			+"    <Author>中版集团数字传媒有限公司</Author>"+CNMarcConstants.NEWLINESTR;
	public static final String excelMiddle = "    <Version>11.6568</Version>"+CNMarcConstants.NEWLINESTR
			+"  </DocumentProperties>"+CNMarcConstants.NEWLINESTR
			+"  <OfficeDocumentSettings xmlns='urn:schemas-microsoft-com:office:office'>"+CNMarcConstants.NEWLINESTR
			+"    <TargetScreenSize>1024x768</TargetScreenSize>"+CNMarcConstants.NEWLINESTR
			+"  </OfficeDocumentSettings>"+CNMarcConstants.NEWLINESTR
			+"  <ExcelWorkbook xmlns='urn:schemas-microsoft-com:office:excel'>"+CNMarcConstants.NEWLINESTR
			+"    <WindowHeight>10005</WindowHeight>"+CNMarcConstants.NEWLINESTR
			+"    <WindowWidth>10005</WindowWidth>"+CNMarcConstants.NEWLINESTR
			+"    <WindowTopX>120</WindowTopX>"+CNMarcConstants.NEWLINESTR
			+"    <WindowTopY>135</WindowTopY>"+CNMarcConstants.NEWLINESTR
			+"    <ProtectStructure>False</ProtectStructure>"+CNMarcConstants.NEWLINESTR
			+"    <ProtectWindows>False</ProtectWindows>"+CNMarcConstants.NEWLINESTR
			+"  </ExcelWorkbook>"+CNMarcConstants.NEWLINESTR
			+"  <Styles>"+CNMarcConstants.NEWLINESTR
			+"    <Style ss:ID='Default' ss:Name='Normal'>"+CNMarcConstants.NEWLINESTR
			+"      <Alignment ss:Vertical='Center'/>"+CNMarcConstants.NEWLINESTR
			+"      <Borders/>"+CNMarcConstants.NEWLINESTR
			+"      <Font ss:FontName='宋体' x:CharSet='134' ss:Size='12'/>"+CNMarcConstants.NEWLINESTR
			+"      <Interior/>"+CNMarcConstants.NEWLINESTR
			+"      <NumberFormat/>"+CNMarcConstants.NEWLINESTR
			+"      <Protection/>"+CNMarcConstants.NEWLINESTR
			+"    </Style>"+CNMarcConstants.NEWLINESTR
			+"    <Style ss:ID='s23'>"+CNMarcConstants.NEWLINESTR
			+"      <NumberFormat ss:Format='0.00_ '/>"+CNMarcConstants.NEWLINESTR
			+"    </Style>"+CNMarcConstants.NEWLINESTR
			+"    <Style ss:ID='s24'>"+CNMarcConstants.NEWLINESTR
			+"      <NumberFormat ss:Format='yyyy/m/d\\ h:mm;@'/>"+CNMarcConstants.NEWLINESTR
			+"    </Style>"+CNMarcConstants.NEWLINESTR
			+"    <Style ss:ID='s25'>"+CNMarcConstants.NEWLINESTR
			+"      <NumberFormat ss:Format='yyyy/m/d;@'/>"+CNMarcConstants.NEWLINESTR
			+"    </Style>"+CNMarcConstants.NEWLINESTR
			+"    <Style ss:ID='s26'>"+CNMarcConstants.NEWLINESTR
			+"      <Alignment ss:Vertical='Center' ss:WrapText='1'/>"+CNMarcConstants.NEWLINESTR
			+"    </Style>"+CNMarcConstants.NEWLINESTR
			+"    <Style ss:ID='s27'>"+CNMarcConstants.NEWLINESTR
			+"      <NumberFormat ss:Format='True/False'/>"+CNMarcConstants.NEWLINESTR
			+"    </Style>"+CNMarcConstants.NEWLINESTR
			+"  </Styles>"+CNMarcConstants.NEWLINESTR;
	public static final String excelColumn = "      <Column ss:AutoFitWidth='0' ss:Width='115'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='90'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='115'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:Index='9' ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:Index='23' ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:AutoFitWidth='0' ss:Width='200'/>"+CNMarcConstants.NEWLINESTR
			+"      <Column ss:Index='33' ss:AutoFitWidth='0' ss:Width='120'/>"+CNMarcConstants.NEWLINESTR;
	
	public static final String excelWorksheetOptions = "    <WorksheetOptions xmlns='urn:schemas-microsoft-com:office:excel'>"+CNMarcConstants.NEWLINESTR
			+"      <Unsynced/>"+CNMarcConstants.NEWLINESTR
			+"      <Selected/>"+CNMarcConstants.NEWLINESTR
			+"      <Panes>"+CNMarcConstants.NEWLINESTR
			+"        <Pane>"+CNMarcConstants.NEWLINESTR
			+"          <Number>3</Number>"+CNMarcConstants.NEWLINESTR
			+"          <ActiveRow>1</ActiveRow>"+CNMarcConstants.NEWLINESTR
			+"          <ActiveCol>1</ActiveCol>"+CNMarcConstants.NEWLINESTR
			+"        </Pane>"+CNMarcConstants.NEWLINESTR
			+"      </Panes>"+CNMarcConstants.NEWLINESTR
			+"      <ProtectObjects>False</ProtectObjects>"+CNMarcConstants.NEWLINESTR
			+"      <ProtectScenarios>False</ProtectScenarios>"+CNMarcConstants.NEWLINESTR
			+"    </WorksheetOptions>"+CNMarcConstants.NEWLINESTR;
	
	/**
	 * CNMarc数据类型，目前主要有国图，calis两种
	 */
	public enum CNMarcStandard {   
		CN,CALIS
	} 
}
