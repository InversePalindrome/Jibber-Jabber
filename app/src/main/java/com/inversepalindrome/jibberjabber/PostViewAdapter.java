/*
Copyright (c) 2020 Inverse Palindrome
Jibber Jabber - PostViewAdapter.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PostViewAdapter extends FirebaseRecyclerAdapter<PostModel, PostViewAdapter.PostViewHolder> {
    private TopicFragment.OnPostSelectedListener listener;
    private FirebaseDatabase database;

    public PostViewAdapter(@NonNull FirebaseRecyclerOptions<PostModel> options, TopicFragment.OnPostSelectedListener listener) {
        super(options);

        this.listener = listener;
        database = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostModel postModel) {
        holder.setBodyText(postModel.getBody());
        holder.setUsernameText(postModel.getUsername());
        holder.setTimeStampText(DateUtility.getDate(postModel.getTimeStampLong()));
        holder.setUsernameOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usersReference = database.getReference().child(Constants.DATABASE_USERS_NODE);
                DatabaseReference userReference = usersReference.child(postModel.getSenderID());
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel userModel = dataSnapshot.getValue(UserModel.class);

                        listener.onUsernameSelected(userModel);
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
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);

        return new PostViewHolder(view);
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView bodyText;
        private TextView usernameText;
        private TextView timeStampText;

        private PostViewHolder(View itemView) {
            super(itemView);

            bodyText = itemView.findViewById(R.id.post_body_text);
            usernameText = itemView.findViewById(R.id.post_username_text);
            timeStampText = itemView.findViewById(R.id.post_timestamp_text);
        }

        private void setBodyText(String bodyText) {
            this.bodyText.setText(bodyText);
        }

        private void setUsernameText(String username) {
            this.usernameText.setText(username);
        }

        private void setTimeStampText(String timeStamp) {
            this.timeStampText.setText(timeStamp);
        }

        private void setUsernameOnClickListener(View.OnClickListener listener) {
            this.usernameText.setOnClickListener(listener);
        }
    }
}