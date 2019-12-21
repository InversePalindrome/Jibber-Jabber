/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ChatView chatView;
    private ChatUser senderUser;
    private ChatUser receiverUser;
    private UserModel receiverUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        receiverUserModel = bundle.getParcelable("receiver");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        chatView = view.findViewById(R.id.chat_chat_view);

        FirebaseUser senderAuthUser = FirebaseAuth.getInstance().getCurrentUser();

        senderUser = new ChatUser(senderAuthUser.getUid(), senderAuthUser.getEmail(), senderAuthUser.getDisplayName());
        receiverUser = new ChatUser(receiverUserModel.uID, receiverUserModel.email, receiverUserModel.username);

        loadConversation();

        customizeChatView();
        customizeActionBar(receiverUserModel.username, receiverUserModel.profileURI);

        return view;
    }

    private void customizeChatView() {
        chatView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.darkerWhite));
        chatView.setRightBubbleColor(ContextCompat.getColor(getActivity(), R.color.lightGrey));
        chatView.setLeftBubbleColor(Color.WHITE);
        chatView.setInputTextHint("New message...");
        chatView.setSendButtonColor(Color.BLACK);
        chatView.setUsernameTextColor(Color.BLACK);
        chatView.setSendTimeTextColor(Color.BLACK);
        chatView.setDateSeparatorColor(Color.BLACK);
        chatView.setMessageMarginTop(5);
        chatView.setMessageMarginBottom(5);

        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message.Builder()
                        .setUser(senderUser)
                        .setRight(true)
                        .setText(chatView.getInputText())
                        .hideIcon(true)
                        .build();

                chatView.send(message);
                chatView.setInputText("");

                final Long tsLong = System.currentTimeMillis() / 1000;
                final String timeStamp = tsLong.toString();

                MessageModel messageModel = new MessageModel(senderUser.getId(), receiverUser.getId(), message.getText(), timeStamp);

                DatabaseReference messagesReference = database.getReference().child(Constants.DATABASE_MESSAGES_NODE);
                DatabaseReference chatReference = messagesReference.child(
                        MessageIDCreator.getMessageID(senderUser.getId(), receiverUser.getId()));
                chatReference.push().setValue(messageModel);
            }
        });
    }

    private void customizeActionBar(String receiverUsername, String receiverProfileURI) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

        View actionBarView = layoutInflater.inflate(R.layout.action_bar_chat, null);
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayShowCustomEnabled(true);

        CircleImageView profileImage = actionBarView.findViewById(R.id.chat_profile_image);

        StorageReference profileImageReference = storage.getReference()
                .child(Constants.STORAGE_IMAGES_NODE).child(receiverProfileURI);

        profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });
        profileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment();
            }
        });

        TextView usernameText = actionBarView.findViewById(R.id.chat_username_text);
        usernameText.setText(receiverUsername);
        usernameText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfileFragment();
            }
        });
    }

    private void loadConversation() {
        DatabaseReference messagesReference = database.getReference().child(Constants.DATABASE_MESSAGES_NODE);
        DatabaseReference chatReference = messagesReference.child(
                MessageIDCreator.getMessageID(senderUser.getId(), receiverUser.getId()));

        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    final String messageText = childDataSnapshot.child(Constants.DATABASE_MESSAGE_NODE).getValue().toString();
                    final String senderID = childDataSnapshot.child(Constants.DATABASE_SENDER_NODE).getValue().toString();
                    final String timeStamp = childDataSnapshot.child(Constants.DATABASE_TIMESTAMP_NODE).getValue().toString();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(Long.parseLong(timeStamp) * 1000);

                    if (senderUser.getId().equals(senderID)) {
                        Message message = new Message.Builder()
                                .setUser(senderUser)
                                .setRight(true)
                                .setText(messageText)
                                .setSendTime(calendar)
                                .hideIcon(true)
                                .build();

                        chatView.send(message);
                    } else {
                        Message message = new Message.Builder()
                                .setUser(receiverUser)
                                .setRight(false)
                                .setText(messageText)
                                .setSendTime(calendar)
                                .hideIcon(true)
                                .build();

                        chatView.receive(message);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        final String startKey = chatReference.push().getKey();

        chatReference.orderByKey().startAt(startKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);

                if (!messageModel.senderID.equals(senderUser.getId())) {
                    Message message = new Message.Builder()
                            .setUser(receiverUser)
                            .setRight(false)
                            .setText(messageModel.message)
                            .hideIcon(true)
                            .build();

                    chatView.receive(message);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void openProfileFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ProfileFragment profileFragment = new ProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", receiverUserModel);

        profileFragment.setArguments(bundle);

        transaction.add(R.id.chat_layout, profileFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
