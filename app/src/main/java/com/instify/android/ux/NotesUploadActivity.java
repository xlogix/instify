package com.instify.android.ux;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.NotesFileModel;
import com.instify.android.services.MyFirebaseUploadService;
import java.util.Calendar;
import javax.annotation.Nonnull;
import timber.log.Timber;

public class NotesUploadActivity extends AppCompatActivity implements View.OnClickListener {
  private static final String TAG = NotesUploadActivity.class.getSimpleName();

  //Declaring views
  private Button buttonChoose;
  private Button buttonUpload;
  private EditText editText;
  private EditText editTextdesc;
  // URI to store the image
  private Uri mFileUri = null;
  private Uri mFilePath;

  private Uri mDownloadUrl;
  private SearchView searchView = null;
  private String mSubjectcode;
  private String mFiletype;
  private BroadcastReceiver mBroadcastReceiver;
  private ProgressDialog mProgressDialog;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_notes);
    setTitle(Html.fromHtml("<small>POST TO- " + getIntent().getStringExtra("code") + "</small>"));

    // Initializing views
    buttonUpload = findViewById(R.id.buttonUpload);
    editText = findViewById(R.id.editTextName);
    editTextdesc = findViewById(R.id.desc);
    mFilePath = Uri.parse(getIntent().getExtras().getString("fileuri"));
    mSubjectcode = getIntent().getExtras().getString("code");
    mFiletype = getIntent().getExtras().getString("filetype");

    // Setting Click Listeners
    buttonUpload.setOnClickListener(this);
    // Download receiver
    mBroadcastReceiver = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        Timber.d(TAG, "onReceive:", intent);
        hideProgressDialog();

        switch (intent.getAction()) {
          case MyFirebaseUploadService.UPLOAD_COMPLETED:
            onUploadResultIntent(intent);
            break;
          default:
            intent.setPackage(NotesUploadActivity.this.getPackageName());
            stopService(intent);
            break;
        }
      }
    };
  }

  private void onUploadResultIntent(Intent intent) {
    // Got a new intent from MyUploadService with a success or failure
    mDownloadUrl = intent.getParcelableExtra(MyFirebaseUploadService.EXTRA_DOWNLOAD_URL);
    mFileUri = intent.getParcelableExtra(MyFirebaseUploadService.EXTRA_FILE_URI);
    intent.setPackage(this.getPackageName());
    if (mDownloadUrl != null) {
      SQLiteHandler db = new SQLiteHandler(this);
      NotesFileModel nfm =
          new NotesFileModel(editText.getText().toString(), mDownloadUrl.toString(),
              editTextdesc.getText().toString(), getCurrentTime(), db.getUserDetails().getName(),
              db.getUserDetails().getRegno(), mFiletype, getUnixtime());
      DatabaseReference ref =
          FirebaseDatabase.getInstance().getReference().child("notes").child(mSubjectcode).push();
      ref.setValue(nfm);
      stopService(intent);
      finish();
    }
  }

  // Get Current Time for naming the file
  public String getCurrentTime() {
    Calendar c = Calendar.getInstance();
    return c.get(Calendar.DAY_OF_MONTH) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-" + c.get(
        Calendar.YEAR) + " " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(
        Calendar.SECOND);
  }

  public long getUnixtime() {
    Calendar c = Calendar.getInstance();
    return c.getTimeInMillis();
  }

  @Override public void onClick(View v) {
    //    if (v == buttonChoose) {
    //      showFileChooser();
    //    }
    if (v == buttonUpload) {
      if (TextUtils.isEmpty(editText.getText().toString()) || TextUtils.isEmpty(
          editTextdesc.getText().toString())) {
        Toast.makeText(this, "Field Cant be empty!!", Toast.LENGTH_SHORT).show();
      } else {
        showProgressDialog();
        uploadFromUri(mFilePath);
      }
    }
  }

  // [START upload_from_uri]
  private void uploadFromUri(Uri fileUri) {
    Timber.d(TAG, "uploadFromUri:src:", fileUri.toString());

    // Save the File URI
    mFileUri = fileUri;

    // Start MyUploadService to upload the file, so that the file is uploaded
    // even if this Activity is killed or put in the background
    startService(new Intent(this, MyFirebaseUploadService.class).putExtra(
        MyFirebaseUploadService.EXTRA_FILE_URI, fileUri)
        .putExtra("storage_loc", mSubjectcode)
        .setAction(MyFirebaseUploadService.ACTION_UPLOAD)
        .setPackage(this.getPackageName()));
  }

  @Override protected void onStart() {
    super.onStart();
    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
    manager.registerReceiver(mBroadcastReceiver, MyFirebaseUploadService.getIntentFilter());
  }

  private void showProgressDialog() {
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setMessage("Uploading...");
      mProgressDialog.setIndeterminate(true);
    }

    mProgressDialog.show();
  }

  private void hideProgressDialog() {
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }
}

