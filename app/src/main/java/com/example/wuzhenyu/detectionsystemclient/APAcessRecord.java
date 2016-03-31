package com.example.wuzhenyu.detectionsystemclient;

import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class APAcessRecord implements Runnable
{
    public Handler myHandler;
    private int judgeValidsendTime = 0;
    private long startSecond, endSecond;
    public void run()
    {
        HttpPost httpPost = new HttpPost("http://202.120.36.190:8080/apacessrecord");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if(SecondActivity.wifiManager.getWifiState()== WifiManager.WIFI_STATE_ENABLED && judgeValidsendTime==0) {
            Date date = new Date();
            startSecond = date.getTime();
            judgeValidsendTime=1;
        }
        if(SecondActivity.wifiManager.getWifiState()==WifiManager.WIFI_STATE_DISABLED && judgeValidsendTime==1){
            judgeValidsendTime=0;
            endSecond = new Date().getTime();
            params.add(new BasicNameValuePair("bssid", SecondActivity.wifiInfo.getBSSID()));
            params.add(new BasicNameValuePair("macAdress", SecondActivity.wifiInfo.getMacAddress()));
            params.add(new BasicNameValuePair("startTime", startSecond+""));
            params.add(new BasicNameValuePair("endTime", endSecond+""));
            params.add(new BasicNameValuePair("longtitude", SecondActivity.bdlocation.getLongitude()+""));
            params.add(new BasicNameValuePair("latitude", SecondActivity.bdlocation.getLatitude()+""));
        }
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String str = EntityUtils.toString(entity, "utf-8");
            Log.e("result", str);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}