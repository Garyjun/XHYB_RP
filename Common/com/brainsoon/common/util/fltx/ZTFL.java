package com.brainsoon.common.util.fltx;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.util.dofile.util.DateTools;
import com.brainsoon.common.util.fltx.webpage.util.HtmlPage;
import com.brainsoon.common.util.fltx.webpage.util.HtmlUtil;
import com.brainsoon.common.util.fltx.webpage.util.MyFile;
import com.mysql.jdbc.Statement;

/**
 *
 * @ClassName: ZTFL
 * @Description:通过网页抓取中图分类。另外对出错的需要单独进行单独处理，可在日志中查看错误信息。
 * @author: tanghui
 * @date:2015-9-6
 */
public class ZTFL {
	private static LinkedHashMap<String,String> map= new LinkedHashMap<String,String>();
	
	static String className = "com.mysql.jdbc.Driver";
	//源
	static String url1 = "jdbc:mysql://localhost:3306/reaper";

	static String db1_user = "root";
	static String db1_password = "root";
	static Connection connection1 = null;
	static Statement statement1 = null;
	static Connection connection3 = null;
	static Statement statement3 = null;
    static {
    	try {
    		try {
				Class.forName(className).newInstance();
				connection1 = (Connection) DriverManager.getConnection(url1, db1_user,db1_password);
				statement1 = (Statement) connection1.createStatement();
				connection3 = (Connection) DriverManager.getConnection(url1, db1_user,db1_password);
				statement3 = (Statement) connection3.createStatement();
			} catch (InstantiationException e) {
				// tanghui Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// tanghui Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// tanghui Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

    }

	public ZTFL() {
		super();
	}

	

    public static void doHtmlStr(String url){
    	String fristStr = "http://oldweb.lib.sjtu.edu.cn/";
		try {
			HtmlPage page=new HtmlPage(url);
			//System.out.println(page.getHtmlCode());
			HtmlUtil hu=new HtmlUtil(page.getHtmlCode());
			String s=hu.firstStringMatche("<!--默认内容页面 -->", "</a></p>");
//			hu.setHtmlCode(s);
//			String r =hu.delHtmlTag(s);
//			System.out.println(s);
			if(StringUtils.isNotBlank(s)){
				s = s.replaceAll("\\s", "");
//				s = s.replaceAll("<br/>", "");
				s = s.replaceAll("&nbsp;", "");
				s = s.replaceAll("　</strong>", "");
				s = s.replaceAll("</strong>", "");
				s = s.replaceAll("　<strong>", "");
				s = s.replaceAll("<strong>", "");
				s = s.replaceAll("<p>", "");
				s = s.replaceAll("<ahref", "<a href");
				s = s.replaceAll("</p>", "");
				s = s.replaceAll("&ldquo;", "");
				s = s.replaceAll("&middot;", "");
				s = s.replaceAll("&rdquo;", "");
				s = s.replaceAll("<palign=\"center\">", "");
				s = s.replaceAll("&lt;", "");
				String[] arg = s.split("<br/>");
				for (int i = 0; i < arg.length; i++) {
					String stri = arg[i];
					if(StringUtils.isNotBlank(stri.trim())){
						String reStr = stri.trim();
						//System.out.println("************原1*********");
						//System.out.println("************new1*********");
						//System.out.println("************原2*********");
						//System.out.println("************new1*********");
						if(reStr.indexOf("<a href=") != -1){
							if(!page.getUrl().equals(new URL(fristStr + "view.do?id=516"))){
								String myStr1 = "";
								String myStr2 = "";
								String myStr3 = "";
								if(reStr.indexOf("特殊分类规定")==-1){
									if(reStr.indexOf("返回") != -1 || reStr.toUpperCase().indexOf("BACK") != -1){
										myStr2 = reStr.substring(0, reStr.lastIndexOf("<a href="));
									}else{
										if(!reStr.endsWith("</a>")){
											myStr2 = reStr.substring(reStr.lastIndexOf("\">")+2, reStr.length()).trim();
											myStr3 = reStr.substring(reStr.lastIndexOf("href=\"")+6, reStr.lastIndexOf("\">")).trim();
										}else{
											myStr2 = reStr.substring(reStr.lastIndexOf("\">")+2, reStr.length()).trim();
											myStr2 = myStr2.substring(0, myStr2.lastIndexOf("</a>"));
											myStr3 = reStr.substring(reStr.lastIndexOf("href=\"")+6, reStr.lastIndexOf("\">")).trim();
										}
									}
										String reStrOther = myStr2.trim().replaceAll("　", ",");
										if(reStrOther.indexOf(",") != -1){
											String[] reStrOthers = reStrOther.split(",");
											for (int j = 0; j < reStrOthers.length; j++) {
												String myStr = reStrOthers[j].trim();
												if(StringUtils.isNotBlank(myStr)){
													if(myStr1.equals("")){
														myStr1 = myStr;
													}else{
														myStr2 = myStr;
													}
												 }
											   }
										    }
											if(StringUtils.isNotBlank(myStr3)){
												//if(map.get(myStr1) == null){
													map.put(myStr1, myStr1 + "," + myStr2);
												//}
												doHtmlStr(fristStr + myStr3);
												//System.out.println(myStr1 + "^^^eee^^" + myStr2+ "^^^^^" + myStr3);
											}else{
												//if(map.get(myStr1) == null){
													map.put(myStr1, myStr1 + "," + myStr2);
												//}

												////System.out.println(myStr1 + "^^^eee^^" + myStr2+ "^^^^^" + myStr3);
											}
								}
							}else{
								if(reStr.indexOf("特殊分类规定")==-1){
									String myStr1 = reStr.substring(0, reStr.indexOf("<")).trim();
									String myStr2 = reStr.substring(reStr.lastIndexOf("\">")+2, reStr.length()).trim();
									String myStr3 = reStr.substring(reStr.lastIndexOf("href=\"")+6, reStr.lastIndexOf("\">")).trim();
									myStr1 = myStr1.replaceAll("<palign=\"left\">", "");
									myStr1 = myStr1.replaceAll("<palign=\"center\">", "");
									myStr2 = myStr2.replaceAll("<palign=\"left\">", "");
									myStr2 = myStr2.replaceAll("<palign=\"center\">", "");
									if(StringUtils.isNotBlank(myStr3)){
										//if(map.get(myStr1) == null){
											map.put(myStr1, myStr1 + "," + myStr2);
										//}
										doHtmlStr(fristStr + myStr3);
										//System.out.println(myStr1 + "^^^^^" + myStr2+ "^^^^^" + myStr3);
									}else{
										//if(map.get(myStr1) == null){
											map.put(myStr1, myStr1 + "," + myStr2);
										//}
									}
								}
							}

						}else{
							if(reStr.indexOf("特殊分类规定")==-1){
								String reStrOther = reStr.trim().replaceAll("　", ",");
								String myStr1 = "";
								String myStr2 = "";
								if(reStrOther.indexOf(",") != -1){
									String[] reStrOthers = reStrOther.split(",");
									for (int j = 0; j < reStrOthers.length; j++) {
										String myStr = reStrOthers[j].trim();
										if(StringUtils.isNotBlank(myStr)){
											if(myStr1.equals("")){
												myStr1 = myStr;
											}else{
												myStr2 = myStr;
											}
										}
									}
									if((myStr2.indexOf("返回") != -1  || reStr.toUpperCase().indexOf("BACK") != -1) && myStr2.indexOf("<a href=") != -1){
										myStr2 = myStr2.substring(0, myStr2.lastIndexOf("<a href="));
									}
									//System.out.println(myStr1 + "^^1^^^" + myStr2);
								}
								myStr1 = myStr1.replaceAll("<palign=\"left\">", "");
								myStr1 = myStr1.replaceAll("<palign=\"center\">", "");
								myStr2 = myStr2.replaceAll("<palign=\"left\">", "");
								myStr2 = myStr2.replaceAll("<palign=\"center\">", "");
								//if(map.get(myStr1) == null){
									map.put(myStr1, myStr1 + "," + myStr2);
								//}
							}
						}


						//System.out.println("************new2*********");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static boolean is_alpha(String alpha) {  
        if(alpha==null) return false;  
        return alpha.matches("[a-zA-Z]+");      
    } 
    
    public static void re(String myStr1,String myStr2,String myStr3){
    	if(StringUtils.isNotBlank(myStr1)){
    		myStr1 = myStr1.replaceAll("<palign=\"left\">", "");
    		myStr1 = myStr1.replaceAll("<palign=\"center\">", "");
    	}
    	
    	if(StringUtils.isNotBlank(myStr2)){
    		myStr2 = myStr2.replaceAll("<palign=\"left\">", "");
    		myStr2 = myStr2.replaceAll("<palign=\"center\">", "");
    	}
    	
    	if(StringUtils.isNotBlank(myStr3)){
    		myStr3 = myStr3.replaceAll("<palign=\"left\">", "");
    		myStr3 = myStr3.replaceAll("<palign=\"center\">", "");
    	}
    }
    
    public static void cjztflh(){
    	String fristStr = "http://oldweb.lib.sjtu.edu.cn/";
		StringBuffer strssss = new StringBuffer();
		StringBuffer strokssss =  new StringBuffer();
		//测试开始
		long ss = DateTools.getStartTime();
		try {
			HtmlPage page = new HtmlPage(new URL(fristStr + "view.do?id=516"));
			//System.out.println(page.getHtmlCode());
			HtmlUtil hu=new HtmlUtil(page.getHtmlCode());
			String s=hu.firstStringMatche("<!--默认内容页面 -->", "</a></p>");
//			hu.setHtmlCode(s);
//			String r =hu.delHtmlTag(s);
//			System.out.println(s);
			System.out.println("- 页面抓取中....");
			if(StringUtils.isNotBlank(s)){
				s = s.replaceAll("\\s", "");
//				s = s.replaceAll("<br/>", "");
				s = s.replaceAll("&nbsp;", "");
				s = s.replaceAll("　</strong>", "");
				s = s.replaceAll("</strong>", "");
				s = s.replaceAll("　<strong>", "");
				s = s.replaceAll("<strong>", "");
				s = s.replaceAll("<p>", "");
				s = s.replaceAll("<ahref", "<a href");
				s = s.replaceAll("</p>", "");
				s = s.replaceAll("&ldquo;", "");
				s = s.replaceAll("&middot;", "");
				s = s.replaceAll("&rdquo;", "");
				s = s.replaceAll("<palign=\"center\">", "");
				s = s.replaceAll("&lt;", "");
				String[] arg = s.split("<br/>");
				for (int i = 0; i < arg.length; i++) {
					String stri = arg[i];
					if(StringUtils.isNotBlank(stri.trim())){
						String reStr = stri.trim();
						//System.out.println("************原1*********");
						//////System.out.println("************new1*********");
						//System.out.println("************原2*********");
						//System.out.println("************new1*********");
						if(reStr.indexOf("<a href=") != -1){
							if(!page.getUrl().equals(new URL(fristStr + "view.do?id=516"))){
								String myStr1 = "";
								String myStr2 = "";
								String myStr3 = "";
								if(reStr.indexOf("特殊分类规定")==-1){
									if(reStr.indexOf("返回") != -1 || reStr.toUpperCase().indexOf("BACK") != -1){
										myStr2 = reStr.substring(0, reStr.lastIndexOf("<a href="));
									}else{
										if(!reStr.endsWith("</a>")){
											myStr2 = reStr.substring(reStr.lastIndexOf("\">")+2, reStr.length()).trim();
											myStr3 = reStr.substring(reStr.lastIndexOf("href=\"")+6, reStr.lastIndexOf("\">")).trim();
										}else{
											myStr2 = reStr.substring(reStr.lastIndexOf("\">")+2, reStr.length()).trim();
											myStr2 = myStr2.substring(0, myStr2.lastIndexOf("</a>"));
											myStr3 = reStr.substring(reStr.lastIndexOf("href=\"")+6, reStr.lastIndexOf("\">")).trim();
										}
									}
										String reStrOther = myStr2.trim().replaceAll("　", ",");
										if(reStrOther.indexOf(",") != -1){
											String[] reStrOthers = reStrOther.split(",");
											for (int j = 0; j < reStrOthers.length; j++) {
												String myStr = reStrOthers[j].trim();
												if(StringUtils.isNotBlank(myStr)){
													if(myStr1.equals("")){
														myStr1 = myStr;
													}else{
														myStr2 = myStr;
													}
												 }
											   }
										    }
											
										    myStr1 = myStr1.replaceAll("<palign=\"left\">", "");
											myStr1 = myStr1.replaceAll("<palign=\"center\">", "");
											myStr2 = myStr2.replaceAll("<palign=\"left\">", "");
											myStr2 = myStr2.replaceAll("<palign=\"center\">", "");
											if(StringUtils.isNotBlank(myStr3)){
												//if(map.get(myStr1) == null){
													map.put(myStr1, myStr1 + "," + myStr2);
												//}
												doHtmlStr(fristStr + myStr3);
												//System.out.println(myStr1 + "^^^eee^^" + myStr2+ "^^^^^" + myStr3);
											}else{
												//if(map.get(myStr1) == null){
													map.put(myStr1, myStr1 + "," + myStr2);
												//}

												////System.out.println(myStr1 + "^^^eee^^" + myStr2+ "^^^^^" + myStr3);
											}
								}
							}else{
								if(reStr.indexOf("特殊分类规定")==-1){
									String myStr1 = reStr.substring(0, reStr.indexOf("<")).trim();
									String myStr2 = reStr.substring(reStr.lastIndexOf("\">")+2, reStr.length()).trim();
									String myStr3 = reStr.substring(reStr.lastIndexOf("href=\"")+6, reStr.lastIndexOf("\">")).trim();
									myStr1 = myStr1.replaceAll("<palign=\"left\">", "");
									myStr1 = myStr1.replaceAll("<palign=\"center\">", "");
									myStr2 = myStr2.replaceAll("<palign=\"left\">", "");
									myStr2 = myStr2.replaceAll("<palign=\"center\">", "");
									if(StringUtils.isNotBlank(myStr3)){
										//if(map.get(myStr1) == null){
											map.put(myStr1, myStr1 + "," + myStr2);
										//}
										doHtmlStr(fristStr + myStr3);
										//System.out.println(myStr1 + "^^^^^" + myStr2+ "^^^^^" + myStr3);
									}else{
										//if(map.get(myStr1) == null){
											map.put(myStr1, myStr1 + "," + myStr2);
										//}
									}
								}
							}

						}else{
							if(reStr.indexOf("特殊分类规定")==-1){
								String reStrOther = reStr.trim().replaceAll("　", ",");
								String myStr1 = "";
								String myStr2 = "";
								if(reStrOther.indexOf(",") != -1){
									String[] reStrOthers = reStrOther.split(",");
									for (int j = 0; j < reStrOthers.length; j++) {
										String myStr = reStrOthers[j].trim();
										if(StringUtils.isNotBlank(myStr)){
											if(myStr1.equals("")){
												myStr1 = myStr;
											}else{
												myStr2 = myStr;
											}
										}
									}
									if((myStr2.indexOf("返回") != -1  || reStr.toUpperCase().indexOf("BACK") != -1) && myStr2.indexOf("<a href=") != -1){
										myStr2 = myStr2.substring(0, myStr2.lastIndexOf("<a href="));
									}
									//System.out.println(myStr1 + "^^1^^^" + myStr2);
								}
								myStr1 = myStr1.replaceAll("<palign=\"left\">", "");
								myStr1 = myStr1.replaceAll("<palign=\"center\">", "");
								myStr2 = myStr2.replaceAll("<palign=\"left\">", "");
								myStr2 = myStr2.replaceAll("<palign=\"center\">", "");
								//if(map.get(myStr1) == null){
									map.put(myStr1, myStr1 + "," + myStr2);
								//}
							}
						}
						//System.out.println("************new2*********");
					}
				}
			}
			
			System.out.println("");
			System.out.println("抓取结束！！！ 共用时： " + DateTools.getTotaltime(ss));
			System.out.println("");
			StringBuffer strmap = new StringBuffer();
			for (Entry<String, String> b1 : map.entrySet()) {
				   String key = b1.getKey();
			       String val = b1.getValue();
			       if(StringUtils.isNotBlank(val) && !val.trim().equals("")){
			    	   val = val.trim();
			    	   strmap.append(key +"@@@@@" + val + "\n");
			       }
			  }
			try {
				MyFile.creatTxtFile("ztfl-map");
				MyFile.writeTxtFile(strmap.toString() + "\n 全用时： " + DateTools.getTotaltime(ss));
				System.out.println("- 创建ztfl-map.txt文件成功！！！");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			System.out.println("");
			System.out.println("入库开始...");
			System.out.println("");
			
			
			//处理特殊情况字符串
			String str ="TB499,业三废处理与综合利用|A121/125,各时期单行著作|A321/328,各时期单行著作|A56,专题汇编|A85,著作汇编的学习和研究|TB41,爆破技术|TB42,密封技术|TB43,薄膜技术|TB44,粉末技术|TB47,工业设计|TB472产品设计|TB476,产品模型制作|TB48,包装工程|TB482,包装设计|[TB482.1],装潢设计|TB482.2,结构设计|TB484,包装材料|TB484.1,纸、纸板|TB484.2,木、木材|TB484.3,塑料|TB484.4,金属|TB484.5,玻璃、陶瓷|TB484.9,其他|TB485,包装类型|TB485.1,缓冲包装|TB485.2,充气包装|TB485.3,运输包装|TB485.4,防锈包装|TB485.5,防潮包装|TB485.9,其他|TB486,包装机械设备|TB486+.1,单机|TB486+.2,组合机|TB486+.3,自动控制机|TB487,包装技术检测|TB488,包装工厂|TB489,各类产品包装|TB49,工厂、车间|TB491,规划与设计|TB492,设备安装与运行|TB493,力能供应与节能|TB494,空调与照明|TB495,给水、排水|TB496,安全与卫生|TB497,技术管理|TB498,贮运|TB51,声学仪器|TB51+1,声振荡器|TB51+2,辐射器和接收器|TB51+3,液声仪|TB51+4,流体测位仪|TB51+5,声音发讯仪|TB51+6,声波分析器|TB51+7,超声波仪器|TB51+8,语音测验仪器|TB52,声学测量|TB52+1,互易原理和声学校准|TB52+2,声压的测量|TB52+3,振动与冲击的测量|TB52+4,声功率的测量|TB52+5,声场的测量|TB52+6,频谱分析|TB52+7,声阻抗的测量|TB52+8,声学仪器校准|TB52+9,计算技术在声学测量中的应用|TB53,振动、噪声及其控制|TB532,振动体的振动与辐射|TB533,振动与噪声的发生|TB533+.1,机器振动与噪声|TB533+.2,交通运输工具的振动与噪声|TB533+.3,高航速的振动与噪声|TB533+.4,城市噪声|TB534,噪声发生器与振动发生器|TB534+.1,噪声发生器及其分析|TB534+.2,振动发生器、振动台及其分析|TB534+.3,材料机件的耐振试验、振动疲劳及声疲劳试验|TB535,振动和噪声的控制及其利用|TB535+.1,隔振、减振材料与结构|TB535+.2,消声器、滤波器及其测试|TB535+.3,噪声的利用|TB54,电声工程|TB55,超声工程|TB551,超声测量|TB552,超声换能器|TB553,超声控制与检测|TB559,超声的应用|TB56,水声工程|TB561,水下声源|TB564,水声材料|TB565,水声仪器与设备|TB565+.1,水声换能器、水听器|TB565+.2,水声探测设备|TB565+.3,发射与接收设备|TB565+.4,显示、记录与数据处理设备|TB565+.5,水池、水槽|TB566,水声探测|[TB567],水下通信（声纳通信|[TB568,水声导航|[TU279.7+44],电视塔、无线电塔、输电构架、雷达无线电塔、索道塔架";
			String[] strs = str.split("[|]");
			for (int i = 0; i < strs.length; i++) {
				String[] strss = strs[i].split(",");
				map.put(strss[0], strs[i]);
			}

			if(map.size() > 0){
				int number = 0;
				int c = 0;
				for (Entry<String, String> b : map.entrySet()) {
					   String key = b.getKey();
				       String val = b.getValue();
				       if(StringUtils.isNotBlank(val) && !val.trim().equals("")){
				    	   val = val.trim();
				    	   val = val.replaceAll("</a>", "").replaceAll("　", "").trim();
					       System.out.println("key ===> " + key  + "  val ==> " + val);//获取值
//					       if(val.indexOf("数学理论") !=-1){
//					    	   System.out.println(val);
//					       }
					       String[] vals = val.split(",");
					       if(vals.length ==2){
					    	   String myStr1 = vals[0];
							   String myStr2 = vals[1];
							   if(StringUtils.isNotBlank(myStr1) && StringUtils.isNotBlank(myStr2)){
								   myStr1 = vals[0].trim();
								   myStr2 = vals[1].trim();
								   //System.out.println("myStr1 ===> " + myStr1  + "  myStr2 ==> " + myStr2);//获取值
						    	   String sql1 = "select count(1) from res_category t where t.code = '" + myStr1 + "' and t.type = 1";
								   ResultSet resultSet1 = statement1.executeQuery(sql1);
								   int rows = 0;
								   while (resultSet1.next()) {
									    rows = resultSet1.getInt(1);//得到指定行的行号
								   }
								   if(rows == 0){
									   String endPath = "";
									   Long parent_id = null;
									   if((myStr1.startsWith("{") && myStr1.endsWith("}"))
											   || (myStr1.startsWith("[") && myStr1.endsWith("]"))){
										   myStr1 = (myStr1.substring(1, myStr1.length()-1));
									   }
									   String sqlMyStr1 = myStr1;
									   sqlMyStr1 = sqlMyStr1.substring(0,sqlMyStr1.length()-1);
									   for(int i=0;i<sqlMyStr1.length();i++){
										   String sql2 = "select count(1) from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 1";
										   ResultSet resultSet2 = statement1.executeQuery(sql2);
										   int rowss = 0;
										   while (resultSet2.next()) {
											    rowss = resultSet2.getInt(1);//得到指定行的行号
										   }
										   if(rowss > 0){
											   String sql22 = "select * from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 1";
											   ResultSet resultSet22 = statement1.executeQuery(sql22);
											     while (resultSet22.next()) {
												   parent_id = resultSet22.getLong("id");
												   String path = resultSet22.getString("path");
												   //System.out.println(parent_id + " ------ " + path);//获取键
												   if(StringUtils.isNotBlank(path)){
													   String[] pathArr = path.split(",");
													   int size = pathArr.length;
													   if(size > 1){
														   String hzPath = pathArr[size-1];
														   endPath = path.substring(0,path.length()-(hzPath.length()+1));
													   }
												   }
												   if(parent_id != null){
													   if(StringUtils.isNotBlank(endPath)){
														   endPath += "," + parent_id;
													   }else{
														   endPath +=  parent_id;
													   }
												   }
											   }
											 }else{
												 i=0;
												 if(sqlMyStr1.length()!=1){
												   sqlMyStr1 = sqlMyStr1.substring(0,sqlMyStr1.length()-1);
												   if(sqlMyStr1.length() == 1){
													   String sql22 = "select * from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 1";
													   ResultSet resultSet22 = statement1.executeQuery(sql22);
														   while (resultSet22.next()) {
															   parent_id = resultSet22.getLong("id");
															   String path = resultSet22.getString("path");
															   //System.out.println(parent_id + " ------ " + path);//获取键
															   if(StringUtils.isNotBlank(path)){
																   String[] pathArr = path.split(",");
																   int size = pathArr.length;
																   if(size > 1){
																	   String hzPath = pathArr[size-1];
																	   endPath = path.substring(0,path.length()-(hzPath.length()+1));
																   }
															   }
															   if(parent_id != null){
																   if(StringUtils.isNotBlank(endPath)){
																	   endPath += "," + parent_id;
																   }else{
																	   endPath +=  parent_id;
																   }
															   }
														     }
												      }
												 }
											 }
										   
										   if(StringUtils.isNotBlank(endPath)){
											   break;
										   }
									   }
									   
									   if(parent_id == null){
										   parent_id = 0l;
									   }
									   
									   myStr1 = myStr1.replaceAll("<palign=\"left\">", "");
									   myStr1 = myStr1.replaceAll("<palign=\"center\">", "");
									   myStr1 = myStr1.replaceAll("\\[", "");
									   
									   //首字符不包含字母，则跳过
									   String myStr0 = myStr1.substring(0, 1);
									   if(!is_alpha(myStr0)){
										   continue;
									   }
									   String sq3 = "insert into res_category(name,type,code,parent_id,path,display_order) values ('" + myStr2 + "','1','" + myStr1 + "'," + parent_id + ",'" + endPath + "',1);";
									   statement1.execute(sq3);
									   System.out.println(sq3);
									   strokssss.append(sq3 + "\n");
									   
									   //===========更新path===================================
									   String sql222 = "select t.id,t.path from res_category t where t.code  = '" +  myStr1 + "' and t.type = 1";
									   ResultSet resultSet222 = statement3.executeQuery(sql222);
									   while (resultSet222.next()) {
										  Long id = resultSet222.getLong("id");
										  String path = resultSet222.getString("path");
										  if(StringUtils.isBlank(path)){
											  path = id + "";
										  }else{
											  path += "," + id;  
										  }
										  
										   String[] endPaths = path.split(",");
										   String endPathStr = "";
										   Map<String,String> maps = new HashMap<String,String>();
										   for (int j = 0; j < endPaths.length; j++) {
											   if(StringUtils.isNotBlank(endPaths[j]) && maps.get(endPaths[j]+"") == null){
												   if(endPathStr == ""){
													   endPathStr +=  endPaths[j]+"";
												   }else{
													   endPathStr +=  "," + endPaths[j];
												   }
												   maps.put(endPaths[j]+"", endPaths[j]);
											   }
										   }
										   maps = null;
										   
										  String sq4 = "update res_category set path = '"+ endPathStr + "' where id=" + id;
										  statement1.execute(sq4);
										  System.out.println(sq4);
										  strokssss.append(sq4 + "\n");
									   }
									   
								   }
						       }else{
						    	   if(vals.length ==1){
						    		   myStr1 = vals[0].trim();
						    		   if(StringUtils.isNotBlank(myStr1)){
								    	   strssss.append(myStr1 + "|");
						    		   }
						    	   }
						       }

						   }else{
					    	   val = key;
					    	   strssss.append(key + "|");
					       }

					   }
				       number++;
				       int num = 4000;
				       //每4000条生成一个txt文本
				       if(map.size() == number || number == num || ((num*c + number)  == map.size())){
				    	    c++;
				    	    System.out.println();
							System.out.println(num*c + number + "条入库成功！！！");
							System.out.println();
							System.out.println();
							System.out.println(strssss);//获取键
							System.out.println();
							System.out.println();
							MyFile.creatTxtFile("ztfl" + c);
							MyFile.writeTxtFile(strokssss.toString() + "\n 全用时： " + DateTools.getTotaltime(ss));
							System.out.println("- 创建ztfl" + c + ".txt文件成功！！！");
							MyFile.creatTxtFile("ztfl-" + c + "-error");
							MyFile.writeTxtFile(strssss.toString() + "\n 全用时： " + DateTools.getTotaltime(ss));
							System.out.println("-ztfl-" + c + "-error.txt文件成功！！！");
							System.out.println();
							System.out.println();
							number = 0;
							strokssss =  new StringBuffer();
							strssss =  new StringBuffer();
							if((4000*c + number)  == map.size()){
								System.out.println("- 全部执行完毕！！！ 全用时： " + DateTools.getTotaltime(ss));
							}
				       }
				  }
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void updateztflh(){
      try {
    	 LinkedHashMap<Long,String> map = new LinkedHashMap<Long,String>();
    	 String sql0 = "select * from res_category t where t.type = 1";
    	 ResultSet resultSet0 = statement1.executeQuery(sql0);
	     while (resultSet0.next()) {
	       Long id = resultSet0.getLong("id");
		   String sqlMyStr1 = resultSet0.getString("code");
		   if(sqlMyStr1.length() == 1){
			   continue;
		   }
		   map.put(id,sqlMyStr1);
	     }

		if(map.size() > 0){
			for (Entry<Long, String> b : map.entrySet()) {
			   String endPath = "";
			   Long parent_id = 0l;
			   Long id = b.getKey();
			   if(id==15){
				   System.out.println("======id=========== " + id);
			   }
			   String sqlMyStr1 = b.getValue();
		       sqlMyStr1 = sqlMyStr1.substring(0,sqlMyStr1.length()-1);
		       for(int i=0;i< sqlMyStr1.length();i++){
		    	   //查父id
				   String sql2 = "select count(1) from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 1";
				   ResultSet resultSet2 = statement1.executeQuery(sql2);
				   int rowss = 0;
				   while (resultSet2.next()) {
					    rowss = resultSet2.getInt(1);//得到指定行的行号
				   }
				   if(rowss > 0){
					   String sql22 = "select * from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 1";
					   ResultSet resultSet22 = statement1.executeQuery(sql22);
					     while (resultSet22.next()) {
						   parent_id = resultSet22.getLong("id");
						   String path = resultSet22.getString("path");
						   //System.out.println(parent_id + " ------ " + path);//获取键
						   if(StringUtils.isNotBlank(path)){
							   String[] pathArr = path.split(",");
							   int size = pathArr.length;
							   if(size > 1){
								   String hzPath = pathArr[size-1];
								   endPath = path.substring(0,path.length()-(hzPath.length()+1));
							   }
						   }
						   if(parent_id != null){
							   if(StringUtils.isNotBlank(endPath)){
								   endPath += "," + parent_id;
							   }else{
								   endPath +=  parent_id;
							   }
						   }
					   }
					 }else{
						 i=0;
						 if(sqlMyStr1.length()!=1){
						   sqlMyStr1 = sqlMyStr1.substring(0,sqlMyStr1.length()-1);
						   if(sqlMyStr1.length() == 1){
							   String sql22 = "select * from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 1";
							   ResultSet resultSet22 = statement1.executeQuery(sql22);
								   while (resultSet22.next()) {
									   parent_id = resultSet22.getLong("id");
									   String path = resultSet22.getString("path");
									   //System.out.println(parent_id + " ------ " + path);//获取键
									   if(StringUtils.isNotBlank(path)){
										   String[] pathArr = path.split(",");
										   int size = pathArr.length;
										   if(size > 1){
											   String hzPath = pathArr[size-1];
											   endPath = path.substring(0,path.length()-(hzPath.length()+1));
										   }
									   }
									   if(parent_id != null){
										   if(StringUtils.isNotBlank(endPath)){
											   endPath += "," + parent_id;
										   }else{
											   endPath +=  parent_id;
										   }
									   }
								     }
						         }
						    }
		               }
				   
				   if(StringUtils.isNotBlank(endPath)){
					   break;
				   }
			  
				 }
		       
			   if(StringUtils.isNotBlank(endPath)){
				 //===========更新path===================================
					 String sq4 = "update res_category set path = '"+ (endPath + "," + id) + "' , parent_id = " + parent_id + " where id=" + id;
					 try {
						statement3.execute(sq4);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					  System.out.println(sq4);
			      } 
			   }
		   }
        } catch (Exception e) {
			e.printStackTrace();
		}
    }


	public static void main(String[] args) throws Exception{
		cjztflh();  //采集中图分类号
		updateztflh(); //更新中图分类号
	}
}
