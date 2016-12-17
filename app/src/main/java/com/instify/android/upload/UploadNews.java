package com.instify.android.upload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.instify.android.R;

/**
 * Created by Abhish3k on 4/18/2016.
 */
public class UploadNews extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_news);

        /*TextView textView = (TextView) findViewById(R.id.news_description);
        String listString = "";
        ArrayList<String> list = GetCalendar.readCalendarEvent(getApplicationContext());

        for (String s : list)
        {
            listString += s + "\t";
        }

        textView.setText(listString);*/

    }
}
