package com.instify.android.ux.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.ux.adapters.ListExpandableAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Abhish3k on 21/02/2016.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeTableFragment extends Fragment {
  @BindView(R.id.calendarButton) ImageButton mCalendarButton;
  @BindView(R.id.currentdate) TextView mCurrentDate;
  Unbinder unbinder;
  @BindView(R.id.error_message) TextView errorMessage;
  @BindView(R.id.placeholder_error) LinearLayout placeholderError;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private ExpandableListView expListView;
  private Context mContext;

  public TimeTableFragment() {
  }

  public static TimeTableFragment newInstance() {
    TimeTableFragment frag = new TimeTableFragment();
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
    // Get Context
    mContext = getContext();
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    // Taking control of the menu options
    setHasOptionsMenu(true);
    // Prevent Volley Crash on Rotate
    setRetainInstance(true);
    // Initialize SwipeRefreshLayout
    mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout_time_table);
    // Set color scheme
    mSwipeRefreshLayout.setColorSchemeResources(R.color.red_primary, R.color.black,
        R.color.google_blue_900);

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
    mCurrentDate.setText(sdf.format(date));

    // Declare elements of another view
    expListView = rootView.findViewById(R.id.expListView);

    // Handle UI
    showRefreshing();

    // Call data fetcher
    final AttemptJson dataObj = new AttemptJson(this);
    dataObj.doInBackground();

    mSwipeRefreshLayout.setOnRefreshListener(() -> {
      showRefreshing();
      dataObj.doInBackground();
    });

    return rootView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  void getTimeTable() {
    // Tag used to cancel the request
    String tag_string_req = "req_timetable";

    // Handle UI
    showRefreshing();

    StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.KEY_URL_GET_TT, response -> {
      Timber.d("Login Response: " + response);
      // Handle UI
      hideRefreshing();

      try {
        JSONObject jObj = new JSONObject(response);
        boolean error = jObj.getBoolean("error");

        // Check for error node in json
        if (!error) {
          ListExpandableAdapter adapter;
          // Declare array List for all headers in list
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

          adapter = new ListExpandableAdapter(mContext, headersArrayList, childArrayList);
          if (adapter.getGroupCount() == 0) {
            showErrorPlaceholder("Erp is not Responding");
          } else {
            hidePlaceHolder();
          }
          expListView.setAdapter(adapter);

          expListView.setOnChildClickListener(
              (parent, v, groupPosition, childPosition, id) -> false);

          expListView.setOnGroupClickListener((parent, v, groupPosition, id) -> false);
          expListView.setOnGroupCollapseListener(groupPosition -> {

          });

          final ExpandableListView finalExpListView = expListView;
          expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override public void onGroupExpand(int groupPosition) {

              if (groupPosition != previousGroup) {
                finalExpListView.collapseGroup(previousGroup);
              }
              previousGroup = groupPosition;
            }
          });
        } else {
          //Handle UI
          hideRefreshing();
          // Error in login. Get the error message

          String errorMsg = jObj.getString("error_msg");
          showErrorPlaceholder(errorMsg);
        }
      } catch (JSONException error) {
        // Handle UI
        hideRefreshing();
        // JSON error
        showErrorPlaceholder("Json error, please try again");
        Timber.e(error.getMessage(), "JSON error : ");
      }
    }, error -> {
      Timber.e("Login Error: " + error.getMessage());
      // Handle UI
      hideRefreshing();
      showErrorPlaceholder("Something went wrong, please try again");
      // Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
    }) {

      @Override protected Map<String, String> getParams() {
        // Posting parameters to login url
        Map<String, String> params = new HashMap<>();
        SQLiteHandler db = new SQLiteHandler(mContext);
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

  public void showErrorPlaceholder(String message) {
    if (placeholderError != null && errorMessage != null) {
      if (placeholderError.getVisibility() != View.VISIBLE) {
        placeholderError.setVisibility(View.VISIBLE);
      }
      errorMessage.setText(message);
    }
  }

  public void hidePlaceHolder() {
    if (placeholderError != null && errorMessage != null) {
      if (placeholderError.getVisibility() == View.VISIBLE) {
        placeholderError.setVisibility(View.INVISIBLE);
      }
      errorMessage.setText("Something went wrong. Try again!");
    }
  }

  private static class AttemptJson extends AsyncTask<String, String, String> {
    private WeakReference<TimeTableFragment> FragReference;

    // only retain a weak reference to the fragment
    AttemptJson(TimeTableFragment context) {
      FragReference = new WeakReference<>(context);
    }

    @Override protected String doInBackground(String... strings) {
      // Get data for both the views
      FragReference.get().getTimeTable();
      return "";
    }
  }
}