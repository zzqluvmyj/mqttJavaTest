package com.shihan;

public class CalculateMaxTransformTime {

    public static void main(String[] args) {
        long startTime, endTime;
        startTime = 1578983193759L;//发送开始startTime
        endTime = 1578983245941L;//接收结束endTime
        System.out.println("传输时间:" + (endTime - startTime));
    }
}
