package com.shihan.domain;

import org.eclipse.paho.client.mqttv3.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PushCallBack implements MqttCallback {
    private String peerName;
    private int expectedArriveMessageNum;//预期接收的信息数，为了判定信息是否全部接收完毕
    private int expectedPublishMessageNum;//预计发布的信息数，为了判定信息是否全部接收完毕
    private int nowArriveMessageNum = 1;
    private int nowPublishMessageNum=1;
    private Map<String, Boolean> result = new HashMap<String, Boolean>();//因为要多次覆盖，用map更合适
    String message;
    int messsageNo;
    long temp;
    private long[] messagesEndTime;
    private long[] arrivedMessageEndTime;
    public long[] getArrivedMessageEndTime() {
        return arrivedMessageEndTime;
    }
    public long[] getMessagesEndTime() {
        return messagesEndTime;
    }
    public PushCallBack(String peerName, int expectedArriveMessageNum,int expectedPublishMessageNum) {
        this.peerName = peerName;
        this.expectedArriveMessageNum = expectedArriveMessageNum;
        this.expectedPublishMessageNum=expectedPublishMessageNum;
        result.put("send", false);
        result.put("receive", false);
        messagesEndTime=new long[expectedPublishMessageNum];
        arrivedMessageEndTime=new long[expectedArriveMessageNum];
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

    public void connectionLost(Throwable throwable) {
        System.out.println(peerName + "连接已断开");
    }

    //订阅后，消息接收到这里

    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        //arrivedMessageEndTime
        try{
            temp=System.currentTimeMillis();
            messsageNo=Integer.parseInt(new String(mqttMessage.getPayload()));
            System.out.println(messsageNo);
            arrivedMessageEndTime[messsageNo]=temp;
        }catch (Exception e){
            e.printStackTrace();
        }
        if(this.nowArriveMessageNum < this.expectedArriveMessageNum) {
            this.nowArriveMessageNum++;
            //System.out.println(s);
        } else {
            this.result.put("receive", true);
            //System.out.println(peerName+"success");
        }
        //System.out.println(new String(mqttMessage.getPayload()));
        //IO操作耗时，不做了
        //System.out.println(peerName + "收到消息：" + "\n  topic:" + s + "\n  QoS:" + mqttMessage.getQos() + "\n  content:" + new String(mqttMessage.getPayload()));
    }

    //传输完成,也就是publish

    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        try{
            temp=System.currentTimeMillis();
            messsageNo=iMqttDeliveryToken.getMessageId()-1;
            messagesEndTime[messsageNo]=temp;
        }catch (Exception e){
            e.printStackTrace();
        }
        if(nowPublishMessageNum<expectedPublishMessageNum){
            this.nowPublishMessageNum++;
        }else{
            this.result.put("send", true);//暂时无客户端发多个消息的要求，所以此处只要发出去一个，就通过
            //System.out.println("send success "+peerName);
            //System.out.println("h: "+Arrays.toString(messagesEndTime));
        }
        //IO操作耗时，不做了
        //System.out.println(peerName + "传输完成");
    }


//    @Override
//    public void connectComplete(boolean b, String s) {
//        System.out.println(String.format("connected ID ： %s", this.peerName));
//    }
}
