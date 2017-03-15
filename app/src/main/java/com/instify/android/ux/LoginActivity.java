package com.instify.android.ux;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.MyApplication;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.UserDataFirebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Abhish3k on 12/27/2016.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;
    private AutoCompleteTextView mEmailField;
    private AutoCompleteTextView mRegNoField;
    private EditText mPasswordField;
    private SQLiteHandler db;
    // [declare_auth]
    public FirebaseAuth mAuth;
    // [declare_auth_listener]
    public FirebaseAuth.AuthStateListener mAuthStateListener;
    // [declare_database_reference]
    private DatabaseReference mFirebaseDatabase;

    UserDataFirebase userInfoObj;

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    // [END on_stop_remove_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    // [END on_stop_remove_listener]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mEmailField = (AutoCompleteTextView) findViewById(R.id.field_email);
        mRegNoField = (AutoCompleteTextView) findViewById(R.id.field_regNo);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.action_login).setOnClickListener(this);
        findViewById(R.id.action_to_register).setOnClickListener(this);

        // SQLite database handler
        db = new SQLiteHandler(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && MyApplication.getInstance().getPrefManager().isLoggedIn()) {
                    // User is already logged in. Take him to main activity
                    intentLoginToMain();

                    /*mFirebaseDatabase.child("users").child(user.getUid()).setValue(userInfoObj);

                    // Checking and waiting till the info has be added //
                    mFirebaseDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(LoginActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });*/

                    // User is signed in
                    Timber.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Timber.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        // [END auth_state_listener]
    }

    public void intentLoginToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    // [START sign_in_with_email]
    private void attemptERPLogin(final String userEmail, final String regNo, final String password) {
        Timber.d(TAG, "attemptERPLogin:" + regNo);
        if (!validateForm()) {
            return;
        }

        // Start showing the progress dialog
        showProgressDialog();

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Timber.d(TAG, "Login Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // User successfully logged in. Create login session
                        MyApplication.getInstance().getPrefManager().setLogin(true);
                        // Fetch the error msg
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(LoginActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                        // Now store the user in SQLite
                        String uid = jObj.getString("folio_no");

                        //   JSONObject user = jObj.getJSONObject("user");
                        String name = jObj.getString("name");
                        String email = jObj.getString("email");
                        String created_at = jObj.getString("image");
                        String regno = jObj.getString("regno");
                        String dept = jObj.getString("dept");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at, password, regno, dept);

                        // Creating user in Firebase
                        attemptFirebaseLogin(userEmail, password);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(LoginActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                // Got an error, hide the Progress bar
                hideProgressDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("regno", regNo);
                params.put("pass", password);

                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    void attemptFirebaseLogin(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Timber.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        Toast.makeText(LoginActivity.this, R.string.auth_success,
                                Toast.LENGTH_SHORT).show();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Timber.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        // Hide the Progress Dialog
                        hideProgressDialog();
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Invalid Email Address");
        } else {
            mEmailField.setError(null);
        }

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
        } else if (password.length() < 6) {
            mPasswordField.setError("At least six characters.");
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.action_login) {
            attemptERPLogin(mEmailField.getText().toString(), mRegNoField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.action_to_register) {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Timber.d("onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
