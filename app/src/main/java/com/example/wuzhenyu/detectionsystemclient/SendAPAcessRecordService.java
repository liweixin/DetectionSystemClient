package com.example.wuzhenyu.detectionsystemclient;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lwx on 2016/4/7.
 */
public class SendAPAcessRecordService extends Service {
    String bssid, macAdress;
    long startTime, endTime;
    boolean firstOnStart;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        int tmp = super.onStartCommand(intent, flags, startId);
        Log.e("service", "onStartCommand");
        if(firstOnStart){
            //每次startService会调用两次onStartCommand，过滤掉多余的一次。
            bssid = intent.getStringExtra("bssid");
            macAdress = intent.getStringExtra("macAdress");
            startTime = intent.getLongExtra("startTime", 0);
            endTime = intent.getLongExtra("endTime", 0);
            ApAcessRecord.sendApAcessRecord(bssid, macAdress, startTime, endTime);
            firstOnStart = false;
        } else {
            firstOnStart = true;
        }
        return Service.START_NOT_STICKY;
    }
    @Override
    public void onCreate(){
        Log.e("service", "onCreate");
        super.onCreate();
        firstOnStart = true;
    }
}
