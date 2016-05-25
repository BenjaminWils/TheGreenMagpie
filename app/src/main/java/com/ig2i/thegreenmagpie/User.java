package com.ig2i.thegreenmagpie;

import java.util.ArrayList;

/**
 * Created by 10081923 on 25/05/2016.
 */
public class User {
    private String mail;
    private double balance;
    private String consent_token;

    private static ArrayList<User> _User = new ArrayList<>();

    public User() {
        _User.add(this);
    }

    public User(String mail) {
        this.mail = mail;
        _User.add(this);
    }

    public User(String mail, double balance, String consent_token) {
        this.mail = mail;
        this.balance = balance;
        this.consent_token = consent_token;
        _User.add(this);
    }

    public String getMail() {
        return mail;
    }

    public double getBalance() {
        // TODO : Sync de la solde avec le serveur
        return balance;
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

    private boolean setAutoAccept(String paypal_client_id) {
        boolean result = false;
        // TODO : Placer script auto-acceptation
        return result;
    }

    private boolean unsetAutoAccept(String paypal_client_id) {
        boolean result = false;
        // TODO : Placer script annulation auto-acceptation
        return result;
    }

    private boolean sendMoney(double amount) {
        boolean result = false;
        // TODO : placer appel SDK Paypal pour envoi d'argent
        return result;
    }

    public static User getUser(String mail) {
        User result = null;

        for (User u : _User) {
            if (u.getMail().equals(mail)) {
                result = u;
            }
        }

        return result;
    }
}
