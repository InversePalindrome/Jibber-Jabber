/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ProfileFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment implements OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        usernameText = view.findViewById(R.id.profile_username_text);
        changeEmailButton = view.findViewById(R.id.profile_change_email_button);
        changePasswordButton = view.findViewById(R.id.profile_change_password_button);
        logOutButton = view.findViewById(R.id.profile_log_out_button);

        changeEmailButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);

        usernameText.setText(user.getDisplayName());

        return view;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.profile_change_email_button:
                onChangeEmail(view);
                break;
            case R.id.profile_change_password_button:
                onChangePassword(view);
                break;
            case R.id.profile_log_out_button:
                onLogOut(view);
                break;
        }
    }

    private void onChangeEmail(View view){
        AlertDialog.Builder emailDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        emailDialogBuilder.setTitle("Change Email");

        final View emailLayout = getLayoutInflater().inflate(R.layout.change_email_layout, null);
        emailDialogBuilder.setView(emailLayout);

        emailDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText emailEntry = emailLayout.findViewById(R.id.change_email_entry);
                final String newEmail = emailEntry.getText().toString();

                if(TextUtils.isEmpty(newEmail)){
                    Toast.makeText(getActivity(), "Please enter email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Email address updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Email address failed to be updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        emailDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog emailDialog = emailDialogBuilder.create();
        emailDialog.show();
    }

    private void onChangePassword(View view){
        AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        passwordDialogBuilder.setTitle("Change Password");

        final View passwordLayout = getLayoutInflater().inflate(R.layout.change_password_layout, null);
        passwordDialogBuilder.setView(passwordLayout);

        passwordDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText passwordEntry = passwordLayout.findViewById(R.id.change_password_entry);
                final String newPassword = passwordEntry.getText().toString();

                if(TextUtils.isEmpty(newPassword)){
                    Toast.makeText(getActivity(), "Please enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(newPassword.length() < Constants.MIN_PASSWORD_LENGTH){
                    Toast.makeText(getActivity(), "Passwords needs to be longer than or equal to 8 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Password successfully updated!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getActivity(), "Password failed to be updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        passwordDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog passwordDialog = passwordDialogBuilder.create();
        passwordDialog.show();
    }

    private void onLogOut(View view){
        auth.signOut();

        FragmentActivity activity = getActivity();
        activity.startActivity(new Intent(activity.getBaseContext(), LoginActivity.class));
        activity.finish();
    }

    private FirebaseAuth auth;
    private FirebaseUser user;

    private TextView usernameText;
    private Button logOutButton;
    private Button changeEmailButton;
    private Button changePasswordButton;
}
