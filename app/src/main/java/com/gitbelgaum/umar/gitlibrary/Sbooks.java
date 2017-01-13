package com.gitbelgaum.umar.gitlibrary;


public class Sbooks {
    private String book_name;
    private String book_author;
    private String book_biblio_num;

    public Sbooks(String book_name, String book_author,String book_biblio_num) {
        this.book_name = book_name;
        this.book_author = book_author;
        this.book_biblio_num = book_biblio_num;
    }

    public String getBook_name() {
        return book_name;
    }

    public String getBook_biblio_num() {
        return book_biblio_num;
    }

    public String getBook_author() {
        return book_author;
    }
}
