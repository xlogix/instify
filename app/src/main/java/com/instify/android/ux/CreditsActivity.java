package com.instify.android.ux;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.instify.android.R;

/**
 * Created by Abhish3k on 8/21/2016.
 */

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
    }

    public void fb(View view) {
        Intent fbIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/aurganon/"));
        startActivity(fbIntent);
    }

    public void tweet(View view) {
        Intent tweetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Aurganon"));
        startActivity(tweetIntent);
    }

    public void insta(View view) {
        Intent instaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/aurganon/"));
        startActivity(instaIntent);
    }


    public void web(View view) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://aurganon.com"));
        startActivity(webIntent);
    }
}
