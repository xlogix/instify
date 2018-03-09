package com.instify.android.ux;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.CampusNewsModel;
import java.sql.Timestamp;

/**
 * Created by Abhish3k on 4/18/2016.
 */

public class UploadNewsActivity extends AppCompatActivity {

  DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
  FirebaseUser fUser;
  DatabaseReference campusNewsRef, finalUploadRef;
  SQLiteHandler db = new SQLiteHandler(this);
  private EditText newsTitle, newsDescription;
  private RadioButton univRadio;
  private Button submitNews;
  private int selectedLevel = 1;
  private String currentUser;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_post_news);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // UI elements //
    newsTitle = findViewById(R.id.news_title);
    newsDescription = findViewById(R.id.news_description);
    univRadio = findViewById(R.id.campusUploadUniv);
    submitNews = findViewById(R.id.post);

    // Firebase objects //
    fUser = FirebaseAuth.getInstance().getCurrentUser();
    currentUser = db.getUserDetails().getRegno();
    campusNewsRef = dbRef.child("campusNews");

    submitNews.setOnClickListener(view -> {
      // timestamp acts as news ID
      if (validateForm()) {
        CampusNewsModel data = new CampusNewsModel(newsTitle.getText().toString(),
            newsDescription.getText().toString(), currentUser, selectedLevel);
        Timestamp tStamp = new Timestamp(System.currentTimeMillis());
        finalUploadRef = campusNewsRef.child(getRefString());
        finalUploadRef.child("" + tStamp.getTime())
            .setValue(data)
            .addOnFailureListener(
                e -> Toast.makeText(UploadNewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                    .show())
            .addOnSuccessListener(aVoid -> {
              Toast.makeText(UploadNewsActivity.this, "News announced!", Toast.LENGTH_SHORT).show();
              newsTitle.setText("");
              newsDescription.setText("");
              finish();
            });
      }
    });
  }

  private boolean validateForm() {
    boolean validate = true;
    if (TextUtils.isEmpty(newsTitle.getText().toString()) || TextUtils.isEmpty(
        newsDescription.getText().toString())) {

      Toast.makeText(UploadNewsActivity.this, "News Title/Description Cant Be Empty",
          Toast.LENGTH_SHORT).show();
      validate = false;
    }
    return validate;
  }

  private String getRefString() {
    return "all";
  }
}