package com.instify.android.ux;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.instify.android.R;

/**
 * Created by Abhish3k on 24-03-2017.
 */

public class SupportUsActivity extends AppCompatActivity {

    // Declare AdView
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_us);

        final TextView phoneNumber = (TextView) findViewById(R.id.copy);
        phoneNumber.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Phone Number : ", "9962892900");
            clipboard.setPrimaryClip(clip);
            Toast.makeText(SupportUsActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        // [START load_banner_ad]
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // [END load_banner_ad]
    }

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
