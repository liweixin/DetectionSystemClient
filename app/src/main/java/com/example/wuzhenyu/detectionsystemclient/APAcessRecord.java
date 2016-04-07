package com.example.wuzhenyu.detectionsystemclient;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwx on 2016/4/7.
 */
public class ApAcessRecord {
    public static void sendApAcessRecord(final String bssid, final String macAdress, final long startTime, final long endTime){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                HttpPost httpPost = new HttpPost("http://202.120.36.190:8080/apacessrecord");
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("bssid", bssid));
                params.add(new BasicNameValuePair("macAdress", macAdress));
                params.add(new BasicNameValuePair("startTime", sdf.format(startTime) + ""));
                params.add(new BasicNameValuePair("endTime", sdf.format(endTime) + ""));
                params.add(new BasicNameValuePair("longtitude", MainActivity.bdLocation.getLongitude() + ""));
                params.add(new BasicNameValuePair("latitude", MainActivity.bdLocation.getLatitude() + ""));
                Log.e("params", params.toString());
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(httpPost);
                    HttpEntity entity = response.getEntity();
                    String str = EntityUtils.toString(entity, "utf-8");
                    Log.e("result", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
