package com.niem.gladow.centroid;

import android.util.Log;

import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.InviteStatus;
import com.niem.gladow.centroid.Enums.TransportationMode;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    private Map<String, InviteStatus> allMembers;
    private String chosenPlace;

    public static final boolean WITH = true;
    public static final boolean WITHOUT = false;


    public Invite(String inviteNumber, long startTime, String allMembers) {
        this.inviteNumber = inviteNumber;
        this.startTime = startTime;
        List<String> _allMembers = new LinkedList<>(Arrays.asList(allMembers.split(",")));
        //put list in map, InviteReply status is added
        this.allMembers = new HashMap<>();
        for (String str: _allMembers) {
            this.allMembers.put(str, new InviteStatus());
        }
        findRealNames(this.allMembers);
    }

//todo numbers to name
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

    public Map<String, InviteStatus> getAllMembers(boolean self, boolean host) {
        Map<String, InviteStatus> _tmp = new HashMap<>(allMembers);
        if(!host){
            _tmp.remove(inviteNumber);
        }
        if(!self){
            _tmp.remove(PersistenceHandler.getInstance().getOwnNumber());
        }
        return _tmp;
    }

    public String getAllMemberSurNames(boolean self, boolean host){
        Map<String, InviteStatus> _memberMap = getAllMembers(self, host);
        String _tmp, _result = "";
        for (Map.Entry<String, InviteStatus> _member : _memberMap.entrySet())
        {
            _tmp = _member.getValue().getRealName().split(" ")[0];
            if(_tmp.matches("")){
                _tmp = _member.getKey();
            }
            _result += ", "+_tmp;
        }
        if(_result.matches("")){
            return _result;
        }else{
            return _result.substring(2); // replace ", " at beginning of String
        }
    }

    //check with the persistence handler friendMap, if numbers can be replaced with names
    private void findRealNames(Map<String, InviteStatus> allMembers) {
        Map<String,String> _friendMap = PersistenceHandler.getInstance().getFriendMap();
        //replace all possible numbers with real names
        for (Map.Entry<String, InviteStatus> _entry: allMembers.entrySet()) {
            String _name;
            _name = _friendMap.get(_entry.getKey());
            if(_name != null) {
                _entry.getValue().setRealName(_name);
            }
        }
    }

    public void updateMember(String updateNumber, InviteReply updateStatus, TransportationMode transportationMode) {
        allMembers.get(updateNumber).setInviteReply(updateStatus);
        allMembers.get(updateNumber).setTransportationMode(transportationMode);
        findRealNames(this.allMembers);
    }

    public List<String> getPlaceToMeetInformations() {
        List<String> _tmp = Arrays.asList(toReadableContent(getChosenPlace()).split(","));
        _tmp.set(1,_tmp.get(1).split(" ")[0]+" "+_tmp.get(1).split(" ")[1]);
        return _tmp;
    }

    public String getChosenPlace() {
        return chosenPlace;
    }

    public void setChosenPlace(String chosenPlace) {
        this.chosenPlace = chosenPlace;
    }

    public String getChosenPlaceContent(){
        return toDisplayContent(getPlaceToMeetInformations());
    }

    public String[] getChosenPlaceForUri(){
        return getChosenPlace().split(",");
    }

    private String toReadableContent(String content) {
        Log.d("XXXX", "place in toReadable start: " + content);

        content = content.replaceAll("rvxy","%");
        try {
            content = URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("XXXX", "place in toReadable after decode: " + content);
        return content;
    }

    private String toDisplayContent(List<String> contentList) {
        String _content = "Your chosen location: \n";
        _content += "Name: " + contentList.get(0) + "\n";

        _content += "Address: " + contentList.get(1) + "\n";

        _content += "Phone: " + contentList.get(2) + "\n";
        return _content;
    }
}
