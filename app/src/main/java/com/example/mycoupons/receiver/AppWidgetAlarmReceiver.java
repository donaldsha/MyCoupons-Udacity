package com.example.mycoupons.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.util.Constants;
import com.example.mycoupons.util.Utilities;

public class AppWidgetAlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DebugLog.logMethod();
        DebugLog.logMessage("Action: " + intent.getAction());
        if (intent.getAction().equals(Constants.ACTION_WIDGET_UPDATE)) {
            Utilities.updateAppWidget(context);
        }
    }
}
