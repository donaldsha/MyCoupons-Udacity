package com.example.mycoupons.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.mycoupons.fragment.SettingsFragment;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.receiver.NotificationAlarmReceiver;
import com.example.mycoupons.util.Constants;

import java.util.Calendar;

public class NotificationAlarmManager{
    public static void setAlarm(Context context) {
        DebugLog.logMethod();
        context = context.getApplicationContext();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (!sharedPreferences.getBoolean(SettingsFragment.KEY_NOTIFICATION_STATE, true)) {
            return;
        }

        int notificationTime = sharedPreferences.getInt(
                SettingsFragment.KEY_NOTIFICATION_TIME,
                SettingsFragment.DEFAULT_NOTIFICATION_TIME
        );
        PendingIntent pendingIntent = getPendingIntent(context);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, notificationTime / 100);
        calendar.set(Calendar.MINUTE, notificationTime % 100);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }


    public static void cancelAlarm(Context context) {
        DebugLog.logMethod();
        context = context.getApplicationContext();
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE))
                .cancel(getPendingIntent(context));
    }

    private static PendingIntent getPendingIntent(Context context) {
        DebugLog.logMethod();
        Intent intent = new Intent(context, NotificationAlarmReceiver.class);
        intent.setAction(Constants.ACTION_SHOW_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
