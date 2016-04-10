package testREST;

import testREST.Enums.InviteReply;
import testREST.Enums.InviteStatus;
import testREST.Enums.TransportationModes;

import java.util.*;

/**
 * Created by yannick_uni on 12/8/15.
 */
public class Invite {
    private String inviteNumber;
    //from Number to flag, flag says if invite was accepted, is enum (accepted, declined, unanswered)
    private Map<String, InviteStatus> friendNumbers = new HashMap<>();
    //creation time of invite
    private long startTime;
    private String centroid = "null";
    private String place = "null";

    public Invite (String inviteNumber, String friendNumbersString, TransportationModes hostTransportation) {
        this.inviteNumber = inviteNumber;
        List<String> _friendNumbersList = Util.getInstance().stringToList(friendNumbersString);
        //writes number to be invited in friendNumbers map and adds default unanswered InviteStatus
        _friendNumbersList.forEach(_number -> friendNumbers.put(_number, new InviteStatus()));
        //add the host, who has already accepted
        InviteStatus _hostStatus = new InviteStatus();
        _hostStatus.setInviteReply(InviteReply.ACCEPTED);
        _hostStatus.setTransportationMode(hostTransportation);
        friendNumbers.put(inviteNumber, _hostStatus);
        startTime = System.currentTimeMillis();
    }

    public String getInviteNumber() {
        return inviteNumber;
    }

    public long getStartTime() {
        return startTime;
    }

    //returns list of all members who have accepted the invite and the host
    public List<String> getInviteAcceptedFriendsAndHost() {
        List<String> _inviteAcceptedFriends = new LinkedList<>();

        friendNumbers.forEach((number,status) -> {
            if (status.getInviteReply() == InviteReply.ACCEPTED) {
                _inviteAcceptedFriends.add(number);
            }
        });
        return _inviteAcceptedFriends;
    }

    //return everyone who has to be invited
    public Set<String> getAllNumbers () {
        Set<String> _allNumbersSet = new HashSet (friendNumbers.keySet());
        return _allNumbersSet;
    }

    //checks if everyone has rsvp'd ;)
    public boolean isInviteComplete () {
        int _acceptedCount = 0;
        for (InviteStatus inviteStatus: friendNumbers.values()) {
            if (inviteStatus.getInviteReply() == InviteReply.UNANSWERED) return false;
            if (inviteStatus.getInviteReply() == InviteReply.ACCEPTED)_acceptedCount++;

        }
        return (_acceptedCount > 1);
    }

    public TransportationModes getMemberTransportationMode(String number) {
        return friendNumbers.get(number).getTransportationMode();
    }

    //puts the friends reply in the friendNumbers map and returns if successful
    public boolean responseToInvite (String ownNumber, InviteReply inviteReply, TransportationModes transportationMode) {


        if (!friendNumbers.containsKey(ownNumber)) {
            return false;
        }
        else {
            //add the responses
            friendNumbers.get(ownNumber).setInviteReply(inviteReply);
            friendNumbers.get(ownNumber).setTransportationMode(transportationMode);
            return true;
        }
    }

    public String getCentroid() {
        return centroid;
    }

    public void setCentroid(String centroid) {
        this.centroid = centroid;
    }

    public Map<String, InviteStatus> getFriendNumbers() {
        return friendNumbers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(startTime + ":");
        sb.append(inviteNumber + ":");
        friendNumbers.forEach((number, flag) -> {
            sb.append(number + "&");
            sb.append(flag.getInviteReply().toString() + "&");
            sb.append(flag.getTransportationMode().toString() + ",");
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.append(":");
        sb.append(place);
        sb.append(":");
        sb.append(centroid);
        return sb.toString();
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
