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

    private String uID;
    private String username;
    private String email;
    private String profileURI;
    private String status;

    public UserModel() {
    }

    public UserModel(String uID, String username, String email, String profileURI, String status) {
        this.uID = uID;
        this.username = username;
        this.email = email;
        this.profileURI = profileURI;
        this.status = status;
    }

    protected UserModel(Parcel in) {
        this.uID = in.readString();
        this.username = in.readString();
        this.email = in.readString();
        this.profileURI = in.readString();
        this.status = in.readString();
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
        dest.writeString(this.status);
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileURI() {
        return profileURI;
    }

    public void setProfileURI(String profileURI) {
        this.profileURI = profileURI;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
