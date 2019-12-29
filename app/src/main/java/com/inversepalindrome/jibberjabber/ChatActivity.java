/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class ChatActivity extends AppCompatActivity implements ChatFragment.OnProfileSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        UserModel senderUserModel = getIntent().getParcelableExtra("sender");
        UserModel receiverUserModel = getIntent().getParcelableExtra("receiver");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ChatFragment chatFragment = new ChatFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("sender", senderUserModel);
        bundle.putParcelable("receiver", receiverUserModel);

        chatFragment.setArguments(bundle);

        transaction.add(R.id.chat_layout, chatFragment);
        transaction.commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ChatFragment) {
            ChatFragment chatFragment = (ChatFragment) fragment;
            chatFragment.setOnProfileSelectedListener(this);
        }
    }

    @Override
    public void onProfileSelected(UserModel userModel) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ProfileFragment profileFragment = new ProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", userModel);

        profileFragment.setArguments(bundle);

        transaction.replace(R.id.chat_layout, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        transaction.addToBackStack(null);
    }
}
