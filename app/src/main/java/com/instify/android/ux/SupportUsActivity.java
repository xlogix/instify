package com.instify.android.ux;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.instify.android.R;

/**
 * Created by Abhish3k on 24-03-2017.
 */

public class SupportUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_us);

        final TextView phoneNumber = (TextView) findViewById(R.id.copy);
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Phone Number : ", "9962892900");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SupportUsActivity.this, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
