/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ProfileFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
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
        AlertDialog.Builder nameDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        nameDialogBuilder.setTitle("Edit Name");

        final View nameLayout = getLayoutInflater().inflate(R.layout.edit_name_layout, null);
        nameDialogBuilder.setView(nameLayout);

        final EditText editNameText = nameLayout.findViewById(R.id.edit_name_entry);

        nameDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameText.setText(editNameText.getText());
            }
        });
        nameDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog nameDialog = nameDialogBuilder.create();
        nameDialog.show();
    }

    private void onEditAbout(View view){
        AlertDialog.Builder aboutDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        aboutDialogBuilder.setTitle("Edit About");

        final View aboutLayout = getLayoutInflater().inflate(R.layout.edit_about_layout, null);
        aboutDialogBuilder.setView(aboutLayout);

        final EditText editAboutEntry = aboutLayout.findViewById(R.id.edit_about_entry);

        aboutDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aboutText.setText(editAboutEntry.getText());
            }
        });
        aboutDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog aboutDialog = aboutDialogBuilder.create();
        aboutDialog.show();
    }

    private TextView nameText;
    private TextView aboutText;
    private ImageButton editNameButton;
    private ImageButton editAboutButton;
}
