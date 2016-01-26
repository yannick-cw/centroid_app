package com.niem.gladow.centroid;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

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
    private static final int ID = 0;
    private static final int NUMBER_STATUS = 2;
    private static final int INVITE_REPLY = 1;
    private static final int TRANSPORTATION_MODE = 2;
    private static final int NUMBER = 0;
    private static final int HOST_NUMBER = 1;
    private static final int INVITE_STATUS = 0;
    private static final int TRANS_MODE = 1;
    private static final int CENTROID = 4;
    private static final int PLACE = 3;

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

    public Map<Long, Invite> getActiveInvites() {
        return activeInvites;
    }

    public Invite getInviteByTime(long startTime) {
        return activeInvites.get(startTime);
    }

    public void addInvite(String inviteNumber, long startTime, String allMembers) {
        //add invite with start time and members number
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
            , TransportationMode transportationMode, Context context) {
        if (GpsDataHandler.getInstance().getLastLocation() == null) {
            return;
        }
        //if the users accepts the invite his latest gps signal is transmitted to the server
        //and the status is set to accepted
        if (inviteReply.equals(InviteReply.ACCEPTED)) {
            new RestConnector(context).execute(RestConnector.POST_NO_RESULT, GpsDataHandler.SEND_GPS
                    + PersistenceHandler.getInstance().getOwnNumber() + "/"
                    + GpsDataHandler.getInstance().getLastLocation().getLongitude() + "/"
                    + GpsDataHandler.getInstance().getLastLocation().getLatitude());
        }

        //send reply
        new RestConnector(context).execute(RestConnector.POST_NO_RESULT, INVITE_RESPONSE +
                PersistenceHandler.getInstance().getOwnNumber() + "/" + startTime + "/" +
                inviteReply + "/" + transportationMode);

        //sets the invite status either accepted or declined
        getInviteByTime(startTime).setStatus(inviteReply);
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
     *
     * @param result  from the server, separated invite
     * @param context
     */
    public void syncInvite(String result, Context context) {
        if (result != null && !"".equals(result)) {
            List<String> _inviteElements = Arrays.asList(result.split(":"));
            long id;
            try {
                id = Long.parseLong(_inviteElements.get(ID));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }

            //get Map with number -> Status(reply + transport)
            Map<String, List<String>> _numberStatus = createNumberStatusMap(_inviteElements);

            //get String with all members numbers
            StringBuilder _allNumbers = getAllNumbers(_numberStatus);
            String _allNumbersString = _allNumbers.toString();

            //if invite does not already exist, create new one
            Invite _invite;
            if (!activeInvites.containsKey(id)) {
                _invite = createInvite(_inviteElements, id, _numberStatus, _allNumbersString);
            } else {
                //otherwise get existing invite
                _invite = activeInvites.get(id);
            }

            //update status and centroid
            for (String _number : _numberStatus.keySet()) {
                //update reply
                InviteReply _reply = InviteReply.valueOf(_numberStatus.get(_number).get(INVITE_STATUS));
                //update transportation mode
                TransportationMode _trans = TransportationMode.valueOf(_numberStatus.get(_number).get(TRANS_MODE));
                //send the update
                _invite.updateMember(_number, _reply, _trans);
            }
            //if there is a centroid, add it to the invite
            if (!_inviteElements.get(CENTROID).equals("null")) {
                _invite.setCentroid(new Centroid(_inviteElements.get(CENTROID)));
                _invite.setStatus(InviteReply.READY);

                //if there is a place as well, add it to the invite
                if (!_inviteElements.get(PLACE).equals("null")) {
                    _invite.setChosenPlace(_inviteElements.get(PLACE));
                }
            }

            //save the invites
            PersistenceHandler.getInstance().saveActiveInvites(activeInvites);
            //send out the broadcast to update the views
            Intent _intent = new Intent(MyGcmListenerService.BROADCAST_UPDATE);
            context.sendBroadcast(_intent);
        }
    }

    @NonNull
    private Invite createInvite(List<String> _inviteElements, long id, Map<String, List<String>> _numberStatus, String _allNumbersString) {
        Invite _invite;
        //create new invite
        activeInvites.put(id, new Invite(_inviteElements.get(HOST_NUMBER), id, _allNumbersString));
        _invite = activeInvites.get(id);

        //check ownNumber status, set accordingly
        String _ownNumber = PersistenceHandler.getInstance().getOwnNumber();

        //add own invite reply and own transportation mode
        InviteReply _reply = InviteReply.valueOf(_numberStatus.get(_ownNumber).get(INVITE_STATUS));
        _invite.setStatus(_reply);
        TransportationMode _trans = TransportationMode.valueOf(_numberStatus.get(_ownNumber).get(TRANS_MODE));
        _invite.setTransportationMode(_trans);
        return _invite;
    }

    private StringBuilder getAllNumbers(Map<String, List<String>> _numberStatus) {
        //get all members numbers from the maps key set
        StringBuilder _allNumbers = new StringBuilder();
        for (String _number : _numberStatus.keySet()) {
            _allNumbers.append(_number + ",");
        }
        //delete the trailing comma
        _allNumbers.deleteCharAt(_allNumbers.length() - 1);
        return _allNumbers;
    }

    private Map<String, List<String>> createNumberStatusMap(List<String> _inviteElements) {
        //create a map from the incoming List
        Map<String, List<String>> _numberStatus = new HashMap();
        String[] _tripleNumberStatus = _inviteElements.get(NUMBER_STATUS).split(",");

        for (String numberReplyTrans : _tripleNumberStatus) {
            List<String> _tupelReplyTrans = new LinkedList();
            _tupelReplyTrans.add(numberReplyTrans.split("&")[INVITE_REPLY]);
            _tupelReplyTrans.add(numberReplyTrans.split("&")[TRANSPORTATION_MODE]);
            _numberStatus.put(numberReplyTrans.split("&")[NUMBER], _tupelReplyTrans);
        }
        return _numberStatus;
    }

    //get all the ids of active invites
    public String getActiveInvitesString() {
        StringBuilder _ids = new StringBuilder();
        for (long id : activeInvites.keySet()) {
            _ids.append(id + ",");
        }
        if (_ids.length() > 0) {
            _ids.deleteCharAt(_ids.length() - 1);
        } else {
            _ids.append("0");
        }
        return _ids.toString();
    }

    //sync every invite with the server
    public void syncAllInvites(String result, Context context) {
        if (result != null && !"".equals(result)) {
            List<String> ids = Arrays.asList(result.split(","));
            for (String id : ids) {
                new RestConnector(context).execute(RestConnector.SYNC_INVITE, "/android/updateInvite/" + id);
            }
        }
    }
}
