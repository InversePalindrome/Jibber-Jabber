/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ResetPasswordFragment.java
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
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ResetPasswordFragment extends Fragment {
    private FirebaseAuth auth;
    private ResetPasswordCompletedListener listener;
    private EditText emailEntry;
    private Button resetPasswordButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        emailEntry = view.findViewById(R.id.reset_password_email_entry);
        resetPasswordButton = view.findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        return view;
    }

    public void setResetPasswordSelectedListener(ResetPasswordCompletedListener listener) {
        this.listener = listener;
    }

    private void resetPassword() {
        final String email = emailEntry.getText().toString().toLowerCase();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Enter your registered email!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    listener.onResetPasswordCompleted();
                } else {
                    Toast.makeText(getContext(), "Failed to send reset password email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface ResetPasswordCompletedListener {
        void onResetPasswordCompleted();
    }
}
