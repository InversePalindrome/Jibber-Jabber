/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - RegisterActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private EditText usernameEntry;
    private EditText emailEntry;
    private EditText passwordEntry;
    private EditText rePasswordEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        usernameEntry = findViewById(R.id.register_username_entry);
        emailEntry = findViewById(R.id.register_email_entry);
        passwordEntry = findViewById(R.id.register_password_entry);
        rePasswordEntry = findViewById(R.id.register_repassword_entry);
    }

    public void onRegister(View view) {
        final String username = usernameEntry.getText().toString();
        final String email = emailEntry.getText().toString();
        final String password = passwordEntry.getText().toString();
        final String rePassword = rePasswordEntry.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please enter username!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(password, rePassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), "Passwords needs to be longer than or equal to 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                            FirebaseUser user = auth.getCurrentUser();

                            if (user != null) {
                                Resources resources = getResources();
                                Uri profileURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                                        + resources.getResourcePackageName(R.drawable.default_profile_icon) + '/'
                                        + resources.getResourceTypeName(R.drawable.default_profile_icon) + '/'
                                        + resources.getResourceEntryName(R.drawable.default_profile_icon));

                                updateAuthAccount(user, username, profileURI);
                                updateUsersDatabase(user, username, email, profileURI.getPath());
                            }

                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateAuthAccount(FirebaseUser user, String username, Uri profileURI) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(profileURI)
                .build();
        user.updateProfile(profileUpdate);
    }

    private void updateUsersDatabase(FirebaseUser user, String username, String email, String profileURI) {
        UserModel userModel = new UserModel(user.getUid(), username, email, profileURI);

        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS);
        usersReference.child(user.getUid()).setValue(userModel);
    }
}
