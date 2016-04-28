package com.veggies.android.todoList.ComponentTests;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.veggies.android.todoList.LoginActivity;
import com.veggies.android.todoList.MainActivity;
import com.veggies.android.todoList.R;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Test that the jumping from LoginActivity to MainActivity is successful and does not crash the application
 */
public class LoginActivityComponentTest extends InstrumentationTestCase{
    @MediumTest
    public void testValidUserLogIn() {
        Instrumentation instrumentation = getInstrumentation();
        // Register we are interested in the LoginActivity
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(LoginActivity.class.getName(), null, false);

        // Start the LoginActivity as the first activity
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(instrumentation.getTargetContext(), LoginActivity.class.getName());
        instrumentation.startActivitySync(intent);

        // Wait for it to start
        Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertThat(currentActivity, is(notNullValue()));

        // Type into the email field
        View currentView = currentActivity.findViewById(R.id.email);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(EditText.class));
        TouchUtils.clickView(this, currentView);
        instrumentation.sendStringSync("foo@example.com");

        // Type into the password field
        currentView = currentActivity.findViewById(R.id.password);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(EditText.class));
        TouchUtils.clickView(this, currentView);
        instrumentation.sendStringSync("hello");

        // Register we are interested in the MainActivity
        // this has to be done before we do something that will send us to that activity
        instrumentation.removeMonitor(monitor);
        monitor = instrumentation.addMonitor(MainActivity.class.getName(), null, false);

        // Click the login button
        currentView = currentActivity.findViewById(R.id.email_sign_in_button);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(Button.class));
        TouchUtils.clickView(this, currentView);

        // Wait for the MainActivity to start
        currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertThat(currentActivity, is(notNullValue()));

        // Make sure login successfully and application does not crash
        currentView = currentActivity.findViewById(R.id.view_id);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(View.class));
    }
}