package com.instify.android.ux.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instify.android.R;
import com.instify.android.app.AppController;
import com.instify.android.models.OrderStatus;
import com.instify.android.models.TimeTableModel;
import com.instify.android.ux.MainActivity;
import com.instify.android.ux.adapters.TimeLineAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Abhish3k on 15-03-2017.
 */

public class TestPerformanceFragment extends Fragment {

    public TestPerformanceFragment() {
    }

    public static TestPerformanceFragment newInstance() {
        TestPerformanceFragment frag = new TestPerformanceFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_time_table);

        return rootView;
    }


    private class AttemptJson extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            // Call the function
            return "";
        }
    }

    private void showRefreshing() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    private void hideRefreshing() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }
}
