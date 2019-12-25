/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - TopicActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class TopicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        TopicModel topicUserModel = getIntent().getParcelableExtra("topic");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        TopicFragment topicFragment = new TopicFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("topic", topicUserModel);

        topicFragment.setArguments(bundle);

        transaction.add(R.id.topic_layout, topicFragment);
        transaction.commit();
    }
}
