package com.brainsoon.common.util.dofile.cnmarc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.util.dofile.code.Epub2Html;
import com.brainsoon.common.util.dofile.util.DoFileException;


/**
 * CNMarc对外方法的实现
 * @author zuo
 *
 */
class CNMarcService {
	public static final Logger logger = Logger.getLogger(CNMarcService.class);

	/**
	 * 根据ISO文件加载CNMarc对象
	 * 
	 * @param filePath 文件全路径
	 * @return List<CNMarc>
	 */
	public static List<CNMarc> loadCNMarcFromISO(String filePath,String encode) {
		File cfile = new File(filePath);
		if (cfile.exists()) {
			List<CNMarc> rtn = new ArrayList<CNMarc>();
			// 按行阅读,装载CNMarc数据
			FileInputStream fInputStream = null;
			InputStreamReader inputStreamReader = null;
			BufferedReader in = null;
			try {
				if(StringUtils.isEmpty(encode)){
					encode = Epub2Html.getFileCharsetByPath(filePath);
				}
				logger.debug("文件" + filePath + "编码:" + encode);
				if (!"UTF-8".equalsIgnoreCase(encode)) {
					encode = "GBK";
				}
				fInputStream = new FileInputStream(filePath);
				inputStreamReader = new InputStreamReader(fInputStream, encode);
				in = new BufferedReader(inputStreamReader);

				String strTmp = "";

				// 按行读取
				while ((strTmp = in.readLine()) != null) {
					CNMarc cnmarc = new CNMarc();
					cnmarc.load(strTmp);
					logger.debug("cnmarc头标区：" + cnmarc.getHeadArea());
					rtn.add(cnmarc);
				}
				
				
			} catch (Exception e) {
				logger.error("获取文件出现异常", e);
			} finally {
				try {
					if (null != fInputStream) {
						fInputStream.close();
					}
					if (null != inputStreamReader) {
						inputStreamReader.close();
					}
					if (null != in) {
						in.close();
						in = null;
					}
				} catch (IOException e) {
					logger.error("装载数据关闭异常", e);
				}
			}
			return rtn;
		}else{
			throw new DoFileException("ISO文件不存在"+filePath);
		}
	}
	/**
	 * 根据CNMarc List 对象生成ISO文件
	 * @param destFile 文件全路径
	 * @param lts Marc列表数据
	 * @param marcStandard marc数据标准 枚举类型
	 */
	public static void createCNMarcFile(String destFile, List<CNMarc> lts,CNMarcConstants.CNMarcStandard marcStandard) {
		BufferedWriter bufferedWriter = null;
		OutputStreamWriter outStream = null;
		FileOutputStream fileOutStream = null;
		try {
			int lsize = lts.size();
			for (int i = 0; i < lsize; i++) {
				if (i == 0) {
					fileOutStream = new FileOutputStream(destFile);
					outStream = new OutputStreamWriter(fileOutStream, CNMarcConstants.ENCODING);
					bufferedWriter = new BufferedWriter(outStream);
				}
				CNMarc cnmarc = lts.get(i);
				if(null != marcStandard && marcStandard.name().equals("CALIS")){//国图转换为calis
					cnmarc = CNMarcUtils.cn2Calis(cnmarc);
				}
				bufferedWriter.write(cnmarc.getMarcData());
				if(i < lsize - 1){
					bufferedWriter.write("\r\n");
				}
			}
		} catch (Exception e) {
			logger.error("生成marc文件出现异常", e);
		} finally {
			try {
				fileOutStream.flush();
				outStream.flush();
				bufferedWriter.flush();
				if (null != fileOutStream) {
					fileOutStream.close();
				}
				if (null != outStream) {
					outStream.close();
				}
				if (null != bufferedWriter) {
					bufferedWriter.close();
					bufferedWriter = null;
				}
			} catch (IOException e) {
				logger.error("生成marc文件出现关闭异常", e);
			}
		}
	}
	/**
	 * 手动生成Marc对象
	 * @param Map<String,CNMarcColumnBase> columns
	 * @return CNMarc
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static CNMarc createCNMarcByMaps(Map<String,CNMarcColumnBase> columns) throws Exception{
		logger.debug("根据简单marc数据，生成Marc对象");
		boolean isSeries = false;//是否是丛书，包含461,calis包含410字段即为丛书
		/**对columns排序，升序*/
		columns = CNMarcUtils.sortMap(columns, "asc");
		CNMarc rtn = new CNMarc();
		CNMarcCatalog catalogObj = new CNMarcCatalog();
		/**生成目次区start 地址目次区格式：【字段号(3个字符) + 数据字段长度(4个字符) + 字段起始字符位置(5个字符)】的倍数 。一个汉字算两个长度**/
		StringBuffer catalog = new StringBuffer();
		catalog.append("001001100000");// 加001（记录控制号，有效长度为10，加上休止符=11，txt状态下休止符不可见）
		catalog.append("005001700011");// 加005（记录版次标识 ，本字段数据为固定长。形状为：YYYYMMDDHHMMSS.T 。T: 1/10秒 ）
		int startIndex = 28;// 11+17(001和005)
		Map<String,CNMarcColumnBase> n_columns = new LinkedHashMap<String, CNMarcColumnBase>();
		//头标区，如果传过来的columns中有值，则提取，否则自动生成。
		String headStr = "";
		for (Iterator ite = columns.entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			String colkey = (String) entry.getKey();
			CNMarcColumnBase colValue = (CNMarcColumnBase) entry.getValue();
			String character = colkey.substring(0, 3);
			logger.debug("character:"+character);
			if(character.equalsIgnoreCase("000")){
				headStr = colValue.getContent();
				continue;
			}
			//判断是否为丛书，丛书头标区为oam2，其他为nam0
			if(CNMarcConstants.seriesCharacter.containsKey(character)){
				isSeries = true;
			}
			//获取colValue中内容的长度
			StringBuffer subStr = new StringBuffer(); 
			if(character.equalsIgnoreCase("001")){
				subStr.append(colValue.getContent());
			}else if(character.equalsIgnoreCase("005")){
				subStr.append(CNMarcUtils.format005(colValue.getContent()));
			}else{
				subStr.append(colValue.getIdenticator1());
				subStr.append(colValue.getIdenticator2());
				List<CNMarcSubColumn> sub = colValue.getSubColumns();
				for (CNMarcSubColumn cnMarcSubColumn : sub) {
					subStr.append(CNMarcConstants.F_CHAR31);
					subStr.append(cnMarcSubColumn.getSubField());
					subStr.append(cnMarcSubColumn.getSubContent());
				}
			}
			subStr.append(CNMarcConstants.F_CHAR30);//单字段休止符
			//重新生成column对象
			CNMarcColumnBase n_col = new CNMarcColumnBase();
			n_col.load(subStr.toString(), character);
			n_columns.put(colkey, n_col);
			int marcLength = CNMarcUtils.getLength(subStr.toString());
			if(!character.equalsIgnoreCase("001") && !character.equalsIgnoreCase("005")){
				catalog.append(character).append(CNMarcUtils.getMarcStrFormat_0(String.valueOf(marcLength), 4)).append(CNMarcUtils.getMarcStrFormat_0(String.valueOf(startIndex), 5));
				startIndex = startIndex + marcLength;
			}
			logger.debug("catalog:"+catalog);
		}
		catalog.append(CNMarcConstants.F_CHAR30);//目次区休止符
		//根据目次区数据装载对象
		catalogObj.load(catalog.toString());
		/**生成目次区end**/
		
