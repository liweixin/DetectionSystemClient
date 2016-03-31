package com.example.wuzhenyu.detectionsystemclient;

import java.util.List;

/**
 * Created by lwx on 2015/12/1.
 */
public class LocationInfoAPI {

    /**
     * count : 5
     * location : [{"latitude":31.022508,"longtitude":121.4353897},{"latitude":31.0256212,"longtitude":121.4342125},{"latitude":31.025095,"longtitude":121.4312778},{"latitude":31.0255874,"longtitude":121.4311581},{"latitude":31.0255873,"longtitude":121.4311553}]
     */

    private int count;
    /**
     * latitude : 31.022508
     * longtitude : 121.4353897
     */

    private List<LocationEntity> location;

    public void setCount(int count) {
        this.count = count;
    }

    public void setLocation(List<LocationEntity> location) {
        this.location = location;
    }

    public int getCount() {
        return count;
    }

    public List<LocationEntity> getLocation() {
        return location;
    }

    public static class LocationEntity {
        private double latitude;
        private double longtitude;

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongtitude(double longtitude) {
            this.longtitude = longtitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongtitude() {
            return longtitude;
        }
    }
}
