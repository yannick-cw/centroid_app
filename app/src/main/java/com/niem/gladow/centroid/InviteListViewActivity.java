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
import java.util.Map;

/**
 * Created by clem on 11.11.15.
 */
public class InviteListViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_listview_activity);

        final ListView _listView = (ListView) findViewById(R.id.listView);

        final Map<Long, Invite> _map = InviteHandler.getActiveInvites();

        final InviteHashMapArrayAdapter _adapter = new InviteHashMapArrayAdapter(this,
                R.layout.invite_list_view_item, new ArrayList(_map.entrySet()));
        _listView.setAdapter(_adapter);

        _listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                try {
//                    // update state of the views
//                    _adapter.toggleCheckList(position);
//                    _adapter.notifyDataSetChanged();

                    TextView _textViewClicked = (TextView) view.findViewById(R.id.invite_id);
                    TextView _textViewClicked1 = (TextView) view.findViewById(R.id.invite_friend);
                    Log.d("Textview.gettext:",_textViewClicked.getText().toString()); //start_time/invite_id
                    Log.d("Textview1.gettext:",_textViewClicked1.getText().toString());

                    startInviteActivity(_textViewClicked.getText().toString());



//                    // check state of view and add/remove it from inviteList
//                    if (_adapter.getCheckStatus(position)) {
//                        Log.d("Name", _textViewClicked.getText().toString());
//                        PersistenceHandler.getInstance().addToInviteList(_textViewClicked.getText().toString());
//                    } else {
//                        /* TODO would be more elegant to check ListView after pressing send button
//                           and only add selected items to inviteList */
//                        PersistenceHandler.getInstance().removeFromInviteList(_textViewClicked.getText().toString());
//                    }
                } catch (Exception e) {
                    Log.v("Exception ON Click", e.getMessage(), e);
                }

            }
        });
    }

    public void startInviteActivity(String inviteId){
        Log.d("Intent for", "INVITE pressed");
        Intent intent = new Intent(this, InviteActivity.class);
        intent.putExtra("InviteId", inviteId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed (){
        //TODO check if this can be made more elegantly (Maybe with onDestroy?)
        //PersistenceHandler.getInstance().clearInviteList(); //cleanup of inviteList
        super.onBackPressed();
    }
}