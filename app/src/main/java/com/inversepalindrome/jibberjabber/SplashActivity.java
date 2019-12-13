/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - SplashActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(SplashActivity.this, LoginActivity.class));

        finish();
    }
}
