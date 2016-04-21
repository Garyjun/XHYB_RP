package com.brainsoon.common.util.dofile.service.pc;

/**
 * 
 * @ClassName: Test_SingleP_MultiC 
 * @Description: 2.单生产者，多消费者。 
 * @author tanghui 
 * @date 2014-5-26 下午2:44:27 
 *
 */
public class Test_SingleP_MultiC {
	public static void main(String[] args){
		Warehouse wh=new Warehouse();
		Thread PT1=new Thread(new Producer("P_one",wh,50),"ProducerThread one");
		Thread CT1=new Thread(new Consumer("C_one",wh,1),"ConsumerrThread one");
		Thread CT2=new Thread(new Consumer("C_two",wh,1),"ConsumerrThread two");
		
		
//		PT1.setPriority(Thread.MIN_PRIORITY);
//		CT1.setPriority(Thread.MAX_PRIORITY);
		PT1.start();
		CT1.start();
		CT2.start();
	}
}
