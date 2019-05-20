package com.instify.android.ux.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.NotesModel;
import com.instify.android.ux.adapters.NotesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Abhish3k on 2/23/2016.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {
  @BindView(R.id.error_message) TextView errormessage;
  @BindView(R.id.placeholder_error) LinearLayout placeholderError;
  Unbinder unbinder;
  private RecyclerView mRecycleViewNotes;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private NotesAdapter mAdapter;
  private Context mContext;

  public NotesFragment() {
  }

  public static NotesFragment newInstance() {
    NotesFragment frag = new NotesFragment();
    Bundle args = new Bundle();
    frag.setArguments(args);
    return frag;
  }

  @Override public void onStart() {
    super.onStart();
  }

  @Override public void onStop() {
    super.onStop();
  }

  @Override public void onPause() {
    super.onPause();
  }

  @Override public void onResume() {
    super.onResume();
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    mContext = getContext();

    View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    // Taking control of the menu options
    setHasOptionsMenu(true);
    // Prevent Volley Crash on Rotate
    setRetainInstance(true);
    // Initialize Views
    mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout_notes);
    mRecycleViewNotes = rootView.findViewById(R.id.recycler_view_notes);

    getSubs();
    // Implement swipe refresh action
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        getSubs();
      }
    });

    return rootView;
  }

  private void getSubs() {
    // Handle UI
    showRefreshing();
    hidePlaceHolder();
    // Tag used to cancel the request
    String tag_string_req = "req_notes";

    StringRequest strReq =
        new StringRequest(Request.Method.POST, AppConfig.KEY_URL_GET_ATTENDANCE, response -> {
          try {
            JSONObject jObj = new JSONObject(response);
            boolean error = jObj.getBoolean("error");
            // Handle UI
            hideRefreshing();

            // Check for error node in json
            if (!error) {
              List<NotesModel> notes = new ArrayList<>();
              hidePlaceHolder();
              JSONArray user = jObj.getJSONArray("subjects");

              for (int i = 0; i < user.length(); i++) {
                String name = user.getString(i);
                JSONObject subs = jObj.getJSONObject(user.getString(i));

                NotesModel fishData = new NotesModel();
                fishData.subjectName = subs.getString("sub-desc");
                fishData.subjectCode = name;
                notes.add(fishData);
              }
              mAdapter = new NotesAdapter(mContext, notes);
              if (mAdapter.getItemCount() == 0) {
                showErrorPlaceholder("Something Wrong With ERP");
              } else {
                hidePlaceHolder();
              }
              mRecycleViewNotes.setLayoutManager(new LinearLayoutManager(mContext));
              mRecycleViewNotes.setAdapter(mAdapter);
            } else {
              // Update UI
              hideRefreshing();
              // Error in login. Get the error message
              showErrorPlaceholder(jObj.getString("error_msg"));
            }
          } catch (JSONException e) {
            // Update UI
            hideRefreshing();
            // JSON error
            e.printStackTrace();
            // Show error placeholder
            showErrorPlaceholder("Json error ");
          }
        }, error -> {
          Timber.e("Network Error: " + error.getMessage());
          showErrorPlaceholder("Network Error");
        }) {
          @Override protected Map<String, String> getParams() {
            // Posting parameters to login url
            Map<String, String> params = new HashMap<>();
            SQLiteHandler db = new SQLiteHandler(mContext);
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

  @Override public void onPrepareOptionsMenu(Menu menu) {
    menu.removeGroup(R.id.main_menu_group);
    super.onPrepareOptionsMenu(menu);
  }

  private void showRefreshing() {
    if (!mSwipeRefreshLayout.isRefreshing()) {
      mSwipeRefreshLayout.setRefreshing(true);
    }
  }

  private void hideRefreshing() {
    if (mSwipeRefreshLayout.isRefreshing()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  public void showErrorPlaceholder(String message) {
    if (placeholderError != null && errormessage != null) {
      if (placeholderError.getVisibility() != View.VISIBLE) {
        placeholderError.setVisibility(View.VISIBLE);
      }
      errormessage.setText(message);
    }
  }

  public void hidePlaceHolder() {
    if (placeholderError != null && errormessage != null) {
      if (placeholderError.getVisibility() == View.VISIBLE) {
        placeholderError.setVisibility(View.INVISIBLE);
      }
      errormessage.setText("Something Went Wrong. Try Again!");
    }
  }
}