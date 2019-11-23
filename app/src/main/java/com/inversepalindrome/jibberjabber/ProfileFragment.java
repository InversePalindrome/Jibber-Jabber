/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ProfileFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.ImageButton;


public class ProfileFragment extends Fragment implements OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        nameText = view.findViewById(R.id.profile_profile_entry);
        aboutText = view.findViewById(R.id.profile_about_entry);
        editNameButton = view.findViewById(R.id.edit_name_button);
        editAboutButton = view.findViewById(R.id.edit_about_button);

        editNameButton.setOnClickListener(this);
        editAboutButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view){
        switch(view.getId())
        {
            case R.id.edit_name_button:
                onEditName(view);
                break;
            case R.id.edit_about_button:
                onEditAbout(view);
                break;
        }
    }

    private void onEditName(View view){
        AlertDialog nameDialog = new AlertDialog.Builder(getActivity()).create();
        nameDialog.setTitle("Edit Name");

        nameDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        nameDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        nameDialog.show();
    }

    private void onEditAbout(View view){
        AlertDialog aboutDialog = new AlertDialog. Builder(getActivity()).create();
        aboutDialog.setTitle("Edit About");
        aboutDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        aboutDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        aboutDialog.show();
    }

    private TextView nameText;
    private TextView aboutText;
    private ImageButton editNameButton;
    private ImageButton editAboutButton;
}
