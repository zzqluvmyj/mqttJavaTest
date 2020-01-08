package com.shihan.test;

import com.shihan.PeerThread;

import java.util.HashMap;
import java.util.Map;

/**
 * 可用于本机测试或多台机器测试多线程接收
 */
public class ReceiveClientTest extends ClientTest {
    private Map<Integer, Boolean> arrivedNoMap = new HashMap<>();//
    private int[] topicNOs;//1,2,3,4....
    private int[] Qoss;//1,1,1,1....
    private int expectArriveMessageNum;
    //
    public ReceiveClientTest(String broker, int threadSize, int topicSize,int expectArriveMessageNum){
        super(broker, threadSize,  topicSize);
        this.expectArriveMessageNum=expectArriveMessageNum;
    }
    public void init(){
        super.init();
        topicNOs = new int[topicSize];
        Qoss = new int[topicSize];
        for (int i = 0; i < topicSize; i++) {
            topicNOs[i] = i;
            Qoss[i] = 1;
        }
        for (int i = 0; i < threadSize; i++) {
            arrivedNoMap.put(i, false);
            threads[i] = new PeerThread(i, broker, expectArriveMessageNum, 0, false);
            threads[i].start();
        }
    }
    public void startTest() {
        super.startTest();
        startTime = System.currentTimeMillis();
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().subscribe(topicNOs,Qoss);
        }
        while (arrivedNoMap.containsValue(false)) {
            for (int j = 0; j < threadSize; j++)
                if (!arrivedNoMap.get(j)) {
                    if (threads[j].getPeer().callBack.isArriveSuccessful()) {
                        arrivedNoMap.put(j, true);
                    }
                }
        }
        endTime = System.currentTimeMillis();
        maxTime = endTime - startTime;
        System.out.println("测试结果:\n" +
                threadSize + "个接收客户端，每个客户端接收" + topicSize + "个主题，" +"每个客户端接收"+expectArriveMessageNum+"个消息\n"+
                "共计接收时间：" + maxTime + " 毫秒");
        System.out.println("s2:"+startTime);
        System.out.println("e2:"+endTime);

    }
    public static void main(String[] args) {
        String broker = "tcp://127.0.0.1:1883";
        int threadSize = 5;
        int topicSize = 5;
        ReceiveClientTest receiveClientTest=new ReceiveClientTest(broker,5,5,25);
        receiveClientTest.init();
        receiveClientTest.startTest();
    }
}
