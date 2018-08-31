package com.example.mycoupons.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.widget.Toast;

import com.example.mycoupons.activity.SettingsActivity;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.model.Coupon;
import com.example.mycoupons.widget.CouponWidgetProvider;

import java.util.ArrayList;
import java.util.Calendar;

public class Utilities {
    private static final int YEAR_START_INDEX = 0;
    private static final int MONTH_START_INDEX = 4;
    private static final int DAY_OF_MONTH_START_INDEX = 6;

    private static final String[] MONTHS = new String[]{
            "",
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    public static long getLongDateToday() {
        Calendar calendar = Calendar.getInstance();
        return getLongDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }


    public static long getLongDate(int year, int month, int dayOfMonth) {
        DebugLog.logMethod();
        String date = String.valueOf(year)
                + (month < 10 ? '0' + String.valueOf(month) : String.valueOf(month))
                + (dayOfMonth < 10 ? '0' + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth));
        return Long.parseLong(date);
    }

    public static String getStringDate(long longDate) {
        DebugLog.logMethod();
        String date = String.valueOf(longDate);
        return date.substring(DAY_OF_MONTH_START_INDEX) + " "
                + MONTHS[Integer.parseInt(date.substring(MONTH_START_INDEX, DAY_OF_MONTH_START_INDEX))] + ", "
                + date.substring(YEAR_START_INDEX, MONTH_START_INDEX);
    }

    public static boolean isCouponExpired(long validUntil) {
        return validUntil < getLongDateToday();
    }

    public static Bitmap getBitmap(Context context, int resourceId) {
        return BitmapFactory.decodeResource(
                context.getResources(),
                resourceId
        );
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void updateAppWidget(Context context) {
        DebugLog.logMethod();
        context = context.getApplicationContext();
        Intent intent = new Intent(context, CouponWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] widgetIds = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(
                        context,
                        CouponWidgetProvider.class
                ));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        context.sendBroadcast(intent);
    }


    public static void setAlarmsOnFirstLaunch(Context context) {
        DebugLog.logMethod();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (!sharedPreferences.getBoolean(Constants.IS_FIRST_LAUNCH, true)) {
            return;
        }
        AppWidgetAlarmManager.setAlarm(context);
        NotificationAlarmManager.setAlarm(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.IS_FIRST_LAUNCH, false);
        editor.apply();
    }


    public static synchronized void updateSharedPreferences(Context context, boolean isSyncRunning, int syncMode) {
        DebugLog.logMethod();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.IS_SYNC_RUNNING, isSyncRunning);
        editor.putInt(Constants.SYNC_MODE, syncMode);
        editor.apply();
    }


    public static void navigateIfSyncInProgress(Context context) {
        DebugLog.logMethod();
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        if (sharedPreferences.getBoolean(Constants.IS_SYNC_RUNNING, false)) {
            Intent intent = new Intent(context, SettingsActivity.class);
            context.startActivity(intent);
        }
    }

    private static final String PREFERENCE_SAMPLE_DATA_ADDED = "sample_data_added";
    public static boolean isSampleDataAdded(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(PREFERENCE_SAMPLE_DATA_ADDED, false);
    }

    public static void updateSampleDataAdded(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(PREFERENCE_SAMPLE_DATA_ADDED, true);
        editor.apply();
    }

    public static ArrayList<Coupon> getSampleData() {
        ArrayList<Coupon> coupons = new ArrayList<>(6);
        coupons.add(new Coupon(-1, "Amazon", "electronics", 20190322, "No code required", "20% off on one plus devices", 1));
        coupons.add(new Coupon(-1, "Nike", "sports", 20190321, "FOOBAR123", "Buy a new Nike shoe and get a sweatshirt free", 0));
        coupons.add(new Coupon(-1, "Vodafone", "telecom", 20190331, "No code required", "1 GB 3G data pack for 21 days at Rs. 245", 0));
        coupons.add(new Coupon(-1, "Walmart", "online", 20190402L, "FOOBAR456", "10% off for online orders", 0));
        coupons.add(new Coupon(-1, "Amazon", "books", 20190401L, "HELLOWORLD", "Same day delivery for CLRS algorithm book", 1));
        coupons.add(new Coupon(-1, "Udacity", "mooc", 20190401L, "SUCHWOW", "10% discount for web developer nanodegree", 0));
        return coupons;
    }
}
