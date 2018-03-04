package com.instify.android.services;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import timber.log.Timber;

/**
 * Created by abhishek on 04/03/18.
 */

public class MyJobService extends JobService {

  @Override public boolean onStartJob(JobParameters jobParameters) {
    Timber.d("Performing long running task in scheduled job");
    // TODO(developer): add long running task here.
    return false;
  }

  @Override public boolean onStopJob(JobParameters jobParameters) {
    return false;
  }
}
