/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;


public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();

        initializeChatView(getIntent().getExtras());
    }

    private void initializeChatView(Bundle bundle){
        chatView = findViewById(R.id.chat_chat_view);
        chatView.setBackgroundColor(ContextCompat.getColor(this, R.color.darkerWhite));
        chatView.setRightBubbleColor(ContextCompat.getColor(this, R.color.lightGrey));
        chatView.setLeftBubbleColor(Color.WHITE);
        chatView.setInputTextHint("New message...");
        chatView.setSendButtonColor(Color.BLACK);
        chatView.setUsernameTextColor(Color.BLACK);
        chatView.setSendTimeTextColor(Color.BLACK);
        chatView.setDateSeparatorColor(Color.BLACK);
        chatView.setMessageMarginTop(5);
        chatView.setMessageMarginBottom(5);

        FirebaseUser senderUser = FirebaseAuth.getInstance().getCurrentUser();
        Uri senderProfileURI = Uri.fromFile(new File(senderUser.getPhotoUrl().getPath()));

        Bitmap senderBitmap = null;
        try {
            senderBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), senderProfileURI);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }

        UserModel receiverUserModel = bundle.getParcelable(Constants.USER_MODEL_RECEIVER);
        setTitle(receiverUserModel.username);

        Uri receiverProfileURI = Uri.fromFile(new File(receiverUserModel.profileURI));

        Bitmap receiverBitmap = null;
        try {
            receiverBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), receiverProfileURI);
        }
        catch(Exception exception){
            exception.printStackTrace();
        }

        ChatUser senderChatUser = new ChatUser(senderUser.getEmail(), senderUser.getDisplayName(), senderBitmap);
        ChatUser receiverChatUser = new ChatUser(receiverUserModel.email, receiverUserModel.username, receiverBitmap);

        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message.Builder()
                        .setUser(senderChatUser)
                        .setRight(true)
                        .setText(chatView.getInputText())
                        .hideIcon(true)
                        .build();

                chatView.send(message);

                chatView.setInputText("");

                DatabaseReference messagesReference = database.getReference().child(Constants.DATABASE_MESSAGES);
                //TODO messagesReference.child("").setValue()
            }
        });
    }

    private FirebaseDatabase database;

    private ChatView chatView;
}
