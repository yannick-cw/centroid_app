package com.niem.gladow.centroid;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class NumberLogicHandler implements AsyncResponse {
    private static final String POST = "1", GET = "2", SEND = "3";
    private static final String SEND_NUMBER = "/android/registerNumber/",
            SEND_CONTACTS = "/android/checkNumbers/", INVITE_FRIENDS = "/android/inviteFriends/";
    private static String ownNumber = PersistenceHandler.getOwnNumber();
    PhoneDataHandler phoneDataHandler;


    private Context context;

    public NumberLogicHandler(Context context) {
        this.context = context;
        phoneDataHandler = new PhoneDataHandler(context);
        //puts himself in the phoneDataHandler delegate object, for returning results
        phoneDataHandler.delegate = this;
        //mEdit.setImeActionLabel("Send", KeyEvent.KEYCODE_ENTER);
    }

    public void executePhoneDataHandler() {
        //starts phoneDataHandler async task without parameters
        phoneDataHandler.execute("");
    }

    private boolean sendContacts(String contacts) {
        //starts RestConnector async task to send contacts to server
        new RestConnector(context).execute(SEND, SEND_CONTACTS + ownNumber + contacts);
        return true;
    }

    public void syncTokenAndNumber () {
        new RestConnector(context).execute(POST, SEND_NUMBER + ownNumber + PersistenceHandler.getInstance().getToken());
    }

    //reads friendlist from file and starts async task RestConnector to send friend numbers to server
    public boolean inviteFriends() {

        new RestConnector(context).execute(GET, INVITE_FRIENDS + ownNumber + PersistenceHandler.getInstance().getInviteList());
        return true;
    }

    //gets called by PhoneDataHandler async tasks, when it is finished
    public void processFinish(String output) {
        //sends contacts to server after cleanup
        sendContacts(output);
    }

}
