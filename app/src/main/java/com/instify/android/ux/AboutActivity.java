package com.instify.android.ux;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.instify.android.BuildConfig;
import com.instify.android.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by Abhish3k on 8/21/2016.
 */

public class AboutActivity extends AppCompatActivity {

  @Override public void onBackPressed() {
    super.onBackPressed();
    finish();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);

    View aboutPage = new AboutPage(this).isRTL(false)
        .setDescription(
            "This app is developed by : Abhishek Uniyal, Vijay Krishna, Arjun Mahishi, Somnath and Chandan Singh"
                + "\n\nSpecial thanks to : Ankur, Trishansh")
        .setImage(R.drawable.ic_logo_96dp)
        .addItem(new Element().setTitle(BuildConfig.VERSION_NAME))
        .addGroup("Connect with us")
        .addEmail("support@fnplus.xyz")
        .addWebsite("https://www.fnplus.xyz/")
        .addFacebook("https://www.facebook.com/fnplusofficial")
        .addYoutube("https://youtu.be/RMDjTWrylOk")
        .addPlayStore("https://play.google.com/store/apps/details?id=com.instify.android")
        .addItem(getCopyRightsElement())
        .create();
    setContentView(aboutPage);
  }

  Element getCopyRightsElement() {
    Element copyRightsElement = new Element();
    final String copyrights =
        String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
    copyRightsElement.setTitle(copyrights);
    copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
    copyRightsElement.setIconTint(R.color.about_item_icon_color);
    copyRightsElement.setGravity(Gravity.CENTER);
    copyRightsElement.setOnClickListener(
        v -> Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show());
    return copyRightsElement;
  }
}
