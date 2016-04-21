package com.brainsoon.common.util.dofile.service.test;

public class MyDataGenerator extends SchedThread {
    protected void executeWork() {
    	
        System.out.println("Execute work ...");
    }

    protected long getNextTime() {
        return System.currentTimeMillis() + 2000L;
    }

    public static void main(String argv[]) {
        MyDataGenerator generator = new MyDataGenerator();
        generator.start();
    }
}