package com.gitbelgaum.umar.gitlibrary;


public class User {
     String name,bnum,email,usn;

    public User(String name, String email, String bnum,String usn) {
        this.name = name;
        this.email = email;
        this.bnum = bnum;
        this.usn = usn;
    }

    public String getname() {
        return name;
    }

    public String getusn() {
        return usn;
    }
}
