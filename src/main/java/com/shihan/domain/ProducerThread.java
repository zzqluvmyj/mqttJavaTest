package com.shihan.domain;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class ProducerThread extends Thread{
    private int producerNo;
    private int topicNum;
    private DefaultMQProducer producer;
    String nameSrvAddr;

    public ProducerThread(int producerNo,int topicNum,String nameSrvAddr){
        this.producerNo=producerNo;
        this.topicNum=topicNum;
        this.nameSrvAddr=nameSrvAddr;
    }

    @Override
        public void run() {
        this.producer = new DefaultMQProducer("producer_group"+producerNo);
        producer.setNamesrvAddr(nameSrvAddr);
        try{
            producer.start();
            for (int i = 0; i < topicNum; i++){
                Message msg = new Message("topic"+i,
                        "TagA",
                        "OrderID188",
                        ("producer "+producerNo+" to topic "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.send(msg);
                //System.out.printf("%s%n", sendResult);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        producer.shutdown();
    }
}
