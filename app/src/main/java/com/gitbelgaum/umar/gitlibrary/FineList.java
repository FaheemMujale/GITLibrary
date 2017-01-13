package com.gitbelgaum.umar.gitlibrary;

/**
 * Created by Faheem on 13/05/2016.
 */
public class FineList {

    public String date,amount;

    public FineList(String date, String amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }
}
