package com.instify.android.ux;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.instify.android.R;
import com.instify.android.helpers.RetrofitBuilder;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.interfaces.RetrofitInterface;
import com.instify.android.models.TestPerformanceResponseModel;
import com.instify.android.ux.adapters.TestPerformanceAdapterParent;
import javax.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Abhish3k on 15-03-2017.
 */

public class TestPerformanceActivity extends AppCompatActivity {

  @BindView(R.id.recycler_view_test_performance) RecyclerView mRecyclerViewTestPerformance;
  @BindView(R.id.fab_refresh) FloatingActionButton refreshingFAB;
  @BindView(R.id.error_message) TextView errorMessage;
  @BindView(R.id.placeholder_error) LinearLayout placeholderError;
  SQLiteHandler db = new SQLiteHandler(this);

  // [START add_lifecycle_methods]

  /**
   * Called when leaving the activity
   */
  @Override public void onPause() {
    super.onPause();
  }

  /**
   * Called when returning to the activity
   */
  @Override protected void onResume() {
    super.onResume();
  }

  /**
   * Called before the activity is destroyed
   */
  @Override public void onDestroy() {
    super.onDestroy();
  }

  // [END add_lifecycle_methods]

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_performance);
    // Setup SupportActionBar
    if (getSupportActionBar() != null) {
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    // Bind the Views
    ButterKnife.bind(this);
    refreshingFAB.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        AttemptJson();
      }
    });
    // Initialize Recycle View
    mRecyclerViewTestPerformance.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerViewTestPerformance.setNestedScrollingEnabled(false);
    // Call the API
    AttemptJson();
  }

  void AttemptJson() {
    // Declare Retrofit
    RetrofitInterface client = RetrofitBuilder.createService(RetrofitInterface.class);
    Call<TestPerformanceResponseModel> call =
        client.GetTestPerformance(db.getUserDetails().getRegno(), db.getUserDetails().getToken());

    call.enqueue(new Callback<TestPerformanceResponseModel>() {
      @Override public void onResponse(@Nullable Call<TestPerformanceResponseModel> call,
          @Nullable Response<TestPerformanceResponseModel> response) {

        TestPerformanceResponseModel t = response.body();

        if (response.isSuccessful() && t != null && t.getTestPerformance() != null) {
          TestPerformanceAdapterParent test =
              new TestPerformanceAdapterParent(t.getTestPerformance(),
                  TestPerformanceActivity.this);
          // Update UI
          Snackbar.make(refreshingFAB, "Sync Successful", Snackbar.LENGTH_SHORT).show();

          if (test.getItemCount() == 0) {
            showErrorPlaceholder("No Data in ERP to display");
          } else {
            hidePlaceHolder();
          }
          // Set Adapter
          mRecyclerViewTestPerformance.setAdapter(test);
          // TODO : Create Adapter here when API is Complete
        } else {
          showErrorPlaceholder("Sync Failed");
        }
      }

      @Override public void onFailure(@Nullable Call<TestPerformanceResponseModel> call,
          @Nullable Throwable t) {
        showErrorPlaceholder("Sync Failed");
      }
    });
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
      errorMessage.setText("Something went wrong. Please try again!");
    }
  }
}