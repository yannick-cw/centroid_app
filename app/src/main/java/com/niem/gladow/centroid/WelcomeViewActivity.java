package com.niem.gladow.centroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.niem.gladow.centroid.gcm.RegistrationIntentService;

public class WelcomeViewActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 12;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_view);

        saveOwnNumber();
        syncWithServer(this.getCurrentFocus());
    }

    public void startCentroid (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void syncWithServer(View view) {
        final Button _startCentroidButton = (Button) findViewById(R.id.startCentroid);
        if (!PersistenceHandler.getInstance().getOwnNumber().equals("/")) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            sendContacts();
            _startCentroidButton.setEnabled(true);

        } else {
            Toast.makeText(this, "Please enter your own number and hit sync", Toast.LENGTH_LONG).show();
        }
    }

    private void saveOwnNumber (){
        //check for permission, if none do if
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            PersistenceHandler.getInstance().saveOwnNumber(this);
        }
    }

    private void sendContacts() {
        //check for permission, if none do if
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        else {
            new NumberLogicHandler(this).executePhoneDataHandler();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveOwnNumber();
                } else {
                    Toast.makeText(this, "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendContacts();
                } else {
                    Toast.makeText(this, "READ_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}
