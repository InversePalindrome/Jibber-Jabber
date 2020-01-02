/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatsFragment.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ChatsFragment extends Fragment {
    private String senderID;
    private OpenChatListener listener;
    private FirebaseDatabase database;
    private UserViewAdapter userViewAdapter;
    private EmptyRecyclerView userView;
    private TextView emptyStartChatText;
    private FloatingActionButton startChatButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();

        senderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initializeChatViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chats, container, false);

        userView = view.findViewById(R.id.chats_user_view);
        startChatButton = view.findViewById(R.id.chats_start_chat_button);
        emptyStartChatText = view.findViewById(R.id.chats_empty_start_chat_text);

        setupUserView();
        setupUserButtonCallbacks();

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

                                                addChatToDatabase(senderUserModel, receiverUserModel);
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

    private void addChatToDatabase(UserModel senderUserModel, UserModel receiverUserModel) {
        DatabaseReference usersChatsReference = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE);
        DatabaseReference senderUserChatsReference = usersChatsReference.child(senderID);
        DatabaseReference senderUserChatReference = senderUserChatsReference.child(receiverUserModel.getuID());
        senderUserChatReference.setValue(receiverUserModel);

        DatabaseReference receiverUserChatsReference = usersChatsReference.child(receiverUserModel.getuID());
        DatabaseReference receiverUserChatReference = receiverUserChatsReference.child(senderUserModel.getuID());
        receiverUserChatReference.setValue(senderUserModel);
    }

    private void initializeChatViewAdapter() {
        Query query = database.getReference().child(Constants.DATABASE_USERS_CHATS_NODE).child(senderID);

        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>().
                setQuery(query, UserModel.class).build();

        userViewAdapter = new UserViewAdapter(options, senderID, listener);
    }

    private void setupUserView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        userView.setLayoutManager(linearLayoutManager);
        userView.setItemAnimator(new DefaultItemAnimator());
        userView.setAdapter(userViewAdapter);
        userView.setEmptyView(emptyStartChatText);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(userView.getContext(),
                linearLayoutManager.getOrientation());

        userView.addItemDecoration(dividerItemDecoration);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                userViewAdapter.deleteUser(viewHolder.getAdapterPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(userView);
    }

    private void setupUserButtonCallbacks() {
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
    }

    public interface OpenChatListener {
        void onOpenChat(UserModel senderUserModel, UserModel receiverUserModel);
    }
}