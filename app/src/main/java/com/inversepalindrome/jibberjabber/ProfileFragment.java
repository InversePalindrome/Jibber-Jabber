/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ProfileFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        profileImage = view.findViewById(R.id.profile_profile_image);
        usernameText = view.findViewById(R.id.profile_username_text);
        changeEmailButton = view.findViewById(R.id.profile_change_email_button);
        changePasswordButton = view.findViewById(R.id.profile_change_password_button);
        logOutButton = view.findViewById(R.id.profile_log_out_button);

        galleryButton = view.findViewById(R.id.profile_gallery_button);
        cameraButton = view.findViewById(R.id.profile_camera_button);

        changeEmailButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        logOutButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);

        profileImage.setImageURI(user.getPhotoUrl());
        usernameText.setText(user.getDisplayName());

        return view;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.profile_change_email_button:
                onChangeEmail();
                break;
            case R.id.profile_change_password_button:
                onChangePassword();
                break;
            case R.id.profile_log_out_button:
                onLogOut();
                break;
            case R.id.profile_gallery_button:
                onOpenGallery();
                break;
            case R.id.profile_camera_button:
                onOpenCamera();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE){
                final Uri imageURI = data.getData();

                profileImage.setImageURI(imageURI);

                FirebaseUser user = auth.getCurrentUser();
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(imageURI)
                        .build();
                user.updateProfile(profileUpdate);
            }
            else if(requestCode == Constants.CAMERA_REQUEST){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                profileImage.setImageBitmap(photo);

                File file = new File(Environment.getExternalStorageDirectory(), "ProfilePhoto.jpg");
                Uri imageURI = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);

                FirebaseUser user = auth.getCurrentUser();
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(imageURI)
                        .build();
                user.updateProfile(profileUpdate);
            }
        }

    }

    private void onChangeEmail(){
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

    private void onChangePassword(){
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

    private void onLogOut(){
        auth.signOut();

        FragmentActivity activity = getActivity();
        activity.startActivity(new Intent(activity.getBaseContext(), LoginActivity.class));
        activity.finish();
    }

    private void onOpenGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Profile Picture"), Constants.PICK_IMAGE);
    }

    private void onOpenCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(Environment.getExternalStorageDirectory(), "ProfilePhoto.jpg");
        Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);

        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
    }

    private FirebaseAuth auth;
    private FirebaseUser user;

    private CircleImageView profileImage;

    private TextView usernameText;
    private Button logOutButton;
    private Button changeEmailButton;
    private Button changePasswordButton;

    private FloatingActionButton galleryButton;
    private FloatingActionButton cameraButton;
}
