package com.instify.android.ux.fragments;

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
import com.android.volley.toolbox.StringRequest;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.notes;
import com.instify.android.ux.adapters.NotesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class NotesFragment extends Fragment {

    RecyclerView mRVFish;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesAdapter mAdapter;

    public NotesFragment() {
    }

    public static NotesFragment newInstance() {
        NotesFragment frag = new NotesFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                rootView.findViewById(R.id.swipe_refresh_layout_notes);

        mRVFish = (RecyclerView) rootView.findViewById(R.id.recycler_view_notes);

        getSubs();
        // Implement swipe refresh action
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSubs();
            }
        });
        return rootView;
    }

    private void getSubs() {

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

                        List<notes> notes = new ArrayList<>();
                        // Declare Hash map for all headers and their corresponding values
                        HashMap<String, ArrayList<String>> childArrayList = new HashMap<>();

                        // expListView = (ExpandableListView)findViewById(R.id.expListView);
                        JSONArray user = jObj.getJSONArray("subjects");

                        int i;
//                        mAdapter = new NotesAdapter(notes);
//                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//                        recyclerView.setLayoutManager(mLayoutManager);
//                        recyclerView.setItemAnimator(new DefaultItemAnimator());


                        for (i = 0; i < user.length(); i++) {
                            String name = user.getString(i);
                            JSONObject subs = jObj.getJSONObject(user.getString(i));


//                            notes movie = notes(subs.getString("sub-desc"));
//                            notes.add(movie);
                            //   notes obj = new notes(subs.getString("sub-desc"));


                            notes fishData = new notes();
                            fishData.fishName = subs.getString("sub-desc");
                            fishData.catName = name;
                            //fishData.sizeName = json_data.getString("registration").trim();
                            //fishData.price = json_data.getString("ID");
                            // fishData.image = "https://hashbird.com/gogrit.in/workspace/srm-api/studentImages/" + json_data.getString("registration").trim() + ".jpg";
                            notes.add(fishData);

                            //  Toast.makeText(getContext(),user.getString(i)+" - "+subs.getString("sub-desc"),Toast.LENGTH_SHORT).show();
                            // Create an object of Att class
                        }

                        mAdapter = new NotesAdapter(getContext(), notes);
                        mRVFish.setAdapter(mAdapter);
                        mRVFish.setLayoutManager(new LinearLayoutManager(getContext()));

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

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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