package com.instify.android.upload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;

import java.sql.Timestamp;

/**
 * Created by Abhish3k on 4/18/2016.
 */

public class UploadNews extends AppCompatActivity {

    private EditText newsTitle, newsDescription;
    private Button submitNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_news);

        // UI elements //
        newsTitle = (EditText) findViewById(R.id.news_title);
        newsDescription = (EditText) findViewById(R.id.news_description);
        submitNews = (Button) findViewById(R.id.post);

        // Firebase objects //
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference campusNewsRef = dbRef.child("CampusNews");

        submitNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()){
                    CampusNewsData data = new CampusNewsData(newsTitle, newsDescription);
                    Timestamp tStamp = new Timestamp(System.currentTimeMillis());
                    campusNewsRef.child(tStamp.toString()).setValue(data);
                }
            }
        });
    }

        private boolean validateForm() {
            if (!newsTitle.getText().toString().equals("") &&
                    !newsDescription.getText().toString().equals("")){
                return true;
            }
            return false;
        }

}


class CampusNewsData{

    public String title, description;

    CampusNewsData(TextView t, TextView d){
        this.title = t.getText().toString();
        this.description = d.getText().toString();
    }
}
