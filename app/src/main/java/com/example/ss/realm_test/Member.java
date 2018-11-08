package com.example.ss.realm_test;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Member extends RealmObject {

    @PrimaryKey
    private int id;
    @Required //필수항목
    private String minor;
    private String name;
    private double distance;
    private int txpower;
    private int rssi;
    private String state;
    private String photo;
    private int safeDistance = 20 ; //초기값

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMinor() { return minor; }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public double getDistance(){return distance;}

    public void setDistance(double distance){this.distance = distance; }

    public int getTxpower(){return txpower;}

    public void setTxpower(int txpower){this.txpower = txpower;}

    public int getRssi(){return rssi;}

    public void setRssi(int rssi){ this.rssi = rssi;}

    public String getState(){return state;}

    public void setState(String state){ this.state = state; }

    public String getphoto() {
        return photo;
    }

    public void setphoto(String profilePhoto) {
        this.photo = profilePhoto;
    }

    public int getSafeDistance() { return safeDistance; }

    public void setSafeDistance(int safeDistance){
        this.safeDistance=safeDistance;
    }

}