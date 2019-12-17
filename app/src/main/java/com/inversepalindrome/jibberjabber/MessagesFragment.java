/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessagesFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MessagesFragment extends Fragment implements OnClickListener {
    private String senderUserID;
    private FirebaseDatabase database;
    private ArrayList<UserModel> userModelItems;
    private MessagesViewAdapter messagesViewAdapter;
    private RecyclerView messagesView;
    private FloatingActionButton startMessageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        senderUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();

        userModelItems = new ArrayList<>();

        messagesViewAdapter = new MessagesViewAdapter(R.layout.layout_message_item, userModelItems, new OnClickListener(){
            @Override
            public void onClick(final View view) {
                int itemPosition = messagesView.getChildLayoutPosition(view);
                UserModel userModelItem = userModelItems.get(itemPosition);

                openChat(userModelItem);
            }
        });

        messagesView = view.findViewById(R.id.messages_recycler_view);
        messagesView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messagesView.setItemAnimator(new DefaultItemAnimator());
        messagesView.setAdapter(messagesViewAdapter);

        startMessageButton = view.findViewById(R.id.messages_start_message_button);
        startMessageButton.setOnClickListener(this);

        loadConversations();

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
                final String email = emailEntry.getText().toString().toLowerCase();

                DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);

                usersReference.orderByChild(Constants.DATABASE_EMAIL_NODE).equalTo(email).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                UserModel receiverUserModel = childDataSnapshot.getValue(UserModel.class);

                                addConversation(senderUserID, receiverUserModel.uID);
                                openChat(receiverUserModel);
                            }
                        } else {
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

    private void openChat(UserModel receiverUserModel) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.USER_MODEL_RECEIVER, receiverUserModel);
        startActivity(intent);
    }

    private void addConversation(String senderID, String receiverID){
        String chatID = MessageIDCreator.getMessageID(senderID, receiverID);

        DatabaseReference chatsReference = database.getReference().child(Constants.DATABASE_CHATS_NODE);
        DatabaseReference chatReference = chatsReference.child(chatID);
        DatabaseReference membersReference = chatReference.child(Constants.DATABASE_MEMBERS_NODE);
        membersReference.child(senderID).setValue(true);
        membersReference.child(receiverID).setValue(true);

        DatabaseReference usersChatsReference = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE);

        DatabaseReference senderChatsReference = usersChatsReference.child(senderID);
        senderChatsReference.child(chatID).setValue(true);

        DatabaseReference receiverChatsReference = usersChatsReference.child(receiverID);
        receiverChatsReference.child(chatID).setValue(true);

        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
        DatabaseReference receiverReference = usersReference.child(receiverID);
        receiverReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               addUserModel(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void loadConversations() {
        DatabaseReference usersChatsReference = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE);

        DatabaseReference senderChatsReference = usersChatsReference.child(senderUserID);
        senderChatsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot chatDataSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference chatsReference = database.getReference().child(Constants.DATABASE_CHATS_NODE);
                    DatabaseReference chatReference = chatsReference.child(chatDataSnapshot.getKey());

                    DatabaseReference membersReference = chatReference.child(Constants.DATABASE_MEMBERS_NODE);
                    membersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                                String memberID = memberSnapshot.getKey();

                                if (!memberID.equals(senderUserID)) {
                                    DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
                                    DatabaseReference receiverReference = usersReference.child(memberID);

                                    receiverReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            addUserModel(dataSnapshot);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void addUserModel(@NonNull DataSnapshot userDataSnapshot){
        final String receiverUsername = userDataSnapshot.child(
                Constants.DATABASE_USERNAME_NODE).getValue().toString();
        final String receiverEmail = userDataSnapshot.child(Constants.DATABASE_EMAIL_NODE)
                .getValue().toString();
        final String receiverProfileURI = userDataSnapshot.child(
                Constants.DATABASE_PROFILE_URI_NODE).getValue().toString();

        userModelItems.add(new UserModel(userDataSnapshot.getKey(), receiverUsername,
                receiverEmail, receiverProfileURI));

        messagesViewAdapter.notifyDataSetChanged();
    }
}