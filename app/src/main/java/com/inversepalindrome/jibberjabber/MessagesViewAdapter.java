/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessagesViewAdapter.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import java.util.ArrayList;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesViewAdapter extends Adapter<MessagesViewAdapter.ViewHolder> {
    private int layout;
    private ArrayList<UserModel> userList;
    private final OnClickListener listener;


    public MessagesViewAdapter(int layout, ArrayList<UserModel> userList, OnClickListener listener) {
        this.layout = layout;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        view.setOnClickListener(listener);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView usernameText = holder.usernameText;
        CircleImageView profileImage = holder.profilePicture;

        UserModel userModel = userList.get(position);

        usernameText.setText(userModel.username);
        Picasso.get().load(Uri.parse(userModel.profileURI)).into(profileImage);
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
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
