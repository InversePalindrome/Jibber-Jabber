/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ProfileFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private UserModel userModel;
    private FirebaseStorage storage;
    private CircleImageView profileImage;
    private TextView usernameText;
    private TextView statusText;
    private TextView emailText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();

        Bundle bundle = getArguments();
        userModel = bundle.getParcelable("user");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profile_profile_image);
        usernameText = view.findViewById(R.id.profile_username_text);
        statusText = view.findViewById(R.id.profile_status_text);
        emailText = view.findViewById(R.id.profile_email_text);

        setupProfilePicture();
        setupProfileInformation();

        return view;
    }

    private void setupProfilePicture() {
        StorageReference profileImageReference = storage.getReference()
                .child(Constants.STORAGE_IMAGES_NODE).child(userModel.getProfileURI());

        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
    }

    private void setupProfileInformation() {
        usernameText.setText(userModel.getUsername());
        statusText.setText(userModel.getStatus());
        emailText.setText(userModel.getEmail());
    }
}
