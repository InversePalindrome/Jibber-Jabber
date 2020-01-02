/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - UserViewAdapter.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserViewAdapter extends FirebaseRecyclerAdapter<UserModel, UserViewAdapter.UserViewHolder> {
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String senderID;
    private ChatsFragment.OpenChatListener listener;

    public UserViewAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options, String senderID, ChatsFragment.OpenChatListener listener) {
        super(options);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        this.senderID = senderID;
        this.listener = listener;
    }

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

        holder.setOnClickListener(new View.OnClickListener() {
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

    public void deleteUser(int position) {
        getSnapshots().getSnapshot(position).getRef().removeValue();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout layout;
        private TextView usernameText;
        private CircleImageView profilePicture;

        private UserViewHolder(View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.user_layout);
            usernameText = itemView.findViewById(R.id.user_item_profile_username);
            profilePicture = itemView.findViewById(R.id.user_item_profile_image);
        }

        private CircleImageView getProfilePicture() {
            return this.profilePicture;
        }

        private void setUsernameText(String username) {
            this.usernameText.setText(username);
        }

        private void setOnClickListener(View.OnClickListener onClickListener) {
            layout.setOnClickListener(onClickListener);
        }
    }
}