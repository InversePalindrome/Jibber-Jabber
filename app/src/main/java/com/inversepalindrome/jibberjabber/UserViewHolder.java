/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - UserViewHolder.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserViewHolder extends RecyclerView.ViewHolder {
    private ConstraintLayout layout;
    private TextView usernameText;
    private CircleImageView profilePicture;

    public UserViewHolder(View itemView) {
        super(itemView);

        layout = itemView.findViewById(R.id.user_layout);
        usernameText = itemView.findViewById(R.id.user_item_profile_username);
        profilePicture = itemView.findViewById(R.id.user_item_profile_image);
    }

    public CircleImageView getProfilePicture() {
        return this.profilePicture;
    }

    public void setUsernameText(String username) {
        this.usernameText.setText(username);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        layout.setOnClickListener(onClickListener);
    }
}