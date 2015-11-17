package com.niem.gladow.centroid;

import android.content.Context;
import android.util.Log;

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
    private static String ownNumber;
    PhoneDataHandler phoneDataHandler;

    private Context context;

    public NumberLogicHandler(Context context) {
        this.context = context;
        phoneDataHandler = new PhoneDataHandler(context);
        //puts himself in the phoneDataHandler delegate object, for returning results
        phoneDataHandler.delegate = this;

        //das hier ist glaube ich noch nicht gut so, sollte nicht einfach eine Methode im Async aufrufen
        ownNumber = cleanNumberString(phoneDataHandler.getOwnNumber()) + "/";

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

    public boolean sendOwnNumber() {

        //starts async task RestConnector to send ownNumber to server
        new RestConnector(context).execute(POST, SEND_NUMBER + ownNumber);
        return true;
    }

    //reads friendlist from file and starts async task RestConnector to send friend numbers to server
    public boolean inviteFriends() {
        InputStream inputStream;
        String readFile;
        try {
            inputStream = context.openFileInput("friend_list");
            readFile = convertInputStreamToString(inputStream);
            Log.d("readFile", readFile);
            new RestConnector(context).execute(GET, INVITE_FRIENDS + ownNumber + readFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    //gets called by PhoneDataHandler async tasks, when it is finished
    public void processFinish(String output) {
        //sends contacts to server after cleanup
        sendContacts(cleanNumberString(output));
    }

    private String cleanNumberString(String numbers) {
        String _tmp = numbers;
        Log.d("NUMBERS", numbers);

        _tmp = _tmp.replaceAll("[+]", "");
        _tmp = _tmp.replaceAll("[^0-9,]", "");
        _tmp = _tmp.replaceAll("[,][0]{2}", ",");
        _tmp = _tmp.replaceAll("[,][0]", ",49");

        //removes leading comma
        if(_tmp.charAt(0)==',') {
            _tmp = _tmp.substring(1);
        }

        Log.d("afterClean", _tmp);

        return _tmp;
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
