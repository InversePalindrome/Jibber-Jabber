/*
Copyright (c) 2020 Inverse Palindrome
Jibber Jabber - MessagingService.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


public class MessagingService extends FirebaseMessagingService {
    private FirebaseDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel
                    (Constants.MESSAGE_CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Message Notifications");
            notificationChannel.enableLights(true);
            notificationChannel.setVibrationPattern(new long[]{0, 500, 250});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> messageData = remoteMessage.getData();

                String senderID = messageData.get("senderID");
                String receiverID = messageData.get("receiverID");

                UserModel senderUserModel = dataSnapshot.child(senderID).getValue(UserModel.class);
                UserModel receiverUserModel = dataSnapshot.child(receiverID).getValue(UserModel.class);

                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                //Sender and receiver interchanged when opening in new device
                chatIntent.putExtra("sender", receiverUserModel);
                chatIntent.putExtra("receiver", senderUserModel);

                TaskStackBuilder taskStack = TaskStackBuilder.create(getApplicationContext());
                taskStack.addNextIntentWithParentStack(chatIntent);

                PendingIntent pendingIntent = taskStack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                        R.drawable.jibber_jabber_icon);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), Constants.MESSAGE_CHANNEL_ID)
                        .setSmallIcon(R.drawable.messages_icon)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("message"))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setWhen(System.currentTimeMillis())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                notificationManager.notify(NotificationID.getID(), notificationBuilder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        SharedPreferences.Editor preferenceEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        preferenceEditor.putString("fcm_token", token);
        preferenceEditor.apply();
    }
}
