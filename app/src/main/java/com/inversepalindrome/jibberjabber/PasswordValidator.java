/*
Copyright (c) 2020 Inverse Palindrome
Jibber Jabber - PasswordValidator.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;


import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public class PasswordValidator {
    public static boolean isPasswordValid(Context context, String password, String rePassword) {
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(context, "Please enter password in every entry!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!TextUtils.equals(password, rePassword)) {
            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(context, "Passwords needs to be longer than or equal to 8 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
