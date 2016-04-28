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
import android.widget.ImageButton;

import com.veggies.android.todoList.LoginActivity;
import com.veggies.android.todoList.MainActivity;
import com.veggies.android.todoList.Popup_Window;
import com.veggies.android.todoList.R;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by JerryCheung on 4/26/16.
 * Test that the jumping from MainActivity to Popup_Window is successful and does not crash the application.
 */
public class PopupWindowComponentTest extends InstrumentationTestCase{

    private static final String testEmail = "foo@example.com";
    private static final String testPwd = "hello";
    private static final String testTitle = "Test Item";
    private static final String testDesc = "This is a test todoItem";

    @MediumTest
    public void testPopupWindow() {
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
        instrumentation.sendStringSync(testEmail);

        // Type into the password field
        currentView = currentActivity.findViewById(R.id.password);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(EditText.class));
        TouchUtils.clickView(this, currentView);
        instrumentation.sendStringSync(testPwd);

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

        //get add new listitem button
        currentView = currentActivity.findViewById(R.id.btnAdd);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(ImageButton.class));

        //Register we are interested in the Popup_Window Activity
        instrumentation.removeMonitor(monitor);
        monitor = instrumentation.addMonitor(Popup_Window.class.getName(), null, false);

        // Click the add todoitem button
        TouchUtils.clickView(this, currentView);;
        // Wait for the Popup_Window Activity to start
        currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
        assertThat(currentActivity, is(notNullValue()));

        // Set the title for the test todoItem
        currentView = currentActivity.findViewById(R.id.todo_title);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(EditText.class));
        TouchUtils.clickView(this, currentView);
        instrumentation.sendStringSync(testTitle);

        // Set the descripption for the test todoItem
        currentView = currentActivity.findViewById(R.id.todo_description);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(EditText.class));
        TouchUtils.clickView(this, currentView);
        instrumentation.sendStringSync(testDesc);

        // Register we are insterested in the MainActivity
        instrumentation.removeMonitor(monitor);
        monitor = instrumentation.addMonitor(MainActivity.class.getName(), null, false);

        currentView = currentActivity.findViewById(R.id.add_list);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(Button.class));

    }
}
