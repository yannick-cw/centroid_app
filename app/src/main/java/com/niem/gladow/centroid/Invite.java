package com.niem.gladow.centroid;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This class represents invites
 */

public class Invite implements Serializable {
    //is the unique id of the invite
    private long startTime;
    //number of person who invited
    private String inviteNumber;
    private Centroid centroid;
    private InviteReply status = InviteReply.UNANSWERED;
    private TransportationMode transportationMode = TransportationMode.DEFAULT;
    private boolean existsCentroid = false;
    //todo sollte eine map sein, die vom server geupdated wird
    private Map<String, InviteReply> allMembers;

    public Invite(String inviteNumber, long startTime, String allMembers) {
        this.inviteNumber = inviteNumber;
        this.startTime = startTime;
        List<String> _allMembers = new LinkedList<>(Arrays.asList(allMembers.split(",")));
        //ownnumber has to be removed from list
        String _ownNumber = PersistenceHandler.getInstance().getOwnNumber();
        _allMembers.remove(_ownNumber);
        //try to replace as many numbers as possible with names
        findRealNames(_allMembers);
        this.allMembers = new HashMap<>();
        for (String str: _allMembers) {
            this.allMembers.put(str, InviteReply.UNANSWERED);
        }
    }


    public long getStartTime() {
        return startTime;
    }


    public Centroid getCentroid() {
        assert(centroid != null);
        return centroid;
    }

    public void setCentroid(Centroid centroid) {
        this.centroid = centroid;
        existsCentroid = true;
    }

    public boolean existsCentroid() {
        return existsCentroid;
    }

    public String getInviteNumber() {
        return inviteNumber;
    }

    public String getInviteNumberName() {
        String _name = PersistenceHandler.getInstance().getFriendMap().get(inviteNumber);
        if(inviteNumber.equals(PersistenceHandler.getInstance().getOwnNumber())) {
            _name = "You";
        }
        return _name != null ? _name : inviteNumber;
    }

    public void setStatus(InviteReply status) {
        this.status = status;
    }


    public InviteReply getStatus() {
        return status;
    }

    public TransportationMode getTransportationMode() {
        return transportationMode;
    }

    public void setTransportationMode(TransportationMode transportationMode) {
        this.transportationMode = transportationMode;
    }

    public Map<String, InviteReply> getAllMembers() {
        return allMembers;
    }

    //check with the persistence handler friendMap, if numbers can be replaced with names
    private void findRealNames(List<String> allMembers) {
        List<String> tmp = new LinkedList<>(allMembers);
        Map<String,String> _friendMap = PersistenceHandler.getInstance().getFriendMap();
        //replace all possible numbers with real names
        for (String _number: tmp) {
            String _name;
            _name = _friendMap.get(_number);
            if(_name != null) {
                allMembers.remove(_number);
                allMembers.add(_name);
            }
        }
    }

}
