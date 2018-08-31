package com.example.mycoupons.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.mycoupons.logger.DebugLog;

public class CouponWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        DebugLog.logMethod();
        return new CouponRemoteViewsFactory(getApplicationContext());
    }
}
