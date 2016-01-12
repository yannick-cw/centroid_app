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
    private static Map<Long, Invite> activeInvites = new HashMap<>();

    private static InviteHandler instance;

    private InviteHandler() {
    }

    public static InviteHandler getInstance() {
        if (instance == null) {
            instance = new InviteHandler();
        }
        return instance;
    }

    //if there is at least one unanswered invite return true
    public static boolean existsUnansweredInvite() {
        for (Invite _invite: activeInvites.values()) {
            if (_invite.getStatus().equals(InviteReply.UNANSWERED)) {
                return true;
            }
        }
        return false;
    }

    //todo save activeInvites and load

    public static Map<Long, Invite> getActiveInvites() {
        return activeInvites;
    }

    public static Invite getInviteByTime (long startTime) {
        return activeInvites.get(startTime);
    }

    public static void addInvite(String inviteNumber, long startTime, String allMembers) {
        //add invite with start time and host number
        InviteHandler.activeInvites.put(startTime, new Invite(inviteNumber, startTime, allMembers));
    }

    public static void removeInvite(Long startTime) {
        activeInvites.remove(startTime);
    }

    public static void addCentroidToInvite(long startTime, String latLong) {
        activeInvites.get(startTime).setCentroid(new Centroid(latLong));
    }

    //todo multiple invites handling
    public static void responseToInvite(long startTime, InviteReply inviteReply, Context context) {
        //send reply
        new RestConnector(context).execute(RestConnector.POST, INVITE_RESPONSE +
                PersistenceHandler.getInstance().getOwnNumber() + "/" + startTime + "/" +
                inviteReply);

        //sets the invite status either accepted or declined
        getInviteByTime(startTime).setStatus(inviteReply);

        //if the users accepts the invite his latest gps signal is transmitted to the server
        //and the status is set to accepted
        //todo latest is not good enough, needs to be a valid one
        if (inviteReply.equals(InviteReply.ACCEPTED)) {
            new GpsDataHandler(context);
        }
    }

}
