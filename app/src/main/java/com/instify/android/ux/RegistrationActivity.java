package com.instify.android.ux;

import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.MyApplication;
import com.instify.android.helpers.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by Abhish3k on 12/16/2016.  // Redid Auth using Realtime Database on 12/22/2016
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    // TAG
    private static final String TAG = RegistrationActivity.class.getSimpleName();

    public ProgressDialog mProgressDialog;
    // [END declare_database]
    private DatabaseReference mFirebaseDatabase;
    // [declare_auth]
    private FirebaseAuth mAuth;
    // [declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    // Init
    private SQLiteHandler db;

    private String userId;
    private AutoCompleteTextView mEmailField;
    private EditText mNameField, mPasswordField, mRegNoField;
    private Button btnRegister;

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        if (mAuthStateListener != null) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }
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
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
    // [END on_stop_remove_listener]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Views
        mRegNoField = (EditText) findViewById(R.id.field_regNO);
        mEmailField = (AutoCompleteTextView) findViewById(R.id.field_email);
        populateAutoComplete();
        mPasswordField = (EditText) findViewById(R.id.field_password);
        btnRegister = (Button) findViewById(R.id.btn_register);

        // Buttons
        findViewById(R.id.btn_register).setOnClickListener(this);

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
                if (user != null) {

                    // Send a confirmation mail to the user's email ID
                    sendConfirmationMail(user);

                    // Update the UI
                    hideProgressDialog();

                    // User is signed in
                    Timber.d("onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    finish();
                } else {
                    // User is signed out
                    Timber.d("onAuthStateChanged:signed_out");
                }
            }
        };
        // [END auth_state_listener]
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    // Button Click Action
    @Override
    public void onClick(View v) {
        attemptERPLogin(mEmailField.getText().toString(), mPasswordField.getText().toString(), mRegNoField.getText().toString());
    }

    void attemptERPLogin(final String emailText, final String passwordText, final String regNo) {

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        Timber.d("Registration:");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

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
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(RegistrationActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                        // User successfully logged in. Create login session
                        MyApplication.getInstance().getPrefManager().setLogin(true);
                        // Now store the user in SQLite
                        String uid = jObj.getString("folio_no");

                        //   JSONObject user = jObj.getJSONObject("user");
                        String name = jObj.getString("name");
                        String email = jObj.getString("email");
                        String created_at = jObj.getString("image");
                        String regno = jObj.getString("regno");
                        String dept = jObj.getString("dept");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at, passwordText, regno, dept);

                        // Creating user in Firebase
                        attemptFirebaseLogin(emailText, passwordText);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(RegistrationActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(RegistrationActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("pass", passwordText);

                return params;
            }
        };
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    void attemptFirebaseLogin(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Timber.d("User exists with the same email id");
                            }
                            Toast.makeText(RegistrationActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    // [START] Request contacts from user
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailField, R.string.contacts_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }
    // [END] Request contact information

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.Loading));
            mProgressDialog.setIndeterminate(true);
            // mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void sendConfirmationMail(FirebaseUser mFirebaseUser) {
        mFirebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.d("Email sent.");
                        }
                    }
                });
    }

    /* [START] Functions required for AutoComplete E-mail TextView */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegistrationActivity.this,
                        android.R.layout.simple_list_item_1, emailAddressCollection);

        mEmailField.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
    /* [STOP] Functions required for AutoComplete E-mail field */
}
