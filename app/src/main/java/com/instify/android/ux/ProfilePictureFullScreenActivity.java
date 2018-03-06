package com.instify.android.ux;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.instify.android.R;
import com.soundcloud.android.crop.Crop;
import java.io.File;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

/**
 * Created by Abhish3k on 29-03-2017.
 */

public class ProfilePictureFullScreenActivity extends AppCompatActivity
    implements EasyPermissions.PermissionCallbacks {

  public static final String ANDROID_RESOURCE = "android.resource://";
  public static final String FORWARD_SLASH = "/";
  // Permission code for Camera and Gallery Permission
  private static final int RC_CAMERA_AND_GALLERY_PERM = 123;
  // [START initialize_auth]
  private FirebaseUser mFirebaseUser;
  // [END initialize_auth]
  private ImageView userImage;
  // Declare AdView
  private AdView mAdView;

  private static Uri resIdToUri(Context context, int resId) {
    return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FORWARD_SLASH + resId);
  }
  // [START add_lifecycle_methods]

  /**
   * Called when leaving the activity
   */
  @Override public void onPause() {
    if (mAdView != null) {
      mAdView.pause();
    }
    super.onPause();
  }

  /**
   * Called when returning to the activity
   */
  @Override public void onResume() {
    super.onResume();
    if (mAdView != null) {
      mAdView.resume();
    }
  }
  // [END add_lifecycle_methods]

  /**
   * Called before the activity is destroyed
   */
  @Override public void onDestroy() {
    if (mAdView != null) {
      mAdView.removeAllViews();
      mAdView.destroy();
      finish();
    }
    super.onDestroy();
  }

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_full_screen_profile_picture);

    // Get current user
    mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    if (getSupportActionBar() != null) {
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      setTitle(mFirebaseUser.getDisplayName());
    }

    // Declare Img Button
    userImage = findViewById(R.id.fullimg);

    // Checks if the user didn't remove the image
    // Put the picture into the image View
    Glide.with(this)
        .load(mFirebaseUser.getPhotoUrl())
        .apply(new RequestOptions().dontAnimate())
        .apply(new RequestOptions().centerCrop())
        .apply(new RequestOptions().priority(Priority.HIGH))
        .into(userImage);

    // AdMob ad unit IDs are not currently stored inside the google-services.json file.
    // Developers using AdMob can store them as custom values in a string resource file or
    // simply use constants. Note that the ad units used here are configured to return only test
    // ads, and should not be used outside this sample.
    mAdView = findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);
    // [END load_banner_ad]

    userImage.setOnClickListener(v -> promptProfileChanger());
  }

  // Send the file to server
  private void sendToServer(Uri received) {
    UserProfileChangeRequest profileUpdates =
        new UserProfileChangeRequest.Builder().setDisplayName(mFirebaseUser.getDisplayName())
            .setPhotoUri(Uri.parse(received.toString()))
            .build();

    mFirebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        Timber.d("User profile updated.");
        Toast.makeText(ProfilePictureFullScreenActivity.this, "Successfully updated",
            Toast.LENGTH_SHORT).show();
      }
    });
  }

  private void promptProfileChanger() {
    final CharSequence[] items =
        { "Take Photo or Choose from Gallery", "Remove Picture", "Cancel" };
    AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePictureFullScreenActivity.this);
    builder.setTitle("Profile Photo");
    builder.setItems(items, (dialog, item) -> {
      if (items[item].equals("Take Photo or Choose from Gallery")) {
        // Get the picture from camera or storage
        getPicture();
      } else if (items[item].equals("Remove Picture")) {
        // Set image
        userImage.setImageResource(R.drawable.default_pic_face);
        // Send to server
        sendToServer(
            resIdToUri(ProfilePictureFullScreenActivity.this, R.drawable.default_pic_face));
        // Restart the activity
        recreate();
      } else if (items[item].equals("Cancel")) {
        dialog.dismiss();
      }
    });
    builder.show();
  }

  @AfterPermissionGranted(RC_CAMERA_AND_GALLERY_PERM) private void getPicture() {
    if (Build.VERSION.SDK_INT < 19) {
      String[] perms = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };

      if (EasyPermissions.hasPermissions(this, perms)) {
        // Have permission, do the thing!
        Crop.pickImage(this);
      } else {
        // Ask for one permission
        EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
            RC_CAMERA_AND_GALLERY_PERM, perms);
      }
    } else {
      String[] perms = {
          Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
          Manifest.permission.MANAGE_DOCUMENTS
      };

      if (EasyPermissions.hasPermissions(this, perms)) {
        // Have permission, do the thing!
        Crop.pickImage(this);
      } else {
        // Ask for one permission
        EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
            RC_CAMERA_AND_GALLERY_PERM, perms);
      }
    }
  }

  // [START] Cropping functions
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // handle result of pick image chooser
    if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
      beginCrop(data.getData());
    } else if (requestCode == Crop.REQUEST_CROP) {
      handleCrop(resultCode, data);
    } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
      // Do something after user returned from app settings screen, like showing a Toast.
      Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
          .show();
    }
  }

  private void beginCrop(Uri source) {
    Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
    Crop.of(source, destination).asSquare().start(this);
  }

  private void handleCrop(int resultCode, Intent result) {
    if (resultCode == RESULT_OK) {
      userImage.setImageURI(Crop.getOutput(result));
      // Send to Server
      sendToServer(Crop.getOutput(result));
      // Restart the activity
      recreate();
    } else if (resultCode == Crop.RESULT_ERROR) {
      Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
    }
  }
  // [END] Cropping functions

  // [START] EasyPermissions Default Functions
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // EasyPermissions handles the request result.
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
    // Some permissions have been granted
    Timber.d("onPermissionsGranted:" + requestCode + ":" + perms.size());
  }

  @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
    // Some permissions have been denied
    Timber.d("onPermissionsDenied:" + requestCode + ":" + perms.size());

    // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
    // This will display a dialog directing them to enable the permission in app settings.
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      new AppSettingsDialog.Builder(this).build().show();
    }
  }
  // [END] EasyPermission Default Functions
}
