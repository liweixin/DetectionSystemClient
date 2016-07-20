package com.prp.detectionsystemclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.prp.detectionsystemclient.function.APAcessRecord;
import com.prp.detectionsystemclient.util.Util;

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
        Log.d("service", "onStartCommand");
        if(/*firstOnStart*/true){
            //每次startService会调用两次onStartCommand，过滤掉多余的一次。
            bssid = intent.getStringExtra("bssid");
            macAdress = intent.getStringExtra("macAdress");
            startTime = intent.getLongExtra("startTime", 0);
            endTime = intent.getLongExtra("endTime", 0);
            APAcessRecord.sendApAcessRecord("apacessrecord", bssid, macAdress, startTime, endTime,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response", response);
                            Util.toast("wifi访问记录上传成功");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("error", error.getMessage());
                        }
                    });
            firstOnStart = false;
        } else {
            firstOnStart = true;
        }
        return Service.START_NOT_STICKY;
    }
    @Override
    public void onCreate(){
        Log.d("service", "onCreate");
        super.onCreate();
        firstOnStart = true;
    }
}