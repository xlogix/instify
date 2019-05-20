package com.instify.android.services;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.instify.android.R;
import com.instify.android.ux.UploadNotesActivity;

import timber.log.Timber;

/**
 * Service to handle uploading files to Firebase Storage.
 */
public class MyFirebaseUploadService extends MyFirebaseBaseTaskService {

  /** Intent Actions **/
  public static final String ACTION_UPLOAD = "action_upload";
  public static final String UPLOAD_COMPLETED = "upload_completed";
  public static final String UPLOAD_ERROR = "upload_error";

  /** Intent Extras **/
  public static final String EXTRA_FILE_URI = "extra_file_uri";
  public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

  // [START declare_ref]
  private StorageReference mStorageRef;
  // [END declare_ref]

  @Override public void onCreate() {
    super.onCreate();

    // [START get_storage_ref]
    mStorageRef = FirebaseStorage.getInstance().getReference();
    // [END get_storage_ref]
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    Timber.d("onStartCommand:" + intent + ":" + startId);
    if (ACTION_UPLOAD.equals(intent.getAction())) {
      Uri fileUri = intent.getParcelableExtra(EXTRA_FILE_URI);
      String path = intent.getStringExtra("storage_loc");
      uploadFromUri(fileUri, path);
    }

    return START_REDELIVER_INTENT;
  }
  // [END upload_from_uri]

  // [START upload_from_uri]
  private void uploadFromUri(final Uri fileUri, final String path) {
    Timber.d("uploadFromUri:src:" + fileUri.toString());

    // [START_EXCLUDE]
    taskStarted();
    showProgressNotification(getString(R.string.progress_uploading), 0, 0);
    // [END_EXCLUDE]

    // [START get_child_ref]
    // Get a reference to store file at photos/<FILENAME>.jpg
    final StorageReference photoRef =
        mStorageRef.child("notes").child(path).child(fileUri.getLastPathSegment());
    // [END get_child_ref]

    // Upload file to Firebase Storage
    Timber.d("uploadFromUri:dst:" + photoRef.getPath());
    photoRef.putFile(fileUri)
            .addOnProgressListener(
                taskSnapshot -> showProgressNotification(getString(R.string.progress_uploading),
                    taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount()))
        .addOnSuccessListener(taskSnapshot -> {
          // Upload succeeded
          Timber.d("uploadFromUri:onSuccess");

          // Get the public download URL
            Task<Uri> Uri = taskSnapshot.getStorage().getDownloadUrl();
            Uri downloadUri = null;
            while (!Uri.isComplete()) {
                downloadUri = Uri.getResult();
            }

          // [START_EXCLUDE]
          broadcastUploadFinished(downloadUri, fileUri);
          showUploadFinishedNotification(downloadUri, fileUri);
          taskCompleted();
          // [END_EXCLUDE]
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception exception) {
            // Upload failed
            Timber.w(exception, "uploadFromUri:onFailure");

            // [START_EXCLUDE]
            broadcastUploadFinished(null, fileUri);
            showUploadFinishedNotification(null, fileUri);
            taskCompleted();
            // [END_EXCLUDE]
          }
        });
  }
  // [END upload_from_uri]

  /**
   * /**
   * Broadcast finished upload (success or failure).
   *
   * @return true if a running receiver received the broadcast.
   */
  private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
    boolean success = downloadUrl != null;

    String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

    Intent broadcast = new Intent(action).putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
        .putExtra(EXTRA_FILE_URI, fileUri);
    return LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcast);
  }

  /**
   * Show a notification for a finished upload.
   */
  private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
    // Hide the progress notification
    dismissProgressNotification();

    // Make Intent to MainActivity
    Intent intent =
        new Intent(this, UploadNotesActivity.class).putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
            .putExtra(EXTRA_FILE_URI, fileUri)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

    boolean success = downloadUrl != null;
    String caption =
        success ? getString(R.string.upload_success) : getString(R.string.upload_failure);
    showFinishedNotification(caption, intent, success);
  }

  public static IntentFilter getIntentFilter() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(UPLOAD_COMPLETED);
    filter.addAction(UPLOAD_ERROR);

    return filter;
  }
}
