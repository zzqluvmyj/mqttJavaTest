package com.shihan;

public class CalculateMaxTransformTime {

    public static void main(String[] args) {
        long startTime, endTime;
        startTime = 1578968222596L;//发送开始startTime
        endTime = 1578968223275L;//接收结束endTime
        System.out.println("传输时间:" + (endTime - startTime));
    }
}
