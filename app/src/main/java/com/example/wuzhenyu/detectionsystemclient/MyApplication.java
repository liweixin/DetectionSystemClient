package com.example.wuzhenyu.detectionsystemclient;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.*;
import android.net.wifi.WifiInfo;
import android.util.Log;

/**
 * Created by lwx on 2016/4/6.
 */
public class MyApplication extends Application {
    public static WifiManager wifiManager;
    public static WifiInfo wifiInfo;
    @Override
    public void onCreate(){
        super.onCreate();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        //监听wifi状态变化
        WifiStateReceiver wifiReceiver=new WifiStateReceiver(this);
        IntentFilter filter=new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(wifiReceiver, filter);
        Log.e("MyApplication", "onCreate");
    }
}
