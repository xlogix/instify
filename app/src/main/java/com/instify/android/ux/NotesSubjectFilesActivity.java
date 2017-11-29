package com.instify.android.ux;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.NotesFileModel;
import com.instify.android.ux.adapters.ListExpandableAdapter;
import com.instify.android.ux.adapters.NotesFileAdapter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class NotesSubjectFilesActivity extends AppCompatActivity {

  private static final String TAG = NotesSubjectFilesActivity.class.getSimpleName();
  // PERMS
  private static final int RC_CAMERA_PERMISSION = 101;
  private static final int RC_STORAGE_PERMISSION = 123;
  private static final int IMAGE_SELECT = 3;
  private static final int DOC_SELECT = 1;
  private static final int AUDIO_SELECT = 5;
  private static final int VIDEO_SELECT = 4;
  private static final int PDF_SELECT = 2;
  private static final int OTHER_SELECT = 6;
  RecyclerView mRVFish;
  SearchView searchView = null;
  @BindView(R.id.error_message) TextView errormessage;
  @BindView(R.id.placeholder_error) LinearLayout placeholderError;
  private Uri mFileUri = null;
  private Uri mFilePath;
  private String type;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notes_subject_files);
    ButterKnife.bind(this);
    setTitle(Html.fromHtml("<small>" + getIntent().getStringExtra("code") + "</small>"));

    mRVFish = findViewById(R.id.recycler_view_notes);
    setNotesFirebase(getIntent().getStringExtra("code"));
    //    getNotes(getIntent().getStringExtra("code"));
  }

  private void setNotesFirebase(final String subjectCode) {
    DatabaseReference ref =
        FirebaseDatabase.getInstance().getReference().child("notes").child(subjectCode);
    FirebaseRecyclerAdapter<NotesFileModel, NotesFileAdapter.MyHolder> adapter =
        new FirebaseRecyclerAdapter<NotesFileModel, NotesFileAdapter.MyHolder>(NotesFileModel.class,
            R.layout.card_view_notes_subjects_item, NotesFileAdapter.MyHolder.class, ref) {
          @Override public int getItemCount() {
            if (super.getItemCount() == 0) {
              showErrorPlaceholder("No notes to display, Be the first to upload!");
            } else {
              hidePlaceHolder();
            }
            return super.getItemCount();
          }

          @Override protected void populateViewHolder(NotesFileAdapter.MyHolder viewHolder,
              NotesFileModel model, int position) {
            viewHolder.setdatatoview(model);
            viewHolder.cv.setOnClickListener(v -> {
              Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(model.getNotefile()));
              startActivity(intent);
            });
          }
        };
    mRVFish.setLayoutManager(new LinearLayoutManager(NotesSubjectFilesActivity.this));

    mRVFish.setAdapter(adapter);
  }

  private void getNotes(final String subjectCode) {

    // Handle UI
    // showRefreshing();

    // Tag used to cancel the request
    String tag_string_req = "req_files";

    StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_FILES, response -> {
      try {
        JSONArray user = new JSONArray(response);
        boolean error;// = jObj.getBoolean("error");
        error = false;

        // Handle UI
        //hideRefreshing();

        // Check for error node in json
        if (!error) {
          ListExpandableAdapter expListAdapter;

          // declare array List for all headers in list
          ArrayList<String> headersArrayList = new ArrayList<>();
          List<NotesFileModel> NotesFileModel = new ArrayList<>();
          // Declare Hash map for all headers and their corresponding values
          HashMap<String, ArrayList<String>> childArrayList = new HashMap<>();

          // expListView = (ExpandableListView)findViewById(R.id.expListView);
          //    JSONArray user = jObj.getJSONArray(response);

          //  int i;
          //                        mAdapter = new NotesAdapter(notes);
          //                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
          //                        recyclerView.setLayoutManager(mLayoutManager);
          //                        recyclerView.setItemAnimator(new DefaultItemAnimator());

          for (int i = 0; i < user.length(); i++) {
            JSONObject json_data = user.getJSONObject(i);
            //  JSONObject subs = user.getJSONObject(user.getString(i));

            //                            notes movie = notes(subs.getString("sub-desc"));
            //                            notes.add(movie);
            //   notes obj = new notes(subs.getString("sub-desc"));

            NotesFileModel fishData = new NotesFileModel();
            fishData.notename = json_data.getString("name");
            fishData.notedesc = json_data.getString("desc");
            fishData.notetime = json_data.getString("uploaded");
            fishData.notefile = json_data.getString("file");
            fishData.noteregno = json_data.getString("regno");
            fishData.noteposter = json_data.getString("author");

            //fishData.sizeName = json_data.getString("registration").trim();
            //fishData.price = json_data.getString("ID");
            // fishData.image = "https://hashbird.com/gogrit.in/workspace/srm-api/studentImages/" + json_data.getString("registration").trim() + ".jpg";
            NotesFileModel.add(fishData);
          }
          // Setup and Handover data to recycler view
          //  mRVFish = (RecyclerView) findViewById(R.id.);
          NotesFileAdapter mAdapter =
              new NotesFileAdapter(NotesSubjectFilesActivity.this, NotesFileModel);
          mRVFish.setAdapter(mAdapter);
          mRVFish.setLayoutManager(new LinearLayoutManager(NotesSubjectFilesActivity.this));

          //                        mAdapter = new NotesAdapter(getApplicationContext(), notes);
          //                        mRVFish.setAdapter(mAdapter);
          //                        mRVFish.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        } else {
          showErrorPlaceholder("Database Error");
          // Update UI
          //   hideRefreshing();
        }
      } catch (JSONException e) {
        // Update UI
        //    hideRefreshing();
        showErrorPlaceholder("No Notes to Display, Be the First To upload");
      }
    }, error -> {
      showErrorPlaceholder("Network Error,Try Again");
      Timber.e("Network Error: " + error.getMessage());
      Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }) {
      @Override protected Map<String, String> getParams() {
        // Posting parameters to login url
        Map<String, String> params = new HashMap<>();
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String regNo = db.getUserDetails().getRegno();
        // String pass = db.getUserDetails().get("created_at");

        params.put("regno", regNo);
        //  params.put("pass", pass);
        params.put("subject", subjectCode);
        return params;
      }
    };
    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {

    // adds item to action bar
    getMenuInflater().inflate(R.menu.search_main, menu);

    // Get Search item from action bar and Get Search service
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchManager searchManager =
        (SearchManager) NotesSubjectFilesActivity.this.getSystemService(Context.SEARCH_SERVICE);
    if (searchItem != null) {
      searchView = (SearchView) searchItem.getActionView();
    }
    if (searchView != null) {
      searchView.setSearchableInfo(
          searchManager.getSearchableInfo(NotesSubjectFilesActivity.this.getComponentName()));
      searchView.setIconified(true);
    }

    MenuItem enai = menu.findItem(R.id.action_add_note);

    enai.setOnMenuItemClickListener(item -> {
      BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
      View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_upload_notes, null);
      bottomSheetDialog.setContentView(sheetView);
      bottomSheetDialog.show();
      LinearLayout doc = sheetView.findViewById(R.id.doc);
      LinearLayout pdf = sheetView.findViewById(R.id.pdf);
      LinearLayout audio = sheetView.findViewById(R.id.audio);
      LinearLayout image = sheetView.findViewById(R.id.image);
      LinearLayout video = sheetView.findViewById(R.id.video);
      LinearLayout camera = sheetView.findViewById(R.id.camera);
      LinearLayout other = sheetView.findViewById(R.id.other);
      doc.setOnClickListener(v -> {
        type = "doc";
        requestStoragePermission();
      });
      pdf.setOnClickListener(v -> {
        type = "pdf";
        requestStoragePermission();
      });
      camera.setOnClickListener(v -> {
        requestCameraPermission();
      });
      video.setOnClickListener(v -> {
        type = "video";
        requestStoragePermission();
      });
      audio.setOnClickListener(v -> {
        type = "audio";
        requestStoragePermission();
      });
      image.setOnClickListener(v -> {
        type = "image";
        requestStoragePermission();
      });
      other.setOnClickListener(v -> {
        type = "other";
        requestStoragePermission();
      });

      return true;
    });
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

  // Requesting permission
  @AfterPermissionGranted(RC_CAMERA_PERMISSION) private void requestCameraPermission() {
    String[] perms = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    if (EasyPermissions.hasPermissions(this, perms)) {
      // Choose file storage location
      dispatchTakePictureIntent();
      //File imagePath = new File(this.getFilesDir(), "imagese");
      //File newFile = new File(imagePath, getCurrentTime() + ".jpg");
      //mFileUri = getUriForFile(this, "com.instify.android.provider", newFile);
      ////String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Instify/";
      ////File path = new File(mPathName);
      ////// Check
      ////boolean isDirectoryCreated = path.exists();
      ////if (!isDirectoryCreated) {
      ////  isDirectoryCreated = path.mkdir();
      ////}
      ////if (isDirectoryCreated) {
      ////  mPathName += getCurrentTime() + ".jpg";
      ////  File filePath = new File(mPathName);
      ////  // Log it
      ////  Timber.d(TAG, "File address :" + filePath);
      ////  // Assign it
      //
      //// Get Intent
      //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      //takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
      //startActivityForResult(takePictureIntent, RC_CAMERA_PERMISSION);
    } else {

      // Ask for one permission
      EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
          RC_CAMERA_PERMISSION, perms);
    }
  }

  // Requesting permission
  @AfterPermissionGranted(RC_STORAGE_PERMISSION) private void requestStoragePermission() {
    if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      // Have permission, do the thing!
      switch (type) {
        case "pdf": {
          Intent intent = new Intent();
          intent.setType("application/pdf");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Complete action using... "),
              PDF_SELECT);
          break;
        }
        case "doc": {
          Intent intent = new Intent();
          intent.setType("application/docx");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Complete action using... "),
              DOC_SELECT);
          break;
        }
        case "audio": {
          Intent intent = new Intent();
          intent.setType("audio/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Complete action using... "),
              AUDIO_SELECT);
          break;
        }
        case "image": {
          Intent intent = new Intent();
          intent.setType("image/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Complete action using... "),
              IMAGE_SELECT);
          break;
        }
        case "video": {
          Intent intent = new Intent();
          intent.setType("video/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Complete action using... "),
              VIDEO_SELECT);
          break;
        }
        default: {
          Intent intent = new Intent();
          intent.setType("*/*");
          intent.setAction(Intent.ACTION_GET_CONTENT);
          startActivityForResult(Intent.createChooser(intent, "Complete action using... "),
              OTHER_SELECT);
          break;
        }
      }
    } else {
      // Ask for one permission
      EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
          RC_STORAGE_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE);
    }
  }

  // [START] EasyPermissions Default Functions
  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // EasyPermissions handles the request result.
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  // [END] EasyPermission Default Functions
  // Get Current Time for naming the file
  public String getCurrentTime() {
    Calendar c = Calendar.getInstance();
    return c.get(Calendar.DAY_OF_MONTH) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-" + c.get(
        Calendar.YEAR) + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(
        Calendar.SECOND);
  }

  // Handling the image chooser activity result
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_CAMERA_PERMISSION && resultCode == RESULT_OK && mFileUri != null) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFileUri.toString());
      intent.putExtra("filetype", "image");
      startActivity(intent);
    } else if (requestCode == RC_STORAGE_PERMISSION && resultCode == RESULT_OK) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFilePath.toString());
      intent.putExtra("filetype", "image");
      startActivity(intent);
    } else if (requestCode == PDF_SELECT && resultCode == RESULT_OK) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFilePath.toString());
      intent.putExtra("filetype", "pdf");
      startActivity(intent);
    } else if (requestCode == DOC_SELECT && resultCode == RESULT_OK) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFilePath.toString());
      intent.putExtra("filetype", "doc");
      startActivity(intent);
    } else if (requestCode == IMAGE_SELECT && resultCode == RESULT_OK) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFilePath.toString());
      intent.putExtra("filetype", "image");
      startActivity(intent);
    } else if (requestCode == VIDEO_SELECT && resultCode == RESULT_OK) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFilePath.toString());
      intent.putExtra("filetype", "video");
      startActivity(intent);
    } else if (requestCode == AUDIO_SELECT && resultCode == RESULT_OK) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFilePath.toString());
      intent.putExtra("filetype", "audio");
      startActivity(intent);
    } else if (requestCode == OTHER_SELECT && resultCode == RESULT_OK) {
      mFilePath = data.getData();
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      intent.putExtra("fileuri", mFilePath.toString());
      intent.putExtra("filetype", "other");
      startActivity(intent);
    } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
      // Do something after user returned from app settings screen, like showing a Toast.
      Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
          .show();
    }
  }

  public void showErrorPlaceholder(String message) {
    if (placeholderError != null && errormessage != null) {
      if (placeholderError.getVisibility() != View.VISIBLE) {
        placeholderError.setVisibility(View.VISIBLE);
      }
      errormessage.setText(message);
    }
  }

  public void hidePlaceHolder() {
    if (placeholderError != null && errormessage != null) {
      if (placeholderError.getVisibility() == View.VISIBLE) {
        placeholderError.setVisibility(View.INVISIBLE);
      }
      errormessage.setText("Something Went Wrong. Try Again!");
    }
  }

  String mCurrentPhotoPath;

  private File createImageFile() throws IOException {
    // Create an image file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(imageFileName,  /* prefix */
        ".jpg",         /* suffix */
        storageDir      /* directory */);

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // Create the File where the photo should go
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // Error occurred while creating the File

      }
      // Continue only if the File was successfully created
      if (photoFile != null) {
        mFileUri = FileProvider.getUriForFile(this, "com.instify.android.provider", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(takePictureIntent, RC_CAMERA_PERMISSION);
      }
    }
  }
}
