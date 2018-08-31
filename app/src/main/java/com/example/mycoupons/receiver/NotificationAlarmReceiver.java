package com.example.mycoupons.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.notification.CouponsTrackerNotification;
import com.example.mycoupons.util.Constants;

public class NotificationAlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DebugLog.logMethod();
        DebugLog.logMessage("Action: " + intent.getAction());
        if (intent.getAction().equals(Constants.ACTION_SHOW_NOTIFICATION)){
            new CouponsTrackerNotification(context).showDailyNotification();
        }
    }
}
