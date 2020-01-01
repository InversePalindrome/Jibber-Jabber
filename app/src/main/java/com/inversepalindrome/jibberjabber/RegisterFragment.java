/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - RegisterFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class RegisterFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private RegisteredListener listener;
    private EditText usernameEntry;
    private EditText emailEntry;
    private TextInputEditText passwordEntry;
    private TextInputEditText rePasswordEntry;
    private Button registerButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        usernameEntry = view.findViewById(R.id.register_username_entry);
        emailEntry = view.findViewById(R.id.register_email_entry);
        passwordEntry = view.findViewById(R.id.register_password_entry);
        rePasswordEntry = view.findViewById(R.id.register_repassword_entry);
        registerButton = view.findViewById(R.id.register_button);

        setupRegisterCallbacks();

        return view;
    }

    public void setRegisterSelectedListener(RegisteredListener listener) {
        this.listener = listener;
    }

    private void registerUser() {
        final String username = usernameEntry.getText().toString();
        final String email = emailEntry.getText().toString().toLowerCase();
        final String password = passwordEntry.getText().toString();
        final String rePassword = rePasswordEntry.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getContext(), "Please enter username!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(getContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.equals(password, rePassword)) {
            Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getContext(), "Passwords needs to be longer than or equal to 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                            FirebaseUser user = auth.getCurrentUser();

                            if (user != null) {
                                addUserToDatabase(user, username, email, Constants.DEFAULT_PROFILE_IMAGE, Constants.DEFAULT_STATUS);
                                updateAuthAccount(user, username);
                            }

                            listener.onRegistrationComplete();
                        } else {
                            Toast.makeText(getContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateAuthAccount(FirebaseUser user, String username) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        user.updateProfile(profileUpdate);
    }

    private void addUserToDatabase(FirebaseUser user, String username, String email, String profileURI, String status) {
        UserModel userModel = new UserModel(user.getUid(), username, email, profileURI, status);

        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
        usersReference.child(user.getUid()).setValue(userModel);
    }

    private void setupRegisterCallbacks(){
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public interface RegisteredListener {
        void onRegistrationComplete();
    }
}
