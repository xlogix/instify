package com.instify.android.ux;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.RetrofitBuilder;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.interfaces.RetrofitInterface;
import com.instify.android.models.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;

/**
 * Created by Abhish3k on 12/27/2016. Google and Custom Login using Firebase
 */

public class LoginActivity extends AppCompatActivity
    implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
  private static final String TAG = LoginActivity.class.getSimpleName();
  /**
   * Id to identity GOOGLE_SIGN_IN request
   */
  private static final int RC_GOOGLE_SIGN_IN = 9001;
  // [declare_auth]
  public FirebaseAuth mAuth;
  // [declare_auth_listener]
  public FirebaseAuth.AuthStateListener mAuthStateListener;
  @BindView(R.id.scrollView) ScrollView mScrollView;
  // @BindView(R.id.constraint_login) ConstraintLayout constraintLayout;
  // Root View
  private ProgressDialog mProgressDialog;
  private AppCompatEditText mRegNoField, mPasswordField;
  private SQLiteHandler db;
  // [declare Google API client]
  private GoogleApiClient mGoogleApiClient;

  // [START on_start_add_listener]
  @Override public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthStateListener);
  }
  // [END on_start_add_listener]

  // [START on_resume_add_listener]
  @Override public void onResume() {
    super.onResume();
    mAuth.addAuthStateListener(mAuthStateListener);
  }
  // [END on_resume_add_listener]

  // [START on_stop_remove_listener]
  @Override public void onStop() {
    super.onStop();
    if (mAuthStateListener != null) {
      mAuth.removeAuthStateListener(mAuthStateListener);
    }
  }
  // [END on_stop_remove_listener]

  // [START on_stop_remove_listener]
  @Override public void onPause() {
    super.onPause();
    if (mAuthStateListener != null) {
      mAuth.removeAuthStateListener(mAuthStateListener);
    }
  }
  // [END on_stop_remove_listener]

  // [START on_destroy_remove_listener]
  @Override public void onDestroy() {
    super.onDestroy();
    if (mProgressDialog != null) {
      mProgressDialog.dismiss();
      mProgressDialog = null;
    }
  }
  // [STOP on_destroy_remove_listener]

  @Override public void onCreate(Bundle savedInstanceState) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window w = getWindow();
      w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    // Setup animation
    AnimationDrawable animationDrawable = (AnimationDrawable) mScrollView.getBackground();
    animationDrawable.setEnterFadeDuration(2500);
    animationDrawable.setExitFadeDuration(2500);
    animationDrawable.start();

    // Views
    mRegNoField = findViewById(R.id.field_regNo);
    mPasswordField = findViewById(R.id.field_password);

    // Buttons
    findViewById(R.id.button_google_login).setOnClickListener(this);
    findViewById(R.id.action_login).setOnClickListener(this);

    // Disable the buttons by default
    mRegNoField.setEnabled(false);
    mPasswordField.setEnabled(false);

    TapTargetView.showFor(this,                 // `this` is an Activity
        TapTarget.forView(findViewById(R.id.button_google_login), "First, Sign in to Google!",
            "This enables you to enter your ERP credentials")
            // All options below are optional
            .outerCircleColor(R.color.colorPrimary)      // Specify a color for the outer circle
            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
            .targetCircleColor(R.color.white)   // Specify a color for the target circle
            .titleTextSize(20)                  // Specify the size (in sp) of the title text
            .titleTextColor(R.color.white)      // Specify the color of the title text
            .descriptionTextSize(10)            // Specify the size (in sp) of the description text
            .descriptionTextColor(
                R.color.colorPrimary)  // Specify the color of the description text
            .textColor(
                R.color.white)            // Specify a color for both the title and description text
            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
            .dimColor(
                R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
            .drawShadow(true)                   // Whether to draw a drop shadow or not
            .cancelable(
                true)                  // Whether tapping outside the outer circle dismisses the view
            .tintTarget(true)                   // Whether to tint the target view's color
            .transparentTarget(
                true)           // Specify whether the target is transparent (displays the content underneath)
            .targetRadius(60));                  // Specify the target radius (in dp)

    // [START config_signin]
    // Configure Google Sign In
    GoogleSignInOptions gso =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(
            getString(R.string.default_web_client_id)).requestEmail().requestProfile().build();

    mGoogleApiClient =
        new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */,
            this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    // [END config_signin]

    // SQLite database handler
    db = new SQLiteHandler(this);

    // [START initialize_auth]
    mAuth = FirebaseAuth.getInstance();
    // [END initialize_auth]

    // [START auth_state_listener]
    mAuthStateListener = firebaseAuth -> {
      FirebaseUser user = firebaseAuth.getCurrentUser();
      if (user != null) {
        // User is signed in
        Timber.d(TAG, "onAuthStateChanged:signed_in:", user.getUid());
      } else {
        // User is signed out
        Timber.d(TAG, "onAuthStateChanged:signed_out");
      }
    };
    // [END auth_state_listener]
  }

  // [START signin]
  private void signInWithGoogle() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
  }
  // [END signin]

  // [START on_activity_result]
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_GOOGLE_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        // Google Sign In was successful, authenticate with Firebase
        GoogleSignInAccount account = result.getSignInAccount();
        firebaseAuthWithGoogle(account);
      } else {
        // Google Sign In failed, update UI appropriately
        Toast.makeText(LoginActivity.this, "Try Again.", Toast.LENGTH_LONG).show();
      }
    }
  }
  // [END on_activity_result]

  // [START auth_with_google]
  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
    Timber.d(acct.getId(), "firebaseAuthWithGoogle:");
    // [START_EXCLUDE silent]
    showProgressDialog();
    // [END_EXCLUDE]

    AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Timber.d(TAG, "signInWithCredential:success");

              // [START_EXCLUDE]
              Toast.makeText(LoginActivity.this, "Sign in successful! Continue with ERP login",
                  Toast.LENGTH_LONG).show();
              // Make the fields active
              mRegNoField.setEnabled(true);
              mPasswordField.setEnabled(true);
              // [END_EXCLUDE]

            } else {
              // If sign in fails, display a message to the user.
              Timber.w(task.getException(), "signInWithCredential:failure");
              Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT)
                  .show();
            }
            // [START_EXCLUDE]
            hideProgressDialog();
            // [END_EXCLUDE]
          }
        });
  }
  // [END auth_with_google]

  public void intentLoginToMain() {
    startActivity(new Intent(this, MainActivity.class));
    finish();
    overridePendingTransition(R.anim.left_to_right_start, R.anim.right_to_left_start);
  }

  // [START sign_in_with_email]
  private void attemptERPLogin(final String regNo, final String password) {
    Timber.d(TAG, "attemptERPLogin:", regNo);
    if (!validateForm()) {
      return;
    }

    // Start showing the progress dialog
    showProgressDialog();

    // Tag used to cancel the request
    String tag_string_req = "req_login";

    StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.KEY_URL_LOGIN, response -> {
      Timber.d(TAG, "Login Response: " + response);
      try {
        JSONObject jObj = new JSONObject(response);
        boolean error = jObj.getBoolean("error");

        // Check for error node in json
        if (!error) {
          // User successfully logged in. Create login session
          AppController.getInstance().getPrefManager().setLogin(true);

          // Now store the user in SQLite
          String uid = jObj.getString("folio_no");

          // JSONObject user = jObj.getJSONObject("user");
          String name = jObj.getString("name");
          String email = jObj.getString("email");
          String created_at = jObj.getString("image");
          String regno = jObj.getString("regno");
          String dept = jObj.getString("dept");

          // Inserting row in users table
          db.addUser(name, email, uid, created_at, password, regno, dept);

          // Take the user to the main activity
          intentLoginToMain();

          // Fetch the error msg
          Toast.makeText(this, jObj.getString("error_msg"), Toast.LENGTH_LONG).show();
        }
      } catch (JSONException e) {
        // JSON error
        e.printStackTrace();
        Toast.makeText(this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
      }
    }, error -> {
      Timber.e(TAG, "Login Error: " + error.getMessage());
      Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
      // Got an error, hide the Progress bar
      hideProgressDialog();
    }) {

      @Override protected Map<String, String> getParams() {
        // Posting parameters to login url
        Map<String, String> params = new HashMap<>();
        params.put("regno", regNo);
        params.put("pass", password);

        return params;
      }
    };
    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
  }

  private void attemptErpLoginRetrofit(final String regNo, final String password) {
    if (!validateForm()) {
      return;
    }

    // Start showing the progress dialog
    showProgressDialog();

    RetrofitInterface client = RetrofitBuilder.createService(RetrofitInterface.class);
    Call<UserModel> call = client.Login(regNo, password);
    call.enqueue(new Callback<UserModel>() {
      @Override public void onResponse(@NonNull Call<UserModel> call,
          @NonNull retrofit2.Response<UserModel> response) {

        if (response.isSuccessful()) {

          UserModel user = response.body();

          if (!user.getError()) {
            // Set login to true
            AppController.getInstance().getPrefManager().setLogin(true);

            if (mAuth.getCurrentUser() != null) {
              user.setEmail(mAuth.getCurrentUser().getEmail());
              user.setUid(mAuth.getCurrentUser().getUid());

              user.setRegno(regNo);
              user.setToken(password);

              // TODO : Pass the object usermodel as arguments to adduser
              db.addUser(user);
              // Update UI
              Toast.makeText(LoginActivity.this, user.getErrorMsg(), Toast.LENGTH_LONG).show();
              hideProgressDialog();
              // Take the user to the main activity
              intentLoginToMain();
            }
          } else {
            // Set the login to false again
            AppController.getInstance().getPrefManager().setLogin(false);
            // Handle UI
            hideProgressDialog();
            Toast.makeText(LoginActivity.this, user.getErrorMsg(), Toast.LENGTH_LONG).show();
          }
        }
      }

      @Override public void onFailure(Call<UserModel> call, Throwable t) {
        Timber.e(TAG, "Login Error: ", t.getMessage());
        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
        Log.d(TAG, "onFailure: "+t.getMessage());
        // Got an error, hide the Progress bar
        hideProgressDialog();
      }
    });
  }

  private boolean validateForm() {
    boolean valid = true;

    String regNo = mRegNoField.getText().toString();
    if (TextUtils.isEmpty(regNo)) {
      mRegNoField.setError("Required.");
      valid = false;
    } else {
      mRegNoField.setError(null);
    }

    String password = mPasswordField.getText().toString();
    if (TextUtils.isEmpty(password)) {
      mPasswordField.setError("Required.");
      valid = false;
    } else {
      mPasswordField.setError(null);
    }

    return valid;
  }

  @Override public void onClick(View v) {
    int i = v.getId();
    if (i == R.id.action_login) {
      attemptErpLoginRetrofit(mRegNoField.getText().toString(),
          mPasswordField.getText().toString());
    } else if (i == R.id.button_google_login) {
      signInWithGoogle();
    }
  }

  public void showProgressDialog() {
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setMessage(getString(R.string.Loading));
      mProgressDialog.setIndeterminate(true);
    }
    mProgressDialog.show();
  }

  public void hideProgressDialog() {
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }

  @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    // An unresolvable error has occurred and Google APIs (including Sign-In) will not
    // be available.
    Timber.d("onConnectionFailed:" + connectionResult);
    Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
  }
}
