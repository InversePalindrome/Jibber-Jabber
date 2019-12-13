/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessageItem.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class MessageItem {
    private String username;
    private String profileURI;

    public MessageItem(String username, String profileURI) {
        this.username = username;
        this.profileURI = profileURI;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileURI() {
        return profileURI;
    }

    public void setProfileURI(String profileURI) {
        this.profileURI = profileURI;
    }
}
