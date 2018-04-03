package com.instify.android.app;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import timber.log.Timber;

/**
 * Created by abhishek on 07/02/18.
 */

public class ForceRemoteConfigUpdateChecker {
  private static final String TAG = ForceRemoteConfigUpdateChecker.class.getSimpleName();

  public static final String KEY_APP_UPDATE_REQUIRED = "force_app_update_required";
  public static final String KEY_CURRENT_VERSION = "force_update_current_version";
  public static final String KEY_UPDATE_URL = "force_update_store_url";

  public static final String KEY_API_UPDATE_REQUIRED = "force_api_update_required";
  public static final String KEY_URL_LOGIN = "api_url_login";
  public static final String KEY_URL_GET_ATTENDANCE = "api_url_get_attendance";
  public static final String KEY_URL_GET_TT = "api_url_get_tt";
  public static final String KEY_URL_GET_FEE = "api_url_get_fee";

  private OnUpdateNeededListener onUpdateNeededListener;
  private Context context;

  public interface OnUpdateNeededListener {
    void onUpdateNeeded(String updateUrl);
  }

  public static Builder with(@NonNull Context context) {
    return new Builder(context);
  }

  public ForceRemoteConfigUpdateChecker(@NonNull Context context,
      OnUpdateNeededListener onUpdateNeededListener) {
    this.context = context;
    this.onUpdateNeededListener = onUpdateNeededListener;
  }

  public void check() {
    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    if (remoteConfig.getBoolean(KEY_APP_UPDATE_REQUIRED)) {
      String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
      String appVersion = getAppVersion(context);
      String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

      if (!TextUtils.equals(currentVersion, appVersion) && onUpdateNeededListener != null) {
        onUpdateNeededListener.onUpdateNeeded(updateUrl);
      }
    }
    if (remoteConfig.getBoolean(KEY_API_UPDATE_REQUIRED)) {
      AppConfig.KEY_URL_LOGIN = remoteConfig.getString(KEY_URL_LOGIN);
      AppConfig.KEY_URL_GET_ATTENDANCE = remoteConfig.getString(KEY_URL_GET_ATTENDANCE);
      AppConfig.KEY_URL_GET_TT = remoteConfig.getString(KEY_URL_GET_TT);
      AppConfig.KEY_URL_GET_FEE = remoteConfig.getString(KEY_URL_GET_FEE);
    }
  }

  private String getAppVersion(Context context) {
    String result = "";

    try {
      result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
      result = result.replaceAll("[a-zA-Z]|-", "");
    } catch (PackageManager.NameNotFoundException e) {
      Timber.e(TAG, e.getMessage());
    }

    return result;
  }

  public static class Builder {

    private Context context;
    private OnUpdateNeededListener onUpdateNeededListener;

    public Builder(Context context) {
      this.context = context;
    }

    public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
      this.onUpdateNeededListener = onUpdateNeededListener;
      return this;
    }

    public ForceRemoteConfigUpdateChecker build() {
      return new ForceRemoteConfigUpdateChecker(context, onUpdateNeededListener);
    }

    public ForceRemoteConfigUpdateChecker check() {
      ForceRemoteConfigUpdateChecker forceRemoteConfigUpdateChecker = build();
      forceRemoteConfigUpdateChecker.check();

      return forceRemoteConfigUpdateChecker;
    }
  }
}
