package com.instify.android.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.instify.android.R;

import timber.log.Timber;

/**
 * Base class for Services that keep track of the number of active jobs and self-stop when the
 * count is zero.
 */
public abstract class MyFirebaseBaseTaskService extends Service {
  private static final String TAG = "MyBaseTaskService";

  static final int PROGRESS_NOTIFICATION_ID = 0;
  static final int FINISHED_NOTIFICATION_ID = 1;

  private int mNumTasks = 0;

  public void taskStarted() {
    changeNumberOfTasks(1);
  }

  public void taskCompleted() {
    changeNumberOfTasks(-1);
  }

  private synchronized void changeNumberOfTasks(int delta) {
    Timber.d("changeNumberOfTasks:" + mNumTasks + ":" + delta);
    mNumTasks += delta;

    // If there are no tasks left, stop the service
    if (mNumTasks <= 0) {
      Timber.d("stopping");
      stopSelf();
    }
  }

  /**
   * Show notification with a progress bar.
   */
  protected void showProgressNotification(String caption, long completedUnits, long totalUnits) {
    int percentComplete = 0;
    if (totalUnits > 0) {
      percentComplete = (int) (100 * completedUnits / totalUnits);
    }

    String channelId = getString(R.string.default_notification_channel_id);
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_file_upload_white_24dp)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(caption)
            .setProgress(100, percentComplete, false)
            .setOngoing(true)
            .setAutoCancel(false);

    NotificationManager manager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    manager.notify(PROGRESS_NOTIFICATION_ID, builder.build());

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel =
          new NotificationChannel(channelId, "global",
              NotificationManager.IMPORTANCE_DEFAULT);
      manager.createNotificationChannel(channel);
    }
  }

  /**
   * Show notification that the activity finished.
   */
  protected void showFinishedNotification(String caption, Intent intent, boolean success) {
    // Make PendingIntent for notification
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* requestCode */, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);

    int icon = success ? R.drawable.ic_check_white_24 : R.drawable.ic_error_white_24dp;

    String channelId = getString(R.string.default_notification_channel_id);
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, channelId).setSmallIcon(icon)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(caption)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

    NotificationManager manager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    manager.notify(FINISHED_NOTIFICATION_ID, builder.build());

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel =
          new NotificationChannel(channelId, "global",
              NotificationManager.IMPORTANCE_DEFAULT);
      manager.createNotificationChannel(channel);
    }
  }

  /**
   * Dismiss the progress notification.
   */
  protected void dismissProgressNotification() {
    NotificationManager manager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    manager.cancel(PROGRESS_NOTIFICATION_ID);
  }
}
