package com.niem.gladow.centroid;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This class represents invites
 */

public class Invite {
    //is the unique id of the invite
    private long startTime;
    //number of person who invited
    private String inviteNumber;
    private Centroid centroid;
    private InviteReply status = InviteReply.UNANSWERED;
    private TransportationMode transportationMode = TransportationMode.DEFAULT;
    private boolean existsCentroid = false;
    private List<String> allMembers;

    public Invite(String inviteNumber, long startTime, String allMembers) {
        this.inviteNumber = inviteNumber;
        this.startTime = startTime;
        this.allMembers = new LinkedList<>(Arrays.asList(allMembers.split(",")));
        //ownnumber has to be removed from list
        String _ownNumber = PersistenceHandler.getInstance().getOwnNumber();
        this.allMembers.remove(_ownNumber);
        //try to replace as many numbers as possible with names
        findRealNames(this.allMembers);
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

    public List<String> getAllMembers() {
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
