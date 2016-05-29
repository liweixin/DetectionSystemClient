package com.example.wuzhenyu.detectionsystemclient;

/**
 * Created by lwx on 2016/5/29.
 */
public class Util {
    //获取用户设备的macAdress
    public static String getMacAdress(){
        return MyApplication.wifiInfo.getMacAddress();
    }
    //获取当前连接wifi的bssid
    public static String getBssid(){
        return MyApplication.wifiManager.getConnectionInfo().getBSSID();
    }
}
