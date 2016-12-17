package com.instify.android.ux;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.instify.android.R;
import com.instify.android.ux.fragments.CampNewsFragment;
import com.instify.android.ux.fragments.ERPFragment;
import com.instify.android.ux.fragments.NotesFragment;
import com.instify.android.ux.fragments.TimeTableFragment;
import com.instify.android.ux.fragments.TrendingFragment;
import com.instify.android.ux.fragments.UnivNewsFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Abhish3k on 3/1/2016. Main Activity
 */

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int RC_TAKE_PICTURE = 101;
    private static final int GALLERY = 1;
    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_GALLERY_PERM = 121;
    private static final String TAG = "ActivityMain";
    private static Bitmap rotateImage = null;
    public FloatingActionButton fab;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private ImageView imageView;
    private Uri mCaptureUri = null;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;

    //Ad View
    /* private AdView mAdView;
    private InterstitialAd mInterstitialAd; */

    private static int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);

        checkPlayServices();

        // Drawer Layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        //hideFloatingActionButton();

        // [START] Initialize Navigation Drawer and Profile Picture

        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        if (navView != null) {
            setupDrawerContent(navView);
        }
        View headerView = navView.inflateHeaderView(R.layout.nav_header_main);
        imageView = (ImageView) headerView.findViewById(R.id.profile);
        ImageButton changer = (ImageButton) headerView.findViewById(R.id.profile_changer);
        // Set image from Google account
        if (mFirebaseUser != null) {
            Glide.with(this)
                    .load(mCaptureUri)
                    .into(imageView);
        }

        // ViewPager
        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        //Listeners
        changer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // Gonna work on three more functions
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //Decode Image to String
        String imgPath = getSharedPreferences("userData", MODE_PRIVATE).getString("PicPath", null);
        if (imgPath != null) {
            byte[] imageAsBytes = Base64.decode(imgPath.getBytes(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }


        if (mFirebaseUser != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + mFirebaseUser.getUid());
            Toast.makeText(MainActivity.this, "Welcome " + mFirebaseUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        } else {
            // User is signed out
            Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
/*
        // Ad-View
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        // Create an InterstitialAd object. This same object can be re-used whenever you want to
        // show an interstitial.
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));

        // [START create_interstitial_ad_listener]
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        // [END create_interstitial_ad_listener]
*/
    }

    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        if (isDeviceOnline()) {
            Snackbar.make(viewPager, "Device Online", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(viewPager, "Device Offline. Functionality may be limited", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /*    @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    */

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        // Some permissions have been granted
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        // Some permissions have been denied
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied permissions and checked NEVER ASK AGAIN.
        // This will display a dialog directing them to enable the permission in app settings.
        EasyPermissions.checkDeniedPermissionsNeverAskAgain(this,
                getString(R.string.rationale_ask_again),
                R.string.setting, R.string.cancel, perms);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Choose file storage location
            File file = new File(Environment.getExternalStorageDirectory(), UUID.randomUUID().toString() + ".jpg");
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            mCaptureUri = Uri.fromFile(file);
            startActivityForResult(takePicture, RC_TAKE_PICTURE);

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

// Ad-Mob Starts (Rest is on OnCreate Method)

    @TargetApi(Build.VERSION_CODES.M)
    @AfterPermissionGranted(RC_GALLERY_PERM)
    public void galleryTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Have permissions, yay!
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture "), GALLERY);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage),
                    RC_GALLERY_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Load a new interstitial ad asynchronously.
     */
// [START request_new_interstitial]
/*    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
    // [END request_new_interstitial]

    // [START add_lifecycle_methods]

    /**
     * Called when leaving the activity
     */
/*    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
/*    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (!mInterstitialAd.isLoaded()) {
            requestNewInterstitial();
        }
    }

    /**
     * Called before the activity is destroyed
     */
/*    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    // [END add_lifecycle_methods]
*/
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Remove Picture", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Profile Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraTask();

                } else if (items[item].equals("Choose from Gallery")) {
                    galleryTask();

                } else if (items[item].equals("Remove Picture")) {
                    imageView.setImageResource(R.drawable.default_pic);
                    getSharedPreferences("userData", MODE_PRIVATE).edit().putString("PicPath", null).apply();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap Image;
        if (requestCode == RC_TAKE_PICTURE && resultCode != 0) {
            try {
                Uri captured_image = mCaptureUri;
                Image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), captured_image);
                Bitmap image = Image;
                imageView.setImageBitmap(Image);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 80, baos);

                // Encoding image to string
                byte[] b = baos.toByteArray();
                String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
                getSharedPreferences("userData", MODE_PRIVATE).edit().putString("PicPath", imageEncoded).apply();

            } catch (IOException e) {
                Log.w(TAG, "File URI is null");
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY && resultCode != 0) {
            Uri mImageUri = data.getData();
            try {
                Image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Image.compress(Bitmap.CompressFormat.PNG, 80, baos);

                if (getOrientation(getApplicationContext(), mImageUri) != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getOrientation(getApplicationContext(), mImageUri));
                    if (rotateImage != null)
                        rotateImage.recycle();
                    rotateImage = Bitmap.createBitmap(Image, 0, 0, Image.getWidth(), Image.getHeight(), matrix,
                            true);
                    imageView.setImageBitmap(rotateImage);
                } else
                    imageView.setImageBitmap(Image);

                // Encoding image to string
                byte[] b = baos.toByteArray();
                String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
                getSharedPreferences("userData", MODE_PRIVATE).edit().putString("PicPath", imageEncoded).apply();

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "NULL, Try Again!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this, "Taking picture failed", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public void signOut() {
        mAuth.signOut();
    }

    //  Log Out User
    private void logoutUser() {
        // Clearing all data from Shared Preferences
        getSharedPreferences("userData", MODE_PRIVATE).edit().clear().apply();
        signOut();
        // After logout redirect user to Login Activity
        Intent i = new Intent(this, IntroActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Activity
        startActivity(i);
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TimeTableFragment(), "Time Table");
        adapter.addFrag(new TrendingFragment(), "What's Trending");
        adapter.addFrag(new CampNewsFragment(), "Campus News");
        adapter.addFrag(new UnivNewsFragment(), "University News");
        adapter.addFrag(new NotesFragment(), "Notes");
        adapter.addFrag(new ERPFragment(), "ERP");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.nav_timetable:
                        viewPager.setCurrentItem(0);
                        break;

                    case R.id.nav_campus:
                        viewPager.setCurrentItem(2);
                        break;

                    case R.id.nav_univ_news:
                        viewPager.setCurrentItem(3);
                        break;

                    case R.id.nav_notes:
                        viewPager.setCurrentItem(4);
                        break;

                    case R.id.nav_erp:
                        viewPager.setCurrentItem(5);
                        break;
                    case R.id.nav_share:
                        //Intent share = new Intent();
                        //share.setAction(Intent.ACTION_SEND);
                        //share.putExtra(Intent.EXTRA_TEXT, "Thanks for Sharing!");
                        try {
                            Intent share = new Intent(Intent.ACTION_VIEW);
                            share.setData(Uri.parse("market://details?id=com.google.android.gms"));
                            startActivity(share);
                        } catch (Exception e) { //Google Play is not installed
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"));
                            startActivity(intent);
                        }
                        //share.setType("text/plain");
                        //startActivity(share);
                        break;
                    case R.id.nav_send:
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            setContentView(R.layout.menu_settings);
            return true;
        } else if (id == R.id.action_Logout) {
            logoutUser();
            return true;
        }
        switch (id) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}