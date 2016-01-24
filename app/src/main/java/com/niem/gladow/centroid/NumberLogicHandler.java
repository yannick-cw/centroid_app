package com.niem.gladow.centroid;

import android.content.Context;
import android.widget.Toast;

import com.niem.gladow.centroid.Enums.TransportationMode;

/**
 * Created by yannick_uni on 11/10/15.
 */
public class NumberLogicHandler implements AsyncResponse {
    private static final String SEND_NUMBER = "/android/registerNumber/",
            SEND_CONTACTS = "/android/checkNumbers/", INVITE_FRIENDS = "/android/inviteFriends/";
    private static String ownNumberWithSlash = PersistenceHandler.getInstance().getOwnNumber() + "/";
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
        new RestConnector(context).execute(RestConnector.SEND, SEND_CONTACTS + ownNumberWithSlash + contacts);
//        DEBUG HELPER: (if the line above is exchanged for the one below, complete contactsList is given from Server, good to test ListView-Options)
//        new RestConnector(context).execute(SEND, SEND_CONTACTS + contacts);
        return true;
    }

    public void syncTokenAndNumber () {
        new RestConnector(context).execute(RestConnector.POST, SEND_NUMBER + ownNumberWithSlash
                                            + PersistenceHandler.getInstance().getToken());
    }

    //reads friendlist from file and starts async task RestConnector to send friend numbers to server
    public boolean inviteFriends(TransportationMode transportationMode) {
        if (GpsDataHandler.getInstance().getLastLocation() == null) {
            Toast.makeText(context, "please give gps permission", Toast.LENGTH_SHORT).show();
            return false;
        }
        new RestConnector(context).execute(RestConnector.GET, INVITE_FRIENDS + ownNumberWithSlash
                                            + PersistenceHandler.getInstance().getInviteList()
                                            + "/" + transportationMode);
        new RestConnector(context).execute(RestConnector.POST, GpsDataHandler.SEND_GPS + ownNumberWithSlash
                + GpsDataHandler.getInstance().getLastLocation().getLongitude()+"/"
                + GpsDataHandler.getInstance().getLastLocation().getLatitude());
        return true;
    }

    //gets called by PhoneDataHandler async tasks, when it is finished
    public void processFinish(String output) {
        //sends contacts to server after cleanup
        sendContacts(output);
    }

}
