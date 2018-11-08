package com.example.ss.realm_test;

public class Distance {
    private double rssi;
    private double txpower;
    private static double N = 1.5;

    public Distance(int rssi, int txpower) {
        this.rssi = rssi;
        this.txpower = txpower;
    }

    public void Distance(double rssi, double txpower){
        this.rssi =rssi;
        this.txpower = txpower;
    }


    public double getDistance(){ //거리 계산~출력 함수
        return Math.pow(10,((txpower-rssi)/(10*N)));
    }
}