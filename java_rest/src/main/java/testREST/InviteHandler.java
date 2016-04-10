package testREST;

import testREST.Enums.InviteReply;
import testREST.Enums.MessageType;
import testREST.centroid_algo.Centroid;
import testREST.Enums.TransportationModes;
import testREST.centroid_algo.WeightedPoint;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yannick_uni on 12/8/15.
 */
public class InviteHandler {
    private final String TRANSPORT = "transport";
    private final String PLACE = "place";
    private final String ALL_NUMBERS = "all_numbers";
    private final String CENTROID = "centroid";
    private final String START_TIME = "time";
    private final String INVITE_NUMBER = "number";
    private final String UPDATE_NUMBER = "update_number";
    private final String UPDATE_STATUS = "update_status";
    private GoogleMessageConnector googleMessageConnector = new GoogleMessageConnector();
    private static Map<Long, Invite> openInvites = new HashMap<>();

    public boolean responseToInvite (String ownNumber, String inviteId, InviteReply inviteReply, TransportationModes transportationMode) {
        //gets the correspondent invite object
        Invite _invite = openInvites.get(Long.parseLong(inviteId));
        Logger.getLogger(NumberHandler.class.getName()).log(Level.SEVERE,_invite.getAllNumbers().toString());
        boolean _result;
        //tries to add the response to the invite object
        _result = _invite.responseToInvite(ownNumber, inviteReply, transportationMode);

        //update every response to all members for progress bar etc
        updateAllMembers(ownNumber, inviteReply, transportationMode, _invite);

        //checks if invite is complete
        if (_invite.isInviteComplete()) {
            Logger.getLogger(NumberHandler.class.getName()).log(Level.INFO,"inside invite complete");
            sendCentroid(_invite);
        }
        return _result;
    }

    private void sendCentroid(Invite _invite) {
        String _centroid = calculateCentroid(_invite);
        _invite.setCentroid(_centroid);
        Map<String, String> _messages = new HashMap<>();
        _messages.put(INVITE_NUMBER, _invite.getInviteNumber());
        _messages.put(START_TIME, String.valueOf(_invite.getStartTime()));
        _messages.put(CENTROID, _centroid);
        pushToDevice(_invite.getInviteAcceptedFriendsAndHost(), _messages , MessageType.CENTROID);
    }

    private void updateAllMembers(String ownNumber, InviteReply inviteReply, TransportationModes transportationMode,Invite _invite) {
        Map<String, String> _messageToMembers = new HashMap<>();
        _messageToMembers.put(START_TIME, String.valueOf(_invite.getStartTime()));
        _messageToMembers.put(UPDATE_NUMBER, ownNumber);
        _messageToMembers.put(UPDATE_STATUS, inviteReply.toString());
        _messageToMembers.put(TRANSPORT, transportationMode.toString());
        Set<String> _allNumbers =_invite.getAllNumbers();
        _allNumbers.remove(ownNumber);
        pushToDevice(_allNumbers, _messageToMembers, MessageType.UPDATE);
    }

    public String inviteFriends(String ownNumber, String friendNumbers, TransportationModes hostTransportationMode) {
        //create new invite object
        Logger.getLogger(NumberHandler.class.getName()).log(Level.INFO,"Own: " + ownNumber + " friendNumbers: " + friendNumbers);
        Invite _invite = new Invite(ownNumber, friendNumbers, hostTransportationMode);
        //put invite object in map, key is start time
        openInvites.put(_invite.getStartTime(), _invite);
        //get numbers to be invited
        Set<String> _friends = _invite.getAllNumbers();
        //pushes to device
        Logger.getLogger(NumberHandler.class.getName()).log(Level.INFO,"SET " + _friends.toString());
        Map<String, String> _messages = new HashMap<>();
        _messages.put(INVITE_NUMBER, _invite.getInviteNumber());
        _messages.put(START_TIME, String.valueOf(_invite.getStartTime()));
        _messages.put(ALL_NUMBERS, Util.getInstance().collectionToString(_friends));
        _messages.put(TRANSPORT, _invite.getMemberTransportationMode(ownNumber).toString());
        return pushToDevice(_friends, _messages, MessageType.INVITE);
    }
    private String calculateCentroid(Invite invite) {
        List <String> _members = invite.getInviteAcceptedFriendsAndHost();
        GpsHandler _gpsHandler = new GpsHandler();
        List _weightedPointList = new LinkedList<>();
        for (String member: _members) {
            _weightedPointList.add(new WeightedPoint(invite.getMemberTransportationMode(member),Double.parseDouble(_gpsHandler.getGPS(member).get(0)),
                    Double.parseDouble(_gpsHandler.getGPS(member).get(1))));
        }
        WeightedPoint centroid = new Centroid().getCentroid(_weightedPointList);
        Logger.getLogger(NumberHandler.class.getName()).log(Level.INFO,"CENTROID: " + centroid.getX() + "::" + centroid.getY());
        return  centroid.getX() + "," + centroid.getY();

    }

