/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - RegisterActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.widget.EditText;
import android.accounts.AccountManager;


public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        accountManager = AccountManager.get(getApplicationContext());

        userEntry = findViewById(R.id.register_username_entry);
        passwordEntry = findViewById(R.id.register_password_entry);
        rePasswordEntry = findViewById(R.id.register_repassword_entry);
    }

    public void onRegister(View view){
        if(!passwordEntry.equals(rePasswordEntry))
        {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }

        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
    }

    private AccountManager accountManager;

    private EditText userEntry;
    private EditText passwordEntry;
    private EditText rePasswordEntry;
}
