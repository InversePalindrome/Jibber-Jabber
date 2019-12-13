/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessageModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class MessageModel {
    public String senderID;
    public String receiverID;
    public String message;

    public MessageModel() {
    }

    public MessageModel(String senderID, String receiverID, String message) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
    }
}
