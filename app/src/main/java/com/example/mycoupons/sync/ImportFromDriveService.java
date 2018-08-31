package com.example.mycoupons.sync;

import android.content.ContentValues;

import com.example.mycoupons.database.CouponContract;
import com.example.mycoupons.logger.DebugLog;
import com.example.mycoupons.model.Coupon;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ImportFromDriveService extends GoogleDriveService {
    @Override
    protected boolean handleIntent() {
        DebugLog.logMethod();
        try {
            // Get coupon data json file from app folder
            DriveFile driveFile = getDriveFile();
            if (driveFile == null) {
                showError("No coupon data available in drive");
                return false;
            }

            // Get the json array of coupons
            String couponsJson = getCouponsJson(driveFile);
            if (couponsJson == null) {
                showError("Error while reading file contents");
                return false;
            }

            // Bulk insert the coupons in db
            getApplicationContext().getContentResolver().bulkInsert(
                    CouponContract.CouponTable.URI,
                    getContentValuesArray(couponsJson)
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            DebugLog.logMessage(e.getMessage());
            showError("Error: " + e.getMessage());
            return false;
        }
    }

    private String getCouponsJson(DriveFile driveFile) {
        DebugLog.logMethod();
        // Open file in read mode
        DriveApi.DriveContentsResult driveContentsResult = driveFile
                .open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null)
                .await();

        DebugLog.logMessage("Status code: " + driveContentsResult.getStatus().getStatusCode()
                + "\nStatus message: " + driveContentsResult.getStatus().getStatusMessage());

        if (!driveContentsResult.getStatus().isSuccess()) {
            DebugLog.logMessage("DriveContentsResult failure");
            return null;
        }

        // Read the contents of the file line by line and generate the json.
        DriveContents driveContents = driveContentsResult.getDriveContents();
        BufferedReader reader = new BufferedReader(new InputStreamReader(driveContents.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            DebugLog.logMessage(e.getMessage());
        }
        driveContents.discard(getGoogleApiClient());
        DebugLog.logMessage("Coupons json: " + builder.toString());
        return builder.toString();
    }


    private ContentValues[] getContentValuesArray(String couponsJson) {
        Gson gson = new Gson();
        /*
        Convert the json representation of the coupon data to an arrayList
        of coupons. Logic to perform this taken from:
        https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Generic-Types
         */
        Type collectionType = new TypeToken<ArrayList<Coupon>>(){}.getType();
        ArrayList<Coupon> coupons = gson.fromJson(couponsJson, collectionType);
        ContentValues[] contentValuesArray = new ContentValues[coupons.size()];
        for (int i = 0; i < contentValuesArray.length; i++) {
            contentValuesArray[i] = Coupon.getContentValues(coupons.get(i));
        }
        return contentValuesArray;
    }
}
