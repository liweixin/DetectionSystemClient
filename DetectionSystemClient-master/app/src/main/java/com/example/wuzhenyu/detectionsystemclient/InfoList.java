package com.example.wuzhenyu.detectionsystemclient;

import android.location.Location;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

import com.baidu.location.BDLocation;

/**
 * Created by HP on 2015/10/23.
 */
public class InfoList {
    public ScanResult scanResult;
    public DhcpInfo dhcpInfo;
    public WifiInfo wifiInfo;
    public BDLocation location;
    static public String getIpString(int i){
        StringBuffer s = new StringBuffer();
        s.append((i & 0xFF) + ".");
        s.append(((i>>8) & 0xFF) + ".");
        s.append(((i>>16) & 0xFF) + ".");
        s.append(((i>>24) & 0xFF) + ".");
        return s.toString();
    }
    public InfoList(ScanResult scanResult, WifiInfo wifiInfo, DhcpInfo dhcpInfo, BDLocation location){
        this.wifiInfo = wifiInfo;
        this.scanResult = scanResult;
        this.dhcpInfo = dhcpInfo;
        this.location = location;
    }
    public InfoList(ScanResult scanResult, WifiInfo wifiInfo){
        this.scanResult = scanResult;
        this.wifiInfo = wifiInfo;
    }
}
