package com.niem.gladow.centroid;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by clem on 11.11.15.
 */
public class InviteFriendsActivity extends Activity {
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_activity);

        final ListView _listView = (ListView) findViewById(R.id.listView);

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
                        /* TODO would be more elegant to check ListView after pressing send button
                           and only add selected items to inviteList */
                        PersistenceHandler.getInstance().removeFromInviteList(_textViewClicked.getText().toString());
                    }
                } catch (Exception e) {
                    Log.v("Exception ON Click", e.getMessage(), e);
                }

            }
        });
    }

    @Override
    public void onBackPressed (){
        //TODO check if this can be made more elegantly (Maybe with onDestroy?)
        PersistenceHandler.getInstance().clearInviteList(); //cleanup of inviteList
        super.onBackPressed();
    }
}