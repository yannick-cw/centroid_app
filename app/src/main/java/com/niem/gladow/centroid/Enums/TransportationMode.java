package com.niem.gladow.centroid.Enums;

/**
 * Created by clem on 12/01/16.
 */
public enum TransportationMode {
    FOOT("w"), CAR("d"), BIKE("b"), PUBLIC("r"), DEFAULT("d");

    private String mode;
    TransportationMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }
}