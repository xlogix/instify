package xyz.fnplus.instify.ux;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;

import xyz.fnplus.instify.R;
import xyz.fnplus.instify.helpers.SQLiteHandler;
import xyz.fnplus.instify.models.CampusNewsModel;

/**
 * Created by Abhish3k on 4/18/2016.
 */

public class UploadNewsActivity extends AppCompatActivity {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    FirebaseUser fUser;
    DatabaseReference campusNewsRef, finalUploadRef;
    SQLiteHandler db = new SQLiteHandler(this);
    private EditText newsTitle, newsDescription;
    private RadioGroup newsLevelRadio;
    private RadioButton univRadio, deptRadio, classRadio;
    private Button submitNews;
    private int selectedLevel = 1;
    private String currentUser;
    private Spinner dept, classYear, classSec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_news);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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
        currentUser = db.getUserDetails().getRegno();
        campusNewsRef = dbRef.child("campusNews");

        newsLevelRadio.setOnCheckedChangeListener((radioGroup, i) -> {
            dept.setSelection(0);
            classYear.setSelection(0);
            classSec.setSelection(0);
            switch (i) {
                case R.id.campusUploadUniv: {
                    dept.setEnabled(false);
                    classYear.setEnabled(false);
                    classSec.setEnabled(false);
                    selectedLevel = 1;
                    break;
                }

                case R.id.campusUploadDept: {
                    dept.setEnabled(true);
                    classYear.setEnabled(false);
                    classSec.setEnabled(false);
                    selectedLevel = 2;
                    break;
                }

                case R.id.campusUploadClass: {
                    dept.setEnabled(true);
                    classYear.setEnabled(true);
                    classSec.setEnabled(true);
                    selectedLevel = 3;

                }
            }
        });

        submitNews.setOnClickListener(view -> {
            // timestamp acts as news ID
            if (validateForm()) {
                CampusNewsModel data = new CampusNewsModel(
                        newsTitle.getText().toString(),
                        newsDescription.getText().toString(),
                        currentUser,
                        selectedLevel
                );
                Timestamp tStamp = new Timestamp(System.currentTimeMillis());
                finalUploadRef = campusNewsRef.child(getRefString());
                finalUploadRef.child("" + tStamp.getTime()).setValue(data)
                        .addOnFailureListener(e -> Toast.makeText(UploadNewsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show())
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
        if (TextUtils.isEmpty(newsTitle.getText().toString()) || TextUtils.isEmpty(newsDescription.getText().toString())) {

            Toast.makeText(UploadNewsActivity.this, "News Title/Description Cant Be Empty", Toast.LENGTH_SHORT).show();
            validate = false;
        }
        switch (newsLevelRadio.getCheckedRadioButtonId()) {
            case R.id.campusUploadUniv: {

                break;
            }

            case R.id.campusUploadDept: {
                if (dept.getSelectedItem().toString().equals("Select Department")) {
                    Toast.makeText(UploadNewsActivity.this, "Choose Dept from the Dropdown", Toast.LENGTH_SHORT).show();
                    validate = false;
                }
                break;
            }

            case R.id.campusUploadClass: {
                if (dept.getSelectedItemPosition() == 0) {
                    Toast.makeText(UploadNewsActivity.this, "Choose Dept from the Dropdown", Toast.LENGTH_SHORT).show();
                    validate = false;
                }
                if (classYear.getSelectedItemPosition() == 0) {
                    Toast.makeText(UploadNewsActivity.this, "Choose Year from the Dropdown", Toast.LENGTH_SHORT).show();
                    validate = false;
                }
                if (classSec.getSelectedItemPosition() == 0) {
                    Toast.makeText(UploadNewsActivity.this, "Choose Section from the Dropdown", Toast.LENGTH_SHORT).show();
                    validate = false;
                }
            }
        }
        return validate;
    }

    private int getIntYear(String strYear) {
        switch (strYear) {
            case "First":
                return 1;
            case "Second":
                return 2;
            case "Third":
                return 3;
            case "Fourth":
                return 4;
            default:
                return -1;
        }
    }

    private String getRefString() {
        switch (newsLevelRadio.getCheckedRadioButtonId()) {
            case R.id.campusUploadUniv:
                return "all";
            case R.id.campusUploadDept:
                return dept.getSelectedItem().toString() + "/all";
            case R.id.campusUploadClass: {
                return dept.getSelectedItem().toString() +
                        "/" + getIntYear(classYear.getSelectedItem().toString()) +
                        "/" + classSec.getSelectedItem().toString();
            }
            default:
                return "all";
        }
    }
}