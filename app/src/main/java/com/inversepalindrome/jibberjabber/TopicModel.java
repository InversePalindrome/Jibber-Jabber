/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - TopicModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


import android.os.Parcel;
import android.os.Parcelable;


public class TopicModel implements Parcelable {
    public String title;
    public String body;
    public String senderID;
    public String timeStamp;

    public TopicModel() {
    }

    public TopicModel(String title, String body, String senderID, String timeStamp) {
        this.title = title;
        this.body = body;
        this.senderID = senderID;
        this.timeStamp = timeStamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.senderID);
        dest.writeString(this.timeStamp);
    }

    protected TopicModel(Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
        this.senderID = in.readString();
        this.timeStamp = in.readString();
    }

    public static final Parcelable.Creator<TopicModel> CREATOR = new Parcelable.Creator<TopicModel>() {
        @Override
        public TopicModel createFromParcel(Parcel source) {
            return new TopicModel(source);
        }

        @Override
        public TopicModel[] newArray(int size) {
            return new TopicModel[size];
        }
    };
}
