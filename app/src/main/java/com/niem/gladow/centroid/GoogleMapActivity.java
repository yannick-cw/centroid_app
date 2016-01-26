package com.niem.gladow.centroid;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends FragmentActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    protected static final String TAG = "GoogleMapActivity";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected LocationRequest mLocationRequest;
    private LatLng centroid;
    protected GoogleMap map;
    private boolean isFirstStart = true;

    private boolean locationUpdateStarted = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //gets the centroid latlng Object, which was attached to the intent
        centroid = getIntent().getParcelableExtra("centroid");
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
    }

    private void updateUI() {
        LatLng _location = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
        //is executed only on the first UI update to set bounds and zoom
        if (isFirstStart) {
            LatLngBounds.Builder _builder = new LatLngBounds.Builder();
            _builder.include(centroid);
            _builder.include(_location);
            LatLngBounds _latLngBounds = _builder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(_latLngBounds, 10));
            isFirstStart = false;
        }
        try {
            map.clear();
            //adds two markers, one with own position and one with centroid
            map.addMarker(new MarkerOptions()
                    .position(_location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .alpha(0.5F)
                    .title("you"));
            map.addMarker(new MarkerOptions()
                    .position(centroid)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("centroid"));
        } catch (Exception e) {Log.e("Error", e.toString());}

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        updateUI();

        if(!locationUpdateStarted) {
            startLocationUpdates();
            locationUpdateStarted = true;
        }
    }

    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.i("LOCATION CHANGED", String.valueOf(mCurrentLocation.getLatitude()));
        updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }
}