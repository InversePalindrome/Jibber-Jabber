/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - DateUtility.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;


public class DateUtility {
    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
