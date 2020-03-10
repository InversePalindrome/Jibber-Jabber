/*
Copyright (c) 2020 Inverse Palindrome
Jibber Jabber - PostModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;


public class PostModel implements Parcelable {
    public static final Creator<PostModel> CREATOR = new Creator<PostModel>() {
        @Override
        public PostModel createFromParcel(Parcel source) {
            return new PostModel(source);
        }

        @Override
        public PostModel[] newArray(int size) {
            return new PostModel[size];
        }
    };
    private String body;
    private String senderID;
    private String username;
    private Object timeStamp;
    private Long timeStampLong;

    public PostModel() {
    }

    public PostModel(String body, String senderID, String username) {
        this.body = body;
        this.senderID = senderID;
        this.username = username;
        this.timeStamp = ServerValue.TIMESTAMP;
        this.timeStampLong = System.currentTimeMillis();
    }

    protected PostModel(Parcel in) {
        this.body = in.readString();
        this.senderID = in.readString();
        this.username = in.readString();
        this.timeStampLong = in.readLong();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStampLong = timeStamp;
    }

    @Exclude
    public Long getTimeStampLong() {
        return timeStampLong;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
        dest.writeString(this.senderID);
        dest.writeString(this.username);
        dest.writeLong(this.timeStampLong);
    }
}
