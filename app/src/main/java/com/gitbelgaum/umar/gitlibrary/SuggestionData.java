package com.gitbelgaum.umar.gitlibrary;

/**
 * Created by Faheem on 07/06/2016.
 */
public class SuggestionData {
    String name,text,date;

    public SuggestionData(String name, String text, String date) {
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public String getname() {
        return name;
    }

    public String gettext() {
        return text;
    }

    public String getdate() {
        return date;
    }
}
