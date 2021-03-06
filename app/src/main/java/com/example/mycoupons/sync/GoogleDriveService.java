package com.example.mycoupons.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.example.mycoupons.R;
import com.example.mycoupons.event.SyncCompleteEvent;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.notification.CouponsTrackerNotification;
import com.example.mycoupons.util.Constants;
import com.example.mycoupons.util.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

public abstract class GoogleDriveService extends IntentService {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ SyncMode.EXPORT, SyncMode.IMPORT })
    public @interface SyncMode {
        int EXPORT = 20;
        int IMPORT = 21;
    }

    private int syncMode = -1;
    private GoogleApiClient googleApiClient;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public GoogleDriveService() {
        super("com.example.mycoupons.WorkerThread");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        DebugLog.logMethod();
        if (intent == null
                || intent.getAction() == null
                || !intent.getAction().equals(Constants.ACTION_GOOGLE_DRIVE_SYNC)) {
            DebugLog.logMessage("Not ACTION_GOOGLE_DRIVE_SYNC hence not handled here");
            return;
        }
        DebugLog.logMessage("Action: " + intent.getAction());

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .build();
        ConnectionResult connectionResult = googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            DebugLog.logMessage("ConnectionResult: " + connectionResult.toString());
            DebugLog.logMessage("ConnectionResult error: " + connectionResult.getErrorCode() + "\n" + connectionResult.getErrorMessage());
            showError(getString(R.string.google_drive_no_resolution));
            return;
        }

        boolean isSuccess = handleIntent();
        googleApiClient.disconnect();
        // Update SharedPreferences indicating completion of sync task
        Utilities.updateSharedPreferences(getApplicationContext(), false, -1);
        DebugLog.logMessage("Completed sync task");

        if (!isSuccess) {
            DebugLog.logMessage("Sync not successful");
            return;
        }

        EventBus.getDefault().post(new SyncCompleteEvent());
        // Create and show sync task success notification.
        CouponsTrackerNotification notification = new CouponsTrackerNotification(getApplicationContext());
        notification.showSyncSuccessNotification(syncMode);
    }

    protected abstract boolean handleIntent();

    /**
     * Returns the app specific {@link DriveFile} if it has already been created. Else
     * returns null.
     */
    protected final DriveFile getDriveFile() {
        DebugLog.logMethod();

        DriveApi.MetadataBufferResult metadataBufferResult = Drive.DriveApi.getAppFolder(getGoogleApiClient())
                .listChildren(getGoogleApiClient())
                .await();
        DebugLog.logMessage("Status code: " + metadataBufferResult.getStatus().getStatusCode()
                + "\nStatus message: " + metadataBufferResult.getStatus().getStatusMessage());
        if (!metadataBufferResult.getStatus().isSuccess()) {
            metadataBufferResult.release();
            DebugLog.logMessage("MetadataBufferResult failure");
            return null;
        }

        MetadataBuffer metadataBuffer = metadataBufferResult.getMetadataBuffer();
        DebugLog.logMessage("MetadataBuffer count: " + metadataBuffer.getCount());
        if (metadataBuffer.getCount() == 0) {
            metadataBuffer.release();
            return null;
        }
        DriveId driveId = metadataBuffer.get(0).getDriveId();
        metadataBuffer.release();
        metadataBufferResult.release();
        return driveId.asDriveFile();
    }

    protected void showError(String message) {
        DebugLog.logMethod();
        DebugLog.logMessage(message);

        EventBus.getDefault().post(new SyncCompleteEvent());
        // Create and show sync task error notification.
        CouponsTrackerNotification notification = new CouponsTrackerNotification(getApplicationContext());
        notification.showSyncErrorNotification(syncMode);
    }

    protected GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    protected void setSyncMode(int syncMode) {
        this.syncMode = syncMode;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DebugLog.logMethod();
        Utilities.updateSharedPreferences(getApplicationContext(), false, -1);
    }
}
