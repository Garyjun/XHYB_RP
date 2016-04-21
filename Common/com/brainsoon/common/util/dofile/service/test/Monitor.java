package com.brainsoon.common.util.dofile.service.test;
//package com.brainsoon.common.util.dofile.conver;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
///**
// * 
// * @ClassName: Monitor 
// * @Description:监控线程  
// * @author tanghui 
// * @date 2014-5-23 上午9:47:26 
// *
// */
//public class Monitor extends Thread {
//
//    private List<Thread> monitoredThread; //要监控的线程名
//
//    public Monitor(List<Thread> monitoredThread) {
//        this.monitoredThread = monitoredThread;
//    }
//
//    public void run() {
//        while (true) {
//            monitor();
//            try {
//                TimeUnit.MILLISECONDS.sleep(2000);
//            } catch (InterruptedException e) {
//                // TODO 记日志
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    /**
//     * 监控主要逻辑
//     */
//    private void monitor() {
//        for (int i = 0; i < monitoredThread.size(); i++) {
////        	monitoredThread.get(i).get
//            Thread.State state = monitoredThread.get(i).getState(); // 获得指定线程状态
////            System.out.println(monitoredThread.get(i).getName() + "监控线程状态是:"
////                    + monitoredThread.get(i).getState());
//            /*
//             * 如果监控线程为终止状态,则重启监控线程
//             */
//            if (Thread.State.TERMINATED.equals(state)) {
//                System.out.println(monitoredThread.get(i).getName()
//                        + "监控线程已经终止,现在开始重启监控线程!");
//                monitoredThread.get(i).start();
//            }
//        }
//    }
//
//    /**
//     * 测试方法
//     */
//    public static void main(String[] args) {
//
//        Basic tm = new Basic("test");
//        tm.setCommands("tail -f XXXX.log");
//        tm.setHostname("IP");
//        tm.setPassword("XXXX");
//        tm.setUsername("XXXX");
//        tm.start();
//        Basic tm1 = new Basic("test1");
//        tm1.setCommands("tail -f XXXX.log");
//        tm1.setHostname("XXXX");
//        tm1.setPassword("XXXX^^");
//        tm1.setUsername("XXXX");
//        ConverUtils.threadPoolToFlv("D:/Project素材/video/1.avi", "D:/Project素材/video/1.flv");
//        tm1.start();
//        List<Thread> list = new ArrayList<Thread>();
//        list.add(tm);
//        list.add(tm1);
//        Monitor m = new Monitor(list);
//        m.start();
//
//    }
//}
//}
