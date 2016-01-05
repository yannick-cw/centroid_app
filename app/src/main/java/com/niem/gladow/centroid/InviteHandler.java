package com.niem.gladow.centroid;

import android.content.Context;
import com.niem.gladow.centroid.Enums.InviteReply;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all invites
 */
public class InviteHandler {
    private static final String INVITE_RESPONSE = "/android/responseToInvite/";
    //Invite objects are saved in this map, with start time as key
    private static Map<Long, Invite> openInvites = new HashMap<>();

    //if there is at least one unanswered invite return true
    public static boolean existsNewInvite() {
        for (Invite _invite: openInvites.values()) {
            if (_invite.getStatus().equals(InviteReply.UNANSWERED)) {
                return true;
            }
        }
        return false;
    }

    //todo save openInvites and load

    //todo replace
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

    //todo replace
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

    public static void addOpenInvites(String inviteNumber, long startTime, String allMembers) {
        //add invite with start time and host number
        InviteHandler.openInvites.put(startTime, new Invite(inviteNumber, startTime, allMembers));
    }

    public static void addCentroidToInvite(long startTime, String latLong) {
        openInvites.get(startTime).setCentroid(new Centroid(latLong));
    }

    //todo multiple invites handling
    public static void responseToInvite(InviteReply inviteReply, Context context) {
        //send reply
        new RestConnector(context).execute(RestConnector.POST, INVITE_RESPONSE +
                PersistenceHandler.getInstance().getOwnNumber() + "/" + getLatestInvite().getStartTime() + "/" +
                inviteReply);

        //if the users accepts the invite his latest gps signal is transmitted to the server
        if (inviteReply.equals(InviteReply.ACCEPTED)) {
            getLatestInvite().setStatus(InviteReply.ACCEPTED);
            new GpsDataHandler(context);
        }
        //if the invite was declined, remove the invite from local map
        //todo remove by time
        else removeInvite(getLatestInvite());
    }

}
