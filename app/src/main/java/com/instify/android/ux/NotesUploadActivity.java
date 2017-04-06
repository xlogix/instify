package com.instify.android.ux;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.instify.android.R;
import com.instify.android.helpers.NotesFilePathHelper;
import com.instify.android.helpers.SQLiteHandler;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.util.UUID;

public class NotesUploadActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring views
    private Button buttonChoose;
    private Button buttonUpload;

    private EditText editText;
    private EditText editTextdesc;
    private Uri mFileUri = null;

    public static final String UPLOAD_URL = "https://hashbird.com/gogrit.in/workspace/srm-api/uploadfile.php";

    // PERMS
    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_PERMISSION = 123;

    //Uri to store the image uri
    private Uri filePath;
    SearchView searchView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_notes);
        setTitle(Html.fromHtml("<small>POST TO- " + getIntent().getStringExtra("code") + "</small>"));

        //Requesting storage permission
        requestStoragePermission();

        //Initializing views
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        editText = (EditText) findViewById(R.id.editTextName);
        editTextdesc = (EditText) findViewById(R.id.desc);

        //Setting clicklistener
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
        String path = NotesFilePathHelper.getPath(this, filePath);

        if (path == null) {

            Toast.makeText(this, "Please move your .pdf file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();
                SQLiteHandler db = new SQLiteHandler(this);
                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
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

    // Method to show file chooser
    private void showFileChooser() {
        final CharSequence[] items = {"Take Photo", "Choose file from Storage", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesUploadActivity.this);
        builder.setTitle("Select Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    // Create intent
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Choose file storage location
                    File file = new File(Environment.getExternalStorageDirectory() + "/Instify/", UUID.randomUUID().toString() + ".jpg");
                    mFileUri = Uri.fromFile(file);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                    // Start Intent
                    startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using "), RC_STORAGE_PERMISSION);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // Handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == RC_TAKE_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null)
                || (requestCode == RC_STORAGE_PERMISSION && resultCode == RESULT_OK)) {
            filePath = data.getData();
            buttonChoose.setText("SELECTED");
        }
    }

    // Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_STORAGE_PERMISSION);
    }


    // This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Checking the request code of our request
        if (requestCode == RC_STORAGE_PERMISSION) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                // Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops! you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


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
