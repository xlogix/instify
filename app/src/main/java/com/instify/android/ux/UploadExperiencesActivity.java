package com.instify.android.ux;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.instify.android.R;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.ExperiencesModel;
import java.util.HashMap;

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
            Log.e("exp", e.getMessage(), e);
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
}
