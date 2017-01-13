package com.gitbelgaum.umar.gitlibrary;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class AlarmRecever extends BroadcastReceiver {
    NotificationManager notificationManager;
    UserLocalStore userLocalStore;
    CurrentReadingfrag currentReadingfrag;
    PendingIntent pendingIntent;
    private int NOTIFICATION_ID = 1;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarm","inside receve");
        this.context = context;
        userLocalStore = new UserLocalStore(context);
        currentReadingfrag = new CurrentReadingfrag();
        currentReadingfrag.updatebooksdata(context);
        userLocalStore.fineUpdated(true);
        Intent in = new Intent(context,CurrentReadingfrag.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(context,0,in,0);
        String result = userLocalStore.getcurrentbookdata();
        String [] book_data = result.split("@@");
        int i = book_data.length/9;
        int j= i;
        while(i>0){
            int k=(j-i)*9;
            int a = userLocalStore.getramaingdays(book_data[k+7]);
            if(a<3){
                //notification
                String message;
                if(book_data[k+6].contains("YES")){
                            message = "Renew Available";
                }else{
                    message = "Return Book";
                    }
                if(userLocalStore.isnotification()){
                sendNotificetion("Title: "+book_data[k],message);}
            }
            i--;
        }

        if(!userLocalStore.getfinedata().isEmpty()) {
            String finedata = userLocalStore.getfinedata();
            String[] data = finedata.split("@");
            int k = data.length;
            String total = data[k - 1];
            String[] tot = total.split("\\.");
            int a = Integer.parseInt(tot[0]);
            if (a > 50 && a < 100) {
                if(userLocalStore.getfifty()){
                sendNotificetion("Your current fine is: "+total + " Rs","Library Fine");
                    userLocalStore.fiftyfinedata(false);
                 }
            }
            if(a>100){
                sendNotificetion("Your card is blocked... Fine is : "+total + " Rs","Library Fine");
            }

        }
    }

    private void sendNotificetion(String title,String message){
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title))
                .setContentText(title);


        builder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        NOTIFICATION_ID++;
    }
}
