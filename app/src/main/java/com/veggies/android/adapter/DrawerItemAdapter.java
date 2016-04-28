package com.veggies.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.veggies.android.custom.DrawerItem;
import com.veggies.android.todoList.R;

import java.util.List;

/**
 * Created by JerryCheung on 4/20/16.
 * This adapter is used for inflating the draweritem
 */
public class DrawerItemAdapter extends BaseAdapter{
    Context context;
    List<DrawerItem> drawerItemList;

    public DrawerItemAdapter(Context context, List<DrawerItem> drawerItemList) {
        super();
        this.context = context;
        this.drawerItemList = drawerItemList;
    }

    public int getCount() {
        return drawerItemList.size();
    }

    public Object getItem(int position) {
        return drawerItemList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.drawer_item, null);
        ImageView drawer_image = (ImageView) view.findViewById(R.id.drawer_item_image);
        TextView drawer_text = (TextView) view.findViewById(R.id.drawer_item_word);
        drawer_image.setImageResource(drawerItemList.get(position).getImageId());
        drawer_text.setText(drawerItemList.get(position).getText());
        return view;
    }
}
