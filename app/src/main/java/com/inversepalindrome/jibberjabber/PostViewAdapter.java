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
    private final View.OnClickListener listener;
    private int layout;
    private ArrayList<TopicModel> topicList;


    public PostViewAdapter(int layout, ArrayList<TopicModel> topicList, View.OnClickListener listener) {
        this.layout = layout;
        this.topicList = topicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        view.setOnClickListener(listener);
        PostViewAdapter.ViewHolder viewHolder = new PostViewAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return topicList == null ? 0 : topicList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView bodyText;

        public ViewHolder(View itemView) {
            super(itemView);


        }

        @Override
        public void onClick(View view) {

        }
    }
}
