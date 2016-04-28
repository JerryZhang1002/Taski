package com.veggies.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.veggies.android.custom.DropdownMenuItem;
import com.veggies.android.todoList.R;

import java.util.List;

/**
 * Created by JerryCheung on 4/20/16.
 * This adapter is used for inflating the item of dropdown menu in the actionbar
 */
public class DropdownMenuItemAdapter extends BaseAdapter implements SpinnerAdapter{
    Context context;
    List<DropdownMenuItem> dropdownMenuItemList;

    public DropdownMenuItemAdapter(Context context, List<DropdownMenuItem> dropdownMenuItemList) {
        super();
        this.context = context;
        this.dropdownMenuItemList = dropdownMenuItemList;
    }

    public int getCount() {
        return dropdownMenuItemList.size();
    }

    public Object getItem(int position) {
        return dropdownMenuItemList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_menu_item, null);
        ImageView menu_image = (ImageView) view.findViewById(R.id.dropdown_menu_item_image);
        TextView menu_text = (TextView) view.findViewById(R.id.dropdown_menu_item_text);
        menu_image.setImageResource(dropdownMenuItemList.get(position).getImageId());
        menu_text.setText(dropdownMenuItemList.get(position).getText());
        return view;
    }
}
