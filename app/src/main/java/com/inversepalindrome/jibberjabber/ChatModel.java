/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class ChatModel {
    public String senderID;
    public String receiverID;
    public String message;
    public String timeStamp;

    public ChatModel() {
    }

    public ChatModel(String senderID, String receiverID, String message, String timeStamp) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.timeStamp = timeStamp;
    }
}
