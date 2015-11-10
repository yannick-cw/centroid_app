package com.niem.gladow.centroid;

import android.content.Context;
import android.util.Log;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class LogicHandler implements AsyncResponse {
    private static final String POST = "1", GET = "2";
    private static final String SERVER_ADDRESS ="http://192.168.1.214:8080", SEND_NUMBER = "/android/registerNumber/",
            SEND_CONTACTS = "/android/checkNumbers/", INVITE_FRIENDS = "/android/inviteFriends/";
    PhoneDataHandler phoneDataHandler;

    private Context context;

    public LogicHandler(Context context) {
        this.context = context;
        phoneDataHandler = new PhoneDataHandler(context);
        phoneDataHandler.delegate = this;
    }

    public void executePhoneDataHandler() {
        phoneDataHandler.execute("");
    }

    private boolean sendContacts(String contacts) {
        new RestConnector(context).execute(GET, SERVER_ADDRESS + SEND_CONTACTS + "1/" + contacts);
        return true;
    }

    public boolean sendOwnNumber() {
        new RestConnector(context).execute(POST, SERVER_ADDRESS + SEND_NUMBER + "00436506064522");
        new RestConnector(context).execute(POST, SERVER_ADDRESS + SEND_NUMBER + "00436604071555");
        new RestConnector(context).execute(POST, SERVER_ADDRESS + SEND_NUMBER + "004369918165580");
        new RestConnector(context).execute(POST, SERVER_ADDRESS + SEND_NUMBER + "00436504293795");
        new RestConnector(context).execute(POST, SERVER_ADDRESS + SEND_NUMBER + "00436506689088");

        return true;
    }


    public boolean inviteFriends() {
        new RestConnector(context).execute(GET, SERVER_ADDRESS + INVITE_FRIENDS + "1/11");
        return true;
    }

    public void processFinish(String output) {
        sendContacts(cleanNumberString(output));
    }

    private String cleanNumberString(String numbers) {
        String _tmp = numbers;
        Log.d("NUMBERS", numbers);

        _tmp = _tmp.replaceAll("[+]", "00");
        _tmp = _tmp.replaceAll("[^0-9,]","");
        Log.d("afterClean", _tmp);

        return _tmp;
    }

}
