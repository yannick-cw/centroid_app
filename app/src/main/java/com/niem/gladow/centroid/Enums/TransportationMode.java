package com.niem.gladow.centroid.Enums;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public enum TransportationMode {
    FOOT("w"), CAR("d"), BIKE("b"), PUBLIC("r"), DEFAULT("d"), DECLINED("d");

    private String mode;
    TransportationMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}