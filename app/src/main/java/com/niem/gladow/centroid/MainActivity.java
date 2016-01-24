package com.niem.gladow.centroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.niem.gladow.centroid.Database.MiniDB;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setup main activity
        //todo maybe remove completely

        super.onCreate(savedInstanceState);
        MiniDB.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check for permission, if none do if

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            GpsDataHandler.init(this);

            if (!PersistenceHandler.getInstance().firstLoadOwnNumberAndToken()) {
                Intent _intent = new Intent(this, WelcomeViewActivity.class);
                startActivity(_intent);
                finish();
            } else {
                //updates contacts
                PersistenceHandler.getInstance().loadFriendMapFromDB();
                Log.d("loaded friend map", PersistenceHandler.getInstance().getFriendMap().toString());
                sendContacts();
                //updates the token every start
                Intent _intent = new Intent(this, RegistrationIntentService.class);
                startService(_intent);
                Intent intent = new Intent(this, CentroidListViewActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void sendContacts() {
        new NumberLogicHandler(this).executePhoneDataHandler();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onResume();
                } else {
                    onResume();
                    Toast.makeText(this, "please give gps permission", Toast.LENGTH_SHORT).show();

                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onResume();
                } else {
                    onResume();
                    Toast.makeText(this, "please give access to contacts", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
