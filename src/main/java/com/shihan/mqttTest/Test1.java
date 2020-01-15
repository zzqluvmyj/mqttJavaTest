//package com.shihan.mqttTest;
//
//import com.shihan.domain.Peer;
//import com.shihan.domain.PeerThread;
//
//import java.util.*;
//
///**
// *         本机测试：
// *         1-n-n
// *         O(n^2)
// *         1个发布端，n个主题，n个接收端，每个接收端订阅n个主题
// *         从生成消息开始计时
// *         在所有线程都接收到消息后终止计时
// */
//
//public class Test1 {
//    public static void main(String[] args) {
//        long maxTime = 0;
//        long endTime = 0;//总的结束时间
//        long startTime = 0;//总的开始时间
//        int size = 5;
//        int[] topicNOs = new int[size];//1,2,3,4....
//        int[] Qoss = new int[size];//1,1,1,1....
//        Map<Integer, Boolean> arrivedNoMap = new HashMap<Integer, Boolean>();//消息到达的map
//        Map<Integer, Boolean> connectedNoMap = new HashMap<Integer, Boolean>();//客户端连接的map
//        Peer peer;//temp
//
//        //Set<Integer> notArrivedNoSet = new HashSet<>();//set在迭代过程中无法修改
//        //Set<Integer> notConnectedNoSet = new HashSet<>();
//
//        //初始化
//        for (int i = 0; i < size; i++) {
//            topicNOs[i] = i;
//            Qoss[i] = 1;
//            arrivedNoMap.put(i, false);
//            connectedNoMap.put(i, false);
//        }
//        String broker = "tcp://127.0.0.1:1883";
//        Peer sender = new Peer(1000, broker, 0,size,true);
//        PeerThread[] threads = new PeerThread[size];
//
//        //创建线程
//        for (int i = 0; i < size; i++) {
//            threads[i] = new PeerThread(i, broker, 1,size*size,false);
//            threads[i].start();//连接到服务器
//        }
//
//        //此时thread的peer可能还没有创建,看一看
////        for (PeerThread p : threads) {
//////            System.out.println(p);
//////            System.out.println(p.getPeer());
//////        }
//
//        //判断是否所有peer已连接，防止出现nullpointer错误
//        while (connectedNoMap.containsValue(false)) {
//            for (int j = 0; j < size; j++)
//                if (!connectedNoMap.get(j)) {
//                    if (threads[j].getPeer() != null && threads[j].getPeer().isConnected()) {//此处如果直接调用，此时thread的peer可能还没有创建
//                        connectedNoMap.put(j, true);
//                    }
//                }
//        }
//        //此时所有线程已连接
//
//        //订阅
//        for (int i = 0; i < size; i++)
//            threads[i].getPeer().subscribe(topicNOs, Qoss);
//
//        startTime = System.currentTimeMillis();
//        //发布
//        for (int i = 0; i < size; i++) {
//            sender.generateMessage(i);
//            sender.publish(i);
//        }
//        //判断是否所有线程都接收完毕
//        while (arrivedNoMap.containsValue(false)) {
//            for (int j = 0; j < size; j++)
//                if (!arrivedNoMap.get(j)) {
//                    peer=threads[j].getPeer();
//                    if (peer.callBack.isArriveSuccessful()) {//此处如果直接调用，此时thread的peer可能还没有创建
//                        arrivedNoMap.put(j, true);
//                        //endTime=endTime>peer.callBack.getArriveEndTime()?endTime:peer.callBack.getArriveEndTime();
//                    }
//                }
//        }
//        maxTime = endTime - startTime;
//        System.out.println("maxTime:" + maxTime + "毫秒");
//    }
//}
//
//
