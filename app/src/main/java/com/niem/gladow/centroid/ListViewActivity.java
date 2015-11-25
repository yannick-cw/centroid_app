package com.niem.gladow.centroid;

import android.app.Activity;
import android.graphics.Color;
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

        final ListView listview = (ListView) findViewById(R.id.listview);

        final Map<String, String> map = PersistenceHandler.getInstance().getFriendMap();

        final HashMapArrayAdapter adapter = new HashMapArrayAdapter (this,
                R.layout.list_view_item, new ArrayList(map.entrySet()) );

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {

                try {
                    TextView clickedItem = (TextView) view.findViewById(R.id.friend_number);
                    // Workaround to save state in alpha-value because views are stateless
                    if(view.getAlpha() == 1){
                        view.setBackgroundColor(Color.GREEN);
                        Log.d("Name",clickedItem.getText().toString());
                        PersistenceHandler.getInstance().addToInviteList(clickedItem.getText().toString());
                        view.setAlpha(0.9f);
                    }else{
                        view.setAlpha(1);
                        view.setBackgroundColor(Color.RED);
                        /* TODO would be more elegant to check ListView after pressing send button
                           and only add selected items to inviteList */
                        PersistenceHandler.getInstance().removeFromInviteList(clickedItem.getText().toString());
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.v("Exception ON Click", e.getMessage(), e);
                }

            }
        });
    }
}