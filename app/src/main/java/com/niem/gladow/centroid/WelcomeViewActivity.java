package com.niem.gladow.centroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WelcomeViewActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 12;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;
    private String _status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_view);
        _status = getIntent().getStringExtra(MainActivity.STATUS);

        if (!permissionsGranted()) {
            requestMissingPermissions();
        }
        else if (_status.equals(MainActivity.FIRST_LOAD)) {
            syncWithServer(this.getCurrentFocus());
        }
    }

    private void requestPermission(String[] permissions, int requestTo) {
        ActivityCompat.requestPermissions(this, permissions,
                requestTo);
    }

    private boolean hasPermission(String accessTo) {
        return ContextCompat.checkSelfPermission(this, accessTo)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void startCentroid (View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*TODO only activate start button if really successful, and ad more return false/true and handling for everything */
    public void syncWithServer(View view) {
        if (!permissionsGranted()) {
            requestMissingPermissions();
        } else {
            saveOwnNumber();
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
    }

    private void requestMissingPermissions() {
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
            requestPermission(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        if (!hasPermission(Manifest.permission.READ_PHONE_STATE) && _status.equals(MainActivity.FIRST_LOAD)) {
            requestPermission(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private boolean permissionsGranted() {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && hasPermission(Manifest.permission.READ_CONTACTS)
                && (hasPermission(Manifest.permission.READ_PHONE_STATE) || !_status.equals(MainActivity.FIRST_LOAD));
    }

    private void saveOwnNumber (){
        PersistenceHandler.getInstance().saveOwnNumber(this);
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
                } else {
                    Toast.makeText(this, "please hit sync - missing permissions", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    syncWithServer(getCurrentFocus());
                } else {
                    Toast.makeText(this, "please hit sync - missing permissions", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    syncWithServer(getCurrentFocus());
                } else {
                    Toast.makeText(this, "please hit sync - missing permissions", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }
}
