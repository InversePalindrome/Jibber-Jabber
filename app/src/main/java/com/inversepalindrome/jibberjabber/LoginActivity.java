/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - LoginActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEntry = (EditText)findViewById(R.id.username_entry);
        passwordEntry = (EditText)findViewById(R.id.password_entry);
    }

    public void onLogin(View view){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    private EditText userEntry;
    private EditText passwordEntry;
}