		/**生成记录头标(长24个字符)start*/
		// 记录长度(0-4) + 记录状态(5) + 执行代码(6-9) + 指示符长度(10) + 子字段标识符长度(11) +
		// 数据起始地址(12-16) + 记录附加定义(17-19) + 地址目次区结构(20-23)
		StringBuffer headBuf = new StringBuffer("");
		int countAll = 24 + catalog.length() + startIndex + 1;
		if(!headStr.equals("")){
			//替换 (1)记录长度               5   0-4  (6)数据基地址           5  12-16 
			headStr = headStr.replaceFirst("#####", CNMarcUtils.getMarcStrFormat_0(String.valueOf(countAll), 5));
			headStr = headStr.replaceFirst("#####", CNMarcUtils.getMarcStrFormat_0(String.valueOf(24 + catalog.length()), 5));
			headBuf.append(headStr);
		}else{
			headBuf.append(CNMarcUtils.getMarcStrFormat_0(String.valueOf(countAll), 5));// 最后还有一个休止符
			if(isSeries){
				headBuf.append("o");// 记录状态默认为“o”(曾为较高层次记录 )
			}else{
				headBuf.append("n");// 记录状态默认为“n”(新记录)
			}
			headBuf.append("a");// 执行代码.记录类型，默认为“a”(文字资料印刷品)
			headBuf.append("m");// 执行代码.书目级别，默认为“m”(专着)
			if(isSeries){
				headBuf.append("2");// 执行代码.层次登记代码，默认为“2”(低层次记录(在最高层以下的记录))
			}else{
				headBuf.append("0");// 执行代码.层次登记代码，默认为“0”(无层次关系)
			}
			headBuf.append(" ");
			headBuf.append("2");// 指示符长度
			headBuf.append("2");// 子字段标识符长度
			headBuf.append(CNMarcUtils.getMarcStrFormat_0(String.valueOf(24 + catalog.length()), 5));// 数据起始地址
			headBuf.append("   ");// 记录附加定义
			headBuf.append("450 ");// 地址目次区结构
		}
		/**生成记录头标(长24个字符)end*/
		
