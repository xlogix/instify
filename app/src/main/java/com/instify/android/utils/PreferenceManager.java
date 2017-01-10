package com.instify.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by Abhish3k on 1/10/2017.
 */

public class PreferenceManager {

    // Shared Preferences
    SharedPreferences mPrefs;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "app_data";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";

    // Constructor
    public PreferenceManager(Context context) {
        this._context = context;
        mPrefs = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mPrefs.edit();
    }

    public static PreferenceManager newInstance(Context context) {
        return new PreferenceManager(context);
    }

    public boolean getIsFirstRun() {
        return mPrefs.getBoolean(ConstantsUtil.IS_FIRST_RUN, true);
    }

    public void setIsFirstRun(boolean firstRun) {
        mPrefs.edit().putBoolean(ConstantsUtil.IS_FIRST_RUN, firstRun).apply();
    }

    public void addNotification(String notification) {

        // get old notifications
        String oldNotifications = getNotifications();

        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return mPrefs.getString(KEY_NOTIFICATIONS, null);
    }

    public boolean getIsDarkTheme() {
        return mPrefs.getBoolean(ConstantsUtil.IS_DARK_THEME, false);
    }

    public void setIsDarkTheme(boolean isDarkTheme) {
        mPrefs.edit().putBoolean(ConstantsUtil.IS_DARK_THEME, isDarkTheme).apply();
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
