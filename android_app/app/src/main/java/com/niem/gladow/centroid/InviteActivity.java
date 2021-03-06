package com.niem.gladow.centroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public class InviteActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String ADD_PLACE = "/android/addPlace";
    private static final String IS_EMPTY = "isEmpty";
    public static final String CENTROID = "centroid";
    public static final String LOCATION = "location";
    public static final String NAME = "name";
    private static final String draengeld = "you draengeld ";
    private static final String DRAENGEL_URI = "/android/draengel/";
    private static final String HAS_ALREADY_REPLIED = " has already replied";
    public static final String PLEASE_GPS = "please activate your gps first";
    private static final String INVITE_ACCEPTED = "invite accepted";
    private static final String INVITE_DECLINED = "invite declined";
    private static final String DECLINE_SURE = "Are you sure you want to decline?";
    public static final String PICK_TRANSPORT = "Pick a transportation Mode";
    public static final String UPDATE_INVITE_URI = "/android/updateInvite/";

    private Invite invite;
    private Button showCentroidButton;
    private Button navigateToDestButton;
    private Button declineInviteButton;
    private Button acceptInviteButton;
    private LinearLayout buttonBox, header;
    private ImageView transportationModeImage;
    private SwipeRefreshLayout swipeLayout;
    private TextView invitePhoneNumber, inviteTime, inviteLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getting the current invite, by Id that was passed in this activity via putExtra(String)
        try {
            invite = InviteHandler.getInstance().getInviteByTime(Long.parseLong(getIntent().getStringExtra(CentroidListViewActivity.INVITE_ID)));
        } catch (NumberFormatException e) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return;
        }
        Log.d("Input Intent:", getIntent().getStringExtra(CentroidListViewActivity.INVITE_ID));

        //setting up needed Views (Buttons etc.)
        Typeface _typeFace = Typeface.createFromAsset(getAssets(), "fonts/VeraBd.ttf");
        header = (LinearLayout) findViewById(R.id.header);
        inviteTime = (TextView) findViewById(R.id.inviteTime);
        inviteTime.setTypeface(_typeFace);
        _typeFace = Typeface.createFromAsset(getAssets(), "fonts/Vera.ttf");
        invitePhoneNumber = (TextView) findViewById(R.id.invitePhoneNumber);
        invitePhoneNumber.setTypeface(_typeFace);
        inviteLocation = (TextView) findViewById(R.id.inviteLocation);
        inviteLocation.setTypeface(_typeFace);
        transportationModeImage = (ImageView) findViewById(R.id.transportationModeImage);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);

        _typeFace = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.button_font));
        buttonBox = (LinearLayout) findViewById(R.id.buttonLayout);
        declineInviteButton = (Button) findViewById(R.id.declineInviteButton);
        declineInviteButton.setTypeface(_typeFace);
        declineInviteButton.getBackground().setColorFilter(Util.getInstance().getButtonColor(this), PorterDuff.Mode.MULTIPLY);

        acceptInviteButton = (Button) findViewById(R.id.acceptInviteButton);
        acceptInviteButton.setTypeface(_typeFace);
        acceptInviteButton.getBackground().setColorFilter(Util.getInstance().getButtonColor(this), PorterDuff.Mode.MULTIPLY);

        showCentroidButton = (Button) findViewById(R.id.showCentroidButton);
        showCentroidButton.setTypeface(_typeFace);
        showCentroidButton.getBackground().setColorFilter(Util.getInstance().getButtonColor(this), PorterDuff.Mode.MULTIPLY);

        navigateToDestButton = (Button) findViewById(R.id.navigateToButton);
        navigateToDestButton.setTypeface(_typeFace);
        navigateToDestButton.getBackground().setColorFilter(Util.getInstance().getButtonColor(this), PorterDuff.Mode.MULTIPLY);

    }

    @Override
    protected void onResume() {
        super.onResume();

        inviteTime.setText(Util.getInstance().getDate(invite.getStartTime()));
        invitePhoneNumber.setText(invite.getStatus().toString());

        //extracting members names from this invite
        final TreeMap<String, InviteStatus> _memberMap = new TreeMap<>(invite.getAllMembers(Invite.WITHOUT, Invite.WITH));
        Log.d("members", invite.getAllMembers(Invite.WITHOUT, Invite.WITH).toString());
        //filling the ListView with Members of this invite via ArrayAdapter
        final ListView _listView = (ListView) findViewById(R.id.memberListView);
        final MemberStatusHashMapArrayAdapter _adapter = new MemberStatusHashMapArrayAdapter(this,
                R.layout.member_list_item, new ArrayList(_memberMap.entrySet()));

        _listView.setAdapter(_adapter);
        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                try {
                    // update state of the views
                    _adapter.notifyDataSetChanged();

                    TextView _memberIdTVClicked = (TextView) view.findViewById(R.id.memberID);
                    TextView _memberName = (TextView) view.findViewById(R.id.memberName);
                    ImageView _memberStatus = (ImageView) view.findViewById(R.id.memberListStatusImage);
                    if (_memberStatus.getTag() == TransportationMode.DEFAULT) {
                        new RestConnector(InviteActivity.this).execute(RestConnector.GET_NO_RESULT, DRAENGEL_URI
                                + PersistenceHandler.getInstance().getOwnNumber() + "/"
                                + _memberIdTVClicked.getText().toString());
                        Snackbar.make(view, draengeld + _memberName.getText().toString().split(" ")[0], Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, _memberName.getText().toString().split(" ")[0] + HAS_ALREADY_REPLIED, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } catch (Exception e) {
                    Log.v("Exception ON Draengeln", e.getMessage(), e);
                }

            }
        });
        //makes buttons visible if invite exits and chooses correct Image
        if (invite.getStatus() != InviteReply.UNANSWERED) {
            //sets the transportationMode ImageView to the corresponding Image
            header.setBackground(getBackgroundResForStatus());
            transportationModeImage.setImageResource(Util.getInstance().getResIdForTransportationImage(invite.getTransportationMode()));
            declineInviteButton.setVisibility(View.GONE);
            acceptInviteButton.setVisibility(View.GONE);
            navigateToDestButton.setVisibility(View.VISIBLE);
            showCentroidButton.setVisibility(View.VISIBLE);
            buttonBox.setOrientation(LinearLayout.VERTICAL);
        }


        //checks if centroid already exists
        if (invite.existsCentroid()) {
            if(invite.getStatus() == InviteReply.DECLINED){
                inviteLocation.setText(R.string.declined_centroid_exists);
            }else{
                if(invite.getInviteNumber().matches(PersistenceHandler.getInstance().getOwnNumber())){
                    showCentroidButton.setText(R.string.choose_location);
                    inviteLocation.setText(R.string.default_centroid_available_host);
                }else{
                    inviteLocation.setText(R.string.default_centroid_available_guest);
                }
            }
            showCentroidButton.setEnabled(true);
            navigateToDestButton.setEnabled(true);
        }else {
            if (invite.getStatus() == InviteReply.DECLINED) {
                if(invite.getInviteNumber().matches(PersistenceHandler.getInstance().getOwnNumber())){
                    inviteLocation.setText(R.string.everyone_declined);
                }else{
                    inviteLocation.setText(R.string.pending_you_declined);

                }
            }
        }

        //checks if location already exists
        if (invite.getChosenPlace() != null) {
            navigateToDestButton.setText(R.string.navigate_toPlace);
            inviteLocation.setText(invite.getLocationName() + "\n@" +
                    invite.getLocationAdress());
            if (!invite.getLocationPhoneNumber().matches("isEmpty")) {
                invitePhoneNumber.setText(invite.getLocationPhoneNumber());
                invitePhoneNumber.setVisibility(View.VISIBLE);
            }

        }
        try {
            this.registerReceiver(broadcastReceiver, new IntentFilter(MyGcmListenerService.BROADCAST_UPDATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable getBackgroundResForStatus() {
        if (invite.existsCentroid()) {
            return ContextCompat.getDrawable(this, R.drawable.border_ready);
        }
        switch (invite.getStatus()) {
            case ACCEPTED:
                return ContextCompat.getDrawable(this, R.drawable.border_accepted);
            case DECLINED:
                return ContextCompat.getDrawable(this, R.drawable.border_declined);
            case UNANSWERED:
                return ContextCompat.getDrawable(this, R.drawable.border_unanswered);
            default:
                return ContextCompat.getDrawable(this, R.drawable.border_unanswered);

        }
    }

    public void showCentroidOnMap(View view) {
        //if the host calls showCentroid, placePicker is loaded
        if (invite.getInviteNumber().equals(PersistenceHandler.getInstance().getOwnNumber())) {
            double _distanceFromCenter = 0.003;
            double lat = invite.getCentroid().getLat();
            double longitude = invite.getCentroid().getLongitude();

            //creates the edges of the map
            LatLng _southWest = new LatLng(lat - _distanceFromCenter
                    , longitude - _distanceFromCenter);
            LatLng _northEast = new LatLng(lat + _distanceFromCenter
                    , longitude + _distanceFromCenter);

            LatLngBounds _latLngBounds = new LatLngBounds(_southWest, _northEast);

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            builder.setLatLngBounds(_latLngBounds);

            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            //if a member calls showCentroid the custom map is loaded
        } else {
            Intent intent = new Intent(this, GoogleMapActivity.class);
            intent.putExtra(CENTROID, new LatLng(invite.getCentroid().getLat()
                    , invite.getCentroid().getLongitude()));

            //if there is already a place picked, add it to the map
            if (invite.getChosenPlace() != null) {
                intent.putExtra(LOCATION, new LatLng(Double.valueOf(invite.getLocationLatitude()), Double.valueOf(invite.getLocationLongitude())));
                intent.putExtra(LOCATION + NAME, invite.getLocationName());
            }
            startActivity(intent);
        }
    }


    //navigates either to the place or to the centroid itself
    public void navigateToDestination(View view) {
        if (invite.getChosenPlace() == null) {
            String lat = String.valueOf(invite.getCentroid().getLat());
            String _longitude = String.valueOf(invite.getCentroid().getLongitude());
            navigateTo(lat, _longitude);
        } else {
            navigateTo(invite.getLocationLatitude(), invite.getLocationLongitude());
        }
    }

    //starts google maps for the given target and the current transportation mode
    private void navigateTo(String lat, String _longitude) {
        Uri _gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + _longitude
                + "&mode=" + invite.getTransportationMode().getMode());

        Intent _mapIntent = new Intent(Intent.ACTION_VIEW, _gmmIntentUri);
        _mapIntent.setPackage("com.google.android.apps.maps");
        if (_mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(_mapIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            displayPlace(PlacePicker.getPlace(data, this));
        }
    }

    private void displayPlace(Place place) {
        if (place == null) {
            return;
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
        TextView _placesTextView = (TextView) findViewById(R.id.inviteLocation);
        _placesTextView.setVisibility(View.VISIBLE);
        _placesTextView.setText(content);

        content = transportReady(content);

        InviteHandler.getInstance().setChosenPlace(content, invite);
        new RestConnector(this).execute(RestConnector.POST_NO_RESULT, ADD_PLACE + "/" + invite.getStartTime()
                + "/" + content);
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

    //is called when accept or decline button is pressed
    private void responseToInvite(View view) {
        switch (view.getId()) {
            case R.id.acceptInviteButton:
                if (GpsDataHandler.getInstance().getLastLocation() == null) {
                    Snackbar.make(view, PLEASE_GPS, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                InviteHandler.getInstance().responseToInvite(invite.getStartTime(), InviteReply.ACCEPTED, invite.getTransportationMode(), this);
                Snackbar.make(view, INVITE_ACCEPTED, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.declineInviteButton:
                invite.setTransportationMode(TransportationMode.DECLINED);
                Log.d("DeclineButton", invite.getTransportationMode().toString());
                InviteHandler.getInstance().responseToInvite(invite.getStartTime(), InviteReply.DECLINED, invite.getTransportationMode(), this);
                Snackbar.make(view, INVITE_DECLINED, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
        declineInviteButton.setVisibility(View.GONE);
        acceptInviteButton.setVisibility(View.GONE);
        onResume();
    }

    public void areYouSureToDeclineDialogue(final View _view) {
        CharSequence transportationModes[] = getResources().getStringArray(R.array.decline_answers);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(DECLINE_SURE);
        builder.setItems(transportationModes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on transportationModes[which]
                switch (which) {
                    case 1:
                        responseToInvite(_view);
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }


    public void chooseTransportationMode(final View _view) {
        CharSequence transportationModes[] = getResources().getStringArray(R.array.transportation_modes);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(PICK_TRANSPORT);
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
            new RestConnector(this).execute(RestConnector.SYNC_INVITE, UPDATE_INVITE_URI + invite.getStartTime());
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
        new RestConnector(this).execute(RestConnector.SYNC_INVITE, UPDATE_INVITE_URI + invite.getStartTime());
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
