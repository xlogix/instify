package com.instify.android.ux;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instify.android.R;
import com.instify.android.helpers.UserInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

/**
 * Created by Abhish3k on 12/16/2016.  // Redid Auth using Realtime Database on 12/22/2016
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    // [END declare_database]
    UserInfo userInfoObj;
    private DatabaseReference mFirebaseDatabase;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    // [END declare_auth_listener]
    private String userId;
    private TextView txtDetails;
    private EditText mNameField, mEmailField, mPasswordField, mRegNoField, mSectionField;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Views
        mNameField = (EditText) findViewById(R.id.field_name);
        mRegNoField = (EditText) findViewById(R.id.field_regNO);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        btnRegister = (Button) findViewById(R.id.btn_register);

        // Buttons
        findViewById(R.id.btn_register).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START auth_state_listener]
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Timber.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent loginToMain = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(loginToMain);
                    finish();
                } else {
                    // User is signed out
                    Timber.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        // [END auth_state_listener]
    }

    @Override
    public void onClick(View v) {
        registerUser(mEmailField.getText().toString(), mPasswordField.getText().toString(), mNameField.getText().toString(),
                mRegNoField.getText().toString(), mSectionField.getText().toString());
    }

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
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
    // [END on_stop_remove_listener]

    void registerUser(String emailText, String passwordText, String name, String regNo, String section) {

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        Timber.d(TAG, "Registration:");
        if (!validateForm()) {
            return;
        }
        // Checks
        isValidEmail(emailText);

        showProgressDialog();

        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth  Also, should we assign department and year just using the registration number?
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        userInfoObj = new UserInfo(regNo, section);

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mFirebaseDatabase.child("users").child(user.getUid()).setValue(userInfoObj);
                    // Checking and waiting till the info has be added //
                    mFirebaseDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Toast.makeText(RegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(RegistrationActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        hideProgressDialog();
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
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

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
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
}
