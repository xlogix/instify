package com.instify.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.instify.android.utils.Theme;

import timber.log.Timber;

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
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String IS_FIRST_RUN = "is_first_run";
    private static final String IS_DARK_THEME = "is_dark_theme";
    private static final String USER_REGNO = "user_regNo";
    private static final String USER_PASSWORD = "user_password";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_HAS_SET_PROFILE_PICTURE = "has_set_profile_picture";

    // Constructor
    public PreferenceManager(Context context) {
        this._context = context;
        mPrefs = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mPrefs.edit();
    }

    public static PreferenceManager newInstance(Context context) {
        return new PreferenceManager(context);
    }

    public String getNotifications() {
        return mPrefs.getString(KEY_NOTIFICATIONS, null);
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

    /**
     * Sets the theme of the app
     *
     * @return theme name value
     */
    public Theme getCurrentTheme(Context context) {
        return Theme.valueOf(mPrefs.getString("app_theme", Theme.Blue.name()));
    }

    public void setCurrentTheme(Theme currentTheme) {
        editor.putString("app_theme", currentTheme.name());
    }

    /**
     * Check if the user is running the app for the first time. Used to check if Intro Activities should be showed.
     *
     * @return boolean value, true or false
     */
    public boolean getIsFirstRun() {
        return mPrefs.getBoolean(IS_FIRST_RUN, true);
    }

    public void setIsFirstRun(boolean firstRun) {
        editor.putBoolean(IS_FIRST_RUN, firstRun).apply();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        // commit changes
        editor.commit();

        Timber.d("User login session modified!");
    }

    public boolean isLoggedIn() {
        return mPrefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /** Check if the user has set a profile picture
     *
     * @return boolean value, true or false
     */
    public boolean getHasSetProfilePicture() {
        return mPrefs.getBoolean(KEY_HAS_SET_PROFILE_PICTURE, true);
    }

    public void setHasSetProfilePicture(boolean profile) {
        editor.putBoolean(KEY_HAS_SET_PROFILE_PICTURE, profile).apply();
    }

    /**
     * Clear shared preferences data (reset)
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }
}
