package com.brainsoon.common.util.fltx.webpage.util;

import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * 本类使用JAVA API中的类库实现日志自定义配置，日志对象生成功能。
 * 
 * @作者 华龙
 * 
 * @时间 2009-10-15
 */
public class LogFactory {

	//是否采集日志配置
	private static boolean initialed=false;
	
	/**
	 * 初始化日志对象的配置
	 */
	private static void initial(String properties){
		//判断是否初始化日志配置
		if(initialed==false){
			try {
				//获取日志管理器
				LogManager lm=LogManager.getLogManager();
				if(properties!=null){
					//加载指定的配置文件
					lm.readConfiguration(new FileInputStream(properties));			
				}
				else{
					//加载默认的配置文件
					lm.readConfiguration(LogFactory.class.getResourceAsStream("logfactory.properties"));
				}
				initialed=true;//设置初始化成功
			} catch (Exception e) {
				initialed=false;//设置初始化失败
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取指定对象、指定日志配置文件路径的日志对象。
	 * @param object 指定的对象。
	 * @param properties 指定日志配置文件路径。
	 * @return 指定对象的日志对象。
	 */
	public static Logger getLogger(Object object,String properties){
		//初始化配置
		initial(properties);
		
		//获取指定对象的日志
		return Logger.getLogger(object.getClass().getName());
	}
	
	/**
	 * 获取指定的对象的，默认日志配置文件的日志对象。
	 * @param object 需要获取日志对象的对象。
	 * @return 获取到指定对象的日志对象。
	 */
	public static Logger getLogger(Object object){
		//加载默认的配置
		return getLogger(object,null);
	}
	
	public static void test() {
		Logger log = getLogger(LogFactory.class);
		log.warning("这是 warning");
		log.info("这是 info");
		log.config("这是 config");
		log.fine("这是 fine");
		log.finest("这是 finest");
	}
	
	public static void main(String[] args) {
		try{
			test();
			
			new Thread(new T()).start();
			new Thread(new T()).start();
			new Thread(new T()).start();
			
			Thread.sleep(1000*1);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class T implements Runnable{
	Logger logger=LogFactory.getLogger(this);
	
	public void run() {
	
		for (int i = 0; i < 10; i++) {
			try {
				logger.info("测试程序在运行 "+i);
				Thread.sleep(1000);
			} catch (Exception e) {
				
			}
		}
	}
}