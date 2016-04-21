package com.brainsoon.common.util.dofile.service.pc;


/**
 * 
 * @ClassName: Warehouse 
 * @Description:  
 * Warehouse：模拟仓库，也就是所谓的多线程共享的数据缓冲区，大部分设计心血都在这个类里。结构是用堆栈实现的，最重要的操作是push，pop方法，前者只能由生产者的实例调用，后者只能由消费者的实例调用。其他还有一些isEmpty，isFull等等的辅助方法，详情可以参见我博客里关于堆栈实现的一篇日志。
现在讲一下Warehouse里的一些细节，是实现的关键。当仓库满的时候，isFull方法返回true，这个时候再想调用push方法的生产者，他们想push的产品已经构造好了，但是在推入仓库的这一步将被转为wait（），直到有消费者来notifyAll（）。同理，如果仓库满的时候，消费者再想pop（）的话也将转入等待，直到被刚刚完成生产工作的生产者唤醒。进一步的，每一次生产任务完成后都要notifyAll，通知在等待的消费者可以消费了，每一次消费任务完成后也要用notifyAll来通知生产者仓库不再爆仓，可以来推入产品了。
接下来谈一下任务不同步的问题。每一个生产者或者消费者在被创建实例的时候都可以指派生产或者消费任务，如果生产者的总任务和与消费者的总任务和不相等的话，就会造成一方的永久等待。为了解决这个问题，加入生产者和消费者的类静态变量表明总任务，每生产或消费一个，静态变量减一，归零的时候表明这一方的任务全部完成了。故每一方在转入wait（）方法的时候，先检查对方的这个变量，如果对方的这个变量是0，任务已经全部完成，那么你再等待也没意义了，只会陷入永久等待，因此这一方也就直接结束了。
如果是生产者先结束的话，那么消费者在将仓库里的产品消费光以后，即使还有消费任务也不管了，直接结束。
如果是消费者先结束的话，那么生产者其实会继续生产，将仓库里铺满爆仓，之后原本即将转入wait（）状态，但是即使转入了也没有消费者来消费了，那么也就直接结束啦！
 * @author tanghui 
 * @date 2014-5-26 下午2:43:17 
 *
 */
public class Warehouse {
	private final int capacity=10;
	private Product[] storage;
	private int nextPos=0;
	boolean allCsmFinished=false;
	boolean allPdcFinished=false;
	
	public Warehouse(){
		storage=new Product[10];
	}

	//This method is only invoked by producer
	public synchronized boolean push(Product toPush, Producer invoker){
		//The use  of while is very important. Using if() statement will cause exception in M_P_M_C model!.
		while(isFull()){ 
			try {
				if(allCsmFinished){
					System.out.println("ACF! "+invoker.getName()+" skips waiting!\n");
					return false;
				}
				
				System.out.println(invoker.getName()+" waits!\n");
				wait();	//The invoker of wait() method is acquiescently the owner of this synchronized lock.
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
		storage[nextPos]=toPush;
		nextPos++;
		
		System.out.println("Produced! ID "+toPush.getID()+" "+toString());
		
		invoker.incThisOneProduced();
		invoker.incTotalProduced();
		System.out.println(invoker.toString()+"\n");
		invoker.decMyTaskRemain();
		invoker.decTotalTaskRemain();
		invoker.testFinish();
		
		if(invoker.getTotalTaskRemain()==0) setAllPdcFinished(true);
//		System.out.println("Producer notifies all!\n");
		//It's safe to notifyAll() at this moment since nextPos is already changed.
		notifyAll();
		return true;
	}

	//This method is only invoked by consumer
	public synchronized Product pop(Consumer invoker){
		//The use  of while is very important. Using if() statement will cause exception in M_P_M_C model!.
		while(isEmpty()){
			try{
				if(allPdcFinished){
					System.out.println("APF! "+invoker.getName()+" skips waiting!\n");
					return null;
				}
				
				System.out.println(invoker.getName()+" waits!\n");
//				System.out.println(Test_SingleP_MultiC.CT1.isAlive());
//				System.out.println(Test_SingleP_MultiC.CT2.isAlive());
//				System.out.println(Test_SingleP_MultiC.PT1.isAlive());
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int idxToReturn=--nextPos;
		
		System.out.println("Consumed! ID "+storage[idxToReturn].getID()+" "+toString());
		
		invoker.incThisOneConsumed();
		invoker.incTotalConsumed();
		System.out.println(invoker.toString()+"\n");
		invoker.decMyTaskRemain();
		invoker.decTotalTaskRemain();
		invoker.testFinish();
		if(invoker.getTotalTaskRemain()==0) setAllCsmFinished(true);
		//NotifyAll() at this time is secure, because nextPos is changed, and return index is saved in another variabl and thus unchanged.
		notifyAll();
		return storage[idxToReturn];
	}
	
	public boolean isEmpty(){
		return nextPos==0;
	}
	
	public boolean isFull(){
		return nextPos==capacity;
	}
	
	public int inventory(){
		return nextPos;
	}
	
	public void setAllCsmFinished(boolean toSet){
		allCsmFinished=toSet;
	}
	
	public void setAllPdcFinished(boolean toSet){
		allPdcFinished=toSet;
	}
	
	public String toString(){
		String str="";
		str+="[Warehouse status: Inventory="+inventory()+" Products:";
		for(int i=0;i<nextPos;i++)
			str+=" "+storage[i].getID();
		str+=" ]";
		return str;
	}
}
