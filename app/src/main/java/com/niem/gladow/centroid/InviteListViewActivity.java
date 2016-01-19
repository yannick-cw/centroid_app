package com.niem.gladow.centroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by clem on 11.11.15.
 */
public class InviteListViewActivity extends Activity {
    private ListView _listView;
    private TreeMap<Long, Invite> _sortedMap;
    private InviteHashMapArrayAdapter _adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_listview_activity);
        _listView = (ListView) findViewById(R.id.listView);
        //sorts the map in descending order
        _sortedMap = new TreeMap<>(Collections.reverseOrder());
        _sortedMap.putAll(InviteHandler.getInstance().getActiveInvites());

        _adapter = new InviteHashMapArrayAdapter(this,
                R.layout.invite_list_view_item, new ArrayList(_sortedMap.entrySet()));

        _listView.setAdapter(_adapter);

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

    }

    @Override
    protected void onResume(){
        super.onResume();
        _sortedMap.putAll(InviteHandler.getInstance().getActiveInvites());
        _adapter.notifyDataSetChanged();
        _listView.invalidateViews();
    }

    public void startInviteActivity(String inviteId){
        Log.d("Intent for", "INVITE pressed");
        Intent intent = new Intent(this, InviteActivity.class);
        intent.putExtra("InviteId", inviteId);
        startActivity(intent);
    }

}