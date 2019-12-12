/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessagesFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MessagesFragment extends Fragment implements OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        database = FirebaseDatabase.getInstance();

        startMessageButton = view.findViewById(R.id.messages_start_message_button);
        startMessageButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messages_start_message_button:
                onStartMessage();
                break;
        }
    }

    public void onStartMessage() {
        AlertDialog.Builder startMessageDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        startMessageDialogBuilder.setTitle("Start Conversation");

        final View startMessageLayout = getLayoutInflater().inflate(R.layout.dialog_start_message, null);
        startMessageDialogBuilder.setView(startMessageLayout);

        startMessageDialogBuilder.setPositiveButton("Open Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText emailEntry = startMessageLayout.findViewById(R.id.start_message_email_entry);
                final String email = emailEntry.getText().toString();

                DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS);

                usersReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            for (DataSnapshot childDataSnapshot: dataSnapshot.getChildren()) {
                                UserModel receiverUserModel = childDataSnapshot.getValue(UserModel.class);

                                openChat(receiverUserModel);
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), "Email address not registered to Jibber Jabber!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        startMessageDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog startMessageDialog = startMessageDialogBuilder.create();
        startMessageDialog.show();
    }

    private void openChat(UserModel receiverUserModel){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.USER_MODEL_RECEIVER, receiverUserModel);
        startActivity(intent);
    }

    private FirebaseDatabase database;

    private FloatingActionButton startMessageButton;
}


