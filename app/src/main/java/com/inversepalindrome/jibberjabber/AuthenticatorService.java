/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - AuthenticatorService.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        AccountAuthenticator accountAuthenticator = new AccountAuthenticator(this);
        return accountAuthenticator.getIBinder();
    }
}
