package com.brainsoon.common.util.dofile.service.pc;

/**
 * 
 * @ClassName: Producer 
 * @Description:  Producer：模拟生产者，与消费者差不多。
 * @author tanghui 
 * @date 2014-5-26 下午2:42:14 
 *
 */
public class Producer implements Runnable{
	private static int totalTaskNumber=0;
	private int myTaskNumber=0;
	private static int totalTaskRemain=0;
	private int myTaskRemain=0;
	private String name;
	private Warehouse wh;
	private static int totalProduced=0; //The total number of product produced yet.
	private int thisOneProduced=0; //The number of product produced by this producer.
	private boolean needContinue=true;
	
	public Producer(String name, Warehouse wh, int taskNumber){
		this.name=name;
		this.wh=wh;
		myTaskNumber=taskNumber;
		myTaskRemain=taskNumber;
		totalTaskNumber+=myTaskNumber;
		totalTaskRemain+=myTaskNumber;
	}
	
	public void produce(){
		Product toProduce=new Product(++Product.totalID);
		needContinue=wh.push(toProduce,this);
	}
	
	public void run(){
		for(int i=0;i<myTaskNumber&&needContinue;i++){
			produce();
		}			
	}
	
	public String toString(){
		String str="";
		str+="Totally produced "+totalProduced+" products. Producer "+name+" produced "+thisOneProduced+" products";
		return str;
	}
	
	public String getName(){
		return name;
	}
	
	public void testFinish(){
		if(myTaskRemain==0){
			System.out.println("Producer "+name+" finished his task!\n");
			
			if(totalTaskRemain==0){
				System.out.println("All producers' taks are finished!\n");
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
	
	public void incTotalProduced(){
		totalProduced++;
	}
	
	public void incThisOneProduced(){
		thisOneProduced++;
	}
}

