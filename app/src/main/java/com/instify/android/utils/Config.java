package com.instify.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class Config {
    private SharedPreferences mPrefs;

    private Config(Context context) {
        mPrefs = context.getSharedPreferences(ConstantsUtil.PREFS_KEY, Context.MODE_PRIVATE);
    }

    public static Config newInstance(Context context) {
        return new Config(context);
    }

    public boolean getIsFirstRun() {
        return mPrefs.getBoolean(ConstantsUtil.IS_FIRST_RUN, true);
    }

    public void setIsFirstRun(boolean firstRun) {
        mPrefs.edit().putBoolean(ConstantsUtil.IS_FIRST_RUN, firstRun).apply();
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
}
