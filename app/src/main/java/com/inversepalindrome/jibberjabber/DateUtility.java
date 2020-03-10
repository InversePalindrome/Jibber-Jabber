/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - DateUtility.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtility {
    public static String getDate(Long timeStamp) {
        Date date = new Date(timeStamp);
        Format format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        return format.format(date);
    }
}
