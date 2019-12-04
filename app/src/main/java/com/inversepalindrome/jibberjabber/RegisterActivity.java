/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - RegisterActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.Toast;
import android.widget.EditText;
import android.view.View;
import android.content.Intent;
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
        String userName = userEntry.getText().toString();
        String password = passwordEntry.getText().toString();
        String rePassword = rePasswordEntry.getText().toString();

        new RegisterTask(this, userName, password, rePassword).execute();
    }

    private static class RegisterTask extends AsyncTask<Void, Void, Intent>{
        RegisterTask(RegisterActivity activity, String userName, String password, String rePassword){
            activityReference = new WeakReference<>(activity);

            this.userName = userName;
            this.password = password;
            this.rePassword = rePassword;
        }

        @Override
        protected Intent doInBackground(Void... params){
            final Bundle bundle = new Bundle();

            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountAuthenticator.ACCOUNT_TYPE);
            bundle.putString(AccountManager.KEY_PASSWORD, password);

            final Intent intent = new Intent();
            intent.putExtras(bundle);

            return intent;
        }

        @Override
        protected void onPostExecute(Intent intent){
            RegisterActivity activity = activityReference.get();

            if(intent.hasExtra(PASSWORD_MATCHING_ERROR)){
                Toast.makeText(activity.getBaseContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else{
                activity.finish();
            }
        }

        private WeakReference<RegisterActivity> activityReference;

        private String userName;
        private String password;
        private String rePassword;
    }

    private AccountManager accountManager;

    private EditText userEntry;
    private EditText passwordEntry;
    private EditText rePasswordEntry;

    private static final String PASSWORD_MATCHING_ERROR = "password_matching_error";
}
