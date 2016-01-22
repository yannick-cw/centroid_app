package com.niem.gladow.centroid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by clem on 11.11.15.
 */
public class InviteFriendsActivity extends AppCompatActivity {
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;
    private GestureDetectorCompat gestureDetectorCompat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ListView _listView = (ListView) findViewById(R.id.listView);
        _listView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InviteFriendsActivity.this.onTouchEvent(event);
                return false;
            }
        });
        final Button _inviteFriendsButton = (Button) findViewById(R.id.inviteFriendsButton);

        final Map<String, String> _map = PersistenceHandler.getInstance().getFriendMap();

        final HashMapArrayAdapter _adapter = new HashMapArrayAdapter(this,
                R.layout.list_view_item, new ArrayList(_map.entrySet()));
        _adapter.setCheckList(_map.size());
        _listView.setAdapter(_adapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                try {
                    // update state of the views
                    _adapter.toggleCheckList(position);
                    _adapter.notifyDataSetChanged();

                    TextView _textViewClicked = (TextView) view.findViewById(R.id.friend_number);

                    // check state of view and add/remove it from inviteList
                    if (_adapter.getCheckStatus(position)) {
                        Log.d("Name", _textViewClicked.getText().toString());
                        PersistenceHandler.getInstance().addToInviteList(_textViewClicked.getText().toString());
                    } else {
                        PersistenceHandler.getInstance().removeFromInviteList(_textViewClicked.getText().toString());
                    }

                    if (!PersistenceHandler.getInstance().getInviteList().isEmpty()) {
                        _inviteFriendsButton.setEnabled(true);
                    } else {
                        _inviteFriendsButton.setEnabled(false);
                    }

                } catch (Exception e) {
                    Log.v("Exception ON Click", e.getMessage(), e);
                }

            }
        });

        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
    }

    @Override
    public void onBackPressed (){
        PersistenceHandler.getInstance().clearInviteList(); //cleanup of inviteList
        Intent intent = new Intent(InviteFriendsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void inviteFriends(View view) {
        Log.d("inviteFriends", "pressed");
        if (getGpsPermission()) {
            chooseTransportationMode(this);
        }else{
            Toast.makeText(this,"GPS Permission Needed",Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseTransportationMode(final Context _context){
        CharSequence transportationModes[] = getResources().getStringArray(R.array.transportation_modes);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a transportation Mode");
        builder.setItems(transportationModes, new DialogInterface.OnClickListener() {
            TransportationMode _transportationMode = TransportationMode.DEFAULT;
            boolean _hasChosen = false;
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on transportationModes[which]
                switch (which) {
                    case 0:
                        _transportationMode = TransportationMode.FOOT;
                        _hasChosen = true;
                        break;
                    case 1:
                        _transportationMode = TransportationMode.BIKE;
                        _hasChosen = true;
                        break;
                    case 2:
                        _transportationMode = TransportationMode.CAR;
                        _hasChosen = true;
                        break;
                    case 3:
                        _transportationMode = TransportationMode.PUBLIC;
                        _hasChosen = true;
                        break;
                }
                if(_hasChosen){
                    new NumberLogicHandler(_context).inviteFriends(_transportationMode);
                    onBackPressed();
                }
            }

        });
        builder.show();
    }

    //TODO put GPS Permission in a good Place and make it nice
    public boolean getGpsPermission () {
        //check for permission, if none do if
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public void sendGps(View view) {
        Log.d("sendOwnGps", "pressed");
        //check for permission, if none do if
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            new GpsDataHandler(this);
        }
    }

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
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe left' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() < event1.getX() && event1.getX() - event2.getX() > 200 && Math.abs(event2.getY() - event1.getY()) < 200){
                //switch another activity
                Intent intent = new Intent(InviteFriendsActivity.this, InviteListViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                finish();
            }

            return true;
        }
    }
}