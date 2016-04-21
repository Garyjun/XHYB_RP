package com.brainsoon.common.util.dofile.test;

import java.util.LinkedList;

public class WorkQueue {
    private final int nThreads;
    private final PoolWorker[] threads;
    private final LinkedList queue;
    public WorkQueue(int nThreads) {
        this.nThreads = nThreads;
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];
 
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new PoolWorker();
            System.out.println("theadnAME:" + threads[i].getName());
            threads[i].start();
        }
    }
    public void execute(Runnable r) {
        synchronized (queue) {
            queue.addLast(r);
            queue.notify();
        }
    }
 
    private class PoolWorker extends Thread {
        public void run() {
            Runnable r;
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
 
                    r = (Runnable) queue.removeFirst();
                }
 
                // If we don't catch RuntimeException,
                // the pool could leak threads
                try {
                    r.run();
                } catch (RuntimeException e) {
                    // You might want to log something here
                }
            }
        }
    }
    public static void main(String args[]) {
        WorkQueue wq = new WorkQueue(3);
        wq.execute(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {}
                    System.out.println("this is a thread" + i);
                }
            }
        });
    }
} 
