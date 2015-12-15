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

package com.niem.gladow.centroid.gcm;

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
import com.niem.gladow.centroid.Enums.InviteReply;
import com.niem.gladow.centroid.Enums.MessageType;
import com.niem.gladow.centroid.InviteHandler;
import com.niem.gladow.centroid.PersistenceHandler;
import com.niem.gladow.centroid.PhoneDataHandler;
import com.niem.gladow.centroid.R;
import com.niem.gladow.centroid.Util;


public class MyGcmListenerService extends GcmListenerService {

    private final String CENTROID = "centroid";
    private final String TIME = "time";
    private final String INVITE_NUMBER = "number";
    private final String MESSAGE_TYPE = "type";

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
        //there always have to be two types of data
        //assert(data.keySet().size() == 2);

        String _messageType = data.get(MESSAGE_TYPE).toString();
        long _startTime = Long.parseLong(data.get(TIME).toString());

        switch (MessageType.valueOf(_messageType)) {
            case INVITE:
                //TODO number (from message) to name
                String _inviteNumber = data.get(INVITE_NUMBER).toString();

                InviteHandler.addOpenInvites(_inviteNumber, _startTime);

                if (_inviteNumber.equals(PersistenceHandler.getInstance().getOwnNumber())) {
                    InviteHandler.getInviteByTime(_startTime).setStatus(InviteReply.ACCEPTED);
                    sendNotification("you created a centroid, awesome!", "centroid created");
                }
                else {
                    sendNotification("you got invited by: " + _inviteNumber, "centroid invite");
                }
                break;
            case CENTROID:
                String _centroid = data.get(CENTROID).toString();
                InviteHandler.addCentroidToInvite(_startTime, _centroid);
                Log.d(MyGcmListenerService.class.getName(), "Centroid: " + _centroid);
                Log.d(MyGcmListenerService.class.getName(), "Time: " + _startTime);
                sendNotification("you got a new centroid!", "centroid arrived");
                break;
            default:
                //todo error
                break;
        }
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

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
