package com.example.mycoupons.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.util.AppWidgetAlarmManager;
import com.example.mycoupons.util.NotificationAlarmManager;

//reset previous alarms and display daily notifications
public class DateTimeChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DebugLog.logMethod();
        DebugLog.logMessage("Action: " + intent.getAction());
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIME_CHANGED)
                || action.equals(Intent.ACTION_DATE_CHANGED)
                || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            // Cancel previously set alarms and set a new alarm
            AppWidgetAlarmManager.cancelAlarm(context);
            AppWidgetAlarmManager.setAlarm(context);

            NotificationAlarmManager.cancelAlarm(context);
            NotificationAlarmManager.setAlarm(context);
        }
    }
}
