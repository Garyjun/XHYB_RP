package com.brainsoon.common.util.dofile.util;

import java.io.InputStream;
import java.util.Properties;

import com.brainsoon.common.support.OperDbUtils;

/**
 * 
 * @ClassName: PropertiesReader 
 * @Description:  本类用提供一个线程同步的方法,读取资源文件中的配置信息
 * @author tanghui 
 * @date 2014-4-17 下午3:09:57 
 *
 */
public class PropertiesReader{
	
	//默认为：webapp.properties
	private static final String WEBAPP_PROPERTIES = "webapp.properties";
	private static PropertiesReader propertiesReader = null;
	private String file;
    private Properties properties;
    //配置从数据库读取还是读取Properties配置文件  0:读Properties配置文件  1：读取数据库
    private static final String READTYPE = "0";
    
    /**
     * 构造 PropertysReader
     * @param {String} path 相对于classes的文件路径
     */
    private PropertiesReader(String path){
        this.file = path;
        this.properties = new Properties();
    }
    
    
    /**
     * <p>
     * 本方法根据资源名获取资源内容
     * <p>
     * 
     * @param {String} key 资源文件内key
     * 
     * @reaurn String key对应的资源内容
     */
    public String getProperty(String key){
    	String value="";
        try{
        	//默认读取数据库
        	if(READTYPE.equals("0")){
        		InputStream in = this.getClass().getClassLoader().getResourceAsStream(this.file);
                properties.load(in);
                value = properties.getProperty(key);
        	}else{
        		value = OperDbUtils.queryParamValueByKey(key);
        	}
        }catch (Exception ex1){
            System.out.println("没有找到资源文件:" + this.file);
        }
        return value;
    }
    
    /**
     * <p>
     * 本方法根据资源名获取资源内容
     * <p>
     * 
     * @param {String} key 资源文件内key
     * @param {Stirng} defaultValue 默认值
     * 
     * @reaurn String key对应的资源内容
     */
    public synchronized String getProperty(String key, String defaultValue){
    	String value="";
        try{
        	//默认读取数据库
        	if(READTYPE.equals("0")){
	            InputStream in = this.getClass().getClassLoader().getResourceAsStream(this.file);
	            properties.load(in);
	            value = properties.getProperty(key, defaultValue);
        	}else{
        		value = OperDbUtils.queryParamValueByKey(key);
            }
        }catch (Exception ex1){
            System.out.println("没有找到资源文件:" + this.file);
        }
        return value;
    }
    
    /**
     * <p>
     * 本方法根据资源名获取资源内容
     * <p>
     * 
     * @param {String} key 资源文件内key
     * @param {Stirng} defaultValue 默认值
     * @param {boolean} isnull 如果配置文件value为空，是否使用默认值
     * 
     * @reaurn String key对应的资源内容
     */
    public synchronized String getProperty(String key, String defaultValue,boolean isnull){
        String value = null;
        //默认读取数据库
    	if(READTYPE.equals("0")){
	        value = getProperty(key,defaultValue);
	       
        }else{
    		value = OperDbUtils.queryParamValueByKey(key);
        }
    	 if(isnull && (value == null || "".equals(value.trim()))){
        	 value = defaultValue;
        }
        return value;
    }
    
    //初始化对象
    public static PropertiesReader getInstance(){
    	if (propertiesReader == null) {
    		propertiesReader = new PropertiesReader(WEBAPP_PROPERTIES);
        }
        return propertiesReader;
    }
    
    //test
    public static void main(String[] args){
        String rootLogger = getInstance().getProperty("ffmpegPath");
        System.out.println(rootLogger);
    }
}
