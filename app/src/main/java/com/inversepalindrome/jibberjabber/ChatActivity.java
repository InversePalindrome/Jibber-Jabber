/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        UserModel receiverUserModel = getIntent().getParcelableExtra("receiver");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ChatFragment chatFragment = new ChatFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("receiver", receiverUserModel);

        chatFragment.setArguments(bundle);

        transaction.add(R.id.chat_layout, chatFragment);
        transaction.commit();
    }
}
