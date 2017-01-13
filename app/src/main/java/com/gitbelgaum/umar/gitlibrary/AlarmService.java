package com.gitbelgaum.umar.gitlibrary;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmService {
    private Context context;
    private PendingIntent mAlarmSender;

    public AlarmService(Context context) {
        this.context = context;
    }

    public void startbookreminderalarm(){
        Intent intent = new Intent(context,AlarmRecever.class);
        mAlarmSender = PendingIntent.getBroadcast(context, 123456789,intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 10);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+60*100*5, AlarmManager.INTERVAL_DAY, mAlarmSender);
    }
}
