package com.example.mycoupons.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.util.AppWidgetAlarmManager;
import com.example.mycoupons.util.NotificationAlarmManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DebugLog.logMethod();
        DebugLog.logMessage("Action: " + intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Reset both alarms.
            AppWidgetAlarmManager.setAlarm(context);
            NotificationAlarmManager.setAlarm(context);
        }
    }
}
