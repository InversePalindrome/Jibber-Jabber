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
import android.net.Uri;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;


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

        profileDialog = new EasyImage.Builder(getContext()).
                setCopyImagesToPublicGalleryFolder(false).
                setChooserTitle("Choose Profile Picture").
                setChooserType(ChooserType.CAMERA_AND_GALLERY).build();

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

        profileDialog.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {
                MediaFile image = imageFiles[0];
                String filePath = image.getFile().toString();

                Uri uri = Uri.parse(filePath);
                profileImage.setImageURI(uri);

                FirebaseUser user = auth.getCurrentUser();
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build();
                user.updateProfile(profileUpdate);
            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                Toast.makeText(getActivity(), "Error: Profile image couldn't be selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {

            }
        });
    }

    private void onChangeEmail(){
        AlertDialog.Builder emailDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        emailDialogBuilder.setTitle("Change Email");

        final View emailLayout = getLayoutInflater().inflate(R.layout.dialog_change_email, null);
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

        final View passwordLayout = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
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
        profileDialog.openGallery(this);
    }

    private void onOpenCamera(){
       profileDialog.openCameraForImage(this);
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

    private EasyImage profileDialog;
}
