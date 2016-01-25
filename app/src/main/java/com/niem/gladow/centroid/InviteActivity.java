package com.niem.gladow.centroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.InviteStatus;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by clem on 05/01/16.
 */
public class InviteActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;
    private static final int PLACE_PICKER_REQUEST = 1;
    public static final String ADD_PLACE = "/android/addPlace";
    public static final String IS_EMPTY = "isEmpty";

    private Invite invite;
    private View showCentroidButton;
    private Button navigateToDestButton;
    private View declineInviteButton;
    private View acceptInviteButton;
    private View inviteHeader;
    private ImageView transportationModeImage;
    private SwipeRefreshLayout swipeLayout;


    //todo string for placeToMeet shit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);

        //getting the current invite, by Id that was passed in this activity via putExtra(String)
        invite = InviteHandler.getInstance().getInviteByTime(Long.parseLong(getIntent().getStringExtra("InviteId")));
        Log.d("Input Intent:",getIntent().getStringExtra("InviteId"));

        //setting up needed Views (Buttons etc.)
        declineInviteButton      = findViewById(R.id.declineInviteButton);
        acceptInviteButton       = findViewById(R.id.acceptInviteButton);
        inviteHeader             = findViewById(R.id.inviteHeader);
        showCentroidButton       = findViewById(R.id.showCentroidButton);
        navigateToDestButton     = (Button) findViewById(R.id.navigateToButton);
        transportationModeImage  = (ImageView) findViewById(R.id.transportationModeImage);



    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView _inviteStatus1 = (TextView)  findViewById(R.id.InviteStatusText1);
        TextView _inviteStatus2 = (TextView)  findViewById(R.id.InviteStatusText2);
        _inviteStatus1.setText(Util.getInstance().getDate(invite.getStartTime()));
        _inviteStatus2.setText(invite.getStatus().toString());

        //extracting members names from this invite
        final TreeMap<String, InviteStatus> _memberMap = new TreeMap<>(invite.getAllMembers(false, true));
        Log.d("members", invite.getAllMembers(false, true).toString());
        //filling the ListView with Members of this invite via ArrayAdapter
        final ListView _listView = (ListView) findViewById(R.id.memberListView);
        final MemberStatusHashMapArrayAdapter _adapter = new MemberStatusHashMapArrayAdapter(this,
                R.layout.member_list_item, new ArrayList(_memberMap.entrySet()));
