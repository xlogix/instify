package com.instify.android.ux;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.google.firebase.auth.FirebaseAuth;
import com.instify.android.R;
import com.instify.android.app.AppController;
import timber.log.Timber;

public class SplashActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    // Splash screen timer
    final int SPLASH_TIME_OUT = 500;

    new Handler().postDelayed(new Runnable() {
      /*
       * Showing splash screen with a timer. This will be useful when you
       * want to show case your app logo / company
       */
      public void run() {
        if (AppController.getInstance().getPrefManager().getIsFirstRun()) {
          startActivity(new Intent(SplashActivity.this, IntroActivity.class));
          finish();
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null && AppController.getInstance()
            .getPrefManager()
            .isLoggedIn()) {
          Timber.d("Pass to main activity");
          startActivity(new Intent(SplashActivity.this, MainActivity.class));
          finish();
        } else {
          Timber.d("Pass to auth activity");
          startActivity(new Intent(SplashActivity.this, LoginActivity.class));
          finish();
        }
      }
    }, SPLASH_TIME_OUT);
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override protected void onStop() {
    super.onStop();
  }
}