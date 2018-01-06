package com.instify.android.ux;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import com.instify.android.R;
import com.instify.android.app.AppController;

/**
 * An example full-screen activity that shows and hides the system ui (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

public class IntroActivity extends AppIntro2 {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Add your slide's fragments here.
    // AppIntro will automatically generate the dots indicator and buttons.
    addSlide(AppIntroFragment.newInstance("University Updates", Typeface.DEFAULT_BOLD.toString(),
        "News/Announcements on the website directly pushed to your device",
        Typeface.SANS_SERIF.toString(), R.drawable.srm_logo,
        getResources().getColor(R.color.colorPrimary), 0, 0));

    addSlide(AppIntroFragment.newInstance("Campus Buzz", Typeface.DEFAULT_BOLD.toString(),
        "Always stay updated on Campus Events and News", Typeface.SANS_SERIF.toString(),
        R.drawable.srm_logo, getResources().getColor(R.color.blue_grey_primary), 0, 0));

    addSlide(AppIntroFragment.newInstance("ERP", Typeface.DEFAULT_BOLD.toString(),
        "View all your Marks info in this page. We provide you with statistical data that will help to make difficult choices",
        Typeface.SANS_SERIF.toString(), R.drawable.srm_logo,
        getResources().getColor(R.color.red_primary), 0, 0));

    addSlide(AppIntroFragment.newInstance("Notes Catalog", Typeface.DEFAULT_BOLD.toString(),
        "See all your notes in one place", Typeface.SANS_SERIF.toString(), R.drawable.srm_logo,
        getResources().getColor(R.color.orange_primary), 0, 0));

    addSlide(AppIntroFragment.newInstance("Time Table", Typeface.DEFAULT_BOLD.toString(),
        "For the first time, TimeTable will get meaningful", Typeface.SANS_SERIF.toString(),
        R.drawable.srm_logo, getResources().getColor(R.color.green_primary), 0, 0));

    // Set required animation
    setFadeAnimation();

    // Hide Skip/Done button.
    showSkipButton(true);
    setProgressButtonEnabled(true);

    // Turn vibration on and set intensity.
    // NOTE: you will probably need to ask VIBRATE permission in Manifest.
    setVibrate(true);
    setVibrateIntensity(50);

    // This will ask for the camera permission AND the contacts permission on the same slide.
    // Ensure your slide talks about both so as not to confuse the user.
    // askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS}, 2);
  }

  @Override public void onSkipPressed(Fragment currentFragment) {
    setIsFirstRunFalse();
    Intent i = new Intent(IntroActivity.this, LoginActivity.class);
    startActivity(i);
    finish();
  }

  @Override public void onDonePressed(Fragment currentFragment) {
    setIsFirstRunFalse();
    Intent i = new Intent(IntroActivity.this, LoginActivity.class);
    startActivity(i);
    finish();
  }

  @Override
  public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
    super.onSlideChanged(oldFragment, newFragment);
    // Do something when the slide changes.
  }

  public void setIsFirstRunFalse() {
    // Set isFirstRun Boolean value to false
    AppController.getInstance().getPrefManager().setIsFirstRun(false);
  }
}