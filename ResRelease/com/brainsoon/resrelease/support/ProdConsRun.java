package com.brainsoon.resrelease.support;

import org.apache.commons.lang.StringUtils;

import com.brainsoon.common.support.GlobalAppCacheMap;
import com.brainsoon.common.util.dofile.util.ConstantsDef;
import com.brainsoon.common.util.dofile.util.PropertiesReader;

/**
 * @ClassName: ProdConsRun
 * @author xiehewei
 * @date 2014年9月1日 下午3:48:16
 *
 */
public class ProdConsRun extends Thread {

	private static ProdConsRun pcrun = new ProdConsRun();
	//用户缓存session key
	public static final String PCKey = "PCKey";
	//生产者key
	public static final String PKey = "ProKey";
	//消费key
	public static final String CKey = "ConKey";
	//生产者循环次数，针对无法取到记录的情况使用
	public static final int PCycleNum = 100;
	private static int converTheadNumber = 1; //如果为空，默认为1
	static {
		String converTheadNum = PropertiesReader.getInstance().getProperty(ConstantsDef.converTheadNumber);
		if(StringUtils.isNotBlank(converTheadNum)){
			converTheadNumber = Integer.parseInt(converTheadNum);
		}
	}
	private Long releaseId;
	private Long orderId;
	//初始化对象
    public static ProdConsRun getInstance(){
        return pcrun;
    }
    
	public void run(){
		try {
			//判断是否已经启动过线程了
			if(!GlobalAppCacheMap.containsKey(PCKey)){ //true or  false
				//缓存key
				GlobalAppCacheMap.putKey(PCKey, PCKey);
				// 生产&消费总控制类
				ProcessFilePC pc = new ProcessFilePC();
				// 生产者
				ProcessFileP p = new ProcessFileP(pc);
				p.setReleaseId(releaseId);
				// 初始化生产者线程
				Thread tp = new Thread(p);
				tp.setName(PKey);
				// 启动生产者线程
				tp.start();
				Thread.sleep(1000);  
				for (int i = 0; i < converTheadNumber; i++) {
					ProcessFileC c = new ProcessFileC(pc);
					c.setOrderId(orderId);
					Thread tc = new Thread(c);
					tc.setName(CKey + i);
					tc.start();
				}
			}
		} catch (Exception e) {
		}finally{
			
		}
	}

	public Long getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
}
