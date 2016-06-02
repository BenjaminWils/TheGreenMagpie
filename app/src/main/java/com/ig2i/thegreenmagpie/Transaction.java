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

    public Transaction(Operation type, double amount, Date date, User theUser) {
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.theUser = theUser;
    }
}
