package com.veggies.android.todoList.UnitTests;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import com.veggies.android.adapter.ToDoListAdapter;
import com.veggies.android.custom.ToDoItem;
import com.veggies.android.model.DBManager;
import com.veggies.android.testHelper.FragmentContainerActivity;
import com.veggies.android.todoList.MainActivity;
import com.veggies.android.todoList.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class SettingFragmentTest extends ActivityInstrumentationTestCase2<FragmentContainerActivity> {

    private SettingFragment mSettingFragment;
    private Instrumentation mInstrumentation;
    private FragmentContainerActivity mActivity;
    private BaseAdapter mAdapter;

    public SettingFragmentTest() {
        super(FragmentContainerActivity.class);
    }

    @Override
    @UiThreadTest
    protected void setUp() throws Exception{
        super.setUp();

        //simulate the start of MainActivity
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EMAIL, "viceversa.cn@gmail.com");
        setActivityIntent(intent);

        mInstrumentation = getInstrumentation();
        mInstrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mSettingFragment = new SettingFragment();
            }
        });

        //ask the MainActivity to load the fragment
        mActivity = getActivity();
        mActivity.loadFragment(mSettingFragment);
        mInstrumentation.waitForIdleSync();
    }

    @SmallTest
    public void testSetup(){
        assertTrue(true);
    }


    @Override
    public void tearDown() throws Exception {

        // tearDown() is run after a test case has finished.
        // finishOpenedActivities() will finish all the activities that have
        // been opened during the test execution.
        super.tearDown();
    }
}
