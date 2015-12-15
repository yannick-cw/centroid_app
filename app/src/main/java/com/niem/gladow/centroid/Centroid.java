package com.niem.gladow.centroid;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

/**
 * This is the data type to save the centroid
 */
public class Centroid {
    LatLng latLng;

    public Centroid(String latLong) {
        //input string from server is splitted
        double _lat = Double.valueOf(latLong.split(",")[0]);
        double _long = Double.valueOf(latLong.split(",")[1]);

        latLng = new LatLng(_lat, _long);
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
