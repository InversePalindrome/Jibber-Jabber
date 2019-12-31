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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ProfileSelectedListener listener;
    private ChatView chatView;
    private ChatUser senderUser;
    private ChatUser receiverUser;
    private UserModel senderUserModel;
    private UserModel receiverUserModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        Bundle bundle = getArguments();
        senderUserModel = bundle.getParcelable("sender");
        receiverUserModel = bundle.getParcelable("receiver");

        senderUser = new ChatUser(senderUserModel.getuID(), senderUserModel.getEmail(), senderUserModel.getUsername());
        receiverUser = new ChatUser(receiverUserModel.getuID(), receiverUserModel.getEmail(), receiverUserModel.getUsername());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatView = view.findViewById(R.id.chat_chat_view);

        loadChatFromDatabase();

        customizeChatView();
        customizeActionBar(receiverUserModel.getUsername(), receiverUserModel.getProfileURI());

        return view;
    }

    public void setProfileSelectedListener(ProfileSelectedListener listener) {
        this.listener = listener;
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

                ChatModel chatModel = new ChatModel(senderUser.getId(), receiverUser.getId(), message.getText(), timeStamp);

                addMessageToDatabase(chatModel);
                sendNotificationToReceiver(chatModel);
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

    private void openProfileFragment() {
        setDefaultActionBar();
        listener.onProfileSelected(receiverUserModel);
    }

    private void sendNotificationToReceiver(ChatModel chatModel) {
        JSONObject notification = new JSONObject();
        JSONObject notificationBody = new JSONObject();

        DatabaseReference fcmTokensReference = database.getReference().child(Constants.DATABASE_FCM_TOKENS_NODE);
        DatabaseReference fcmTokenReference = fcmTokensReference.child(chatModel.getReceiverID());

        fcmTokenReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fcmToken = dataSnapshot.getValue().toString();

                try {
                    notificationBody.put("title", "New message from " + senderUser.getName());
                    notificationBody.put("message", chatModel.getMessage());
                    notificationBody.put("senderID", chatModel.getSenderID());
                    notificationBody.put("receiverID", chatModel.getReceiverID());

                    notification.put("to", fcmToken);
                    notification.put("data", notificationBody);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Constants.FCM_API, notification
                        , new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Request error " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Authorization", Constants.SERVER_KEY);
                        params.put("Content-Type", Constants.CONTENT_TYPE);

                        return params;
                    }
                };

                RequestQueueSingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setDefaultActionBar() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void addMessageToDatabase(ChatModel chatModel) {
        DatabaseReference chatsReference = database.getReference().child(Constants.DATABASE_CHATS_NODE);

        DatabaseReference chatReference = chatsReference.child(
                ChatIDCreator.getChatID(senderUser.getId(), receiverUser.getId()));
        chatReference.push().setValue(chatModel);
    }

    private void loadChatFromDatabase() {
        DatabaseReference chatsReference = database.getReference().child(Constants.DATABASE_CHATS_NODE);
        DatabaseReference chatReference = chatsReference.child(
                ChatIDCreator.getChatID(senderUser.getId(), receiverUser.getId()));

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
                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);

                if (!chatModel.getSenderID().equals(senderUser.getId())) {
                    Message message = new Message.Builder()
                            .setUser(receiverUser)
                            .setRight(false)
                            .setText(chatModel.getMessage())
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

    public interface ProfileSelectedListener {
        void onProfileSelected(UserModel userModel);
    }
}
