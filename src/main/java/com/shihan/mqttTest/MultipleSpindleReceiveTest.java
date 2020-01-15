package com.shihan.mqttTest;

import com.shihan.domain.PeerThread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class MultipleSpindleReceiveTest extends ClientTest {
    private Map<Integer, Boolean> arrivedNoMap = new HashMap<Integer, Boolean>();//
    private int[][] topicNOs;//[0,1,2],[3,4,5],...
    private int[] Qoss;//1,1,1,1....
    private int expectArriveMessageNum;
    private int topics;//每个线程要接收的主题数，不同线程接收完全不相同的主题
    public long[][] messagesEndTime;//消息发送结束时间

    public MultipleSpindleReceiveTest(String broker, int threadSize,int topics) {
        super(broker, threadSize, 1);
        this.expectArriveMessageNum = topics;
        this.topics=topics;//
        topicNOs=new int[threadSize][topics];
        Qoss=new int[topics];
        messagesEndTime=new long[threadSize][topics];
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
            threads[i].getPeer().subscribe("atopic",topicNOs[i], Qoss);
        }
        System.out.println("已全部订阅");
        while (arrivedNoMap.containsValue(false)) {
            for (int j = 0; j < threadSize; j++)
                if (!arrivedNoMap.get(j)) {
                    if (threads[j].getPeer().callBack.isArriveSuccessful()) {
                        arrivedNoMap.put(j, true);
                        messagesEndTime[j]=threads[j].getPeer().callBack.getArrivedMessageEndTime();
                    }
                }
        }
        endTime = System.currentTimeMillis();
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().disConnect();
        }
        File f;
        FileWriter w;
        BufferedWriter out;
            f = new File("receiveEndTimes.txt");
        try {
            f.createNewFile();
            w = new FileWriter(f);
            out = new BufferedWriter(w);
            for (int i = 0; i < threadSize; i++) {
                for (int j = 0; j < topics; j++) {
                    out.write(messagesEndTime[i][j] + " ");
                }
            }
            out.flush();
            out.close();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(endTime);
    }

    public static void main(String[] args) {
        String broker = "tcp://10.0.3.250:1883";
        //String broker = "tcp://127.0.0.1:1883";
        int threadSize = 100;
        int topics=100;
        MultipleSpindleReceiveTest m=new MultipleSpindleReceiveTest(broker,threadSize,topics);
        m.init();
        m.startTest();
    }
}
