/*
Copyright (c) 2019 Inverse Palindrome
Jibber Jabber - AuthenticationActivity.java
https://inversepalindrome.com/
*/


package com.inversepalindrome.jibberjabber;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class AuthenticationActivity extends AppCompatActivity implements
        LoginFragment.LoginSelectedListener, RegisterFragment.RegisteredListener,
        ResetPasswordFragment.ResetPasswordCompletedListener {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            finish();
        }

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();

        transaction.add(R.id.authentication_layout, loginFragment);
        transaction.commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof LoginFragment) {
            LoginFragment loginFragment = (LoginFragment) fragment;
            loginFragment.setLoginSelectedListener(this);
        } else if (fragment instanceof RegisterFragment) {
            RegisterFragment registerFragment = (RegisterFragment) fragment;
            registerFragment.setRegisterSelectedListener(this);
        } else if (fragment instanceof ResetPasswordFragment) {
            ResetPasswordFragment resetPasswordFragment = (ResetPasswordFragment) fragment;
            resetPasswordFragment.setResetPasswordSelectedListener(this);
        }
    }

    @Override
    public void onLoginSelected() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onBeginRegistration() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RegisterFragment registerFragment = new RegisterFragment();

        transaction.replace(R.id.authentication_layout, registerFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRegistrationComplete() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();

        transaction.replace(R.id.authentication_layout, loginFragment);
        transaction.commit();
    }

    @Override
    public void onForgotPassword() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();

        transaction.replace(R.id.authentication_layout, resetPasswordFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResetPasswordCompleted() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();

        transaction.replace(R.id.authentication_layout, loginFragment);
        transaction.commit();
    }
}
