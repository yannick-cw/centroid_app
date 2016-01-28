package com.niem.gladow.centroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeViewActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 12;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;
    private static final String HIT_SYNC = "please hit sync - missing permissions";
    private static final String HIT_NEXT_SYNC = "please hit next to sync";
    private static final String HIT_NEXT = "please hit next";
    private static final String NEXT_FOR_PERMISSION = "please hit next to grand permission";
    private static final String PLEASE_HIT_NEXT_TO_REGISTER = "please hit next to register";
    private String _status;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_view);
        _status = getIntent().getStringExtra(MainActivity.STATUS);
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        Button nex = (Button) findViewById(R.id.button);
        nex.getBackground().setColorFilter(Util.getInstance().getButtonColor(this), PorterDuff.Mode.MULTIPLY);


        Typeface _typeFace = Typeface.createFromAsset(getAssets(), "fonts/VeraSe.ttf");
        welcomeText.setTypeface(_typeFace);
        nex.setTypeface(_typeFace);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setTypeface(_typeFace);

        if (!permissionsGranted()) {
            requestMissingPermissions();
        } else if (_status.equals(MainActivity.FIRST_LOAD)) {
            syncWithServer();
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

    public void syncButton(View view) {
        syncWithServer();
    }

    private void syncWithServer() {
        if (!permissionsGranted()) {
            requestMissingPermissions();
            welcomeText.setText(NEXT_FOR_PERMISSION);
        } else {
            saveOwnNumber();
            if (!PersistenceHandler.getInstance().getOwnNumber().equals("/") && PersistenceHandler.getInstance().isOwnNumberInFile()) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
                sendContacts();
                if (PersistenceHandler.getInstance().isTokenInFile()) {
                    Intent _intent = new Intent(this, MainActivity.class);
                    startActivity(_intent);
                    finish();
                } else {
                    welcomeText.setText(PLEASE_HIT_NEXT_TO_REGISTER);
                    try {
                        Snackbar.make(getCurrentFocus(), HIT_NEXT, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                welcomeText.setText(HIT_NEXT_SYNC);
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

    private void saveOwnNumber() {
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
                    try {
                        Snackbar.make(getCurrentFocus(), HIT_SYNC, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    syncButton(getCurrentFocus());
                } else {
                    try {
                        Snackbar.make(getCurrentFocus(), HIT_SYNC, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    syncButton(getCurrentFocus());
                } else {
                    try {
                        Snackbar.make(getCurrentFocus(), HIT_SYNC, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

        }
    }
}
