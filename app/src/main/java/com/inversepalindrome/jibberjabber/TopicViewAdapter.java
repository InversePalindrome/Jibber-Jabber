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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


public class TopicViewAdapter extends FirebaseRecyclerAdapter<TopicModel, TopicViewAdapter.TopicViewHolder> {
    private ForumFragment.OpenTopicListener listener;

    public TopicViewAdapter(@NonNull FirebaseRecyclerOptions<TopicModel> options, ForumFragment.OpenTopicListener listener) {
        super(options);

        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull TopicViewHolder holder, int position, @NonNull TopicModel topicModel) {
        holder.setTitleText(topicModel.getTitle());
        holder.setBodyText(topicModel.getBody());
        holder.setUsernameText(topicModel.getUsername());
        holder.setTimeStampText(topicModel.getTimeStamp());
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onOpenTopic(topicModel);
            }
        });
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);

        return new TopicViewHolder(view);
    }

    public class TopicViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout topicLayout;
        private TextView titleText;
        private TextView bodyText;
        private TextView usernameText;
        private TextView timeStampText;

        public TopicViewHolder(View itemView) {
            super(itemView);

            topicLayout = itemView.findViewById(R.id.topic_layout);
            titleText = itemView.findViewById(R.id.topic_title_text);
            bodyText = itemView.findViewById(R.id.topic_body_text);
            usernameText = itemView.findViewById(R.id.topic_username_text);
            timeStampText = itemView.findViewById(R.id.topic_timestamp_text);
        }

        private void setTitleText(String title) {
            this.titleText.setText(title);
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

        public void setOnClickListener(View.OnClickListener listener) {
            topicLayout.setOnClickListener(listener);
        }
    }
}