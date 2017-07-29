package com.instify.android.app;

/**
 * Created by Abhish3k on 1/8/2017.
 */

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.instify.android.BuildConfig;
import com.instify.android.helpers.PreferenceManager;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.ux.IntroActivity;
import com.squareup.leakcanary.LeakCanary;
import timber.log.Timber;

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    private RequestQueue mRequestQueue;
    private static AppController mInstance;
    private PreferenceManager mPrefs;
    public FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if (BuildConfig.DEBUG) {
            // Plant Tiber debug tree
            Timber.plant(new Timber.DebugTree());
            // Initialise Leak Canary
            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);
            // Initialise Stetho
            Stetho.initializeWithDefaults(this);
        } else {
            // Obtain the FirebaseAnalytics
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            // Set Analytics collection to true
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
            // Set Crash collection to true
            FirebaseCrash.setCrashCollectionEnabled(true);
        }
    }

    public PreferenceManager getPrefManager() {
        if (mPrefs == null) {
            mPrefs = new PreferenceManager(this);
        }

        return mPrefs;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void logoutUser() {
        mPrefs.clear();
        // SignOut from Firebase
        FirebaseAuth.getInstance().signOut();
        // Delete database
        SQLiteHandler sqLiteHandler = new SQLiteHandler(this);
        sqLiteHandler.deleteUsers();
        // Set First Run to true
        AppController.getInstance().getPrefManager().setIsFirstRun(true);
        // Launch the intro activity
        Intent intent = new Intent(this, IntroActivity.class);
        // Closing all the Activities & Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
