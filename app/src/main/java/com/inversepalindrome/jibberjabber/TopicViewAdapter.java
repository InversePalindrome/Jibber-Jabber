/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - TopicViewAdapter.java
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


public class TopicViewAdapter extends RecyclerView.Adapter<TopicViewAdapter.ViewHolder> {
    private final View.OnClickListener listener;
    private int layout;
    private ArrayList<TopicModel> topicList;


    public TopicViewAdapter(int layout, ArrayList<TopicModel> topicList, View.OnClickListener listener) {
        this.layout = layout;
        this.topicList = topicList;
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
        final TextView titleText = holder.titleText;
        final TextView bodyText = holder.bodyText;
        final TextView usernameText = holder.usernameText;
        final TextView timeStampText = holder.timeStampText;

        TopicModel topicModel = topicList.get(position);

        titleText.setText(topicModel.getTitle());
        bodyText.setText(topicModel.getBody());
        usernameText.setText(topicModel.getUsername());
        timeStampText.setText(topicModel.getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return topicList == null ? 0 : topicList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView titleText;
        private TextView bodyText;
        private TextView usernameText;
        private TextView timeStampText;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.topic_title_text);
            bodyText = itemView.findViewById(R.id.topic_body_text);
            usernameText = itemView.findViewById(R.id.topic_username_text);
            timeStampText = itemView.findViewById(R.id.topic_timestamp_text);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
