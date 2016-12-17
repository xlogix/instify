package com.instify.android.ux;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instify.android.R;

public class SplashActivity extends Activity {
    public FirebaseAnalytics mFirebaseAnalytics;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Obtain the FirebaseAnalytics, Auth instances
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        // Splash screen timer
        final int SPLASH_TIME_OUT = 1000;

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            public void run() {
                Boolean isFirstRun = getSharedPreferences("userData", MODE_PRIVATE).getBoolean("IsFirstRun", true);
                //String ifRegistered = getSharedPreferences("userData", MODE_PRIVATE).getString("CurrentUser", null);

                // This method will be executed once the timer is over
                if (isFirstRun) {
                    Intent i = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(i);
                    getSharedPreferences("userData", MODE_PRIVATE).edit().putBoolean("IsFirstRun", false).apply();
                    // close this activity
                    finish();
                } else if (mFirebaseUser == null) {
                    Intent i = new Intent(SplashActivity.this, AuthActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}