    private String pushToDevice(Collection<String> collection, Map messages, MessageType messageType) {
        String _result = "";
        for (String number : collection) {
            try {
                //for every friend number send host number via GCM, get token via number from allNumbers map
                //takes the current allNumbers map from the number handler
                Map<String, String> _allNumbers = NumberHandler.getAllNumbers();
                Logger.getLogger(NumberHandler.class.getName()).log(Level.INFO,"number " + number + "pushed");
                _result += googleMessageConnector.pushMessage(_allNumbers.get(number), messages, messageType);
            } catch (IOException e) {
                Logger.getLogger(NumberHandler.class.getName()).log(Level.SEVERE,e.getMessage());
                _result += e.getMessage();
            }
        }
        return _result;
    }

    public String draengeln(String ownNumber, String friendNumber) {
        Map<String, String> _messages = new HashMap<>();
        _messages.put(INVITE_NUMBER, ownNumber);
        List<String> _friend = new LinkedList<>();
        _friend.add(friendNumber);
        pushToDevice(_friend, _messages, MessageType.DRAENGEL);
        return "";
    }

    public boolean addPlaceToInvite(String inviteId, String place) {
        Invite _invite = openInvites.get(Long.parseLong(inviteId));
        _invite.setPlace(place);
        Set<String> _allNumbers =_invite.getAllNumbers();
        _allNumbers.remove(_invite.getInviteNumber());
        Map<String, String> _messageToMembers = new HashMap<>();
        _messageToMembers.put(START_TIME, String.valueOf(_invite.getStartTime()));
        _messageToMembers.put(PLACE, place);
        pushToDevice(_allNumbers, _messageToMembers, MessageType.PLACE);
        return true;
    }

    //returns the requested invite as a string
    public String getInviteString(String inviteId) {
        Logger.getLogger(InviteHandler.class.getName()).log(Level.INFO, "trying to resolve client: " + Long.parseLong(inviteId)
        + "existing:" + openInvites.keySet());
        return openInvites.get(Long.parseLong(inviteId)).toString();
    }

    //checks for invite not currently known by the client
    public String getMissingInvites(String ownNumber, String inviteIds) {
        List<String> _clientIds = Arrays.asList(inviteIds.split(","));
        Set<Long> ids = new HashSet<>(openInvites.keySet());
        StringBuilder _missingIds = new StringBuilder();

        for (Iterator<Long> i = ids.iterator(); i.hasNext();) {
            Long id = i.next();
            for (String _clientId: _clientIds) {
                if(id.toString().equals(_clientId)) {
                    i.remove();
                    break;
                }
            }
        }
        for(Long id: ids) {
            openInvites.get(id).getAllNumbers().forEach(n -> {
                if(n.equals(ownNumber)) {
                    _missingIds.append(id + ",");
                }
            });
        }

//
//        for(Long id: ids) {
//            for(String clientId: _clientIds) {
//                if(!clientId.equals(id.toString())) {
//                    openInvites.get(id).getAllNumbers().forEach(n -> {
//                        if(n.equals(ownNumber)) {
//                            _missingIds.append(id + ",");
//                        }
//                    });
//                    break;
//                }
//            }
//        }
        Logger.getLogger(InviteHandler.class.getName()).log(Level.INFO, "ids "+ ids + "missingIDS:" + _missingIds );
        if(_missingIds.length() > 0) {
            _missingIds.deleteCharAt(_missingIds.length() - 1);
        }
        return _missingIds.toString();
    }
}
