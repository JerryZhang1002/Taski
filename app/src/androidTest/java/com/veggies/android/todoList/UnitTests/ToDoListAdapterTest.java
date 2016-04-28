package com.veggies.android.todoList.UnitTests;

import android.view.View;
import android.widget.TextView;

import com.rey.material.widget.CheckBox;
import com.veggies.android.adapter.ToDoListAdapter;
import com.veggies.android.custom.ToDoItem;
import com.veggies.android.todoList.R;

import java.util.ArrayList;
import java.util.List;

public class ToDoListAdapterTest extends ListViewFragmentTest {

    private ToDoListAdapter mAdapter;
    private ToDoItem mToDoItem;
    private List<ToDoItem> mList = new ArrayList<> ();
    private String[] types = new String[]{"Default", "Personal", "Shopping", "Wishlist", "Work"};

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //super class' setup will prepare the activity and the ListViewFragment
        mToDoItem = new ToDoItem(0, "title", "description", "date", 0, 0, 0, "placeholder");
        mList.add(mToDoItem);
        mAdapter = new ToDoListAdapter(mActivity.getApplicationContext(), mList, mListViewFragment);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListViewFragment.setListAdapter(mAdapter);
            }
        });

        //wait for the UI to finish setting items
        getInstrumentation().waitForIdleSync();
    }

    public void testConstructor(){
        ToDoItem resultItem = (ToDoItem)mListViewFragment.getListAdapter().getItem(0);
        assertTrue(resultItem.getTitle().equals(mToDoItem.getTitle()));
        assertTrue(resultItem.getDescription().equals(mToDoItem.getDescription()));
        assertTrue(resultItem.getDate().equals(mToDoItem.getDate()));
        assertTrue(resultItem.getAudioPath().equals(mToDoItem.getAudioPath()));
        assertTrue(resultItem.getId() == mToDoItem.getId());
        assertTrue(resultItem.getType() == mToDoItem.getType());
        assertTrue(resultItem.getTimeMillis() == mToDoItem.getTimeMillis());
    }

    public void testGetView(){
        int position = 0;

        View view = mAdapter.getView(position, null, null);
        assertTrue(view != null);

        TextView title = (TextView) view.findViewById(R.id.list_title);
        TextView type = (TextView) view.findViewById(R.id.list_type);
        TextView date = (TextView) view.findViewById(R.id.list_date);
        CheckBox state = (CheckBox) view.findViewById(R.id.list_state);

        ToDoItem toDoItem = mList.get(position);

        //check that what we get are the same as what we set before
        assertTrue(title.getText().equals(toDoItem.getTitle()));
        assertTrue(type.getText().equals(types[toDoItem.getType()]));
        assertTrue(date.getText().equals(toDoItem.getDate()));
        if(toDoItem.getComplete() == 1){
            assertTrue(state.isChecked());
        }
        else{
            assertFalse(state.isChecked());
        }
    }

    @Override
    protected void tearDown() throws Exception {
        //do the necessary tearDown work
        super.tearDown();
    }

}
