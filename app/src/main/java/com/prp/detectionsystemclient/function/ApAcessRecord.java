package com.prp.detectionsystemclient.function;

/**
 * Created by liweixin on 2016/7/20.
 */
import android.net.wifi.ScanResult;
import android.util.Log;

import com.android.volley.Response;
import com.prp.detectionsystemclient.network.Network;
import com.prp.detectionsystemclient.network.StringPostRequest;
import com.prp.detectionsystemclient.util.Util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by lwx on 2016/4/7.
 */
public class APAcessRecord {
    public static void sendApAcessRecord(String addr, final String bssid, final String macAdress, final long startTime, final long endTime, Response.Listener<String> listener, Response.ErrorListener errorListener){
        //"http://202.120.36.190:8080/apacessrecord"
        String url = Network.baseAdress + addr;
        Map<String, String> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("bssid", bssid);
        map.put("macAdress", macAdress);
        map.put("startTime", sdf.format(startTime)+"");
        map.put("endTime", sdf.format(endTime)+"");
        map.put("longtitude", Util.getLongtitude()+"");
        map.put("latitude", Util.getLatitude()+"");
        StringPostRequest stringPostRequest = new StringPostRequest(url, listener, errorListener, map);
        Network.getQueue().add(stringPostRequest);
    }
}