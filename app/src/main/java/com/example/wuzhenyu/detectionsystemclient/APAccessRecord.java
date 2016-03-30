package com.example.wuzhenyu.detectionsystemclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Override;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.net.wifi.ScanResult;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JsonObject;
import org.json.JsonArray;

import java.util.Date;

public class APAccessRecord implements Runnable
{
    public Handler myHandler;
    private int judgeValidsendTime = 0;
    private long startsecond;
    public void run()
    {
        HttpPost httpPost = new HttpPost("http://202.120.36.190:8080/apacessrecord");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if(MainActivity.wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED && judgeValidsendTime==0) {
            Date date = new Date();
            startsecond = date.getTime();
            judgeValidsendTime=1;
        }
        if(MainActivity.wifiManager.getWifiState()==WifiManager.WIFI_STATE_DISABLED && judgeValidsendTime==1){
            judgeValidsendTime=0;
            Date endsecond = new Date();
            params.add(new NameValuePair("bssid", MainActivity.wifiInfo.getBSSID()));
            params.add(new NameValuePair("macAdress", MainActivity.wifiInfo.getMacAddress()));
            params.add(new NameValuePair("startTime", startsecond));
            params.add(new NameValuePair("endTime", endsecond.getTime()));
            params.add(new NameValuePair("longtitude", MainActivity.bdlocation.getLongitude()));
            params.add(new NameValuePair("latitude", MainActivity.bdlocation.getLatitude()));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String str = EntityUtils.toString(entity, "utf-8");
        Log.e("result", str);
    }
}
