package com.shihan.mqttTest;

import com.shihan.domain.PeerThread;

import java.util.HashMap;
import java.util.Map;

public class MultipleSpindleReceiveTest extends ClientTest {
    private Map<Integer, Boolean> arrivedNoMap = new HashMap<Integer, Boolean>();//
    private int[][] topicNOs;//[0,1,2],[3,4,5],...
    private int[] Qoss;//1,1,1,1....
    private int expectArriveMessageNum;
    private int topics;//每个线程要接收的主题数，不同线程接收完全不相同的主题

    public MultipleSpindleReceiveTest(String broker, int threadSize,int topics) {
        super(broker, threadSize, 1);
        this.expectArriveMessageNum = topics;
        this.topics=topics;//
        topicNOs=new int[threadSize][topics];
        Qoss=new int[topics];
    }

    @Override
    public void init() {
        super.init();
        for(int i=0;i<topics;i++){
            Qoss[i]=1;
        }
        for (int i = 0; i < threadSize; i++) {
            arrivedNoMap.put(i, false);
            threads[i] = new PeerThread(i, broker, expectArriveMessageNum, 0, false);
            threads[i].start();
        }
        int num=0;
        for(int i=0;i<threadSize;i++){
            for(int j=0;j<topics;j++){
                topicNOs[i][j]=num++;
            }
        }
    }

    @Override
    public void startTest() {
        super.startTest();
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().subscribe(topicNOs[i], Qoss);
        }
        System.out.println("已全部订阅");
        while (arrivedNoMap.containsValue(false)) {
            for (int j = 0; j < threadSize; j++)
                if (!arrivedNoMap.get(j)) {
                    if (threads[j].getPeer().callBack.isArriveSuccessful()) {
                        arrivedNoMap.put(j, true);
                    }
                }
        }
        endTime = System.currentTimeMillis();
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().disConnect();
        }
        System.out.println("endTime:" + endTime);
    }

    public static void main(String[] args) {
        String broker = "tcp://127.0.0.1:1883";
        int threadSize = 5;
        int topics=5;
        MultipleSpindleReceiveTest m=new MultipleSpindleReceiveTest(broker,threadSize,topics);
        m.init();
        m.startTest();
    }
}
