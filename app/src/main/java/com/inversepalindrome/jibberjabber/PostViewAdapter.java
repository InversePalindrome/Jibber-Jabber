/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - PostViewAdapter.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {
    private int layout;
    private ArrayList<PostModel> postList;
    private OnPostSelectedListener listener;

    public PostViewAdapter(int layout, ArrayList<PostModel> postList, OnPostSelectedListener listener) {
        this.layout = layout;
        this.postList = postList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        PostViewAdapter.ViewHolder viewHolder = new PostViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewAdapter.ViewHolder holder, int position) {
        final TextView bodyText = holder.bodyText;
        final TextView usernameText = holder.usernameText;
        final TextView timeStampText = holder.timeStampText;

        PostModel postModel = postList.get(position);

        bodyText.setText(postModel.getBody());

        usernameText.setText(postModel.getUsername());
        usernameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUsernameSelected(postModel);
            }
        });

        timeStampText.setText(postModel.getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return postList == null ? 0 : postList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView bodyText;
        private TextView usernameText;
        private TextView timeStampText;

        public ViewHolder(View itemView) {
            super(itemView);

            bodyText = itemView.findViewById(R.id.post_body_text);
            usernameText = itemView.findViewById(R.id.post_username_text);
            timeStampText = itemView.findViewById(R.id.post_timestamp_text);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public interface OnPostSelectedListener {
        void onUsernameSelected(PostModel postModel);
    }
}
