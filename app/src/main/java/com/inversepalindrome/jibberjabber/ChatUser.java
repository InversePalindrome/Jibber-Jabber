/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatUser.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.graphics.Bitmap;

import com.github.bassaer.chatmessageview.model.IChatUser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ChatUser implements IChatUser {
    private String uID;
    private String email;
    private String username;
    private Bitmap icon;

    public ChatUser(String uID, String email, String username) {
        this.uID = uID;
        this.email = email;
        this.username = username;
    }

    @NotNull
    @Override
    public String getId() {
        return this.uID;
    }

    @Nullable
    @Override
    public String getName() {
        return this.username;
    }

    @Nullable
    @Override
    public Bitmap getIcon() {
        return this.icon;
    }

    @Override
    public void setIcon(@NotNull Bitmap bitmap) {
        this.icon = bitmap;
    }

    public String getEmail() {
        return this.email;
    }
}
