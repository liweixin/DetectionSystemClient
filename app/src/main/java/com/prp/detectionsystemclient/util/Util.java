package com.prp.detectionsystemclient.util;

import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.prp.detectionsystemclient.MyApplication;
import com.prp.detectionsystemclient.data.NearbyWifi;
import com.prp.detectionsystemclient.data.WifiInfosAPI;

import java.util.HashMap;

/**
 * Created by lwx on 2016/6/3.
 */
public class Util {
    private static double latitude;
    private static double longtitude;
    private static NearbyWifi nearbyWifi = null;
    public static HashMap<Location, WifiInfosAPI> map = new HashMap<>();

    public static WifiInfosAPI getWifiInfosAPI() {
        return wifiInfosAPI;
    }

    public static void setWifiInfosAPI(WifiInfosAPI wifiInfosAPI) {
        Util.wifiInfosAPI = wifiInfosAPI;
    }

    private static WifiInfosAPI wifiInfosAPI = null;

    public static NearbyWifi getNearbyWifi() {
        return nearbyWifi;
    }

    public static void setNearbyWifi(NearbyWifi nearbyWifi) {
        Util.nearbyWifi = nearbyWifi;
    }

    public static void toast(String text){
        Toast.makeText(MyApplication.instance.getContext(), text, Toast.LENGTH_SHORT).show();
    }
    public static double getLatitude(){
        /*if(latitude-0<=0.000001){
            throw new IllegalStateException("latitude has not been init.");
        }*/
        return latitude;
    }
    public static double getLongtitude(){
        /*if(longtitude-0<=0.000001){
            throw new IllegalStateException("Longtitude has not been init.");
        }*/
        return longtitude;
    }
    public static void setLatitude(double latitude){
        Util.latitude = latitude;
    }
    public static void setLongtitude(double longtitude){
        Util.longtitude = longtitude;
    }
    public static class Location{
        private double latitude;
        private double longtitude;
        public Location(double latitude, double longtitude){
            this.latitude = latitude;
            this.longtitude = longtitude;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Location)) return false;

            Location location = (Location) o;

            if (Double.compare(location.latitude, latitude) != 0) return false;
            return Double.compare(location.longtitude, longtitude) == 0;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(latitude);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(longtitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
}
