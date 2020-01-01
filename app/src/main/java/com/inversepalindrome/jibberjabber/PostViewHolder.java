/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - PostViewHolder.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class PostViewHolder extends RecyclerView.ViewHolder {
    private TextView bodyText;
    private TextView usernameText;
    private TextView timeStampText;

    public PostViewHolder(View itemView) {
        super(itemView);

        bodyText = itemView.findViewById(R.id.post_body_text);
        usernameText = itemView.findViewById(R.id.post_username_text);
        timeStampText = itemView.findViewById(R.id.post_timestamp_text);
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

    public void setUsernameOnClickListener(View.OnClickListener listener) {
        this.usernameText.setOnClickListener(listener);
    }
}