/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - MessageIDCreator.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


public class MessageIDCreator {
    public static String getMessageID(String uID1, String uID2) {
        if (uID1.compareTo(uID2) < 0) {
            return uID1 + uID2;
        }

        return uID2 + uID1;
    }
}
