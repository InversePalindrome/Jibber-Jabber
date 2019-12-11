/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - UserModel.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class UserModel {
    public UserModel(String username, String email)
    {
        this.username = username;
        this.email = email;
    }

    public String username;
    public String email;
    public String profileURL = "";
}
