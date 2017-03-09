package com.instify.android.ux;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.instify.android.R;
import com.instify.android.helpers.MyAppWebViewClient;

import timber.log.Timber;

/**
 * Created by Abhish3k on 09-03-2017.
 */

public class FeekartWebViewActivity extends Activity {

    private static final String URL = "http://feekart.srmuniv.ac.in/srmopp/";

    public SwipeRefreshLayout swipeRefreshLayout;
    private WebView mWebView;

    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feekart_webview);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_webview);
        swipeRefreshLayout.setColorSchemeResources(R.color.red_primary, R.color.black, R.color.google_blue_500);

        swipeRefreshLayout.setRefreshing(true);

        // Find the WebView
        mWebView = (WebView) findViewById(R.id.webView);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore the previous URL and history stack
            mWebView.restoreState(savedInstanceState);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(0);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT > 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT <= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        }

        // Webview Settings [START]
        // Stop local links and redirects from opening in browser instead of WebView
        mWebView.setWebViewClient(new MyAppWebViewClient());

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);

        // Enable JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // Enable FileUpload
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        // Enable Database
        mWebView.getSettings().setDatabaseEnabled(true);
        // Other Settings
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setSupportMultipleWindows(true);

        // Accepts Cookies Now
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }
        // WebView Settings [STOP]

        mWebView.loadUrl(URL);
        delayedRefresh();

        // Implementing onRefresh Listener. Trigger for manual refresh.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                mWebView.reload();
                delayedRefresh();
            }
        });
    }

    // Make it look like website is loading!
    void delayedRefresh() {
        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        };
        timerThread.start();
    }

    // Prevent the back-button from closing the app
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // Check if device is online
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDeviceOnline()) {
            Timber.d("Network available");
            //Toast.makeText(MainActivity.this, "Device Online", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FeekartWebViewActivity.this, "Device Offline. Functionality may be limited", Toast.LENGTH_SHORT).show();
        }
    }
}
