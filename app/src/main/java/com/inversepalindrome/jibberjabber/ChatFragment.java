/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.github.bassaer.chatmessageview.view.ChatView;


public class ChatFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        database = FirebaseDatabase.getInstance();
        DatabaseReference messagesReference = database.getReference().child(Constants.DATABASE_MESSAGES);

        chatView = view.findViewById(R.id.chat_chat_view);
        chatView.setRightBubbleColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
        chatView.setLeftBubbleColor(Color.WHITE);
        chatView.setInputTextHint("New message...");
        chatView.setSendButtonColor(Color.BLACK);
        chatView.setSendIcon(R.drawable.start_message_icon);
        chatView.setUsernameTextColor(Color.BLACK);
        chatView.setSendTimeTextColor(Color.BLACK);
        chatView.setDateSeparatorColor(Color.BLACK);
        chatView.setMessageMarginTop(5);
        chatView.setMessageMarginBottom(5);

        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Message message = new Message.Builder()
                        .setUser(me)
                        .setRight(true)
                        .setText(chatView.getInputText())
                        .hideIcon(true)
                        .build();

                chatView.send(message);

                chatView.setInputText("");*/
            }
        });

        return view;
    }

    private FirebaseDatabase database;

    private ChatView chatView;
}
