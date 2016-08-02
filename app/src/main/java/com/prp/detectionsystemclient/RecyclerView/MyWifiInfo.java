package com.prp.detectionsystemclient.RecyclerView;

import java.util.Random;

/**
 * Created by lwx on 2016/6/3.
 */
public class MyWifiInfo {
    public static final int SAFE = 1;
    public static final int DANGEROUS = -1;
    public static final int UNKNOW = 0;
    String content;
    int signal;
    int security;
    String bssid;

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    public MyWifiInfo(String content, int signal, String bssid){
        this.content = content;
        this.signal = signal;
        this.bssid = bssid;
        Random random = new Random();
        security = random.nextInt(3) - 1;
    }
    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content = content;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
}
