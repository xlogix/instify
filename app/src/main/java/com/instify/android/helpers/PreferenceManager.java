package com.instify.android.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import timber.log.Timber;
import com.instify.android.app.Themes;

/**
 * Created by Abhish3k on 1/10/2017.
 */

public class PreferenceManager {

    // Shared pref file name
    private static final String PREF_NAME = "app_data";
    // All shared preferences keys
    private static final String REG_ID = "reg_id";
    private static final String IS_FIRST_RUN = "is_first_run";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String SENT_REG_TOKEN_TO_SERVER = "sentRegTokenToServer";
    // Shared preferences
    private SharedPreferences mPrefs;
    // Editor for shared preferences
    private SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public PreferenceManager(Context context) {
        this._context = context;
        mPrefs = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mPrefs.edit();
    }

    /**
     * Sets the theme of the app
     *
     * @return theme name value
     */
    public Themes getCurrentTheme(Context context) {
        return Themes.valueOf(mPrefs.getString("app_theme", Themes.Blue.name()));
    }

    public void setCurrentTheme(Themes currentTheme) {
        editor.putString("app_theme", currentTheme.name());
    }

    /**
     * Save the registration ID of the user
     *
     * @return void
     */
    public String getRegId() {
        return mPrefs.getString(REG_ID, "reg_id");
    }

    public void setRegId(String regId) {
        editor.putString(REG_ID, regId);
    }

    public void sentRegIdToServer(Boolean value) {
        editor.putBoolean(SENT_REG_TOKEN_TO_SERVER, value);
        // commit changes
        editor.commit();
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
     * Check if the user is running the app for the first time. Used to check if Intro Activities should be showed.
     *
     * @return boolean value, true or false
     */
    public boolean isLoggedIn() {
        return mPrefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        // commit changes
        editor.commit();

        Timber.d("User login session modified!");
    }

    /**
     * Clear shared preferences data (reset)
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }
}
