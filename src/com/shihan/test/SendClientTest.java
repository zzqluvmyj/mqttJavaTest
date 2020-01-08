package com.shihan.test;
import com.shihan.PeerThread;
import java.util.HashMap;
import java.util.Map;

/**
 * 可用于本机测试或多台机器测试多线程发布
 */

public class SendClientTest extends ClientTest {
    private Map<Integer, Boolean> publishedNoMap = new HashMap<>();//消息发布的map
    //broker名，线程数，每个线程要发布的topicSize
    public SendClientTest(String broker, int threadSize, int topicSize) {
        super(broker, threadSize, topicSize);
    }

    public void init() {
        super.init();
        for (int i = 0; i < threadSize; i++) {
            publishedNoMap.put(i, false);
            threads[i] = new PeerThread(i, broker, 0, topicSize,true);
            threads[i].start();
        }
    }

    public void startTest() {
        super.startTest();
        startTime = System.currentTimeMillis();
        for (int i = 0; i < threadSize; i++) {
            threads[i].getPeer().generateMessage();
            for (int j = 0; j < topicSize; j++) {
                threads[i].getPeer().publish(j);
            }
        }
        while (publishedNoMap.containsValue(false)) {
            for (int j = 0; j < threadSize; j++)
                if (!publishedNoMap.get(j)) {
                    if (threads[j].getPeer().callBack.isSendSuccessful()) {
                        publishedNoMap.put(j, true);
                    }
                }
        }
        endTime = System.currentTimeMillis();
        maxTime = endTime - startTime;
        System.out.println("测试结果:\n" +
                threadSize + "个发送客户端，每个客户端发布" + topicSize + "个主题，每个客户端向一个主题发送一个信息\n" +
                "共计发送时间：" + maxTime + " 毫秒");
        System.out.println("s1:"+startTime);
        System.out.println("e1:"+endTime);

    }

    public static void main(String[] args) {
        String broker = "tcp://10.0.3.219:1883";
        int threadSize = 1000;
        int topicSize = 1000;
        SendClientTest sendClientTest = new SendClientTest(broker, threadSize, topicSize);
        sendClientTest.init();
        sendClientTest.startTest();
    }
}
