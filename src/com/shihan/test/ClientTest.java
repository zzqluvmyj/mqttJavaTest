package com.shihan.test;

import com.shihan.PeerThread;

import java.util.HashMap;
import java.util.Map;

public class ClientTest {
    protected String broker;
    protected int threadSize;
    protected int topicSize;
    protected long maxTime = 0;
    protected long endTime = 0;//总的结束时间
    protected long startTime = 0;//总的开始时间
    protected Map<Integer, Boolean> connectedNoMap = new HashMap<>();//客户端连接的map
    protected PeerThread[] threads;

    public ClientTest(String broker, int threadSize, int topicSize){
        this.broker = broker;
        this.threadSize = threadSize;
        this.topicSize = topicSize;
    }
    public void init() {
        for(int i=0;i<threadSize;i++){
            connectedNoMap.put(i, false);
        }
        threads = new PeerThread[threadSize];
    }

    public void startTest(){//连接测试
        while (connectedNoMap.containsValue(false)) {
            for (int j = 0; j < threadSize; j++)
                if (!connectedNoMap.get(j)) {
                    if (threads[j].getPeer() != null && threads[j].getPeer().isConnected()) {//此处如果直接调用，此时thread的peer可能还没有创建
                        connectedNoMap.put(j, true);
                    }
                }
        }
    }
}
