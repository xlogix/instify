# Instify
[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=7)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat)](http://opensource.org/licenses/MIT)

# Features

It's also on Google Play:
<a href="https://play.google.com/store/apps/details?id=com.instify.android" target="_blank">
  <img alt="Get it on Google Play"
      src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png" height="60"/>
</a>

#### Manifest Settings

```xml
 <supports-screens
        android:anyDensity="true"
        android:largeScreens="true"
        android:normalScreens="true"
        android:smallScreens="true" />

    <!-- Normal permissions, access automatically granted to app -->
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
    <uses-permission android:name="com.android.launcher.permission.UNINSTALL_SHORTCUT" />

    <!-- Dangerous permissions, access must be requested at runtime -->
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    <!-- FCM (Firebase Cloud Messaging) for all build types configuration -->
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <!-- self-defined permission prevents other apps to hijack PNs -->
    <permission
        android:name="${applicationId}.permission.C2D_MESSAGE"
        android:protectionLevel="signature" />
    <uses-permission android:name="${applicationId}.permission.C2D_MESSAGE" />

    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />
    <uses-feature
        android:name="android.hardware.camera.front"
        android:required="false" />
    <uses-feature
        android:name="android.hardware.camera.autofocus"
        android:required="true" />
