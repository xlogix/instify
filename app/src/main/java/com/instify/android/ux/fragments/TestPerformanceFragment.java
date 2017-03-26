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
        ((MainActivity) getActivity()).mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeTableModel> mDataList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mWithLinePadding;

    private String userRegNo = "ra1511008020111";
    private String userPass = "dps12345";
    private final String endpoint = "http://instify.herokuapp.com/api/time-table/?regno="
            + userRegNo + "&password=" + userPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_time_table);

        mWithLinePadding = true;

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewTimeTable);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        mRecyclerView.setHasFixedSize(true);

        initView();

        TestPerformanceFragment.AttemptJson dataObj = new TestPerformanceFragment.AttemptJson();
        dataObj.doInBackground();
        return rootView;
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void initView() {
        // setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    private class AttemptJson extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            getData();
            return "";
        }
    }

    private void getData() {
        /**
         * Handle UI
         */
        showRefreshing();
        /**
         * Method to make json object request where json response is dynamic
         * */
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, endpoint, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String msg = "";
                            // Hide the Progress Dialog
                            hideRefreshing();

                            Iterator<String> it = response.keys();
                            while (it.hasNext()) {
                                String key = it.next();
                                if (response.get(key) instanceof JSONArray) {
                                    JSONArray array = response.getJSONArray(key);
                                    int size = array.length();
                                    for (int i = 0; i < size; i++) {
                                        msg += "Hour " + (i + 1) + " : " + array.get(i) + "\n";
                                        mDataList.add(new TimeTableModel(array.get(i).toString(), "2017-02-12 08:00", OrderStatus.INACTIVE));
                                    }
                                    // Notify the adapter that data has been retrieved.
                                    mTimeLineAdapter.notifyDataSetChanged();
                                } else {
                                    msg = key + ":" + response.getString(key);
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            Timber.d("JSON error : ", "Object DataSet is Incorrect");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Error: " + error.getMessage());
                // Handle UI
                hideRefreshing();
                Toast.makeText(getContext(), "Error Receiving Data", Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(req);
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
