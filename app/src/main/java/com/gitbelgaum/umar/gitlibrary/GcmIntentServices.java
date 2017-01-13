package com.gitbelgaum.umar.gitlibrary;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
/**
 * Created by Faheem on 12/05/2016.
 */
public class GcmIntentServices extends IntentService{


    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    UserLocalStore userLocalStore;
    public GcmIntentServices() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        userLocalStore = new UserLocalStore(getApplicationContext());
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                if(userLocalStore.isnotification()){
                sendNotification("Send error: " + extras.toString());}
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                if(userLocalStore.isnotification()){
                sendNotification("Deleted messages on server: " +
                        extras.toString());}
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                String recieved_message=intent.getStringExtra("text_message");

                if(userLocalStore.isnotification()){
                sendNotification(recieved_message);}
                if(userLocalStore.getwhatsnew().isEmpty()) {
                    userLocalStore.storewhatsnew(recieved_message);
                }else{
                    userLocalStore.storewhatsnew(userLocalStore.getwhatsnew()+"%&%"+recieved_message);
                }
                Intent sendIntent =new Intent("message_recieved");
                sendIntent.putExtra("message",recieved_message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        String[] message = msg.split("#");
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setContentTitle("Git Library")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message[0]))
                        .setContentText(message[0]);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
