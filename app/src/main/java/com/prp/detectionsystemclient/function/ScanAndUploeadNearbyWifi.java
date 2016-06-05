package com.prp.detectionsystemclient.function;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.volley.Response;
import com.prp.detectionsystemclient.network.Network;
import com.prp.detectionsystemclient.network.StringPostRequest;
import com.prp.detectionsystemclient.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lwx on 2016/6/4.
 */
public class ScanAndUploeadNearbyWifi {
    static List<ScanResult> wifiList = null;
    static WifiInfo wifiInfo = null;
    public static void scan(WifiManager wifiManager, Response.Listener<String> listener, Response.ErrorListener errorListener){
        wifiList = wifiManager.getScanResults();
        wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifilist", wifiList.toString());
        for(int i=0; i<wifiList.size(); i++){
            upload("apFeatures", wifiList.get(i), listener, errorListener); //上传扫描到的周围wifi信息
        }
    }

    public static WifiInfo getWifiInfo(){
        return wifiInfo;
    }

    public static List<ScanResult> getWifiList() {
        return wifiList;
    }

    public static void setWifiList(List<ScanResult> wifiList) {
        ScanAndUploeadNearbyWifi.wifiList = wifiList;
    }

    private static void upload(String addr, ScanResult scanResult, Response.Listener<String> listener, Response.ErrorListener errorListener){
        String url = Network.baseAdress + addr;
        Map<String, String> map = new HashMap<>();
        map.put("MacAdress", wifiInfo.getMacAddress());
        map.put("BSSID", scanResult.BSSID);
        map.put("SSID", scanResult.SSID);
        map.put("Security", scanResult.capabilities);
        map.put("Signal", String.valueOf(Math.abs(scanResult.level)));
        map.put("Latitude", Util.getLatitude()+"");
        map.put("Longtitude", Util.getLongtitude()+"");
        StringPostRequest stringPostRequest = new StringPostRequest(url, listener, errorListener, map);
        Network.getQueue().add(stringPostRequest);
    }
}
