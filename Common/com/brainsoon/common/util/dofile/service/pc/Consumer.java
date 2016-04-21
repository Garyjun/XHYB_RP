package com.brainsoon.common.util.dofile.service.pc;


/**
 * 
 * @ClassName: Consumer 
 * @Description: Consumer：模拟消费者，可以有多个实例，每个具有一个名字，
 * 每个的消费任务量可以指定，然后有一个类静态变量标明总任务数。 
 * @author tanghui 
 * @date 2014-5-26 下午2:41:39 
 *
 */
public class Consumer implements Runnable{
	private static int totalTaskNumber=0;
	private int myTaskNumber=0;
	private static int totalTaskRemain=0;
	private int myTaskRemain=0;
	private String name;
	private Warehouse wh;
	private static int totalConsumed=0; //The total number of product produced yet.
	private int thisOneConsumed=0; //The number of product produced by this producer.
	private boolean needContinue=true;
	
	public Consumer(String name, Warehouse wh, int taskNumber){
		this.name=name;
		this.wh=wh;
		myTaskNumber=taskNumber;
		myTaskRemain=taskNumber;
		totalTaskNumber+=taskNumber;
		totalTaskRemain+=taskNumber;
	}
	
	public void consume(){
//		System.out.println("Im consumer here!");
		Product popedProduct=wh.pop(this);
		if(popedProduct==null) needContinue=false;
	}
	
	public void run(){
		for(int i=0;i<myTaskNumber&&needContinue;i++){
			consume();
		}
	}
	
	public String toString(){
		String str="";
		str+="Totally comsumed "+totalConsumed+" products. Consumer "+name+" consumed "+thisOneConsumed+" products";
		return str;
	}
	
	public String getName(){
		return name;
	}
	
	public void testFinish(){
		if(myTaskRemain==0){
			System.out.println("Consumer "+name+" finished his task!\n");
			
			if(totalTaskRemain==0){
				System.out.println("All consumers' task are finished!\n");
			}
		}
	}
	
	public int getTotalTaskRemain(){
		return totalTaskRemain;
	}
	
	public void decMyTaskRemain(){
		myTaskRemain--;
	}
	
	public void decTotalTaskRemain(){
		totalTaskRemain--;
	}
	
	public void incTotalConsumed(){
		totalConsumed++;
	}
	
	public void incThisOneConsumed(){
		thisOneConsumed++;
	}
	
}
