package com.shihan.domain;

public class PeerThread extends Thread {
    private int clientNo;
    private Peer peer;
    private  String broker;
    private int expectedArriveMessageNum;
    private int expectedPublishMessageNum;
    private boolean isSendPeer;
    @Override
    public void run() {
//        super.run();
        peer=new Peer(clientNo,broker,expectedArriveMessageNum,expectedPublishMessageNum,isSendPeer);
        //System.out.println("线程"+clientNo+"已启动,Peer()已创建");
    }
    public PeerThread(int clientNo,String broker,int expectedArriveMessageNum,int expectedPublishMessageNum,boolean isSendPeer){
        this.clientNo=clientNo;
        this.broker=broker;
        this.expectedArriveMessageNum=expectedArriveMessageNum;
        this.expectedPublishMessageNum= expectedPublishMessageNum;
        this.isSendPeer=isSendPeer;
    }
    public Peer getPeer(){
        return peer;
    }

    @Override
    public String toString() {
        return "PeerThread{" +
                "clientNo=" + clientNo +
                '}';
    }
}
