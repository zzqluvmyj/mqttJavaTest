package com.shihan.mqttTest;

import com.shihan.domain.PeerThread;

import java.util.HashMap;
import java.util.Map;

public class CorrespondingSendTest extends ClientTest {
    private Map<Integer, Boolean> publishedNoMap = new HashMap<>();//消息发布的map
    //broker名，线程数，每个线程要发布的topicSize
    public CorrespondingSendTest(String broker, int threadSize) {
        super(broker, threadSize, 1);//topicsize这里无关紧要
    }
    @Override
    public void init() {
        super.init();
        for (int i = 0; i < threadSize; i++) {
            publishedNoMap.put(i, false);
            threads[i] = new PeerThread(i, broker, 0, topicSize,true);
            threads[i].start();
        }
    }

    @Override
    public void startTest() {
        super.startTest();
        for (int i = 0; i < threadSize; i++) {
                threads[i].getPeer().generateMessage(i);//对应地生成消息
        }
        startTime = System.currentTimeMillis();//发送消息时计时
        for (int i = 0; i < threadSize; i++) {
                threads[i].getPeer().publish(i);//每个线程只有一个消息，发布一次就行
        }
        while (publishedNoMap.containsValue(false)) {
            for (int j = 0; j < threadSize; j++)
                if (!publishedNoMap.get(j)) {
                    if (threads[j].getPeer().callBack.isSendSuccessful()) {
                        publishedNoMap.put(j, true);
                    }
                }
        }
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().disConnect();
        }
        System.out.println("已全部发布");
        System.out.println("startTime:"+startTime);
    }
    public static void main(String[] args) {
        String broker ="tcp://10.0.3.250:1883";
        int threadSize = 10000;
        CorrespondingSendTest correspondingSendTest = new CorrespondingSendTest(broker, threadSize);
        correspondingSendTest.init();
        correspondingSendTest.startTest();
    }
}
