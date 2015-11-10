package com.niem.gladow.centroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String POST = "1", GET = "2";
    private static final String SERVER_ADDRESS ="http://10.181.26.131:8080", SEND_NUMBER = "/android/registerNumber/",
            SEND_CONTACTS = "/android/checkNumbers/", INVITE_FRIENDS = "/android/inviteFriends/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void sendOwnNumber(View view) {
        Log.d("sendButton", "pressed");
        new RestConnector(this).execute(POST, SERVER_ADDRESS + SEND_NUMBER + "11");

    }

    public void sendContacts(View view) {
        Log.d("contactButton", "pressed");
        new RestConnector(this).execute(GET, SERVER_ADDRESS + SEND_CONTACTS + "1/11,12");

    }

    public void inviteFriends(View view) {
        Log.d("inviteFriends", "pressed");
        //new RestConnector(this).execute(GET, SERVER_ADDRESS + INVITE_FRIENDS + "1/11");
        PhoneDataHandler phoneDataHandler = new PhoneDataHandler(this);
        phoneDataHandler.execute("");

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
