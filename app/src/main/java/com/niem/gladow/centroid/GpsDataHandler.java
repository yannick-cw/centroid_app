package com.niem.gladow.centroid;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * The GpsDataHandler is the central point to manage the phones GPS data
 */
public class GpsDataHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    public static final String SEND_GPS = "/android/currentGPS/";
    protected LocationRequest mLocationRequest;
    private boolean locationUpdateStarted = false;

    private static GpsDataHandler instance;

    private GpsDataHandler(Context context) {
        buildGoogleApiClient(context);
        googleApiClient.connect();
    }

    public static void init(Context context) {
        if(instance == null) {
            instance = new GpsDataHandler(context);
        }
    }

    public static GpsDataHandler getInstance() {
        assert (instance != null);
        return instance;
    }

    protected synchronized void buildGoogleApiClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(!locationUpdateStarted) {
                startLocationUpdates();
                locationUpdateStarted = true;
            }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("GPSDATAHANDLER", "Location Changed");
        lastLocation = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("GpsDataHandler", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("GpsDataHandler", "Connection suspended");
        googleApiClient.connect();
    }

    public Location getLastLocation() {
        return lastLocation;
    }
}
