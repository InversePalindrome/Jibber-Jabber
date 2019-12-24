/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - ChatIDCreator.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class ChatIDCreator {
    public static String getChatID(String uID1, String uID2) {
        if (uID1.compareTo(uID2) < 0) {
            return uID1 + uID2;
        }

        return uID2 + uID1;
    }
}
