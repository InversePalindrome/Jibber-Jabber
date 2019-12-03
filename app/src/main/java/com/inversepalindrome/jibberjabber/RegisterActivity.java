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
        new RegisterTask(this).execute();
    }

    private static class RegisterTask extends AsyncTask<Void, Void, Intent>{
        RegisterTask(RegisterActivity activity){
            activityReference = new WeakReference<>(activity);
        }
        @Override
        protected Intent doInBackground(Void... params){
            final Bundle bundle = new Bundle();

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
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }
        }

        private WeakReference<RegisterActivity> activityReference;
    }

    private AccountManager accountManager;

    private EditText userEntry;
    private EditText passwordEntry;
    private EditText rePasswordEntry;

    private static final String PASSWORD_MATCHING_ERROR = "password_matching_error";
}
