package com.example.mycoupons.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.mycoupons.R;
import com.example.mycoupons.fragment.SettingsFragment;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.util.Constants;

public class SettingsActivity extends AppCompatActivity {

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        DebugLog.logMethod();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.menu_item_settings));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG) == null) {
            DebugLog.logMessage("Null");
            settingsFragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, settingsFragment, SettingsFragment.TAG)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        DebugLog.logMethod();
        DebugLog.logMessage("requestCode: " + requestCode + ", successful: " + (resultCode == RESULT_OK));


        SettingsFragment settingsFragment = (SettingsFragment) getFragmentManager().findFragmentByTag(SettingsFragment.TAG);
        DebugLog.logMessage("Settings Fragment is null? " + (settingsFragment == null));
        if (settingsFragment != null
                && requestCode == Constants.CONNECTION_RESOLUTION_REQUEST_CODE
                && resultCode == RESULT_OK) {
            settingsFragment.connectGoogleApiClient();
        }
    }
}

