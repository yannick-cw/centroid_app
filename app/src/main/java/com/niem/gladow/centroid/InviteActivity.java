package com.niem.gladow.centroid;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.HashMap;
import java.util.List;

/**
 * Created by clem on 05/01/16.
 */
public class InviteActivity extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;

    private Invite invite;

    private View showCentroidButton;
    private View declineInviteButton;
    private View acceptInviteButton;
    private View inviteHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_activity);

        //getting the current invite, by Id that was passed in this activity via putExtra(String)
        invite = InviteHandler.getInviteByTime(Long.parseLong(getIntent().getStringExtra("InviteId")));
        Log.d("Input Intent:",getIntent().getStringExtra("InviteId"));

        //setting up needed Views (Buttons etc.)
        declineInviteButton = findViewById(R.id.declineInviteButton);
        acceptInviteButton = findViewById(R.id.acceptInviteButton);
        inviteHeader = findViewById(R.id.showAcceptedStatus);
        TextView _inviteStatus1 = (TextView) findViewById(R.id.InviteStatusText1);
        TextView _inviteStatus2 = (TextView) findViewById(R.id.InviteStatusText2);
        _inviteStatus1.setText(Util.getInstance().getDate(invite.getStartTime()));
        _inviteStatus2.setText(invite.getStatus().toString());
        showCentroidButton = findViewById(R.id.showCentroidButton);

        //extracting members names from this invite
        final List<String> _list = invite.getAllMembers();
        Log.d("members",invite.getAllMembers().toString());
        //filling the ListView with Members of this invite via ArrayAdapter
        final ListView _listView = (ListView) findViewById(R.id.memberListView);
        final StableArrayAdapter _adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, _list);
        _listView.setAdapter(_adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //makes buttons visible if invite exits
        if (invite.getStatus() != InviteReply.UNANSWERED) {
            declineInviteButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.GONE);
            inviteHeader.setVisibility(View.VISIBLE);
        }
        //checks if centroid already exists
        if (invite.existsCentroid()){
            showCentroidButton.setEnabled(true);
        }
    }

    public void showCentroidOnMap(View view) {
        Intent intent = new Intent(this, GoogleMapActivity.class);
        intent.putExtra("centroid", InviteHandler.getInviteByTime(invite.getStartTime()).getCentroid().getLatLng());
        startActivity(intent);
    }

    //is called when accept or decline button is pressed
    public void responseToInvite(View view) {
        switch (view.getId()) {
            case R.id.acceptInviteButton:
                if (!getGpsPermission()) return;
                chooseTransportationMode();
                InviteHandler.responseToInvite(invite.getStartTime(),InviteReply.ACCEPTED,invite.getTransportationMode(), this);
                Toast.makeText(this, "Invite accepted", Toast.LENGTH_SHORT).show();
                break;
            case R.id.declineInviteButton:
                InviteHandler.responseToInvite(invite.getStartTime(),InviteReply.DECLINED,invite.getTransportationMode(), this);
                Toast.makeText(this, "Invite declined", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }
        declineInviteButton.setVisibility(View.GONE);
        acceptInviteButton.setVisibility(View.GONE);
        //TODO check if centroid is active
        onBackPressed();
    }

    private void chooseTransportationMode(){
        // TODO POPUP with choices of TransportationMode
        invite.setTransportationMode(TransportationMode.BIKE);
    }

    //TODO put GPS Permission in one nice place
    //TODO check if can be refactored together with sendGps()
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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

    //TODO check if normal ArrayAdapter<String> is sufficient, maybe change to Hashmap if status of friends should be displayed too
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }


}
