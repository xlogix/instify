package xyz.fnplus.instify.ux;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import timber.log.Timber;
import xyz.fnplus.instify.R;
import xyz.fnplus.instify.app.AppController;

public class SplashActivity extends Activity {

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
        // Obtain the current logged in user
        FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Splash screen timer
        final int SPLASH_TIME_OUT = 1000;

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            public void run() {
                if (AppController.getInstance().getPrefManager().getIsFirstRun()) {
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                    finish();
                } else if (mFirebaseUser != null && AppController.getInstance().getPrefManager().isLoggedIn()) {
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