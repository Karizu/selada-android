package com.boardinglabs.mireta.selada.messaging;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.boardinglabs.mireta.selada.R;

/**
 * Created by Dhimas on 2/15/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
//        }
    }

}
