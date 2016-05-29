package com.example.wuzhenyu.detectionsystemclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WifiStateReceiver extends BroadcastReceiver {
    Context context;
    String bssid;
    String macAdress;
    long startTime, endTime;
    boolean init;
    public WifiStateReceiver(Context context) {
        // TODO Auto-generated constructor stub
        this.context=context;
        macAdress = MyApplication.wifiInfo.getMacAddress();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(SecondActivity.wifiInfo!=null){
            macAdress = SecondActivity.wifiInfo.getMacAddress();
        }
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
                List<String> results = new TraceRouteActivity().exe("./data/data/com.example.wuzhenyu.detectionsystemclient/files/traceroute 202.120.36.190");
                String result = "";
			/* 将结果转换成字符串, 输出到 TextView中 */
                for(String line : results){
                    result += line + "\n";
                }
                final String finalResult = result;
                TraceRouteRecord.sendTraceRouteRecord("http://202.120.36.190:8080/traceroute/upload", Util.getBssid(), Util.getMacAdress(), result);
                if(init){
                    Intent serviceIntent = new Intent(context, SendAPAcessRecordService.class);
                    serviceIntent.putExtra("bssid", bssid);
                    serviceIntent.putExtra("macAdress", macAdress);
                    serviceIntent.putExtra("startTime", startTime);
                    serviceIntent.putExtra("endTime", endTime);
                    context.startService(serviceIntent);
                    Log.e("send", "send");
                    init = false;
                } else {

                }
                startTime = new Date().getTime();
                endTime = 0;
                bssid = MyApplication.wifiManager.getConnectionInfo().getBSSID();
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