		/**拼装对象 **/
		rtn.setHeadArea(headBuf.toString());//头标区
		rtn.setCatalog(catalogObj);//目次区
		rtn.setColumns(n_columns);//数据区
		
		return rtn;
	}
	/**
	 * 获取CNMarc对象，详细说明(字段翻译过)
	 * @param marc
	 * @return Map<String,String> 
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String,String> getTranslateDesc(CNMarc marc) throws UnsupportedEncodingException{
		Map<String,String> rtn = new LinkedHashMap<String, String>(); 
		for (Iterator ite = marc.getColumns().entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			CNMarcColumnBase colValue = (CNMarcColumnBase) entry.getValue();
			List<CNMarcSubColumn> subMarc = colValue.getSubColumns();
			for (CNMarcSubColumn cnMarcSubColumn : subMarc) {
				CNMarcUtils.putAll(CNMarcUtils.translateCNMarcSubColumn(cnMarcSubColumn), rtn);
			}
		}
		return rtn;
	}
	/**
	 * 获取CNMarc对象，详细说明（字段未经翻译）
	 * @param marc
	 * @return Map<String,String>
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String,String> getNoTranslateDesc(CNMarc marc) throws Exception{
		Map<String,String> rtn = new LinkedHashMap<String, String>(); 
		if(null != marc){
			rtn.put("000", marc.getHeadArea());
			for (Iterator ite = marc.getColumns().entrySet().iterator(); ite.hasNext();) {
				Map.Entry entry = (Map.Entry) ite.next();
				String colkey = (String) entry.getKey();
				CNMarcColumnBase colValue = (CNMarcColumnBase) entry.getValue();
				rtn.put(colkey, colValue.getDescription(false,false));
			}
		}
		return rtn;
	}
	/**
	 * 生成CNMarc文件，excel格式
	 * @param destFile 文件名
	 * @param titleJSON 所需导出的字段名称 如: "[{'ISBN':'010ISBN'},{'书名':'200正题名$$200其他题名信息$$200分册名$$200分册名'}]"
	 * @param data CNMarc数据
	 */
	@SuppressWarnings("rawtypes")
	public static void createCNMarcFile(String destFile,String titleJSON,List<CNMarc> data){
		List<Map<String,String>> lst = extractCNMarc(titleJSON, data);
		//遍历出titleJSON 中的key 组成数组
		JSONArray jsonArr = JSONArray.fromObject(titleJSON);
		int jsonSize = jsonArr.size();
		String [] titles = new String[jsonSize];
		for (int i = 0; i < jsonSize; i++) {
			JSONObject jsonObject = (JSONObject) jsonArr.get(i);
			for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
			    String key = (String)iter.next();
			    titles[i] = key;
			}
		}
		exportExcel(destFile, titles, lst);
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getFieldDescByCharacter(String [] characters,String split,int max,Map<String,String> subFields,boolean repeat,CNMarc cnMarc){
		StringBuffer rtn = new StringBuffer();
		for (String character : characters) {
			if(null == split){
				split = "";
			}
			List repeatIn = null;
			//获取指定的字段
			for(int j = 0;j < max;j ++){
				String nId = character;
				if(j > 0){
					nId = character +"-" + j;
				}
				CNMarcColumnBase column = cnMarc.getColumns().get(nId);
				//遍历column中的内容
				if(null != column){
					if(!repeat){
						repeatIn = new ArrayList();
					}
					if(rtn.length() > 0){
						//添加字段间的连接符
						rtn.append(split);
					}
					List<CNMarcSubColumn> subs = column.getSubColumns();
					StringBuffer subBuffer = new StringBuffer();
					for (CNMarcSubColumn cnMarcSubColumn : subs) {
						String subField = cnMarcSubColumn.getSubField();
						if(!repeat){
							if(repeatIn.contains(subField)){
								continue;
							}
						}
						if(null == subFields || subFields.containsKey(subField)){
							//追加内容连接符及，subFields中的value
							if(null != subFields && subBuffer.length() > 0){
								String subConnect = subFields.get(subField)==null?"":subFields.get(subField);
								subBuffer.append(subConnect);
							}
							//获取内容
							subBuffer.append(cnMarcSubColumn.getSubContent());
							if(!repeat){
								repeatIn.add(subField);
							}
						}
					}
					rtn.append(subBuffer);
				}
				
			}
		}
		return rtn.toString();
	}
	/**
	 * 根据marc数据，抽取指定字段
	 * @param titleJSON 如:"[{'ISBN':'010ISBN'},{'书名':'200正题名$$200其他题名信息$$200分册名$$200分册名'}]"
	 * @param data CNMarc数据
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static List<Map<String,String>> extractCNMarc(String titleJSON,List<CNMarc> data){
		if(null != data){
			List<Map<String,String>> rtn = new ArrayList<Map<String,String>>();
			Map<String,String> marcdt;
			for (CNMarc cnMarc : data) {
				marcdt = new HashMap<String, String>();
				JSONArray jsonArr = JSONArray.fromObject(titleJSON);
				for (int i = 0; i < jsonArr.size(); i++) {
					JSONObject jsonObject = (JSONObject) jsonArr.get(i);
					for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
					    String key = (String)iter.next();
					    System.out.println(key + "#" + jsonObject.getString(key));
					    StringBuffer strBuffer = new StringBuffer();
					    if(key.equals("封面文件")){//封面文件取001字段，转换为int
					    	strBuffer.append(Integer.parseInt(cnMarc.getColumns().get("001").getContent())+".jpg");
					    }else if(key.equals("书名")){//书名：    200$a:$e.$h,$i
							strBuffer.append(getBookName(cnMarc));
					    }else if(key.equals("着作者")){//着作者：    200$f ; / $g   
							strBuffer.append(getComposer(cnMarc));
					    }else if(key.equals("主题词")){//主题词：    606$a   等，子字段除形式复分用逗号外，其他都是用 -- 连接
							strBuffer.append(getSubject(cnMarc));
					    }else{
						    String [] fields = StringUtils.split(jsonObject.getString(key),"$$");
						    if(null != fields){
						    	for (int j = 0; j < fields.length; j++) {
						    		//fields由两部分组成，描述$分隔符，其中$为F_CHAR31
						    		String [] currentFileds = StringUtils.split(fields[j],CNMarcConstants.F_CHAR31);
						    		String ccontent = "";
						    		if(currentFileds.length >= 2){
						    			ccontent = cnMarc.getFieldDesc(currentFileds[0],currentFileds[1]);
						    		}else if(key.equals("中图法分类")){//中图法分类 690$a字段，只取一个
						    			ccontent = cnMarc.getFieldDesc(currentFileds[0],1,"");
						    		}else{
							    		ccontent = cnMarc.getFieldDesc(currentFileds[0]);
							    	}
						    		if(null == ccontent || ccontent.equals("")){
						    			continue;
						    		}
						    		if(j > 0 && strBuffer.length() >0 && currentFileds.length >= 2){
						    			strBuffer.append(currentFileds[1]);
						    		}
						    		strBuffer.append(ccontent);
								}
						    }
					    }
					    marcdt.put(key,strBuffer.toString());
					}
				}
				rtn.add(marcdt);
			}
			return rtn;
		}
		return null;
	}
	/**
	 * 获取书名
	 * @param marc
	 * @return String
	 */
	public static String getBookName(CNMarc marc){
		////书名：    200$a:$e.$h,$i
		Map<String,String> subFields = new HashMap<String, String>();
    	subFields.put("a", "");
		subFields.put("e", "：");
		subFields.put("h", "．");
		subFields.put("i", "，");
		String [] characters = {"200"};
		return getFieldDescByCharacter(characters, null, CNMarcConstants.repeatFieldMax, subFields,false, marc);
	}
	/**
	 * 获取着作者
	 * @param marc
	 * @return String
	 */
	public static String getComposer(CNMarc marc){
		//着作者：    200$f ; / $g   
		Map<String,String> subFields = new HashMap<String, String>();
    	subFields.put("f", " ; / ");
		subFields.put("g", " ; / ");
		String [] characters = {"200"};
		return getFieldDescByCharacter(characters, null, CNMarcConstants.repeatFieldMax, subFields,false, marc);
	}
	/**
	 * 获取主题词
	 * @param marc
	 * @return String
	 */
	public static String getSubject(CNMarc marc){
		//主题词：    606$a   等，子字段除形式复分用逗号外，其他都是用 -- 连接
    	//'606主标目$$606论题复分"+CNMarcConstants.F_CHAR31+" -- '},
    	Map<String,String> subFields = new HashMap<String, String>();
    	subFields.put("a", " -- ");
    	subFields.put("b", " -- ");
    	subFields.put("c", " -- ");
    	subFields.put("d", " -- ");
    	subFields.put("e", " -- ");
    	subFields.put("f", " -- ");
    	subFields.put("g", " -- ");
    	subFields.put("h", " -- ");
    	subFields.put("i", " -- ");
		subFields.put("j", " , ");
		subFields.put("k", " -- ");
		subFields.put("l", " -- ");
		subFields.put("m", " -- ");
		subFields.put("n", " -- ");
		subFields.put("q", " -- ");
		subFields.put("r", " -- ");
		subFields.put("s", " -- ");
		subFields.put("t", " -- ");
		subFields.put("u", " -- ");
		subFields.put("v", " -- ");
		subFields.put("w", " -- ");
		subFields.put("x", " -- ");
		subFields.put("y", " -- ");
		subFields.put("z", " -- ");
		String [] characters = {"601","602","604","605","606","607","608","610"};
		return getFieldDescByCharacter(characters, null, CNMarcConstants.repeatFieldMax, subFields,false, marc);
	}
	
	/**
	 * 获取作者姓名
	 * @param marc
	 * @return
	 */
	public static String getCreator(CNMarc marc){
		Map<String,String> subFields = new HashMap<String, String>();
		subFields.put("a", "");
		subFields.put("b", "");
		subFields.put("c", "");
		String [] characters = {"701","711"};
		return getFieldDescByCharacter(characters, "，", CNMarcConstants.repeatFieldMax, subFields,false, marc);
	}
	
	/**
	 * 获取责任方式
	 * @param marc
	 * @return String
	 */
	public static String getType(CNMarc marc){
		//责任方式：    701$4   或711$4
    	Map<String,String> subFields = new HashMap<String, String>();
		subFields.put("4", " ");
		String [] characters = {"701","702","704","705","706","707","708","710","711"};
		String rn = getFieldDescByCharacter(characters, ",", CNMarcConstants.repeatFieldMax, subFields,false, marc);
		String[] rnArr = rn.split(",");
		if(rnArr!=null && rnArr.length>0) {
			return rnArr[0];
		} else {
			return rn;
		}
	}
	
	/**
	 * 获取译者姓名
	 * @param marc
	 * @return String
	 */
	public static String getContributortrl(CNMarc marc){
		Map<String,String> subFields = new HashMap<String, String>();
		subFields.put("a", " , ");
		subFields.put("b", " , ");
		subFields.put("c", " , ");
		String [] characters = {"702","712"};
		return getFieldDescByCharacter(characters, "，", CNMarcConstants.repeatFieldMax, subFields,false, marc);
	}
	/**
	 * 导出excel文件
	 * @param fileName
	 * @param titles 如["ISBN","装订方式",...]
	 * @param data 可以使用 extractCNMarc方法抽取特定数据
	 */
	public static void exportExcel(String fileName,String[] titles,List<Map<String,String>> data){
		//数据长度
		int dtLength = data.size();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		StringBuffer str = new StringBuffer();
		str.append(CNMarcConstants.excelStart)
		//生成创建日期
		.append("    <Created>").append(sdf.format(new Date())).append("</Created>").append(CNMarcConstants.NEWLINESTR)
		
		.append(CNMarcConstants.excelMiddle)
		.append("  <Worksheet ss:Name='CBIP数据'>").append(CNMarcConstants.NEWLINESTR)
		.append("    <Table ss:ExpandedColumnCount='33' ss:ExpandedRowCount='").append(dtLength+1).append("' x:FullColumns='1'").append(CNMarcConstants.NEWLINESTR)
		.append("       x:FullRows='1' ss:DefaultColumnWidth='54' ss:DefaultRowHeight='14.25'>").append(CNMarcConstants.NEWLINESTR)
		.append(CNMarcConstants.excelColumn)
		.append("      <Row ss:AutoFitHeight='0'>").append(CNMarcConstants.NEWLINESTR);
		//excel 头信息
		for (int i = 0; i < titles.length; i++) {
			str.append("        <Cell><Data ss:Type='String'>").append(titles[i]).append("</Data></Cell>").append(CNMarcConstants.NEWLINESTR);
			if(titles[i].equalsIgnoreCase("ISBN")){
				str.append("        <Cell><Data ss:Type='String'>ISBN10</Data></Cell>").append(CNMarcConstants.NEWLINESTR);
				str.append("        <Cell><Data ss:Type='String'>ISBN13</Data></Cell>").append(CNMarcConstants.NEWLINESTR);
			}
		}
		str.append("      </Row>").append(CNMarcConstants.NEWLINESTR);
	 	//excel 内容
		for (Map<String, String> map : data) {
			str.append("      <Row ss:AutoFitHeight='0'>").append(CNMarcConstants.NEWLINESTR);
			for (int i = 0; i < titles.length; i++) {
				String currentTitle = titles[i];
				String mapValue = map.get(currentTitle);
				if(currentTitle.equalsIgnoreCase("并列书名")){//去掉 = 号
					mapValue = StringUtils.replace(mapValue, " = ", "");
				}else if(currentTitle.equalsIgnoreCase("开本")){
					mapValue = CNMarcUtils.formatSize(mapValue);
				}
				str.append("        <Cell><Data ss:Type='String'>").append(mapValue).append("</Data></Cell>").append(CNMarcConstants.NEWLINESTR);
				if(currentTitle.equalsIgnoreCase("ISBN")){
					str.append("        <Cell><Data ss:Type='String'>").append(CNMarcUtils.getISBN10(mapValue)).append("</Data></Cell>").append(CNMarcConstants.NEWLINESTR);
					str.append("        <Cell><Data ss:Type='String'>").append(mapValue).append("</Data></Cell>").append(CNMarcConstants.NEWLINESTR);
				}
			}
			str.append("      </Row>").append(CNMarcConstants.NEWLINESTR);
		}
		str.append("    </Table>").append(CNMarcConstants.NEWLINESTR)
		.append(CNMarcConstants.excelWorksheetOptions)
		.append("  </Worksheet>").append(CNMarcConstants.NEWLINESTR)
		.append("</Workbook>").append(CNMarcConstants.NEWLINESTR);
		//写入文件
		BufferedWriter bufferedWriter = null;
		OutputStreamWriter outStream = null;
		FileOutputStream fileOutStream = null;
		try {
			fileOutStream = new FileOutputStream(fileName);
			outStream = new OutputStreamWriter(fileOutStream, "utf-8");
			bufferedWriter = new BufferedWriter(outStream);
			bufferedWriter.write(str.toString());
		} catch (Exception e) {
		} finally {
			try {
				fileOutStream.flush();
				outStream.flush();
				bufferedWriter.flush();
				if (null != fileOutStream) {
					fileOutStream.close();
				}
				if (null != outStream) {
					outStream.close();
				}
				if (null != bufferedWriter) {
					bufferedWriter.close();
					bufferedWriter = null;
				}
			} catch (IOException e) {
			}
		}
	}
	public static CNMarc createCNMarcByColumnsStr(String columnsStr) throws Exception{
		String [] cols = StringUtils.split(columnsStr,"\r\n");
		int i = 0;
		Map<String,CNMarcColumnBase> columns = new LinkedHashMap<String, CNMarcColumnBase>();
		CNMarcColumnBase col_x = null;
		List<CNMarcSubColumn> subColumnsList = null;
		for (String column : cols) {
			if(i++ == 0){
				continue;
			}
			//每一行是一个字段，第一行应该跳过 (前三位为字段号，后面为内容)
			String character = StringUtils.substring(column, 0,3);
			logger.debug("character:"+character);
			//按us切割其他部分
			String col = StringUtils.substring(column, 3);
			if(!character.equals("000")){
				//移出最后一个分组符号RS
				col = CNMarcUtils.filterControlCharacter(col,1);
			}
			//准备对象
			col_x = new CNMarcColumnBase();
			col_x.setCharacter(character);
			if(character.equals("000") || character.equals("001") || character.equals("005")){
				col_x.setContent(col);
			}else{
				String[] subColumns = StringUtils.split(col, CNMarcConstants.F_CHAR31);
				int j = 0;
				subColumnsList = new ArrayList<CNMarcSubColumn>();
				//第一组为指示符
				for (String content : subColumns) {
					if(j == 0){
						col_x.setIdenticator1(StringUtils.substring(content, 0, 1));
						col_x.setIdenticator2(StringUtils.substring(content, 1, 2));
					}else{
						//第一个字符为子字段标识符
						subColumnsList.add(new CNMarcSubColumn(StringUtils.substring(content, 0, 1),StringUtils.substring(content, 1)));
					}
					j++;
				}
				col_x.setSubColumns(subColumnsList);
			}
			//防止character重复
			for(int m = 0;m < CNMarcConstants.repeatFieldMax;m ++){
				String nId = character;
				if(m > 0){
					nId = character +"-" + m;
				}
				if(!columns.containsKey(nId)){
					character = nId;
					break;
				}
			}
			columns.put(character, col_x);
		}
		return createCNMarcByMaps(columns);
	}
}
