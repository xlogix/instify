package com.instify.android.ux;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.instify.android.R;
import com.instify.android.helpers.RetrofitBuilder;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.interfaces.RetrofitInterface;
import com.instify.android.models.TestPerformanceResponseModel;
import com.instify.android.ux.adapters.TestPerformanceAdapterParent;

/**
 * Created by Abhish3k on 15-03-2017.
 */

public class TestPerformanceActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view_test_performance)
    RecyclerView mRecyclerViewTestPerformance;
    @BindView(R.id.swipe_refresh_layout_test_performance)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private SQLiteHandler db = new SQLiteHandler(this);

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
        // Initialize SwipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeResources(R.color.red_primary, R.color.black, R.color.google_blue_900);
        mSwipeRefreshLayout.setOnRefreshListener(this::AttemptJson);
        // Initialize Recycle View
        mRecyclerViewTestPerformance.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewTestPerformance.setHasFixedSize(true);
        mRecyclerViewTestPerformance.setNestedScrollingEnabled(false);

        // Call the API
        AttemptJson();
    }

    void AttemptJson() {
        // Declare Retrofit
        RetrofitInterface client = RetrofitBuilder.createService(RetrofitInterface.class);
        Call<TestPerformanceResponseModel> call = client.GetTestPerformance(db.getUserDetails().getRegno(), db.getUserDetails().getToken());
        // Update UI
        showRefreshing();

        call.enqueue(new Callback<TestPerformanceResponseModel>() {
            @Override
            public void onResponse(Call<TestPerformanceResponseModel> call, Response<TestPerformanceResponseModel> response) {
                TestPerformanceResponseModel t = response.body();
                if (response.isSuccessful()) {
                    // Update UI, Snackbar is only shown when loading fails (keeping it minimal)
                    hideRefreshing();

                    TestPerformanceAdapterParent test = new TestPerformanceAdapterParent(t.getTestPerformance(), TestPerformanceActivity.this);
                    mRecyclerViewTestPerformance.setAdapter(test);
                    // TODO : Create Adapter here When api is Complete
                } else {
                    // Update UI
                    hideRefreshing();
                    Snackbar.make(findViewById(android.R.id.content), "Sync Failed", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TestPerformanceResponseModel> call, Throwable t) {
                // Update UI
                hideRefreshing();
                Snackbar.make(findViewById(android.R.id.content), "Sync Failed", Snackbar.LENGTH_SHORT).show();
            }
        });
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