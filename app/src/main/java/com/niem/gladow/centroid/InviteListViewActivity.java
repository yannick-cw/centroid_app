package com.niem.gladow.centroid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

/**
 * Created by clem on 11.11.15.
 */
public class InviteListViewActivity extends AppCompatActivity {
    private ListView _listView;
    private TreeMap<Long, Invite> _sortedMap;
    private InviteHashMapArrayAdapter _adapter;
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_listview_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        _listView = (ListView) findViewById(R.id.listView);

        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
    }

    @Override
    protected void onResume(){
        super.onResume();
        //sorts the map in descending order
        _sortedMap = new TreeMap<>(Collections.reverseOrder());
        _sortedMap.putAll(InviteHandler.getInstance().getActiveInvites());

        _adapter = new InviteHashMapArrayAdapter(this,
                R.layout.invite_list_view_item, new ArrayList(_sortedMap.entrySet()));
        _listView.setAdapter(_adapter);
        _listView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InviteListViewActivity.this.onTouchEvent(event);
                return false;
            }
        });

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                try {
                    // update state of the views
                    _adapter.notifyDataSetChanged();

                    TextView _textViewClicked = (TextView) view.findViewById(R.id.invite_id);
                    TextView _textViewClicked1 = (TextView) view.findViewById(R.id.invite_friend);
                    Log.d("Textview.gettext:", _textViewClicked.getText().toString()); //start_time/invite_id
                    Log.d("Textview1.gettext:", _textViewClicked1.getText().toString());

                    startInviteActivity(_textViewClicked.getText().toString());

                } catch (Exception e) {
                    Log.v("Exception ON Click", e.getMessage(), e);
                }

            }
        });

        _sortedMap.putAll(InviteHandler.getInstance().getActiveInvites());
        _adapter.notifyDataSetChanged();
        _listView.invalidateViews();
        this.registerReceiver(broadcastReceiver, new IntentFilter(MyGcmListenerService.BROADCAST_UPDATE));

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }

    public void startInviteActivity(String inviteId){
        Log.d("Intent for", "INVITE pressed");
        Intent intent = new Intent(this, InviteActivity.class);
        intent.putExtra("InviteId", inviteId);
        startActivity(intent);
    }

    //Broadcast handler
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //calls onCreate to update the view
            onResume();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe right' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if(event2.getX() > event1.getX() && event2.getX() - event1.getX() > 200){
                //switch another activity
                Intent intent = new Intent(InviteListViewActivity.this, InviteFriendsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
            }

            return true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(InviteListViewActivity.this, MainActivity.class);
        startActivity(intent);
    }
}