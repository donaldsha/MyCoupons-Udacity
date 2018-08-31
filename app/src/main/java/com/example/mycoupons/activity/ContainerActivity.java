package com.example.mycoupons.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.mycoupons.R;
import com.example.mycoupons.adapter.CouponListAdapter;
import com.example.mycoupons.adapter.CouponListCursorAdapter;
import com.example.mycoupons.fragment.CouponFragment;
import com.example.mycoupons.fragment.MyProfileFragment;
import com.example.mycoupons.fragment.NotificationCouponListFragment;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.model.Coupon;
import com.example.mycoupons.util.Constants;
import com.example.mycoupons.util.Utilities;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

public class ContainerActivity extends AppCompatActivity implements
        CouponListAdapter.OnCouponClickListener,
        CouponListCursorAdapter.OnCursorCouponClickListener{

    private int fragmentType = -1;
    private boolean isTablet = false;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        DebugLog.logMethod();

        isTablet = getResources().getBoolean(R.bool.is_tablet);

        fragmentType = getIntent().getIntExtra(Constants.BUNDLE_EXTRA_FRAGMENT_TYPE, -1);
        if (fragmentType == -1) {
            finish();
            return;
        }

        Utilities.navigateIfSyncInProgress(getApplicationContext());

        if (fragmentType == Constants.FragmentType.MY_PROFILE_FRAGMENT) {
            // Load MyProfileFragment only if it is not already present
            if (getSupportFragmentManager().findFragmentByTag(MyProfileFragment.TAG) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(getFragmentContainerId(false), new MyProfileFragment(), MyProfileFragment.TAG)
                        .commit();
            }
        } else if (fragmentType == Constants.FragmentType.NOTIFICATION_FRAGMENT) {
            // Load NotificationCouponListFragment only if it is not already present
            if (getSupportFragmentManager().findFragmentByTag(NotificationCouponListFragment.TAG) == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(getFragmentContainerId(false), new NotificationCouponListFragment(), NotificationCouponListFragment.TAG)
                        .commit();
            }
        }
    }


    @Override
    public void onCursorCouponClick(Coupon coupon) {
        DebugLog.logMethod();
        DebugLog.logMessage("Coupon: " + coupon.toString());
        showCouponFromFragment(coupon);
    }

    @Override
    public void onCouponClick(Coupon coupon) {
        DebugLog.logMethod();
        DebugLog.logMessage("Coupon: " + coupon.toString());
        showCouponFromFragment(coupon);
    }


    private void showCouponFromFragment(Coupon coupon) {
        DebugLog.logMethod();
        Bundle extras = getExtras(CouponFragment.Mode.VIEW, coupon);

        // Start CouponActivity if is not a tablet
        if (!isTablet) {
            Intent intent = new Intent(this, CouponActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
            return;
        }

        CouponFragment couponFragment = new CouponFragment();
        couponFragment.setArguments(extras);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(getFragmentContainerId(true), couponFragment, CouponFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    private Bundle getExtras(int mode, Coupon coupon) {
        DebugLog.logMethod();
        return getExtras(mode, coupon, new ArrayList<String>(), new ArrayList<String>());
    }

    private Bundle getExtras(int mode, Coupon coupon, ArrayList<String> merchantSuggestions, ArrayList<String> categorySuggestions) {
        DebugLog.logMethod();
        Bundle extras = new Bundle();
        extras.putInt(Constants.COUPON_FRAGMENT_MODE, mode);
        extras.putParcelable(Constants.COUPON_PARCELABLE, coupon);
        extras.putStringArrayList(Constants.BUNDLE_EXTRA_MERCHANT_SUGGESTIONS, merchantSuggestions);
        extras.putStringArrayList(Constants.BUNDLE_EXTRA_CATEGORY_SUGGESTIONS, categorySuggestions);
        return extras;
    }


    private int getFragmentContainerId(boolean isCouponView) {
        DebugLog.logMethod();
        if (!isTablet) {
            return R.id.fragment_container;
        }
        int id = R.id.fragment_container_left;
        if (isCouponView) {
            id = R.id.fragment_container_right;
        }
        return id;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DebugLog.logMethod();
        if (item.getItemId() == android.R.id.home) {
            DebugLog.logMessage("Home button pressed");
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initInterstitialAd() {
        DebugLog.logMethod();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                DebugLog.logMethod();
                ContainerActivity.this.finish();
            }
        });
        requestNewInterstitial();
    }

    @Override
    public void onBackPressed() {
        DebugLog.logMethod();
        DebugLog.logMessage("BackStackEntryCount: " + getSupportFragmentManager().getBackStackEntryCount());
        /*
        If it is not MyProfileFragment or there still are coupons displayed by
        CouponFragment, do not load interstitial ad.
         */
        if (fragmentType != Constants.FragmentType.MY_PROFILE_FRAGMENT
                || getSupportFragmentManager().getBackStackEntryCount() > 0) {
            super.onBackPressed();
            return;
        }

        DebugLog.logMessage("Show Interstitial Ad");

        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Loads a new interstitial ad.
     */
    private void requestNewInterstitial() {
        DebugLog.logMethod();
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice(getString(R.string.test_device_id))
                .build();
        interstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DebugLog.logMethod();
        initInterstitialAd();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DebugLog.logMethod();
        interstitialAd.setAdListener(null);
        interstitialAd = null;
    }

}
