package com.brainsoon.common.util.dofile.service.pc;


/**
 * 
 * @ClassName: d 
 * @Description: 4.多生产者，多消费者。 
 * @author tanghui 
 * @date 2014-5-26 下午2:45:07 
 *
 */
public class Test_MultiP_MultiC {
	public static void main(String[] args){
		Warehouse wh=new Warehouse();
		Thread PT1=new Thread(new Producer("P_one",wh,20),"ProducerThread one");
		Thread PT2=new Thread(new Producer("P_two",wh,60),"ProducerThread two");
		Thread CT1=new Thread(new Consumer("C_one",wh,50),"ConsumerrThread one");		
		Thread CT2=new Thread(new Consumer("C_two",wh,30),"ConsumerrThread two");
//		PT1.setPriority(Thread.MIN_PRIORITY);
//		CT1.setPriority(Thread.MAX_PRIORITY);
		PT1.start();
		PT2.start();
		CT1.start();
		CT2.start();
	}
}

