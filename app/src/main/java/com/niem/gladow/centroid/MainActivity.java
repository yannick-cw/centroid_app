package com.niem.gladow.centroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //Button creates logic handler and send own number to server
    public void sendOwnNumber(View view) {
        Log.d("sendButton", "pressed");
        new LogicHandler(this).sendOwnNumber();

    }

    //Button creates logic handler and starts async task which gets own numbers from contacts and sends them to server
    public void sendContacts(View view) {
        Log.d("contactButton", "pressed");
        new LogicHandler(this).executePhoneDataHandler();
    }

    //creates logic handler and sends friends numbers to server
    public void inviteFriends(View view) throws Exception {
        Log.d("inviteFriends", "pressed");
        new LogicHandler(this).inviteFriends();

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
