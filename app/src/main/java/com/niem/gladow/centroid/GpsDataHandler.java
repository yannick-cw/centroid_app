package com.niem.gladow.centroid;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * The GpsDataHandler is the central point to manage the phones GPS data
 */
public class GpsDataHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private Context context;
    private Location lastLocation;
    private static final String SEND_GPS = "/android/currentGPS/";
    private static String OWN_NUMBER;

    //the gps data is send on creation of the class
    public GpsDataHandler (Context context) {
        this.context = context;
        buildGoogleApiClient();
        googleApiClient.connect();
        OWN_NUMBER = PersistenceHandler.getInstance().getOwnNumber() ;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (OWN_NUMBER.equals("/")){
            Toast.makeText(context,"Please enter your Number and try again",Toast.LENGTH_LONG).show();
            Log.d("GPS_NO_OWN_NUMBER", PersistenceHandler.getInstance().getOwnNumber());
        }else{
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            //todo latest is not good enough, needs to be a valid one

            assert(lastLocation != null);
            new RestConnector(context).execute(RestConnector.POST, SEND_GPS + OWN_NUMBER +"/"
                                                              + lastLocation.getLongitude()+"/"
                                                              + lastLocation.getLatitude());
            Log.d("lastLocation", String.valueOf(lastLocation.getLongitude()));
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("GpsDataHandler", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i("GpsDataHandler", "Connection suspended");
        googleApiClient.connect();
    }

}
