package com.veggies.android.todoList.UnitTests;


import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.veggies.android.custom.ToDoItem;
import com.veggies.android.todoList.Popup_Window;
import com.veggies.android.todoList.R;

import java.util.Calendar;

public class Popup_WindowTest extends ActivityInstrumentationTestCase2<Popup_Window>{
    private Popup_Window mActivity;
    private Instrumentation mInstrumentation;
    private Button add;
    private Button delete;
    private EditText title;
    private EditText description;
    private CheckBox isCompleted;
    private TextView editTime;
    private Spinner spinner;
    private ImageButton img_btn_record;
    private ImageButton img_btn_play;


    public Popup_WindowTest() {
        super(Popup_Window.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);

        //simulate starting the Popup_Window activity from ListViewFragment
        ToDoItem toDoItem = new ToDoItem(0, "title", "description", "date", 0, 1, 3, "placeholder");
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(ToDoItem.TODO_TITLE, toDoItem.getTitle());
        bundle.putString(ToDoItem.TODO_DESCRIPTION, toDoItem.getDescription());
        bundle.putString(ToDoItem.TODO_DATE, toDoItem.getDate());
        bundle.putInt(ToDoItem.TODO_COMPLETE, toDoItem.getComplete());
        bundle.putInt(ToDoItem.TODO_POSITION, 2);     //need to record the pos......
        bundle.putInt(ToDoItem.TODO_ID, toDoItem.getId());   //record the ID
        bundle.putInt(ToDoItem.TODO_TYPE, toDoItem.getType());
        bundle.putString(ToDoItem.TODO_AUDIO, toDoItem.getAudioPath());
        intent.putExtras(bundle);
        setActivityIntent(intent);

        mInstrumentation = getInstrumentation();
        mActivity = getActivity();

        title = (EditText) mActivity.findViewById(R.id.todo_title);
        description = (EditText) mActivity.findViewById(R.id.todo_description);
        isCompleted = (CheckBox) mActivity.findViewById(R.id.completed);
        editTime = (TextView) mActivity.findViewById(R.id.todo_date);
        add = (Button) mActivity.findViewById(R.id.add_list);
        delete = (Button) mActivity.findViewById(R.id.delete_list);
        spinner = (Spinner) mActivity.findViewById(R.id.tasklist_spinner);
        img_btn_record = (ImageButton) mActivity.findViewById(R.id.img_btn_audio_record);
        img_btn_play = (ImageButton) mActivity.findViewById(R.id.img_btn_audio_play);

        //wait for the UI to be idle
        mInstrumentation.waitForIdleSync();
    }

    @SmallTest
    public void testSetup(){
        assertTrue(true);
    }


    @SmallTest
    public void testNonNull() {
        assertTrue(title != null);
        assertTrue(description != null);
        assertTrue(isCompleted != null);
        assertTrue(editTime != null);
        assertTrue(add != null);
        assertTrue(delete != null);
        assertTrue(spinner != null);
        assertTrue(img_btn_record != null);
        assertTrue(img_btn_play != null);
    }

    @SmallTest
    public void testGetIntent(){
        Intent intent = mActivity.getIntent();
        final Bundle bundle = intent.getExtras();

        assertTrue(bundle.getString(ToDoItem.TODO_TITLE).equals("title"));
        assertTrue(bundle.getString(ToDoItem.TODO_DESCRIPTION).equals("description"));
        assertTrue(bundle.getString(ToDoItem.TODO_DATE).equals("date"));
        assertTrue(bundle.getString(ToDoItem.TODO_AUDIO).equals("placeholder"));
        assertTrue(bundle.getInt(ToDoItem.TODO_ID) == 0);
        assertTrue(bundle.getInt(ToDoItem.TODO_TYPE) == 3);
        assertTrue(bundle.getInt(ToDoItem.TODO_POSITION) == 2);
        assertTrue(bundle.getInt(ToDoItem.TODO_COMPLETE) == 1);
    }

    @Override
    public void tearDown() throws Exception {
        // tearDown() is run after a test case has finished.
        // finishOpenedActivities() will finish all the activities that have
        // been opened during the test execution.
        super.tearDown();
    }
}
