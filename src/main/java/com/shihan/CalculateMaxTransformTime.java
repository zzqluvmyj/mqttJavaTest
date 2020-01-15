package com.shihan;

public class CalculateMaxTransformTime {

    public static void main(String[] args) {
        long startTime, endTime;
        startTime = 1579078004028l;//发送开始startTime
        endTime = 1579078004195l;//接收结束endTime
        System.out.println("传输时间:" + (endTime - startTime));
        System.out.printf("\b");
    }
}
