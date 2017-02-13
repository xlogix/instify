package com.instify.android.ux.fragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.instify.android.R;
import com.instify.android.app.MyApplication;
import com.instify.android.ux.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private SwipeRefreshLayout mSwipeRefreshLayout;
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
        // create a new chart object
        mChart = (BarChart) rootView.findViewById(R.id.barChart);

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AttemptJson().execute();
            }
        }, 6000);*/

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 0f));
        entries.add(new BarEntry(1f, 0f));
        entries.add(new BarEntry(2f, 0f));
        entries.add(new BarEntry(3f, 0f));
        // gap of 2f
        entries.add(new BarEntry(5f, 0f));
        entries.add(new BarEntry(6f, 0f));

        BarDataSet set = new BarDataSet(entries, "BarDataSet");
        BarData data = new BarData(set);
        mChart.setData(data);
        mChart.getDescription().setEnabled(false);
        mChart.animateXY(2000, 2000);
        //mChart.setOnChartGestureListener(this);
        mChart.setDrawBarShadow(true);
        mChart.setFitBars(true); // make the x-axis fit exactly all bars
        mChart.invalidate();

        return rootView;
    }

    /*private void askPassword() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter your password");

                // Set up the input
                final EditText password = new EditText(getActivity());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                builder.setView(password);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPassword = password.getText().toString();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }*/

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