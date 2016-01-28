package com.niem.gladow.centroid;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
 */
public class CentroidListViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String INVITE_ID = "InviteId";
    public static final String UPDATE_ALL_INVITES_URI = "/android/updateAllInvites/";
    private ListView _listView;
    private TreeMap<Long, Invite> _sortedMap;
    private CentroidListHashMapArrayAdapter _adapter;
    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.centroid_listview_activity);
        Toolbar toolbar = (Toolbar)    findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);

        initializePlusButton();

        _listView = (ListView) findViewById(R.id.friendsListView);

        checkForGooglePlayServices();


    }

    private void checkForGooglePlayServices() {
        //check if right google play service is available
        Integer resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            //Do what you want
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                dialog.show();
            }
        }
    }

    private void initializePlusButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.createNewCentroidButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CentroidListViewActivity.this, InviteFriendsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sorts the map in descending order
        _sortedMap = new TreeMap<>(Collections.reverseOrder());
        try {
            _sortedMap.putAll(InviteHandler.getInstance().getActiveInvites());
        } catch (Exception e) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        _adapter = new CentroidListHashMapArrayAdapter(this,
                R.layout.centroid_list_view_item, new ArrayList(_sortedMap.entrySet()));
        _listView.setAdapter(_adapter);
        _listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CentroidListViewActivity.this.onTouchEvent(event);
                return false;
            }
        });

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                try {
                    // update state of the views
                    _adapter.notifyDataSetChanged();

                    TextView _textViewClicked = (TextView) view.findViewById(R.id.centroidListItemInviteId);
                    TextView _textViewClicked1 = (TextView) view.findViewById(R.id.centroidListItemStatus);
                    Log.d("TextView.getText:", _textViewClicked.getText().toString()); //start_time/invite_id
                    Log.d("TextView1.getText:", _textViewClicked1.getText().toString());

                    startInviteActivity(_textViewClicked.getText().toString());

                } catch (Exception e) {
                    Log.v("Exception ON Click", e.getMessage(), e);
                }

            }
        });

        _sortedMap.putAll(InviteHandler.getInstance().getActiveInvites());
        _adapter.notifyDataSetChanged();
        _listView.invalidateViews();

        try {
            this.registerReceiver(broadcastReceiver, new IntentFilter(MyGcmListenerService.BROADCAST_UPDATE));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void startInviteActivity(String inviteId) {
        Log.d("Intent for", "INVITE pressed");
        Intent intent = new Intent(this, InviteActivity.class);
        intent.putExtra(INVITE_ID, inviteId);
        startActivity(intent);
    }

    //Broadcast handler
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //calls onResume to update the view
            onResume();
        }
    };


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
            new RestConnector(this).execute(RestConnector.SYNC_ALL_INVITES,
                    UPDATE_ALL_INVITES_URI + PersistenceHandler.getInstance().getOwnNumber() + "/"
                            + InviteHandler.getInstance().getActiveInvitesString());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new RestConnector(this).execute(RestConnector.SYNC_ALL_INVITES,
                UPDATE_ALL_INVITES_URI + PersistenceHandler.getInstance().getOwnNumber() + "/"
                        + InviteHandler.getInstance().getActiveInvitesString());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }
}