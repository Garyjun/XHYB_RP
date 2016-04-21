package com.brainsoon.common.util.dofile.cnmarc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * CNMarc对外提供的方法
 * @author zuo
 *
 */
public class ICNMarcService {
	public static final Logger logger = Logger.getLogger(ICNMarcService.class);
	/**
	 * 根据ISO文件加载CNMarc对象
	 * 
	 * @param filePath 文件全路径
	 * @param encode 文件编码
	 * @return List<CNMarc>
	 */
	public static List<CNMarc> loadCNMarcFromISO(String filePath,String encode) {
		logger.info("进入loadCNMarcFromISO方法");
		List<CNMarc> rtn = CNMarcService.loadCNMarcFromISO(filePath,encode);
		logger.info("退出loadCNMarcFromISO方法");
		return rtn;
	}
	/**
	 * 根据marc数据加载marc对象，单条
	 * @param marc
	 * @return CNMarc
	 */
	public static CNMarc loadCNMarcFromString(String marc){
		logger.info("进入loadCNMarcFromString方法");
		CNMarc cnmarc = null;
		if(null != marc){
			cnmarc = new CNMarc();
			cnmarc.load(marc);
		}
		logger.info("退出loadCNMarcFromString方法");
		return cnmarc;
	}
	/**
	 * 根据CNMarc List 对象生成ISO文件
	 * @param destFile 文件全路径
	 * @param data Marc列表数据
	 * @param marcStandard marc数据标准,枚举类型
	 */
	public static void createCNMarcFile(String destFile, List<CNMarc> data,CNMarcConstants.CNMarcStandard marcStandard) {
		logger.info("进入createCNMarcFile方法");
		CNMarcService.createCNMarcFile(destFile, data,marcStandard);
		logger.info("退出createCNMarcFile方法");
	}
	/**
	 * 根据CNMarc对象生成ISO文件
	 * @param destFile
	 * @param cnmarc
	 * @param marcStandard marc数据标准,枚举类型
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void createCNMarcFile(String destFile,CNMarc cnmarc,CNMarcConstants.CNMarcStandard marcStandard){
		logger.info("进入createCNMarcFile方法");
		List lts = new ArrayList();
		lts.add(cnmarc);
		createCNMarcFile(destFile,lts,marcStandard);
		logger.info("退出createCNMarcFile方法");
	}
	/**
	 * 生成CNMarc文件，excel格式
	 * @param destFile 文件名
	 * @param isExport327 是否导出327字段
	 * @param data CNMarc数据
	 */
	public static void createCNMarcFile(String destFile,boolean isExport327,List<CNMarc> data){
		logger.info("进入createCNMarcFile方法");
		String titleJSON = "[{'ISBN':'010ISBN'}," +
				"{'书名':''}," + //service处理
				"{'丛书名':'225丛编题名'}," +
				"{'并列书名':'200并列正题名'}," +
				"{'着作者':''}," + //service处理
				"{'出版地':'210出版(发行)地'}," +
				"{'出版者':'210出版(发行)者'}," +
				"{'首版年月':''}," +
				"{'本版年月':'210出版(发行)时间'}," +
				"{'本版版次':'205版本说明'}," +
				"{'本次印刷年月':'210制作时间'}," +
				"{'印次':'205印次'}," +
				"{'定价':'010获得方式和／或定价'}," +
				"{'装帧':'010装订方式'}," +
				"{'页数':'215页数或卷册数(数量及其单位)'}," +
				"{'开本':'215尺寸或开本'}," +  //(需换算为cm 厘米)
				"{'尺寸':'215尺寸或开本'}," +  //(需换算为cm 厘米)
				"{'附件类型和数量':'215附件'}," +
				"{'中图法分类':'690分类号'}," +
				"{'语种':'101正文、声道等语种'}," +
				"{'主题词':''},"; //service处理
				if(isExport327){
					titleJSON +="{'目录':'327附注内容'},";
				}
		 titleJSON += "{'内容提要':'330提要或文摘'}," +
				"{'着作者简介':'314责任者附注'}," +
				"{'精彩页':''}," +
				"{'获奖情况':''}," +
				"{'读者对象':'333读者对象'}," +
				"{'出版状态':''}," +
				"{'每包册数':''}," +
				"{'封面文件':''}," +
				"{'样本地址':'856样本地址'}" +
				"]";
		CNMarcService.createCNMarcFile(destFile, titleJSON, data);
		logger.info("退出createCNMarcFile方法");
	}
	
