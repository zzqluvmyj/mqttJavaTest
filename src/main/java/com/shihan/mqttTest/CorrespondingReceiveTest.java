package com.shihan.mqttTest;

import com.shihan.domain.PeerThread;

import java.util.HashMap;
import java.util.Map;

public class CorrespondingReceiveTest extends ClientTest{
    private Map<Integer, Boolean> arrivedNoMap = new HashMap<Integer, Boolean>();//
    private int[] topicNOs={0};//0,1,2,3,4....
    private int[] Qoss={1};//1,1,1,1....
    private int expectArriveMessageNum;

    public CorrespondingReceiveTest(String broker, int threadSize) {
        super(broker, threadSize, 1);
        this.expectArriveMessageNum = 1;
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < threadSize; i++) {
            arrivedNoMap.put(i, false);
            threads[i] = new PeerThread(i, broker, expectArriveMessageNum, 0, false);
            threads[i].start();
        }
    }

    @Override
    public void startTest() {
        super.startTest();
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().subscribe(topicNOs, Qoss);
            topicNOs[0]++;
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
        System.out.println("endTime:" + endTime);

    }
    public static void main(String[] args) {
        String broker = "tcp://127.0.0.1:1883";
        int threadSize = 500;
        CorrespondingReceiveTest correspondingReceiveTest = new CorrespondingReceiveTest(broker, threadSize);
        correspondingReceiveTest.init();
        correspondingReceiveTest.startTest();
    }
}
