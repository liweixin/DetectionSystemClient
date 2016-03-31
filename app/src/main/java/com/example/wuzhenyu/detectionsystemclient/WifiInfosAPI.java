package com.example.wuzhenyu.detectionsystemclient;

import java.util.List;

/**
 * Created by lwx on 2015/12/1.
 */
public class WifiInfosAPI {
    /**
     * count : 2
     * wifiInfos : [{"signals":58,"security":"[ESS]","ssid":"STJUSE","bssid":"58:66:ba:68:74:90"},{"signals":84,"security":"[ESS]","ssid":"STJUSE","bssid":"58:66:ba:aa:03:60"}]
     */

    private int count;
    /**
     * signals : 58
     * security : [ESS]
     * ssid : STJUSE
     * bssid : 58:66:ba:68:74:90
     */

    private List<WifiInfosEntity> wifiInfos;

    public void setCount(int count) {
        this.count = count;
    }

    public void setWifiInfos(List<WifiInfosEntity> wifiInfos) {
        this.wifiInfos = wifiInfos;
    }

    public int getCount() {
        return count;
    }

    public List<WifiInfosEntity> getWifiInfos() {
        return wifiInfos;
    }

    public static class WifiInfosEntity {
        private int signals;
        private String security;
        private String ssid;
        private String bssid;
        private String timeString;

        public void setSignals(int signals) {
            this.signals = signals;
        }

        public void setSecurity(String security) {
            this.security = security;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public void setBssid(String bssid) {
            this.bssid = bssid;
        }

        public int getSignals() {
            return signals;
        }

        public String getSecurity() {
            return security;
        }

        public String getSsid() {
            return ssid;
        }

        public String getBssid() {
            return bssid;
        }

        public String getTimeString() { return timeString; }
    }
}
