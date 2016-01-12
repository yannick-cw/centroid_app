package com.niem.gladow.centroid;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This is the data type to save the centroid
 */
public class Centroid implements Serializable {
    double latitude;
    double longitude;
    public Centroid(String latLong) {
        //input string from server is splitted
        latitude  = Double.valueOf(latLong.split(",")[0]);
        longitude = Double.valueOf(latLong.split(",")[1]);
    }

    public double getLat() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
