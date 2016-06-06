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

    public void setBalance(float balance){
        this.balance = balance;
    }

    public void setToken(String token){
        this.token = token;
    }

    private boolean addBalance(double amount, String paypal_client_id) {
        boolean result = false;
        // TODO

        return result;
    }

    private boolean askForRefund(String paypal_client_id) {
        boolean result = false;

        return result;
    }

    private boolean sendMoney(double amount) {
        boolean result = false;
        // TODO : placer appel SDK Paypal pour envoi d'argent
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
