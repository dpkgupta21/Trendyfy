package com.trendyfy.utility;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.trendyfy.activity.HomeActivity;
import com.trendyfy.preference.AppPreference;
import com.trendyfy.preference.PreferenceHelp;

public class SessionManager {

    private static final String TAG = "<SessionManager>";
    private Context mContext;

    // Constructor
    public SessionManager(Context mContext) {
        this.mContext = mContext;

        Log.d(TAG, "session manager onstructor called");
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        AppPreference.removeObjectIntoPref(mContext, PreferenceHelp.USER_INFO);
        AppPreference.removeObjectIntoPref(mContext, PreferenceHelp.ADDED_PRODUCT);

        // Remove city from preferences
        AppPreference.setCityId(mContext, null);
        AppPreference.setCityName(mContext, null);
        AppPreference.setLocationId(mContext, null);
        AppPreference.setLocationName(mContext, null);
        AppPreference.setTotalCartPrice(mContext, 0.0f);

        //TraphoriaPreference.setLoggedIn(mContext, false);
        // After logout redirect user to Loing Activity
        Intent i = new Intent(mContext, HomeActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        mContext.startActivity(i);
        Utils.ShowLog(TAG, "logging out user");
    }


}
