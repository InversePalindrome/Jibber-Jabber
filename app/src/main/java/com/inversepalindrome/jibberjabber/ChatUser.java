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
    private String id;
    private String username;
    private Bitmap icon;

    public ChatUser(String id, String username, Bitmap icon) {
        this.id = id;
        this.username = username;
        this.icon = icon;
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

    @NotNull
    @Override
    public String getId() {
        return this.id;
    }

    @Nullable
    @Override
    public String getName() {
        return this.username;
    }
}
