/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessagesViewAdapter.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesViewAdapter extends RecyclerView.Adapter<MessagesViewAdapter.ViewHolder> {
    private int layout;
    private ArrayList<MessageItem> messageList;

    public MessagesViewAdapter(int layout, ArrayList<MessageItem> messageList) {
        this.layout = layout;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView usernameText = holder.usernameText;
        CircleImageView profilePicture = holder.profilePicture;

        MessageItem messageItem = messageList.get(position);

        usernameText.setText(messageItem.getUsername());
        profilePicture.setImageURI(Uri.parse(messageItem.getProfileURI()));
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView usernameText;
        public CircleImageView profilePicture;

        public ViewHolder(View itemView) {
            super(itemView);

            usernameText = itemView.findViewById(R.id.message_item_profile_username);
            profilePicture = itemView.findViewById(R.id.message_item_profile_image);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
