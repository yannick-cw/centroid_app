package com.niem.gladow.centroid;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

/**
 * Created by yannick_uni on 12/15/15.
 */
public class Centroid {
    LatLng latLng;

    public Centroid(String latLong) {
        double _lat = Double.valueOf(latLong.split(",")[1]);
        double _long = Double.valueOf(latLong.split(",")[0]);

        latLng = new LatLng(_lat, _long);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
