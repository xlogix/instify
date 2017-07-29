package com.instify.android.ux.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.NotesModel;
import com.instify.android.ux.adapters.NotesAdapter;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class NotesFragment extends Fragment {
    private RecyclerView mRVFish;
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
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        // Taking control of the menu options
        setHasOptionsMenu(true);
        // Initialize Views

        //Prevent Volley Crash on Rotate
        setRetainInstance(true);
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
                AppConfig.URL_ATTENDANCE, response -> {
            try {
                JSONObject jObj = new JSONObject(response);
                boolean error = jObj.getBoolean("error");

                // Handle UI
                hideRefreshing();

                // Check for error node in json
                if (!error) {
                    List<NotesModel> notes = new ArrayList<>();

                    JSONArray user = jObj.getJSONArray("subjects");

                    for (int i = 0; i < user.length(); i++) {
                        String name = user.getString(i);
                        JSONObject subs = jObj.getJSONObject(user.getString(i));

                        NotesModel fishData = new NotesModel();
                        fishData.fishName = subs.getString("sub-desc");
                        fishData.catName = name;
                        //fishData.sizeName = json_data.getString("registration").trim();
                        //fishData.price = json_data.getString("ID");
                        // fishData.image = "https://hashbird.com/gogrit.in/workspace/srm-api/studentImages/" + json_data.getString("registration").trim() + ".jpg";
                        notes.add(fishData);

                        //  Toast.makeText(getContext(),user.getString(i)+" - "+subs.getString("sub-desc"),Toast.LENGTH_SHORT).show()
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
        }, error -> {
            Timber.e("Network Error: " + error.getMessage());

            Toast.makeText(getContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();
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
}