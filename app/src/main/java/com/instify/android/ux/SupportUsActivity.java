package com.instify.android.ux;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.instify.android.R;

import timber.log.Timber;

/**
 * Created by Abhish3k on 24-03-2017.
 */

public class SupportUsActivity extends AppCompatActivity {
    private static final String TAG = SupportUsActivity.class.getSimpleName();

    // Declare AdView
    private AdView mAdView;
    // Initialize Ad
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_us);

        // [START instantiate_interstitial_ad]
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        // TODO : Remove test device before release
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("11408151BC4116DE6AD4B6BFC1B34457")
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        // [START create_interstitial_ad_listener]
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Add your method here
                Toast.makeText(getApplicationContext(),
                        "Thanks for supporting us! :)",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded() {
                Timber.w(TAG, "onAdLoaded:");
                // Ad received, ready to display
                mInterstitialAd.isLoaded();
                // Show the ad
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                // See https://goo.gl/sCZj0H for possible error codes.
                Timber.w(TAG, "onAdFailedToLoad:" + i);
            }

            @Override
            public void onAdOpened() {
                Timber.w(TAG, "onAdOpened: Add was opened");
            }

        });
        // [END create_interstitial_ad_listener]

        final TextView phoneNumber = (TextView) findViewById(R.id.copy);
        phoneNumber.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Phone Number : ", "9962892900");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(SupportUsActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // [START load_banner_ad]
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequestBanner = new AdRequest.Builder().build();
        mAdView.loadAd(adRequestBanner);
        // [END load_banner_ad]
    }

    /**
     * Load a new interstitial ad asynchronously.
     */
    // [START request_new_interstitial]
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("11408151BC4116DE6AD4B6BFC1B34457")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
    // [END request_new_interstitial]

    // [START add_lifecycle_methods]

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
            finish();
        }
        super.onDestroy();
    }
    // [END add_lifecycle_methods]
}
