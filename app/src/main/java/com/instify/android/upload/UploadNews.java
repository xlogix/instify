package com.instify.android.upload;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.models.CampusNewsModel;

import java.sql.Timestamp;

/**
 * Created by Abhish3k on 4/18/2016.
 */

public class UploadNews extends AppCompatActivity {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser fUser;
    DatabaseReference campusNewsRef, finalUploadRef;

    private EditText newsTitle, newsDescription;
    private RadioGroup newsLevelRadio;
    private RadioButton univRadio, deptRadio, classRadio;
    private Button submitNews;
    private int selectedLevel;
    private String currentUser;
    private Spinner dept, classYear, classSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_news);

        // UI elements //
        newsTitle = (EditText) findViewById(R.id.news_title);
        newsDescription = (EditText) findViewById(R.id.news_description);
        newsLevelRadio = (RadioGroup) findViewById(R.id.campusUploadRadioGroup);
        univRadio = (RadioButton) findViewById(R.id.campusUploadUniv);
        deptRadio = (RadioButton) findViewById(R.id.campusUploadDept);
        classRadio = (RadioButton) findViewById(R.id.campusUploadClass);
        submitNews = (Button) findViewById(R.id.post);
        dept = (Spinner) findViewById(R.id.campusUploadDeptSpinner);
        classYear = (Spinner) findViewById(R.id.campusUploadClassYearSpinner);
        classSec = (Spinner) findViewById(R.id.campusUploadClassSecSpinner);

        // Adapters //
        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter
                .createFromResource(this, R.array.campusUploadDeptArray, R.layout.support_simple_spinner_dropdown_item);
        deptAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> classYearAdapter = ArrayAdapter
                .createFromResource(this, R.array.campusUploadYearArray, R.layout.support_simple_spinner_dropdown_item);
        classYearAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> classSecAdapter = ArrayAdapter
                .createFromResource(this, R.array.campusUploadSecArray, R.layout.support_simple_spinner_dropdown_item);
        classSecAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        // End Adapters //

        dept.setAdapter(deptAdapter);
        classYear.setAdapter(classYearAdapter);
        classSec.setAdapter(classSecAdapter);

        // disabling the spinners
        dept.setEnabled(false);
        classYear.setEnabled(false);
        classSec.setEnabled(false);

        // Firebase objects //
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = getIntent().getStringExtra("username");
        campusNewsRef = dbRef.child("campusNews");

        newsLevelRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.campusUploadUniv: {
                        dept.setEnabled(false);
                        classYear.setEnabled(false);
                        classSec.setEnabled(false);
                        selectedLevel = R.id.campusUploadUniv;
                        break;
                    }

                    case R.id.campusUploadDept: {
                        dept.setEnabled(true);
                        classYear.setEnabled(false);
                        classSec.setEnabled(false);
                        selectedLevel = R.id.campusUploadDept;
                        break;
                    }

                    case R.id.campusUploadClass: {
                        dept.setEnabled(true);
                        classYear.setEnabled(true);
                        classSec.setEnabled(true);
                        selectedLevel = R.id.campusUploadClass;
                    }
                }
            }
        });

        submitNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // timestamp acts as news ID
                if (validateForm()) {
                    selectedLevel = newsLevelRadio.getCheckedRadioButtonId();
                    CampusNewsModel data = new CampusNewsModel(
                            newsTitle.getText().toString(),
                            newsDescription.getText().toString(),
                            selectedLevel,
                            getIntYear(classYear.getSelectedItem().toString()),
                            dept.getSelectedItem().toString(),
                            classSec.getSelectedItem().toString(),
                            currentUser
                    );
                    Timestamp tStamp = new Timestamp(System.currentTimeMillis());
                    finalUploadRef = campusNewsRef.child(getRefString());
                    finalUploadRef.child("" + tStamp.getTime()).setValue(data)
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
        return (!newsTitle.getText().toString().equals("") && !newsDescription.getText().toString().equals(""));
    }

    private int getIntYear(String strYear){
        int intYear;
        switch(strYear){
            case "First": return 1;
            case "Second": return 2;
            case "Third" : return 3;
            case "Fourth" : return 4;
            default: return -1;
        }
    }

    private String getRefString(){
        switch(newsLevelRadio.getCheckedRadioButtonId()){
            case R.id.campusUploadUniv: return "all";
            case R.id.campusUploadDept: return dept.getSelectedItem().toString() + "/all";
            case R.id.campusUploadClass: {
                return dept.getSelectedItem().toString() +
                        "/" + getIntYear(classYear.getSelectedItem().toString()) +
                        "/" + classSec.getSelectedItem().toString();
            }
            default: return "all";
        }
    }

}


