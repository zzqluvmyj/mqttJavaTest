package com.shihan.domain;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

public class ConsumerThread extends Thread {
    private int consumerNo;
    private DefaultMQPushConsumer consumer;
    private int topicNum;
    private boolean isEnd=false;
    int expectedMessageNum;
    int nowMessageNum=1;
    String nameSrvAddr;
    public boolean isEnd() {
        return isEnd;
    }
    @Override
    public void run() {
        this.consumer = new DefaultMQPushConsumer("consumer_group"+consumerNo);
        consumer.setNamesrvAddr(nameSrvAddr);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //set to broadcast mode
        consumer.setMessageModel(MessageModel.BROADCASTING);

        try{
            for(int i=0;i<topicNum;i++){
                consumer.subscribe("topic"+i, "TagA");
            }
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                                ConsumeConcurrentlyContext context) {
                    //System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");
                    //System.out.println("已接收");
                    //应该每个consumer，它都会使用多线程的方式去接收
                    if(nowMessageNum<expectedMessageNum){
                        nowMessageNum++;
                    }else{
                        isEnd=true;
                        System.out.println("consumer "+consumerNo+"已全部接收");
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("consumer"+consumerNo+"已就绪");
    }
    public ConsumerThread(int consumerNo,int topicNum,int expectedMessageNum,String nameSrvAddr){
        this.consumerNo=consumerNo;
        this.topicNum=topicNum;
        this.expectedMessageNum=expectedMessageNum;
        this.nameSrvAddr=nameSrvAddr;
    }
}
