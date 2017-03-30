package com.instify.android.ux;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instify.android.R;

import timber.log.Timber;

/**
 * Created by Abhish3k on 29-03-2017.
 */

public class ProfilePictureFullScreenActivity extends AppCompatActivity {

    private static final String TAG = ProfilePictureFullScreenActivity.class.getSimpleName();

    private AdView mAdView;
    // [START_EXCLUDE]
    private InterstitialAd mInterstitialAd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_profile_picture);

        FirebaseUser mFirebaseUser;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // [START initialize_auth]
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // [END initialize_auth]

        ImageView userImage = (ImageView) findViewById(R.id.fullimg);
        TextView displayName = (TextView) findViewById(R.id.user_name);

        // Assign the name
        displayName.setText(mFirebaseUser.getDisplayName());
        // Put the picture into the image View
        Glide.with(this)
                .load(mFirebaseUser.getPhotoUrl()).placeholder(R.drawable.default_pic_face)
                .crossFade()
                .centerCrop()
                .into(userImage);
        userImage.setScaleType(ImageView.ScaleType.FIT_XY);

        // Initialize Mobile Ads (AdWords)
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // [END load_banner_ad]

        // AdMob ad unit IDs are not currently stored inside the google-services.json file.
        // Developers using AdMob can store them as custom values in a string resource file or
        // simply use constants. Note that the ad units used here are configured to return only test
        // ads, and should not be used outside this sample.

        // [START instantiate_interstitial_ad]
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        // [START create_interstitial_ad_listener]
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                // Ad received, ready to display
                mInterstitialAd.isLoaded();
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                // See https://goo.gl/sCZj0H for possible error codes.
                Timber.w(TAG, "onAdFailedToLoad:" + i);
            }
        });
        // [END create_interstitial_ad_listener]
    }

    /**
     * Load a new interstitial ad asynchronously.
     */
    // [START request_new_interstitial]
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
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
        if (!mInterstitialAd.isLoaded()) {
            requestNewInterstitial();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    // [END add_lifecycle_methods]

    @VisibleForTesting
    AdView getAdView() {
        return mAdView;
    }
}
