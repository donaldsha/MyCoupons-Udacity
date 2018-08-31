package com.example.mycoupons.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.mycoupons.R;
import com.example.mycoupons.fragment.CouponFragment;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.util.Constants;
import com.example.mycoupons.util.Utilities;

public class CouponActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        DebugLog.logMethod();
        if (getIntent() == null
                || getIntent().getExtras() == null) {
            DebugLog.logMessage("Finishing CouponActivity");
            finish();
            return;
        }

        if (getIntent().getBooleanExtra(Constants.BUNDLE_EXTRA_VIEW_ALL, false)) {
            finish();
            return;
        }

        Utilities.navigateIfSyncInProgress(getApplicationContext());

        if (getSupportFragmentManager() != null
                && getSupportFragmentManager().findFragmentByTag(CouponFragment.TAG) != null) {
            DebugLog.logMessage("Not null");
            return;
        }

        CouponFragment couponFragment = new CouponFragment();
        couponFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, couponFragment, CouponFragment.TAG)
                .commit();
    }

}
