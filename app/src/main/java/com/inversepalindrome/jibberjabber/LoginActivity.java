/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - LoginActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
            //finish();
        }

        emailEntry = findViewById(R.id.login_email_entry);
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
            editor.putString("email", emailEntry.getText().toString());
            editor.putString("password", passwordEntry.getText().toString());
        }

        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        if (preferences.getBoolean("remember_me", false)) {
            rememberMeCheckBox.setChecked(true);
            emailEntry.setText(preferences.getString("email", ""));
            passwordEntry.setText(preferences.getString("password", ""));
        }
    }

    public void onLogin(View view) {
        final String email = emailEntry.getText().toString();
        final String password = passwordEntry.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getBaseContext(), MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Login failed! Please check your input.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onRegister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    public void onForgotPassword(View view){
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
    }

    private FirebaseAuth auth;

    private EditText emailEntry;
    private EditText passwordEntry;
    private CheckBox rememberMeCheckBox;
}

