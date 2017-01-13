package com.gitbelgaum.umar.gitlibrary;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Faheem on 12/05/2016.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(),GcmIntentServices.class.getName());
        startWakefulService(context,(intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
