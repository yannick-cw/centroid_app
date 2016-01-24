package com.niem.gladow.centroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by clem on 11.11.15.
 */
public class InviteFriendsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 13;
    private SwipeRefreshLayout swipeLayout;

//todo remove toast for created centroid and just show
    //todo update on new data missing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_friends_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);

        final ListView _listView = (ListView) findViewById(R.id.listView);
        _listView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InviteFriendsActivity.this.onTouchEvent(event);
                return false;
            }
        });
        final Button _inviteFriendsButton = (Button) findViewById(R.id.showCentroidsButton);

        final Map<String, String> _map = PersistenceHandler.getInstance().getFriendMap();

        final InviteFriendsHashMapArrayAdapter _adapter = new InviteFriendsHashMapArrayAdapter(this,
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

    }

    public void inviteFriends(View view) {
        Log.d("inviteFriends", "pressed");
        chooseTransportationMode(this);
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
                if (_hasChosen) {
                    if( new NumberLogicHandler(_context).inviteFriends(_transportationMode)) {
                        onBackPressed();
                    }
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
            //todo here is no check for permission, as in main
            new NumberLogicHandler(this).executePhoneDataHandler();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRefresh() {
        new NumberLogicHandler(this).executePhoneDataHandler();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

}