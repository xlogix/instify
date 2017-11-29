package com.instify.android.app;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import timber.log.Timber;

class FirebaseDeclarations {

  // Prevents multiple references of objects in the app

  public static DatabaseReference getBaseRef() {
    return FirebaseDatabase.getInstance().getReference();
  }

  public static String getCurrentUserId() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      return user.getUid();
    }
    return null;
  }

  public static DatabaseReference getCurrentUserRef() {
    String uid = getCurrentUserId();
    if (uid != null) {
      return getBaseRef().child("users").child(getCurrentUserId());
    }
    return null;
  }

  public static DatabaseReference getUsersRef() {
    return getBaseRef().child("users");
  }

  public static DatabaseReference getCommentsRef() {
    return getBaseRef().child("comments");
  }

  public static DatabaseReference getLikesRef() {
    return getBaseRef().child("likes");
  }

  // Subscribe to Notifications
  public static void subscribeToPushService() {
    FirebaseMessaging.getInstance().subscribeToTopic("news");
    Timber.d("Instify", "Subscribed");

    String token = FirebaseInstanceId.getInstance().getToken();
    // Log
    Timber.d("Sender Token : ", token);
  }
}