//        _adapter.setCheckList(_memberMap.size());

        _listView.setAdapter(_adapter);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                try {
                    // update state of the views
                    _adapter.notifyDataSetChanged();

                    TextView _memberIdTVClicked = (TextView) view.findViewById(R.id.memberID);
                    //TODO draengeln(_memberIdTVClicked.getText().toString());
                    // _memberIdTVClicked.getText().toString();

                } catch (Exception e) {
                    Log.v("Exception ON Click", e.getMessage(), e);
                }

            }
        });
        //makes buttons visible if invite exits and chooses correct Image
        if (invite.getStatus() != InviteReply.UNANSWERED) {
            //sets the transportationMode ImageView to the corresponding Image
            //TODO nice images with variable Resolutions
            transportationModeImage.setImageResource(Util.getInstance().getResIdForTransportationImage(invite.getTransportationMode()));

            //TODO entferne DEBUG Block
            //only needed for refresh ---  DEBUG
            if(invite.getStatus() == InviteReply.DECLINED){
                invite.setTransportationMode(TransportationMode.DECLINED);
            }
            // --- DEBUG-END

            declineInviteButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.GONE);
            inviteHeader.setVisibility(View.VISIBLE);
        }
        //checks if centroid already exists
        if (invite.existsCentroid()){
            showCentroidButton.setEnabled(true);
            navigateToDestButton.setEnabled(true);
        }
        if(invite.getChosenPlace() != null) {
            navigateToDestButton.setText(R.string.navigate_toPlace);
            TextView _placesTextView = (TextView) findViewById(R.id.placesTextView);
            _placesTextView.setVisibility(View.VISIBLE);
            _placesTextView.setText(toDisplayContent(toReadableContent(invite.getChosenPlace())));
        }

        try {
            this.registerReceiver(broadcastReceiver, new IntentFilter(MyGcmListenerService.BROADCAST_UPDATE));
        } catch (Exception e) {
            e.printStackTrace();
        }



    }




    public void showCentroidOnMap(View view) {
        if(invite.getInviteNumber().equals(PersistenceHandler.getInstance().getOwnNumber())) {
            LatLng _southWest = new LatLng(InviteHandler.getInstance().getInviteByTime(invite.getStartTime()).getCentroid().getLat()-0.003
                    , InviteHandler.getInstance().getInviteByTime(invite.getStartTime()).getCentroid().getLongitude() - 0.003);
            LatLng _northEast = new LatLng(InviteHandler.getInstance().getInviteByTime(invite.getStartTime()).getCentroid().getLat() + 0.003
                    , InviteHandler.getInstance().getInviteByTime(invite.getStartTime()).getCentroid().getLongitude() + 0.003);
            LatLngBounds _latLngBounds = new LatLngBounds(_southWest, _northEast);
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            builder.setLatLngBounds(_latLngBounds);
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        } else {
        Intent intent = new Intent(this, GoogleMapActivity.class);
        intent.putExtra("centroid", new LatLng(
                InviteHandler.getInstance().getInviteByTime(invite.getStartTime()).getCentroid().getLat()
                , InviteHandler.getInstance().getInviteByTime(invite.getStartTime()).getCentroid().getLongitude()));
        startActivity(intent);
        }
    }


    public void navigateToDestination(View view){
        if(invite.getChosenPlace() != null){
            navigateToCentroid(view);
        }else{
            navigateToPlace(view);
        }
    }

    public void navigateToCentroid(View view) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="
                + String.valueOf(InviteHandler.getInstance().getInviteByTime(invite.getStartTime())
                    .getCentroid().getLat()) + ","
                + String.valueOf(InviteHandler.getInstance().getInviteByTime(invite.getStartTime())
                    .getCentroid().getLongitude())
                + "&mode=" + invite.getTransportationMode().getMode());

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    public void navigateToPlace(View view) {
        String [] _placeInfo = toReadableContent(invite.getChosenPlace()).split(",");

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + _placeInfo[_placeInfo.length-2]
                + "," + _placeInfo[_placeInfo.length-1] + "&mode=" + invite.getTransportationMode().getMode());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            invite.setPlaceToMeet(PlacePicker.getPlace(data, this));
            displayPlace(PlacePicker.getPlace(data, this));
        }
    }

    private boolean displayPlace(Place place) {
        if(place == null) {
            return false;
        }
        String content = "";
        if (!TextUtils.isEmpty(place.getName())) {
            content += place.getName().toString().replaceAll(",", " ") + ",";
        } else {
            content += IS_EMPTY + ",";
        }
        if (!TextUtils.isEmpty(place.getAddress())) {
            content += place.getAddress().toString().replaceAll(",", " ") + ",";
        } else {
            content += IS_EMPTY + ",";
        }
        if (!TextUtils.isEmpty(place.getPhoneNumber())) {
            content += place.getPhoneNumber().toString().replaceAll(",", " ") + ",";
        } else {
            content += IS_EMPTY + ",";
        }
        content += place.getLatLng();
        TextView _placesTextView = (TextView) findViewById(R.id.placesTextView);
        _placesTextView.setVisibility(View.VISIBLE);
        _placesTextView.setText(content);

        content = transportReady(content);

        InviteHandler.getInstance().setChosenPlace(content, invite);
        new RestConnector(this).execute(RestConnector.POST, ADD_PLACE + "/" + invite.getStartTime()
                + "/" + content);
        return true;
    }

    private String transportReady(String content) {
        String _content;
        _content = content.replaceAll("\\(", ",");
        _content = _content.replaceAll("\\)", "");
        try {
            _content = URLEncoder.encode(_content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        _content = _content.replaceAll("%", "rvxy");

        return _content;
    }

    private String toReadableContent(String content) {
        Log.d("XXXX", "place in toReadable start: " + content);

        content = content.replaceAll("rvxy","%");
        try {
            content = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("XXXX", "place in toReadable after decode: " + content);
        return content;
    }

    private String toDisplayContent(String content) {
        List<String> _contentList = Arrays.asList(content.split(","));
        String _content = "Your chosen location: \n";
        _content += "Name: " + _contentList.get(0) + "\n";

        _content += "Address: " + _contentList.get(1) + "\n";

        _content += "Phone: " + _contentList.get(2) + "\n";
        return _content;
    }

    //is called when accept or decline button is pressed
    public void responseToInvite(View view) {
        switch (view.getId()) {
            case R.id.acceptInviteButton:
                if (GpsDataHandler.getInstance().getLastLocation() == null) {
                    Toast.makeText(this, "please activate gps first", Toast.LENGTH_SHORT).show();
                    return;
                }
                InviteHandler.getInstance().responseToInvite(invite.getStartTime(), InviteReply.ACCEPTED, invite.getTransportationMode(), this);
                Toast.makeText(this, "Invite accepted", Toast.LENGTH_SHORT).show();
                break;
            case R.id.declineInviteButton:
                invite.setTransportationMode(TransportationMode.DECLINED);
                Log.d("DeclineButton",invite.getTransportationMode().toString());
                InviteHandler.getInstance().responseToInvite(invite.getStartTime(), InviteReply.DECLINED, invite.getTransportationMode(), this);
                Toast.makeText(this, "Invite declined", Toast.LENGTH_SHORT).show();
                onBackPressed();
                break;
        }
        declineInviteButton.setVisibility(View.GONE);
        acceptInviteButton.setVisibility(View.GONE);
        onResume();
    }

    public void chooseTransportationMode(final View _view){
        CharSequence transportationModes[] = getResources().getStringArray(R.array.transportation_modes);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a transportation Mode");
        builder.setItems(transportationModes, new DialogInterface.OnClickListener() {
            boolean _hasChosen = false;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on transportationModes[which]
                switch (which) {
                    case 0:
                        invite.setTransportationMode(TransportationMode.FOOT);
                        _hasChosen = true;
                        break;
                    case 1:
                        invite.setTransportationMode(TransportationMode.BIKE);
                        _hasChosen = true;
                        break;
                    case 2:
                        invite.setTransportationMode(TransportationMode.CAR);
                        _hasChosen = true;
                        break;
                    case 3:
                        invite.setTransportationMode(TransportationMode.PUBLIC);
                        _hasChosen = true;
                        break;
                }
                if (_hasChosen) {
                    responseToInvite(_view);
                }
            }
        });
        builder.show();
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
            //todo
            new RestConnector(this).execute(RestConnector.SYNC, "/android/updateInvite/" + invite.getStartTime());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        new RestConnector(this).execute(RestConnector.SYNC, "/android/updateInvite/" + invite.getStartTime());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

    //Broadcast handler
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //calls onCreate to update the view
            onResume();
        }
    };
}
