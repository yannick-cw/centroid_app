package com.niem.gladow.centroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity{

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
        new NumberLogicHandler(this).sendOwnNumber();

    }

    //Button creates logic handler and starts async task which gets own numbers from contacts and sends them to server
    public void sendContacts(View view) {
        Log.d("contactButton", "pressed");
        new NumberLogicHandler(this).executePhoneDataHandler();
    }

    //creates logic handler and sends friends numbers to server
    public void inviteFriends(View view) throws Exception {
        Log.d("inviteFriends", "pressed");
        new NumberLogicHandler(this).inviteFriends();
    }

    public void sendGps(View view) {
        Log.d("sendOwnGps", "pressed");
        new GpsDataHandler(this);
    }

    public void seeList(View view){
        Log.d("pressed","seelist");
        Intent intent = new Intent(this, ListViewActivity.class);
        startActivity(intent);
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
