package com.brainsoon.common.util.dofile.cnmarc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.brainsoon.common.exception.ServiceException;

/**
 * 处理CNMarc用到的常用方法
 * @author zuo
 *
 */
public class CNMarcUtils {
	private static final Logger logger = Logger.getLogger(CNMarcUtils.class);
	/**
	 * 按字节长度截取字符串
	 * @param str 将要截取的字符串参数
	 * @param start 截取字符长的起始位置
	 * @param toCount 截取的字节长度
	 * @return 返回截取后的字符串
	 * @throws UnsupportedEncodingException 
	 */
	public static String substring(String str,int start, int toCount) throws UnsupportedEncodingException {
		if (str == null)
			return "";
		int realLength = 0;
		String rtnStr = "";
		char[] tempChar = str.toCharArray();
		//按起始位置截取
		for(int i = 0;i < tempChar.length; i ++ ){
			String s1 = String.valueOf(tempChar[i]);
			byte[] b = s1.getBytes(CNMarcConstants.ENCODING);
			realLength += b.length;
			if(realLength > start && rtnStr.getBytes(CNMarcConstants.ENCODING).length < toCount){
				rtnStr += tempChar[i];
			}
		}
		return rtnStr;
	}
	/**
	 * 按字节长度截取字符串
	 * @param str 将要截取的字符串参数
	 * @param start 截取字符长的起始位置
	 * @return 返回截取后的字符串
	 * @throws UnsupportedEncodingException 
	 */
	public static String substring(String str,int start) throws UnsupportedEncodingException{
		return substring(str, start, str.getBytes(CNMarcConstants.ENCODING).length);
	}
	/**
	 * 按字节拆分字节数组
	 * @param srcByte
	 * @param cuter
	 * @return 截取成功返回List<byte[]>，否则返回null
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<byte[]> splitByteByChar(byte[] srcByte,char cuter){
		if(null == srcByte || srcByte.length <= 0){
			return null;
		}else{
			List<byte[]> rtn = new ArrayList<byte[]>();
			List lt = new ArrayList();
			for (int i = 0; i < srcByte.length; i++) {
				if(srcByte[i] == cuter || i == srcByte.length-1){
					if(lt.size() > 0){
						byte[] cc = new byte[lt.size()];
						for (int j = 0; j < cc.length; j++) {
							cc[j] = (byte)Integer.parseInt(lt.get(j).toString());
						}
						lt = new ArrayList();
						rtn.add(cc);
					}
				}else{
					lt.add(srcByte[i]);
				}
			}
			if(rtn.size() > 0){
				return rtn;
			}else{
				return null;
			}
		}
	}
	/**
	 * byte[]转化为string
	 * @param res
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String byte2String(byte[] res) throws UnsupportedEncodingException{
		return new String(res,CNMarcConstants.ENCODING);
	}
	/**
	 * byte转化为string
	 * @param res
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String byte2String(byte res) throws UnsupportedEncodingException{
		return byte2String(new byte[]{res});
	}
	/**
	 * 将int 转化为 byte[]
	 * @param res 要转化的整数
	 * @return 
	 */
	public static byte[] int2byte(int res){
		byte[] targets = new byte[4];
		targets[0] = (byte)(res & 0xff);//最低位
		targets[1] = (byte)((res >> 8) & 0xff);//次低位
		targets[2] = (byte)((res >> 16) & 0xff);//次高位
		targets[3] = (byte)(res >> 24);//最高位，无符号右移
		return targets;
	}
	/**
	 * byte[]转化为int
	 * @param res
	 * @return
	 */
	public static int byte2int(byte[] res){
		int targets = (res[0] & 0xff)|((res[1]<<8) & 0xff00) | ((res[2] << 24) >>> 8)|(res[3] << 24);
		return targets;
	}
	/**
	 * 根据目次区切割数据
	 * @param catalog 目次区对象
	 * @param column 数据区对象
	 * @param res 整个数据区字符串
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map splitColumnsByCatalog(CNMarcCatalog catalog,String res) throws Exception{
		if(null != catalog){
			Map<String, String> catalogs = catalog.getColumnCatalogs();
			if(catalogs.size() > 0){
				Map column = new LinkedHashMap<String,CNMarcColumnBase>();
				//开始
				for (Iterator ite = catalogs.entrySet().iterator(); ite.hasNext();) {
					Map.Entry entry = (Map.Entry) ite.next();
					String colkey = (String) entry.getKey();
					String colValue = (String) entry.getValue();
					//按value切割(四位长度 + 五位数字表示的字段起始字符位置)
					int count = Integer.valueOf(substring(colValue, 0, 4));
					int start = Integer.valueOf(substring(colValue, 4));
					logger.debug(colkey+"------"+colValue+"长度："+count+"起始位置："+start);
					String character = colkey.substring(0, 3);
					CNMarcColumnBase col = new CNMarcColumnBase();
					col.load(substring(res,start ,count),character);
					column.put(colkey, col);
				}
				return column;
			}
		}
		return null;
	}
	/**
	 * 替换字符串,按byte匹配
	 * @param res 原字符串
	 * @param repl 要替换的字符
	 * @param with 替换为的字符
	 * @return 替换后的字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String replaceStr(String res,char repl,char with) throws UnsupportedEncodingException{
		if(!"".equals(res)){
			byte[] bt = res.getBytes(CNMarcConstants.ENCODING);
			//循环替换
			for (int i = 0; i < bt.length; i++) {
				byte b = bt[i];
				if(b == repl){
					bt[i] = (byte)with;
				}
			}
			return byte2String(bt);
		}
		return "";
	}
	/**
	 * 过滤掉字符串中的三类控制字符
	 * @param res
	 * @param type  0 过滤 三类， 1 只过滤 RS
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String filterControlCharacter(String res,int type) throws UnsupportedEncodingException{
		if(!"".equals(res)){
			byte[] bt = res.getBytes(CNMarcConstants.ENCODING);
			//循环替换
			List lt = new ArrayList();
			for (int i = 0; i < bt.length; i++) {
				byte b = bt[i];
				if(type == 0){
					if(b == CNMarcConstants.F_CHAR29 || b == CNMarcConstants.F_CHAR30 || b == CNMarcConstants.F_CHAR31){
						continue;
					}
				}else if(type == 1){
					if(b == CNMarcConstants.F_CHAR30){
						continue;
					}
				}
				lt.add(b);
			}
			//生成新的字节数组
			byte[] nbyte = new byte[lt.size()];
			for (int i = 0; i < nbyte.length; i++) {
				nbyte[i] = (byte)Integer.parseInt(lt.get(i).toString());
			}
			return byte2String(nbyte);
		}
		return "";
	}
	/**
	 * JSON 字符串 转换为 Map
	 * @param json
	 * @return HashMap
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map JSON2Map(String json){
		if(null == json || "".equals(json)){
			return null;
		}
		Map rtn = null;
		JSONArray jsonArr = JSONArray.fromObject(json);
		for(int i = 0; i < jsonArr.size(); i ++){   
			if(i == 0){
				rtn = new HashMap();
			}
			String json_c = jsonArr.getString(i);  
			JSONObject jsonObject = JSONObject.fromObject(json_c);
	   		for (Iterator iter = jsonObject.keys(); iter.hasNext();) { //先遍历整个 people 对象
	   		    String key = (String)iter.next();
	   		    rtn.put(key, jsonObject.getString(key));
	   		}
	     }   
		return rtn;
	}
	/**
	 * 根据配置翻译CNMarcSubColumn对象中的内容
	 * @param column
	 * @param addDescribe 是否加说明即CNFILEDS中的key如 ISBN：
	 * @return
	 * @throws UnsupportedEncodingException 待完善,如果一个字段只定义了一部分,可能会忽略没有定义的 2012年6月5日完善
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String translateCNMarcSubColumn(CNMarcSubColumn column,boolean addDescribe) throws UnsupportedEncodingException{
		if(null == column){
			return "";
		}
		CNMarcColumnBase pcol = column.getParent();
		StringBuffer strBuffer = new StringBuffer();
		String subFieldId = column.getSubField();//子字段标识符
		String subContent = column.getSubContent();//子字段原内容
		//为了准确的替换翻译后的内容,又不丢失没有翻译的内容,采用数组占位方式,替换字符串
		String [] tempContent = chars2Strings(subContent.toCharArray());
		if(pcol != null){
			String character = pcol.getCharacter();//字段标识
			//根据翻译Map集合翻译字段内容 常量里的所有值,总集合
			Map transMap = CNMarcFieldsConstants.CNFILEDS;
			//遍历Map 取所有字段号为 subFieldId的数据,对该字段进行翻译（字段标识符在key的前三位）
			for (Iterator itr = transMap.entrySet().iterator(); itr.hasNext();) {
				Map.Entry colentry = (Map.Entry)itr.next();
				String fieldDesc = colentry.getKey().toString();
				String vcontent = colentry.getValue().toString();
				//获取value中的子字段标识符
				String [] fieldArr = vcontent.split(CNMarcFieldsConstants.FILEDSPLITSTR);
				String keyField = "-1";//字段标识符
				if(fieldDesc.length() > 3){
					keyField = fieldDesc.substring(0,3);
					fieldDesc = fieldDesc.substring(3);
				}
				if(keyField.equalsIgnoreCase(character)){
					String currentSubFieldId = fieldArr[0];//子字段标识符
					if(currentSubFieldId.equals(subFieldId)){
						strBuffer = new StringBuffer();
						int currentStart = Integer.parseInt(fieldArr[1]);//字符开始位置
						int currentCount = 0;//字符数
						Map translateMap = null; //当前翻译串
						if(currentStart >= 0){
							currentCount = Integer.parseInt(fieldArr[2]);
							if(fieldArr.length > 3){
								translateMap = CNMarcUtils.JSON2Map(fieldArr[3]);
							}
						}
						//翻译该字段
						if(addDescribe){
							strBuffer.append(fieldDesc).append(":");
						}
						if(currentStart == -1){//截取了所有内容,不用特殊占位处理
							strBuffer.append(CNMarcUtils.substring(subContent, currentStart));
						}else{
							String subStr = CNMarcUtils.substring(subContent, currentStart, currentCount);
							if(fieldArr.length > 3){//需要转换
								strBuffer.append(translateSubContentByMap(subStr,translateMap));
							}else{
								strBuffer.append(subStr);
							}
						}
						int ii = 0;
						for(int m = currentStart;m < currentStart + currentCount; m ++){
							if(ii++ == 0){
								tempContent[m] = strBuffer.toString();
							}else{
								tempContent[m] = "";
							}
						}
					}
				}
			}
			
			
		}
		//找到对应翻译字段,则进行翻译,否则原样返回
		String rtnStr = "";
		for(String rrstr : tempContent){
			rtnStr += rrstr;
		}
		return rtnStr;
	}
	/**
	 * 把CNMarcSubColumn内容翻译为Map
	 * @param column
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String,String> translateCNMarcSubColumn(CNMarcSubColumn column) throws UnsupportedEncodingException{
		if(null == column){
			return null;
		}
		CNMarcColumnBase pcol = column.getParent();
		StringBuffer strBuffer = new StringBuffer();
		String subFieldId = column.getSubField();//子字段标识符
		String subContent = column.getSubContent();//子字段原内容
		if(pcol != null){
			//返回的map集合
			Map<String,String> rtnMap = new LinkedHashMap<String, String>();
			
			String character = pcol.getCharacter();//字段标识
			//根据翻译Map集合翻译字段内容 常量里的所有值,总集合
			Map transMap = CNMarcFieldsConstants.CNFILEDS;
			//遍历Map 取所有字段号为 subFieldId的数据,对该字段进行翻译（字段标识符在key的前三位）
			for (Iterator itr = transMap.entrySet().iterator(); itr.hasNext();) {
				Map.Entry colentry = (Map.Entry)itr.next();
				String fieldDesc = colentry.getKey().toString();
				String vcontent = colentry.getValue().toString();
				String [] fieldArr = vcontent.split(CNMarcFieldsConstants.FILEDSPLITSTR);
				String keyField = "-1";//字段标识符
				if(fieldDesc.length() > 3){
					keyField = fieldDesc.substring(0,3);
					//fieldDesc = fieldDesc.substring(3); //key前三位字段标识符，由前台决定是否显示
				}
				if(keyField.equalsIgnoreCase(character)){
					String currentSubFieldId = fieldArr[0];//子字段标识符
					if(currentSubFieldId.equals(subFieldId)){
						strBuffer = new StringBuffer();
						int currentStart = Integer.parseInt(fieldArr[1]);//字符开始位置
						int currentCount = 0;//字符数
						Map translateMap = null; //当前翻译串
						if(currentStart >= 0){
							currentCount = Integer.parseInt(fieldArr[2]);
							if(fieldArr.length > 3){
								translateMap = CNMarcUtils.JSON2Map(fieldArr[3]);
							}
						}
						//翻译该字段
						if(currentStart == -1){//截取了所有内容,不用特殊占位处理
							strBuffer.append(CNMarcUtils.substring(subContent, currentStart));
						}else{
							String subStr = CNMarcUtils.substring(subContent, currentStart, currentCount);
							if(fieldArr.length > 3){//需要转换
								strBuffer.append(translateSubContentByMap(subStr,translateMap));
							}else{
								strBuffer.append(subStr);
							}
						}
						rtnMap.put(fieldDesc, strBuffer.toString());
					}
				}
			}
			if(rtnMap.size() == 0){
				//如果未找到定义的字段，则放入字段标识符，原内容为value
				rtnMap.put(character+subFieldId, subContent);
			}
			return rtnMap;
		}
		return null;
	}
	/**
	 * 根据map 翻译 内容
	 * @param res 要翻译的内容
	 * @param translateMap 翻译使用的map
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	public static String translateSubContentByMap(String res,Map<String,String> translateMap){
		StringBuffer strBuffer = new StringBuffer();
		/*取map中的一条数据，判断key长度，如果长度与截取后的字符串一样，则直接取值即可，如果小于截取后的字符串长度，则采用replace方法（处理翻译组合功能）
		    举例：第一种情况，相等。如:编目语种：以3个字符代码表示编目所试用的语种，而  翻译Map中为         [{chi:汉语}]
		  	      第二种情况，不相等。如：阅读对象：用4个字符位置记载作品的适用对象。左边对齐，不用的位置填空格。而  翻译Map中为        [{a:普通青少年},{m:普通成人}]
		*/
		if(null != translateMap && translateMap.size() > 0){
			Map.Entry entry = (Map.Entry) translateMap.entrySet().iterator().next();
			String key = String.valueOf(entry.getKey());
			if(null == key){
				return res;
			}
			if(key.length() < res.getBytes().length){
				char [] splitStr = res.toCharArray();
				for (char cstr : splitStr) {
					Object nstr = translateMap.get(cstr+"");
					if(null != nstr && !nstr.equals("") && !nstr.equals("null")){
						strBuffer.append(nstr);
					}else{
						strBuffer.append(cstr);
					}
				}
			}else{
				String nstr = translateMap.get(res)+"";
				if(!nstr.equals("") && !nstr.equals("null")){
					res = nstr;
				}
				strBuffer.append(res);
			}
			return strBuffer.toString();
		}else{
			return res;
		}
	}
	/**
	 * char数组转换为String数组
	 * @param res
	 * @return
	 */
	public static String [] chars2Strings(char [] res){
		if(null != res){
			int resLength = res.length;
			String [] rtn = new String[resLength];
			for(int i = 0 ;i < resLength; i ++){
				rtn[i] = String.valueOf(res[i]);
			}
			return rtn;
		}
		return null;
	}
	/**
	 * 获取字符串的长度(一个汉字算两个长度)
	 * 
	 * @param value - 输入字符串
	 * @return 返回字符串的长度
	 */
	public static int getLength(String value) {
		if (value == null || value.trim().length() == 0)
			return 0;
		else {
			byte[] bbs = value.getBytes();
			int len = bbs.length;
			return len;
		}
	}

	/**
	 * 如果数据长度不够，将数据前面补0，以达到指定长度的数据
	 * 
	 * @param value 输入数据，如：12
	 * @param count 指定格式的数据长度，如：4
	 * @return 返回格式化好的数据，如：0012
	 */
	public static String getMarcStrFormat_0(String value, int count) {
		if (value == null || value.length() == 0)
			return value;
		if (value.length() >= count)
			return value;
		StringBuffer buf = new StringBuffer("");
		int pos = value.length();
		for (int i = pos; i < count; i++) {
			buf.append("0");
		}
		buf.append(value);
		return buf.toString();
	}
	/**
	 * 把srcMap中的数据拷贝到destMap中，如果key值重复，则自动在后边加--x (x从1开始)
	 * @param srcMap
	 * @param destMap
	 */
	@SuppressWarnings("rawtypes")
	public static void putAll(Map<String,String> srcMap,Map<String,String> destMap){
		if(null != srcMap && null != destMap){
			for(Iterator itr = srcMap.entrySet().iterator();itr.hasNext();){
				Map.Entry entry = (Map.Entry)itr.next();
				String key = String.valueOf(entry.getKey());
				String value = String.valueOf(entry.getValue());
				String nkey = key;
				//支持n个重复字段
				for(int i = 0;i < CNMarcConstants.repeatFieldMax; i ++ ){
					if(i > 0){
						nkey = key + "--" + i;
					}
					if(!destMap.containsKey(nkey)){
						destMap.put(nkey, value);
						break;
					}
				}
			}
		}
	}
	/**
	 * 10位ISBN转13位 
	 * (1) 用1分别乘书号的前12位中的奇数位, 用3乘以偶数位:(位数从左到右为13位到2位）
　　	   (2) 将各乘积相加，求出总和 ； 
　　        (3) 将总和除以10，得出余数； 
　　        (4) 将10减去余数后即为校验位。如相减后的数值为10,校验位则为0。
	 * @param isbn
	 * @return xxx-x-xxx-xxxxx-x
	 */
	public static String getISBN13(String isbn) {
		//剔除分隔符 - 
		isbn = isbn.replace("-", "");
		if (isbn.length() != 10) {
			return isbn;
		}
		isbn = isbn.substring(0, isbn.length() - 1);
		isbn = "978" + isbn;
		//加入分隔符
		isbn = isbn.substring(0, 3) + "-" + isbn.substring(3, 4) + "-"
				+ splitShortISBN(isbn.substring(4, 12)) + "-"
				+ getISBNCheckCode13(isbn);
		return isbn;
	}
	
	/**
	 * 13位ISBN转10位 
	 * （模数11 余数 0-10 差数 1-11 校验位：0-9, x(差数为10）
	 * @param isbn13
	 * @return x-xxx-xxxxx-x
	 */
	public static String getISBN10(String isbn13) {
		//剔除分隔符 - 
		isbn13 = isbn13.replace("-", "");
		if (isbn13.length() == 13) {
			String location4 = isbn13.substring(3,4);
			String location13 = getISBNCheckCode10(StringUtils.substring(isbn13, 3, 12));
			isbn13 = location4 + "-" + splitShortISBN(isbn13.substring(4, 12)) + "-" + location13;
		}
		return isbn13;
	}
	
	public static int getISBNCheckCode13(String isbn) {
		int a = 0;
		int b = 0;
		int c = 0;
		int d = 0;
		for (int i = 0; i < isbn.length(); i++) {
			int x = Integer.parseInt(isbn.substring(i, i+1));
			if (i % 2 == 0) {
				a += x;
			} else {
				b += x;
			}
		}
		c = a + 3 * b;
		d = 10 - c % 10;
		if(d == 10){
			d = 0;
		}
		return d;
	}
	
	public static String getISBNCheckCode10(String isbn) {
		if (isbn.length() != 9 || !StringUtils.isNumeric(isbn)) {
			throw new ServiceException("参数不合法");
		}
		String location13 = "0";
		int arithmetical = (getIntFromStr(isbn, 0) * 10
				+ getIntFromStr(isbn, 1) * 9 + getIntFromStr(isbn, 2) * 8
				+ getIntFromStr(isbn, 3) * 7 + getIntFromStr(isbn, 4) * 6
				+ getIntFromStr(isbn, 5) * 5 + getIntFromStr(isbn, 6) * 4
				+ getIntFromStr(isbn, 7) * 3 + getIntFromStr(isbn, 8) * 2) % 11;
		if(arithmetical != 0){
			int isbnSum = 11 - arithmetical;
			if (isbnSum == 10) {
				return "X";
			}
			
			return String.valueOf(isbnSum);
		}
		return "0";
	}
	
	private static int getIntFromStr(String str, int index) {
		return Integer.valueOf(str.substring(index, index+1));
	}
	
	/**
	 * 拆分isbn中间8位规则
	 * @param shortISBN 中间8位isbn号，无“-”
	 * @return
	 *  第1位是0，则社号为2位，书号为6位。
		第1位是1或2或3，则社号为3位，书号为5位。
		第1位是4或5或6，则社号为4位，书号为4位。
		第1位是7或8，则社号为5位，书号为3位。
		第1位是9，则社号为6位，书号为2位。
		0	978-7-0X-xxxxxx-Z
		1-3  978-7-1/2/3xx-xxxxx-Z
		4-6  978-7-4/5/6xxx-xxxx-Z
		7-8  978-7-7/8xxxx-xxx-Z
		9   978-7-9xxxxx-xx-Z
	 * @author Fuwenbin
	 * @date Aug 17, 2012 11:31:28 AM 
	 */
	public static String splitShortISBN(String shortISBN) {
		if (shortISBN.length() != 8 || !StringUtils.isNumeric(shortISBN)) {
			throw new ServiceException("参数不合法");
		}
		
		int startInt = Integer.valueOf(StringUtils.left(shortISBN, 1));
		if (startInt == 0) {
			return StringUtils.left(shortISBN, 2) + "-" + StringUtils.right(shortISBN, 6);
		}
		
		if (startInt >= 1 && startInt <= 3) {
			return StringUtils.left(shortISBN, 3) + "-" + StringUtils.right(shortISBN, 5);
		}
		
		if (startInt >= 4 && startInt <= 6) {
			return StringUtils.left(shortISBN, 4) + "-" + StringUtils.right(shortISBN, 4);
		}
		
		if (startInt == 7 || startInt == 8) {
			return StringUtils.left(shortISBN, 5) + "-" + StringUtils.right(shortISBN, 3);
		}
		
		return StringUtils.left(shortISBN, 6) + "-" + StringUtils.right(shortISBN, 2);
	}
	
	/**
	 * 转换字段标识符,用于国图,calis标准互换
	 * @param srcStr
	 * @param direction 1 calis转换为国图 、 2 国图转化为 calis
	 * @return
	 */
	public static String replaceCharacter(String srcStr,String direction){
		if(null != srcStr){
			//Map中的key是国图标准,value为calis标准
			Map<String,String> datas = CNMarcConstants.replaceCharacter;
			srcStr = doRepalce(srcStr, direction, datas);
		}
		return srcStr;
	}
	/**
	 * 转换子字段标识符,用于国图,calis标准互换
	 * @param srcStr
	 * @param direction 1 calis转换为国图 、 2 国图转化为 calis
	 * @return
	 */
	public static String replaceSubField(String srcStr,String direction){
		if(null != srcStr){
			//Map中的key是国图标准,value为calis标准
			Map<String,String> datas = CNMarcConstants.replaceSubField;
			srcStr = doRepalce(srcStr, direction, datas);
		}
		return srcStr;
	}
	@SuppressWarnings("rawtypes")
	private static String doRepalce(String srcStr, String direction,
			Map<String, String> datas) {
		if(direction.equals("1")){
			if(datas.containsValue(srcStr)){
				for(Iterator itr = datas.entrySet().iterator();itr.hasNext();){
					Map.Entry entry = (Map.Entry)itr.next();
					if(srcStr.equals(entry.getValue())){
						srcStr = String.valueOf(entry.getKey());
						break;
					}
				}
			}
		}
		if(direction.equals("2")){
			if(datas.containsKey(srcStr)){
				for(Iterator itr = datas.entrySet().iterator();itr.hasNext();){
					Map.Entry entry = (Map.Entry)itr.next();
					if(srcStr.equals(entry.getKey())){
						srcStr = String.valueOf(entry.getValue());
						break;
					}
				}
			}
		}
		return srcStr;
	}
	/**
	 * 国图标准转换为calis标准
	 * @param cnmarc
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	public static CNMarc cn2Calis(CNMarc cnmarc) throws UnsupportedEncodingException{
		//转换目次区,如410转为为461
		CNMarcCatalog catalog = cnmarc.getCatalog();
		Map<String,String> newColumnCatalogs = new LinkedHashMap<String, String>();
		for (Iterator ite = catalog.getColumnCatalogs().entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			String colkey = (String) entry.getKey();
			String colValue = (String) entry.getValue();
			//由于重复字段的原因,处理key,只取前三位替换
			String oldkey = CNMarcUtils.substring(colkey, 0, 3);
			String newkey = replaceCharacter(oldkey,"2");
			colkey = StringUtils.replace(colkey, oldkey, newkey);
			newColumnCatalogs.put(colkey, colValue);
		}
		cnmarc.getCatalog().setColumnCatalogs(newColumnCatalogs);
		
		//处理数据区，同样需要转换字段标识符，另外还需要转换子字段标识符，内容
		Map<String, CNMarcColumnBase> newColumns = new LinkedHashMap<String,CNMarcColumnBase>();
		Map<String, CNMarcColumnBase> columns = cnmarc.getColumns();
		for (Iterator ite = columns.entrySet().iterator(); ite.hasNext();) {
			Map.Entry entry = (Map.Entry) ite.next();
			String colkey = (String) entry.getKey();
			CNMarcColumnBase colValue = (CNMarcColumnBase) entry.getValue();
			//由于重复字段的原因,处理key,只取前三位替换
			String oldkey = CNMarcUtils.substring(colkey, 0, 3);
			String newkey = replaceCharacter(oldkey,"2");
			colkey = StringUtils.replace(colkey, oldkey, newkey);
			//转换CNMarcColumnBase对象
			colValue.setCharacter(replaceCharacter(colValue.getCharacter(),"2"));
			List<CNMarcSubColumn> subColumns = colValue.getSubColumns();
			for (CNMarcSubColumn cnMarcSubColumn : subColumns) {
				String subField = cnMarcSubColumn.getSubField();
				cnMarcSubColumn.setSubField(replaceSubField(subField,"2"));
				//第n版转为n版 只针对205字段
				if(oldkey.equals("205") && subField.equals("a")){
					cnMarcSubColumn.setSubContent("第"+cnMarcSubColumn.getSubContent());
				}
			}
			newColumns.put(colkey, colValue);
		}
		cnmarc.setColumns(newColumns);
		return cnmarc;
	}
	/**
	 * 对map按key排序
	 * @param srcMap
	 * @param order 值为 asc 或者 desc
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map sortMap(Map srcMap, final String order) {
		if (null != srcMap) {
			Map destMap = new LinkedHashMap();
			List<Map.Entry<String, String>> mappingList = null;
			// 通过ArrayList构造函数把map.entrySet()转换成list
			mappingList = new ArrayList<Map.Entry<String, String>>(
					srcMap.entrySet());
			// 通过比较器实现比较排序
			Collections.sort(mappingList,
					new Comparator<Map.Entry<String, String>>() {
						public int compare(Map.Entry<String, String> mapping1,
								Map.Entry<String, String> mapping2) {
							if (order.equalsIgnoreCase("desc")) {
								return mapping2.getKey().compareTo(
										mapping1.getKey());
							} else {
								return mapping1.getKey().compareTo(
										mapping2.getKey());
							}
						}
					});
			for (Map.Entry<String, String> mapping : mappingList) {
				destMap.put(mapping.getKey(), mapping.getValue());
			}
			return destMap;
		}
		return null;
	}
	/**
	 * 转换尺寸
	 * @param size
	 * @return 开本
	 */
	@SuppressWarnings("rawtypes")
	public static String formatSize(String size){
		String rtn = size;
		Map<String,String> dimension = CNMarcConstants.dimension;
		for(Iterator itr = dimension.entrySet().iterator();itr.hasNext();){
			Map.Entry entry = (Map.Entry)itr.next();
			if(org.apache.commons.lang.StringUtils.contains(rtn, entry.getKey().toString())){
				rtn = entry.getValue().toString();
				break;
			}
		}
		return rtn;
	}
	/**
	 * 格式化005字段,使其符合YYYYMMDDHHMMSS.T格式,不符合补零
	 * @param col_005
	 * @return
	 */
	public static String format005(String col_005){
		if(!StringUtils.isEmpty(col_005)){
			int length = col_005.length();
			for(;length < 16;length++ ){
				String temp = "0";
				if(length == 14){
					temp = ".";
				}
				col_005 = col_005 + temp;
			}
		}
		return col_005;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(substring("哈哈nihao不aaa", 6, 4));
//		String test = "01177nam0 2200277   450 001001100000005001700011010003200028100004100060101000800101102002300109105001800132106000600150200010400156210003400260215002200294304002900316330023500345333014300580606002700723690001300750690001300763701003100776801002600807856005300833980001300886001155307320120517000000.0  ";
//		System.out.println(substring(test, 0, 24)+"---");
//		System.out.println(substring(test, 12, 5)+"---");
//		System.out.println(Integer.parseInt(substring(test, 12, 5))+"---");
//		System.out.println(substring(test, 24, 277-24-1)+"---");
//		String catalog = "001001100000005001700011010003200028100004100060101000800101102002300109105001800132106000600150200010400156210003400260215002200294304002900316330023500345333014300580606002700723690001300750690001300763701003100776801002600807856005300833980001300886";
//		for(int i = 0 ; i < catalog.getBytes(CNMarcConstants.encoding).length-1; i += 12){
//			System.out.println(CNMarcUtils.substring(catalog, i, 12)+"=======");
//		}
		String fileBody = "2 a研究生数学丛书Ayan jiu sheng shu xue cong shuf张三h1";
		System.out.println(fileBody);
		List<byte[]> dd = splitByteByChar(fileBody.getBytes(CNMarcConstants.ENCODING),CNMarcConstants.F_CHAR31);
		for (Iterator iterator = dd.iterator(); iterator.hasNext();) {
			byte[] bs = (byte[]) iterator.next();
			try {
				System.out.println(new String(bs,"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String res = " 0a张东军Azhang dong jun4编译 ";
		System.out.println(replaceStr(res, CNMarcConstants.F_CHAR31, CNMarcConstants.F_CHAR36));
		
	}
}
