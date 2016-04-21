package com.brainsoon.common.util.dofile.service.pc;


/**
 * 
 * @ClassName: Test_SingleP_SingleC 
 * @Description:1.单生产者，单消费者。  
 * @author tanghui 
 * @date 2014-5-26 下午2:44:02 
 *
 */
public class Test_SingleP_SingleC {
	public static void main(String[] args){
		Warehouse wh=new Warehouse();
		Thread PT1=new Thread(new Producer("P_one",wh,50),"ProducerThread one");
		Thread CT1=new Thread(new Consumer("C_one",wh,50),"ConsumerrThread one");
//		PT1.setPriority(Thread.MIN_PRIORITY);
//		CT1.setPriority(Thread.MAX_PRIORITY);
		PT1.start();
		CT1.start();		
	}
}

