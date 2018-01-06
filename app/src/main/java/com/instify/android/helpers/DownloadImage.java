package com.instify.android.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

import timber.log.Timber;

/**
 * Created by Chandan on 3/1/2017.
 * This is currently not being used as user's Google account image is being used.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
  ImageView bmImage;

  public DownloadImage(ImageView bmImage) {
    this.bmImage = bmImage;
  }

  protected Bitmap doInBackground(String... urls) {
    String urlDisplay = urls[0];
    Bitmap mIcon = null;
    try {
      InputStream in = new java.net.URL(urlDisplay).openStream();
      mIcon = BitmapFactory.decodeStream(in);
    } catch (Exception e) {
      Timber.e(e, "Error %s", e.getMessage());
      e.printStackTrace();
    }
    return mIcon;
  }

  protected void onPostExecute(Bitmap result) {
    bmImage.setImageBitmap(result);
  }
}