package com.ig2i.thegreenmagpie;

import java.util.Date;

/**
 * Created by 10081923 on 25/05/2016.
 */
public class Transaction {
    private Operation type;
    private double amount;
    private String date;
    private String clientEmail;
    private String sellerEmail;

    public Transaction(Operation type, double amount, String date, String clientEmail, String sellerEmail) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.clientEmail = clientEmail;
        this.sellerEmail = sellerEmail;
    }

    public Operation getType() {
        return type;
    }

    public void setType(Operation type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }
}
