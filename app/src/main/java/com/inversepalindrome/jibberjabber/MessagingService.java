/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessagingService.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
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
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);

        Map<String, String> messageData = remoteMessage.getData();

        String senderID = messageData.get("senderID");
        String receiverID = messageData.get("receiverID");

        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel senderUserModel = dataSnapshot.child(senderID).getValue(UserModel.class);
                UserModel receiverUserModel = dataSnapshot.child(receiverID).getValue(UserModel.class);

                intent.putExtra("sender", senderUserModel);
                intent.putExtra("receiver", receiverUserModel);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

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

        Editor preferenceEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        preferenceEditor.putString("notification_token", token);
        preferenceEditor.apply();

        FirebaseMessaging.getInstance().subscribeToTopic(token);
    }
}
