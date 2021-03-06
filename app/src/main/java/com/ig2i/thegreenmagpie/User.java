package com.ig2i.thegreenmagpie;

import android.util.Log;

import com.ig2i.thegreenmagpie.buyer.GetUserData;

import java.util.ArrayList;

/**
 * Created by 10081923 on 25/05/2016.
 */
public class User {
    private String email;
    private float balance;
    private String token;
    private boolean autoConnect;

    private static ArrayList<User> _User = new ArrayList<>();

    public User() {
        _User.add(this);
    }

    public User(String email) {
        this.email = email;
        _User.add(this);
    }

    public User(String email, float balance, String token) {
        this.email = email;
        this.balance = balance;
        this.token = token;
        _User.add(this);
    }

    public boolean getAutoConnect(){
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect){
        this.autoConnect = autoConnect;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setData(){
        new GetUserData(new GetUserData.AsyncResponse() {
            @Override
            public void getUserDataIsFinished(String output) {
                Log.d("userData", output);
            }
        }).execute(this.email);
    }

    public float getBalance(){
        return this.balance;
    }

    public String getToken(){
        return this.token;
    }

    public void setBalance(float balance){
        this.balance = balance;
    }

    public void setToken(String token){
        this.token = token;
    }

    private boolean askForRefund(String paypal_client_id) {
        boolean result = false;

        return result;
    }

    public static User getUser(String email) {
        User result = null;

        for (User u : _User) {
            if (u.getEmail().equals(email)) {
                result = u;
            }
        }

        return result;
    }
}
