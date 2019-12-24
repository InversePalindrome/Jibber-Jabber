/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - TopicModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class TopicModel {
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
}
