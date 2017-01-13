package com.gitbelgaum.umar.gitlibrary;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class UserLocalStore {

    SharedPreferences userdata;

    public UserLocalStore(Context context){
        userdata = context.getSharedPreferences("USER_SP",Context.MODE_PRIVATE);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.putString("name",user.name);
        useredit.putString("email",user.email);
        useredit.putString("bnum",user.bnum);
        useredit.putString("usn",user.usn);
        useredit.putBoolean("loggedin",true);
        useredit.commit();
    }
    public boolean isuserloggedin(){
        boolean loggedin = userdata.getBoolean("loggedin", false);
        return loggedin;
    }




    public void storeRegId(String regId,int appVersion){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putString(Util.PROPERTY_REG_ID, regId);
        editor.putInt(Util.PROPERTY_APP_VERSION, appVersion);
        editor.commit();/* new Change*///////////////////////////////////////////////////////////////
    }
    public String getRegId(){
        String regid = userdata.getString(Util.PROPERTY_REG_ID, "");
        return regid;
    }

    public int registeredVersion(){
        int rv = userdata.getInt(Util.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        return rv;
    }

    public User getuserlogedin(){
        String name = userdata.getString("name","");
        String email = userdata.getString("email","");
        String bnum = userdata.getString("bnum","");
        String usn = userdata.getString("usn","");
        User user = new User(name,email,bnum,usn);
        return user;
    }

    public void storebookhistory(String result){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putString("Book_History",result);
        editor.commit();

    }

    public String getbookhistory(){
        return userdata.getString("Book_History","");
    }
    public void storecurrentbookdata(String result){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putString("Current_Book",result);
        editor.commit();

    }

    public void storeremainingdays(String itemnumber , String remainingdays){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putString(itemnumber,remainingdays);
        editor.commit();
    }
    public int getramaingdays(String itemnumber){
        String remaingingdays = userdata.getString(itemnumber, "5");
        return Integer.parseInt(remaingingdays);
    }
    public void storeuserpropic(String data){
        SharedPreferences.Editor editor = userdata.edit();
        editor.putString("Propic",data);
        editor.commit();
    }
    public Bitmap getpropic(){
        String data =  userdata.getString("Propic", "");

        byte[] decodedString = Base64.decode(data.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public void storebookcoverimage(Bitmap bitmap,int position){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] image = byteArrayOutputStream.toByteArray();
        String encodedimg = Base64.encodeToString(image, Base64.DEFAULT);
        SharedPreferences.Editor editor = userdata.edit();
        editor.putString(position+"img",encodedimg);
        editor.commit();
    }

    public Bitmap getbookcoverimage(int position){
        String data = userdata.getString(position+"img","");
        byte[] decodedString = Base64.decode(data.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public void storewhatsnew(String msg){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.putString("whats_new",msg);
        useredit.commit();
    }
    public String getwhatsnew(){
        return userdata.getString("whats_new","");
    }

    public String getcurrentbookdata(){
        return userdata.getString("Current_Book","");
    }

    public void setupdate(boolean update){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.putBoolean("update",update);
        useredit.commit();
    }
    public void setnotification(boolean notify){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.putBoolean("notify",notify);
        useredit.commit();
    }
    public boolean isnotification(){
       return  userdata.getBoolean("notify",true);
    }

    public void storefinedata(String data){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.putString("fine",data);
        useredit.commit();
    }
    public String getfinedata(){
       return userdata.getString("fine","");
    }

    public void fiftyfinedata(boolean b){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.putBoolean("fifty",b);
        useredit.commit();
    }
    public boolean getfifty(){
        return userdata.getBoolean("fifty",true);
    }
    public boolean isupdated(){
        return userdata.getBoolean("update", true);
    }
    public void fineUpdated(boolean a){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.putBoolean("fine_update",a);
        useredit.commit();
    }
    public boolean isfineUpdated(){
        return userdata.getBoolean("fine_update",true);
    }
    public void clearuserdata(){
        SharedPreferences.Editor useredit = userdata.edit();
        useredit.clear();
        useredit.commit();
    }
}
