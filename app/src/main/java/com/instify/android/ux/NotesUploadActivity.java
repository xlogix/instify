package com.instify.android.ux;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.helpers.SQLiteHandler;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class NotesUploadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = NotesUploadActivity.class.getSimpleName();

    //Declaring views
    private Button buttonChoose;
    private Button buttonUpload;
    private EditText editText;
    private EditText editTextdesc;

    // PERMS
    private static final int RC_CAMERA_PERMISSION = 101;
    private static final int RC_STORAGE_PERMISSION = 123;

    // URI to store the image
    private Uri mFileUri = null;
    private Uri mFilePath;
    private SearchView searchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_notes);
        setTitle(Html.fromHtml("<small>POST TO- " + getIntent().getStringExtra("code") + "</small>"));

        // Initializing views
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        editText = (EditText) findViewById(R.id.editTextName);
        editTextdesc = (EditText) findViewById(R.id.desc);

        // Setting Click Listeners
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
    }

    /*
    * This is the method responsible for pdf upload
    * We need the full pdf path and the name for the pdf in this method
    * */
    public void uploadMultipart() {
        // Getting name for the image
        String name = editText.getText().toString().trim();
        String desc = editTextdesc.getText().toString().trim();

        // Getting the actual path of the image
        String path = mFilePath.toString();

        if (path == null) {
            Toast.makeText(this, "Please move your file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();
                SQLiteHandler db = new SQLiteHandler(this);
                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, AppConfig.UPLOAD_URL)
                        .addFileToUpload(path, "pdf") //Adding file
                        .addParameter("name", name)
                        .addParameter("regno", db.getUserDetails().get("token"))//Adding text parameter to the request
                        .addParameter("desc", desc)//Adding text parameter to the request
                        .addParameter("code", getIntent().getStringExtra("code"))//Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload
                Toast.makeText(getApplicationContext(), "Your File is being uploaded...", Toast.LENGTH_LONG).show();
                finish();

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Get Current Time for naming the file
    public String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-" +
                c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR) + "-" +
                c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
    }

    // Method to show file chooser
    private void showFileChooser() {
        final CharSequence[] items = {"Choose from Storage", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesUploadActivity.this);
        builder.setTitle("Select File");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    // Request the permission
                    requestCameraPermission();

                } else if (items[item].equals("Choose from Storage")) {
                    // Request the permission
                    requestStoragePermission();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // Requesting permission
    @AfterPermissionGranted(RC_CAMERA_PERMISSION)
    private void requestCameraPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Choose file storage location
            String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Instify/";
            File path = new File(mPathName);
            // Check
            boolean isDirectoryCreated = path.exists();
            if (!isDirectoryCreated) {
                isDirectoryCreated = path.mkdir();
            }
            if (isDirectoryCreated) {
                mPathName += getCurrentTime() + ".jpg";
                File filePath = new File(mPathName);
                // Log it
                Timber.d(TAG, "File address :" + filePath);
                // Assign it
                mFileUri = Uri.fromFile(filePath);
                // Get Intent
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                startActivityForResult(takePictureIntent, RC_CAMERA_PERMISSION);
            }
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_CAMERA_PERMISSION, perms);
        }
    }

    // Requesting permission
    @AfterPermissionGranted(RC_STORAGE_PERMISSION)
    private void requestStoragePermission() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Complete action using... "), RC_STORAGE_PERMISSION);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera),
                    RC_STORAGE_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    // Handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CAMERA_PERMISSION && resultCode == RESULT_OK) {
            mFilePath = data.getData();
            buttonChoose.setText("SELECTED");
        } else if (requestCode == RC_STORAGE_PERMISSION && resultCode == RESULT_OK) {
            mFilePath = data.getData();
            buttonChoose.setText("SELECTED");
        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // [START] EasyPermissions Default Functions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    // [END] EasyPermission Default Functions

    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if (v == buttonUpload) {
            uploadMultipart();
        }
    }
}
