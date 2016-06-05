package com.prp.detectionsystemclient.network;

import android.util.Log;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prp.detectionsystemclient.MyApplication;

/**
 * Created by lwx on 2016/6/2.
 */
public class Network {
    public static String ipAdress = "202.120.36.190";
    public static String port = "8080";
    public static String baseAdress = "http://"+ ipAdress + ":" + port + "/";
    public static String traceRouteIp = ipAdress;
    private static RequestQueue mQueue = Volley.newRequestQueue(MyApplication.instance.getContext());
    public Network(){}
    public static RequestQueue getQueue(){
        return mQueue;
    }
    public static void get(StringRequest stringRequest){
        mQueue.add(stringRequest);
    }
    public static void post(StringRequest stringRequest){
        mQueue.add(stringRequest);
    }
}
