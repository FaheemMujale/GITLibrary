package com.gitbelgaum.umar.gitlibrary;

import android.graphics.Bitmap;

/**
 * Created by Faheem on 23/04/2016.
 */
public class Hbooks {
    private String title;
    private String author;
    private String isbn;
    private String issue_date;
    private String return_date;
    private String bibnum;
    public Bitmap cover_image;

    public Hbooks(String title, String author,String issue_date, String return_date,String bibnum,String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.issue_date = issue_date;
        this.return_date = return_date;
        this.bibnum = bibnum;
    }

    public Bitmap getCover_image() {
        return cover_image;
    }

    public void setCover_image(Bitmap cover_image) {
        this.cover_image = cover_image;
    }

    public String getBibnum() {
        return bibnum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
