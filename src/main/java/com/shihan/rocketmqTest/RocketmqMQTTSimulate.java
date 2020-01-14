package com.shihan.rocketmqTest;

import com.shihan.domain.ConsumerThread;
import com.shihan.domain.ProducerThread;

import java.util.HashMap;
import java.util.Map;

/**
 * 以BroadConsumer和BroadProducer为基础
 */
public class RocketmqMQTTSimulate {
    private long startTime;//开始发送的时间
    private long endTime;//接收完毕的时间，需要判断是否所有接收线程完毕
    int consumerNum;
    int topicNum;
    int producerNum;
    int expectedMessageNum;
    String nameSrvAddr;
    Map<Integer, Boolean> consumerNoMap = new HashMap<Integer, Boolean>();//判断是否接收完毕
    //Map<Integer, Boolean> producerNoMap = new HashMap<Integer, Boolean>();//判断是否发送完毕，此时并不重要，不要也行
    public void startProducer(){
        ProducerThread [] producerThreads=new ProducerThread[producerNum];
        for(int i=0;i<producerNum;i++){
            producerThreads[i]=new ProducerThread(i,topicNum,nameSrvAddr);
        }
        startTime=System.currentTimeMillis();
        for(int i=0;i<producerNum;i++){
            producerThreads[i].start();
        }
        System.out.println("startTime:"+startTime);
    }
    public void startConsumer(){
        ConsumerThread [] consumerThreads=new ConsumerThread[consumerNum];
        for(int i=0;i<consumerNum;i++){
            consumerThreads[i]=new ConsumerThread(i,topicNum,expectedMessageNum,nameSrvAddr);
            consumerThreads[i].start();
        }
        //判定是否所有线程都已经接收
        while (consumerNoMap.containsValue(false)){
            for(int i=0;i<consumerNum;i++){
                if(!consumerNoMap.get(i)){
                    if(consumerThreads[i].isEnd()){
                        consumerNoMap.put(i,true);
                    }
                }
            }
        }
        endTime=System.currentTimeMillis();
        System.out.println("endTime:"+endTime);
    }

    public RocketmqMQTTSimulate(int consumerNum,int topicNum, int producerNum,String nameSrvAddr){
        this.consumerNum=consumerNum;
        this.topicNum=topicNum;
        this.producerNum=producerNum;
        this.expectedMessageNum=topicNum*producerNum;
        this.nameSrvAddr=nameSrvAddr;
        for(int i=0;i<consumerNum;i++){
            consumerNoMap.put(i,false);
        }
    }
    public static void main(String[] args) {
        int consumerNum=10;
        int topicNum=10;
        int producerNum=10;
        String nameSrvAddr="10.0.3.250:9876";
        RocketmqMQTTSimulate simulate=new RocketmqMQTTSimulate(consumerNum,producerNum,topicNum,nameSrvAddr);
        simulate.startConsumer();
        //simulate.startProducer();
    }
}
