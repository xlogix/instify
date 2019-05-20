package com.instify.android.ux;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.instify.android.R;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.ExperiencesModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class UploadExperiencesActivity extends AppCompatActivity {

  @BindView(R.id.imageUpload) ImageButton imageUpload;
  @BindView(R.id.experience_title) EditText experienceTitle;
  @BindView(R.id.experience_description) EditText experienceDescription;
  @BindView(R.id.categorySpinner) Spinner categorySpinner;
  @BindView(R.id.postExperience) Button postExperience;
  @BindView(R.id.relative_layout) RelativeLayout relativeLayout;
  String currentUserRno, imageUrl;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload_experiences);
    ButterKnife.bind(this);
    SQLiteHandler db = new SQLiteHandler(this);
    currentUserRno = db.getUserDetails().getRegno();
  }

  @OnClick(R.id.imageUpload) public void onImageUploadClicked() {
      CropImage.activity()
              .start(this);
  }

  @OnClick(R.id.postExperience) public void onPostExperienceClicked() {
    if (!validateForm()) {
      HashMap<String, Boolean> votes = new HashMap<>();

      votes.put(currentUserRno, true);
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      DocumentReference documentReference = db.collection("experiences").document();
      String id = documentReference.getId();
      ExperiencesModel experience = new ExperiencesModel(id, experienceTitle.getText().toString(),
          experienceDescription.getText().toString(), currentUserRno, imageUrl,
          categorySpinner.getSelectedItem().toString(), votes);

      documentReference.set(experience)
          .addOnSuccessListener(documentReference2 -> finish())
          .addOnFailureListener(e -> {
            Snackbar.make(relativeLayout, "Post Failed" + e, Snackbar.LENGTH_LONG).show();
            Timber.e(e.getMessage(), "exp");
          });
    }
  }

  boolean validateForm() {
    boolean cancel = false;
    experienceTitle.setError(null);
    experienceDescription.setError(null);
    if (experienceTitle.getText().toString().isEmpty()) {
      experienceTitle.setError("Can't Be Empty");
      cancel = true;
    }
    if (experienceDescription.getText().toString().isEmpty()) {
      experienceDescription.setError("Can't Be Empty");
      cancel = true;
    }
    return cancel;
  }


  // Getting the result after crop.
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (resultCode == RESULT_OK) {
        Uri resultUri = result.getUri();
        Glide.with(this)
                .load(resultUri)
                .into(imageUpload);
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Exception error = result.getError();
      }
    }
  }
}
