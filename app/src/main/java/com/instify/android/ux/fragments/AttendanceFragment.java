package com.instify.android.ux.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Abhish3k on 1/2/2017.
 */

public class AttendanceFragment extends Fragment {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    //    private ExpandableListView expListView;
    private CardView attdCards;
    private TextView updatedAt;
    private SimpleStringRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);
        // Taking control of the menu options
        setHasOptionsMenu(true);
        // Initialize SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_attendance);

        attdCards = (CardView) rootView.findViewById(R.id.attdCard);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.attdRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                    Boolean error = jObj.getBoolean("error");

                    // Handle UI
                    hideRefreshing();

                    mAdapter = new SimpleStringRecyclerViewAdapter(getContext(), jObj);
                    recyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    // Update UI
                    hideRefreshing();
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Check your network connection", Toast.LENGTH_LONG).show();
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
                String regNo = db.getUserDetails().getRegno();
                String pass = db.getUserDetails().getToken();

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

    private class AsyncGetAttendance extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            getAttendance();
            return null;
        }
    }

    class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<AttendanceFragment.SimpleStringRecyclerViewAdapter.ViewHolder> {
        private Context mContext;
        private JSONObject attdObj;
        private String subjectCode;

        // Constructor
        private SimpleStringRecyclerViewAdapter(Context context, JSONObject attdObj) {
            mContext = context;
            this.attdObj = attdObj;
        }

        @Override
        public AttendanceFragment.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_attendance, parent, false);
            return new AttendanceFragment.SimpleStringRecyclerViewAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AttendanceFragment.SimpleStringRecyclerViewAdapter.ViewHolder holder, int position) {

            try {
                Att attObj = new Att();
                subjectCode = attdObj.getJSONArray("subjects").getString(position);
                holder.attdExtra.setVisibility(View.GONE);
                holder.mTextViewTitle.setText(attdObj.getJSONObject(subjectCode).getString("sub-desc"));
                holder.mTextViewPercent.setText(attdObj.getJSONObject(subjectCode).getString("avg-attd") + "%");
                holder.attdMax.setText("Maximum hours: " + attdObj.getJSONObject(subjectCode).getString("max-hrs"));
                holder.attdAttend.setText("Attended hours: " + attdObj.getJSONObject(subjectCode).getString("attd-hrs"));
                holder.attdAbsent.setText("Absent hours: " + attdObj.getJSONObject(subjectCode).getString("abs-hrs"));
                holder.attdOd.setText("OD/ML: " + attdObj.getJSONObject(subjectCode).getString("od-hrs"));
                holder.attdMin.setText("Hour(s) required for min. attendance: " + (int) attObj.attnCalc(
                        Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("attd-hrs")),
                        Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("max-hrs"))
                ));
                holder.attdBuffer.setText("Hour(s) available to take leave : " + (int) attObj.getBuffer(
                        Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("attd-hrs")),
                        Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("max-hrs"))
                ));
                holder.attdCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.toggle) {
                            holder.attdExtra.setVisibility(View.GONE);
                            holder.attdExpand.setImageResource(R.drawable.ic_expand_more);
                            holder.toggle = false;
                        } else {
                            holder.attdExtra.setVisibility(View.VISIBLE);
                            holder.attdExpand.setImageResource(R.drawable.ic_expand_less);
                            holder.toggle = true;
                        }
                    }
                });

                if (Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("avg-attd")) < 76.0) {
                    holder.mTextViewPercent.setTextColor(getResources().getColor(R.color.red_accent));
                } else if (Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("avg-attd")) >= 90.0) {
                    holder.mTextViewPercent.setTextColor(getResources().getColor(R.color.green_accent));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            try {
                return attdObj.getJSONArray("subjects").length();
            } catch (JSONException e) {
                Toast.makeText(mContext, "Problem with json", Toast.LENGTH_SHORT).show();
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final AppCompatTextView mTextViewTitle, mTextViewPercent, attdMax, attdAbsent,
                    attdAttend, attdOd, attdBuffer, attdMin;
            private final RelativeLayout attdExtra;
            private final CardView attdCard;
            private final ImageView attdExpand;
            private Boolean toggle;

            private ViewHolder(View view) {
                super(view);
                mView = view;
                attdCard = (CardView) view.findViewById(R.id.attdCard);
                mTextViewTitle = (AppCompatTextView) view.findViewById(R.id.attdSubjectName);
                mTextViewPercent = (AppCompatTextView) view.findViewById(R.id.attdSubjectPercent);
                attdMax = (AppCompatTextView) view.findViewById(R.id.attdMaxHours);
                attdAbsent = (AppCompatTextView) view.findViewById(R.id.attdAbsentHours);
                attdAttend = (AppCompatTextView) view.findViewById(R.id.attdAttendHours);
                attdOd = (AppCompatTextView) view.findViewById(R.id.attdOdPercent);
                attdBuffer = (AppCompatTextView) view.findViewById(R.id.attdBuffer);
                attdMin = (AppCompatTextView) view.findViewById(R.id.attdMin);
                attdExtra = (RelativeLayout) view.findViewById(R.id.attdExtra);
                attdExpand = (ImageView) view.findViewById(R.id.attdExpand);
                toggle = false;
            }
        }
    }

    private class Att {

        double main_attendance;
        //Variables
        int count = 0, num = 1, denom = 1;
        int countp = 0, deno = 1;
        private double pre;
        private double ttl;

        private Att() {
        }

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
        private double getBuffer(double present, double total) {
            this.attnCalc(present, total);
            return this.predict();
        }
    }
}