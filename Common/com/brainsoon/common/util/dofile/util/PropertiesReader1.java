package com.brainsoon.common.util.dofile.util;

import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @ClassName: PropertiesReader 
 * @Description:  本类用提供一个线程同步的方法,读取资源文件中的配置信息
 * @author tanghui 
 * @date 2014-4-17 下午3:09:57 
 *
 */
public class PropertiesReader1{
	
	//默认为：webapp.properties
	private static final String WEBAPP_PROPERTIES = "webapp.properties";
	private static PropertiesReader1 propertiesReader = null;
	private String file;
    private Properties properties;
    
    /**
     * 构造 PropertysReader
     * @param {String} path 相对于classes的文件路径
     */
    private PropertiesReader1(String path){
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
    public synchronized String getProperty(String key){
        try{
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(this.file);
            properties.load(in);
            
        }catch (Exception ex1){
            System.out.println("没有找到资源文件:" + this.file);
        }
        return properties.getProperty(key);
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
        try{
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(this.file);
            properties.load(in);
            
        }catch (Exception ex1){
            System.out.println("没有找到资源文件:" + this.file);
        }
        return properties.getProperty(key, defaultValue);
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
        value = getProperty(key,defaultValue);
        if(isnull && (value == null || "".equals(value.trim()))){
        	 value = defaultValue;
        }
        return value;
    }
    
    //初始化对象
    public static PropertiesReader1 getInstance(){
    	if (propertiesReader == null) {
    		propertiesReader = new PropertiesReader1(WEBAPP_PROPERTIES);
        }
        return propertiesReader;
    }
    
    //test
    public static void main(String[] args){
        String rootLogger = getInstance().getProperty("ffmpegPath");
        System.out.println(rootLogger);
    }
}
