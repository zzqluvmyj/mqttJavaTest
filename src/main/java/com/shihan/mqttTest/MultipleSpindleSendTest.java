package com.shihan.mqttTest;

import com.shihan.domain.PeerThread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;

public class MultipleSpindleSendTest extends ClientTest {
    public long[][] messagesStartTime;//消息开始发送时间
    public long[][] messagesEndTime;//消息发送结束时间
    private int topics;
    private Map<Integer, Boolean> publishedNoMap = new HashMap<>();//消息发布的map

    //broker名，线程数，每个线程要发布的topicSize
    public MultipleSpindleSendTest(String broker, int threadSize, int topics) {
        super(broker, threadSize, 1);//topicsize这里无关紧要,每个线程发送给一个topic
        this.topics = topics;
        messagesStartTime = new long[threadSize][topics];
        messagesEndTime = new long[threadSize][topics];
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < threadSize; i++) {
            publishedNoMap.put(i, false);
            threads[i] = new PeerThread(i, broker, 0, topics, true);
            threads[i].start();
        }
    }

    @Override
    public void startTest() {
        super.startTest();
        int num = 0;
        startTime = System.currentTimeMillis();//发送消息时计时
        for (int i = 0; i < threadSize; i++) {
            for (int j = 0; j < topics; j++) {
                threads[i].getPeer().generateMessage(j);//对应地生成消息,j为一个消息的消息序号
                messagesStartTime[i][j] = System.currentTimeMillis();
                threads[i].getPeer().publish(num++);//每个线程只有一个消息，发布一次就行
            }
        }
        while (publishedNoMap.containsValue(false)) {
            for (int j = 0; j < threadSize; j++)
                if (!publishedNoMap.get(j)) {
                    if (threads[j].getPeer().callBack.isSendSuccessful()) {
                        publishedNoMap.put(j, true);
                        messagesEndTime[j] = threads[j].getPeer().callBack.getMessagesEndTime();
                    }
                }
        }
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().disConnect();
        }
        System.out.println("已全部发布");
//        for(int i=0;i<messagesEndTime.length;i++){
//            System.out.println(Arrays.toString(messagesStartTime[i]));
//            System.out.println(Arrays.toString(messagesEndTime[i]));
//        }
        writeToFile(true);
        writeToFile(false);
    }

    public void writeToFile(boolean isStartTime) {
        File f;
        FileWriter w;
        BufferedWriter out;
        if (isStartTime) {
            f = new File("startTimes.txt");
        }else{
            f = new File("endTimes.txt");
        }
        try {
            f.createNewFile();
            w = new FileWriter(f);
            out = new BufferedWriter(w);
            for (int i = 0; i < threadSize; i++) {
                for (int j = 0; j < topics; j++) {
                    if (isStartTime)
                        out.write(messagesStartTime[i][j] + " ");
                    else
                        out.write(messagesEndTime[i][j] + " ");
                }
            }
            out.flush();
            out.close();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String broker = "tcp://127.0.0.1:1883";
        int threadSize = 5;
        int topics = 10;
        MultipleSpindleSendTest m = new MultipleSpindleSendTest(broker, threadSize, topics);
        m.init();
        m.startTest();
    }
}
