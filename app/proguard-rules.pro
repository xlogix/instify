# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepattributes Signature
-keepattributes Exceptions
-optimizations !field/removal/writeonly,!field/marking/private,!class/merging/*,!code/allocation/variable

# Basic proguard configuration to support all the devices
-keep class android.support.v4.** {  *; }

-keep class android.support.v7.internal.** { *; }

-keep interface android.support.v7.internal.** { *; }

-keep class android.support.v7.** { *; }

-keep interface android.support.v7.** { *; }

# Remove unwanted logging for increased security
-assumenosideeffects class android.util.Log {
     public static boolean isLoggable(java.lang.String, int);
     public static int v(...);
     public static int i(...);
     public static int w(...);
     public static int d(...);
     public static int e(...);
}

# Proguard for Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

