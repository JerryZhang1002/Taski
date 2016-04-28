package com.veggies.android.todoList.UnitTests;


import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.veggies.android.alarm.AlarmReceiver;
import com.veggies.android.todoList.ListViewFragment;

public class AlarmReceiverTest extends ListViewFragmentTest{
    private AlarmReceiver mAlarmReceiver;

    /**
     * Assumes the ListViewFragment has been loaded.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mAlarmReceiver = new AlarmReceiver();

    }


    @SmallTest
    public void testSetAlarm(){
        Long triggerTime = System.currentTimeMillis() + 1000;

        //simulate setting alarm
        Intent intent = new Intent(mActivity, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.ALARM_TIME, triggerTime);
        mAlarmReceiver.onReceive(mActivity, intent);
        mInstrumentation.waitForIdleSync();

        assertTrue(intent.getLongExtra(AlarmReceiver.ALARM_TIME, 0) == triggerTime);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
