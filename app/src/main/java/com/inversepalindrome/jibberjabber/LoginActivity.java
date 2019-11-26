/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - LoginActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.CheckBox;
import android.accounts.AccountManager;
import android.accounts.AccountAuthenticatorActivity;
import android.content.SharedPreferences;


public class LoginActivity extends AccountAuthenticatorActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountManager = AccountManager.get(getApplicationContext());
        userEntry = findViewById(R.id.login_username_entry);
        passwordEntry = findViewById(R.id.login_password_entry);
        rememberMeCheckBox = findViewById(R.id.login_remember_me_checkbox);
    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember_me", rememberMeCheckBox.isChecked());

        if(rememberMeCheckBox.isChecked()) {
            editor.putString("username", userEntry.getText().toString());
            editor.putString("password", passwordEntry.getText().toString());
        }

        editor.apply();
    }

    @Override
    protected void onResume(){
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if(preferences.getBoolean("remember_me", false))
        {
             userEntry.setText(preferences.getString("username", ""));
             passwordEntry.setText(preferences.getString("password", ""));
        }
    }

    public void onLogin(View view){
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    public void onRegister(View view){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private AccountManager accountManager;

    private EditText userEntry;
    private EditText passwordEntry;
    private CheckBox rememberMeCheckBox;
}
