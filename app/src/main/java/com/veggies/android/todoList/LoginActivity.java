package com.veggies.android.todoList;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.rey.material.widget.CheckBox;
import com.veggies.android.backup.GoogleAPIHandler;
import com.veggies.android.model.DBHelper;
import com.veggies.android.model.DBManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>, OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final int RC_GOOGLE_LOGIN = 0;

    /**
     *  A constant for requesting permissions.
     */
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    /**
     * Three five to record the user login preferences
     */
    private static final String SETTINGS = "setting";
    private static final String REM_PWD_ISCHECKED = "rem_pwd_ischecked";
    private static final String AUTO_LOGIN_ISCHECKED = "auto_login_ischecked";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private GoogleSignInAccount mGoogleAccount = null;
    private static final String mServerClientID = "";
    /**
     * email address
     */
    private String mEmail = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox remPwd;
    private CheckBox autoLogin;
    private SharedPreferences sp;

    @Override
    protected  void onStart(){
        super.onStart();
        GoogleAPIHandler.revokeAccess();
    }

    /*
    *  callback function when application stops
    * */
    @Override
    protected  void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setTitle("Please Login");
        getActionBar().setIcon(R.drawable.logo_white);
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(0xff303F9F);
        getActionBar().setBackgroundDrawable(colorDrawable);

        //pass context to GoogleAPIHanlder
        GoogleAPIHandler.setContext(this);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(this);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        remPwd = (CheckBox)findViewById(R.id.remem_pwd);
        autoLogin = (CheckBox)findViewById(R.id.au_login);
        sp = this.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);

        if (sp.getBoolean(REM_PWD_ISCHECKED, false)) {
            remPwd.setChecked(true);
            mEmailView.setText(sp.getString(USERNAME, ""));
            mPasswordView.setText(sp.getString(PASSWORD, ""));
            if (sp.getBoolean(AUTO_LOGIN_ISCHECKED, false)) {
                autoLogin.setChecked(true);
                startMainActivity(sp.getString(USERNAME, ""));
            }
        }

        remPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (remPwd.isChecked()) {
                    sp.edit().putBoolean(REM_PWD_ISCHECKED, true).commit();
                } else {
                    if (autoLogin.isChecked()) {
                        sp.edit().putBoolean(AUTO_LOGIN_ISCHECKED, false).commit();
                        autoLogin.setChecked(false);
                    }
                    sp.edit().putBoolean(REM_PWD_ISCHECKED, false).commit();
                }
            }
        });

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (autoLogin.isChecked()) {
                    sp.edit().putBoolean(AUTO_LOGIN_ISCHECKED, true).commit();
                    if (!remPwd.isChecked()) {
                        remPwd.setChecked(true);
                        sp.edit().putBoolean(REM_PWD_ISCHECKED, true).commit();
                    }
                } else {
                    sp.edit().putBoolean(AUTO_LOGIN_ISCHECKED, false).commit();
                }
            }
        });

        // setup Google sign in object
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_FILE))
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestProfile()
                .requestEmail()
                .build();

        GoogleAPIHandler.setGoogleSigninOption(gso);

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Drive.API)
                .addConnectionCallbacks(this)
                //.addOnConnectionFailedListener(this)
                .build();

        GoogleAPIHandler.setGoogleApiClient(googleApiClient);

        SignInButton signInButton = (SignInButton) findViewById(R.id.btnGoogle_sign_in);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        if (sp.getBoolean(REM_PWD_ISCHECKED, false)) {
            remPwd.setChecked(true);
        } else {
            remPwd.setChecked(false);
        }

        if (sp.getBoolean(AUTO_LOGIN_ISCHECKED, false)) {
            autoLogin.setChecked(true);
        } else {
            autoLogin.setChecked(false);
        }

        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    /**
     *  handler function for Google login Result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("handleSignInResult", "handleSignInResult:" + result.getStatus().toString());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            mGoogleAccount = result.getSignInAccount();
            GoogleAPIHandler.setGoogleAccount(mGoogleAccount);
            // connect to API client
            GoogleAPIHandler.getGoogleApiClient().connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
            ;
            if( GoogleAPIHandler.getGoogleApiClient().isConnected()){
                //already connected, manually call the method
                GoogleAPIHandler.onConnected(new Runnable() {
                    @Override
                    public void run() {
                        startMainActivity(mEmail);
                    }
                });
            }
            else{
                showProgress(true);
            }
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this.getApplicationContext(), "Google Sign-in Failed",
                    Toast.LENGTH_SHORT).show();

            if(GoogleAPIHandler.getGoogleApiClient().isConnected()){
                Auth.GoogleSignInApi.revokeAccess(GoogleAPIHandler.getGoogleApiClient());
            }

            startMainActivity(null);
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * this function will attempt Login with Google account
     */
    private void googleLogin(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(GoogleAPIHandler.getGoogleApiClient());

        //calling this will start the Google sign-in activity
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        mEmail = email;

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    // Use Regular Expression to check the format of email address
    private boolean isEmailValid(String email) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(EMAIL_REGEX);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            //no permission to read contacts
            return new CursorLoader(this,
                    // Retrieve data rows for the device user's 'profile' contact.
                    Uri.EMPTY,

                    ProfileQuery.PROJECTION,

                    // Select only email addresses.
                    ContactsContract.Contacts.Data.MIMETYPE +
                            " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE},

                    // Show primary email addresses first. Note that there won't be
                    // a primary email address if the user hasn't specified one.
                    ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
        }
        else{
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

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor == null){
            //this means we don't have permission to read contacts

            return;
        }
        else {
            List<String> emails = new ArrayList<>();

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getString(ProfileQuery.ADDRESS));
                cursor.moveToNext();
            }

            addEmailsToAutoComplete(emails);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.email_sign_in_button) {
            String usremail = mEmailView.getText().toString();
            String usrPwd = mPasswordView.getText().toString();
            if (remPwd.isChecked()) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(USERNAME, usremail);
                editor.putString(PASSWORD, usrPwd);
                editor.commit();
            }
            attemptLogin();
        }
        else if (id == R.id.btnGoogle_sign_in) {
            googleLogin();
        }

    /*
        switch(id){
            case R.id.email_sign_in_button:
                attemptLogin();
                break;

            case R.id.btnGoogle_sign_in:
                googleLogin();
                break;

            default:
        }      */
    }

    /*
        method needed for implementing ConnectionCallbacks
        This will be called once GoogleAPIHandler.getGoogleApiClient().connect has connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        //disable the progress animation
        showProgress(false);

        if(mGoogleAccount!= null && mGoogleAccount.getDisplayName() != null && mGoogleAccount.getEmail() != null) {
            Log.d("handleSignInResult", "Connected as: " + mGoogleAccount.getDisplayName() + " ,Email: " + mGoogleAccount.getEmail());
        }

        GoogleAPIHandler.onConnected(new Runnable() {
            @Override
            public void run() {
                GoogleAPIHandler.restoreDB(true, null, null);
            }
        });
    }



    protected void showMessage(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    /*
        method needed for implementing ConnectionCallbacks
    */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("handleSignInResult", "onConnectionSuspended");
    }

    /*
        call back method when connection failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("handleSignInResult", "onConnectionFailed");
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * TODO need to pass username
     */
    private void startMainActivity(String email){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EMAIL, email);
        startActivity(intent);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                startMainActivity(mEmail);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

