package com.gitbelgaum.umar.gitlibrary;

/**
 * Created by Faheem on 12/05/2016.
 */
public class ChatObject {

        String message;

        String date;

        public ChatObject(String message,String date) {
            this.message = message;
            this.date   = date;
        }

        public String getMessage() {

            return message;
        }
    public String getDate() {
        return date;
    }
}
