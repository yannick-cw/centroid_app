package com.niem.gladow.centroid;

import android.content.Context;
import android.util.Log;

import com.niem.gladow.centroid.Enums.InviteReply;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yannick_uni on 12/8/15.
 */
public class InviteHandler {
    private static final String INVITE_RESPONSE = "/android/responseToInvite/";
    private static Map<Long, Invite> openInvites = new HashMap<>();

    public static boolean existsNewInvite() {
        for (Invite _invite: openInvites.values()) {
            if (_invite.getStatus().equals(InviteReply.UNANSWERED)) {
                return true;
            }
        }
        return false;
    }

    public static Invite getLatestInvite() {
        long newestInvite = 0;

        for (long l: openInvites.keySet()) {
            if (l>newestInvite && openInvites.get(l).getStatus().equals(InviteReply.UNANSWERED)) {
                newestInvite = l;
            }
        }
        return openInvites.get(newestInvite);
    }

    public static Invite getInviteByTime (long startTime) {
        return openInvites.get(startTime);
    }

    public static Invite getLatestInviteWithActiveAwesomeCentroid() {
        long newestInvite = 0;

        for (long l: openInvites.keySet()) {
            if (l>newestInvite && openInvites.get(l).existsCentroid()) {
                newestInvite = l;
            }
        }
        return openInvites.get(newestInvite);
    }

    public static void removeInvite(Invite invite) {
        openInvites.remove(invite.getStartTime());
    }

    public static void addOpenInvites(String inviteNumber, long startTime) {
        InviteHandler.openInvites.put(startTime, new Invite(inviteNumber, startTime));
    }

    //todo multiple invites handling
    public static void responseToInvite(InviteReply inviteReply, Context context) {
        new RestConnector(context).execute(RestConnector.POST, INVITE_RESPONSE +
                PersistenceHandler.getInstance().getOwnNumber() + "/" + getLatestInvite().getInviteNumber() + "/" +
                inviteReply);

        //if the users accepts the invite his latest gps signal is transmitted to the server
        if (inviteReply.equals(InviteReply.ACCEPTED)) {
            getLatestInvite().setStatus(InviteReply.ACCEPTED);
            new GpsDataHandler(context);
        }
        else removeInvite(getLatestInvite());
    }

    public static void addCentroidToInvite(long startTime, String latLong) {
        openInvites.get(startTime).setCentroid(new Centroid(latLong));
    }
}