	/**
	 * 手动生成Marc对象
	 * @param Map<String,CNMarcColumnBase> columns
	 * @return CNMarc
	 * @throws Exception
	 */
	public static CNMarc createCNMarcByMaps(Map<String,CNMarcColumnBase> columns) throws Exception{
		logger.info("进入createCNMarcByMaps方法");
		CNMarc cnmarc = CNMarcService.createCNMarcByMaps(columns);
		logger.info("退出createCNMarcByMaps方法");
		return cnmarc;
	}
	/**
	 * 根据数据库marc字段生成marc对象
	 * @param columnsStr
	 * @return
	 * @throws Exception
	 */
	public static CNMarc createCNMarcByColumnsStr(String marc) throws Exception{
		logger.info("进入createCNMarcByColumnsStr方法");
		CNMarc cnmarc = CNMarcService.createCNMarcByColumnsStr(marc);
		logger.info("退出createCNMarcByColumnsStr方法");
		return cnmarc;
	}
	/**
	 * 获取CNMarc对象，详细说明(字段翻译过)
	 * @param marc
	 * @return Map<String,String> 
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String,String> getTranslateDesc(CNMarc marc) throws UnsupportedEncodingException{
		logger.info("进入getTranslateDesc方法");
		Map<String,String> rtn = CNMarcService.getTranslateDesc(marc);
		logger.info("退出getTranslateDesc方法");
		return rtn;
	}
	/**
	 * 获取CNMarc对象，详细说明（字段未经翻译）
	 * @param marc
	 * @return Map<String,String>
	 * @throws Exception
	 */
	public static Map<String,String> getNoTranslateDesc(CNMarc marc) throws Exception{
		logger.info("进入getNoTranslateDesc方法");
		Map<String,String> rtn = CNMarcService.getNoTranslateDesc(marc);
		logger.info("退出getNoTranslateDesc方法");
		return rtn;
	}
	/**
	 * 按字段标识符获取内容
	 * @param characters 字段标识符数组
	 * @param split	    字段间内容分隔符
	 * @param max		最大获取的字段数
	 * @param subFields	需要获取的子字段标识符Map， key为标识符，value为子字段间分隔符
	 * @param repeat	是否获取重复的子字段
	 * @param cnMarc marc数据
	 * @return
	 */
	public static String getFieldDescByCharacter(String [] characters,String split,int max,Map<String,String> subFields,boolean repeat,CNMarc cnMarc){
		logger.info("进入getFieldDescByCharacter方法");
		String rtn = CNMarcService.getFieldDescByCharacter(characters, split, max, subFields, repeat,cnMarc);
		logger.info("退出getFieldDescByCharacter方法");
		return rtn;
	}
	/**
	 * 获取书名
	 * @param marc
	 * @return String
	 */
	public static String getBookName(CNMarc marc){
		logger.info("进入getBookName方法");
		String rtn = CNMarcService.getBookName(marc);
		logger.info("退出getBookName方法");
		return rtn;
	}
	/**
	 * 获取着作者
	 * @param marc
	 * @return String
	 */
	public static String getComposer(CNMarc marc){
		logger.info("进入getComposer方法");
		String rtn = CNMarcService.getComposer(marc);
		logger.info("退出getComposer方法");
		return rtn;
	}
	/**
	 * 获取主题词
	 * @param marc
	 * @return String
	 */
	public static String getSubject(CNMarc marc){
		logger.info("进入getSubject方法");
		String rtn = CNMarcService.getSubject(marc);
		logger.info("退出getSubject方法");
		return rtn;
	}
	
	/**
	 * 获取作者姓名
	 * @param marc
	 * @return
	 */
	public static String getCreator(CNMarc marc){
		logger.info("进入getCreator方法");
		String rtn = CNMarcService.getCreator(marc);
		logger.info("退出getCreator方法");
		return rtn;
	}
	/**
	 * 获取责任方式
	 * @param marc
	 * @return
	 */
	public static String getType(CNMarc marc){
		logger.info("进入getType方法");
		String rtn = CNMarcService.getType(marc);
		logger.info("退出getType方法");
		return rtn;
	}
	
	/**
	 * 获取译者姓名
	 * @param marc
	 * @return
	 */
	public static String getContributortrl(CNMarc marc){
		logger.info("进入getContributortrl方法");
		String rtn = CNMarcService.getContributortrl(marc);
		logger.info("退出getContributortrl方法");
		return rtn;
	}
	
	/**
	 * 导出excel文件
	 * @param fileName
	 * @param titles 如["ISBN","装订方式",...]
	 * @param data 可以使用 extractCNMarc方法抽取特定数据
	 */
	public static void exportExcel(String fileName,String[] titles,List<Map<String,String>> data){
		logger.info("进入exportExcel方法");
		CNMarcService.exportExcel(fileName, titles, data);
		logger.info("退出exportExcel方法");
	}
}
