/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - RegisterActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        userNameEntry = findViewById(R.id.register_username_entry);
        emailEntry = findViewById(R.id.register_email_entry);
        passwordEntry = findViewById(R.id.register_password_entry);
        rePasswordEntry = findViewById(R.id.register_repassword_entry);
    }

    public void onRegister(View view){
        final String userName = userNameEntry.getText().toString();
        final String email = emailEntry.getText().toString();
        final String password = passwordEntry.getText().toString();
        final String rePassword = rePasswordEntry.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(getApplicationContext(), "Please enter username!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!TextUtils.equals(password, rePassword)){
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length() < Constants.MIN_PASSWORD_LENGTH){
            Toast.makeText(getApplicationContext(), "Passwords needs to be longer than or equal to 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email , password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                            FirebaseUser user = auth.getCurrentUser();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .build();
                            user.updateProfile(profileUpdate);

                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private FirebaseAuth auth;

    private EditText userNameEntry;
    private EditText emailEntry;
    private EditText passwordEntry;
    private EditText rePasswordEntry;
}
