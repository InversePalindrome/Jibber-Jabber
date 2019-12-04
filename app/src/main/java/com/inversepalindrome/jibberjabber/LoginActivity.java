/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - LoginActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;
import android.accounts.Account;
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
    protected void onPause() {
        super.onPause();

        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember_me", rememberMeCheckBox.isChecked());

        if (rememberMeCheckBox.isChecked()) {
            editor.putString("username", userEntry.getText().toString());
            editor.putString("password", passwordEntry.getText().toString());
        }

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (preferences.getBoolean("remember_me", false)) {
            userEntry.setText(preferences.getString("username", ""));
            passwordEntry.setText(preferences.getString("password", ""));
        }
    }

    public void onLogin(View view) {
        final String userName = userEntry.getText().toString();
        final String password = passwordEntry.getText().toString();

        new LoginTask(this, userName, password).execute();
    }

    public void onRegister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private static class LoginTask extends AsyncTask<Void, Void, Intent> {
        LoginTask(LoginActivity loginActivity, String userName, String password) {
            activityReference = new WeakReference<>(loginActivity);

            this.userName = userName;
            this.password = password;
        }

        @Override
        protected Intent doInBackground(Void... params) {
            final Bundle bundle = new Bundle();

            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountAuthenticator.ACCOUNT_TYPE);

            final Intent intent = new Intent();
            intent.putExtras(bundle);

            return intent;
        }

        @Override
        protected void onPostExecute(Intent intent) {
            LoginActivity activity = activityReference.get();
            AccountManager accountManager = activity.accountManager;

            //final Account account = new Account(userName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));

            if(intent.hasExtra(LOGIN_ERROR)){
                Toast.makeText(activity.getBaseContext(), "Wrong username or password!", Toast.LENGTH_SHORT).show();
            }
            else{
                /*
                if(activity.getIntent().getBooleanExtra(AccountAuthenticator.IS_ADDING_NEW_ACCOUNT, false)){
                    String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

                    accountManager.addAccountExplicitly(account, password, null);
                }
                else{
                    accountManager.setPassword(account, password);
                }*/

                activity.startActivity(new Intent(activity.getBaseContext(), MainActivity.class));
                activity.finish();
            }
        }

        private WeakReference<LoginActivity> activityReference;

        private String userName;
        private String password;
    }

    private AccountManager accountManager;

    private EditText userEntry;
    private EditText passwordEntry;
    private CheckBox rememberMeCheckBox;

    private static final String LOGIN_ERROR = "login_error";
}

