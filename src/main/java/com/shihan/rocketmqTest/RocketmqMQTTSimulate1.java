package com.shihan.rocketmqTest;

import com.shihan.domain.ConsumerThread;
import com.shihan.domain.ProducerThread;

import java.util.HashMap;
import java.util.Map;

/**
 * 以BroadConsumer和BroadProducer为基础
 */
public class RocketmqMQTTSimulate1 {
    public static void main(String[] args) {
        int consumerNum=5;
        int topicNum=5;
        int producerNum=5;
        String nameSrvAddr="10.0.3.250:9876";
        RocketmqMQTTSimulate simulate=new RocketmqMQTTSimulate(consumerNum,producerNum,topicNum,nameSrvAddr);
        //simulate.startConsumer();
        simulate.startProducer();
    }
}
