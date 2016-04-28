package com.veggies.android.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.rey.material.widget.CheckBox;
import com.veggies.android.backup.GoogleAPIHandler;
import com.veggies.android.custom.ToDoItem;
import com.veggies.android.model.DBManager;
import com.veggies.android.todoList.R;

import java.util.List;


/**
 * Created by JerryCheung on 2/23/16.
 * This adapter is used for inflating the ToDoItem listview in ListViewFragment
 */
public class ToDoListAdapter extends BaseAdapter {
    Context context;
    List<ToDoItem> toDoItemList;
    String[] types;
    AdapterInterface checkboxListener;


    public interface AdapterInterface {
        public void checkboxchange();
    }

    public ToDoListAdapter(Context context, List<ToDoItem> toDoItemList, AdapterInterface checkboxListener) {
        super();
        this.context = context;
        this.toDoItemList = toDoItemList;
        this.checkboxListener = checkboxListener;
        types = new String[]{"Default", "Personal", "Shopping", "Wishlist", "Work"};
    }

    public int getCount() {
        return toDoItemList.size();
    }

    public Object getItem(int position) {
        return toDoItemList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ToDoItem toDoItem = toDoItemList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.todo_listview_item, null);
        TextView title = (TextView) view.findViewById(R.id.list_title);
        TextView type = (TextView) view.findViewById(R.id.list_type);
        TextView desc = (TextView) view.findViewById(R.id.list_desc);

        desc.setText(toDoItemList.get(position).getDescription());
        type.setTypeface(null, Typeface.BOLD);
        TextView date = (TextView) view.findViewById(R.id.list_date);
        CheckBox state = (CheckBox) view.findViewById(R.id.list_state);
        title.setText(toDoItemList.get(position).getTitle());
        date.setText(toDoItemList.get(position).getDate());
        type.setText(types[toDoItemList.get(position).getType()]);
        if (toDoItemList.get(position).getComplete() == 1) {
            state.setChecked(true);
        }
        state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GoogleAPIHandler.backupDB();

                if (isChecked) {      //perform logic
                    toDoItem.setComplete(1);
                    DBManager.updateToDoList(context, toDoItem);
                }
                else {
                    toDoItem.setComplete(0);
                    DBManager.updateToDoList(context, toDoItem);
                }
                if (com.veggies.android.todoList.MainActivity.isDisplayAllList) {
                    checkboxListener.checkboxchange();
                }
            }
        });
        return view;
    }
}
