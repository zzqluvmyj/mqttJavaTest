package com.shihan.domain;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Peer {
    int clientNo = 0;//客户端序号
    int Qos=1;//默认Qos
    String clientId;//客户端名
    String topicId ;//主题名
    String broker = "tcp://127.0.0.1:1883";//默认，本地服务器地址
//    String broker;
    MqttClient client;
    MqttTopic topic;
    MqttConnectOptions connOpts;
    MqttMessage message;
    MemoryPersistence persistence = new MemoryPersistence();
    public PushCallBack callBack;//保存时间和回调
    boolean isConnected=false;

    public boolean isConnected() {
        return isConnected;
    }

    //得到发布时间前需要先判断是否发布了
    public Peer(int clientNo,String broker,int expectedArriveMessageNum,int expectedPublishMessageNum,boolean isSendPeer) {
        this.broker=broker;
        this.clientNo = clientNo;
        this.clientId=isSendPeer?"sendClient" + clientNo:"receiveClient"+clientNo;
        try{
            client = new MqttClient(broker, clientId, persistence);
            this.callBack=new PushCallBack(clientId,expectedArriveMessageNum,expectedPublishMessageNum);
        }catch (MqttException e){
            e.printStackTrace();
        }
        connect();//创建后自动连接，不要调用connect在外面
        //System.out.println("connected,"+clientId);
    }
//    public Peer(int clientNo,int expectedArriveMessageNum){
//        this.clientId="client"+clientNo;
//        this.callBack=new PushCallBack(clientId,expectedArriveMessageNum);
//        try{
//            client=new MqttClient(broker,clientId,persistence);
//            connect();
//        }catch (MqttException e){
//            e.printStackTrace();
//        }
//        connect();
//    }

    public void connect() {//同步连接
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName("user");
        connOpts.setPassword("user".toCharArray());
        connOpts.setConnectionTimeout(100);
        connOpts.setKeepAliveInterval(60);
        try {

            client.setCallback(callBack);
            client.connect(connOpts);//这个函数自带waitForCompletion
            isConnected=true;
            //System.out.println("hello,"+clientId+",you connected");
        } catch (Exception e) {
            e.printStackTrace();
            //此处多线程的时候有错
            //猜测我是用MqttClient阻塞方法，多线程做的测试，这个和MqttAsyncClient可能有冲突
        }
    }

    public void generateMessage(int messageNo){
        message=new MqttMessage();
        message.setQos(this.Qos);
        message.setRetained(false);
        message.setPayload((""+messageNo).getBytes());
        message.setId(messageNo);
    }
    public void generateMessage(){
        message=new MqttMessage();
        message.setQos(this.Qos);
        message.setRetained(false);
        message.setPayload(("nothing").getBytes());
    }


    public void publish(int topicNo) {//发布消息
        this.topicId="topic"+topicNo;
        topic=client.getTopic(topicId);
        MqttDeliveryToken token=new MqttDeliveryToken();
        try{
            token= topic.publish(message);//消息发布前返回token，发布后返回null
            token.waitForCompletion();//阻止当前线程，直到发布完成
            //System.out.println(this.clientId+"->"+topicId);
            //System.out.println( token.getMessage().getPayload());

        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void subscribe(int[] topicNos,int[] QoSs) {//订阅
        if(topicNos.length!=QoSs.length){
            System.out.println("订阅的topicNo和qOS数量不一致");
            return ;
        }
        int n=topicNos.length;
        String []topicIDs=new String[n];
        for(int i=0;i<n;i++){
            topicIDs[i]="topic"+topicNos[i];
        }
        try{
            client.subscribe(topicIDs,QoSs);
            //System.out.println(clientId+"订阅");
            //for(String s:topicIDs)
            //    System.out.println(s);
        }catch (MqttException e){
            e.printStackTrace();
        }

    }
    public void disConnect(){
        try{
            client.disconnect();
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        int [] topicNOs={0,1,2};
//        int [] QoSs={1,1,2};
//        Peer peer=new Peer(0,"tcp://10.0.3.250:1883");
//        peer.connect();
//        peer.subscribe(topicNOs,QoSs);

        //连接测试
//        Peer peer1=new Peer(1);
//        Peer peer2=new Peer(2);
//
//        //订阅测试
//        peer2.subscribe(new int[]{1},new int[]{1});
//
//        //发布测试
//        peer1.generateMessage();
//        peer1.publish(1);

        //500连接测试
//        int size=1000;
//        PeerThread [] threads=new PeerThread[size];
//        String broker="tcp://127.0.0.1:1883";
//        for(int i=0;i<size;i++){
//            threads[i]=new PeerThread(i,broker,1,1,true);
//            //System.out.println("创建到"+i);
//            threads[i].start();//连接到服务器
//        }
        //artimes连接测试
        String broker="tcp://127.0.0.1:1883";
        Peer peer=new Peer(0,broker,0,0,true);

    }

    @Override
    public String toString() {
        return "Peer{" +
                "clientNo=" + clientNo +
                '}';
    }
}
