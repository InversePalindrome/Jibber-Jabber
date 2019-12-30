/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - PostModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class PostModel {
    private String body;
    private String senderID;
    private String username;
    private String timeStamp;

    public PostModel() {
    }

    public PostModel(String body, String senderID, String username, String timeStamp) {
        this.body = body;
        this.senderID = senderID;
        this.username = username;
        this.timeStamp = timeStamp;
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
