package com.instify.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.instify.android.utils.Theme;

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
    private static final String IS_SIGNED_IN_FROM_GOOGLE_OR_FACEBOOK = "is_signed_in_from_google_or_facebook";
    private static final String SET_USER_PASSWORD = "set_user_password";

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

    /**
     * Check if the user is signed in from Google or Facebook
     *
     * @return boolean value, true or false
     */
    public boolean getSignedInFromGoogleOrFacebook() {
        return mPrefs.getBoolean(IS_SIGNED_IN_FROM_GOOGLE_OR_FACEBOOK, false);
    }

    public void setIsSignedInFromGoogleOrFacebook(boolean signedIn) {
        editor.putBoolean(IS_SIGNED_IN_FROM_GOOGLE_OR_FACEBOOK, true);
    }

    /**
     * Check if the user is signed in from Google or Facebook
     *
     * @return boolean value, true or false
     */
    public void setUserPassword(String password) {
        editor.putString(SET_USER_PASSWORD, password);
    }

    public String getUserPassword() {
        return mPrefs.getString(SET_USER_PASSWORD, "empty");
    }

    /**
     * Check if the user is signed in from Google or Facebook
     *
     * @return boolean value, true or false
     */
    public boolean getIsDarkTheme() {
        return mPrefs.getBoolean(IS_DARK_THEME, false);
    }

    public void setIsDarkTheme(boolean isDarkTheme) {
        editor.putBoolean(IS_DARK_THEME, isDarkTheme).apply();
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Clear shared preferences data (reset)
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }
}
