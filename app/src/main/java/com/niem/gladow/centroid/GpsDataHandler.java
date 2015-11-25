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
 * Created by yannick_uni on 11/17/15.
 */
public class GpsDataHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private Context context;
    private Location lastLocation;
    private PhoneDataHandler phoneDataHandler;
    private static final String SEND_GPS = "/android/currentGPS/", POST = "1";
    private static String OWN_NUMBER = PersistenceHandler.getInstance().getOwnNumber();

    public GpsDataHandler (Context context) {
        this.context = context;
        buildGoogleApiClient();
        googleApiClient.connect();
        phoneDataHandler = new PhoneDataHandler(context);
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
        }else{
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            new RestConnector(context).execute(POST, SEND_GPS + OWN_NUMBER
                                                              + lastLocation.getLatitude()+"/"
                                                              + lastLocation.getLongitude());
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
