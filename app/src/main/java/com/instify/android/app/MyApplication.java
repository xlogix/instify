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
import com.instify.android.BuildConfig;
import com.instify.android.utils.ActivityFrameMetricsUtil;
import com.instify.android.utils.PreferenceManager;
import com.instify.android.ux.SplashActivity;

import timber.log.Timber;

public class MyApplication extends Application {
    public static final String TAG = MyApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static MyApplication mInstance;

    private PreferenceManager pref;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        if (BuildConfig.DEBUG) {
            // Plant Tiber debug tree
            Timber.plant(new Timber.DebugTree());
            // Initialize Activity frame matrix for analytics
            registerActivityLifecycleCallbacks(new ActivityFrameMetricsUtil.Builder().build());
        } else {
            // TODO example of implementation custom crash reporting solution -  Crashlytics.
            /*Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());*/
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public PreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new PreferenceManager(this);
        }

        return pref;
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

    public void logout() {
        pref.clear();
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}