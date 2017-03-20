package com.instify.android.ux.fragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.MyApplication;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.ux.MainActivity;
import com.instify.android.ux.adapters.ListAdapterExpandable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Abhish3k on 1/2/2017.
 */

public class AttendanceFragment extends Fragment implements OnChartGestureListener {

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
        ((MainActivity) getActivity()).mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    class Att {

        private double pre;
        private double ttl;
        double main_attendance;
        //Variables
        int count = 0, num = 1, denom = 1;
        int countp = 0, deno = 1;

        //****************************************************************************
        public double attnCalc(double present, double total) {
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
        public double predict() {

            if (main_attendance > 75) {
                while (deno <= 1000) {

                    double predicted_attandance = ((pre) / (ttl + deno)) * 100;
                    if (predicted_attandance >= 75) {
                        countp++;
                    }
                    deno++;
                }

            }
            return countp;
        }
        //****************************************************************************
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ExpandableListView expListView;
    private BarChart mChart;
    private Typeface tf;
    private String userRegNo, userPass;
    private final String endpoint = "http://instify.herokuapp.com/api/attendance/?regno="
            + userRegNo + "&password=" + userPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_attendance);
        // [LAYOUT LIST] Expand list view
        expListView = (ExpandableListView) rootView.findViewById(R.id.expListView);
        // [LAYOUT GRAPH] create a new chart object
        // mChart = (BarChart) rootView.findViewById(R.id.barChart);

        // Fetch the attendance
        AttendanceFragment.AsyncGetAttendance dataObj = new AttendanceFragment.AsyncGetAttendance();
        dataObj.doInBackground();

        // Setting the data in the chart (8 entries).. [DISABLED}

       /* List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 0f));
        entries.add(new BarEntry(1f, 0f));
        entries.add(new BarEntry(2f, 0f));
        entries.add(new BarEntry(3f, 0f));
        // gap of 2f
        entries.add(new BarEntry(5f, 0f));
        entries.add(new BarEntry(6f, 0f));
        entries.add(new BarEntry(3f, 0f));
        entries.add(new BarEntry(3f, 0f));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        BarData data = new BarData(set);
        mChart.setData(data);
        mChart.getDescription().setEnabled(false);
        mChart.animateXY(2000, 2000);
        //mChart.setOnChartGestureListener(this);
        mChart.setDrawBarShadow(true);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.invalidate();*/

        ((MainActivity) getActivity()).mSharedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
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
                AppConfig.URL_ATTANDENCE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Handle UI
                    hideRefreshing();

                    // Check for error node in json
                    if (!error) {
                        ListAdapterExpandable expListAdapter;

                        // declare array List for all headers in list
                        ArrayList<String> headersArrayList = new ArrayList<>();

                        // Declare Hash map for all headers and their corresponding values
                        HashMap<String, ArrayList<String>> childArrayList = new HashMap<>();

                        // expListView = (ExpandableListView)findViewById(R.id.expListView);
                        JSONArray user = jObj.getJSONArray("subjects");

                        int i;
                        double ar[], br[];
                        ar = new double[100];
                        br = new double[100];

                        for (i = 0; i < user.length(); i++) {
                            // Create an object of Att class
                            Att obj = new Att();

                            String name = user.getString(i);
                            JSONObject subs = jObj.getJSONObject(user.getString(i));

                            ArrayList<String> daysOfWeekArrayList = new ArrayList<>();
                            headersArrayList.add(name + "-" + subs.getString("sub-desc") + " " + subs.getString("avg-attd") + "%");

                            daysOfWeekArrayList.add("MAX-HOURS: " + subs.getString("max-hrs"));
                            daysOfWeekArrayList.add("ATTENDED-HOURS: " + subs.getString("attd-hrs"));
                            daysOfWeekArrayList.add("ABSENT-HOURS: " + subs.getString("abs-hrs"));
                            daysOfWeekArrayList.add("PERCENTAGE: " + subs.getString("avg-attd") + "%");
                            ar[i] = Double.parseDouble(subs.getString("attd-hrs"));
                            br[i] = Double.parseDouble(subs.getString("max-hrs"));
                            double tempa = ar[i];
                            double tempb = br[i];

                            double resultA = obj.attnCalc(tempa, tempb);
                            double resultB = obj.predict();
                            daysOfWeekArrayList.add("TOT CLASSES FOR >= 75% :  " + resultA);
                            daysOfWeekArrayList.add("May Take Leave For Next: " + resultB + " consecutive classes *if taken ");

                            childArrayList.put(name + "-" + subs.getString("sub-desc") + " " + subs.getString("avg-attd") + "%", daysOfWeekArrayList);
                        }

                        expListAdapter = new ListAdapterExpandable(getContext(), headersArrayList, childArrayList);

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
                Toast.makeText(getContext(),
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

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private String[] mLabels = new String[]{"Company A", "Company B", "Company C", "Company D", "Company E", "Company F"};

    private String getLabel(int i) {
        return mLabels[i];
    }

    protected BarData generateBarData(int dataSets, float range, int count) {

        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();

        for (int i = 0; i < dataSets; i++) {

            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

//            entries = FileUtils.loadEntriesFromAssets(getActivity().getAssets(), "stacked_bars.txt");

            for (int j = 0; j < count; j++) {
                entries.add(new BarEntry(j, (float) (Math.random() * range) + range / 4));
            }

            BarDataSet ds = new BarDataSet(entries, getLabel(i));
            ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
            sets.add(ds);
        }

        BarData d = new BarData(sets);
        d.setValueTypeface(tf);
        return d;
    }

    class AttemptJson extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            updateChart();
            return "";
        }
    }

    private void updateChart() {
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
                            Iterator<String> it = response.keys();
                            while (it.hasNext()) {
                                String key = it.next();
                                if (response.get(key) instanceof JSONArray) {
                                    JSONArray array = response.getJSONArray(key);
                                    int size = array.length();
                                    for (int i = 0; i < size; i++) {

                                    }
                                } else {
                                    System.out.println(key + ":" + response.getString(key));
                                }
                            }
                        } catch (JSONException e) {
                            Log.d("debug", "Object DataSet is incorrect");
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
        MyApplication.getInstance().addToRequestQueue(req);
    }

    private void showRefreshing() {
        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);
    }

    private void hideRefreshing() {
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END");
        mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        mChart.saveToGallery("myAttendance" + System.currentTimeMillis(), 85);
        Log.i("DoubleTap", "Saved the graph successfully...");
        Toast.makeText(getActivity(), "Saved the graph successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }
}