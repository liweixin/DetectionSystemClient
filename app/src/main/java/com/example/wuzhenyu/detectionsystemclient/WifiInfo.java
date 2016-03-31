package com.example.wuzhenyu.detectionsystemclient;

/**
 * Created by lwx on 2015/11/30.
 */
public class WifiInfo {
    private String bssid, ssid, security, timeString;
    private int signal;
    public WifiInfo(String bssid, String ssid, String security, String timeString, int signal){
        this.bssid = bssid;
        this.ssid = ssid;
        this.security = security;
        this.signal = signal;
        this.timeString = timeString;
    }
    public String getBssid(){
        return bssid;
    }
    public String getSsid(){
        return ssid;
    }
    public String getSecurity(){
        return security;
    }
    public String getTimeString() { return timeString; }
    public int getSignal(){
        return signal;
    }
}
