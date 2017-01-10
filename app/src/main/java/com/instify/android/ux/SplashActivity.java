package com.instify.android.ux;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instify.android.R;
import com.instify.android.app.MyApplication;

import timber.log.Timber;

public class SplashActivity extends Activity {

    public FirebaseAnalytics mFirebaseAnalytics;
    // Firebase instance variables
    private FirebaseUser mFirebaseUser;

    /**
     * layoutIntroScreen is faded using animations to get LayoutContent
     */
    private View layoutIntroScreen;
    private View layoutContent;

    /**
     * Indicates that window has been already detached.
     */
    private boolean windowDetached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //layoutContent = findViewById(R.id.splash_content);
        layoutIntroScreen = findViewById(R.id.splash_content);
        // Obtain the FirebaseAnalytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Obtain the current logged in user
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Splash screen timer
        final int SPLASH_TIME_OUT = 1000;

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            public void run() {
                if (MyApplication.getInstance().getPrefManager().getIsFirstRun()) {
                    Intent i = new Intent(SplashActivity.this, IntroActivity.class);
                    startActivity(i);
                    finish();
                } else if (mFirebaseUser == null) {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    Timber.d("Pass to auth activity");
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    Timber.d("Pass to main activity");
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * Hide intro screen and display content layout with animation.
     */
    private void animateContentVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (windowDetached) {
                            if (layoutContent != null) layoutContent.setVisibility(View.VISIBLE);
                        } else {
//                            // If lollipop use reveal animation. On older phones use fade animation.
                            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                Timber.d("Circular animation.");
                                // get the center for the animation circle
                                final int cx = (layoutContent.getLeft() + layoutContent.getRight()) / 2;
                                final int cy = (layoutContent.getTop() + layoutContent.getBottom()) / 2;

                                // get the final radius for the animation circle
                                int dx = Math.max(cx, layoutContent.getWidth() - cx);
                                int dy = Math.max(cy, layoutContent.getHeight() - cy);
                                float finalRadius = (float) Math.hypot(dx, dy);

                                Animator animator = ViewAnimationUtils.createCircularReveal(layoutContent, cx, cy, 0, finalRadius);
                                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                animator.setDuration(1250);
                                layoutContent.setVisibility(View.VISIBLE);
                                animator.start();
                            } else {
                                Timber.d("Alpha animation.");
                                layoutContent.setAlpha(0f);
                                layoutContent.setVisibility(View.VISIBLE);
                                layoutContent.animate()
                                        .alpha(1f)
                                        .setDuration(1000)
                                        .setListener(null);
                            }
                        }
                    }
                }, 330);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onAttachedToWindow() {
        windowDetached = false;
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        windowDetached = true;
        super.onDetachedFromWindow();
    }
}