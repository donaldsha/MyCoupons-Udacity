package com.example.mycoupons.util;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Constants {

    public static final String IS_FIRST_LAUNCH = "is_first_launch";

    public static final String BUNDLE_EXTRA_COUPONS = "bundle_extra_coupons";
    public static final String BUNDLE_EXTRA_LIST_POSITION = "bundle_extra_list_position";
    public static final String BUNDLE_EXTRA_MERCHANT_SUGGESTIONS = "bundle_extra_merchant_suggestions";
    public static final String BUNDLE_EXTRA_CATEGORY_SUGGESTIONS = "bundle_extra_category_suggestions";
    public static final String BUNDLE_EXTRA_WIDGET_CLICK = "bundle_extra_widget_click";

    public static final String BUNDLE_EXTRA_NUM_COUPONS_AVAILABLE = "num_coupons_available";

    public static final String COUPON_FRAGMENT_MODE = "coupon_fragment_mode";
    public static final String COUPON_PARCELABLE = "coupon_parcelable";

    public static final String DATE_PICKER_SHOWING = "date_picker_showing";
    public static final String DATE_PICKER_YEAR = "date_picker_year";
    public static final String DATE_PICKER_MONTH = "date_picker_month";
    public static final String DATE_PICKER_DAY_OF_MONTH = "date_picker_day_of_month";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ FragmentType.MY_PROFILE_FRAGMENT, FragmentType.NOTIFICATION_FRAGMENT })
    public @interface FragmentType {

        int MY_PROFILE_FRAGMENT = 2001;
        int NOTIFICATION_FRAGMENT = 2002;
    }

    public static final String BUNDLE_EXTRA_FRAGMENT_TYPE = "bundle_extra_fragment_type";
    public static final String BUNDLE_EXTRA_LOAD_COUPON_FRAGMENT = "bundle_extra_load_coupon_fragment";
    public static final String BUNDLE_EXTRA_VIEW_ALL = "bundle_extra_view_all";

    public static final String ACTION_WIDGET_UPDATE = "com.example.mycoupons.ACTION_WIDGET_UPDATE";
    public static final String ACTION_SHOW_NOTIFICATION = "com.example.mycoupons.ACTION_SHOW_NOTIFICATION";

    public static final String ACTION_GOOGLE_DRIVE_SYNC = "com.example.mycoupons.ACTION_GOOGLE_DRIVE_SYNC";
    public static final int CONNECTION_RESOLUTION_REQUEST_CODE = 3001;

    public static final String IS_SYNC_RUNNING = "preference_is_sync_running";
    public static final String SYNC_MODE = "preference_sync_mode";

}
