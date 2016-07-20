package com.prp.detectionsystemclient;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import com.prp.detectionsystemclient.broadcastReceiver.WifiStateReceiver;

/**
 * Created by lwx on 2016/6/2.
 */
public class MyApplication extends Application {
    private Context context;
    public static MyApplication instance;
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        //监听wifi状态变化
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiStateReceiver wifiReceiver=new WifiStateReceiver(this, wifiManager);
        IntentFilter filter=new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        this.registerReceiver(wifiReceiver, filter);
    }
    public Context getContext(){
        if(context==null){
            context = getApplicationContext();
        }
        return context;
    }
}
