package com.veggies.android.todoList.ComponentTests;

import android.os.Looper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.ImageButton;

import com.veggies.android.testHelper.FragmentContainerActivity;
import com.veggies.android.todoList.ListViewFragment;
import com.veggies.android.todoList.R;
import com.veggies.android.todoList.SettingFragment;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by JerryCheung on 4/25/16.
 * Test that the jump between two major fragments does not crash the application
 */
public class MainActivityComponentTest extends ActivityInstrumentationTestCase2<FragmentContainerActivity>{
    private ListViewFragment listViewFragment;
    private SettingFragment settingFragment;

    public MainActivityComponentTest() {
        super(FragmentContainerActivity.class);
        Looper.prepare();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listViewFragment = new ListViewFragment();
        settingFragment = new SettingFragment();
        getActivity().loadFragment(listViewFragment);
        View currentView = getActivity().findViewById(R.id.btnAdd);
        assertThat(currentView, is(notNullValue()));
        assertThat(currentView, instanceOf(ImageButton.class));
    }

    @SmallTest
    public void testJump() {
        getActivity().loadFragment(settingFragment);
    }
}
