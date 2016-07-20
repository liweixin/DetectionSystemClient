package com.prp.detectionsystemclient.broadcastReceiver;

/**
 * Created by liweixin on 2016/7/20.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.prp.detectionsystemclient.service.SendAPAcessRecordService;

import java.util.Date;

public class WifiStateReceiver extends BroadcastReceiver {
    Context context;
    String bssid;
    String macAdress;
    long startTime, endTime;
    boolean init;
    WifiManager wifiManager;
    WifiInfo wifiInfo;

    public WifiStateReceiver(Context context, WifiManager wifiManager) {
        this.context=context;
        this.wifiManager = wifiManager;
        this.wifiInfo = wifiManager.getConnectionInfo();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if(SecondActivity.wifiInfo!=null){
            macAdress = SecondActivity.wifiInfo.getMacAddress();
        }*/
        Log.d("wifiStateReceiver", "onReceive");
        if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION))
        {
            //信号变化
        }else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            Log.e("wifistate", "网络状态改变");
            NetworkInfo info=intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            Log.e("state", info.getState().toString());
            if(info.getState().equals(NetworkInfo.State.DISCONNECTED))
            {//如果断开连接
                endTime = new Date().getTime();
                init = true;
            } else if(info.getState().equals(NetworkInfo.State.CONNECTED)){
                //traceroute
                /*List<String> results = new TraceRouteActivity().exe("./data/data/com.example.wuzhenyu.detectionsystemclient/files/traceroute 202.120.36.190");
                String result = "";
                for(String line : results){
                    result += line + "\n";
                }
                final String finalResult = result;
                TraceRouteRecord.sendTraceRouteRecord("http://202.120.36.190:8080/traceroute/upload", Util.getBssid(), Util.getMacAdress(), result);*/
                if(init){
                    Intent serviceIntent = new Intent(context, SendAPAcessRecordService.class);
                    serviceIntent.putExtra("bssid", wifiInfo.getBSSID());
                    serviceIntent.putExtra("macAdress", wifiInfo.getMacAddress());
                    serviceIntent.putExtra("startTime", startTime);
                    serviceIntent.putExtra("endTime", endTime);
                    context.startService(serviceIntent);
                    Log.e("send", "send");
                    init = false;
                } else {

                }
                startTime = new Date().getTime();
                endTime = 0;
                bssid = wifiManager.getConnectionInfo().getBSSID();
            }
        }else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
        {
            //WIFI开关
            int wifistate=intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_DISABLED);
            if(wifistate==WifiManager.WIFI_STATE_DISABLED)
            {//如果关闭
                Log.e("wifi", "turned off");
            } else if (wifistate==WifiManager.WIFI_STATE_ENABLED){
                Log.e("wifi", "turned on");
            }
        }
    }
}