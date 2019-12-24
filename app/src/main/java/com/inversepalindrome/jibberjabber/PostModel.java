/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - PostModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class PostModel {
    public String body;
    public String senderID;
    public String timeStamp;

    public PostModel(String body, String senderID, String timeStamp) {
        this.body = body;
        this.senderID = senderID;
        this.timeStamp = timeStamp;
    }
}
