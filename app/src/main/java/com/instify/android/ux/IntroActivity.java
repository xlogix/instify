package com.instify.android.ux;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.instify.android.R;

/**
 * An example full-screen activity that shows and hides the system ui (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class IntroActivity extends AppIntro {

    // Please DO NOT override onCreate. Use init.
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("University Updates", "News/Announcements on the website directly pushed to your device", R.drawable.srm_logo, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance("Campus Buzz", "Always stay updated on Campus Events and News", R.drawable.srm_logo, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance("ERP", "View all your Marks info in this page. We provide you with statistical data that will help to make difficult choices", R.drawable.srm_logo, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance("Notes Catalog", "See all your notes in one place", R.drawable.srm_logo, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance("Time Table", "For the first time, TimeTable will get meaningful", R.drawable.srm_logo, R.color.colorAccent));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.

    }

    @Override
    public void onSkipPressed() {
        Intent i = new Intent(IntroActivity.this, AuthActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onDonePressed() {
        Intent i = new Intent(IntroActivity.this, AuthActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }
    // Implement on_back_pressed
}