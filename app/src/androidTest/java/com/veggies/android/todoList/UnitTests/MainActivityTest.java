package com.veggies.android.todoList.UnitTests;


import android.app.Instrumentation;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ListView;

import com.veggies.android.todoList.MainActivity;
import com.veggies.android.todoList.R;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{
    private MainActivity mMainActivity;
    private Instrumentation mInstrumentation;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;


    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        setActivityInitialTouchMode(true);

        mInstrumentation = getInstrumentation();
        mMainActivity = getActivity();

        mDrawerLayout = (DrawerLayout) mMainActivity.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) mMainActivity.findViewById(R.id.left_drawer);

    }

    @SmallTest
    public void testSetup(){
        assertTrue(true);
    }


    @SmallTest
    public void testNonNull() {
        assertTrue(mDrawerLayout != null);
        assertTrue(mDrawerList != null);
    }

    @Override
    public void tearDown() throws Exception {
        // tearDown() is run after a test case has finished.
        // finishOpenedActivities() will finish all the activities that have
        // been opened during the test execution.
        super.tearDown();
    }
}
