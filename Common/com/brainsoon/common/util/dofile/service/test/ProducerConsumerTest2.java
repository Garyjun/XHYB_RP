package com.brainsoon.common.util.dofile.service.test;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 多个生产者、消费者问题与线程间的通信(用阻塞队列实现)
 * @author long
 *
 */
public class ProducerConsumerTest2 {

	public static void main(String[] args) {
	
		final BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(3);
				
		ExecutorService service = Executors.newFixedThreadPool(10);
		
		for(int i = 0;i<4;i++)
		{
			service.execute(new ProducerAndConsumer(blockingQueue));
		}
	}
}


class ProducerAndConsumer implements Runnable
{  
	private boolean flag = false;
	
	private Integer j = 1;
	
	private Lock lock = new ReentrantLock(); 
	
	Condition pro_con = lock.newCondition();
	
	Condition con_con = lock.newCondition();
	
	private BlockingQueue<Integer> blockingQueue;
	
	public ProducerAndConsumer(BlockingQueue<Integer> blockingQueue)
	{
		this.blockingQueue= blockingQueue;
	}
	
	
	//生产
	public void put()
	{
		try {
			lock.lock();
			while(flag)
			pro_con.await();
			System.out.println("正在准备放入数据。。。");
			Thread.sleep(new Random().nextInt(10)*100);
			Integer value = new Random().nextInt(30);
			blockingQueue.put(value);
			System.out.println(Thread.currentThread().getName()+"   放入的数据    "+value);
			flag = true;
			con_con.signal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	
	
	public void get()
	{
		 try {
			 lock.lock();
			while(!flag)
				con_con.await();
			System.out.println("正在准备取数据。。。");
			Thread.sleep(new Random().nextInt(10)*1000);
			System.out.println(Thread.currentThread().getName()+"   取到的数据为"+blockingQueue.take());
			     flag = false;
			     pro_con.signal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		 finally
		 {
			 lock.unlock();
		 }
	}
	
	@Override
	public void run() {
		
		while(true)
		{
			if(j==1)
			{
				put();
			}
			else
			{
				get();
			}
			j=(j+1)%2;
		}
	}
}
