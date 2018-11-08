package com.example.ss.realm_test;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver{
    Context context;

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            //set the alarm here
        }
    }
}
