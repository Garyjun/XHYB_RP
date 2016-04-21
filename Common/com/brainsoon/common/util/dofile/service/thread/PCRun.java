package com.brainsoon.common.util.dofile.service.thread;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.PropertiesReader;


/**
 * 
 * @ClassName: PCRun 
 * @Description:  
 * @author tanghui 
 * @date 2014-10-25 下午12:06:34 
 *
 */
public class PCRun extends Thread{

	private static PCRun pcrun = new PCRun();
	//生产消费缓存session key
	public static final String PCRunKey = "PCRunKey";
	//生产者key
	public static final String ProducersKey = "ProducersKey";
	//消费key
	public static final String ConsumersKey = "ConsumersKey";
	//生产者循环次数，针对无法取到记录的情况使用
	public static final int PCycleNum = 5;
	private static int converTheadNumber = 1; //如果为空，默认为1
	static {
		String converTheadNum = PropertiesReader.getInstance().getProperty(ConstantsDef.converTheadNumber);
		if(StringUtils.isNotBlank(converTheadNum)){
			converTheadNumber = Integer.parseInt(converTheadNum);
		}
	}
	
	//初始化对象
    public static PCRun getInstance(){
        return pcrun;
    }
    
	public void run(){
		
		try {
			//根据缓存的key判断是否已经启动过线程了,如果启动过就不再启动
			if(!GlobalAppCacheMap.containsKey(PCRunKey)){ //true or  false
				//没有缓存key，则放入缓存key
				GlobalAppCacheMap.putKey(PCRunKey, PCRunKey);
				// 生产&消费总控制类
				ConverFilePC pc = new ConverFilePC();
				// 生产者
				ConverFileP p = new ConverFileP(pc);
				// 初始化生存者线程
				Thread tp = new Thread(p);
				tp.setName(ProducersKey);
				// 启动生存者线程
				tp.start();
				Thread.sleep(1000);  
				for (int i = 0; i < converTheadNumber; i++) {
					Thread tc = new Thread(new ConverFileC(pc));
					tc.setName(ConsumersKey + i);
					tc.start();
					// 消费者
//					Object obj = GlobalAppCacheMap.getValue(ConsumersKey + i);
//					if(obj == null){
//						// 初始化消费者线程1-n
//						Thread tc = new Thread(new ConverFileC(pc));
//						tc.setName(ConsumersKey + i);
////						GlobalAppCacheMap.putKey(ConsumersKey + i, ConsumersKey + i);
////						System.out.println("---------qidongba!!!!!--------");
//						// 启动消费者线程1-n
//						tc.start();
//					}else{
//						System.out.println("---------noqidongba!!!!!--------");
//					}
				}
			}
		} catch (Exception e) {
		}finally{
			
		}
	}
}
