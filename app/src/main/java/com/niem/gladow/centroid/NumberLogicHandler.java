package com.niem.gladow.centroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.niem.gladow.centroid.Enums.TransportationMode;

/**
 * Created by yannick and clemens 2016
 *
 * centroid
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
        new RestConnector(context).execute(RestConnector.SEND_CONTACTS, SEND_CONTACTS + ownNumberWithSlash + contacts);
        return true;
    }

    public void syncTokenAndNumber() {
        new RestConnector(context).execute(RestConnector.POST_NO_RESULT, SEND_NUMBER + ownNumberWithSlash
                + PersistenceHandler.getInstance().getToken());
    }

    //reads friend list from file and starts async task RestConnector to send friend numbers to server
    public boolean inviteFriends(TransportationMode transportationMode) {
        if (GpsDataHandler.getInstance().getLastLocation() == null) {
            return false;
        }
        new RestConnector(context).execute(RestConnector.POST_NO_RESULT, GpsDataHandler.SEND_GPS + ownNumberWithSlash
                + GpsDataHandler.getInstance().getLastLocation().getLongitude() + "/"
                + GpsDataHandler.getInstance().getLastLocation().getLatitude());

        new RestConnector(context).execute(RestConnector.GET_NO_RESULT, INVITE_FRIENDS + ownNumberWithSlash
                + PersistenceHandler.getInstance().getInviteList()
                + "/" + transportationMode);
        return true;
    }

    //gets called by PhoneDataHandler async tasks, when it is finished
    public void processFinish(String output) {
        //sends contacts to server after cleanup
        sendContacts(output);
    }

    public void saveFriendMap(String result) {
        PersistenceHandler.getInstance().createFriendMap(result);
        PersistenceHandler.getInstance().saveFriendMapToDB();
        Intent _intent = new Intent(MyGcmListenerService.BROADCAST_UPDATE);
        context.sendBroadcast(_intent);
        Log.d("friend Map", PersistenceHandler.getInstance().getFriendMap().values().toString());
    }

}
