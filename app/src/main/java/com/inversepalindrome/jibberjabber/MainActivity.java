/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MainActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch(item.getItemId()){
                    case R.id.forum_fragment:
                        switchFragment(new ForumFragment());
                        return true;
                    case R.id.messages_fragment:
                        switchFragment(new MessagesFragment());
                        return true;
                    case R.id.profile_fragment:
                        switchFragment(new ProfileFragment());
                        return true;
                }

                return false;
            }
        });

        navigationView.setSelectedItemId(R.id.forum_fragment);
    }

    private void switchFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_layout, fragment);
        transaction.commit();
    }

    private BottomNavigationView navigationView;
}
