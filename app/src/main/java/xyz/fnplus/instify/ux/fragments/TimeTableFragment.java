package xyz.fnplus.instify.ux.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;
import xyz.fnplus.instify.R;
import xyz.fnplus.instify.app.AppConfig;
import xyz.fnplus.instify.app.AppController;
import xyz.fnplus.instify.helpers.SQLiteHandler;
import xyz.fnplus.instify.models.OrderStatus;
import xyz.fnplus.instify.models.TimeTableModel;
import xyz.fnplus.instify.ux.adapters.ListExpandableAdapter;
import xyz.fnplus.instify.ux.adapters.TimeLineAdapter;

/**
 * Created by Abhish3k on 21/02/2016.
 * Thanks :)
 */

public class TimeTableFragment extends Fragment {
    @BindView(R.id.calendarbutton)
    ImageButton mCalendarbutton;
    @BindView(R.id.currentdate)
    TextView mCurrentdate;
    Unbinder unbinder;
    String userRegNo;
    String userPass;
    private String TAG = TimeTableFragment.class.getSimpleName();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private ExpandableListView expListView;
    private List<TimeTableModel> mDataList = new ArrayList<>();
    private boolean mWithLinePadding;

    public TimeTableFragment() {
    }

    public static TimeTableFragment newInstance() {
        TimeTableFragment frag = new TimeTableFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @OnClick(R.id.calendarbutton)
    void showcalendar() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(), null, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        // Taking control of the menu options
        unbinder = ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        //Prevent Volley Crash on Rotate
        setRetainInstance(true);
        // Initialize SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_time_table);
        // Set color scheme
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red_primary, R.color.black, R.color.google_blue_900);

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());

        mCurrentdate.setText(sdf.format(date));

        /*// Declare elements of TimeLine view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewTimeTable);
        mRecyclerView.setLayoutManager(getLinearLayoutManager());
        // Setting the adapter
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mWithLinePadding);
        mRecyclerView.setAdapter(mTimeLineAdapter);*/

        // Declare elements of another view
        expListView = (ExpandableListView) rootView.findViewById(R.id.expListView);

        /*// Declare FAB
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clicked) {
                    mRecyclerView.setVisibility(View.GONE);
                    expListView.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_menu_camera);
                    clicked = false;
                } else {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    expListView.setVisibility(View.GONE);
                    clicked = true;
                }
            }
        });*/

        // Handle UI
        showRefreshing();

        // mWithLinePadding = true;

        // Call data fetcher
        final AttemptJson dataObj = new AttemptJson();
        dataObj.doInBackground();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            showRefreshing();
            dataObj.doInBackground();
        });


        return rootView;
    }

    private LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getData() {

        // Fetch details from the database
        SQLiteHandler db = new SQLiteHandler(getContext());
        userRegNo = db.getUserDetails().getRegno();
        userPass = db.getUserDetails().getToken();

        // Set the end point with the acquired credentials
        String endpoint = "http://instify.herokuapp.com/api/time-table/?regno="
                + userRegNo + "&password=" + userPass;

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

        final String[] mTimeValues = new String[]{date + " 08:00", date + " 09:10", date + " 10:05", date + " 11:00", date + " 12:55", date + " 13:50", date + " 14:45"};

        /*
         * Method to make json object request where json response is dynamic
         * */
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, endpoint, null,
                response -> {
                    try {
                        // Hide the Progress Dialog
                        hideRefreshing();
                        // Clear the list
                        mDataList.clear();
                        // Handle response
                        String msg = "";
                        Iterator<String> it = response.keys();
                        while (it.hasNext()) {
                            String key = it.next();
                            if (response.get(key) instanceof JSONArray) {
                                JSONArray array = response.getJSONArray(key);
                                int size = array.length();
                                // [TRY] Fix the card overlap issue
                                mTimeLineAdapter.notifyItemRangeRemoved(0, size);
                                for (int i = 0; i < size; i++) {
                                    msg += "Hour " + (i + 1) + " : " + array.get(i) + "\n";
                                    mDataList.add(new TimeTableModel(array.get(i).toString(), mTimeValues[i], OrderStatus.INACTIVE));
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
                }, error -> {
            Timber.e("Error: " + error.getMessage());
            // Handle UI
            hideRefreshing();
            Toast.makeText(getContext(), "Error Receiving Data", Toast.LENGTH_LONG).show();
        });

        int socketTimeout = 10000;  // 10 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(req);
    }

    private void getTimeTable() {
        // Tag used to cancel the request
        String tag_string_req = "req_timetable";

        // Handle UI
        showRefreshing();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GETTT, response -> {
            Timber.d("Login Response: " + response);
            // Handle UI
            hideRefreshing();

            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // Check for error node in json
                if (!error) {

                    ListExpandableAdapter adapter;

                    // declare array List for all headers in list
                    ArrayList<String> headersArrayList = new ArrayList<>();

                    // Declare Hash map for all headers and their corresponding values
                    HashMap<String, ArrayList<String>> childArrayList = new HashMap<>();

                    JSONArray monday = jObj.getJSONArray("monday");
                    Integer i;

                    // For Monday
                    ArrayList<String> mondayList = new ArrayList<>();
                    headersArrayList.add("MONDAY");
                    for (i = 0; i < monday.length(); i++) {
                        String name = monday.getString(i);
                        mondayList.add("HOUR " + (i + 1) + " - " + name);
                    }
                    childArrayList.put("MONDAY", mondayList);

                    // For Tuesday
                    JSONArray tuesday = jObj.getJSONArray("tuesday");
                    headersArrayList.add("TUESDAY");
                    ArrayList<String> tuesdayList = new ArrayList<>();
                    for (i = 0; i < tuesday.length(); i++) {
                        String name = tuesday.getString(i);
                        tuesdayList.add("HOUR " + (i + 1) + " - " + name);
                    }
                    childArrayList.put("TUESDAY", tuesdayList);

                    // For Wednesday
                    JSONArray wednesday = jObj.getJSONArray("wednesday");
                    headersArrayList.add("WEDNESDAY");
                    ArrayList<String> wednesdayList = new ArrayList<>();
                    for (i = 0; i < wednesday.length(); i++) {
                        String name = wednesday.getString(i);
                        wednesdayList.add("HOUR " + (i + 1) + " - " + name);
                    }
                    childArrayList.put("WEDNESDAY", wednesdayList);

                    // For Thursday
                    JSONArray thursday = jObj.getJSONArray("thursday");
                    headersArrayList.add("THURSDAY");
                    ArrayList<String> thursdayList = new ArrayList<>();
                    for (i = 0; i < thursday.length(); i++) {
                        String name = thursday.getString(i);
                        thursdayList.add("HOUR " + (i + 1) + " - " + name);
                    }
                    childArrayList.put("THURSDAY", thursdayList);

                    // For Friday
                    JSONArray friday = jObj.getJSONArray("friday");
                    headersArrayList.add("FRIDAY");
                    ArrayList<String> fridayList = new ArrayList<>();
                    for (i = 0; i < friday.length(); i++) {
                        String name = friday.getString(i);
                        fridayList.add("HOUR " + (i + 1) + " - " + name);
                    }
                    childArrayList.put("FRIDAY", fridayList);


                    adapter = new ListExpandableAdapter(getContext(), headersArrayList, childArrayList);

                    expListView.setAdapter(adapter);

                    expListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> false);

                    expListView.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
                    expListView.setOnGroupCollapseListener(groupPosition -> {

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
                    //Handle UI
                    hideRefreshing();
                    // Error in login. Get the error message
                    String errorMsg = jObj.getString("error_msg");
                    Toast.makeText(getContext(),
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // Handle UI
                hideRefreshing();
                // JSON error
                e.printStackTrace();
                Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }, error -> {
            Timber.e("Login Error: " + error.getMessage());
            // Handle UI
            hideRefreshing();
            Toast.makeText(getActivity(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                SQLiteHandler db = new SQLiteHandler(getContext());
                String unm = db.getUserDetails().getRegno();
                String pass = db.getUserDetails().getToken();

                params.put("regno", unm);
                params.put("pass", pass);

                return params;
            }
        };
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

    private class AttemptJson extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            // Get data for both the views
            getTimeTable();
            return "";
        }
    }
}


