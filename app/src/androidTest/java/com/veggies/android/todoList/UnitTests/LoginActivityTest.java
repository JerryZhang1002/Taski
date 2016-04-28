package com.veggies.android.todoList.UnitTests;


import android.app.Instrumentation;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.common.SignInButton;
import com.rey.material.widget.CheckBox;
import com.veggies.android.todoList.LoginActivity;
import com.veggies.android.todoList.R;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity>{
    private LoginActivity mActivity;
    private Instrumentation mInstrumentation;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private CheckBox remPwd;
    private CheckBox autoLogin;
    private Button mEmailSignInButton;
    private SignInButton mSignInButton;


    /**
     *  The LoginActivity is the activity to start on launch. Don't need to check the intent.
     */
    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        mInstrumentation = getInstrumentation();
        mActivity = getActivity();

        mEmailView = (AutoCompleteTextView) mActivity.findViewById(R.id.email);
        mPasswordView = (EditText) mActivity.findViewById(R.id.password);
        mEmailSignInButton = (Button) mActivity.findViewById(R.id.email_sign_in_button);
        mLoginFormView = mActivity.findViewById(R.id.login_form);
        mProgressView = mActivity.findViewById(R.id.login_progress);
        remPwd = (CheckBox)mActivity.findViewById(R.id.remem_pwd);
        autoLogin = (CheckBox)mActivity.findViewById(R.id.au_login);
        mSignInButton = (SignInButton) mActivity.findViewById(R.id.btnGoogle_sign_in);
    }

    @SmallTest
    public void testSetup(){
        assertTrue(true);
    }


    @SmallTest
    public void testNonNull() {
        assertTrue(mEmailView != null);
        assertTrue(mPasswordView != null);
        assertTrue(mEmailSignInButton != null);
        assertTrue(mLoginFormView != null);
        assertTrue(mProgressView != null);
        assertTrue(remPwd != null);
        assertTrue(autoLogin != null);
        assertTrue(mSignInButton != null);
    }

    @Override
    public void tearDown() throws Exception {
        // tearDown() is run after a test case has finished.
        // finishOpenedActivities() will finish all the activities that have
        // been opened during the test execution.
        super.tearDown();
    }
}
