/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MainActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity implements
        ChatsFragment.OpenChatListener, SettingsFragment.LogOutSelectedListener {
    private FirebaseAuth auth;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        storeUserIDInSharedPreferences(auth.getCurrentUser().getUid());

        navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.forum_fragment:
                        switchFragment(new ForumFragment());
                        return true;
                    case R.id.chats_fragment:
                        switchFragment(new ChatsFragment());
                        return true;
                    case R.id.settings_fragment:
                        switchFragment(new SettingsFragment());
                        return true;
                }

                return false;
            }
        });

        navigationView.setSelectedItemId(R.id.chats_fragment);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ChatsFragment) {
            ChatsFragment chatsFragment = (ChatsFragment) fragment;
            chatsFragment.setOpenChatListener(this);
        } else if (fragment instanceof SettingsFragment) {
            SettingsFragment settingsFragment = (SettingsFragment) fragment;
            settingsFragment.setLogOutSelectedListener(this);
        }
    }

    @Override
    public void onOpenChat(UserModel senderUserModel, UserModel receiverUserModel) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("sender", senderUserModel);
        intent.putExtra("receiver", receiverUserModel);
        startActivity(intent);
    }

    @Override
    public void onLogOut() {
        auth.signOut();

        startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
        finish();
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_layout, fragment);
        transaction.commit();
    }

    private void storeUserIDInSharedPreferences(String userID) {
        SharedPreferences.Editor preferenceEditor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        preferenceEditor.putString("userID", userID);
        preferenceEditor.apply();
    }
}
