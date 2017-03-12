package com.instify.android.ux.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instify.android.R;
import com.instify.android.app.MyApplication;
import com.instify.android.ux.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import timber.log.Timber;

/**
 * Created by Abhish3k on 21/02/2016.
 * Thanks :)
 */

public class TimeTableFragment extends Fragment {

    public TimeTableFragment() {
    }

    public static TimeTableFragment newInstance() {
        TimeTableFragment frag = new TimeTableFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    TextView ttData;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String userRegNo = "ra1511008020111";
    //    private String userPass = MyApplication.getInstance().getPrefManager().getUserPassword();
    private String userPass = "dps12345";
    private final String endpoint = "http://instify.herokuapp.com/api/time-table/?regno="
            + userRegNo + "&password=" + userPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
        ttData = (TextView) rootView.findViewById(R.id.timeTableData);
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_time_table);

        AttemptJson dataObj = new AttemptJson();
        dataObj.doInBackground();
        return rootView;
    }

    class AttemptJson extends AsyncTask<String, String, String> {

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
                            Iterator<String> it = response.keys();
                            while (it.hasNext()) {
                                String key = it.next();
                                if (response.get(key) instanceof JSONArray) {
                                    JSONArray array = response.getJSONArray(key);
                                    int size = array.length();
                                    for (int i = 0; i < size; i++) {
                                        msg += "Hour " + (i + 1) + " : " + array.get(i) + "\n";
                                    }
                                    ttData.setText(msg);
                                } else {
                                    msg = key + ":" + response.getString(key);
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
//                                    System.out.println(key + ":" + response.getString(key));
                                }

                                hideRefreshing();
                            }
                        } catch (JSONException e) {
                            Timber.d("JSON error : ", "Object DataSet is incorrect");
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
}


