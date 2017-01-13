package com.gitbelgaum.umar.gitlibrary;


import android.graphics.Bitmap;

public class Cbooks {

    String book_title,book_author,book_issuedate,book_datedue,days_remaining,book_barcode,book_renewals,isbn,book_itemnumber;
    Bitmap img;
    public Cbooks(String book_title, String book_author , String book_issuedate,
                  String book_datedue,String days_remaining, String book_barcode, String book_renewals,String book_itemnumber, String isbn) {
        this.book_title = book_title;
        this.book_issuedate = book_issuedate;
        this.book_author = book_author;
        this.book_datedue = book_datedue;
        this.book_barcode = book_barcode;
        this.days_remaining = days_remaining;
        this.book_itemnumber = book_itemnumber;
        this.book_renewals = book_renewals;
        this.isbn = isbn;
    }

    public String getDays_remaining() {
        return days_remaining;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getBook_itemnumber() {
        return book_itemnumber;
    }

    public String getBook_title() {
        return book_title;
    }

    public String getBook_author() {
        return book_author;
    }

    public String getBook_issuedate() {
        return book_issuedate;
    }

    public String getBook_datedue() {
        return book_datedue;
    }

    public String getBook_barcode() {
        return book_barcode;
    }

    public String getBook_renewals() {
        return book_renewals;
    }

    public String getBook_isbn() {
        return isbn;
    }


}

