package com.niem.gladow.centroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by clem on 11.11.15.
 */
public class ListViewActivity extends Activity {

    public void inviteFriends(View view) {
        Log.d("inviteFriends", "pressed");
        new NumberLogicHandler(this).inviteFriends();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_activity);

        final ListView _listView = (ListView) findViewById(R.id.listview);

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