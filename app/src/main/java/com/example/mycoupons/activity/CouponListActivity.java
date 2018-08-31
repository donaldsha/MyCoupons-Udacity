package com.example.mycoupons.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.mycoupons.R;
import com.example.mycoupons.adapter.CouponListAdapter;
import com.example.mycoupons.adapter.FragmentTabsAdapter;
import com.example.mycoupons.database.CouponContract;
import com.example.mycoupons.event.FabVisibilityChangeEvent;
import com.example.mycoupons.fragment.CouponFragment;
import com.example.mycoupons.fragment.PastCouponListFragment;
import com.example.mycoupons.fragment.UpcomingCouponListFragment;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.model.Coupon;
import com.example.mycoupons.util.Constants;
import com.example.mycoupons.util.Utilities;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CouponListActivity extends AppCompatActivity implements CouponListAdapter.OnCouponClickListener {

    private Unbinder unbinder;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.floating_action_button)
    FloatingActionButton fab;

    private UpcomingCouponListFragment upcomingCouponListFragment;
    private PastCouponListFragment pastCouponListFragment;

    private boolean isTablet;
    private boolean loadFragmentFromWidgetClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);
        unbinder = ButterKnife.bind(this);

        DebugLog.logMethod();

        Utilities.setAlarmsOnFirstLaunch(getApplicationContext());

        Utilities.navigateIfSyncInProgress(getApplicationContext());

        isTablet = getResources().getBoolean(R.bool.is_tablet);

        toolbar.setTitle(getString(R.string.app_name));
        toolbar.inflateMenu(R.menu.home_screen);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DebugLog.logMethod();
                switch (item.getItemId()) {
                    case R.id.menu_item_my_profile: {
                        DebugLog.logMessage("My profile");
                        Intent intent = new Intent(CouponListActivity.this, ContainerActivity.class);
                        intent.putExtra(Constants.BUNDLE_EXTRA_FRAGMENT_TYPE, Constants.FragmentType.MY_PROFILE_FRAGMENT);
                        startActivity(intent);
                        return true;
                    }

                    case R.id.menu_item_settings: {
                        DebugLog.logMessage("Settings");
                        Intent intent = new Intent(CouponListActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        return true;
                    }

                    case R.id.menu_item_sample_data: {
                        DebugLog.logMessage("Sample data");
                        if (!Utilities.isSampleDataAdded(getApplicationContext())) {
                            Utilities.showToast(getApplicationContext(), getString(R.string.warning_sample_data));
                            new AddSampleDataTask().execute();
                        } else {
                            Utilities.showToast(getApplicationContext(), getString(R.string.multiple_add_sample_data));
                        }
                    }
                }
                return true;
            }
        });
        initFragments(savedInstanceState);
        setUpViewPager();

        tabLayout.setupWithViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTablet) {
                    startCouponActivity(CouponFragment.Mode.CREATE, Coupon.getDummy());
                } else {
                    loadCouponFragment(CouponFragment.Mode.CREATE, Coupon.getDummy());
                }
            }
        });

        loadFragmentFromWidgetClick = true;
        if (savedInstanceState != null) {
            loadFragmentFromWidgetClick = savedInstanceState.getBoolean(Constants.BUNDLE_EXTRA_WIDGET_CLICK, true);
        }
        handleWidgetItemClick();
    }

    private void initFragments(Bundle savedInstanceState) {
        DebugLog.logMethod();
        if (savedInstanceState != null) {
            DebugLog.logMessage("Getting fragments");
            upcomingCouponListFragment = (UpcomingCouponListFragment) getSupportFragmentManager().getFragment(savedInstanceState, UpcomingCouponListFragment.TAG);
            pastCouponListFragment = (PastCouponListFragment) getSupportFragmentManager().getFragment(savedInstanceState, PastCouponListFragment.TAG);
        } else {
            DebugLog.logMessage("Creating fragments");
            upcomingCouponListFragment = new UpcomingCouponListFragment();
            pastCouponListFragment = new PastCouponListFragment();
        }
    }

    private void setUpViewPager() {
        DebugLog.logMethod();
        FragmentTabsAdapter adapter = new FragmentTabsAdapter(getSupportFragmentManager());
        adapter.addFragment(upcomingCouponListFragment, getString(R.string.title_upcoming_coupon_list_fragment));
        adapter.addFragment(pastCouponListFragment, getString(R.string.title_past_coupon_list_fragment));
        viewPager.setAdapter(adapter);
    }

    /**
     * Loads the appropriate coupon that was clicked in the widget.
     */
    private void handleWidgetItemClick() {
        DebugLog.logMethod();
        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
            return;
        }

        DebugLog.logMessage("Load coupon fragment");
        Bundle bundle = intent.getExtras();
        boolean loadCouponFragment = intent.getBooleanExtra(Constants.BUNDLE_EXTRA_LOAD_COUPON_FRAGMENT, false);
        if (loadCouponFragment && loadFragmentFromWidgetClick) {
            loadFragmentFromWidgetClick = false;
            onCouponClick((Coupon) bundle.getParcelable(Constants.COUPON_PARCELABLE));
        }
    }

    @Override
    public void onCouponClick(Coupon coupon) {
        DebugLog.logMethod();
        if (!isTablet) {
            startCouponActivity(CouponFragment.Mode.VIEW, coupon);
        } else {
            loadCouponFragment(CouponFragment.Mode.VIEW, coupon);
        }
    }

    private void startCouponActivity(int mode, Coupon coupon) {
        DebugLog.logMethod();
        Intent intent = new Intent(this, CouponActivity.class);
        intent.putExtras(getExtras(mode, coupon));
        startActivity(intent);
    }

    private void loadCouponFragment(int mode, Coupon coupon) {
        DebugLog.logMethod();
        CouponFragment couponFragment = new CouponFragment();
        couponFragment.setArguments(getExtras(mode, coupon));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_right, couponFragment, CouponFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    private Bundle getExtras(int mode, Coupon coupon) {
        DebugLog.logMethod();
        Bundle extras = new Bundle();
        extras.putInt(Constants.COUPON_FRAGMENT_MODE, mode);
        extras.putParcelable(Constants.COUPON_PARCELABLE, coupon);
        extras.putStringArrayList(
                Constants.BUNDLE_EXTRA_MERCHANT_SUGGESTIONS,
                upcomingCouponListFragment.getMerchants() == null
                        ? new ArrayList<String>()
                        : upcomingCouponListFragment.getMerchants()
        );
        extras.putStringArrayList(
                Constants.BUNDLE_EXTRA_CATEGORY_SUGGESTIONS,
                upcomingCouponListFragment.getCategories() == null
                        ? new ArrayList<String>()
                        : upcomingCouponListFragment.getCategories()
        );
        return extras;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        DebugLog.logMethod();
        getSupportFragmentManager().putFragment(outState, UpcomingCouponListFragment.TAG, upcomingCouponListFragment);
        getSupportFragmentManager().putFragment(outState, PastCouponListFragment.TAG, pastCouponListFragment);
        outState.putBoolean(Constants.BUNDLE_EXTRA_WIDGET_CLICK, loadFragmentFromWidgetClick);
    }

    @Override
    protected void onDestroy() {
        DebugLog.logMethod();
        upcomingCouponListFragment = null;
        pastCouponListFragment = null;
        viewPager.setAdapter(null);
        unbinder.unbind();
        super.onDestroy();
    }

    private class AddSampleDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<Coupon> coupons = Utilities.getSampleData();
            ContentValues[] contentValuesArray = new ContentValues[coupons.size()];
            for (int i = 0, l = coupons.size(); i < l; i++) {
                contentValuesArray[i] = Coupon.getContentValues(coupons.get(i));
            }
            getContentResolver().bulkInsert(
                    CouponContract.CouponTable.URI,
                    contentValuesArray
            );
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Utilities.updateSampleDataAdded(getApplicationContext());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isTablet) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        if (isTablet) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFabVisibilityChangeEvent(FabVisibilityChangeEvent event) {
        if (event.showFab) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.INVISIBLE);
        }
    }

}
