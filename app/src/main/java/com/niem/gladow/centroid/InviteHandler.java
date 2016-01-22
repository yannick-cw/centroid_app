package com.niem.gladow.centroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
        activeInvites.get(startTime).setStatus(InviteReply.READY);
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }

    public void setInviteStatus(long startTime, InviteReply inviteReply) {
        activeInvites.get(startTime).setStatus(inviteReply);
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }

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
        if (inviteReply.equals(InviteReply.ACCEPTED)) {
            new GpsDataHandler(context);
        }
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }

    public void updateMemberStatus(long startTime, String updateNumber, InviteReply updateStatus, TransportationMode transportationMode) {
        activeInvites.get(startTime).updateMember(updateNumber, updateStatus, transportationMode);
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }

    public void setChosenPlace(String place, Invite invite) {
        invite.setChosenPlace(place);
        PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
    }

    /**
     * parsing the transmitted string
     * either creates new invite or updates existing one
     * @param result
     * @param context
     */
    public void syncInvite(String result, Context context) {
        Log.d("XXXX", "result update: " + result);
        if(result != null && !"".equals(result)) {
            List<String> _list = Arrays.asList(result.split(":"));
            long id = Long.parseLong(_list.get(0));
            Log.d("XXXX", "Invite exists? " + activeInvites.containsKey(id));
            Map<String, List<String>> _numberStatus = new HashMap();
            String[] _pairNumberStatus = _list.get(2).split(",");
            for (String str: _pairNumberStatus) {
                List<String> _tupel = new LinkedList();
                _tupel.add(str.split("&")[1]);
                _tupel.add(str.split("&")[2]);
                _numberStatus.put(str.split("&")[0], _tupel);
            }
            StringBuilder _allNumbers = new StringBuilder();
            for (String str: _numberStatus.keySet()) {
                _allNumbers.append(str + ",");
            }
            _allNumbers.deleteCharAt(_allNumbers.length() - 1);

            //if invite does not already exist, create new one
            if(!activeInvites.containsKey(id)) {
                activeInvites.put(id, new Invite(_list.get(1), id, _allNumbers.toString()));

                //check ownNumber status, set accordingly
                activeInvites.get(id).setStatus(InviteReply.valueOf(_numberStatus.get(PersistenceHandler.getInstance().getOwnNumber()).get(0)));
                activeInvites.get(id).setTransportationMode(TransportationMode.valueOf(_numberStatus.get(PersistenceHandler.getInstance().getOwnNumber()).get(1)));
            }

            //update status and centroid
            for (String str: _numberStatus.keySet()) {
                activeInvites.get(id).updateMember(str, InviteReply.valueOf(_numberStatus.get(str).get(0)), TransportationMode.valueOf(_numberStatus.get(str).get(1)));
            }
            if(!_list.get(3).equals("null")) {
                activeInvites.get(id).setCentroid(new Centroid(_list.get(3)));
            }
            PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
            Intent _intent = new Intent(MyGcmListenerService.BROADCAST_UPDATE);
            context.sendBroadcast(_intent);
        }
    }

    public String getActiveInvitesString() {
        StringBuilder _ids = new StringBuilder();
        for (long id: activeInvites.keySet()) {
            _ids.append(id + ",");
        }
        _ids.deleteCharAt(_ids.length() - 1);
        return _ids.toString();
    }

    public void syncAllInvites(String result, Context context) {
        if(result != null && !"".equals(result)) {
            List<String> ids = Arrays.asList(result.split(","));
            for (String id: ids) {
                new RestConnector(context).execute(RestConnector.SYNC, "/android/updateInvite/" + id);
            }
        }
    }
}
