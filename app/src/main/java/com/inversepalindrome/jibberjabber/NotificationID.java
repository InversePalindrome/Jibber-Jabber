/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - NotificationID.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


import java.util.concurrent.atomic.AtomicInteger;


public class NotificationID {
    private final static AtomicInteger id = new AtomicInteger(0);

    public static int getID() {
        return id.incrementAndGet();
    }
}
