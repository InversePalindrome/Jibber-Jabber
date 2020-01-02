/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - LoginFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class LoginFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private LoginSelectedListener listener;
    private EditText emailEntry;
    private EditText passwordEntry;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private TextView registerAccountText;
    private TextView forgotPasswordText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEntry = view.findViewById(R.id.login_email_entry);
        passwordEntry = view.findViewById(R.id.login_password_entry);
        rememberMeCheckBox = view.findViewById(R.id.login_remember_me_checkbox);
        loginButton = view.findViewById(R.id.login_button);
        registerAccountText = view.findViewById(R.id.login_register_label);
        forgotPasswordText = view.findViewById(R.id.login_forgot_password_text);

        setupButtonCallbacks();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember_me", rememberMeCheckBox.isChecked());

        if (rememberMeCheckBox.isChecked()) {
            editor.putString("email", emailEntry.getText().toString());
            editor.putString("password", passwordEntry.getText().toString());
        }

        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        if (preferences.getBoolean("remember_me", false)) {
            rememberMeCheckBox.setChecked(true);
            emailEntry.setText(preferences.getString("email", ""));
            passwordEntry.setText(preferences.getString("password", ""));
        }
    }

    public void setLoginSelectedListener(LoginSelectedListener listener) {
        this.listener = listener;
    }

    private void loginUser() {
        final String email = emailEntry.getText().toString().toLowerCase();
        final String password = passwordEntry.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addFCMTokenToDatabase(auth.getCurrentUser().getUid());
                            listener.onLoginSelected();
                        } else {
                            Toast.makeText(getContext(), "Login failed! Please check your input.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addFCMTokenToDatabase(String uID) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getContext());

        String FCMToken = sharedPreferences.getString("fcm_token", "NO_TOKEN");

        DatabaseReference FCMTokensReference = database.getReference().child(Constants.DATABASE_FCM_TOKENS_NODE);
        FCMTokensReference.child(uID).setValue(FCMToken);
    }

    private void setupButtonCallbacks() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        registerAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBeginRegistration();
            }
        });
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onForgotPassword();
            }
        });
    }

    public interface LoginSelectedListener {
        void onLoginSelected();

        void onBeginRegistration();

        void onForgotPassword();
    }
}

