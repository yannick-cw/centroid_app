package com.niem.gladow.centroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.niem.gladow.centroid.Database.MiniDB;

/**
 * This activity just handles the starting app and redirects the user to the right place
 * */
public class MainActivity extends AppCompatActivity {
    public static final String STATUS = "status";
    public static final String FIRST_LOAD = "firstLoad";
    public static final String NO_PERMISSION = "noPermission";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup main activity
        super.onCreate(savedInstanceState);
        MiniDB.init(this);

        //check for permission, if none do if
        if (!PersistenceHandler.getInstance().firstLoadOwnNumberAndToken()) {
            goToWelcomeView(FIRST_LOAD);

        } else if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && hasPermission(Manifest.permission.READ_CONTACTS)) {
            GpsDataHandler.init(this);

            //updates contacts
            PersistenceHandler.getInstance().loadFriendMapFromDB();
            Log.d("loaded friend map", PersistenceHandler.getInstance().getFriendMap().toString());
            sendContacts();
            //todo try if works
//            //updates the token every start
//            Intent _intent = new Intent(this, RegistrationIntentService.class);
//            startService(_intent);
            Intent intent = new Intent(this, CentroidListViewActivity.class);
            startActivity(intent);
            finish();

        } else {
            goToWelcomeView(NO_PERMISSION);
        }
    }

    private boolean hasPermission(String accessTo) {
        return ContextCompat.checkSelfPermission(this, accessTo) == PackageManager.PERMISSION_GRANTED;
    }

    private void goToWelcomeView(String request) {
        Intent _intent = new Intent(this, WelcomeViewActivity.class);
        _intent.putExtra(STATUS, request);
        startActivity(_intent);
        finish();
    }

    private void sendContacts() {
        new NumberLogicHandler(this).executePhoneDataHandler();
    }
}
