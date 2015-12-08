package com.niem.gladow.centroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.niem.gladow.centroid.gcm.RegistrationIntentService;


public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 11;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //TODO check if token is still valid right now it is reloaded every start (same one)
        //TODO additional check if play services installed please

        if (!PersistenceHandler.getInstance().firstLoadOwnNumberAndToken(this)) {
            Intent _intent = new Intent(this, WelcomeViewActivity.class);
            startActivity(_intent);
        }
        else {
            //updates contacts
            PersistenceHandler.getInstance().loadFriendMapFromDB(this);
            Log.d("loaded friend map", PersistenceHandler.getInstance().getFriendMap().toString());
            sendContacts();
            //updates the token every start
            Intent _intent = new Intent(this, RegistrationIntentService.class);
            startService(_intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (InviteHandler.ExistsNewInvite()) {
            //todo put button in variable
            findViewById(R.id.declineInviteButton).setVisibility(View.VISIBLE);
            findViewById(R.id.acceptInviteButton).setVisibility(View.VISIBLE);
        }
    }

    //TODO DRY
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

    public void sendGps(View view) {
        Log.d("sendOwnGps", "pressed");
        //check for permission, if none do if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            new GpsDataHandler(this);
        }
    }

    public boolean getGpsPermission () {
        //check for permission, if none do if
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public void responseToInvite(View view) {
        switch (view.getId()) {
            case R.id.acceptInviteButton:
                if (!getGpsPermission()) return;
                InviteHandler.responseToInvite(InviteReply.ACCEPTED, this);
                Toast.makeText(this, "Invite accepted", Toast.LENGTH_SHORT).show();
                break;
            case R.id.declineInviteButton:
                InviteHandler.responseToInvite(InviteReply.DECLINED, this);
                Toast.makeText(this, "Invite declined", Toast.LENGTH_SHORT).show();
                break;
        }
        findViewById(R.id.declineInviteButton).setVisibility(View.GONE);
        findViewById(R.id.acceptInviteButton).setVisibility(View.GONE);
    }

    public void seeList(View view) {
        Log.d("List", "pressed");
        Intent intent = new Intent(this, ListViewActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//TODO stürtzt ab, wenn permission nicht erteilt oder gps aus und erteilt (gps)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendGps(this.getCurrentFocus());
                } else {
                    Toast.makeText(this, "FINE_LOCATION Denied", Toast.LENGTH_SHORT).show();
                }
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
