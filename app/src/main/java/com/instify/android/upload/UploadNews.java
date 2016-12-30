package com.instify.android.upload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.helpers.CampusNewsData;

import java.sql.Timestamp;

/**
 * Created by Abhish3k on 4/18/2016.
 */

public class UploadNews extends AppCompatActivity {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser fUser;
    DatabaseReference campusNewsRef;
    private EditText newsTitle, newsDescription;
    private RadioGroup newsLevelRadio;
    private Button submitNews;
    private int selectedLevel;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_news);

        // UI elements //
        newsTitle = (EditText) findViewById(R.id.news_title);
        newsDescription = (EditText) findViewById(R.id.news_description);
        newsLevelRadio = (RadioGroup) findViewById(R.id.campusUploadRadioGroup);
        submitNews = (Button) findViewById(R.id.post);
        selectedLevel = newsLevelRadio.getCheckedRadioButtonId();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        // Auth details //
        if(fUser != null){
            currentUser = fUser.getEmail();
            Toast.makeText(UploadNews.this, "User : " + currentUser, Toast.LENGTH_SHORT).show();
        }
        else{
            currentUser = "noname";
            Toast.makeText(UploadNews.this, "NoUser", Toast.LENGTH_SHORT).show();
        }

        // Firebase objects //
        campusNewsRef = dbRef.child("CampusNews");

        submitNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    // TODO: Use a relevant key for the news in the database, attach user details.
                    selectedLevel = newsLevelRadio.getCheckedRadioButtonId();
                    CampusNewsData data = new CampusNewsData(newsTitle, newsDescription, selectedLevel, currentUser);
                    Timestamp tStamp = new Timestamp(System.currentTimeMillis());
                    campusNewsRef.child("" + tStamp.getTime()).setValue(data)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadNews.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UploadNews.this, "News announced!", Toast.LENGTH_SHORT).show();
                                    newsTitle.setText("");
                                    newsDescription.setText("");
                                    finish();
                                }
                            });
                }
            }
        });
    }

    private boolean validateForm() {
        if (!newsTitle.getText().toString().equals("") &&
                !newsDescription.getText().toString().equals("")) {
            return true;
        }
        return false;
    }

}
