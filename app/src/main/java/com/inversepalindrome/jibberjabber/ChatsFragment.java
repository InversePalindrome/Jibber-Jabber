/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatsFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;


public class ChatsFragment extends Fragment {
    private String senderID;
    private OpenChatListener listener;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private ArrayList<UserModel> userModelItems;
    private FirebaseRecyclerAdapter userViewAdapter;
    private EmptyRecyclerView userView;
    private TextView emptyStartChatText;
    private FloatingActionButton startChatButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        userModelItems = new ArrayList<>();

        senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initializeChatViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chats, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        userView = view.findViewById(R.id.chats_user_view);
        startChatButton = view.findViewById(R.id.chats_start_chat_button);
        emptyStartChatText = view.findViewById(R.id.chats_empty_start_chat_text);

        userView.setLayoutManager(linearLayoutManager);
        userView.setItemAnimator(new DefaultItemAnimator());
        userView.setAdapter(userViewAdapter);
        userView.setEmptyView(emptyStartChatText);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userView.getContext(),
                linearLayoutManager.getOrientation());

        userView.addItemDecoration(dividerItemDecoration);

        UserItemCallback userItemCallback = new UserItemCallback(getContext(), new UserItemActions() {
            @Override
            public void onDeleteClicked(int position) {
                removeChatFromDatabase(userModelItems.get(position).getuID());
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(userItemCallback);
        itemTouchHelper.attachToRecyclerView(userView);

        startChatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartChatDialog();
            }
        });
        emptyStartChatText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartChatDialog();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        userViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        userViewAdapter.stopListening();
    }

    void setOpenChatListener(OpenChatListener listener) {
        this.listener = listener;
    }

    private void openStartChatDialog() {
        AlertDialog.Builder startChatDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        startChatDialogBuilder.setTitle("Start Chat");

        final View startChatView = getLayoutInflater().inflate(R.layout.dialog_start_chat, null);
        startChatDialogBuilder.setView(startChatView);

        startChatDialogBuilder.setPositiveButton("Open Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final EditText emailEntry = startChatView.findViewById(R.id.start_chat_email_entry);
                final String email = emailEntry.getText().toString().toLowerCase();

                DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
                DatabaseReference senderUserReference = usersReference.child(senderID);
                senderUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel senderUserModel = dataSnapshot.getValue(UserModel.class);

                        usersReference.orderByChild(Constants.DATABASE_EMAIL_NODE).equalTo(email).
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                UserModel receiverUserModel = childDataSnapshot.getValue(UserModel.class);

                                                addChatToDatabase(receiverUserModel);
                                                listener.onOpenChat(senderUserModel, receiverUserModel);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        startChatDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog startChatDialog = startChatDialogBuilder.create();
        startChatDialog.show();
    }

    private void addChatToDatabase(UserModel receiverUserModel) {
        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
        DatabaseReference receiverUserReference = usersReference.child(receiverUserModel.getuID());
        receiverUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel receiverUserModel = dataSnapshot.getValue(UserModel.class);

                DatabaseReference usersChatsReference = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE);
                DatabaseReference userChatsReference = usersChatsReference.child(senderID);
                DatabaseReference userChatReference = userChatsReference.child(receiverUserModel.getuID());
                userChatReference.setValue(receiverUserModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference senderUserReference = usersReference.child(senderID);
        senderUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel senderUserModel = dataSnapshot.getValue(UserModel.class);

                DatabaseReference usersChatsReference = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE);
                DatabaseReference userChatsReference = usersChatsReference.child(receiverUserModel.getuID());
                DatabaseReference userChatReference = userChatsReference.child(senderUserModel.getuID());
                userChatReference.setValue(senderUserModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void removeChatFromDatabase(String receiverID) {
        String chatID = ChatIDCreator.getChatID(senderID, receiverID);

        DatabaseReference chatsReference = database.getReference().child(Constants.DATABASE_CHATS_NODE);
        chatsReference.child(chatID).removeValue();

        DatabaseReference usersChatsReference = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE);
        DatabaseReference userChatsReference = usersChatsReference.child(senderID);
        userChatsReference.child(receiverID).removeValue();
    }

    private void initializeChatViewAdapter(){
        Query query = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE).child(senderID);

        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>().
                setQuery(query, new SnapshotParser<UserModel>() {
                    @NonNull
                    @Override
                    public UserModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new UserModel(snapshot.child(Constants.DATABASE_UID_NODE).getValue().toString(),
                                snapshot.child(Constants.DATABASE_USERNAME_NODE).getValue().toString(),
                                snapshot.child(Constants.DATABASE_EMAIL_NODE).getValue().toString(),
                                snapshot.child(Constants.DATABASE_PROFILE_URI_NODE).getValue().toString(),
                                snapshot.child(Constants.DATABASE_STATUS_NODE).getValue().toString());
                    }
                }).build();

        userViewAdapter = new FirebaseRecyclerAdapter<UserModel, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserModel userModel) {
                holder.setUsernameText(userModel.getUsername());

                StorageReference profileImageReference = storage.getReference()
                        .child(Constants.STORAGE_IMAGES_NODE).child(userModel.getProfileURI());

                profileImageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(holder.getProfilePicture());
                    }
                });

                holder.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
                        DatabaseReference senderUserReference = usersReference.child(senderID);
                        senderUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                UserModel senderUserModel = dataSnapshot.getValue(UserModel.class);

                                listener.onOpenChat(senderUserModel, userModel);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

                return new UserViewHolder(view);
            }
        };
    }

    public interface OpenChatListener {
        void onOpenChat(UserModel senderUserModel, UserModel receiverUserModel);
    }
}