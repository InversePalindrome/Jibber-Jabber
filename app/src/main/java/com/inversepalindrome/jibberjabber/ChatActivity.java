/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class ChatActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private ChatView chatView;
    private ChatUser senderUser;
    private ChatUser receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        chatView = findViewById(R.id.chat_chat_view);

        FirebaseUser senderAuthUser = FirebaseAuth.getInstance().getCurrentUser();
        UserModel receiverUserModel = getIntent().getParcelableExtra(Constants.USER_MODEL_RECEIVER);

        senderUser = new ChatUser(senderAuthUser.getUid(), senderAuthUser.getEmail(), senderAuthUser.getDisplayName());
        receiverUser = new ChatUser(receiverUserModel.uID, receiverUserModel.email, receiverUserModel.username);

        loadConversation();

        customizeChatView();

        setTitle(receiverUser.getName());
    }

    private void customizeChatView() {
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

        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message.Builder()
                        .setUser(senderUser)
                        .setRight(true)
                        .setText(chatView.getInputText())
                        .hideIcon(true)
                        .build();

                chatView.send(message);
                chatView.setInputText("");

                final Long tsLong = System.currentTimeMillis()/1000;
                final String timeStamp = tsLong.toString();

                MessageModel messageModel = new MessageModel(senderUser.getId(), receiverUser.getId(), message.getText(), timeStamp);

                DatabaseReference messagesReference = database.getReference().child(Constants.DATABASE_MESSAGES_NODE);
                DatabaseReference chatReference = messagesReference.child(
                        MessageIDCreator.getMessageID(senderUser.getId(), receiverUser.getId()));
                chatReference.push().setValue(messageModel);
            }
        });
    }

    private void loadConversation(){
        DatabaseReference messagesReference = database.getReference().child(Constants.DATABASE_MESSAGES_NODE);
        DatabaseReference chatReference = messagesReference.child(
                MessageIDCreator.getMessageID(senderUser.getId(), receiverUser.getId()));

        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                    final String messageText = childDataSnapshot.child(Constants.DATABASE_MESSAGE_NODE).getValue().toString();
                    final String senderID = childDataSnapshot.child(Constants.DATABASE_SENDER_NODE).getValue().toString();
                    final String timeStamp = childDataSnapshot.child(Constants.DATABASE_TIMESTAMP_NODE).getValue().toString();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(timeStamp) * 1000);

                    if(senderUser.getId().equals(senderID)){
                        Message message = new Message.Builder()
                                .setUser(senderUser)
                                .setRight(true)
                                .setText(messageText)
                                .setSendTime(calendar)
                                .hideIcon(true)
                                .build();

                        chatView.send(message);
                    }
                    else {
                        Message message = new Message.Builder()
                                .setUser(receiverUser)
                                .setRight(false)
                                .setText(messageText)
                                .setSendTime(calendar)
                                .hideIcon(true)
                                .build();

                        chatView.receive(message);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        final String startKey = chatReference.push().getKey();

        chatReference.orderByKey().startAt(startKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);

                if(!messageModel.senderID.equals(senderUser.getId())){
                    Message message = new Message.Builder()
                            .setUser(receiverUser)
                            .setRight(false)
                            .setText(messageModel.message)
                            .hideIcon(true)
                            .build();

                    chatView.receive(message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
