package com.instify.android.ux.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.ux.adapters.ListExpandableAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Abhish3k on 1/2/2017.
 */

public class AttendanceFragment extends Fragment {

    public AttendanceFragment() {
    }

    public static AttendanceFragment newInstance() {
        AttendanceFragment frag = new AttendanceFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ExpandableListView expListView;
    private TextView updatedAt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);
        // Taking control of the menu options
        setHasOptionsMenu(true);
        // Initialize SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_attendance);
        // [LAYOUT LIST] Expand list view
        expListView = (ExpandableListView) rootView.findViewById(R.id.expListView);
        updatedAt = (TextView) rootView.findViewById(R.id.textDate);

        // Fetch the attendance
        AttendanceFragment.AsyncGetAttendance dataObj = new AttendanceFragment.AsyncGetAttendance();
        dataObj.doInBackground();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAttendance();
            }
        });

        return rootView;
    }

    private class AsyncGetAttendance extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            getAttendance();
            return null;
        }
    }

    private void getAttendance() {

        // Handle UI
        showRefreshing();

        // Tag used to cancel the request
        String tag_string_req = "req_attendance";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ATTENDANCE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Handle UI
                    hideRefreshing();

                    // Check for error node in json
                    if (!error) {
                        ListExpandableAdapter expListAdapter;

                        // declare array List for all headers in list
                        ArrayList<String> headersArrayList = new ArrayList<>();

                        // Declare Hash map for all headers and their corresponding values
                        HashMap<String, ArrayList<String>> childArrayList = new HashMap<>();

                        JSONArray user = jObj.getJSONArray("subjects");
                        JSONObject updated = jObj.getJSONObject("updated");

                        int i;
                        double ar[], br[];
                        ar = new double[20];
                        br = new double[20];

                        // Set the text
                        updatedAt.setText(updated.toString());

                        for (i = 0; i < user.length(); i++) {
                            // Create an object of Att class
                            Att obj = new Att();

                            String name = user.getString(i);
                            JSONObject subs = jObj.getJSONObject(user.getString(i));

                            ArrayList<String> daysOfWeekArrayList = new ArrayList<>();
                            headersArrayList.add(name + "-" + subs.getString("sub-desc") + " (" + subs.getString("avg-attd") + "%)");

                            daysOfWeekArrayList.add("MAX-HOURS: " + subs.getString("max-hrs"));
                            daysOfWeekArrayList.add("ATTENDED-HOURS: " + subs.getString("attd-hrs"));
                            daysOfWeekArrayList.add("ABSENT-HOURS: " + subs.getString("abs-hrs"));
                            daysOfWeekArrayList.add("OD/ML PERCENTAGE: " + subs.getString("od-hrs"));
                            daysOfWeekArrayList.add("PERCENTAGE: " + subs.getString("avg-attd") + "%");

                            ar[i] = Double.parseDouble(subs.getString("attd-hrs"));
                            br[i] = Double.parseDouble(subs.getString("max-hrs"));
                            // Variable declaration
                            double tempa = ar[i];
                            double tempb = br[i];

                            double resultA = obj.attnCalc(tempa, tempb);
                            double resultB = obj.predict();
                            daysOfWeekArrayList.add("TOTAL HOUR(S) FOR >= 75% :  " + resultA);
                            daysOfWeekArrayList.add("MAY TAKE LEAVE FOR NEXT: " + resultB + " CONSECUTIVE HOUR(S)");

                            childArrayList.put(name + "-" + subs.getString("sub-desc") + " " + subs.getString("avg-attd") + "%", daysOfWeekArrayList);
                        }

                        expListAdapter = new ListExpandableAdapter(getContext(), headersArrayList, childArrayList);

                        expListView.setAdapter(expListAdapter);

                        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v,
                                                        int groupPosition, int childPosition, long id) {
                                return false;
                            }
                        });

                        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v,
                                                        int groupPosition, long id) {

                                return false;
                            }
                        });
                        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                            @Override
                            public void onGroupCollapse(int groupPosition) {

                            }
                        });

                        final ExpandableListView finalExpListView = expListView;
                        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                            int previousGroup = -1;

                            @Override
                            public void onGroupExpand(int groupPosition) {

                                if (groupPosition != previousGroup)
                                    finalExpListView.collapseGroup(previousGroup);
                                previousGroup = groupPosition;
                            }
                        });

                    } else {
                        // Update UI
                        hideRefreshing();
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // Update UI
                    hideRefreshing();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Network Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                SQLiteHandler db = new SQLiteHandler(getContext());
                String regNo = db.getUserDetails().get("token");
                String pass = db.getUserDetails().get("created_at");

                params.put("regno", regNo);
                params.put("pass", pass);

                return params;
            }
        };
        // Set a maximum timeout if there are network problems
        int socketTimeout = 10000;  // 10 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.removeGroup(R.id.main_menu_group);
        super.onPrepareOptionsMenu(menu);
    }

    private void showRefreshing() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    private void hideRefreshing() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    class Att {

        private double pre;
        private double ttl;
        double main_attendance;
        //Variables
        int count = 0, num = 1, denom = 1;
        int countp = 0, deno = 1;

        //****************************************************************************
        private double attnCalc(double present, double total) {
            pre = present;
            ttl = total;
            main_attendance = pre / ttl * 100;

            while (true) {

                double current_attendance = ((present + num) / (total + denom)) * 100;
                num++;
                denom++;

                if (current_attendance > 75) {
                    break;
                }
                count++;
            }
            return count;
        }

        //****************************************************************************
        private double predict() {

            if (main_attendance > 75) {
                while (deno <= 1000) {

                    double predicted_attendance = ((pre) / (ttl + deno)) * 100;
                    if (predicted_attendance >= 75) {
                        countp++;
                    }
                    deno++;
                }
            }
            return countp;
        }
        //****************************************************************************
    }
}