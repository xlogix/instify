package com.instify.android.ux.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.instify.android.R;
import com.instify.android.helpers.MyAppWebViewClient;

public class LabzFragment extends Fragment {

    public LabzFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LabzFragment newInstance() {
        LabzFragment fragment = new LabzFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG = LabzFragment.class.getSimpleName();
    private static final String URL = "https://xlogix.github.io/instify/";

    public SwipeRefreshLayout mSwipeRefreshLayout;
    private WebView mWebView;


    @SuppressLint({"SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_labz, container, false);
        // Views declaration
        mWebView = (WebView) rootView.findViewById(R.id.webView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.red500, R.color.black, R.color.google_blue_500);
            mSwipeRefreshLayout.setRefreshing(true);
        }

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
        } else if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT <= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // [START] WebView Settings
        // Stop local links and redirects from opening in browser instead of WebView
        mWebView.setWebViewClient(new MyAppWebViewClient());

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setScrollbarFadingEnabled(true);

        // Enable JavaScript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // Enable Database
        mWebView.getSettings().setDatabaseEnabled(true);
        // Other Settings
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSupportMultipleWindows(true);

        // Accepts Cookies Now
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
        }
        // [END] WebView Settings

        mWebView.loadUrl(URL);
        delayedRefresh();

        // Implementing onRefresh Listener. Trigger for manual refresh.
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mWebView.reload();
                delayedRefresh();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        };
        timerThread.start();
    }
}
