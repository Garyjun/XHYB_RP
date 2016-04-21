package com.brainsoon.common.util.fltx;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.brainsoon.common.util.dofile.util.DateTools;
import com.brainsoon.common.util.dofile.util.DoFileUtils;
import com.brainsoon.common.util.fltx.webpage.util.MyFile;
import com.mysql.jdbc.Statement;

/**
 * 
 * @author 唐辉
 * 学科分类编码
 */
public class XKFL {

	private static Log logger = LogFactory.getLog(XKFL.class);
	
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
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

    }
	



	public static void main(String[] args) throws Exception{
		StringBuffer strssss = new StringBuffer();
		StringBuffer strokssss =  new StringBuffer();
		//测试开始
		long ss = DateTools.getStartTime();
		try {
			List<String> listTxt =  null;
			File file = null;
			String localLogPath = "F://projectJEE/BSFW_ZB/Common/com/brainsoon/common/util/fltx/file/中华人民共和国学科分类与代码简表.txt";
			if(StringUtils.isNotBlank(localLogPath)){
				file = new File(localLogPath);
				if(file.exists()){
					listTxt = DoFileUtils.readTxt(localLogPath, "");
				}
			}
			
			if(listTxt != null && listTxt.size() > 0){
			   for (int i = 0; i < listTxt.size(); i++) {
				  String srcFileName = listTxt.get(i);
				  if(StringUtils.isNotBlank(srcFileName)){
					  String[] strs = srcFileName.split(",");
					  if(strs != null && strs.length > 0){
						  map.put(strs[0], srcFileName);
					  }
				   }
			    }
			}
			
			if(map.size() > 0){
				int k = 0;
				int number = 0;
				int c = 0;
				for (Entry<String, String> b : map.entrySet()) {
					  logger.info("共有：【" +map.size() +"】条记录，还有：【" + (map.size()-k) +"】条记录待处理。");
					   String key = b.getKey();
				       String val = b.getValue().trim();
				       if(StringUtils.isNotBlank(val)){
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
						    	   String sql1 = "select count(1) from res_category t where t.code = '" + myStr1 + "' and t.type = 2";
								   ResultSet resultSet1 = statement1.executeQuery(sql1);
								   int rows = 0;
								   while (resultSet1.next()) {
									    rows = resultSet1.getInt(1);//得到指定行的行号
								   }
								   if(rows == 0){
									   String endPath = "";
									   Long parent_id = null;
									   String sqlMyStr1 = myStr1;
									   sqlMyStr1 = sqlMyStr1.substring(0,sqlMyStr1.length()-1);
									   for(int i=0;i<sqlMyStr1.length();i++){
										   String sql2 = "select count(1) from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 2";
										   ResultSet resultSet2 = statement1.executeQuery(sql2);
										   int rowss = 0;
										   while (resultSet2.next()) {
											    rowss = resultSet2.getInt(1);//得到指定行的行号
										   }
										   if(rowss > 0){
											   String sql22 = "select * from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 2";
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
													   String sql22 = "select * from res_category t where t.code  = '" +  sqlMyStr1 + "' and t.type = 2";
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
									   
									   String sq3 = "insert into res_category(name,type,code,parent_id,path) values ('" + myStr2 + "','2','" + myStr1 + "'," + parent_id + ",'" + endPath + "');";
									   statement1.execute(sq3);
									   System.out.println(sq3);
									   strokssss.append(sq3 + "\n");
									   
									   //===========更新path===================================
									   String sql222 = "select t.id,t.path from res_category t where t.code  = '" +  myStr1 + "' and t.type = 2";
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
													   endPathStr +=  endPaths[j];
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
				       k++;
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
							MyFile.creatTxtFile("xkfl" + c);
							MyFile.writeTxtFile(strokssss.toString() + "\n 全用时： " + DateTools.getTotaltime(ss));
							System.out.println("- 创建xkfl" + c + ".txt文件成功！！！");
							MyFile.creatTxtFile("xkfl-" + c + "-error");
							MyFile.writeTxtFile(strssss.toString() + "\n 全用时： " + DateTools.getTotaltime(ss));
							System.out.println("-xkfl-" + c + "-error.txt文件成功！！！");
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
}
