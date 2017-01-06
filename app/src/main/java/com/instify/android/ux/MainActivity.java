package com.instify.android.ux;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instify.android.R;
import com.instify.android.models.UserData;
import com.instify.android.ux.fragments.AttendanceFragment;
import com.instify.android.ux.fragments.CampNewsFragment;
import com.instify.android.ux.fragments.NotesFragment;
import com.instify.android.ux.fragments.TimeTableFragment;
import com.instify.android.ux.fragments.UnivNewsFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * Created by Abhish3k on 3/1/2016. Main Activity
 */

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    /* Play Services Request required to check if Google Services is installed or not */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int RC_SETTINGS_SCREEN = 125;
    private static final int RC_TAKE_PICTURE = 101;
    private static final int GALLERY = 1;
    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_GALLERY_PERM = 121;
    private static final String TAG = "ActivityMain";
    public FloatingActionButton mSharedFab;
    private DrawerLayout drawerLayout;
    private ViewPager mViewPager;
    private ImageView imageView;
    private Uri mCaptureUri = null;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference dbRef, userRef;

    public UserData userInfoObject;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures that user didn't un-install Google Play Services required for Firebase related tasks.
        checkPlayServices();
        if (isDeviceOnline()) {
            Timber.d(TAG, "Device is online.");
        } else {
            Snackbar.make(mViewPager, "Device Offline. Functionality may be limited", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedFab = (FloatingActionButton) findViewById(R.id.shared_fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.content_description_open_navigation_drawer, R.string.content_description_close_navigation_drawer);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        // [START] Initialize Navigation Drawer and Profile Picture
        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        if (navView != null) {
            setupDrawerContent(navView);
        }
        View headerView = navView.inflateHeaderView(R.layout.nav_header_main);
        imageView = (ImageView) headerView.findViewById(R.id.profile);
        ImageButton changer = (ImageButton) headerView.findViewById(R.id.profile_changer);
        // Set image from Firebase account
        if (mFirebaseUser != null) {
            Glide.with(this)
                    .load(mCaptureUri)
                    .into(imageView);
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Set the default tab as Campus Portal
        mViewPager.setCurrentItem(1);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 1:
                        mSharedFab.show();
                        break;
                    case 3:
                        mSharedFab.show();
                        break;
                    default:
                        mSharedFab.hide();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);

        //Listeners
        changer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptProfileChanger();
            }
        });

        //Decode Image to String
        String imgPath = getSharedPreferences("userData", MODE_PRIVATE).getString("PicPath", null);
        if (imgPath != null) {
            byte[] imageAsBytes = Base64.decode(imgPath.getBytes(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }

        checkPlayServices();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + mFirebaseUser.getUid());
            Toast.makeText(MainActivity.this, "Welcome " + mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();

            dbRef = FirebaseDatabase.getInstance().getReference();
            userRef = dbRef.child("users").child(mFirebaseUser.getUid());

            // User object generation for the database - for usage in all fragments
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userInfoObject = dataSnapshot.getValue(UserData.class);
                    Toast.makeText(MainActivity.this, "User data collected!! :)", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            // User is signed out
            Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onAuthStateChanged:signed_out");
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

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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

            // Camera
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCaptureUri);
                cameraIntents.add(intent);
            }

            startActivityForResult(takePicture, RC_TAKE_PICTURE);

        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

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

    private void promptProfileChanger() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Remove Picture", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Profile Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    // Open Camera
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
                image.compress(Bitmap.CompressFormat.PNG, 20, baos);

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
                Image.compress(Bitmap.CompressFormat.PNG, 20, baos);

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

    //  Log Out User
    private void logoutUser() {
        // Clearing all data from Shared Preferences
        getSharedPreferences("userData", MODE_PRIVATE).edit().clear().apply();
        // SignOut from Firebase
        mAuth.signOut();
        // After logout redirect user to Intro Activity
        Intent i = new Intent(this, IntroActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Activity
        startActivity(i);
        finish();
    }

    public void intentGPACalculator(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //menuItem.setChecked(true);
                switch (menuItem.getItemId()) {
                    case R.id.nav_gpa:
                        intentGPACalculator(MainActivity.this, "com.gupta.ishansh.gcmcalculator");
                        break;
                    case R.id.nav_erp:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_campus:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_timetable:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_notes:
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.nav_univ_news:
                        mViewPager.setCurrentItem(4);
                        break;
                    case R.id.nav_share:
                        try {
                            Intent share = new Intent(Intent.ACTION_VIEW);
                            share.setData(Uri.parse("market://details?id=com.google.android.gms"));
                            startActivity(share);
                        } catch (Exception e) { // Google Play is not installed
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms"));
                            startActivity(intent);
                        }
                        break;
                    case R.id.nav_send:
                        String[] emails = {"abhishekuniyal09@gmail.com"};
                        String subject = "I want to submit a Feedback";
                        String message = "Hi, ";
                        Intent email = new Intent(Intent.ACTION_SENDTO);
                        email.putExtra(Intent.EXTRA_EMAIL, emails);
                        email.putExtra(Intent.EXTRA_SUBJECT, subject);
                        email.putExtra(Intent.EXTRA_TEXT, message);
                        email.setType("*/*");
                        email.setData(Uri.parse("mailto:"));

                        if (email.resolveActivity(getPackageManager()) != null) {
                            startActivity(email);
                        }
                        break;
                    case R.id.nav_about:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        break;
                    case R.id.nav_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                    case R.id.nav_logout:
                        logoutUser();
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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

        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_filter) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Tab layout and Fragments

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Default Constructor
         */
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        static final int NUM_TABS = 5;

        static final int TAB_ATTENDANCE = 0;
        static final int TAB_CAMPUS_NEWS = 1;
        static final int TAB_TIME_TABLE = 2;
        static final int TAB_NOTES = 3;
        static final int TAB_UNIVERSITY_NEWS = 4;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case TAB_ATTENDANCE:
                    return AttendanceFragment.newInstance();
                case TAB_CAMPUS_NEWS:
                    return CampNewsFragment.newInstance();
                case TAB_TIME_TABLE:
                    return TimeTableFragment.newInstance();
                case TAB_NOTES:
                    return NotesFragment.newInstance();
                case TAB_UNIVERSITY_NEWS:
                    return UnivNewsFragment.newInstance();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Attendance";
                case 1:
                    return "Campus Portal";
                case 2:
                    return "Time Table";
                case 3:
                    return "Notes";
                case 4:
                    return "University Portal";
            }
            return null;
        }
    }

    // [START] EasyPermissions Default Functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
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

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.rationale_ask_again))
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(getString(R.string.setting))
                    .setNegativeButton(getString(R.string.cancel), null /* click listener */)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }
    // [END] EasyPermission Default Functions
}