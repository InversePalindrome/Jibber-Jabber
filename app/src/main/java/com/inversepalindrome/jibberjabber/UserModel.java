/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - UserModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.os.Parcel;
import android.os.Parcelable;


public class UserModel implements Parcelable {
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
    public String uID;
    public String username;
    public String email;
    public String profileURI;
    public UserModel() {
    }

    public UserModel(String uID, String username, String email, String profileURI) {
        this.uID = uID;
        this.username = username;
        this.email = email;
        this.profileURI = profileURI;
    }

    protected UserModel(Parcel in) {
        this.uID = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.profileURI = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uID);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.profileURI);
    }
}
