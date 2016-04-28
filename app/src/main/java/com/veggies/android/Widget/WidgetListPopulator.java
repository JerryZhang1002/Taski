package com.veggies.android.Widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.veggies.android.custom.ToDoItem;
import com.veggies.android.model.DBManager;
import com.veggies.android.todoList.R;

import java.util.ArrayList;
import java.util.List;


    public class WidgetListPopulator implements RemoteViewsFactory {
    private List<ToDoItem> mList = new ArrayList<>();
    private Context mContext;
    private int mAppWidgetID;


    public WidgetListPopulator(Context context, Intent intent){
        mContext = context;
        mAppWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        //populate the list with the unfinished items
        mList = DBManager.queryToDoListByComplete(mContext, 0);
    }

    @Override
    public void onCreate() {

        //Ref: http://developer.android.com/guide/topics/appwidgets/index.html
        // In onCreate() you setup any connections / cursors to your data source.

        //pull unfinished items from database
        mList = DBManager.queryToDoListByComplete(mContext, 0);
    }

    @Override
    public void onDataSetChanged() {
        //pull new data from the DB
        mList = DBManager.queryToDoListByComplete(mContext, 0);

        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.app_widget_listview);

        Intent remoteViewIntent = new Intent(mContext, WidgetService.class);
        remoteViewIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetID);
        remoteViewIntent.setData(Uri.parse(remoteViewIntent.toUri(Intent.URI_INTENT_SCHEME)));

        //set listview adapter
        remoteViews.setRemoteAdapter(mAppWidgetID, R.id.lvWidget, remoteViewIntent);

        //default empty view
        remoteViews.setEmptyView(R.id.lvWidget, R.id.empty_view);

        // Instruct the widget manager to update the widget
        AppWidgetManager.getInstance(mContext).updateAppWidget(mAppWidgetID, remoteViews);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_listview_item);
        ToDoItem item = mList.get(position);

        //set the properties for the item
        remoteView.setTextViewText(R.id.tvWidget_list_title, item.getTitle());
        remoteView.setTextViewText(R.id.tvWidget_list_date, item.getDate());

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
