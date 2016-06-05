package com.prp.detectionsystemclient.data;

import java.util.List;

/**
 * Created by lwx on 2016/6/3.
 */
public class NearbyWifi {

    /**
     * count : 15
     * location : [{"latitude":31.028267,"longtitude":121.444008},{"latitude":31.028342,"longtitude":121.444003},{"latitude":31.028406,"longtitude":121.443999},{"latitude":31.032559,"longtitude":121.443299},{"latitude":31.029156,"longtitude":121.442828},{"latitude":31.030854,"longtitude":121.442549},{"latitude":31.030823,"longtitude":121.442483},{"latitude":31.030894,"longtitude":121.442442},{"latitude":31.030783,"longtitude":121.442435},{"latitude":31.030734,"longtitude":121.442426},{"latitude":31.030724,"longtitude":121.442398},{"latitude":31.030335,"longtitude":121.441388},{"latitude":31.026759,"longtitude":121.437759},{"latitude":31.026746,"longtitude":121.437736},{"latitude":0,"longtitude":0}]
     */

    private int count;
    /**
     * latitude : 31.028267
     * longtitude : 121.444008
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

        @Override
        public String toString() {
            return "LocationEntity{" +
                    "latitude=" + latitude +
                    ", longtitude=" + longtitude +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NearbyWifi{" +
                "count=" + count +
                ", location=" + location +
                '}';
    }
}
