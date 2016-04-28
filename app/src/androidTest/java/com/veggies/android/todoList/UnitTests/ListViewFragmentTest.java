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
import com.veggies.android.todoList.ListViewFragment;
import com.veggies.android.todoList.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragmentTest extends ActivityInstrumentationTestCase2<FragmentContainerActivity> {

    protected ListViewFragment mListViewFragment;
    protected Instrumentation mInstrumentation;
    protected FragmentContainerActivity mActivity;
    private BaseAdapter mAdapter;

    public ListViewFragmentTest() {
        super(FragmentContainerActivity.class);
    }

    @Override
    @UiThreadTest
    protected void setUp() throws Exception{
        super.setUp();

        Intent intent = new Intent();
        intent.putExtra(MainActivity.EMAIL, "viceversa.cn@gmail.com");
        setActivityIntent(intent);

        Bundle data = new Bundle();
        data.putInt(ListViewFragment.TYPE, DBManager.LIST_TYPE_ALL);

        mInstrumentation = getInstrumentation();

        mInstrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mListViewFragment = new ListViewFragment();
            }
        });

        // update the main content by replacing fragments
        mListViewFragment.setArguments(data);

        //ask the MainActivity to load the fragment
        mActivity = getActivity();
        mActivity.loadFragment(mListViewFragment);

        mInstrumentation.waitForIdleSync();
    }

    @SmallTest
    public void testSetup(){
        assertTrue(true);
    }

    @SmallTest
    public void testAddItem(){
        List<ToDoItem> list = new ArrayList<> ();
        ToDoItem toDoItem = new ToDoItem(0, "title", "description", "date", 0, 0, 0, "placeholder");
        list.add(toDoItem);
        mAdapter = new ToDoListAdapter(mActivity.getApplicationContext(), list, mListViewFragment);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListViewFragment.setListAdapter(mAdapter);
            }
        });

        //wait for the UI to finish setting items
        getInstrumentation().waitForIdleSync();

        ToDoItem resultItem = (ToDoItem)mListViewFragment.getListAdapter().getItem(0);
        assertTrue(resultItem.getDate().equals(toDoItem.getDate()));
    }

    @Override
    protected void tearDown() throws Exception {

        // tearDown() is run after a test case has finished.
        // finishOpenedActivities() will finish all the activities that have
        // been opened during the test execution.
        super.tearDown();
    }
}
