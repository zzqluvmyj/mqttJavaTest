package com.shihan;

import org.eclipse.paho.client.mqttv3.*;

import java.util.HashMap;
import java.util.Map;

public class PushCallBack implements MqttCallback {
    private String peerName;
    private int expectedArriveMessageNum;//预期接收的信息数，为了判定信息是否全部接收完毕
    private int expectedPublishMessageNum;//预计发布的信息数，为了判定信息是否全部接收完毕
    private int nowArriveMessageNum = 1;
    private int nowPublishMessageNum=1;
    private long sendEndTime = 0;
    private long arriveEndTime = 0;
    private Map<String, Boolean> result = new HashMap<>();//因为要多次覆盖，用map更合适

    public PushCallBack(String peerName, int expectedArriveMessageNum,int expectedPublishMessageNum) {
        this.peerName = peerName;
        this.expectedArriveMessageNum = expectedArriveMessageNum;
        this.expectedPublishMessageNum=expectedPublishMessageNum;
        result.put("send", false);
        result.put("receive", false);
    }

    //消息是否发出成功
    public boolean isSendSuccessful() {
        return result.get("send");
    }

    //是否收到所有消息
    public boolean isArriveSuccessful() {
        return result.get("receive");
    }

    //连接断开
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println(peerName + "连接已断开");
    }

    //订阅后，消息接收到这里
    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        if (nowArriveMessageNum < expectedArriveMessageNum) {
            this.nowArriveMessageNum++;
        } else {
            this.result.put("receive", true);
            this.arriveEndTime = System.currentTimeMillis();
        }
        //IO操作耗时，不做了
        //System.out.println(peerName + "收到消息：" + "\n  topic:" + s + "\n  QoS:" + mqttMessage.getQos() + "\n  content:" + new String(mqttMessage.getPayload()));
    }

    //传输完成,也就是publish
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        if(nowPublishMessageNum<expectedPublishMessageNum){
            this.nowPublishMessageNum++;
        }else{
            this.result.put("send", true);//暂时无客户端发多个消息的要求，所以此处只要发出去一个，就通过
            this.sendEndTime = System.currentTimeMillis();//当前客户端发送完所有消息的时间
            System.out.println("send success "+peerName);
        }
        //IO操作耗时，不做了
        //System.out.println(peerName + "传输完成");
    }

    public long getSendEndTime() {
        return sendEndTime;
    }

    public long getArriveEndTime() {
        return arriveEndTime;
    }

//    @Override
//    public void connectComplete(boolean b, String s) {
//        System.out.println(String.format("connected ID ： %s", this.peerName));
//    }
}
