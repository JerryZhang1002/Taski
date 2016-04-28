package com.veggies.android.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.veggies.android.custom.ToDoItem;
import com.veggies.android.model.DBManager;
import com.veggies.android.todoList.R;

import java.util.List;

public class WidgetListAdatper extends BaseAdapter{
    private List<ToDoItem> mList;
    private Context mContext;

    public WidgetListAdatper(Context context){
        mContext = context;
        mList = DBManager.queryToDoListByComplete(context, 0);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.widget_listview_item, null);

        ToDoItem item = mList.get(position);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvWidget_list_title);
        TextView tvDate = (TextView) view.findViewById(R.id.tvWidget_list_date);

        tvTitle.setText(item.getTitle());
        tvDate.setText(item.getDate());

        return view;
    }
}
