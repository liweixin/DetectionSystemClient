package com.example.wuzhenyu.detectionsystemclient;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwx on 2016/5/29.
 */
public class TraceRouteRecord {
    public static void sendTraceRouteRecord(final String url, final String bssid, final String macAdress, final String content){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("bssid", bssid));
                params.add(new BasicNameValuePair("macAdress", macAdress));
                params.add(new BasicNameValuePair("content", content));
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
