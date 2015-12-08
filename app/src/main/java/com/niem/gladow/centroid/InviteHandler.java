package com.niem.gladow.centroid;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yannick_uni on 12/8/15.
 */
public class InviteHandler {
    private static final String INVITE_RESPONSE = "/android/responseToInvite/";
    private static boolean existsNewInvite = false;
    private static List<String> openInvites = new LinkedList();

    public static boolean ExistsNewInvite() {
        return existsNewInvite;
    }

    private static void setExistsNewInvite(boolean existsNewInvite) {
        InviteHandler.existsNewInvite = existsNewInvite;
    }

    public static String getLatestInviteNumber() {
        return InviteHandler.openInvites.get(InviteHandler.openInvites.size()-1);
    }

    public static void removeInvite(String number) {
        InviteHandler.openInvites.remove(number);
        if (InviteHandler.openInvites.isEmpty()) setExistsNewInvite(false);
    }

    public static void addOpenInvites(String number) {
        InviteHandler.openInvites.add(number);
        setExistsNewInvite(true);
    }

    public static void responseToInvite(InviteReply inviteReply, Context context) {
        new RestConnector(context).execute(RestConnector.POST, INVITE_RESPONSE +
                PersistenceHandler.getInstance().getOwnNumber() + getLatestInviteNumber() + "/" +
                inviteReply);
        removeInvite(getLatestInviteNumber());
        //if the users accepts the invite his latest gps signal is transmitted to the server
        if (inviteReply.equals(InviteReply.ACCEPTED)) new GpsDataHandler(context);
    }
}
