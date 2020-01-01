/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - TopicViewHolder.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


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

    public void setTitleText(String title) {
        this.titleText.setText(title);
    }

    public void setBodyText(String bodyText) {
        this.bodyText.setText(bodyText);
    }

    public void setUsernameText(String username) {
        this.usernameText.setText(username);
    }

    public void setTimeStampText(String timeStamp) {
        this.timeStampText.setText(timeStamp);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        topicLayout.setOnClickListener(listener);
    }
}
