package com.instify.android.ux;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.instify.android.MeFragment;
import com.instify.android.R;
import com.instify.android.ux.adapters.BottomNavViewPagerAdapter;
import com.instify.android.ux.fragments.AttendanceFragment;
import com.instify.android.ux.fragments.UnivNewsFragment;

public class EnhancedMainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    MenuItem prevMenuItem;
    MeFragment meFragment;
    UnivNewsFragment univNewsFragment;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhanced_main);

        //getting bottom navigation view
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_main);

        //Initializing viewPager
        viewPager = findViewById(R.id.viewpager);

        navigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.me:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.my_feed:
                                viewPager.setCurrentItem(1);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                navigation.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navigation.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setupViewPager(ViewPager viewPager) {
        BottomNavViewPagerAdapter adapter = new BottomNavViewPagerAdapter(getSupportFragmentManager());
        meFragment=new MeFragment();
        univNewsFragment=new UnivNewsFragment();
        adapter.addFragment(meFragment);
        adapter.addFragment(univNewsFragment);
        viewPager.setAdapter(adapter);
    }

    @Override public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}
