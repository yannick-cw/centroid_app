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
    private static String ownNumber = "";
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

//    public boolean inputOwnNumber(){
//
//    }

    public boolean sendOwnNumber() {
        //das hier ist glaube ich noch nicht gut so, sollte nicht einfach eine Methode im Async aufrufen
        Log.d("sendOwnNumber","else");
        ownNumber = phoneDataHandler.getOwnNumber();
        if (ownNumber.equals("/")){
            Toast.makeText(context,"Please enter your Number and try again",Toast.LENGTH_LONG).show();

            return false;
        }else{
            //starts async task RestConnector to send ownNumber to server
            new RestConnector(context).execute(POST, SEND_NUMBER + ownNumber);
            return true;
        }
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

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }


}
