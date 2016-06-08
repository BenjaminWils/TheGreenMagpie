package com.ig2i.thegreenmagpie;

import java.util.ArrayList;

/**
 * Created by 10081923 on 25/05/2016.
 */
public class History {
    private static ArrayList<Transaction> listTransactions = new ArrayList<>();

    public static void addTransaction(Operation type, double amount, User theUser) {
        // TODO : ajouter la transac à la BDD
    }

    public static ArrayList<Transaction> getWholeHistory(int limit) {
        ArrayList<Transaction> result = null;

        // TODO : récupérer toutes les transactions dans une certaine @limit

        return result;
    }

    public static ArrayList<Transaction> getUserHistory(User theUser, int limit) {
        ArrayList<Transaction> result = null;

        // TODO : récupérer les transactions du @theUser dans une certaine @limit

        return result;
    }


}
