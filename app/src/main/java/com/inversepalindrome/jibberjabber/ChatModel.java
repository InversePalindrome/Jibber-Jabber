/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class ChatModel {
    private String senderID;
    private String receiverID;
    private String message;
    private Long timeStamp;

    public ChatModel() {
    }

    public ChatModel(String senderID, String receiverID, String message, Long timeStamp) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
