package com.ig2i.thegreenmagpie;

import java.util.Date;

/**
 * Created by 10081923 on 25/05/2016.
 */
public class Transaction {
    private Operation type;
    private double amount;
    private Date date;
    private User theUser;

    public Transaction(Operation type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public Transaction(Operation type, double amount, Date date, User theUser) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.theUser = theUser;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getTheUser() {
        return theUser;
    }

    public void setTheUser(User theUser) {
        this.theUser = theUser;
    }
}
