package com.niem.gladow.centroid;

import android.content.Context;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.TransportationMode;

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
        Map<Long, Invite> tmp = PersistenceHandler.getInstance().loadActiveInvites();
        if (tmp != null) {
            activeInvites = tmp;
        }
    }

    public static InviteHandler getInstance() {
        if (instance == null) {
            instance = new InviteHandler();
        }
        return instance;
    }
    
    //if there is at least one unanswered invite return true
    public boolean existsUnansweredInvite() {
        for (Invite _invite: activeInvites.values()) {
            if (_invite.getStatus().equals(InviteReply.UNANSWERED)) {
                return true;
            }
        }
        return false;
    }

    //todo save activeInvites and load

    public Map<Long, Invite> getActiveInvites() {
        return activeInvites;
    }

    public Invite getInviteByTime (long startTime) {
        return activeInvites.get(startTime);
    }

    public void addInvite(String inviteNumber, long startTime, String allMembers) {
        //add invite with start time and host number
        InviteHandler.activeInvites.put(startTime, new Invite(inviteNumber, startTime, allMembers));
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }

    public void removeInvite(Long startTime) {
        activeInvites.remove(startTime);
    }

    public void addCentroidToInvite(long startTime, String latLong) {
        activeInvites.get(startTime).setCentroid(new Centroid(latLong));
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }

    //todo multiple invites handling
    public void responseToInvite(long startTime, InviteReply inviteReply
                                        ,TransportationMode transportationMode, Context context) {
        //send reply
        new RestConnector(context).execute(RestConnector.POST, INVITE_RESPONSE +
                PersistenceHandler.getInstance().getOwnNumber() + "/" + startTime + "/" +
                inviteReply + "/" + transportationMode);

        //sets the invite status either accepted or declined
        getInviteByTime(startTime).setStatus(inviteReply);

        //if the users accepts the invite his latest gps signal is transmitted to the server
        //and the status is set to accepted
        //todo latest is not good enough, needs to be a valid one
        if (inviteReply.equals(InviteReply.ACCEPTED)) {
            new GpsDataHandler(context);
        }
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }
}
