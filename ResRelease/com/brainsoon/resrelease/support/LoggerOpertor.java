package com.brainsoon.resrelease.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * 
 * @ClassName: LoggerOpertor 
 * @Description: 生成日志文件 
 * @author tanghui 
 * @date 2014-11-27 下午2:37:27 
 *
 */
public class LoggerOpertor{
	protected static final Logger logger = Logger.getLogger(LoggerOpertor.class);

	/**
	 * 
	 * @Title: readTxtFile 
	 * @Description: 读取
	 * @param  type  qfm：封面      qyf：源文件     qcf：转换后文件
	 * @return  Map<String,ResMsg> 
	 * @throws
	 */
    public static Map<String,ResMsg> readTxtFile(String filePath,String type,Map<String,ResMsg> map){
    	InputStreamReader read = null;
    	try {
        	if(map == null){
        		map = new HashMap<String,ResMsg>();
        	}
            String encoding="UTF-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                read = new InputStreamReader(
                new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    if(StringUtils.isNotBlank(lineTxt)){
                    	String name = ""; //资源名称
                    	String[] txt = lineTxt.split("=");
                    	if(txt != null && txt.length == 2){
                    		name = txt[1]; //资源id
                    		if(StringUtils.isNotBlank(name)){
                    			ResMsg resMsg = map.get(name);
                    			if(resMsg == null){
                    				resMsg = new ResMsg();
                    				resMsg.setName(name);
                    			}
                    			if(type.equals("qfm")){
                            		resMsg.setQfm(true);
                            	}else if(type.equals("qyf")){
                            		resMsg.setQyf(true);
                            	}else if(type.equals("qcf")){
                            		resMsg.setQcf(true);
                            	}
                        		String txt1 = txt[1];
                        		if(StringUtils.isNotBlank(txt1)){
                        			String[] txt2 = txt1.split(",");
                        			if(txt2 != null && txt2.length == 2){
                        				String resId = txt2[0]; //资源id
                                		String resPath = txt2[1]; //资源路径
                                		if(StringUtils.isNotBlank(resId)){
                                			resMsg.setResId(resId);
                                		}
                                		if(StringUtils.isNotBlank(resPath)){
                                			resMsg.setResPath(resPath);
                                		}
                        			}else{
                        				
                        			}
                        		}
                        		//放入map中
                            	//map.remove(name);
                            	map.put(name, resMsg);
                    		}
                    	}else{
                    		
                    	}
                    }
                }
                
	        }else{
	            System.out.println("找不到指定的文件");
	        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }finally{
        	if(read != null){
        		try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        return map;
    }
    
    
    /**
     * 
     * @Title: showMsg 
     * @Description: 
     * @param   showFullInfo 是否显示全部信息 true 显示  false 只显示资源名称
     * @return String 
     * @throws
     */
    public static String showMsg(Map<String,ResMsg> map,boolean showFullInfo) {
    	StringBuffer msg = new StringBuffer();
    	int baseSourceNum = 0;
    	int thumbSourceNum = 0;
    	int converSourceNum = 0;
    	if(map != null && map.size() > 0){
    		for (String key : map.keySet()) {
			   ResMsg resMsg = map.get(key);
			   if(resMsg != null){
				   String str = "";
				   if(resMsg.getQyf()){
					   str += "源文件";
					   baseSourceNum++;
				   }
				   if(resMsg.getQfm()){
					   if(StringUtils.isBlank(str)){
						   str += "封面";
						   thumbSourceNum++;
					   }else{
						   str += ",封面";
						   thumbSourceNum++;
					   }
				   }
				   if(resMsg.getQcf()){
					   if(StringUtils.isBlank(str)){
						   str += "转换后的文件";
						   converSourceNum++;
					   }else{
						   str += ",转换后的文件";
						   converSourceNum++;
					   }
				   }
				   
				   if(StringUtils.isNotBlank(str)){
					   if(showFullInfo){
						   msg.append("资源名【" + key  +"】,资源ID【" + resMsg.getResId() +"】,资源PATH【" + resMsg.getResPath() +"】：缺{" + str + "}\n");
					   }else{
						   msg.append("资源名【" + key  +"】：缺{" + str + "}\r\n");
					   }
				   }
			   }
			}
    		msg.append("\r\n");
    		msg.append("\r\n");
    		msg.append("总计:\r\n");
    		if(baseSourceNum!=0){
    			msg.append("【"+baseSourceNum+"】个缺源文件\n");
    		}
    		if(thumbSourceNum!=0){
    			msg.append("【"+thumbSourceNum+"】个缺封面\n");
    		}
    		if(converSourceNum!=0){
    			msg.append("【"+converSourceNum+"】个缺转换后的文件\n");
    		}
    	}
    	System.out.println(msg.toString());
    	return msg.toString();
    }
    
  
    /**
     * 写txt
     * 
     * @param file
     * @param sb
     */
    public static void createTxt(String file, String str) {
    	XMLWriter xmlWriter = null;
    	FileOutputStream fos = null;
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            format.setExpandEmptyElements(true);
            format.setTrimText(false);
            fos = new FileOutputStream(file);
            xmlWriter = new XMLWriter(fos, format);
            xmlWriter.write(str);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	if(xmlWriter != null){
        		try {
					xmlWriter.close();
				} catch (IOException e) {
					// tanghui Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	if(fos != null){
        		try {
					fos.close();
				} catch (IOException e) {
					// tanghui Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }
    
 
 
    
    public static void main(String argv[]){
        String filePath = "d:\\1.txt";
        String filePath1 = "d:\\2.txt";
        String filePath2 = "d:\\2.txt";
        String filePath5 = "d:\\5.txt";
//      "res/";
        Map<String,ResMsg> map =  readTxtFile(filePath,"qfm",null);
        map =  readTxtFile(filePath1,"qyf",map);
        map =  readTxtFile(filePath2,"qcf",map);
        ResMsg resMsg = map.get("1 sa ");
        System.out.println(resMsg.toString());
        String msg = showMsg(map,false);
        createTxt(filePath5, msg);
        
        
        
    }
}
