/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.niem.gladow.centroid;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.niem.gladow.centroid.Database.MiniDB;
import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.MessageType;
import com.niem.gladow.centroid.Enums.TransportationMode;


public class MyGcmListenerService extends GcmListenerService {

    public static final String BROADCAST_UPDATE = "broadcast_update";
    private final String CENTROID = "centroid";
    private final String TIME = "time";
    private final String INVITE_NUMBER = "number";
    private final String MESSAGE_TYPE = "type";
    private final String ALL_NUMBERS = "all_numbers";
    private final String UPDATE_NUMBER = "update_number";
    private final String UPDATE_STATUS = "update_status";
    private final String PLACE = "place";
    private final String TRANSPORT = "transport";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        MiniDB.init(this);
        String _messageType = data.get(MESSAGE_TYPE).toString();
        long _startTime;
        TransportationMode _trans;

        InviteHandler inviteHandler = InviteHandler.getInstance();
        Log.d("XXXX", "GMC RECEIVE: " + MessageType.valueOf(_messageType));
        switch (MessageType.valueOf(_messageType)) {
            case INVITE:
                _startTime = Long.parseLong(data.get(TIME).toString());
                String _inviteNumber = data.get(INVITE_NUMBER).toString();
                String _allNumbers = data.get(ALL_NUMBERS).toString();

                inviteHandler.addInvite(_inviteNumber, _startTime, _allNumbers);
                _trans = TransportationMode.valueOf(data.get(TRANSPORT).toString());

                if (_inviteNumber.equals(PersistenceHandler.getInstance().getOwnNumber())) {
                    inviteHandler.setInviteStatus(_startTime, InviteReply.ACCEPTED);
                    inviteHandler.getInviteByTime(_startTime).setTransportationMode(_trans);
                    inviteHandler.updateMemberStatus(_startTime, _inviteNumber, InviteReply.ACCEPTED, TransportationMode.DEFAULT);
                }
                else {
                    String _inviteName = PersistenceHandler.getInstance().getFriendMap().get(_inviteNumber);
                    if (_inviteName == null) {
                        _inviteName = _inviteNumber;
                    }
                    sendNotification("you got invited by: " + _inviteName, "centroid invite");
                    inviteHandler.updateMemberStatus(_startTime, _inviteNumber, InviteReply.ACCEPTED, _trans);
                }
                break;
            case CENTROID:
                _startTime = Long.parseLong(data.get(TIME).toString());
                if(inviteHandler.getInviteByTime(_startTime) != null) {
                    String _centroid = data.get(CENTROID).toString();
                    inviteHandler.addCentroidToInvite(_startTime, _centroid);
                    Log.d(MyGcmListenerService.class.getName(), "Centroid: " + _centroid);
                    Log.d(MyGcmListenerService.class.getName(), "Time: " + _startTime);
                    sendNotification("you got a new centroid!", "centroid arrived");
                } else {
                    new RestConnector(this).execute(RestConnector.SYNC_INVITE, "/android/updateInvite/" + _startTime);
                }
                break;
            case UPDATE:
                _trans = TransportationMode.valueOf(data.get(TRANSPORT).toString());
                _startTime = Long.parseLong(data.get(TIME).toString());
                if(inviteHandler.getInviteByTime(_startTime) != null) {
                    String _updateNumber = data.get(UPDATE_NUMBER).toString();
                    InviteReply _updateStatus = InviteReply.valueOf(data.get(UPDATE_STATUS).toString());
                    inviteHandler.updateMemberStatus(_startTime, _updateNumber, _updateStatus, _trans);
                } else {
                    new RestConnector(this).execute(RestConnector.SYNC_INVITE, "/android/updateInvite/" + _startTime);
                }
                break;
            case PLACE:
                _startTime = Long.parseLong(data.get(TIME).toString());
                String _place = data.get(PLACE).toString();
                Log.d("XXXX", "place in GCM: " + _place);
                inviteHandler.setChosenPlace(_place, inviteHandler.getInviteByTime(_startTime));
                break;
            case DRAENGEL:
                String _friend = (String) data.get(INVITE_NUMBER);
                String _realName = PersistenceHandler.getInstance().getFriendMap().get(_friend).split(" ")[0];
                if (_realName == null) {
                    _realName = _friend;
                }
                //todo try if works
                new RestConnector(this).execute(RestConnector.SYNC_ALL_INVITES,
                        "/android/updateAllInvites/" + PersistenceHandler.getInstance().getOwnNumber() + "/"
                                + InviteHandler.getInstance().getActiveInvitesString());
                sendNotification(_realName + " asks you to respond.", "centroid");
            default:
                break;
        }
        broadcastToActivities();
    }
    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message, String title) {
        Intent intent = new Intent(this, com.niem.gladow.centroid.MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //todo logo in push
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.map_centroid)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void broadcastToActivities() {
        Intent _intent = new Intent(BROADCAST_UPDATE);
        this.sendBroadcast(_intent);
    }
}
