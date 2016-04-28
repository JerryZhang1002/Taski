package com.veggies.android.todoList;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.veggies.android.Widget.WidgetRefresher;
import com.veggies.android.adapter.ToDoListAdapter;
import com.veggies.android.alarm.MyAlarmManager;
import com.veggies.android.backup.GoogleAPIHandler;
import com.veggies.android.custom.ToDoItem;
import com.veggies.android.model.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xiaobin Lin 2/22/2016.
 * This fragment is used to show a mList of to-do items.
 */

public class ListViewFragment extends ListFragment implements ToDoListAdapter.AdapterInterface{
    public static final String TYPE = "type";

    private List<ToDoItem> mList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeView = null;
    private Activity mParentActivity;
    private int type = DBManager.LIST_TYPE_ALL;

    public ListViewFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            type = getArguments().getInt(TYPE, DBManager.LIST_TYPE_ALL);
        }
        mParentActivity = this.getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listview, container, false);
        ImageButton btnAdd = (ImageButton) view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //show a popup window(activity) with details of adding a new to-do item
                Intent intent = new Intent(getActivity(),Popup_Window.class);
                startActivity(intent);
            }
        });

        mSwipeView = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mSwipeView.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        GoogleAPIHandler.restoreDB(false, afterRefreshRunnable, refreshFailedRunnable);
                        WidgetRefresher.updateWidget(mParentActivity);
                    }
                }
        );
        return view;
    }

    //the code to run when local DB is restored from remote DB
    //this will be on the UI thread
    private final Runnable afterRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            populateList();
            reScheduleAlarms();
            mSwipeView.setRefreshing(false);
        }
    };

    private final Runnable refreshFailedRunnable = new Runnable() {
        @Override
        public void run() {
            mSwipeView.setRefreshing(false);
        }
    };

    /**
     * re-schedule alarms
     */
    private void reScheduleAlarms(){
        for(ToDoItem todo : mList){

            if(todo.getTimeMillis() > System.currentTimeMillis()){
                MyAlarmManager.getAlarmReceiver().setAlarm(mParentActivity, todo.getTimeMillis());
            }
        }
    }

    /**
     * Callback function for clicking on an to-do item in the mList
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ToDoItem toDoItem = (ToDoItem)getListAdapter().getItem(position);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(ToDoItem.TODO_TITLE, toDoItem.getTitle());
        bundle.putString(ToDoItem.TODO_DESCRIPTION, toDoItem.getDescription());
        bundle.putString(ToDoItem.TODO_DATE, toDoItem.getDate());
        bundle.putInt(ToDoItem.TODO_COMPLETE, toDoItem.getComplete());
        bundle.putInt(ToDoItem.TODO_POSITION, position);     //need to record the pos......
        bundle.putInt(ToDoItem.TODO_ID, toDoItem.getId());   //record the ID
        bundle.putInt(ToDoItem.TODO_TYPE, toDoItem.getType());
        bundle.putString(ToDoItem.TODO_AUDIO, toDoItem.getAudioPath());
        intent.putExtras(bundle);
        intent.setClass(getActivity(), Popup_Window.class);
        startActivity(intent);//ForResult(intent, REQUEST_TODO_EDIT);       //result code is set to 1. indicate that it is a reediting operation. only need to update existing ToDoItem rather than making a new one.
    }
    
    @Override
    public void onResume() {
        super.onResume();
        populateList();
    }

    @Override
    public void checkboxchange() {
        this.onResume();
    }
    private void populateList(){
        if (type >= DBManager.LIST_TYPE_DEFAULT && type <= DBManager.LIST_TYPE_WORK) {
            mList = DBManager.queryToDoListByType(getActivity(), type);
        }
        else if (type == DBManager.LIST_TYPE_ALL) {
            mList = DBManager.queryToDoListByFuzzy(getActivity(), null);
        }
        else if (type == DBManager.LIST_TYPE_UNFINISHED) {
            mList = DBManager.queryToDoListByComplete(getActivity(), 0);
        }
        BaseAdapter adapter = new ToDoListAdapter(getActivity(), mList, this);
        setListAdapter(adapter);
    }
}
