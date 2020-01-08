package com.shihan.test;

public class CalculateMaxTranformTime {
    public static void main(String[] args) {
        long s1,s2,e1,e2;
        s1=1578466426320L;
        e1=1578466430896L;
        s2=1578466432761L;
        e2=1578466432956L;
        if(e1<s2){
            System.out.println("传输时间:"+(e1-s1+e2-s2));
        }else {
            System.out.println("传输时间:"+(e2-s1));
        }
    }
}
