package com.instify.android.app;

/**
 * Created by Abhish3k on 1/8/2017.
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.instify.android.BuildConfig;
import com.instify.android.helpers.PreferenceManager;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.ux.IntroActivity;
import com.squareup.leakcanary.LeakCanary;
import com.thefinestartist.Base;
import io.fabric.sdk.android.Fabric;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class AppController extends MultiDexApplication {
  public static final String TAG = AppController.class.getSimpleName();

  private static AppController mInstance;
  public FirebaseAnalytics mFirebaseAnalytics;
  private RequestQueue mRequestQueue;
  private PreferenceManager mPrefs;
  private SQLiteHandler sqLiteHandler;

  static {
    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
  }

  public static synchronized AppController getInstance() {
    return mInstance;
  }

  @Override public void onCreate() {
    super.onCreate();
    mInstance = this;
    FirebaseApp.initializeApp(this);
    // Disk Persistence in Firebase
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Fixes crash reported on Firebase, issue : https://github.com/TheFinestArtist/FinestWebView-Android/issues/79
    Base.initialize(this);
    // Initialize the SQLiteHandler
    sqLiteHandler = new SQLiteHandler(this);

    // Check Build Config for debugging libraries
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
      // Set Crash Reporting to true
      Fabric.with(this, new Crashlytics());
      // Initialize FirebaseRemoteConfig
      FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
      // set in-app defaults
      Map<String, Object> remoteConfigDefaults = new HashMap();

      // Check for app update required parameter
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_APP_UPDATE_REQUIRED, false);
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_CURRENT_VERSION, "2.1.0");
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_UPDATE_URL,
          "https://play.google.com/store/apps/details?id=com.instify.android");

      // Check for api update required parameter
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_API_UPDATE_REQUIRED, false);
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_URL_LOGIN,
          "https://fnplus.xyz/srm-api/get-info.php");
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_URL_GET_ATTENDANCE,
          "https://fnplus.xyz/srm-api/get-aatd.php");
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_URL_GET_TT,
          "https://fnplus.xyz/srm-api/get-ptt.php");
      remoteConfigDefaults.put(ForceRemoteConfigUpdateChecker.KEY_URL_GET_FEE,
          "https://fnplus.xyz/srm-api/fee_details.php");

      firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
      firebaseRemoteConfig.fetch(120) // fetch every 2 minutes
          .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override public void onComplete(@NonNull Task<Void> task) {
              if (task.isSuccessful()) {
                Timber.d(TAG, "RemoteConfig is fetched.");
                firebaseRemoteConfig.activateFetched();
              }
            }
          });
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

  public static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    return dir.delete();
  }

  public void clearApplicationData() {
    File cache = getCacheDir();
    File appDir = new File(cache.getParent());
    if(appDir.exists()){
      String[] children = appDir.list();
      for(String s : children){
        if(!s.equals("lib")){
          deleteDir(new File(appDir, s));
          Timber.i("File /data/data/APP_PACKAGE/" + s +" DELETED");
        }
      }
    }
  }

  public void logoutUser() {
    // Clear cache
    clearApplicationData();
    // Clear shared preferences data
    getPrefManager().clear();
    // Set first run to true
    getPrefManager().setIsFirstRun(true);
    // Delete database
    sqLiteHandler.deleteUsers();
    // Sign-out from Firebase
    FirebaseAuth.getInstance().signOut();
    // Launch the intro activity
    Intent intent = new Intent(this, IntroActivity.class);
    // Closing all the Activities & Add new Flag to start new Activity
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
  }
}
