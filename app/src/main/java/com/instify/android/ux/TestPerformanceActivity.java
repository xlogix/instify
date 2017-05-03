package com.instify.android.ux;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instify.android.R;
import com.instify.android.helpers.RetrofitBuilder;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.interfaces.RetrofitInterface;
import com.instify.android.models.TestPerformanceResponseModel;
import com.instify.android.ux.adapters.TestPerformanceAdapterParent;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Abhish3k on 15-03-2017.
 */

public class TestPerformanceActivity extends AppCompatActivity {

    @BindView(R.id.nav_drawer_user_photo)
    CircleImageView mNavDrawerUserPhoto;
    @BindView(R.id.nav_drawer_header_text)
    TextView mNavDrawerHeaderText;
    @BindView(R.id.recycler_view_test_performance)
    RecyclerView mRecyclerViewTestPerformance;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    SQLiteHandler db = new SQLiteHandler(this);
    private FirebaseUser mFirebaseUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_performance);
        ButterKnife.bind(this);
        mRecyclerViewTestPerformance.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewTestPerformance.setHasFixedSize(true);
        setSupportActionBar(mToolbar);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser != null) {
            try {
                // Set profile picture from Firebase account
                Glide.with(this)
                        .load(mFirebaseUser.getPhotoUrl().toString()).placeholder(R.drawable.default_pic_face)
                        .dontAnimate()
                        .centerCrop()
                        .priority(Priority.HIGH)
                        .into(mNavDrawerUserPhoto);
            } catch (Exception e) {
                Timber.d(e);
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        // Set the name
        mNavDrawerHeaderText.setText(db.getUserDetails().get("name"));

        // Setup SupportActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mToolbarLayout.setTitle("Test Performance");
                    isShow = true;
                } else if (isShow) {
                    mToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        AttemptJson();

    }


    void AttemptJson() {

        RetrofitInterface client = RetrofitBuilder.createService(RetrofitInterface.class);
        Call<TestPerformanceResponseModel> call = client.GetTestPerformance(db.getUserDetails().get("token"), db.getUserDetails().get("created_at"));
//        showRefreshing();
        call.enqueue(new Callback<TestPerformanceResponseModel>() {
            @Override
            public void onResponse(Call<TestPerformanceResponseModel> call, Response<TestPerformanceResponseModel> response) {
                TestPerformanceResponseModel t = response.body();
                if (response.isSuccessful()) {
//                    hideRefreshing();
                    TestPerformanceAdapterParent test = new TestPerformanceAdapterParent(t.getTestPerformance(), TestPerformanceActivity.this);
                    mRecyclerViewTestPerformance.setAdapter(test);
                    Snackbar.make(findViewById(android.R.id.content), "Sync Successful", Snackbar.LENGTH_SHORT).show();

                    //TODO Create Adapter here When api is Complete
                } else {


//                    hideRefreshing();
                    Snackbar.make(findViewById(android.R.id.content), "Sync Failed", Snackbar.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<TestPerformanceResponseModel> call, Throwable t) {

//                hideRefreshing();

                Snackbar.make(findViewById(android.R.id.content), "Sync Failed", Snackbar.LENGTH_SHORT).show();
            }
        });

    }


}