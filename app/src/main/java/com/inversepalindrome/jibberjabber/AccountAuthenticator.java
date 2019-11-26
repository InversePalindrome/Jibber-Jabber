/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - AccountAuthenticator.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.os.Bundle;
import android.text.TextUtils;
import android.content.Intent;
import android.content.Context;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.AccountAuthenticatorResponse;


public class AccountAuthenticator extends AbstractAccountAuthenticator {
    public AccountAuthenticator(Context context){
        super(context);

        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
         final Intent intent = new Intent(context, LoginActivity.class);
         intent.putExtra(ACCOUNT_TYPE, accountType);
         intent.putExtra(TOKEN_TYPE, authTokenType);
         intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final AccountManager accountManager = AccountManager.get(context);

        String authToken = accountManager.peekAuthToken(account, authTokenType);

        if(TextUtils.isEmpty(authToken))
        {
            final String password = accountManager.getPassword(account);

            if(password != null)
            {
                //authToken =
            }
        }
        else
        {
            final Bundle bundle = new Bundle();

            bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            bundle.putString(AccountManager.KEY_AUTHTOKEN, authToken);

            return bundle;
        }

        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name);
        intent.putExtra(TOKEN_TYPE, authTokenType);

        final Bundle bundle = new Bundle();

        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return "full";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        final Bundle bundle = new Bundle();

        return bundle;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    private final Context context;
    private final String ACCOUNT_TYPE = "user_account";
    private final String TOKEN_TYPE = "full_access";
}
