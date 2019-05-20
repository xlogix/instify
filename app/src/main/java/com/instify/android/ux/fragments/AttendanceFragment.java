package com.instify.android.ux.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Abhish3k on 1/2/2017.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendanceFragment extends Fragment {

  @BindView(R.id.placeholder_error) LinearLayout placeholderError;
  Unbinder unbinder;
  @BindView(R.id.error_message) TextView errorMessage;
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private CardView attdCards;
  private SimpleStringRecyclerViewAdapter mAdapter;
  private RecyclerView recyclerView;
  private Context mContext;

  public AttendanceFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment ExperiencesFragment.
   */
  public static AttendanceFragment newInstance() {
    AttendanceFragment frag = new AttendanceFragment();
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

  /**
   * Called when leaving the fragment
   */
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
    View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    // Taking control of the menu options
    setHasOptionsMenu(true);
    // Initialize SwipeRefreshLayout
    mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout_attendance);
    // Prevent Volley Crash on Rotate
    setRetainInstance(true);

    attdCards = rootView.findViewById(R.id.attdCard);
    recyclerView = rootView.findViewById(R.id.attdRecycle);
    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
    recyclerView.setItemAnimator(new DefaultItemAnimator());

    // Fetch the attendance
    AsyncGetAttendance dataObj = new AsyncGetAttendance();
    dataObj.doInBackground();

    mSwipeRefreshLayout.setOnRefreshListener(this::getAttendance);

    return rootView;
  }

  private void getAttendance() {
    // Handle UI
    showRefreshing();
    // Tag used to cancel the request
    String tag_string_req = "req_attendance";

    StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.KEY_URL_GET_ATTENDANCE,
        new Response.Listener<String>() {
          @Override public void onResponse(String response) {
            try {
              hidePlaceHolder();
              JSONObject jObj = new JSONObject(response);
              // Boolean error = jObj.getBoolean("error");
              // Handle UI
              hideRefreshing();
              // Hide Placeholder
              hidePlaceHolder();
              // Set Adapter
              mAdapter = new SimpleStringRecyclerViewAdapter(mContext, jObj);
              recyclerView.setAdapter(mAdapter);
            } catch (JSONException e) {
              // Update UI
              hideRefreshing();
              // JSON error
              Timber.e("Error: The ERP is misbehaving!");
              showErrorPlaceholder("We are sorry. The ERP is misbehaving");
            }
          }
        }, error -> {
      // Empty content in view
      recyclerView.setAdapter(null);
      // Log the response
      Timber.e("Network Error: " + error.getMessage());
      // Show the default placeholder
      showErrorPlaceholder("It's your internet :(");
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

    // Set a maximum timeout if there are network problems
    int socketTimeout = 10000;  // 10 seconds - change to what you want
    RetryPolicy policy =
        new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    strReq.setRetryPolicy(policy);

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
      errorMessage.setText("Something Went Wrong. Try Again!");
    }
  }

  private class AsyncGetAttendance extends AsyncTask<String, String, String> {

    @Override protected String doInBackground(String... strings) {
      getAttendance();
      return null;
    }
  }

  class SimpleStringRecyclerViewAdapter
      extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private JSONObject attdObj;
    private String subjectCode;

    // Constructor
    private SimpleStringRecyclerViewAdapter(Context context, JSONObject attdObj) {
      mContext = context;
      this.attdObj = attdObj;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.card_view_attendance, parent, false);
      return new ViewHolder(itemView);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {

      try {
        Att attObj = new Att();
        subjectCode = attdObj.getJSONArray("subjects").getString(position);
        holder.attdExtra.setVisibility(View.GONE);
        holder.mTextViewTitle.setText(attdObj.getJSONObject(subjectCode).getString("sub-desc"));
        holder.mTextViewPercent.setText(
            attdObj.getJSONObject(subjectCode).getString("avg-attd") + "%");

        holder.attdMax.setText(
            "Maximum hours: " + attdObj.getJSONObject(subjectCode).getString("max-hrs"));

        holder.attdAttend.setText(
            "Attended hours: " + attdObj.getJSONObject(subjectCode).getString("attd-hrs"));

        holder.attdAbsent.setText(
            "Absent hours: " + attdObj.getJSONObject(subjectCode).getString("abs-hrs"));

        holder.attdOd.setText("OD/ML: " + attdObj.getJSONObject(subjectCode).getString("od-hrs"));

        holder.attdMin.setText("Hour(s) required for min. attendance: " + (int) attObj.attnCalc(
            Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("attd-hrs")),
            Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("max-hrs"))));

        holder.attdBuffer.setText("Hour(s) available to take leave : " + (int) attObj.getBuffer(
            Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("attd-hrs")),
            Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("max-hrs"))));

        holder.attdCard.setOnClickListener(v -> {
          if (holder.toggle) {
            holder.attdExtra.setVisibility(View.GONE);
            holder.attdExpand.setImageResource(R.drawable.ic_expand_more);
            holder.toggle = false;
          } else {
            holder.attdExtra.setVisibility(View.VISIBLE);
            holder.attdExpand.setImageResource(R.drawable.ic_expand_less);
            holder.toggle = true;
          }
        });

        if (Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("avg-attd")) < 76.0) {
          holder.mTextViewPercent.setTextColor(getResources().getColor(R.color.red_accent));
        } else if (Double.parseDouble(attdObj.getJSONObject(subjectCode).getString("avg-attd"))
            >= 90.0) {
          holder.mTextViewPercent.setTextColor(getResources().getColor(R.color.green_accent));
        }
      } catch (JSONException error) {
        Timber.e(error.getMessage(), "JSON Response : ");
      }
    }

    @Override public int getItemCount() {
      hidePlaceHolder();
      try {
        if (attdObj.getJSONArray("subjects").length() == 0) {
          showErrorPlaceholder("No Attendance data in erp");
        } else {
          hidePlaceHolder();
        }
        return attdObj.getJSONArray("subjects").length();
      } catch (JSONException e) {
        showErrorPlaceholder("Problem with json");
        Timber.e(e.getMessage(), "JSON Response : ");
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
        attdCard = view.findViewById(R.id.attdCard);
        mTextViewTitle = view.findViewById(R.id.attdSubjectName);
        mTextViewPercent = view.findViewById(R.id.attdSubjectPercent);
        attdMax = view.findViewById(R.id.attdMaxHours);
        attdAbsent = view.findViewById(R.id.attdAbsentHours);
        attdAttend = view.findViewById(R.id.attdAttendHours);
        attdOd = view.findViewById(R.id.attdOdPercent);
        attdBuffer = view.findViewById(R.id.attdBuffer);
        attdMin = view.findViewById(R.id.attdMin);
        attdExtra = view.findViewById(R.id.attdExtra);
        attdExpand = view.findViewById(R.id.attdExpand);
        toggle = false;
      }
    }
  }

  private class Att {

    double main_attendance;
    // Variables
    int count = 0, num = 1, denom = 1;
    int countp = 0, deno = 1;
    private double pre;
    private double ttl;

    private Att() {
    }

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

    private double getBuffer(double present, double total) {
      this.attnCalc(present, total);
      return this.predict();
    }
  }
}